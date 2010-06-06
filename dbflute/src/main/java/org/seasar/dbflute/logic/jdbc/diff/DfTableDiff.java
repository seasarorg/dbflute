package org.seasar.dbflute.logic.jdbc.diff;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfTableDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableName;
    protected final DfDiffMode _diffMode;
    protected DfNextPreviousBean _schemaDiff;
    protected DfNextPreviousBean _objectTypeDiff;
    protected final List<DfColumnDiff> _addedColumnList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _changedColumnList = DfCollectionUtil.newArrayList();
    protected final List<DfColumnDiff> _deletedColumnList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfTableDiff(String tableName, DfDiffMode diffMode) {
        _tableName = tableName;
        _diffMode = diffMode;
    }

    protected DfTableDiff(Map<String, Object> diffMap) {
        _tableName = (String) diffMap.get("tableName");
        _diffMode = DfDiffMode.valueOf((String) diffMap.get("diffMode"));
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

    public static DfTableDiff createFromDiffMap(Map<String, Object> diffMap) {
        return new DfTableDiff(diffMap);
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        if (!DfDiffMode.CHANGED.equals(_diffMode)) {
            return true; // if not change, always different
        }
        if (_schemaDiff != null || _objectTypeDiff != null) {
            return true;
        }
        for (DfColumnDiff diff : _addedColumnList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        for (DfColumnDiff diff : _changedColumnList) {
            if (diff.hasDiff()) {
                return true;
            }
        }
        for (DfColumnDiff diff : _deletedColumnList) {
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
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("diffMode", _diffMode.toString());
        map.put("schemaDiff", _schemaDiff != null ? _schemaDiff.createDiffMap() : null);
        map.put("objectTypeDiff", _objectTypeDiff != null ? _objectTypeDiff.createDiffMap() : null);
        final Map<String, Map<String, Object>> columnDiffMap = DfCollectionUtil.newLinkedHashMap();
        for (DfColumnDiff diff : _addedColumnList) {
            if (diff.hasDiff()) {
                columnDiffMap.put(diff.getColumnName(), diff.createDiffMap());
            }
        }
        for (DfColumnDiff diff : _changedColumnList) {
            if (diff.hasDiff()) {
                columnDiffMap.put(diff.getColumnName(), diff.createDiffMap());
            }
        }
        for (DfColumnDiff diff : _deletedColumnList) {
            if (diff.hasDiff()) {
                columnDiffMap.put(diff.getColumnName(), diff.createDiffMap());
            }
        }
        map.put("columnDiff", columnDiffMap);
        return map;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableName() {
        return _tableName;
    }

    public void setSchemaDiff(DfNextPreviousBean schemaDiff) {
        _schemaDiff = schemaDiff;
    }

    public void setObjectTypeDiff(DfNextPreviousBean objectTypeDiff) {
        _objectTypeDiff = objectTypeDiff;
    }

    public void addAddedColumn(DfColumnDiff diff) {
        _addedColumnList.add(diff);
    }

    public void addChangedColumn(DfColumnDiff diff) {
        _changedColumnList.add(diff);
    }

    public void addDeletedColumn(DfColumnDiff diff) {
        _deletedColumnList.add(diff);
    }
}
