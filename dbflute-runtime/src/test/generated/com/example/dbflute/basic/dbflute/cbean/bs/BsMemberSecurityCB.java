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
 * The base condition-bean of MEMBER_SECURITY.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberSecurityCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected MemberSecurityCQ _conditionQuery;

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
        return "MEMBER_SECURITY";
    }

    public String getTableSqlName() {
        return "MEMBER_SECURITY";
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
    public MemberSecurityCQ query() {
        return getConditionQuery();
    }

    public MemberSecurityCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new MemberSecurityCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;MemberSecurityCB&gt;() {
     *     public void query(MemberSecurityCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<MemberSecurityCB> unionQuery) {
        final MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberSecurityCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;MemberSecurityCB&gt;() {
     *     public void query(MemberSecurityCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<MemberSecurityCB> unionQuery) {
        final MemberSecurityCB cb = new MemberSecurityCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberSecurityCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
    protected MemberNss _nssMember;
    public MemberNss getNssMember() {
        if (_nssMember == null) { _nssMember = new MemberNss(null); }
        return _nssMember;
    }
    public MemberNss setupSelect_Member() {
        doSetupSelect(new SsCall() { public ConditionQuery qf() { return query().queryMember(); } });
        if (_nssMember == null || !_nssMember.hasConditionQuery())
        { _nssMember = new MemberNss(query().queryMember()); }
        return _nssMember;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<MemberSecurityCQ>() {
            public boolean has() { return true; } public MemberSecurityCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<MemberSecurityCQ> {
        protected SpQyCall<MemberSecurityCQ> _myQyCall;
        protected MemberCB.Specification _member;
        public Specification(ConditionBean baseCB, SpQyCall<MemberSecurityCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnMemberId() { doColumn("MEMBER_ID"); }
        public void columnLoginPassword() { doColumn("LOGIN_PASSWORD"); }
        public void columnReminderQuestion() { doColumn("REMINDER_QUESTION"); }
        public void columnReminderAnswer() { doColumn("REMINDER_ANSWER"); }
        public void columnRegisterDatetime() { doColumn("REGISTER_DATETIME"); }
        public void columnRegisterProcess() { doColumn("REGISTER_PROCESS"); }
        public void columnRegisterUser() { doColumn("REGISTER_USER"); }
        public void columnUpdateDatetime() { doColumn("UPDATE_DATETIME"); }
        public void columnUpdateProcess() { doColumn("UPDATE_PROCESS"); }
        public void columnUpdateUser() { doColumn("UPDATE_USER"); }
        public void columnVersionNo() { doColumn("VERSION_NO"); }
        protected void doSpecifyRequiredColumn() {
            columnMemberId();// PK
            if (_myQyCall.qy().hasConditionQueryMember()) {
            }
        }
        protected String getTableDbName() { return "MEMBER_SECURITY"; }
        public MemberCB.Specification specifyMember() {
            assertForeign("member");
            if (_member == null) {
                _member = new MemberCB.Specification(_baseCB, new SpQyCall<MemberCQ>() {
                    public boolean has() { return _myQyCall.has() && _myQyCall.qy().hasConditionQueryMember(); }
                    public MemberCQ qy() { return _myQyCall.qy().queryMember(); } }
                    , _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, _dbmetaProvider);
            }
            return _member;
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
    protected String getConditionBeanClassNameInternally() { return MemberSecurityCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberSecurityCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
