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
 * The initializer of additional foreign key.
 * @author jflute
 */
public class DfAdditionalForeignKeyInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfAdditionalForeignKeyInitializer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Database _database;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfAdditionalForeignKeyInitializer(Database database) {
        _database = database;
    }

    // ===================================================================================
    //                                                                AdditionalForeignKey
    //                                                                ====================
    public void initializeAdditionalForeignKey() {
        _log.info("/======================================");
        _log.info("...Initializing additional foreign key.");

        final Map<String, Map<String, String>> additionalForeignKeyMap = getAdditionalForeignKeyMap();
        final Set<String> foreignKeyNameKeySet = additionalForeignKeyMap.keySet();
        for (String foreignKeyName : foreignKeyNameKeySet) {
            final String foreignTableName = getForeignTableName(foreignKeyName);
            assertForeignTable(foreignTableName);
            final List<String> foreignColumnNameList = getForeignColumnNameList(foreignKeyName, foreignTableName);
            assertForeignTableColumn(foreignTableName, foreignColumnNameList);
            final String localTableName = getLocalTableName(foreignKeyName);
            final String fixedCondition = getFixedCondition(foreignKeyName);
            final String fixedSuffix = getFixedSuffix(foreignKeyName);

            _log.info("    " + foreignKeyName);
            if (localTableName.equals("*")) {
                processAllTableFK(foreignKeyName, foreignTableName, foreignColumnNameList, fixedCondition, fixedSuffix);
            } else {
                assertLocalTable(localTableName);
                final Table table = getTable(localTableName);
                final List<String> localColumnNameList = getLocalColumnNameList(foreignKeyName, foreignTableName,
                        foreignColumnNameList, localTableName, true);
                assertLocalTableColumn(localTableName, localColumnNameList);
                if (table.isExistForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList)) {
                    String msg = "The foreign key has already set up: ";
                    _log.info(msg + foreignTableName + " " + localColumnNameList + " " + foreignColumnNameList);
                    continue;
                }

                setupForeignKeyToTable(foreignTableName, foreignColumnNameList, fixedCondition, table,
                        localColumnNameList, fixedSuffix);
                showResult(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList);
            }
        }
        _log.info("========/");
    }

    protected void processAllTableFK(String foreignName, String foreignTableName, List<String> foreignColumnNameList,
            String fixedCondition, String fixedSuffix) {
        final Table[] tableArray = getTables();
        for (final Table table : tableArray) {
            final List<String> localColumnNameList = getLocalColumnNameList(foreignName, foreignTableName,
                    foreignColumnNameList, table.getName(), false);
            if (localColumnNameList == null || !table.containsColumnsByFlexibleName(localColumnNameList)) {
                continue;
            }
            if (table.isExistForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList)) {
                String msg = "The foreign key has already set up: ";
                msg = msg + " localTable=" + table.getName() + " foreignTable=" + foreignTableName;
                msg = msg + " localColumnNameList=" + localColumnNameList + " foreignColumnNameList="
                        + foreignColumnNameList;
                _log.info(msg);
                continue;
            }
            setupForeignKeyToTable(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList,
                    fixedSuffix);
            showResult(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList);
        }
    }

    protected void setupForeignKeyToTable(String foreignTableName, List<String> foreignColumnNameList,
            String fixedCondition, Table table, List<String> localColumnNameList, String fixedSuffix) {
        final ForeignKey fk = new ForeignKey();
        fk.setForeignTableName(foreignTableName);
        fk.addReference(localColumnNameList, foreignColumnNameList);
        if (fixedCondition != null && fixedCondition.trim().length() > 0) {
            fk.setFixedCondition(fixedCondition);
        }
        if (fixedSuffix != null && fixedSuffix.trim().length() > 0) {
            fk.setFixedSuffix(fixedSuffix);
        }
        table.addForeignKey(fk);
        getTable(foreignTableName).addReferrer(fk);
        for (String foreignColumnName : foreignColumnNameList) {
            final Column foreignColumn = getTable(foreignTableName).getColumn(foreignColumnName);
            foreignColumn.addReferrer(fk);
        }
    }

    protected void showResult(String foreignTableName, List<String> foreignColumnNameList, String fixedCondition,
            Table table, List<String> localColumnNameList) {
        String msg = "       Add foreign key " + table.getName() + "." + localColumnNameList;
        if (fixedCondition != null && fixedCondition.trim().length() > 0) {
            msg = msg + " to " + foreignTableName + "." + foreignColumnNameList + " with " + fixedCondition;
        } else {
            msg = msg + " to " + foreignTableName + "." + foreignColumnNameList;
        }
        _log.info(msg);
    }

    protected List<String> getForeignColumnNameList(String foreignName, final String foreignTableName) {
        List<String> foreignColumnNameList = getForeignColumnNameList(foreignName);
        if (foreignColumnNameList == null || foreignColumnNameList.isEmpty()) {
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
        List<String> localColumnNameList = getLocalColumnNameList(foreignName);
        if (localColumnNameList == null || localColumnNameList.isEmpty()) {
            localColumnNameList = new ArrayList<String>();
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
        if (!getTable(foreignTableName).containsColumnsByFlexibleName(foreignColumnNameList)) {
            String msg = "Not found column by the foreignColumnNameList: " + foreignColumnNameList;
            msg = msg + " of the foreign table '" + foreignTableName + "'";
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
        if (!getTable(localTableName).containsColumnsByFlexibleName(localColumnNameList)) {
            String msg = "Not found column by the localColumnNameList: " + localColumnNameList;
            msg = msg + " of the local table '" + localTableName + "'";
            msg = msg + " additionalForeignKeyMap=" + getAdditionalForeignKeyMap();
            throw new IllegalStateException(msg);
        }
    }

    protected String getLocalTableName(String foreignKeyName) {
        return getProperties().findLocalTableName(foreignKeyName);
    }

    protected String getForeignTableName(String foreignKeyName) {
        return getProperties().findForeignTableName(foreignKeyName);
    }

    protected List<String> getLocalColumnNameList(String foreignKeyName) {
        return getProperties().findLocalColumnNameList(foreignKeyName);
    }

    protected List<String> getForeignColumnNameList(String foreignKeyName) {
        return getProperties().findForeignColumnNameList(foreignKeyName);
    }

    protected String getFixedCondition(String foreignKeyName) {
        return getProperties().findFixedCondition(foreignKeyName);
    }

    protected String getFixedSuffix(String foreignKeyName) {
        return getProperties().findFixedSuffix(foreignKeyName);
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

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected Database getDatabase() {
        return _database;
    }
}