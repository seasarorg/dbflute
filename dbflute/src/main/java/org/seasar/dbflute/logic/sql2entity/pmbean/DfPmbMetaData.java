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
package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.friends.velocity.DfGenerator;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlFile;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPmbMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                            Basic Info
    //                                            ----------
    protected String _className;
    protected String _superClassName;
    protected DfPagingType _pagingType; // null means no paging
    protected Map<String, String> _propertyNameTypeMap;
    protected Map<String, String> _propertyNameOptionMap;
    protected Set<String> _autoDetectedPropertyNameSet;
    protected Set<String> _alternateBooleanMethodNameSet;
    protected DfOutsideSqlFile _outsideSqlFile;
    protected Map<String, String> _bqpElementMap;
    protected DfCustomizeEntityInfo _customizeEntityInfo;

    public enum DfPagingType {
        UNKNOWN, MANUAL, AUTO
    }

    // -----------------------------------------------------
    //                                             Procedure
    //                                             ---------
    // only when for procedure
    protected String _procedureName;
    protected Map<String, String> _propertyNameColumnNameMap;
    protected Map<String, DfProcedureColumnMetaInfo> _propertyNameColumnInfoMap;
    protected boolean _procedureCalledBySelect;
    protected boolean _procedureRefCustomizeEntity;

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected final DfColumnHandler _columnHandler = new DfColumnHandler();

    // ===================================================================================
    //                                                                          Basic Info
    //                                                                          ==========
    public String getBusinessName() {
        final String pmbTitleName;
        {
            final String pmbClassName = _className;
            if (pmbClassName.endsWith("Pmb")) {
                pmbTitleName = Srl.substringLastFront(pmbClassName, "Pmb");
            } else {
                pmbTitleName = pmbClassName;
            }
        }
        return pmbTitleName;
    }

    public String getBaseClassName() {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final String basePrefix = getBasicProperties().getBasePrefix();
        return projectPrefix + basePrefix + _className;
    }

    public String getExtendedClassName() {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        return projectPrefix + _className;
    }

    public String getEntityClassName() {
        return getBqpElement(DfBehaviorQueryPathSetupper.KEY_ENTITY_NAME);
    }

    public String getBehaviorClassName() {
        return getBqpElement(DfBehaviorQueryPathSetupper.KEY_BEHAVIOR_NAME);
    }

    public String getBehaviorQueryPath() { // resolved sub-directory
        final String subDirPath = getBqpElement(DfBehaviorQueryPathSetupper.KEY_SUB_DIRECTORY_PATH);
        final StringBuilder sb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(subDirPath)) {
            sb.append(Srl.replace(subDirPath, "/", ":")).append(":");
        }
        final String plainPath = getBqpElement(DfBehaviorQueryPathSetupper.KEY_BEHAVIOR_QUERY_PATH);
        sb.append(plainPath);
        return sb.toString();
    }

    public String getSqlTitle() {
        return getBqpElement(DfBehaviorQueryPathSetupper.KEY_TITLE);
    }

    public String getSqlDescription() {
        return getBqpElement(DfBehaviorQueryPathSetupper.KEY_DESCRIPTION);
    }

    protected String getBqpElement(String key) {
        return isRelatedToBehaviorQuery() ? _bqpElementMap.get(key) : null;
    }

    public boolean hasSuperClassDefinition() {
        return _superClassName != null && _superClassName.trim().length() > 0;
    }

    public boolean hasPagingExtension() {
        return hasSuperClassDefinition() && _pagingType != null;
    }

    public boolean isRelatedToBehaviorQuery() {
        return _bqpElementMap != null;
    }

    public boolean isRelatedToCustomizeEntity() {
        return _customizeEntityInfo != null;
    }

    public boolean isRelatedToProcedure() {
        return _procedureName != null;
    }

    public boolean isPropertyTypeList(String propertyName) {
        final String propertyType = getPropertyNameTypeMap().get(propertyName);
        return Srl.containsAll(propertyType, "List<", ">");
    }

    // ===================================================================================
    //                                                                  TypedParameterBean
    //                                                                  ==================
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public boolean isTypedParameterBean() {
        return isTypedSelectPmb() || isTypedUpdatePmb();
    }

    public boolean isTypedSelectPmb() {
        return isTypedListHandling() || isTypedEntityHandling() || isTypedCursorHandling() || isTypedPagingHandling();
    }

    public boolean isTypedUpdatePmb() {
        return isTypedExecuteHandling();
    }

    public boolean isTypedReturnEntityPmb() {
        if (isRelatedToCustomizeEntity() && _customizeEntityInfo.isScalarHandling()) {
            return false;
        }
        return isTypedListHandling() || isTypedEntityHandling() || isTypedPagingHandling();
    }

    public boolean isTypedReturnCustomizeEntityPmb() {
        return isTypedReturnEntityPmb() && !_customizeEntityInfo.isDomainHandling();
    }

    public boolean isTypedReturnDomainEntityPmb() {
        return isTypedReturnEntityPmb() && _customizeEntityInfo.isDomainHandling();
    }

    // -----------------------------------------------------
    //                                              Handling
    //                                              --------
    public boolean isTypedListHandling() {
        if (!isRelatedToBehaviorQuery() || !isRelatedToCustomizeEntity()) {
            return false;
        }
        if (isTypedPagingHandling()) {
            return false;
        }
        return _customizeEntityInfo.isResultHandling();
    }

    public boolean isTypedEntityHandling() {
        if (!isRelatedToBehaviorQuery() || !isRelatedToCustomizeEntity()) {
            return false;
        }
        // *allowed to use entity handling with paging handling
        //if (isTypedPagingHandling()) {
        //    return false;
        //}
        return _customizeEntityInfo.isResultHandling();
    }

    public boolean isTypedPagingHandling() { // abstract judgment
        return isTypedManualPagingHandling() || isTypedAutoPagingHandling();
    }

    public boolean isTypedManualPagingHandling() {
        return judgeTypedPagingHandling(DfPagingType.MANUAL);
    }

    public boolean isTypedAutoPagingHandling() {
        return judgeTypedPagingHandling(DfPagingType.AUTO);
    }

    protected boolean judgeTypedPagingHandling(DfPagingType targetType) {
        if (!isRelatedToBehaviorQuery() || !isRelatedToCustomizeEntity()) {
            return false;
        }
        if (!hasPagingExtension()) {
            return false;
        }
        if (DfPagingType.UNKNOWN.equals(_pagingType)) { // "extends Paging"
            final boolean research = researchManualPaging();
            return DfPagingType.MANUAL.equals(targetType) ? research : !research;
        } else {
            // "extends ManualPaging" or "extends AutoPaging"
            return targetType.equals(_pagingType);
        }
    }

    protected boolean researchManualPaging() {
        if (!hasPagingExtension()) {
            return false;
        }
        if (_bqpElementMap == null) {
            return false;
        }
        final String sql = _bqpElementMap.get("sql");
        if (sql == null) {
            String msg = "The element value 'sql' should not be null: " + _bqpElementMap;
            throw new IllegalStateException(msg);
        }
        if (getBasicProperties().isDatabaseMySQL()) {
            // "pmb.fetchSize" is also treated as case insensitive
            // because the expression can be "pmb.FetchSize" on DBFlute.NET(C#)
            return Srl.containsAllIgnoreCase(sql, "limit", "pmb.fetchSize");
        } else if (getBasicProperties().isDatabasePostgreSQL()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "limit");
        } else if (getBasicProperties().isDatabaseOracle()) {
            return Srl.containsAllIgnoreCase(sql, "rownum");
        } else if (getBasicProperties().isDatabaseDB2()) {
            return Srl.containsAllIgnoreCase(sql, "row_number()");
        } else if (getBasicProperties().isDatabaseSQLServer()) {
            return Srl.containsAllIgnoreCase(sql, "row_number()");
        } else if (getBasicProperties().isDatabaseH2()) {
            // H2 implements both limit only (same as MySQL) and offset + limit
            return Srl.containsAllIgnoreCase(sql, "offset", "limit")
                    || Srl.containsAllIgnoreCase(sql, "limit", "pmb.fetchSize");
        } else if (getBasicProperties().isDatabaseDerby()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "fetch");
        } else if (getBasicProperties().isDatabaseSQLite()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "limit");
        } else {
            return false;
        }
    }

    public boolean isTypedCursorHandling() {
        if (!isRelatedToBehaviorQuery() || !isRelatedToCustomizeEntity()) {
            return false;
        }
        return _customizeEntityInfo.isCursorHandling();
    }

    public boolean isTypedExecuteHandling() {
        if (!isRelatedToBehaviorQuery()) {
            return false;
        }
        if (isRelatedToCustomizeEntity()) {
            return false; // means select
        }
        final String bqpPath = getBehaviorQueryPath();
        return !bqpPath.startsWith("select");
    }

    // -----------------------------------------------------
    //                                          Related Info
    //                                          ------------
    public String getCustomizeEntityType() {
        if (!isRelatedToCustomizeEntity()) {
            String msg = "This parameter-bean was not related to customize entity.";
            throw new IllegalStateException(msg);
        }
        if (_customizeEntityInfo.isCursorHandling()) {
            return "Void";
        }
        if (_customizeEntityInfo.isScalarHandling()) {
            return _customizeEntityInfo.getScalarJavaNative();
        }
        final String entityClassName = _customizeEntityInfo.getEntityClassName();
        if (entityClassName == null) {
            String msg = "The class name of the customize entity was not found.";
            throw new IllegalStateException(msg);
        }
        return entityClassName;
    }

    public String buildTypedDisp() {
        final StringBuilder logSb = new StringBuilder();
        if (isTypedParameterBean()) {
            logSb.append("(typed to ");
            final StringBuilder typedSb = new StringBuilder();
            if (isTypedListHandling()) {
                typedSb.append(", list");
            }
            if (isTypedEntityHandling()) {
                typedSb.append(", entity");
            }
            if (isTypedPagingHandling()) {
                if (isTypedManualPagingHandling()) {
                    typedSb.append(", manual-paging");
                } else if (isTypedAutoPagingHandling()) {
                    typedSb.append(", auto-paging");
                } else { // basically no way
                    typedSb.append(", paging");
                }
            }
            if (isTypedCursorHandling()) {
                typedSb.append(", cursor");
            }
            if (isTypedExecuteHandling()) {
                typedSb.append(", execute");
            }
            typedSb.delete(0, ", ".length());
            typedSb.append(")");
            logSb.append(typedSb);
        }
        return logSb.toString();
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public boolean hasPropertyOptionOriginalOnlyOneSetter(String propertyName) {
        return hasPropertyOptionAnyLikeSearch(propertyName) || hasPropertyOptionAnyFromTo(propertyName);
    }

    // -----------------------------------------------------
    //                                           LikeSeasrch
    //                                           -----------
    public boolean hasPropertyOptionAnyLikeSearch() {
        final Set<String> propertyNameSet = getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyLikeSearch(propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyLikeSearch(String propertyName) {
        return isPropertyOptionLikeSearch(propertyName) || isPropertyOptionPrefixSearch(propertyName)
                || isPropertyOptionContainSearch(propertyName) || isPropertyOptionSuffixSearch(propertyName);
    }

    public boolean isPropertyOptionLikeSearch(String propertyName) {
        return containsPropertyOption(propertyName, "like");
    }

    public boolean isPropertyOptionPrefixSearch(String propertyName) {
        return containsPropertyOption(propertyName, "likePrefix");
    }

    public boolean isPropertyOptionContainSearch(String propertyName) {
        return containsPropertyOption(propertyName, "likeContain");
    }

    public boolean isPropertyOptionSuffixSearch(String propertyName) {
        return containsPropertyOption(propertyName, "likeSuffix");
    }

    // -----------------------------------------------------
    //                                                FromTo
    //                                                ------
    public boolean hasPropertyOptionAnyFromTo() {
        final Set<String> propertyNameSet = getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyFromTo(String propertyName) {
        return isPropertyOptionFromDate(propertyName) || isPropertyOptionToDate(propertyName);
    }

    public boolean isPropertyOptionFromDate(String propertyName) {
        return containsPropertyOption(propertyName, "fromDate");
    }

    public boolean isPropertyOptionToDate(String propertyName) {
        return containsPropertyOption(propertyName, "toDate");
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    public boolean isPropertyOptionClassification(String propertyName, AppData schemaData) {
        if (isPropertyOptionSpecifiedClassification(propertyName)) {
            return true;
        }
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null && column.hasClassification();
    }

    protected boolean isPropertyOptionSpecifiedClassification(String propertyName) {
        final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(propertyName);
        return obj.isPmbMetaDataPropertyOptionClassification();
    }

    public String getPropertyOptionClassificationName(String propertyName, AppData schemaData) {
        // should be called when it has classification
        if (isPropertyOptionSpecifiedClassification(propertyName)) {
            final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(propertyName);
            return obj.getPmbMetaDataPropertyOptionClassificationName();
        }
        final Column column = getPropertyOptionClassificationColumn(propertyName, schemaData);
        return column.getClassificationName();
    }

    public List<Map<String, String>> getPropertyOptionClassificationMapList(String propertyName, AppData schemaData) {
        // should be called when it has classification
        if (isPropertyOptionSpecifiedClassification(propertyName)) {
            final DfPmbPropertyOptionClassification obj = createPropertyOptionClassification(propertyName);
            return obj.getPmbMetaDataPropertyOptionClassificationMapList();
        }
        final Column column = getPropertyOptionClassificationColumn(propertyName, schemaData);
        return column.getClassificationMapList();
    }

    protected Column getPropertyOptionClassificationColumn(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        if (column == null) { // no way
            String msg = "The reference column should exist at this timing:";
            msg = msg + " property=" + _className + "." + propertyName;
            throw new IllegalStateException(msg);
        }
        if (!column.hasClassification()) { // no way
            String msg = "The reference column should have a classification at this timing:";
            msg = msg + " property=" + _className + "." + propertyName + " column=" + column;
            throw new IllegalStateException(msg);
        }
        return column;
    }

    // -----------------------------------------------------
    //                                             Reference
    //                                             ---------
    public boolean hasPropertyOptionReference() {
        final Set<String> propertyNameSet = getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(propertyName)) {
                return true;
            }
        }
        return false;
    }

    public String getPropertyRefName(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? column.getName() : "";
    }

    public String getPropertyRefAlias(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? column.getAliasExpression() : "";
    }

    public String getPropertyRefLineDisp(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? "{" + column.getColumnDefinitionLineDisp() + "}" : "";
    }

    public boolean isPropertyRefColumnChar(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? column.isJdbcTypeChar() : false;
    }

    public String getPropertyRefDbType(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? column.getDbType() : "";
    }

    public String getPropertyRefSize(String propertyName, AppData schemaData) {
        final Column column = getPropertyOptionReferenceColumn(propertyName, schemaData);
        return column != null ? column.getColumnSizeSettingExpression() : "";
    }

    protected Column getPropertyOptionReferenceColumn(String propertyName, AppData schemaData) {
        final DfPmbPropertyOptionReference reference = createPropertyOptionReference(propertyName);
        return reference.getPmbMetaDataPropertyOptionReferenceColumn(schemaData);
    }

    // -----------------------------------------------------
    //                                               Display
    //                                               -------
    public String getPropertyRefColumnInfo(String propertyName, AppData schemaData) {
        if (isRelatedToProcedure()) {
            final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
            return metaInfo != null ? ": {" + metaInfo.getColumnDefinitionLineDisp() + "}" : "";
        }
        final StringBuilder sb = new StringBuilder();
        final String optionDisp = getPropertyOptionDisp(propertyName);
        sb.append(optionDisp);
        final String name = getPropertyRefName(propertyName, schemaData);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(name)) { // basically normal parameter-bean
            final String alias = getPropertyRefAlias(propertyName, schemaData);
            final String lineDisp = getPropertyRefLineDisp(propertyName, schemaData);
            sb.append(" :: refers to " + alias + name + ": " + lineDisp);
        }
        return sb.toString();
    }

    protected String getPropertyOptionDisp(String propertyName) {
        final String option = findPropertyOption(propertyName);
        return option != null ? ":" + option : "";
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean containsPropertyOption(String propertyName, String option) {
        final String specified = findPropertyOption(propertyName);
        if (specified == null) {
            return false;
        }
        final List<String> splitList = splitOption(specified);
        for (String element : splitList) {
            if (element.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> splitOption(String option) {
        return DfPmbPropertyOptionFinder.splitOption(option);
    }

    protected DfPmbPropertyOptionClassification createPropertyOptionClassification(String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(propertyName);
        final DfClassificationProperties clsProp = getClassificationProperties();
        return new DfPmbPropertyOptionClassification(this, propertyName, clsProp, finder);
    }

    protected DfPmbPropertyOptionReference createPropertyOptionReference(String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(propertyName);
        return new DfPmbPropertyOptionReference(this, propertyName, finder);
    }

    protected String findPropertyOption(String propertyName) {
        final DfPmbPropertyOptionFinder finder = createPropertyOptionFinder(propertyName);
        return finder.findPmbMetaDataPropertyOption(propertyName);
    }

    protected DfPmbPropertyOptionFinder createPropertyOptionFinder(String propertyName) {
        return new DfPmbPropertyOptionFinder(propertyName, this);
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    public boolean hasProcedureOverload() {
        final Map<String, DfProcedureColumnMetaInfo> columnInfoMap = getPropertyNameColumnInfoMap();
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

    public boolean isPropertyOptionProcedureParameterIn(String propertyName) {
        String option = findPropertyOption(propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnIn.toString());
    }

    public boolean isPropertyOptionProcedureParameterOut(String propertyName) {
        String option = findPropertyOption(propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterInOut(String propertyName) {
        String option = findPropertyOption(propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnInOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterReturn(String propertyName) {
        String option = findPropertyOption(propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnReturn.toString());
    }

    public boolean isPropertyOptionProcedureParameterResult(String propertyName) {
        String option = findPropertyOption(propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnResult.toString());
    }

    public String getPropertyColumnName(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPropertyNameColumnNameMap().get(propertyName);
    }

    // -----------------------------------------------------
    //                                Handling Determination
    //                                ----------------------
    public boolean needsStringClobHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeStringClob(metaInfo.getDbTypeName());
    }

    public boolean needsBytesOidHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeBytesOid(metaInfo.getDbTypeName());
    }

    public boolean needsFixedLengthStringHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeFixedLengthString(metaInfo.getDbTypeName());
    }

    public boolean needsObjectBindingBigDecimalHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeObjectBindingBigDecimal(metaInfo.getDbTypeName());
    }

    public boolean needsOracleArrayHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        if (!getBasicProperties().isDatabaseOracle()
                || !getLittleAdjustmentProperties().isAvailableDatabaseNativeJDBC()) {
            return false;
        }
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        return metaInfo != null && metaInfo.hasTypeArrayInfo();
    }

    public boolean needsOracleStructHandling(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        if (!getBasicProperties().isDatabaseOracle()
                || !getLittleAdjustmentProperties().isAvailableDatabaseNativeJDBC()) {
            return false;
        }
        final DfProcedureColumnMetaInfo metaInfo = getProcedureColumnInfo(propertyName);
        return metaInfo != null && metaInfo.hasTypeStructInfo();
    }

    // -----------------------------------------------------
    //                                           Oracle Type
    //                                           -----------
    public String getProcedureParameterOracleArrayTypeName(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayInfo()) {
            return columnInfo.getTypeArrayInfo().getTypeSqlName();
        }
        return "";
    }

    public String getProcedureParameterOracleArrayElementTypeName(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayInfo()) {
            return columnInfo.getTypeArrayInfo().getElementType();
        }
        return "";
    }

    public String getProcedureParameterOracleArrayElementJavaNative(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(propertyName);
        if (columnInfo != null && columnInfo.hasTypeArrayElementJavaNative()) {
            return columnInfo.getTypeArrayInfo().getElementJavaNative();
        }
        return "Object"; // as default
    }

    public String getProcedureParameterOracleArrayElementJavaNativeTypeLiteral(String propertyName) {
        final String javaNative = getProcedureParameterOracleArrayElementJavaNative(propertyName);
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getClassTypeLiteral(Srl.substringFirstFrontIgnoreCase(javaNative, "<"));
    }

    public String getProcedureParameterOracleStructTypeName(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(propertyName);
        if (columnInfo != null && columnInfo.hasTypeStructInfo()) {
            return columnInfo.getTypeStructInfo().getTypeSqlName();
        }
        return "";
    }

    public String getProcedureParameterOracleStructEntityType(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final DfProcedureColumnMetaInfo columnInfo = getProcedureColumnInfo(propertyName);
        if (columnInfo != null && columnInfo.hasTypeStructEntityType()) {
            return columnInfo.getTypeStructInfo().getEntityType();
        }
        return "Object"; // as default
    }

    public String getProcedureParameterOracleStructEntityTypeTypeLiteral(String propertyName) {
        final String entityType = getProcedureParameterOracleStructEntityType(propertyName);
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getClassTypeLiteral(Srl.substringFirstFrontIgnoreCase(entityType, "<"));
    }

    // -----------------------------------------------------
    //                                           Column Info
    //                                           -----------
    protected DfProcedureColumnMetaInfo getProcedureColumnInfo(String propertyName) {
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final Map<String, DfProcedureColumnMetaInfo> columnInfoMap = getPropertyNameColumnInfoMap();
        if (columnInfoMap != null) {
            return columnInfoMap.get(propertyName);
        }
        return null;
    }

    // ===================================================================================
    //                                                                          Adjustment
    //                                                                          ==========
    public void adjustPropertyMetaFinally(AppData schemaData) {
        // basically this adjusts property types for auto-detected properties
        // it should be called after initialization
        // (not called by procedure)
        final DfPropertyTypePackageResolver packageResolver = new DfPropertyTypePackageResolver();
        final Set<String> autoDetectedPropertyNameSet = getAutoDetectedPropertyNameSet();
        if (autoDetectedPropertyNameSet == null || autoDetectedPropertyNameSet.isEmpty()) {
            return;
        }
        final Map<String, String> propertyNameTypeMap = getPropertyNameTypeMap();
        for (String propertyName : autoDetectedPropertyNameSet) { // loop for auto-detected properties
            final String beforeType = propertyNameTypeMap.get(propertyName);
            String afterType = null;
            if (isPropertyTypeList(propertyName) && isPropertyOptionClassification(propertyName, schemaData)) {
                // list and classification option
                final String classificationName = getPropertyOptionClassificationName(propertyName, schemaData);
                final String plainType = "$$CDef$$." + classificationName;
                // ParameterBean has the "import" clause of language-embedded utility
                afterType = packageResolver.resolvePackageNameExceptUtil(plainType);
            } else if (!beforeType.contains("CDef")) {
                final Column refColumn = getPropertyOptionReferenceColumn(propertyName, schemaData);
                if (refColumn != null) {
                    // not CDef and reference
                    afterType = refColumn.getJavaNative();
                    final String utilPrefix = "java.util.";
                    if (Srl.startsWith(afterType, utilPrefix) && Srl.count(afterType, ".") == 2) {
                        // ParameterBean has the "import" clause of language-embedded utility
                        afterType = Srl.substringFirstRear(afterType, utilPrefix);
                    }
                }
            }
            if (afterType != null) {
                final String finalType;
                if (isPropertyTypeList(propertyName)) {
                    final String prefix = Srl.substringFirstFront(beforeType, "List<");
                    final String suffix = Srl.substringLastRear(beforeType, ">");
                    finalType = prefix + "List<" + afterType + ">" + suffix;
                } else {
                    finalType = afterType;
                }
                propertyNameTypeMap.put(propertyName, finalType); // override
            }
        }
    }

    // ===================================================================================
    //                                                                     OutputDirectory
    //                                                                     ===============
    /**
     * @return The output directory for Sql2Entity. (NotNull)
     */
    public String getSql2EntityOutputDirectory() {
        final String sql2EntityOutputDirectory = doGetPlainSql2EntityOutputDirectory();
        if (sql2EntityOutputDirectory != null) {
            return sql2EntityOutputDirectory;
        } else {
            return getOutsideSqlProperties().getSql2EntityOutputDirectory();
        }
    }

    public void switchSql2EntityOutputDirectory() {
        final String sql2EntityOutputDirectory = doGetPlainSql2EntityOutputDirectory();
        getOutsideSqlProperties().switchSql2EntityOutputDirectory(sql2EntityOutputDirectory);
    }

    protected String doGetPlainSql2EntityOutputDirectory() {
        final String sql2EntityOutputDirectory;
        if (_outsideSqlFile != null) {
            sql2EntityOutputDirectory = _outsideSqlFile.getSql2EntityOutputDirectory();
        } else {
            sql2EntityOutputDirectory = null;
        }
        return sql2EntityOutputDirectory;
    }

    protected static DfGenerator getGeneratorInstance() {
        return DfGenerator.getInstance();
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

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return getProperties().getOutsideSqlProperties();
    }

    protected DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentPmbMetaDataPropertyName(String propertyName) {
        if (propertyName == null || propertyName.trim().length() == 0) {
            String msg = "The propertyName should not be null or empty: " + propertyName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_className);
        sb.append(", ").append(_superClassName);
        sb.append(", ").append(_propertyNameTypeMap);
        sb.append(", ").append(_propertyNameOptionMap);
        sb.append(", ").append(_procedureName);
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getClassName() {
        return _className;
    }

    public void setClassName(String className) {
        this._className = className;
    }

    public String getSuperClassName() {
        return _superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this._superClassName = superClassName;
    }

    public Map<String, String> getPropertyNameTypeMap() {
        return _propertyNameTypeMap;
    }

    public void setPropertyNameTypeMap(Map<String, String> propertyNameTypeMap) {
        this._propertyNameTypeMap = propertyNameTypeMap;
    }

    public Map<String, String> getPropertyNameOptionMap() {
        return _propertyNameOptionMap;
    }

    public void setPropertyNameOptionMap(Map<String, String> propertyNameOptionMap) {
        this._propertyNameOptionMap = propertyNameOptionMap;
    }

    public Set<String> getAutoDetectedPropertyNameSet() {
        return _autoDetectedPropertyNameSet;
    }

    public void setAutoDetectedPropertyNameSet(Set<String> autoDetectedPropertyNameSet) {
        this._autoDetectedPropertyNameSet = autoDetectedPropertyNameSet;
    }

    public Set<String> getAlternateMethodBooleanNameSet() {
        return _alternateBooleanMethodNameSet;
    }

    public void setAlternateMethodBooleanNameSet(Set<String> alternateBooleanMethodNameSet) {
        this._alternateBooleanMethodNameSet = alternateBooleanMethodNameSet;
    }

    public DfOutsideSqlFile getOutsideSqlFile() {
        return _outsideSqlFile;
    }

    public void setOutsideSqlFile(DfOutsideSqlFile outsideSqlFile) {
        this._outsideSqlFile = outsideSqlFile;
    }

    public Map<String, String> getBqpElementMap() {
        return _bqpElementMap;
    }

    public void setBqpElementMap(Map<String, String> bqpElementMap) {
        this._bqpElementMap = bqpElementMap;
    }

    public DfCustomizeEntityInfo getCustomizeEntityInfo() {
        return _customizeEntityInfo;
    }

    public void setCustomizeEntityInfo(DfCustomizeEntityInfo customizeEntityInfo) {
        this._customizeEntityInfo = customizeEntityInfo;
    }

    public DfPagingType getPagingType() {
        return _pagingType;
    }

    public void setPagingType(DfPagingType pagingType) {
        this._pagingType = pagingType;
    }

    // -----------------------------------------------------
    //                                             Procedure
    //                                             ---------
    public String getProcedureName() {
        return _procedureName;
    }

    public void setProcedureName(String procedureName) {
        this._procedureName = procedureName;
    }

    public Map<String, String> getPropertyNameColumnNameMap() {
        return _propertyNameColumnNameMap;
    }

    public void setPropertyNameColumnNameMap(Map<String, String> propertyNameColumnNameMap) {
        this._propertyNameColumnNameMap = propertyNameColumnNameMap;
    }

    public Map<String, DfProcedureColumnMetaInfo> getPropertyNameColumnInfoMap() {
        return _propertyNameColumnInfoMap;
    }

    public void setPropertyNameColumnInfoMap(Map<String, DfProcedureColumnMetaInfo> propertyNameColumnInfoMap) {
        this._propertyNameColumnInfoMap = propertyNameColumnInfoMap;
    }

    public boolean isProcedureCalledBySelect() {
        return _procedureCalledBySelect;
    }

    public void setProcedureCalledBySelect(boolean procedureCalledBySelect) {
        this._procedureCalledBySelect = procedureCalledBySelect;
    }

    public boolean isProcedureRefCustomizeEntity() {
        return _procedureRefCustomizeEntity;
    }

    public void setProcedureRefCustomizeEntity(boolean procedureRefCustomizeEntity) {
        this._procedureRefCustomizeEntity = procedureRefCustomizeEntity;
    }
}
