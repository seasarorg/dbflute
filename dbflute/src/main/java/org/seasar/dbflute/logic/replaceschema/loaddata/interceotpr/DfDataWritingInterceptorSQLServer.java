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
package org.seasar.dbflute.logic.replaceschema.loaddata.interceotpr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

/**
 * @author jflute
 */
public class DfDataWritingInterceptorSQLServer implements DfDataWritingInterceptor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDataWritingInterceptorSQLServer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected boolean _loggingSql;
    protected final Set<String> _identityTableSet = StringSet.createAsFlexible();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataWritingInterceptorSQLServer(DataSource dataSource, boolean loggingSql) {
        _dataSource = dataSource;
        _loggingSql = loggingSql;
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    public void processBeforeHandlingTable(String tableName, Map<String, DfColumnMetaInfo> columnMap) {
        if (hasIdentityColumn(_dataSource, tableName, columnMap)) {
            turnOnIdentityInsert(_dataSource, tableName);
            _identityTableSet.add(tableName);
        }
    }

    public void processFinallyHandlingTable(String tableName, Map<String, DfColumnMetaInfo> columnMap) {
        if (_identityTableSet.contains(tableName)) {
            turnOffIdentityInsert(_dataSource, tableName);
        }
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    protected boolean hasIdentityColumn(DataSource dataSource, String tableName, Map<String, DfColumnMetaInfo> columnMap) {
        final String sql = "select ident_current ('" + tableName + "') as IDENT_CURRENT";
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

    protected void turnOnIdentityInsert(DataSource dataSource, String tableName) {
        setIdentityInsert(dataSource, tableName, true);
    }

    protected void turnOffIdentityInsert(DataSource dataSource, String tableName) {
        setIdentityInsert(dataSource, tableName, false);
    }

    protected void setIdentityInsert(DataSource dataSource, String tableName, boolean insertOn) {
        final String sql = buildIdentityInsertSettingSql(tableName, insertOn);
        if (_loggingSql) {
            _log.info(sql);
        }
        Connection conn = null;
        try {
            conn = getConnection(dataSource);
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

    protected String buildIdentityInsertSettingSql(String tableName, boolean insertOn) {
        return "set identity_insert " + tableName + " " + (insertOn ? "on" : "off");
    }

    protected static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected static Statement createStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isLoggingInsertSql() {
        return _loggingSql;
    }

    public void setLoggingInsertSql(boolean loggingSql) {
        this._loggingSql = loggingSql;
    }
}
