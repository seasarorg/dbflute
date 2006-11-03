package org.apache.torque.helper.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class GeneratedClassPackageProperties extends AbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public GeneratedClassPackageProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                  Properties - EntityDao Package
    //                                                  ==============================
    public String getPackageBase() {
        return stringProp("torque.packageBase", "");
    }

    public String getBaseCommonPackage() {
        return filterBase(stringProp("torque.baseCommonPackage", getPackageInfo().getBaseCommonPackage()));
    }

    public String getBaseBehaviorPackage() {
        return filterBase(stringProp("torque.baseBehaviorPackage", getPackageInfo().getBaseBehaviorPackage()));
    }

    public String getBaseDaoPackage() {
        return filterBase(stringProp("torque.baseDaoPackage", getPackageInfo().getBaseDaoPackage()));
    }

    public String getBaseEntityPackage() {
        return filterBase(stringProp("torque.baseEntityPackage", getPackageInfo().getBaseEntityPackage()));
    }

    public String getConditionBeanPackage() {
        return filterBase(stringProp("torque.conditionBeanPackage", getPackageInfo().getConditionBeanPackage()));
    }

    public String getExtendedBehaviorPackage() {
        return filterBase(stringProp("torque.extendedBehaviorPackage", getPackageInfo().getExtendedBehaviorPackage()));
    }

    public String getExtendedDaoPackage() {
        return filterBase(stringProp("torque.extendedDaoPackage", getPackageInfo().getExtendedDaoPackage()));
    }

    public String getExtendedEntityPackage() {
        return filterBase(stringProp("torque.extendedEntityPackage", getPackageInfo().getExtendedEntityPackage()));
    }

    protected String filterBase(String packageString) {
        if (getPackageBase().trim().length() > 0) {
            return getPackageBase() + "." + packageString;
        } else {
            return packageString;
        }
    }

    protected GeneratedClassPackageInfo _languageMetaData;

    protected GeneratedClassPackageInfo getPackageInfo() {
        if (getBasicProperties().isTargetLanguageJava()) {
            if (_languageMetaData == null) {
                _languageMetaData = new JavaPackageInfo();
            }
        } else if (getBasicProperties().isTargetLanguageCSharp()) {
            if (_languageMetaData == null) {
                _languageMetaData = new CSharpPackageInfo();
            }
        } else {
            String msg = "The language is unsupported: " + getBasicProperties().getTargetLanguage();
            throw new IllegalStateException(msg);
        }
        return _languageMetaData;
    }

    public static interface GeneratedClassPackageInfo {
        public String getBaseCommonPackage();

        public String getBaseBehaviorPackage();

        public String getBaseDaoPackage();

        public String getBaseEntityPackage();

        public String getConditionBeanPackage();

        public String getExtendedBehaviorPackage();

        public String getExtendedDaoPackage();

        public String getExtendedEntityPackage();
    }

    public static class JavaPackageInfo implements GeneratedClassPackageInfo {

        public String getBaseCommonPackage() {
            return "allcommon";
        }

        public String getBaseBehaviorPackage() {
            return "bsbhv";
        }

        public String getBaseDaoPackage() {
            return "bsdao";
        }

        public String getBaseEntityPackage() {
            return "bsentity";
        }

        public String getConditionBeanPackage() {
            return "cbean";
        }

        public String getExtendedBehaviorPackage() {
            return "exbhv";
        }

        public String getExtendedDaoPackage() {
            return "exdao";
        }

        public String getExtendedEntityPackage() {
            return "exentity";
        }
    }

    public static class CSharpPackageInfo implements GeneratedClassPackageInfo {
        public String getBaseCommonPackage() {
            return "Allcommon";
        }

        public String getBaseBehaviorPackage() {
            return "Bsbhv";
        }

        public String getBaseDaoPackage() {
            return "Bsdao";
        }

        public String getBaseEntityPackage() {
            return "Bsentity";
        }

        public String getConditionBeanPackage() {
            return "Cbean";
        }

        public String getExtendedBehaviorPackage() {
            return "Exbhv";
        }

        public String getExtendedDaoPackage() {
            return "Exdao";
        }

        public String getExtendedEntityPackage() {
            return "Exentity";
        }
    }
}