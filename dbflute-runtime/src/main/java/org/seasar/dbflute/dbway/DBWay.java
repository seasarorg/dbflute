package org.seasar.dbflute.dbway;

/**
 * The interface of DB way.
 * @author jflute
 */
public interface DBWay {

    // ===================================================================================
    //                                                                        Identity Way
    //                                                                        ============
    /**
     * Get the SQL string for getting inserted value of identity.
     * @return The SQL string for getting inserted value of sequence. (Nullable: If it does not have identity, returns null.)
     */
    public String getIdentitySelectSql();
}
