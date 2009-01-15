package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;
import com.example.dbflute.basic.dbflute.exentity.Purchase;

/**
 * The DB meta of PURCHASE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class PurchaseDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final PurchaseDbm _instance = new PurchaseDbm();
    private PurchaseDbm() {}
    public static PurchaseDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "PURCHASE"; }
    public String getTablePropertyName() { return "purchase"; }
    public String getTableSqlName() { return "PURCHASE"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnPurchaseId = cci("PURCHASE_ID", null, "purchaseId", Long.class, true, null, null);
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, false, null, null);
    protected ColumnInfo _columnProductId = cci("PRODUCT_ID", null, "productId", Integer.class, false, null, null);
    protected ColumnInfo _columnPurchaseDatetime = cci("PURCHASE_DATETIME", null, "purchaseDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnPurchaseCount = cci("PURCHASE_COUNT", null, "purchaseCount", Integer.class, false, null, null);
    protected ColumnInfo _columnPurchasePrice = cci("PURCHASE_PRICE", null, "purchasePrice", Integer.class, false, null, null);
    protected ColumnInfo _columnPaymentCompleteFlg = cci("PAYMENT_COMPLETE_FLG", null, "paymentCompleteFlg", Integer.class, false, null, null);
    protected ColumnInfo _columnRegisterDatetime = cci("REGISTER_DATETIME", null, "registerDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnRegisterUser = cci("REGISTER_USER", null, "registerUser", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterProcess = cci("REGISTER_PROCESS", null, "registerProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateDatetime = cci("UPDATE_DATETIME", null, "updateDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnUpdateUser = cci("UPDATE_USER", null, "updateUser", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateProcess = cci("UPDATE_PROCESS", null, "updateProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnVersionNo = cci("VERSION_NO", null, "versionNo", Long.class, false, null, null, OptimisticLockType.VERSION_NO);

    public ColumnInfo columnPurchaseId() { return _columnPurchaseId; }
    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnProductId() { return _columnProductId; }
    public ColumnInfo columnPurchaseDatetime() { return _columnPurchaseDatetime; }
    public ColumnInfo columnPurchaseCount() { return _columnPurchaseCount; }
    public ColumnInfo columnPurchasePrice() { return _columnPurchasePrice; }
    public ColumnInfo columnPaymentCompleteFlg() { return _columnPaymentCompleteFlg; }
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
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnPurchaseId()); }
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
    public ForeignInfo foreignProduct() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnProductId(), ProductDbm.getInstance().columnProductId());
        return cfi("product", this, ProductDbm.getInstance(), map, 1, false);
    }
    public ForeignInfo foreignSummaryProduct() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnProductId(), SummaryProductDbm.getInstance().columnProductId());
        return cfi("summaryProduct", this, SummaryProductDbm.getInstance(), map, 2, false);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

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
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.Purchase"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.PurchaseCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.PurchaseDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.PurchaseBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<Purchase> getEntityType() { return Purchase.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public Purchase newMyEntity() { return new Purchase(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((Purchase)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((Purchase)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<Purchase>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsPurchaseId(), columnPurchaseId());
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsProductId(), columnProductId());
        setupEps(_epsMap, new EpsPurchaseDatetime(), columnPurchaseDatetime());
        setupEps(_epsMap, new EpsPurchaseCount(), columnPurchaseCount());
        setupEps(_epsMap, new EpsPurchasePrice(), columnPurchasePrice());
        setupEps(_epsMap, new EpsPaymentCompleteFlg(), columnPaymentCompleteFlg());
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
        findEps(_epsMap, propertyName).setup((Purchase)entity, value);
    }
    
    public static class EpsPurchaseId implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setPurchaseId((Long)v); }
    }
    public static class EpsMemberId implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsProductId implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setProductId((Integer)v); }
    }
    public static class EpsPurchaseDatetime implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setPurchaseDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsPurchaseCount implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setPurchaseCount((Integer)v); }
    }
    public static class EpsPurchasePrice implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setPurchasePrice((Integer)v); }
    }
    public static class EpsPaymentCompleteFlg implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setPaymentCompleteFlg((Integer)v); }
    }
    public static class EpsRegisterDatetime implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setRegisterDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsRegisterUser implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setRegisterUser((String)v); }
    }
    public static class EpsRegisterProcess implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setRegisterProcess((String)v); }
    }
    public static class EpsUpdateDatetime implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setUpdateDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsUpdateUser implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setUpdateUser((String)v); }
    }
    public static class EpsUpdateProcess implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setUpdateProcess((String)v); }
    }
    public static class EpsVersionNo implements Eps<Purchase> {
        public void setup(Purchase e, Object v) { e.setVersionNo((Long)v); }
    }
}
