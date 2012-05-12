package org.seasar.dbflute.properties.assistant.freegenerate.converter;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class DfFreeGenMethodConverter {

    public static interface DfConvertMethodReflector {
        void reflect();
    }

    public boolean processConvertMethod(final String requestName, final Map<String, String> resultMap,
            final String key, final String value, List<DfConvertMethodReflector> reflectorList) {
        {
            final ScopeInfo capScope = Srl.extractScopeFirst(value, "df:cap(", ")");
            if (capScope != null) {
                reflectorList.add(new DfConvertMethodReflector() {
                    public void reflect() {
                        final String content = capScope.getContent();
                        final String refValue = resultMap.get(content);
                        assertColumnRefValueExists(content, refValue, requestName, key, refValue);
                        resultMap.put(key, Srl.initCap(refValue));
                    }
                });
                return true;
            }
        }
        {
            final ScopeInfo uncapScope = Srl.extractScopeFirst(value, "df:uncap(", ")");
            if (uncapScope != null) {
                reflectorList.add(new DfConvertMethodReflector() {
                    public void reflect() {
                        final String content = uncapScope.getContent();
                        final String refValue = resultMap.get(content);
                        assertColumnRefValueExists(content, refValue, requestName, key, refValue);
                        resultMap.put(key, Srl.initUncap(refValue));
                    }
                });
                return true;
            }
        }
        {
            final ScopeInfo capCamelScope = Srl.extractScopeFirst(value, "df:capCamel(", ")");
            if (capCamelScope != null) {
                reflectorList.add(new DfConvertMethodReflector() {
                    public void reflect() {
                        final String content = capCamelScope.getContent();
                        final String refValue = resultMap.get(content);
                        assertColumnRefValueExists(content, refValue, requestName, key, refValue);
                        resultMap.put(key, Srl.initCap(Srl.camelize(refValue)));
                    }
                });
                return true;
            }
        }
        {
            final ScopeInfo uncapCamelScope = Srl.extractScopeFirst(value, "df:uncapCamel(", ")");
            if (uncapCamelScope != null) {
                reflectorList.add(new DfConvertMethodReflector() {
                    public void reflect() {
                        final String content = uncapCamelScope.getContent();
                        final String refValue = resultMap.get(content);
                        assertColumnRefValueExists(content, refValue, requestName, key, refValue);
                        resultMap.put(key, Srl.initUncap(Srl.camelize(refValue)));
                    }
                });
                return true;
            }
        }
        return false;
    }

    protected void assertColumnRefValueExists(String content, String refValue, String requestName, final String key,
            final String value) {
        if (refValue == null) {
            String msg = "Not found the reference value of the key in FreeGen " + requestName + ":";
            msg = msg + " key=" + key + " ref=" + content;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

}
