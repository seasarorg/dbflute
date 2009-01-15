package org.seasar.dbflute.dbway;

/**
 * The DB way of MS-Access.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfMSAccess implements DBWay {

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
