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
    public DfSequenceHandlerPostgreSQL(DataSource dataSource, String schema) {
        super(dataSource, schema);
    }

    // ===================================================================================
    //                                                            Serial Sequence Handling
    //                                                            ========================
    @Override
    public void incrementSequenceToDataMax(Map<String, String> tableSequenceMap) {
        super.incrementSequenceToDataMax(tableSequenceMap);
        try {
            handleSerialTypeSequence();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void handleSerialTypeSequence() throws SQLException {
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
        final Connection conn = _dataSource.getConnection();
        final DatabaseMetaData metaData = conn.getMetaData();
        final List<DfTableMetaInfo> tableList = tableHandler.getTableList(metaData, _schema);
        final DfUniqueKeyHandler uniqueKeyHandler = new DfUniqueKeyHandler();
        final DfColumnHandler columnHandler = new DfColumnHandler() {
            @Override
            protected List<String> getRealSimpleColumnExceptList(String schemaName) {
                return new ArrayList<String>(); // All column target!
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
            final String primaryKeyName = pkList.get(0);
            if (!autoIncrementHandler.isAutoIncrementColumn(conn, tableMetaInfo, primaryKeyName)) {
                continue;
            }
            final Map<String, DfColumnMetaInfo> columnMetaMap = columnHandler.getColumnMetaInfo(metaData, _schema,
                    tableName);
            final DfColumnMetaInfo columnMetaInfo = columnMetaMap.get(primaryKeyName);
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
            Statement statement = conn.createStatement();

            Integer count = selectCount(statement, tableName);
            if (count == null || count == 0) {
                // It is not necessary to increment because the table has no data.
                continue;
            }

            String sql = "select setval('" + sequenceName + "', (select max(" + primaryKeyName + ")";
            sql = sql + " from " + tableName + "))";
            _log.info(sql);
            statement.execute(sql);
        }
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    @Override
    protected Integer selectNextVal(Statement statement, String sequenceName) throws SQLException {
        ResultSet rs = statement.executeQuery("select nextval ('" + sequenceName + "')");
        rs.next();
        return rs.getInt(1);
    }
}