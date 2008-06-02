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
    //                                                                            Behavior
    //                                                                            ========
    public boolean isMakeEntityTraceRelation() {
        return booleanProp("torque.isMakeEntityTraceRelation", false);
    }

    public boolean isMakeBehaviorLoopUpdate() {
        return booleanProp("torque.isMakeBehaviorLoopUpdate", false);
    }
    
    public boolean isMakeFlatExpansion() {
        return booleanProp("torque.isMakeFlatExpansion", false);
    }
    

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public boolean isMakeDBMetaStaticDefinition() {
        return booleanProp("torque.isMakeDBMetaStaticDefinition", false);
    }
    
    public boolean isMakeDBMetaJDBCSupport() {
        return booleanProp("torque.isMakeDBMetaJDBCSupport", false);
    }
    
    public boolean isMakeDBMetaCommonColumnHandling() {
        return booleanProp("torque.isMakeDBMetaCommonColumnHandling", false);
    }
    
    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isMakeClassificationValueLabelList() {
        return booleanProp("torque.isMakeClassificationValueLabelList", false);
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