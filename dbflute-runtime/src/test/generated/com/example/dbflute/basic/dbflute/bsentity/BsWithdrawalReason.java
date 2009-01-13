package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of WITHDRAWAL_REASON that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     WITHDRAWAL_REASON_CODE
 * 
 * [column]
 *     WITHDRAWAL_REASON_CODE, WITHDRAWAL_REASON_TEXT, DISPLAY_ORDER
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
 *     MEMBER_WITHDRAWAL
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     memberWithdrawalList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWithdrawalReason implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is WITHDRAWAL_REASON. */
    public static final String TABLE = "WITHDRAWAL_REASON";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** WITHDRAWAL_REASON_CODE: {PK : NotNull : CHAR(3)} */
    protected String _withdrawalReasonCode;

    /** WITHDRAWAL_REASON_TEXT: {NotNull : CLOB} */
    protected String _withdrawalReasonText;

    /** DISPLAY_ORDER: {UQ : NotNull : INTEGER} */
    protected Integer _displayOrder;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWithdrawalReason() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "WITHDRAWAL_REASON";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "withdrawalReason";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
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
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [memberWithdrawalList]
    // * * * * * * * * */
    /** MEMBER_WITHDRAWAL as 'memberWithdrawalList'. */
    protected List<MemberWithdrawal> _childrenMemberWithdrawalList;

    /**
     * MEMBER_WITHDRAWAL as 'memberWithdrawalList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'memberWithdrawalList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<MemberWithdrawal> getMemberWithdrawalList() {
        if (_childrenMemberWithdrawalList == null) { _childrenMemberWithdrawalList = new ArrayList<MemberWithdrawal>(); }
        return _childrenMemberWithdrawalList;
    }

    /**
     * MEMBER_WITHDRAWAL as 'memberWithdrawalList'.
     * @param memberWithdrawalList The entity list of referrer property 'memberWithdrawalList'. (Nullable)
     */
    public void setMemberWithdrawalList(List<MemberWithdrawal> memberWithdrawalList) {
        _childrenMemberWithdrawalList = memberWithdrawalList;
    }


    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_withdrawalReasonCode == null) { return false; }
        return true;
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
     * If the primary-key of the other is same as this one, returns true.
     * @param other Other entity.
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsWithdrawalReason)) { return false; }
        BsWithdrawalReason otherEntity = (BsWithdrawalReason)other;
        if (!helpComparingValue(getWithdrawalReasonCode(), otherEntity.getWithdrawalReasonCode())) { return false; }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) { return true; }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from primary-key.
     * @return Hash-code from primary-keys.
     */
    public int hashCode() {
        int result = 17;
        if (this.getWithdrawalReasonCode() != null) { result = result + getWithdrawalReasonCode().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getWithdrawalReasonCode());
        sb.append(delimiter).append(getWithdrawalReasonText());
        sb.append(delimiter).append(getDisplayOrder());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : NotNull : CHAR(3)} */
    public static final String withdrawalReasonCode_COLUMN = "WITHDRAWAL_REASON_CODE";

    /**
     * WITHDRAWAL_REASON_CODE: {PK : NotNull : CHAR(3)} <br />
     * @return The value of the column 'WITHDRAWAL_REASON_CODE'. (Nullable)
     */
    public String getWithdrawalReasonCode() {
        return _withdrawalReasonCode;
    }

    /**
     * WITHDRAWAL_REASON_CODE: {PK : NotNull : CHAR(3)} <br />
     * @param withdrawalReasonCode The value of the column 'WITHDRAWAL_REASON_CODE'. (Nullable)
     */
    public void setWithdrawalReasonCode(String withdrawalReasonCode) {
        _modifiedProperties.addPropertyName("withdrawalReasonCode");
        this._withdrawalReasonCode = withdrawalReasonCode;
    }

    /** The column annotation for S2Dao. {NotNull : CLOB} */
    public static final String withdrawalReasonText_COLUMN = "WITHDRAWAL_REASON_TEXT";

    /**
     * WITHDRAWAL_REASON_TEXT: {NotNull : CLOB} <br />
     * @return The value of the column 'WITHDRAWAL_REASON_TEXT'. (Nullable)
     */
    public String getWithdrawalReasonText() {
        return _withdrawalReasonText;
    }

    /**
     * WITHDRAWAL_REASON_TEXT: {NotNull : CLOB} <br />
     * @param withdrawalReasonText The value of the column 'WITHDRAWAL_REASON_TEXT'. (Nullable)
     */
    public void setWithdrawalReasonText(String withdrawalReasonText) {
        _modifiedProperties.addPropertyName("withdrawalReasonText");
        this._withdrawalReasonText = withdrawalReasonText;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : INTEGER} */
    public static final String displayOrder_COLUMN = "DISPLAY_ORDER";

    /**
     * DISPLAY_ORDER: {UQ : NotNull : INTEGER} <br />
     * @return The value of the column 'DISPLAY_ORDER'. (Nullable)
     */
    public Integer getDisplayOrder() {
        return _displayOrder;
    }

    /**
     * DISPLAY_ORDER: {UQ : NotNull : INTEGER} <br />
     * @param displayOrder The value of the column 'DISPLAY_ORDER'. (Nullable)
     */
    public void setDisplayOrder(Integer displayOrder) {
        _modifiedProperties.addPropertyName("displayOrder");
        this._displayOrder = displayOrder;
    }

}
