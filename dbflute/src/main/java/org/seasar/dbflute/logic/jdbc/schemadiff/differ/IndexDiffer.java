package org.seasar.dbflute.logic.jdbc.schemadiff.differ;

import java.util.List;

import org.apache.torque.engine.database.model.Index;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfIndexDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfTableDiff;

/**
 * @author jflute
 */
public class IndexDiffer extends BasicConstraintKeyDiffer<Index, DfIndexDiff> {

    public IndexDiffer(DfTableDiff tableDiff) {
        super(tableDiff);
    }

    public String constraintName(Index key) {
        return key.getName();
    }

    public List<Index> keyList(Table table) {
        return table.getIndexList();
    }

    public String column(Index key) {
        return buildCommaString(key.getIndexColumnMap().values());
    }

    public void diff(DfIndexDiff diff, Index nextKey, Index previousKey) {
        if (diff.hasDiff()) {
            _tableDiff.addIndexDiff(diff);
        }
    }

    public DfIndexDiff createAddedDiff(String constraintName) {
        return DfIndexDiff.createAdded(constraintName);
    }

    public DfIndexDiff createChangedDiff(String constraintName) {
        return DfIndexDiff.createChanged(constraintName);
    }

    public DfIndexDiff createDeletedDiff(String constraintName) {
        return DfIndexDiff.createDeleted(constraintName);
    }
}
