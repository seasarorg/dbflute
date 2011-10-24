package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfForeignKeyDiff extends DfConstraintDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
    protected DfNextPreviousDiff _foreignTableDiff;

    {
        _nextPreviousItemList.add(new NextPreviousHandlerBase() {
            public String titleName() {
                return "FK Table";
            }

            public String propertyName() {
                return "foreignTableDiff";
            }

            public DfNextPreviousDiff provide() {
                return _foreignTableDiff;
            }

            public void restore(Map<String, Object> foreignKeyDiffMap) {
                _foreignTableDiff = restoreNextPreviousDiff(foreignKeyDiffMap, propertyName());
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfForeignKeyDiff(String columnName, DfDiffType diffType) {
        super(columnName, diffType);
    }

    protected DfForeignKeyDiff(Map<String, Object> foreignKeyDiffMap) {
        super(foreignKeyDiffMap);
    }

    public static DfForeignKeyDiff createAdded(String constraintName) {
        return new DfForeignKeyDiff(constraintName, DfDiffType.ADD);
    }

    public static DfForeignKeyDiff createChanged(String constraintName) {
        return new DfForeignKeyDiff(constraintName, DfDiffType.CHANGE);
    }

    public static DfForeignKeyDiff createDeleted(String constraintName) {
        return new DfForeignKeyDiff(constraintName, DfDiffType.DELETE);
    }

    public static DfForeignKeyDiff createFromDiffMap(Map<String, Object> columnDiffMap) {
        return new DfForeignKeyDiff(columnDiffMap);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                             Diff Item
    //                                             ---------
    public boolean hasForeignTableDiff() {
        return _foreignTableDiff != null;
    }

    public DfNextPreviousDiff getForeignTableDiff() {
        return _foreignTableDiff;
    }

    public void setForeignTableDiff(DfNextPreviousDiff foreignTableDiff) {
        _foreignTableDiff = foreignTableDiff;
    }
}
