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
 * The base condition-bean of MEMBER_ADDRESS.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberAddressCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected MemberAddressCQ _conditionQuery;

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
        return "MEMBER_ADDRESS";
    }

    public String getTableSqlName() {
        return "MEMBER_ADDRESS";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("MEMBER_ADDRESS_ID");
            if (obj instanceof Integer) {
                query().setMemberAddressId_Equal((Integer)obj);
            } else {
                query().setMemberAddressId_Equal(new Integer((String)obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_MemberAddressId_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_MemberAddressId_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public MemberAddressCQ query() {
        return getConditionQuery();
    }

    public MemberAddressCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new MemberAddressCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;MemberAddressCB&gt;() {
     *     public void query(MemberAddressCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<MemberAddressCB> unionQuery) {
        final MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberAddressCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;MemberAddressCB&gt;() {
     *     public void query(MemberAddressCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<MemberAddressCB> unionQuery) {
        final MemberAddressCB cb = new MemberAddressCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberAddressCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
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
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<MemberAddressCQ>() {
            public boolean has() { return true; } public MemberAddressCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<MemberAddressCQ> {
        protected SpQyCall<MemberAddressCQ> _myQyCall;
        protected MemberCB.Specification _member;
        public Specification(ConditionBean baseCB, SpQyCall<MemberAddressCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnMemberAddressId() { doColumn("MEMBER_ADDRESS_ID"); }
        public void columnMemberId() { doColumn("MEMBER_ID"); }
        public void columnValidBeginDate() { doColumn("VALID_BEGIN_DATE"); }
        public void columnValidEndDate() { doColumn("VALID_END_DATE"); }
        public void columnAddress() { doColumn("ADDRESS"); }
        public void columnRegisterDatetime() { doColumn("REGISTER_DATETIME"); }
        public void columnRegisterProcess() { doColumn("REGISTER_PROCESS"); }
        public void columnRegisterUser() { doColumn("REGISTER_USER"); }
        public void columnUpdateDatetime() { doColumn("UPDATE_DATETIME"); }
        public void columnUpdateProcess() { doColumn("UPDATE_PROCESS"); }
        public void columnUpdateUser() { doColumn("UPDATE_USER"); }
        public void columnVersionNo() { doColumn("VERSION_NO"); }
        protected void doSpecifyRequiredColumn() {
            columnMemberAddressId();// PK
            if (_myQyCall.qy().hasConditionQueryMember()) {
                columnMemberId();// FK
            }
        }
        protected String getTableDbName() { return "MEMBER_ADDRESS"; }
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
    protected String getConditionBeanClassNameInternally() { return MemberAddressCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberAddressCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
