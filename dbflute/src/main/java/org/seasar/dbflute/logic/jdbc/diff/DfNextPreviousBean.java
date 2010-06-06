package org.seasar.dbflute.logic.jdbc.diff;

import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DfNextPreviousBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _nextValue;
    protected final String _previousValue;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfNextPreviousBean(String nextValue, String previousValue) {
        _nextValue = nextValue;
        _previousValue = previousValue;
    }

    public DfNextPreviousBean(Map<String, String> nextPreviousMap) {
        _nextValue = nextPreviousMap.get("next");
        _previousValue = nextPreviousMap.get("previous");
    }

    // ===================================================================================
    //                                                                             DiffMap
    //                                                                             =======
    public Map<String, String> createDiffMap() {
        final Map<String, String> map = DfCollectionUtil.newLinkedHashMap();
        map.put("next", _nextValue);
        map.put("previous", _previousValue);
        return map;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getNextValue() {
        return _nextValue;
    }

    public String getPreviousValue() {
        return _previousValue;
    }
}
