package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfUniqueKeyDiff extends DfConstraintDiff {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfUniqueKeyDiff(String columnName, DfDiffType diffType) {
        super(columnName, diffType);
    }

    protected DfUniqueKeyDiff(Map<String, Object> uniqueKeyDiffMap) {
        super(uniqueKeyDiffMap);
    }

    public static DfUniqueKeyDiff createAdded(String constraintName) {
        return new DfUniqueKeyDiff(constraintName, DfDiffType.ADD);
    }

    public static DfUniqueKeyDiff createChanged(String constraintName) {
        return new DfUniqueKeyDiff(constraintName, DfDiffType.CHANGE);
    }

    public static DfUniqueKeyDiff createDeleted(String constraintName) {
        return new DfUniqueKeyDiff(constraintName, DfDiffType.DELETE);
    }

    public static DfUniqueKeyDiff createFromDiffMap(Map<String, Object> uniqueKeyDiffMap) {
        return new DfUniqueKeyDiff(uniqueKeyDiffMap);
    }
}
