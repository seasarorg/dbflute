package org.seasar.dbflute.dbway;

/**
 * The DB way of SQLServer.
 * @author jflute
 */
public class WayOfSQLServer implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "select @@identity";
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return errorCode != null && errorCode == 2627;
    }
}
