package org.dbflute.dbway;

/**
 * The DB way of SQLServer.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfSQLServer implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "select @@identity";
    }
}
