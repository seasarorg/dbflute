package org.seasar.dbflute.properties.assistant.commoncolumn;

import org.seasar.dbflute.util.DfStringUtil;

public class CommonColumnSetupResource {
    protected String className;
    protected String propertyName;
    protected String variablePrefix;

    public CommonColumnSetupResource(String variablePrefix) {
        this.variablePrefix = variablePrefix;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyNameInitCap() {
        return DfStringUtil.initCapAfterTrimming(propertyName);
    }

    public String getPropertyVariableName() {
        return variablePrefix + propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
