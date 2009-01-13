package com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.customize.SimpleVendorCheck;

/**
 * The DB meta of SimpleVendorCheck. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SimpleVendorCheckDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SimpleVendorCheckDbm _instance = new SimpleVendorCheckDbm();
    private SimpleVendorCheckDbm() {}
    public static SimpleVendorCheckDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "SimpleVendorCheck"; }
    public String getTablePropertyName() { return "simpleVendorCheck"; }
    public String getTableSqlName() { return "SimpleVendorCheck"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnVendorCheckId = cci("VENDOR_CHECK_ID", null, "vendorCheckId", Long.class, false, 16, 0);
    protected ColumnInfo _columnDecimalDigit = cci("DECIMAL_DIGIT", null, "decimalDigit", java.math.BigDecimal.class, false, 5, 3);
    protected ColumnInfo _columnIntegerNonDigit = cci("INTEGER_NON_DIGIT", null, "integerNonDigit", Integer.class, false, 5, 0);
    protected ColumnInfo _columnTypeOfBoolean = cci("TYPE_OF_BOOLEAN", null, "typeOfBoolean", Boolean.class, false, 1, 0);
    protected ColumnInfo _columnTypeOfText = cci("TYPE_OF_TEXT", null, "typeOfText", String.class, false, 2147483647, 0);

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
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.customize.SimpleVendorCheck"; }
    public String getConditionBeanTypeName() { return null; }
    public String getDaoTypeName() { return null; }
    public String getBehaviorTypeName() { return null; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<SimpleVendorCheck> getEntityType() { return SimpleVendorCheck.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public SimpleVendorCheck newMyEntity() { return new SimpleVendorCheck(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((SimpleVendorCheck)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((SimpleVendorCheck)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<SimpleVendorCheck>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
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
        findEps(_epsMap, propertyName).setup((SimpleVendorCheck)entity, value);
    }
    
    public static class EpsVendorCheckId implements Eps<SimpleVendorCheck> {
        public void setup(SimpleVendorCheck e, Object v) { e.setVendorCheckId((Long)v); }
    }
    public static class EpsDecimalDigit implements Eps<SimpleVendorCheck> {
        public void setup(SimpleVendorCheck e, Object v) { e.setDecimalDigit((java.math.BigDecimal)v); }
    }
    public static class EpsIntegerNonDigit implements Eps<SimpleVendorCheck> {
        public void setup(SimpleVendorCheck e, Object v) { e.setIntegerNonDigit((Integer)v); }
    }
    public static class EpsTypeOfBoolean implements Eps<SimpleVendorCheck> {
        public void setup(SimpleVendorCheck e, Object v) { e.setTypeOfBoolean((Boolean)v); }
    }
    public static class EpsTypeOfText implements Eps<SimpleVendorCheck> {
        public void setup(SimpleVendorCheck e, Object v) { e.setTypeOfText((String)v); }
    }
}
