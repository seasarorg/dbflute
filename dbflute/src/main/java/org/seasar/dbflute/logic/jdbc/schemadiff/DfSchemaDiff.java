package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
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
            // changed
            final DfTableDiff tableDiff = DfTableDiff.createChanged(next.getName());
            setupNextPreviousIfNotSame(next, previous, tableDiff, new NextPreviousDiffSetupper<Table, DfTableDiff>() {
                public Object provide(Table obj) {
                    return obj.getUnifiedSchema().getCatalogSchema();
                }

                public void setup(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setUnifiedSchemaDiff(nextPreviousDiff);
                }
            });
            setupNextPreviousIfNotSame(next, previous, tableDiff, new NextPreviousDiffSetupper<Table, DfTableDiff>() {
                public Object provide(Table obj) {
                    return obj.getType();
                }

                public void setup(DfTableDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                    diff.setObjectTypeDiff(nextPreviousDiff);
                }
            });
            processAddedColumn(tableDiff, next, previous);
            processChangedColumn(tableDiff, next, previous);
            processDeletedColumn(tableDiff, next, previous);
            processPrimaryKey(tableDiff, next, previous);
            processForeignKey(tableDiff, next, previous);
            processUniqueKey(tableDiff, next, previous);
            processIndex(tableDiff, next, previous);
            if (tableDiff.hasDiff()) {
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

    protected void setupNextPreviousIfNotSame(Table next, Table previous, DfTableDiff diff,
            NextPreviousDiffSetupper<Table, DfTableDiff> setupper) {
        final Object nextValue = setupper.provide(next);
        final Object previousValue = setupper.provide(previous);
        if (!isSame(nextValue, previousValue)) {
            setupper.setup(diff, createNextPreviousDiff(nextValue.toString(), previousValue.toString()));
        }
    }

    protected boolean isSameTableName(Table next, Table previous) {
        return isSame(next.getName(), previous.getName());
    }

    // -----------------------------------------------------
    //                                        Column Process
    //                                        --------------
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
            // changed
            final DfColumnDiff columnDiff = DfColumnDiff.createChanged(next.getName());
            setupNextPreviousIfNotSame(next, previous, columnDiff,
                    new NextPreviousDiffSetupper<Column, DfColumnDiff>() {
                        public Object provide(Column obj) {
                            return obj.getDbType();
                        }

                        public void setup(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                            diff.setDbTypeDiff(nextPreviousDiff);
                        }
                    });
            setupNextPreviousIfNotSame(next, previous, columnDiff,
                    new NextPreviousDiffSetupper<Column, DfColumnDiff>() {
                        public Object provide(Column obj) {
                            return obj.getColumnSize();
                        }

                        public void setup(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                            diff.setColumnSizeDiff(nextPreviousDiff);
                        }
                    });
            setupNextPreviousIfNotSame(next, previous, columnDiff,
                    new NextPreviousDiffSetupper<Column, DfColumnDiff>() {
                        public Object provide(Column obj) {
                            return obj.getDefaultValue();
                        }

                        public void setup(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                            diff.setDefaultValueDiff(nextPreviousDiff);
                        }
                    });
            setupNextPreviousIfNotSame(next, previous, columnDiff,
                    new NextPreviousDiffSetupper<Column, DfColumnDiff>() {
                        public Object provide(Column obj) {
                            return obj.isNotNull();
                        }

                        public void setup(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                            diff.setNotNullDiff(nextPreviousDiff);
                        }
                    });
            setupNextPreviousIfNotSame(next, previous, columnDiff,
                    new NextPreviousDiffSetupper<Column, DfColumnDiff>() {
                        public Object provide(Column obj) {
                            return obj.isAutoIncrement();
                        }

                        public void setup(DfColumnDiff diff, DfNextPreviousDiff nextPreviousDiff) {
                            diff.setAutoIncrementDiff(nextPreviousDiff);
                        }
                    });
            if (columnDiff.hasDiff()) {
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

    protected <ITEM> void setupNextPreviousIfNotSame(Column next, Column previous, DfColumnDiff diff,
            NextPreviousDiffSetupper<Column, DfColumnDiff> setupper) {
        final Object nextValue = setupper.provide(next);
        final Object previousValue = setupper.provide(previous);
        if (!isSame(nextValue, previousValue)) {
            setupper.setup(diff, createNextPreviousDiff(nextValue.toString(), previousValue.toString()));
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
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createAdded(previousName);
                tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
                return;
            } else if (previousName == null) { // added
                final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(nextName);
                tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
                return;
            }
        }
        // changed
        final DfPrimaryKeyDiff primaryKeyDiff = DfPrimaryKeyDiff.createChanged(nextName);
        final String nextColumn = nextTable.getPrimaryKeyNameCommaString();
        final String previousColumn = previousTable.getPrimaryKeyNameCommaString();
        if (!isSame(nextColumn, previousColumn)) {
            final DfNextPreviousDiff columnDiff = createNextPreviousDiff(nextColumn, previousColumn);
            primaryKeyDiff.setColumnDiff(columnDiff);
        }
        if (primaryKeyDiff.hasDiff()) {
            tableDiff.addPrimaryKeyDiff(primaryKeyDiff);
        }
    }

    // -----------------------------------------------------
    //                                    ForeignKey Process
    //                                    ------------------
    protected void processForeignKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        // TODO
    }

    // -----------------------------------------------------
    //                                     UniqueKey Process
    //                                     -----------------
    protected void processUniqueKey(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        // TODO
    }

    // -----------------------------------------------------
    //                                         Index Process
    //                                         -------------
    protected void processIndex(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        // TODO
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
        if (!_tableDiffAllList.isEmpty()) {
            final Map<String, Map<String, Object>> tableDiffMap = DfCollectionUtil.newLinkedHashMap();
            schemaDiffMap.put(TABLE_DIFF_KEY, tableDiffMap);
            for (DfTableDiff tableDiff : _tableDiffAllList) {
                if (tableDiff.hasDiff()) {
                    tableDiffMap.put(tableDiff.getTableName(), tableDiff.createTableDiffMap());
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
            } else if (TABLE_DIFF_KEY.equals(key)) { // table elements
                restoreTableDiffMap(key, value, schemaDiffMap);
            }
        }
    }

    protected void restoreTableDiffMap(String key, Object value, Map<String, Object> schemaDiffMap) {
        assertElementValueMap(key, value, schemaDiffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> tableDiffAllMap = (Map<String, Object>) value;
        final Set<Entry<String, Object>> entrySet = tableDiffAllMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String tableName = entry.getKey();
            final Object tableDiffObj = entry.getValue();
            assertElementValueMap(tableName, tableDiffObj, tableDiffAllMap);
            @SuppressWarnings("unchecked")
            final Map<String, Object> tableDiffMap = (Map<String, Object>) tableDiffObj;
            final DfTableDiff tableDiff = createTableDiff(tableDiffMap);
            addTableDiff(tableDiff);
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
        for (DfTableDiff diff : _tableDiffAllList) {
            if (diff.hasDiff()) {
                return true;
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
