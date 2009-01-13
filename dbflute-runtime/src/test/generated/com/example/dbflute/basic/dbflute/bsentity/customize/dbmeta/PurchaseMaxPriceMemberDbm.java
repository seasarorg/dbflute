package com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.customize.PurchaseMaxPriceMember;

/**
 * The DB meta of PurchaseMaxPriceMember. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class PurchaseMaxPriceMemberDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final PurchaseMaxPriceMemberDbm _instance = new PurchaseMaxPriceMemberDbm();
    private PurchaseMaxPriceMemberDbm() {}
    public static PurchaseMaxPriceMemberDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "PurchaseMaxPriceMember"; }
    public String getTablePropertyName() { return "purchaseMaxPriceMember"; }
    public String getTableSqlName() { return "PurchaseMaxPriceMember"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, false, 10, 0);
    protected ColumnInfo _columnMemberName = cci("MEMBER_NAME", null, "memberName", String.class, false, 200, 0);
    protected ColumnInfo _columnPurchaseMaxPrice = cci("PURCHASE_MAX_PRICE", null, "purchaseMaxPrice", Integer.class, false, 10, 0);
    protected ColumnInfo _columnMemberStatusName = cci("MEMBER_STATUS_NAME", null, "memberStatusName", String.class, false, 50, 0);

    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnMemberName() { return _columnMemberName; }
    public ColumnInfo columnPurchaseMaxPrice() { return _columnPurchaseMaxPrice; }
    public ColumnInfo columnMemberStatusName() { return _columnMemberStatusName; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() {
        throw new UnsupportedOperationException("The table does not have primary key: " + getTableDbName());
    }
    public boolean hasPrimaryKey() { return false; }
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

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.customize.PurchaseMaxPriceMember"; }
    public String getConditionBeanTypeName() { return null; }
    public String getDaoTypeName() { return null; }
    public String getBehaviorTypeName() { return null; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<PurchaseMaxPriceMember> getEntityType() { return PurchaseMaxPriceMember.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public PurchaseMaxPriceMember newMyEntity() { return new PurchaseMaxPriceMember(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((PurchaseMaxPriceMember)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((PurchaseMaxPriceMember)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<PurchaseMaxPriceMember>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsMemberName(), columnMemberName());
        setupEps(_epsMap, new EpsPurchaseMaxPrice(), columnPurchaseMaxPrice());
        setupEps(_epsMap, new EpsMemberStatusName(), columnMemberStatusName());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((PurchaseMaxPriceMember)entity, value);
    }
    
    public static class EpsMemberId implements Eps<PurchaseMaxPriceMember> {
        public void setup(PurchaseMaxPriceMember e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsMemberName implements Eps<PurchaseMaxPriceMember> {
        public void setup(PurchaseMaxPriceMember e, Object v) { e.setMemberName((String)v); }
    }
    public static class EpsPurchaseMaxPrice implements Eps<PurchaseMaxPriceMember> {
        public void setup(PurchaseMaxPriceMember e, Object v) { e.setPurchaseMaxPrice((Integer)v); }
    }
    public static class EpsMemberStatusName implements Eps<PurchaseMaxPriceMember> {
        public void setup(PurchaseMaxPriceMember e, Object v) { e.setMemberStatusName((String)v); }
    }
}
