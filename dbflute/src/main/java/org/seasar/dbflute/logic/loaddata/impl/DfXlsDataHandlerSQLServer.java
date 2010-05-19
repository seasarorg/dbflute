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
package org.seasar.dbflute.logic.loaddata.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.helper.dataset.DfDataTable;

/**
 * @author jflute
 */
public class DfXlsDataHandlerSQLServer extends DfXlsDataHandlerImpl {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSeparatedDataHandlerImpl.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Set<String> _identityTableSet = StringSet.createAsFlexible();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfXlsDataHandlerSQLServer(DataSource dataSource) {
        super(dataSource);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    protected void beforeHandlingTable(DataSource dataSource, DfDataTable dataTable) {
        if (hasIdentityColumn(dataSource, dataTable)) {
            turnOnIdentityInsert(dataSource, dataTable);
            _identityTableSet.add(dataTable.getTableName());
        }
    }

    protected void finallyHandlingTable(DataSource dataSource, DfDataTable dataTable) {
        if (_identityTableSet.contains(dataTable.getTableName())) {
            turnOffIdentityInsert(dataSource, dataTable);
        }
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    private boolean hasIdentityColumn(DataSource dataSource, final DfDataTable dataTable) {
        final String sql = "SELECT IDENT_CURRENT ('" + dataTable.getTableName() + "') AS IDENT_CURRENT";
        final Connection conn = getConnection(dataSource);
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

    private void turnOnIdentityInsert(DataSource dataSource, final DfDataTable dataTable) {
        setIdentityInsert(dataSource, dataTable, "ON");
    }

    private void turnOffIdentityInsert(DataSource dataSource, final DfDataTable dataTable) {
        setIdentityInsert(dataSource, dataTable, "OFF");
    }

    private void setIdentityInsert(DataSource dataSource, final DfDataTable dataTable, final String command) {
        final String sql = "SET IDENTITY_INSERT " + dataTable.getTableName() + " " + command;
        if (_loggingInsertSql) {
            _log.info(sql);
        }
        final Connection conn = getConnection(dataSource);
        try {
            final Statement stmt = createStatement(conn);
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
            close(conn);
        }
    }

    private static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Statement createStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
