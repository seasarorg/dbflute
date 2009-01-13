package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.MemberSecurity;

/**
 * The DB meta of MEMBER_SECURITY. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class MemberSecurityDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final MemberSecurityDbm _instance = new MemberSecurityDbm();
    private MemberSecurityDbm() {}
    public static MemberSecurityDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "MEMBER_SECURITY"; }
    public String getTablePropertyName() { return "memberSecurity"; }
    public String getTableSqlName() { return "MEMBER_SECURITY"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, true, null, null);
    protected ColumnInfo _columnLoginPassword = cci("LOGIN_PASSWORD", null, "loginPassword", String.class, false, 50, 0);
    protected ColumnInfo _columnReminderQuestion = cci("REMINDER_QUESTION", null, "reminderQuestion", String.class, false, 50, 0);
    protected ColumnInfo _columnReminderAnswer = cci("REMINDER_ANSWER", null, "reminderAnswer", String.class, false, 50, 0);
    protected ColumnInfo _columnRegisterDatetime = cci("REGISTER_DATETIME", null, "registerDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnRegisterProcess = cci("REGISTER_PROCESS", null, "registerProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterUser = cci("REGISTER_USER", null, "registerUser", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateDatetime = cci("UPDATE_DATETIME", null, "updateDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnUpdateProcess = cci("UPDATE_PROCESS", null, "updateProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateUser = cci("UPDATE_USER", null, "updateUser", String.class, false, 200, 0);
    protected ColumnInfo _columnVersionNo = cci("VERSION_NO", null, "versionNo", Long.class, false, null, null, OptimisticLockType.VERSION_NO);

    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnLoginPassword() { return _columnLoginPassword; }
    public ColumnInfo columnReminderQuestion() { return _columnReminderQuestion; }
    public ColumnInfo columnReminderAnswer() { return _columnReminderAnswer; }
    public ColumnInfo columnRegisterDatetime() { return _columnRegisterDatetime; }
    public ColumnInfo columnRegisterProcess() { return _columnRegisterProcess; }
    public ColumnInfo columnRegisterUser() { return _columnRegisterUser; }
    public ColumnInfo columnUpdateDatetime() { return _columnUpdateDatetime; }
    public ColumnInfo columnUpdateProcess() { return _columnUpdateProcess; }
    public ColumnInfo columnUpdateUser() { return _columnUpdateUser; }
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
    public ForeignInfo foreignMember() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberId(), MemberDbm.getInstance().columnMemberId());
        return cfi("member", this, MemberDbm.getInstance(), map, 0, true);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasVersionNo() { return true; }
    public ColumnInfo getVersionNoColumnInfo() { return _columnVersionNo; }
    public boolean hasCommonColumn() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.MemberSecurity"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.MemberSecurityCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.MemberSecurityDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.MemberSecurityBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<MemberSecurity> getEntityType() { return MemberSecurity.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public MemberSecurity newMyEntity() { return new MemberSecurity(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((MemberSecurity)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((MemberSecurity)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<MemberSecurity>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsLoginPassword(), columnLoginPassword());
        setupEps(_epsMap, new EpsReminderQuestion(), columnReminderQuestion());
        setupEps(_epsMap, new EpsReminderAnswer(), columnReminderAnswer());
        setupEps(_epsMap, new EpsRegisterDatetime(), columnRegisterDatetime());
        setupEps(_epsMap, new EpsRegisterProcess(), columnRegisterProcess());
        setupEps(_epsMap, new EpsRegisterUser(), columnRegisterUser());
        setupEps(_epsMap, new EpsUpdateDatetime(), columnUpdateDatetime());
        setupEps(_epsMap, new EpsUpdateProcess(), columnUpdateProcess());
        setupEps(_epsMap, new EpsUpdateUser(), columnUpdateUser());
        setupEps(_epsMap, new EpsVersionNo(), columnVersionNo());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((MemberSecurity)entity, value);
    }
    
    public static class EpsMemberId implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsLoginPassword implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setLoginPassword((String)v); }
    }
    public static class EpsReminderQuestion implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setReminderQuestion((String)v); }
    }
    public static class EpsReminderAnswer implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setReminderAnswer((String)v); }
    }
    public static class EpsRegisterDatetime implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setRegisterDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsRegisterProcess implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setRegisterProcess((String)v); }
    }
    public static class EpsRegisterUser implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setRegisterUser((String)v); }
    }
    public static class EpsUpdateDatetime implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setUpdateDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsUpdateProcess implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setUpdateProcess((String)v); }
    }
    public static class EpsUpdateUser implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setUpdateUser((String)v); }
    }
    public static class EpsVersionNo implements Eps<MemberSecurity> {
        public void setup(MemberSecurity e, Object v) { e.setVersionNo((Long)v); }
    }
}
