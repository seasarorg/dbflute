/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.metahandler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;

/**
 * This class generates an XML schema of an existing database from JDBC meta data.
 * @author jflute
 */
public class DfTableHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfTableHandler.class);

    // ===================================================================================
    //                                                                        Meta Getting
    //                                                                        ============
    /**
     * Get all the table names in the current database that are not system tables.
     * @param dbMeta JDBC database meta data. (NotNull)
     * @param schemaName The name of schema. (Nullable)
     * @return The list of all the table meta info in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableList(DatabaseMetaData dbMeta, String schemaName) throws SQLException {
        schemaName = filterSchemaName(schemaName);
        final String[] objectTypes = getRealObjectTypeTargetArray(schemaName);
        final List<DfTableMetaInfo> tableList = new ArrayList<DfTableMetaInfo>();
        ResultSet resultSet = null;
        try {
            _log.info("$ ...Getting tables: schema=" + schemaName + " objectTypes=" + Arrays.asList(objectTypes));
            resultSet = dbMeta.getTables(null, schemaName, "%", objectTypes);
            while (resultSet.next()) {
                final String tableName = resultSet.getString(3);
                final String tableType = resultSet.getString(4);
                final String tableSchema = resultSet.getString("TABLE_SCHEM");
                final String tableComment = resultSet.getString("REMARKS");

                if (isTableExcept(schemaName, tableName)) {
                    _log.info("$ " + tableName + " is excepted!");
                    continue;
                }
                if (isOracle() && tableName.startsWith("BIN$")) {
                    _log.info("$ " + tableName + " is excepted! {Forced}");
                    continue;
                }

                final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
                tableMetaInfo.setTableName(tableName);
                tableMetaInfo.setTableType(tableType);
                tableMetaInfo.setTableSchema(tableSchema);
                tableMetaInfo.setTableComment(tableComment);
                tableList.add(tableMetaInfo);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }

        resolveSameNameTable(tableList);
        return tableList;
    }

    protected String[] getRealObjectTypeTargetArray(String schemaName) {
        if (schemaName != null) {
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = additionalSchemaMap.get(schemaName);
            if (schemaInfo != null) {
                final List<String> objectTypeTargetList = schemaInfo.getObjectTypeTargetList();
                assertObjectTypeTargetListNotEmpty(schemaName, objectTypeTargetList);
                return objectTypeTargetList.toArray(new String[objectTypeTargetList.size()]);
            }
        }
        final List<String> objectTypeTargetList = getProperties().getDatabaseProperties().getObjectTypeTargetList();
        assertObjectTypeTargetListNotEmpty(schemaName, objectTypeTargetList);
        return objectTypeTargetList.toArray(new String[objectTypeTargetList.size()]);
    }

    protected void assertObjectTypeTargetListNotEmpty(String schemaName, List<String> objectTypeTargetList) {
        if (objectTypeTargetList == null || objectTypeTargetList.isEmpty()) {
            String msg = "The property 'objectTypeTargetList' should be required:";
            msg = msg + " schemaName=" + schemaName;
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Resolve same name table.
     * If the same table names exist, it marks about it.
     * @param tableMetaInfoList The list of table meta info. (NotNull)
     */
    protected void resolveSameNameTable(final List<DfTableMetaInfo> tableMetaInfoList) {
        final Set<String> tableNameSet = new HashSet<String>();
        final Set<String> sameNameTableNameSet = new HashSet<String>();
        for (DfTableMetaInfo info : tableMetaInfoList) {
            final String tableName = info.getTableName();
            if (tableNameSet.contains(tableName)) {
                sameNameTableNameSet.add(tableName);
            }
            tableNameSet.add(tableName);
        }
        if (tableNameSet.size() == tableMetaInfoList.size()) {
            return;
        }
        for (DfTableMetaInfo tableMetaInfo : tableMetaInfoList) {
            final String tableName = tableMetaInfo.getTableName();
            if (sameNameTableNameSet.contains(tableName)) {
                _log.info("$ sameNameTable --> " + tableMetaInfo);
                tableMetaInfo.setExistSameNameTable(true);
            }
        }
    }
}