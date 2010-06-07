package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfColumnDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _columnName;
    protected final DfDiffMode _diffMode;
    protected DfNextPreviousDiff _dbTypeDiff;
    protected DfNextPreviousDiff _columnSizeDiff;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfColumnDiff(String columnName, DfDiffMode diffMode) {
        _columnName = columnName;
        _diffMode = diffMode;
    }

    protected DfColumnDiff(Map<String, Object> columnDiffMap) {
        _columnName = (String) columnDiffMap.get("columnName");
        assertColumnNameExists(_columnName, columnDiffMap);
        _diffMode = DfDiffMode.valueOf((String) columnDiffMap.get("diffMode"));
        acceptDiffMap(columnDiffMap);
    }

    protected void assertColumnNameExists(String columnName, Map<String, Object> columnDiffMap) {
        if (columnName == null) { // basically no way
            String msg = "The columnName is required in column diff-map:";
            msg = msg + " columnDiffMap=" + columnDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertDiffModeExists(String columnName, Map<String, Object> columnDiffMap, DfDiffMode diffMode) {
        if (diffMode == null) { // basically no way
            String msg = "The diffMode is required in column diff-map:";
            msg = msg + " column=" + columnName + " columnDiffMap=" + columnDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    public static DfColumnDiff createAdded(String columnName) {
        return new DfColumnDiff(columnName, DfDiffMode.ADDED);
    }

    public static DfColumnDiff createChanged(String columnName) {
        return new DfColumnDiff(columnName, DfDiffMode.CHANGED);
    }

    public static DfColumnDiff createDeleted(String columnName) {
        return new DfColumnDiff(columnName, DfDiffMode.DELETED);
    }

    public static DfColumnDiff createFromDiffMap(Map<String, Object> columnDiffMap) {
        return new DfColumnDiff(columnDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("columnName", _columnName);
        map.put("diffMode", _diffMode.toString());
        map.put("dbTypeDiff", _dbTypeDiff != null ? _dbTypeDiff.createDiffMap() : null);
        map.put("columnSizeDiff", _columnSizeDiff != null ? _columnSizeDiff.createDiffMap() : null);
        return map;
    }

    public void acceptDiffMap(Map<String, Object> columnDiffMap) {
        _dbTypeDiff = restoreNextPreviousDiff(columnDiffMap, "dbTypeDiff");
        _columnSizeDiff = restoreNextPreviousDiff(columnDiffMap, "columnSizeDiff");
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        if (!DfDiffMode.CHANGED.equals(_diffMode)) {
            return true; // if not change, always different
        }
        if (_dbTypeDiff != null || _columnSizeDiff != null) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnName() {
        return _columnName;
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

    public boolean hasDbTypeDiff() {
        return _dbTypeDiff != null;
    }

    public DfNextPreviousDiff getDbTypeDiff() {
        return _dbTypeDiff;
    }

    public void setDbTypeDiff(DfNextPreviousDiff dbTypeDiff) {
        _dbTypeDiff = dbTypeDiff;
    }

    public boolean hasColumnSizeDiff() {
        return _columnSizeDiff != null;
    }

    public DfNextPreviousDiff getColumnSizeDiff() {
        return _columnSizeDiff;
    }

    public void setColumnSizeDiff(DfNextPreviousDiff columnSizeDiff) {
        _columnSizeDiff = columnSizeDiff;
    }
}
