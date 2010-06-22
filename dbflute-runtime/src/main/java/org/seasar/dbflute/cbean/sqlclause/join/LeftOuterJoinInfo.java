package org.seasar.dbflute.cbean.sqlclause.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;

/**
 * @author jflute
 */
public class LeftOuterJoinInfo implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _aliasName;
    protected String _baseTableDbName;
    protected String _joinTableDbName;
    protected final List<QueryClause> _inlineWhereClauseList = new ArrayList<QueryClause>();
    protected final List<QueryClause> _additionalOnClauseList = new ArrayList<QueryClause>();
    protected Map<ColumnRealName, ColumnRealName> _joinOnMap;
    protected String _fixedCondition;
    protected boolean _innerJoin;

    // ===================================================================================
    //                                                                        Judge Status
    //                                                                        ============
    public boolean hasInlineOrOnClause() {
        return !_inlineWhereClauseList.isEmpty() || !_additionalOnClauseList.isEmpty();
    }

    public boolean hasFixedCondition() {
        return _fixedCondition != null && _fixedCondition.trim().length() > 0;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAliasName() {
        return _aliasName;
    }

    public void setAliasName(String aliasName) {
        _aliasName = aliasName;
    }

    public String getBaseTableDbName() {
        return _baseTableDbName;
    }

    public void setBaseTableDbName(String baseTableDbName) {
        _baseTableDbName = baseTableDbName;
    }

    public String getJoinTableDbName() {
        return _joinTableDbName;
    }

    public void setJoinTableDbName(String joinTableDbName) {
        _joinTableDbName = joinTableDbName;
    }

    public List<QueryClause> getInlineWhereClauseList() {
        return _inlineWhereClauseList;
    }

    public void addInlineWhereClause(QueryClause inlineWhereClause) {
        _inlineWhereClauseList.add(inlineWhereClause);
    }

    public List<QueryClause> getAdditionalOnClauseList() {
        return _additionalOnClauseList;
    }

    public void addAdditionalOnClause(QueryClause additionalOnClause) {
        _additionalOnClauseList.add(additionalOnClause);
    }

    public Map<ColumnRealName, ColumnRealName> getJoinOnMap() {
        return _joinOnMap;
    }

    public void setJoinOnMap(Map<ColumnRealName, ColumnRealName> joinOnMap) {
        _joinOnMap = joinOnMap;
    }

    public String getFixedCondition() {
        return _fixedCondition;
    }

    public void setFixedCondition(String fixedCondition) {
        _fixedCondition = fixedCondition;
    }

    public boolean isInnerJoin() {
        return _innerJoin;
    }

    public void setInnerJoin(boolean innerJoin) {
        _innerJoin = innerJoin;
    }
}
