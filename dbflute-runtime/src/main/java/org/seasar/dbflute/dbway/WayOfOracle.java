package org.seasar.dbflute.dbway;

/**
 * The DB way of Oracle.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfOracle implements DBWay {

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
        return errorCode != null && errorCode == 1;
    }
}
