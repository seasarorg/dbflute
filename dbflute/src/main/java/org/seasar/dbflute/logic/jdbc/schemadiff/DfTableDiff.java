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

    // -----------------------------------------------------
    //                                       PrimaryKey Diff
    //                                       ---------------
    protected final List<DfPrimaryKeyDiff> _primaryKeyDiffAllList = DfCollectionUtil.newArrayList();
    protected final List<DfPrimaryKeyDiff> _addedPrimaryKeyDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfPrimaryKeyDiff> _changedPrimaryKeyDiffList = DfCollectionUtil.newArrayList();
    protected final List<DfPrimaryKeyDiff> _deletedPrimaryKeyDiffList = DfCollectionUtil.newArrayList();

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
        {
            final List<DfColumnDiff> diffAllList = _columnDiffAllList;
            if (!_columnDiffAllList.isEmpty()) {
                final Map<String, Map<String, Object>> diffMap = DfCollectionUtil.newLinkedHashMap();
                for (DfColumnDiff columnDiff : diffAllList) {
                    if (columnDiff.hasDiff()) {
                        diffMap.put(columnDiff.getColumnName(), columnDiff.createColumnDiffMap());
                    }
                }
                map.put("columnDiff", diffMap);
            }
        }
        {
            final List<DfPrimaryKeyDiff> diffAllList = _primaryKeyDiffAllList;
            if (!diffAllList.isEmpty()) {
                final Map<String, Map<String, Object>> diffMap = DfCollectionUtil.newLinkedHashMap();
                for (DfPrimaryKeyDiff primaryKeyDiff : diffAllList) {
                    if (primaryKeyDiff.hasDiff()) {
                        diffMap.put(primaryKeyDiff.getConstraintName(), primaryKeyDiff.createPrimaryKeyDiffMap());
                    }
                }
                map.put("primaryKeyDiff", diffMap);
            }
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

    protected void restorePrimaryKeyDiff(Map<String, Object> tableDiffMap) {
        final String key = "primaryKeyDiff";
        final Object value = tableDiffMap.get(key);
        if (value == null) {
            return;
        }
        assertElementValueMap(key, value, tableDiffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> primaryKeyDiffAllMap = (Map<String, Object>) value;
        final Set<Entry<String, Object>> entrySet = primaryKeyDiffAllMap.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String constraintName = entry.getKey();
            final Object primaryKeyDiffObj = entry.getValue();
            assertElementValueMap(constraintName, primaryKeyDiffObj, primaryKeyDiffAllMap);
            @SuppressWarnings("unchecked")
            final Map<String, Object> primaryKeyDiffMap = (Map<String, Object>) primaryKeyDiffObj;
            final DfPrimaryKeyDiff primaryKeyDiff = createPrimaryKeyDiff(primaryKeyDiffMap);
            addPrimaryKeyDiff(primaryKeyDiff);
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
        for (DfPrimaryKeyDiff diff : _primaryKeyDiffAllList) {
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
        if (columnDiff.isAdded()) {
            _addedColumnDiffList.add(columnDiff);
        } else if (columnDiff.isChanged()) {
            _changedColumnDiffList.add(columnDiff);
        } else if (columnDiff.isDeleted()) {
            _deletedColumnDiffList.add(columnDiff);
        } else {
            String msg = "Unknown diff-type of column: ";
            msg = msg + " diffType=" + columnDiff.getDiffType();
            msg = msg + " columnDiff=" + columnDiff;
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                           Column Diff
    //                                           -----------
    public boolean hasPrimaryKeyDiff() {
        return !_primaryKeyDiffAllList.isEmpty();
    }

    public List<DfPrimaryKeyDiff> getPrimaryKeyDiffAllList() {
        return _primaryKeyDiffAllList;
    }

    public List<DfPrimaryKeyDiff> getAddedPrimaryKeyDiffList() {
        return _addedPrimaryKeyDiffList;
    }

    public List<DfPrimaryKeyDiff> getChangedPrimaryKeyDiffList() {
        return _changedPrimaryKeyDiffList;
    }

    public List<DfPrimaryKeyDiff> getDeletedPrimaryKeyDiffList() {
        return _deletedPrimaryKeyDiffList;
    }

    public void addPrimaryKeyDiff(DfPrimaryKeyDiff primaryKeyDiff) {
        _primaryKeyDiffAllList.add(primaryKeyDiff);
        if (primaryKeyDiff.isAdded()) {
            _addedPrimaryKeyDiffList.add(primaryKeyDiff);
        } else if (primaryKeyDiff.isChanged()) {
            _changedPrimaryKeyDiffList.add(primaryKeyDiff);
        } else if (primaryKeyDiff.isDeleted()) {
            _deletedPrimaryKeyDiffList.add(primaryKeyDiff);
        } else {
            String msg = "Unknown diff-type of column: ";
            msg = msg + " diffType=" + primaryKeyDiff.getDiffType();
            msg = msg + " primaryKeyDiff=" + primaryKeyDiff;
            throw new IllegalStateException(msg);
        }
    }
}
