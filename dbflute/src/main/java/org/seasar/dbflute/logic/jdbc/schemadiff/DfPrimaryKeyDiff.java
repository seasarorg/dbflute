package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfPrimaryKeyDiff extends DfConstraintDiff {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfPrimaryKeyDiff(String columnName, DfDiffType diffType) {
        super(columnName, diffType);
    }

    protected DfPrimaryKeyDiff(Map<String, Object> primaryKeyDiffMap) {
        super(primaryKeyDiffMap);
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

    public static DfPrimaryKeyDiff createFromDiffMap(Map<String, Object> primaryKeyDiffMap) {
        return new DfPrimaryKeyDiff(primaryKeyDiffMap);
    }
}
