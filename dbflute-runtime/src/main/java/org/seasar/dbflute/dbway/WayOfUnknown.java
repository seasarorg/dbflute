package org.seasar.dbflute.dbway;

/**
 * The DB way of Unknown.
 * @author jflute
 */
public class WayOfUnknown implements DBWay {

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
