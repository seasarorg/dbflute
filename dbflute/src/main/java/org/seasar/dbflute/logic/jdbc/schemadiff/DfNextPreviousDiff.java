package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfNextPreviousDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _next;
    protected final String _previous;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfNextPreviousDiff(String nextValue, String previousValue) {
        _next = nextValue;
        _previous = previousValue;
    }

    protected DfNextPreviousDiff(Map<String, Object> nextPreviousDiffMap) {
        _next = (String) nextPreviousDiffMap.get("next");
        _previous = (String) nextPreviousDiffMap.get("previous");
    }

    public static DfNextPreviousDiff create(String nextValue, String previousValue) {
        return new DfNextPreviousDiff(nextValue, previousValue);
    }

    public static DfNextPreviousDiff create(Map<String, Object> nextPreviousDiffMap) {
        return new DfNextPreviousDiff(nextPreviousDiffMap);
    }

    // ===================================================================================
    //                                                                            Diff Map
    //                                                                            ========
    public Map<String, String> createNextPreviousDiffMap() {
        final Map<String, String> map = DfCollectionUtil.newLinkedHashMap();
        map.put("next", _next);
        map.put("previous", _previous);
        return map;
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() {
        return !isSame(_next, _previous);
    }

    // ===================================================================================
    //                                                                          Expression
    //                                                                          ==========
    public String getDisplayForHtml() {
        return escape(_previous + " to " + _next);
    }

    protected String escape(String value) {
        return getDocumentProperties().resolveTextForSchemaHtml(value);
    }

    protected DfDocumentProperties getDocumentProperties() {
        return DfBuildProperties.getInstance().getDocumentProperties();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getNext() {
        return _next;
    }

    public String getPrevious() {
        return _previous;
    }
}
