package org.seasar.dbflute.jdbc;

import org.seasar.dbflute.util.DfTraceViewUtil;

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
    //                                                                                View
    //                                                                                ====
    /**
     * The performance view of command invoking. e.g. 01m40s012ms <br />
     * (before building SQL clause after mapping to entity). <br />
     * Basically it returns true but no guarantee, because this is additional info. <br />
     * For example, no-modified-column update execution does not have its SQL execution.
     * @return The view string of command invoking. (NotNull) 
     */
    public String toCommandPerformanceView() {
        return convertToPerformanceView(_commandAfterTimeMillis - _commandBeforeTimeMillis);
    }

    /**
     * The performance view of SQL execution. e.g. 01m40s012ms <br />
     * (from mapping to entity to building SQL clause) <br />
     * Basically NotNull but no guarantee, because this is additional info. <br />
     * For example, no-modified-column update execution does not have its SQL execution. <br />
     * When batch execution, all statements is contained to the time.
     * @return The view string of SQL execution. (NotNull: if no time, returns "*No time")
     */
    public String toSqlPerformanceView() {
        if (hasSqlTimeMillis()) {
            return convertToPerformanceView(_sqlAfterTimeMillis - _sqlBeforeTimeMillis);
        } else {
            return "*No time";
        }
    }

    /**
     * Convert to performance view.
     * @param after_minus_before The difference between before time and after time.
     * @return The view string to show performance. e.g. 01m40s012ms (NotNull)
     */
    protected String convertToPerformanceView(long after_minus_before) {
        return DfTraceViewUtil.convertToPerformanceView(after_minus_before);
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    /**
     * Does it have the time of SQL execution. <br />
     * Basically it returns true but no guarantee, because this is additional info. <br />
     * For example, no-modified-column update execution does not have its SQL execution.
     * @return The determination, true or false. (basically true but no guarantee)
     */
    public boolean hasSqlTimeMillis() {
        return _sqlAfterTimeMillis != null && _sqlBeforeTimeMillis != null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("commandBefore=").append(_commandBeforeTimeMillis);
        sb.append(", commandAfter=").append(_commandAfterTimeMillis);
        sb.append(", sqlBefore=").append(_sqlBeforeTimeMillis);
        sb.append(", sqlAfter=").append(_sqlAfterTimeMillis);
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the time as millisecond before command invoking (before building SQL clause). <br />
     * NotNull when SQL result handler, Null when SQLFireHook.
     * @return The long value of millisecond. (NotNull)
     */
    public Long getCommandBeforeTimeMillis() {
        return _commandBeforeTimeMillis;
    }

    /**
     * Get the time as millisecond after command invoking (after mapping to entity). <br />
     * NotNull when SQL result handler, Null when SQLFireHook.
     * @return The long value of millisecond. (NotNull)
     */
    public Long getCommandAfterTimeMillis() {
        return _commandAfterTimeMillis;
    }

    /**
     * Get the time as millisecond before SQL execution (after building SQL clause). <br />
     * Basically NotNull but no guarantee, because this is additional info. <br />
     * For example, no-modified-column update execution does not have its SQL execution. <br />
     * When batch execution, all statements is contained to the time.
     * @return The long value of millisecond. (basically NotNull but no guarantee)
     */
    public Long getSqlBeforeTimeMillis() {
        return _sqlBeforeTimeMillis;
    }

    /**
     * Get the time as millisecond after SQL execution (before mapping to entity). <br />
     * Basically NotNull but no guarantee, because this is additional info. <br />
     * For example, no-modified-column update execution does not have its SQL execution. <br />
     * When batch execution, all statements is contained to the time. <br />
     * Null in the SQLFireHook's finally call-back when SQL fire failed.
     * @return The long value of millisecond. (basically NotNull but no guarantee)
     */
    public Long getSqlAfterTimeMillis() {
        return _sqlAfterTimeMillis;
    }
}
