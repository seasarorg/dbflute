package org.seasar.dbflute.properties;

import java.util.Properties;

import org.seasar.dbflute.util.basic.DfStringUtil;

/**
 * @author jflute
 * @since 0.5.4 (2007/07/18 Wednesday)
 */
public final class DfS2DaoAdjustmentProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfS2DaoAdjustmentProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                       S2Dao Version
    //                                                                       =============
    // public boolean isVersionAfter1047() {
    //     return hasS2DaoVersion() ? isS2DaoVersionGreaterEqual("1.0.47") : true;
    // }

    protected boolean hasS2DaoVersion() {
        final String value = stringProp("torque.s2daoVersion", null);
        if (value != null && value.trim().length() != 9) {
            return true;
        }
        return false;
    }

    protected String getS2DaoVersion() {
        final String s2daoVersion = stringProp("torque.s2daoVersion", null);
        return s2daoVersion != null ? DfStringUtil.replace(s2daoVersion, ".", "") : "9.9.99";// If null, return the latest version!
    }

    protected boolean isS2DaoVersionGreaterEqual(String targetVersion) {
        final String s2daoVersion = getS2DaoVersion();
        final String filteredTargetVersion = DfStringUtil.replace(targetVersion, ".", "");
        return s2daoVersion.compareToIgnoreCase(filteredTargetVersion) >= 0;
    }

    // ===================================================================================
    //                                                                     S2Dao Component
    //                                                                     ===============
    public String getExtendedDaoMetaDataFactoryImplClassName() {
        final DfBasicProperties basicProperties = getBasicProperties();
        final DfGeneratedClassPackageProperties generatedClassPackageProperties = getGeneratedClassPackageProperties();
        final String baseCommonPackage = generatedClassPackageProperties.getBaseCommonPackage();
        final String projectPrefix = basicProperties.getProjectPrefix();
        final String s2daoPackage = getS2DaoPackage();
        final String defaultClassName = baseCommonPackage + "." + s2daoPackage + "." + projectPrefix
                + "S2DaoMetaDataFactoryImpl";
        return stringPropNoEmpty("torque.extendedDaoMetaDataFactoryImplClassName", defaultClassName);
    }

    public String getExtendedAnnotationReaderFactoryClassName() {
        final String defaultClassName = "org.seasar.dao.impl.AnnotationReaderFactoryImpl";
        return stringPropNoEmpty("torque.extendedAnnotationReaderFactoryClassName", defaultClassName);
    }

    protected String getS2DaoPackage() {
        String name = "s2dao";
        if (getBasicProperties().isTargetLanguageCSharp()) {
            name = "S2Dao";
        }
        return name;
    }

    // ===================================================================================
    //                                                                      S2Dao Property
    //                                                                      ==============
    public boolean hasDaoSqlFileEncoding() {
        final String daoSqlFileEncoding = getDaoSqlFileEncoding();
        if (daoSqlFileEncoding != null && daoSqlFileEncoding.trim().length() != 0) {
            return true;
        }
        return false;
    }

    public String getDaoSqlFileEncoding() {
        final String defaultEncoding = "UTF-8";
        final String property = stringProp("torque.daoSqlFileEncoding", defaultEncoding);
        return !property.equals("null") ? property : defaultEncoding;
    }
}