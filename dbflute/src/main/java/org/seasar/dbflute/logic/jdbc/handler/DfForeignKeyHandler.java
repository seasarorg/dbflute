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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
    protected Set<String> _refTableCheckSet;

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
        final Map<String, String> exceptedFKKeyMap = newLinkedHashMap();
        ResultSet foreignKeys = null;
        try {
            final String catalogName = unifiedSchema.getPureCatalog();
            final String schemaName = unifiedSchema.getPureSchema();
            foreignKeys = dbMeta.getImportedKeys(catalogName, schemaName, tableName);
            while (foreignKeys.next()) {
                final String foreignCatalogName = foreignKeys.getString(1);
                final String foreignSchemaName = foreignKeys.getString(2);
                final String foreignTableName = foreignKeys.getString(3);
                String fkName = foreignKeys.getString(12);
                if (fkName == null) { // if FK has no name - make it up (use table name instead)
                    fkName = foreignTableName;
                }

                // handling except tables if the set for check is set
                // (basically if the foreign table is non-generate target, it is excepted)
                if (_refTableCheckSet != null && !_refTableCheckSet.contains(foreignTableName)) {
                    exceptedFKKeyMap.put(fkName, foreignTableName);
                    continue;
                }

                // check except columns
                final String localColumnName = foreignKeys.getString(8);
                final String foreignColumnName = foreignKeys.getString(4);
                assertFKColumnNotExcepted(unifiedSchema, tableName, localColumnName);
                final UnifiedSchema foreignSchema = createAsDynamicSchema(foreignCatalogName, foreignSchemaName);
                assertPKColumnNotExcepted(foreignSchema, foreignTableName, foreignColumnName);

                DfForeignKeyMetaInfo metaInfo = fkMap.get(fkName);
                if (metaInfo == null) {
                    metaInfo = new DfForeignKeyMetaInfo();
                    fkMap.put(fkName, metaInfo);
                } else {
                    // basically no way!
                    // but DB2 returns to-ALIAS foreign key as same-name FK
                    // it overrides here (use later)
                    String firstLocal = metaInfo.getLocalTableName();
                    String firstForeign = metaInfo.getForeignTableName();
                    final StringBuilder sb = new StringBuilder();
                    sb.append("...Handling same-name FK (use later one):");
                    sb.append(ln()).append("[Duplicate Foreign Key]: ").append(fkName);
                    sb.append(ln()).append(" first = ").append(firstLocal).append(" to ").append(firstForeign);
                    sb.append(ln()).append(" later = ").append(tableName).append(" to ").append(foreignTableName);
                    _log.info(sb.toString());
                }
                metaInfo.setForeignKeyName(fkName);
                metaInfo.setLocalTableName(tableName);
                metaInfo.setForeignTableName(foreignTableName);
                metaInfo.putColumnNameMap(localColumnName, foreignColumnName);
            }
        } finally {
            if (foreignKeys != null) {
                foreignKeys.close();
            }
        }
        if (!exceptedFKKeyMap.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("...Excepting foreign keys from the table:");
            sb.append(ln()).append("[Excepted Foreign Key]");
            final Set<String> exceptedFKKeySet = exceptedFKKeyMap.keySet();
            for (String exceptedKey : exceptedFKKeySet) {
                final String exceptedFKTable = exceptedFKKeyMap.get(exceptedKey);
                sb.append(ln()).append(" ").append(exceptedKey);
                sb.append(" (").append(tableName).append(" to ").append(exceptedFKTable).append(")");
            }
            _log.info(sb.toString());
        }
        return filterSameForeignKeyMetaInfo(fkMap);
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

    protected Map<String, DfForeignKeyMetaInfo> filterSameForeignKeyMetaInfo(Map<String, DfForeignKeyMetaInfo> fks) {
        final Map<String, DfForeignKeyMetaInfo> filteredForeignKeyMetaInfoMap = new LinkedHashMap<String, DfForeignKeyMetaInfo>();
        final Map<Map<String, Object>, Object> checkMap = new LinkedHashMap<Map<String, Object>, Object>();
        final Set<String> foreignKeyNameSet = fks.keySet();
        for (String foreinKeyName : foreignKeyNameSet) {
            final DfForeignKeyMetaInfo metaInfo = fks.get(foreinKeyName);

            final Map<String, Object> checkKeyMap = new LinkedHashMap<String, Object>();
            checkKeyMap.put(metaInfo.getForeignTableName(), new Object());
            checkKeyMap.put("columnNameMap:" + metaInfo.getColumnNameMap(), new Object());
            if (checkMap.containsKey(checkKeyMap)) {
                String msg = "A structural one of the same row already exists.";
                msg = msg + "The skipped foreign-key name is " + foreinKeyName + ".";
                msg = msg + " The columns are " + checkKeyMap + ".";
                _log.warn(msg);
            } else {
                checkMap.put(checkKeyMap, new Object());
                filteredForeignKeyMetaInfoMap.put(foreinKeyName, metaInfo);
            }
        }
        return filteredForeignKeyMetaInfoMap;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Set<String> getRefTableCheckSet() {
        return _refTableCheckSet;
    }

    public void setRefTableCheckSet(Set<String> refTableCheckSet) {
        this._refTableCheckSet = refTableCheckSet;
    }
}