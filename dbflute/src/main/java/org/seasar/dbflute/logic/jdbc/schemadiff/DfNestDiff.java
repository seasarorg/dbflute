package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.logic.jdbc.schemadiff.DfAbstractDiff.NextPreviousHandler;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public interface DfNestDiff {

    DfDiffType getDiffType();

    String getKeyName();

    boolean hasDiff();

    Map<String, Object> createDiffMap();

    void acceptDiffMap(Map<String, Object> diffMap);

    List<NextPreviousHandler> getNextPreviousDiffList();
}
