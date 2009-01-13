package org.dbflute.bhv;

import org.dbflute.Entity;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ListResultBean;
import org.dbflute.cbean.PagingResultBean;
import org.dbflute.dbmeta.DBMeta;

/**
 * The interface of behavior-readable.
 * @author DBFlute(AutoGenerator)
 */
public interface BehaviorReadable {

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    public String getTableDbName();

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the instance of DBMeta.
     * @return The instance of DBMeta. (NotNull)
     */
    public DBMeta getDBMeta();

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /**
     * New entity.
     * @return Entity. (NotNull)
     */
    public Entity newEntity();

    /**
     * New condition-bean.
     * @return Condition-bean. (NotNull)
     */
    public ConditionBean newConditionBean();

    // ===================================================================================
    //                                                                       Basic Get All
    //                                                                       =============
    /**
     * Get count all.
     * @return Count all.
     */
    public int getCountAll();

    // ===================================================================================
    //                                                                    Basic Read Count
    //                                                                    ================
    /**
     * Read count by condition-bean.
     * <pre>
     * If the argument 'condition-bean' is effective about fetch-scope,
     * this method invoke select count ignoring the fetch-scope.
     * </pre>
     * @param cb Condition-bean. This condition-bean should not be set up about fetch-scope. (NotNull)
     * @return Read count. (NotNull)
     */
    public int readCount(ConditionBean cb);

    // ===================================================================================
    //                                                                   Basic Read Entity
    //                                                                   =================
    /**
     * Read entity by condition-bean.
     * @param cb Condition-bean. (NotNull)
     * @return Read entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Entity readEntity(ConditionBean cb);

    /**
     * Read simple entity by condition-bean with deleted check.
     * @param cb Condition-bean. (NotNull)
     * @return Read entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Entity readEntityWithDeletedCheck(ConditionBean cb);

    // ===================================================================================
    //                                                                     Basic Read List
    //                                                                     ===============
    /**
     * Read list as result-bean.
     * @param cb Condition-bean. (NotNull)
     * @return List-result-bean. If the select result is zero, it returns empty list. (NotNull)
     */
    public ListResultBean<Entity> readList(ConditionBean cb);

    /**
     * Read page as result-bean.
     * @param cb Condition-bean. (NotNull)
     * @return Read page. (NotNull)
     */
    public PagingResultBean<Entity> readPage(final ConditionBean cb);

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * The implementation.
     * @return The value of sequence. (NotNull)
     */
    public java.math.BigDecimal readNextVal();

    // ===================================================================================
    //                                                                             Warm Up
    //                                                                             =======
    /**
     * Warm up the command of behavior.
     */
    public void warmUpCommand();
}
