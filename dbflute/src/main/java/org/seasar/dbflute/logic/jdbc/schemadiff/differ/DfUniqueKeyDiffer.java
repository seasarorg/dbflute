package org.seasar.dbflute.logic.jdbc.schemadiff.differ;

import java.util.List;

import org.apache.torque.engine.database.model.Table;
import org.apache.torque.engine.database.model.Unique;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfTableDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfUniqueKeyDiff;

/**
 * @author jflute
 */
public class DfUniqueKeyDiffer extends DfBasicConstraintKeyDiffer<Unique, DfUniqueKeyDiff> {

    public DfUniqueKeyDiffer(DfTableDiff tableDiff) {
        super(tableDiff);
    }

    public String constraintName(Unique key) {
        return key.getName();
    }

    public List<Unique> keyList(Table table) {
        return table.getUniqueList();
    }

    public String column(Unique key) {
        return buildCommaString(key.getIndexColumnMap().values());
    }

    public void diff(DfUniqueKeyDiff diff, Unique nextKey, Unique previousKey) {
        if (diff.hasDiff()) {
            _tableDiff.addUniqueKeyDiff(diff);
        }
    }

    public DfUniqueKeyDiff createAddedDiff(String constraintName) {
        return DfUniqueKeyDiff.createAdded(constraintName);
    }

    public DfUniqueKeyDiff createChangedDiff(String constraintName) {
        return DfUniqueKeyDiff.createChanged(constraintName);
    }

    public DfUniqueKeyDiff createDeletedDiff(String constraintName) {
        return DfUniqueKeyDiff.createDeleted(constraintName);
    }
}
