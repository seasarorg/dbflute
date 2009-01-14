package com.example.dbflute.basic.dbflute.bsbhv.pmbean;


/**
 * The parameter-bean of OptionMemberPmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsOptionMemberPmb  {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of memberId. */
    protected Integer _memberId;

    /** The value of memberName. */
    protected String _memberName;

    /** The value of likeSearchOption for memberName. */
    protected org.seasar.dbflute.cbean.coption.LikeSearchOption _memberNameInternalLikeSearchOption;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsOptionMemberPmb() {
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
        sb.append(delimiter).append(_memberId);
        sb.append(delimiter).append(_memberName);
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of memberId. (Converted empty to null)
     * @return The value of memberId. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public Integer getMemberId() {
        return _memberId;
    }

    /**
     * Set the value of memberId.
     * @param memberId The value of memberId. (Nullable)
     */
    public void setMemberId(Integer memberId) {
        _memberId = memberId;
    }

    /**
     * Get the value of memberName. (Converted empty to null)
     * @return The value of memberName. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public String getMemberName() {
        return (String)convertEmptyToNullIfString(_memberName);
    }

    /**
     * Set the value of memberName.
     * @param memberName The value of memberName. (Nullable)
     */
    public void setMemberName(String memberName) {
        _memberName = memberName;
    }

    /**
     * Set the value of memberName.
     * @param memberName The value of memberName. (Nullable)
     * @param memberNameOption The option of likeSearch for memberName. (Nullable)
     */
    public void setMemberName(String memberName, org.seasar.dbflute.cbean.coption.LikeSearchOption memberNameOption) {
        _memberName = memberName;
        _memberNameInternalLikeSearchOption = memberNameOption;
    }

    /**
     * Get the internal option of likeSearch for memberName. {Internal Method: Don't Invoke This!}
     * @return The internal option of likeSearch for memberName. (Nullable)
     */
    public org.seasar.dbflute.cbean.coption.LikeSearchOption getMemberNameInternalLikeSearchOption() {
        return _memberNameInternalLikeSearchOption;
    }

}
