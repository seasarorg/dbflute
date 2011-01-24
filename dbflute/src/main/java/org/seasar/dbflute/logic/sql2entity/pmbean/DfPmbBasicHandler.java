package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfTypeMappingProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPmbBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The meta data of parameter bean. */
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap;

    // helper
    protected final DfColumnHandler _columnHandler = new DfColumnHandler();
    protected final DfBehaviorQueryPathSetupper _bqpSetupper = new DfBehaviorQueryPathSetupper();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbBasicHandler(Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                           Meta Data
    //                                                                           =========
    public boolean isExistPmbMetaData() {
        return _pmbMetaDataMap != null && !_pmbMetaDataMap.isEmpty();
    }

    public Collection<DfPmbMetaData> getPmbMetaDataList() {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty.";
            throw new IllegalStateException(msg);
        }
        return _pmbMetaDataMap.values();
    }

    public String getSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (!hasSuperClassDefinition(className)) {
            return "";
        }
        final DfPmbMetaData metaData = findPmbMetaData(className);
        String superClassName = metaData.getSuperClassName();
        if (DfBuildProperties.getInstance().isVersionJavaOverNinety()) { // as patch for 90
            if (superClassName.contains("SimplePagingBean")) {
                superClassName = "org.seasar.dbflute.cbean.SimplePagingBean";
            }
        }
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return " " + languageDependencyInfo.getGrammarInfo().getExtendsStringMark() + " " + superClassName;
    }

    public boolean hasSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        String superClassName = metaData.getSuperClassName();
        return superClassName != null && superClassName.trim().length() > 0;
    }

    public boolean hasPagingExtension(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (!hasSuperClassDefinition(className)) {
            return false;
        }
        final DfPmbMetaData metaData = findPmbMetaData(className);
        final String superClassName = metaData.getSuperClassName();
        return superClassName.contains("Paging");
    }

    public boolean hasPmbMetaDataCheckSafetyResult(String className) {
        final String definition = getSuperClassDefinition(className);
        return definition.contains("SimplePagingBean");
    }

    public Map<String, String> getPropertyNameOptionMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameOptionMap();
    }

    protected DfPmbMetaData findPmbMetaData(String className) {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            String msg = "The className has no meta data: className=" + className;
            throw new IllegalStateException(msg);
        }
        return metaData;
    }

    public Set<String> getPropertySet(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return getPropertyNameTypeMap(className).keySet();
    }

    public String getPropertyType(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPropertyNameTypeMap(className).get(propertyName);
    }

    protected Map<String, String> getPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameTypeMap();
    }

    public String getPropertyTypeRemovedCSharpNullable(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final String propertyType = getPropertyType(className, propertyName);
        return propertyType.endsWith("?") ? Srl.substringLastFront(propertyType, "?") : propertyType;
    }

    public boolean isPmbMetaDataPropertyJavaNativeStringObject(String className, String propertyName) {
        final String propertyType = getPropertyType(className, propertyName);
        final DfTypeMappingProperties prop = getProperties().getTypeMappingProperties();
        return prop.isJavaNativeStringObject(propertyType);
    }

    public boolean isPmbMetaDataPropertyJavaNativeNumberObject(String className, String propertyName) {
        final String propertyType = getPropertyType(className, propertyName);
        final DfTypeMappingProperties prop = getProperties().getTypeMappingProperties();
        return prop.isJavaNativeNumberObject(propertyType);
    }

    public boolean isPmbMetaDataPropertyJavaNativeBooleanObject(String className, String propertyName) {
        final String propertyType = getPropertyType(className, propertyName);
        final DfTypeMappingProperties prop = getProperties().getTypeMappingProperties();
        return prop.isJavaNativeBooleanObject(propertyType);
    }

    // ===================================================================================
    //                                                                          Typed Info
    //                                                                          ==========
    public boolean isTypedParameterBean(String className) {
        final DfPmbMetaData pmbMetaData = findPmbMetaData(className);
        final File sqlFile = pmbMetaData.getSqlFile();
        if (sqlFile == null) {
            return false;
        }
        return isTypedListHandling(className) || isTypedCursorHandling(className) || isTypedPagingHandling(className)
                || isTypedExecuteHandling(className);
    }

    public boolean isTypedListHandling(String className) {
        final DfCustomizeEntityInfo customizeEntityInfo = findCustomizeEntityInfo(className);
        return customizeEntityInfo != null ? customizeEntityInfo.isNormalHandling() : false;
    }

    public boolean isTypedCursorHandling(String className) {
        final DfCustomizeEntityInfo customizeEntityInfo = findCustomizeEntityInfo(className);
        return customizeEntityInfo != null ? customizeEntityInfo.isCursorHandling() : false;
    }

    public boolean isTypedPagingHandling(String className) {
        final DfCustomizeEntityInfo customizeEntityInfo = findCustomizeEntityInfo(className);
        return customizeEntityInfo != null ? hasPagingExtension(className) : false;
    }

    public boolean isTypedAutoPagingHandling(String className) {
        if (!isTypedPagingHandling(className)) {
            return false;
        }
        return false; // TODO;
    }

    public boolean isTypedManualPagingHandling(String className) {
        if (!isTypedPagingHandling(className)) {
            return false;
        }
        return false; // TODO;
    }

    public boolean isTypedExecuteHandling(String className) {
        final DfCustomizeEntityInfo customizeEntityInfo = findCustomizeEntityInfo(className);
        if (customizeEntityInfo != null) {
            return false;
        }
        final Map<String, String> elementMap = findBqpElementMap(className);
        final String bqpPath = elementMap.get("behaviorQueryPath");
        if (bqpPath == null) {
            String msg = "The element value 'behaviorQueryPath' should not be null: " + elementMap;
            throw new IllegalStateException(msg);
        }
        return !bqpPath.startsWith("select");
    }

    protected DfCustomizeEntityInfo findCustomizeEntityInfo(String className) {
        final DfPmbMetaData pmbMetaData = findPmbMetaData(className);
        return pmbMetaData.getCustomizeEntityInfo();
    }

    protected Map<String, String> findBqpElementMap(String className) {
        final DfPmbMetaData pmbMetaData = findPmbMetaData(className);
        final File sqlFile = pmbMetaData.getSqlFile();
        if (sqlFile == null) {
            return null;
        }
        final Map<String, Map<String, String>> bqpMap = _bqpSetupper.extractBasicBqpMap(DfCollectionUtil
                .newArrayList(sqlFile));
        if (bqpMap.isEmpty()) {
            return null; // means the file was not under behavior query path
        }
        return bqpMap.get(0); // must be only one
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    public boolean isForProcedure(String className) {
        return findPmbMetaData(className).getProcedureName() != null;
    }

    public String getProcedureName(String className) {
        return findPmbMetaData(className).getProcedureName();
    }

    public boolean isProcedureCalledBySelect(String className) {
        final DfPmbMetaData pmbMetaData = findPmbMetaData(className);
        return pmbMetaData.isProcedureCalledBySelect();
    }

    public boolean isRefCustomizeEntity(String className) {
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        return metaData.isRefCustomizeEntity();
    }

    public boolean hasProcedureOverload(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final Map<String, DfProcedureColumnMetaInfo> columnInfoMap = getPropertyNameColumnInfoMap(className);
        if (columnInfoMap == null) {
            return false;
        }
        final Collection<DfProcedureColumnMetaInfo> values = columnInfoMap.values();
        for (DfProcedureColumnMetaInfo columnInfo : values) {
            if (columnInfo.getOverloadNo() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isPropertyOptionProcedureParameterIn(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnIn.toString());
    }

    public boolean isPropertyOptionProcedureParameterOut(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnInOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnReturn.toString());
    }

    public boolean isPropertyOptionProcedureParameterResult(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnResult.toString());
    }

    public String getPropertyColumnName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        return getPropertyNameColumnNameMap(className).get(propertyName);
    }

    protected Map<String, String> getPropertyNameColumnNameMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnNameMap();
    }

    // -----------------------------------------------------
    //                                Handling Determination
    //                                ----------------------
    public boolean needsStringClobHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeStringClob(metaInfo.getDbTypeName());
    }

    public boolean needsBytesOidHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeBytesOid(metaInfo.getDbTypeName());
    }

    public boolean needsFixedLengthStringHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeFixedLengthString(metaInfo.getDbTypeName());
    }

    public boolean needsObjectBindingBigDecimalHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeObjectBindingBigDecimal(metaInfo.getDbTypeName());
    }

    public boolean needsOracleArrayHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        if (!getBasicProperties().isDatabaseOracle()
                || !getLittleAdjustmentProperties().isAvailableDatabaseNativeJDBC()) {
            return false;
        }
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        return metaInfo != null && metaInfo.hasTypeArrayInfo();
    }

    public boolean needsOracleStructHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        if (!getBasicProperties().isDatabaseOracle()
                || !getLittleAdjustmentProperties().isAvailableDatabaseNativeJDBC()) {
            return false;
        }
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
        return metaInfo != null && metaInfo.hasTypeStructInfo();
    }

    // -----------------------------------------------------
    //                                           Oracle Type
    //                                           -----------
    public String getProcedureParameterOracleArrayTypeName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(className, propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayInfo()) {
            return columnInfo.getTypeArrayInfo().getTypeSqlName();
        }
        return "";
    }

    public String getProcedureParameterOracleArrayElementTypeName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(className, propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayInfo()) {
            return columnInfo.getTypeArrayInfo().getElementType();
        }
        return "";
    }

    public String getProcedureParameterOracleArrayElementJavaNative(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(className, propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayElementJavaNative()) {
            return columnInfo.getTypeArrayInfo().getElementJavaNative();
        }
        return "Object"; // as default
    }

    public String getProcedureParameterOracleArrayElementJavaNativeTypeLiteral(String className, String propertyName) {
        final String javaNative = getProcedureParameterOracleArrayElementJavaNative(className, propertyName);
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getClassTypeLiteral(Srl.substringFirstFrontIgnoreCase(javaNative, "<"));
    }

    public String getProcedureParameterOracleStructTypeName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(className, propertyName);
        if (columnInfo != null && columnInfo.hasTypeStructInfo()) {
            return columnInfo.getTypeStructInfo().getTypeSqlName();
        }
        return "";
    }

    public String getProcedureParameterOracleStructEntityType(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(className, propertyName);
        if (columnInfo != null && columnInfo.hasTypeStructEntityType()) {
            return columnInfo.getTypeStructInfo().getEntityType();
        }
        return "Object"; // as default
    }

    public String getProcedureParameterOracleStructEntityTypeTypeLiteral(String className, String propertyName) {
        final String entityType = getProcedureParameterOracleStructEntityType(className, propertyName);
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getClassTypeLiteral(Srl.substringFirstFrontIgnoreCase(entityType, "<"));
    }

    // -----------------------------------------------------
    //                                           Column Info
    //                                           -----------
    protected DfProcedureColumnMetaInfo getProcedureColumnInfo(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final Map<String, DfProcedureColumnMetaInfo> columnInfoMap = getPropertyNameColumnInfoMap(className);
        if (columnInfoMap != null) {
            return columnInfoMap.get(propertyName);
        }
        return null;
    }

    protected Map<String, DfProcedureColumnMetaInfo> getPropertyNameColumnInfoMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnInfoMap();
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public boolean hasPropertyOptionOriginalOnlyOneSetter(String className, String propertyName) {
        return hasPropertyOptionAnyLikeSearch(className, propertyName)
                || hasPropertyOptionAnyFromTo(className, propertyName);
    }

    // -----------------------------------------------------
    //                                           LikeSeasrch
    //                                           -----------
    public boolean hasPropertyOptionAnyLikeSearch(String className) {
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyLikeSearch(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyLikeSearch(String className, String propertyName) {
        return isPropertyOptionLikeSearch(className, propertyName)
                || isPropertyOptionPrefixSearch(className, propertyName)
                || isPropertyOptionContainSearch(className, propertyName)
                || isPropertyOptionSuffixSearch(className, propertyName);
    }

    public boolean isPropertyOptionLikeSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "like");
    }

    public boolean isPropertyOptionPrefixSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likePrefix");
    }

    public boolean isPropertyOptionContainSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likeContain");
    }

    public boolean isPropertyOptionSuffixSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likeSuffix");
    }

    // -----------------------------------------------------
    //                                                FromTo
    //                                                ------
    public boolean hasPropertyOptionAnyFromTo(String className) {
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyFromTo(String className, String propertyName) {
        return isPropertyOptionFromDate(className, propertyName) || isPropertyOptionToDate(className, propertyName);
    }

    public boolean isPropertyOptionFromDate(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "fromDate");
    }

    public boolean isPropertyOptionToDate(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "toDate");
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    public boolean isPropertyOptionClassification(String className, String propertyName, AppData appData) {
        if (isPropertyOptionSpecifiedClassification(className, propertyName)) {
            return true;
        }
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null && column.hasClassification();
    }

    protected boolean isPropertyOptionSpecifiedClassification(String className, String propertyName) {
        final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
        return obj.isPmbMetaDataPropertyOptionClassification();
    }

    public String getPropertyOptionClassificationName(String className, String propertyName, AppData appData) {
        // should be called when it has classification
        if (isPropertyOptionSpecifiedClassification(className, propertyName)) {
            final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
            return obj.getPmbMetaDataPropertyOptionClassificationName();
        }
        final Column column = getPropertyOptionClassificationColumn(className, propertyName, appData);
        return column.getClassificationName();
    }

    public List<Map<String, String>> getPropertyOptionClassificationMapList(String className, String propertyName,
            AppData appData) {
        // should be called when it has classification
        if (isPropertyOptionSpecifiedClassification(className, propertyName)) {
            final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
            return obj.getPmbMetaDataPropertyOptionClassificationMapList();
        }
        final Column column = getPropertyOptionClassificationColumn(className, propertyName, appData);
        return column.getClassificationMapList();
    }

    protected Column getPropertyOptionClassificationColumn(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        if (column == null) { // no way
            String msg = "The reference column should exist at this timing:";
            msg = msg + " property=" + className + "." + propertyName;
            throw new IllegalStateException(msg);
        }
        if (!column.hasClassification()) { // no way
            String msg = "The reference column should have a classification at this timing:";
            msg = msg + " property=" + className + "." + propertyName + " column=" + column;
            throw new IllegalStateException(msg);
        }
        return column;
    }

    // -----------------------------------------------------
    //                                             Reference
    //                                             ---------
    public boolean hasPropertyOptionReference(String className) {
        DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public String getPropertyRefName(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getName() : "";
    }

    public String getPropertyRefAlias(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getAliasExpression() : "";
    }

    public String getPropertyRefLineDisp(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? "{" + column.getColumnDefinitionLineDisp() + "}" : "";
    }

    public boolean isPropertyRefColumnChar(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.isJdbcTypeChar() : false;
    }

    public String getPropertyRefDbType(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getDbType() : "";
    }

    public String getPropertyRefSize(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getColumnSizeSettingExpression() : "";
    }

    protected Column getPropertyOptionReferenceColumn(String className, String propertyName, AppData appData) {
        final DfPmbPropertyOptionReference reference = createPropertyOptionReference(className, propertyName);
        return reference.getPmbMetaDataPropertyOptionReferenceColumn(appData);
    }

    // -----------------------------------------------------
    //                                               Display
    //                                               -------
    public String getPropertyRefColumnInfo(String className, String propertyName, AppData appData) {
        if (isForProcedure(className)) {
            final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(className, propertyName);
            return metaInfo != null ? ": {" + metaInfo.getColumnDefinitionLineDisp() + "}" : "";
        }
        final StringBuilder sb = new StringBuilder();
        final String optionDisp = getPropertyOptionDisp(className, propertyName);
        sb.append(optionDisp);
        final String name = getPropertyRefName(className, propertyName, appData);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(name)) { // basically normal parameter-bean
            final String alias = getPropertyRefAlias(className, propertyName, appData);
            final String lineDisp = getPropertyRefLineDisp(className, propertyName, appData);
            sb.append(" :: refers to " + alias + name + ": " + lineDisp);
        }
        return sb.toString();
    }

    protected String getPropertyOptionDisp(String className, String propertyName) {
        final String option = findPropertyOption(className, propertyName);
        return option != null ? ":" + option : "";
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean containsPropertyOption(String className, String propertyName, String option) {
        final String specified = findPropertyOption(className, propertyName);
        if (specified == null) {
            return false;
        }
        final List<String> splitList = splitOption(specified);
        for (String element : splitList) {
            if (element.trim().equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> splitOption(String option) {
        return DfPmbPropertyOptionFinder.splitOption(option);
    }

    protected DfPmbPropertyOptionClassification createPropertyOptionClassification(String className, String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        final DfClassificationProperties clsProp = getClassificationProperties();
        return new DfPmbPropertyOptionClassification(className, propertyName, clsProp, finder);
    }

    protected DfPmbPropertyOptionReference createPropertyOptionReference(String className, String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        return new DfPmbPropertyOptionReference(className, propertyName, finder);
    }

    protected String findPropertyOption(String className, String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        return finder.findPmbMetaDataPropertyOption(className, propertyName);
    }

    protected DfPmbPropertyOptionFinder createPropertyOptionFinder(String className, String propertyName) {
        return new DfPmbPropertyOptionFinder(className, propertyName, _pmbMetaDataMap);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    public String getSlashPath(File file) {
        return replaceString(file.getPath(), getFileSeparator(), "/");
    }

    public String getFileSeparator() {
        return File.separator;
    }

    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentPmbMetaDataClassPropertyName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
    }

    protected void assertArgumentPmbMetaDataClassName(String className) {
        if (className == null || className.trim().length() == 0) {
            String msg = "The className should not be null or empty: [" + className + "]";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertArgumentPmbMetaDataPropertyName(String propertyName) {
        if (propertyName == null || propertyName.trim().length() == 0) {
            String msg = "The propertyName should not be null or empty: [" + propertyName + "]";
            throw new IllegalArgumentException(msg);
        }
    }
}
