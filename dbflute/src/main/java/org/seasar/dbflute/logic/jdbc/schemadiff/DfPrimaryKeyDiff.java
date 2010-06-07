package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfPrimaryKeyDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final String _constraintName;
    protected final DfDiffType _diffType;

    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
    protected DfNextPreviousDiff _columnDiff;

    protected List<NextPreviousItemHandler> _nextPreviousItemList = DfCollectionUtil.newArrayList();
    {
        _nextPreviousItemList.add(new NextPreviousItemHandler() {
            public String propertyName() {
                return "columnDiff";
            }

            public DfNextPreviousDiff provide() {
                return _columnDiff;
            }

            public void restore(Map<String, Object> primaryKeyDiffMap) {
                _columnDiff = restoreNextPreviousDiff(primaryKeyDiffMap, propertyName());
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfPrimaryKeyDiff(String columnName, DfDiffType diffType) {
        _constraintName = columnName;
        _diffType = diffType;
    }

    protected DfPrimaryKeyDiff(Map<String, Object> primaryKeyDiffMap) {
        _constraintName = (String) primaryKeyDiffMap.get("constraintName");
        assertPrimaryKeyNameExists(_constraintName, primaryKeyDiffMap);
        _diffType = DfDiffType.valueOf((String) primaryKeyDiffMap.get("diffType"));
        acceptColumnDiffMap(primaryKeyDiffMap);
    }

    protected void assertPrimaryKeyNameExists(String constraintName, Map<String, Object> primaryKeyDiffMap) {
        if (constraintName == null) { // basically no way
            String msg = "The constraintName is required in primary-key diff-map:";
            msg = msg + " primaryKeyDiffMap=" + primaryKeyDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertDiffTypeExists(String constraintName, Map<String, Object> primaryKeyDiffMap,
            DfDiffType diffType) {
        if (diffType == null) { // basically no way
            String msg = "The diffType is required in column diff-map:";
            msg = msg + " primaryKey=" + constraintName + " primaryKeyDiffMap=" + primaryKeyDiffMap;
            throw new IllegalStateException(msg);
        }
    }

    public static DfPrimaryKeyDiff createAdded(String constraintName) {
        return new DfPrimaryKeyDiff(constraintName, DfDiffType.ADD);
    }

    public static DfPrimaryKeyDiff createChanged(String constraintName) {
        return new DfPrimaryKeyDiff(constraintName, DfDiffType.CHANGE);
    }

    public static DfPrimaryKeyDiff createDeleted(String constraintName) {
        return new DfPrimaryKeyDiff(constraintName, DfDiffType.DELETE);
    }

    public static DfPrimaryKeyDiff createFromDiffMap(Map<String, Object> columnDiffMap) {
        return new DfPrimaryKeyDiff(columnDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, Object> createPrimaryKeyDiffMap() {
        final Map<String, Object> map = DfCollectionUtil.newLinkedHashMap();
        map.put("constraintName", _constraintName);
        map.put("diffType", _diffType.toString());
        final List<NextPreviousItemHandler> nextPreviousItemList = _nextPreviousItemList;
        for (NextPreviousItemHandler provider : nextPreviousItemList) {
            final DfNextPreviousDiff nextPreviousDiff = provider.provide();
            if (nextPreviousDiff != null) {
                map.put(provider.propertyName(), nextPreviousDiff.createNextPreviousDiffMap());
            }
        }
        return map;
    }

    public void acceptColumnDiffMap(Map<String, Object> columnDiffMap) {
        final List<NextPreviousItemHandler> nextPreviousItemList = _nextPreviousItemList;
        for (NextPreviousItemHandler provider : nextPreviousItemList) {
            provider.restore(columnDiffMap);
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
        return false;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getConstraintName() {
        return _constraintName;
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
    public boolean hasColumnDiff() {
        return _columnDiff != null;
    }

    public DfNextPreviousDiff getColumnDiff() {
        return _columnDiff;
    }

    public void setColumnDiff(DfNextPreviousDiff columnDiff) {
        _columnDiff = columnDiff;
    }
}
