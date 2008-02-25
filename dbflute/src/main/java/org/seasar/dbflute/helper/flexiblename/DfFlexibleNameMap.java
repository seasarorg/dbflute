package org.seasar.dbflute.helper.flexiblename;

import java.util.Map;

import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.framework.util.CaseInsensitiveMap;

/**
 * @author jflute
 */
public class DfFlexibleNameMap<T, E> {

    final CaseInsensitiveMap caseInsensitiveMap;

    public DfFlexibleNameMap(Map map) {
        caseInsensitiveMap = new CaseInsensitiveMap();
        caseInsensitiveMap.putAll(map);
    }

    public boolean containsKey(String flexibleName) {
        if (caseInsensitiveMap.containsKey(flexibleName)) {
            return true;
        }
        if (caseInsensitiveMap.containsKey(removeUnderscore(flexibleName))) {
            return true;
        }
        return false;
    }

    public E get(String flexibleName) {
        final Object value = caseInsensitiveMap.get(flexibleName);
        if (value != null) {
            return (E) value;
        }
        return (E) caseInsensitiveMap.get(removeUnderscore(flexibleName));
    }
    
    public boolean isEmpty() {
        return caseInsensitiveMap.isEmpty();
    }

    protected String removeUnderscore(String target) {
        return DfStringUtil.replace(target, "_", "");
    }
    
    public String toString() {
        return caseInsensitiveMap.toString();
    }
}