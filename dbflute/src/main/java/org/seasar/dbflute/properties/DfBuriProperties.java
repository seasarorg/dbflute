package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.collection.DfStringKeyMap;
import org.seasar.dbflute.util.basic.DfStringUtil;

/**
 * @author jflute
 */
public final class DfBuriProperties extends DfAbstractHelperProperties {

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
    //     ; targetTableList = list:{ DOCUMENT }
    //     ; outputDirectory = ./../
    //     ; activityDefinitionMap = map:{
    //         ; [package] = map:{
    //             ; [process] = list:{ [activity1], [activity2] }
    //         }
    //     }
    // }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isUseBuri() {
        if (hasBuriDefinitionMap()) {
            return true;
        }
        boolean isUseBuri = booleanProp("torque.isUseBuri", false);
        if (isUseBuri) {
            return true;
        }
        return booleanProp("torque.isUseS2Buri", false);
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

    // ===================================================================================
    //                                                                   Table Process Map
    //                                                                   =================
    protected Map<String, List<String>> _tableProcessMap;

    protected Map<String, List<String>> getTargetProcessMap() {
        if (_tableProcessMap == null) {
            _tableProcessMap = DfStringKeyMap.createAsFlexible();
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
        if (!(processMappingValue instanceof List)) {
            String msg = "The type of process mapping value should be List: " + processMappingValue;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    /**
     * @return The directory for output. (NotNull)
     */
    public String getOutputDirectory() { // not required
        final String value = (String) getBuriDefinitionMap().get("outputDirectory");
        if (value == null) {
            return getBasicProperties().getOutputDirectory();
        }
        return getBasicProperties().getOutputDirectory() + "/" + value;
    }

    // ===================================================================================
    //                                                                 Activity Definition
    //                                                                 ===================
    protected Map<String, Map<String, List<String>>> _activityDefinitionMap;

    public Map<String, Map<String, List<String>>> getActivityDefinitionMap() {
        if (_activityDefinitionMap == null) {
            _activityDefinitionMap = new LinkedHashMap<String, Map<String, List<String>>>();
            final Map<String, Object> map = getBuriPropertyAsMap("activityDefinitionMap");
            final Set<String> packageNameSet = map.keySet();
            for (String packageName : packageNameSet) {
                final Object packageValue = map.get(packageName);
                assertPackageValueIsMap(packageValue);
                @SuppressWarnings("unchecked")
                final Map<String, Object> processMap = (Map<String, Object>) packageValue;
                final Map<String, List<String>> processResultMap = new LinkedHashMap<String, List<String>>();
                final Set<String> processNameSet = processMap.keySet();
                for (String processName : processNameSet) {
                    final Object processValue = processMap.get(processName);
                    assertProcessValueIsList(processValue);
                    @SuppressWarnings("unchecked")
                    final List<String> activityList = (List<String>) processValue;
                    processResultMap.put(processName, activityList);
                }
                _activityDefinitionMap.put(packageName, processResultMap);
            }
        }
        return _activityDefinitionMap;
    }

    protected void assertPackageValueIsMap(Object packageValue) {
        if (!(packageValue instanceof Map)) {
            String msg = "The type of package value should be Map: " + packageValue;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertProcessValueIsList(Object processValue) {
        if (!(processValue instanceof List)) {
            String msg = "The type of process value should be List: " + processValue;
            throw new IllegalStateException(msg);
        }
    }

    public Map<String, List<String>> getProcessMap(String packageName) {
        final Map<String, List<String>> map = getActivityDefinitionMap().get(packageName);
        return map != null ? map : new HashMap<String, List<String>>();
    }

    public List<String> getActivityList(String packageName, String processName) {
        final Map<String, List<String>> processMap = getProcessMap(packageName);
        return processMap != null ? processMap.get(processName) : new ArrayList<String>();
    }

    public List<String> getPackageProcessPathList() {
        final List<String> packageProcessPathList = new ArrayList<String>();
        final Map<String, Map<String, List<String>>> activityDefinitionMap = getActivityDefinitionMap();
        final Set<String> packageNameSet = activityDefinitionMap.keySet();
        for (String packageName : packageNameSet) {
            final Map<String, List<String>> processMap = activityDefinitionMap.get(packageName);
            final Set<String> processName = processMap.keySet();
            packageProcessPathList.add(packageName + "." + processName);
        }
        return packageProcessPathList;
    }

    // ===================================================================================
    //                                                              Buri Table Information
    //                                                              ======================
    protected Map<String, String> _tableJavaNameCaseInsensitiveMap = DfStringKeyMap.createAsCaseInsensitive();
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
    protected Map<String, String> _columnJavaNameCaseInsensitiveMap = DfStringKeyMap.createAsCaseInsensitive();
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