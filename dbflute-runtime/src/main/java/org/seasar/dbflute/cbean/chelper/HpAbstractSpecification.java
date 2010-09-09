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
    protected final HpSpQyCall<CQ> _qyCall; // not final because it may be switched
    protected HpSpQyCall<CQ> _switchedToQyCall;
    protected final HpCBPurpose _purpose;
    protected final DBMetaProvider _dbmetaProvider;
    protected CQ _query;
    protected boolean _alreadySpecifiedRequiredColumn; // also means specification existence
    protected int _specifyColumnCount;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param baseCB The condition-bean of base level. (NotNull)
     * @param qyCall The call-back for condition-query. (NotNull)
     * @param purpose The purpose of condition-bean. (NotNull)
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     */
    protected HpAbstractSpecification(ConditionBean baseCB, HpSpQyCall<CQ> qyCall, HpCBPurpose purpose,
            DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _qyCall = qyCall;
        _purpose = purpose;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                Column Specification
    //                                                                ====================
    protected void doColumn(String columnName) {
        ++_specifyColumnCount;
        assertColumn(columnName);
        if (_query == null) {
            _query = currentQyCall().qy();
        }
        if (isRequiredColumnSpecificationEnabled()) {
            _alreadySpecifiedRequiredColumn = true;
            doSpecifyRequiredColumn();
        }
        final String relationPath = _query.xgetRelationPath() != null ? _query.xgetRelationPath() : "";
        final String tableAliasName;
        if (_query.isBaseQuery()) {
            tableAliasName = _baseCB.getSqlClause().getLocalTableAliasName();
        } else {
            tableAliasName = _baseCB.getSqlClause().resolveJoinAliasName(relationPath, _query.xgetNestLevel());
        }
        _baseCB.getSqlClause().specifySelectColumn(tableAliasName, columnName, _query.getTableDbName());
    }

    protected void assertColumn(String columnName) {
        if (_purpose.isNoSpecifyColumnTwoOrMore()) {
            if (_specifyColumnCount > 1) {
                throwSpecifyColumnTwoOrMoreColumnException(columnName);
            }
            // no specification is checked at an other timing
        }
        if (isNormalUse()) { // only normal purpose needs
            if (_query == null && !currentQyCall().has()) { // setupSelect check!
                throwSpecifyColumnNotSetupSelectColumnException(columnName);
            }
        }
    }

    protected HpSpQyCall<CQ> currentQyCall() {
        return _switchedToQyCall != null ? _switchedToQyCall : _qyCall;
    }

    protected boolean isRequiredColumnSpecificationEnabled() {
        if (_alreadySpecifiedRequiredColumn) {
            return false;
        }
        return isNormalUse(); // only normal purpose needs
    }

    protected void assertRelation(String relationName) {
        if (_purpose.isNoSpecifyRelation()) {
            throwSpecifyRelationIllegalPurposeException(relationName);
        }
    }

    protected void assertDerived(String referrerName) {
        if (_purpose.isNoSpecifyDerivedReferrer()) {
            throwSpecifyDerivedReferrerIllegalPurposeException(referrerName);
        }
    }

    protected boolean isNormalUse() {
        return HpCBPurpose.NORMAL_USE.equals(_purpose);
    }

    protected abstract void doSpecifyRequiredColumn();

    protected abstract String getTableDbName();

    // ===================================================================================
    //                                                                  Exception Throwing
    //                                                                  ==================
    protected void throwSpecifyColumnTwoOrMoreColumnException(String columnName) {
        createCBExThrower().throwSpecifyColumnTwoOrMoreColumnException(_purpose, _baseCB, columnName);
    }

    protected void throwSpecifyColumnNotSetupSelectColumnException(String columnName) {
        createCBExThrower().throwSpecifyColumnNotSetupSelectColumnException(_baseCB, columnName);
    }

    protected void throwSpecifyRelationIllegalPurposeException(String relationName) {
        createCBExThrower().throwSpecifyRelationIllegalPurposeException(_purpose, _baseCB, relationName);
    }

    protected void throwSpecifyDerivedReferrerIllegalPurposeException(String referrerName) {
        createCBExThrower().throwSpecifyDerivedReferrerIllegalPurposeException(_purpose, _baseCB, referrerName);
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
    public boolean isAlreadySpecifiedRequiredColumn() {
        return _alreadySpecifiedRequiredColumn;
    }

    public void xswitchQyCall(HpSpQyCall<CQ> qyCall) {
        if (qyCall == null) {
            String msg = "The argument 'qyCall' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        _switchedToQyCall = qyCall;
    }
}