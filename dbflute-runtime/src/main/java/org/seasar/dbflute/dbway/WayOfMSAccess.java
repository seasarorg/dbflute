package org.seasar.dbflute.dbway;

/**
 * The DB way of MS-Access.
 * @author jflute
 */
public class WayOfMSAccess implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "SELECT @@IDENTITY";
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return false; // Unknown
    }
}
