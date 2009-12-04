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
        try {
            conn = _dataSource.getConnection();
            Set<Entry<String, String>> entrySet = tableSequenceMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String tableName = entry.getKey();
                String sequenceName = entry.getValue();
                DatabaseMetaData metaData = conn.getMetaData();
                List<String> pkList = uniqueKeyHandler.getPrimaryColumnNameList(metaData, _schema, tableName);
                if (pkList.size() != 1) {
                    skippedMap.put(tableName, pkList);
                    continue;
                }
                String primaryKeyName = pkList.get(0);
                Statement statement = conn.createStatement();
                Integer count = selectCount(statement, tableName);
                if (count == null || count == 0) {
                    // It is not necessary to increment because the table has no data.
                    continue;
                }
                Integer actualValue = selectDataMax(statement, tableName, primaryKeyName);
                if (actualValue == null) {
                    // It is not necessary to increment because the table has no data.
                    continue;
                }
                Integer sequenceValue = selectNextVal(statement, sequenceName);
                Integer startPoint = sequenceValue;
                while (actualValue > sequenceValue) {
                    sequenceValue = selectNextVal(statement, sequenceName);
                }
                _log.info("    " + sequenceName + ": " + startPoint + " to " + sequenceValue);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
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

    protected Integer selectCount(Statement statement, String tableName) throws SQLException {
        ResultSet rs = statement.executeQuery("select count(*) from " + tableName);
        if (!rs.next()) {
            return null;
        }
        return rs.getInt(1);
    }

    protected Integer selectDataMax(Statement statement, String tableName, String primaryKeyName) throws SQLException {
        ResultSet rs = statement.executeQuery("select max(" + primaryKeyName + ") as MAX_VALUE from " + tableName);
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
            msg = msg + " table=" + tableName + " primaryKey=" + primaryKeyName;
            msg = msg + " value=" + value;
            throw new IllegalStateException(msg);
        }
        return actualValue;
    }

    protected abstract Integer selectNextVal(Statement statement, String sequenceName) throws SQLException;

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}