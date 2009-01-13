package com.example.dbflute.basic.dbflute.cbean.bs;

import java.util.Map;

import org.dbflute.cbean.AbstractConditionBean;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.SubQuery;
import org.dbflute.cbean.UnionQuery;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.DBMetaProvider;

import com.example.dbflute.basic.dbflute.allcommon.DBFluteConfig;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.allcommon.ImplementedSqlClauseCreator;
import com.example.dbflute.basic.dbflute.cbean.*;
import com.example.dbflute.basic.dbflute.cbean.cq.*;
import com.example.dbflute.basic.dbflute.cbean.nss.*;

/**
 * The base condition-bean of MEMBER_LOGIN.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberLoginCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected MemberLoginCQ _conditionQuery;

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
        return "MEMBER_LOGIN";
    }

    public String getTableSqlName() {
        return "MEMBER_LOGIN";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("MEMBER_LOGIN_ID");
            if (obj instanceof Long) {
                query().setMemberLoginId_Equal((Long)obj);
            } else {
                query().setMemberLoginId_Equal(new Long((String)obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_MemberLoginId_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_MemberLoginId_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public MemberLoginCQ query() {
        return getConditionQuery();
    }

    public MemberLoginCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new MemberLoginCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;MemberLoginCB&gt;() {
     *     public void query(MemberLoginCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<MemberLoginCB> unionQuery) {
        final MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberLoginCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;MemberLoginCB&gt;() {
     *     public void query(MemberLoginCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<MemberLoginCB> unionQuery) {
        final MemberLoginCB cb = new MemberLoginCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberLoginCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
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

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;
    public Specification specify() {
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<MemberLoginCQ>() {
            public boolean has() { return true; } public MemberLoginCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<MemberLoginCQ> {
        protected SpQyCall<MemberLoginCQ> _myQyCall;
        protected MemberCB.Specification _member;
        protected MemberStatusCB.Specification _memberStatus;
        public Specification(ConditionBean baseCB, SpQyCall<MemberLoginCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnMemberLoginId() { doColumn("MEMBER_LOGIN_ID"); }
        public void columnMemberId() { doColumn("MEMBER_ID"); }
        public void columnLoginDatetime() { doColumn("LOGIN_DATETIME"); }
        public void columnLoginMobileFlg() { doColumn("LOGIN_MOBILE_FLG"); }
        public void columnLoginMemberStatusCode() { doColumn("LOGIN_MEMBER_STATUS_CODE"); }
        protected void doSpecifyRequiredColumn() {
            columnMemberLoginId();// PK
            if (_myQyCall.qy().hasConditionQueryMember()) {
                columnMemberId();// FK
            }
            if (_myQyCall.qy().hasConditionQueryMemberStatus()) {
                columnLoginMemberStatusCode();// FK
            }
        }
        protected String getTableDbName() { return "MEMBER_LOGIN"; }
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
    protected String getConditionBeanClassNameInternally() { return MemberLoginCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberLoginCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
