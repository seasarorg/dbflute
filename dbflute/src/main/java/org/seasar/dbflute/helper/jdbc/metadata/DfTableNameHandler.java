/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author jflute
 */
public class DfTableNameHandler extends DfAbstractMetaDataHandler {

    private static final Log _log = LogFactory.getLog(DfTableNameHandler.class);

    public static class DfTableMetaInfo {
        protected String _tableName;

        protected String _tableType;

        public boolean isTableTypeView() {
            return _tableType != null ? _tableType.equalsIgnoreCase("VIEW") : false;
        }

        public String getTableName() {
            return _tableName;
        }

        public void setTableName(String tableName) {
            this._tableName = tableName;
        }

        public String getTableType() {
            return _tableType;
        }

        public void setTableType(String tableType) {
            this._tableType = tableType;
        }
    }

    /**
     * Get all the table names in the current database that are not
     * system tables.
     * 
     * @param dbMeta JDBC database metadata.
     * @return The list of all the table meta info in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableNames(DatabaseMetaData dbMeta, String schemaName) throws SQLException {
        // /---------------------------------------------------- [My Extension]
        // Get DatabaseTypes from ContextProperties.
        // These are the entity types we want from the database
        final String[] types = getDatabaseTypeStringArray();
        logDatabaseTypes(types);
        // -------------------/

        final List<DfTableMetaInfo> tables = new ArrayList<DfTableMetaInfo>();
        ResultSet resultSet = null;
        try {
            resultSet = dbMeta.getTables(null, schemaName, "%", types);
            while (resultSet.next()) {
                final String tableName = resultSet.getString(3);
                final String tableType = resultSet.getString(4);

                if (isTableExcept(tableName)) {
                    _log.debug("$ isTableExcept(" + tableName + ") == true");
                    continue;
                }
                if (DfBuildProperties.getInstance().getBasicProperties().isDatabaseOracle()) {
                    if (tableName.startsWith("BIN$")) {
                        _log.debug("$ isTableExcept(" + tableName
                                + ") == true {Forced because the database is Oracle!}");
                        continue;
                    }
                }

                final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
                tableMetaInfo.setTableName(tableName);
                tableMetaInfo.setTableType(tableType);
                tables.add(tableMetaInfo);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return tables;
    }

    /**
     * Get database-type-string-array.
     * 
     * @return Database-type-string-array.
     */
    protected String[] getDatabaseTypeStringArray() {
        final List<Object> defaultList = new ArrayList<Object>();
        defaultList.add("TABLE");
        defaultList.add("VIEW");
        final List ls = getProperties().listProp("torque.database.type.list", defaultList);
        final String[] result = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            result[i] = (String) ls.get(i);
        }
        return result;
    }

    /**
     * Log database-types. {This is a mere helper method.}
     * 
     * @param types Database-types. (NotNull)
     */
    protected void logDatabaseTypes(String[] types) {
        String typeString = "";
        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                typeString = types[i];
            } else {
                typeString = typeString + " - " + types[i];
            }
        }
        _log.info("$ DatabaseTypes are '" + typeString + "'");
    }

}