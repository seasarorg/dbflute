package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public abstract class DfAbstractDiff {

    // ===================================================================================
    //                                                                         Create Diff
    //                                                                         ===========
    protected DfNextPreviousDiff createNextPreviousDiff(String next, String previous) {
        return DfNextPreviousDiff.create(next, previous);
    }

    protected DfNextPreviousDiff createNextPreviousDiff(Integer next, Integer previous) {
        return DfNextPreviousDiff.create(next.toString(), previous.toString());
    }

    protected DfNextPreviousDiff createNextPreviousDiff(Boolean next, Boolean previous) {
        return DfNextPreviousDiff.create(next.toString(), previous.toString());
    }

    protected DfNextPreviousDiff restoreNextPreviousDiff(Map<String, Object> diffMap, String key) {
        final Object value = diffMap.get(key);
        if (value == null) {
            return null;
        }
        assertElementValueMap(key, value, diffMap);
        @SuppressWarnings("unchecked")
        final Map<String, Object> nextPreviousDiffMap = (Map<String, Object>) value;
        return DfNextPreviousDiff.create(nextPreviousDiffMap);
    }

    protected DfTableDiff createTableDiff(Map<String, Object> tableDiffMap) {
        return DfTableDiff.createFromDiffMap(tableDiffMap);
    }

    protected DfColumnDiff createColumnDiff(Map<String, Object> columnDiffMap) {
        return DfColumnDiff.createFromDiffMap(columnDiffMap);
    }

    protected static interface NextPreviousItemHandler {
        String propertyName();

        DfNextPreviousDiff provide();

        void restore(Map<String, Object> diffMap);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    // ===================================================================================
    //                                                                         Same Helper
    //                                                                         ===========
    protected static interface NextPreviousSetupper<OBJECT, DIFF> {
        Object provide(OBJECT obj);

        void setup(DIFF diff, DfNextPreviousDiff nextPreviousDiff);
    }

    protected boolean isSame(Object next, Object previous) {
        if (next == null && previous == null) {
            return true;
        }
        if (next == null || previous == null) {
            return false;
        }
        return next.equals(previous);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertElementValueMap(String key, Object value, Map<String, Object> diffMap) {
        if (!(value instanceof Map<?, ?>)) { // basically no way
            String msg = "The element in diff-map should be Map:";
            msg = msg + " key=" + key + " value=" + value + " diffMap=" + diffMap;
            throw new IllegalStateException(msg);
        }
    }
}
