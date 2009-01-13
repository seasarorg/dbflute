package org.dbflute.bhv.outsidesql;

import org.dbflute.DBDef;
import org.dbflute.bhv.core.BehaviorCommand;
import org.dbflute.bhv.core.BehaviorCommandInvoker;
import org.dbflute.bhv.core.command.OutsideSqlSelectCursorCommand;
import org.dbflute.jdbc.CursorHandler;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.outsidesql.OutsideSqlOption;

/**
 * The cursor executor of outside-SQL.
 * @param <PARAMETER_BEAN> The type of parameter-bean.
 * @author DBFlute(AutoGenerator)
 */
public class OutsideSqlCursorExecutor<PARAMETER_BEAN> {

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
    public OutsideSqlCursorExecutor(BehaviorCommandInvoker behaviorCommandInvoker
                                  , OutsideSqlOption outsideSqlOption
                                  , String tableDbName
                                  , DBDef currentDBDef) {
        this._behaviorCommandInvoker = behaviorCommandInvoker;
        this._outsideSqlOption = outsideSqlOption;
        this._tableDbName = tableDbName;
        this._currentDBDef = currentDBDef;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    /**
     * Select the cursor of the entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param handler The handler of cursor. (NotNull)
     * @return The result object that the cursor handler returns. (Nullable)
     * @exception org.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     */
    public Object selectCursor(String path, PARAMETER_BEAN pmb, CursorHandler handler) {
        return invoke(createSelectCursorCommand(path, pmb, handler));
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected BehaviorCommand<Object> createSelectCursorCommand(String path, PARAMETER_BEAN pmb, CursorHandler handler) {
        return xsetupCommand(new OutsideSqlSelectCursorCommand(), path, pmb, handler);
    }

    private OutsideSqlSelectCursorCommand xsetupCommand(OutsideSqlSelectCursorCommand command, String path, PARAMETER_BEAN pmb, CursorHandler handler) {
        command.setTableDbName(_tableDbName);
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setOutsideSqlPath(path);
        command.setParameterBean(pmb);
        command.setOutsideSqlOption(_outsideSqlOption);
        command.setCurrentDBDef(_currentDBDef);
        command.setCursorHandler(handler);
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
    public OutsideSqlCursorExecutor<PARAMETER_BEAN> dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }

    public OutsideSqlCursorExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
		_outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }
}
