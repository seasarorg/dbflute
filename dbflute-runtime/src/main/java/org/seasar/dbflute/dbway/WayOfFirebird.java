package org.seasar.dbflute.dbway;

/**
 * The DB way of Firebird.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfFirebird implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return null;
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return false; // Unknown
    }
}
