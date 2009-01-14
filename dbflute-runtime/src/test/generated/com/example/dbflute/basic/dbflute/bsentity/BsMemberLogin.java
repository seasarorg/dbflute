package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of MEMBER_LOGIN that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     MEMBER_LOGIN_ID
 * 
 * [column]
 *     MEMBER_LOGIN_ID, MEMBER_ID, LOGIN_DATETIME, LOGIN_MOBILE_FLG, LOGIN_MEMBER_STATUS_CODE
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     MEMBER_LOGIN_ID
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     MEMBER, MEMBER_STATUS
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member, memberStatus
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsMemberLogin implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is MEMBER_LOGIN. */
    public static final String TABLE = "MEMBER_LOGIN";

    /** ID-Annotation */
    public static final String memberLoginId_ID = "identity";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** MEMBER_LOGIN_ID: {PK : ID : NotNull : BIGINT} */
    protected Long _memberLoginId;

    /** MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} */
    protected Integer _memberId;

    /** LOGIN_DATETIME: {UQ : NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _loginDatetime;

    /** LOGIN_MOBILE_FLG: {NotNull : INTEGER} */
    protected Integer _loginMobileFlg;

    /** LOGIN_MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} */
    protected String _loginMemberStatusCode;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsMemberLogin() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "MEMBER_LOGIN";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "memberLogin";
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
     * Classify the value of loginMobileFlg as the classification of Flg. <br />
     * フラグを示す
     * @param cls The value of loginMobileFlg as the classification of Flg. (Nullable)
     */
    public void classifyLoginMobileFlg(CDef.Flg cls) {
        setLoginMobileFlg(cls != null ? new Integer(cls.code()) : null);
    }

    /**
     * Classify the value of loginMobileFlg as True. <br />
     * はい: 有効を示す
     */
    public void classifyLoginMobileFlgTrue() {
        classifyLoginMobileFlg(CDef.Flg.True);
    }

    /**
     * Classify the value of loginMobileFlg as False. <br />
     * いいえ: 無効を示す
     */
    public void classifyLoginMobileFlgFalse() {
        classifyLoginMobileFlg(CDef.Flg.False);
    }

    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    /**
     * Get the value of loginMobileFlg as the classification of Flg. <br />
     * フラグを示す
     * @return The value of loginMobileFlg as the classification of Flg. (Nullable)
     */
    public CDef.Flg getLoginMobileFlgAsFlg() {
        return CDef.Flg.codeOf(_loginMobileFlg);
    }

    /**
     * Is the value of the column 'loginMobileFlg' 'True'? <br />
     * はい: 有効を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isLoginMobileFlgTrue() {
        CDef.Flg cls = getLoginMobileFlgAsFlg();
        return cls != null ? cls.equals(CDef.Flg.True) : false;
    }

    /**
     * Is the value of the column 'loginMobileFlg' 'False'? <br />
     * いいえ: 無効を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isLoginMobileFlgFalse() {
        CDef.Flg cls = getLoginMobileFlgAsFlg();
        return cls != null ? cls.equals(CDef.Flg.False) : false;
    }

    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    /**
     * Get the value of the column 'loginMobileFlg' as classification name.
     * @return The value of the column 'loginMobileFlg' as classification name. (Nullable)
     */
    public String getLoginMobileFlgName() {
        CDef.Flg cls = getLoginMobileFlgAsFlg();
        return cls != null ? cls.name() : null;
    }

    /**
     * Get the value of the column 'loginMobileFlg' as classification alias.
     * @return The value of the column 'loginMobileFlg' as classification alias. (Nullable)
     */
    public String getLoginMobileFlgAlias() {
        CDef.Flg cls = getLoginMobileFlgAsFlg();
        return cls != null ? cls.alias() : null;
    }

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

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [memberStatus]
    // * * * * * * * * */
    public static final int memberStatus_RELNO = 1;
    public static final String memberStatus_RELKEYS = "LOGIN_MEMBER_STATUS_CODE:MEMBER_STATUS_CODE";

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

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_memberLoginId == null) { return false; }
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
        if (other == null || !(other instanceof BsMemberLogin)) { return false; }
        BsMemberLogin otherEntity = (BsMemberLogin)other;
        if (!helpComparingValue(getMemberLoginId(), otherEntity.getMemberLoginId())) { return false; }
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
        if (this.getMemberLoginId() != null) { result = result + getMemberLoginId().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getMemberLoginId());
        sb.append(delimiter).append(getMemberId());
        sb.append(delimiter).append(getLoginDatetime());
        sb.append(delimiter).append(getLoginMobileFlg());
        sb.append(delimiter).append(getLoginMemberStatusCode());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : ID : NotNull : BIGINT} */
    public static final String memberLoginId_COLUMN = "MEMBER_LOGIN_ID";

    /**
     * MEMBER_LOGIN_ID: {PK : ID : NotNull : BIGINT} <br />
     * @return The value of the column 'MEMBER_LOGIN_ID'. (Nullable)
     */
    public Long getMemberLoginId() {
        return _memberLoginId;
    }

    /**
     * MEMBER_LOGIN_ID: {PK : ID : NotNull : BIGINT} <br />
     * @param memberLoginId The value of the column 'MEMBER_LOGIN_ID'. (Nullable)
     */
    public void setMemberLoginId(Long memberLoginId) {
        _modifiedProperties.addPropertyName("memberLoginId");
        this._memberLoginId = memberLoginId;
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

    /** The column annotation for S2Dao. {UQ : NotNull : TIMESTAMP} */
    public static final String loginDatetime_COLUMN = "LOGIN_DATETIME";

    /**
     * LOGIN_DATETIME: {UQ : NotNull : TIMESTAMP} <br />
     * @return The value of the column 'LOGIN_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getLoginDatetime() {
        return _loginDatetime;
    }

    /**
     * LOGIN_DATETIME: {UQ : NotNull : TIMESTAMP} <br />
     * @param loginDatetime The value of the column 'LOGIN_DATETIME'. (Nullable)
     */
    public void setLoginDatetime(java.sql.Timestamp loginDatetime) {
        _modifiedProperties.addPropertyName("loginDatetime");
        this._loginDatetime = loginDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : INTEGER} */
    public static final String loginMobileFlg_COLUMN = "LOGIN_MOBILE_FLG";

    /**
     * LOGIN_MOBILE_FLG: {NotNull : INTEGER} <br />
     * @return The value of the column 'LOGIN_MOBILE_FLG'. (Nullable)
     */
    public Integer getLoginMobileFlg() {
        return _loginMobileFlg;
    }

    /**
     * LOGIN_MOBILE_FLG: {NotNull : INTEGER} <br />
     * @param loginMobileFlg The value of the column 'LOGIN_MOBILE_FLG'. (Nullable)
     */
    public void setLoginMobileFlg(Integer loginMobileFlg) {
        _modifiedProperties.addPropertyName("loginMobileFlg");
        this._loginMobileFlg = loginMobileFlg;
    }

    /** The column annotation for S2Dao. {NotNull : CHAR(3) : FK to MEMBER_STATUS} */
    public static final String loginMemberStatusCode_COLUMN = "LOGIN_MEMBER_STATUS_CODE";

    /**
     * LOGIN_MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} <br />
     * @return The value of the column 'LOGIN_MEMBER_STATUS_CODE'. (Nullable)
     */
    public String getLoginMemberStatusCode() {
        return _loginMemberStatusCode;
    }

    /**
     * LOGIN_MEMBER_STATUS_CODE: {NotNull : CHAR(3) : FK to MEMBER_STATUS} <br />
     * @param loginMemberStatusCode The value of the column 'LOGIN_MEMBER_STATUS_CODE'. (Nullable)
     */
    public void setLoginMemberStatusCode(String loginMemberStatusCode) {
        _modifiedProperties.addPropertyName("loginMemberStatusCode");
        this._loginMemberStatusCode = loginMemberStatusCode;
    }

}
