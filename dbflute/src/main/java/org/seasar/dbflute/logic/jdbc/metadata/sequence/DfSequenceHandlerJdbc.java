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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;

/**
 * @author jflute
 * @since 0.9.5.2 (2009/07/09 Thursday)
 */
public abstract class DfSequenceHandlerJdbc implements DfSequenceHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceHandlerJdbc.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected String _schema;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceHandlerJdbc(DataSource dataSource, String schema) {
        _dataSource = dataSource;
        _schema = schema;
    }

    // ===================================================================================
    //                                                                  Increment Sequence
    //                                                                  ==================
    public void incrementSequenceToDataMax(Map<String, String> tableSequenceMap) {
        DfUniqueKeyHandler uniqueKeyHandler = new DfUniqueKeyHandler();
        Map<String, List<String>> skippedMap = new LinkedHashMap<String, List<String>>();
        _log.info("...Incrementing sequences to max value of table data");
        Connection conn = null;
        Statement st = null;
        try {
            conn = _dataSource.getConnection();
            st = conn.createStatement();
            final Set<Entry<String, String>> entrySet = tableSequenceMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                final String tableName = entry.getKey();
                final String sequenceName = entry.getValue();
                final DatabaseMetaData metaData = conn.getMetaData();
                final DfPrimaryKeyMetaInfo pkInfo = uniqueKeyHandler.getPrimaryKey(metaData, _schema, tableName);
                final List<String> pkList = pkInfo.getPrimaryKeyList();
                if (pkList.size() != 1) {
                    skippedMap.put(tableName, pkList);
                    continue;
                }
                final String primaryKeyColumnName = pkList.get(0);
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
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
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
        if (!skippedMap.isEmpty()) {
            _log.info("*Skipped incrementing sequences(Not PK Only One):");
            Set<Entry<String, List<String>>> skippedEntrySet = skippedMap.entrySet();
            for (Entry<String, List<String>> skippedEntry : skippedEntrySet) {
                String tableName = skippedEntry.getKey();
                List<String> pkList = skippedEntry.getValue();
                _log.info("    " + tableName + ": pk=" + pkList);
            }
        }
    }

    protected void callSequenceLoop(Statement st, String sequenceName, Integer actualValue) throws SQLException {
        Integer sequenceValue = selectNextVal(st, sequenceName);
        final Integer startPoint = sequenceValue;
        while (actualValue > sequenceValue) {
            sequenceValue = selectNextVal(st, sequenceName);
        }
        _log.info("    " + sequenceName + ": " + startPoint + " to " + sequenceValue);
    }

    protected Integer selectCount(Statement statement, String tableName) throws SQLException {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("select count(*) from " + tableName);
            if (!rs.next()) {
                return null;
            }
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

    protected Integer selectDataMax(Statement statement, String tableName, String primaryKeyColumnName)
            throws SQLException {
        final String sql = "select max(" + primaryKeyColumnName + ") as MAX_VALUE from " + tableName;
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            if (!rs.next()) {
                return null;
            }
            String value = rs.getString(1);
            if (value == null) {
                return null;
            }
            Integer actualValue;
            try {
                actualValue = Integer.valueOf(value);
            } catch (NumberFormatException e) {
                String msg = "The type of primary key related to sequece should be Number:";
                msg = msg + " table=" + tableName + " primaryKey=" + primaryKeyColumnName;
                msg = msg + " value=" + value;
                throw new IllegalStateException(msg);
            }
            return actualValue;
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

    protected abstract Integer selectNextVal(Statement statement, String sequenceName) throws SQLException;

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}