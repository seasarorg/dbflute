package org.apache.torque.helper.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class SelectParamProperties extends AbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public SelectParamProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                       Properties - Select Param
    //                                                       =========================
    public String getSelectQueryTimeout() {
        return stringProp("torque.selectQueryTimeout", "-1");
    }

    public boolean isSelectQueryTimeoutValid() {
        return !getSelectQueryTimeout().startsWith("-");
    }

    public String getStatementResultSetType() {
        return stringProp("torque.statementResultSetType", "");
    }

    public String getStatementResultSetConcurrency() {
        return stringProp("torque.statementResultSetConcurrency", "");
    }

    public boolean isStatementResultSetTypeValid() {
        return getStatementResultSetType().trim().length() != 0;
    }
}