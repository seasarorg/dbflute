package com.example.dbflute.basic.dbflute.bsentity.customize;

import java.util.*;

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;


/**
 * The entity of UnpaidSummaryMember that the type is null. <br />
 * <pre>
 * [primary-key]
 *     
 * 
 * [column]
 *     MEMBER_ID, MEMBER_NAME, UNPAID_PRICE_SUMMARY, MEMBER_STATUS_NAME
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUnpaidSummaryMember implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is UnpaidSummaryMember. */
    public static final String TABLE = "UnpaidSummaryMember";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** MEMBER_ID: {INTEGER(10,0)} */
    protected Integer _memberId;

    /** MEMBER_NAME: {VARCHAR(200,0)} */
    protected String _memberName;

    /** UNPAID_PRICE_SUMMARY: {INTEGER(10,0)} */
    protected Integer _unpaidPriceSummary;

    /** MEMBER_STATUS_NAME: {VARCHAR(50,0)} */
    protected String _memberStatusName;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUnpaidSummaryMember() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "UnpaidSummaryMember";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "unpaidSummaryMember";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta.UnpaidSummaryMemberDbm.getInstance();
    }

    // ===================================================================================
    //                                                          Classification Classifying
    //                                                          ==========================
    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        return false;
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    public Set<String> getModifiedPropertyNames() {
        return _modifiedProperties.getPropertyNames();
    }

    protected EntityModifiedProperties newEntityModifiedProperties() {
        return new EntityModifiedProperties();
    }

    public void clearModifiedPropertyNames() {
        _modifiedProperties.clear();
    }

    public boolean hasModification() {
        return !_modifiedProperties.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If the all-column value of the other is same as this one, returns true.
     * @param other Other entity. (Nullable)
     * @return Comparing result. If other is null, returns false.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsUnpaidSummaryMember)) { return false; }
        final BsUnpaidSummaryMember otherEntity = (BsUnpaidSummaryMember)other;
        if (!helpComparingValue(getMemberId(), otherEntity.getMemberId())) { return false; }
        if (!helpComparingValue(getMemberName(), otherEntity.getMemberName())) { return false; }
        if (!helpComparingValue(getUnpaidPriceSummary(), otherEntity.getUnpaidPriceSummary())) { return false; }
        if (!helpComparingValue(getMemberStatusName(), otherEntity.getMemberStatusName())) { return false; }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) { return true; }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from all columns.
     * @return Hash-code from all-columns.
     */
    public int hashCode() {
        int result = 17;
        if (this.getMemberId() != null) { result = result + this.getMemberId().hashCode(); }
        if (this.getMemberName() != null) { result = result + this.getMemberName().hashCode(); }
        if (this.getUnpaidPriceSummary() != null) { result = result + this.getUnpaidPriceSummary().hashCode(); }
        if (this.getMemberStatusName() != null) { result = result + this.getMemberStatusName().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getMemberId());
        sb.append(delimiter).append(getMemberName());
        sb.append(delimiter).append(getUnpaidPriceSummary());
        sb.append(delimiter).append(getMemberStatusName());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {INTEGER(10,0)} */
    public static final String memberId_COLUMN = "MEMBER_ID";

    /**
     * MEMBER_ID: {INTEGER(10,0)} <br />
     * @return The value of the column 'MEMBER_ID'. (Nullable)
     */
    public Integer getMemberId() {
        return _memberId;
    }

    /**
     * MEMBER_ID: {INTEGER(10,0)} <br />
     * @param memberId The value of the column 'MEMBER_ID'. (Nullable)
     */
    public void setMemberId(Integer memberId) {
        _modifiedProperties.addPropertyName("memberId");
        this._memberId = memberId;
    }

    /** The column annotation for S2Dao. {VARCHAR(200,0)} */
    public static final String memberName_COLUMN = "MEMBER_NAME";

    /**
     * MEMBER_NAME: {VARCHAR(200,0)} <br />
     * @return The value of the column 'MEMBER_NAME'. (Nullable)
     */
    public String getMemberName() {
        return _memberName;
    }

    /**
     * MEMBER_NAME: {VARCHAR(200,0)} <br />
     * @param memberName The value of the column 'MEMBER_NAME'. (Nullable)
     */
    public void setMemberName(String memberName) {
        _modifiedProperties.addPropertyName("memberName");
        this._memberName = memberName;
    }

    /** The column annotation for S2Dao. {INTEGER(10,0)} */
    public static final String unpaidPriceSummary_COLUMN = "UNPAID_PRICE_SUMMARY";

    /**
     * UNPAID_PRICE_SUMMARY: {INTEGER(10,0)} <br />
     * @return The value of the column 'UNPAID_PRICE_SUMMARY'. (Nullable)
     */
    public Integer getUnpaidPriceSummary() {
        return _unpaidPriceSummary;
    }

    /**
     * UNPAID_PRICE_SUMMARY: {INTEGER(10,0)} <br />
     * @param unpaidPriceSummary The value of the column 'UNPAID_PRICE_SUMMARY'. (Nullable)
     */
    public void setUnpaidPriceSummary(Integer unpaidPriceSummary) {
        _modifiedProperties.addPropertyName("unpaidPriceSummary");
        this._unpaidPriceSummary = unpaidPriceSummary;
    }

    /** The column annotation for S2Dao. {VARCHAR(50,0)} */
    public static final String memberStatusName_COLUMN = "MEMBER_STATUS_NAME";

    /**
     * MEMBER_STATUS_NAME: {VARCHAR(50,0)} <br />
     * @return The value of the column 'MEMBER_STATUS_NAME'. (Nullable)
     */
    public String getMemberStatusName() {
        return _memberStatusName;
    }

    /**
     * MEMBER_STATUS_NAME: {VARCHAR(50,0)} <br />
     * @param memberStatusName The value of the column 'MEMBER_STATUS_NAME'. (Nullable)
     */
    public void setMemberStatusName(String memberStatusName) {
        _modifiedProperties.addPropertyName("memberStatusName");
        this._memberStatusName = memberStatusName;
    }

}
