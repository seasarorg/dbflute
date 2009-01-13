package com.example.dbflute.basic.dbflute.bsbhv;

import java.util.List;
import java.util.Map;

import org.dbflute.*;
import org.dbflute.bhv.ValueLabelSetupper;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ListResultBean;
import org.dbflute.cbean.PagingBean;
import org.dbflute.cbean.PagingHandler;
import org.dbflute.cbean.PagingInvoker;
import org.dbflute.cbean.PagingResultBean;
import org.dbflute.cbean.ResultBeanBuilder;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.jdbc.StatementConfig;

import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.allcommon.DBCurrent;
import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of PURCHASE that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PURCHASE_ID
 * 
 * [column]
 *     PURCHASE_ID, MEMBER_ID, PRODUCT_ID, PURCHASE_DATETIME, PURCHASE_COUNT, PURCHASE_PRICE, PAYMENT_COMPLETE_FLG, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     PURCHASE_ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER, PRODUCT, SUMMARY_PRODUCT
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member, product, summaryProduct
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsPurchaseBhv extends org.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "PURCHASE"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return PurchaseDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public PurchaseDbm getMyDBMeta() { return PurchaseDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public Purchase newMyEntity() { return new Purchase(); }
    public PurchaseCB newMyConditionBean() { return new PurchaseCB(); }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    protected DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                             Default StatementConfig
    //                                                             =======================
    @Override
    protected StatementConfig getDefaultStatementConfig() {
        return DBFluteConfig.getInstance().getDefaultStatementConfig();
    }
    
    // ===================================================================================
    //                                                                        Count Select
    //                                                                        ============
    /**
     * Select the count of the condition-bean. {IgnorePagingCondition}
     * @param cb The condition-bean of Purchase. (NotNull)
     * @return The selected count.
     */
    public int selectCount(PurchaseCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of Purchase. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Purchase selectEntity(final PurchaseCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<Purchase, PurchaseCB>() {
            public List<Purchase> callbackSelectList(PurchaseCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of Purchase. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Purchase selectEntityWithDeletedCheck(final PurchaseCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<Purchase, PurchaseCB>() {
            public List<Purchase> callbackSelectList(PurchaseCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Purchase selectByPKValueWithDeletedCheck(Long purchaseId) {
        Purchase entity = new Purchase();
        entity.setPurchaseId(purchaseId);
        final PurchaseCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of Purchase. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<Purchase> selectList(PurchaseCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<Purchase>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of Purchase. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<Purchase> selectPage(final PurchaseCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<Purchase> invoker = new PagingInvoker<Purchase>(getTableDbName());
        final PagingHandler<Purchase> handler = new PagingHandler<Purchase>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<Purchase> paging() { return selectList(cb); }
        };
        return invoker.invokePaging(handler);
    }

    // ===================================================================================
    //                                                                       Scalar Select
    //                                                                       =============
    /**
     * Select the scalar value derived by a function. <br />
     * Call a function method after this method called like as follows:
     * <pre>
     * purchaseBhv.scalarSelect(Date.class).max(new ScalarQuery(PurchaseCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<PurchaseCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        PurchaseCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<PurchaseCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of Purchase. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(PurchaseCB cb, ValueLabelSetupper<Purchase> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of foreign table 'Member'.
     * @param purchaseList The list of purchase. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<Member> pulloutMember(List<Purchase> purchaseList) {
        return helpPulloutInternally(purchaseList, new InternalPulloutCallback<Purchase, Member>() {
            public Member callbackGetForeignEntity(Purchase entity) { return entity.getMember(); } });
    }
    /**
     * Pull out the list of foreign table 'Product'.
     * @param purchaseList The list of purchase. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<Product> pulloutProduct(List<Purchase> purchaseList) {
        return helpPulloutInternally(purchaseList, new InternalPulloutCallback<Purchase, Product>() {
            public Product callbackGetForeignEntity(Purchase entity) { return entity.getProduct(); } });
    }
    /**
     * Pull out the list of foreign table 'SummaryProduct'.
     * @param purchaseList The list of purchase. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<SummaryProduct> pulloutSummaryProduct(List<Purchase> purchaseList) {
        return helpPulloutInternally(purchaseList, new InternalPulloutCallback<Purchase, SummaryProduct>() {
            public SummaryProduct callbackGetForeignEntity(Purchase entity) { return entity.getSummaryProduct(); } });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param purchase The entity of insert target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(Purchase purchase) {
        assertEntityNotNull(purchase);
        delegateInsert(purchase);
    }

    @Override
    protected void doCreate(Entity purchase) {
        insert((Purchase)purchase);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param purchase The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final Purchase purchase) {
        helpUpdateInternally(purchase, new InternalUpdateCallback<Purchase>() {
            public int callbackDelegateUpdate(Purchase entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((Purchase)entity);
    }
    
    /**
     * Update the entity non-strictly modified-only. {UpdateCountZeroException, NonConcurrencyControl}
     * @param purchase The entity of update target. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void updateNonstrict(final Purchase purchase) {
        helpUpdateNonstrictInternally(purchase, new InternalUpdateNonstrictCallback<Purchase>() {
            public int callbackDelegateUpdateNonstrict(Purchase entity) { return delegateUpdateNonstrict(entity); } });
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        updateNonstrict((Purchase)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param purchase The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final Purchase purchase) {
        helpInsertOrUpdateInternally(purchase, new InternalInsertOrUpdateCallback<Purchase, PurchaseCB>() {
            public void callbackInsert(Purchase entity) { insert(entity); }
            public void callbackUpdate(Purchase entity) { update(entity); }
            public PurchaseCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(PurchaseCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity purchase) {
        insertOrUpdate((Purchase)purchase);
    }

    /**
     * Insert or update the entity non-strictly modified-only. {NonConcurrencyControl(when update)}
     * @param purchase The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdateNonstrict(Purchase purchase) {
        helpInsertOrUpdateInternally(purchase, new InternalInsertOrUpdateNonstrictCallback<Purchase>() {
            public void callbackInsert(Purchase entity) { insert(entity); }
            public void callbackUpdateNonstrict(Purchase entity) { updateNonstrict(entity); }
        });
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdateNonstrict((Purchase)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param purchase The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(Purchase purchase) {
        helpDeleteInternally(purchase, new InternalDeleteCallback<Purchase>() {
            public int callbackDelegateDelete(Purchase entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity purchase) {
        delete((Purchase)purchase);
    }

    /**
     * Delete the entity non-strictly. {UpdateCountZeroException, NonConcurrencyControl}
     * @param purchase Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(Purchase purchase) {
        helpDeleteNonstrictInternally(purchase, new InternalDeleteNonstrictCallback<Purchase>() {
            public int callbackDelegateDeleteNonstrict(Purchase entity) { return delegateDeleteNonstrict(entity); } });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {UpdateCountZeroException, NonConcurrencyControl}
     * @param purchase Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(Purchase purchase) {
        helpDeleteNonstrictIgnoreDeletedInternally(purchase, new InternalDeleteNonstrictIgnoreDeletedCallback<Purchase>() {
            public int callbackDelegateDeleteNonstrict(Purchase entity) { return delegateDeleteNonstrict(entity); } });
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param purchaseList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<Purchase> purchaseList) {
        assertObjectNotNull("purchaseList", purchaseList);
        return delegateInsertList(purchaseList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param purchaseList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchUpdate(List<Purchase> purchaseList) {
        assertObjectNotNull("purchaseList", purchaseList);
        return delegateUpdateList(purchaseList);
    }

    /**
     * Batch update the list non-strictly. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param purchaseList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdateNonstrict(List<Purchase> purchaseList) {
        assertObjectNotNull("purchaseList", purchaseList);
        return delegateUpdateListNonstrict(purchaseList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param purchaseList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchDelete(List<Purchase> purchaseList) {
        assertObjectNotNull("purchaseList", purchaseList);
        return delegateDeleteList(purchaseList);
    }

    /**
     * Batch delete the list non-strictly. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param purchaseList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDeleteNonstrict(List<Purchase> purchaseList) {
        assertObjectNotNull("purchaseList", purchaseList);
        return delegateDeleteListNonstrict(purchaseList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param purchase Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(Purchase purchase, PurchaseCB cb) {
        assertObjectNotNull("purchase", purchase); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(purchase);
        filterEntityOfUpdate(purchase); assertEntityOfUpdate(purchase);
        return invoke(createQueryUpdateEntityCBCommand(purchase, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(PurchaseCB cb) {
        assertCBNotNull(cb);
        return invoke(createQueryDeleteCBCommand(cb));
    }

    // ===================================================================================
    //                                                                      Various Update
    //                                                                      ==============
    
    // ===================================================================================
    //                                                                     Delegate Method
    //                                                                     ===============
    // [Behavior Command]
    // -----------------------------------------------------
    //                                                Select
    //                                                ------
    protected int delegateSelectCount(PurchaseCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((PurchaseCB)cb); }
    protected List<Purchase> delegateSelectList(PurchaseCB cb) {
        return invoke(createSelectListCBCommand(cb, Purchase.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((PurchaseCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(Purchase e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(Purchase e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateUpdateNonstrict(Purchase e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateNonstrictEntityCommand(e));
    }
    protected int delegateDelete(Purchase e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }
    protected int delegateDeleteNonstrict(Purchase e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<Purchase> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<Purchase> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateUpdateListNonstrict(List<Purchase> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    protected int[] delegateDeleteList(List<Purchase> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }
    protected int[] delegateDeleteListNonstrict(List<Purchase> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteNonstrictEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    @Override
    protected boolean hasVersionNoValue(Entity entity) {
        return !(downcast(entity).getVersionNo() + "").equals("null");// For primitive type
    }

    @Override
    protected boolean hasUpdateDateValue(Entity entity) {
        return false;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected Purchase downcast(Entity entity) {
        return helpDowncastInternally(entity, Purchase.class);
    }
}
