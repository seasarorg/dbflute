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

import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfCustomizeEntityInfo;

/**
 * @author jflute
 */
public class DfPmbMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _className;
    protected String _superClassName;
    protected Map<String, String> _propertyNameTypeMap;
    protected Map<String, String> _propertyNameOptionMap;

    // for typed parameter-bean
    protected File _sqlFile;
    protected Map<String, String> _bqpElementMap;
    protected DfCustomizeEntityInfo _customizeEntityInfo;
    protected DfPagingType _pagingType; // null means no paging

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
    protected boolean _refCustomizeEntity;

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

    public boolean isRefCustomizeEntity() {
        return _refCustomizeEntity;
    }

    public void setRefCustomizeEntity(boolean refCustomizeEntity) {
        this._refCustomizeEntity = refCustomizeEntity;
    }
}
