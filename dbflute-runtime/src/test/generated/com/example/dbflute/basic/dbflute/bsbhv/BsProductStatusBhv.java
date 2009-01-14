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
 * The behavior of PRODUCT_STATUS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_STATUS_CODE
 * 
 * [column]
 *     PRODUCT_STATUS_CODE, PRODUCT_STATUS_NAME
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
 *     
 * 
 * [referrer-table]
 *     PRODUCT, SUMMARY_PRODUCT
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     productList, summaryProductList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsProductStatusBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "PRODUCT_STATUS"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return ProductStatusDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public ProductStatusDbm getMyDBMeta() { return ProductStatusDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public ProductStatus newMyEntity() { return new ProductStatus(); }
    public ProductStatusCB newMyConditionBean() { return new ProductStatusCB(); }

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
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @return The selected count.
     */
    public int selectCount(ProductStatusCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public ProductStatus selectEntity(final ProductStatusCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<ProductStatus, ProductStatusCB>() {
            public List<ProductStatus> callbackSelectList(ProductStatusCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public ProductStatus selectEntityWithDeletedCheck(final ProductStatusCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<ProductStatus, ProductStatusCB>() {
            public List<ProductStatus> callbackSelectList(ProductStatusCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public ProductStatus selectByPKValueWithDeletedCheck(String productStatusCode) {
        ProductStatus entity = new ProductStatus();
        entity.setProductStatusCode(productStatusCode);
        final ProductStatusCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<ProductStatus> selectList(ProductStatusCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<ProductStatus>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<ProductStatus> selectPage(final ProductStatusCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<ProductStatus> invoker = new PagingInvoker<ProductStatus>(getTableDbName());
        final PagingHandler<ProductStatus> handler = new PagingHandler<ProductStatus>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<ProductStatus> paging() { return selectList(cb); }
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
     * productStatusBhv.scalarSelect(Date.class).max(new ScalarQuery(ProductStatusCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<ProductStatusCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        ProductStatusCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<ProductStatusCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of ProductStatus. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(ProductStatusCB cb, ValueLabelSetupper<ProductStatus> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer of productList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setProductStatusCode_InScope(pkList);
     * cb.query().addOrderBy_ProductStatusCode_Asc();
     * </pre>
     * @param productStatusList The entity list of productStatus. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadProductList(List<ProductStatus> productStatusList, ConditionBeanSetupper<ProductCB> conditionBeanSetupper) {
        assertObjectNotNull("productStatusList<ProductStatus>", productStatusList);
        assertObjectNotNull("conditionBeanSetupper<ProductCB>", conditionBeanSetupper);
        if (productStatusList.isEmpty()) { return; }
        loadProductList(productStatusList, new LoadReferrerOption<ProductCB, Product>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param productStatusList The entity list of productStatus. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadProductList(List<ProductStatus> productStatusList, LoadReferrerOption<ProductCB, Product> loadReferrerOption) {
        assertObjectNotNull("productStatusList<ProductStatus>", productStatusList);
        assertObjectNotNull("loadReferrerOption<Product, ProductCB>", loadReferrerOption);
        if (productStatusList.isEmpty()) { return; }
        final ProductBhv referrerBhv = xgetBSFLR().select(ProductBhv.class);
        helpLoadReferrerInternally(productStatusList, loadReferrerOption, new InternalLoadReferrerCallback<ProductStatus, String, ProductCB, Product>() {
            public String callbackBase_getPrimaryKeyValue(ProductStatus entity) { return entity.getProductStatusCode(); }
            public void callbackBase_setReferrerList(ProductStatus entity, List<Product> referrerList) { entity.setProductList(referrerList); }
            public ProductCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(ProductCB cb, List<String> pkList) { cb.query().setProductStatusCode_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(ProductCB cb) { cb.query().addOrderBy_ProductStatusCode_Asc(); }
            public List<Product> callbackReferrer_selectList(ProductCB cb) { return referrerBhv.selectList(cb); }
            public String callbackReferrer_getForeignKeyValue(Product entity) { return entity.getProductStatusCode(); }
            public void callbackReferrer_setForeignEntity(Product referrerEntity, ProductStatus baseEntity) { referrerEntity.setProductStatus(baseEntity); }
        } );
    }
    /**
     * Load referrer of summaryProductList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setProductStatusCode_InScope(pkList);
     * cb.query().addOrderBy_ProductStatusCode_Asc();
     * </pre>
     * @param productStatusList The entity list of productStatus. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadSummaryProductList(List<ProductStatus> productStatusList, ConditionBeanSetupper<SummaryProductCB> conditionBeanSetupper) {
        assertObjectNotNull("productStatusList<ProductStatus>", productStatusList);
        assertObjectNotNull("conditionBeanSetupper<SummaryProductCB>", conditionBeanSetupper);
        if (productStatusList.isEmpty()) { return; }
        loadSummaryProductList(productStatusList, new LoadReferrerOption<SummaryProductCB, SummaryProduct>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param productStatusList The entity list of productStatus. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadSummaryProductList(List<ProductStatus> productStatusList, LoadReferrerOption<SummaryProductCB, SummaryProduct> loadReferrerOption) {
        assertObjectNotNull("productStatusList<ProductStatus>", productStatusList);
        assertObjectNotNull("loadReferrerOption<SummaryProduct, SummaryProductCB>", loadReferrerOption);
        if (productStatusList.isEmpty()) { return; }
        final SummaryProductBhv referrerBhv = xgetBSFLR().select(SummaryProductBhv.class);
        helpLoadReferrerInternally(productStatusList, loadReferrerOption, new InternalLoadReferrerCallback<ProductStatus, String, SummaryProductCB, SummaryProduct>() {
            public String callbackBase_getPrimaryKeyValue(ProductStatus entity) { return entity.getProductStatusCode(); }
            public void callbackBase_setReferrerList(ProductStatus entity, List<SummaryProduct> referrerList) { entity.setSummaryProductList(referrerList); }
            public SummaryProductCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(SummaryProductCB cb, List<String> pkList) { cb.query().setProductStatusCode_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(SummaryProductCB cb) { cb.query().addOrderBy_ProductStatusCode_Asc(); }
            public List<SummaryProduct> callbackReferrer_selectList(SummaryProductCB cb) { return referrerBhv.selectList(cb); }
            public String callbackReferrer_getForeignKeyValue(SummaryProduct entity) { return entity.getProductStatusCode(); }
            public void callbackReferrer_setForeignEntity(SummaryProduct referrerEntity, ProductStatus baseEntity) { referrerEntity.setProductStatus(baseEntity); }
        } );
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    
    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param productStatus The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(ProductStatus productStatus) {
        assertEntityNotNull(productStatus);
        delegateInsert(productStatus);
    }

    @Override
    protected void doCreate(Entity productStatus) {
        insert((ProductStatus)productStatus);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param productStatus The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final ProductStatus productStatus) {
        helpUpdateInternally(productStatus, new InternalUpdateCallback<ProductStatus>() {
            public int callbackDelegateUpdate(ProductStatus entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((ProductStatus)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((ProductStatus)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param productStatus The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final ProductStatus productStatus) {
        helpInsertOrUpdateInternally(productStatus, new InternalInsertOrUpdateCallback<ProductStatus, ProductStatusCB>() {
            public void callbackInsert(ProductStatus entity) { insert(entity); }
            public void callbackUpdate(ProductStatus entity) { update(entity); }
            public ProductStatusCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(ProductStatusCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity productStatus) {
        insertOrUpdate((ProductStatus)productStatus);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((ProductStatus)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param productStatus The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(ProductStatus productStatus) {
        helpDeleteInternally(productStatus, new InternalDeleteCallback<ProductStatus>() {
            public int callbackDelegateDelete(ProductStatus entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity productStatus) {
        delete((ProductStatus)productStatus);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productStatusList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<ProductStatus> productStatusList) {
        assertObjectNotNull("productStatusList", productStatusList);
        return delegateInsertList(productStatusList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productStatusList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<ProductStatus> productStatusList) {
        assertObjectNotNull("productStatusList", productStatusList);
        return delegateUpdateList(productStatusList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productStatusList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<ProductStatus> productStatusList) {
        assertObjectNotNull("productStatusList", productStatusList);
        return delegateDeleteList(productStatusList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param productStatus Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(ProductStatus productStatus, ProductStatusCB cb) {
        assertObjectNotNull("productStatus", productStatus); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(productStatus);
        filterEntityOfUpdate(productStatus); assertEntityOfUpdate(productStatus);
        return invoke(createQueryUpdateEntityCBCommand(productStatus, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(ProductStatusCB cb) {
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
    protected int delegateSelectCount(ProductStatusCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((ProductStatusCB)cb); }
    protected List<ProductStatus> delegateSelectList(ProductStatusCB cb) {
        return invoke(createSelectListCBCommand(cb, ProductStatus.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((ProductStatusCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(ProductStatus e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(ProductStatus e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(ProductStatus e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<ProductStatus> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<ProductStatus> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<ProductStatus> ls) {
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
    protected ProductStatus downcast(Entity entity) {
        return helpDowncastInternally(entity, ProductStatus.class);
    }
}
