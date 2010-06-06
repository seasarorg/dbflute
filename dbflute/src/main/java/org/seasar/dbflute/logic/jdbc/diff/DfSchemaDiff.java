package org.seasar.dbflute.logic.jdbc.diff;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfSchemaDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Database _nextDb;
    protected Database _previousDb;
    protected Date _diffDate;
    protected final List<DfTableDiff> _addedTableList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _changedTableList = DfCollectionUtil.newArrayList();
    protected final List<DfTableDiff> _deletedTableList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Load Schema
    //                                                                         ===========
    public void loadNextSchema() throws EngineException {
        final DfSchemaXmlReader reader = createSchemaXmlReader();
        reader.read();
        _nextDb = reader.getSchemaData().getDatabase();
        _diffDate = new Date(DfSystemUtil.currentTimeMillis());
    }

    public void loadPreviousSchema() throws EngineException {
        final DfSchemaXmlReader reader = createSchemaXmlReader();
        reader.read();
        _previousDb = reader.getSchemaData().getDatabase();
    }

    // ===================================================================================
    //                                                                             Analyze
    //                                                                             =======
    public void analyzeDiff() throws EngineException {
        processAddedTable();
        processChangedTable();
        processDeletedTable();
    }

    // ===================================================================================
    //                                                                       Table Process
    //                                                                       =============
    protected void processAddedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table table : tableList) {
            final Table found = findPreviousTable(table);
            if (found == null) { // added
                _addedTableList.add(DfTableDiff.createAdded(table.getName()));
            }
        }
    }

    protected void processChangedTable() {
        final List<Table> tableList = _nextDb.getTableList();
        for (Table next : tableList) {
            final Table previous = findPreviousTable(next);
            if (previous == null) {
                continue;
            }
            final DfTableDiff diff = DfTableDiff.createChanged(next.getName());
            if (!isSameSchema(next, previous)) {
                final String nextSchema = next.getUnifiedSchema().getCatalogSchema();
                final String previousSchema = previous.getUnifiedSchema().getCatalogSchema();
                diff.setSchemaDiff(createNextPreviousBean(nextSchema, previousSchema));
            }
            if (!isSameObjectType(next, previous)) {
                diff.setObjectTypeDiff(createNextPreviousBean(next.getType(), previous.getType()));
            }
            processAddedColumn(diff, next, previous);
            processChangedColumn(diff, next, previous);
            processDeletedColumn(diff, next, previous);
            if (diff.hasDiff()) {
                _changedTableList.add(diff);
            }
        }
    }

    protected void processDeletedTable() {
        final List<Table> tableList = _previousDb.getTableList();
        for (Table table : tableList) {
            final Table found = findNextTable(table);
            if (found == null) { // deleted
                _deletedTableList.add(DfTableDiff.createDeleted(table.getName()));
            }
        }
    }

    protected boolean isSameSchema(Table next, Table previous) {
        return isSame(next.getUnifiedSchema(), previous.getUnifiedSchema());
    }

    protected boolean isSameObjectType(Table next, Table previous) {
        return isSame(next.getType(), previous.getType());
    }

    // ===================================================================================
    //                                                                      Column Process
    //                                                                      ==============
    protected void processAddedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column column : columnList) {
            final Column found = previousTable.getColumn(column.getName());
            if (found == null) { // added
                tableDiff.addAddedColumn(DfColumnDiff.createAdded(column.getName()));
            }
        }
    }

    protected void processChangedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = nextTable.getColumnList();
        for (Column next : columnList) {
            final Column previous = previousTable.getColumn(next.getName());
            if (previous == null) {
                continue;
            }
            final DfColumnDiff diff = DfColumnDiff.createChanged(next.getName());
            if (!isSameDbType(next, previous)) {
                diff.setDbTypeDiff(createNextPreviousBean(next.getDbType(), previous.getDbType()));
            }
            if (!isSameColumnSize(next, previous)) {
                diff.setColumnSizeDiff(createNextPreviousBean(next.getColumnSize(), previous.getColumnSize()));
            }
            tableDiff.addChangedColumn(diff);
        }
    }

    protected void processDeletedColumn(DfTableDiff tableDiff, Table nextTable, Table previousTable) {
        final List<Column> columnList = previousTable.getColumnList();
        for (Column column : columnList) {
            final Column found = nextTable.getColumn(column.getName());
            if (found == null) { // deleted
                tableDiff.addAddedColumn(DfColumnDiff.createDeleted(column.getName()));
            }
        }
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
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfNextPreviousBean createNextPreviousBean(String next, String previous) {
        return new DfNextPreviousBean(next, previous);
    }

    protected boolean isSame(Object next, Object previous) {
        if (next == null && previous == null) {
            return true;
        }
        if (next == null || previous == null) {
            return false;
        }
        return next.equals(previous);
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        for (DfTableDiff diff : _addedTableList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        for (DfTableDiff diff : _changedTableList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        for (DfTableDiff diff : _deletedTableList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                             DiffMap
    //                                                                             =======
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> tableDiffMap = DfCollectionUtil.newLinkedHashMap();
        tableDiffMap.put("$$DiffDate$$", DfTypeUtil.toString(_diffDate, "yyyy/MM/dd HH:mm:dd"));
        tableDiffMap.put("$$NextTableCount$$", _nextDb.getTableList().size());
        tableDiffMap.put("$$PreviousTableCount$$", _previousDb.getTableList().size());
        tableDiffMap.put("$$AddedTableCount$$", _addedTableList.size());
        tableDiffMap.put("$$ChangedTableCount$$", _changedTableList.size());
        tableDiffMap.put("$$DeletedTableCount$$", _deletedTableList.size());
        for (DfTableDiff diff : _addedTableList) {
            if (diff.hasDiff()) {
                tableDiffMap.put(diff.getTableName(), diff.createDiffMap());
            }
        }
        for (DfTableDiff diff : _changedTableList) {
            if (diff.hasDiff()) {
                tableDiffMap.put(diff.getTableName(), diff.createDiffMap());
            }
        }
        for (DfTableDiff diff : _deletedTableList) {
            if (diff.hasDiff()) {
                tableDiffMap.put(diff.getTableName(), diff.createDiffMap());
            }
        }
        return tableDiffMap;
    }

    // ===================================================================================
    //                                                                       Schema Reader
    //                                                                       =============
    protected DfSchemaXmlReader createSchemaXmlReader() {
        return new DfSchemaXmlReader(getSchemaXml(), getDatabaseType());
    }

    protected String getSchemaXml() {
        return getBasicProperties().getProejctSchemaXMLFilePath();
    }

    protected String getDatabaseType() {
        return getBasicProperties().getDatabaseType();
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }
}
