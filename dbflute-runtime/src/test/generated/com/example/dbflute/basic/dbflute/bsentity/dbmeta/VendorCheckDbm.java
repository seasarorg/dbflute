package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.VendorCheck;

/**
 * The DB meta of VENDOR_CHECK. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class VendorCheckDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final VendorCheckDbm _instance = new VendorCheckDbm();
    private VendorCheckDbm() {}
    public static VendorCheckDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "VENDOR_CHECK"; }
    public String getTablePropertyName() { return "vendorCheck"; }
    public String getTableSqlName() { return "VENDOR_CHECK"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnVendorCheckId = cci("VENDOR_CHECK_ID", null, "vendorCheckId", Long.class, true, 16, 0);
    protected ColumnInfo _columnDecimalDigit = cci("DECIMAL_DIGIT", null, "decimalDigit", java.math.BigDecimal.class, false, 5, 3);
    protected ColumnInfo _columnIntegerNonDigit = cci("INTEGER_NON_DIGIT", null, "integerNonDigit", Integer.class, false, 5, 0);
    protected ColumnInfo _columnTypeOfBoolean = cci("TYPE_OF_BOOLEAN", null, "typeOfBoolean", Boolean.class, false, null, null);
    protected ColumnInfo _columnTypeOfText = cci("TYPE_OF_TEXT", null, "typeOfText", String.class, false, null, null);

    public ColumnInfo columnVendorCheckId() { return _columnVendorCheckId; }
    public ColumnInfo columnDecimalDigit() { return _columnDecimalDigit; }
    public ColumnInfo columnIntegerNonDigit() { return _columnIntegerNonDigit; }
    public ColumnInfo columnTypeOfBoolean() { return _columnTypeOfBoolean; }
    public ColumnInfo columnTypeOfText() { return _columnTypeOfText; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnVendorCheckId()); }
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

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.VendorCheck"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.VendorCheckCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.VendorCheckDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.VendorCheckBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<VendorCheck> getEntityType() { return VendorCheck.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public VendorCheck newMyEntity() { return new VendorCheck(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((VendorCheck)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((VendorCheck)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<VendorCheck>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsVendorCheckId(), columnVendorCheckId());
        setupEps(_epsMap, new EpsDecimalDigit(), columnDecimalDigit());
        setupEps(_epsMap, new EpsIntegerNonDigit(), columnIntegerNonDigit());
        setupEps(_epsMap, new EpsTypeOfBoolean(), columnTypeOfBoolean());
        setupEps(_epsMap, new EpsTypeOfText(), columnTypeOfText());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((VendorCheck)entity, value);
    }
    
    public static class EpsVendorCheckId implements Eps<VendorCheck> {
        public void setup(VendorCheck e, Object v) { e.setVendorCheckId((Long)v); }
    }
    public static class EpsDecimalDigit implements Eps<VendorCheck> {
        public void setup(VendorCheck e, Object v) { e.setDecimalDigit((java.math.BigDecimal)v); }
    }
    public static class EpsIntegerNonDigit implements Eps<VendorCheck> {
        public void setup(VendorCheck e, Object v) { e.setIntegerNonDigit((Integer)v); }
    }
    public static class EpsTypeOfBoolean implements Eps<VendorCheck> {
        public void setup(VendorCheck e, Object v) { e.setTypeOfBoolean((Boolean)v); }
    }
    public static class EpsTypeOfText implements Eps<VendorCheck> {
        public void setup(VendorCheck e, Object v) { e.setTypeOfText((String)v); }
    }
}
