/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common column properties.
 * 
 * @author jflute
 */
public final class DfCommonColumnProperties extends DfAbstractHelperProperties {

    private static final Log _log = LogFactory.getLog(DfCommonColumnProperties.class);

    public DfCommonColumnProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                      Properties - Common-Column
    //                                                      ==========================
    public static final String KEY_commonColumnMap = "commonColumnMap";
    protected Map<String, Object> _commonColumnMap;

    public Map<String, Object> getCommonColumnMap() {
        if (_commonColumnMap == null) {
            _commonColumnMap = mapProp("torque." + KEY_commonColumnMap, DEFAULT_EMPTY_MAP);
        }
        return _commonColumnMap;
    }

    protected List<String> _commonColumnNameList;

    public List<String> getCommonColumnNameList() {
        if (_commonColumnNameList == null) {
            final Map<String, Object> commonColumnMap = getCommonColumnMap();
            _commonColumnNameList = new ArrayList<String>(commonColumnMap.keySet());
        }
        return _commonColumnNameList;
    }

    // --------------------------------------
    //                           aspect point
    //                           ------------
    public String getCommonColumnSetupInterceptorAspectPoint() {
        return stringProp("torque.commonColumnSetupInterceptorAspectPoint", "behavior");
    }

    public boolean isAvailableCommonColumnSetupInterceptorToBehavior() {
        if (!isExistCommonColumnSetupElement()) {
            return false;
        }
        final boolean oldProp = booleanProp("torque.isAvailableCommonColumnSetupInterceptorToBehavior", false);
        if (oldProp) {
            return true;
        }
        final String aspectPoint = getCommonColumnSetupInterceptorAspectPoint();
        if (aspectPoint.equalsIgnoreCase("behavior")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAvailableCommonColumnSetupInterceptorToDao() {
        if (!isExistCommonColumnSetupElement()) {
            return false;
        }
        final boolean oldProp = booleanProp("torque.isAvailableCommonColumnSetupInterceptorToDao", false);
        if (oldProp) {
            return true;
        }
        final String aspectPoint = getCommonColumnSetupInterceptorAspectPoint();
        if (aspectPoint.equalsIgnoreCase("dao")) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isExistCommonColumnSetupElement() {
        final Map<String, Object> insertElementMap = getCommonColumnSetupBeforeInsertInterceptorLogicMap();
        final Map<String, Object> updateElementMap = getCommonColumnSetupBeforeUpdateInterceptorLogicMap();
        final Map<String, Object> deleteElementMap = getCommonColumnSetupBeforeDeleteInterceptorLogicMap();
        if (insertElementMap.isEmpty() && updateElementMap.isEmpty() && deleteElementMap.isEmpty()) {
            return false;
        }
        return true;
    }

    // --------------------------------------
    //                                 insert
    //                                 ------
    public static final String KEY_commonColumnSetupBeforeInsertInterceptorLogicMap = "commonColumnSetupBeforeInsertInterceptorLogicMap";
    protected Map<String, Object> _commonColumnSetupBeforeInsertInterceptorLogicMap;

    public Map<String, Object> getCommonColumnSetupBeforeInsertInterceptorLogicMap() {
        if (_commonColumnSetupBeforeInsertInterceptorLogicMap == null) {
            final String key = "torque." + KEY_commonColumnSetupBeforeInsertInterceptorLogicMap;
            _commonColumnSetupBeforeInsertInterceptorLogicMap = mapProp(key, DEFAULT_EMPTY_MAP);
        }
        return _commonColumnSetupBeforeInsertInterceptorLogicMap;
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeInsertInterceptorLogicMap(String columnName) {
        final Map map = getCommonColumnSetupBeforeInsertInterceptorLogicMap();
        final String logic = (String) map.get(columnName);
        if (logic != null && logic.trim().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getCommonColumnSetupBeforeInsertInterceptorLogicByColumnName(String columnName) {
        final Map map = getCommonColumnSetupBeforeInsertInterceptorLogicMap();
        return (String) map.get(columnName);
    }

    // --------------------------------------
    //                                 update
    //                                 ------
    public static final String KEY_commonColumnSetupBeforeUpdateInterceptorLogicMap = "commonColumnSetupBeforeUpdateInterceptorLogicMap";
    protected Map<String, Object> _commonColumnSetupBeforeUpdateInterceptorLogicMap;

    public Map<String, Object> getCommonColumnSetupBeforeUpdateInterceptorLogicMap() {
        if (_commonColumnSetupBeforeUpdateInterceptorLogicMap == null) {
            final String key = "torque." + KEY_commonColumnSetupBeforeUpdateInterceptorLogicMap;
            _commonColumnSetupBeforeUpdateInterceptorLogicMap = mapProp(key, DEFAULT_EMPTY_MAP);
        }
        return _commonColumnSetupBeforeUpdateInterceptorLogicMap;
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeUpdateInterceptorLogicMap(String columnName) {
        final Map map = getCommonColumnSetupBeforeUpdateInterceptorLogicMap();
        final String logic = (String) map.get(columnName);
        if (logic != null && logic.trim().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getCommonColumnSetupBeforeUpdateInterceptorLogicByColumnName(String columnName) {
        final Map map = getCommonColumnSetupBeforeUpdateInterceptorLogicMap();
        return (String) map.get(columnName);
    }

    // --------------------------------------
    //                                 delete
    //                                 ------
    public static final String KEY_commonColumnSetupBeforeDeleteInterceptorLogicMap = "commonColumnSetupBeforeDeleteInterceptorLogicMap";
    protected Map<String, Object> _commonColumnSetupBeforeDeleteInterceptorLogicMap;

    public Map<String, Object> getCommonColumnSetupBeforeDeleteInterceptorLogicMap() {
        if (_commonColumnSetupBeforeDeleteInterceptorLogicMap == null) {
            final String key = "torque." + KEY_commonColumnSetupBeforeDeleteInterceptorLogicMap;
            _commonColumnSetupBeforeDeleteInterceptorLogicMap = mapProp(key, DEFAULT_EMPTY_MAP);
        }
        return _commonColumnSetupBeforeDeleteInterceptorLogicMap;
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeDeleteInterceptorLogicMap(String columnName) {
        final Map map = getCommonColumnSetupBeforeDeleteInterceptorLogicMap();
        final String logic = (String) map.get(columnName);
        if (logic != null && logic.trim().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getCommonColumnSetupBeforeDeleteInterceptorLogicByColumnName(String columnName) {
        final Map map = getCommonColumnSetupBeforeDeleteInterceptorLogicMap();
        return (String) map.get(columnName);
    }
}