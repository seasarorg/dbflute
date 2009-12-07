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
package org.seasar.dbflute.logic.schemainitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * The schema initializer for Oracle.
 * @author jflute
 * @since 0.8.0 (2008/09/05 Friday)
 */
public class DfSchemaInitializerOracle extends DfSchemaInitializerJdbc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSchemaInitializerOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected StringSet _droppedPackageSet = StringSet.createAsCaseInsensitive();

    // ===================================================================================
    //                                                                    Drop Foreign Key
    //                                                                    ================
    @Override
    protected boolean isSkipDropForeignKey(DfTableMetaInfo tableMetaInfo) {
        return tableMetaInfo.isTableTypeSynonym();
    }

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    @Override
    protected void setupDropTable(StringBuilder sb, DfTableMetaInfo metaInfo) {
        if (metaInfo.isTableTypeSynonym()) {
            final String tableName = filterTableName(metaInfo.getTableName());
            sb.append("drop synonym ").append(tableName);
        } else {
            super.setupDropTable(sb, metaInfo);
        }
    }

    // ===================================================================================
    //                                                                       Drop Sequence
    //                                                                       =============
    @Override
    protected void dropSequence(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        dropSequence(conn);
    }

    protected void dropSequence(Connection conn) {
        final List<String> sequenceNameList = new ArrayList<String>();
        final String metaDataSql = "select * from ALL_SEQUENCES where SEQUENCE_OWNER = '" + _schema + "'";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaDataSql);
            rs = st.executeQuery(metaDataSql);
            while (rs.next()) {
                final String sequenceName = rs.getString("SEQUENCE_NAME");
                sequenceNameList.add(sequenceName);
            }
        } catch (SQLException continued) {
            String msg = "*Failed to the SQL:" + ln();
            msg = msg + (continued.getMessage() != null ? continued.getMessage() : null) + ln();
            msg = msg + metaDataSql;
            _log.info(metaDataSql);
            return;
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
        }
        try {
            st = conn.createStatement();
            for (String sequenceName : sequenceNameList) {
                final String dropSequenceSql = "drop sequence " + _schema + "." + sequenceName;
                _log.info(dropSequenceSql);
                st.execute(dropSequenceSql);
            }
        } catch (SQLException e) {
            String msg = "Failed to drop sequences: " + sequenceNameList;
            throw new IllegalStateException(msg, e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      Drop Procedure
    //                                                                      ==============
    @Override
    protected void handlePackageProcedure(DfProcedureMetaInfo metaInfo, Statement st, SQLException e)
            throws SQLException {
        final String catalog = metaInfo.getProcedureCatalog();
        if (catalog == null || catalog.trim().length() == 0) {
            throw e; // not package so pure exception
        }
        if (_droppedPackageSet.contains(catalog)) {
            _log.info("  (o) already dropped: " + catalog);
            return; // already dropped
        }
        final String sql = "drop package " + catalog; // a catalog is package if Oracle
        try {
            st.execute(sql);
            _log.info("  (o) retry: " + sql);
            _droppedPackageSet.add(catalog);
        } catch (SQLException ignored) {
            _log.info("  (x) retry: " + sql);
            throw e;
        }
    }

    // ===================================================================================
    //                                                                        Drop DB Link
    //                                                                        ============
    @Override
    protected void dropDBLink(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        dropDBLink(conn);
    }

    protected void dropDBLink(Connection conn) {
        final List<String> dbLinkNameList = new ArrayList<String>();
        final List<String> publicDBLinkNameList = new ArrayList<String>();
        final String metaDataSql = "select * from ALL_DB_LINKS where OWNER = '" + _schema + "'";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaDataSql);
            rs = st.executeQuery(metaDataSql);
            while (rs.next()) {
                final String dbLinkName = rs.getString("DB_LINK");
                final String userName = rs.getString("USERNAME");
                if (userName != null && userName.trim().length() > 0) {
                    dbLinkNameList.add(dbLinkName);
                } else {
                    publicDBLinkNameList.add(dbLinkName);
                }
            }
        } catch (SQLException continued) {
            String msg = "*Failed to the SQL:" + ln();
            msg = msg + (continued.getMessage() != null ? continued.getMessage() : null) + ln();
            msg = msg + metaDataSql;
            _log.info(metaDataSql);
            return;
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
        }
        try {
            st = conn.createStatement();
            for (String dbLinkName : dbLinkNameList) {
                final String dropDbLinkSql = "drop database link " + dbLinkName;
                _log.info(dropDbLinkSql);
                st.execute(dropDbLinkSql);
            }
            for (String dbLinkName : publicDBLinkNameList) {
                String dropDbLinkSql = "drop database link " + dbLinkName;
                _log.info(dropDbLinkSql);
                try {
                    st.execute(dropDbLinkSql);
                } catch (SQLException e) {
                    try {
                        // retry with 'public' option
                        dropDbLinkSql = "drop public database link " + dbLinkName;
                        st.execute(dropDbLinkSql);
                        _log.info("  (o) retry: " + dropDbLinkSql);
                    } catch (SQLException ignored) {
                        _log.info("  (x) retry: " + dropDbLinkSql);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop DB links: " + dbLinkNameList;
            throw new IllegalStateException(msg, e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
        }
    }
}