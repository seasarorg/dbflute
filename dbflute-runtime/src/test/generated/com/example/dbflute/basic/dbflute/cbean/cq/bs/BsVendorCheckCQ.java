package com.example.dbflute.basic.dbflute.cbean.cq.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.*;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import com.example.dbflute.basic.dbflute.cbean.cq.ciq.*;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;

/**
 * The base condition-query of VENDOR_CHECK.
 * @author DBFlute(AutoGenerator)
 */
public class BsVendorCheckCQ extends AbstractBsVendorCheckCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected VendorCheckCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsVendorCheckCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                              Inline
    //                                                                              ======
    /**
     * Prepare inline query. <br />
     * {select ... from ... left outer join (select * from VENDOR_CHECK) where abc = [abc] ...}
     * @return Inline query. (NotNull)
     */
    public VendorCheckCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = new VendorCheckCIQ(getChildQuery(), getSqlClause(), getAliasName(), getNestLevel(), this);
        }
        _inlineQuery.xsetOnClauseInline(false); return _inlineQuery;
    }
    
    /**
     * Prepare on-clause query. <br />
     * {select ... from ... left outer join VENDOR_CHECK on ... and abc = [abc] ...}
     * @return On-clause query. (NotNull)
     */
    public VendorCheckCIQ on() {
        if (isBaseQuery(this)) { throw new UnsupportedOperationException("Unsupported on-clause for local table!"); }
        VendorCheckCIQ inlineQuery = inline(); inlineQuery.xsetOnClauseInline(true); return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _vendorCheckId;
    public ConditionValue getVendorCheckId() {
        if (_vendorCheckId == null) { _vendorCheckId = new ConditionValue(); }
        return _vendorCheckId;
    }
    protected ConditionValue getCValueVendorCheckId() { return getVendorCheckId(); }

    public BsVendorCheckCQ addOrderBy_VendorCheckId_Asc() { regOBA("VENDOR_CHECK_ID"); return this; }
    public BsVendorCheckCQ addOrderBy_VendorCheckId_Desc() { regOBD("VENDOR_CHECK_ID"); return this; }

    protected ConditionValue _decimalDigit;
    public ConditionValue getDecimalDigit() {
        if (_decimalDigit == null) { _decimalDigit = new ConditionValue(); }
        return _decimalDigit;
    }
    protected ConditionValue getCValueDecimalDigit() { return getDecimalDigit(); }

    public BsVendorCheckCQ addOrderBy_DecimalDigit_Asc() { regOBA("DECIMAL_DIGIT"); return this; }
    public BsVendorCheckCQ addOrderBy_DecimalDigit_Desc() { regOBD("DECIMAL_DIGIT"); return this; }

    protected ConditionValue _integerNonDigit;
    public ConditionValue getIntegerNonDigit() {
        if (_integerNonDigit == null) { _integerNonDigit = new ConditionValue(); }
        return _integerNonDigit;
    }
    protected ConditionValue getCValueIntegerNonDigit() { return getIntegerNonDigit(); }

    public BsVendorCheckCQ addOrderBy_IntegerNonDigit_Asc() { regOBA("INTEGER_NON_DIGIT"); return this; }
    public BsVendorCheckCQ addOrderBy_IntegerNonDigit_Desc() { regOBD("INTEGER_NON_DIGIT"); return this; }

    protected ConditionValue _typeOfBoolean;
    public ConditionValue getTypeOfBoolean() {
        if (_typeOfBoolean == null) { _typeOfBoolean = new ConditionValue(); }
        return _typeOfBoolean;
    }
    protected ConditionValue getCValueTypeOfBoolean() { return getTypeOfBoolean(); }

    public BsVendorCheckCQ addOrderBy_TypeOfBoolean_Asc() { regOBA("TYPE_OF_BOOLEAN"); return this; }
    public BsVendorCheckCQ addOrderBy_TypeOfBoolean_Desc() { regOBD("TYPE_OF_BOOLEAN"); return this; }

    protected ConditionValue _typeOfText;
    public ConditionValue getTypeOfText() {
        if (_typeOfText == null) { _typeOfText = new ConditionValue(); }
        return _typeOfText;
    }
    protected ConditionValue getCValueTypeOfText() { return getTypeOfText(); }

    public BsVendorCheckCQ addOrderBy_TypeOfText_Asc() { regOBA("TYPE_OF_TEXT"); return this; }
    public BsVendorCheckCQ addOrderBy_TypeOfText_Desc() { regOBD("TYPE_OF_TEXT"); return this; }

    // ===================================================================================
    //                                                           Specified Derived OrderBy
    //                                                           =========================
    public BsVendorCheckCQ addSpecifiedDerivedOrderBy_Asc(String aliasName) { registerSpecifiedDerivedOrderBy_Asc(aliasName); return this; }
    public BsVendorCheckCQ addSpecifiedDerivedOrderBy_Desc(String aliasName) { registerSpecifiedDerivedOrderBy_Desc(aliasName); return this; }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    // ===================================================================================
    //                                                                     Scalar SubQuery
    //                                                                     ===============
    protected Map<String, VendorCheckCQ> _scalarSubQueryMap;
    public Map<String, VendorCheckCQ> getScalarSubQuery() { return _scalarSubQueryMap; }
    public String keepScalarSubQuery(VendorCheckCQ subQuery) {
        if (_scalarSubQueryMap == null) { _scalarSubQueryMap = newLinkedHashMap(); }
        String key = "subQueryMapKey" + (_scalarSubQueryMap.size() + 1);
        _scalarSubQueryMap.put(key, subQuery); return "scalarSubQuery." + key;
    }

    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return VendorCheckCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return VendorCheckCQ.class.getName(); }
    protected String getMapClassNameInternally() { return Map.class.getName(); }
}
