package org.seasar.dbflute.properties;

import java.util.Properties;

/**
 * @author jflute
 */
public final class DfMakingOptionProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfMakingOptionProperties(Properties prop) {
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

    // ===================================================================================
    //                                                                              Entity
    //                                                                              ======
    public boolean isMakeEntityTraceRelation() {
        return booleanProp("torque.isMakeEntityTraceRelation", false);
    }

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    public boolean isMakeFlatExpansion() {
        return booleanProp("torque.isMakeFlatExpansion", false);
    }

    // ===================================================================================
    //                                                                                 Dao
    //                                                                                 ===
    public boolean isMakeDaoInterface() {
        if (isTargetLanguageCSharp()) {
            return true; // It is not implemented at CSharp yet
        }
        if (isTargetContainerSpring()) {
            return false; // If the container is for Spring Framework, it always returns false!
        }
        return booleanProp("torque.isMakeDaoInterface", true); // TODO: @jflute Since 0.8.5 false 
    }
    
    protected boolean isTargetLanguageCSharp() {
        return getBasicProperties().isTargetLanguageCSharp();
    }

    protected boolean isTargetContainerSpring() {
        return getBasicProperties().isTargetContainerSpring();
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isMakeClassificationValueLabelList() {
        return booleanProp("torque.isMakeClassificationValueLabelList", false);
    }
}