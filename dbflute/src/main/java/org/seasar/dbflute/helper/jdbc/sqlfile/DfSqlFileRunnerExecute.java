/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;

/**
 * @author jflute
 */
public class DfSqlFileRunnerExecute extends DfSqlFileRunnerBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSqlFileRunnerExecute.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileRunnerExecute(DfRunnerInformation runInfo, DataSource dataSource) {
        super(runInfo, dataSource);
    }

    // ===================================================================================
    //                                                                         Execute SQL
    //                                                                         ===========
    /**
     * Execute the SQL statement.
     * @param statement Statement. (NotNull)
     * @param sql SQL. (NotNull)
     */
    protected void execSQL(Statement statement, String sql) {
        try {
            statement.execute(sql);
            _goodSqlCount++;
        } catch (SQLException e) {
            if (!_runInfo.isErrorContinue()) {
                String msg = "Look! Read the message below." + getLineSeparator();
                msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
                msg = msg + "It failed to execute the SQL!" + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQL File]" + getLineSeparator();
                msg = msg + _srcFile + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[Executed SQL]" + getLineSeparator();
                msg = msg + sql + getLineSeparator();
                msg = msg + getLineSeparator();
                msg = msg + "[SQLException]" + getLineSeparator();
                msg = msg + e.getMessage() + getLineSeparator();
                msg = msg + "* * * * * * * * * */";
                throw new DfSQLExecutionFailureException(msg, e);
            }
            _log.warn("Failed to execute: " + sql, e);
            _log.warn("" + System.getProperty("line.separator"));
        }
    }

    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
