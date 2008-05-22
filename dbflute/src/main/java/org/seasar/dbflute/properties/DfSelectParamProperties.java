package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * @author jflute
 */
public final class DfSelectParamProperties extends DfAbstractHelperProperties {

    public DfSelectParamProperties(Properties prop) {
        super(prop);
    }

    public String getStatementResultSetType() {
        final String value = stringProp("torque.statementResultSetType", "ResultSet.TYPE_FORWARD_ONLY");
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