package org.seasar.dbflute.dbway;

/**
 * The DB way of DB2.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfDB2 implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "values IDENTITY_VAL_LOCAL()";
    }
}
