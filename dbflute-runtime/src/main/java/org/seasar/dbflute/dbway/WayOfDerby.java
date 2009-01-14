package org.seasar.dbflute.dbway;

/**
 * The DB way of Derby.
 * @author DBFlute(AutoGenerator)
 */
public class WayOfDerby implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "values IDENTITY_VAL_LOCAL()";
    }
}
