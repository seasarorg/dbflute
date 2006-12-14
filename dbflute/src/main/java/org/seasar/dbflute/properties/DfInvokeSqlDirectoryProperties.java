package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

public final class DfInvokeSqlDirectoryProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfInvokeSqlDirectoryProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                    Properties - invokeSqlDirectoryDefinitionMap
    //                                    ============================================
    public static final String KEY_invokeSqlDirectoryDefinitionMap = "invokeSqlDirectoryDefinitionMap";
    protected Map<String, Object> _invokeSqlDirectoryDefinitionMap;

    public Map<String, Object> getInvokeSqlDirectoryDefinitionMap() {
        if (_invokeSqlDirectoryDefinitionMap == null) {
            _invokeSqlDirectoryDefinitionMap = mapProp("torque." + KEY_invokeSqlDirectoryDefinitionMap,
                    DEFAULT_EMPTY_MAP);
        }
        return _invokeSqlDirectoryDefinitionMap;
    }

    public String getInvokeSqlDirectorySqlDirectory() {
        final String sqlDirectory = (String) getInvokeSqlDirectoryDefinitionMap().get("sqlDirectory");
        if (sqlDirectory != null) {
            return sqlDirectory;
        } else {
            final String javaDir = getBasicProperties().getJavaDir_for_main();
            final String extendedDaoPackage = getGeneratedClassPackageProperties().getExtendedDaoPackage();
            return javaDir + "/" + extendedDaoPackage.replace('.', '/');
        }
    }

    public boolean isInvokeSqlDirectoryAutoCommit() {
        final String isAutoCommitString = (String) getInvokeSqlDirectoryDefinitionMap().get("isAutoCommit");
        if (isAutoCommitString != null && isAutoCommitString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInvokeSqlDirectoryRollbackOnly() {
        final String isRollbackOnlyString = (String) getInvokeSqlDirectoryDefinitionMap().get("isRollbackOnly");
        if (isRollbackOnlyString != null && isRollbackOnlyString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInvokeSqlDirectoryErrorContinue() {
        final String isErrorContinueString = (String) getInvokeSqlDirectoryDefinitionMap().get("isErrorContinue");
        if (isErrorContinueString != null && isErrorContinueString.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }
}