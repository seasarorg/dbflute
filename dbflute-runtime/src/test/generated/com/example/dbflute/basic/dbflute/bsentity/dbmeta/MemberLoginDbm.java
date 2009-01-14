package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.MemberLogin;

/**
 * The DB meta of MEMBER_LOGIN. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class MemberLoginDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final MemberLoginDbm _instance = new MemberLoginDbm();
    private MemberLoginDbm() {}
    public static MemberLoginDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "MEMBER_LOGIN"; }
    public String getTablePropertyName() { return "memberLogin"; }
    public String getTableSqlName() { return "MEMBER_LOGIN"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberLoginId = cci("MEMBER_LOGIN_ID", null, "memberLoginId", Long.class, true, null, null);
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, false, null, null);
    protected ColumnInfo _columnLoginDatetime = cci("LOGIN_DATETIME", null, "loginDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnLoginMobileFlg = cci("LOGIN_MOBILE_FLG", null, "loginMobileFlg", Integer.class, false, null, null);
    protected ColumnInfo _columnLoginMemberStatusCode = cci("LOGIN_MEMBER_STATUS_CODE", null, "loginMemberStatusCode", String.class, false, 3, 0);

    public ColumnInfo columnMemberLoginId() { return _columnMemberLoginId; }
    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnLoginDatetime() { return _columnLoginDatetime; }
    public ColumnInfo columnLoginMobileFlg() { return _columnLoginMobileFlg; }
    public ColumnInfo columnLoginMemberStatusCode() { return _columnLoginMemberStatusCode; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnMemberLoginId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasTwoOrMorePrimaryKeys() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignMember() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberDbm.getInstance().columnMemberId());
        return cfi("member", this, MemberDbm.getInstance(), map, 0, false);
    }
    public ForeignInfo foreignMemberStatus() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnLoginMemberStatusCode(), MemberStatusDbm.getInstance().columnMemberStatusCode());
        return cfi("memberStatus", this, MemberStatusDbm.getInstance(), map, 1, false);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.MemberLogin"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.MemberLoginCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.MemberLoginDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.MemberLoginBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<MemberLogin> getEntityType() { return MemberLogin.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public MemberLogin newMyEntity() { return new MemberLogin(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((MemberLogin)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((MemberLogin)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<MemberLogin>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberLoginId(), columnMemberLoginId());
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsLoginDatetime(), columnLoginDatetime());
        setupEps(_epsMap, new EpsLoginMobileFlg(), columnLoginMobileFlg());
        setupEps(_epsMap, new EpsLoginMemberStatusCode(), columnLoginMemberStatusCode());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((MemberLogin)entity, value);
    }
    
    public static class EpsMemberLoginId implements Eps<MemberLogin> {
        public void setup(MemberLogin e, Object v) { e.setMemberLoginId((Long)v); }
    }
    public static class EpsMemberId implements Eps<MemberLogin> {
        public void setup(MemberLogin e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsLoginDatetime implements Eps<MemberLogin> {
        public void setup(MemberLogin e, Object v) { e.setLoginDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsLoginMobileFlg implements Eps<MemberLogin> {
        public void setup(MemberLogin e, Object v) { e.setLoginMobileFlg((Integer)v); }
    }
    public static class EpsLoginMemberStatusCode implements Eps<MemberLogin> {
        public void setup(MemberLogin e, Object v) { e.setLoginMemberStatusCode((String)v); }
    }
}
