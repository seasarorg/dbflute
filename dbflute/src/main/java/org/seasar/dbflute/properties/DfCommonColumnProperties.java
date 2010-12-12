/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.properties.assistant.commoncolumn.CommonColumnSetupResource;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public final class DfCommonColumnProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCommonColumnProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                       Common Column
    //                                                                       =============
    public static final String KEY_commonColumnMap = "commonColumnMap";
    protected Map<String, Object> _commonColumnTopMap;
    protected Map<String, String> _commonColumnMap;

    @SuppressWarnings("unchecked")
    public Map<String, String> getCommonColumnMap() {
        if (_commonColumnTopMap == null) {
            _commonColumnTopMap = mapProp("torque." + KEY_commonColumnMap, DEFAULT_EMPTY_MAP);
            if (_commonColumnTopMap.containsKey(KEY_commonColumnMap)) {
                // For the way by dfprop-setting.
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // map:{
                //     ; commonColumnMap = map:{
                //         ; REGISTER_DATETIME=TIMESTAMP ; REGISTER_USER=VARCHAR ; REGISTER_PROCESS=VARCHAR
                //         ; UPDATE_DATETIME=TIMESTAMP   ; UPDATE_USER=VARCHAR   ; UPDATE_PROCESS=VARCHAR
                //     }
                //     ; ...
                // - - - - - - - - - -/ 
                final Map<String, String> commonColumnMap = (Map<String, String>) _commonColumnTopMap
                        .get(KEY_commonColumnMap);
                final Map<String, String> flmap = StringKeyMap.createAsFlexibleOrdered();
                flmap.putAll(commonColumnMap);
                _commonColumnMap = flmap;
            } else {
                _commonColumnMap = new HashMap<String, String>(); // empty
            }
        }
        return _commonColumnMap;
    }

    protected List<String> _commonColumnNameList;

    public List<String> getCommonColumnNameList() {
        if (_commonColumnNameList == null) {
            final Map<String, String> commonColumnMap = getCommonColumnMap();
            _commonColumnNameList = new ArrayList<String>(commonColumnMap.keySet());
        }
        return _commonColumnNameList;
    }

    // -----------------------------------------------------
    //                                     Column Conversion
    //                                     -----------------
    // *basically unsupported but this logic is remained for the future
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // ex) If common columns start with table name
    // map:{
    //     ; commonColumnMap = map:{
    //         ; $-TABLE_NAME_REGISTER_DATETIME=TIMESTAMP ; ...
    //         ; $-TABLE_NAME_UPDATE_DATETIME=TIMESTAMP   ; ...
    //     }
    //     ; ...
    // - - - - - - - - - -/
    public static final String COMMON_COLUMN_CONVERSION_PREFIX_MARK = "$-";

    protected List<String> _commonColumnNameConversionList;

    public List<String> getCommonColumnNameConversionList() {
        if (_commonColumnNameConversionList == null) {
            _commonColumnNameConversionList = new ArrayList<String>();
            final Map<String, String> commonColumnMap = getCommonColumnMap();
            final Set<String> keySet = commonColumnMap.keySet();
            for (String columnName : keySet) {
                if (columnName.startsWith(COMMON_COLUMN_CONVERSION_PREFIX_MARK)) {
                    _commonColumnNameConversionList.add(columnName);
                }
            }
        }
        return _commonColumnNameConversionList;
    }

    public boolean isCommonColumnConversion(String commonColumnName) {
        return commonColumnName.startsWith(COMMON_COLUMN_CONVERSION_PREFIX_MARK);
    }

    public String filterCommonColumn(String commonColumnName) {
        if (commonColumnName.startsWith(COMMON_COLUMN_CONVERSION_PREFIX_MARK)) {
            return commonColumnName.substring(COMMON_COLUMN_CONVERSION_PREFIX_MARK.length());
        } else {
            return commonColumnName;
        }
    }

    // ===================================================================================
    //                                                                 Common Column Setup
    //                                                                 ===================
    public boolean isExistCommonColumnSetupElement() {
        final Map<String, Object> insertElementMap = getBeforeInsertMap();
        final Map<String, Object> updateElementMap = getBeforeUpdateMap();
        if (insertElementMap.isEmpty() && updateElementMap.isEmpty()) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                    Intercept Insert
    //                                                                    ================
    public static final String KEY_commonColumnSetupBeforeInsertInterceptorLogicMap = "commonColumnSetupBeforeInsertInterceptorLogicMap";
    protected Map<String, Object> _beforeInsertMap;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getBeforeInsertMap() {
        if (_beforeInsertMap == null) {
            getCommonColumnMap(); // For initialization of commonColumnMap.
            if (_commonColumnTopMap != null && _commonColumnTopMap.containsKey("beforeInsertMap")) {
                // For the way by dfprop-setting.
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // ; beforeInsertMap = map:{
                //     ; REGISTER_DATETIME = $$AccessContext$$.getAccessTimestampOnThread()
                //     ; REGISTER_USER     = $$AccessContext$$.getAccessUserOnThread()
                //     ; REGISTER_PROCESS  = $$AccessContext$$.getAccessProcessOnThread()
                //     ; UPDATE_DATETIME   = entity.getRegisterDatetime()
                //     ; UPDATE_USER       = entity.getRegisterUser()
                //     ; UPDATE_PROCESS    = entity.getRegisterProcess()
                // }
                // - - - - - - - - - -/ 
                _beforeInsertMap = (Map<String, Object>) _commonColumnTopMap.get("beforeInsertMap");
            } else {
                // For old style.
                final String key = "torque." + KEY_commonColumnSetupBeforeInsertInterceptorLogicMap;
                _beforeInsertMap = mapProp(key, DEFAULT_EMPTY_MAP);
            }
            filterCommonColumnSetupValue(_beforeInsertMap);
        }
        return _beforeInsertMap;
    }

    public boolean hasCommonColumnBeforeInsertLogic(String columnName) {
        final Map<String, Object> map = getBeforeInsertMap();
        final String logic = (String) map.get(columnName);
        if (logic != null && logic.trim().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getCommonColumnBeforeInsertLogicByColumnName(String columnName) {
        final Map<String, Object> map = getBeforeInsertMap();
        return (String) map.get(columnName);
    }

    // ===================================================================================
    //                                                                    Intercept Update
    //                                                                    ================
    public static final String KEY_commonColumnSetupBeforeUpdateInterceptorLogicMap = "commonColumnSetupBeforeUpdateInterceptorLogicMap";
    protected Map<String, Object> _beforeUpdateMap;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getBeforeUpdateMap() {
        if (_beforeUpdateMap == null) {
            getCommonColumnMap(); // For initialization of commonColumnMap.
            if (_commonColumnTopMap != null && _commonColumnTopMap.containsKey("beforeUpdateMap")) {
                // For the way by dfprop-setting.
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // ; beforeUpdateMap = map:{
                //     ; REGISTER_DATETIME = $$AccessContext$$.getAccessTimestampOnThread()
                //     ; REGISTER_USER     = $$AccessContext$$.getAccessUserOnThread()
                //     ; REGISTER_PROCESS  = $$AccessContext$$.getAccessProcessOnThread()
                // }
                // - - - - - - - - - -/ 
                _beforeUpdateMap = (Map<String, Object>) _commonColumnTopMap.get("beforeUpdateMap");
            } else {
                // For old style.
                final String key = "torque." + KEY_commonColumnSetupBeforeUpdateInterceptorLogicMap;
                _beforeUpdateMap = mapProp(key, DEFAULT_EMPTY_MAP);
            }
            filterCommonColumnSetupValue(_beforeUpdateMap);
        }
        return _beforeUpdateMap;
    }

    public boolean hasCommonColumnBeforeUpdateLogic(String columnName) {
        final Map<String, Object> map = getBeforeUpdateMap();
        final String logic = (String) map.get(columnName);
        if (logic != null && logic.trim().length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getCommonColumnBeforeUpdateLogicByColumnName(String columnName) {
        final Map<String, Object> map = getBeforeUpdateMap();
        return (String) map.get(columnName);
    }

    // ===================================================================================
    //                                                                    Intercept Common
    //                                                                    ================
    // -----------------------------------------------------
    //                                        Logic Handling
    //                                        --------------
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // ex) Get a value from a method of classification.
    // DELETE_FLG = $entity.classifyDeleteFlgTrue()
    // - - - - - - - - - -/
    public boolean isCommonColumnSetupInvokingLogic(String logic) {
        return logic.startsWith("$");
    }

    public String removeCommonColumnSetupInvokingMark(String logic) {
        String filtered = filterInvokingLogic(logic.substring("$".length()));
        if (!filtered.trim().endsWith(";")) {
            filtered = filtered + ";";
        }
        return filtered;
    }

    protected String filterInvokingLogic(String logic) {
        String tmp = DfPropertyUtil.convertAll(logic, "$$Semicolon$$", ";");
        tmp = DfPropertyUtil.convertAll(tmp, "$$StartBrace$$", "{");
        tmp = DfPropertyUtil.convertAll(tmp, "$$EndBrace$$", "}");
        return tmp;
    }

    // -----------------------------------------------------
    //                                                filter
    //                                                ------
    protected void filterCommonColumnSetupValue(Map<String, Object> map) {
        final String baseCommonPackage = getBasicProperties().getBaseCommonPackage();
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            String value = (String) map.get(key);
            if (value != null && value.contains("$$allcommon$$")) {
                value = DfStringUtil.replace(value, "$$allcommon$$", baseCommonPackage);
            }
            if (value != null && value.contains("$$AccessContext$$")) {
                if (DfBuildProperties.getInstance().isVersionJavaOverNinety()) { // as patch for 90
                    final String accessContext = "org.seasar.dbflute.AccessContext";
                    value = DfStringUtil.replace(value, "$$AccessContext$$", accessContext);
                } else {
                    final String accessContext = baseCommonPackage + "." + projectPrefix + "AccessContext";
                    value = DfStringUtil.replace(value, "$$AccessContext$$", accessContext);
                }
            }
            final String prefixMark = COMMON_COLUMN_SETUP_RESOURCE_PREFIX_MARK;
            final String secondMark = COMMON_COLUMN_SETUP_RESOURCE_SECOND_MARK;
            final String variablePrefix = COMMON_COLUMN_SETUP_RESOURCE_VARIABLE_PREFIX;
            if (value != null && value.startsWith(prefixMark)) {
                final boolean valid = setupCommonColumnSetupResource(value);
                if (valid) {
                    final String tmp = value.substring(prefixMark.length());
                    value = variablePrefix + tmp.substring(tmp.indexOf(secondMark) + secondMark.length());
                }
            }
            map.put(key, value);
        }
    }

    // -----------------------------------------------------
    //                                              resource
    //                                              --------
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
    // ex) Get date from dateProvider
    // R_TIMESTAMP = @org.seasar.dbflute.DateProvider@dateProvider.getDate()
    // - - - - - - - - - -/
    protected static final String COMMON_COLUMN_SETUP_RESOURCE_PREFIX_MARK = "@";
    protected static final String COMMON_COLUMN_SETUP_RESOURCE_SECOND_MARK = "@";
    protected static final String COMMON_COLUMN_SETUP_RESOURCE_VARIABLE_PREFIX = "_";

    protected Map<String, CommonColumnSetupResource> _commonColumnSetupResourceMap = new LinkedHashMap<String, CommonColumnSetupResource>();

    public boolean hasCommonColumnSetupResource() {
        final Map<String, CommonColumnSetupResource> map = getCommonColumnSetupResourceMap();
        return map != null && !map.isEmpty();
    }

    public List<CommonColumnSetupResource> getCommonColumnSetupResourceList() {
        return new ArrayList<CommonColumnSetupResource>(getCommonColumnSetupResourceMap().values());
    }

    protected Map<String, CommonColumnSetupResource> getCommonColumnSetupResourceMap() {
        return _commonColumnSetupResourceMap;
    }

    protected boolean setupCommonColumnSetupResource(String value) {
        final String prefixMark = COMMON_COLUMN_SETUP_RESOURCE_PREFIX_MARK;
        final String secondMark = COMMON_COLUMN_SETUP_RESOURCE_SECOND_MARK;
        if (!value.startsWith(prefixMark)) {
            return false;
        }
        String remainderString = value.substring(prefixMark.length());
        if (!remainderString.contains(secondMark)) {
            String msg = "The common column setup may be wrong format.";
            msg = msg + " Not found second mark[" + secondMark + "]" + ": value=" + value;
            msg = msg + " right answer=@org....DataProvider@dataProvider.getDate()";
            throw new IllegalStateException(msg);
        }
        final int secondMarkIndex = remainderString.indexOf(prefixMark);
        final String className = remainderString.substring(0, secondMarkIndex);
        remainderString = remainderString.substring(secondMarkIndex + 1);
        final int methodCallDotIndex = remainderString.indexOf(".");
        if (methodCallDotIndex < 0) {
            String msg = "The common column setup may be wrong format.";
            msg = msg + " Not found method call: value=" + value;
            msg = msg + " right answer=...DataProvider@dataProvider.getDate()";
            throw new IllegalStateException(msg);
        }
        final String propertyName = remainderString.substring(0, remainderString.indexOf("."));
        final CommonColumnSetupResource resource = createCommonColumnSetupResource(className, propertyName);
        _commonColumnSetupResourceMap.put(propertyName, resource);
        return true;
    }

    protected CommonColumnSetupResource createCommonColumnSetupResource(String className, String propertyName) {
        final CommonColumnSetupResource resource = newCommonColumnSetupResource();
        resource.setClassName(className);
        resource.setPropertyName(propertyName);
        return resource;
    }

    protected CommonColumnSetupResource newCommonColumnSetupResource() {
        return new CommonColumnSetupResource(COMMON_COLUMN_SETUP_RESOURCE_VARIABLE_PREFIX);
    }
}