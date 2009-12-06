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
    protected void dropTable(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        super.dropTable(conn, tableMetaInfoList);
        dropSequence(conn);
        dropDbLink(conn);
    }

    @Override
    protected void setupDropTable(StringBuilder sb, DfTableMetaInfo metaInfo) {
        if (metaInfo.isTableTypeSynonym()) {
            final String tableName = filterTableName(metaInfo.getTableName());
            sb.append("drop synonym ").append(tableName);
        } else {
            super.setupDropTable(sb, metaInfo);
        }
    }

    protected void dropSequence(Connection conn) {
        final List<String> sequenceNameList = new ArrayList<String>();
        final String metaDataSql = "select * from ALL_SEQUENCES where SEQUENCE_OWNER = '" + _schema + "'";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaDataSql);
            rs = statement.executeQuery(metaDataSql);
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
            if (statement != null) {
                try {
                    statement.close();
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
            statement = conn.createStatement();
            for (String sequenceName : sequenceNameList) {
                final String dropSequenceSql = "drop sequence " + _schema + "." + sequenceName;
                _log.info(dropSequenceSql);
                statement.execute(dropSequenceSql);
            }
        } catch (SQLException e) {
            String msg = "Failed to drop sequences: " + sequenceNameList;
            throw new IllegalStateException(msg, e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
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

    protected void dropDbLink(Connection conn) {
        final List<String> dbLinkNameList = new ArrayList<String>();
        final List<String> publicDbLinkNameList = new ArrayList<String>();
        final String metaDataSql = "select * from ALL_DB_LINKS where OWNER = '" + _schema + "'";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaDataSql);
            rs = statement.executeQuery(metaDataSql);
            while (rs.next()) {
                final String dbLinkName = rs.getString("DB_LINK");
                final String userName = rs.getString("USERNAME");
                if (userName != null && userName.trim().length() > 0) {
                    dbLinkNameList.add(dbLinkName);
                } else {
                    publicDbLinkNameList.add(dbLinkName);
                }
            }
        } catch (SQLException continued) {
            String msg = "*Failed to the SQL:" + ln();
            msg = msg + (continued.getMessage() != null ? continued.getMessage() : null) + ln();
            msg = msg + metaDataSql;
            _log.info(metaDataSql);
            return;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
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
            statement = conn.createStatement();
            for (String dbLinkName : dbLinkNameList) {
                final String dropDbLinkSql = "drop database link " + _schema + "." + dbLinkName;
                _log.info(dropDbLinkSql);
                statement.execute(dropDbLinkSql);
            }
            for (String dbLinkName : publicDbLinkNameList) {
                String dropDbLinkSql = "drop public database link " + _schema + "." + dbLinkName;
                _log.info(dropDbLinkSql);
                try {
                    statement.execute(dropDbLinkSql);
                } catch (SQLException e) {
                    try {
                        dropDbLinkSql = "drop database link " + _schema + "." + dbLinkName;
                        statement.execute(dropDbLinkSql);
                        _log.info("  --> (o) " + dropDbLinkSql);
                    } catch (SQLException ignored) {
                        _log.info("  --> (x) " + dropDbLinkSql);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            String msg = "Failed to drop DB links: " + dbLinkNameList;
            throw new IllegalStateException(msg, e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
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