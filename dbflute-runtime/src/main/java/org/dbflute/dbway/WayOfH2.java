package org.dbflute.dbway;

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
}
