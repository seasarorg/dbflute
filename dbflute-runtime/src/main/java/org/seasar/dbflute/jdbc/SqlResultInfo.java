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
package org.seasar.dbflute.jdbc;

/**
 * The information of SQL result.
 * @author jflute
 */
public class SqlResultInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _result;
    protected String _tableDbName;
    protected String _commandName;
    protected String _displaySql;
    protected long _before;
    protected long _after;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getResult() {
        return _result;
    }

    public void setResult(Object result) {
        this._result = result;
    }

    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        this._tableDbName = tableDbName;
    }

    public String getCommandName() {
        return _commandName;
    }

    public void setCommandName(String commandName) {
        this._commandName = commandName;
    }

    public String getDisplaySql() {
        return _displaySql;
    }

    public void setDisplaySql(String displaySql) {
        this._displaySql = displaySql;
    }

    public long getBefore() {
        return _before;
    }

    public void setBefore(long before) {
        this._before = before;
    }

    public long getAfter() {
        return _after;
    }

    public void setAfter(long after) {
        this._after = after;
    }
}
