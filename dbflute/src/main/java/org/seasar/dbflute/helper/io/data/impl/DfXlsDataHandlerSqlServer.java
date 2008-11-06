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
package org.seasar.dbflute.helper.io.data.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.util.jdbc.DfConnectionUtil;
import org.seasar.dbflute.util.jdbc.DfDataSourceUtil;

/**
 * @author jflute
 */
public class DfXlsDataHandlerSqlServer extends DfXlsDataHandlerImpl {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Set<String> _identityTableSet = new HashSet<String>();

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    protected void beforeHandlingTable(DataSource dataSource, DataTable dataTable) {
        if (hasIdentityColumn(dataSource, dataTable)) {
            turnOnIdentityInsert(dataSource, dataTable);
            _identityTableSet.add(dataTable.getTableName());
        }
    }

    protected void finallyHandlingTable(DataSource dataSource, DataTable dataTable) {
        if (_identityTableSet.contains(dataTable.getTableName())) {
            turnOffIdentityInsert(dataSource, dataTable);
        }
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    private boolean hasIdentityColumn(DataSource dataSource, final DataTable dataTable) {
        final String sql = "SELECT IDENT_CURRENT ('" + dataTable.getTableName() + "') AS IDENT_CURRENT";
        final Connection conn = DfDataSourceUtil.getConnection(dataSource);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                final Object value = rs.getObject(1);
                return value != null;
            }
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private void turnOnIdentityInsert(DataSource dataSource, final DataTable dataTable) {
        setIdentityInsert(dataSource, dataTable, "ON");
    }

    private void turnOffIdentityInsert(DataSource dataSource, final DataTable dataTable) {
        setIdentityInsert(dataSource, dataTable, "OFF");
    }

    private void setIdentityInsert(DataSource dataSource, final DataTable dataTable, final String command) {
        final String sql = "SET IDENTITY_INSERT " + dataTable.getTableName() + " " + command;
        if (_loggingInsertSql) {
            _log.info(sql);
        }
        final Connection conn = DfDataSourceUtil.getConnection(dataSource);
        try {
            final Statement stmt = DfConnectionUtil.createStatement(conn);
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } finally {
            DfConnectionUtil.close(conn);
        }
    }
}
