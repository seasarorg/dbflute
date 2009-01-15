package com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;
import com.example.dbflute.basic.dbflute.exentity.customize.UnpaidSummaryMember;

/**
 * The DB meta of UnpaidSummaryMember. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UnpaidSummaryMemberDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UnpaidSummaryMemberDbm _instance = new UnpaidSummaryMemberDbm();
    private UnpaidSummaryMemberDbm() {}
    public static UnpaidSummaryMemberDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "UnpaidSummaryMember"; }
    public String getTablePropertyName() { return "unpaidSummaryMember"; }
    public String getTableSqlName() { return "UnpaidSummaryMember"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnMemberId = cci("MEMBER_ID", null, "memberId", Integer.class, false, 10, 0);
    protected ColumnInfo _columnMemberName = cci("MEMBER_NAME", null, "memberName", String.class, false, 200, 0);
    protected ColumnInfo _columnUnpaidPriceSummary = cci("UNPAID_PRICE_SUMMARY", null, "unpaidPriceSummary", Integer.class, false, 10, 0);
    protected ColumnInfo _columnMemberStatusName = cci("MEMBER_STATUS_NAME", null, "memberStatusName", String.class, false, 50, 0);

    public ColumnInfo columnMemberId() { return _columnMemberId; }
    public ColumnInfo columnMemberName() { return _columnMemberName; }
    public ColumnInfo columnUnpaidPriceSummary() { return _columnUnpaidPriceSummary; }
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
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.customize.UnpaidSummaryMember"; }
    public String getConditionBeanTypeName() { return null; }
    public String getDaoTypeName() { return null; }
    public String getBehaviorTypeName() { return null; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UnpaidSummaryMember> getEntityType() { return UnpaidSummaryMember.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public UnpaidSummaryMember newMyEntity() { return new UnpaidSummaryMember(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((UnpaidSummaryMember)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((UnpaidSummaryMember)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<UnpaidSummaryMember>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsMemberId(), columnMemberId());
        setupEps(_epsMap, new EpsMemberName(), columnMemberName());
        setupEps(_epsMap, new EpsUnpaidPriceSummary(), columnUnpaidPriceSummary());
        setupEps(_epsMap, new EpsMemberStatusName(), columnMemberStatusName());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((UnpaidSummaryMember)entity, value);
    }
    
    public static class EpsMemberId implements Eps<UnpaidSummaryMember> {
        public void setup(UnpaidSummaryMember e, Object v) { e.setMemberId((Integer)v); }
    }
    public static class EpsMemberName implements Eps<UnpaidSummaryMember> {
        public void setup(UnpaidSummaryMember e, Object v) { e.setMemberName((String)v); }
    }
    public static class EpsUnpaidPriceSummary implements Eps<UnpaidSummaryMember> {
        public void setup(UnpaidSummaryMember e, Object v) { e.setUnpaidPriceSummary((Integer)v); }
    }
    public static class EpsMemberStatusName implements Eps<UnpaidSummaryMember> {
        public void setup(UnpaidSummaryMember e, Object v) { e.setMemberStatusName((String)v); }
    }
}
