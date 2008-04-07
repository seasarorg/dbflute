package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * @author jflute
 */
public final class DfSourceReductionProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
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
    
    public boolean isMakeConditionQueryClassificationRestriction() {
        return booleanProp("torque.isMakeConditionQueryClassificationRestriction", false);
    }
    
    /**
     * @return Determination
     * @deprecated
     */
    public boolean isMakeConditionQueryNumericArgumentLong() {
        return booleanProp("torque.isMakeConditionQueryNumericArgumentLong", false);
    }
    
    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isMakeClassificationValueLabelList() {
        return booleanProp("torque.isMakeClassificationValueLabelList", false);
    }
    
    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    public boolean isMakeBehaviorLoopUpdate() {
        return booleanProp("torque.isMakeBehaviorLoopUpdate", false);
    }
    
    /**
     * @return Determination
     * @deprecated
     */
    public boolean isMakeBehaviorCopyInsert() {
        return booleanProp("torque.isMakeBehaviorCopyInsert", false);
    }

    /**
     * @return Determination
     * @deprecated
     */
    public boolean isMakeBehaviorForUpdate() {
        return booleanProp("torque.isMakeBehaviorForUpdate", false);
    }

    // ===================================================================================
    //                                                                           Traceable
    //                                                                           =========
    /**
     * @return Determination
     * @deprecated
     */
    public boolean isMakeTraceablePreparedStatement() {
        return booleanProp("torque.isMakeTraceablePreparedStatement", false);
    }
}