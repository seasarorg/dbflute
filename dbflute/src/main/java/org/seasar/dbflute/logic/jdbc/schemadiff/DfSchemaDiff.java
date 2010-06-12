package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Index;
import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.Unique;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfSchemaDiff extends DfAbstractDiff {

    //[diff-date] = map:{
    //    ; diffDate = 2010/12/12 12:34:56
    //    ; tableCount = map:{ next = 123 ; previous = 145}
    //    ; tableDiff = map:{
    //        ; [table-name] = map:{
    //            ; diffType = [ADD or CHANGE or DELETE]
    //            ; unifiedSchemaDiff = map:{ next = [schema] ; previous = [schema] }
    //            ; objectTypeDiff = map:{ next = [type] ; previous = [type] }
    //            ; columnDiff = map:{
    //                [column-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; dbTypeDiff = map:{next = [db-type-name]; previous = [db-type-name]}
    //                    ; columnSizeDiff = map:{next = [column-size&digit]; previous = [column-size&digit]}
    //                    ; defaultValueDiff = map:{next = [default-value]; previous = [default-value]}
    //                    ; notNullDiff = map:{next = [true or false] ; previous = [true or false]}
    //                    ; autoIncrementDiff = map:{next = [true or false] ; previous = [true or false]}
    //                }
    //            }
    //            ; primaryKeyDiff = map:{
    //                ; [pk-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //            ; foreingkKeyDiff = map:{
    //                ; [fk-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                    ; foreignTableDiff = map:{
    //                        ; next = [table-name]
    //                        ; previous = [table-name]
    //                    }
    //                }
    //            }
    //            ; uniqueKeyDiff = map:{
    //                ; [uq-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //            ; indexDiff = map:{
    //                ; [index-name] = map:{
    //                    ; diffType = [ADD or CHANGE or DELETE]
    //                    ; columnDiff = map:{
    //                        ; next = [column-name, ...]
    //                        ; previous = [column-name, ...]
    //                    }
    //                }
    //            }
    //        }
    //    }
    //}

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String DIFF_DATE_KEY = "diffDate";
    public static final String DIFF_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String TABLE_COUNT_KEY = "tableCount";
    public static final String TABLE_DIFF_KEY = "tableDiff";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                           Load Schema
    //                                           -----------
    protected Database _nextDb; // not null after next loading
    protected Database _previousDb; // not null after previous loading
    protected Integer _previousTableCount; // not null after previous loading
    protected boolean _firstTime; // judged when loading previous schema
    protected boolean _loadingFailure; // judged when loading previous schema

    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected Date _diffDate; // not null after loading next schema
    protected DfNextPreviousDiff _tableCountDiff; // not null after next loading

    // -----------------------------------------------------
    //                                            Table Diff
    //                                            ----------
    protected final List<DfTableDiff> _tableDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _addedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _changedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _deletedTableDiffList = DfCollectionUtil.newArrayList();

    protected List<NestDiffSetupper> _nestDiffList = DfCollectionUtil.newArrayList();
    {
        _nestDiffList.add(new NestDiffSetupper() {
            public String propertyName() {
                return TABLE_DIFF_KEY;
            }

            public List<? extends DfNestDiff> provide() {
                return _tableDiffAllList;
            }

            public void setup(Map<String, Object> diff) {
                addTableDiff(createTableDiff(diff));
            }
        });
    }

    // ===================================================================================
    //                                                                         Load Schema
    //                                                                         ===========
    public void loadPreviousSchema() { // before loading next schema
        final DfSchemaXmlReader reader = createSchemaXmlReader();
        try {
            reader.read();
        } catch (FileNotFoundException normal) {
            _firstTime = true;
            return;
        } catch (IOException e) {
            _loadingFailure = true;
            handleException(e);
        }
        try {
            _previousDb = reader.getSchemaData().getDatabase();
        } catch (EngineException e) {
            _loadingFailure = true;
            handleException(e);
        }
        _previousTableCount = _previousDb.getTableList().size();
    }

    public void loadNextSchema() { // after loading previous schema
        if (isFirstTime()) {
            String msg = "You should not call this because of first time.";
            throw new IllegalStateException(msg);
        }
        if (_previousDb == null) {
            String msg = "You should not call this because of previous not loaded.";
            throw new IllegalStateException(msg);
        }
        final DfSchemaXmlReader reader = createSchemaXmlReader();
        try {
            reader.read();
        } catch (IOException e) {
            handleException(e);
        }
        try {
            _nextDb = reader.getSchemaData().getDatabase();
        } catch (EngineException e) {
            handleException(e);
        }
        _diffDate = new Date(DfSystemUtil.currentTimeMillis());
        final int nextTableCount = _nextDb.getTableList().size();
        _tableCountDiff = createNextPreviousDiff(nextTableCount, _previousTableCount);
    }

    protected void handleException(Exception e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to load schema XML.");
        br.addItem("SchemaXML");
        br.addElement(getSchemaXmlFilePath());
        br.addItem("DatabaseType");
        br.addElement(getDatabaseType());
        br.addItem("Exception");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg, e);
    }

    // ===================================================================================
    //                                                                        Analyze Diff
    //                                                                        ============
    public void analyzeDiff() {
        processAddedTable();
        processChangedTable();
        processDeletedTable();
    }

    // -----------------------------------------------------
    //                                         Table Process
    //                                         -------------
    protected void processAddedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table table : tableList) {
            final Table found = findPreviousTable(table);
            if (found == null || !isSameTableName(table, found)) { // added
                addTableDiff(DfTableDiff.createAdded(table.getName()));
            }
        }
    }

    protected void processChangedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table next : tableList) {
            final Table previous = findPreviousTable(next);
            if (previous == null || !isSameTableName(next, previous)) {
                continue;
            }
            // found
            final DfTableDiff tableDiff = DfTableDiff.createChanged(next.getName());
            diffNextPrevious(next, previous, tableDiff, new StringNextPreviousDiffer<Table, DfTableDiff>() {
                public String provide(Table obj) {
                    return obj.getUnifiedSchema().getCatalogSchema();
                }

                public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setUnifiedSchemaDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, tableDiff, new StringNextPreviousDiffer<Table, DfTableDiff>() {
                public String provide(Table obj) {
                    return obj.getType();
                }

                public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setObjectTypeDiff(nextPreviousDiff);
                }
            });
            processColumn(tableDiff, next, previous);
            processPrimaryKey(tableDiff, next, previous);
            processForeignKey(tableDiff, next, previous);
            processUniqueKey(tableDiff, next, previous);
            processIndex(tableDiff, next, previous);
            if (tableDiff.hasDiff()) { // changed
                addTableDiff(tableDiff);
            }
        }
    }

    protected void processDeletedTable() {
        final List<Table> tableList = _previousDb.getTableList();
        for (Table table : tableList) {
            final Table found = findNextTable(table);
            if (found == null || !isSameTableName(table, found)) { // deleted
                addTableDiff(DfTableDiff.createDeleted(table.getName()));
            }
        }
    }

    protected <TYPE> void diffNextPrevious(Table next, Table previous, DfTableDiff diff,
            NextPreviousDiffer<Table, DfTableDiff, TYPE> setupper) {
        final TYPE nextValue = setupper.provide(next);
        final TYPE previousValue = setupper.provide(previous);
        if (!setupper.isMatch(nextValue, previousValue)) {
            setupper.diff(diff, createNextPreviousDiff(nextValue.toString(), previousValue.toString()));
        }
    }

    protected boolean isSameTableName(Table next, Table previous) {
        return isSame(next.getName(), previous.getName());
    }

    // -----------------------------------------------------
    //                                        Column Process
    //                                        --------------
    protected void processColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedColumn(tableDiff, nextTable, previousTable);
        processChangedColumn(tableDiff, nextTable, previousTable);
        processDeletedColumn(tableDiff, nextTable, previousTable);
    }

    protected void processAddedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column column : columnList) {
            final Column found = previousTable.getColumn(column.getName());
            if (found == null || !isSameColumnName(column, found)) { // added
                tableDiff.addColumnDiff(DfColumnDiff.createAdded(column.getName()));
            }
        }
    }

    protected void processChangedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column next : columnList) {
            final Column previous = previousTable.getColumn(next.getName());
            if (previous == null || !isSameColumnName(next, previous)) {
                continue;
            }
            // found
            final DfColumnDiff columnDiff = DfColumnDiff.createChanged(next.getName());
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getDbType();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDbTypeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getColumnSize();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setColumnSizeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new StringNextPreviousDiffer<Column, DfColumnDiff>() {
                public String provide(Column obj) {
                    return obj.getDefaultValue();
                }

                @Override
                public boolean isMatch(String next, String previous) {
                    if (super.isMatch(next, previous)) {
                        return true;
                    }
                    final boolean bothValid = next != null && previous != null;
                    if (bothValid && getBasicProperties().isDatabaseH2()) {
                        if (Srl.hasKeywordAllIgnoreCase("SYSTEM_SEQUENCE", next, previous)) {
                            return true;
                        }
                    }
                    return false;
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDefaultValueDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new BooleanNextPreviousDiffer<Column, DfColumnDiff>() {
                public Boolean provide(Column obj) {
                    return obj.isNotNull();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setNotNullDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new BooleanNextPreviousDiffer<Column, DfColumnDiff>() {
                public Boolean provide(Column obj) {
                    return obj.isAutoIncrement();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setAutoIncrementDiff(nextPreviousDiff);
                }
            });
            if (columnDiff.hasDiff()) { // changed
                tableDiff.addColumnDiff(columnDiff);
            }
        }
    }

    protected void processDeletedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = previousTable.getColumnList();
        for (Column column : columnList) {
            final Column found = nextTable.getColumn(column.getName());
            if (found == null || !isSameColumnName(column, found)) { // deleted
                tableDiff.addColumnDiff(DfColumnDiff.createDeleted(column.getName()));
            }
        }
    }

    protected <ITEM, TYPE> void diffNextPrevious(Column next, Column previous, DfColumnDiff diff,
            NextPreviousDiffer<Column, DfColumnDiff, TYPE> differ) {
        final TYPE nextValue = differ.provide(next);
        final TYPE previousValue = differ.provide(previous);
        if (!differ.isMatch(nextValue, previousValue)) {
            differ.diff(diff, createNextPreviousDiff(nextValue.toString(), previousValue.toString()));
        }
    }

    protected boolean isSameColumnName(Column next, Column previous) {
        return isSame(next.getName(), previous.getName());
    }

    // -----------------------------------------------------
    //                                    PrimaryKey Process
    //                                    ------------------
    protected void processPrimaryKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        if (!nextTable.hasPrimaryKey() && !previousTable.hasPrimaryKey()) {
            return; // both no PK
        }
        final String noNamePKName = "(PK)";
        final String nextName = nextTable.getPrimaryKeyConstraintName();
        final String previousName = previousTable.getPrimaryKeyConstraintName();
        if (nextName == null && previousName == null) { // has PK but both no name
            if (hasSameStructuredPrimaryKey(nextTable, previousTable)) {
                return; // no changed
            } else {
                final String constraintName = noNamePKName;
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
                processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
            }
        }
        final String constraintName = nextName != null ? nextName : noNamePKName;
        if (isSame(nextName, previousName)) {
            final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
            processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
        } else if (hasSameStructuredPrimaryKey(nextTable, previousTable)) {
            final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
            final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
            primaryKeyDiff.setNameDiff(nameDiff);
            tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
        } else {
            if (nextName == null) { // deleted
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createDeleted(previousName));
                return;
            } else if (previousName == null) { // added
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createAdded(nextName));
                return;
            } else { // both are not null and different structure
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(constraintName);
                final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
                primaryKeyDiff.setNameDiff(nameDiff);
                processPrimaryKeyColumnDiff(tableDiff, nextTable, previousTable, primaryKeyDiff, constraintName);
            }
        }
    }

    protected boolean hasSameStructuredPrimaryKey(Table nextTable, Table previousTable) {
        final String nextCommaString = nextTable.getPrimaryKeyNameCommaString();
        final String previousCommaString = previousTable.getPrimaryKeyNameCommaString();
        return Srl.equalsPlain(nextCommaString, previousCommaString);
    }

    protected void processPrimaryKeyColumnDiff(DfTableDiff tableDiff, Table nextTable, Table previousTable,
            DfPrimaryKeyDiff primaryKeyDiff, String constraintName) {
        final String nextColumn = nextTable.getPrimaryKeyNameCommaString();
        final String previousColumn = previousTable.getPrimaryKeyNameCommaString();
        if (!isSame(nextColumn, previousColumn)) {
            final DfNextPreviousDiff columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            primaryKeyDiff.setColumnDiff(columnDiff);
        }
        if (primaryKeyDiff.hasDiff()) { // changed
            tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
        }
    }

    // -----------------------------------------------------
    //                                    ForeignKey Process
    //                                    ------------------
    protected void processForeignKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new ForeignKeyDiffer(tableDiff));
    }

    protected class ForeignKeyDiffer extends BasicConstraintKeyDiffer<ForeignKey> {

        public ForeignKeyDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(ForeignKey key) {
            return key.getName();
        }

        public List<ForeignKey> keyList(Table table) {
            return DfCollectionUtil.newArrayList(table.getForeignKeys());
        }

        public String column(ForeignKey key) {
            return key.getLocalColumnNameCommaString();
        }

        @Override
        public boolean isSameStructure(ForeignKey next, ForeignKey previous) {
            if (isSame(column(next), column(previous))) {
                if (isSame(next.getForeignTable().getName(), previous.getForeignTable().getName())) {
                    return true;
                }
            }
            return false;
        }

        public void diff(ForeignKey nextKey, ForeignKey previousKey, DfNextPreviousDiff nameDiff,
                DfNextPreviousDiff columnDiff) {
            final DfForeignKeyDiff foreignKeyDiff = DfForeignKeyDiff.createChanged(nextKey.getName());
            foreignKeyDiff.setNameDiff(nameDiff);
            foreignKeyDiff.setColumnDiff(columnDiff);

            // foreignTable
            final String nextFKTable = nextKey.getForeignTableName();
            final String previousFKTable = previousKey.getForeignTableName();
            if (!isSame(nextFKTable, previousFKTable)) {
                final DfNextPreviousDiff fkTableDiff = createNextPreviousDiff(nextFKTable, previousFKTable);
                foreignKeyDiff.setForeignTableDiff(fkTableDiff);
            }

            if (foreignKeyDiff.hasDiff()) { // changed
                _tableDiff.addForeignKeyDiff(foreignKeyDiff);
            }
        }
    }

    // -----------------------------------------------------
    //                                     UniqueKey Process
    //                                     -----------------
    protected void processUniqueKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new UniqueKeyDiffer(tableDiff));
    }

    protected class UniqueKeyDiffer extends BasicConstraintKeyDiffer<Unique> {

        public UniqueKeyDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(Unique key) {
            return key.getName();
        }

        public List<Unique> keyList(Table table) {
            return table.getUniqueList();
        }

        public String column(Unique key) {
            return buildCommaString(key.getIndexColumnMap().values());
        }

        public void diff(Unique nextKey, Unique previousKey, DfNextPreviousDiff nameDiff, DfNextPreviousDiff columnDiff) {
            final DfUniqueKeyDiff uniqueKeyDiff = DfUniqueKeyDiff.createChanged(nextKey.getName());
            uniqueKeyDiff.setNameDiff(nameDiff);
            uniqueKeyDiff.setColumnDiff(columnDiff);
            if (uniqueKeyDiff.hasDiff()) { // changed
                _tableDiff.addUniqueKeyDiff(uniqueKeyDiff);
            }
        }
    }

    // -----------------------------------------------------
    //                                         Index Process
    //                                         -------------
    protected void processIndex(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processConstraintKey(nextTable, previousTable, new IndexDiffer(tableDiff));
    }

    protected class IndexDiffer extends BasicConstraintKeyDiffer<Index> {

        public IndexDiffer(DfTableDiff tableDiff) {
            super(tableDiff);
        }

        public String constraintName(Index key) {
            return key.getName();
        }

        public List<Index> keyList(Table table) {
            return table.getIndexList();
        }

        public String column(Index key) {
            return buildCommaString(key.getIndexColumnMap().values());
        }

        public void diff(Index nextKey, Index previousKey, DfNextPreviousDiff nameDiff, DfNextPreviousDiff columnDiff) {
            final DfIndexDiff indexDiff = DfIndexDiff.createChanged(nextKey.getName());
            indexDiff.setNameDiff(nameDiff);
            indexDiff.setColumnDiff(columnDiff);
            if (indexDiff.hasDiff()) { // changed
                _tableDiff.addIndexDiff(indexDiff);
            }
        }
    }

    // -----------------------------------------------------
    //                                    Constraint Process
    //                                    ------------------
    protected <KEY> void processConstraintKey(Table nextTable, Table previousTable, ConstraintKeyDiffer<KEY> differ) { // for except PK
        final List<KEY> keyList = differ.keyList(nextTable);
        final Map<String, KEY> nextPreviousMap = DfCollectionUtil.newLinkedHashMap();
        final Map<String, KEY> previousNextMap = DfCollectionUtil.newLinkedHashMap();
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextName == null) {
                continue;
            }
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (differ.isSameConstraintName(nextName, previousName)) { // found
                    nextPreviousMap.put(nextName, previousKey);
                    previousNextMap.put(previousName, nextKey);
                    continue nextLoop;
                }
            }
        }
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextName == null || nextPreviousMap.containsKey(nextName)) {
                continue;
            }
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (previousNextMap.containsKey(previousName)) {
                    continue;
                }
                if (differ.isSameStructure(nextKey, previousKey)) { // found
                    nextPreviousMap.put(nextName, previousKey);
                    previousNextMap.put(previousName, nextKey);
                    continue nextLoop;
                }
            }
        }
        final Set<Entry<String, KEY>> entrySet = nextPreviousMap.entrySet();
        for (Entry<String, KEY> entry : entrySet) {
            final String nextName = entry.getKey();
            final KEY previousKey = entry.getValue();
            final String previousName = differ.constraintName(previousKey);
            final KEY nextKey = previousNextMap.get(previousName);
            processChangedConstraintKeyDiff(nextKey, previousKey, nextName, previousName, differ);
        }

        processAddedConstraintKey(nextTable, previousTable, differ, nextPreviousMap, previousNextMap);
        processDeletedConstraintKey(nextTable, previousTable, differ, nextPreviousMap, previousNextMap);
    }

    protected <KEY> void processChangedConstraintKeyDiff(KEY nextKey, KEY previousKey, String nextName,
            String previousName, ConstraintKeyDiffer<KEY> differ) {
        if (differ.isSameConstraintName(nextName, previousName)) { // same name, different structure
            final String nextColumn = differ.column(nextKey);
            final String previousColumn = differ.column(previousKey);
            DfNextPreviousDiff columnDiff = null;
            if (!isSame(nextColumn, previousColumn)) {
                columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            }
            differ.diff(nextKey, previousKey, null, columnDiff);
        } else { // different name, same structure
            final DfNextPreviousDiff nameDiff = createNextPreviousDiff(nextName, previousName);
            differ.diff(nextKey, previousKey, nameDiff, null);
        }
    }

    protected <KEY> void processAddedConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY> differ, Map<String, KEY> nextPreviousMap, Map<String, KEY> previousNextMap) {
        final List<KEY> keyList = differ.keyList(nextTable);
        for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            if (nextPreviousMap.containsKey(nextName)) {
                continue;
            }
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (previousNextMap.containsKey(previousName)) {
                    continue;
                }
            }
            // added
            differ.diff(nextKey, null, null, null);
        }
    }

    protected <KEY> void processDeletedConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY> differ, Map<String, KEY> nextPreviousMap, Map<String, KEY> previousNextMap) { // for except PK
        final List<KEY> keyList = differ.keyList(previousTable);
        for (KEY previousKey : keyList) {
            final String previousName = differ.constraintName(previousKey);
            if (previousNextMap.containsKey(previousName)) {
                continue;
            }
            for (KEY nextKey : differ.keyList(nextTable)) {
                final String nextName = differ.constraintName(nextKey);
                if (nextPreviousMap.containsKey(nextName)) {
                    continue;
                }
            }
            // deleted
            differ.diff(null, previousKey, null, null);
        }
    }

    protected static interface ConstraintKeyDiffer<KEY> {
        List<KEY> keyList(Table table);

        String constraintName(KEY key);

        String column(KEY key);

        boolean isSameConstraintName(String next, String previous);

        boolean isSameStructure(KEY next, KEY previous);

        void diff(KEY nextKey, KEY previousKey, DfNextPreviousDiff nameDiff, DfNextPreviousDiff columnDiff);
    }

    protected abstract class BasicConstraintKeyDiffer<KEY> implements ConstraintKeyDiffer<KEY> {

        protected DfTableDiff _tableDiff;

        public BasicConstraintKeyDiffer(DfTableDiff tableDiff) {
            _tableDiff = tableDiff;
        }

        protected String buildCommaString(Collection<String> values) {
            final StringBuilder sb = new StringBuilder();
            int index = 0;
            for (String value : values) {
                if (index > 0) {
                    sb.append(", ");
                }
                sb.append(value);
                ++index;
            }
            return sb.toString();
        }

        public boolean isSameConstraintName(String next, String previous) {
            return isSame(next, previous);
        }

        public boolean isSameStructure(KEY next, KEY previous) {
            return isSame(column(next), column(previous));
        }
    }

    // *unused after all
    //protected boolean isAutoGeneratedConstraintName(String... names) { // for all constraints
    //    if (names == null || names.length == 0) {
    //        return false;
    //    }
    //    if (isDatabaseOracle()) {
    //        if (Srl.hasPrefixAllIgnoreCase("SYS_", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseDB2() || isDatabaseDerby()) {
    //        if (Srl.hasPrefixAllIgnoreCase("SQL", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseSQLServer()) {
    //        if (Srl.hasPrefixAllIgnoreCase("PK__", names)) {
    //            return true;
    //        }
    //    }
    //    if (isDatabaseH2()) {
    //        if (Srl.hasPrefixAllIgnoreCase("CONSTRAINT_", names)) {
    //            return true;
    //        }
    //    }
    //    return false;
    //}

    // ===================================================================================
    //                                                                         Find Object
    //                                                                         ===========
    protected Table findNextTable(Table table) {
        return _nextDb.getTable(table.getName());
    }

    protected Table findPreviousTable(Table table) {
        return _previousDb.getTable(table.getName());
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createSchemaDiffMap() {
        final Map<String, Object> schemaDiffMap = DfCollectionUtil.newLinkedHashMap();
        schemaDiffMap.put(DIFF_DATE_KEY, DfTypeUtil.toString(_diffDate, DIFF_DATE_PATTERN));
        schemaDiffMap.put(TABLE_COUNT_KEY, _tableCountDiff.createNextPreviousDiffMap());

        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            if (!diffAllList.isEmpty()) {
                final Map<String, Map<String, Object>> diffMap = DfCollectionUtil.newLinkedHashMap();
                schemaDiffMap.put(setupper.propertyName(), diffMap);
                for (DfNestDiff nestDiff : diffAllList) {
                    if (nestDiff.hasDiff()) {
                        diffMap.put(nestDiff.getKeyName(), nestDiff.createDiffMap());
                    }
                }
            }
        }
        return schemaDiffMap;
    }

    public void acceptSchemaDiffMap(Map<String, Object> schemaDiffMap) {
        final Set<Entry<String, Object>> entrySet = schemaDiffMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (DIFF_DATE_KEY.equals(key)) {
                _diffDate = DfTypeUtil.toDate(value, DIFF_DATE_PATTERN);
                assertDiffDateExists(key, _diffDate, schemaDiffMap);
            } else if (TABLE_COUNT_KEY.equals(key)) {
                _tableCountDiff = restoreNextPreviousDiff(schemaDiffMap, key);
                assertTableCountExists(key, _tableCountDiff, schemaDiffMap);
            } else {
                final List<NestDiffSetupper> nestDiffList = _nestDiffList;
                for (NestDiffSetupper setupper : nestDiffList) {
                    if (setupper.propertyName().equals(key)) {
                        restoreNestDiff(schemaDiffMap, setupper);
                    }
                }
            }
        }
    }

    protected void assertDiffDateExists(String key, Date diffDate, Map<String, Object> schemaDiffMap) {
        if (diffDate == null) { // basically no way
            String msg = "The diff-date of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertTableCountExists(String key, DfNextPreviousDiff nextPreviousDiff,
            Map<String, Object> schemaDiffMap) {
        if (nextPreviousDiff == null) { // basically no way
            String msg = "The table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertNextTableCountExists(String key, String nextTableCount, Map<String, Object> schemaDiffMap) {
        if (nextTableCount == null) { // basically no way
            String msg = "The next table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertPreviousTableCountExists(String key, String previousTableCount,
            Map<String, Object> schemaDiffMap) {
        if (previousTableCount == null) { // basically no way
            String msg = "The previous table count of diff-map is required:";
            msg = msg + " key=" + key + " schemaDiffMap=" + schemaDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        final List<NestDiffSetupper> nestDiffList = _nestDiffList;
        for (NestDiffSetupper setupper : nestDiffList) {
            final List<? extends DfNestDiff> diffAllList = setupper.provide();
            for (DfNestDiff nestDiff : diffAllList) {
                if (nestDiff.hasDiff()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFirstTime() {
        return _firstTime;
    }

    public boolean isLoadingFailure() {
        return _loadingFailure;
    }

    // ===================================================================================
    //                                                                       Schema Reader
    //                                                                       =============
    protected DfSchemaXmlReader createSchemaXmlReader() {
        return new DfSchemaXmlReader(getSchemaXmlFilePath(), getDatabaseType());
    }

    protected String getSchemaXmlFilePath() {
        return getBasicProperties().getProejctSchemaXMLFilePath();
    }

    protected String getDatabaseType() {
        return getBasicProperties().getDatabaseType();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getDiffDate() {
        return DfTypeUtil.toString(_diffDate, DIFF_DATE_PATTERN);
    }

    public DfNextPreviousDiff getTableCount() {
        return _tableCountDiff;
    }

    // -----------------------------------------------------
    //                                            Table Diff
    //                                            ----------
    public List<DfTableDiff> getTableDiffAllList() {
        return _tableDiffAllList;
    }

    public List<DfTableDiff> getAddedTableDiffList() {
        return _addedTableDiffList;
    }

    public List<DfTableDiff> getChangedTableDiffList() {
        return _changedTableDiffList;
    }

    public List<DfTableDiff> getDeletedTableDiffList() {
        return _deletedTableDiffList;
    }

    public void addTableDiff(DfTableDiff tableDiff) {
        _tableDiffAllList.add(tableDiff);
        if (tableDiff.isAdded()) {
            _addedTableDiffList.add(tableDiff);
        } else if (tableDiff.isChanged()) {
            _changedTableDiffList.add(tableDiff);
        } else if (tableDiff.isDeleted()) {
            _deletedTableDiffList.add(tableDiff);
        } else {
            String msg = "Unknown diff-type of table: ";
            msg = msg + " diffType=" + tableDiff.getDiffType();
            msg = msg + " tableDiff=" + tableDiff;
            throw new IllegalStateException(msg);
        }
    }
}
