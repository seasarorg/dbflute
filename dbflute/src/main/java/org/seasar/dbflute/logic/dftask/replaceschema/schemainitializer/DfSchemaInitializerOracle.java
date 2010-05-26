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
package org.seasar.dbflute.logic.dftask.replaceschema.schemainitializer;

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
        if (!_unifiedSchema.hasSchema()) {
            return;
        }
        final String schema = _unifiedSchema.getPureSchema();
        final List<String> sequenceNameList = new ArrayList<String>();
        final String metaDataSql = "select * from ALL_SEQUENCES where SEQUENCE_OWNER = '" + schema + "'";
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
                final String dropSequenceSql = "drop sequence " + schema + "." + sequenceName;
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
    //                                                                        Drop DB Link
    //                                                                        ============
    @Override
    protected void dropDBLink(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        dropDBLink(conn);
    }

    /**
     * Drop DB links that are private DB links. <br />
     * @param conn The connection to main schema. (NotNull)
     */
    protected void dropDBLink(Connection conn) {
        if (!_unifiedSchema.hasSchema()) {
            return;
        }
        final List<String> dbLinkNameList = new ArrayList<String>();
        final String schema = _unifiedSchema.getPureSchema();
        final String metaDataSql = "select * from ALL_DB_LINKS where OWNER = '" + schema + "'";
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaDataSql);
            rs = st.executeQuery(metaDataSql);
            while (rs.next()) {
                final String dbLinkName = rs.getString("DB_LINK");
                dbLinkNameList.add(dbLinkName);
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