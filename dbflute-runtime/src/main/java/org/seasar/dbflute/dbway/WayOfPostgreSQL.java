package org.seasar.dbflute.dbway;

/**
 * The DB way of PostgreSQL.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfPostgreSQL implements DBWay {

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
        return "23505".equals(sqlState);
    }
}
