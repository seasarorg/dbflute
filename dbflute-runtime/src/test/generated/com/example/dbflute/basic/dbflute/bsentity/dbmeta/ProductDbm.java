package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;
import com.example.dbflute.basic.dbflute.exentity.Product;

/**
 * The DB meta of PRODUCT. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class ProductDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ProductDbm _instance = new ProductDbm();
    private ProductDbm() {}
    public static ProductDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "PRODUCT"; }
    public String getTablePropertyName() { return "product"; }
    public String getTableSqlName() { return "PRODUCT"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnProductId = cci("PRODUCT_ID", null, "productId", Integer.class, true, null, null);
    protected ColumnInfo _columnProductName = cci("PRODUCT_NAME", null, "productName", String.class, false, 50, 0);
    protected ColumnInfo _columnProductHandleCode = cci("PRODUCT_HANDLE_CODE", null, "productHandleCode", String.class, false, 100, 0);
    protected ColumnInfo _columnProductStatusCode = cci("PRODUCT_STATUS_CODE", null, "productStatusCode", String.class, false, 3, 0);
    protected ColumnInfo _columnRegisterDatetime = cci("REGISTER_DATETIME", null, "registerDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnRegisterUser = cci("REGISTER_USER", null, "registerUser", String.class, false, 200, 0);
    protected ColumnInfo _columnRegisterProcess = cci("REGISTER_PROCESS", null, "registerProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateDatetime = cci("UPDATE_DATETIME", null, "updateDatetime", java.sql.Timestamp.class, false, null, null);
    protected ColumnInfo _columnUpdateUser = cci("UPDATE_USER", null, "updateUser", String.class, false, 200, 0);
    protected ColumnInfo _columnUpdateProcess = cci("UPDATE_PROCESS", null, "updateProcess", String.class, false, 200, 0);
    protected ColumnInfo _columnVersionNo = cci("VERSION_NO", null, "versionNo", Long.class, false, null, null, OptimisticLockType.VERSION_NO);

    public ColumnInfo columnProductId() { return _columnProductId; }
    public ColumnInfo columnProductName() { return _columnProductName; }
    public ColumnInfo columnProductHandleCode() { return _columnProductHandleCode; }
    public ColumnInfo columnProductStatusCode() { return _columnProductStatusCode; }
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
    public boolean hasIdentity() { return true; }
    public boolean hasVersionNo() { return true; }
    public ColumnInfo getVersionNoColumnInfo() { return _columnVersionNo; }
    public boolean hasCommonColumn() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.Product"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.ProductCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.ProductDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.ProductBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<Product> getEntityType() { return Product.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public Product newMyEntity() { return new Product(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((Product)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((Product)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<Product>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsProductId(), columnProductId());
        setupEps(_epsMap, new EpsProductName(), columnProductName());
        setupEps(_epsMap, new EpsProductHandleCode(), columnProductHandleCode());
        setupEps(_epsMap, new EpsProductStatusCode(), columnProductStatusCode());
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
        findEps(_epsMap, propertyName).setup((Product)entity, value);
    }
    
    public static class EpsProductId implements Eps<Product> {
        public void setup(Product e, Object v) { e.setProductId((Integer)v); }
    }
    public static class EpsProductName implements Eps<Product> {
        public void setup(Product e, Object v) { e.setProductName((String)v); }
    }
    public static class EpsProductHandleCode implements Eps<Product> {
        public void setup(Product e, Object v) { e.setProductHandleCode((String)v); }
    }
    public static class EpsProductStatusCode implements Eps<Product> {
        public void setup(Product e, Object v) { e.setProductStatusCode((String)v); }
    }
    public static class EpsRegisterDatetime implements Eps<Product> {
        public void setup(Product e, Object v) { e.setRegisterDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsRegisterUser implements Eps<Product> {
        public void setup(Product e, Object v) { e.setRegisterUser((String)v); }
    }
    public static class EpsRegisterProcess implements Eps<Product> {
        public void setup(Product e, Object v) { e.setRegisterProcess((String)v); }
    }
    public static class EpsUpdateDatetime implements Eps<Product> {
        public void setup(Product e, Object v) { e.setUpdateDatetime((java.sql.Timestamp)v); }
    }
    public static class EpsUpdateUser implements Eps<Product> {
        public void setup(Product e, Object v) { e.setUpdateUser((String)v); }
    }
    public static class EpsUpdateProcess implements Eps<Product> {
        public void setup(Product e, Object v) { e.setUpdateProcess((String)v); }
    }
    public static class EpsVersionNo implements Eps<Product> {
        public void setup(Product e, Object v) { e.setVersionNo((Long)v); }
    }
}
