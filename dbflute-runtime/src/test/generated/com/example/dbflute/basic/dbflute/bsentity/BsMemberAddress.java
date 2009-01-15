package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import com.example.dbflute.basic.dbflute.allcommon.EntityDefinedCommonColumn;
import org.seasar.dbflute.dbmeta.DBMeta;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of MEMBER_ADDRESS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_ADDRESS_ID
 * 
 * [column]
 *     MEMBER_ADDRESS_ID, MEMBER_ID, VALID_BEGIN_DATE, VALID_END_DATE, ADDRESS, REGISTER_DATETIME, REGISTER_PROCESS, REGISTER_USER, UPDATE_DATETIME, UPDATE_PROCESS, UPDATE_USER, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberAddress implements EntityDefinedCommonColumn, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is MEMBER_ADDRESS. */
    public static final String TABLE = "MEMBER_ADDRESS";
    
    /** VERSION_NO-Annotation */
    public static final String VERSION_NO_PROPERTY = "versionNo";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** MEMBER_ADDRESS_ID: {PK : NotNull : INTEGER} */
    protected Integer _memberAddressId;

    /** MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} */
    protected Integer _memberId;

    /** VALID_BEGIN_DATE: {UQ : NotNull : DATE} */
    protected java.util.Date _validBeginDate;

    /** VALID_END_DATE: {NotNull : DATE} */
    protected java.util.Date _validEndDate;

    /** ADDRESS: {NotNull : VARCHAR(200)} */
    protected String _address;

    /** REGISTER_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _registerDatetime;

    /** REGISTER_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _registerProcess;

    /** REGISTER_USER: {NotNull : VARCHAR(200)} */
    protected String _registerUser;

    /** UPDATE_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _updateDatetime;

    /** UPDATE_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _updateProcess;

    /** UPDATE_USER: {NotNull : VARCHAR(200)} */
    protected String _updateUser;

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
    public BsMemberAddress() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "MEMBER_ADDRESS";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "memberAddress";
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
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [member]
    // * * * * * * * * */
    public static final int member_RELNO = 0;
    public static final String member_RELKEYS = "MEMBER_ID:MEMBER_ID";

    /** MEMBER as 'member'. */
    protected Member _parentMember;

    /**
     * MEMBER as 'member'. {without lazy-load}
     * @return The entity of foreign property 'member'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public Member getMember() {
        return _parentMember;
    }

    /**
     * MEMBER as 'member'.
     * @param member The entity of foreign property 'member'. (Nullable)
     */
    public void setMember(Member member) {
        _parentMember = member;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_memberAddressId == null) { return false; }
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
        if (other == null || !(other instanceof BsMemberAddress)) { return false; }
        BsMemberAddress otherEntity = (BsMemberAddress)other;
        if (!helpComparingValue(getMemberAddressId(), otherEntity.getMemberAddressId())) { return false; }
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
        if (this.getMemberAddressId() != null) { result = result + getMemberAddressId().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getMemberAddressId());
        sb.append(delimiter).append(getMemberId());
        sb.append(delimiter).append(getValidBeginDate());
        sb.append(delimiter).append(getValidEndDate());
        sb.append(delimiter).append(getAddress());
        sb.append(delimiter).append(getRegisterDatetime());
        sb.append(delimiter).append(getRegisterProcess());
        sb.append(delimiter).append(getRegisterUser());
        sb.append(delimiter).append(getUpdateDatetime());
        sb.append(delimiter).append(getUpdateProcess());
        sb.append(delimiter).append(getUpdateUser());
        sb.append(delimiter).append(getVersionNo());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : NotNull : INTEGER} */
    public static final String memberAddressId_COLUMN = "MEMBER_ADDRESS_ID";

    /**
     * MEMBER_ADDRESS_ID: {PK : NotNull : INTEGER} <br />
     * @return The value of the column 'MEMBER_ADDRESS_ID'. (Nullable)
     */
    public Integer getMemberAddressId() {
        return _memberAddressId;
    }

    /**
     * MEMBER_ADDRESS_ID: {PK : NotNull : INTEGER} <br />
     * @param memberAddressId The value of the column 'MEMBER_ADDRESS_ID'. (Nullable)
     */
    public void setMemberAddressId(Integer memberAddressId) {
        _modifiedProperties.addPropertyName("memberAddressId");
        this._memberAddressId = memberAddressId;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : INTEGER : FK to MEMBER} */
    public static final String memberId_COLUMN = "MEMBER_ID";

    /**
     * MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} <br />
     * @return The value of the column 'MEMBER_ID'. (Nullable)
     */
    public Integer getMemberId() {
        return _memberId;
    }

    /**
     * MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} <br />
     * @param memberId The value of the column 'MEMBER_ID'. (Nullable)
     */
    public void setMemberId(Integer memberId) {
        _modifiedProperties.addPropertyName("memberId");
        this._memberId = memberId;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : DATE} */
    public static final String validBeginDate_COLUMN = "VALID_BEGIN_DATE";

    /**
     * VALID_BEGIN_DATE: {UQ : NotNull : DATE} <br />
     * @return The value of the column 'VALID_BEGIN_DATE'. (Nullable)
     */
    public java.util.Date getValidBeginDate() {
        return _validBeginDate;
    }

    /**
     * VALID_BEGIN_DATE: {UQ : NotNull : DATE} <br />
     * @param validBeginDate The value of the column 'VALID_BEGIN_DATE'. (Nullable)
     */
    public void setValidBeginDate(java.util.Date validBeginDate) {
        _modifiedProperties.addPropertyName("validBeginDate");
        this._validBeginDate = validBeginDate;
    }

    /** The column annotation for S2Dao. {NotNull : DATE} */
    public static final String validEndDate_COLUMN = "VALID_END_DATE";

    /**
     * VALID_END_DATE: {NotNull : DATE} <br />
     * @return The value of the column 'VALID_END_DATE'. (Nullable)
     */
    public java.util.Date getValidEndDate() {
        return _validEndDate;
    }

    /**
     * VALID_END_DATE: {NotNull : DATE} <br />
     * @param validEndDate The value of the column 'VALID_END_DATE'. (Nullable)
     */
    public void setValidEndDate(java.util.Date validEndDate) {
        _modifiedProperties.addPropertyName("validEndDate");
        this._validEndDate = validEndDate;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String address_COLUMN = "ADDRESS";

    /**
     * ADDRESS: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'ADDRESS'. (Nullable)
     */
    public String getAddress() {
        return _address;
    }

    /**
     * ADDRESS: {NotNull : VARCHAR(200)} <br />
     * @param address The value of the column 'ADDRESS'. (Nullable)
     */
    public void setAddress(String address) {
        _modifiedProperties.addPropertyName("address");
        this._address = address;
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
