package org.seasar.dbflute.jdbc;

/**
 * The information of execution time.
 * @author jflute
 */
public class ExecutionTimeInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Long _commandBeforeTimeMillis;
    protected final Long _commandAfterTimeMillis;
    protected final Long _sqlBeforeTimeMillis;
    protected final Long _sqlAfterTimeMillis;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ExecutionTimeInfo(Long commandBeforeTimeMillis, Long commandAfterTimeMillis, Long sqlBeforeTimeMillis,
            Long sqlAfterTimeMillis) {
        _commandBeforeTimeMillis = commandBeforeTimeMillis;
        _commandAfterTimeMillis = commandAfterTimeMillis;
        _sqlBeforeTimeMillis = sqlBeforeTimeMillis;
        _sqlAfterTimeMillis = sqlAfterTimeMillis;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the time as millisecond before command invoking (before building SQL clause). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getCommandBeforeTimeMillis() {
        return _commandBeforeTimeMillis;
    }

    /**
     * Get the time as millisecond after command invoking (after mapping to entity). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getCommandAfterTimeMillis() {
        return _commandAfterTimeMillis;
    }

    /**
     * Get the time as millisecond before SQL invoking (after building SQL clause). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getSqlBeforeTimeMillis() {
        return _sqlBeforeTimeMillis;
    }

    /**
     * Get the time as millisecond after SQL invoking (before mapping to entity). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getSqlAfterTimeMillis() {
        return _sqlAfterTimeMillis;
    }
}
