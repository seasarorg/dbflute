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
        return booleanProp("torque.isMakeDeprecated", true);
    }

    // ===============================================================================
    //                                                     Properties - ConditionQuery
    //                                                     ===========================
    public boolean isMakeConditionQueryEqualEmptyString() {
        return booleanProp("torque.isMakeConditionQueryEqualEmptyString", true);
    }

    public boolean isMakeConditionQueryNumericArgumentLong() {
        return booleanProp("torque.isMakeConditionQueryNumericArgumentLong", true);
    }

    // ===============================================================================
    //                                                           Properties - Behavior
    //                                                           =====================
    public boolean isMakeBehaviorForUpdate() {
        return booleanProp("torque.isMakeBehaviorForUpdate", true);
    }
}