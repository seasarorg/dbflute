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

/**
 * The base condition-bean of WITHDRAWAL_REASON.
 * @author DBFlute(AutoGenerator)
 */
public class BsWithdrawalReasonCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected WithdrawalReasonCQ _conditionQuery;

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
        return "WITHDRAWAL_REASON";
    }

    public String getTableSqlName() {
        return "WITHDRAWAL_REASON";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("WITHDRAWAL_REASON_CODE");
            if (obj instanceof String) {
                query().setWithdrawalReasonCode_Equal((String)obj);
            } else {
                checkTypeString(obj, "withdrawalReasonCode", "String");
                query().setWithdrawalReasonCode_Equal((String)obj);
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_WithdrawalReasonCode_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_WithdrawalReasonCode_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public WithdrawalReasonCQ query() {
        return getConditionQuery();
    }

    public WithdrawalReasonCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new WithdrawalReasonCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;WithdrawalReasonCB&gt;() {
     *     public void query(WithdrawalReasonCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<WithdrawalReasonCB> unionQuery) {
        final WithdrawalReasonCB cb = new WithdrawalReasonCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final WithdrawalReasonCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;WithdrawalReasonCB&gt;() {
     *     public void query(WithdrawalReasonCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<WithdrawalReasonCB> unionQuery) {
        final WithdrawalReasonCB cb = new WithdrawalReasonCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final WithdrawalReasonCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<WithdrawalReasonCQ>() {
            public boolean has() { return true; } public WithdrawalReasonCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<WithdrawalReasonCQ> {
        protected SpQyCall<WithdrawalReasonCQ> _myQyCall;
        public Specification(ConditionBean baseCB, SpQyCall<WithdrawalReasonCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnWithdrawalReasonCode() { doColumn("WITHDRAWAL_REASON_CODE"); }
        public void columnWithdrawalReasonText() { doColumn("WITHDRAWAL_REASON_TEXT"); }
        public void columnDisplayOrder() { doColumn("DISPLAY_ORDER"); }
        protected void doSpecifyRequiredColumn() {
            columnWithdrawalReasonCode();// PK
        }
        protected String getTableDbName() { return "WITHDRAWAL_REASON"; }
        public RAFunction<MemberWithdrawalCB, WithdrawalReasonCQ> derivedMemberWithdrawalList() {
            return new RAFunction<MemberWithdrawalCB, WithdrawalReasonCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<MemberWithdrawalCB, WithdrawalReasonCQ>() {
                public void setup(String function, SubQuery<MemberWithdrawalCB> subQuery, WithdrawalReasonCQ cq, String aliasName) {
                    cq.xsderiveMemberWithdrawalList(function, subQuery, aliasName); } }, _dbmetaProvider);
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
    protected String getConditionBeanClassNameInternally() { return WithdrawalReasonCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return WithdrawalReasonCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
