package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.SummaryProduct;

/**
 * The DB meta of SUMMARY_PRODUCT. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SummaryProductDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SummaryProductDbm _instance = new SummaryProductDbm();
    private SummaryProductDbm() {}
    public static SummaryProductDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "SUMMARY_PRODUCT"; }
    public String getTablePropertyName() { return "summaryProduct"; }
    public String getTableSqlName() { return "SUMMARY_PRODUCT"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnProductId = cci("PRODUCT_ID", null, "productId", Integer.class, true, null, null);
    protected ColumnInfo _columnProductName = cci("PRODUCT_NAME", null, "productName", String.class, false, 50, 0);
    protected ColumnInfo _columnProductStatusCode = cci("PRODUCT_STATUS_CODE", null, "productStatusCode", String.class, false, 3, 0);
    protected ColumnInfo _columnLatestPurchaseDatetime = cci("LATEST_PURCHASE_DATETIME", null, "latestPurchaseDatetime", java.sql.Timestamp.class, false, null, null);

    public ColumnInfo columnProductId() { return _columnProductId; }
    public ColumnInfo columnProductName() { return _columnProductName; }
    public ColumnInfo columnProductStatusCode() { return _columnProductStatusCode; }
    public ColumnInfo columnLatestPurchaseDatetime() { return _columnLatestPurchaseDatetime; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnProductId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasTwoOrMorePrimaryKeys() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignProductStatus() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnProductStatusCode(), ProductStatusDbm.getInstance().columnProductStatusCode());
        return cfi("productStatus", this, ProductStatusDbm.getInstance(), map, 0, false);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    public ReferrerInfo referrerPurchaseList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnProductId(), PurchaseDbm.getInstance().columnProductId());
        return cri("purchaseList", this, PurchaseDbm.getInstance(), map, false);
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.SummaryProduct"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.SummaryProductCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.SummaryProductDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.SummaryProductBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<SummaryProduct> getEntityType() { return SummaryProduct.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public SummaryProduct newMyEntity() { return new SummaryProduct(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((SummaryProduct)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((SummaryProduct)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<SummaryProduct>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsProductId(), columnProductId());
        setupEps(_epsMap, new EpsProductName(), columnProductName());
        setupEps(_epsMap, new EpsProductStatusCode(), columnProductStatusCode());
        setupEps(_epsMap, new EpsLatestPurchaseDatetime(), columnLatestPurchaseDatetime());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((SummaryProduct)entity, value);
    }
    
    public static class EpsProductId implements Eps<SummaryProduct> {
        public void setup(SummaryProduct e, Object v) { e.setProductId((Integer)v); }
    }
    public static class EpsProductName implements Eps<SummaryProduct> {
        public void setup(SummaryProduct e, Object v) { e.setProductName((String)v); }
    }
    public static class EpsProductStatusCode implements Eps<SummaryProduct> {
        public void setup(SummaryProduct e, Object v) { e.setProductStatusCode((String)v); }
    }
    public static class EpsLatestPurchaseDatetime implements Eps<SummaryProduct> {
        public void setup(SummaryProduct e, Object v) { e.setLatestPurchaseDatetime((java.sql.Timestamp)v); }
    }
}
