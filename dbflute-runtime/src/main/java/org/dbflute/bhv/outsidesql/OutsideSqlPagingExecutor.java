package org.dbflute.bhv.outsidesql;

import java.util.List;

import org.dbflute.DBDef;
import org.dbflute.bhv.core.BehaviorCommand;
import org.dbflute.bhv.core.BehaviorCommandInvoker;
import org.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.dbflute.cbean.ListResultBean;
import org.dbflute.cbean.PagingBean;
import org.dbflute.cbean.PagingHandler;
import org.dbflute.cbean.PagingInvoker;
import org.dbflute.cbean.PagingResultBean;
import org.dbflute.cbean.ResultBeanBuilder;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.outsidesql.OutsideSqlOption;


/**
 * The paging executor of outside-SQL.
 * @author DBFlute(AutoGenerator)
 */
public class OutsideSqlPagingExecutor {

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
    protected final DBDef _currentDBDef;
	
	/** The default configuration of statement. (Nullable) */
	protected final StatementConfig _defaultStatementConfig;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public OutsideSqlPagingExecutor(BehaviorCommandInvoker behaviorCommandInvoker
                                  , OutsideSqlOption outsideSqlOption
                                  , String tableDbName
                                  , DBDef currentDBDef
                                  , StatementConfig defaultStatementConfig) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._outsideSqlOption = outsideSqlOption;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
        this._defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select list with paging.
     * <p>
     * The SQL should have Paging without Count. <br />
     * You do not need to use pagingBean's isPaging() method on your 'Parameter Comment'. <br />
     * <pre>
     * - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Your Correct SQL {MySQL and manualPaging}
     * - - - - - - - - - - - - - - - - - - - - - - -
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME
     * #      , memberStatus.MEMBER_STATUS_NAME
     * #   from MEMBER member
     * #     left outer join MEMBER_STATUS memberStatus
     * #       on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
     * #  /[*]BEGIN[*]/where
     * #    /[*]IF pmb.memberId != null[*]/member.MEMBER_ID = /[*]pmb.memberId[*]/'123'/[*]END[*]/
     * #    /[*]IF pmb.memberName != null[*]/and member.MEMBER_NAME like /[*]pmb.memberName[*]/'Billy' || '%'/[*]END[*]/
     * #  /[*]END[*]/
     * #  order by member.UPDATE_DATETIME desc
     * #  limit /[*]$pmb.pageStartIndex[*]/80, /[*]$pmb.fetchSize[*]/20
     * # 
     * o [*] is easy escape to Java Doc Comment.
     * o If it's autoPaging, the line of 'limit 80, 20' is unnecessary!
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL that executes count and paging. (NotNull)
     * @param pmb The bean of paging parameter. (NotNull)
     * @param entityType The type of result entity. (NotNull)
     * @return The result bean of paged list. (NotNull)
     * @exception org.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> ListResultBean<ENTITY> selectList(String path, PagingBean pmb, Class<ENTITY> entityType) {
        setupScrollableCursorIfNeeds();
        List<ENTITY> resultList = invoke(createSelectListCommand(path, pmb, entityType));
        return new ResultBeanBuilder<ENTITY>(_tableDbName).buildListResultBean(resultList);
    }

    /**
     * Select page.
     * <p>
     * The SQL should have Count and Paging. <br />
     * You can realize by pagingBean's isPaging() method on your 'Parameter Comment'. For example, 'IF Comment'. <br />
     * It returns false when it executes Count. And it returns true when it executes Paging. <br />
     * <pre>
     * - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Your Correct SQL {MySQL and manualPaging}
     * - - - - - - - - - - - - - - - - - - - - - - -
     * # /[*]IF pmb.isPaging()[*]/
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME
     * #      , memberStatus.MEMBER_STATUS_NAME
     * # -- ELSE select count(*)
     * # /[*]END[*]/
     * #   from MEMBER member
     * #     /[*]IF pmb.isPaging()[*]/
     * #     left outer join MEMBER_STATUS memberStatus
     * #       on member.MEMBER_STATUS_CODE = memberStatus.MEMBER_STATUS_CODE
     * #     /[*]END[*]/
     * #  /[*]BEGIN[*]/where
     * #    /[*]IF pmb.memberId != null[*]/member.MEMBER_ID = /[*]pmb.memberId[*]/'123'/[*]END[*]/
     * #    /[*]IF pmb.memberName != null[*]/and member.MEMBER_NAME like /[*]pmb.memberName[*]/'Billy' || '%'/[*]END[*]/
     * #  /[*]END[*]/
     * #  /[*]IF pmb.isPaging()[*]/
     * #  order by member.UPDATE_DATETIME desc
     * #  /[*]END[*]/
     * #  /[*]IF pmb.isPaging()[*]/
     * #  limit /[*]$pmb.pageStartIndex[*]/80, /[*]$pmb.fetchSize[*]/20
     * #  /[*]END[*]/
     * # 
     * o [*] is easy escape to Java Doc Comment.
     * o If it's autoPaging, the line of 'limit 80, 20' is unnecessary!
     * 
     * - - - - - - - - - - - - - - - - - - - - - - - - -
     * ex) Wrong SQL {part 1}
     *     -- Line comment before ELSE comment --
     * - - - - - - - - - - - - - - - - - - - - - - - - -
     * # /[*]IF pmb.isPaging()[*]/
     * # select member.MEMBER_ID
     * #      , member.MEMBER_NAME -- The name of member...    *NG
     * #      -- The status name of member...                  *NG
     * #      , memberStatus.MEMBER_STATUS_NAME
     * # -- ELSE select count(*)
     * # /[*]END[*]/
     * # ...
     * o It's restriction...Sorry
     * </pre>
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL that executes count and paging. (NotNull)
     * @param pmb The bean of paging parameter. (NotNull)
     * @param entityType The type of result entity. (NotNull)
     * @return The result bean of paging. (NotNull)
     * @exception org.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public <ENTITY> PagingResultBean<ENTITY> selectPage(final String path
                                                      , final PagingBean pmb
                                                      , final Class<ENTITY> entityType) {
        final OutsideSqlEntityExecutor<PagingBean> countExecutor = createCountExecutor();
        final PagingHandler<ENTITY> handler = new PagingHandler<ENTITY>() {
            public PagingBean getPagingBean() {
                return pmb;
            }
            public int count() {
                pmb.xsetPaging(false);
                return countExecutor.selectEntityWithDeletedCheck(path, pmb, Integer.class);
            }
            public List<ENTITY> paging() {
                pmb.xsetPaging(true);
                return selectList(path, pmb, entityType);
            }
        };
        final PagingInvoker<ENTITY> invoker = new PagingInvoker<ENTITY>(_tableDbName);
        if (pmb.isCountLater()) {
            invoker.countLater();
        }
        return invoker.invokePaging(handler);
    }

    protected OutsideSqlEntityExecutor<PagingBean> createCountExecutor() {
        final OutsideSqlOption countOption = _outsideSqlOption.copyOptionWithoutPaging();
        return new OutsideSqlEntityExecutor<PagingBean>(_behaviorCommandInvoker, countOption, _tableDbName, _currentDBDef);
    }

    protected void setupScrollableCursorIfNeeds() {
        if (!_outsideSqlOption.isAutoPaging()) {
            return;
        }
        StatementConfig statementConfig = _outsideSqlOption.getStatementConfig();
        if (statementConfig != null && statementConfig.hasResultSetType()) {
            return;
        }
        if (_defaultStatementConfig != null && _defaultStatementConfig.hasResultSetType()) {
            return;
        }
        if (statementConfig == null) {
            statementConfig = new StatementConfig();
            configure(statementConfig);
        }
        statementConfig.typeScrollInsensitive();
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, Object pmb, Class<ENTITY> entityType) {
        return xsetupCommand(new OutsideSqlSelectListCommand<ENTITY>(), path, pmb, entityType);
    }

    private <ENTITY> OutsideSqlSelectListCommand<ENTITY> xsetupCommand(OutsideSqlSelectListCommand<ENTITY> command, String path, Object pmb, Class<ENTITY> entityType) {
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
    public OutsideSqlPagingExecutor configure(StatementConfig statementConfig) {
		_outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }

    public OutsideSqlPagingExecutor dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }
}
