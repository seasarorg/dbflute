package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.util.DfStringUtil;

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

    protected String getOldStyleEnvironmentType() { // Old Style!
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
    //                                                                             Logging
    //                                                                             =======
    public boolean isLoggingInsertSql() {
        return isProperty("isLoggingInsertSql", true, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                   SQL File Encoding
    //                                                                   =================
    public String getSqlFileEncoding() {
        final String sqlFileEncoding = (String) getReplaceSchemaDefinitionMap().get("sqlFileEncoding");
        if (sqlFileEncoding != null && sqlFileEncoding.trim().length() != 0) {
            return sqlFileEncoding;
        } else {
            return "UTF-8";
        }
    }

    // ===================================================================================
    //                                                                          Skip Sheet
    //                                                                          ==========
    public String getSkipSheet() {
        final String skipSheet = (String) getReplaceSchemaDefinitionMap().get("skipSheet");
        if (skipSheet != null && skipSheet.trim().length() != 0) {
            return skipSheet;
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                  Increment Sequence
    //                                                                  ==================
    public boolean isIncrementSequenceToDataMax() {
        return isProperty("isIncrementSequenceToDataMax", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                               Suppress Batch Update
    //                                                               =====================
    public boolean isSuppressBatchUpdate() {
        return isProperty("isSuppressBatchUpdate", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                          Drop Table
    //                                                                          ==========
    public boolean isDropGenerateTableOnly() {
        return isProperty("isDropGenerateTableOnly", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                     Additional Drop
    //                                                                     ===============
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAdditionalDropMapList() {
        Object obj = getReplaceSchemaDefinitionMap().get("additionalDropMapList");
        if (obj == null) {
            return new ArrayList<Map<String, Object>>();
        }
        if (!(obj instanceof List)) {
            String msg = "The type of the property 'additionalDropMapList' should be List: " + obj;
            throw new DfIllegalPropertyTypeException(msg);
        }
        return (List<Map<String, Object>>) obj;
    }

    public String getAdditionalDropSchema(Map<String, Object> additionalDropMap) {
        final Object obj = additionalDropMap.get("schema");
        if (obj == null) {
            String msg = "The schema in the property 'additionalDropMapList' should not be null: " + obj;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
        if (!(obj instanceof String)) {
            String msg = "The schema should be String: schema=" + obj + " type=" + obj.getClass();
            throw new DfIllegalPropertyTypeException(msg);
        }
        return (String) obj;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAdditionalDropObjectTypeList(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("objectTypeTargetList");
        if (obj == null) {
            obj = additionalDropMap.get("objectTypeList"); // old style
            if (obj == null) {
                ArrayList<String> defaultList = new ArrayList<String>();
                defaultList.add("TABLE");
                defaultList.add("VIEW");
                return defaultList;
            }
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: objectTypeTargetList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAdditionalDropTableTargetList(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("tableTargetList");
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
    public List<String> getAdditionalDropTableExceptList(Map<String, Object> additionalDropMap) {
        Object obj = additionalDropMap.get("tableExceptList");
        if (obj == null) {
            return new ArrayList<String>();
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: tableExceptList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
    }

    public boolean isAdditionalDropAllTable(Map<String, Object> additionalDropMap) {
        return isProperty("isDropAllTable", false, additionalDropMap);
    }

    // ===================================================================================
    //                                                        Suppress Initializing Schema
    //                                                        ============================
    public boolean isSuppressTruncateTable() {
        return isProperty("isSuppressTruncateTable", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropForeignKey() {
        return isProperty("isSuppressDropForeignKey", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isSuppressDropTable() {
        return isProperty("isSuppressDropTable", false, getReplaceSchemaDefinitionMap());
    }

    // ===================================================================================
    //                                                                        Other Closet
    //                                                                        ============
    public boolean isAutoCommit() { // It's closet! No useful!
        return isProperty("isAutoCommit", true, getReplaceSchemaDefinitionMap());
    }

    public boolean isRollbackOnly() { // It's closet! No useful!
        return isProperty("isRollbackOnly", false, getReplaceSchemaDefinitionMap());
    }

    public boolean isErrorContinue() { // It's closet!
        return isProperty("isErrorContinue", true, getReplaceSchemaDefinitionMap());
    }

    /**
     * @return The process command of call-back for before-take-finally. (Nullable)
     */
    public String getBeforeTakeFinally() { // It's closet!
        return (String) getReplaceSchemaDefinitionMap().get("beforeTakeFinally");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}