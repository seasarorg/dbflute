package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.util.basic.DfStringUtil;

/**
 * @author jflute
 */
public final class DfReplaceSchemaProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaProperties.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfReplaceSchemaProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                          replaceSchemaDefinitionMap
    //                                                          ==========================
    public static final String KEY_replaceSchemaDefinitionMap = "replaceSchemaDefinitionMap";
    protected Map<String, Object> _replaceSchemaDefinitionMap;

    public Map<String, Object> getReplaceSchemaDefinitionMap() {
        if (_replaceSchemaDefinitionMap == null) {
            _replaceSchemaDefinitionMap = mapProp("torque." + KEY_replaceSchemaDefinitionMap, DEFAULT_EMPTY_MAP);
            _log.info("...Initializing " + KEY_replaceSchemaDefinitionMap + ": " + _replaceSchemaDefinitionMap);
        }
        return _replaceSchemaDefinitionMap;
    }

    // ===================================================================================
    //                                                                            SQL File
    //                                                                            ========
    public String getReplaceSchemaSqlFile() {
        return "./playsql/replace-schema.sql";
    }

    public String getTakeFinallySqlFile() {
        return "./playsql/take-finally.sql";
    }

    // ===================================================================================
    //                                                                   Data Loading Type
    //                                                                   =================
    public String getDataLoadingType() {
        final String propString = (String) getReplaceSchemaDefinitionMap().get("dataLoadingType");
        if (propString == null) {
            return getOldStyleEnvironmentType();
        }
        return propString;
    }

    protected String getOldStyleEnvironmentType() {// Old Style!
        final String propString = (String) getReplaceSchemaDefinitionMap().get("environmentType");
        if (propString == null) {
            return "ut";
        }
        return propString;
    }

    // ===================================================================================
    //                                                                Filter Variables Map
    //                                                                ====================
    protected Map<String, String> _filterVariablesMap;

    @SuppressWarnings("unchecked")
    protected Map<String, String> getFilterVariablesMap() {
        if (_filterVariablesMap != null) {
            return _filterVariablesMap;
        }
        _filterVariablesMap = (Map<String, String>) getReplaceSchemaDefinitionMap().get("filterVariablesMap");
        if (_filterVariablesMap == null) {
            _filterVariablesMap = new HashMap<String, String>();
        }
        return _filterVariablesMap;
    }

    protected String getFilterVariablesBeginMark() {
        return "/*$";
    }

    protected String getFilterVariablesEndMark() {
        return "*/";
    }

    public String resolveFilterVariablesIfNeeds(String sql) {
        final String beginMark = getFilterVariablesBeginMark();
        final String endMark = getFilterVariablesEndMark();
        final Map<String, String> filterVariablesMap = getFilterVariablesMap();
        if (!filterVariablesMap.isEmpty() && sql.contains(beginMark) && sql.contains(endMark)) {
            final Set<String> keySet = filterVariablesMap.keySet();
            for (String key : keySet) {
                final String variableMark = beginMark + key + endMark;
                if (sql.contains(variableMark)) {
                    final String value = filterVariablesMap.get(key);
                    sql = replaceString(sql, variableMark, value);
                }
            }
        }
        return sql;
    }

    // ===================================================================================
    //                                                                      Callback Class
    //                                                                      ==============
    /**
     * @return The process command of call-back for before-take-finally. (Nullable)
     */
    public String getBeforeTakeFinally() {
        return (String) getReplaceSchemaDefinitionMap().get("beforeTakeFinally");
    }

    // ===================================================================================
    //                                                                    Other Properties
    //                                                                    ================
    public boolean isLoggingInsertSql() {
        String value = (String) getReplaceSchemaDefinitionMap().get("loggingInsertSql");
        if (value == null) {
            value = (String) getReplaceSchemaDefinitionMap().get("isLoggingInsertSql");
            if (value == null) {
                return true;
            }
        }
        return value.equalsIgnoreCase("true");
    }

    public boolean isAutoCommit() {
        return analyzeBooleanProperty("isAutoCommit", true);
    }

    public boolean isRollbackOnly() {
        return analyzeBooleanProperty("isRollbackOnly", false);
    }

    public boolean isErrorContinue() {
        return analyzeBooleanProperty("isErrorContinue", true);
    }

    public String getSqlFileEncoding() {
        final String sqlFileEncoding = (String) getReplaceSchemaDefinitionMap().get("sqlFileEncoding");
        if (sqlFileEncoding != null && sqlFileEncoding.trim().length() != 0) {
            return sqlFileEncoding;
        } else {
            return "UTF-8";
        }
    }

    public String getSkipSheet() {
        final String skipSheet = (String) getReplaceSchemaDefinitionMap().get("skipSheet");
        if (skipSheet != null && skipSheet.trim().length() != 0) {
            return skipSheet;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getOnceMoreDropDefinitionMap() {
        final Map<String, Object> map = (Map<String, Object>) getReplaceSchemaDefinitionMap().get(
                "onceMoreDropDefinitionMap");
        if (map != null) {
            return map;
        } else {
            return new HashMap<String, Object>();
        }
    }

    public String getOnceMoreDropDefinitionSchema() {
        final Map<String, Object> map = getOnceMoreDropDefinitionMap();
        final Object obj = map.get("schema");
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof String)) {
            String msg = "The schema should be String: schema=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (String) obj;
    }

    @SuppressWarnings("unchecked")
    public List<String> getOnceMoreDropDefinitionObjectTypeList() {
        final Map<String, Object> map = getOnceMoreDropDefinitionMap();
        Object obj = map.get("objectTypeList");
        if (obj == null) {
            obj = map.get("targetDatabaseTypeList");
            if (obj == null) {
                return new ArrayList<String>();
            }
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: objectTypeList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
    }

    @SuppressWarnings("unchecked")
    public List<String> getOnceMoreDropTableTargetList() {
        final Map<String, Object> map = getOnceMoreDropDefinitionMap();
        Object obj = map.get("tableTargetList");
        if (obj == null) {
            return new ArrayList<String>();
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: tableTargetList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
    }

    @SuppressWarnings("unchecked")
    public List<String> getOnceMoreDropTableExceptList() {
        final Map<String, Object> map = getOnceMoreDropDefinitionMap();
        Object obj = map.get("tableExceptList");
        if (obj == null) {
            return new ArrayList<String>();
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: tableExceptList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
    }

    @SuppressWarnings("unchecked")
    public boolean isOnceMoreDropAllTable() {
        String value = (String) getOnceMoreDropDefinitionMap().get("dropAllTable");
        if (value == null) {
            value = (String) getOnceMoreDropDefinitionMap().get("isDropAllTable");
            if (value == null) {
                return false;
            }
        }
        return value.equalsIgnoreCase("true");
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean analyzeBooleanProperty(String propertyName, boolean defaultDetermination) {
        String propString = (String) getReplaceSchemaDefinitionMap().get(propertyName);
        if (propString == null) {
            if (propertyName.startsWith("is")) {
                final String secondString = propertyName.substring("is".length());
                final String secondProperty = secondString.substring(0, 1).toLowerCase() + secondString.substring(1);
                final String secondPropString = (String) getReplaceSchemaDefinitionMap().get(secondProperty);
                if (secondPropString != null) {
                    propString = secondPropString;
                }
            }
            if (propString == null) {
                return defaultDetermination;
            }
        }
        if (propString != null && propString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}