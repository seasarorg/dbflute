package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfIndexDiff extends DfConstraintDiff {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfIndexDiff(String columnName, DfDiffType diffType) {
        super(columnName, diffType);
    }

    protected DfIndexDiff(Map<String, Object> indexDiffMap) {
        super(indexDiffMap);
    }

    public static DfIndexDiff createAdded(String constraintName) {
        return new DfIndexDiff(constraintName, DfDiffType.ADD);
    }

    public static DfIndexDiff createChanged(String constraintName) {
        return new DfIndexDiff(constraintName, DfDiffType.CHANGE);
    }

    public static DfIndexDiff createDeleted(String constraintName) {
        return new DfIndexDiff(constraintName, DfDiffType.DELETE);
    }

    public static DfIndexDiff createFromDiffMap(Map<String, Object> indexDiffMap) {
        return new DfIndexDiff(indexDiffMap);
    }
}
