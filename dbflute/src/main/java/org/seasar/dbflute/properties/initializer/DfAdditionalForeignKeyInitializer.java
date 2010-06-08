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
package org.seasar.dbflute.properties.initializer;

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
import org.seasar.dbflute.exception.DfPropertySettingColumnNotFoundException;
import org.seasar.dbflute.exception.DfPropertySettingTableNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
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
        _log.info("/=======================================");
        _log.info("...Initializing additional foreign keys.");

        final Map<String, Map<String, String>> additionalForeignKeyMap = getAdditionalForeignKeyMap();
        final Set<String> foreignKeyNameKeySet = additionalForeignKeyMap.keySet();
        for (String foreignKeyName : foreignKeyNameKeySet) {
            final String foreignTableName = getForeignTableName(foreignKeyName);
            assertForeignTable(foreignKeyName, foreignTableName);
            final List<String> foreignColumnNameList = getForeignColumnNameList(foreignKeyName, foreignTableName);
            assertForeignTableColumn(foreignKeyName, foreignTableName, foreignColumnNameList);
            final String localTableName = getLocalTableName(foreignKeyName);

            _log.info(foreignKeyName);
            if (localTableName.equals("$$ALL$$") || localTableName.equals("*")) { // "*" is for compatible
                processAllTableFK(foreignKeyName, foreignTableName, foreignColumnNameList);
            } else {
                processOneTableFK(foreignKeyName, localTableName, foreignTableName, foreignColumnNameList);
            }
        }
        _log.info("==========/");
    }

    protected void processAllTableFK(String foreignKeyName, String foreignTableName, List<String> foreignColumnNameList) {
        final String fixedCondition = getFixedCondition(foreignKeyName);
        final String fixedSuffix = getFixedSuffix(foreignKeyName);
        for (final Table table : getTables()) {
            final String localTableName = table.getName();
            final List<String> localColumnNameList = getLocalColumnNameList(foreignKeyName, foreignTableName,
                    foreignColumnNameList, localTableName, false);
            if (localColumnNameList == null || !table.containsColumn(localColumnNameList)) {
                continue;
            }
            if (table.existsForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList, fixedSuffix)) {
                String msg = "The foreign key has already set up: ";
                _log.info(msg + foreignKeyName + "(" + fixedSuffix + ")");
                continue;
            }
            final String currentForeignKeyName = foreignKeyName + "_" + table.getName();
            setupForeignKeyToTable(currentForeignKeyName, foreignTableName, foreignColumnNameList, fixedCondition,
                    table, localColumnNameList, fixedSuffix);
            showResult(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList);
        }
    }

    protected void processOneTableFK(String foreignKeyName, String localTableName, String foreignTableName,
            List<String> foreignColumnNameList) {
        assertLocalTable(foreignKeyName, localTableName);
        final String fixedCondition = getFixedCondition(foreignKeyName);
        final String fixedSuffix = getFixedSuffix(foreignKeyName);
        final Table table = getTable(localTableName);
        final List<String> localColumnNameList = getLocalColumnNameList(foreignKeyName, foreignTableName,
                foreignColumnNameList, localTableName, true);
        assertLocalTableColumn(foreignKeyName, localTableName, localColumnNameList);
        if (table.existsForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList, fixedSuffix)) {
            String msg = "The foreign key has already set up: ";
            _log.info(msg + foreignKeyName + "(" + fixedSuffix + ")");
            return;
        }
        setupForeignKeyToTable(foreignKeyName, foreignTableName, foreignColumnNameList, fixedCondition, table,
                localColumnNameList, fixedSuffix);
        showResult(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList);
    }

    protected void setupForeignKeyToTable(String foreignKeyName, String foreignTableName,
            List<String> foreignColumnNameList, String fixedCondition, Table table, List<String> localColumnNameList,
            String fixedSuffix) {
        final ForeignKey fk = new ForeignKey();
        fk.setName(foreignKeyName);
        fk.setForeignTableName(foreignTableName);
        fk.addReference(localColumnNameList, foreignColumnNameList);
        fk.setAdditionalForeignKey(true);
        if (fixedCondition != null && fixedCondition.trim().length() > 0) {
            fk.setFixedCondition(fixedCondition);
        }
        if (fixedSuffix != null && fixedSuffix.trim().length() > 0) {
            fk.setFixedSuffix(fixedSuffix);
        }
        table.addForeignKey(fk);
        final boolean canBeReferrer = getTable(foreignTableName).addReferrer(fk);
        if (canBeReferrer) {
            for (String foreignColumnName : foreignColumnNameList) {
                final Column foreignColumn = getTable(foreignTableName).getColumn(foreignColumnName);
                foreignColumn.addReferrer(fk);
            }
        } else {
            String msg = "  *Cannot add referrer!";
            _log.info(msg);
        }
    }

    protected void showResult(String foreignTableName, List<String> foreignColumnNameList, String fixedCondition,
            Table table, List<String> localColumnNameList) {
        String msg = "  Add foreign key " + table.getName() + "." + localColumnNameList;
        if (fixedCondition != null && fixedCondition.trim().length() > 0) {
            msg = msg + " to " + foreignTableName + "." + foreignColumnNameList;
            _log.info(msg);
            String withFixedCondition = "  with " + fixedCondition;
            _log.info(withFixedCondition);
        } else {
            msg = msg + " to " + foreignTableName + "." + foreignColumnNameList;
            _log.info(msg);
        }
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

    protected List<String> getLocalColumnNameList(String foreignKeyName, final String foreignTableName,
            List<String> foreignColumnNameList, final String localTableName, boolean errorIfNotFound) {
        List<String> localColumnNameList = getLocalColumnNameList(foreignKeyName);
        if (localColumnNameList != null && !localColumnNameList.isEmpty()) {
            return localColumnNameList;
        }
        // searching local columns by foreign columns (PK)
        localColumnNameList = new ArrayList<String>();
        final Table localTable = getTable(localTableName);
        for (String foreignColumnName : foreignColumnNameList) {
            final Column column = localTable.getColumn(foreignColumnName);
            if (column != null) {
                localColumnNameList.add(column.getName());
                continue;
            }
            if (errorIfNotFound) {
                final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
                br.addNotice("Not found local column by the foreign column of additionalForeignKey.");
                br.addItem("Advice");
                br.addElement("When localColumnName is omitted, the local table should have");
                br.addElement("the columns that are same as primary keys of foreign table.");
                br.addItem("Additional FK");
                br.addElement(foreignKeyName);
                br.addItem("Local Table");
                br.addElement(localTableName);
                br.addItem("Foreign Table");
                br.addElement(foreignTableName);
                br.addItem("Foreign Column");
                br.addElement(foreignColumnNameList);
                final String msg = br.buildExceptionMessage();
                throw new DfPropertySettingColumnNotFoundException(msg);
            } else {
                return null;
            }
        }
        return localColumnNameList;
    }

    protected void assertForeignTable(final String foreignKeyName, final String foreignTableName) {
        if (getTable(foreignTableName) != null) {
            return;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found table by the foreignTableName of additionalForeignKey.");
        br.addItem("Additional FK");
        br.addElement(foreignKeyName);
        br.addItem("NotFound Table");
        br.addElement(foreignTableName);
        final String msg = br.buildExceptionMessage();
        throw new DfPropertySettingTableNotFoundException(msg);
    }

    protected void assertForeignTableColumn(final String foreignKeyName, final String foreignTableName,
            List<String> foreignColumnNameList) {
        if (getTable(foreignTableName).containsColumn(foreignColumnNameList)) {
            return;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found column by the foreignColumnName of additionalForeignKey.");
        br.addItem("Additional FK");
        br.addElement(foreignKeyName);
        br.addItem("Foreign Table");
        br.addElement(foreignTableName);
        br.addItem("NotFound Column");
        br.addElement(foreignColumnNameList);
        final String msg = br.buildExceptionMessage();
        throw new DfPropertySettingColumnNotFoundException(msg);
    }

    protected void assertLocalTable(final String foreignKeyName, final String localTableName) {
        if (getTable(localTableName) != null) {
            return;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found table by the localTableName of additionalForeignKey.");
        br.addItem("Additional FK");
        br.addElement(foreignKeyName);
        br.addItem("NotFound Table");
        br.addElement(localTableName);
        final String msg = br.buildExceptionMessage();
        throw new DfPropertySettingTableNotFoundException(msg);
    }

    protected void assertLocalTableColumn(final String foreignKeyName, final String localTableName,
            List<String> localColumnNameList) {
        if (getTable(localTableName).containsColumn(localColumnNameList)) {
            return;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found column by the localColumnName of additionalForeignKey.");
        br.addItem("Additional FK");
        br.addElement(foreignKeyName);
        br.addItem("Local Table");
        br.addElement(localTableName);
        br.addItem("NotFound Column");
        br.addElement(localColumnNameList);
        final String msg = br.buildExceptionMessage();
        throw new DfPropertySettingColumnNotFoundException(msg);
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