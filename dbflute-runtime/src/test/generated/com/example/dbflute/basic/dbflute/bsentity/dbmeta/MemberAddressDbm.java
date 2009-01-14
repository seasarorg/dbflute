package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.MemberAddress;

/**
 * The DB meta of MEMBER_ADDRESS. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class MemberAddressDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final MemberAddressDbm _instance = new MemberAddressDbm();
    private MemberAddressDbm() {}
    public static MemberAddressDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "MEMBER_ADDRESS"; }
    public String getTablePropertyName() { return "memberAddress"; }
    public String getTableSqlName() { return "MEMBER_ADDRESS"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberAddressId = cci("MEMBER_ADDRESS_ID", null, "memberAddressId", Integer.class, true, null, null);
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, false, null, null);
    protected ColumnInfo _columnValidBeginDate = cci("VALID_BEGIN_DATE", null, "validBeginDate", java.util.Date.class, false, null, null);
    protected ColumnInfo _columnValidEndDate = cci("VALID_END_DATE", null, "validEndDate", java.util.Date.class, false, null, null);
    protected ColumnInfo _columnAddress = cci("ADDRESS", null, "address", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterDatetime = cci("REGISTER_DATETIME", null, "registerDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnRegisterProcess = cci("REGISTER_PROCESS", null, "registerProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterUser = cci("REGISTER_USER", null, "registerUser", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateDatetime = cci("UPDATE_DATETIME", null, "updateDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnUpdateProcess = cci("UPDATE_PROCESS", null, "updateProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateUser = cci("UPDATE_USER", null, "updateUser", String.class, false, 200, 0);
    protected ColumnInfo _columnVersionNo = cci("VERSION_NO", null, "versionNo", Long.class, false, null, null, OptimisticLockType.VERSION_NO);

    public ColumnInfo columnMemberAddressId() { return _columnMemberAddressId; }
    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnValidBeginDate() { return _columnValidBeginDate; }
    public ColumnInfo columnValidEndDate() { return _columnValidEndDate; }
    public ColumnInfo columnAddress() { return _columnAddress; }
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
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnMemberAddressId()); }
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
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.MemberAddress"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.MemberAddressCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.MemberAddressDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.MemberAddressBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<MemberAddress> getEntityType() { return MemberAddress.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public MemberAddress newMyEntity() { return new MemberAddress(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((MemberAddress)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((MemberAddress)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<MemberAddress>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberAddressId(), columnMemberAddressId());
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsValidBeginDate(), columnValidBeginDate());
        setupEps(_epsMap, new EpsValidEndDate(), columnValidEndDate());
        setupEps(_epsMap, new EpsAddress(), columnAddress());
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
        findEps(_epsMap, propertyName).setup((MemberAddress)entity, value);
    }
    
    public static class EpsMemberAddressId implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setMemberAddressId((Integer)v); }
    }
    public static class EpsMemberId implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsValidBeginDate implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setValidBeginDate((java.util.Date)v); }
    }
    public static class EpsValidEndDate implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setValidEndDate((java.util.Date)v); }
    }
    public static class EpsAddress implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setAddress((String)v); }
    }
    public static class EpsRegisterDatetime implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setRegisterDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsRegisterProcess implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setRegisterProcess((String)v); }
    }
    public static class EpsRegisterUser implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setRegisterUser((String)v); }
    }
    public static class EpsUpdateDatetime implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setUpdateDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsUpdateProcess implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setUpdateProcess((String)v); }
    }
    public static class EpsUpdateUser implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setUpdateUser((String)v); }
    }
    public static class EpsVersionNo implements Eps<MemberAddress> {
        public void setup(MemberAddress e, Object v) { e.setVersionNo((Long)v); }
    }
}
