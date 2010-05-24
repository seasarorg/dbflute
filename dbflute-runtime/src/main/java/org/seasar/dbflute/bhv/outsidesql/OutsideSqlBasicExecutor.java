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
import org.seasar.dbflute.bhv.core.command.AbstractOutsideSqlCommand;
import org.seasar.dbflute.bhv.core.command.OutsideSqlCallCommand;
import org.seasar.dbflute.bhv.core.command.OutsideSqlExecuteCommand;
import org.seasar.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.ResultBeanBuilder;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.outsidesql.ProcedurePmb;

/**
 * The executor of outside-SQL. <br />
 * <pre>
 * {Basic}
 *   o selectList()
 *   o execute()
 *   o call()
 * 
 * {Entity}
 *   o entityHandling().selectEntity()
 *   o entityHandling().selectEntityWithDeletedCheck()
 * 
 * {Paging}
 *   o autoPaging().selectList()
 *   o autoPaging().selectPage()
 *   o manualPaging().selectList()
 *   o manualPaging().selectPage()
 * 
 * {Cursor}
 *   o cursorHandling().selectCursor()
 * 
 * {Option}
 *   o dynamicBinding().selectList()
 *   o removeBlockComment().selectList()
 *   o removeLineComment().selectList()
 *   o formatSql().selectList()
 * </pre>
 * @author jflute
 */
public class OutsideSqlBasicExecutor {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The invoker of behavior command. (NotNull) */
    protected final BehaviorCommandInvoker _behaviorCommandInvoker;

    /** Table DB name. (NotNull) */
    protected final String _tableDbName;

    /** The current database definition. (NotNull) */
    protected final DBDef _currentDBDef;

    /** The default configuration of statement. (Nullable) */
    protected final StatementConfig _defaultStatementConfig;

    /** Is it dynamic binding? */
    protected boolean _dynamicBinding;

    /** Does it remove block comments from the SQL? */
    protected boolean _removeBlockComment;

    /** Does it remove line comments from the SQL? */
    protected boolean _removeLineComment;

    /** Does it format the SQL? */
    protected boolean _formatSql;

    /** The configuration of statement. (Nullable) */
    protected StatementConfig _statementConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlBasicExecutor(BehaviorCommandInvoker behaviorCommandInvoker, String tableDbName,
            DBDef currentDBDef, StatementConfig defaultStatementConfig) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
        this._defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select the list of the entity by the outside-SQL.
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberName_PrefixSearch("S");
     * Class&lt;SimpleMember&gt; entityType = SimpleMember.class;
     * ListResultBean&lt;SimpleMember&gt; memberList
     *     = memberBhv.outsideSql().<span style="color: #FD4747">selectList</span>(path, pmb, entityType);
     * for (SimpleMember member : memberList) {
     *     ... = member.get...();
     * }
     * </pre>
     * It needs to use customize-entity and parameter-bean.
     * The way to generate them is following:
     * <pre>
     * -- #df:entity#
     * -- !df:pmb!
     * -- !!Integer memberId!!
     * -- !!String memberName!!
     * -- !!...!!
     * </pre>
     * @param <ENTITY> The type of entity for element.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The element type of entity. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> ListResultBean<ENTITY> selectList(String path, Object pmb, Class<ENTITY> entityType) {
        List<ENTITY> resultList = invoke(createSelectListCommand(path, pmb, entityType));
        return createListResultBean(resultList);
    }

    protected <ENTITY> ListResultBean<ENTITY> createListResultBean(List<ENTITY> selectedList) {
        return new ResultBeanBuilder<ENTITY>(_tableDbName).buildListResultBean(selectedList);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * Execute the outside-SQL. {insert, update, delete, etc...}
     * <pre>
     * String path = MemberBhv.PATH_selectSimpleMember;
     * SimpleMemberPmb pmb = new SimpleMemberPmb();
     * pmb.setMemberId(3);
     * int count = memberBhv.outsideSql().<span style="color: #FD4747">execute</span>(path, pmb);
     * </pre>
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @return The count of execution.
     * @exception org.seasar.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public int execute(String path, Object pmb) {
        return invoke(createExecuteCommand(path, pmb));
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                      Procedure Call
    //                                                                      ==============
    /**
     * Call the procedure.
     * <pre>
     * SpInOutParameterPmb pmb = new SpInOutParameterPmb();
     * pmb.setVInVarchar("foo");
     * pmb.setVInOutVarchar("bar");
     * memberBhv.outsideSql().<span style="color: #FD4747">call</span>(pmb);
     * String outVar = pmb.getVOutVarchar();
     * </pre>
     * It needs to use parameter-bean for procedure (ProcedurePmb).
     * The way to generate is to set the option of DBFlute property and execute Sql2Entity.
     * @param pmb The parameter-bean for procedure. (NotNull)
     */
    public void call(ProcedurePmb pmb) {
        if (pmb == null) {
            throw new IllegalArgumentException("The argument of call() 'pmb' should not be null!");
        }
        invoke(createCallCommand(pmb.getProcedureName(), pmb));
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, Object pmb,
            Class<ENTITY> entityType) {
        final OutsideSqlSelectListCommand<ENTITY> cmd;
        {
            final OutsideSqlSelectListCommand<ENTITY> newed = newOutsideSqlSelectListCommand();
            cmd = xsetupCommand(newed, path, pmb); // has a little generic headache...
        }
        cmd.setEntityType(entityType);
        return cmd;
    }

    protected <ENTITY> OutsideSqlSelectListCommand<ENTITY> newOutsideSqlSelectListCommand() {
        return new OutsideSqlSelectListCommand<ENTITY>();
    }

    protected BehaviorCommand<Integer> createExecuteCommand(String path, Object pmb) {
        return xsetupCommand(newOutsideSqlExecuteCommand(), path, pmb);
    }

    protected OutsideSqlExecuteCommand newOutsideSqlExecuteCommand() {
        return new OutsideSqlExecuteCommand();
    }

    protected BehaviorCommand<Void> createCallCommand(String path, Object pmb) {
        return xsetupCommand(newOutsideSqlCallCommand(), path, pmb);
    }

    protected OutsideSqlCallCommand newOutsideSqlCallCommand() {
        return new OutsideSqlCallCommand();
    }

    protected <COMMAND extends AbstractOutsideSqlCommand<?>> COMMAND xsetupCommand(COMMAND command, String path,
            Object pmb) {
        command.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setOutsideSqlPath(path);
        command.setParameterBean(pmb);
        command.setOutsideSqlOption(createOutsideSqlOption());
        command.setCurrentDBDef(_currentDBDef);
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
    //                                                                              Paging
    //                                                                              ======
    /**
     * Prepare the paging as manualPaging.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">manualPaging()</span>.selectPage(path, pmb, SimpleMember.class);
     * </pre>
     * If you call this, you need to write paging condition on your SQL.
     * <pre>
     * ex) MySQL
     * select member.MEMBER_ID, member...
     *   from Member member
     *  where ...
     *  order by ...
     *  limit 40, 20 <span style="color: #3F7E5E">-- is necessary!</span>
     * </pre>
     * @return The executor of paging that the paging mode is manual. (NotNull)
     */
    public OutsideSqlPagingExecutor manualPaging() {
        final OutsideSqlOption option = createOutsideSqlOption();
        option.manualPaging();
        return createOutsideSqlPagingExecutor(option);
    }

    /**
     * Prepare the paging as autoPaging.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">autoPaging()</span>.selectPage(path, pmb, SimpleMember.class);
     * </pre>
     * If you call this, you don't need to write paging condition on your SQL.
     * <pre>
     * ex) MySQL
     * select member.MEMBER_ID, member...
     *   from Member member
     *  where ...
     *  order by ...
     * <span style="color: #3F7E5E">-- limit 40, 20 -- is unnecessary!</span>
     * </pre>
     * @return The executor of paging that the paging mode is auto. (NotNull)
     */
    public OutsideSqlPagingExecutor autoPaging() {
        final OutsideSqlOption option = createOutsideSqlOption();
        option.autoPaging();
        return createOutsideSqlPagingExecutor(option);
    }

    protected OutsideSqlPagingExecutor createOutsideSqlPagingExecutor(OutsideSqlOption option) {
        return new OutsideSqlPagingExecutor(_behaviorCommandInvoker, option, _tableDbName, _currentDBDef,
                _defaultStatementConfig);
    }

    // ===================================================================================
    //                                                                              Cursor
    //                                                                              ======
    /**
     * Prepare cursor handling.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">cursorHandling()</span>.selectCursor(path, pmb, handler);
     * </pre>
     * @return The cursor executor of outside-SQL. (NotNull)
     */
    public OutsideSqlCursorExecutor<Object> cursorHandling() {
        return createOutsideSqlCursorExecutor(createOutsideSqlOption());
    }

    protected OutsideSqlCursorExecutor<Object> createOutsideSqlCursorExecutor(OutsideSqlOption option) {
        return new OutsideSqlCursorExecutor<Object>(_behaviorCommandInvoker, option, _tableDbName, _currentDBDef);
    }

    /**
     * Prepare entity handling.
     * <pre>
     * memberBhv.outsideSql().<span style="color: #FD4747">entityHandling()</span>.selectEntityWithDeletedCheck(path, pmb, SimpleMember.class);
     * </pre>
     * @return The cursor executor of outside-SQL. (NotNull)
     */
    public OutsideSqlEntityExecutor<Object> entityHandling() {
        return createOutsideSqlEntityExecutor(createOutsideSqlOption());
    }

    protected OutsideSqlEntityExecutor<Object> createOutsideSqlEntityExecutor(OutsideSqlOption option) {
        return new OutsideSqlEntityExecutor<Object>(_behaviorCommandInvoker, option, _tableDbName, _currentDBDef);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    // -----------------------------------------------------
    //                                       Dynamic Binding
    //                                       ---------------
    /**
     * Set up dynamic-binding for this outside-SQL. <br />
     * You can use bind variable comment in embedded variable comment by this.
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor dynamicBinding() {
        _dynamicBinding = true;
        return this;
    }

    // -----------------------------------------------------
    //                                       Remove from SQL
    //                                       ---------------
    /**
     * Set up remove-block-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor removeBlockComment() {
        _removeBlockComment = true;
        return this;
    }

    /**
     * Set up remove-line-comment for this outside-SQL.
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor removeLineComment() {
        _removeLineComment = true;
        return this;
    }

    // -----------------------------------------------------
    //                                            Format SQL
    //                                            ----------
    /**
     * Set up format-SQL for this outside-SQL. <br />
     * (For example, empty lines removed)
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor formatSql() {
        _formatSql = true;
        return this;
    }

    // -----------------------------------------------------
    //                                      Statement Config
    //                                      ----------------
    /**
     * Configure statement JDBC options. (For example, queryTimeout, fetchSize, ...)
     * @param statementConfig The configuration of statement. (Nullable)
     * @return this. (NotNull)
     */
    public OutsideSqlBasicExecutor configure(StatementConfig statementConfig) {
        _statementConfig = statementConfig;
        return this;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected OutsideSqlOption createOutsideSqlOption() {
        final OutsideSqlOption option = new OutsideSqlOption();
        if (_dynamicBinding) {
            option.dynamicBinding();
        }
        if (_removeBlockComment) {
            option.removeBlockComment();
        }
        if (_removeLineComment) {
            option.removeLineComment();
        }
        if (_formatSql) {
            option.formatSql();
        }
        option.setStatementConfig(_statementConfig);
        option.setTableDbName(_tableDbName);// as information
        return option;
    }
}
