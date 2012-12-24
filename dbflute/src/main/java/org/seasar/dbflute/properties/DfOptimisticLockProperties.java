/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.8.8.1 (2009/01/09 Friday)
 */
public final class DfOptimisticLockProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param prop Properties. (NotNull)
     */
    public DfOptimisticLockProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                      Optimistic Lock Definition Map
    //                                                      ==============================
    public static final String KEY_optimisticLockDefinitionMap = "optimisticLockDefinitionMap";
    protected Map<String, Object> _optimisticLockDefinitionMap;

    public Map<String, Object> getOptimisticLockDefinitionMap() {
        if (_optimisticLockDefinitionMap == null) {
            _optimisticLockDefinitionMap = mapProp("torque." + KEY_optimisticLockDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _optimisticLockDefinitionMap;
    }

    public String getProperty(String key, String defaultValue) {
        Map<String, Object> map = getOptimisticLockDefinitionMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be string:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value;
            } else {
                return defaultValue;
            }
        }
        return stringProp("torque." + key, defaultValue);
    }

    public boolean isProperty(String key, boolean defaultValue) {
        Map<String, Object> map = getOptimisticLockDefinitionMap();
        Object obj = map.get(key);
        if (obj != null) {
            if (!(obj instanceof String)) {
                String msg = "The key's value should be boolean:";
                msg = msg + " " + DfTypeUtil.toClassTitle(obj) + "=" + obj;
                throw new IllegalStateException(msg);
            }
            String value = (String) obj;
            if (value.trim().length() > 0) {
                return value.trim().equalsIgnoreCase("true");
            } else {
                return defaultValue;
            }
        }
        return booleanProp("torque." + key, defaultValue);
    }

    // ===================================================================================
    //                                                                          Field Name
    //                                                                          ==========
    public String getUpdateDateFieldName() {
        return getProperty("updateDateFieldName", "");
    }

    public String getVersionNoFieldName() {
        return getProperty("versionNoFieldName", "version_no");
    }

    public boolean isOptimisticLockColumn(String columnName) {
        final String updateDate = getUpdateDateFieldName();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(updateDate) && updateDate.equalsIgnoreCase(columnName)) {
            return true;
        }
        final String versionNo = getVersionNoFieldName();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(versionNo) && versionNo.equalsIgnoreCase(columnName)) {
            return true;
        }
        return false;
    }
}