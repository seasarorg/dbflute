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

import java.io.File;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPmbMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _className;
    protected String _superClassName;
    protected DfPagingType _pagingType; // null means no paging
    protected Map<String, String> _propertyNameTypeMap;
    protected Map<String, String> _propertyNameOptionMap;
    protected File _sqlFile;
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

    // ===================================================================================
    //                                                                  TypedParameterBean
    //                                                                  ==================
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

    public boolean isTypedListHandling() {
        if (isTypedPagingHandling()) {
            return false;
        }
        return isRelatedToCustomizeEntity() ? _customizeEntityInfo.isResultHandling() : false;
    }

    public boolean isTypedEntityHandling() {
        // *allowed to use entity handling with paging handling
        //if (isTypedPagingHandling()) {
        //    return false;
        //}
        return isRelatedToCustomizeEntity() ? _customizeEntityInfo.isResultHandling() : false;
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
        if (!isRelatedToBehaviorQuery()) {
            return false;
        }
        if (!isRelatedToCustomizeEntity()) {
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
            return Srl.containsIgnoreCase(sql, "limit") && Srl.contains(sql, "pmb.fetchSize");
        } else if (getBasicProperties().isDatabasePostgreSQL()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "limit");
        } else if (getBasicProperties().isDatabaseOracle()) {
            return Srl.containsAllIgnoreCase(sql, "rownum");
        } else if (getBasicProperties().isDatabaseDB2()) {
            return Srl.containsAllIgnoreCase(sql, "row_number()");
        } else if (getBasicProperties().isDatabaseSQLServer()) {
            return Srl.containsAllIgnoreCase(sql, "row_number()");
        } else if (getBasicProperties().isDatabaseH2()) {
            // H2 implements both limit only and offset + limit
            return Srl.containsAllIgnoreCase(sql, "offset", "limit")
                    || (Srl.containsIgnoreCase(sql, "limit") && Srl.contains(sql, "pmb.fetchSize"));
        } else if (getBasicProperties().isDatabaseDerby()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "fetch");
        } else if (getBasicProperties().isDatabaseSQLite()) {
            return Srl.containsAllIgnoreCase(sql, "offset", "limit");
        } else {
            return false;
        }
    }

    public boolean isTypedCursorHandling() {
        if (!isRelatedToBehaviorQuery()) {
            return false;
        }
        return isRelatedToCustomizeEntity() ? _customizeEntityInfo.isCursorHandling() : false;
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

    public File getSqlFile() {
        return _sqlFile;
    }

    public void setSqlFile(File sqlFile) {
        this._sqlFile = sqlFile;
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
