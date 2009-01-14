package com.example.dbflute.basic.dbflute.cbean.bs;

import java.util.Map;

import org.seasar.dbflute.cbean.AbstractConditionBean;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.allcommon.ImplementedSqlClauseCreator;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;
import com.example.dbflute.basic.dbflute.cbean.nss.*;

/**
 * The base condition-bean of MEMBER.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected MemberCQ _conditionQuery;

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    @Override
    protected SqlClause createSqlClause() {
        return new ImplementedSqlClauseCreator().createSqlClause(this);
    }
    
    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "MEMBER";
    }

    public String getTableSqlName() {
        return "MEMBER";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("MEMBER_ID");
            if (obj instanceof Integer) {
                query().setMemberId_Equal((Integer)obj);
            } else {
                query().setMemberId_Equal(new Integer((String)obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_MemberId_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_MemberId_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public MemberCQ query() {
        return getConditionQuery();
    }

    public MemberCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new MemberCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
        }
        return _conditionQuery;
    }

    /**
     * The implementation.
     * @return The conditionQuery of the local table as interface. (NotNull)
     */
    public ConditionQuery localCQ() {
        return getConditionQuery();
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Set up 'union'.
     * <pre>
     * cb.query().union(new UnionQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<MemberCB> unionQuery) {
        final MemberCB cb = new MemberCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<MemberCB> unionQuery) {
        final MemberCB cb = new MemberCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
    protected MemberStatusNss _nssMemberStatus;
    public MemberStatusNss getNssMemberStatus() {
        if (_nssMemberStatus == null) { _nssMemberStatus = new MemberStatusNss(null); }
        return _nssMemberStatus;
    }
    public MemberStatusNss setupSelect_MemberStatus() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMemberStatus(); } });
        if (_nssMemberStatus == null || !_nssMemberStatus.hasConditionQuery())
        { _nssMemberStatus = new MemberStatusNss(query().queryMemberStatus()); }
        return _nssMemberStatus;
    }
    protected MemberAddressNss _nssMemberAddressAsValid;
    public MemberAddressNss getNssMemberAddressAsValid() {
        if (_nssMemberAddressAsValid == null) { _nssMemberAddressAsValid = new MemberAddressNss(null); }
        return _nssMemberAddressAsValid;
    }
    public MemberAddressNss setupSelect_MemberAddressAsValid(final java.util.Date targetDate) {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMemberAddressAsValid(targetDate); } });
        if (_nssMemberAddressAsValid == null || !_nssMemberAddressAsValid.hasConditionQuery())
        { _nssMemberAddressAsValid = new MemberAddressNss(query().queryMemberAddressAsValid(targetDate)); }
        return _nssMemberAddressAsValid;
    }

    protected MemberSecurityNss _nssMemberSecurityAsOne;
    public MemberSecurityNss getNssMemberSecurityAsOne() {
        if (_nssMemberSecurityAsOne == null) { _nssMemberSecurityAsOne = new MemberSecurityNss(null); }
        return _nssMemberSecurityAsOne;
    }
    public MemberSecurityNss setupSelect_MemberSecurityAsOne() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMemberSecurityAsOne(); } });
        if (_nssMemberSecurityAsOne == null || !_nssMemberSecurityAsOne.hasConditionQuery()) { _nssMemberSecurityAsOne = new MemberSecurityNss(query().queryMemberSecurityAsOne()); }
        return _nssMemberSecurityAsOne;
    }

    protected MemberWithdrawalNss _nssMemberWithdrawalAsOne;
    public MemberWithdrawalNss getNssMemberWithdrawalAsOne() {
        if (_nssMemberWithdrawalAsOne == null) { _nssMemberWithdrawalAsOne = new MemberWithdrawalNss(null); }
        return _nssMemberWithdrawalAsOne;
    }
    public MemberWithdrawalNss setupSelect_MemberWithdrawalAsOne() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMemberWithdrawalAsOne(); } });
        if (_nssMemberWithdrawalAsOne == null || !_nssMemberWithdrawalAsOne.hasConditionQuery()) { _nssMemberWithdrawalAsOne = new MemberWithdrawalNss(query().queryMemberWithdrawalAsOne()); }
        return _nssMemberWithdrawalAsOne;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<MemberCQ>() {
            public boolean has() { return true; } public MemberCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<MemberCQ> {
        protected SpQyCall<MemberCQ> _myQyCall;
        protected MemberStatusCB.Specification _memberStatus;
        protected MemberAddressCB.Specification _memberAddressAsValid;
        protected MemberSecurityCB.Specification _memberSecurityAsOne;
        protected MemberWithdrawalCB.Specification _memberWithdrawalAsOne;
        public Specification(ConditionBean baseCB, SpQyCall<MemberCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnMemberId() { doColumn("MEMBER_ID"); }
        public void columnMemberName() { doColumn("MEMBER_NAME"); }
        public void columnMemberAccount() { doColumn("MEMBER_ACCOUNT"); }
        public void columnMemberStatusCode() { doColumn("MEMBER_STATUS_CODE"); }
        public void columnMemberFormalizedDatetime() { doColumn("MEMBER_FORMALIZED_DATETIME"); }
        public void columnMemberBirthday() { doColumn("MEMBER_BIRTHDAY"); }
        public void columnRegisterDatetime() { doColumn("REGISTER_DATETIME"); }
        public void columnRegisterUser() { doColumn("REGISTER_USER"); }
        public void columnRegisterProcess() { doColumn("REGISTER_PROCESS"); }
        public void columnUpdateDatetime() { doColumn("UPDATE_DATETIME"); }
        public void columnUpdateUser() { doColumn("UPDATE_USER"); }
        public void columnUpdateProcess() { doColumn("UPDATE_PROCESS"); }
        public void columnVersionNo() { doColumn("VERSION_NO"); }
        protected void doSpecifyRequiredColumn() {
            columnMemberId();// PK
            if (_myQyCall.qy().hasConditionQueryMemberStatus()) {
                columnMemberStatusCode();// FK
            }
            if (_myQyCall.qy().hasConditionQueryMemberAddressAsValid()) {
            }
        }
        protected String getTableDbName() { return "MEMBER"; }
        public MemberStatusCB.Specification specifyMemberStatus() {
            assertForeign("memberStatus");
            if (_memberStatus == null) {
                _memberStatus = new MemberStatusCB.Specification(_baseCB, new SpQyCall<MemberStatusCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMemberStatus(); }
                    public MemberStatusCQ qy() { return _myQyCall.qy().queryMemberStatus(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _memberStatus;
        }
        public MemberAddressCB.Specification specifyMemberAddressAsValid() {
            assertForeign("memberAddressAsValid");
            if (_memberAddressAsValid == null) {
                _memberAddressAsValid = new MemberAddressCB.Specification(_baseCB, new SpQyCall<MemberAddressCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMemberAddressAsValid(); }
                    public MemberAddressCQ qy() { return _myQyCall.qy().getConditionQueryMemberAddressAsValid(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _memberAddressAsValid;
        }
        public MemberSecurityCB.Specification specifyMemberSecurityAsOne() {
            assertForeign("memberSecurityAsOne");
            if (_memberSecurityAsOne == null) {
                _memberSecurityAsOne = new MemberSecurityCB.Specification(_baseCB, new SpQyCall<MemberSecurityCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMemberSecurityAsOne(); }
                    public MemberSecurityCQ qy() { return _myQyCall.qy().queryMemberSecurityAsOne(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _memberSecurityAsOne;
        }
        public MemberWithdrawalCB.Specification specifyMemberWithdrawalAsOne() {
            assertForeign("memberWithdrawalAsOne");
            if (_memberWithdrawalAsOne == null) {
                _memberWithdrawalAsOne = new MemberWithdrawalCB.Specification(_baseCB, new SpQyCall<MemberWithdrawalCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMemberWithdrawalAsOne(); }
                    public MemberWithdrawalCQ qy() { return _myQyCall.qy().queryMemberWithdrawalAsOne(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _memberWithdrawalAsOne;
        }
        public RAFunction<MemberAddressCB, MemberCQ> derivedMemberAddressList() {
            return new RAFunction<MemberAddressCB, MemberCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<MemberAddressCB, MemberCQ>() {
                public void setup(String function, SubQuery<MemberAddressCB> subQuery, MemberCQ cq, String aliasName) {
                    cq.xsderiveMemberAddressList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
        public RAFunction<MemberLoginCB, MemberCQ> derivedMemberLoginList() {
            return new RAFunction<MemberLoginCB, MemberCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<MemberLoginCB, MemberCQ>() {
                public void setup(String function, SubQuery<MemberLoginCB> subQuery, MemberCQ cq, String aliasName) {
                    cq.xsderiveMemberLoginList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
        public RAFunction<PurchaseCB, MemberCQ> derivedPurchaseList() {
            return new RAFunction<PurchaseCB, MemberCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<PurchaseCB, MemberCQ>() {
                public void setup(String function, SubQuery<PurchaseCB> subQuery, MemberCQ cq, String aliasName) {
                    cq.xsderivePurchaseList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
    }

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    @Override
    protected String getLogDateFormat() { return DBFluteConfig.getInstance().getLogDateFormat(); }
    @Override
    protected String getLogTimestampFormat() { return DBFluteConfig.getInstance().getLogTimestampFormat(); }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() { return MemberCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
