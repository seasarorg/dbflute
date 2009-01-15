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
    String getIdentitySelectSql();
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    /**
     * Is the SQLException from unique constraint? {Use both SQLState and ErrorCode}
     * @param sqlState SQLState of the SQLException. (Nullable)
     * @param errorCode ErrorCode of the SQLException. (Nullable)
     * @return Is the SQLException from unique constraint?
     */
    boolean isUniqueConstraintException(String sqlState, Integer errorCode);
}
