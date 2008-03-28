package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DfReplaceSchemaProperties extends DfAbstractHelperProperties {

    private static final Log _log = LogFactory.getLog(DfReplaceSchemaProperties.class);

    public DfReplaceSchemaProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                   Properties - invokeReplaceSchemaDefinitionMap
    //                                   =============================================
    public static final String KEY_oldKey = "invokeReplaceSchemaDefinitionMap";
    public static final String KEY_replaceSchemaDefinitionMap = "replaceSchemaDefinitionMap";
    protected Map<String, Object> _replaceSchemaDefinitionMap;

    public Map<String, Object> getReplaceSchemaDefinitionMap() {
        if (_replaceSchemaDefinitionMap == null) {
            final Map<String, Object> defaultMap = mapProp("torque." + KEY_oldKey, DEFAULT_EMPTY_MAP);
            _replaceSchemaDefinitionMap = mapProp("torque." + KEY_replaceSchemaDefinitionMap, defaultMap);
            _log.info("...Initializing " + KEY_replaceSchemaDefinitionMap + ": " + _replaceSchemaDefinitionMap);
        }
        return _replaceSchemaDefinitionMap;
    }

    public String getReplaceSchemaSqlFile() {
        final String sqlFile = (String) getReplaceSchemaDefinitionMap().get("sqlFile");
        if (sqlFile != null && sqlFile.trim().length() != 0) {
            return sqlFile;
        } else {
            return "./playsql/replace-schema.sql";
        }
    }

    public String getTakeFinallySqlFile() {
        return "./playsql/take-finally.sql";
    }

    public boolean isEnvironmentTypeTest() {
        return getEnvironmentType().equalsIgnoreCase("test");
    }

    public boolean isEnvironmentTypeUT() {
        return getEnvironmentType().equalsIgnoreCase("ut");
    }

    public boolean isEnvironmentTypeIT() {
        return getEnvironmentType().equalsIgnoreCase("it");
    }

    public boolean isEnvironmentTypePT() {
        return getEnvironmentType().equalsIgnoreCase("pt");
    }

    public boolean isEnvironmentTypeReal() {
        return getEnvironmentType().equalsIgnoreCase("real");
    }

    public String getEnvironmentType() {
        final String propString = (String) getReplaceSchemaDefinitionMap().get("environmentType");
        if (propString == null) {
            return "ut";
        }
        return propString;

        // For All OK!
        //        if (propString.trim().equalsIgnoreCase("ut")) {
        //            return "ut";
        //        } else if (propString.trim().equalsIgnoreCase("it")) {
        //            return "it";
        //        } else if (propString.trim().equalsIgnoreCase("pt")) {
        //            return "pt";
        //        } else if (propString.trim().equalsIgnoreCase("real")) {
        //            return "real";
        //        } else {
        //            String msg = "The environmentType[" + propString + "] is unsupported. Options are {test/it/pt/real}";
        //            throw new IllegalStateException(msg);
        //        }
    }

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

    public boolean isStringTimestamp() {
        return analyzeBooleanProperty("isStringTimestamp", true);
    }

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