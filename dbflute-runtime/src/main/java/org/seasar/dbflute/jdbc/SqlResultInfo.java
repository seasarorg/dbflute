/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.jdbc;

/**
 * The information of SQL result.
 * @author jflute
 */
public class SqlResultInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Object _result;
    protected final String _tableDbName;
    protected final String _commandName;
    protected final SqlLogInfo _sqlLogInfo;
    protected final ExecutionTimeInfo _executionTimeInfo;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlResultInfo(Object result, String tableDbName, String commandName, SqlLogInfo sqlLogInfo,
            ExecutionTimeInfo millisInfo) {
        _result = result;
        _tableDbName = tableDbName;
        _commandName = commandName;
        _sqlLogInfo = sqlLogInfo;
        _executionTimeInfo = millisInfo;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the result of SQL execution (mapped to entity if select). <br />
     * @return The instance of result. (NullAllowed)
     */
    public Object getResult() {
        return _result;
    }

    /**
     * Get the table DB name for the behavior command.
     * @return The DB name of table. (NotNull)
     */
    public String getTableDbName() {
        return _tableDbName;
    }

    /**
     * Get the name of the behavior command.
     * @return The name of the behavior command. (NotNull)
     */
    public String getCommandName() {
        return _commandName;
    }

    /**
     * Get the information of SQL info. <br />
     * <pre>
     * [SqlLogInfo]
     * o executedSql : The actually-executed SQL, which JDBC can analyze. (NotNull)
     * o bindArgs : The argument values of bind variables. (NotNull, EmptyAllowed)
     * o bindArgTypes : The argument types of bind variables. (NotNull, EmptyAllowed)
     * o displaySql : The SQL string for display, bind variables are embedded. (NotNull)
     * </pre>
     * @return The information of SQL info. (NotNull) 
     */
    public SqlLogInfo getSqlLogInfo() {
        return _sqlLogInfo;
    }

    /**
     * Get the information of execution time.
     * <pre>
     * [SqlLogInfo]
     * o commandBeforeTimeMillis : The time as millisecond before command invoking (before building SQL clause). (NotNull)
     * o commandAfterTimeMillis : The time as millisecond after command invoking (after mapping to entity). (NotNull, EmptyAllowed)
     * o sqlBeforeTimeMillis : The time as millisecond before SQL invoking (after building SQL clause). (NotNull, EmptyAllowed)
     * o sqlAfterTimeMillis : The time as millisecond after SQL invoking (before mapping to entity). (NotNull)
     * </pre>
     * @return The information of execution time. (NotNull)
     */
    public ExecutionTimeInfo getExecutionTimeInfo() {
        return _executionTimeInfo;
    }
}
