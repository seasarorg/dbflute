package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfOtherProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfOtherProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                              Properties - Other
    //                                                              ==================
    public boolean isStopGenerateExtendedBhv() {
        return booleanProp("torque.isStopGenerateExtendedBhv", false);
    }

    public boolean isStopGenerateExtendedDao() {
        return booleanProp("torque.isStopGenerateExtendedDao", false);
    }

    public boolean isStopGenerateExtendedEntity() {
        return booleanProp("torque.isStopGenerateExtendedEntity", false);
    }

    public boolean isVersionAfter1040() {
        return booleanProp("torque.isVersionAfter1040", true);
    }

    public boolean isAvailableOtherConnectionDaoInitialization() {
        return booleanProp("torque.isAvailableOtherConnectionDaoInitialization", false);
    }
    
    public boolean isAvailableDaoMethodLazyInitializing() {
        return booleanProp("torque.isAvailableDaoMethodLazyInitializing", false);
    }
}