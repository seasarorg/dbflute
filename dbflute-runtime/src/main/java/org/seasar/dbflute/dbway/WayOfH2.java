package org.seasar.dbflute.dbway;

/**
 * The DB way of H2.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfH2 implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "CALL IDENTITY()";
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return "23001".equals(sqlState);
    }
}
