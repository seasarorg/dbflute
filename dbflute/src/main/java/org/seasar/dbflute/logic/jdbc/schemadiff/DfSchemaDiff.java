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

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfSchemaDiff extends DfAbstractDiff {

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
            diffNextPrevious(next, previous, tableDiff, new NextPreviousDiffer<Table, DfTableDiff>() {
                public Object provide(Table obj) {
                    return obj.getUnifiedSchema().getCatalogSchema();
                }

                public void diff(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setUnifiedSchemaDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, tableDiff, new NextPreviousDiffer<Table, DfTableDiff>() {
                public Object provide(Table obj) {
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

    protected void diffNextPrevious(Table next, Table previous, DfTableDiff diff,
            NextPreviousDiffer<Table, DfTableDiff> setupper) {
        final Object nextValue = setupper.provide(next);
        final Object previousValue = setupper.provide(previous);
        if (!isSame(nextValue, previousValue)) {
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
            diffNextPrevious(next, previous, columnDiff, new NextPreviousDiffer<Column, DfColumnDiff>() {
                public Object provide(Column obj) {
                    return obj.getDbType();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDbTypeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new NextPreviousDiffer<Column, DfColumnDiff>() {
                public Object provide(Column obj) {
                    return obj.getColumnSize();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setColumnSizeDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new NextPreviousDiffer<Column, DfColumnDiff>() {
                public Object provide(Column obj) {
                    return obj.getDefaultValue();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setDefaultValueDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new NextPreviousDiffer<Column, DfColumnDiff>() {
                public Object provide(Column obj) {
                    return obj.isNotNull();
                }

                public void diff(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setNotNullDiff(nextPreviousDiff);
                }
            });
            diffNextPrevious(next, previous, columnDiff, new NextPreviousDiffer<Column, DfColumnDiff>() {
                public Object provide(Column obj) {
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

    protected <ITEM> void diffNextPrevious(Column next, Column previous, DfColumnDiff diff,
            NextPreviousDiffer<Column, DfColumnDiff> setupper) {
        final Object nextValue = setupper.provide(next);
        final Object previousValue = setupper.provide(previous);
        if (!isSame(nextValue, previousValue)) {
            setupper.diff(diff, createNextPreviousDiff(nextValue.toString(), previousValue.toString()));
        }
    }

    protected boolean isSameColumnName(Column next, Column previous) {
        return isSame(next.getName(), previous.getName());
    }

    // -----------------------------------------------------
    //                                    PrimaryKey Process
    //                                    ------------------
    protected void processPrimaryKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final String nextName = nextTable.getPrimaryKeyConstraintName();
        final String previousName = previousTable.getPrimaryKeyConstraintName();
        if (!isSame(nextName, previousName)) {
            if (nextName == null) { // deleted
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createDeleted(previousName));
                return;
            } else if (previousName == null) { // added
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createAdded(nextName));
                return;
            } else { // both deleted and added 
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createDeleted(previousName));
                tableDiff.addPrimaryKeyDiff(DfPrimaryKeyDiff.createAdded(nextName));
                return;
            }
        }
        // found
        final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(nextName);
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
        processAddedForeignKey(tableDiff, nextTable, previousTable);
        processChangedForeignKey(tableDiff, nextTable, previousTable);
        processDeletedForeignKey(tableDiff, nextTable, previousTable);
    }

    protected void processAddedForeignKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedConstraintKey(nextTable, previousTable, new ForeignKeyDiffer() {
            public void diff(ForeignKey nextKey, ForeignKey previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addForeignKeyDiff(DfForeignKeyDiff.createAdded(nextKey.getName()));
            }
        });
    }

    protected void processChangedForeignKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processChangedConstraintKey(nextTable, previousTable, new ForeignKeyDiffer() {
            public void diff(ForeignKey nextKey, ForeignKey previousKey, DfNextPreviousDiff columnDiff) {
                final DfForeignKeyDiff foreignKeyDiff = DfForeignKeyDiff.createChanged(nextKey.getName());

                // columnDiff
                foreignKeyDiff.setColumnDiff(columnDiff);

                // foreignTable
                final String nextFKTable = nextKey.getForeignTableName();
                final String previousFKTable = previousKey.getForeignTableName();
                if (!isSame(nextFKTable, previousFKTable)) {
                    final DfNextPreviousDiff fkTableDiff = createNextPreviousDiff(nextFKTable, previousFKTable);
                    foreignKeyDiff.setForeignTableDiff(fkTableDiff);
                }

                if (foreignKeyDiff.hasDiff()) { // changed
                    tableDiff.addForeignKeyDiff(foreignKeyDiff);
                }
            }
        });
    }

    protected void processDeletedForeignKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processDeletedConstraintKey(nextTable, previousTable, new ForeignKeyDiffer() {
            public void diff(ForeignKey nextKey, ForeignKey previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addForeignKeyDiff(DfForeignKeyDiff.createDeleted(previousKey.getName()));
            }
        });
    }

    protected static abstract class ForeignKeyDiffer implements ConstraintKeyDiffer<ForeignKey> {
        public String constraintName(ForeignKey key) {
            return key.getName();
        }

        public List<ForeignKey> keyList(Table table) {
            return DfCollectionUtil.newArrayList(table.getForeignKeys());
        }

        public String column(ForeignKey key) {
            return key.getLocalColumnNameCommaString();
        }
    }

    // -----------------------------------------------------
    //                                     UniqueKey Process
    //                                     -----------------
    protected void processUniqueKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedUniqueKey(tableDiff, nextTable, previousTable);
        processChangedUniqueKey(tableDiff, nextTable, previousTable);
        processDeletedUniqueKey(tableDiff, nextTable, previousTable);
    }

    protected void processAddedUniqueKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedConstraintKey(nextTable, previousTable, new UniqueKeyDiffer() {
            public void diff(Unique nextKey, Unique previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addUniqueKeyDiff(DfUniqueKeyDiff.createAdded(nextKey.getName()));
            }
        });
    }

    protected void processChangedUniqueKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processChangedConstraintKey(nextTable, previousTable, new UniqueKeyDiffer() {
            public void diff(Unique nextKey, Unique previousKey, DfNextPreviousDiff columnDiff) {
                final DfUniqueKeyDiff uniqueKeyDiff = DfUniqueKeyDiff.createChanged(nextKey.getName());

                // columnDiff
                uniqueKeyDiff.setColumnDiff(columnDiff);

                if (uniqueKeyDiff.hasDiff()) { // changed
                    tableDiff.addUniqueKeyDiff(uniqueKeyDiff);
                }
            }
        });
    }

    protected void processDeletedUniqueKey(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processDeletedConstraintKey(nextTable, previousTable, new UniqueKeyDiffer() {
            public void diff(Unique nextKey, Unique previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addUniqueKeyDiff(DfUniqueKeyDiff.createDeleted(previousKey.getName()));
            }
        });
    }

    protected static abstract class UniqueKeyDiffer implements ConstraintKeyDiffer<Unique> {
        public String constraintName(Unique key) {
            return key.getName();
        }

        public List<Unique> keyList(Table table) {
            return table.getUniqueList();
        }

        public String column(Unique key) {
            final Collection<String> values = key.getIndexColumnMap().values();
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
    }

    // -----------------------------------------------------
    //                                         Index Process
    //                                         -------------
    protected void processIndex(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedIndex(tableDiff, nextTable, previousTable);
        processChangedIndex(tableDiff, nextTable, previousTable);
        processDeletedIndex(tableDiff, nextTable, previousTable);
    }

    protected void processAddedIndex(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processAddedConstraintKey(nextTable, previousTable, new IndexDiffer() {
            public void diff(Index nextKey, Index previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addIndexDiff(DfIndexDiff.createAdded(nextKey.getName()));
            }
        });
    }

    protected void processChangedIndex(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processChangedConstraintKey(nextTable, previousTable, new IndexDiffer() {
            public void diff(Index nextKey, Index previousKey, DfNextPreviousDiff columnDiff) {
                final DfIndexDiff indexDiff = DfIndexDiff.createChanged(nextKey.getName());

                // columnDiff
                indexDiff.setColumnDiff(columnDiff);

                if (indexDiff.hasDiff()) { // changed
                    tableDiff.addIndexDiff(indexDiff);
                }
            }
        });
    }

    protected void processDeletedIndex(final DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        processDeletedConstraintKey(nextTable, previousTable, new IndexDiffer() {
            public void diff(Index nextKey, Index previousKey, DfNextPreviousDiff columnDiff) {
                tableDiff.addIndexDiff(DfIndexDiff.createDeleted(previousKey.getName()));
            }
        });
    }

    protected static abstract class IndexDiffer implements ConstraintKeyDiffer<Index> {
        public String constraintName(Index key) {
            return key.getName();
        }

        public List<Index> keyList(Table table) {
            return table.getIndexList();
        }

        public String column(Index key) {
            final Collection<String> values = key.getIndexColumnMap().values();
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
    }

    // -----------------------------------------------------
    //                                    Constraint Process
    //                                    ------------------
    protected <KEY> void processAddedConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY> handler) {
        final List<KEY> keyList = handler.keyList(nextTable);
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = handler.constraintName(nextKey);
            for (KEY previousKey : handler.keyList(previousTable)) {
                final String previousName = handler.constraintName(previousKey);
                if (isSame(nextName, previousName)) {
                    continue nextLoop;
                }
            }
            // added
            handler.diff(nextKey, null, null);
        }
    }

    protected <KEY> void processChangedConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY> differ) {
        final List<KEY> keyList = differ.keyList(nextTable);
        final Map<String, KEY> previousMap = DfCollectionUtil.newLinkedHashMap();
        nextLoop: for (KEY nextKey : keyList) {
            final String nextName = differ.constraintName(nextKey);
            for (KEY previousKey : differ.keyList(previousTable)) {
                final String previousName = differ.constraintName(previousKey);
                if (isSame(nextName, previousName)) { // found
                    previousMap.put(nextName, previousKey);
                    continue nextLoop;
                }
            }
        }
        for (KEY nextKey : differ.keyList(nextTable)) {
            final String nextName = differ.constraintName(nextKey);
            final KEY previousKey = previousMap.get(nextName);
            if (previousKey == null) {
                continue;
            }
            final String nextColumn = differ.column(nextKey);
            final String previousColumn = differ.column(previousKey);
            DfNextPreviousDiff columnDiff = null;
            if (!isSame(nextColumn, previousColumn)) {
                columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            }
            differ.diff(nextKey, previousKey, columnDiff);
        }
    }

    protected <KEY> void processDeletedConstraintKey(Table nextTable, Table previousTable,
            ConstraintKeyDiffer<KEY> handler) {
        final List<KEY> keyList = handler.keyList(previousTable);
        previousLoop: for (KEY previousKey : keyList) {
            final String previousName = handler.constraintName(previousKey);
            for (KEY nextKey : handler.keyList(nextTable)) {
                final String nextName = handler.constraintName(nextKey);
                if (isSame(nextName, previousName)) {
                    continue previousLoop;
                }
            }
            // deleted
            handler.diff(null, previousKey, null);
        }
    }

    protected static interface ConstraintKeyDiffer<KEY> {
        List<KEY> keyList(Table table);

        String constraintName(KEY key);

        String column(KEY key);

        void diff(KEY nextKey, KEY previousKey, DfNextPreviousDiff columnDiff);
    }

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
