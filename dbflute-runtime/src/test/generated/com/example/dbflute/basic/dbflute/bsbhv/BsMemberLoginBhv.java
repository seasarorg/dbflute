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
import com.example.dbflute.basic.dbflute.allcommon.*;
import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of MEMBER_LOGIN that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_LOGIN_ID
 * 
 * [column]
 *     MEMBER_LOGIN_ID, MEMBER_ID, LOGIN_DATETIME, LOGIN_MOBILE_FLG, LOGIN_MEMBER_STATUS_CODE
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     MEMBER_LOGIN_ID
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     MEMBER, MEMBER_STATUS
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member, memberStatus
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberLoginBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "MEMBER_LOGIN"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return MemberLoginDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public MemberLoginDbm getMyDBMeta() { return MemberLoginDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public MemberLogin newMyEntity() { return new MemberLogin(); }
    public MemberLoginCB newMyConditionBean() { return new MemberLoginCB(); }

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
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @return The selected count.
     */
    public int selectCount(MemberLoginCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberLogin selectEntity(final MemberLoginCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<MemberLogin, MemberLoginCB>() {
            public List<MemberLogin> callbackSelectList(MemberLoginCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberLogin selectEntityWithDeletedCheck(final MemberLoginCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<MemberLogin, MemberLoginCB>() {
            public List<MemberLogin> callbackSelectList(MemberLoginCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberLogin selectByPKValueWithDeletedCheck(Long memberLoginId) {
        MemberLogin entity = new MemberLogin();
        entity.setMemberLoginId(memberLoginId);
        final MemberLoginCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<MemberLogin> selectList(MemberLoginCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<MemberLogin>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<MemberLogin> selectPage(final MemberLoginCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<MemberLogin> invoker = new PagingInvoker<MemberLogin>(getTableDbName());
        final PagingHandler<MemberLogin> handler = new PagingHandler<MemberLogin>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<MemberLogin> paging() { return selectList(cb); }
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
     * memberLoginBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberLoginCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<MemberLoginCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        MemberLoginCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<MemberLoginCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of MemberLogin. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(MemberLoginCB cb, ValueLabelSetupper<MemberLogin> valueLabelSetupper) {
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
     * @param memberLoginList The list of memberLogin. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<Member> pulloutMember(List<MemberLogin> memberLoginList) {
        return helpPulloutInternally(memberLoginList, new InternalPulloutCallback<MemberLogin, Member>() {
            public Member callbackGetForeignEntity(MemberLogin entity) { return entity.getMember(); } });
    }
    /**
     * Pull out the list of foreign table 'MemberStatus'.
     * @param memberLoginList The list of memberLogin. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<MemberStatus> pulloutMemberStatus(List<MemberLogin> memberLoginList) {
        return helpPulloutInternally(memberLoginList, new InternalPulloutCallback<MemberLogin, MemberStatus>() {
            public MemberStatus callbackGetForeignEntity(MemberLogin entity) { return entity.getMemberStatus(); } });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param memberLogin The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(MemberLogin memberLogin) {
        assertEntityNotNull(memberLogin);
        delegateInsert(memberLogin);
    }

    @Override
    protected void doCreate(Entity memberLogin) {
        insert((MemberLogin)memberLogin);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberLogin The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final MemberLogin memberLogin) {
        helpUpdateInternally(memberLogin, new InternalUpdateCallback<MemberLogin>() {
            public int callbackDelegateUpdate(MemberLogin entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((MemberLogin)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((MemberLogin)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param memberLogin The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final MemberLogin memberLogin) {
        helpInsertOrUpdateInternally(memberLogin, new InternalInsertOrUpdateCallback<MemberLogin, MemberLoginCB>() {
            public void callbackInsert(MemberLogin entity) { insert(entity); }
            public void callbackUpdate(MemberLogin entity) { update(entity); }
            public MemberLoginCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(MemberLoginCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity memberLogin) {
        insertOrUpdate((MemberLogin)memberLogin);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((MemberLogin)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberLogin The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(MemberLogin memberLogin) {
        helpDeleteInternally(memberLogin, new InternalDeleteCallback<MemberLogin>() {
            public int callbackDelegateDelete(MemberLogin entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity memberLogin) {
        delete((MemberLogin)memberLogin);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberLoginList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<MemberLogin> memberLoginList) {
        assertObjectNotNull("memberLoginList", memberLoginList);
        return delegateInsertList(memberLoginList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberLoginList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<MemberLogin> memberLoginList) {
        assertObjectNotNull("memberLoginList", memberLoginList);
        return delegateUpdateList(memberLoginList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberLoginList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<MemberLogin> memberLoginList) {
        assertObjectNotNull("memberLoginList", memberLoginList);
        return delegateDeleteList(memberLoginList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param memberLogin Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(MemberLogin memberLogin, MemberLoginCB cb) {
        assertObjectNotNull("memberLogin", memberLogin); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(memberLogin);
        filterEntityOfUpdate(memberLogin); assertEntityOfUpdate(memberLogin);
        return invoke(createQueryUpdateEntityCBCommand(memberLogin, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(MemberLoginCB cb) {
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
    protected int delegateSelectCount(MemberLoginCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((MemberLoginCB)cb); }
    protected List<MemberLogin> delegateSelectList(MemberLoginCB cb) {
        return invoke(createSelectListCBCommand(cb, MemberLogin.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((MemberLoginCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(MemberLogin e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(MemberLogin e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(MemberLogin e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<MemberLogin> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<MemberLogin> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<MemberLogin> ls) {
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
    protected MemberLogin downcast(Entity entity) {
        return helpDowncastInternally(entity, MemberLogin.class);
    }
}
