package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public enum HpCBPurpose {

    NORMAL_USE(new HpSpec()) // basic (all functions can be used)
    , UNION_QUERY(new HpSpec().nonSetupSelect().nonSpecify().nonOrderBy()) // Union
    , EXISTS_REFERRER(new HpSpec().nonSetupSelect().nonSpecify().nonOrderBy().subQuery()) // ExistsReferrer 
    , IN_SCOPE_RELATION(new HpSpec().nonSetupSelect().nonSpecify().nonOrderBy().subQuery()) // InScopeRelation
    , DERIVED_REFERRER(new HpSpec().nonSetupSelect().nonSpecifyColumnTwoOrMore().nonSpecifyColumnWithDerivedReferrer()
            .nonSpecifyDerivedReferrerTwoOrMore().nonOrderBy().subQuery()) // DerivedReferrer
    , SCALAR_SELECT(new HpSpec().nonSetupSelect().nonSpecifyColumnTwoOrMore().nonSpecifyColumnWithDerivedReferrer()
            .nonSpecifyDerivedReferrerTwoOrMore().nonSpecifyRelation().nonOrderBy()) // ScalarSelect
    , SCALAR_CONDITION(new HpSpec().nonSetupSelect().nonSpecifyColumnTwoOrMore().nonSpecifyRelation()
            .nonSpecifyDerivedReferrer().nonOrderBy().subQuery()) // ScalarCondition

    // A purpose that can specify but not allowed to query
    // needs to switch condition-bean used in specification
    // to non-checked condition-bean.
    // Because specification uses query internally.
    , COLUMN_QUERY(new HpSpec().nonSetupSelect().nonSpecifyColumnTwoOrMore().nonSpecifyColumnWithDerivedReferrer()
            .nonSpecifyDerivedReferrerTwoOrMore().nonQuery()) // ColumnQuery
    , VARYING_UPDATE(new HpSpec().nonSetupSelect().nonSpecifyColumnTwoOrMore().nonSpecifyRelation()
            .nonSpecifyDerivedReferrer().nonQuery()) // VaryingUpdate
    , SPECIFIED_UPDATE(new HpSpec().nonSetupSelect().nonSpecifyRelation().nonSpecifyDerivedReferrer().nonQuery()) // SpecifiedUpdate

    // for intoCB (not for resourceCB)
    , QUERY_INSERT(new HpSpec().nonSetupSelect().nonSpecifyDerivedReferrer().nonSpecifyRelation().nonQuery()
            .nonOrderBy()) // QueryInsert

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

    public boolean isNonSetupSelect() {
        return _spec.isNonSetupSelect();
    }

    public boolean isNonSpecify() {
        return _spec.isNonSpecify();
    }

    public boolean isNonSpecifyColumnTwoOrMore() {
        return _spec.isNonSpecifyColumnTwoOrMore();
    }

    public boolean isNonSpecifyColumnWithDerivedReferrer() {
        return _spec.isNonSpecifyColumnWithDerivedReferrer();
    }

    public boolean isNonSpecifyRelation() {
        return _spec.isNonSpecifyRelation();
    }

    public boolean isNonSpecifyDerivedReferrer() {
        return _spec.isNonSpecifyDerivedReferrer();
    }

    public boolean isNonSpecifyDerivedReferrerTwoOrMore() {
        return _spec.isNonSpecifyDerivedReferrerTwoOrMore();
    }

    public boolean isNonQuery() {
        return _spec.isNonQuery();
    }

    public boolean isNonOrderBy() {
        return _spec.isNonOrderBy();
    }

    public boolean isSubQuery() {
        return _spec.isSubQuery();
    }

    @Override
    public String toString() {
        return Srl.camelize(name());
    }

    public static class HpSpec {
        protected boolean _nonSetupSelect;
        protected boolean _nonSpecify;
        protected boolean _nonSpecifyColumnTwoOrMore;
        protected boolean _nonSpecifyColumnWithDerivedReferrer;
        protected boolean _nonSpecifyRelation;
        protected boolean _nonSpecifyDerivedReferrer;
        protected boolean _nonSpecifyDerivedReferrerTwoOrMore;
        protected boolean _nonQuery;
        protected boolean _nonOrderBy;
        protected boolean _subQuery;

        public HpSpec nonSetupSelect() {
            _nonSetupSelect = true;
            return this;
        }

        public HpSpec nonSpecify() {
            _nonSpecify = true;
            return this;
        }

        public HpSpec nonSpecifyColumnTwoOrMore() {
            _nonSpecifyColumnTwoOrMore = true;
            return this;
        }

        public HpSpec nonSpecifyColumnWithDerivedReferrer() {
            _nonSpecifyColumnWithDerivedReferrer = true;
            return this;
        }

        public HpSpec nonSpecifyRelation() {
            _nonSpecifyRelation = true;
            return this;
        }

        public HpSpec nonSpecifyDerivedReferrer() {
            _nonSpecifyDerivedReferrer = true;
            return this;
        }

        public HpSpec nonSpecifyDerivedReferrerTwoOrMore() {
            _nonSpecifyDerivedReferrerTwoOrMore = true;
            return this;
        }

        public HpSpec nonQuery() {
            _nonQuery = true;
            return this;
        }

        public HpSpec nonOrderBy() {
            _nonOrderBy = true;
            return this;
        }

        public HpSpec subQuery() {
            _subQuery = true;
            return this;
        }

        public boolean isNonSetupSelect() {
            return _nonSetupSelect;
        }

        public boolean isNonSpecify() {
            return _nonSpecify;
        }

        public boolean isNonSpecifyColumnTwoOrMore() {
            return _nonSpecifyColumnTwoOrMore;
        }

        public boolean isNonSpecifyColumnWithDerivedReferrer() {
            return _nonSpecifyColumnWithDerivedReferrer;
        }

        public boolean isNonSpecifyRelation() {
            return _nonSpecifyRelation;
        }

        public boolean isNonSpecifyDerivedReferrer() {
            return _nonSpecifyDerivedReferrer;
        }

        public boolean isNonSpecifyDerivedReferrerTwoOrMore() {
            return _nonSpecifyDerivedReferrerTwoOrMore;
        }

        public boolean isNonQuery() {
            return _nonQuery;
        }

        public boolean isNonOrderBy() {
            return _nonOrderBy;
        }

        public boolean isSubQuery() {
            return _subQuery;
        }
    }
}
