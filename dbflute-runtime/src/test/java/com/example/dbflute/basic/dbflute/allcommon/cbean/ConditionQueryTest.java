package com.example.dbflute.basic.dbflute.allcommon.cbean;


import org.dbflute.cbean.cvalue.ConditionValue;

import com.example.dbflute.basic.dbflute.bsentity.dbmeta.MemberDbm;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.MemberStatusDbm;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.MemberWithdrawalDbm;
import com.example.dbflute.basic.dbflute.bsentity.dbmeta.WithdrawalReasonDbm;
import com.example.dbflute.basic.dbflute.cbean.MemberCB;
import com.example.dbflute.basic.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/26 Tuesday)
 */
public class ConditionQueryTest extends PlainTestCase {

    public void test_invokeQuery_by_propertyName() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String propertyName = MemberDbm.getInstance().columnMemberName().getPropertyName();
        
        // ## Act ##
        cb.query().invokeQuery(propertyName, "Equal", "testValue");

        // ## Assert ##
        final ConditionValue value = cb.query().getMemberName();
        log("conditionValue=" + value);
        assertEquals("testValue", value.getEqual());
    }
    
    public void test_invokeQuery_by_columnName() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String propertyName = MemberDbm.getInstance().columnMemberName().getColumnDbName();
        
        // ## Act ##
        cb.query().invokeQuery(propertyName, "Equal", "testValue");
        
        // ## Assert ##
        final ConditionValue value = cb.query().getMemberName();
        log("conditionValue=" + value);
        assertEquals("testValue", value.getEqual());
    }
    
    public void test_invokeQuery_resolveRelation() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String foreingPropertyName = MemberDbm.getInstance().foreignMemberStatus().getForeignPropertyName();
        final String propertyName = MemberStatusDbm.getInstance().columnMemberStatusName().getPropertyName();
        
        // ## Act ##
        cb.query().invokeQuery(foreingPropertyName + "." + propertyName, "Equal", "testValue");
        
        // ## Assert ##
        final ConditionValue value = cb.query().queryMemberStatus().getMemberStatusName();
        log("conditionValue=" + value);
        assertEquals("testValue", value.getEqual());
    }
    
    public void test_invokeQuery_resolveNestedRelation() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String foreingPropertyName1 = MemberDbm.getInstance().foreignMemberWithdrawalAsOne().getForeignPropertyName();
        final String foreingPropertyName2 = MemberWithdrawalDbm.getInstance().foreignWithdrawalReason().getForeignPropertyName();
        final String propertyName = WithdrawalReasonDbm.getInstance().columnWithdrawalReasonText().getPropertyName();
        final String targetName = foreingPropertyName1 + "." + foreingPropertyName2 + "." + propertyName;
        
        // ## Act ##
        cb.query().invokeQuery(targetName, "Equal", "testValue");
        
        // ## Assert ##
        final ConditionValue value = cb.query().queryMemberWithdrawalAsOne().queryWithdrawalReason().getWithdrawalReasonText();
        log("conditionValue=" + value);
        assertEquals("testValue", value.getEqual());
    }

    public void test_invokeOrderBy_by_propertyName() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String propertyName = MemberDbm.getInstance().columnMemberName().getPropertyName();
        final String columnName = MemberDbm.getInstance().columnMemberName().getColumnDbName();
        
        // ## Act ##
        cb.query().invokeOrderBy(propertyName, true);

        // ## Assert ##
        final String orderByClause = cb.query().getSqlClause().getOrderByClause();
        log("orderByClause=" + orderByClause);
        assertTrue(orderByClause.contains(columnName));
        assertTrue(orderByClause.contains("asc"));
    }
    
    public void test_invokeOrderBy_by_columnName() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String columnName = MemberDbm.getInstance().columnMemberName().getColumnDbName();
        
        // ## Act ##
        cb.query().invokeOrderBy(columnName, true);
        
        // ## Assert ##
        final String orderByClause = cb.query().getSqlClause().getOrderByClause();
        log("orderByClause=" + orderByClause);
        assertTrue(orderByClause.contains(columnName));
        assertTrue(orderByClause.contains("asc"));
    }
    
    public void test_invokeOrderBy_resolveRelation() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String foreingPropertyName = MemberDbm.getInstance().foreignMemberStatus().getForeignPropertyName();
        final String propertyName = MemberStatusDbm.getInstance().columnMemberStatusName().getPropertyName();
        final String columnName = MemberStatusDbm.getInstance().columnMemberStatusName().getColumnDbName();
        
        // ## Act ##
        cb.query().invokeOrderBy(foreingPropertyName + "." + propertyName, false);
        
        // ## Assert ##
        final String orderByClause = cb.query().getSqlClause().getOrderByClause();
        log("orderByClause=" + orderByClause);
        assertTrue(orderByClause.contains(columnName));
        assertTrue(orderByClause.contains("desc"));
    }
    
    public void test_invokeOrderBy_resolveNestedRelation() {
        // ## Arrange ##
        final MemberCB cb = new MemberCB();
        final String foreingPropertyName1 = MemberDbm.getInstance().foreignMemberWithdrawalAsOne().getForeignPropertyName();
        final String foreingPropertyName2 = MemberWithdrawalDbm.getInstance().foreignWithdrawalReason().getForeignPropertyName();
        final String propertyName = WithdrawalReasonDbm.getInstance().columnWithdrawalReasonText().getPropertyName();
        final String targetName = foreingPropertyName1 + "." + foreingPropertyName2 + "." + propertyName;
        final String columnName = WithdrawalReasonDbm.getInstance().columnWithdrawalReasonText().getColumnDbName();
        
        // ## Act ##
        cb.query().invokeOrderBy(targetName, false);
        
        // ## Assert ##
        final String orderByClause = cb.query().getSqlClause().getOrderByClause();
        log("orderByClause=" + orderByClause);
        assertTrue(orderByClause.contains(columnName));
        assertTrue(orderByClause.contains("desc"));
    }
}
