/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.logic.sql2entity.analyzer;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.cbean.SimplePagingBean;
import org.seasar.dbflute.exception.DfCustomizeEntityDuplicateException;
import org.seasar.dbflute.exception.DfParameterBeanDuplicateException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData.DfPagingType;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPropertyTypePackageResolver;
import org.seasar.dbflute.outsidesql.ProcedurePmb;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.node.BindVariableNode;
import org.seasar.dbflute.twowaysql.node.IfNode;
import org.seasar.dbflute.twowaysql.node.Node;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimeException;
import org.seasar.dbflute.util.DfTypeUtil.ParseTimestampException;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfParameterBeanResolver {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final List<String> _reservBooleanMethodList = new ArrayList<String>();
    static {
        for (Method method : SimplePagingBean.class.getMethods()) {
            if (method.getReturnType().equals(boolean.class)) {
                _reservBooleanMethodList.add(method.getName());
            }
        }
        for (Method method : ProcedurePmb.class.getMethods()) {
            if (method.getReturnType().equals(boolean.class)) {
                _reservBooleanMethodList.add(method.getName());
            }
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfSql2EntityMeta _sql2entityMeta;
    protected final File _sqlFile;
    protected final AppData _schemaData;
    protected final DfSql2EntityMarkAnalyzer _outsideSqlMarkAnalyzer = new DfSql2EntityMarkAnalyzer();
    protected final DfSqlFileNameResolver _sqlFileNameResolver = new DfSqlFileNameResolver();
    protected final DfPropertyTypePackageResolver _propertyTypePackageResolver = new DfPropertyTypePackageResolver();
    protected final DfBehaviorQueryPathSetupper _bqpSetupper = new DfBehaviorQueryPathSetupper();

    // temporary collection resolved by auto-detect
    protected final Set<String> _alternateBooleanMethodNameSet = new LinkedHashSet<String>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfParameterBeanResolver(DfSql2EntityMeta sql2entityMeta, File sqlFile, AppData schemaData) {
        _sql2entityMeta = sql2entityMeta;
        _sqlFile = sqlFile;
        _schemaData = schemaData;
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    /**
     * Extract the meta data of parameter bean.
     * @param sql Target SQL. (NotNull and NotEmpty)
     * @return the meta data of parameter bean. (NullAllowed: If it returns null, it means 'not found'.)
     */
    public DfPmbMetaData extractPmbMetaData(String sql) {
        final String parameterBeanName = getParameterBeanName(sql);
        if (parameterBeanName == null) {
            return null;
        }
        final DfPmbMetaData pmbMetaData = new DfPmbMetaData();
        processClassHeader(sql, parameterBeanName, pmbMetaData);
        processParameterProperty(sql, parameterBeanName, pmbMetaData);
        pmbMetaData.adjustPropertyMetaFinally(_schemaData);

        filterAlternateBooleanMethod(pmbMetaData);
        if (!_alternateBooleanMethodNameSet.isEmpty()) {
            // copy and clear the collection just in case
            final Set<String> set = new LinkedHashSet<String>(_alternateBooleanMethodNameSet);
            pmbMetaData.setAlternateMethodBooleanNameSet(set);
            _alternateBooleanMethodNameSet.clear();
        }
        return pmbMetaData;
    }

    protected void filterAlternateBooleanMethod(DfPmbMetaData pmbMetaData) {
        if (_alternateBooleanMethodNameSet.isEmpty()) {
            return;
        }
        for (String reservBooleanMethod : _reservBooleanMethodList) {
            if (_alternateBooleanMethodNameSet.contains(reservBooleanMethod)) {
                _alternateBooleanMethodNameSet.remove(reservBooleanMethod);
            }
        }
        final Map<String, String> propertyNameTypeMap = pmbMetaData.getPropertyNameTypeMap();
        for (String propertyName : propertyNameTypeMap.keySet()) {
            final String getterName = "get" + Srl.initCap(propertyName);
            if (_alternateBooleanMethodNameSet.contains(getterName)) {
                _alternateBooleanMethodNameSet.remove(getterName);
            }
            final String isName = "is" + Srl.initCap(propertyName);
            if (_alternateBooleanMethodNameSet.contains(isName)) {
                _alternateBooleanMethodNameSet.remove(isName);
            }
        }
    }

    // ===================================================================================
    //                                                                        Class Header
    //                                                                        ============
    protected void processClassHeader(String sql, String parameterBeanName, DfPmbMetaData pmbMetaData) {
        final String delimiter = "extends";
        final int idx = parameterBeanName.indexOf(delimiter);
        {
            String className = (idx >= 0) ? parameterBeanName.substring(0, idx) : parameterBeanName;
            className = className.trim();
            className = resolvePmbNameIfNeeds(className, _sqlFile);
            pmbMetaData.setClassName(className);
        }
        if (idx >= 0) {
            final String superClassName = parameterBeanName.substring(idx + delimiter.length()).trim();
            pmbMetaData.setSuperClassName(superClassName);
            resolveSuperClassSimplePagingBean(pmbMetaData);
        }
    }

    protected void resolveSuperClassSimplePagingBean(DfPmbMetaData pmbMetaData) {
        final String superClassName = pmbMetaData.getSuperClassName();
        if (Srl.endsWithIgnoreCase(superClassName, "Paging") // main
                || Srl.equalsIgnoreCase(superClassName, "SPB")) { // an old style for compatibility before 0.9.7.5
            pmbMetaData.setSuperClassName("SimplePagingBean");
            if (Srl.equalsIgnoreCase(superClassName, "ManualPaging")) {
                pmbMetaData.setPagingType(DfPagingType.MANUAL);
            } else if (Srl.equalsIgnoreCase(superClassName, "AutoPaging")) {
                pmbMetaData.setPagingType(DfPagingType.AUTO);
            } else {
                pmbMetaData.setPagingType(DfPagingType.UNKNOWN);
            }
        }
    }

    // ===================================================================================
    //                                                                  Parameter Property
    //                                                                  ==================
    protected void processParameterProperty(String sql, String parameterBeanName, DfPmbMetaData pmbMetaData) {
        final Map<String, String> propertyNameTypeMap = new LinkedHashMap<String, String>();
        final Map<String, String> propertyNameOptionMap = new LinkedHashMap<String, String>();
        final Set<String> autoDetectedPropertyNameSet = new LinkedHashSet<String>();
        pmbMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
        pmbMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
        pmbMetaData.setAutoDetectedPropertyNameSet(autoDetectedPropertyNameSet);
        final List<String> parameterBeanElement = getParameterBeanPropertyTypeList(sql);
        final String autoDetectMark = "AutoDetect";
        for (String element : parameterBeanElement) {
            element = element.trim();
            if (element.equalsIgnoreCase(autoDetectMark)) {
                processAutoDetect(sql, propertyNameTypeMap, propertyNameOptionMap, autoDetectedPropertyNameSet);
                break;
            }
        }
        for (String element : parameterBeanElement) {
            final String nameDelimiter = " ";
            final String optionDelimiter = ":";
            element = element.trim();
            if (autoDetectMark.equals(element)) {
                continue; // because of already resolved
            }
            final int optionIndex = element.indexOf(optionDelimiter);
            final String propertyDef;
            final String optionDef;
            if (optionIndex > 0) {
                propertyDef = element.substring(0, optionIndex).trim();
                optionDef = element.substring(optionIndex + optionDelimiter.length()).trim();
            } else {
                propertyDef = element;
                optionDef = null;
            }
            final int nameIndex = propertyDef.lastIndexOf(nameDelimiter);
            if (nameIndex <= 0) {
                String msg = "The parameter bean element should be [typeName propertyName].";
                msg = msg + " But: element=" + element + " srcFile=" + _sqlFile;
                throw new IllegalStateException(msg);
            }
            // ParameterBean has the "import" clause of language-embedded utility
            final String typeName = resolvePackageNameExceptUtil(propertyDef.substring(0, nameIndex).trim());
            final String propertyName = propertyDef.substring(nameIndex + nameDelimiter.length()).trim();
            if (propertyNameTypeMap.containsKey(propertyName)) {
                // means the auto-detected property is found,
                // and it should be overridden
                propertyNameTypeMap.remove(propertyName);
                propertyNameOptionMap.remove(propertyName);
            }
            propertyNameTypeMap.put(propertyName, typeName);
            if (optionDef != null) {
                propertyNameOptionMap.put(propertyName, optionDef);
            }
        }
        pmbMetaData.setSqlFile(_sqlFile);
        final Map<String, Map<String, String>> bqpMap = _bqpSetupper.extractBasicBqpMap(DfCollectionUtil
                .newArrayList(_sqlFile));
        if (!bqpMap.isEmpty()) {
            final Map<String, String> bqpElementMap = bqpMap.values().iterator().next();
            pmbMetaData.setBqpElementMap(bqpElementMap);
        }
    }

    protected String resolvePackageNameExceptUtil(String typeName) {
        return _propertyTypePackageResolver.resolvePackageNameExceptUtil(typeName);
    }

    // -----------------------------------------------------
    //                                            AutoDetect
    //                                            ----------
    protected void processAutoDetect(String sql, Map<String, String> propertyNameTypeMap,
            Map<String, String> propertyNameOptionMap, Set<String> autoDetectedPropertyNameSet) {
        final SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        final Node rootNode = analyzer.analyze();
        doProcessAutoDetect(sql, propertyNameTypeMap, propertyNameOptionMap, autoDetectedPropertyNameSet, rootNode);
    }

    protected void doProcessAutoDetect(String sql, Map<String, String> propertyNameTypeMap,
            Map<String, String> propertyNameOptionMap, Set<String> autoDetectedPropertyNameSet, Node node) {
        // only bind variable comment is supported
        // because simple specification is very important here
        if (node instanceof BindVariableNode) {
            final BindVariableNode bindNode = (BindVariableNode) node;
            processAutoDetectBindNode(sql, propertyNameTypeMap, propertyNameOptionMap, autoDetectedPropertyNameSet,
                    bindNode);
        } else if (node instanceof IfNode) {
            final IfNode ifNode = (IfNode) node;
            // *IF comment is unsupported about auto-detection
            //doProcessAutoDetectIfNode(sql, propertyNameTypeMap, propertyNameOptionMap, ifNode);

            // process alternate boolean methods
            // which is supported with auto-detect
            doProcessAlternateBooleanMethodIfNode(sql, ifNode);
        }
        for (int i = 0; i < node.getChildSize(); i++) {
            final Node childNode = node.getChild(i);

            // recursive call
            doProcessAutoDetect(sql, propertyNameTypeMap, propertyNameOptionMap, autoDetectedPropertyNameSet, childNode);
        }
    }

    protected void processAutoDetectBindNode(String sql, Map<String, String> propertyNameTypeMap,
            Map<String, String> propertyNameOptionMap, Set<String> autoDetectedPropertyNameSet,
            BindVariableNode variableNode) {
        final String expression = variableNode.getExpression();
        final String testValue = variableNode.getTestValue();
        if (testValue == null) {
            return;
        }
        if (Srl.count(expression, ".") != 1) { // e.g. "pmb.memberIdList.size()"
            return;
        }
        if (!Srl.startsWith(expression, "pmb.")) {
            return;
        }
        final String propertyName = Srl.substringFirstRear(expression, "pmb.");
        if (Srl.equalsIgnoreCase(propertyName, "OutsideSqlPath", "EntityType", "ProcedureName", "EscapeStatement",
                "CalledBySelect", "FetchStartIndex", "FetchSize", "FetchPageNumber", "PageStartIndex", "PageEndIndex",
                "SafetyMaxResultSize", "ParameterMap", "OrderByClause", "OrderByComponent")) {
            // reservation names should be skipped
            // (properties for TypedParameterBean and SimplePagingBean and so on...)
            return;
        }
        final String typeName = derivePropertyTypeFromTestValue(testValue);
        propertyNameTypeMap.put(propertyName, typeName); // override if same one exists
        autoDetectedPropertyNameSet.add(propertyName);
        final String option = variableNode.getOptionDef();
        // add option if it exists
        // so it is enough to set an option to only one bind variable comment
        // if several bind variable comments for the same property exist
        if (Srl.is_NotNull_and_NotTrimmedEmpty(option)) {
            propertyNameOptionMap.put(propertyName, option);
        } else {
            final String parsedOption = derivePropertyOptionFromTestValue(testValue);
            if (Srl.is_NotNull_and_NotTrimmedEmpty(parsedOption)) {
                propertyNameOptionMap.put(propertyName, parsedOption);
            }
        }
    }

    protected String derivePropertyTypeFromTestValue(String testValue) {
        final String plainType = doDerivePropertyTypeFromTestValue(testValue);
        return resolvePackageNameExceptUtil(switchPlainTypeNameIfCSharp(plainType));
    }

    protected String doDerivePropertyTypeFromTestValue(String testValue) { // test point
        if (testValue == null) {
            String msg = "The argument 'testValue' should be not null.";
            throw new IllegalArgumentException(msg);
        }
        final String plainTypeName;
        if (Srl.startsWithIgnoreCase(testValue, "date '", "date'")) {
            plainTypeName = "Date";
        } else if (Srl.startsWithIgnoreCase(testValue, "timestamp '", "timestamp'")) {
            plainTypeName = "Timestamp";
        } else if (Srl.startsWithIgnoreCase(testValue, "time '", "time'")) {
            plainTypeName = "Time";
        } else {
            if (Srl.isQuotedSingle(testValue)) {
                final String unquoted = Srl.unquoteSingle(testValue);
                Timestamp timestamp = null;
                Time time = null;
                try {
                    timestamp = DfTypeUtil.toTimestamp(unquoted);
                } catch (ParseTimestampException ignored) {
                    try {
                        time = DfTypeUtil.toTime(unquoted);
                    } catch (ParseTimeException andIgnored) {
                    }
                }
                if (timestamp != null) {
                    final String timeParts = DfTypeUtil.toString(timestamp, "HH:mm:ss.SSS");
                    if (timeParts.equals("00:00:00.000")) {
                        plainTypeName = "Date";
                    } else {
                        plainTypeName = "Timestamp";
                    }
                } else if (time != null) {
                    plainTypeName = "Time";
                } else {
                    plainTypeName = "String";
                }
            } else if (Srl.isQuotedAnything(testValue, "(", ")")) {
                final String unquoted = Srl.unquoteAnything(testValue, "(", ")");
                final List<String> elementList = Srl.splitListTrimmed(unquoted, ",");
                if (elementList.size() > 0) {
                    final String firstElement = elementList.get(0);
                    // InScope for Date is unsupported at this analyzing
                    if (Srl.isQuotedSingle(firstElement)) {
                        plainTypeName = "List<String>";
                    } else {
                        final String elementType = doDeriveNonQuotedLiteralTypeFromTestValue(firstElement);
                        plainTypeName = "List<" + elementType + ">";
                    }
                } else {
                    plainTypeName = "List<String>";
                }
            } else {
                plainTypeName = doDeriveNonQuotedLiteralTypeFromTestValue(testValue);
            }
        }
        return plainTypeName;
    }

    protected String doDeriveNonQuotedLiteralTypeFromTestValue(String testValue) {
        final String plainTypeName;
        if (Srl.contains(testValue, ".")) {
            BigDecimal decimalValue = null;
            try {
                decimalValue = DfTypeUtil.toBigDecimal(testValue);
            } catch (NumberFormatException ignored) {
            }
            if (decimalValue != null) {
                plainTypeName = "BigDecimal";
            } else { // means unknown type
                plainTypeName = "String";
            }
        } else {
            Long longValue = null;
            try {
                longValue = DfTypeUtil.toLong(testValue);
            } catch (NumberFormatException ignored) {
            }
            if (longValue != null) {
                if (longValue > Long.valueOf(Integer.MAX_VALUE)) {
                    plainTypeName = "Long";
                } else {
                    plainTypeName = "Integer";
                }
            } else {
                if (testValue.equalsIgnoreCase("true") || testValue.equalsIgnoreCase("false")) {
                    plainTypeName = "Boolean";
                } else { // means unknown type
                    plainTypeName = "String";
                }
            }
        }
        return plainTypeName;
    }

    protected String switchPlainTypeNameIfCSharp(String plainTypeName) {
        final boolean csharp = getBasicProperties().isTargetLanguageCSharp();
        if (!csharp) {
            return plainTypeName;
        }
        return doSwitchPlainTypeNameIfCSharp(plainTypeName);
    }

    protected String doSwitchPlainTypeNameIfCSharp(String plainTypeName) { // test point
        if (Srl.equalsPlain(plainTypeName, "BigDecimal")) {
            return "decimal?";
        } else if (Srl.equalsPlain(plainTypeName, "Long")) {
            return "long?";
        } else if (Srl.equalsPlain(plainTypeName, "Integer")) {
            return "int?";
        } else if (Srl.equalsPlain(plainTypeName, "Date", "Timestamp", "Time")) {
            return "DateTime?";
        } else if (Srl.equalsPlain(plainTypeName, "Boolean")) {
            return "bool?";
        } else if (Srl.isQuotedAnything(plainTypeName, "List<", ">")) {
            final String elementType = Srl.unquoteAnything(plainTypeName, "List<", ">");
            return "IList<" + doSwitchPlainTypeNameIfCSharp(elementType) + ">";
        } else {
            return plainTypeName;
        }
    }

    protected String derivePropertyOptionFromTestValue(String testValue) { // test point
        if (Srl.isQuotedSingle(testValue)) {
            final String unquoted = Srl.unquoteSingle(testValue);
            final int count = Srl.count(unquoted, "%");
            if (Srl.endsWith(unquoted, "%") && count == 1) {
                return "likePrefix";
            } else if (Srl.startsWith(unquoted, "%") && count == 1) {
                return "likeSuffix";
            } else if (Srl.isQuotedAnything(unquoted, "%") && count == 2) {
                return "likeContain";
            } else if (count > 0) {
                return "like";
            }
        }
        return null;
    }

    // *IF comment is unsupported about auto-detection
    //protected void doProcessAutoDetectIfNode(String sql, Map<String, String> propertyNameTypeMap,
    //        Map<String, String> propertyNameOptionMap, IfNode ifNode) {
    //    final String expression = ifNode.getExpression();
    //    if (Srl.count(expression, ".") != 1) { // e.g. "pmb.memberIdList.size()"
    //        return;
    //    }
    //    if (Srl.containsAny(expression, "=", "<>", "!=", ">", "<")) {
    //        return; // unknown (type)
    //    }
    //    if (Srl.contains(expression, "()")) {
    //        return; // method type
    //    }
    //    // only Boolean type is detected
    //    final String propertyName = Srl.substringFirstRear(expression, "pmb.").trim();
    //    propertyNameTypeMap.put(propertyName, "Boolean");
    //}

    protected void doProcessAlternateBooleanMethodIfNode(String sql, IfNode ifNode) {
        final String expression = ifNode.getExpression().trim();
        if (Srl.count(expression, ".") != 1) { // e.g. "pmb.memberIdList.size()"
            return;
        }
        if (Srl.containsAny(expression, "=", "<>", "!=", ">", "<")) {
            return; // unknown (type)
        }
        if (!Srl.endsWith(expression, "()")) {
            return; // no method type
        }
        // pmb.foo() or !pmb.foo() here
        String methodName = Srl.substringFirstRear(expression, "pmb.").trim(); // -> foo()
        methodName = Srl.substringLastFront(methodName, "()"); // -> foo
        _alternateBooleanMethodNameSet.add(methodName); // filter later
    }

    // ===================================================================================
    //                                                                   Assert Definition
    //                                                                   =================
    protected void assertDuplicateEntity(String entityName, File currentSqlFile) {
        final DfCustomizeEntityInfo entityInfo = _sql2entityMeta.getEntityInfoMap().get(entityName);
        if (entityInfo == null) {
            return;
        }
        final File sqlFile = entityInfo.getSqlFile();
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The same-name customize-entities were found.");
        br.addItem("CustomizeEntity");
        br.addElement(entityName);
        br.addItem("SQL Files");
        br.addElement(sqlFile);
        br.addElement(currentSqlFile);
        final String msg = br.buildExceptionMessage();
        throw new DfCustomizeEntityDuplicateException(msg);
    }

    protected void assertDuplicateParameterBean(String pmbName, File currentSqlFile) {
        final DfPmbMetaData metaData = _sql2entityMeta.getPmbMetaDataMap().get(pmbName);
        if (metaData == null) {
            return;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The same-name parameter-beans were found.");
        br.addItem("ParameterBean");
        br.addElement(pmbName);
        br.addItem("SQL Files");
        br.addElement(metaData.getSqlFile());
        br.addElement(currentSqlFile);
        final String msg = br.buildExceptionMessage();
        throw new DfParameterBeanDuplicateException(msg);
    }

    // ===================================================================================
    //                                                                           Analyzing
    //                                                                           =========
    protected String getParameterBeanName(final String sql) {
        return _outsideSqlMarkAnalyzer.getParameterBeanName(sql);
    }

    protected List<String> getParameterBeanPropertyTypeList(final String sql) {
        return _outsideSqlMarkAnalyzer.getParameterBeanPropertyTypeList(sql);
    }

    protected String resolvePmbNameIfNeeds(String className, File file) {
        return _sqlFileNameResolver.resolvePmbNameIfNeeds(className, file.getName());
    }

    // ===================================================================================
    //                                                                          SQL Helper
    //                                                                          ==========
    protected String removeBlockComment(final String sql) {
        return Srl.removeBlockComment(sql);
    }

    protected String removeLineComment(final String sql) {
        return Srl.removeLineComment(sql); // with removing CR
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DBDef currentDBDef() {
        return getBasicProperties().getCurrentDBDef();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }
}
