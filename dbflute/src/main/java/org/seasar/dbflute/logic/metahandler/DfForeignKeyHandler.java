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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;

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
     * Retrieves a list of foreign key columns for a given table.
     * @param dbMeta JDBC meta data.
     * @param tableMetaInfo The meta information of table.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMetaInfo(DatabaseMetaData dbMeta, String schemaName,
            DfTableMetaInfo tableMetaInfo) throws SQLException {
        schemaName = filterSchemaName(schemaName);
        schemaName = tableMetaInfo.selectMetaExtractingSchemaName(schemaName);
        final String tableName = tableMetaInfo.getTableName();
        return getForeignKeyMetaInfo(dbMeta, schemaName, tableName);
    }

    /**
     * Retrieves a list of foreign key columns for a given table.
     * @param dbMeta JDBC meta data.
     * @param tableName The name of table.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMetaInfo(DatabaseMetaData dbMeta, String schemaName,
            String tableName) throws SQLException {
        final Map<String, DfForeignKeyMetaInfo> fkMap = new LinkedHashMap<String, DfForeignKeyMetaInfo>();
        if (!isForeignKeyExtractingSupported()) {
            return fkMap;
        }
        final Map<String, String> exceptedFKKeyMap = new LinkedHashMap<String, String>();
        ResultSet foreignKeys = null;
        try {
            foreignKeys = dbMeta.getImportedKeys(null, schemaName, tableName);
            while (foreignKeys.next()) {
                final String refTableName = foreignKeys.getString(3);
                String fkName = foreignKeys.getString(12);
                if (fkName == null) { // if FK has no name - make it up (use table name instead)
                    fkName = refTableName;
                }

                if (isTableExcept(refTableName)) {
                    exceptedFKKeyMap.put(fkName, refTableName);
                    continue;
                }
                if (_refTableCheckSet != null && !_refTableCheckSet.contains(refTableName)) {
                    exceptedFKKeyMap.put(fkName, refTableName);
                    continue;
                }

                final String localColumnName = foreignKeys.getString(8);
                final String foreignColumnName = foreignKeys.getString(4);
                if (isColumnExcept(localColumnName) || isColumnExcept(foreignColumnName)) {
                    continue;
                }

                DfForeignKeyMetaInfo metaInfo = fkMap.get(fkName);
                if (metaInfo == null) {
                    metaInfo = new DfForeignKeyMetaInfo();
                    metaInfo.setForeignKeyName(fkName);
                    metaInfo.setLocalTableName(tableName);
                    metaInfo.setForeignTableName(refTableName);
                    fkMap.put(fkName, metaInfo);
                }
                metaInfo.putColumnNameMap(localColumnName, foreignColumnName);
            }
        } finally {
            if (foreignKeys != null) {
                foreignKeys.close();
            }
        }
        if (!exceptedFKKeyMap.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("...Excepting foreign keys from the table:").append(ln()).append("[Excepted Foreign Key]");
            final Set<String> exceptedFKKeySet = exceptedFKKeyMap.keySet();
            for (String exceptedKey : exceptedFKKeySet) {
                sb.append(ln()).append(" ").append(exceptedKey);
                sb.append(" (").append(tableName).append(" to ");
                sb.append(exceptedFKKeyMap.get(exceptedKey)).append(")");
            }
            _log.info(sb.toString());
        }
        return filterSameForeignKeyMetaInfo(fkMap);
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