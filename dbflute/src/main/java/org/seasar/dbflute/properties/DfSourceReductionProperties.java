package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class DfSourceReductionProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfSourceReductionProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                                         Properties - Deprecated
    //                                                         =======================
    public boolean isMakeDeprecated() {
        return booleanProp("torque.isMakeDeprecated", false);
    }

    // ===============================================================================
    //                                                     Properties - ConditionQuery
    //                                                     ===========================
    public boolean isMakeConditionQueryEqualEmptyString() {
        return booleanProp("torque.isMakeConditionQueryEqualEmptyString", false);
    }

    public boolean isMakeConditionQueryNumericArgumentLong() {
        return booleanProp("torque.isMakeConditionQueryNumericArgumentLong", false);
    }

    // ===============================================================================
    //                                                           Properties - Behavior
    //                                                           =====================
    public boolean isMakeBehaviorForUpdate() {
        return booleanProp("torque.isMakeBehaviorForUpdate", false);
    }
}