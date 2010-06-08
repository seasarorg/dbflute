package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @param <CQ> The type of condition-query.
 */
public abstract class HpAbstractSpecification<CQ extends ConditionQuery> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionBean _baseCB;
    protected HpSpQyCall<CQ> _qyCall; // overridden when GeneralOneSpecification
    protected final boolean _forDerivedReferrer;
    protected final boolean _forScalarSelect;
    protected final boolean _forScalarSubQuery;
    protected final DBMetaProvider _dbmetaProvider;
    protected CQ _query;
    protected boolean _alreadySpecifyRequiredColumn; // also means specification existence
    protected boolean _forGeneralOneSpecificaion;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param baseCB The condition-bean of base level. (NotNull)
     * @param qyCall The call-back for condition-query. (NotNull)
     * @param forDerivedReferrer Is this for derive referrer?
     * @param forScalarSelect Is this for scalar select?
     * @param forScalarSubQuery  Is this for scalar sub-query?
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     */
    protected HpAbstractSpecification(ConditionBean baseCB, HpSpQyCall<CQ> qyCall, boolean forDerivedReferrer,
            boolean forScalarSelect, boolean forScalarSubQuery, DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _qyCall = qyCall;
        _forDerivedReferrer = forDerivedReferrer;
        _forScalarSelect = forScalarSelect;
        _forScalarSubQuery = forScalarSubQuery;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                Column Specification
    //                                                                ====================
    protected void doColumn(String columnName) {
        assertColumn(columnName);
        if (_query == null) {
            _query = _qyCall.qy();
        }
        if (isRequiredColumnSpecificationEnabled()) {
            _alreadySpecifyRequiredColumn = true;
            doSpecifyRequiredColumn();
        }
        final String relationPath = _query.getRelationPath() != null ? _query.getRelationPath() : "";
        final String tableAliasName;
        if (_query.isBaseQuery()) {
            tableAliasName = _baseCB.getSqlClause().getLocalTableAliasName();
        } else {
            tableAliasName = _baseCB.getSqlClause().resolveJoinAliasName(relationPath, _query.getNestLevel());
        }
        _baseCB.getSqlClause().specifySelectColumn(tableAliasName, columnName, _query.getTableDbName());
    }

    protected boolean isRequiredColumnSpecificationEnabled() {
        return !_forGeneralOneSpecificaion && !_forDerivedReferrer && !_forScalarSelect && !_forScalarSubQuery
                && !_alreadySpecifyRequiredColumn;
    }

    protected void assertColumn(String columnName) {
        if (_forGeneralOneSpecificaion || _forDerivedReferrer) {
            return;
        }
        if (_query == null && !_qyCall.has()) { // setupSelect check!
            throwSpecifyColumnNotSetupSelectColumnException(columnName);
        }
    }

    protected void assertForeign(String foreignPropertyName) {
        if (_forScalarSelect) {
            throwScalarSelectInvalidForeignSpecificationException(foreignPropertyName);
        }
        if (_forScalarSubQuery) {
            throwScalarSubQueryInvalidForeignSpecificationException(foreignPropertyName);
        }
    }

    protected abstract void doSpecifyRequiredColumn();

    protected abstract String getTableDbName();

    // ===================================================================================
    //                                                                  Exception Throwing
    //                                                                  ==================
    protected void throwSpecifyColumnNotSetupSelectColumnException(String columnName) {
        createCBExThrower().throwSpecifyColumnNotSetupSelectColumnException(_baseCB, getTableDbName(), columnName);
    }

    protected void throwDerivedReferrerInvalidForeignSpecificationException(String foreignPropertyName) {
        createCBExThrower().throwDerivedReferrerInvalidForeignSpecificationException(foreignPropertyName);
    }

    protected void throwScalarSelectInvalidForeignSpecificationException(String foreignPropertyName) {
        createCBExThrower().throwScalarSelectInvalidForeignSpecificationException(foreignPropertyName);
    }

    protected void throwScalarSubQueryInvalidForeignSpecificationException(String foreignPropertyName) {
        createCBExThrower().throwScalarSubQueryInvalidForeignSpecificationException(foreignPropertyName);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isAlreadySpecifyRequiredColumn() {
        return _alreadySpecifyRequiredColumn;
    }

    public boolean isForGeneralOneSpecificaion() {
        return _forGeneralOneSpecificaion;
    }
}