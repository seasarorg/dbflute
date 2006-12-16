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
package org.seasar.dbflute.torque;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;

/**
 * Torque task utility.
 * 
 * @author mkubo
 */
public class DfAdditionalForeignKeyInitializer {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(DfAdditionalForeignKeyInitializer.class);

    protected Database _database;

    public DfAdditionalForeignKeyInitializer(Database database) {
        _database = database;
    }

    protected Database getDatabase() {
        return _database;
    }

    // ===============================================================================
    //                                                            AdditionalForeignKey
    //                                                            ====================
    public void initializeAdditionalForeignKey() {
        _log.debug("/======================================");
        _log.debug("...Initializing additional foreign key.");

        final Map<String, Map<String, String>> additionalForeignKeyMap = getAdditionalForeignKeyMap();
        final Set<String> tableNameKeySet = additionalForeignKeyMap.keySet();
        for (String foreignName : tableNameKeySet) {

            final String foreignTableName = getComponentForeignTableName(foreignName);
            assertForeignTable(foreignTableName);
            List<String> foreignColumnNameList = getForeignColumnNameList(foreignName, foreignTableName);
            assertForeignTableColumn(foreignTableName, foreignColumnNameList);

            final String localTableName = getComponentLocalTableName(foreignName);
            assertLocalTable(localTableName);

            _log.debug("    " + foreignName);
            if (localTableName.equals("*")) {
                final Table[] tableArray = getTables();
                for (final Table table : tableArray) {

                    final List<String> localColumnNameList = getLocalColumnNameList(foreignName, foreignTableName,
                            foreignColumnNameList, table.getName(), false);
                    if (localColumnNameList == null || !table.containsColumn(localColumnNameList)) {
                        continue;
                    }

                    if (table.isExistForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList)) {
                        String msg = "The foreign-key has already set up: ";
                        _log.debug(msg + foreignTableName + " " + localColumnNameList + " " + foreignColumnNameList);
                        continue;
                    }

                    final ForeignKey fk = new ForeignKey();
                    fk.setForeignTableName(foreignTableName);
                    fk.addReference(localColumnNameList, foreignColumnNameList);
                    table.addForeignKey(fk);
                    getTable(foreignTableName).addReferrer(fk);
                    for (String foreignColumnName : foreignColumnNameList) {
                        final Column foreignColumn = getTable(foreignTableName).getColumn(foreignColumnName);
                        foreignColumn.addReferrer(fk);
                    }

                    String msg = "       Add foreign key " + table.getName() + "." + localColumnNameList;
                    msg = msg + " - " + foreignTableName + "." + foreignColumnNameList;
                    _log.debug(msg);
                }
            } else {
                final Table table = getTable(localTableName);
                final List<String> localColumnNameList = getLocalColumnNameList(foreignName, foreignTableName,
                        foreignColumnNameList, localTableName, true);
                assertLocalTableColumn(localTableName, localColumnNameList);
                if (table.isExistForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList)) {
                    String msg = "The foreign-key has already set up: ";
                    _log.debug(msg + foreignTableName + " " + localColumnNameList + " " + foreignColumnNameList);
                    continue;
                }

                final ForeignKey fk = new ForeignKey();
                fk.setForeignTableName(foreignTableName);
                fk.addReference(localColumnNameList, foreignColumnNameList);
                table.addForeignKey(fk);
                getTable(foreignTableName).addReferrer(fk);
                for (String foreignColumnName : foreignColumnNameList) {
                    final Column foreignColumn = getTable(foreignTableName).getColumn(foreignColumnName);
                    foreignColumn.addReferrer(fk);
                }

                String msg = "       Add foreign key " + table.getName() + "." + localColumnNameList;
                msg = msg + " to " + foreignTableName + "." + foreignColumnNameList;
                _log.debug(msg);
            }
        }
        _log.debug("========/");
    }

    protected List<String> getForeignColumnNameList(String foreignName, final String foreignTableName) {
        List<String> foreignColumnNameList = getComponentForeignColumnNameList(foreignName);
        if (foreignColumnNameList == null) {
            foreignColumnNameList = new ArrayList<String>();
            final List<Column> foreignPrimaryKeyList = getTable(foreignTableName).getPrimaryKey();
            if (foreignPrimaryKeyList.isEmpty()) {
                String msg = "The foreignTable[" + foreignTableName + "] should have primary-key!";
                throw new RuntimeException(msg);
            }
            for (Column column : foreignPrimaryKeyList) {
                foreignColumnNameList.add(column.getName());
            }
        }
        return foreignColumnNameList;
    }

    protected DfAdditionalForeignKeyProperties getProperties() {
        return DfBuildProperties.getInstance().getAdditionalForeignKeyProperties();
    }

    protected List<String> getLocalColumnNameList(String foreignName, final String foreignTableName,
            List<String> foreignColumnNameList, final String localTableName, boolean isErrorNotFound) {
        final List<String> localColumnNameList = getComponentLocalColumnNameList(foreignName);
        if (localColumnNameList == null || localColumnNameList.isEmpty()) {
            for (String foreignColumnName : foreignColumnNameList) {
                final Column column = getTable(localTableName).getColumn(foreignColumnName);
                if (column == null) {
                    if (isErrorNotFound) {
                        String msg = "The localTable[" + localTableName + "] should have the columns '";
                        msg = msg + foreignColumnNameList + "' same as primary keys of foreign table["
                                + foreignTableName + "]";
                        throw new RuntimeException(msg);
                    } else {
                        return null;
                    }
                }
                localColumnNameList.add(column.getName());
            }
        }
        return localColumnNameList;
    }

    protected void assertForeignTable(final String foreignTableName) {
        if (getTable(foreignTableName) == null) {
            String msg = "Not found table by the foreignTableName: " + foreignTableName;
            msg = msg + " additionalForeignKeyMap=" + getAdditionalForeignKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected void assertForeignTableColumn(final String foreignTableName, List<String> foreignColumnNameList) {
        if (!getTable(foreignTableName).containsColumn(foreignColumnNameList)) {
            String msg = "Not found column by the foreignColumnNameList: " + foreignColumnNameList;
            msg = msg + " of " + foreignTableName;
            msg = msg + " additionalForeignKeyMap=" + getAdditionalForeignKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected void assertLocalTable(final String localTableName) {
        if (getTable(localTableName) == null) {
            String msg = "Not found table by the localTableName: " + localTableName;
            msg = msg + " additionalForeignKeyMap=" + getAdditionalForeignKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected void assertLocalTableColumn(final String localTableName, List<String> localColumnNameList) {
        if (!getTable(localTableName).containsColumn(localColumnNameList)) {
            String msg = "Not found column by the localColumnNameList: " + localColumnNameList;
            msg = msg + " of " + localTableName;
            msg = msg + " additionalForeignKeyMap=" + getAdditionalForeignKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected String getComponentLocalTableName(String foreignName) {
        return getProperties().getAdditionalForeignKeyComponentLocalTableName(foreignName);
    }

    protected String getComponentForeignTableName(String foreignName) {
        return getProperties().getAdditionalForeignKeyComponentForeignTableName(foreignName);
    }

    protected List<String> getComponentLocalColumnNameList(String foreignName) {
        return getProperties().getComponentLocalColumnNameList(foreignName);
    }

    protected List<String> getComponentForeignColumnNameList(String foreignName) {
        return getProperties().getComponentForeignColumnNameList(foreignName);
    }

    protected Map<String, Map<String, String>> getAdditionalForeignKeyMap() {
        return getProperties().getAdditionalForeignKeyMap();
    }

    protected Table getTable(String tableName) {
        return getDatabase().getTable(tableName);
    }

    protected Table[] getTables() {
        return getDatabase().getTables();
    }
}