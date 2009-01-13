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
 * The behavior of MEMBER_WITHDRAWAL that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_ID
 * 
 * [column]
 *     MEMBER_ID, WITHDRAWAL_REASON_CODE, WITHDRAWAL_REASON_INPUT_TEXT, WITHDRAWAL_DATETIME, REGISTER_DATETIME, REGISTER_PROCESS, REGISTER_USER, UPDATE_DATETIME, UPDATE_PROCESS, UPDATE_USER, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER, WITHDRAWAL_REASON
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member, withdrawalReason
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberWithdrawalBhv extends org.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "MEMBER_WITHDRAWAL"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return MemberWithdrawalDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public MemberWithdrawalDbm getMyDBMeta() { return MemberWithdrawalDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public MemberWithdrawal newMyEntity() { return new MemberWithdrawal(); }
    public MemberWithdrawalCB newMyConditionBean() { return new MemberWithdrawalCB(); }

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
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @return The selected count.
     */
    public int selectCount(MemberWithdrawalCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberWithdrawal selectEntity(final MemberWithdrawalCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<MemberWithdrawal, MemberWithdrawalCB>() {
            public List<MemberWithdrawal> callbackSelectList(MemberWithdrawalCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberWithdrawal selectEntityWithDeletedCheck(final MemberWithdrawalCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<MemberWithdrawal, MemberWithdrawalCB>() {
            public List<MemberWithdrawal> callbackSelectList(MemberWithdrawalCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberWithdrawal selectByPKValueWithDeletedCheck(Integer memberId) {
        MemberWithdrawal entity = new MemberWithdrawal();
        entity.setMemberId(memberId);
        final MemberWithdrawalCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<MemberWithdrawal> selectList(MemberWithdrawalCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<MemberWithdrawal>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<MemberWithdrawal> selectPage(final MemberWithdrawalCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<MemberWithdrawal> invoker = new PagingInvoker<MemberWithdrawal>(getTableDbName());
        final PagingHandler<MemberWithdrawal> handler = new PagingHandler<MemberWithdrawal>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<MemberWithdrawal> paging() { return selectList(cb); }
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
     * memberWithdrawalBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberWithdrawalCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<MemberWithdrawalCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        MemberWithdrawalCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<MemberWithdrawalCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of MemberWithdrawal. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(MemberWithdrawalCB cb, ValueLabelSetupper<MemberWithdrawal> valueLabelSetupper) {
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
     * @param memberWithdrawalList The list of memberWithdrawal. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<Member> pulloutMember(List<MemberWithdrawal> memberWithdrawalList) {
        return helpPulloutInternally(memberWithdrawalList, new InternalPulloutCallback<MemberWithdrawal, Member>() {
            public Member callbackGetForeignEntity(MemberWithdrawal entity) { return entity.getMember(); } });
    }
    /**
     * Pull out the list of foreign table 'WithdrawalReason'.
     * @param memberWithdrawalList The list of memberWithdrawal. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<WithdrawalReason> pulloutWithdrawalReason(List<MemberWithdrawal> memberWithdrawalList) {
        return helpPulloutInternally(memberWithdrawalList, new InternalPulloutCallback<MemberWithdrawal, WithdrawalReason>() {
            public WithdrawalReason callbackGetForeignEntity(MemberWithdrawal entity) { return entity.getWithdrawalReason(); } });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param memberWithdrawal The entity of insert target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(MemberWithdrawal memberWithdrawal) {
        assertEntityNotNull(memberWithdrawal);
        delegateInsert(memberWithdrawal);
    }

    @Override
    protected void doCreate(Entity memberWithdrawal) {
        insert((MemberWithdrawal)memberWithdrawal);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberWithdrawal The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final MemberWithdrawal memberWithdrawal) {
        helpUpdateInternally(memberWithdrawal, new InternalUpdateCallback<MemberWithdrawal>() {
            public int callbackDelegateUpdate(MemberWithdrawal entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((MemberWithdrawal)entity);
    }
    
    /**
     * Update the entity non-strictly modified-only. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberWithdrawal The entity of update target. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void updateNonstrict(final MemberWithdrawal memberWithdrawal) {
        helpUpdateNonstrictInternally(memberWithdrawal, new InternalUpdateNonstrictCallback<MemberWithdrawal>() {
            public int callbackDelegateUpdateNonstrict(MemberWithdrawal entity) { return delegateUpdateNonstrict(entity); } });
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        updateNonstrict((MemberWithdrawal)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param memberWithdrawal The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final MemberWithdrawal memberWithdrawal) {
        helpInsertOrUpdateInternally(memberWithdrawal, new InternalInsertOrUpdateCallback<MemberWithdrawal, MemberWithdrawalCB>() {
            public void callbackInsert(MemberWithdrawal entity) { insert(entity); }
            public void callbackUpdate(MemberWithdrawal entity) { update(entity); }
            public MemberWithdrawalCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(MemberWithdrawalCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity memberWithdrawal) {
        insertOrUpdate((MemberWithdrawal)memberWithdrawal);
    }

    /**
     * Insert or update the entity non-strictly modified-only. {NonConcurrencyControl(when update)}
     * @param memberWithdrawal The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdateNonstrict(MemberWithdrawal memberWithdrawal) {
        helpInsertOrUpdateInternally(memberWithdrawal, new InternalInsertOrUpdateNonstrictCallback<MemberWithdrawal>() {
            public void callbackInsert(MemberWithdrawal entity) { insert(entity); }
            public void callbackUpdateNonstrict(MemberWithdrawal entity) { updateNonstrict(entity); }
        });
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdateNonstrict((MemberWithdrawal)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberWithdrawal The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(MemberWithdrawal memberWithdrawal) {
        helpDeleteInternally(memberWithdrawal, new InternalDeleteCallback<MemberWithdrawal>() {
            public int callbackDelegateDelete(MemberWithdrawal entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity memberWithdrawal) {
        delete((MemberWithdrawal)memberWithdrawal);
    }

    /**
     * Delete the entity non-strictly. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberWithdrawal Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(MemberWithdrawal memberWithdrawal) {
        helpDeleteNonstrictInternally(memberWithdrawal, new InternalDeleteNonstrictCallback<MemberWithdrawal>() {
            public int callbackDelegateDeleteNonstrict(MemberWithdrawal entity) { return delegateDeleteNonstrict(entity); } });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberWithdrawal Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(MemberWithdrawal memberWithdrawal) {
        helpDeleteNonstrictIgnoreDeletedInternally(memberWithdrawal, new InternalDeleteNonstrictIgnoreDeletedCallback<MemberWithdrawal>() {
            public int callbackDelegateDeleteNonstrict(MemberWithdrawal entity) { return delegateDeleteNonstrict(entity); } });
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberWithdrawalList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<MemberWithdrawal> memberWithdrawalList) {
        assertObjectNotNull("memberWithdrawalList", memberWithdrawalList);
        return delegateInsertList(memberWithdrawalList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberWithdrawalList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchUpdate(List<MemberWithdrawal> memberWithdrawalList) {
        assertObjectNotNull("memberWithdrawalList", memberWithdrawalList);
        return delegateUpdateList(memberWithdrawalList);
    }

    /**
     * Batch update the list non-strictly. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberWithdrawalList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdateNonstrict(List<MemberWithdrawal> memberWithdrawalList) {
        assertObjectNotNull("memberWithdrawalList", memberWithdrawalList);
        return delegateUpdateListNonstrict(memberWithdrawalList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberWithdrawalList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchDelete(List<MemberWithdrawal> memberWithdrawalList) {
        assertObjectNotNull("memberWithdrawalList", memberWithdrawalList);
        return delegateDeleteList(memberWithdrawalList);
    }

    /**
     * Batch delete the list non-strictly. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberWithdrawalList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDeleteNonstrict(List<MemberWithdrawal> memberWithdrawalList) {
        assertObjectNotNull("memberWithdrawalList", memberWithdrawalList);
        return delegateDeleteListNonstrict(memberWithdrawalList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param memberWithdrawal Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(MemberWithdrawal memberWithdrawal, MemberWithdrawalCB cb) {
        assertObjectNotNull("memberWithdrawal", memberWithdrawal); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(memberWithdrawal);
        filterEntityOfUpdate(memberWithdrawal); assertEntityOfUpdate(memberWithdrawal);
        return invoke(createQueryUpdateEntityCBCommand(memberWithdrawal, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(MemberWithdrawalCB cb) {
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
    protected int delegateSelectCount(MemberWithdrawalCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((MemberWithdrawalCB)cb); }
    protected List<MemberWithdrawal> delegateSelectList(MemberWithdrawalCB cb) {
        return invoke(createSelectListCBCommand(cb, MemberWithdrawal.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((MemberWithdrawalCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(MemberWithdrawal e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(MemberWithdrawal e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateUpdateNonstrict(MemberWithdrawal e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateNonstrictEntityCommand(e));
    }
    protected int delegateDelete(MemberWithdrawal e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }
    protected int delegateDeleteNonstrict(MemberWithdrawal e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<MemberWithdrawal> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<MemberWithdrawal> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateUpdateListNonstrict(List<MemberWithdrawal> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    protected int[] delegateDeleteList(List<MemberWithdrawal> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }
    protected int[] delegateDeleteListNonstrict(List<MemberWithdrawal> ls) {
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
    protected MemberWithdrawal downcast(Entity entity) {
        return helpDowncastInternally(entity, MemberWithdrawal.class);
    }
}
