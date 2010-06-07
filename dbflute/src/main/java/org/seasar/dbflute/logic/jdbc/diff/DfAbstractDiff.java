package org.seasar.dbflute.logic.jdbc.diff;

import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public abstract class DfAbstractDiff {

    protected void assertElementMap(String key, Object value, Map<String, Object> diffMap) {
        if (!(value instanceof Map<?, ?>)) { // basically no way
            String msg = "The element in table diff-map should be Map:";
            msg = msg + " key=" + key + " value=" + value + " diffMap=" + diffMap;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                         Create Diff
    //                                                                         ===========
    protected DfNextPreviousDiff createNextPreviousDiff(String next, String previous) {
        return DfNextPreviousDiff.create(next, previous);
    }

    protected DfNextPreviousDiff createNextPreviousDiff(Map<String, Object> diffMap, String key) {
        final Object value = diffMap.get(key);
        assertElementMap(key, value, diffMap);
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

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected boolean isSame(Object next, Object previous) {
        if (next == null && previous == null) {
            return true;
        }
        if (next == null || previous == null) {
            return false;
        }
        return next.equals(previous);
    }

}
