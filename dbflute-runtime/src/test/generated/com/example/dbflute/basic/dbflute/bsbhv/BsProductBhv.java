package com.example.dbflute.basic.dbflute.bsbhv;

import java.util.List;
import java.util.Map;

import org.dbflute.*;
import org.dbflute.bhv.ConditionBeanSetupper;
import org.dbflute.bhv.LoadReferrerOption;
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

import com.example.dbflute.basic.dbflute.exbhv.*;
import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.allcommon.DBCurrent;
import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of PRODUCT that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_ID
 * 
 * [column]
 *     PRODUCT_ID, PRODUCT_NAME, PRODUCT_HANDLE_CODE, PRODUCT_STATUS_CODE, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     PRODUCT_ID
 * 
 * [version-no]
 *     VERSION_NO
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
public abstract class BsProductBhv extends org.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "PRODUCT"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return ProductDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public ProductDbm getMyDBMeta() { return ProductDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public Product newMyEntity() { return new Product(); }
    public ProductCB newMyConditionBean() { return new ProductCB(); }

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
     * @param cb The condition-bean of Product. (NotNull)
     * @return The selected count.
     */
    public int selectCount(ProductCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of Product. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Product selectEntity(final ProductCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<Product, ProductCB>() {
            public List<Product> callbackSelectList(ProductCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of Product. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Product selectEntityWithDeletedCheck(final ProductCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<Product, ProductCB>() {
            public List<Product> callbackSelectList(ProductCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Product selectByPKValueWithDeletedCheck(Integer productId) {
        Product entity = new Product();
        entity.setProductId(productId);
        final ProductCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of Product. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<Product> selectList(ProductCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<Product>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of Product. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<Product> selectPage(final ProductCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<Product> invoker = new PagingInvoker<Product>(getTableDbName());
        final PagingHandler<Product> handler = new PagingHandler<Product>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<Product> paging() { return selectList(cb); }
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
     * productBhv.scalarSelect(Date.class).max(new ScalarQuery(ProductCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<ProductCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        ProductCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<ProductCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of Product. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(ProductCB cb, ValueLabelSetupper<Product> valueLabelSetupper) {
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
     * @param productList The entity list of product. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadPurchaseList(List<Product> productList, ConditionBeanSetupper<PurchaseCB> conditionBeanSetupper) {
        assertObjectNotNull("productList<Product>", productList);
        assertObjectNotNull("conditionBeanSetupper<PurchaseCB>", conditionBeanSetupper);
        if (productList.isEmpty()) { return; }
        loadPurchaseList(productList, new LoadReferrerOption<PurchaseCB, Purchase>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param productList The entity list of product. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadPurchaseList(List<Product> productList, LoadReferrerOption<PurchaseCB, Purchase> loadReferrerOption) {
        assertObjectNotNull("productList<Product>", productList);
        assertObjectNotNull("loadReferrerOption<Purchase, PurchaseCB>", loadReferrerOption);
        if (productList.isEmpty()) { return; }
        final PurchaseBhv referrerBhv = xgetBSFLR().select(PurchaseBhv.class);
        helpLoadReferrerInternally(productList, loadReferrerOption, new InternalLoadReferrerCallback<Product, Integer, PurchaseCB, Purchase>() {
            public Integer callbackBase_getPrimaryKeyValue(Product entity) { return entity.getProductId(); }
            public void callbackBase_setReferrerList(Product entity, List<Purchase> referrerList) { entity.setPurchaseList(referrerList); }
            public PurchaseCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(PurchaseCB cb, List<Integer> pkList) { cb.query().setProductId_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(PurchaseCB cb) { cb.query().addOrderBy_ProductId_Asc(); }
            public List<Purchase> callbackReferrer_selectList(PurchaseCB cb) { return referrerBhv.selectList(cb); }
            public Integer callbackReferrer_getForeignKeyValue(Purchase entity) { return entity.getProductId(); }
            public void callbackReferrer_setForeignEntity(Purchase referrerEntity, Product baseEntity) { referrerEntity.setProduct(baseEntity); }
        } );
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of foreign table 'ProductStatus'.
     * @param productList The list of product. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<ProductStatus> pulloutProductStatus(List<Product> productList) {
        return helpPulloutInternally(productList, new InternalPulloutCallback<Product, ProductStatus>() {
            public ProductStatus callbackGetForeignEntity(Product entity) { return entity.getProductStatus(); } });
    }
  
    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param product The entity of insert target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(Product product) {
        assertEntityNotNull(product);
        delegateInsert(product);
    }

    @Override
    protected void doCreate(Entity product) {
        insert((Product)product);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param product The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final Product product) {
        helpUpdateInternally(product, new InternalUpdateCallback<Product>() {
            public int callbackDelegateUpdate(Product entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((Product)entity);
    }
    
    /**
     * Update the entity non-strictly modified-only. {UpdateCountZeroException, NonConcurrencyControl}
     * @param product The entity of update target. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void updateNonstrict(final Product product) {
        helpUpdateNonstrictInternally(product, new InternalUpdateNonstrictCallback<Product>() {
            public int callbackDelegateUpdateNonstrict(Product entity) { return delegateUpdateNonstrict(entity); } });
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        updateNonstrict((Product)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param product The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final Product product) {
        helpInsertOrUpdateInternally(product, new InternalInsertOrUpdateCallback<Product, ProductCB>() {
            public void callbackInsert(Product entity) { insert(entity); }
            public void callbackUpdate(Product entity) { update(entity); }
            public ProductCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(ProductCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity product) {
        insertOrUpdate((Product)product);
    }

    /**
     * Insert or update the entity non-strictly modified-only. {NonConcurrencyControl(when update)}
     * @param product The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdateNonstrict(Product product) {
        helpInsertOrUpdateInternally(product, new InternalInsertOrUpdateNonstrictCallback<Product>() {
            public void callbackInsert(Product entity) { insert(entity); }
            public void callbackUpdateNonstrict(Product entity) { updateNonstrict(entity); }
        });
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdateNonstrict((Product)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param product The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(Product product) {
        helpDeleteInternally(product, new InternalDeleteCallback<Product>() {
            public int callbackDelegateDelete(Product entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity product) {
        delete((Product)product);
    }

    /**
     * Delete the entity non-strictly. {UpdateCountZeroException, NonConcurrencyControl}
     * @param product Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(Product product) {
        helpDeleteNonstrictInternally(product, new InternalDeleteNonstrictCallback<Product>() {
            public int callbackDelegateDeleteNonstrict(Product entity) { return delegateDeleteNonstrict(entity); } });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {UpdateCountZeroException, NonConcurrencyControl}
     * @param product Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(Product product) {
        helpDeleteNonstrictIgnoreDeletedInternally(product, new InternalDeleteNonstrictIgnoreDeletedCallback<Product>() {
            public int callbackDelegateDeleteNonstrict(Product entity) { return delegateDeleteNonstrict(entity); } });
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<Product> productList) {
        assertObjectNotNull("productList", productList);
        return delegateInsertList(productList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchUpdate(List<Product> productList) {
        assertObjectNotNull("productList", productList);
        return delegateUpdateList(productList);
    }

    /**
     * Batch update the list non-strictly. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdateNonstrict(List<Product> productList) {
        assertObjectNotNull("productList", productList);
        return delegateUpdateListNonstrict(productList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchDelete(List<Product> productList) {
        assertObjectNotNull("productList", productList);
        return delegateDeleteList(productList);
    }

    /**
     * Batch delete the list non-strictly. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param productList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDeleteNonstrict(List<Product> productList) {
        assertObjectNotNull("productList", productList);
        return delegateDeleteListNonstrict(productList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param product Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(Product product, ProductCB cb) {
        assertObjectNotNull("product", product); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(product);
        filterEntityOfUpdate(product); assertEntityOfUpdate(product);
        return invoke(createQueryUpdateEntityCBCommand(product, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(ProductCB cb) {
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
    protected int delegateSelectCount(ProductCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((ProductCB)cb); }
    protected List<Product> delegateSelectList(ProductCB cb) {
        return invoke(createSelectListCBCommand(cb, Product.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((ProductCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(Product e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(Product e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateUpdateNonstrict(Product e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateNonstrictEntityCommand(e));
    }
    protected int delegateDelete(Product e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }
    protected int delegateDeleteNonstrict(Product e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<Product> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<Product> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateUpdateListNonstrict(List<Product> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    protected int[] delegateDeleteList(List<Product> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }
    protected int[] delegateDeleteListNonstrict(List<Product> ls) {
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
    protected Product downcast(Entity entity) {
        return helpDowncastInternally(entity, Product.class);
    }
}
