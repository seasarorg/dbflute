/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfPropertySettingColumnNotFoundException;
import org.seasar.dbflute.exception.DfPropertySettingTableNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.properties.DfAdditionalForeignKeyProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

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
            if (localTableName.equals("$$ALL$$") || localTableName.equals("*")) { // "*" is for compatibility
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
        final String fixedInline = getFixedInline(foreignKeyName);
        final String fixedReferrer = getFixedReferrer(foreignKeyName);
        final String comment = getComment(foreignKeyName);

        // for check about same-column self reference
        final Table foreignTable = getTable(foreignTableName);
        final StringSet foreignColumnSet = StringSet.createAsFlexible();
        foreignColumnSet.addAll(foreignColumnNameList);

        for (final Table localTable : getTableList()) {
            final String localTableName = localTable.getTableDbName();
            final List<String> localColumnNameList = getLocalColumnNameList(foreignKeyName, foreignTableName,
                    foreignColumnNameList, localTableName, false);
            if (!localTable.containsColumn(localColumnNameList)) {
                continue;
            }

            // check same-column self reference
            final StringSet localColumnSet = StringSet.createAsFlexible();
            localColumnSet.addAll(localColumnNameList);
            final boolean selfReference = localTable.getTableDbName().equals(foreignTable.getTableDbName());
            if (selfReference && localColumnSet.equalsUnderCharOption(foreignColumnSet)) {
                continue;
            }

            // check same foreign key existence
            if (localTable.existsForeignKey(foreignTableName, localColumnNameList, foreignColumnNameList, fixedSuffix)) {
                _log.info("The foreign key has already set up: " + foreignKeyName + "(" + fixedSuffix + ")");
                continue;
            }

            final String currentForeignKeyName = foreignKeyName + "_" + toConstraintPart(localTableName);
            setupForeignKeyToTable(currentForeignKeyName, foreignTableName, foreignColumnNameList, fixedCondition,
                    localTable, localColumnNameList, fixedSuffix, fixedInline, fixedReferrer, comment);
            showResult(foreignTableName, foreignColumnNameList, fixedCondition, localTable, localColumnNameList);
        }
    }

    protected String toConstraintPart(String tableDbName) {
        return Srl.replace(tableDbName, ".", "_");
    }

    protected void processOneTableFK(String foreignKeyName, String localTableName, String foreignTableName,
            List<String> foreignColumnNameList) {
        assertLocalTable(foreignKeyName, localTableName);
        final String fixedCondition = getFixedCondition(foreignKeyName);
        final String fixedSuffix = getFixedSuffix(foreignKeyName);
        final String fixedInline = getFixedInline(foreignKeyName);
        final String fixedReferrer = getFixedReferrer(foreignKeyName);
        final String comment = getComment(foreignKeyName);
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
                localColumnNameList, fixedSuffix, fixedInline, fixedReferrer, comment);
        showResult(foreignTableName, foreignColumnNameList, fixedCondition, table, localColumnNameList);
    }

    protected void setupForeignKeyToTable(String foreignKeyName, String foreignTableName,
            List<String> foreignColumnNameList, String fixedCondition, Table table, List<String> localColumnNameList,
            String fixedSuffix, String fixedInline, String fixedReferrer, String comment) {
        // set up foreign key instance
        final ForeignKey fk = createAdditionalForeignKey(foreignKeyName, foreignTableName, localColumnNameList,
                foreignColumnNameList, fixedCondition, fixedSuffix, fixedInline, fixedReferrer, comment);
        table.addForeignKey(fk);

        // set up referrer instance
        final Table foreignTable = getTable(foreignTableName);
        final boolean canBeReferrer = foreignTable.addReferrer(fk);
        if (canBeReferrer) {
            for (String foreignColumnName : foreignColumnNameList) {
                final Column foreignColumn = foreignTable.getColumn(foreignColumnName);
                foreignColumn.addReferrer(fk);
            }
        } else {
            _log.info("  *Referrer setting was not allowed in this case");
        }

        // set up implicit reverse foreign key if fixed condition is valid
        // (and if a same-structured FK does not exist)
        // because biz-one-to-one needs reverse foreign key for ConditionBean's Specify
        // ...
        // ...
        // Sorry, I forgot the detail of the reason...
        if (fk.hasFixedCondition() && !isSuppressImplicitReverseFK(foreignKeyName)) {
            processImplicitReverseForeignKey(table, foreignTable, localColumnNameList, foreignColumnNameList);
        }
    }

    protected void processImplicitReverseForeignKey(Table table, Table foreignTable, List<String> localColumnNameList,
            List<String> foreignColumnNameList) { // called only when a fixed condition exists
        // name is "FK_ + foreign + local" because it's reversed
        final String localTableName = table.getTableDbName();
        final String foreignTableName = foreignTable.getTableDbName();
        final String reverseName = buildReverseFKName(localTableName, foreignTableName);
        final String comment = "This relation is auto-detected as implicit reverse FK.";
        final List<Column> primaryKey = table.getPrimaryKey();
        if (localColumnNameList.size() != primaryKey.size()) {
            return; // may be biz-many-to-one (not biz-one-to-one)
        }
        for (String localColumnName : localColumnNameList) {
            final Column localColumn = table.getColumn(localColumnName);
            if (!localColumn.isPrimaryKey()) { // check PK just in case
                return; // basically no way because a fixed condition exists
                // and FK to unique key is unsupported
            }
        }
        // here all local columns are elements of primary key
        if (foreignTable.existsForeignKey(localTableName, foreignColumnNameList, localColumnNameList)) {
            return; // same-structured FK already exists
        }
        String fixedSuffix = null;
        if (foreignTable.hasForeignTableContainsOne(table)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("By");
            for (String foreignColumnName : foreignColumnNameList) {
                sb.append(foreignTable.getColumn(foreignColumnName).getJavaName());
            }
            fixedSuffix = sb.toString();
        }
        final ForeignKey fk = createAdditionalForeignKey(reverseName, localTableName, foreignColumnNameList,
                localColumnNameList, null, fixedSuffix, null, null, comment);
        fk.setImplicitReverseForeignKey(true);
        foreignTable.addForeignKey(fk);
        final boolean canBeReferrer = table.addReferrer(fk);
        if (canBeReferrer) { // basically true because foreign columns are PK and no fixed condition
            for (String localColumnName : localColumnNameList) {
                final Column localColumn = table.getColumn(localColumnName);
                localColumn.addReferrer(fk);
            }
            _log.info("  *Reversed FK was also made implicitly");
        }
    }

    protected String buildReverseFKName(String localTableName, String foreignTableName) {
        return "FK_" + toConstraintPart(foreignTableName) + "_" + toConstraintPart(localTableName) + "_IMPLICIT";
    }

    protected ForeignKey createAdditionalForeignKey(String foreignKeyName, String foreignTableName,
            List<String> localColumnNameList, List<String> foreignColumnNameList, String fixedCondition,
            String fixedSuffix, String fixedInline, String fixedReferrer, String comment) {
        final ForeignKey fk = new ForeignKey();
        fk.setName(foreignKeyName);
        if (foreignTableName.contains(".")) {
            final String foreignSchema = Srl.substringLastFront(foreignTableName, ".");
            final String foreignTablePureName = Srl.substringLastRear(foreignTableName, ".");
            fk.setForeignSchema(UnifiedSchema.createAsDynamicSchema(foreignSchema));
            fk.setForeignTablePureName(foreignTablePureName);
        } else {
            fk.setForeignSchema(getDatabase().getDatabaseSchema()); // main schema
            fk.setForeignTablePureName(foreignTableName);
        }
        fk.addReference(localColumnNameList, foreignColumnNameList);
        fk.setAdditionalForeignKey(true);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(fixedCondition)) {
            fk.setFixedCondition(fixedCondition);
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(fixedSuffix)) {
            fk.setFixedSuffix(fixedSuffix);
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(fixedInline)) {
            fk.setFixedInline(fixedInline.equalsIgnoreCase("true"));
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(fixedReferrer)) {
            fk.setFixedReferrer(fixedReferrer.equalsIgnoreCase("true"));
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(comment)) {
            fk.setComment(comment);
        }
        return fk;
    }

    protected void showResult(String foreignTableName, List<String> foreignColumnNameList, String fixedCondition,
            Table table, List<String> localColumnNameList) {
        String msg = "  Add foreign key " + table.getTableDbName() + "." + localColumnNameList;
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

    protected List<String> getForeignColumnNameList(String foreignKeyName, final String foreignTableName) {
        List<String> foreignColumnNameList = getForeignColumnNameList(foreignKeyName);
        if (foreignColumnNameList != null && !foreignColumnNameList.isEmpty()) {
            return foreignColumnNameList;
        }
        foreignColumnNameList = DfCollectionUtil.newArrayList();
        final List<Column> foreignPrimaryKeyList = getTable(foreignTableName).getPrimaryKey();
        if (foreignPrimaryKeyList.isEmpty()) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Not found primary key on the foreign table of additionalForeignKey.");
            br.addItem("Advice");
            br.addElement("Foreign table should have primary keys.");
            br.addItem("Additional FK");
            br.addElement(foreignKeyName);
            br.addItem("Foreign Table");
            br.addElement(foreignTableName);
            final String msg = br.buildExceptionMessage();
            throw new DfIllegalPropertySettingException(msg);
        }
        for (Column column : foreignPrimaryKeyList) {
            foreignColumnNameList.add(column.getName());
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
        localColumnNameList = DfCollectionUtil.newArrayList();
        final Table localTable = getTable(localTableName);
        for (String foreignColumnName : foreignColumnNameList) {
            final Column column = localTable.getColumn(foreignColumnName);
            if (column != null) {
                // no check about same-column self reference here
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
                return DfCollectionUtil.newArrayList(); // means not found
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

    protected String getFixedInline(String foreignKeyName) {
        return getProperties().findFixedInline(foreignKeyName);
    }

    protected String getFixedReferrer(String foreignKeyName) {
        return getProperties().findFixedReferrer(foreignKeyName);
    }

    protected String getComment(String foreignKeyName) {
        return getProperties().findComment(foreignKeyName);
    }

    protected boolean isSuppressImplicitReverseFK(String foreignKeyName) {
        return getProperties().isSuppressImplicitReverseFK(foreignKeyName);
    }

    protected Map<String, Map<String, String>> getAdditionalForeignKeyMap() {
        return getProperties().getAdditionalForeignKeyMap();
    }

    protected Table getTable(String tableName) {
        return getDatabase().getTable(tableName);
    }

    protected List<Table> getTableList() {
        return getDatabase().getTableList();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected Database getDatabase() {
        return _database;
    }
}