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
 * The behavior of MEMBER that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_ID
 * 
 * [column]
 *     MEMBER_ID, MEMBER_NAME, MEMBER_ACCOUNT, MEMBER_STATUS_CODE, MEMBER_FORMALIZED_DATETIME, MEMBER_BIRTHDAY, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     MEMBER_ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER_STATUS, MEMBER_ADDRESS(AsValid), MEMBER_SECURITY(AsOne), MEMBER_WITHDRAWAL(AsOne)
 * 
 * [referrer-table]
 *     MEMBER_ADDRESS, MEMBER_LOGIN, PURCHASE, MEMBER_SECURITY, MEMBER_WITHDRAWAL
 * 
 * [foreign-property]
 *     memberStatus, memberAddressAsValid, memberSecurityAsOne, memberWithdrawalAsOne
 * 
 * [referrer-property]
 *     memberAddressList, memberLoginList, purchaseList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberBhv extends org.seasar.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    public static final String PATH_selectLatestFormalizedDatetime = "selectLatestFormalizedDatetime";
    public static final String PATH_selectMemberName = "selectMemberName";
    public static final String PATH_selectOptionMember = "selectOptionMember";
    public static final String PATH_selectPurchaseMaxPriceMember = "selectPurchaseMaxPriceMember";
    public static final String PATH_selectPurchaseSummaryMember = "selectPurchaseSummaryMember";
    public static final String PATH_selectSimpleMember = "selectSimpleMember";
    public static final String PATH_selectUnpaidSummaryMember = "selectUnpaidSummaryMember";
    public static final String PATH_updateForcedWithdrawal = "updateForcedWithdrawal";
    public static final String PATH_subdirectory_selectSubDirectoryCheck = "subdirectory:selectSubDirectoryCheck";
    public static final String PATH_various_pmbcheck_selectMapLikeSearch = "various:pmbcheck:selectMapLikeSearch";
    public static final String PATH_various_pmbcheck_selectResolvedPackageName = "various:pmbcheck:selectResolvedPackageName";
    public static final String PATH_various_wrongexample_selectBindVariableNotFoundProperty = "various:wrongexample:selectBindVariableNotFoundProperty";
    public static final String PATH_various_wrongexample_selectIfCommentNotBooleanResult = "various:wrongexample:selectIfCommentNotBooleanResult";
    public static final String PATH_various_wrongexample_selectIfCommentWrongExpression = "various:wrongexample:selectIfCommentWrongExpression";
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "MEMBER"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return MemberDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public MemberDbm getMyDBMeta() { return MemberDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public Member newMyEntity() { return new Member(); }
    public MemberCB newMyConditionBean() { return new MemberCB(); }

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
     * @param cb The condition-bean of Member. (NotNull)
     * @return The selected count.
     */
    public int selectCount(MemberCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of Member. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Member selectEntity(final MemberCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<Member, MemberCB>() {
            public List<Member> callbackSelectList(MemberCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of Member. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Member selectEntityWithDeletedCheck(final MemberCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<Member, MemberCB>() {
            public List<Member> callbackSelectList(MemberCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public Member selectByPKValueWithDeletedCheck(Integer memberId) {
        Member entity = new Member();
        entity.setMemberId(memberId);
        final MemberCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of Member. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<Member> selectList(MemberCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<Member>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of Member. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<Member> selectPage(final MemberCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<Member> invoker = new PagingInvoker<Member>(getTableDbName());
        final PagingHandler<Member> handler = new PagingHandler<Member>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<Member> paging() { return selectList(cb); }
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
     * memberBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<MemberCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        MemberCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<MemberCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of Member. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(MemberCB cb, ValueLabelSetupper<Member> valueLabelSetupper) {
        return createValueLabelList(selectList(cb), valueLabelSetupper);
    }

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    /**
     * Load referrer of memberAddressList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setMemberId_InScope(pkList);
     * cb.query().addOrderBy_MemberId_Asc();
     * </pre>
     * @param memberList The entity list of member. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadMemberAddressList(List<Member> memberList, ConditionBeanSetupper<MemberAddressCB> conditionBeanSetupper) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("conditionBeanSetupper<MemberAddressCB>", conditionBeanSetupper);
        if (memberList.isEmpty()) { return; }
        loadMemberAddressList(memberList, new LoadReferrerOption<MemberAddressCB, MemberAddress>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param memberList The entity list of member. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadMemberAddressList(List<Member> memberList, LoadReferrerOption<MemberAddressCB, MemberAddress> loadReferrerOption) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("loadReferrerOption<MemberAddress, MemberAddressCB>", loadReferrerOption);
        if (memberList.isEmpty()) { return; }
        final MemberAddressBhv referrerBhv = xgetBSFLR().select(MemberAddressBhv.class);
        helpLoadReferrerInternally(memberList, loadReferrerOption, new InternalLoadReferrerCallback<Member, Integer, MemberAddressCB, MemberAddress>() {
            public Integer callbackBase_getPrimaryKeyValue(Member entity) { return entity.getMemberId(); }
            public void callbackBase_setReferrerList(Member entity, List<MemberAddress> referrerList) { entity.setMemberAddressList(referrerList); }
            public MemberAddressCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(MemberAddressCB cb, List<Integer> pkList) { cb.query().setMemberId_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(MemberAddressCB cb) { cb.query().addOrderBy_MemberId_Asc(); }
            public List<MemberAddress> callbackReferrer_selectList(MemberAddressCB cb) { return referrerBhv.selectList(cb); }
            public Integer callbackReferrer_getForeignKeyValue(MemberAddress entity) { return entity.getMemberId(); }
            public void callbackReferrer_setForeignEntity(MemberAddress referrerEntity, Member baseEntity) { referrerEntity.setMember(baseEntity); }
        } );
    }
    /**
     * Load referrer of memberLoginList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setMemberId_InScope(pkList);
     * cb.query().addOrderBy_MemberId_Asc();
     * </pre>
     * @param memberList The entity list of member. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadMemberLoginList(List<Member> memberList, ConditionBeanSetupper<MemberLoginCB> conditionBeanSetupper) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("conditionBeanSetupper<MemberLoginCB>", conditionBeanSetupper);
        if (memberList.isEmpty()) { return; }
        loadMemberLoginList(memberList, new LoadReferrerOption<MemberLoginCB, MemberLogin>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param memberList The entity list of member. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadMemberLoginList(List<Member> memberList, LoadReferrerOption<MemberLoginCB, MemberLogin> loadReferrerOption) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("loadReferrerOption<MemberLogin, MemberLoginCB>", loadReferrerOption);
        if (memberList.isEmpty()) { return; }
        final MemberLoginBhv referrerBhv = xgetBSFLR().select(MemberLoginBhv.class);
        helpLoadReferrerInternally(memberList, loadReferrerOption, new InternalLoadReferrerCallback<Member, Integer, MemberLoginCB, MemberLogin>() {
            public Integer callbackBase_getPrimaryKeyValue(Member entity) { return entity.getMemberId(); }
            public void callbackBase_setReferrerList(Member entity, List<MemberLogin> referrerList) { entity.setMemberLoginList(referrerList); }
            public MemberLoginCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(MemberLoginCB cb, List<Integer> pkList) { cb.query().setMemberId_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(MemberLoginCB cb) { cb.query().addOrderBy_MemberId_Asc(); }
            public List<MemberLogin> callbackReferrer_selectList(MemberLoginCB cb) { return referrerBhv.selectList(cb); }
            public Integer callbackReferrer_getForeignKeyValue(MemberLogin entity) { return entity.getMemberId(); }
            public void callbackReferrer_setForeignEntity(MemberLogin referrerEntity, Member baseEntity) { referrerEntity.setMember(baseEntity); }
        } );
    }
    /**
     * Load referrer of purchaseList with the setupper for condition-bean of referrer. <br />
     * About internal policy, the value of primary key(and others too) is treated as case-insensitive. <br />
     * The condition-bean that the setupper provides have settings before you touch it. It is as follows:
     * <pre>
     * cb.query().setMemberId_InScope(pkList);
     * cb.query().addOrderBy_MemberId_Asc();
     * </pre>
     * @param memberList The entity list of member. (NotNull)
     * @param conditionBeanSetupper The instance of referrer condition-bean setupper for registering referrer condition. (NotNull)
     */
    public void loadPurchaseList(List<Member> memberList, ConditionBeanSetupper<PurchaseCB> conditionBeanSetupper) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("conditionBeanSetupper<PurchaseCB>", conditionBeanSetupper);
        if (memberList.isEmpty()) { return; }
        loadPurchaseList(memberList, new LoadReferrerOption<PurchaseCB, Purchase>(conditionBeanSetupper));
    }
    /**
     * {Refer to overload method that has an argument of condition-bean setupper.}
     * @param memberList The entity list of member. (NotNull)
     * @param loadReferrerOption The option of load-referrer. (NotNull)
     */
    public void loadPurchaseList(List<Member> memberList, LoadReferrerOption<PurchaseCB, Purchase> loadReferrerOption) {
        assertObjectNotNull("memberList<Member>", memberList);
        assertObjectNotNull("loadReferrerOption<Purchase, PurchaseCB>", loadReferrerOption);
        if (memberList.isEmpty()) { return; }
        final PurchaseBhv referrerBhv = xgetBSFLR().select(PurchaseBhv.class);
        helpLoadReferrerInternally(memberList, loadReferrerOption, new InternalLoadReferrerCallback<Member, Integer, PurchaseCB, Purchase>() {
            public Integer callbackBase_getPrimaryKeyValue(Member entity) { return entity.getMemberId(); }
            public void callbackBase_setReferrerList(Member entity, List<Purchase> referrerList) { entity.setPurchaseList(referrerList); }
            public PurchaseCB callbackReferrer_newMyConditionBean() { return referrerBhv.newMyConditionBean(); }
            public void callbackReferrer_queryForeignKeyInScope(PurchaseCB cb, List<Integer> pkList) { cb.query().setMemberId_InScope(pkList); }
            public void callbackReferrer_queryAddOrderByForeignKeyAsc(PurchaseCB cb) { cb.query().addOrderBy_MemberId_Asc(); }
            public List<Purchase> callbackReferrer_selectList(PurchaseCB cb) { return referrerBhv.selectList(cb); }
            public Integer callbackReferrer_getForeignKeyValue(Purchase entity) { return entity.getMemberId(); }
            public void callbackReferrer_setForeignEntity(Purchase referrerEntity, Member baseEntity) { referrerEntity.setMember(baseEntity); }
        } );
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    /**
     * Pull out the list of foreign table 'MemberStatus'.
     * @param memberList The list of member. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<MemberStatus> pulloutMemberStatus(List<Member> memberList) {
        return helpPulloutInternally(memberList, new InternalPulloutCallback<Member, MemberStatus>() {
            public MemberStatus callbackGetForeignEntity(Member entity) { return entity.getMemberStatus(); } });
    }
    /**
     * Pull out the list of foreign table 'MemberAddress'.
     * @param memberList The list of member. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<MemberAddress> pulloutMemberAddressAsValid(List<Member> memberList) {
        return helpPulloutInternally(memberList, new InternalPulloutCallback<Member, MemberAddress>() {
            public MemberAddress callbackGetForeignEntity(Member entity) { return entity.getMemberAddressAsValid(); } });
    }
          /**
     * Pull out the list of referrer-as-one table 'MemberSecurity'.
     * @param memberList The list of member. (NotNull)
     * @return The list of referrer-as-one table. (NotNull)
     */
    public List<MemberSecurity> pulloutMemberSecurityAsOne(List<Member> memberList) {
        return helpPulloutInternally(memberList, new InternalPulloutCallback<Member, MemberSecurity>() {
            public MemberSecurity callbackGetForeignEntity(Member entity) { return entity.getMemberSecurityAsOne(); } });
    }
        /**
     * Pull out the list of referrer-as-one table 'MemberWithdrawal'.
     * @param memberList The list of member. (NotNull)
     * @return The list of referrer-as-one table. (NotNull)
     */
    public List<MemberWithdrawal> pulloutMemberWithdrawalAsOne(List<Member> memberList) {
        return helpPulloutInternally(memberList, new InternalPulloutCallback<Member, MemberWithdrawal>() {
            public MemberWithdrawal callbackGetForeignEntity(Member entity) { return entity.getMemberWithdrawalAsOne(); } });
    }
    
    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param member The entity of insert target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(Member member) {
        assertEntityNotNull(member);
        delegateInsert(member);
    }

    @Override
    protected void doCreate(Entity member) {
        insert((Member)member);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param member The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final Member member) {
        helpUpdateInternally(member, new InternalUpdateCallback<Member>() {
            public int callbackDelegateUpdate(Member entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((Member)entity);
    }
    
    /**
     * Update the entity non-strictly modified-only. {UpdateCountZeroException, NonConcurrencyControl}
     * @param member The entity of update target. (NotNull) {PrimaryKeyRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void updateNonstrict(final Member member) {
        helpUpdateNonstrictInternally(member, new InternalUpdateNonstrictCallback<Member>() {
            public int callbackDelegateUpdateNonstrict(Member entity) { return delegateUpdateNonstrict(entity); } });
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        updateNonstrict((Member)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param member The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final Member member) {
        helpInsertOrUpdateInternally(member, new InternalInsertOrUpdateCallback<Member, MemberCB>() {
            public void callbackInsert(Member entity) { insert(entity); }
            public void callbackUpdate(Member entity) { update(entity); }
            public MemberCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(MemberCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity member) {
        insertOrUpdate((Member)member);
    }

    /**
     * Insert or update the entity non-strictly modified-only. {NonConcurrencyControl(when update)}
     * @param member The entity of insert or update target. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdateNonstrict(Member member) {
        helpInsertOrUpdateInternally(member, new InternalInsertOrUpdateNonstrictCallback<Member>() {
            public void callbackInsert(Member entity) { insert(entity); }
            public void callbackUpdateNonstrict(Member entity) { updateNonstrict(entity); }
        });
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdateNonstrict((Member)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param member The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(Member member) {
        helpDeleteInternally(member, new InternalDeleteCallback<Member>() {
            public int callbackDelegateDelete(Member entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity member) {
        delete((Member)member);
    }

    /**
     * Delete the entity non-strictly. {UpdateCountZeroException, NonConcurrencyControl}
     * @param member Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(Member member) {
        helpDeleteNonstrictInternally(member, new InternalDeleteNonstrictCallback<Member>() {
            public int callbackDelegateDeleteNonstrict(Member entity) { return delegateDeleteNonstrict(entity); } });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {UpdateCountZeroException, NonConcurrencyControl}
     * @param member Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(Member member) {
        helpDeleteNonstrictIgnoreDeletedInternally(member, new InternalDeleteNonstrictIgnoreDeletedCallback<Member>() {
            public int callbackDelegateDeleteNonstrict(Member entity) { return delegateDeleteNonstrict(entity); } });
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<Member> memberList) {
        assertObjectNotNull("memberList", memberList);
        return delegateInsertList(memberList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchUpdate(List<Member> memberList) {
        assertObjectNotNull("memberList", memberList);
        return delegateUpdateList(memberList);
    }

    /**
     * Batch update the list non-strictly. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdateNonstrict(List<Member> memberList) {
        assertObjectNotNull("memberList", memberList);
        return delegateUpdateListNonstrict(memberList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchDelete(List<Member> memberList) {
        assertObjectNotNull("memberList", memberList);
        return delegateDeleteList(memberList);
    }

    /**
     * Batch delete the list non-strictly. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDeleteNonstrict(List<Member> memberList) {
        assertObjectNotNull("memberList", memberList);
        return delegateDeleteListNonstrict(memberList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param member Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(Member member, MemberCB cb) {
        assertObjectNotNull("member", member); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(member);
        filterEntityOfUpdate(member); assertEntityOfUpdate(member);
        return invoke(createQueryUpdateEntityCBCommand(member, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(MemberCB cb) {
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
    protected int delegateSelectCount(MemberCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((MemberCB)cb); }
    protected List<Member> delegateSelectList(MemberCB cb) {
        return invoke(createSelectListCBCommand(cb, Member.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((MemberCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(Member e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(Member e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateUpdateNonstrict(Member e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateNonstrictEntityCommand(e));
    }
    protected int delegateDelete(Member e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }
    protected int delegateDeleteNonstrict(Member e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<Member> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<Member> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateUpdateListNonstrict(List<Member> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    protected int[] delegateDeleteList(List<Member> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }
    protected int[] delegateDeleteListNonstrict(List<Member> ls) {
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
    protected Member downcast(Entity entity) {
        return helpDowncastInternally(entity, Member.class);
    }
}
