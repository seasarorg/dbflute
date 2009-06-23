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
package org.seasar.dbflute.friends.torque;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfAdditionalPrimaryKeyProperties;

/**
 * The initializer of additional primary key.
 * @author jflute
 * @since 0.7.7 (2008/07/30 Wednesday)
 */
public class DfAdditionalPrimaryKeyInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfAdditionalPrimaryKeyInitializer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Database _database;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfAdditionalPrimaryKeyInitializer(Database database) {
        _database = database;
    }

    // ===================================================================================
    //                                                                AdditionalPrimaryKey
    //                                                                ====================
    public void initializeAdditionalPrimaryKey() {
        _log.info("/=======================================");
        _log.info("...Initializing additional primary keys.");

        final Map<String, Map<String, String>> additionalPrimaryKeyMap = getAdditionalPrimaryKeyMap();
        final Set<String> primaryNameKeySet = additionalPrimaryKeyMap.keySet();
        for (String primaryKeyName : primaryNameKeySet) {
            final String tableName = getTableName(primaryKeyName);
            assertTable(tableName);
            final List<String> columnNameList = getLocalColumnNameList(primaryKeyName);
            assertColumnList(tableName, columnNameList);
            final Table table = getTable(tableName);
            if (table.hasPrimaryKey()) {
                String msg = "The primary key of the table has already set up: ";
                msg = msg + " tableName=" + tableName + " existing primaryKey=" + table.getPrimaryKeyDispValueString();
                msg = msg + " your specified primaryKey=" + columnNameList;
                _log.info(msg);
                continue;
            }
            for (String columnName : columnNameList) {
                final Column column = table.getColumn(columnName);
                column.setPrimaryKey(true);
                column.setAdditionalPrimaryKey(true);
            }
            showResult(primaryKeyName, table, columnNameList);
        }
        _log.info("==========/");
    }

    protected void showResult(String primaryKeyName, Table table, List<String> columnNameList) {
        _log.info("    " + primaryKeyName);
        if (columnNameList.size() == 1) {
            _log.info("       Add primary key " + table.getName() + "." + columnNameList.get(0));
        } else {
            _log.info("       Add primary key " + table.getName() + "." + columnNameList);
        }
    }

    protected DfAdditionalPrimaryKeyProperties getProperties() {
        return DfBuildProperties.getInstance().getAdditionalPrimaryKeyProperties();
    }

    protected void assertTable(final String tableName) {
        if (getTable(tableName) == null) {
            String msg = "Not found table by the tableName: " + tableName;
            msg = msg + " additionalPrimaryKeyMap=" + getAdditionalPrimaryKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected void assertColumnList(final String tableName, List<String> columnNameList) {
        if (!getTable(tableName).containsColumnsByFlexibleName(columnNameList)) {
            String msg = "Not found column by the columnNames: " + columnNameList;
            msg = msg + " of the table '" + tableName + "'";
            msg = msg + " additionalPrimaryKeyMap=" + getAdditionalPrimaryKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected String getTableName(String primaryKeyName) {
        return getProperties().findTableName(primaryKeyName);
    }

    protected List<String> getLocalColumnNameList(String primaryKeyName) {
        return getProperties().findColumnNameList(primaryKeyName);
    }

    protected Map<String, Map<String, String>> getAdditionalPrimaryKeyMap() {
        return getProperties().getAdditionalPrimaryKeyMap();
    }

    protected Table getTable(String tableName) {
        return getDatabase().getTable(tableName);
    }

    protected Table[] getTables() {
        return getDatabase().getTables();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected Database getDatabase() {
        return _database;
    }
}