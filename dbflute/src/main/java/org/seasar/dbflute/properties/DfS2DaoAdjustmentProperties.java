package org.seasar.dbflute.properties;

import java.util.Properties;

import org.seasar.framework.util.StringUtil;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 * @since 0.5.4 (2007/07/18 Wednesday)
 */
public final class DfS2DaoAdjustmentProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfS2DaoAdjustmentProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                                   S2Dao Version
    //                                                                   =============
    public boolean isVersionAfter1046() {
        if (!hasS2DaoVersion()) {
            return true;
        }
        return isS2DaoVersionGreaterEqual("1.0.46");
    }

    public boolean isVersionAfter1043() {
        if (!hasS2DaoVersion()) {
            return true;
        }
        return isS2DaoVersionGreaterEqual("1.0.43");
    }

    public boolean isVersionAfter1040() {
        if (!hasS2DaoVersion()) {
            return booleanProp("torque.isVersionAfter1040", true);
        }
        return isS2DaoVersionGreaterEqual("1.0.40");
    }

    protected boolean hasS2DaoVersion() {
        final String value = stringProp("torque.s2daoVersion", null);
        if (value != null && value.trim().length() != 9) {
            return true;
        }
        return false;
    }

    protected String getS2DaoVersion() {
        final String s2daoVersion = stringProp("torque.s2daoVersion", null);
        return s2daoVersion != null ? StringUtil.replace(s2daoVersion, ".", "") : "9.9.99";// If null, return the latest version!
    }

    protected boolean isS2DaoVersionGreaterEqual(String targetVersion) {
        final String s2daoVersion = getS2DaoVersion();
        final String filteredTargetVersion = StringUtil.replace(targetVersion, ".", "");
        return s2daoVersion.compareToIgnoreCase(filteredTargetVersion) >= 0;
    }

    // ===============================================================================
    //                                                                  S2Dao Override
    //                                                                  ==============
    public boolean isAvailableOtherConnectionDaoInitialization() {
        return booleanProp("torque.isAvailableOtherConnectionDaoInitialization", false);
    }

    public boolean isAvailableDaoMethodLazyInitializing() {
        return booleanProp("torque.isAvailableDaoMethodLazyInitializing", false);
    }

    // ===============================================================================
    //                                                                    S2Dao Follow
    //                                                                    ============
    public boolean isAvailableChildNoAnnotationGenerating() {
        return booleanProp("torque.isAvailableChildNoAnnotationGenerating", false);
    }

    // ===============================================================================
    //                                                                 S2Dao Component
    //                                                                 ===============
    public String getExtendedDaoMetaDataFactoryImplClassName() {
        final DfBasicProperties basicProperties = getBasicProperties();
        final DfGeneratedClassPackageProperties generatedClassPackageProperties = getGeneratedClassPackageProperties();
        final String baseCommonPackage = generatedClassPackageProperties.getBaseCommonPackage();
        final String projectPrefix = basicProperties.getProjectPrefix();
        final String defaultClassName = baseCommonPackage + ".s2dao." + projectPrefix + "S2DaoMetaDataFactoryImpl";
        return stringPropNoEmpty("torque.extendedDaoMetaDataFactoryImplClassName", defaultClassName);
    }

    public String getExtendedAnnotationReaderFactoryClassName() {
        final String defaultClassName = "org.seasar.dao.impl.AnnotationReaderFactoryImpl";
        return stringPropNoEmpty("torque.extendedAnnotationReaderFactoryClassName", defaultClassName);
    }
    
    // ===============================================================================
    //                                                                  S2Dao Property
    //                                                                  ==============
    public boolean hasDaoSqlFileEncoding() {
        final String daoSqlFileEncoding = getDaoSqlFileEncoding();
        if (daoSqlFileEncoding != null && daoSqlFileEncoding.trim().length() != 0) {
            return true;
        }
        return false;
    }

    public String getDaoSqlFileEncoding() {
        return stringProp("torque.daoSqlFileEncoding", "");
    }

}