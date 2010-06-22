package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public enum HpCBPurpose {

    NORMAL_USE(new HpSpec()) // basic (all functions can be used)
    , UNION_QUERY(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // Union
    , EXISTS_REFERRER(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // ExistsReferrer 
    , IN_SCOPE_RELATION(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // InScopeRelation
    , DERIVED_REFERRER(new HpSpec().noSetupSelect().noSpecifyColumnTwoOrMore().noSpecifyDerivedReferrer().noOrderBy()) // DerivedReferrer
    , SCALAR_SELECT(new HpSpec().noSetupSelect().noSpecifyColumnTwoOrMore().noSpecifyRelation()
            .noSpecifyDerivedReferrer().noOrderBy()) // ScalarSelect
    , SCALAR_CONDITION(new HpSpec().noSetupSelect().noSpecifyColumnTwoOrMore().noSpecifyRelation()
            .noSpecifyDerivedReferrer().noOrderBy()) // ScalarCondition

    // A purpose that can specify but not allowed to query
    // needs to switch condition-bean used in specification
    // to non-checked condition-bean.
    // Because specification uses query internally.
    , COLUMN_QUERY(new HpSpec().noSetupSelect().noSpecifyColumnTwoOrMore().noSpecifyDerivedReferrer().noQuery()) // ColumnQuery
    , VARYING_UPDATE(new HpSpec().noSetupSelect().noSpecifyColumnTwoOrMore().noSpecifyRelation()
            .noSpecifyDerivedReferrer().noQuery()) // VaryingUpdate

    // QueryUpdate and QueryDelete are not defined here
    // because their condition-beans are created by an application
    // (not call-back style)
    //, QUERY_UPDATE(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // QueryUpdate
    //, QUERY_DELETE(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // QueryDelete
    ;

    private final HpSpec _spec;

    private HpCBPurpose(HpSpec spec) {
        _spec = spec;
    }

    public boolean isAny(HpCBPurpose... purposes) {
        for (HpCBPurpose purpose : purposes) {
            if (equals(purpose)) {
                return true;
            }
        }
        return false;
    }

    // any checks are not implemented
    // because it's so touch

    public boolean isNoSetupSelect() {
        return _spec.isNoSetupSelect();
    }

    public boolean isNoSpecify() {
        return _spec.isNoSpecify();
    }

    public boolean isNoSpecifyColumnTwoOrMore() {
        return _spec.isNoSpecifyColumnTwoOrMore();
    }

    public boolean isNoSpecifyRelation() {
        return _spec.isNoSpecifyRelation();
    }

    public boolean isNoSpecifyDerivedReferrer() {
        return _spec.isNoSpecifyDerivedReferrer();
    }

    public boolean isNoQuery() {
        return _spec.isNoQuery();
    }

    public boolean isNoOrderBy() {
        return _spec.isNoOrderBy();
    }

    @Override
    public String toString() {
        return Srl.camelize(name());
    }

    public static class HpSpec {
        protected boolean _noSetupSelect;
        protected boolean _noSpecify;
        protected boolean _noSpecifyColumnTwoOrMore;
        protected boolean _noSpecifyRelation;
        protected boolean _noSpecifyDerivedReferrer;
        protected boolean _noQuery;
        protected boolean _noOrderBy;

        public HpSpec noSetupSelect() {
            _noSetupSelect = true;
            return this;
        }

        public HpSpec noSpecify() {
            _noSpecify = true;
            return this;
        }

        public HpSpec noSpecifyColumnTwoOrMore() {
            _noSpecifyColumnTwoOrMore = true;
            return this;
        }

        public HpSpec noSpecifyRelation() {
            _noSpecifyRelation = true;
            return this;
        }

        public HpSpec noSpecifyDerivedReferrer() {
            _noSpecifyDerivedReferrer = true;
            return this;
        }

        public HpSpec noQuery() {
            _noQuery = true;
            return this;
        }

        public HpSpec noOrderBy() {
            _noOrderBy = true;
            return this;
        }

        public boolean isNoSetupSelect() {
            return _noSetupSelect;
        }

        public boolean isNoSpecify() {
            return _noSpecify;
        }

        public boolean isNoSpecifyColumnTwoOrMore() {
            return _noSpecifyColumnTwoOrMore;
        }

        public boolean isNoSpecifyRelation() {
            return _noSpecifyRelation;
        }

        public boolean isNoSpecifyDerivedReferrer() {
            return _noSpecifyDerivedReferrer;
        }

        public boolean isNoQuery() {
            return _noQuery;
        }

        public boolean isNoOrderBy() {
            return _noOrderBy;
        }
    }
}
