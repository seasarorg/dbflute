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
 * The behavior of WITHDRAWAL_REASON that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     WITHDRAWAL_REASON_CODE
 * 
 * [column]
 *     WITHDRAWAL_REASON_CODE, WITHDRAWAL_REASON_TEXT, DISPLAY_ORDER
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
 *     MEMBER_WITHDRAWAL
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     memberWithdrawalList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWithdrawalReasonBhv extends org.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "WITHDRAWAL_REASON"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return WithdrawalReasonDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public WithdrawalReasonDbm getMyDBMeta() { return WithdrawalReasonDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public WithdrawalReason newMyEntity() { return new WithdrawalReason(); }
    public WithdrawalReasonCB newMyConditionBean() { return new WithdrawalReasonCB(); }

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
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @return The selected count.
     */
    public int selectCount(WithdrawalReasonCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public WithdrawalReason selectEntity(final WithdrawalReasonCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<WithdrawalReason, WithdrawalReasonCB>() {
            public List<WithdrawalReason> callbackSelectList(WithdrawalReasonCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public WithdrawalReason selectEntityWithDeletedCheck(final WithdrawalReasonCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<WithdrawalReason, WithdrawalReasonCB>() {
            public List<WithdrawalReason> callbackSelectList(WithdrawalReasonCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public WithdrawalReason selectByPKValueWithDeletedCheck(String withdrawalReasonCode) {
        WithdrawalReason entity = new WithdrawalReason();
        entity.setWithdrawalReasonCode(withdrawalReasonCode);
        final WithdrawalReasonCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<WithdrawalReason> selectList(WithdrawalReasonCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<WithdrawalReason>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<WithdrawalReason> selectPage(final WithdrawalReasonCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<WithdrawalReason> invoker = new PagingInvoker<WithdrawalReason>(getTableDbName());
        final PagingHandler<WithdrawalReason> handler = new PagingHandler<WithdrawalReason>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<WithdrawalReason> paging() { return selectList(cb); }
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
     * withdrawalReasonBhv.scalarSelect(Date.class).max(new ScalarQuery(WithdrawalReasonCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<WithdrawalReasonCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        WithdrawalReasonCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<WithdrawalReasonCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of WithdrawalReason. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(WithdrawalReasonCB cb, ValueLabelSetupper<WithdrawalReason> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer of memberWithdrawalList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setWithdrawalReasonCode_InScope(pkList);
     * cb.query().addOrderBy_WithdrawalReasonCode_Asc();
     * </pre>
     * @param withdrawalReasonList The entity list of withdrawalReason. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadMemberWithdrawalList(List<WithdrawalReason> withdrawalReasonList, ConditionBeanSetupper<MemberWithdrawalCB> conditionBeanSetupper) {
        assertObjectNotNull("withdrawalReasonList<WithdrawalReason>", withdrawalReasonList);
        assertObjectNotNull("conditionBeanSetupper<MemberWithdrawalCB>", conditionBeanSetupper);
        if (withdrawalReasonList.isEmpty()) { return; }
        loadMemberWithdrawalList(withdrawalReasonList, new LoadReferrerOption<MemberWithdrawalCB, MemberWithdrawal>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param withdrawalReasonList The entity list of withdrawalReason. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadMemberWithdrawalList(List<WithdrawalReason> withdrawalReasonList, LoadReferrerOption<MemberWithdrawalCB, MemberWithdrawal> loadReferrerOption) {
        assertObjectNotNull("withdrawalReasonList<WithdrawalReason>", withdrawalReasonList);
        assertObjectNotNull("loadReferrerOption<MemberWithdrawal, MemberWithdrawalCB>", loadReferrerOption);
        if (withdrawalReasonList.isEmpty()) { return; }
        final MemberWithdrawalBhv referrerBhv = xgetBSFLR().select(MemberWithdrawalBhv.class);
        helpLoadReferrerInternally(withdrawalReasonList, loadReferrerOption, new InternalLoadReferrerCallback<WithdrawalReason, String, MemberWithdrawalCB, MemberWithdrawal>() {
            public String callbackBase_getPrimaryKeyValue(WithdrawalReason entity) { return entity.getWithdrawalReasonCode(); }
            public void callbackBase_setReferrerList(WithdrawalReason entity, List<MemberWithdrawal> referrerList) { entity.setMemberWithdrawalList(referrerList); }
            public MemberWithdrawalCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(MemberWithdrawalCB cb, List<String> pkList) { cb.query().setWithdrawalReasonCode_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(MemberWithdrawalCB cb) { cb.query().addOrderBy_WithdrawalReasonCode_Asc(); }
            public List<MemberWithdrawal> callbackReferrer_selectList(MemberWithdrawalCB cb) { return referrerBhv.selectList(cb); }
            public String callbackReferrer_getForeignKeyValue(MemberWithdrawal entity) { return entity.getWithdrawalReasonCode(); }
            public void callbackReferrer_setForeignEntity(MemberWithdrawal referrerEntity, WithdrawalReason baseEntity) { referrerEntity.setWithdrawalReason(baseEntity); }
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
     * @param withdrawalReason The entity of insert target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(WithdrawalReason withdrawalReason) {
        assertEntityNotNull(withdrawalReason);
        delegateInsert(withdrawalReason);
    }

    @Override
    protected void doCreate(Entity withdrawalReason) {
        insert((WithdrawalReason)withdrawalReason);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param withdrawalReason The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final WithdrawalReason withdrawalReason) {
        helpUpdateInternally(withdrawalReason, new InternalUpdateCallback<WithdrawalReason>() {
            public int callbackDelegateUpdate(WithdrawalReason entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((WithdrawalReason)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((WithdrawalReason)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param withdrawalReason The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final WithdrawalReason withdrawalReason) {
        helpInsertOrUpdateInternally(withdrawalReason, new InternalInsertOrUpdateCallback<WithdrawalReason, WithdrawalReasonCB>() {
            public void callbackInsert(WithdrawalReason entity) { insert(entity); }
            public void callbackUpdate(WithdrawalReason entity) { update(entity); }
            public WithdrawalReasonCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(WithdrawalReasonCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity withdrawalReason) {
        insertOrUpdate((WithdrawalReason)withdrawalReason);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((WithdrawalReason)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param withdrawalReason The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(WithdrawalReason withdrawalReason) {
        helpDeleteInternally(withdrawalReason, new InternalDeleteCallback<WithdrawalReason>() {
            public int callbackDelegateDelete(WithdrawalReason entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity withdrawalReason) {
        delete((WithdrawalReason)withdrawalReason);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param withdrawalReasonList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<WithdrawalReason> withdrawalReasonList) {
        assertObjectNotNull("withdrawalReasonList", withdrawalReasonList);
        return delegateInsertList(withdrawalReasonList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param withdrawalReasonList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<WithdrawalReason> withdrawalReasonList) {
        assertObjectNotNull("withdrawalReasonList", withdrawalReasonList);
        return delegateUpdateList(withdrawalReasonList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param withdrawalReasonList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<WithdrawalReason> withdrawalReasonList) {
        assertObjectNotNull("withdrawalReasonList", withdrawalReasonList);
        return delegateDeleteList(withdrawalReasonList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param withdrawalReason Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(WithdrawalReason withdrawalReason, WithdrawalReasonCB cb) {
        assertObjectNotNull("withdrawalReason", withdrawalReason); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(withdrawalReason);
        filterEntityOfUpdate(withdrawalReason); assertEntityOfUpdate(withdrawalReason);
        return invoke(createQueryUpdateEntityCBCommand(withdrawalReason, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(WithdrawalReasonCB cb) {
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
    protected int delegateSelectCount(WithdrawalReasonCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((WithdrawalReasonCB)cb); }
    protected List<WithdrawalReason> delegateSelectList(WithdrawalReasonCB cb) {
        return invoke(createSelectListCBCommand(cb, WithdrawalReason.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((WithdrawalReasonCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(WithdrawalReason e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(WithdrawalReason e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(WithdrawalReason e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<WithdrawalReason> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<WithdrawalReason> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<WithdrawalReason> ls) {
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
    protected WithdrawalReason downcast(Entity entity) {
        return helpDowncastInternally(entity, WithdrawalReason.class);
    }
}
