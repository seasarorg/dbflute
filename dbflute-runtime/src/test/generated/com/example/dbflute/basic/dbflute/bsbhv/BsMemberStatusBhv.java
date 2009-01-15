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
import com.example.dbflute.basic.dbflute.allcommon.*;
import com.example.dbflute.basic.dbflute.exbhv.*;
import com.example.dbflute.basic.dbflute.exentity.*;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.*;
import com.example.dbflute.basic.dbflute.cbean.*;

/**
 * The behavior of MEMBER_STATUS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_STATUS_CODE
 * 
 * [column]
 *     MEMBER_STATUS_CODE, MEMBER_STATUS_NAME, DISPLAY_ORDER
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
 *     MEMBER, MEMBER_LOGIN
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     memberList, memberLoginList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberStatusBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    public static final String PATH_selectDisplayMemberStatus = "selectDisplayMemberStatus";
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "MEMBER_STATUS"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return MemberStatusDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public MemberStatusDbm getMyDBMeta() { return MemberStatusDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public MemberStatus newMyEntity() { return new MemberStatus(); }
    public MemberStatusCB newMyConditionBean() { return new MemberStatusCB(); }

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
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @return The selected count.
     */
    public int selectCount(MemberStatusCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberStatus selectEntity(final MemberStatusCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<MemberStatus, MemberStatusCB>() {
            public List<MemberStatus> callbackSelectList(MemberStatusCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberStatus selectEntityWithDeletedCheck(final MemberStatusCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<MemberStatus, MemberStatusCB>() {
            public List<MemberStatus> callbackSelectList(MemberStatusCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberStatus selectByPKValueWithDeletedCheck(String memberStatusCode) {
        MemberStatus entity = new MemberStatus();
        entity.setMemberStatusCode(memberStatusCode);
        final MemberStatusCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<MemberStatus> selectList(MemberStatusCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<MemberStatus>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<MemberStatus> selectPage(final MemberStatusCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<MemberStatus> invoker = new PagingInvoker<MemberStatus>(getTableDbName());
        final PagingHandler<MemberStatus> handler = new PagingHandler<MemberStatus>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<MemberStatus> paging() { return selectList(cb); }
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
     * memberStatusBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberStatusCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<MemberStatusCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        MemberStatusCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<MemberStatusCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of MemberStatus. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(MemberStatusCB cb, ValueLabelSetupper<MemberStatus> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer of memberList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setMemberStatusCode_InScope(pkList);
     * cb.query().addOrderBy_MemberStatusCode_Asc();
     * </pre>
     * @param memberStatusList The entity list of memberStatus. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadMemberList(List<MemberStatus> memberStatusList, ConditionBeanSetupper<MemberCB> conditionBeanSetupper) {
        assertObjectNotNull("memberStatusList<MemberStatus>", memberStatusList);
        assertObjectNotNull("conditionBeanSetupper<MemberCB>", conditionBeanSetupper);
        if (memberStatusList.isEmpty()) { return; }
        loadMemberList(memberStatusList, new LoadReferrerOption<MemberCB, Member>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param memberStatusList The entity list of memberStatus. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadMemberList(List<MemberStatus> memberStatusList, LoadReferrerOption<MemberCB, Member> loadReferrerOption) {
        assertObjectNotNull("memberStatusList<MemberStatus>", memberStatusList);
        assertObjectNotNull("loadReferrerOption<Member, MemberCB>", loadReferrerOption);
        if (memberStatusList.isEmpty()) { return; }
        final MemberBhv referrerBhv = xgetBSFLR().select(MemberBhv.class);
        helpLoadReferrerInternally(memberStatusList, loadReferrerOption, new InternalLoadReferrerCallback<MemberStatus, String, MemberCB, Member>() {
            public String callbackBase_getPrimaryKeyValue(MemberStatus entity) { return entity.getMemberStatusCode(); }
            public void callbackBase_setReferrerList(MemberStatus entity, List<Member> referrerList) { entity.setMemberList(referrerList); }
            public MemberCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(MemberCB cb, List<String> pkList) { cb.query().setMemberStatusCode_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(MemberCB cb) { cb.query().addOrderBy_MemberStatusCode_Asc(); }
            public List<Member> callbackReferrer_selectList(MemberCB cb) { return referrerBhv.selectList(cb); }
            public String callbackReferrer_getForeignKeyValue(Member entity) { return entity.getMemberStatusCode(); }
            public void callbackReferrer_setForeignEntity(Member referrerEntity, MemberStatus baseEntity) { referrerEntity.setMemberStatus(baseEntity); }
        } );
    }
    /**
     * Load referrer of memberLoginList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setLoginMemberStatusCode_InScope(pkList);
     * cb.query().addOrderBy_LoginMemberStatusCode_Asc();
     * </pre>
     * @param memberStatusList The entity list of memberStatus. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadMemberLoginList(List<MemberStatus> memberStatusList, ConditionBeanSetupper<MemberLoginCB> conditionBeanSetupper) {
        assertObjectNotNull("memberStatusList<MemberStatus>", memberStatusList);
        assertObjectNotNull("conditionBeanSetupper<MemberLoginCB>", conditionBeanSetupper);
        if (memberStatusList.isEmpty()) { return; }
        loadMemberLoginList(memberStatusList, new LoadReferrerOption<MemberLoginCB, MemberLogin>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param memberStatusList The entity list of memberStatus. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadMemberLoginList(List<MemberStatus> memberStatusList, LoadReferrerOption<MemberLoginCB, MemberLogin> loadReferrerOption) {
        assertObjectNotNull("memberStatusList<MemberStatus>", memberStatusList);
        assertObjectNotNull("loadReferrerOption<MemberLogin, MemberLoginCB>", loadReferrerOption);
        if (memberStatusList.isEmpty()) { return; }
        final MemberLoginBhv referrerBhv = xgetBSFLR().select(MemberLoginBhv.class);
        helpLoadReferrerInternally(memberStatusList, loadReferrerOption, new InternalLoadReferrerCallback<MemberStatus, String, MemberLoginCB, MemberLogin>() {
            public String callbackBase_getPrimaryKeyValue(MemberStatus entity) { return entity.getMemberStatusCode(); }
            public void callbackBase_setReferrerList(MemberStatus entity, List<MemberLogin> referrerList) { entity.setMemberLoginList(referrerList); }
            public MemberLoginCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(MemberLoginCB cb, List<String> pkList) { cb.query().setLoginMemberStatusCode_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(MemberLoginCB cb) { cb.query().addOrderBy_LoginMemberStatusCode_Asc(); }
            public List<MemberLogin> callbackReferrer_selectList(MemberLoginCB cb) { return referrerBhv.selectList(cb); }
            public String callbackReferrer_getForeignKeyValue(MemberLogin entity) { return entity.getLoginMemberStatusCode(); }
            public void callbackReferrer_setForeignEntity(MemberLogin referrerEntity, MemberStatus baseEntity) { referrerEntity.setMemberStatus(baseEntity); }
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
     * @param memberStatus The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(MemberStatus memberStatus) {
        assertEntityNotNull(memberStatus);
        delegateInsert(memberStatus);
    }

    @Override
    protected void doCreate(Entity memberStatus) {
        insert((MemberStatus)memberStatus);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberStatus The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final MemberStatus memberStatus) {
        helpUpdateInternally(memberStatus, new InternalUpdateCallback<MemberStatus>() {
            public int callbackDelegateUpdate(MemberStatus entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((MemberStatus)entity);
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        update((MemberStatus)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param memberStatus The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final MemberStatus memberStatus) {
        helpInsertOrUpdateInternally(memberStatus, new InternalInsertOrUpdateCallback<MemberStatus, MemberStatusCB>() {
            public void callbackInsert(MemberStatus entity) { insert(entity); }
            public void callbackUpdate(MemberStatus entity) { update(entity); }
            public MemberStatusCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(MemberStatusCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity memberStatus) {
        insertOrUpdate((MemberStatus)memberStatus);
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdate((MemberStatus)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberStatus The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(MemberStatus memberStatus) {
        helpDeleteInternally(memberStatus, new InternalDeleteCallback<MemberStatus>() {
            public int callbackDelegateDelete(MemberStatus entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity memberStatus) {
        delete((MemberStatus)memberStatus);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberStatusList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<MemberStatus> memberStatusList) {
        assertObjectNotNull("memberStatusList", memberStatusList);
        return delegateInsertList(memberStatusList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberStatusList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdate(List<MemberStatus> memberStatusList) {
        assertObjectNotNull("memberStatusList", memberStatusList);
        return delegateUpdateList(memberStatusList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberStatusList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDelete(List<MemberStatus> memberStatusList) {
        assertObjectNotNull("memberStatusList", memberStatusList);
        return delegateDeleteList(memberStatusList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param memberStatus Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(MemberStatus memberStatus, MemberStatusCB cb) {
        assertObjectNotNull("memberStatus", memberStatus); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(memberStatus);
        filterEntityOfUpdate(memberStatus); assertEntityOfUpdate(memberStatus);
        return invoke(createQueryUpdateEntityCBCommand(memberStatus, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(MemberStatusCB cb) {
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
    protected int delegateSelectCount(MemberStatusCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((MemberStatusCB)cb); }
    protected List<MemberStatus> delegateSelectList(MemberStatusCB cb) {
        return invoke(createSelectListCBCommand(cb, MemberStatus.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((MemberStatusCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(MemberStatus e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(MemberStatus e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateDelete(MemberStatus e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }

    protected int[] delegateInsertList(List<MemberStatus> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<MemberStatus> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateDeleteList(List<MemberStatus> ls) {
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
    protected MemberStatus downcast(Entity entity) {
        return helpDowncastInternally(entity, MemberStatus.class);
    }
}
