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
package org.seasar.dbflute.logic.jdbc.handler;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.Srl;

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
     * Get all the table names in the current database that are not system tables. <br />
     * This does not contain additional schema. only specified schema is considered.
     * @param dbMeta JDBC database meta data. (NotNull)
     * @param schemaName The name of schema that can contain catalog name as prefix. (Nullable)
     * @return The list of all the table meta info in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableList(DatabaseMetaData dbMeta, String schemaName) throws SQLException {
        return doGetTableList(dbMeta, schemaName);
    }

    protected List<DfTableMetaInfo> doGetTableList(DatabaseMetaData dbMeta, String schemaName) throws SQLException {
        schemaName = filterSchemaName(schemaName);
        final String[] objectTypes = getRealObjectTypeTargetArray(schemaName);
        final List<DfTableMetaInfo> tableList = new ArrayList<DfTableMetaInfo>();
        ResultSet resultSet = null;
        try {
            _log.info("...Getting tables: schema=" + schemaName + " objectTypes=" + Arrays.asList(objectTypes));
            final String catalogName = extractCatalogName(schemaName);
            final String realSchemaName = extractPureSchemaName(schemaName);
            resultSet = dbMeta.getTables(catalogName, realSchemaName, "%", objectTypes);
            while (resultSet.next()) {
                final String tableName = resultSet.getString("TABLE_NAME");
                final String tableType = resultSet.getString("TABLE_TYPE");
                final String tableCatalog;
                {
                    String tmpCatalog = resultSet.getString("TABLE_CAT");
                    if (Srl.is_Null_or_TrimmedEmpty(tmpCatalog)) { // because PostgreSQL returns null
                        if (Srl.is_NotNull_and_NotTrimmedEmpty(catalogName)) {
                            tmpCatalog = catalogName;
                        } else {
                            if (getBasicProperties().isDatabasePostgreSQL()) {
                                String url = getDatabaseProperties().getDatabaseUrl();
                                url = Srl.substringFirstFront(url, "?");
                                tmpCatalog = Srl.substringLastRear(url, "/");
                            }
                        }
                    }
                    tableCatalog = tmpCatalog;
                }
                final String tableSchema = resultSet.getString("TABLE_SCHEM");
                final String tableComment = resultSet.getString("REMARKS");

                final String catalogSchema;
                if (Srl.is_NotNull_and_NotTrimmedEmpty(tableCatalog)) {
                    // basically for additionalSchema
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(tableSchema)) {
                        catalogSchema = tableCatalog + "." + tableSchema;
                    } else {
                        // basically MySQL
                        catalogSchema = tableCatalog + "." + DfDatabaseProperties.NO_NAME_SCHEMA;
                    }
                } else {
                    catalogSchema = tableSchema;
                }

                if (isTableExcept(catalogSchema, tableName)) {
                    _log.info(tableName + " is excepted!");
                    continue;
                }
                if (isSystemTableForDBMS(tableName)) {
                    _log.info(tableName + " is excepted! {system table}");
                    continue;
                }

                final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
                tableMetaInfo.setTableName(tableName);
                tableMetaInfo.setTableType(tableType);
                tableMetaInfo.setCatalogSchema(catalogSchema);
                tableMetaInfo.setTableComment(tableComment);
                tableList.add(tableMetaInfo);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return tableList;
    }

    public boolean isSystemTableForDBMS(String tableName) {
        if (isOracle() && tableName.startsWith("BIN$")) {
            return true;
        }
        if (isSQLServer()) {
            final Set<String> systemSet = StringSet.createAsFlexible();
            systemSet.add("sysobjects");
            systemSet.add("sysconstraints");
            systemSet.add("syssegments");
            if (systemSet.contains(tableName)) {
                return true;
            }
        }
        if (isSQLite() && tableName.startsWith("sqlite_")) {
            return true;
        }
        return false;
    }

    protected String[] getRealObjectTypeTargetArray(String schemaName) {
        if (schemaName != null) {
            final DfAdditionalSchemaInfo schemaInfo = getAdditionalSchemaInfo(schemaName);
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
}