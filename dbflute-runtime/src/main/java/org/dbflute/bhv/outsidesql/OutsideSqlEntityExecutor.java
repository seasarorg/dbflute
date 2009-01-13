package org.dbflute.bhv.outsidesql;

import java.util.List;

import org.dbflute.DBDef;
import org.dbflute.bhv.core.BehaviorCommand;
import org.dbflute.bhv.core.BehaviorCommandInvoker;
import org.dbflute.bhv.core.command.OutsideSqlSelectListCommand;
import org.dbflute.cbean.ConditionBeanContext;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.outsidesql.OutsideSqlOption;
import org.dbflute.util.SimpleSystemUtil;


/**
 * The cursor executor of outside-SQL.
 * @param <PARAMETER_BEAN> The type of parameter-bean.
 * @author DBFlute(AutoGenerator)
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
    public OutsideSqlEntityExecutor(BehaviorCommandInvoker behaviorCommandInvoker
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
     * Select entity.
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntity(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final List<ENTITY> ls = invoke(createSelectListCommand(path, pmb, entityType));
        if (ls.isEmpty()) {
            return null;
        }
        if (ls.size() > 1) {
            throwEntityDuplicatedException(ls.size() + "", buildSearch4LogString(path, pmb, entityType), null);
        }
        return ls.get(0);
    }

    /**
     * Select entity with deleted check.
     * @param <ENTITY> The type of entity.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The type of entity. (NotNull)
     * @return The selected entity. (Nullable)
     * @exception org.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted(not found).
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity is duplicated.
     */
    public <ENTITY> ENTITY selectEntityWithDeletedCheck(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        final List<ENTITY> ls = invoke(createSelectListCommand(path, pmb, entityType));
        if (ls == null || ls.isEmpty()) {
            throwEntityAlreadyDeletedException(buildSearch4LogString(path, pmb, entityType));
        }
        if (ls.size() > 1) {
            throwEntityDuplicatedException(ls.size() + "", buildSearch4LogString(path, pmb, entityType), null);
        }
        return ls.get(0);
    }

    protected <ENTITY> String buildSearch4LogString(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        String tmp = "Table  = " + _outsideSqlOption.getTableDbName() + getLineSeparator();
        tmp = tmp + "Path   = " + path + getLineSeparator();
        tmp = tmp + "Pmb    = " + (pmb != null ? pmb.getClass().getSimpleName() : "null") + ":" + pmb + getLineSeparator();
        tmp = tmp + "Entity = " + (entityType != null ? entityType.getSimpleName() : "null")  + getLineSeparator();
        tmp = tmp + "Option = " + _outsideSqlOption;
        return tmp;
    }

    // -----------------------------------------------------
    //                                                Helper
    //                                                ------
    protected void throwEntityAlreadyDeletedException(Object searchKey4Log) {
        ConditionBeanContext.throwEntityAlreadyDeletedException(searchKey4Log);
    }

    protected void throwEntityDuplicatedException(String resultCountString, Object searchKey4Log, Throwable cause) {
        ConditionBeanContext.throwEntityDuplicatedException(resultCountString, searchKey4Log, cause);
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected <ENTITY> BehaviorCommand<List<ENTITY>> createSelectListCommand(String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
        return xsetupCommand(new OutsideSqlSelectListCommand<ENTITY>(), path, pmb, entityType);
    }

    private <ENTITY> OutsideSqlSelectListCommand<ENTITY> xsetupCommand(OutsideSqlSelectListCommand<ENTITY> command, String path, PARAMETER_BEAN pmb, Class<ENTITY> entityType) {
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
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> configure(StatementConfig statementConfig) {
		_outsideSqlOption.setStatementConfig(statementConfig);
        return this;
    }
	
    public OutsideSqlEntityExecutor<PARAMETER_BEAN> dynamicBinding() {
        _outsideSqlOption.dynamicBinding();
        return this;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String getLineSeparator() {
        return SimpleSystemUtil.getLineSeparator();
    }
}
