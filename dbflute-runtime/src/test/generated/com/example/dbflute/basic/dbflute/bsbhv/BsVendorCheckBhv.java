package com.example.dbflute.basic.dbflute.bsbhv;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.*;
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

import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.allcommon.DBCurrent;
import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of VENDOR_CHECK that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     VENDOR_CHECK_ID
 * 
 * [column]
 *     VENDOR_CHECK_ID, DECIMAL_DIGIT, INTEGER_NON_DIGIT, TYPE_OF_BOOLEAN, TYPE_OF_TEXT
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
 *     
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsVendorCheckBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    public static final String PATH_various_vendorcheck_selectSimpleVendorCheck = "various:vendorcheck:selectSimpleVendorCheck";
    public static final String PATH_various_vendorcheck_selectVendorCheckDecimalSum = "various:vendorcheck:selectVendorCheckDecimalSum";
    public static final String PATH_various_vendorcheck_selectVendorCheckIntegerSum = "various:vendorcheck:selectVendorCheckIntegerSum";
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "VENDOR_CHECK"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return VendorCheckDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public VendorCheckDbm getMyDBMeta() { return VendorCheckDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public VendorCheck newMyEntity() { return new VendorCheck(); }
    public VendorCheckCB newMyConditionBean() { return new VendorCheckCB(); }

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
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @return The selected count.
     */
    public int selectCount(VendorCheckCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public VendorCheck selectEntity(final VendorCheckCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<VendorCheck, VendorCheckCB>() {
            public List<VendorCheck> callbackSelectList(VendorCheckCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public VendorCheck selectEntityWithDeletedCheck(final VendorCheckCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<VendorCheck, VendorCheckCB>() {
            public List<VendorCheck> callbackSelectList(VendorCheckCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public VendorCheck selectByPKValueWithDeletedCheck(Long vendorCheckId) {
        VendorCheck entity = new VendorCheck();
        entity.setVendorCheckId(vendorCheckId);
        final VendorCheckCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<VendorCheck> selectList(VendorCheckCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<VendorCheck>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<VendorCheck> selectPage(final VendorCheckCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<VendorCheck> invoker = new PagingInvoker<VendorCheck>(getTableDbName());
        final PagingHandler<VendorCheck> handler = new PagingHandler<VendorCheck>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<VendorCheck> paging() { return selectList(cb); }
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
     * vendorCheckBhv.scalarSelect(Date.class).max(new ScalarQuery(VendorCheckCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<VendorCheckCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        VendorCheckCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<VendorCheckCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of VendorCheck. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(VendorCheckCB cb, ValueLabelSetupper<VendorCheck> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param vendorCheck The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(VendorCheck vendorCheck) {
        assertEntityNotNull(vendorCheck);
        delegateInsert(vendorCheck);
    }

    @Override
    protected void doCreate(Entity vendorCheck) {
        insert((VendorCheck)vendorCheck);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param vendorCheck The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final VendorCheck vendorCheck) {
        helpUpdateInternally(vendorCheck, new InternalUpdateCallback<VendorCheck>() {
            public int callbackDelegateUpdate(VendorCheck entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((VendorCheck)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((VendorCheck)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param vendorCheck The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final VendorCheck vendorCheck) {
        helpInsertOrUpdateInternally(vendorCheck, new InternalInsertOrUpdateCallback<VendorCheck, VendorCheckCB>() {
            public void callbackInsert(VendorCheck entity) { insert(entity); }
            public void callbackUpdate(VendorCheck entity) { update(entity); }
            public VendorCheckCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(VendorCheckCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity vendorCheck) {
        insertOrUpdate((VendorCheck)vendorCheck);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((VendorCheck)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param vendorCheck The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(VendorCheck vendorCheck) {
        helpDeleteInternally(vendorCheck, new InternalDeleteCallback<VendorCheck>() {
            public int callbackDelegateDelete(VendorCheck entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity vendorCheck) {
        delete((VendorCheck)vendorCheck);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param vendorCheckList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<VendorCheck> vendorCheckList) {
        assertObjectNotNull("vendorCheckList", vendorCheckList);
        return delegateInsertList(vendorCheckList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param vendorCheckList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<VendorCheck> vendorCheckList) {
        assertObjectNotNull("vendorCheckList", vendorCheckList);
        return delegateUpdateList(vendorCheckList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param vendorCheckList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<VendorCheck> vendorCheckList) {
        assertObjectNotNull("vendorCheckList", vendorCheckList);
        return delegateDeleteList(vendorCheckList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param vendorCheck Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(VendorCheck vendorCheck, VendorCheckCB cb) {
        assertObjectNotNull("vendorCheck", vendorCheck); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(vendorCheck);
        filterEntityOfUpdate(vendorCheck); assertEntityOfUpdate(vendorCheck);
        return invoke(createQueryUpdateEntityCBCommand(vendorCheck, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(VendorCheckCB cb) {
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
    protected int delegateSelectCount(VendorCheckCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((VendorCheckCB)cb); }
    protected List<VendorCheck> delegateSelectList(VendorCheckCB cb) {
        return invoke(createSelectListCBCommand(cb, VendorCheck.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((VendorCheckCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(VendorCheck e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(VendorCheck e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(VendorCheck e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<VendorCheck> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<VendorCheck> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<VendorCheck> ls) {
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
    protected VendorCheck downcast(Entity entity) {
        return helpDowncastInternally(entity, VendorCheck.class);
    }
}
