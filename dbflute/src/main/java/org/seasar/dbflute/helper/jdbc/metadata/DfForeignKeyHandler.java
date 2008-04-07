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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler.DfTableMetaInfo;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author jflute
 */
public class DfForeignKeyHandler extends DfAbstractMetaDataHandler {

    private static final Log _log = LogFactory.getLog(DfForeignKeyHandler.class);

    /**
     * Retrieves a list of foreign key columns for a given table.
     * @param dbMeta JDBC meta data.
     * @param tableMetaInfo The meta information of table.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMetaInfo(DatabaseMetaData dbMeta, String schemaName,
            DfTableMetaInfo tableMetaInfo) throws SQLException {
        final String tableName = tableMetaInfo.getTableName();
        final Map<String, DfForeignKeyMetaInfo> fkMap = new LinkedHashMap<String, DfForeignKeyMetaInfo>();
        ResultSet foreignKeys = null;
        try {
            final String realSchemaName = tableMetaInfo.selectRealSchemaName(schemaName);
            foreignKeys = dbMeta.getImportedKeys(null, realSchemaName, tableName);
            while (foreignKeys.next()) {
                String refTableName = foreignKeys.getString(3);

                if (isTableExcept(refTableName)) {
                    continue;
                }

                String fkName = foreignKeys.getString(12);
                if (fkName == null) {// if FK has no name - make it up (use tablename instead)
                    fkName = refTableName;
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

    public static class DfForeignKeyMetaInfo {
        protected String _foreignKeyName;

        protected String _localTableName;

        protected String _foreignTableName;

        protected Map<String, String> _columnNameMap = new LinkedHashMap<String, String>();

        public String getForeignKeyName() {
            return _foreignKeyName;
        }

        public void setForeignKeyName(String foreignKeyName) {
            this._foreignKeyName = foreignKeyName;
        }

        public String getLocalTableName() {
            return _localTableName;
        }

        public void setLocalTableName(String localtableName) {
            this._localTableName = localtableName;
        }

        public String getForeignTableName() {
            return _foreignTableName;
        }

        public void setForeignTableName(String foreignTableName) {
            this._foreignTableName = foreignTableName;
        }

        public Map<String, String> getColumnNameMap() {
            return _columnNameMap;
        }

        public void putColumnNameMap(String localColumnName, String foreignColumnName) {
            this._columnNameMap.put(localColumnName, foreignColumnName);
        }

        @Override
        public String toString() {
            return _foreignKeyName + "-{" + _localTableName + ":" + _foreignTableName + "--" + _columnNameMap + "}";
        }

    }
}