package com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;
import com.example.dbflute.basic.dbflute.exentity.customize.VendorCheckIntegerSum;

/**
 * The DB meta of VendorCheckIntegerSum. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class VendorCheckIntegerSumDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final VendorCheckIntegerSumDbm _instance = new VendorCheckIntegerSumDbm();
    private VendorCheckIntegerSumDbm() {}
    public static VendorCheckIntegerSumDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "VendorCheckIntegerSum"; }
    public String getTablePropertyName() { return "vendorCheckIntegerSum"; }
    public String getTableSqlName() { return "VendorCheckIntegerSum"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnIntegerNonDigitSum = cci("INTEGER_NON_DIGIT_SUM", null, "integerNonDigitSum", Integer.class, false, 5, 0);

    public ColumnInfo columnIntegerNonDigitSum() { return _columnIntegerNonDigitSum; }

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
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.customize.VendorCheckIntegerSum"; }
    public String getConditionBeanTypeName() { return null; }
    public String getDaoTypeName() { return null; }
    public String getBehaviorTypeName() { return null; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<VendorCheckIntegerSum> getEntityType() { return VendorCheckIntegerSum.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public VendorCheckIntegerSum newMyEntity() { return new VendorCheckIntegerSum(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((VendorCheckIntegerSum)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((VendorCheckIntegerSum)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<VendorCheckIntegerSum>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsIntegerNonDigitSum(), columnIntegerNonDigitSum());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((VendorCheckIntegerSum)entity, value);
    }
    
    public static class EpsIntegerNonDigitSum implements Eps<VendorCheckIntegerSum> {
        public void setup(VendorCheckIntegerSum e, Object v) { e.setIntegerNonDigitSum((Integer)v); }
    }
}
