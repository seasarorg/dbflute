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

import java.lang.reflect.Method;
import java.util.ArrayList;
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
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingBean;
import org.seasar.dbflute.cbean.PagingHandler;
import org.seasar.dbflute.cbean.PagingInvoker;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.ResultBeanBuilder;
import org.seasar.dbflute.cbean.ScalarQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.chelper.HpFixedConditionQueryResolver;
import org.seasar.dbflute.cbean.coption.CursorSelectOption;
import org.seasar.dbflute.cbean.coption.ScalarSelectOption;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByElement;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.RelationInfo;
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.EntityPrimaryKeyNotFoundException;
import org.seasar.dbflute.exception.FetchingOverSafetySizeException;
import org.seasar.dbflute.exception.IllegalBehaviorStateException;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.exception.PagingOverSafetySizeException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.outsidesql.executor.OutsideSqlBasicExecutor;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The abstract class of readable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorReadable implements BehaviorReadable {

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

    /**
     * {@inheritDoc}
     */
    public Entity readEntityWithDeletedCheck(ConditionBean cb) {
        assertCBStateValid(cb);
        return doReadEntityWithDeletedCheck(cb);
    }

    protected abstract Entity doReadEntityWithDeletedCheck(ConditionBean cb);

    // -----------------------------------------------------
    //                                       Internal Helper
    //                                       ---------------
    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityInternally(CB cb,
            Class<ENTITY> entityType, InternalSelectEntityCallback<ENTITY, CB> callback) {
        if (cb.hasSelectAllPossible() && cb.getFetchSize() != 1) { // if no condition for one
            throwSelectEntityConditionNotFoundException(cb);
        }
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOne(cb);
        final List<ENTITY> ls;
        try {
            ls = callback.callbackSelectList(cb, entityType);
        } catch (DangerousResultSizeException e) {
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

    protected static interface InternalSelectEntityCallback<ENTITY extends Entity, CB extends ConditionBean> {
        public List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> ENTITY helpSelectEntityWithDeletedCheckInternally(
            CB cb, Class<ENTITY> entityType, final InternalSelectEntityWithDeletedCheckCallback<ENTITY, CB> callback) {
        final ENTITY entity = helpSelectEntityInternally(cb, entityType,
                new InternalSelectEntityCallback<ENTITY, CB>() {
                    public List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType) {
                        return callback.callbackSelectList(cb, entityType);
                    }
                });
        assertEntityNotDeleted(entity, cb);
        return entity;
    }

    protected static interface InternalSelectEntityWithDeletedCheckCallback<ENTITY extends Entity, CB extends ConditionBean> {
        public List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType);
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

    // for selectList() and selectCursor() (on sub class)
    protected <ENTITY extends Entity> void assertSpecifyDerivedReferrerEntityProperty(ConditionBean cb,
            Class<ENTITY> entityType) {
        final List<String> aliasList = cb.getSqlClause().getSpecifiedDerivingAliasList();
        for (String alias : aliasList) { // if derived referrer does not exist, empty loop
            final Method[] methods = entityType.getMethods();
            final String expectedName = "set" + Srl.replace(alias, "_", "");
            boolean exists = false;
            for (Method method : methods) {
                final String methodName = method.getName();
                if (methodName.startsWith("set") && expectedName.equalsIgnoreCase(methodName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throwSpecifyDerivedReferrerEntityPropertyNotFoundException(alias, entityType);
            }
        }
    }

    protected void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String alias, Class<?> entityType) {
        createCBExThrower().throwSpecifyDerivedReferrerEntityPropertyNotFoundException(alias, entityType);
    }

    // -----------------------------------------------------
    //                                       Internal Helper
    //                                       ---------------
    protected <ENTITY extends Entity, CB extends ConditionBean> ListResultBean<ENTITY> helpSelectListInternally(CB cb,
            Class<ENTITY> entityType, InternalSelectListCallback<ENTITY, CB> callback) {
        assertCBNotDreamCruise(cb);
        try {
            return createListResultBean(cb, callback.callbackSelectList(cb, entityType));
        } catch (FetchingOverSafetySizeException e) {
            createBhvExThrower().throwDangerousResultSizeException(cb, e);
            return null; // unreachable
        }
    }

    protected static interface InternalSelectListCallback<ENTITY extends Entity, CB extends ConditionBean> {
        List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType);
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

    // -----------------------------------------------------
    //                                       Internal Helper
    //                                       ---------------
    protected <ENTITY extends Entity, CB extends ConditionBean> PagingResultBean<ENTITY> helpSelectPageInternally(
            CB cb, Class<ENTITY> entityType, InternalSelectPageCallback<ENTITY, CB> callback) {
        assertCBNotDreamCruise(cb);
        try {
            final PagingHandler<ENTITY> handler = createPagingHandler(cb, entityType, callback);
            final PagingInvoker<ENTITY> invoker = createPagingInvoker(cb);
            return invoker.invokePaging(handler);
        } catch (PagingOverSafetySizeException e) {
            createBhvExThrower().throwDangerousResultSizeException(cb, e);
            return null; // unreachable
        }
    }

    protected static interface InternalSelectPageCallback<ENTITY extends Entity, CB extends ConditionBean> {
        int callbackSelectCount(CB cb);

        List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> PagingHandler<ENTITY> createPagingHandler(final CB cb,
            final Class<ENTITY> entityType, final InternalSelectPageCallback<ENTITY, CB> callback) {
        return new PagingHandler<ENTITY>() {
            public PagingBean getPagingBean() {
                return cb;
            }

            public int count() {
                try {
                    cb.getSqlClause().makePagingAdjustmentEffective();
                    return callback.callbackSelectCount(cb);
                } finally {
                    cb.getSqlClause().ignorePagingAdjustment();
                }
            }

            public List<ENTITY> paging() {
                try {
                    cb.getSqlClause().makePagingAdjustmentEffective();
                    return callback.callbackSelectList(cb, entityType);
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
            EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType,
            InternalSelectCursorCallback<ENTITY, CB> callback) {
        assertCBNotDreamCruise(cb);
        final CursorSelectOption option = cb.getCursorSelectOption();
        if (option != null && option.isByPaging()) {
            helpSelectCursorHandlingByPaging(cb, entityRowHandler, entityType, callback, option);
        } else { // basically here
            callback.callbackSelectCursor(cb, entityRowHandler, entityType);
        }
    }

    protected static interface InternalSelectCursorCallback<ENTITY extends Entity, CB extends ConditionBean> {
        void callbackSelectCursor(CB cb, EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType);

        List<ENTITY> callbackSelectList(CB cb, Class<ENTITY> entityType);
    }

    protected <ENTITY extends Entity, CB extends ConditionBean> void helpSelectCursorHandlingByPaging(CB cb,
            EntityRowHandler<ENTITY> entityRowHandler, Class<ENTITY> entityType,
            InternalSelectCursorCallback<ENTITY, CB> callback, CursorSelectOption option) {
        helpSelectCursorCheckingByPagingAllowed(cb, option);
        helpSelectCursorCheckingOrderByPK(cb, option);
        final int pageSize = option.getPageSize();
        int pageNumber = 1;
        while (true) {
            cb.paging(pageSize, pageNumber);
            List<ENTITY> pageList = callback.callbackSelectList(cb, entityType);
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
    public <RESULT> SLFunction<ConditionBean, RESULT> readScalar(Class<RESULT> resultType) {
        @SuppressWarnings("unchecked")
        final SLFunction<ConditionBean, RESULT> func = (SLFunction<ConditionBean, RESULT>) doReadScalar(resultType);
        return func;
    }

    protected abstract <RESULT> SLFunction<? extends ConditionBean, RESULT> doReadScalar(Class<RESULT> resultType);

    /**
     * The scalar function. <br />
     * This is not static class because this uses the method 'invoke(BehaviorCommand)'
     * @param <CB> The type of condition-bean.
     * @param <RESULT> The type of result.
     */
    public class SLFunction<CB extends ConditionBean, RESULT> { // SL: ScaLar

        /** The condition-bean for scalar select. (NotNull) */
        protected CB _conditionBean;

        /** The condition-bean for scalar select. (NotNull) */
        protected Class<RESULT> _resultType;

        /**
         * @param conditionBean The condition-bean initialized only for scalar select. (NotNull)
         * @param resultType The type os result. (NotNull)
         */
        public SLFunction(CB conditionBean, Class<RESULT> resultType) {
            _conditionBean = conditionBean;
            _resultType = resultType;
        }

        /**
         * Select the count value. <br />
         * You can also get same result by selectCount(cb) method.
         * <pre>
         * memberBhv.scalarSelect(Integer.class).<span style="color: #FD4747">count</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnMemberId</span>(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The count value calculated by function. (NotNull)
         */
        public RESULT count(ScalarQuery<CB> scalarQuery) {
            return doCount(scalarQuery, null);
        }

        /**
         * Select the count value with function conversion option.
         * <pre>
         * memberBhv.scalarSelect(Integer.class).<span style="color: #FD4747">count</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().columnMemberId(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The count value calculated by function. (NotNull)
         */
        public RESULT count(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doCount(scalarQuery, option);
        }

        protected RESULT doCount(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.UNIQUE_COUNT, option);
        }

        /**
         * Select the count-distinct value. <br />
         * You can also get same result by selectCount(cb) method.
         * <pre>
         * memberBhv.scalarSelect(Integer.class).<span style="color: #FD4747">countDistinct</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnMemberId</span>(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The count-distinct value calculated by function. (NotNull)
         */
        public RESULT countDistinct(ScalarQuery<CB> scalarQuery) {
            return doCountDistinct(scalarQuery, null);
        }

        /**
         * Select the count-distinct value with function conversion option.
         * <pre>
         * memberBhv.scalarSelect(Integer.class).<span style="color: #FD4747">countDistinct</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().columnMemberId(); <span style="color: #3F7E5E">// the required specification of (basically) primary key column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The count-distinct value calculated by function. (NotNull)
         */
        public RESULT countDistinct(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doCountDistinct(scalarQuery, option);
        }

        protected RESULT doCountDistinct(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.COUNT_DISTINCT, option);
        }

        /**
         * Select the maximum value.
         * <pre>
         * memberBhv.scalarSelect(Date.class).<span style="color: #FD4747">max</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The maximum value calculated by function. (NullAllowed)
         */
        public RESULT max(ScalarQuery<CB> scalarQuery) {
            return doMax(scalarQuery, null);
        }

        /**
         * Select the maximum value with function conversion option.
         * <pre>
         * memberBhv.scalarSelect(Date.class).<span style="color: #FD4747">max</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The maximum value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
         */
        public RESULT max(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doMax(scalarQuery, option);
        }

        protected RESULT doMax(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.MAX, option);
        }

        /**
         * Select the minimum value.
         * <pre>
         * memberBhv.scalarSelect(Date.class).<span style="color: #FD4747">min</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The minimum value calculated by function. (NullAllowed)
         */
        public RESULT min(ScalarQuery<CB> scalarQuery) {
            return doMin(scalarQuery, null);
        }

        /**
         * Select the minimum value with function conversion option.
         * <pre>
         * memberBhv.scalarSelect(Date.class).<span style="color: #FD4747">min</span>(new ScalarQuery(MemberCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnBirthdate</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setMemberStatusCode_Equal_Formalized(); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The minimum value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
         */
        public RESULT min(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doMin(scalarQuery, option);
        }

        protected RESULT doMin(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.MIN, option);
        }

        /**
         * Select the summary value.
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).<span style="color: #FD4747">sum</span>(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The summary value calculated by function. (NullAllowed)
         */
        public RESULT sum(ScalarQuery<CB> scalarQuery) {
            return doSum(scalarQuery, null);
        }

        /**
         * Select the summary value with function conversion option.
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).<span style="color: #FD4747">sum</span>(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The summary value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
         */
        public RESULT sum(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doSum(scalarQuery, option);
        }

        protected RESULT doSum(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.SUM, option);
        }

        /**
         * Select the average value.
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).<span style="color: #FD4747">avg</span>(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
         * });
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @return The average value calculated by function. (NullAllowed)
         */
        public RESULT avg(ScalarQuery<CB> scalarQuery) {
            return doAvg(scalarQuery, null);
        }

        /**
         * Select the average value.
         * <pre>
         * purchaseBhv.scalarSelect(Integer.class).<span style="color: #FD4747">avg</span>(new ScalarQuery(PurchaseCB cb) {
         *     cb.specify().<span style="color: #FD4747">columnPurchaseCount</span>(); <span style="color: #3F7E5E">// the required specification of target column</span>
         *     cb.query().setPurchaseDatetime_GreaterEqual(date); <span style="color: #3F7E5E">// query as you like it</span>
         * }, new ScalarSelectOption().<span style="color: #FD4747">coalesce</span>(0));
         * </pre>
         * @param scalarQuery The query for scalar. (NotNull)
         * @param option The option for scalar. (NotNull)
         * @return The average value calculated by function. (NullAllowed: or NotNull if you use coalesce by option)
         */
        public RESULT avg(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarSelectOption(option);
            return doAvg(scalarQuery, option);
        }

        protected RESULT doAvg(ScalarQuery<CB> scalarQuery, ScalarSelectOption option) {
            assertScalarQuery(scalarQuery);
            return exec(scalarQuery, SqlClause.SelectClauseType.AVG, option);
        }

        protected RESULT exec(ScalarQuery<CB> scalarQuery, SqlClause.SelectClauseType selectClauseType,
                ScalarSelectOption option) {
            assertObjectNotNull("scalarQuery", scalarQuery);
            assertObjectNotNull("selectClauseType", selectClauseType);
            assertObjectNotNull("conditionBean", _conditionBean);
            assertObjectNotNull("resultType", _resultType);
            scalarQuery.query(_conditionBean);
            setupTargetColumnInfo(option);
            setupScalarSelectOption(option);
            assertScalarSelectRequiredSpecifyColumn();
            return invoke(createSelectScalarCBCommand(_conditionBean, _resultType, selectClauseType));
        }

        protected void setupTargetColumnInfo(ScalarSelectOption option) {
            if (option == null) {
                return;
            }
            final SqlClause sqlClause = _conditionBean.getSqlClause();
            ColumnInfo columnInfo = sqlClause.getSpecifiedColumnInfoAsOne();
            if (columnInfo != null) {
                columnInfo = sqlClause.getSpecifiedDerivingColumnInfoAsOne();
            }
            option.xsetTargetColumnInfo(columnInfo);
        }

        protected void setupScalarSelectOption(ScalarSelectOption option) {
            if (option != null) {
                _conditionBean.xacceptScalarSelectOption(option);
                _conditionBean.localCQ().xregisterParameterOption(option);
            }
        }

        protected void assertScalarSelectRequiredSpecifyColumn() {
            final SqlClause sqlClause = _conditionBean.getSqlClause();
            final String columnName = sqlClause.getSpecifiedColumnDbNameAsOne();
            final String subQuery = sqlClause.getSpecifiedDerivingSubQueryAsOne();
            // should be specified is an only one object (column or sub-query)
            if ((columnName != null && subQuery != null) || (columnName == null && subQuery == null)) {
                throwScalarSelectInvalidColumnSpecificationException();
            }
        }

        protected void throwScalarSelectInvalidColumnSpecificationException() {
            createCBExThrower().throwScalarSelectInvalidColumnSpecificationException(_conditionBean, _resultType);
        }

        protected void assertScalarQuery(ScalarQuery<?> scalarQuery) {
            if (scalarQuery == null) {
                String msg = "The argument 'scalarQuery' for ScalarSelect should not be null.";
                throw new IllegalArgumentException(msg);
            }
        }

        protected void assertScalarSelectOption(ScalarSelectOption option) {
            if (option == null) {
                String msg = "The argument 'option' for ScalarSelect should not be null.";
                throw new IllegalArgumentException(msg);
            }
        }
    }

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
    /**
     * Help load referrer internally.
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param callback The internal call-back of loadReferrer. (NotNull) 
     */
    protected <LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> void helpLoadReferrerInternally(
            List<LOCAL_ENTITY> localEntityList, LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            InternalLoadReferrerCallback<LOCAL_ENTITY, PK, REFERRER_CB, REFERRER_ENTITY> callback) {
        doHelpLoadReferrerInternally(localEntityList, loadReferrerOption, callback);
    }

    /**
     * Do help load referrer internally.
     * About internal policy, the value of primary key(and others too) is treated as CaseInsensitive.
     * @param <LOCAL_ENTITY> The type of base entity.
     * @param <PK> The type of primary key.
     * @param <REFERRER_CB> The type of referrer condition-bean.
     * @param <REFERRER_ENTITY> The type of referrer entity.
     * @param localEntityList The list of local entity. (NotNull)
     * @param loadReferrerOption The option of loadReferrer. (NotNull)
     * @param callback The internal call-back of loadReferrer. (NotNull) 
     */
    protected <LOCAL_ENTITY extends Entity, PK, REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> void doHelpLoadReferrerInternally(
            List<LOCAL_ENTITY> localEntityList, LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> loadReferrerOption,
            final InternalLoadReferrerCallback<LOCAL_ENTITY, PK, REFERRER_CB, REFERRER_ENTITY> callback) {

        // - - - - - - - - - - -
        // Assert pre-condition
        // - - - - - - - - - - -
        assertBehaviorSelectorNotNull("loadReferrer");
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
        if (localEntityList.isEmpty()) {
            return;
        }

        // - - - - - - - - - - - - - -
        // Prepare temporary container
        // - - - - - - - - - - - - - -
        final Map<PK, LOCAL_ENTITY> pkLocalEntityMap = new LinkedHashMap<PK, LOCAL_ENTITY>();
        final List<PK> pkList = new ArrayList<PK>();
        for (LOCAL_ENTITY localEntity : localEntityList) {
            final PK primaryKeyValue = callback.getPKVal(localEntity);
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
        final Map<PK, List<REFERRER_ENTITY>> pkReferrerListMap = new LinkedHashMap<PK, List<REFERRER_ENTITY>>();
        for (REFERRER_ENTITY referrerEntity : referrerList) {
            final PK referrerListKey;
            {
                final PK foreignKeyValue = callback.getFKVal(referrerEntity);
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
            final PK referrerListKey;
            {
                final PK primaryKey = callback.getPKVal(localEntity);
                referrerListKey = toLoadReferrerMappingKey(primaryKey);
            }
            if (pkReferrerListMap.containsKey(referrerListKey)) {
                callback.setRfLs(localEntity, pkReferrerListMap.get(referrerListKey));
            } else {
                callback.setRfLs(localEntity, new ArrayList<REFERRER_ENTITY>());
            }
        }
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
        return (PK) toLowerCaseIfString(value);
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

    // assertLoadReferrerArgument() as Internal
    protected void xassLRArg(Entity entity, ConditionBeanSetupper<? extends ConditionBean> conditionBeanSetupper) {
        assertObjectNotNull("entity(" + DfTypeUtil.toClassTitle(getDBMeta().getEntityType()) + ")", entity);
        assertObjectNotNull("conditionBeanSetupper", conditionBeanSetupper);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            ConditionBeanSetupper<? extends ConditionBean> conditionBeanSetupper) {
        assertObjectNotNull("List<" + DfTypeUtil.toClassTitle(getDBMeta().getEntityType()) + ">", entityList);
        assertObjectNotNull("conditionBeanSetupper", conditionBeanSetupper);
    }

    protected void xassLRArg(Entity entity,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("entity(" + DfTypeUtil.toClassTitle(getDBMeta().getEntityType()) + ")", entity);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
    }

    protected void xassLRArg(List<? extends Entity> entityList,
            LoadReferrerOption<? extends ConditionBean, ? extends Entity> loadReferrerOption) {
        assertObjectNotNull("List<" + DfTypeUtil.toClassTitle(getDBMeta().getEntityType()) + ">", entityList);
        assertObjectNotNull("loadReferrerOption", loadReferrerOption);
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
            List<LOCAL_ENTITY> localEntityList, InternalPulloutCallback<LOCAL_ENTITY, FOREIGN_ENTITY> callback) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("callback", callback);
        final Set<FOREIGN_ENTITY> foreignSet = new LinkedHashSet<FOREIGN_ENTITY>();
        final Map<FOREIGN_ENTITY, List<LOCAL_ENTITY>> foreignReferrerMap = new LinkedHashMap<FOREIGN_ENTITY, List<LOCAL_ENTITY>>();
        final boolean existsReferrer = callback.hasRf();
        for (LOCAL_ENTITY entity : localEntityList) {
            final FOREIGN_ENTITY foreignEntity = callback.getFr(entity);
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
                foreignReferrerMap.get(foreignEntity).add(entity);
            }
        }
        final Set<Entry<FOREIGN_ENTITY, List<LOCAL_ENTITY>>> entrySet = foreignReferrerMap.entrySet();
        for (Entry<FOREIGN_ENTITY, List<LOCAL_ENTITY>> entry : entrySet) {
            callback.setRfLs(entry.getKey(), entry.getValue());
        }
        return new ArrayList<FOREIGN_ENTITY>(foreignSet);
    }

    protected static interface InternalPulloutCallback<LOCAL_ENTITY extends Entity, FOREIGN_ENTITY extends Entity> {
        FOREIGN_ENTITY getFr(LOCAL_ENTITY entity); // getForeignEntity()

        boolean hasRf(); // hasReferrer()

        void setRfLs(FOREIGN_ENTITY foreignEntity, List<LOCAL_ENTITY> localList); // setReferrerList()
    }

    // ===================================================================================
    //                                                                      Extract Column
    //                                                                      ==============
    protected <LOCAL_ENTITY extends Entity, COLUMN> List<COLUMN> helpExtractListInternally(
            List<LOCAL_ENTITY> localEntityList, InternalExtractCallback<LOCAL_ENTITY, COLUMN> callback) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("callback", callback);
        final List<COLUMN> valueList = new ArrayList<COLUMN>();
        for (LOCAL_ENTITY entity : localEntityList) {
            final COLUMN column = callback.getCV(entity);
            if (column != null) {
                valueList.add(column);
            }
        }
        return valueList;
    }

    protected <LOCAL_ENTITY extends Entity, COLUMN> Set<COLUMN> helpExtractSetInternally(
            List<LOCAL_ENTITY> localEntityList, InternalExtractCallback<LOCAL_ENTITY, COLUMN> callback) {
        assertObjectNotNull("localEntityList", localEntityList);
        assertObjectNotNull("callback", callback);
        final Set<COLUMN> valueSet = new LinkedHashSet<COLUMN>();
        for (LOCAL_ENTITY entity : localEntityList) {
            final COLUMN column = callback.getCV(entity);
            if (column != null) {
                valueSet.add(column);
            }
        }
        return valueSet;
    }

    protected static interface InternalExtractCallback<LOCAL_ENTITY extends Entity, COLUMN> {
        COLUMN getCV(LOCAL_ENTITY entity); // getColumnValue()
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
            Class<RESULT> resultType, SqlClause.SelectClauseType selectClauseType) {
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
    protected abstract boolean hasVersionNoValue(Entity entity);

    /**
     * Does the entity have a value of update-date? 
     * @param entity The instance of entity. (NotNull)
     * @return The determination, true or false.
     */
    protected abstract boolean hasUpdateDateValue(Entity entity);

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
            String msg = "The entity should be " + DfTypeUtil.toClassTitle(clazz);
            msg = msg + " but it was: " + entity.getClass();
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
            String msg = "The condition-bean should be " + DfTypeUtil.toClassTitle(clazz);
            msg = msg + " but it was: " + cb.getClass();
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
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
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
     * @param entity Entity. (NotNull)
     */
    protected void assertEntityNotNull(Entity entity) {
        assertObjectNotNull("entity", entity);
    }

    /**
     * Assert that the condition-bean state is valid.
     * @param cb Condition-bean. (NotNull)
     */
    protected void assertCBStateValid(ConditionBean cb) {
        assertCBNotNull(cb);
        assertCBNotDreamCruise(cb);
    }

    /**
     * Assert that the condition-bean is not null.
     * @param cb Condition-bean. (NotNull)
     */
    protected void assertCBNotNull(ConditionBean cb) {
        assertObjectNotNull("cb", cb);
    }

    /**
     * Assert that the condition-bean is not dream cruise.
     * @param cb Condition-bean. (NotNull)
     */
    protected void assertCBNotDreamCruise(ConditionBean cb) {
        if (cb.xisDreamCruiseShip()) {
            String msg = "The condition-bean should not be dream cruise: " + cb.getClass();
            throw new IllegalConditionBeanOperationException(msg);
        }
    }

    /**
     * Assert that the entity has primary-key value. e.g. insert(), update()
     * @param entity Entity. (NotNull)
     */
    protected void assertEntityNotNullAndHasPrimaryKeyValue(Entity entity) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            throwEntityPrimaryKeyNotFoundException(entity);
        }
    }

    protected void throwEntityPrimaryKeyNotFoundException(Entity entity) {
        final String classTitle = DfTypeUtil.toClassTitle(entity);
        final String behaviorName = Srl.substringLastRear(entity.getDBMeta().getBehaviorTypeName(), ".");
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The primary-key value in the entity was not found.");
        br.addItem("Advice");
        br.addElement("An entity should have its primary-key value when e.g. insert(), update().");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    " + classTitle + " entity = new " + classTitle + "();");
        br.addElement("    entity.setFooName(...);");
        br.addElement("    entity.setFooDate(...);");
        br.addElement("    " + behaviorName + ".updateNonstrict(entity);");
        br.addElement("  (o):");
        br.addElement("    " + classTitle + " entity = new " + classTitle + "();");
        br.addElement("    entity.setFooId(...); // *Point");
        br.addElement("    entity.setFooName(...);");
        br.addElement("    entity.setFooDate(...);");
        br.addElement("    " + behaviorName + ".updateNonstrict(entity);");
        br.addElement("Or if your process is insert(), you might expect identity.");
        br.addElement("Confirm the primary-key's identity setting.");
        br.addItem("Entity");
        br.addElement(entity);
        final String msg = br.buildExceptionMessage();
        throw new EntityPrimaryKeyNotFoundException(msg);
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
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
     * @param ls List. (NotNull)
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
     * @param ls List. (NotNull)
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
     * @param ls List. (NotNull)
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
     * @param obj Object. (NullAllowed)
     * @return Lower object. (NullAllowed)
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
