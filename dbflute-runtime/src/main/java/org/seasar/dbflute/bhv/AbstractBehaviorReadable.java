/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.bhv;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.core.BehaviorCommand;
import org.seasar.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.dbflute.bhv.core.command.AbstractBehaviorCommand;
import org.seasar.dbflute.bhv.core.command.AbstractEntityCommand;
import org.seasar.dbflute.bhv.core.command.InsertEntityCommand;
import org.seasar.dbflute.bhv.core.command.SelectCountCBCommand;
import org.seasar.dbflute.bhv.core.command.SelectCursorCBCommand;
import org.seasar.dbflute.bhv.core.command.SelectListCBCommand;
import org.seasar.dbflute.bhv.core.command.SelectNextValCommand;
import org.seasar.dbflute.bhv.core.command.SelectNextValSubCommand;
import org.seasar.dbflute.bhv.core.command.SelectScalarCBCommand;
import org.seasar.dbflute.cbean.AndQuery;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.OrQuery;
import org.seasar.dbflute.cbean.PagingBean;
import org.seasar.dbflute.cbean.PagingHandler;
import org.seasar.dbflute.cbean.PagingInvoker;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.ResultBeanBuilder;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.chelper.HpFixedConditionQueryResolver;
import org.seasar.dbflute.cbean.chelper.HpSLSExecutor;
import org.seasar.dbflute.cbean.chelper.HpSLSFunction;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.CursorSelectOption;
import org.seasar.dbflute.cbean.sqlclause.clause.SelectClauseType;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByElement;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.RelationInfo;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.FetchingOverSafetySizeException;
import org.seasar.dbflute.exception.IllegalBehaviorStateException;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.exception.PagingOverSafetySizeException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.optional.OptionalEntity;
import org.seasar.dbflute.optional.OptionalObjectExceptionThrower;
import org.seasar.dbflute.optional.RelationOptionalFactory;
import org.seasar.dbflute.outsidesql.executor.OutsideSqlBasicExecutor;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The abstract class of readable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorReadable implements BehaviorReadable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The empty instance for provider of list handling for nested referrer. (wild-card generic for downcast) */
    protected static final NestedReferrerListGateway<?> EMPTY_NREF_LGWAY = new NestedReferrerListGateway<Entity>() {
        public void withNestedReferrer(ReferrerListHandler<Entity> handler) {
            final List<Entity> emptyList = DfCollectionUtil.emptyList();
            handler.handle(emptyList);
        }
    };

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Behavior-selector instance. It's basically referred at loadReferrer. (Required for loadReferrer) */
    protected BehaviorCommandInvoker _behaviorCommandInvoker;

    /** Behavior-selector instance. It's basically referred at loadReferrer. (Required for loadReferrer) */
    protected BehaviorSelector _behaviorSelector;

    // ===================================================================================
    //                                                                          Count Read
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public int readCount(ConditionBean cb) {
        assertCBStateValid(cb);
        return doReadCount(cb);
    }

    protected abstract int doReadCount(ConditionBean cb);

    // ===================================================================================
    //                                                                         Entity Read 
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public Entity readEntity(ConditionBean cb) {
        assertCBStateValid(cb);
        return doReadEntity(cb);
    }

    protected abstract Entity doReadEntity(ConditionBean cb);

    protected <ENTITY> OptionalEntity<ENTITY> createOptionalEntity(ENTITY entity, final Object... searchKey) {
        return new OptionalEntity<ENTITY>(entity, new OptionalObjectExceptionThrower() {
            public void throwNotFoundException() {
                throwSelectEntityAlreadyDeletedException(searchKey);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public Entity readEntityWithDeletedCheck(ConditionBean cb) {
        assertCBStateValid(cb);
        return doReadEntityWithDeletedCheck(cb);
    }

    protected abstract Entity doReadEntityWithDeletedCheck(ConditionBean cb);

    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityInternally(CB cb,
            Class<ENTITY> entityType) {
        assertConditionBeanSelectResource(cb, entityType);
        if (cb.hasSelectAllPossible() && cb.getFetchSize() != 1) { // if no condition for one
            throwSelectEntityConditionNotFoundException(cb);
        }
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOne(cb);
        final List<ENTITY> ls;
        try {
            ls = delegateSelectList(cb, entityType);
        } catch (FetchingOverSafetySizeException e) {
            throwSelectEntityDuplicatedException("{over safetyMaxResultSize '1'}", cb, e);
            return null; // unreachable
        } finally {
            xrestoreSafetyResult(cb, preSafetyMaxResultSize);
        }
        if (ls.isEmpty()) {
            return null;
        }
        assertEntitySelectedAsOne(ls, cb);
        return (ENTITY) ls.get(0);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityWithDeletedCheckInternally(
            CB cb, Class<ENTITY> entityType) {
        final ENTITY entity = helpSelectEntityInternally(cb, entityType);
        assertEntityNotDeleted(entity, cb);
        return entity;
    }

    protected int xcheckSafetyResultAsOne(ConditionBean cb) {
        final int safetyMaxResultSize = cb.getSafetyMaxResultSize();
        cb.checkSafetyResult(1);
        return safetyMaxResultSize;
    }

    protected void xrestoreSafetyResult(ConditionBean cb, int preSafetyMaxResultSize) {
        cb.checkSafetyResult(preSafetyMaxResultSize);
    }

    // -----------------------------------------------------
    //                                       Result Handling
    //                                       ---------------
    /**
     * Assert that the entity is not deleted.
     * @param entity Selected entity. (NullAllowed)
     * @param searchKey Search-key for logging.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     */
    protected void assertEntityNotDeleted(Entity entity, Object searchKey) {
        if (entity == null) {
            throwSelectEntityAlreadyDeletedException(searchKey);
        }
    }

    /**
     * Assert that the entity is not deleted.
     * @param ls Selected list. (NullAllowed)
     * @param searchKey Search-key for logging. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     */
    protected void assertEntityNotDeleted(List<? extends Entity> ls, Object searchKey) {
        if (ls == null || ls.isEmpty()) {
            throwSelectEntityAlreadyDeletedException(searchKey);
        }
    }

    /**
     * Assert that the entity is selected as one.
     * @param ls Selected list. (NotNull)
     * @param searchKey Search-key for logging. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException
     */
    protected void assertEntitySelectedAsOne(List<? extends Entity> ls, Object searchKey) {
        if (ls == null || ls.isEmpty()) {
            throwSelectEntityAlreadyDeletedException(searchKey);
        }
        if (ls.size() > 1) {
            throwSelectEntityDuplicatedException(String.valueOf(ls.size()), searchKey, null);
        }
    }

    protected void throwSelectEntityAlreadyDeletedException(Object searchKey) {
        createBhvExThrower().throwSelectEntityAlreadyDeletedException(searchKey);
    }

    protected void throwSelectEntityDuplicatedException(String resultCountExp, Object searchKey, Throwable cause) {
        createBhvExThrower().throwSelectEntityDuplicatedException(resultCountExp, searchKey, cause);
    }

    protected void throwSelectEntityConditionNotFoundException(ConditionBean cb) {
        createBhvExThrower().throwSelectEntityConditionNotFoundException(cb);
    }

    // ===================================================================================
    //                                                                           List Read
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public <ENTITY extends Entity> ListResultBean<ENTITY> readList(ConditionBean cb) {
        assertCBStateValid(cb);
        @SuppressWarnings("unchecked")
        final ListResultBean<ENTITY> entityList = (ListResultBean<ENTITY>) doReadList(cb);
        return entityList;
    }

    protected abstract ListResultBean<? extends Entity> doReadList(ConditionBean cb);

    protected boolean isSuppressSpecifyDerivedReferrerEntityPropertyCheck() {
        return false;
    }

    protected void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String alias, Class<?> entityType) {
        createCBExThrower().throwSpecifyDerivedReferrerEntityPropertyNotFoundException(alias, entityType);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> ListResultBean<ENTITY> helpSelectListInternally(CB cb,
            Class<ENTITY> entityType) {
        assertConditionBeanSelectResource(cb, entityType);
        try {
            final List<ENTITY> selectedList = delegateSelectList(cb, entityType);
            return createListResultBean(cb, selectedList);
        } catch (FetchingOverSafetySizeException e) {
            createBhvExThrower().throwDangerousResultSizeException(cb, e);
            return null; // unreachable
        }
    }

    protected <ENTITY extends Entity> ListResultBean<ENTITY> createListResultBean(ConditionBean cb,
            List<ENTITY> selectedList) {
        return new ResultBeanBuilder<ENTITY>(getTableDbName()).buildListResultBean(cb, selectedList);
    }

    // ===================================================================================
    //                                                                           Page Read
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public <ENTITY extends Entity> PagingResultBean<ENTITY> readPage(final ConditionBean cb) {
        assertCBStateValid(cb);
        @SuppressWarnings("unchecked")
        final PagingResultBean<ENTITY> entityList = (PagingResultBean<ENTITY>) doReadPage(cb);
        return entityList;
    }

    protected abstract PagingResultBean<? extends Entity> doReadPage(ConditionBean cb);

    protected <ENTITY extends Entity, CB extends ConditionBean> PagingResultBean<ENTITY> helpSelectPageInternally(
            CB cb, Class<ENTITY> entityType) {
        assertConditionBeanSelectResource(cb, entityType);
        try {
            final PagingHandler<ENTITY> handler = createPagingHandler(cb, entityType);
            final PagingInvoker<ENTITY> invoker = createPagingInvoker(cb);
            return invoker.invokePaging(handler);
        } catch (PagingOverSafetySizeException e) {
            createBhvExThrower().throwDangerousResultSizeException(cb, e);
            return null; // unreachable
        }
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> PagingHandler<ENTITY> createPagingHandler(final CB cb,
            final Class<ENTITY> entityType) {
        return new PagingHandler<ENTITY>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                try {
                    cb.getSqlClause().makePagingAdjustmentEffective();
                    return delegateSelectCountPlainly(cb);
                } finally {
                    cb.getSqlClause().ignorePagingAdjustment();
                }
            }

            public List<ENTITY> paging() {
                try {
                    cb.getSqlClause().makePagingAdjustmentEffective();
                    return delegateSelectList(cb, entityType);
                } finally {
                    cb.getSqlClause().ignorePagingAdjustment();
                }
            }
        };
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> PagingInvoker<ENTITY> createPagingInvoker(CB cb) {
        return cb.createPagingInvoker(getTableDbName());
    }

    // ===================================================================================
    //                                                                         Cursor Read
    //                                                                         ===========
    protected <ENTITY extends Entity, CB extends ConditionBean> void helpSelectCursorInternally(CB cb,
            EntityRowHandler<ENTITY> handler, Class<ENTITY> entityType) {
        assertObjectNotNull("entityRowHandler", handler);
        assertConditionBeanSelectResource(cb, entityType);
        final CursorSelectOption option = cb.getCursorSelectOption();
        if (option != null && option.isByPaging()) {
            helpSelectCursorHandlingByPaging(cb, handler, entityType, option);
        } else { // basically here
            delegateSelectCursor(cb, handler, entityType);
        }
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> void helpSelectCursorHandlingByPaging(CB cb,
            EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType, CursorSelectOption option) {
        helpSelectCursorCheckingByPagingAllowed(cb, option);
        helpSelectCursorCheckingOrderByPK(cb, option);
        final int pageSize = option.getPageSize();
        int pageNumber = 1;
        while (true) {
            cb.paging(pageSize, pageNumber);
            List<ENTITY> pageList = delegateSelectList(cb, entityType);
            for (ENTITY entity : pageList) {
                entityRowHandler.handle(entity);
            }
            if (pageList.size() < pageSize) { // means last page
                break;
            }
            ++pageNumber;
        }
    }

    protected <CB extends ConditionBean> void helpSelectCursorCheckingByPagingAllowed(CB cb, CursorSelectOption option) {
        if (!cb.getSqlClause().isCursorSelectByPagingAllowed()) {
            String msg = "The cursor select by paging is not allowed at the DBMS.";
            throw new IllegalConditionBeanOperationException(msg);
        }
    }

    protected <CB extends ConditionBean> void helpSelectCursorCheckingOrderByPK(CB cb, CursorSelectOption option) {
        if (option.isOrderByPK()) {
            final OrderByClause orderByClause = cb.getOrderByComponent();
            final OrderByElement orderByFirstElement = orderByClause.getOrderByFirstElement();
            if (orderByFirstElement == null || !orderByFirstElement.getColumnInfo().isPrimary()) {
                String msg = "The cursor select by paging needs order by primary key: " + cb.getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
        }
    }

    // ===================================================================================
    //                                                                         Scalar Read
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public <RESULT> HpSLSFunction<ConditionBean, RESULT> readScalar(Class<RESULT> resultType) {
        @SuppressWarnings("unchecked")
        final HpSLSFunction<ConditionBean, RESULT> func = (HpSLSFunction<ConditionBean, RESULT>) doReadScalar(resultType);
        return func;
    }

    protected <CB extends ConditionBean, RESULT> HpSLSExecutor<CB, RESULT> createHpSLSExecutor() {
        return new HpSLSExecutor<CB, RESULT>() {
            public RESULT execute(CB lcb, Class<RESULT> ltp, SelectClauseType sctp) {
                return invoke(createSelectScalarCBCommand(lcb, ltp, sctp));
            }
        };
    }

    protected <CB extends ConditionBean, RESULT> HpSLSFunction<CB, RESULT> createSLSFunction(CB cb, Class<RESULT> tp,
            HpSLSExecutor<CB, RESULT> exec) {
        return new HpSLSFunction<CB, RESULT>(cb, tp, exec);
    }

    protected abstract <RESULT> HpSLSFunction<? extends ConditionBean, RESULT> doReadScalar(Class<RESULT> resultType);

    // ===================================================================================
    //                                                                          OutsideSql
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public <BEHAVIOR extends BehaviorReadable> OutsideSqlBasicExecutor<BEHAVIOR> readyOutsideSql() {
        return doOutsideSql();
    }

    /**
     * Prepare an outside-SQL execution by returning an instance of the executor for outside-SQL. <br />
     * It's an extension point for your adding original customization to outside-SQL executions.
     * @param <BEHAVIOR> The type of behavior.
     * @return The basic executor for outside-SQL. (NotNull) 
     */
    protected <BEHAVIOR extends BehaviorReadable> OutsideSqlBasicExecutor<BEHAVIOR> doOutsideSql() {
        assertBehaviorCommandInvoker("outsideSql");
        return _behaviorCommandInvoker.createOutsideSqlBasicExecutor(getTableDbName());
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * {@inheritDoc}
     */
    public Number readNextVal() {
        return doReadNextVal();
    }

    protected abstract Number doReadNextVal();

    // ===================================================================================
    //                                                                       Load Referrer
    //                                                                       =============
    // -----------------------------------------------------
    //                                       New Entry Point
    //                                       ---------------
    /**
     * Help load referrer internally. (new entry point) <br />
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param referrerProperty The property name of referrer. (NotNull) 
     * @return The callback to load nested referrer. (NotNull)
     */
    protected <LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    NestedReferrerListGateway<REFERRER_ENTITY> helpLoadReferrerInternally(List<LOCAL_ENTITY> localEntityList,
            LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption, String referrerProperty) {
        return doHelpLoadReferrerInternally(localEntityList, loadReferrerOption, referrerProperty);
    }

    protected <LOCAL_ENTITY extends Entity, KEY, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    NestedReferrerListGateway<REFERRER_ENTITY> doHelpLoadReferrerInternally(List<LOCAL_ENTITY> localEntityList,
            LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption, final String referrerProperty) {
        final DBMeta dbmeta = getDBMeta();
        final ReferrerInfo referrerInfo = dbmeta.findReferrerInfo(referrerProperty);
        final BehaviorReadable referrerBhv = xfindReferrerBehavior(referrerInfo);
        final Set<ColumnInfo> pkColSet = referrerInfo.getLocalReferrerColumnInfoMap().keySet(); // might be unique key
        final Map<ColumnInfo, ColumnInfo> mappingColMap = referrerInfo.getReferrerLocalColumnInfoMap(); // key is referrer's
        final InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY> callback;
        if (pkColSet.size() == 1) { // simple key
            final ColumnInfo pkCol = pkColSet.iterator().next();
            final ColumnInfo fkCol = mappingColMap.keySet().iterator().next();
            callback = xcreateLoadReferrerCallback(referrerProperty, dbmeta, referrerInfo, referrerBhv, pkCol, fkCol);
        } else { // compound key
            final Set<ColumnInfo> fkColSet = mappingColMap.keySet();
            callback = xcreateLoadReferrerCallback(referrerProperty, dbmeta, referrerInfo, referrerBhv, pkColSet,
                    fkColSet, mappingColMap);
        }
        return helpLoadReferrerInternally(localEntityList, loadReferrerOption, callback);
    }

    protected BehaviorReadable xfindReferrerBehavior(final ReferrerInfo referrerInfo) {
        final String behaviorName = referrerInfo.getReferrerDBMeta().getBehaviorTypeName();
        @SuppressWarnings("unchecked")
        final Class<BehaviorReadable> behaviorType = (Class<BehaviorReadable>) DfReflectionUtil.forName(behaviorName);
        return xgetBSFLR().select(behaviorType);
    }

    // -----------------------------------------------------
    //                                      Loading Callback
    //                                      ----------------
    protected <LOCAL_ENTITY extends Entity, KEY, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY> // return
    xcreateLoadReferrerCallback(final String referrerProperty, final DBMeta dbmeta, final ReferrerInfo referrerInfo,
            final BehaviorReadable referrerBhv, final ColumnInfo pkCol, final ColumnInfo fkCol) {
        return new InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY>() { // for simple key
            public KEY getPKVal(LOCAL_ENTITY entity) {
                return pkCol.read(entity); // (basically) PK cannot be optional because of not-null
            }

            public void setRfLs(LOCAL_ENTITY entity, List<REFERRER_ENTITY> referrerList) {
                referrerInfo.write(entity, referrerList);
            }

            @SuppressWarnings("unchecked")
            public REFERRER_CB newMyCB() {
                return (REFERRER_CB) referrerBhv.newConditionBean();
            }

            public void qyFKIn(REFERRER_CB cb, List<KEY> pkList) {
                final String conditionKey = ConditionKey.CK_IN_SCOPE.getConditionKey();
                cb.localCQ().invokeQuery(fkCol.getColumnDbName(), conditionKey, pkList);
            }

            public void qyOdFKAsc(REFERRER_CB cb) {
                cb.localCQ().invokeOrderBy(fkCol.getColumnDbName(), true);
            }

            public void spFKCol(REFERRER_CB cb) {
                cb.localSp().xspecifyColumn(fkCol.getColumnDbName());
            }

            public List<REFERRER_ENTITY> selRfLs(REFERRER_CB cb) {
                return referrerBhv.readList(cb);
            }

            public KEY getFKVal(REFERRER_ENTITY entity) {
                final Class<?> fkType = fkCol.getObjectNativeType();
                final Class<?> pkType = pkCol.getObjectNativeType();
                final Object fkValue = fkCol.read(entity);
                return xconvertFK2PKImplicitly(referrerProperty, fkType, pkType, fkValue);
            }

            public void setlcEt(REFERRER_ENTITY referrerEntity, LOCAL_ENTITY localEntity) {
                final RelationInfo reverseInfo = referrerInfo.getReverseRelation();
                final Object written = xconvertToRelationOptionalEntityIfNeeds(localEntity, reverseInfo);
                reverseInfo.write(referrerEntity, written);
            }

            public String getRfPrNm() {
                return referrerProperty;
            }
        };
    }

    protected <LOCAL_ENTITY extends Entity, KEY, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY> // return
    xcreateLoadReferrerCallback(final String referrerProperty, final DBMeta dbmeta, final ReferrerInfo referrerInfo,
            final BehaviorReadable referrerBhv, final Set<ColumnInfo> pkColSet, final Set<ColumnInfo> fkColSet,
            final Map<ColumnInfo, ColumnInfo> mappingColMap) {
        return new InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY>() { // for compound key
            @SuppressWarnings("unchecked")
            public KEY getPKVal(LOCAL_ENTITY entity) {
                final Map<String, Object> keyMap = xnewLoadReferrerCompoundKeyMap();
                for (ColumnInfo pkCol : pkColSet) {
                    keyMap.put(pkCol.getColumnDbName(), pkCol.read(entity)); // key is DB name
                }
                return (KEY) keyMap;
                // cannot use because it might be unique key
                //return (KEY) dbmeta.extractPrimaryKeyMap(entity);
            }

            public void setRfLs(LOCAL_ENTITY entity, List<REFERRER_ENTITY> referrerList) {
                referrerInfo.write(entity, referrerList);
            }

            @SuppressWarnings("unchecked")
            public REFERRER_CB newMyCB() {
                return (REFERRER_CB) referrerBhv.newConditionBean();
            }

            public void qyFKIn(REFERRER_CB cb, final List<KEY> pkList) {
                // compound key doesn't use InScope so OrScopeQuery 
                cb.invokeOrScopeQuery(new OrQuery<ConditionBean>() {
                    public void query(ConditionBean orCB) {
                        for (final KEY pkKey : pkList) {
                            @SuppressWarnings("unchecked")
                            final Map<String, Object> pkMap = (Map<String, Object>) pkKey;
                            orCB.invokeOrScopeQueryAndPart(new AndQuery<ConditionBean>() {
                                public void query(ConditionBean andCB) {
                                    for (ColumnInfo fkCol : fkColSet) {
                                        final ColumnInfo pkCol = mappingColMap.get(fkCol);
                                        final Object pkValue = pkMap.get(pkCol.getColumnDbName()); // key is DB name
                                        andCB.localCQ().invokeQueryEqual(fkCol.getColumnDbName(), pkValue);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            public void qyOdFKAsc(REFERRER_CB cb) {
                for (ColumnInfo fkCol : fkColSet) {
                    cb.localCQ().invokeOrderBy(fkCol.getColumnDbName(), true);
                }
            }

            public void spFKCol(REFERRER_CB cb) {
                for (ColumnInfo fkCol : fkColSet) {
                    cb.localSp().xspecifyColumn(fkCol.getColumnDbName());
                }
            }

            public List<REFERRER_ENTITY> selRfLs(REFERRER_CB cb) {
                return referrerBhv.readList(cb);
            }

            @SuppressWarnings("unchecked")
            public KEY getFKVal(REFERRER_ENTITY entity) {
                final Map<String, Object> fkMap = xnewLoadReferrerCompoundKeyMap();
                for (ColumnInfo fkCol : fkColSet) {
                    final Object fkValue = fkCol.read(entity);
                    final ColumnInfo pkCol = mappingColMap.get(fkCol);
                    final String mapKey = pkCol.getColumnDbName(); // key is DB name
                    final Class<?> fkType = fkCol.getObjectNativeType();
                    final Class<?> pkType = pkCol.getObjectNativeType();
                    final Object realValue;
                    if (fkType.equals(pkType)) { // basically true
                        realValue = fkValue;
                    } else { // different type (needs implicit conversion)
                        realValue = xconvertFK2PKImplicitly(referrerProperty, fkType, pkType, fkValue);
                    }
                    fkMap.put(mapKey, realValue);
                }
                return (KEY) fkMap;
            }

            public void setlcEt(REFERRER_ENTITY referrerEntity, LOCAL_ENTITY localEntity) {
                final RelationInfo reverseInfo = referrerInfo.getReverseRelation(); // always exists
                final Object written = xconvertToRelationOptionalEntityIfNeeds(localEntity, reverseInfo);
                reverseInfo.write(referrerEntity, written);
            }

            public String getRfPrNm() {
                return referrerProperty;
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected <KEY> KEY xconvertFK2PKImplicitly(String referrerProperty, Class<?> fkType, Class<?> pkType,
            Object fkValue) {
        // DB-able entity does not support optional for property
        // only supported in immutable entity
        final KEY realValue;
        if (fkType.equals(pkType)) { // basically true
            realValue = (KEY) fkValue;
        } else { // different type (needs implicit conversion)
            if (String.class.equals(pkType)) { // e.g. Integer to String
                realValue = (KEY) fkValue.toString();
            } else if (Number.class.isAssignableFrom(pkType)) { // e.g. Long to Integer
                realValue = (KEY) DfTypeUtil.toNumber(fkValue, pkType);
            } else if (Date.class.isAssignableFrom(fkType)) {
                if (Date.class.equals(pkType)) { // e.g. Timestamp to Date
                    realValue = (KEY) new Date(((Date) fkValue).getTime());
                } else if (Timestamp.class.equals(pkType)) { // e.g. Date to Timestamp
                    realValue = (KEY) new Timestamp(((Date) fkValue).getTime());
                } else { // cannot conversion
                    realValue = (KEY) fkValue;
                }
            } else { // cannot conversion
                realValue = (KEY) fkValue;
            }
        }
        return realValue;
    }

    protected Object xconvertToRelationOptionalEntityIfNeeds(Object localEntity, RelationInfo reverseInfo) {
        final Object writtenObj;
        if (isRelationOptional(reverseInfo.getPropertyAccessType())) {
            writtenObj = toRelationOptional(reverseInfo.getRelationPropertyName(), localEntity);
        } else {
            writtenObj = localEntity;
        }
        return writtenObj;
    }

    // -----------------------------------------------------
    //                                   Ancient Entry Point
    //                                   -------------------
    /**
     * Help load referrer internally. (ancient entry point) <br />
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <KEY> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param callback The internal callback of loadReferrer. (NotNull) 
     * @return The callback to load nested referrer. (NotNull)
     */
    protected <LOCAL_ENTITY extends Entity, KEY, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    NestedReferrerListGateway<REFERRER_ENTITY> helpLoadReferrerInternally(List<LOCAL_ENTITY> localEntityList,
            LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY> callback) {
        return doHelpLoadReferrerInternally(localEntityList, loadReferrerOption, callback);
    }

    protected <LOCAL_ENTITY extends Entity, KEY, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> // generic
    NestedReferrerListGateway<REFERRER_ENTITY> doHelpLoadReferrerInternally(List<LOCAL_ENTITY> localEntityList,
            LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            final InternalLoadReferrerCallback<LOCAL_ENTITY, KEY, REFERRER_CB, REFERRER_ENTITY> callback) {
        // - - - - - - - - - -
        // Assert precondition
        // - - - - - - - - - -
        assertBehaviorSelectorNotNull("loadReferrer");
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
        if (localEntityList.isEmpty()) {
            @SuppressWarnings("unchecked")
            final NestedReferrerListGateway<REFERRER_ENTITY> empty = (NestedReferrerListGateway<REFERRER_ENTITY>) EMPTY_NREF_LGWAY;
            return empty;
        }

        // - - - - - - - - - - - - - -
        // Prepare temporary container
        // - - - - - - - - - - - - - -
        final Map<KEY, LOCAL_ENTITY> pkLocalEntityMap = new LinkedHashMap<KEY, LOCAL_ENTITY>();
        final List<KEY> pkList = new ArrayList<KEY>();
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final KEY primaryKeyValue = callback.getPKVal(localEntity);
            if (primaryKeyValue == null) {
                String msg = "PK value of local entity should not be null: " + localEntity;
                throw new IllegalArgumentException(msg);
            }
            pkList.add(primaryKeyValue);
            pkLocalEntityMap.put(toLoadReferrerMappingKey(primaryKeyValue), localEntity);
        }

        // - - - - - - - - - - - - - - - -
        // Prepare referrer condition bean
        // - - - - - - - - - - - - - - - -
        final REFERRER_CB cb;
        if (loadReferrerOption.getReferrerConditionBean() != null) {
            cb = loadReferrerOption.getReferrerConditionBean();
        } else {
            cb = callback.newMyCB();
        }

        // - - - - - - - - - - - - - -
        // Select the list of referrer
        // - - - - - - - - - - - - - -
        callback.qyFKIn(cb, pkList);
        final String referrerPropertyName = callback.getRfPrNm();
        final String fixedCondition = xbuildReferrerCorrelatedFixedCondition(cb, referrerPropertyName);
        final String basePointAliasName = cb.getSqlClause().getBasePointAliasName();
        final boolean hasFixedCondition = fixedCondition != null && fixedCondition.trim().length() > 0;
        if (hasFixedCondition) {
            cb.getSqlClause().registerWhereClause(fixedCondition, basePointAliasName);
        }
        cb.xregisterUnionQuerySynchronizer(new UnionQuery<ConditionBean>() {
            public void query(ConditionBean unionCB) {
                @SuppressWarnings("unchecked")
                REFERRER_CB referrerUnionCB = (REFERRER_CB) unionCB;
                // for when application uses union query in condition-bean set-upper.
                callback.qyFKIn(referrerUnionCB, pkList);
                if (hasFixedCondition) {
                    referrerUnionCB.getSqlClause().registerWhereClause(fixedCondition, basePointAliasName);
                }
            }
        });
        if (pkList.size() > 1) {
            callback.qyOdFKAsc(cb);
            cb.getOrderByComponent().exchangeFirstOrderByElementForLastOne();
        }
        loadReferrerOption.delegateConditionBeanSettingUp(cb);
        if (cb.getSqlClause().hasSpecifiedSelectColumn(basePointAliasName)) {
            callback.spFKCol(cb); // specify required columns for relation
        }
        final List<REFERRER_ENTITY> referrerList = callback.selRfLs(cb);
        loadReferrerOption.delegateEntitySettingUp(referrerList);

        // - - - - - - - - - - - - - - - - - - - - - - - -
        // Create the map of {primary key / referrer list}
        // - - - - - - - - - - - - - - - - - - - - - - - -
        final Map<KEY, List<REFERRER_ENTITY>> pkReferrerListMap = new LinkedHashMap<KEY, List<REFERRER_ENTITY>>();
        for (REFERRER_ENTITY referrerEntity : referrerList) {
            final KEY referrerListKey;
            {
                final KEY foreignKeyValue = callback.getFKVal(referrerEntity);
                referrerListKey = toLoadReferrerMappingKey(foreignKeyValue);
            }
            if (!pkReferrerListMap.containsKey(referrerListKey)) {
                pkReferrerListMap.put(referrerListKey, new ArrayList<REFERRER_ENTITY>());
            }
            (pkReferrerListMap.get(referrerListKey)).add(referrerEntity);

            // for Reverse Reference.
            final LOCAL_ENTITY localEntity = pkLocalEntityMap.get(referrerListKey);
            callback.setlcEt(referrerEntity, localEntity);
        }

        // - - - - - - - - - - - - - - - - - -
        // Relate referrer list to base entity
        // - - - - - - - - - - - - - - - - - -
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final KEY referrerListKey;
            {
                final KEY primaryKey = callback.getPKVal(localEntity);
                referrerListKey = toLoadReferrerMappingKey(primaryKey);
            }
            if (pkReferrerListMap.containsKey(referrerListKey)) {
                callback.setRfLs(localEntity, pkReferrerListMap.get(referrerListKey));
            } else {
                callback.setRfLs(localEntity, new ArrayList<REFERRER_ENTITY>());
            }
        }

        // - - - - - - - - - - - - - - - - - - - -
        // Return callback to load nested referrer
        // - - - - - - - - - - - - - - - - - - - -
        return new NestedReferrerListGateway<REFERRER_ENTITY>() {
            public void withNestedReferrer(ReferrerListHandler<REFERRER_ENTITY> handler) {
                handler.handle(Collections.unmodifiableList(referrerList));
            }
        };
    }

    protected String xbuildReferrerCorrelatedFixedCondition(ConditionBean cb, String referrerPropertyName) {
        if (referrerPropertyName == null) {
            return null;
        }
        final DBMeta localDBMeta = getDBMeta();
        if (!localDBMeta.hasReferrer(referrerPropertyName)) { // one-to-one referrer
            return null;
        }
        final ReferrerInfo referrerInfo = localDBMeta.findReferrerInfo(referrerPropertyName);
        return xdoBuildReferrerCorrelatedFixedCondition(cb, referrerInfo);
    }

    protected String xdoBuildReferrerCorrelatedFixedCondition(ConditionBean cb, ReferrerInfo referrerInfo) {
        final RelationInfo reverseRelation = referrerInfo.getReverseRelation();
        if (reverseRelation == null) {
            return null;
        }
        if (!(reverseRelation instanceof ForeignInfo)) {
            String msg = "The reverse relation (referrer's reverse) should be foreign info: " + referrerInfo;
            throw new IllegalStateException(msg);
        }
        final ForeignInfo foreignInfo = (ForeignInfo) reverseRelation;
        final String fixedCondition = foreignInfo.getFixedCondition();
        if (fixedCondition == null || fixedCondition.trim().length() == 0) {
            return null;
        }
        final String localAliasMark = HpFixedConditionQueryResolver.LOCAL_ALIAS_MARK;
        final String basePointAliasName = cb.getSqlClause().getBasePointAliasName();
        return Srl.replace(fixedCondition, localAliasMark, basePointAliasName);
    }

    /**
     * Convert the primary key to mapping key for load-referrer. <br />
     * This default implementation is to-lower if string type.
     * @param <PK> The type of primary key.
     * @param value The value of primary key. (NotNull)
     * @return The value of primary key. (NotNull)
     */
    @SuppressWarnings("unchecked")
    protected <PK> PK toLoadReferrerMappingKey(PK value) {
        if (value instanceof String) { // simple key
            return (PK) toLowerCaseIfString(value);
        }
        if (value instanceof Map<?, ?>) { // compound key
            final Map<String, Object> pkMap = (Map<String, Object>) value;
            final Map<String, Object> filteredMap = xnewLoadReferrerCompoundKeyMap();
            for (Map.Entry<String, Object> entry : pkMap.entrySet()) {
                final String key = entry.getKey();
                final Object element = entry.getValue();
                if (element instanceof String) {
                    filteredMap.put(key, toLowerCaseIfString(element));
                } else {
                    filteredMap.put(key, element);
                }
            }
            return (PK) filteredMap;
        }
        return value;
    }

    protected Map<String, Object> xnewLoadReferrerCompoundKeyMap() {
        return new LinkedHashMap<String, Object>();
    }

    /**
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer conditionBean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     */
    protected static interface InternalLoadReferrerCallback<LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> {
        // for Base
        PK getPKVal(LOCAL_ENTITY entity); // getPrimaryKeyValue()

        void setRfLs(LOCAL_ENTITY entity, List<REFERRER_ENTITY> referrerList); // setReferrerList()

        // for Referrer
        REFERRER_CB newMyCB(); // newMyConditionBean()

        void qyFKIn(REFERRER_CB cb, List<PK> pkList); // queryForeignKeyInScope()

        void qyOdFKAsc(REFERRER_CB cb); // queryAddOrderByForeignKeyAsc() 

        void spFKCol(REFERRER_CB cb); // specifyForeignKeyColumn()

        List<REFERRER_ENTITY> selRfLs(REFERRER_CB cb); // selectReferrerList() 

        PK getFKVal(REFERRER_ENTITY entity); // getForeignKeyValue()

        void setlcEt(REFERRER_ENTITY referrerEntity, LOCAL_ENTITY localEntity); // setLocalEntity()

        String getRfPrNm(); // getReferrerPropertyName()
    }

    protected <ELEMENT extends Entity> List<ELEMENT> xnewLRAryLs(ELEMENT entity) {
        final List<ELEMENT> ls = new ArrayList<ELEMENT>(1);
        ls.add(entity);
        return ls;
    }

    // assertLoadReferrerArgument() as Internal
    protected void xassLRArg(List<? extends Entity> entityList, ReferrerLoaderHandler<?> handler) {
        assertObjectNotNull("LoadReferrer's entityList", entityList);
        assertObjectNotNull("LoadReferrer's handler", handler);
    }

    protected void xassLRArg(Entity entity, ReferrerLoaderHandler<?> handler) {
        assertObjectNotNull("LoadReferrer's entity", entity);
        assertObjectNotNull("LoadReferrer's handler", handler);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            ReferrerConditionSetupper<? extends ConditionBean> setupper) {
        assertObjectNotNull("LoadReferrer's entityList", entityList);
        assertObjectNotNull("LoadReferrer's setupper", setupper);
    }

    protected void xassLRArg(Entity entity, ReferrerConditionSetupper<? extends ConditionBean> setupper) {
        assertObjectNotNull("LoadReferrer's entity", entity);
        assertObjectNotNull("LoadReferrer's setupper", setupper);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("LoadReferrer's entityList", entityList);
        assertObjectNotNull("LoadReferrer's loadReferrerOption", loadReferrerOption);
    }

    protected void xassLRArg(Entity entity,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("LoadReferrer's entity", entity);
        assertObjectNotNull("LoadReferrer's loadReferrerOption", loadReferrerOption);
    }

    protected BehaviorSelector xgetBSFLR() { // getBehaviorSelectorForLoadReferrer() as Internal
        assertBehaviorSelectorNotNull("loadReferrer");
        return getBehaviorSelector();
    }

    private void assertBehaviorSelectorNotNull(String methodName) {
        if (_behaviorSelector != null) {
            return;
        }
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the selector of behavior in the behavior!");
        br.addItem("Advice");
        br.addElement("Please confirm the definition of the selector at your component configuration of DBFlute.");
        br.addElement("It is precondition that '" + methodName + "()' needs the selector instance.");
        br.addItem("Behavior");
        br.addElement("Behavior for " + getTableDbName());
        br.addItem("Attribute");
        br.addElement("behaviorCommandInvoker   : " + _behaviorCommandInvoker);
        br.addElement("behaviorSelector         : " + _behaviorSelector);
        final String msg = br.buildExceptionMessage();
        throw new IllegalBehaviorStateException(msg);
    }

    protected <ELEMENT> List<ELEMENT> xnewLRLs(ELEMENT element) { // newLoadReferrerList() as Internal
        List<ELEMENT> ls = new ArrayList<ELEMENT>(1);
        ls.add(element);
        return ls;
    }

    // ===================================================================================
    //                                                                   Pull out Relation
    //                                                                   =================
    protected <LOCAL_ENTITY extends Entity, FOREIGN_ENTITY extends Entity> List<FOREIGN_ENTITY> helpPulloutInternally(
            List<LOCAL_ENTITY> localEntityList, String foreignPropertyName) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("foreignPropertyName", foreignPropertyName);
        final DBMeta dbmeta = getDBMeta();
        final ForeignInfo foreignInfo = dbmeta.findForeignInfo(foreignPropertyName);
        final RelationInfo reverseInfo = foreignInfo.getReverseRelation();
        final boolean existsReferrer = reverseInfo != null;
        final RelationOptionalFactory optionalFactory = xgetROpFactory();
        final Set<FOREIGN_ENTITY> foreignSet = new LinkedHashSet<FOREIGN_ENTITY>();
        final Map<FOREIGN_ENTITY, List<LOCAL_ENTITY>> foreignReferrerMap = new LinkedHashMap<FOREIGN_ENTITY, List<LOCAL_ENTITY>>();
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final FOREIGN_ENTITY foreignEntity = xextractPulloutForeignEntity(foreignInfo, reverseInfo,
                    optionalFactory, localEntity);
            if (foreignEntity == null) {
                continue;
            }
            if (!foreignSet.contains(foreignEntity)) {
                foreignSet.add(foreignEntity);
            }
            if (existsReferrer) {
                if (!foreignReferrerMap.containsKey(foreignEntity)) {
                    foreignReferrerMap.put(foreignEntity, new ArrayList<LOCAL_ENTITY>());
                }
                foreignReferrerMap.get(foreignEntity).add(localEntity);
            }
        }
        if (existsReferrer) {
            for (Entry<FOREIGN_ENTITY, List<LOCAL_ENTITY>> entry : foreignReferrerMap.entrySet()) {
                final FOREIGN_ENTITY foreignEntity = entry.getKey();
                final List<LOCAL_ENTITY> mappedLocalList = entry.getValue();
                final Object writtenObj = xextractPulloutReverseWrittenObject(foreignInfo, reverseInfo,
                        optionalFactory, mappedLocalList);
                reverseInfo.write(foreignEntity, writtenObj);
            }
        }
        return new ArrayList<FOREIGN_ENTITY>(foreignSet);
    }

    @SuppressWarnings("unchecked")
    protected <LOCAL_ENTITY extends Entity, FOREIGN_ENTITY extends Entity> FOREIGN_ENTITY xextractPulloutForeignEntity(
            ForeignInfo foreignInfo, RelationInfo reverseInfo, RelationOptionalFactory optionalFactory,
            LOCAL_ENTITY localEntity) {
        final Object mightBeOptional = foreignInfo.read(localEntity); // non-reflection
        final FOREIGN_ENTITY foreignEntity;
        if (optionalFactory.isOptional(mightBeOptional)) {
            foreignEntity = (FOREIGN_ENTITY) optionalFactory.orElseNull(mightBeOptional);
        } else {
            foreignEntity = (FOREIGN_ENTITY) mightBeOptional;
        }
        return foreignEntity;
    }

    protected <LOCAL_ENTITY> Object xextractPulloutReverseWrittenObject(ForeignInfo foreignInfo,
            RelationInfo reverseInfo, RelationOptionalFactory optionalFactory, List<LOCAL_ENTITY> mappedLocalList) {
        final Object writtenObj;
        if (foreignInfo.isOneToOne()) {
            if (mappedLocalList != null && !mappedLocalList.isEmpty()) { // should have only one element
                final LOCAL_ENTITY plainFirstElement = mappedLocalList.get(0);
                if (plainFirstElement != null && optionalFactory.isOptionalType(reverseInfo.getPropertyAccessType())) {
                    writtenObj = optionalFactory.createOptionalPresentEntity(plainFirstElement);
                } else {
                    writtenObj = plainFirstElement;
                }
            } else {
                writtenObj = null;
            }
        } else { // many-to-one so reverse is list
            writtenObj = mappedLocalList;
        }
        return writtenObj;
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    protected <LOCAL_ENTITY extends Entity, COLUMN> List<COLUMN> helpExtractListInternally(
            List<LOCAL_ENTITY> localEntityList, String propertyName) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("propertyName", propertyName);
        final List<COLUMN> valueList = new ArrayList<COLUMN>();
        return xdoHelpExtractSetInternally(localEntityList, propertyName, valueList);
    }

    protected <LOCAL_ENTITY extends Entity, COLUMN> Set<COLUMN> helpExtractSetInternally(
            List<LOCAL_ENTITY> localEntityList, String propertyName) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("propertyName", propertyName);
        final Set<COLUMN> valueSet = new LinkedHashSet<COLUMN>();
        return xdoHelpExtractSetInternally(localEntityList, propertyName, valueSet);
    }

    protected <LOCAL_ENTITY extends Entity, COLUMN, COLLECTION extends Collection<COLUMN>> COLLECTION xdoHelpExtractSetInternally(
            List<LOCAL_ENTITY> localEntityList, String propertyName, COLLECTION collection) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("propertyName", propertyName);
        final ColumnInfo columnInfo = getDBMeta().findColumnInfo(propertyName);
        for (LOCAL_ENTITY entity : localEntityList) {
            final COLUMN column = columnInfo.read(entity);
            if (column != null) {
                collection.add(column);
            }
        }
        return collection;
    }

    // ===================================================================================
    //                                                                      Process Method
    //                                                                      ==============
    // defined here (on the readable interface) for non-primary key value
    /**
     * Filter the entity of insert. (basically for non-primary-key insert)
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     * @param option The option of insert. (NullAllowed)
     */
    protected void filterEntityOfInsert(Entity targetEntity, InsertOption<? extends ConditionBean> option) {
    }

    // ===================================================================================
    //                                                                      Delegate Entry
    //                                                                      ==============
    protected int delegateSelectCountUniquely(ConditionBean cb) {
        return invoke(createSelectCountCBCommand(cb, true));
    }

    protected int delegateSelectCountPlainly(ConditionBean cb) {
        return invoke(createSelectCountCBCommand(cb, false));
    }

    protected <ENTITY extends Entity> void delegateSelectCursor(ConditionBean cb, EntityRowHandler<ENTITY> handler,
            Class<ENTITY> entityType) {
        invoke(createSelectCursorCBCommand(cb, handler, entityType));
    }

    protected <ENTITY extends Entity> List<ENTITY> delegateSelectList(ConditionBean cb, Class<ENTITY> entityType) {
        return invoke(createSelectListCBCommand(cb, entityType));
    }

    protected <RESULT> RESULT delegateSelectNextVal(Class<RESULT> resultType) {
        return invoke(createSelectNextValCommand(resultType));
    }

    protected <RESULT> RESULT delegateSelectNextValSub(Class<RESULT> resultType, String columnDbName,
            String sequenceName, Integer incrementSize, Integer cacheSize) {
        return invoke(createSelectNextValSubCommand(resultType, columnDbName, sequenceName, incrementSize, cacheSize));
    }

    protected int delegateInsertNoPK(Entity entity, InsertOption<? extends ConditionBean> option) {
        // only filtering for extension is supported (filtering for common columns is unsupported)
        assertEntityNotNull(entity);
        filterEntityOfInsert(entity, option);
        return invoke(createInsertEntityCommand(entity, option));
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    // -----------------------------------------------------
    //                                               Warm up
    //                                               -------
    public void warmUpCommand() {
        {
            final SelectCountCBCommand cmd = createSelectCountCBCommand(newConditionBean(), true);
            cmd.setInitializeOnly(true);
            invoke(cmd);
        }
        {
            final SelectCountCBCommand cmd = createSelectCountCBCommand(newConditionBean(), false);
            cmd.setInitializeOnly(true);
            invoke(cmd);
        }
        {
            final Class<? extends Entity> entityType = getDBMeta().getEntityType();
            final SelectListCBCommand<? extends Entity> cmd = createSelectListCBCommand(newConditionBean(), entityType);
            cmd.setInitializeOnly(true);
            invoke(cmd);
        }
    }

    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    protected SelectCountCBCommand createSelectCountCBCommand(ConditionBean cb, boolean uniqueCount) {
        assertBehaviorCommandInvoker("createSelectCountCBCommand");
        final SelectCountCBCommand cmd = newSelectCountCBCommand();
        xsetupSelectCommand(cmd);
        cmd.setConditionBean(cb);
        cmd.setUniqueCount(uniqueCount);
        return cmd;
    }

    protected SelectCountCBCommand newSelectCountCBCommand() {
        return new SelectCountCBCommand();
    }

    protected <ENTITY extends Entity> SelectCursorCBCommand<ENTITY> createSelectCursorCBCommand(ConditionBean cb,
            EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType) {
        assertBehaviorCommandInvoker("createSelectCursorCBCommand");
        final SelectCursorCBCommand<ENTITY> cmd = newSelectCursorCBCommand();
        xsetupSelectCommand(cmd);
        cmd.setConditionBean(cb);
        cmd.setEntityType(entityType);
        cmd.setEntityRowHandler(entityRowHandler);
        return cmd;
    }

    protected <ENTITY extends Entity> SelectCursorCBCommand<ENTITY> newSelectCursorCBCommand() {
        return new SelectCursorCBCommand<ENTITY>();
    }

    protected <ENTITY extends Entity> SelectListCBCommand<ENTITY> createSelectListCBCommand(ConditionBean cb,
            Class<ENTITY> entityType) {
        assertBehaviorCommandInvoker("createSelectListCBCommand");
        final SelectListCBCommand<ENTITY> cmd = newSelectListCBCommand();
        xsetupSelectCommand(cmd);
        cmd.setConditionBean(cb);
        cmd.setEntityType(entityType);
        return cmd;
    }

    protected <ENTITY extends Entity> SelectListCBCommand<ENTITY> newSelectListCBCommand() {
        return new SelectListCBCommand<ENTITY>();
    }

    protected <RESULT> SelectNextValCommand<RESULT> createSelectNextValCommand(Class<RESULT> resultType) {
        assertBehaviorCommandInvoker("createSelectNextValCommand");
        final SelectNextValCommand<RESULT> cmd = newSelectNextValCommand();
        xsetupSelectCommand(cmd);
        cmd.setResultType(resultType);
        cmd.setDBMeta(getDBMeta());
        cmd.setSequenceCacheHandler(_behaviorCommandInvoker.getSequenceCacheHandler());
        return cmd;
    }

    protected <RESULT> SelectNextValCommand<RESULT> newSelectNextValCommand() {
        return new SelectNextValCommand<RESULT>();
    }

    protected <RESULT> SelectNextValCommand<RESULT> createSelectNextValSubCommand(Class<RESULT> resultType,
            String columnDbName, String sequenceName, Integer incrementSize, Integer cacheSize) {
        assertBehaviorCommandInvoker("createSelectNextValCommand");
        final SelectNextValSubCommand<RESULT> cmd = newSelectNextValSubCommand();
        xsetupSelectCommand(cmd);
        cmd.setResultType(resultType);
        cmd.setDBMeta(getDBMeta());
        cmd.setSequenceCacheHandler(_behaviorCommandInvoker.getSequenceCacheHandler());
        cmd.setColumnInfo(getDBMeta().findColumnInfo(columnDbName));
        cmd.setSequenceName(sequenceName);
        cmd.setIncrementSize(incrementSize);
        cmd.setCacheSize(cacheSize);
        return cmd;
    }

    protected <RESULT> SelectNextValSubCommand<RESULT> newSelectNextValSubCommand() {
        return new SelectNextValSubCommand<RESULT>();
    }

    protected <RESULT> SelectScalarCBCommand<RESULT> createSelectScalarCBCommand(ConditionBean cb,
            Class<RESULT> resultType, SelectClauseType selectClauseType) {
        assertBehaviorCommandInvoker("createSelectScalarCBCommand");
        final SelectScalarCBCommand<RESULT> cmd = newSelectScalarCBCommand();
        xsetupSelectCommand(cmd);
        cmd.setConditionBean(cb);
        cmd.setResultType(resultType);
        cmd.setSelectClauseType(selectClauseType);
        return cmd;
    }

    protected <RESULT> SelectScalarCBCommand<RESULT> newSelectScalarCBCommand() {
        return new SelectScalarCBCommand<RESULT>();
    }

    protected void xsetupSelectCommand(AbstractBehaviorCommand<?> cmd) {
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
    }

    // -----------------------------------------------------
    //                                                 Write
    //                                                 -----
    // defined here (on the readable interface) for non-primary key value
    protected InsertEntityCommand createInsertEntityCommand(Entity entity, InsertOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createInsertEntityCommand");
        final InsertEntityCommand cmd = newInsertEntityCommand();
        xsetupEntityCommand(cmd, entity);
        cmd.setInsertOption(option);
        return cmd;
    }

    protected InsertEntityCommand newInsertEntityCommand() {
        return new InsertEntityCommand();
    }

    protected void xsetupEntityCommand(AbstractEntityCommand cmd, Entity entity) {
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setEntity(entity);
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    /**
     * Invoke the command of behavior.
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The instance of result. (NullAllowed)
     */
    protected <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        return _behaviorCommandInvoker.invoke(behaviorCommand);
    }

    protected void assertBehaviorCommandInvoker(String methodName) {
        if (_behaviorCommandInvoker != null) {
            return;
        }
        // don't use exception thrower because the thrower is created by BehaviorCommandInvoker
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the invoker of behavior command in the behavior!");
        br.addItem("Advice");
        br.addElement("Please confirm the definition of the set-upper at your component configuration of DBFlute.");
        br.addElement("It is precondition that '" + methodName + "()' needs the invoker instance.");
        br.addItem("Behavior");
        br.addElement("Behavior for " + getTableDbName());
        br.addItem("Attribute");
        br.addElement("behaviorCommandInvoker   : " + _behaviorCommandInvoker);
        br.addElement("behaviorSelector         : " + _behaviorSelector);
        final String msg = br.buildExceptionMessage();
        throw new IllegalBehaviorStateException(msg);
    }

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * Does the entity have a value of version-no? 
     * @param entity The instance of entity. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean hasVersionNoValue(Entity entity) {
        return false; // as default
    }

    /**
     * Does the entity have a value of update-date? 
     * @param entity The instance of entity. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean hasUpdateDateValue(Entity entity) {
        return false; // as default
    }

    // ===================================================================================
    //                                                                   Optional Handling
    //                                                                   =================
    /**
     * Create present or null entity as relation optional.
     * @param relationTitle The title of relation for exception message. (NotNull)
     * @param relationRow The entity instance of relation row. (NullAllowed)
     * @return The optional object for the entity, which has present or null entity. (NotNull)
     */
    protected Object toRelationOptional(final String relationTitle, Object relationRow) {
        assertObjectNotNull("relationTitle", relationTitle);
        final RelationOptionalFactory factory = xgetROpFactory();
        final Object result;
        if (relationRow != null) {
            result = factory.createOptionalPresentEntity(relationRow);
        } else {
            result = factory.createOptionalNullEntity(new OptionalObjectExceptionThrower() {
                public void throwNotFoundException() {
                    String msg = "Not found the relation row for: " + relationTitle;
                    throw new EntityAlreadyDeletedException(msg);
                }
            });
        }
        return result;
    }

    /**
     * Is the property type optional for relation?
     * @param relationPropertyType The type of relation property. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isRelationOptional(Class<?> relationPropertyType) {
        assertObjectNotNull("relationPropertyType", relationPropertyType);
        final RelationOptionalFactory factory = xgetROpFactory();
        return factory.isOptionalType(relationPropertyType);
    }

    protected RelationOptionalFactory xgetROpFactory() {
        assertBehaviorCommandInvoker("xgetROpFactory");
        return _behaviorCommandInvoker.getRelationOptionalFactory();
    }

    // ===================================================================================
    //                                                                     Downcast Helper
    //                                                                     ===============
    @SuppressWarnings("unchecked")
    protected <ENTITY extends Entity> ENTITY helpEntityDowncastInternally(Entity entity, Class<ENTITY> clazz) {
        assertObjectNotNull("entity", entity);
        assertObjectNotNull("clazz", clazz);
        try {
            return (ENTITY) entity;
        } catch (ClassCastException e) {
            String classTitle = DfTypeUtil.toClassTitle(clazz);
            String msg = "The entity should be " + classTitle + " but it was: " + entity.getClass();
            throw new IllegalStateException(msg, e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> CB helpConditionBeanDowncastInternally(ConditionBean cb, Class<CB> clazz) {
        assertObjectNotNull("cb", cb);
        assertObjectNotNull("clazz", clazz);
        try {
            return (CB) cb;
        } catch (ClassCastException e) {
            String classTitle = DfTypeUtil.toClassTitle(clazz);
            String msg = "The condition-bean should be " + classTitle + " but it was: " + cb.getClass();
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected BehaviorExceptionThrower createBhvExThrower() {
        assertBehaviorCommandInvoker("createBhvExThrower");
        return _behaviorCommandInvoker.createBehaviorExceptionThrower();
    }

    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName The variable name for message. (NotNull)
     * @param value The value the checked variable. (NotNull)
     * @exception IllegalArgumentException When the variable name or the variable is null.
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the entity is not null.
     * @param entity The instance of entity to be checked. (NotNull)
     */
    protected void assertEntityNotNull(Entity entity) {
        assertObjectNotNull("entity", entity);
    }

    /**
     * Assert that the entity has primary-key value. e.g. insert(), update(), delete()
     * @param entity The instance of entity to be checked. (NotNull)
     */
    protected void assertEntityNotNullAndHasPrimaryKeyValue(Entity entity) {
        assertEntityNotNull(entity);
        final Set<String> uniqueDrivenPropSet = entity.myuniqueDrivenProperties();
        if (uniqueDrivenPropSet.isEmpty()) { // PK, basically here
            if (!entity.hasPrimaryKeyValue()) {
                createBhvExThrower().throwEntityPrimaryKeyNotFoundException(entity);
            }
        } else { // unique-driven
            for (String prop : uniqueDrivenPropSet) {
                final ColumnInfo columnInfo = getDBMeta().findColumnInfo(prop);
                if (columnInfo != null) {
                    final Object value = columnInfo.read(entity);
                    if (value == null) {
                        createBhvExThrower().throwEntityUniqueKeyNotFoundException(entity);
                    }
                }
            }
        }
    }

    /**
     * Assert that the condition-bean state is valid.
     * @param cb The instance of condition-bean to be checked. (NotNull)
     */
    protected void assertCBStateValid(ConditionBean cb) {
        assertCBNotNull(cb);
        assertCBNotDreamCruise(cb);
    }

    /**
     * Assert that the condition-bean is not null.
     * @param cb The instance of condition-bean to be checked. (NotNull)
     */
    protected void assertCBNotNull(ConditionBean cb) {
        assertObjectNotNull("cb", cb);
    }

    /**
     * Assert that the condition-bean is not dream cruise.
     * @param cb The instance of condition-bean to be checked. (NotNull)
     */
    protected void assertCBNotDreamCruise(ConditionBean cb) {
        if (cb.xisDreamCruiseShip()) {
            String msg = "The condition-bean should not be dream cruise: " + cb.getClass();
            throw new IllegalConditionBeanOperationException(msg);
        }
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> void assertConditionBeanSelectResource(CB cb,
            Class<ENTITY> entityType) {
        assertCBStateValid(cb);
        assertObjectNotNull("entityType", entityType);
        assertSpecifyDerivedReferrerEntityProperty(cb, entityType);
    }

    protected <ENTITY extends Entity> void assertSpecifyDerivedReferrerEntityProperty(ConditionBean cb,
            Class<ENTITY> entityType) {
        if (isSuppressSpecifyDerivedReferrerEntityPropertyCheck()) {
            return;
        }
        final List<String> aliasList = cb.getSqlClause().getSpecifiedDerivingAliasList();
        if (aliasList.isEmpty()) {
            return;
        }
        final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(entityType);
        for (String alias : aliasList) {
            DfPropertyDesc pd = null;
            if (beanDesc.hasPropertyDesc(alias)) { // case insensitive
                pd = beanDesc.getPropertyDesc(alias);
            } else {
                final String noUnsco = Srl.replace(alias, "_", "");
                if (beanDesc.hasPropertyDesc(noUnsco)) { // flexible name
                    pd = beanDesc.getPropertyDesc(noUnsco);
                }
            }
            if (pd != null && pd.hasWriteMethod()) {
                continue;
            }
            throwSpecifyDerivedReferrerEntityPropertyNotFoundException(alias, entityType);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName The variable name for message. (NotNull)
     * @param value The value the checked variable. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull(variableName, value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                           Assert List
    //                                           -----------
    /**
     * Assert that the list is empty.
     * @param ls The instance of list to be checked. (NotNull)
     */
    protected void assertListNotNullAndEmpty(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (!ls.isEmpty()) {
            String msg = "The list should be empty: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the list is not empty.
     * @param ls The instance of list to be checked. (NotNull)
     */
    protected void assertListNotNullAndNotEmpty(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (ls.isEmpty()) {
            String msg = "The list should not be empty: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the list having only one.
     * @param ls The instance of list to be checked. (NotNull)
     */
    protected void assertListNotNullAndHasOnlyOne(List<?> ls) {
        assertObjectNotNull("ls", ls);
        if (ls.size() != 1) {
            String msg = "The list should contain only one object: ls=" + ls.toString();
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * To lower case if the type is String.
     * @param obj The object might be string. (NullAllowed)
     * @return The lower string or plain object. (NullAllowed)
     */
    protected Object toLowerCaseIfString(Object obj) {
        if (obj != null && obj instanceof String) {
            return ((String) obj).toLowerCase();
        }
        return obj;
    }

    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the invoker of behavior command.
     * @return The invoker of behavior command. (NullAllowed: But normally NotNull)
     */
    protected BehaviorCommandInvoker getBehaviorCommandInvoker() {
        return _behaviorCommandInvoker;
    }

    /**
     * Set the invoker of behavior command.
     * @param behaviorCommandInvoker The invoker of behavior command. (NotNull)
     */
    public void setBehaviorCommandInvoker(BehaviorCommandInvoker behaviorCommandInvoker) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
    }

    /**
     * Get the selector of behavior.
     * @return The select of behavior. (NullAllowed: But normally NotNull)
     */
    protected BehaviorSelector getBehaviorSelector() {
        return _behaviorSelector;
    }

    /**
     * Set the selector of behavior.
     * @param behaviorSelector The selector of behavior. (NotNull)
     */
    public void setBehaviorSelector(BehaviorSelector behaviorSelector) {
        this._behaviorSelector = behaviorSelector;
    }
}
