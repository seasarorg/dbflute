package org.seasar.dbflute.logic.jdbc.schemadiff.differ;

import java.util.List;

import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfConstraintDiff;

/**
 * @author jflute
 * @param <KEY> The type of constraint key.
 * @param <DIFF> The type of constraint diff.
 */
public interface ConstraintKeyDiffer<KEY, DIFF extends DfConstraintDiff> {

    List<KEY> keyList(Table table);

    String constraintName(KEY key);

    String column(KEY key);

    boolean isSameConstraintName(String next, String previous);

    boolean isSameStructure(KEY next, KEY previous);

    void diff(DIFF diff, KEY nextKey, KEY previousKey);

    DIFF createAddedDiff(String constraintName);

    DIFF createChangedDiff(String constraintName);

    DIFF createDeletedDiff(String constraintName);
}
