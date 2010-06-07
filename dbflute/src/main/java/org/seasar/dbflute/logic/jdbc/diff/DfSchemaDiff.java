package org.seasar.dbflute.logic.jdbc.diff;

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
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfSchemaDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String DIFF_DATE_KEY = "$$DiffDate$$";
    public static final String DIFF_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String TABLE_COUNT_KEY = "$$TableCount$$";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                           Load Schema
    //                                           -----------
    protected Database _nextDb; // not null after loading
    protected Integer _nextTableCount = null;
    protected Database _previousDb; // not null after loading
    protected Integer _previousTableCount = null;
    protected Date _diffDate; // not null after loading next schema
    protected boolean _firstTime; // judged when loading previous schema
    protected boolean _loadingFailure; // judged when loading previous schema

    // -----------------------------------------------------
    //                                          Analyze Diff
    //                                          ------------
    protected final List<DfTableDiff> _tableDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _addedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _changedTableDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _deletedTableDiffList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Load Schema
    //                                                                         ===========
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
        _nextTableCount = _nextDb.getTableList().size();
    }

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
            if (!isSameUnifiedSchema(next, previous)) {
                final String nextSchema = next.getUnifiedSchema().getCatalogSchema();
                final String previousSchema = previous.getUnifiedSchema().getCatalogSchema();
                tableDiff.setUnifiedSchemaDiff(createNextPreviousDiff(nextSchema, previousSchema));
            }
            if (!isSameObjectType(next, previous)) {
                tableDiff.setObjectTypeDiff(createNextPreviousDiff(next.getType(), previous.getType()));
            }
            processAddedColumn(tableDiff, next, previous);
            processChangedColumn(tableDiff, next, previous);
            processDeletedColumn(tableDiff, next, previous);
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

    protected boolean isSameTableName(Table next, Table previous) {
        return isSame(next.getName(), previous.getName());
    }

    protected boolean isSameUnifiedSchema(Table next, Table previous) {
        return isSame(next.getUnifiedSchema(), previous.getUnifiedSchema());
    }

    protected boolean isSameObjectType(Table next, Table previous) {
        return isSame(next.getType(), previous.getType());
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
            final DfColumnDiff diff = DfColumnDiff.createChanged(next.getName());
            if (!isSameDbType(next, previous)) {
                diff.setDbTypeDiff(createNextPreviousDiff(next.getDbType(), previous.getDbType()));
            }
            if (!isSameColumnSize(next, previous)) {
                diff.setColumnSizeDiff(createNextPreviousDiff(next.getColumnSize(), previous.getColumnSize()));
            }
            tableDiff.addColumnDiff(diff);
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

    protected boolean isSameColumnName(Column next, Column previous) {
        return isSame(next.getName(), previous.getName());
    }

    protected boolean isSameDbType(Column next, Column previous) {
        return isSame(next.getDbType(), previous.getDbType());
    }

    protected boolean isSameColumnSize(Column next, Column previous) {
        return isSame(next.getColumnSize(), previous.getColumnSize());
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
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> tableDiffMap = DfCollectionUtil.newLinkedHashMap();
        tableDiffMap.put(DIFF_DATE_KEY, DfTypeUtil.toString(_diffDate, DIFF_DATE_PATTERN));
        final Map<String, Object> tableCountMap = DfCollectionUtil.newLinkedHashMap();
        tableCountMap.put("next", _nextTableCount);
        tableCountMap.put("previous", _previousTableCount);
        tableCountMap.put("added", _addedTableDiffList.size());
        tableCountMap.put("changed", _changedTableDiffList.size());
        tableCountMap.put("deleted", _deletedTableDiffList.size());
        tableDiffMap.put(TABLE_COUNT_KEY, tableCountMap);
        for (DfTableDiff tableDiff : _tableDiffAllList) {
            if (tableDiff.hasDiff()) {
                tableDiffMap.put(tableDiff.getTableName(), tableDiff.createDiffMap());
            }
        }
        return tableDiffMap;
    }

    public void acceptDiffMap(Map<String, Object> schemaDiffMap) {
        final Set<Entry<String, Object>> entrySet = schemaDiffMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (DIFF_DATE_KEY.equals(key)) {
                _diffDate = DfTypeUtil.toDate(value, DIFF_DATE_PATTERN);
                assertDiffDateExists(key, _diffDate, schemaDiffMap);
            } else if (TABLE_COUNT_KEY.equals(key)) {
                final DfNextPreviousDiff nextPreviousDiff = createNextPreviousDiff(schemaDiffMap, key);
                final String nextValue = nextPreviousDiff.getNextValue();
                if (Srl.is_NotNull_and_NotTrimmedEmpty(nextValue)) { // basically true
                    _nextTableCount = DfTypeUtil.toInteger(nextValue);
                }
                final String previousValue = nextPreviousDiff.getPreviousValue();
                if (Srl.is_NotNull_and_NotTrimmedEmpty(previousValue)) { // basically true
                    _previousTableCount = DfTypeUtil.toInteger(previousValue);
                }
            } else { // table elements
                assertTableElementMap(key, value);
                @SuppressWarnings("unchecked")
                final Map<String, Object> tableDiffMap = (Map<String, Object>) value;
                final DfTableDiff tableDiff = DfTableDiff.createFromDiffMap(tableDiffMap);
                addTableDiff(tableDiff);
            }
        }
    }

    protected void assertDiffDateExists(String key, Date diffDate, Map<String, Object> diffMap) {
        if (diffDate == null) { // basically no way
            String msg = "The diff-date of diff-map is required:";
            msg = msg + " key=" + key + " diffMap=" + diffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertTableElementMap(String key, Object value) {
        if (!(value instanceof Map<?, ?>)) { // basically no way
            String msg = "The elements of tables should be Map:";
            msg = msg + " table=" + key + " value=" + value;
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
    public Date getDiffDate() {
        return _diffDate;
    }

    public Integer getNextTableCount() {
        return _nextTableCount;
    }

    public Integer getPreviousTableCount() {
        return _previousTableCount;
    }

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
        if (DfDiffMode.ADDED.equals(tableDiff.getDiffMode())) {
            _addedTableDiffList.add(tableDiff);
        } else if (DfDiffMode.CHANGED.equals(tableDiff.getDiffMode())) {
            _changedTableDiffList.add(tableDiff);
        } else if (DfDiffMode.DELETED.equals(tableDiff.getDiffMode())) {
            _deletedTableDiffList.add(tableDiff);
        } else {
            String msg = "Unknown diff-mode of table: ";
            msg = msg + " diffMode=" + tableDiff.getDiffMode();
            msg = msg + " tableDiff=" + tableDiff;
            throw new IllegalStateException(msg);
        }
    }
}
