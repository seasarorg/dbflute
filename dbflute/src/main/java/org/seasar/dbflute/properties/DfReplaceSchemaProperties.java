package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

public final class DfReplaceSchemaProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
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

    public boolean isReplaceSchemaAutoCommit() {
        final String isAutoCommitString = (String) getReplaceSchemaDefinitionMap().get("isAutoCommit");
        if (isAutoCommitString != null && isAutoCommitString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isReplaceSchemaRollbackOnly() {
        final String isRollbackOnlyString = (String) getReplaceSchemaDefinitionMap().get("isRollbackOnly");
        if (isRollbackOnlyString != null && isRollbackOnlyString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isReplaceSchemaErrorContinue() {
        final String isErrorContinueString = (String) getReplaceSchemaDefinitionMap().get("isErrorContinue");
        if (isErrorContinueString != null && isErrorContinueString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }
}