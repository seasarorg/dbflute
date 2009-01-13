package com.example.dbflute.basic.dbflute.bsbhv.pmbean;


/**
 * The parameter-bean of MapLikeSearchPmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsMapLikeSearchPmb  {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of conditionMap. */
    protected java.util.Map<String, Object> _conditionMap;

    /** The value of likeSearchOption for conditionMap. */
    protected org.dbflute.cbean.coption.LikeSearchOption _conditionMapInternalLikeSearchOption;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMapLikeSearchPmb() {
    }
    
    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * @param value Query value. (Nullable)
     * @return Converted value. (Nullable)
     */
    protected String convertEmptyToNullIfString(String value) {
        return filterRemoveEmptyString(value);
    }

    /**
     * @param value Query value string. (Nullable)
     * @return Removed-empty value. (Nullable)
     */
    protected String filterRemoveEmptyString(String value) {
        return ((value != null && !"".equals(value)) ? value : null);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * The override.
     * @return The view of properties. (NotNull)
     */
    @Override
    public String toString() {
        final String delimiter = ",";
        final StringBuffer sb = new StringBuffer();
        sb.append(delimiter).append(_conditionMap);
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of conditionMap. (Converted empty to null)
     * @return The value of conditionMap. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.Map<String, Object> getConditionMap() {
        return _conditionMap;
    }

    /**
     * Set the value of conditionMap.
     * @param conditionMap The value of conditionMap. (Nullable)
     */
    public void setConditionMap(java.util.Map<String, Object> conditionMap) {
        _conditionMap = conditionMap;
    }

    /**
     * Set the value of conditionMap.
     * @param conditionMap The value of conditionMap. (Nullable)
     * @param conditionMapOption The option of likeSearch for conditionMap. (Nullable)
     */
    public void setConditionMap(java.util.Map<String, Object> conditionMap, org.dbflute.cbean.coption.LikeSearchOption conditionMapOption) {
        _conditionMap = conditionMap;
        _conditionMapInternalLikeSearchOption = conditionMapOption;
    }

    /**
     * Get the internal option of likeSearch for conditionMap. {Internal Method: Don't Invoke This!}
     * @return The internal option of likeSearch for conditionMap. (Nullable)
     */
    public org.dbflute.cbean.coption.LikeSearchOption getConditionMapInternalLikeSearchOption() {
        return _conditionMapInternalLikeSearchOption;
    }

}
