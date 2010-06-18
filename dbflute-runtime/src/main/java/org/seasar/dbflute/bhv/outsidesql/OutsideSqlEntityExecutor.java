/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.outsidesql;

import java.util.List;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.bhv.core.BehaviorCommand;
import org.seasar.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.seasar.dbflute.exception.DangerousResultSizeException;
import org.seasar.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.dbflute.jdbc.FetchBean;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The cursor executor of outside-SQL.
 * @param <PARAMETER_BEAN> The type of parameter-bean.
 * @author jflute
 */
public class OutsideSqlEntityExecutor<PARAMETER_BEAN> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The invoker of behavior command. (NotNull) */
    protected final BehaviorCommandInvoker _behaviorCommandInvoker;

    /** The option of outside-SQL. (NotNull) */
    protected final OutsideSqlOption _outsideSqlOption;

    /** The DB name of table. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected DBDef _currentDBDef;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlEntityExecutor(BehaviorCommandInvoker behaviorCommandInvoker, OutsideSqlOption outsideSqlOption,
            String tableDbName, DBDef currentDBDef) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._outsideSqlOption = outsideSqlOption;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select entity by the outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * SimpleMember member
     *     = memberBhv.outsideSql().entityHandling().<span style="color: #FD4747">selectEntity</span>(path, pmb, entityType);
     * if (member != null) {
     *     ... = member.get...();
     * } else {
     *     ...
     * }
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.seasar.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntity(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final int preSafetyMaxResultSize = xcheckSafetyResultAsOneIfNeed(pmb);
        final List<ENTITY> ls;
        try {
            ls = invoke(createSelectListCommand(path, pmb, entityType));
        } catch (DangerousResultSizeException e) {
            final String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwSelectEntityDuplicatedException("{over safetyMaxResultSize '1'}", searchKey4Log, e);
            return null; // unreachable
        } finally {
            xrestoreSafetyResultIfNeed(pmb, preSafetyMaxResultSize);
        }
        if (ls == null || ls.isEmpty()) {
            return null;
        }
        if (ls.size() > 1) {
            final String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwSelectEntityDuplicatedException(String.valueOf(ls.size()), searchKey4Log, null);
        }
        return ls.get(0);
    }

    /**
     * Select entity with deleted check by the outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * SimpleMember member
     *     = memberBhv.outsideSql().entityHandling().<span style="color: #FD4747">selectEntityWithDeletedCheck</span>(path, pmb, entityType);
     * ... = member.get...(); <span style="color: #3F7E5E">// the entity always be not null</span>
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.seasar.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted(not found).
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntityWithDeletedCheck(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final ENTITY entity = selectEntity(path, pmb, entityType);
        if (entity == null) {
            String searchKey4Log = buildSearchKey4Log(path, pmb, entityType);
            throwSelectEntityAlreadyDeletedException(searchKey4Log);
        }
        return entity;
    }

    protected <ENTITY> String buildSearchKey4Log(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        String tmp = "table  = " + _outsideSqlOption.getTableDbName() + ln();
        tmp = tmp + "path   = " + path + ln();
        tmp = tmp + "pmbean = " + DfTypeUtil.toClassTitle(pmb) + ":" + pmb + ln();
        tmp = tmp + "entity = " + DfTypeUtil.toClassTitle(entityType) + ln();
        tmp = tmp + "option = " + _outsideSqlOption;
        return tmp;
    }

    protected int xcheckSafetyResultAsOneIfNeed(PARAMETER_BEAN pmb) {
        if (pmb instanceof FetchBean) {
            final int safetyMaxResultSize = ((FetchBean) pmb).getSafetyMaxResultSize();
            ((FetchBean) pmb).checkSafetyResult(1);
            return safetyMaxResultSize;
        }
        return 0;
    }

    protected void xrestoreSafetyResultIfNeed(PARAMETER_BEAN pmb, int preSafetyMaxResultSize) {
        if (pmb instanceof FetchBean) {
            ((FetchBean) pmb).checkSafetyResult(preSafetyMaxResultSize);
        }
    }

    protected void throwSelectEntityAlreadyDeletedException(Object searchKey) {
        createBhvExThrower().throwSelectEntityAlreadyDeletedException(searchKey);
    }

    protected void throwSelectEntityDuplicatedException(String resultCountExp, Object searchKey, Throwable cause) {
        createBhvExThrower().throwSelectEntityDuplicatedException(resultCountExp, searchKey, cause);
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, PARAMETER_BEAN pmb,
            Class<ENTITY> entityType) {
        final OutsideSqlSelectListCommand<ENTITY> newed = newOutsideSqlSelectListCommand();
        return xsetupCommand(newed, path, pmb, entityType);
    }

    protected <ENTITY> OutsideSqlSelectListCommand<ENTITY> newOutsideSqlSelectListCommand() {
        return new OutsideSqlSelectListCommand<ENTITY>();
    }

    protected <ENTITY> OutsideSqlSelectListCommand<ENTITY> xsetupCommand(OutsideSqlSelectListCommand<ENTITY> command,
            String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        command.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setOutsideSqlPath(path);
        command.setParameterBean(pmb);
        command.setOutsideSqlOption(_outsideSqlOption);
        command.setCurrentDBDef(_currentDBDef);
        command.setEntityType(entityType);
        return command;
    }

    /**
     * Invoke the command of behavior.
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The instance of result. (Nullable)
     */
    protected <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        return _behaviorCommandInvoker.invoke(behaviorCommand);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set up dynamic-binding for this outside-SQL. <br />
     * You can use bind variable in embedded variable by this.
     * @return this. (NotNull)
     * @deprecated You does not need to call this to set bind variable in embedded variable.
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }

    /**
     * Set up remove-block-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> removeBlockComment() {
        _outsideSqlOption.removeBlockComment();
        return this;
    }

    /**
     * Set up remove-line-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> removeLineComment() {
        _outsideSqlOption.removeLineComment();
        return this;
    }

    /**
     * Set up format-SQL for this outside-SQL. <br />
     * (For example, empty lines removed)
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> formatSql() {
        _outsideSqlOption.formatSql();
        return this;
    }

    /**
     * Configure statement JDBC options. (For example, queryTimeout, fetchSize, ...)
     * @param statementConfig The configuration of statement. (Nullable)
     * @return this. (NotNull)
     */
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
        _outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected BehaviorExceptionThrower createBhvExThrower() {
        return _behaviorCommandInvoker.createBehaviorExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
