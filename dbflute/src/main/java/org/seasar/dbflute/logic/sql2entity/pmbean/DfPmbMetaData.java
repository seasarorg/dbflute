package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.io.File;
import java.util.Map;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;

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

    // -----------------------------------------------------
    //                                             Procedure
    //                                             ---------
    // only when for procedure
    protected String _procedureName;
    protected Map<String, String> _propertyNameColumnNameMap;
    protected Map<String, DfProcedureColumnMetaInfo> _propertyNameColumnInfoMap;
    protected File _sqlFile;
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

    public File getSqlFile() {
        return _sqlFile;
    }

    public void setSqlFile(File sqlFile) {
        this._sqlFile = sqlFile;
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
