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
package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/31 Tuesday)
 */
public class DfSqlFileRunnerResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected File _srcFile;
    protected List<ErrorContinuedSql> _errorContinuedSqlList = new ArrayList<ErrorContinuedSql>();
    protected int _goodSqlCount = 0;
    protected int _totalSqlCount = 0;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSqlFileRunnerResult() {
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public void clear() {
        _srcFile = null;
        _errorContinuedSqlList.clear();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public File getSrcFile() {
        return _srcFile;
    }

    public void setSrcFile(File srcFile) {
        _srcFile = srcFile;
    }

    public List<ErrorContinuedSql> getErrorContinuedSqlList() {
        return _errorContinuedSqlList;
    }

    public void addErrorContinuedSql(SQLException e, String sql) {
        final ErrorContinuedSql errorContinuedSql = new ErrorContinuedSql();
        errorContinuedSql.setSql(sql);
        errorContinuedSql.setSqlEx(e);
        _errorContinuedSqlList.add(errorContinuedSql);
    }

    public static class ErrorContinuedSql {
        protected String _sql;
        protected SQLException _sqlEx;

        public String getSql() {
            return _sql;
        }

        public void setSql(String sql) {
            this._sql = sql;
        }

        public SQLException getSqlEx() {
            return _sqlEx;
        }

        public void setSqlEx(SQLException ex) {
            _sqlEx = ex;
        }
    }

    public int getGoodSqlCount() {
        return _goodSqlCount;
    }

    public void setGoodSqlCount(int goodSqlCount) {
        this._goodSqlCount = goodSqlCount;
    }

    public int getTotalSqlCount() {
        return _totalSqlCount;
    }

    public void setTotalSqlCount(int totalSqlCount) {
        this._totalSqlCount = totalSqlCount;
    }
}
