package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * @author jflute
 */
public final class DfSourceReductionProperties extends DfAbstractHelperProperties {

    /**
     * Constructor.
     * 
     * @param prop Properties. (NotNull)
     */
    public DfSourceReductionProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                     Make Deprecated
    //                                                                     ===============
    public boolean isMakeDeprecated() {
        return booleanProp("torque.isMakeDeprecated", false);
    }

    public boolean isMakeRecentlyDeprecated() {
        return booleanProp("torque.isMakeRecentlyDeprecated", true);
    }

    // ===================================================================================
    //                                                                 Make ConditionQuery
    //                                                                 ===================
    public boolean isMakeConditionQueryEqualEmptyString() {
        return booleanProp("torque.isMakeConditionQueryEqualEmptyString", false);
    }

    public boolean isMakeConditionQueryNumericArgumentLong() {
        return booleanProp("torque.isMakeConditionQueryNumericArgumentLong", false);
    }

    public boolean isMakeConditionQueryClassificationRestriction() {
        return booleanProp("torque.isMakeConditionQueryClassificationRestriction", false);
    }
    
    // ===============================================================================
    //                                                           Properties - Behavior
    //                                                           =====================
    public boolean isMakeBehaviorCopyInsert() {
        return booleanProp("torque.isMakeBehaviorCopyInsert", false);
    }

    public boolean isMakeBehaviorLoopUpdate() {
        return booleanProp("torque.isMakeBehaviorLoopUpdate", false);
    }

    public boolean isMakeBehaviorForUpdate() {
        return booleanProp("torque.isMakeBehaviorForUpdate", false);
    }

    // ===============================================================================
    //                                                          Properties - Traceable
    //                                                          ======================
    public boolean isMakeTraceablePreparedStatement() {
        return booleanProp("torque.isMakeTraceablePreparedStatement", false);
    }
}