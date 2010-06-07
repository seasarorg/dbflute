package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfTableDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableName;
    protected final DfDiffMode _diffMode;
    protected DfNextPreviousDiff _unifiedSchemaDiff;
    protected DfNextPreviousDiff _objectTypeDiff;
    protected final List<DfColumnDiff> _columnDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _addedColumnDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _changedColumnDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _deletedColumnDiffList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfTableDiff(String tableName, DfDiffMode diffMode) {
        _tableName = tableName;
        _diffMode = diffMode;
    }

    protected DfTableDiff(Map<String, Object> tableDiffMap) {
        _tableName = (String) tableDiffMap.get("tableName");
        assertTableNameExists(_tableName, tableDiffMap);
        _diffMode = DfDiffMode.valueOf((String) tableDiffMap.get("diffMode"));
        assertDiffModeExists(_tableName, tableDiffMap, _diffMode);
        acceptDiffMap(tableDiffMap);
    }

    protected void assertTableNameExists(String tableName, Map<String, Object> tableDiffMap) {
        if (tableName == null) { // basically no way
            String msg = "The tableName is required in table diff-map:";
            msg = msg + " tableDiffMap=" + tableDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertDiffModeExists(String tableName, Map<String, Object> tableDiffMap, DfDiffMode diffMode) {
        if (diffMode == null) { // basically no way
            String msg = "The diffMode is required in table diff-map:";
            msg = msg + " table=" + tableName + " tableDiffMap=" + tableDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    public static DfTableDiff createAdded(String tableName) {
        return new DfTableDiff(tableName, DfDiffMode.ADDED);
    }

    public static DfTableDiff createChanged(String tableName) {
        return new DfTableDiff(tableName, DfDiffMode.CHANGED);
    }

    public static DfTableDiff createDeleted(String tableName) {
        return new DfTableDiff(tableName, DfDiffMode.DELETED);
    }

    public static DfTableDiff createFromDiffMap(Map<String, Object> tableDiffMap) {
        return new DfTableDiff(tableDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("tableName", _tableName);
        map.put("diffMode", _diffMode.toString());
        map.put("unifiedSchemaDiff", _unifiedSchemaDiff != null ? _unifiedSchemaDiff.createDiffMap() : null);
        map.put("objectTypeDiff", _objectTypeDiff != null ? _objectTypeDiff.createDiffMap() : null);
        final Map<String, Map<String, Object>> columnDiffMap = DfCollectionUtil.newLinkedHashMap();
        for (DfColumnDiff diff : _columnDiffAllList) {
            if (diff.hasDiff()) {
                columnDiffMap.put(diff.getColumnName(), diff.createDiffMap());
            }
        }
        map.put("columnDiff", columnDiffMap);
        return map;
    }

    protected void acceptDiffMap(Map<String, Object> tableDiffMap) {
        _unifiedSchemaDiff = restoreNextPreviousDiff(tableDiffMap, "unifiedSchemaDiff");
        _objectTypeDiff = restoreNextPreviousDiff(tableDiffMap, "objectTypeDiff");
        {
            final String key = "columnDiff";
            final Object value = tableDiffMap.get(key);
            if (value != null) {
                assertElementMap(key, value, tableDiffMap);
                @SuppressWarnings("unchecked")
                final Map<String, Object> columnDiffMap = (Map<String, Object>) value;
                final DfColumnDiff columnDiff = createColumnDiff(columnDiffMap);
                addColumnDiff(columnDiff);
            }
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        if (!DfDiffMode.CHANGED.equals(_diffMode)) {
            return true; // if not change, always different
        }
        if (_unifiedSchemaDiff != null || _objectTypeDiff != null) {
            return true;
        }
        for (DfColumnDiff diff : _columnDiffAllList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableName() {
        return _tableName;
    }

    public DfDiffMode getDiffMode() {
        return _diffMode;
    }

    public boolean isAdded() {
        return DfDiffMode.ADDED.equals(_diffMode);
    }

    public boolean isChanged() {
        return DfDiffMode.CHANGED.equals(_diffMode);
    }

    public boolean isDeleted() {
        return DfDiffMode.DELETED.equals(_diffMode);
    }

    public boolean hasUnifiedSchemaDiff() {
        return _unifiedSchemaDiff != null;
    }

    public DfNextPreviousDiff getUnifiedSchemaDiff() {
        return _unifiedSchemaDiff;
    }

    public void setUnifiedSchemaDiff(DfNextPreviousDiff unifiedSchemaDiff) {
        _unifiedSchemaDiff = unifiedSchemaDiff;
    }

    public boolean hasObjectTypeDiff() {
        return _objectTypeDiff != null;
    }

    public DfNextPreviousDiff getObjectTypeDiff() {
        return _objectTypeDiff;
    }

    public void setObjectTypeDiff(DfNextPreviousDiff objectTypeDiff) {
        _objectTypeDiff = objectTypeDiff;
    }

    public List<DfColumnDiff> getColumnDiffAllList() {
        return _columnDiffAllList;
    }

    public List<DfColumnDiff> getAddedColumnDiffList() {
        return _addedColumnDiffList;
    }

    public List<DfColumnDiff> getChangedColumnDiffList() {
        return _changedColumnDiffList;
    }

    public List<DfColumnDiff> getDeletedColumnDiffList() {
        return _deletedColumnDiffList;
    }

    public void addColumnDiff(DfColumnDiff columnDiff) {
        _columnDiffAllList.add(columnDiff);
        if (DfDiffMode.ADDED.equals(columnDiff.getDiffMode())) {
            _addedColumnDiffList.add(columnDiff);
        } else if (DfDiffMode.CHANGED.equals(columnDiff.getDiffMode())) {
            _changedColumnDiffList.add(columnDiff);
        } else if (DfDiffMode.DELETED.equals(columnDiff.getDiffMode())) {
            _deletedColumnDiffList.add(columnDiff);
        } else {
            String msg = "Unknown diff-mode of column: ";
            msg = msg + " diffMode=" + columnDiff.getDiffMode();
            msg = msg + " columnDiff=" + columnDiff;
            throw new IllegalStateException(msg);
        }
    }
}
