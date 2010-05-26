package org.seasar.dbflute.logic.pmbean;

import java.io.File;
import java.util.Map;

/**
 * @author jflute
 */
public class DfParameterBeanMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String className;
    protected String superClassName;
    protected Map<String, String> propertyNameTypeMap;
    protected Map<String, String> propertyNameOptionMap;
    protected Map<String, String> propertyNameColumnNameMap; // only when this is for procedure
    protected String procedureName; // only when this is for procedure
    protected File sqlFile; // when procedure, always null
    protected boolean refCustomizeEntity;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(className);
        sb.append(", ").append(superClassName);
        sb.append(", ").append(propertyNameTypeMap);
        sb.append(", ").append(propertyNameOptionMap);
        sb.append(", ").append(procedureName);
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public Map<String, String> getPropertyNameTypeMap() {
        return propertyNameTypeMap;
    }

    public void setPropertyNameTypeMap(Map<String, String> propertyNameTypeMap) {
        this.propertyNameTypeMap = propertyNameTypeMap;
    }

    public Map<String, String> getPropertyNameOptionMap() {
        return propertyNameOptionMap;
    }

    public void setPropertyNameOptionMap(Map<String, String> propertyNameOptionMap) {
        this.propertyNameOptionMap = propertyNameOptionMap;
    }

    public Map<String, String> getPropertyNameColumnNameMap() {
        return propertyNameColumnNameMap;
    }

    public void setPropertyNameColumnNameMap(Map<String, String> propertyNameColumnNameMap) {
        this.propertyNameColumnNameMap = propertyNameColumnNameMap;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public File getSqlFile() {
        return sqlFile;
    }

    public void setSqlFile(File sqlFile) {
        this.sqlFile = sqlFile;
    }

    public boolean isRefCustomizeEntity() {
        return refCustomizeEntity;
    }

    public void setRefCustomizeEntity(boolean refCustomizeEntity) {
        this.refCustomizeEntity = refCustomizeEntity;
    }
}
