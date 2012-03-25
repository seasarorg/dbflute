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
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected Object _result;
    protected String _tableDbName;
    protected String _commandName;
    protected String _displaySql;

    // -----------------------------------------------------
    //                                            TimeMillis
    //                                            ----------
    // basically NotNull but no guarantee
    protected Long _commandBeforeTimeMillis;
    protected Long _commandAfterTimeMillis;
    protected Long _sqlBeforeTimeMillis;
    protected Long _sqlAfterTimeMillis;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Get the result of SQL execution (mapped to entity if select). <br />
     * @return The instance of result. (NullAllowed)
     */
    public Object getResult() {
        return _result;
    }

    public void setResult(Object result) {
        this._result = result;
    }

    /**
     * Get the table DB name for the behavior command.
     * @return The DB name of table. (NotNull)
     */
    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        this._tableDbName = tableDbName;
    }

    /**
     * Get the name of the behavior command.
     * @return The name of the behavior command. (NotNull)
     */
    public String getCommandName() {
        return _commandName;
    }

    public void setCommandName(String commandName) {
        this._commandName = commandName;
    }

    /**
     * Get the SQL for display. <br />
     * If the statement is batch-update, this value contains only a part of batch statements.
     * @return The string of SQL. (NullAllowed: for example, when batch logging is limited by option)
     */
    public String getDisplaySql() {
        return _displaySql;
    }

    public void setDisplaySql(String displaySql) {
        this._displaySql = displaySql;
    }

    // -----------------------------------------------------
    //                                            TimeMillis
    //                                            ----------
    /**
     * Get the time as millisecond before command invoking (before building SQL clause). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getCommandBeforeTimeMillis() {
        return _commandBeforeTimeMillis;
    }

    public void setCommandBeforeTimeMillis(Long commandInvokeTimeMillis) {
        this._commandBeforeTimeMillis = commandInvokeTimeMillis;
    }

    /**
     * Get the time as millisecond after command invoking (after mapping to entity). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getCommandAfterTimeMillis() {
        return _commandAfterTimeMillis;
    }

    public void setCommandAfterTimeMillis(Long commandAfterTimeMillis) {
        this._commandAfterTimeMillis = commandAfterTimeMillis;
    }

    /**
     * Get the time as millisecond before SQL invoking (after building SQL clause). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getSqlBeforeTimeMillis() {
        return _sqlBeforeTimeMillis;
    }

    public void setSqlBeforeTimeMillis(Long sqlBeforeTimeMillis) {
        this._sqlBeforeTimeMillis = sqlBeforeTimeMillis;
    }

    /**
     * Get the time as millisecond after SQL invoking (before mapping to entity). <br />
     * Basically NotNull but no guarantee, because this is additional info.
     * @return The long value of millisecond. (NullAllowed: basically NotNull but no guarantee)
     */
    public Long getSqlAfterTimeMillis() {
        return _sqlAfterTimeMillis;
    }

    public void setSqlAfterTimeMillis(Long sqlAfterTimeMillis) {
        this._sqlAfterTimeMillis = sqlAfterTimeMillis;
    }
}
