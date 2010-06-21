package org.seasar.dbflute.cbean.chelper;

/**
 * @author jflute
 */
public enum HpCBPurpose {

    NORMAL(new HpSpec()) // basic
    , UNION(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // Union
    , EXISTS_REFERRER(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // ExistsReferrer 
    , INSCOPE_RELATION(new HpSpec().noSetupSelect().noSpecify().noOrderBy()) // InScopeRelation
    , DERIVED_REFERRER(new HpSpec().noSetupSelect().noSpecifyColumnTwice().noSpecifyDerivedReferrer().noOrderBy()) // DerivedReferrer
    , SCALAR_SELECT(new HpSpec().noSetupSelect().noSpecifyColumnTwice().noSpecifyDerivedReferrer().noOrderBy()) // ScalarSelect
    , SCALAR_CONDITION(new HpSpec().noSetupSelect().noSpecifyColumnTwice().noSpecifyDerivedReferrer().noOrderBy()) // ScalarCondition

    // A purpose that can specify but not allowed to query
    // needs to switch condition-bean used in specification
    // to non-checked condition-bean.
    // Because specification uses query internally.
    , COLUMN_QUERY(new HpSpec().noSetupSelect().noSpecifyColumnTwice().noSpecifyDerivedReferrer().noQuery()) // ColumnQuery
    , VARYING_UPDATE(new HpSpec().noSetupSelect().noSpecifyColumnTwice().noSpecifyDerivedReferrer().noQuery()) // VaryingUpdate

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

    public boolean isNoSpecifyTwice() {
        return _spec.isNoSpecifyTwice();
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

    public static class HpSpec {
        protected boolean _noSetupSelect;
        protected boolean _noSpecify;
        protected boolean _noSpecifyColumnTwice;
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

        public HpSpec noSpecifyColumnTwice() {
            _noSpecifyColumnTwice = true;
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

        public boolean isNoSpecifyTwice() {
            return _noSpecifyColumnTwice;
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
