package com.example.dbflute.basic.dbflute.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.helper.StringKeyMap;

import com.example.dbflute.basic.dbflute.exentity.WithdrawalReason;

/**
 * The DB meta of WITHDRAWAL_REASON. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class WithdrawalReasonDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WithdrawalReasonDbm _instance = new WithdrawalReasonDbm();
    private WithdrawalReasonDbm() {}
    public static WithdrawalReasonDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() { return "WITHDRAWAL_REASON"; }
    public String getTablePropertyName() { return "withdrawalReason"; }
    public String getTableSqlName() { return "WITHDRAWAL_REASON"; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnWithdrawalReasonCode = cci("WITHDRAWAL_REASON_CODE", null, "withdrawalReasonCode", String.class, true, 3, 0);
    protected ColumnInfo _columnWithdrawalReasonText = cci("WITHDRAWAL_REASON_TEXT", null, "withdrawalReasonText", String.class, false, null, null);
    protected ColumnInfo _columnDisplayOrder = cci("DISPLAY_ORDER", null, "displayOrder", Integer.class, false, null, null);

    public ColumnInfo columnWithdrawalReasonCode() { return _columnWithdrawalReasonCode; }
    public ColumnInfo columnWithdrawalReasonText() { return _columnWithdrawalReasonText; }
    public ColumnInfo columnDisplayOrder() { return _columnDisplayOrder; }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() { return cpui(columnWithdrawalReasonCode()); }
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
    public ReferrerInfo referrerMemberWithdrawalList() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnWithdrawalReasonCode(), MemberWithdrawalDbm.getInstance().columnWithdrawalReasonCode());
        return cri("memberWithdrawalList", this, MemberWithdrawalDbm.getInstance(), map, false);
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "com.example.dbflute.basic.dbflute.exentity.WithdrawalReason"; }
    public String getConditionBeanTypeName() { return "com.example.dbflute.basic.dbflute.cbean.bs.WithdrawalReasonCB"; }
    public String getDaoTypeName() { return "com.example.dbflute.basic.dbflute.exdao.WithdrawalReasonDao"; }
    public String getBehaviorTypeName() { return "com.example.dbflute.basic.dbflute.exbhv.WithdrawalReasonBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<WithdrawalReason> getEntityType() { return WithdrawalReason.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public WithdrawalReason newMyEntity() { return new WithdrawalReason(); }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((WithdrawalReason)entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((WithdrawalReason)entity, columnValueMap, _epsMap);
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
    protected Map<String, Eps<WithdrawalReason>> _epsMap = StringKeyMap.createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsWithdrawalReasonCode(), columnWithdrawalReasonCode());
        setupEps(_epsMap, new EpsWithdrawalReasonText(), columnWithdrawalReasonText());
        setupEps(_epsMap, new EpsDisplayOrder(), columnDisplayOrder());
    }
    
    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
        findEps(_epsMap, propertyName).setup((WithdrawalReason)entity, value);
    }
    
    public static class EpsWithdrawalReasonCode implements Eps<WithdrawalReason> {
        public void setup(WithdrawalReason e, Object v) { e.setWithdrawalReasonCode((String)v); }
    }
    public static class EpsWithdrawalReasonText implements Eps<WithdrawalReason> {
        public void setup(WithdrawalReason e, Object v) { e.setWithdrawalReasonText((String)v); }
    }
    public static class EpsDisplayOrder implements Eps<WithdrawalReason> {
        public void setup(WithdrawalReason e, Object v) { e.setDisplayOrder((Integer)v); }
    }
}
