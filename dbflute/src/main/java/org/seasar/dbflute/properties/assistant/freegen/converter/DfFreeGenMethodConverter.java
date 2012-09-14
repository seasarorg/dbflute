/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.properties.assistant.freegen.converter;

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

    public boolean processConvertMethod(final String requestName, final Map<String, Object> resultMap,
            final String key, final String value, List<DfConvertMethodReflector> reflectorList) {
        {
            final ScopeInfo capScope = Srl.extractScopeFirst(value, "df:cap(", ")");
            if (capScope != null) {
                reflectorList.add(new DfConvertMethodReflector() {
                    public void reflect() {
                        final String content = capScope.getContent();
                        final String refValue = (String) resultMap.get(content);
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
                        final String refValue = (String) resultMap.get(content);
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
                        final String refValue = (String) resultMap.get(content);
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
                        final String refValue = (String) resultMap.get(content);
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
