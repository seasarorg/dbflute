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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * @author jflute
 */
public class DfForeignKeyHandler extends DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfForeignKeyHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, DfTableMetaInfo> _generatedTableMap;

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    /**
     * Retrieves a map of foreign key columns for a given table. (the key is FK name)
     * @param metaData JDBC meta data. (NotNull)
     * @param tableInfo The meta information of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMap(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        return getForeignKeyMap(metaData, unifiedSchema, tableName);
    }

    /**
     * Retrieves a map of foreign key columns for a given table. (the key is FK name)
     * @param metaData JDBC meta data. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (Nullable)
     * @param tableName The name of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName) throws SQLException {
        Map<String, DfForeignKeyMetaInfo> resultMap = doGetForeignKeyMetaInfo(metaData, unifiedSchema, tableName);
        if (resultMap.isEmpty()) { // for lower case
            resultMap = doGetForeignKeyMetaInfo(metaData, unifiedSchema, tableName.toLowerCase());
        }
        if (resultMap.isEmpty()) { // for upper case
            resultMap = doGetForeignKeyMetaInfo(metaData, unifiedSchema, tableName.toUpperCase());
        }
        return resultMap;
    }

    protected Map<String, DfForeignKeyMetaInfo> doGetForeignKeyMetaInfo(DatabaseMetaData dbMeta,
            UnifiedSchema unifiedSchema, String tableName) throws SQLException {
        final Map<String, DfForeignKeyMetaInfo> fkMap = newLinkedHashMap();
        if (!isForeignKeyExtractingSupported()) {
            return fkMap;
        }
        final Map<String, String> exceptedFKMap = newLinkedHashMap();
        ResultSet rs = null;
        try {
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            rs = dbMeta.getImportedKeys(catalogName, schemaName, tableName);
            while (rs.next()) {
                final String foreignCatalogName = rs.getString(1);
                final String foreignSchemaName = rs.getString(2);
                final String foreignTableName = rs.getString(3);
                String fkName = rs.getString(12);
                if (fkName == null) {
                    // basically no way
                    // make it up (use table name instead)
                    fkName = "FK_" + tableName + "_" + foreignTableName;
                }

                // handling except tables if the set for check is set
                // (basically if the foreign table is non-generate target, it is excepted)
                if (!isForeignTableGenerated(foreignTableName)) {
                    exceptedFKMap.put(fkName, foreignTableName);
                    continue;
                }

                // check except columns
                final String localColumnName = rs.getString(8);
                final String foreignColumnName = rs.getString(4);
                assertFKColumnNotExcepted(unifiedSchema, tableName, localColumnName);
                final UnifiedSchema foreignSchema = createAsDynamicSchema(foreignCatalogName, foreignSchemaName);
                assertPKColumnNotExcepted(foreignSchema, foreignTableName, foreignColumnName);

                DfForeignKeyMetaInfo metaInfo = fkMap.get(fkName);
                if (metaInfo == null) { // basically here
                    metaInfo = new DfForeignKeyMetaInfo();
                    fkMap.put(fkName, metaInfo);
                } else {
                    // /- - - - - - - - - - - -
                    // same-name FK was found!
                    // - - - - - - - - - -/
                    final String firstName = metaInfo.getForeignTableName();
                    final String secondName = foreignTableName;
                    if (firstName.equalsIgnoreCase(secondName)) { // multiple FK
                        metaInfo.putColumnNameMap(localColumnName, foreignColumnName);
                        continue; // putting columns only
                    } else {
                        // here: same-name FK and same different foreign table.
                        // Basically no way!
                        // But DB2 returns to-ALIAS foreign key as same-name FK.
                        // Same type as local's type is prior
                        // and if types are different, use first.
                        final String msgBase = "...Handling same-name FK (use first one): " + fkName + " to ";
                        if (judgeSameNameForeignKey(tableName, firstName, secondName)) {
                            // use first (skip current)
                            _log.info(msgBase + firstName);
                            continue;
                        } else {
                            // use second (override)
                            _log.info(msgBase + secondName);
                        }
                    }
                }
                // first or override
                metaInfo.setForeignKeyName(fkName);
                metaInfo.setLocalTableName(tableName);
                metaInfo.setForeignTableName(foreignTableName);
                metaInfo.putColumnNameMap(localColumnName, foreignColumnName);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        handleExceptedForeignKey(exceptedFKMap, tableName);
        return filterSameStructureForeignKey(fkMap);
    }

    protected void assertFKColumnNotExcepted(UnifiedSchema unifiedSchema, String tableName, String columnName) {
        if (isColumnExcept(unifiedSchema, tableName, columnName)) {
            String msg = "FK columns are unsupported on 'columnExcept' property:";
            msg = msg + " unifiedSchema=" + unifiedSchema;
            msg = msg + " tableName=" + tableName;
            msg = msg + " columnName=" + columnName;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

    protected void assertPKColumnNotExcepted(UnifiedSchema unifiedSchema, String tableName, String columnName) {
        if (isColumnExcept(unifiedSchema, tableName, columnName)) {
            String msg = "PK columns are unsupported on 'columnExcept' property:";
            msg = msg + " unifiedSchema=" + unifiedSchema;
            msg = msg + " tableName=" + tableName;
            msg = msg + " columnName=" + columnName;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

    protected boolean judgeSameNameForeignKey(String localName, String firstName, String secondName) {
        final DfTableMetaInfo localInfo = getTableInfo(localName);
        final DfTableMetaInfo firstInfo = getTableInfo(firstName);
        final DfTableMetaInfo secondInfo = getTableInfo(secondName);
        if (localInfo != null && firstInfo != null && secondInfo != null) {
            final String localType = localInfo.getTableType();
            if (localType.equals(firstInfo.getTableType())) {
                // use first
                return false;
            } else if (localType.equals(secondInfo.getTableType())) {
                // use second
                return true;
            }
        }
        return true; // use first
    }

    protected void handleExceptedForeignKey(Map<String, String> exceptedFKMap, String localTableName) {
        if (exceptedFKMap.isEmpty()) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("...Excepting foreign keys (refers to non-generated table):");
        sb.append(ln()).append("[Excepted Foreign Key]");
        final Set<Entry<String, String>> entrySet = exceptedFKMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            final String fkName = entry.getKey();
            final String foreignTableName = entry.getValue();
            sb.append(ln()).append(" ").append(fkName);
            sb.append(" (").append(localTableName).append(" to ").append(foreignTableName).append(")");
        }
        _log.info(sb.toString());
    }

    protected Map<String, DfForeignKeyMetaInfo> filterSameStructureForeignKey(Map<String, DfForeignKeyMetaInfo> fkMap) {
        final Map<String, DfForeignKeyMetaInfo> filteredFKMap = newLinkedHashMap();
        final Map<Map<String, Object>, Object> checkMap = newLinkedHashMap();
        final Object dummyObj = new Object();
        final Set<Entry<String, DfForeignKeyMetaInfo>> entrySet = fkMap.entrySet();
        for (Entry<String, DfForeignKeyMetaInfo> entry : entrySet) {
            final String foreinKeyName = entry.getKey();
            final DfForeignKeyMetaInfo metaInfo = entry.getValue();
            final Map<String, Object> checkKey = newLinkedHashMap();
            checkKey.put(metaInfo.getForeignTableName(), dummyObj);
            checkKey.put("columnNameMap:" + metaInfo.getColumnNameMap(), dummyObj);
            if (checkMap.containsKey(checkKey)) { // basically no way
                String msg = "*The same-structural foreign key was found:";
                msg = msg + " skipped = " + foreinKeyName + " - " + checkKey;
                _log.warn(msg);
            } else {
                checkMap.put(checkKey, dummyObj);
                filteredFKMap.put(foreinKeyName, metaInfo);
            }
        }
        return filteredFKMap;
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void exceptForeignTableNotGenerated(Map<String, DfTableMetaInfo> generatedTableMap) {
        _generatedTableMap = generatedTableMap;
    }

    protected boolean isForeignTableGenerated(String foreignTableName) {
        if (_generatedTableMap == null || _generatedTableMap.isEmpty()) {
            // means no check of generation
            return true;
        }
        final DfTableMetaInfo info = _generatedTableMap.get(foreignTableName);
        if (info == null) {
            return false;
        }
        if (info.isOutOfGenerateTarget()) {
            return false;
        }
        return true;
    }

    protected DfTableMetaInfo getTableInfo(String tableName) {
        if (_generatedTableMap == null) {
            return null;
        }
        return _generatedTableMap.get(tableName);
    }
}