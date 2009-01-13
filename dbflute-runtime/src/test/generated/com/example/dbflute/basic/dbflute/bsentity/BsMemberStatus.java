package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of MEMBER_STATUS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_STATUS_CODE
 * 
 * [column]
 *     MEMBER_STATUS_CODE, MEMBER_STATUS_NAME, DISPLAY_ORDER
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
 *     MEMBER, MEMBER_LOGIN
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     memberList, memberLoginList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberStatus implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is MEMBER_STATUS. */
    public static final String TABLE = "MEMBER_STATUS";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** MEMBER_STATUS_CODE: {PK : NotNull : CHAR(3)} */
    protected String _memberStatusCode;

    /** MEMBER_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} */
    protected String _memberStatusName;

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
    public BsMemberStatus() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "MEMBER_STATUS";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "memberStatus";
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
    /**
     * Classify the value of memberStatusCode as the classification of MemberStatus. <br />
     * 会員の状態を示す
     * @param cls The value of memberStatusCode as the classification of MemberStatus. (Nullable)
     */
    public void classifyMemberStatusCode(CDef.MemberStatus cls) {
        setMemberStatusCode(cls != null ? new String(cls.code()) : null);
    }

    /**
     * Classify the value of memberStatusCode as Provisional. <br />
     * 仮会員: 仮会員を示す
     */
    public void classifyMemberStatusCodeProvisional() {
        classifyMemberStatusCode(CDef.MemberStatus.Provisional);
    }

    /**
     * Classify the value of memberStatusCode as Formalized. <br />
     * 正式会員: 正式会員を示す
     */
    public void classifyMemberStatusCodeFormalized() {
        classifyMemberStatusCode(CDef.MemberStatus.Formalized);
    }

    /**
     * Classify the value of memberStatusCode as Withdrawal. <br />
     * 退会会員: 退会会員を示す
     */
    public void classifyMemberStatusCodeWithdrawal() {
        classifyMemberStatusCode(CDef.MemberStatus.Withdrawal);
    }

    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    /**
     * Get the value of memberStatusCode as the classification of MemberStatus. <br />
     * 会員の状態を示す
     * @return The value of memberStatusCode as the classification of MemberStatus. (Nullable)
     */
    public CDef.MemberStatus getMemberStatusCodeAsMemberStatus() {
        return CDef.MemberStatus.codeOf(_memberStatusCode);
    }

    /**
     * Is the value of the column 'memberStatusCode' 'Provisional'? <br />
     * 仮会員: 仮会員を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isMemberStatusCodeProvisional() {
        CDef.MemberStatus cls = getMemberStatusCodeAsMemberStatus();
        return cls != null ? cls.equals(CDef.MemberStatus.Provisional) : false;
    }

    /**
     * Is the value of the column 'memberStatusCode' 'Formalized'? <br />
     * 正式会員: 正式会員を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isMemberStatusCodeFormalized() {
        CDef.MemberStatus cls = getMemberStatusCodeAsMemberStatus();
        return cls != null ? cls.equals(CDef.MemberStatus.Formalized) : false;
    }

    /**
     * Is the value of the column 'memberStatusCode' 'Withdrawal'? <br />
     * 退会会員: 退会会員を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isMemberStatusCodeWithdrawal() {
        CDef.MemberStatus cls = getMemberStatusCodeAsMemberStatus();
        return cls != null ? cls.equals(CDef.MemberStatus.Withdrawal) : false;
    }

    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    /**
     * Get the value of the column 'memberStatusCode' as classification name.
     * @return The value of the column 'memberStatusCode' as classification name. (Nullable)
     */
    public String getMemberStatusCodeName() {
        CDef.MemberStatus cls = getMemberStatusCodeAsMemberStatus();
        return cls != null ? cls.name() : null;
    }

    /**
     * Get the value of the column 'memberStatusCode' as classification alias.
     * @return The value of the column 'memberStatusCode' as classification alias. (Nullable)
     */
    public String getMemberStatusCodeAlias() {
        CDef.MemberStatus cls = getMemberStatusCodeAsMemberStatus();
        return cls != null ? cls.alias() : null;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [memberList]
    // * * * * * * * * */
    /** MEMBER as 'memberList'. */
    protected List<Member> _childrenMemberList;

    /**
     * MEMBER as 'memberList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'memberList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<Member> getMemberList() {
        if (_childrenMemberList == null) { _childrenMemberList = new ArrayList<Member>(); }
        return _childrenMemberList;
    }

    /**
     * MEMBER as 'memberList'.
     * @param memberList The entity list of referrer property 'memberList'. (Nullable)
     */
    public void setMemberList(List<Member> memberList) {
        _childrenMemberList = memberList;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [memberLoginList]
    // * * * * * * * * */
    /** MEMBER_LOGIN as 'memberLoginList'. */
    protected List<MemberLogin> _childrenMemberLoginList;

    /**
     * MEMBER_LOGIN as 'memberLoginList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'memberLoginList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<MemberLogin> getMemberLoginList() {
        if (_childrenMemberLoginList == null) { _childrenMemberLoginList = new ArrayList<MemberLogin>(); }
        return _childrenMemberLoginList;
    }

    /**
     * MEMBER_LOGIN as 'memberLoginList'.
     * @param memberLoginList The entity list of referrer property 'memberLoginList'. (Nullable)
     */
    public void setMemberLoginList(List<MemberLogin> memberLoginList) {
        _childrenMemberLoginList = memberLoginList;
    }


    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_memberStatusCode == null) { return false; }
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
        if (other == null || !(other instanceof BsMemberStatus)) { return false; }
        BsMemberStatus otherEntity = (BsMemberStatus)other;
        if (!helpComparingValue(getMemberStatusCode(), otherEntity.getMemberStatusCode())) { return false; }
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
        if (this.getMemberStatusCode() != null) { result = result + getMemberStatusCode().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getMemberStatusCode());
        sb.append(delimiter).append(getMemberStatusName());
        sb.append(delimiter).append(getDisplayOrder());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : NotNull : CHAR(3)} */
    public static final String memberStatusCode_COLUMN = "MEMBER_STATUS_CODE";

    /**
     * MEMBER_STATUS_CODE: {PK : NotNull : CHAR(3)} <br />
     * @return The value of the column 'MEMBER_STATUS_CODE'. (Nullable)
     */
    public String getMemberStatusCode() {
        return _memberStatusCode;
    }

    /**
     * MEMBER_STATUS_CODE: {PK : NotNull : CHAR(3)} <br />
     * @param memberStatusCode The value of the column 'MEMBER_STATUS_CODE'. (Nullable)
     */
    public void setMemberStatusCode(String memberStatusCode) {
        _modifiedProperties.addPropertyName("memberStatusCode");
        this._memberStatusCode = memberStatusCode;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : VARCHAR(50)} */
    public static final String memberStatusName_COLUMN = "MEMBER_STATUS_NAME";

    /**
     * MEMBER_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} <br />
     * @return The value of the column 'MEMBER_STATUS_NAME'. (Nullable)
     */
    public String getMemberStatusName() {
        return _memberStatusName;
    }

    /**
     * MEMBER_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} <br />
     * @param memberStatusName The value of the column 'MEMBER_STATUS_NAME'. (Nullable)
     */
    public void setMemberStatusName(String memberStatusName) {
        _modifiedProperties.addPropertyName("memberStatusName");
        this._memberStatusName = memberStatusName;
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
