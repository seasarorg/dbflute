package org.seasar.dbflute.logic.jdbc.schemadiff;

import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfNextPreviousDiff extends DfAbstractDiff {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // it may be quoted so not final
    protected String _next;
    protected String _previous;
    protected boolean _quoteDispIfNeeds;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfNextPreviousDiff(String nextValue, String previousValue) {
        _next = nextValue;
        _previous = previousValue;
    }

    protected DfNextPreviousDiff(Map<String, Object> nextPreviousDiffMap) {
        this(nextPreviousDiffMap, false);
    }

    protected DfNextPreviousDiff(Map<String, Object> nextPreviousDiffMap, boolean unquote) {
        final String next = (String) nextPreviousDiffMap.get("next");
        final String previous = (String) nextPreviousDiffMap.get("previous");
        final boolean bothQuoted = isBothQuoted(next, previous);
        _next = unquoteIfNeeds(next, unquote, bothQuoted);
        _previous = unquoteIfNeeds(previous, unquote, bothQuoted);
    }

    protected boolean isBothQuoted(String next, String previous) {
        if (next != null && previous != null) {
            return Srl.isQuotedDouble(next) && Srl.isQuotedDouble(previous);
        }
        return false;
    }

    protected String unquoteIfNeeds(String value, boolean unquote, boolean bothQuoted) {
        return (value != null && unquote && bothQuoted) ? Srl.unquoteDouble(value) : value;
    }

    public static DfNextPreviousDiff create(String nextValue, String previousValue) {
        return new DfNextPreviousDiff(nextValue, previousValue);
    }

    public static DfNextPreviousDiff create(Map<String, Object> nextPreviousDiffMap) {
        return new DfNextPreviousDiff(nextPreviousDiffMap);
    }

    public static DfNextPreviousDiff createUnquote(Map<String, Object> nextPreviousDiffMap) {
        return new DfNextPreviousDiff(nextPreviousDiffMap, true);
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

    public Map<String, String> createNextPreviousDiffQuotedMap() {
        final Map<String, String> map = DfCollectionUtil.newLinkedHashMap();
        map.put("next", "\"" + _next + "\"");
        map.put("previous", "\"" + _previous + "\"");
        return map;
    }

    // ===================================================================================
    //                                                                              Status
    //                                                                              ======
    public boolean hasDiff() { // required items only return false
        return !isSame(_next, _previous);
    }

    // ===================================================================================
    //                                                                          Expression
    //                                                                          ==========
    public String getDisplayForHtml() {
        final StringBuilder sb = new StringBuilder();
        final boolean quote = _quoteDispIfNeeds && (canBeTrimmed(_next) || canBeTrimmed(_previous));
        sb.append(quote ? "\"" : "").append(_previous).append(quote ? "\"" : "");
        sb.append(" -> ");
        sb.append(quote ? "\"" : "").append(_next).append(quote ? "\"" : "");
        return escape(sb.toString());
    }

    protected boolean canBeTrimmed(String value) {
        return value != null && value.trim().length() != value.length();
    }

    public void quoteDispIfNeeds() {
        _quoteDispIfNeeds = true;
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
