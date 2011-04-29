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
package org.seasar.dbflute.logic.jdbc.metadata.basic;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfForeignKeyExtractor extends DfAbstractMetaDataBasicExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfForeignKeyExtractor.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, DfTableMeta> _generatedTableMap;

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
    public Map<String, DfForeignKeyMeta> getForeignKeyMap(DatabaseMetaData metaData, DfTableMeta tableInfo)
            throws SQLException {
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        final String tableName = tableInfo.getTableName();
        return getForeignKeyMap(metaData, unifiedSchema, tableName);
    }

    /**
     * Retrieves a map of foreign key columns for a given table. (the key is FK name)
     * @param metaData JDBC meta data. (NotNull)
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (NullAllowed)
     * @param tableName The name of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMeta> getForeignKeyMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName) throws SQLException {
        Map<String, DfForeignKeyMeta> map = doGetForeignKeyMap(metaData, unifiedSchema, tableName, false);
        if (canRetryCaseInsensitive()) {
            if (map.isEmpty()) { // retry by lower case
                map = doGetForeignKeyMap(metaData, unifiedSchema, tableName.toLowerCase(), true);
            }
            if (map.isEmpty()) { // retry by upper case
                map = doGetForeignKeyMap(metaData, unifiedSchema, tableName.toUpperCase(), true);
            }
        }
        return map;
    }

    protected Map<String, DfForeignKeyMeta> doGetForeignKeyMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        final Map<String, DfForeignKeyMeta> fkMap = newLinkedHashMap();
        if (isForeignKeyExtractingUnsupported()) {
            return fkMap;
        }
        final Map<String, String> exceptedFKMap = newLinkedHashMap();
        ResultSet rs = null;
        try {
            rs = extractForeignKeyMetaData(metaData, unifiedSchema, tableName, retry);
            if (rs == null) {
                return DfCollectionUtil.newHashMap();
            }
            while (rs.next()) {
                // /- - - - - - - - - - - - - - - - - - - - - - - -
                // same policy as table process about JDBC handling
                // (see DfTableHandler.java)
                // - - - - - - - - - -/

                final String localTableName = rs.getString(7);
                if (checkMetaTableDiffIfNeeds(tableName, localTableName)) {
                    continue;
                }

                final String foreignCatalogName = rs.getString(1);
                final String foreignSchemaName = rs.getString(2);
                final String foreignTableName = rs.getString(3);
                final String foreignColumnName = rs.getString(4);
                final String localColumnName = rs.getString(8);
                final String fkName;
                {
                    final String fkPlainName = rs.getString(12);
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(fkPlainName)) {
                        fkName = fkPlainName;
                    } else {
                        // basically no way but SQLite comes here
                        // make it up automatically just in case
                        // (use local column name and foreign table name)
                        fkName = "FK_" + tableName + "_" + localColumnName + "_" + foreignTableName;
                        _log.info("...Making FK name (because of no name): " + fkName);
                    }
                }

                // handling except tables if the set for check is set
                // (basically if the foreign table is non-generate target, it is excepted)
                if (!isForeignTableGenerated(foreignTableName)) {
                    exceptedFKMap.put(fkName, foreignTableName);
                    continue;
                }

                // check except columns
                assertFKColumnNotExcepted(unifiedSchema, tableName, localColumnName);
                final UnifiedSchema foreignSchema = createAsDynamicSchema(foreignCatalogName, foreignSchemaName);
                assertPKColumnNotExcepted(foreignSchema, foreignTableName, foreignColumnName);

                DfForeignKeyMeta metaInfo = fkMap.get(fkName);
                if (metaInfo == null) { // basically here
                    metaInfo = new DfForeignKeyMeta();
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
                        final String msgBase = "...Handling same-name FK ";
                        if (judgeSameNameForeignKey(tableName, firstName, secondName)) {
                            // use first (skip current)
                            _log.info(msgBase + "(use first one): " + fkName + " to " + firstName);
                            continue;
                        } else {
                            // use second (override)
                            _log.info(msgBase + "(use second one): " + fkName + " to " + secondName);
                        }
                    }
                }
                // first or override
                metaInfo.setForeignKeyName(fkName);
                metaInfo.setLocalTableName(localTableName);
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

    protected ResultSet extractForeignKeyMetaData(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, boolean retry) throws SQLException {
        try {
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            return metaData.getImportedKeys(catalogName, schemaName, tableName);
        } catch (SQLException e) {
            if (retry) {
                // because the exception may be thrown when the table is not found
                return null;
            } else {
                throw e;
            }
        }
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
        final DfTableMeta localInfo = getTableMeta(localName);
        final DfTableMeta firstInfo = getTableMeta(firstName);
        final DfTableMeta secondInfo = getTableMeta(secondName);
        if (localInfo != null && firstInfo != null && secondInfo != null) {
            final String localType = localInfo.getTableType();
            if (localType.equals(firstInfo.getTableType())) {
                // use first
                return true;
            } else if (localType.equals(secondInfo.getTableType())) {
                // use second
                return false;
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

    protected Map<String, DfForeignKeyMeta> filterSameStructureForeignKey(Map<String, DfForeignKeyMeta> fkMap) {
        final Map<String, DfForeignKeyMeta> filteredFKMap = newLinkedHashMap();
        final Map<Map<String, Object>, Object> checkMap = newLinkedHashMap();
        final Object dummyObj = new Object();
        final Set<Entry<String, DfForeignKeyMeta>> entrySet = fkMap.entrySet();
        for (Entry<String, DfForeignKeyMeta> entry : entrySet) {
            final String foreinKeyName = entry.getKey();
            final DfForeignKeyMeta metaInfo = entry.getValue();
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
    public void exceptForeignTableNotGenerated(Map<String, DfTableMeta> generatedTableMap) {
        _generatedTableMap = generatedTableMap;
    }

    protected boolean isForeignTableGenerated(String foreignTableName) {
        if (_generatedTableMap == null || _generatedTableMap.isEmpty()) {
            // means no check of generation
            return true;
        }
        final DfTableMeta meta = _generatedTableMap.get(foreignTableName);
        if (meta == null) {
            return false;
        }
        if (meta.isOutOfGenerateTarget()) {
            return false;
        }
        return true;
    }

    protected DfTableMeta getTableMeta(String tableName) {
        if (_generatedTableMap == null) {
            return null;
        }
        return _generatedTableMap.get(tableName);
    }
}