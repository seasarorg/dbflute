package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfTableDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final String _tableName;
    protected final DfDiffType _diffType;

    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
    protected DfNextPreviousDiff _unifiedSchemaDiff;
    protected DfNextPreviousDiff _objectTypeDiff;

    protected List<NextPreviousItemHandler> _nextPreviousItemList = DfCollectionUtil.newArrayList();
    {
        _nextPreviousItemList.add(new NextPreviousItemHandler() {
            public String propertyName() {
                return "unifiedSchemaDiff";
            }

            public DfNextPreviousDiff provide() {
                return _unifiedSchemaDiff;
            }

            public void restore(Map<String, Object> tableDiffMap) {
                _unifiedSchemaDiff = restoreNextPreviousDiff(tableDiffMap, propertyName());
            }
        });
        _nextPreviousItemList.add(new NextPreviousItemHandler() {
            public String propertyName() {
                return "objectTypeDiff";
            }

            public DfNextPreviousDiff provide() {
                return _objectTypeDiff;
            }

            public void restore(Map<String, Object> tableDiffMap) {
                _objectTypeDiff = restoreNextPreviousDiff(tableDiffMap, propertyName());
            }
        });
    }
    // -----------------------------------------------------
    //                                           Column Diff
    //                                           -----------
    protected final List<DfColumnDiff> _columnDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _addedColumnDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _changedColumnDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _deletedColumnDiffList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfTableDiff(String tableName, DfDiffType diffType) {
        _tableName = tableName;
        _diffType = diffType;
    }

    protected DfTableDiff(Map<String, Object> tableDiffMap) {
        _tableName = (String) tableDiffMap.get("tableName");
        assertTableNameExists(_tableName, tableDiffMap);
        _diffType = DfDiffType.valueOf((String) tableDiffMap.get("diffType"));
        assertDiffTypeExists(_tableName, tableDiffMap, _diffType);
        acceptTableDiffMap(tableDiffMap);
    }

    protected void assertTableNameExists(String tableName, Map<String, Object> tableDiffMap) {
        if (tableName == null) { // basically no way
            String msg = "The tableName is required in table diff-map:";
            msg = msg + " tableDiffMap=" + tableDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertDiffTypeExists(String tableName, Map<String, Object> tableDiffMap, DfDiffType diffType) {
        if (diffType == null) { // basically no way
            String msg = "The diffType is required in table diff-map:";
            msg = msg + " table=" + tableName + " tableDiffMap=" + tableDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    public static DfTableDiff createAdded(String tableName) {
        return new DfTableDiff(tableName, DfDiffType.ADD);
    }

    public static DfTableDiff createChanged(String tableName) {
        return new DfTableDiff(tableName, DfDiffType.CHANGE);
    }

    public static DfTableDiff createDeleted(String tableName) {
        return new DfTableDiff(tableName, DfDiffType.DELETE);
    }

    public static DfTableDiff createFromDiffMap(Map<String, Object> tableDiffMap) {
        return new DfTableDiff(tableDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createTableDiffMap() {
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("tableName", _tableName);
        map.put("diffType", _diffType.toString());
        final List<NextPreviousItemHandler> nextPreviousItemList = _nextPreviousItemList;
        for (NextPreviousItemHandler provider : nextPreviousItemList) {
            final DfNextPreviousDiff nextPreviousDiff = provider.provide();
            if (nextPreviousDiff != null) {
                map.put(provider.propertyName(), nextPreviousDiff.createNextPreviousDiffMap());
            }
        }
        if (!_columnDiffAllList.isEmpty()) {
            final Map<String, Map<String, Object>> columnDiffMap = DfCollectionUtil.newLinkedHashMap();
            for (DfColumnDiff columnDiff : _columnDiffAllList) {
                if (columnDiff.hasDiff()) {
                    columnDiffMap.put(columnDiff.getColumnName(), columnDiff.createColumnDiffMap());
                }
            }
            map.put("columnDiff", columnDiffMap);
        }
        return map;
    }

    protected void acceptTableDiffMap(Map<String, Object> tableDiffMap) {
        final List<NextPreviousItemHandler> nextPreviousItemList = _nextPreviousItemList;
        for (NextPreviousItemHandler provider : nextPreviousItemList) {
            provider.restore(tableDiffMap);
        }
        restoreColumnDiff(tableDiffMap);
    }

    protected void restoreColumnDiff(Map<String, Object> tableDiffMap) {
        final String key = "columnDiff";
        final Object value = tableDiffMap.get(key);
        if (value == null) {
            return;
        }
        assertElementValueMap(key, value, tableDiffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> columnDiffAllMap = (Map<String, Object>) value;
        final Set<Entry<String, Object>> entrySet = columnDiffAllMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String columnName = entry.getKey();
            final Object columnDiffObj = entry.getValue();
            assertElementValueMap(columnName, columnDiffObj, columnDiffAllMap);
            @SuppressWarnings("unchecked")
            final Map<String, Object> columnDiffMap = (Map<String, Object>) columnDiffObj;
            final DfColumnDiff columnDiff = createColumnDiff(columnDiffMap);
            addColumnDiff(columnDiff);
        }
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        if (!DfDiffType.CHANGE.equals(_diffType)) {
            return true; // if not change, always different
        }
        final List<NextPreviousItemHandler> nextPreviousItemList = _nextPreviousItemList;
        for (NextPreviousItemHandler provider : nextPreviousItemList) {
            if (provider.provide() != null) {
                return true;
            }
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
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getTableName() {
        return _tableName;
    }

    public DfDiffType getDiffType() {
        return _diffType;
    }

    public boolean isAdded() {
        return DfDiffType.ADD.equals(_diffType);
    }

    public boolean isChanged() {
        return DfDiffType.CHANGE.equals(_diffType);
    }

    public boolean isDeleted() {
        return DfDiffType.DELETE.equals(_diffType);
    }

    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
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

    // -----------------------------------------------------
    //                                           Column Diff
    //                                           -----------
    public boolean hasColumnDiff() {
        return !_columnDiffAllList.isEmpty();
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
        if (DfDiffType.ADD.equals(columnDiff.getDiffType())) {
            _addedColumnDiffList.add(columnDiff);
        } else if (DfDiffType.CHANGE.equals(columnDiff.getDiffType())) {
            _changedColumnDiffList.add(columnDiff);
        } else if (DfDiffType.DELETE.equals(columnDiff.getDiffType())) {
            _deletedColumnDiffList.add(columnDiff);
        } else {
            String msg = "Unknown diff-type of column: ";
            msg = msg + " diffType=" + columnDiff.getDiffType();
            msg = msg + " columnDiff=" + columnDiff;
            throw new IllegalStateException(msg);
        }
    }
}
