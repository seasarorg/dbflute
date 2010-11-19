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
package org.seasar.dbflute.logic.replaceschema.schemainitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.util.Srl;

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
        doDropSequence(conn);
    }

    protected void doDropSequence(Connection conn) {
        dropDicObject(conn, "sequences", "sequence", "ALL_SEQUENCES", "SEQUENCE_OWNER", "SEQUENCE_NAME", null, true);
    }

    // ===================================================================================
    //                                                                        Drop DB Link
    //                                                                        ============
    @Override
    protected void dropDBLink(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        doDropDBLink(conn);
    }

    /**
     * Drop DB links that are private DB links. <br />
     * @param conn The connection to main schema. (NotNull)
     */
    protected void doDropDBLink(Connection conn) {
        dropDicObject(conn, "DB links", "database link", "ALL_DB_LINKS", "OWNER", "DB_LINK", null, false);
    }

    // ===================================================================================
    //                                                                    Drop Type Object
    //                                                                    ================
    @Override
    protected void dropTypeObject(Connection conn, List<DfTableMetaInfo> tableMetaInfoList) {
        final int retryLimit = 10;
        int retryCount = 0;
        while (true) {
            try {
                doDropTypeObject(conn, "TYPECODE asc");
                break;
            } catch (SQLFailureException e) {
                try {
                    ++retryCount;
                    doDropTypeObject(conn, "TYPECODE desc");
                    break;
                } catch (SQLFailureException ignored) {
                    if (retryLimit >= retryCount) {
                        throw e;
                    }
                    continue;
                }
            }
        }
    }

    protected void doDropTypeObject(Connection conn, String orderBy) {
        dropDicObject(conn, "type objects", "type", "ALL_TYPES", "OWNER", "TYPE_NAME", orderBy, false);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected void dropDicObject(Connection conn, String titleName, String sqlName, String tableName,
            String ownerColumnName, String targetColumnName, String orderBy, boolean schemaPrefix) {
        if (!_unifiedSchema.hasSchema()) {
            return;
        }
        final String schema = _unifiedSchema.getPureSchema();
        final List<String> objectNameList = new ArrayList<String>();
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(tableName);
        sb.append(" where ").append(ownerColumnName).append(" = '").append(schema).append("'");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(orderBy)) {
            sb.append(" order by ").append(orderBy);
        }
        final String metaSql = sb.toString();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            _log.info("...Executing helper SQL:" + ln() + metaSql);
            rs = st.executeQuery(metaSql);
            while (rs.next()) {
                final String objectName = rs.getString(targetColumnName);
                objectNameList.add(objectName);
            }
        } catch (SQLException continued) {
            // if the data dictionary table is not found,
            // it continues because it might be a version difference 
            String msg = "*Failed to the SQL:" + ln();
            msg = msg + (continued.getMessage() != null ? continued.getMessage() : null) + ln();
            msg = msg + metaSql;
            _log.info(metaSql);
            return;
        } finally {
            closeResource(rs, st);
        }
        try {
            st = conn.createStatement();
            for (String objectName : objectNameList) {
                final String prefix = schemaPrefix ? schema + "." : "";
                final String dropSql = "drop " + sqlName + " " + prefix + objectName;
                _log.info(dropSql);
                st.execute(dropSql);
            }
        } catch (SQLException e) {
            String msg = "Failed to drop " + titleName + ": " + objectNameList;
            throw new SQLFailureException(msg, e);
        } finally {
            closeStatement(st);
        }
    }
}