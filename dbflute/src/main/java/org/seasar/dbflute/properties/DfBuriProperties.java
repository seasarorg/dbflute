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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.properties.assistant.DfTableFinder;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public final class DfBuriProperties extends DfAbstractHelperProperties {

    // ===============================================================================
    //                                                                      Definition
    //                                                                      ==========
    /** Log-instance */
    private static final Log _log = LogFactory.getLog(DfBuriProperties.class);
    private static final String VIEW_ALL_ROUND_STATE = "BURI_ALL_ROUND_STATE";
    private static final String VIEW_ALL_ROUND_STATE_HISTORY = "BURI_ALL_ROUND_STATE_HISTORY";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBuriProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> buriDefinitionMap;

    protected Map<String, Object> getBuriDefinitionMap() {
        if (buriDefinitionMap == null) {
            buriDefinitionMap = mapProp("torque.buriDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return buriDefinitionMap;
    }

    // [Buri Definition]
    // map:{
    //     ; activityDefinitionMap = map:{
    //         ; [package] = map:{
    //             ; [process] = map:{
    //                 status = list:{ st1; st2; st3 }
    //                 action = list:{ ac1; ac2; ac3 }
    //             }
    //         }
    //     }
    //     ; tableProcessMap = list:{ [package].[process] }
    // }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isUseBuri() {
        return hasBuriDefinitionMap();
    }

    protected boolean hasBuriDefinitionMap() {
        return !getBuriDefinitionMap().isEmpty();
    }

    public boolean isTargetTable(String tableName) {
        if (!isUseBuri()) {
            return false;
        }
        return getTargetProcessMap().containsKey(tableName);
    }

    public boolean isBuriAllRoundStateHistory(String tableName) {
        return VIEW_ALL_ROUND_STATE_HISTORY.equalsIgnoreCase(tableName);
    }

    // ===================================================================================
    //                                                                 Activity Definition
    //                                                                 ===================
    protected Map<String, Map<String, Map<String, List<String>>>> _activityDefinitionMap;

    public Map<String, Map<String, Map<String, List<String>>>> getActivityDefinitionMap() {
        if (_activityDefinitionMap == null) {
            _activityDefinitionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
            final Map<String, Object> activityDefinitionMap = getBuriPropertyAsMap("activityDefinitionMap");
            if (activityDefinitionMap != null) {
                final Set<String> packageNameSet = activityDefinitionMap.keySet();
                for (String packageName : packageNameSet) {
                    final Object packageValue = activityDefinitionMap.get(packageName);
                    assertPackageValueIsMap(packageValue);
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> processMap = (Map<String, Object>) packageValue;
                    final Map<String, Map<String, List<String>>> processResultMap = newLinkedHashMap();
                    final Set<String> processNameSet = processMap.keySet();
                    for (String processName : processNameSet) {
                        final Object processValue = processMap.get(processName);
                        assertProcessValueIsMap(processValue);
                        @SuppressWarnings("unchecked")
                        final Map<String, List<String>> activityMap = (Map<String, List<String>>) processValue;
                        processResultMap.put(processName, activityMap);
                    }
                    _activityDefinitionMap.put(packageName, processResultMap);
                }
            }
        }
        return _activityDefinitionMap;
    }

    protected void assertPackageValueIsMap(Object packageValue) {
        if (!(packageValue instanceof Map<?, ?>)) {
            String msg = "The type of package value should be Map: " + packageValue;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertProcessValueIsMap(Object processValue) {
        if (!(processValue instanceof Map<?, ?>)) {
            String msg = "The type of process value should be Map: " + processValue;
            throw new IllegalStateException(msg);
        }
    }

    public Map<String, Map<String, List<String>>> getProcessMap(String packageName) {
        final Map<String, Map<String, List<String>>> map = getActivityDefinitionMap().get(packageName);
        return map != null ? map : new HashMap<String, Map<String, List<String>>>();
    }

    public List<String> getPackageProcessPathList() {
        final List<String> packageProcessPathList = new ArrayList<String>();
        final Map<String, Map<String, Map<String, List<String>>>> activityDefinitionMap = getActivityDefinitionMap();
        final Set<String> packageNameSet = activityDefinitionMap.keySet();
        for (String packageName : packageNameSet) {
            final Map<String, Map<String, List<String>>> processMap = activityDefinitionMap.get(packageName);
            final Set<String> processName = processMap.keySet();
            packageProcessPathList.add(packageName + "." + processName);
        }
        return packageProcessPathList;
    }

    public List<String> getStatusList(String packageName, String processName) {
        final Map<String, Map<String, List<String>>> processMap = getProcessMap(packageName);
        if (processMap == null) {
            return new ArrayList<String>();
        }
        Map<String, List<String>> map = processMap.get(processName);
        List<String> statusList = map.get("status");
        return statusList != null ? statusList : new ArrayList<String>();
    }

    public List<String> getActionList(String packageName, String processName) {
        final Map<String, Map<String, List<String>>> processMap = getProcessMap(packageName);
        if (processMap == null) {
            return new ArrayList<String>();
        }
        Map<String, List<String>> map = processMap.get(processName);
        List<String> actionList = map.get("action");
        return actionList != null ? actionList : new ArrayList<String>();
    }

    // ===================================================================================
    //                                                                   Table Process Map
    //                                                                   =================
    protected Map<String, List<String>> _tableProcessMap;

    protected Map<String, List<String>> getTargetProcessMap() {
        if (_tableProcessMap == null) {
            _tableProcessMap = StringKeyMap.createAsFlexible();
            final Map<String, Object> buriPropertyAsMap = getBuriPropertyAsMap("tableProcessMap");
            if (buriPropertyAsMap != null) {
                final Set<String> tableNameSet = buriPropertyAsMap.keySet();
                for (String tableName : tableNameSet) {
                    final Object processMappingValue = buriPropertyAsMap.get(tableName);
                    if (processMappingValue == null) {
                        continue;
                    }
                    assertProcessMappingValueIsList(processMappingValue);
                    @SuppressWarnings("unchecked")
                    List<String> processList = (List<String>) processMappingValue;
                    _tableProcessMap.put(tableName, processList);
                }
            }
        }
        return _tableProcessMap;
    }

    public List<String> getTableProcessForMethodNameList(String tableName) {
        final List<String> processList = getTargetProcessMap().get(tableName);
        if (processList == null) {
            return new ArrayList<String>();
        }
        final ArrayList<String> resultList = new ArrayList<String>();
        for (String process : processList) {
            resultList.add(DfStringUtil.replace(process, ".", "_"));
        }
        return resultList;
    }

    protected void assertProcessMappingValueIsList(Object processMappingValue) {
        if (!(processMappingValue instanceof List<?>)) {
            String msg = "The type of process mapping value should be List: " + processMappingValue;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                              Buri Table Information
    //                                                              ======================
    protected Map<String, String> _tableJavaNameCaseInsensitiveMap = StringKeyMap.createAsCaseInsensitive();
    {
        putTableJavaNameMap("BuriPath"); // 1
        putTableJavaNameMap("BuriData"); // 2
        putTableJavaNameMap("BuriBranch"); // 3
        putTableJavaNameMap("BuriState"); // 4
        putTableJavaNameMap("BuriStateUser"); // 5
        putTableJavaNameMap("BuriUser"); // 6
        putTableJavaNameMap("BuriTransaction"); // 7
        putTableJavaNameMap("BuriStateUndoLog"); // 8
        putTableJavaNameMap("BuriPathData"); // 9
        putTableJavaNameMap("BuriPathDataUser"); // 10
        putTableJavaNameMap("BuriPathHistoryData"); // 11
        putTableJavaNameMap("BuriPathHistoryDataUser"); // 12
        putTableJavaNameMap("BuriDataPathHistory"); // 13
    }

    protected void putTableJavaNameMap(String tableName) {
        _tableJavaNameCaseInsensitiveMap.put(tableName, tableName);
    }

    public boolean isBuriInternalTable(String javaName) {
        if (javaName == null || javaName.trim().length() == 0) {
            return false;
        }
        return arrangeBuriTableJavaName(javaName) != null;
    }

    public String arrangeBuriTableJavaName(String javaName) {
        if (javaName == null || javaName.trim().length() == 0) {
            return null;
        }
        final String key = javaName.trim().toLowerCase();
        return _tableJavaNameCaseInsensitiveMap.get(key);
    }

    // ===================================================================================
    //                                                             Buri Column Information
    //                                                             =======================
    protected Map<String, String> _columnJavaNameCaseInsensitiveMap = StringKeyMap.createAsCaseInsensitive();
    {
        putColumnJavaNameMap("StateId");
        putColumnJavaNameMap("PathId");
        putColumnJavaNameMap("DataId");
        putColumnJavaNameMap("BranchId");
        putColumnJavaNameMap("StateUserId");
        putColumnJavaNameMap("BuriUserId");
        putColumnJavaNameMap("StateUndoLogId");
        putColumnJavaNameMap("ParentBranchId");
        putColumnJavaNameMap("InsertUserId");
        putColumnJavaNameMap("HistoryId");
        putColumnJavaNameMap("UserIdVal");
        putColumnJavaNameMap("UserIdNum");
        putColumnJavaNameMap("PkeyVal");
        putColumnJavaNameMap("PkeyNum");
        putColumnJavaNameMap("DataType");
        putColumnJavaNameMap("TableName");
        putColumnJavaNameMap("ParticipantName");
        putColumnJavaNameMap("ParticipantType");
        putColumnJavaNameMap("Btid");
        putColumnJavaNameMap("PathName");
        putColumnJavaNameMap("RealPathName");
        putColumnJavaNameMap("PathType");
        putColumnJavaNameMap("Action");
        putColumnJavaNameMap("InsertDate");
        putColumnJavaNameMap("AutoRunTime");
        putColumnJavaNameMap("ProcessDate");
        putColumnJavaNameMap("AbortDate");
        putColumnJavaNameMap("VersionNo");
        putColumnJavaNameMap("DeleteDate");
        putColumnJavaNameMap("CreateBtid");
    }

    protected void putColumnJavaNameMap(String columnName) {
        _columnJavaNameCaseInsensitiveMap.put(columnName, columnName);
    }

    public String arrangeBuriColumnJavaName(String javaName) {
        if (javaName == null || javaName.trim().length() == 0) {
            return null;
        }
        final String key = javaName.trim().toLowerCase();
        return _columnJavaNameCaseInsensitiveMap.get(key);
    }

    // ===================================================================================
    //                                                                AdditionalForeignKey
    //                                                                ====================
    public void setupImplicitAdditionalForeignKey(DfTableFinder finder) {
        if (!isUseBuri()) {
            return;
        }
        _log.info("/===========================================");
        _log.info("...Setting up implicit foreign key for Buri.");
        final DfAdditionalForeignKeyProperties fkprop = getAdditionalForeignKeyProperties();
        final Map<String, Map<String, String>> fkMap = fkprop.getAdditionalForeignKeyMap();
        final String entityPackage = getBasicProperties().getExtendedEntityPackage();
        final Map<String, List<String>> targetProcessMap = getTargetProcessMap();
        final Set<String> tableNameSet = targetProcessMap.keySet();
        for (String current : tableNameSet) {
            final List<String> relatedProcessList = targetProcessMap.get(current);
            final String tableName = current.toUpperCase();
            int id = 1;
            for (String process : relatedProcessList) {
                final boolean statMade = createStateViewFk(fkMap, finder, tableName, entityPackage, process, id);
                final boolean histMade = createStateHistoryViewFK(fkMap, finder, tableName, entityPackage, process, id);
                if (statMade || histMade) {
                    ++id;
                }
            }
        }
        _log.info("==========/");
    }

    protected boolean createStateViewFk(Map<String, Map<String, String>> fkMap, DfTableFinder finder, String tableName,
            String entityPackage, String relatedProcess, int identity) {
        final String viewName = VIEW_ALL_ROUND_STATE;
        final String foreignName = "FK_" + tableName + "_" + viewName + "_" + identity;
        if (fkMap.containsKey(foreignName)) {
            return false;
        }
        final LinkedHashMap<String, String> elementMap = newLinkedHashMap();
        final Table table = finder.findTable(tableName);
        if (table == null) {
            String msg = "The table was not found: " + tableName;
            throw new IllegalStateException(msg);
        }
        if (table.hasCompoundPrimaryKey()) {
            String msg = "The table should have the only one primary key: " + tableName;
            throw new IllegalStateException(msg);
        }
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_LOCAL_TABLE_NAME, tableName);
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FOREIGN_TABLE_NAME, viewName);
        final String primaryKeyName = table.getPrimaryKeyAsOne().getName();
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_LOCAL_COLUMN_NAME, primaryKeyName);
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FOREIGN_COLUMN_NAME, "INTERNAL_PK_VALUE");

        // Fixed Condition
        final String entityName = table.getExtendedEntityClassName();
        final String fqcn = entityPackage + "." + entityName;
        final StringBuilder sb = new StringBuilder();
        sb.append("$$foreignAlias$$.STATUS_PATH_NAME like '").append(relatedProcess).append(".%'");
        sb.append(" and $$foreignAlias$$.INTERNAL_DATA_TYPE = '").append(fqcn).append("'");
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FIXED_CONDITION, sb.toString());

        // Fixed Suffix
        final String processExpression = DfStringUtil.replace(relatedProcess, ".", "_");
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FIXED_SUFFIX, "_" + processExpression);

        fkMap.put(foreignName, elementMap);
        _log.info(foreignName);
        return true;
    }

    protected boolean createStateHistoryViewFK(Map<String, Map<String, String>> fkMap, DfTableFinder finder,
            String tableName, String entityPackage, String relatedProcess, int identity) {
        if (!hasBuriAllRoundStateHistory(finder)) {
            // Not error because the history view is not required.
            return false;
        }
        final String viewName = VIEW_ALL_ROUND_STATE_HISTORY;
        final String foreignName = "FK_" + viewName + "_" + tableName + "_" + identity;
        if (fkMap.containsKey(foreignName)) {
            return false;
        }
        final LinkedHashMap<String, String> elementMap = newLinkedHashMap();
        final Table table = finder.findTable(tableName);
        if (table == null) {
            String msg = "The table was not found: " + tableName;
            throw new IllegalStateException(msg);
        }
        if (table.hasCompoundPrimaryKey()) {
            String msg = "The table should have the only one primary key: " + tableName;
            throw new IllegalStateException(msg);
        }
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_LOCAL_TABLE_NAME, viewName);
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FOREIGN_TABLE_NAME, tableName);
        final String primaryKeyName = table.getPrimaryKeyAsOne().getName();
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_LOCAL_COLUMN_NAME, "INTERNAL_PK_VALUE");
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FOREIGN_COLUMN_NAME, primaryKeyName);

        // Fixed Condition
        final String entityName = table.getExtendedEntityClassName();
        final String fqcn = entityPackage + "." + entityName;
        final StringBuilder sb = new StringBuilder();
        sb.append("$$localAlias$$.INTERNAL_DATA_TYPE = '").append(fqcn).append("'");
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FIXED_CONDITION, sb.toString());

        // Fixed Suffix
        final String processExpression = DfStringUtil.replace(relatedProcess, ".", "_");
        elementMap.put(DfAdditionalForeignKeyProperties.KEY_FIXED_SUFFIX, "_" + processExpression);

        fkMap.put(foreignName, elementMap);
        _log.info(foreignName);
        return true;
    }

    public boolean hasBuriAllRoundStateHistory(DfTableFinder finder) {
        final String viewName = VIEW_ALL_ROUND_STATE_HISTORY;
        return finder.findTable(viewName) != null;
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getBuriPropertyRequired(String key) {
        final String value = getBuriProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " buriDefinitionMap=" + getBuriDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getBuriPropertyIfNullEmpty(String key) {
        final String value = getBuriProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getBuriProperty(String key) {
        final String value = (String) getBuriDefinitionMap().get(key);
        return value;
    }

    protected boolean isBuriProperty(String key) {
        final String value = (String) getBuriDefinitionMap().get(key);
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getBuriPropertyAsMap(String key) {
        return (Map<String, Object>) getBuriDefinitionMap().get(key);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getBuriPropertyAsList(String key) {
        return (List<String>) getBuriDefinitionMap().get(key);
    }
}