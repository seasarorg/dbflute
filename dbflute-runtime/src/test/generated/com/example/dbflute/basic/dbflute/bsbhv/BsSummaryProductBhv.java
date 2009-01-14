package com.example.dbflute.basic.dbflute.bsbhv;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.*;
import org.seasar.dbflute.bhv.ConditionBeanSetupper;
import org.seasar.dbflute.bhv.LoadReferrerOption;
import org.seasar.dbflute.bhv.ValueLabelSetupper;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingBean;
import org.seasar.dbflute.cbean.PagingHandler;
import org.seasar.dbflute.cbean.PagingInvoker;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.ResultBeanBuilder;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.jdbc.StatementConfig;

import com.example.dbflute.basic.dbflute.exbhv.*;
import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.allcommon.DBCurrent;
import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of SUMMARY_PRODUCT that the type is VIEW. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_ID
 * 
 * [column]
 *     PRODUCT_ID, PRODUCT_NAME, PRODUCT_STATUS_CODE, LATEST_PURCHASE_DATETIME
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     PRODUCT_STATUS
 * 
 * [referrer-table]
 *     PURCHASE
 * 
 * [foreign-property]
 *     productStatus
 * 
 * [referrer-property]
 *     purchaseList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsSummaryProductBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "SUMMARY_PRODUCT"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return SummaryProductDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public SummaryProductDbm getMyDBMeta() { return SummaryProductDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public SummaryProduct newMyEntity() { return new SummaryProduct(); }
    public SummaryProductCB newMyConditionBean() { return new SummaryProductCB(); }

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
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @return The selected count.
     */
    public int selectCount(SummaryProductCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public SummaryProduct selectEntity(final SummaryProductCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<SummaryProduct, SummaryProductCB>() {
            public List<SummaryProduct> callbackSelectList(SummaryProductCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public SummaryProduct selectEntityWithDeletedCheck(final SummaryProductCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<SummaryProduct, SummaryProductCB>() {
            public List<SummaryProduct> callbackSelectList(SummaryProductCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public SummaryProduct selectByPKValueWithDeletedCheck(Integer productId) {
        SummaryProduct entity = new SummaryProduct();
        entity.setProductId(productId);
        final SummaryProductCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<SummaryProduct> selectList(SummaryProductCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<SummaryProduct>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<SummaryProduct> selectPage(final SummaryProductCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<SummaryProduct> invoker = new PagingInvoker<SummaryProduct>(getTableDbName());
        final PagingHandler<SummaryProduct> handler = new PagingHandler<SummaryProduct>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<SummaryProduct> paging() { return selectList(cb); }
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
     * summaryProductBhv.scalarSelect(Date.class).max(new ScalarQuery(SummaryProductCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<SummaryProductCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        SummaryProductCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<SummaryProductCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of SummaryProduct. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(SummaryProductCB cb, ValueLabelSetupper<SummaryProduct> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer of purchaseList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setProductId_InScope(pkList);
     * cb.query().addOrderBy_ProductId_Asc();
     * </pre>
     * @param summaryProductList The entity list of summaryProduct. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadPurchaseList(List<SummaryProduct> summaryProductList, ConditionBeanSetupper<PurchaseCB> conditionBeanSetupper) {
        assertObjectNotNull("summaryProductList<SummaryProduct>", summaryProductList);
        assertObjectNotNull("conditionBeanSetupper<PurchaseCB>", conditionBeanSetupper);
        if (summaryProductList.isEmpty()) { return; }
        loadPurchaseList(summaryProductList, new LoadReferrerOption<PurchaseCB, Purchase>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param summaryProductList The entity list of summaryProduct. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadPurchaseList(List<SummaryProduct> summaryProductList, LoadReferrerOption<PurchaseCB, Purchase> loadReferrerOption) {
        assertObjectNotNull("summaryProductList<SummaryProduct>", summaryProductList);
        assertObjectNotNull("loadReferrerOption<Purchase, PurchaseCB>", loadReferrerOption);
        if (summaryProductList.isEmpty()) { return; }
        final PurchaseBhv referrerBhv = xgetBSFLR().select(PurchaseBhv.class);
        helpLoadReferrerInternally(summaryProductList, loadReferrerOption, new InternalLoadReferrerCallback<SummaryProduct, Integer, PurchaseCB, Purchase>() {
            public Integer callbackBase_getPrimaryKeyValue(SummaryProduct entity) { return entity.getProductId(); }
            public void callbackBase_setReferrerList(SummaryProduct entity, List<Purchase> referrerList) { entity.setPurchaseList(referrerList); }
            public PurchaseCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(PurchaseCB cb, List<Integer> pkList) { cb.query().setProductId_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(PurchaseCB cb) { cb.query().addOrderBy_ProductId_Asc(); }
            public List<Purchase> callbackReferrer_selectList(PurchaseCB cb) { return referrerBhv.selectList(cb); }
            public Integer callbackReferrer_getForeignKeyValue(Purchase entity) { return entity.getProductId(); }
            public void callbackReferrer_setForeignEntity(Purchase referrerEntity, SummaryProduct baseEntity) { referrerEntity.setSummaryProduct(baseEntity); }
        } );
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of foreign table 'ProductStatus'.
     * @param summaryProductList The list of summaryProduct. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<ProductStatus> pulloutProductStatus(List<SummaryProduct> summaryProductList) {
        return helpPulloutInternally(summaryProductList, new InternalPulloutCallback<SummaryProduct, ProductStatus>() {
            public ProductStatus callbackGetForeignEntity(SummaryProduct entity) { return entity.getProductStatus(); } });
    }
  
    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param summaryProduct The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(SummaryProduct summaryProduct) {
        assertEntityNotNull(summaryProduct);
        delegateInsert(summaryProduct);
    }

    @Override
    protected void doCreate(Entity summaryProduct) {
        insert((SummaryProduct)summaryProduct);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param summaryProduct The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final SummaryProduct summaryProduct) {
        helpUpdateInternally(summaryProduct, new InternalUpdateCallback<SummaryProduct>() {
            public int callbackDelegateUpdate(SummaryProduct entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((SummaryProduct)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((SummaryProduct)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param summaryProduct The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final SummaryProduct summaryProduct) {
        helpInsertOrUpdateInternally(summaryProduct, new InternalInsertOrUpdateCallback<SummaryProduct, SummaryProductCB>() {
            public void callbackInsert(SummaryProduct entity) { insert(entity); }
            public void callbackUpdate(SummaryProduct entity) { update(entity); }
            public SummaryProductCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(SummaryProductCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity summaryProduct) {
        insertOrUpdate((SummaryProduct)summaryProduct);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((SummaryProduct)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param summaryProduct The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(SummaryProduct summaryProduct) {
        helpDeleteInternally(summaryProduct, new InternalDeleteCallback<SummaryProduct>() {
            public int callbackDelegateDelete(SummaryProduct entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity summaryProduct) {
        delete((SummaryProduct)summaryProduct);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param summaryProductList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<SummaryProduct> summaryProductList) {
        assertObjectNotNull("summaryProductList", summaryProductList);
        return delegateInsertList(summaryProductList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param summaryProductList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<SummaryProduct> summaryProductList) {
        assertObjectNotNull("summaryProductList", summaryProductList);
        return delegateUpdateList(summaryProductList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param summaryProductList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<SummaryProduct> summaryProductList) {
        assertObjectNotNull("summaryProductList", summaryProductList);
        return delegateDeleteList(summaryProductList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param summaryProduct Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(SummaryProduct summaryProduct, SummaryProductCB cb) {
        assertObjectNotNull("summaryProduct", summaryProduct); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(summaryProduct);
        filterEntityOfUpdate(summaryProduct); assertEntityOfUpdate(summaryProduct);
        return invoke(createQueryUpdateEntityCBCommand(summaryProduct, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(SummaryProductCB cb) {
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
    protected int delegateSelectCount(SummaryProductCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((SummaryProductCB)cb); }
    protected List<SummaryProduct> delegateSelectList(SummaryProductCB cb) {
        return invoke(createSelectListCBCommand(cb, SummaryProduct.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((SummaryProductCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(SummaryProduct e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(SummaryProduct e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(SummaryProduct e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<SummaryProduct> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<SummaryProduct> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<SummaryProduct> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    @Override
    protected boolean hasVersionNoValue(Entity entity) {
        return false;
    }

    @Override
    protected boolean hasUpdateDateValue(Entity entity) {
        return false;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected SummaryProduct downcast(Entity entity) {
        return helpDowncastInternally(entity, SummaryProduct.class);
    }
}
