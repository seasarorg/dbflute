package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    //                                                                    Environment Type
    //                                                                    ================
    public String getEnvironmentType() {
        final String propString = (String) getReplaceSchemaDefinitionMap().get("environmentType");
        if (propString == null) {
            return "ut";
        }
        return propString;
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
        String propString = (String) getReplaceSchemaDefinitionMap().get("loggingInsertSql");
        if (propString == null) {
            propString = (String) getReplaceSchemaDefinitionMap().get("isLoggingInsertSql");
            if (propString == null) {
                return true;
            }
        }
        return propString.equalsIgnoreCase("true");
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
    protected Map<String, Object> getAdditionalDropDefinitionMap() {
        final Map<String, Object> map = (Map<String, Object>) getReplaceSchemaDefinitionMap().get(
                "additionalDropDefinitionMap");
        if (map != null) {
            return map;
        } else {
            return new HashMap<String, Object>();
        }
    }

    public String getAdditionalDropDefinitionSchema() {
        final Map<String, Object> map = getAdditionalDropDefinitionMap();
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
    public List<String> getAdditionalDropDefinitionTargetDatabaseTypeList() {
        final Map<String, Object> map = getAdditionalDropDefinitionMap();
        final Object obj = map.get("targetDatabaseTypeList");
        if (obj == null) {
            return new ArrayList<String>();
        }
        if (!(obj instanceof List)) {
            String msg = "The schema should be List<String>: targetDatabaseTypeList=" + obj + " type=" + obj.getClass();
            throw new IllegalStateException(msg);
        }
        return (List<String>) obj;
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
}