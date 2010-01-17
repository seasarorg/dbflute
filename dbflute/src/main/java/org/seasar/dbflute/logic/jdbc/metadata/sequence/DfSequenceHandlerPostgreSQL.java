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
package org.seasar.dbflute.logic.jdbc.metadata.sequence;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.logic.jdbc.handler.DfAutoIncrementHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfTableHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * @author jflute
 * @since 0.9.5.2 (2009/07/09 Thursday)
 */
public class DfSequenceHandlerPostgreSQL extends DfSequenceHandlerJdbc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceHandlerPostgreSQL.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceHandlerPostgreSQL(DataSource dataSource, String schema, List<String> allSchemaList) {
        super(dataSource, schema, allSchemaList);
    }

    // ===================================================================================
    //                                                            Serial Sequence Handling
    //                                                            ========================
    @Override
    public void incrementSequenceToDataMax(Map<String, String> tableSequenceMap) {
        super.incrementSequenceToDataMax(tableSequenceMap);
        try {
            handleSerialTypeSequence(tableSequenceMap);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void handleSerialTypeSequence(Map<String, String> tableSequenceMap) throws SQLException {
        final DfTableHandler tableHandler = new DfTableHandler() {
            @Override
            protected List<String> getRealTableExceptList(String schemaName) {
                return new ArrayList<String>(); // All table target!
            }

            @Override
            protected List<String> getRealTableTargetList(String schemaName) {
                return new ArrayList<String>(); // All table target!
            }
        };
        Connection conn = null;
        Statement st = null;
        try {
            conn = _dataSource.getConnection();
            st = conn.createStatement();
            final DatabaseMetaData metaData = conn.getMetaData();
            final List<DfTableMetaInfo> tableList = tableHandler.getTableList(metaData, _schema);
            final DfUniqueKeyHandler uniqueKeyHandler = new DfUniqueKeyHandler();
            final DfColumnHandler columnHandler = new DfColumnHandler() {
                @Override
                protected Map<String, List<String>> getRealColumnExceptMap(String schemaName) {
                    return new HashMap<String, List<String>>(); // all column target
                }
            };
            final DfAutoIncrementHandler autoIncrementHandler = new DfAutoIncrementHandler();
            _log.info("...Incrementing serial type sequence");
            for (DfTableMetaInfo tableMetaInfo : tableList) {
                final String tableName = tableMetaInfo.getTableName();
                final DfPrimaryKeyMetaInfo pkInfo = uniqueKeyHandler.getPrimaryKey(metaData, _schema, tableMetaInfo);
                final List<String> pkList = pkInfo.getPrimaryKeyList();
                if (pkList.size() != 1) {
                    continue;
                }
                final String primaryKeyColumnName = pkList.get(0);
                if (!autoIncrementHandler.isAutoIncrementColumn(conn, tableMetaInfo, primaryKeyColumnName)) {
                    continue;
                }
                final Map<String, DfColumnMetaInfo> columnMetaMap = columnHandler.getColumnMetaInfo(metaData, _schema,
                        tableName);
                final DfColumnMetaInfo columnMetaInfo = columnMetaMap.get(primaryKeyColumnName);
                final String defaultValue = columnMetaInfo.getDefaultValue();
                if (defaultValue == null) {
                    continue;
                }
                final String prefix = "nextval('";
                if (!defaultValue.startsWith(prefix)) {
                    continue;
                }
                final String excludedPrefixString = defaultValue.substring(prefix.length());
                final int endIndex = excludedPrefixString.indexOf("'");
                if (endIndex < 0) {
                    continue;
                }
                final String sequenceName = excludedPrefixString.substring(0, endIndex);
                if (tableSequenceMap.containsKey(sequenceName)) {
                    continue; // already incremented
                }

                final Integer count = selectCount(st, tableName);
                if (count == null || count == 0) {
                    // It is not necessary to increment because the table has no data.
                    continue;
                }
                final Integer actualValue = selectDataMax(st, tableName, primaryKeyColumnName);
                if (actualValue == null) {
                    // It is not necessary to increment because the table has no data.
                    continue;
                }
                callSequenceLoop(st, sequenceName, actualValue);

                // *It's an old style.
                //String sql = "select setval('" + sequenceName + "', (select max(" + primaryKeyColumnName + ")";
                //sql = sql + " from " + tableName + "))";
                //_log.info(sql);
                //st.execute(sql);
            }
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                    _log.info("Statement.close() threw the exception!", ignored);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("Connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    @Override
    protected Integer selectNextVal(Statement st, String sequenceName) throws SQLException {
        ResultSet rs = null;
        try {
            rs = st.executeQuery("select nextval ('" + sequenceName + "')");
            rs.next();
            return rs.getInt(1);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("ResultSet.close() threw the exception!", ignored);
                }
            }
        }
    }
}