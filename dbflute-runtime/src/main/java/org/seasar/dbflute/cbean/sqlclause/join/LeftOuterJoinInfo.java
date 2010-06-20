package org.seasar.dbflute.cbean.sqlclause.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.dbmeta.name.ColumnRealName;

/**
 * @author jflute
 */
public class LeftOuterJoinInfo {

    protected String _aliasName;
    protected String _baseTableDbName;
    protected String _joinTableDbName;
    protected final List<String> _inlineWhereClauseList = new ArrayList<String>();
    protected final List<String> _additionalOnClauseList = new ArrayList<String>();
    protected Map<ColumnRealName, ColumnRealName> _joinOnMap;
    protected String _fixedCondition;
    protected boolean _innerJoin;

    public boolean hasInlineOrOnClause() {
        return !_inlineWhereClauseList.isEmpty() || !_additionalOnClauseList.isEmpty();
    }

    public boolean hasFixedCondition() {
        return _fixedCondition != null && _fixedCondition.trim().length() > 0;
    }

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

    public List<String> getInlineWhereClauseList() {
        return _inlineWhereClauseList;
    }

    public void addInlineWhereClause(String inlineWhereClause) {
        _inlineWhereClauseList.add(inlineWhereClause);
    }

    public List<String> getAdditionalOnClauseList() {
        return _additionalOnClauseList;
    }

    public void addAdditionalOnClause(String additionalOnClause) {
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
