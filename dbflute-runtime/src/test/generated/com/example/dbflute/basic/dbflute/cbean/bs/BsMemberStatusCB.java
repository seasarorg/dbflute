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

/**
 * The base condition-bean of MEMBER_STATUS.
 * @author DBFlute(AutoGenerator)
 */
public class BsMemberStatusCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();
    protected MemberStatusCQ _conditionQuery;

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
        return "MEMBER_STATUS";
    }

    public String getTableSqlName() {
        return "MEMBER_STATUS";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("MEMBER_STATUS_CODE");
            if (obj instanceof String) {
                query().setMemberStatusCode_Equal((String)obj);
            } else {
                checkTypeString(obj, "memberStatusCode", "String");
                query().setMemberStatusCode_Equal((String)obj);
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_MemberStatusCode_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_MemberStatusCode_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public MemberStatusCQ query() {
        return getConditionQuery();
    }

    public MemberStatusCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new MemberStatusCQ(null, getSqlClause(), getSqlClause().getLocalTableAliasName(), 0);
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
     * cb.query().union(new UnionQuery&lt;MemberStatusCB&gt;() {
     *     public void query(MemberStatusCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<MemberStatusCB> unionQuery) {
        final MemberStatusCB cb = new MemberStatusCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberStatusCQ cq = cb.query(); query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;MemberStatusCB&gt;() {
     *     public void query(MemberStatusCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<MemberStatusCB> unionQuery) {
        final MemberStatusCB cb = new MemberStatusCB(); cb.xsetupForUnion(); unionQuery.query(cb);
        final MemberStatusCQ cq = cb.query(); query().xsetUnionAllQuery(cq);
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
        if (_specification == null) { _specification = new Specification(this, new SpQyCall<MemberStatusCQ>() {
            public boolean has() { return true; } public MemberStatusCQ qy() { return query(); } }, _forDerivedReferrer, _forScalarSelect, _forScalarSubQuery, getDBMetaProvider()); }
        return _specification;
    }

    public static class Specification extends AbstractSpecification<MemberStatusCQ> {
        protected SpQyCall<MemberStatusCQ> _myQyCall;
        public Specification(ConditionBean baseCB, SpQyCall<MemberStatusCQ> qyCall
                           , boolean forDeriveReferrer, boolean forScalarSelect, boolean forScalarSubQuery
                           , DBMetaProvider dbmetaProvider)
        { super(baseCB, qyCall, forDeriveReferrer, forScalarSelect, forScalarSubQuery, dbmetaProvider); _myQyCall = qyCall; }
        public void columnMemberStatusCode() { doColumn("MEMBER_STATUS_CODE"); }
        public void columnMemberStatusName() { doColumn("MEMBER_STATUS_NAME"); }
        public void columnDisplayOrder() { doColumn("DISPLAY_ORDER"); }
        protected void doSpecifyRequiredColumn() {
            columnMemberStatusCode();// PK
        }
        protected String getTableDbName() { return "MEMBER_STATUS"; }
        public RAFunction<MemberCB, MemberStatusCQ> derivedMemberList() {
            return new RAFunction<MemberCB, MemberStatusCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<MemberCB, MemberStatusCQ>() {
                public void setup(String function, SubQuery<MemberCB> subQuery, MemberStatusCQ cq, String aliasName) {
                    cq.xsderiveMemberList(function, subQuery, aliasName); } }, _dbmetaProvider);
        }
        public RAFunction<MemberLoginCB, MemberStatusCQ> derivedMemberLoginList() {
            return new RAFunction<MemberLoginCB, MemberStatusCQ>(_baseCB, _myQyCall.qy(), new RAQSetupper<MemberLoginCB, MemberStatusCQ>() {
                public void setup(String function, SubQuery<MemberLoginCB> subQuery, MemberStatusCQ cq, String aliasName) {
                    cq.xsderiveMemberLoginList(function, subQuery, aliasName); } }, _dbmetaProvider);
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
    protected String getConditionBeanClassNameInternally() { return MemberStatusCB.class.getName(); }
    protected String getConditionQueryClassNameInternally() { return MemberStatusCQ.class.getName(); }
    protected String getSubQueryClassNameInternally() { return SubQuery.class.getName(); }
}
