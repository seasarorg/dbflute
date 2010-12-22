/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core.execution;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.dbflute.s2dao.sqlcommand.TnAbstractNodeStaticCommand;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public abstract class AbstractOutsideSqlExecution extends TnAbstractNodeStaticCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _removeBlockComment;
    protected boolean _removeLineComment;
    protected boolean _formatSql;
    protected OutsideSqlFilter _outsideSqlFilter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractOutsideSqlExecution(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    public String filterSql(String sql) {
        if (_outsideSqlFilter != null) {
            sql = _outsideSqlFilter.filterExecution(sql, getOutsideSqlExecutionFilterType());
        }
        if (_removeBlockComment) {
            sql = Srl.removeBlockComment(sql);
        }
        if (_removeLineComment) {
            sql = Srl.removeLineComment(sql);
        }
        if (_formatSql) {
            sql = Srl.removeEmptyLine(sql);
        }
        return sql;
    }

    protected abstract OutsideSqlFilter.ExecutionFilterType getOutsideSqlExecutionFilterType();

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isRemoveBlockComment() {
        return _removeBlockComment;
    }

    public void setRemoveBlockComment(boolean removeBlockComment) {
        this._removeBlockComment = removeBlockComment;
    }

    public boolean isRemoveLineComment() {
        return _removeLineComment;
    }

    public void setRemoveLineComment(boolean removeLineComment) {
        this._removeLineComment = removeLineComment;
    }

    public boolean isRemoveEmptyLine() {
        return _formatSql;
    }

    public void setFormatSql(boolean formatSql) {
        this._formatSql = formatSql;
    }

    public OutsideSqlFilter getOutsideSqlFilter() {
        return _outsideSqlFilter;
    }

    public void setOutsideSqlFilter(OutsideSqlFilter outsideSqlFilter) {
        this._outsideSqlFilter = outsideSqlFilter;
    }
}
