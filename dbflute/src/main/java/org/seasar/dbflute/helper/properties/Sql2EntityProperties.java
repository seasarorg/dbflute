package org.seasar.dbflute.helper.properties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class Sql2EntityProperties extends AbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public Sql2EntityProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                            Properties - sql2EntityDefinitionMap
    //                                            ====================================
    public static final String KEY_sql2EntityDefinitionMap = "sql2EntityDefinitionMap";
    protected Map<String, Object> _sql2EntityDefinitionMap;

    public Map<String, Object> getSql2EntityDefinitionMap() {
        if (_sql2EntityDefinitionMap == null) {
            final Map<String, Object> sql2EntityDefaultMap = new LinkedHashMap<String, Object>();

            final GeneratedClassPackageProperties packageProp = new GeneratedClassPackageProperties(getProperties());
            getBasicProperties().getJavaLocation_for_main();
            packageProp.getExtendedDaoPackage();

            final String defaultSqlDirectory = getDefaultSqlDirectory();
            sql2EntityDefaultMap.put("sqlDirectory", defaultSqlDirectory);
            sql2EntityDefaultMap.put("isPlainEntity", "" + isDefaultPlainEntity());
            _sql2EntityDefinitionMap = mapProp("torque." + KEY_sql2EntityDefinitionMap, sql2EntityDefaultMap);
        }
        return _sql2EntityDefinitionMap;
    }

    protected String getDefaultSqlDirectory() {
        final String javaDir = getBasicProperties().getJavaDir_for_main();
        final String exdaoPackage = getGeneratedClassPackageProperties().getExtendedDaoPackage();
        final String defaultSqlDirectory = javaDir + "/" + exdaoPackage.replace('.', '/');
        return defaultSqlDirectory;
    }

    protected boolean isDefaultPlainEntity() {
        return false;
    }

    public String getSqlDirectory() {
        final String value = (String) getSql2EntityDefinitionMap().get("sqlDirectory");
        if (value == null || value.trim().length() == 0) {
            return getDefaultSqlDirectory();
        } else {
            return value;
        }
    }

    public boolean isPlainEntity() {
        final String value = (String) getSql2EntityDefinitionMap().get("isPlainEntity");
        if (value == null || value.trim().length() == 0) {
            return isDefaultPlainEntity();
        } else {
            return value.equalsIgnoreCase("true");
        }
    }

    public String getOutputDirectory() {
        final String value = (String) getSql2EntityDefinitionMap().get("outputDirectory");
        if (value == null || value.trim().length() == 0) {
            return getBasicProperties().getJavaDir_for_main();
        } else {
            return value;
        }
    }

    protected String getPackageString() {
        final String value = (String) getSql2EntityDefinitionMap().get("packageString");
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    public String getBaseEntityPackage() {
        final String packageString = getPackageString();
        if (packageString != null && packageString.trim().length() != 0) {
            return packageString;
        } else {
            final String defaultPackage = getGeneratedClassPackageProperties().getBaseEntityPackage() + ".customize";
            if (defaultPackage != null && defaultPackage.trim().length() != 0) {
                return defaultPackage;
            } else {
                String msg = "Both packageString in sql2entity-property and baseEntityPackage are null.";
                throw new IllegalStateException(msg);
            }
        }
    }

    public String getDBMetaPackage() {
        return getBaseEntityPackage() + ".dbmeta";
    }

    public String getExtendedEntityPackage() {
        final String packageString = getPackageString();
        if (packageString != null && packageString.trim().length() != 0) {
            return packageString;
        } else {
            final String defaultPackage = getGeneratedClassPackageProperties().getExtendedEntityPackage()
                    + ".customize";
            if (defaultPackage != null && defaultPackage.trim().length() != 0) {
                return defaultPackage;
            } else {
                String msg = "Both packageString in sql2entity-property and extendedEntityPackage are null.";
                throw new IllegalStateException(msg);
            }
        }
    }
}