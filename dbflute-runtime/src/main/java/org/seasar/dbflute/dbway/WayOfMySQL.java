package org.seasar.dbflute.dbway;

/**
 * The DB way of MySQL.
 * @author jflute
 */
public class WayOfMySQL implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "SELECT LAST_INSERT_ID()";
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return errorCode != null && errorCode == 1062;
    }
}
