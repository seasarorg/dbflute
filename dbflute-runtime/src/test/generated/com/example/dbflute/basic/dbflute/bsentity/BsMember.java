package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.EntityDefinedCommonColumn;
import org.seasar.dbflute.dbmeta.DBMeta;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of MEMBER that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_ID
 * 
 * [column]
 *     MEMBER_ID, MEMBER_NAME, MEMBER_ACCOUNT, MEMBER_STATUS_CODE, MEMBER_FORMALIZED_DATETIME, MEMBER_BIRTHDAY, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     MEMBER_ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER_STATUS, MEMBER_ADDRESS(AsValid), MEMBER_SECURITY(AsOne), MEMBER_WITHDRAWAL(AsOne)
 * 
 * [referrer-table]
 *     MEMBER_ADDRESS, MEMBER_LOGIN, PURCHASE, MEMBER_SECURITY, MEMBER_WITHDRAWAL
 * 
 * [foreign-property]
 *     memberStatus, memberAddressAsValid, memberSecurityAsOne, memberWithdrawalAsOne
 * 
 * [referrer-property]
 *     memberAddressList, memberLoginList, purchaseList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMember implements EntityDefinedCommonColumn, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is MEMBER. */
    public static final String TABLE = "MEMBER";
    
    /** VERSION_NO-Annotation */
    public static final String VERSION_NO_PROPERTY = "versionNo";

    /** ID-Annotation */
    public static final String memberId_ID = "identity";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** MEMBER_ID: {PK : ID : NotNull : INTEGER : FK to MEMBER_ADDRESS} */
    protected Integer _memberId;

    /** MEMBER_NAME: {NotNull : VARCHAR(200)} */
    protected String _memberName;

    /** MEMBER_ACCOUNT: {UQ : NotNull : VARCHAR(50)} */
    protected String _memberAccount;

    /** MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} */
    protected String _memberStatusCode;

    /** MEMBER_FORMALIZED_DATETIME: {TIMESTAMP} */
    protected java.sql.Timestamp _memberFormalizedDatetime;

    /** MEMBER_BIRTHDAY: {DATE} */
    protected java.util.Date _memberBirthday;

    /** REGISTER_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _registerDatetime;

    /** REGISTER_USER: {NotNull : VARCHAR(200)} */
    protected String _registerUser;

    /** REGISTER_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _registerProcess;

    /** UPDATE_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _updateDatetime;

    /** UPDATE_USER: {NotNull : VARCHAR(200)} */
    protected String _updateUser;

    /** UPDATE_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _updateProcess;

    /** VERSION_NO: {NotNull : BIGINT} */
    protected Long _versionNo;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();

    /** Is common column auto set up effective? */
    protected boolean _canCommonColumnAutoSetup = true;
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMember() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "MEMBER";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "member";
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
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [memberStatus]
    // * * * * * * * * */
    public static final int memberStatus_RELNO = 0;
    public static final String memberStatus_RELKEYS = "MEMBER_STATUS_CODE:MEMBER_STATUS_CODE";

    /** MEMBER_STATUS as 'memberStatus'. */
    protected MemberStatus _parentMemberStatus;

    /**
     * MEMBER_STATUS as 'memberStatus'. {without lazy-load}
     * @return The entity of foreign property 'memberStatus'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public MemberStatus getMemberStatus() {
        return _parentMemberStatus;
    }

    /**
     * MEMBER_STATUS as 'memberStatus'.
     * @param memberStatus The entity of foreign property 'memberStatus'. (Nullable)
     */
    public void setMemberStatus(MemberStatus memberStatus) {
        _parentMemberStatus = memberStatus;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [memberAddressAsValid]
    // * * * * * * * * */
    public static final int memberAddressAsValid_RELNO = 1;
    public static final String memberAddressAsValid_RELKEYS = "MEMBER_ID:MEMBER_ID";

    /** MEMBER_ADDRESS as 'memberAddressAsValid'. */
    protected MemberAddress _parentMemberAddressAsValid;

    /**
     * MEMBER_ADDRESS as 'memberAddressAsValid'. {without lazy-load}
     * @return The entity of foreign property 'memberAddressAsValid'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public MemberAddress getMemberAddressAsValid() {
        return _parentMemberAddressAsValid;
    }

    /**
     * MEMBER_ADDRESS as 'memberAddressAsValid'.
     * @param memberAddressAsValid The entity of foreign property 'memberAddressAsValid'. (Nullable)
     */
    public void setMemberAddressAsValid(MemberAddress memberAddressAsValid) {
        _parentMemberAddressAsValid = memberAddressAsValid;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [memberSecurityAsOne]
    // * * * * * * * * */
    public static final int memberSecurityAsOne_RELNO = 2;
    public static final String memberSecurityAsOne_RELKEYS = "MEMBER_ID:MEMBER_ID";
    
    /** MEMBER_SECURITY as 'memberSecurityAsOne'. */
    protected MemberSecurity _childrenmemberSecurityAsOne;

    /**
     * MEMBER_SECURITY as 'memberSecurityAsOne'. {without lazy-load} <br />
     * @return the entity of foreign property(referrer-as-one) 'memberSecurityAsOne'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public MemberSecurity getMemberSecurityAsOne() {
        return _childrenmemberSecurityAsOne;
    }

    /**
     * MEMBER_SECURITY as 'memberSecurityAsOne'.
     * @param memberSecurityAsOne The entity of foreign property(referrer-as-one) 'memberSecurityAsOne'. (Nullable)
     */
    public void setMemberSecurityAsOne(MemberSecurity memberSecurityAsOne) {
        _childrenmemberSecurityAsOne = memberSecurityAsOne;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [memberWithdrawalAsOne]
    // * * * * * * * * */
    public static final int memberWithdrawalAsOne_RELNO = 3;
    public static final String memberWithdrawalAsOne_RELKEYS = "MEMBER_ID:MEMBER_ID";
    
    /** MEMBER_WITHDRAWAL as 'memberWithdrawalAsOne'. */
    protected MemberWithdrawal _childrenmemberWithdrawalAsOne;

    /**
     * MEMBER_WITHDRAWAL as 'memberWithdrawalAsOne'. {without lazy-load} <br />
     * @return the entity of foreign property(referrer-as-one) 'memberWithdrawalAsOne'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public MemberWithdrawal getMemberWithdrawalAsOne() {
        return _childrenmemberWithdrawalAsOne;
    }

    /**
     * MEMBER_WITHDRAWAL as 'memberWithdrawalAsOne'.
     * @param memberWithdrawalAsOne The entity of foreign property(referrer-as-one) 'memberWithdrawalAsOne'. (Nullable)
     */
    public void setMemberWithdrawalAsOne(MemberWithdrawal memberWithdrawalAsOne) {
        _childrenmemberWithdrawalAsOne = memberWithdrawalAsOne;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [memberAddressList]
    // * * * * * * * * */
    /** MEMBER_ADDRESS as 'memberAddressList'. */
    protected List<MemberAddress> _childrenMemberAddressList;

    /**
     * MEMBER_ADDRESS as 'memberAddressList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'memberAddressList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<MemberAddress> getMemberAddressList() {
        if (_childrenMemberAddressList == null) { _childrenMemberAddressList = new ArrayList<MemberAddress>(); }
        return _childrenMemberAddressList;
    }

    /**
     * MEMBER_ADDRESS as 'memberAddressList'.
     * @param memberAddressList The entity list of referrer property 'memberAddressList'. (Nullable)
     */
    public void setMemberAddressList(List<MemberAddress> memberAddressList) {
        _childrenMemberAddressList = memberAddressList;
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

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [purchaseList]
    // * * * * * * * * */
    /** PURCHASE as 'purchaseList'. */
    protected List<Purchase> _childrenPurchaseList;

    /**
     * PURCHASE as 'purchaseList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'purchaseList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<Purchase> getPurchaseList() {
        if (_childrenPurchaseList == null) { _childrenPurchaseList = new ArrayList<Purchase>(); }
        return _childrenPurchaseList;
    }

    /**
     * PURCHASE as 'purchaseList'.
     * @param purchaseList The entity list of referrer property 'purchaseList'. (Nullable)
     */
    public void setPurchaseList(List<Purchase> purchaseList) {
        _childrenPurchaseList = purchaseList;
    }


    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_memberId == null) { return false; }
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
    //                                                           Common Column Auto Filter
    //                                                           =========================
    /**
     * Enable common column auto set up. {for after disable because the default is enabled}
     */
    public void enableCommonColumnAutoSetup() {
        _canCommonColumnAutoSetup = true;
    }

    /**
     * Disables auto set-up of common columns.
     */
    public void disableCommonColumnAutoSetup() {
        _canCommonColumnAutoSetup = false;
    }
    
    /**
     * Can the entity set up common column by auto?
     * @return Determination.
     */
    public boolean canCommonColumnAutoSetup() { // for Framework
        return _canCommonColumnAutoSetup;
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
        if (other == null || !(other instanceof BsMember)) { return false; }
        BsMember otherEntity = (BsMember)other;
        if (!helpComparingValue(getMemberId(), otherEntity.getMemberId())) { return false; }
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
        if (this.getMemberId() != null) { result = result + getMemberId().hashCode(); }
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
        sb.append(delimiter).append(getMemberAccount());
        sb.append(delimiter).append(getMemberStatusCode());
        sb.append(delimiter).append(getMemberFormalizedDatetime());
        sb.append(delimiter).append(getMemberBirthday());
        sb.append(delimiter).append(getRegisterDatetime());
        sb.append(delimiter).append(getRegisterUser());
        sb.append(delimiter).append(getRegisterProcess());
        sb.append(delimiter).append(getUpdateDatetime());
        sb.append(delimiter).append(getUpdateUser());
        sb.append(delimiter).append(getUpdateProcess());
        sb.append(delimiter).append(getVersionNo());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : ID : NotNull : INTEGER : FK to MEMBER_ADDRESS} */
    public static final String memberId_COLUMN = "MEMBER_ID";

    /**
     * MEMBER_ID: {PK : ID : NotNull : INTEGER : FK to MEMBER_ADDRESS} <br />
     * @return The value of the column 'MEMBER_ID'. (Nullable)
     */
    public Integer getMemberId() {
        return _memberId;
    }

    /**
     * MEMBER_ID: {PK : ID : NotNull : INTEGER : FK to MEMBER_ADDRESS} <br />
     * @param memberId The value of the column 'MEMBER_ID'. (Nullable)
     */
    public void setMemberId(Integer memberId) {
        _modifiedProperties.addPropertyName("memberId");
        this._memberId = memberId;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String memberName_COLUMN = "MEMBER_NAME";

    /**
     * MEMBER_NAME: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'MEMBER_NAME'. (Nullable)
     */
    public String getMemberName() {
        return _memberName;
    }

    /**
     * MEMBER_NAME: {NotNull : VARCHAR(200)} <br />
     * @param memberName The value of the column 'MEMBER_NAME'. (Nullable)
     */
    public void setMemberName(String memberName) {
        _modifiedProperties.addPropertyName("memberName");
        this._memberName = memberName;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : VARCHAR(50)} */
    public static final String memberAccount_COLUMN = "MEMBER_ACCOUNT";

    /**
     * MEMBER_ACCOUNT: {UQ : NotNull : VARCHAR(50)} <br />
     * @return The value of the column 'MEMBER_ACCOUNT'. (Nullable)
     */
    public String getMemberAccount() {
        return _memberAccount;
    }

    /**
     * MEMBER_ACCOUNT: {UQ : NotNull : VARCHAR(50)} <br />
     * @param memberAccount The value of the column 'MEMBER_ACCOUNT'. (Nullable)
     */
    public void setMemberAccount(String memberAccount) {
        _modifiedProperties.addPropertyName("memberAccount");
        this._memberAccount = memberAccount;
    }

    /** The column annotation for S2Dao. {NotNull : CHAR(3) : FK to MEMBER_STATUS} */
    public static final String memberStatusCode_COLUMN = "MEMBER_STATUS_CODE";

    /**
     * MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} <br />
     * @return The value of the column 'MEMBER_STATUS_CODE'. (Nullable)
     */
    public String getMemberStatusCode() {
        return _memberStatusCode;
    }

    /**
     * MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} <br />
     * @param memberStatusCode The value of the column 'MEMBER_STATUS_CODE'. (Nullable)
     */
    public void setMemberStatusCode(String memberStatusCode) {
        _modifiedProperties.addPropertyName("memberStatusCode");
        this._memberStatusCode = memberStatusCode;
    }

    /** The column annotation for S2Dao. {TIMESTAMP} */
    public static final String memberFormalizedDatetime_COLUMN = "MEMBER_FORMALIZED_DATETIME";

    /**
     * MEMBER_FORMALIZED_DATETIME: {TIMESTAMP} <br />
     * @return The value of the column 'MEMBER_FORMALIZED_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getMemberFormalizedDatetime() {
        return _memberFormalizedDatetime;
    }

    /**
     * MEMBER_FORMALIZED_DATETIME: {TIMESTAMP} <br />
     * @param memberFormalizedDatetime The value of the column 'MEMBER_FORMALIZED_DATETIME'. (Nullable)
     */
    public void setMemberFormalizedDatetime(java.sql.Timestamp memberFormalizedDatetime) {
        _modifiedProperties.addPropertyName("memberFormalizedDatetime");
        this._memberFormalizedDatetime = memberFormalizedDatetime;
    }

    /** The column annotation for S2Dao. {DATE} */
    public static final String memberBirthday_COLUMN = "MEMBER_BIRTHDAY";

    /**
     * MEMBER_BIRTHDAY: {DATE} <br />
     * @return The value of the column 'MEMBER_BIRTHDAY'. (Nullable)
     */
    public java.util.Date getMemberBirthday() {
        return _memberBirthday;
    }

    /**
     * MEMBER_BIRTHDAY: {DATE} <br />
     * @param memberBirthday The value of the column 'MEMBER_BIRTHDAY'. (Nullable)
     */
    public void setMemberBirthday(java.util.Date memberBirthday) {
        _modifiedProperties.addPropertyName("memberBirthday");
        this._memberBirthday = memberBirthday;
    }

    /** The column annotation for S2Dao. {NotNull : TIMESTAMP} */
    public static final String registerDatetime_COLUMN = "REGISTER_DATETIME";

    /**
     * REGISTER_DATETIME: {NotNull : TIMESTAMP} <br />
     * @return The value of the column 'REGISTER_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getRegisterDatetime() {
        return _registerDatetime;
    }

    /**
     * REGISTER_DATETIME: {NotNull : TIMESTAMP} <br />
     * @param registerDatetime The value of the column 'REGISTER_DATETIME'. (Nullable)
     */
    public void setRegisterDatetime(java.sql.Timestamp registerDatetime) {
        _modifiedProperties.addPropertyName("registerDatetime");
        this._registerDatetime = registerDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String registerUser_COLUMN = "REGISTER_USER";

    /**
     * REGISTER_USER: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'REGISTER_USER'. (Nullable)
     */
    public String getRegisterUser() {
        return _registerUser;
    }

    /**
     * REGISTER_USER: {NotNull : VARCHAR(200)} <br />
     * @param registerUser The value of the column 'REGISTER_USER'. (Nullable)
     */
    public void setRegisterUser(String registerUser) {
        _modifiedProperties.addPropertyName("registerUser");
        this._registerUser = registerUser;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String registerProcess_COLUMN = "REGISTER_PROCESS";

    /**
     * REGISTER_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'REGISTER_PROCESS'. (Nullable)
     */
    public String getRegisterProcess() {
        return _registerProcess;
    }

    /**
     * REGISTER_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @param registerProcess The value of the column 'REGISTER_PROCESS'. (Nullable)
     */
    public void setRegisterProcess(String registerProcess) {
        _modifiedProperties.addPropertyName("registerProcess");
        this._registerProcess = registerProcess;
    }

    /** The column annotation for S2Dao. {NotNull : TIMESTAMP} */
    public static final String updateDatetime_COLUMN = "UPDATE_DATETIME";

    /**
     * UPDATE_DATETIME: {NotNull : TIMESTAMP} <br />
     * @return The value of the column 'UPDATE_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getUpdateDatetime() {
        return _updateDatetime;
    }

    /**
     * UPDATE_DATETIME: {NotNull : TIMESTAMP} <br />
     * @param updateDatetime The value of the column 'UPDATE_DATETIME'. (Nullable)
     */
    public void setUpdateDatetime(java.sql.Timestamp updateDatetime) {
        _modifiedProperties.addPropertyName("updateDatetime");
        this._updateDatetime = updateDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String updateUser_COLUMN = "UPDATE_USER";

    /**
     * UPDATE_USER: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'UPDATE_USER'. (Nullable)
     */
    public String getUpdateUser() {
        return _updateUser;
    }

    /**
     * UPDATE_USER: {NotNull : VARCHAR(200)} <br />
     * @param updateUser The value of the column 'UPDATE_USER'. (Nullable)
     */
    public void setUpdateUser(String updateUser) {
        _modifiedProperties.addPropertyName("updateUser");
        this._updateUser = updateUser;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String updateProcess_COLUMN = "UPDATE_PROCESS";

    /**
     * UPDATE_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'UPDATE_PROCESS'. (Nullable)
     */
    public String getUpdateProcess() {
        return _updateProcess;
    }

    /**
     * UPDATE_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @param updateProcess The value of the column 'UPDATE_PROCESS'. (Nullable)
     */
    public void setUpdateProcess(String updateProcess) {
        _modifiedProperties.addPropertyName("updateProcess");
        this._updateProcess = updateProcess;
    }

    /** The column annotation for S2Dao. {NotNull : BIGINT} */
    public static final String versionNo_COLUMN = "VERSION_NO";

    /**
     * VERSION_NO: {NotNull : BIGINT} <br />
     * @return The value of the column 'VERSION_NO'. (Nullable)
     */
    public Long getVersionNo() {
        return _versionNo;
    }

    /**
     * VERSION_NO: {NotNull : BIGINT} <br />
     * @param versionNo The value of the column 'VERSION_NO'. (Nullable)
     */
    public void setVersionNo(Long versionNo) {
        _modifiedProperties.addPropertyName("versionNo");
        this._versionNo = versionNo;
    }

}
