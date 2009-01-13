package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.Member;

/**
 * The DB meta of MEMBER. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class MemberDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final MemberDbm _instance = new MemberDbm();
    private MemberDbm() {}
    public static MemberDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "MEMBER"; }
    public String getTablePropertyName() { return "member"; }
    public String getTableSqlName() { return "MEMBER"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, true, null, null);
    protected ColumnInfo _columnMemberName = cci("MEMBER_NAME", null, "memberName", String.class, false, 200, 0);
    protected ColumnInfo _columnMemberAccount = cci("MEMBER_ACCOUNT", null, "memberAccount", String.class, false, 50, 0);
    protected ColumnInfo _columnMemberStatusCode = cci("MEMBER_STATUS_CODE", null, "memberStatusCode", String.class, false, 3, 0);
    protected ColumnInfo _columnMemberFormalizedDatetime = cci("MEMBER_FORMALIZED_DATETIME", null, "memberFormalizedDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnMemberBirthday = cci("MEMBER_BIRTHDAY", null, "memberBirthday", java.util.Date.class, false, null, null);
    protected ColumnInfo _columnRegisterDatetime = cci("REGISTER_DATETIME", null, "registerDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnRegisterUser = cci("REGISTER_USER", null, "registerUser", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterProcess = cci("REGISTER_PROCESS", null, "registerProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateDatetime = cci("UPDATE_DATETIME", null, "updateDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnUpdateUser = cci("UPDATE_USER", null, "updateUser", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateProcess = cci("UPDATE_PROCESS", null, "updateProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnVersionNo = cci("VERSION_NO", null, "versionNo", Long.class, false, null, null, OptimisticLockType.VERSION_NO);

    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnMemberName() { return _columnMemberName; }
    public ColumnInfo columnMemberAccount() { return _columnMemberAccount; }
    public ColumnInfo columnMemberStatusCode() { return _columnMemberStatusCode; }
    public ColumnInfo columnMemberFormalizedDatetime() { return _columnMemberFormalizedDatetime; }
    public ColumnInfo columnMemberBirthday() { return _columnMemberBirthday; }
    public ColumnInfo columnRegisterDatetime() { return _columnRegisterDatetime; }
    public ColumnInfo columnRegisterUser() { return _columnRegisterUser; }
    public ColumnInfo columnRegisterProcess() { return _columnRegisterProcess; }
    public ColumnInfo columnUpdateDatetime() { return _columnUpdateDatetime; }
    public ColumnInfo columnUpdateUser() { return _columnUpdateUser; }
    public ColumnInfo columnUpdateProcess() { return _columnUpdateProcess; }
    public ColumnInfo columnVersionNo() { return _columnVersionNo; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnMemberId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasTwoOrMorePrimaryKeys() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignMemberStatus() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberStatusCode(), MemberStatusDbm.getInstance().columnMemberStatusCode());
        return cfi("memberStatus", this, MemberStatusDbm.getInstance(), map, 0, false);
    }
    public ForeignInfo foreignMemberAddressAsValid() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberAddressDbm.getInstance().columnMemberId());
        return cfi("memberAddressAsValid", this, MemberAddressDbm.getInstance(), map, 1, true);
    }
    public ForeignInfo foreignMemberSecurityAsOne() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberSecurityDbm.getInstance().columnMemberId());
        return cfi("memberSecurityAsOne", this, MemberSecurityDbm.getInstance(), map, 2, true);
    }
    public ForeignInfo foreignMemberWithdrawalAsOne() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberWithdrawalDbm.getInstance().columnMemberId());
        return cfi("memberWithdrawalAsOne", this, MemberWithdrawalDbm.getInstance(), map, 3, true);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    public ReferrerInfo referrerMemberAddressList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberAddressDbm.getInstance().columnMemberId());
        return cri("memberAddressList", this, MemberAddressDbm.getInstance(), map, false);
    }
    public ReferrerInfo referrerMemberLoginList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberLoginDbm.getInstance().columnMemberId());
        return cri("memberLoginList", this, MemberLoginDbm.getInstance(), map, false);
    }
    public ReferrerInfo referrerPurchaseList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), PurchaseDbm.getInstance().columnMemberId());
        return cri("purchaseList", this, PurchaseDbm.getInstance(), map, false);
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() { return true; }
    public boolean hasVersionNo() { return true; }
    public ColumnInfo getVersionNoColumnInfo() { return _columnVersionNo; }
    public boolean hasCommonColumn() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.Member"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.MemberCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.MemberDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.MemberBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<Member> getEntityType() { return Member.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public Member newMyEntity() { return new Member(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((Member)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((Member)entity, columnValueMap, _epsMap);
    }

    public void acceptColumnValueMapString(Entity entity, String columnValueMapString) {
        MapStringUtil.acceptColumnValueMapString(columnValueMapString, entity);
    }

    // -----------------------------------------------------
    //                                               Extract
    //                                               -------
    public String extractPrimaryKeyMapString(Entity entity) { return MapStringUtil.extractPrimaryKeyMapString(entity); }
    public String extractPrimaryKeyMapString(Entity entity, String startBrace, String endBrace, String delimiter, String equal) {
        return doExtractPrimaryKeyMapString(entity, startBrace, endBrace, delimiter, equal);
    }
    public String extractColumnValueMapString(Entity entity) { return MapStringUtil.extractColumnValueMapString(entity); }
    public String extractColumnValueMapString(Entity entity, String startBrace, String endBrace, String delimiter, String equal) {
        return doExtractColumnValueMapString(entity, startBrace, endBrace, delimiter, equal);
    }

    // -----------------------------------------------------
    //                                               Convert
    //                                               -------
    public List<Object> convertToColumnValueList(Entity entity) { return newArrayList(convertToColumnValueMap(entity).values()); }
    public Map<String, Object> convertToColumnValueMap(Entity entity) { return doConvertToColumnValueMap(entity); }
    public List<String> convertToColumnStringValueList(Entity entity) { return newArrayList(convertToColumnStringValueMap(entity).values()); }
    public Map<String, String> convertToColumnStringValueMap(Entity entity) { return doConvertToColumnStringValueMap(entity); }

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    protected Map<String, Eps<Member>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsMemberName(), columnMemberName());
        setupEps(_epsMap, new EpsMemberAccount(), columnMemberAccount());
        setupEps(_epsMap, new EpsMemberStatusCode(), columnMemberStatusCode());
        setupEps(_epsMap, new EpsMemberFormalizedDatetime(), columnMemberFormalizedDatetime());
        setupEps(_epsMap, new EpsMemberBirthday(), columnMemberBirthday());
        setupEps(_epsMap, new EpsRegisterDatetime(), columnRegisterDatetime());
        setupEps(_epsMap, new EpsRegisterUser(), columnRegisterUser());
        setupEps(_epsMap, new EpsRegisterProcess(), columnRegisterProcess());
        setupEps(_epsMap, new EpsUpdateDatetime(), columnUpdateDatetime());
        setupEps(_epsMap, new EpsUpdateUser(), columnUpdateUser());
        setupEps(_epsMap, new EpsUpdateProcess(), columnUpdateProcess());
        setupEps(_epsMap, new EpsVersionNo(), columnVersionNo());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((Member)entity, value);
    }
    
    public static class EpsMemberId implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsMemberName implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberName((String)v); }
    }
    public static class EpsMemberAccount implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberAccount((String)v); }
    }
    public static class EpsMemberStatusCode implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberStatusCode((String)v); }
    }
    public static class EpsMemberFormalizedDatetime implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberFormalizedDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsMemberBirthday implements Eps<Member> {
        public void setup(Member e, Object v) { e.setMemberBirthday((java.util.Date)v); }
    }
    public static class EpsRegisterDatetime implements Eps<Member> {
        public void setup(Member e, Object v) { e.setRegisterDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsRegisterUser implements Eps<Member> {
        public void setup(Member e, Object v) { e.setRegisterUser((String)v); }
    }
    public static class EpsRegisterProcess implements Eps<Member> {
        public void setup(Member e, Object v) { e.setRegisterProcess((String)v); }
    }
    public static class EpsUpdateDatetime implements Eps<Member> {
        public void setup(Member e, Object v) { e.setUpdateDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsUpdateUser implements Eps<Member> {
        public void setup(Member e, Object v) { e.setUpdateUser((String)v); }
    }
    public static class EpsUpdateProcess implements Eps<Member> {
        public void setup(Member e, Object v) { e.setUpdateProcess((String)v); }
    }
    public static class EpsVersionNo implements Eps<Member> {
        public void setup(Member e, Object v) { e.setVersionNo((Long)v); }
    }
}
