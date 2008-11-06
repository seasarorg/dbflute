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
        return booleanProp("torque.isMakeDaoInterface", true); // TODO: @jflute Since 0.8.5 false 
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isMakeClassificationValueLabelList() {
        return booleanProp("torque.isMakeClassificationValueLabelList", false);
    }
}