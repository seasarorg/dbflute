package org.seasar.dbflute.properties;

import java.util.Properties;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfGeneratedClassPackageProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfGeneratedClassPackageProperties(Properties prop) {
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

    protected DfGeneratedClassPackageDefault getPackageInfo() {
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return languageDependencyInfo.getGeneratedClassPackageInfo();
    }
}