package org.seasar.dbflute.logic.jdbc.diff;

import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfColumnDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _columnName;
    protected final DfDiffMode _diffMode;
    protected DfNextPreviousBean _dbTypeDiff;
    protected DfNextPreviousBean _columnSizeDiff;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfColumnDiff(String columnName, DfDiffMode diffMode) {
        _columnName = columnName;
        _diffMode = diffMode;
    }

    protected DfColumnDiff(Map<String, Object> diffMap) {
        _columnName = (String) diffMap.get("columnName");
        _diffMode = DfDiffMode.valueOf((String) diffMap.get("diffMode"));
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

    public static DfColumnDiff createFromDiffMap(Map<String, Object> diffMap) {
        return new DfColumnDiff(diffMap);
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
    //                                                                             DiffMap
    //                                                                             =======
    public Map<String, Object> createDiffMap() {
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("diffMode", _diffMode.toString());
        map.put("dbTypeDiff", _dbTypeDiff != null ? _dbTypeDiff.createDiffMap() : null);
        map.put("columnSizeDiff", _columnSizeDiff != null ? _columnSizeDiff.createDiffMap() : null);
        return map;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnName() {
        return _columnName;
    }

    public void setDbTypeDiff(DfNextPreviousBean dbTypeDiff) {
        _dbTypeDiff = dbTypeDiff;
    }

    public void setColumnSizeDiff(DfNextPreviousBean columnSizeDiff) {
        _columnSizeDiff = columnSizeDiff;
    }
}
