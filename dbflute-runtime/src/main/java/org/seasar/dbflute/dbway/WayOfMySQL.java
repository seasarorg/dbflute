package org.seasar.dbflute.dbway;

/**
 * The DB way of MySQL.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfMySQL implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "SELECT LAST_INSERT_ID()";
    }
}
