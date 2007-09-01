package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfSelectParamProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfSelectParamProperties(Properties prop) {
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
        final String value = stringProp("torque.statementResultSetType", "ResultSet.TYPE_SCROLL_INSENSITIVE");
        if (value.startsWith("ResultSet.")) {
            return "java.sql." + value;
        }
        return value;
    }

    public String getStatementResultSetConcurrency() {
        final String value = stringProp("torque.statementResultSetConcurrency", "ResultSet.CONCUR_READ_ONLY");
        if (value.startsWith("ResultSet.")) {
            return "java.sql." + value;
        }
        return value;
    }

    public boolean isStatementResultSetTypeValid() {
        return getStatementResultSetType().trim().length() != 0;
    }
}