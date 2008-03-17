package org.seasar.dbflute.helper.flexiblename;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfFlexibleNameMap<KEY, VALUE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected LinkedHashMap<KEY, VALUE> map = new LinkedHashMap<KEY, VALUE>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFlexibleNameMap() {
    }
    
    public DfFlexibleNameMap(Map<KEY, ? extends VALUE> map) {
        putAll(map);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    public VALUE get(KEY key) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            return (VALUE) map.get(stringKey);
        } else {
            return (VALUE) map.get(key);
        }
    }

    public VALUE put(KEY key, VALUE value) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            return map.put(stringKey, value);
        } else {
            return map.put(key, value);
        }
    }

    public final void putAll(Map<KEY, ? extends VALUE> map) {
        final Set<KEY> keySet = map.keySet();
        for (KEY key : keySet) {
            put(key, map.get(key));
        }
    }

    public VALUE remove(KEY key) {
        final KEY stringKey = convertStringKey(key);
        if (stringKey != null) {
            return map.remove(stringKey);
        } else {
            return map.remove(key);
        }
    }

    public boolean containsKey(KEY key) {
        return get(key) != null;
    }
    
    public void clear() {
        map.clear();
    }
    
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public Set<KEY> keySet() {
        return map.keySet();
    }
    
    public Collection<VALUE> values() {
        return map.values();
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    @SuppressWarnings("unchecked")
    protected KEY convertStringKey(KEY key) {
        if (!(key instanceof String)) {
            return null;
        }
        return (KEY) toLowerCaseKey(removeUnderscore((String) key));
    }

    protected String removeUnderscore(String key) {
        return DfStringUtil.replace((String) key, "_", "");
    }

    protected String toLowerCaseKey(String key) {
        return ((String) key).toLowerCase();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return map.toString();
    }
}