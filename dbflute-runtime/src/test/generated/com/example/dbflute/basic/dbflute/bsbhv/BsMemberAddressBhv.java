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
 * The behavior of MEMBER_ADDRESS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_ADDRESS_ID
 * 
 * [column]
 *     MEMBER_ADDRESS_ID, MEMBER_ID, VALID_BEGIN_DATE, VALID_END_DATE, ADDRESS, REGISTER_DATETIME, REGISTER_PROCESS, REGISTER_USER, UPDATE_DATETIME, UPDATE_PROCESS, UPDATE_USER, VERSION_NO
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
 *     MEMBER
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberAddressBhv extends org.dbflute.bhv.AbstractBehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /*df:BehaviorQueryPathBegin*/
    /*df:BehaviorQueryPathEnd*/

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /** @return The name on database of table. (NotNull) */
    public String getTableDbName() { return "MEMBER_ADDRESS"; }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /** @return The instance of DBMeta. (NotNull) */
    public DBMeta getDBMeta() { return MemberAddressDbm.getInstance(); }

    /** @return The instance of DBMeta as my table type. (NotNull) */
    public MemberAddressDbm getMyDBMeta() { return MemberAddressDbm.getInstance(); }

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    public Entity newEntity() { return newMyEntity(); }
    public ConditionBean newConditionBean() { return newMyConditionBean(); }
    public MemberAddress newMyEntity() { return new MemberAddress(); }
    public MemberAddressCB newMyConditionBean() { return new MemberAddressCB(); }

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
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @return The selected count.
     */
    public int selectCount(MemberAddressCB cb) {
        assertCBNotNull(cb);
        return delegateSelectCount(cb);
    }

    // ===================================================================================
    //                                                                       Entity Select
    //                                                                       =============
    /**
     * Select the entity by the condition-bean.
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @return The selected entity. (Nullalble)
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberAddress selectEntity(final MemberAddressCB cb) {
        return helpSelectEntityInternally(cb, new InternalSelectEntityCallback<MemberAddress, MemberAddressCB>() {
            public List<MemberAddress> callbackSelectList(MemberAddressCB cb) { return selectList(cb); } });
    }

    /**
     * Select the entity by the condition-bean with deleted check.
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @return The selected entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberAddress selectEntityWithDeletedCheck(final MemberAddressCB cb) {
        return helpSelectEntityWithDeletedCheckInternally(cb, new InternalSelectEntityWithDeletedCheckCallback<MemberAddress, MemberAddressCB>() {
            public List<MemberAddress> callbackSelectList(MemberAddressCB cb) { return selectList(cb); } });
    }

    /* (non-javadoc)
     * Select the entity with deleted check. {by primary-key value}
     * @param primaryKey The keys of primary.
     * @return The selected entity. (NotNull)
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception com.example.dbflute.basic.dbflute.allcommon.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public MemberAddress selectByPKValueWithDeletedCheck(Integer memberAddressId) {
        MemberAddress entity = new MemberAddress();
        entity.setMemberAddressId(memberAddressId);
        final MemberAddressCB cb = newMyConditionBean();
        cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
        return selectEntityWithDeletedCheck(cb);
    }

    // ===================================================================================
    //                                                                         List Select
    //                                                                         ===========
    /**
     * Select the list as result bean.
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @return The result bean of selected list. (NotNull)
     */
    public ListResultBean<MemberAddress> selectList(MemberAddressCB cb) {
        assertCBNotNull(cb);
        return new ResultBeanBuilder<MemberAddress>(getTableDbName()).buildListResultBean(cb, delegateSelectList(cb));
    }

    // ===================================================================================
    //                                                                         Page Select
    //                                                                         ===========
    /**
     * Select the page as result bean.
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @return The result bean of selected page. (NotNull)
     */
    public PagingResultBean<MemberAddress> selectPage(final MemberAddressCB cb) {
        assertCBNotNull(cb);
        final PagingInvoker<MemberAddress> invoker = new PagingInvoker<MemberAddress>(getTableDbName());
        final PagingHandler<MemberAddress> handler = new PagingHandler<MemberAddress>() {
            public PagingBean getPagingBean() { return cb; }
            public int count() { return selectCount(cb); }
            public List<MemberAddress> paging() { return selectList(cb); }
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
     * memberAddressBhv.scalarSelect(Date.class).max(new ScalarQuery(MemberAddressCB cb) {
     *     cb.specify().columnXxxDatetime(); // the required specification of target column
     *     cb.query().setXxxName_PrefixSearch("S"); // query as you like it
     * });
     * </pre>
     * @param <RESULT> The type of result.
     * @param resultType The type of result. (NotNull)
     * @return The scalar value derived by a function. (Nullable)
     */
    public <RESULT> SLFunction<MemberAddressCB, RESULT> scalarSelect(Class<RESULT> resultType) {
        MemberAddressCB cb = newMyConditionBean();
        cb.xsetupForScalarSelect();
        return new SLFunction<MemberAddressCB, RESULT>(cb, resultType);
    }

    // ===================================================================================
    //                                                                      Various Select
    //                                                                      ==============
    /**
     * Select the list of value-label.
     * @param cb The condition-bean of MemberAddress. (NotNull)
     * @param valueLabelSetupper The setupper of value-label. (NotNull)
     * @return The list of value-label. (NotNull)
     */
    public List<Map<String, Object>> selectValueLabelList(MemberAddressCB cb, ValueLabelSetupper<MemberAddress> valueLabelSetupper) {
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
     * @param memberAddressList The list of memberAddress. (NotNull)
     * @return The list of foreign table. (NotNull)
     */
    public List<Member> pulloutMember(List<MemberAddress> memberAddressList) {
        return helpPulloutInternally(memberAddressList, new InternalPulloutCallback<MemberAddress, Member>() {
            public Member callbackGetForeignEntity(MemberAddress entity) { return entity.getMember(); } });
    }

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    /**
     * Insert the entity.
     * @param memberAddress The entity of insert target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insert(MemberAddress memberAddress) {
        assertEntityNotNull(memberAddress);
        delegateInsert(memberAddress);
    }

    @Override
    protected void doCreate(Entity memberAddress) {
        insert((MemberAddress)memberAddress);
    }

    /**
     * Update the entity modified-only. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberAddress The entity of update target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void update(final MemberAddress memberAddress) {
        helpUpdateInternally(memberAddress, new InternalUpdateCallback<MemberAddress>() {
            public int callbackDelegateUpdate(MemberAddress entity) { return delegateUpdate(entity); } });
    }

    @Override
    protected void doModify(Entity entity) {
        update((MemberAddress)entity);
    }
    
    /**
     * Update the entity non-strictly modified-only. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberAddress The entity of update target. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void updateNonstrict(final MemberAddress memberAddress) {
        helpUpdateNonstrictInternally(memberAddress, new InternalUpdateNonstrictCallback<MemberAddress>() {
            public int callbackDelegateUpdateNonstrict(MemberAddress entity) { return delegateUpdateNonstrict(entity); } });
    }

    @Override
    protected void doModifyNonstrict(Entity entity) {
        updateNonstrict((MemberAddress)entity);
    }

    /**
     * Insert or update the entity modified-only. {ConcurrencyControl(when update)}
     * @param memberAddress The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdate(final MemberAddress memberAddress) {
        helpInsertOrUpdateInternally(memberAddress, new InternalInsertOrUpdateCallback<MemberAddress, MemberAddressCB>() {
            public void callbackInsert(MemberAddress entity) { insert(entity); }
            public void callbackUpdate(MemberAddress entity) { update(entity); }
            public MemberAddressCB callbackNewMyConditionBean() { return newMyConditionBean(); }
            public int callbackSelectCount(MemberAddressCB cb) { return selectCount(cb); }
        });
    }

    @Override
    protected void doCreateOrUpdate(Entity memberAddress) {
        insertOrUpdate((MemberAddress)memberAddress);
    }

    /**
     * Insert or update the entity non-strictly modified-only. {NonConcurrencyControl(when update)}
     * @param memberAddress The entity of insert or update target. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void insertOrUpdateNonstrict(MemberAddress memberAddress) {
        helpInsertOrUpdateInternally(memberAddress, new InternalInsertOrUpdateNonstrictCallback<MemberAddress>() {
            public void callbackInsert(MemberAddress entity) { insert(entity); }
            public void callbackUpdateNonstrict(MemberAddress entity) { updateNonstrict(entity); }
        });
    }

    @Override
    protected void doCreateOrUpdateNonstrict(Entity entity) {
        insertOrUpdateNonstrict((MemberAddress)entity);
    }

    /**
     * Delete the entity. {UpdateCountZeroException, ConcurrencyControl}
     * @param memberAddress The entity of delete target. (NotNull) {PrimaryKeyRequired, ConcurrencyColumnRequired}
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void delete(MemberAddress memberAddress) {
        helpDeleteInternally(memberAddress, new InternalDeleteCallback<MemberAddress>() {
            public int callbackDelegateDelete(MemberAddress entity) { return delegateDelete(entity); } });
    }

    @Override
    protected void doRemove(Entity memberAddress) {
        delete((MemberAddress)memberAddress);
    }

    /**
     * Delete the entity non-strictly. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberAddress Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrict(MemberAddress memberAddress) {
        helpDeleteNonstrictInternally(memberAddress, new InternalDeleteNonstrictCallback<MemberAddress>() {
            public int callbackDelegateDeleteNonstrict(MemberAddress entity) { return delegateDeleteNonstrict(entity); } });
    }

    /**
     * Delete the entity non-strictly ignoring deleted. {UpdateCountZeroException, NonConcurrencyControl}
     * @param memberAddress Entity. (NotNull) {PrimaryKeyRequired}
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void deleteNonstrictIgnoreDeleted(MemberAddress memberAddress) {
        helpDeleteNonstrictIgnoreDeletedInternally(memberAddress, new InternalDeleteNonstrictIgnoreDeletedCallback<MemberAddress>() {
            public int callbackDelegateDeleteNonstrict(MemberAddress entity) { return delegateDeleteNonstrict(entity); } });
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * Batch insert the list. This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberAddressList The list of the entity. (NotNull)
     * @return The array of inserted count.
     */
    public int[] batchInsert(List<MemberAddress> memberAddressList) {
        assertObjectNotNull("memberAddressList", memberAddressList);
        return delegateInsertList(memberAddressList);
    }

    /**
     * Batch update the list. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberAddressList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchUpdate(List<MemberAddress> memberAddressList) {
        assertObjectNotNull("memberAddressList", memberAddressList);
        return delegateUpdateList(memberAddressList);
    }

    /**
     * Batch update the list non-strictly. All columns are update target. {NOT modified only} <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberAddressList The list of the entity. (NotNull)
     * @return The array of updated count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchUpdateNonstrict(List<MemberAddress> memberAddressList) {
        assertObjectNotNull("memberAddressList", memberAddressList);
        return delegateUpdateListNonstrict(memberAddressList);
    }

    /**
     * Batch delete the list. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberAddressList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.BatchEntityAlreadyUpdatedException When the entity has already been updated. This exception extends ${glEntityAlreadyUpdateException}.
     */
    public int[] batchDelete(List<MemberAddress> memberAddressList) {
        assertObjectNotNull("memberAddressList", memberAddressList);
        return delegateDeleteList(memberAddressList);
    }

    /**
     * Batch delete the list non-strictly. <br />
     * This method use 'Batch Update' of java.sql.PreparedStatement.
     * @param memberAddressList The list of the entity. (NotNull)
     * @return The array of deleted count.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    public int[] batchDeleteNonstrict(List<MemberAddress> memberAddressList) {
        assertObjectNotNull("memberAddressList", memberAddressList);
        return delegateDeleteListNonstrict(memberAddressList);
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    /**
     * Query update the several entities. {NoConcurrencyControl}
     * @param memberAddress Entity. (NotNull) {PrimaryKeyNotRequired}
     * @param cb Condition-bean. (NotNull)
     * @return The updated count.
     */
    public int queryUpdate(MemberAddress memberAddress, MemberAddressCB cb) {
        assertObjectNotNull("memberAddress", memberAddress); assertCBNotNull(cb);
        setupCommonColumnOfUpdateIfNeeds(memberAddress);
        filterEntityOfUpdate(memberAddress); assertEntityOfUpdate(memberAddress);
        return invoke(createQueryUpdateEntityCBCommand(memberAddress, cb));
    }

    /**
     * Query delete the several entities. {NoConcurrencyControl}
     * @param cb Condition-bean. (NotNull)
     * @return The deleted count.
     */
    public int queryDelete(MemberAddressCB cb) {
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
    protected int delegateSelectCount(MemberAddressCB cb) {
        return invoke(createSelectCountCBCommand(cb));
    }
    protected int doCallReadCount(ConditionBean cb) { return delegateSelectCount((MemberAddressCB)cb); }
    protected List<MemberAddress> delegateSelectList(MemberAddressCB cb) {
        return invoke(createSelectListCBCommand(cb, MemberAddress.class));
    }
    @SuppressWarnings("unchecked")
    protected List<Entity> doCallReadList(ConditionBean cb) { return (List)delegateSelectList((MemberAddressCB)cb); }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected int delegateInsert(MemberAddress e) {
        if (!processBeforeInsert(e)) { return 1; } return invoke(createInsertEntityCommand(e));
    }
    protected int doCallCreate(Entity entity) {return delegateInsert(downcast(entity)); }
    protected int delegateUpdate(MemberAddress e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateEntityCommand(e));
    }
    protected int doCallModify(Entity entity) { return delegateUpdate(downcast(entity)); }
    protected int delegateUpdateNonstrict(MemberAddress e) {
        if (!processBeforeUpdate(e)) { return 1; } return invoke(createUpdateNonstrictEntityCommand(e));
    }
    protected int delegateDelete(MemberAddress e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteEntityCommand(e));
    }
    protected int doCallRemove(Entity entity) { return delegateDelete(downcast(entity)); }
    protected int delegateDeleteNonstrict(MemberAddress e) {
        if (!processBeforeDelete(e)) { return 1; } return invoke(createDeleteNonstrictEntityCommand(e));
    }

    protected int[] delegateInsertList(List<MemberAddress> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchInsertEntityCommand(helpFilterBeforeInsertInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doCreateList(List<Entity> ls) { return delegateInsertList((List)ls); }
    protected int[] delegateUpdateList(List<MemberAddress> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doModifyList(List<Entity> ls) { return delegateUpdateList((List)ls); }
    protected int[] delegateUpdateListNonstrict(List<MemberAddress> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchUpdateNonstrictEntityCommand(helpFilterBeforeUpdateInternally(ls)));
    }
    protected int[] delegateDeleteList(List<MemberAddress> ls) {
        if (ls.isEmpty()) { return new int[]{}; }
        return invoke(createBatchDeleteEntityCommand(helpFilterBeforeDeleteInternally(ls)));
    }
    @SuppressWarnings("unchecked")
    protected int[] doRemoveList(List<Entity> ls) { return delegateDeleteList((List)ls); }
    protected int[] delegateDeleteListNonstrict(List<MemberAddress> ls) {
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
    protected MemberAddress downcast(Entity entity) {
        return helpDowncastInternally(entity, MemberAddress.class);
    }
}
