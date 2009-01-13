package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.MemberStatus;

/**
 * The DB meta of MEMBER_STATUS. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class MemberStatusDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final MemberStatusDbm _instance = new MemberStatusDbm();
    private MemberStatusDbm() {}
    public static MemberStatusDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "MEMBER_STATUS"; }
    public String getTablePropertyName() { return "memberStatus"; }
    public String getTableSqlName() { return "MEMBER_STATUS"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberStatusCode = cci("MEMBER_STATUS_CODE", null, "memberStatusCode", String.class, true, 3, 0);
    protected ColumnInfo _columnMemberStatusName = cci("MEMBER_STATUS_NAME", null, "memberStatusName", String.class, false, 50, 0);
    protected ColumnInfo _columnDisplayOrder = cci("DISPLAY_ORDER", null, "displayOrder", Integer.class, false, null, null);

    public ColumnInfo columnMemberStatusCode() { return _columnMemberStatusCode; }
    public ColumnInfo columnMemberStatusName() { return _columnMemberStatusName; }
    public ColumnInfo columnDisplayOrder() { return _columnDisplayOrder; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnMemberStatusCode()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasTwoOrMorePrimaryKeys() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    public ReferrerInfo referrerMemberList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberStatusCode(), MemberDbm.getInstance().columnMemberStatusCode());
        return cri("memberList", this, MemberDbm.getInstance(), map, false);
    }
    public ReferrerInfo referrerMemberLoginList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnMemberStatusCode(), MemberLoginDbm.getInstance().columnLoginMemberStatusCode());
        return cri("memberLoginList", this, MemberLoginDbm.getInstance(), map, false);
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.MemberStatus"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.MemberStatusCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.MemberStatusDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.MemberStatusBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<MemberStatus> getEntityType() { return MemberStatus.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public MemberStatus newMyEntity() { return new MemberStatus(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((MemberStatus)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((MemberStatus)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<MemberStatus>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberStatusCode(), columnMemberStatusCode());
        setupEps(_epsMap, new EpsMemberStatusName(), columnMemberStatusName());
        setupEps(_epsMap, new EpsDisplayOrder(), columnDisplayOrder());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((MemberStatus)entity, value);
    }
    
    public static class EpsMemberStatusCode implements Eps<MemberStatus> {
        public void setup(MemberStatus e, Object v) { e.setMemberStatusCode((String)v); }
    }
    public static class EpsMemberStatusName implements Eps<MemberStatus> {
        public void setup(MemberStatus e, Object v) { e.setMemberStatusName((String)v); }
    }
    public static class EpsDisplayOrder implements Eps<MemberStatus> {
        public void setup(MemberStatus e, Object v) { e.setDisplayOrder((Integer)v); }
    }
}
