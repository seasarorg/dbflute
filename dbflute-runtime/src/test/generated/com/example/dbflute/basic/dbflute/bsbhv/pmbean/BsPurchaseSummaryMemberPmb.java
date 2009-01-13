package com.example.dbflute.basic.dbflute.bsbhv.pmbean;


/**
 * The parameter-bean of PurchaseSummaryMemberPmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsPurchaseSummaryMemberPmb  {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of memberStatusCode. */
    protected String _memberStatusCode;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsPurchaseSummaryMemberPmb() {
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
        sb.append(delimiter).append(_memberStatusCode);
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of memberStatusCode. (Converted empty to null)
     * @return The value of memberStatusCode. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public String getMemberStatusCode() {
        return (String)convertEmptyToNullIfString(_memberStatusCode);
    }

    /**
     * Set the value of memberStatusCode.
     * @param memberStatusCode The value of memberStatusCode. (Nullable)
     */
    public void setMemberStatusCode(String memberStatusCode) {
        _memberStatusCode = memberStatusCode;
    }

    /**
     * Set the value of memberStatusCode as Provisional. <br />
     * 仮会員を示す
     */
    public void setMemberStatusCode_Provisional() {
        _memberStatusCode = com.example.dbflute.basic.dbflute.allcommon.CDef.MemberStatus.Provisional.code();
    }

    /**
     * Set the value of memberStatusCode as Formalized. <br />
     * 正式会員を示す
     */
    public void setMemberStatusCode_Formalized() {
        _memberStatusCode = com.example.dbflute.basic.dbflute.allcommon.CDef.MemberStatus.Formalized.code();
    }

    /**
     * Set the value of memberStatusCode as Withdrawal. <br />
     * 退会会員を示す
     */
    public void setMemberStatusCode_Withdrawal() {
        _memberStatusCode = com.example.dbflute.basic.dbflute.allcommon.CDef.MemberStatus.Withdrawal.code();
    }

}
