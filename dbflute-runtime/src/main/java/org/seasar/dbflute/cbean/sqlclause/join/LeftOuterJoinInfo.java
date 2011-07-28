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
    protected String _foreignAliasName; // unique key for this info
    protected String _foreignTableDbName;
    protected String _localAliasName;
    protected String _localTableDbName;

    // join condition and info
    protected Map<ColumnRealName, ColumnRealName> _joinOnMap;
    protected LeftOuterJoinInfo _localJoinInfo; // to be able to trace back toward base point

    // foreign key info
    protected boolean _pureFK; // has foreign key constraint (not additional, not referrer)
    protected boolean _notNullFKColumn; // local column for foreign key has not null constraint

    // query for join
    protected final List<QueryClause> _inlineWhereClauseList = new ArrayList<QueryClause>();
    protected final List<QueryClause> _additionalOnClauseList = new ArrayList<QueryClause>();

    // fixed condition
    protected String _fixedCondition;
    protected transient FixedConditionResolver _fixedConditionResolver;

    // additional join attribute
    protected boolean _innerJoin; // option (true if inner-join forced or auto-detected)
    protected boolean _underInnerJoin; // option (true if the join has foreign's inner-join)
    protected boolean _whereUsedJoin; // option (true if used on where clause or foreign's use)

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    // -----------------------------------------------------
    //                                      In-line/OrClause
    //                                      ----------------
    public boolean hasInlineOrOnClause() {
        return !_inlineWhereClauseList.isEmpty() || !_additionalOnClauseList.isEmpty();
    }

    // -----------------------------------------------------
    //                                        FixedCondition
    //                                        --------------
    public boolean hasFixedCondition() {
        return _fixedCondition != null && _fixedCondition.trim().length() > 0;
    }

    public void resolveFixedCondition() { // required before using fixed-condition
        if (hasFixedCondition() && _fixedConditionResolver != null) {
            _fixedCondition = _fixedConditionResolver.resolveVariable(_fixedCondition);
        }
    }

    public String resolveFixedInlineView(String foreignTableSqlName) {
        if (hasFixedCondition() && _fixedConditionResolver != null) {
            return _fixedConditionResolver.resolveFixedInlineView(foreignTableSqlName);
        }
        return foreignTableSqlName.toString();
    }

    // -----------------------------------------------------
    //                                             Countable
    //                                             ---------
    public boolean isCountableJoin() { // called when building clause
        return isInnerJoin() || isUnderInnerJoin() || isWhereUsedJoin();
    }

    // -----------------------------------------------------
    //                                             InnerJoin
    //                                             ---------
    public boolean isStructuralPossibleInnerJoin() { // called when building clause
        if (!isPureStructuralPossibleInnerJoin()) {
            return false;
        }
        LeftOuterJoinInfo current = _localJoinInfo;
        while (true) {
            if (current == null) { // means first level (not nested) join
                break;
            }
            // means nested join (e.g. SERVICE_RANK if MEMBER is base point)
            if (!current.isTraceStructuralPossibleInnerJoin()) {
                return false;
            }
            current = current.getLocalJoinInfo();
        }
        return true;
    }

    protected boolean isPureStructuralPossibleInnerJoin() {
        return !hasInlineOrOnClause() && _pureFK && _notNullFKColumn;
    }

    protected boolean isTraceStructuralPossibleInnerJoin() {
        // more pattern may exist but it save logic simple for safety
        return isInnerJoin() || isPureStructuralPossibleInnerJoin();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getForeignAliasName() {
        return _foreignAliasName;
    }

    public void setForeignAliasName(String foreignAliasName) {
        _foreignAliasName = foreignAliasName;
    }

    public String getForeignTableDbName() {
        return _foreignTableDbName;
    }

    public void setForeignTableDbName(String foreignTableDbName) {
        _foreignTableDbName = foreignTableDbName;
    }

    public String getLocalAliasName() {
        return _localAliasName;
    }

    public void setLocalAliasName(String localAliasName) {
        _localAliasName = localAliasName;
    }

    public String getLocalTableDbName() {
        return _localTableDbName;
    }

    public void setLocalTableDbName(String localTableDbName) {
        _localTableDbName = localTableDbName;
    }

    public Map<ColumnRealName, ColumnRealName> getJoinOnMap() {
        return _joinOnMap;
    }

    public void setJoinOnMap(Map<ColumnRealName, ColumnRealName> joinOnMap) {
        _joinOnMap = joinOnMap;
    }

    public LeftOuterJoinInfo getLocalJoinInfo() {
        return _localJoinInfo;
    }

    public void setLocalJoinInfo(LeftOuterJoinInfo localJoinInfo) {
        _localJoinInfo = localJoinInfo;
    }

    public boolean getPureFK() {
        return _pureFK;
    }

    public void setPureFK(boolean pureFK) {
        this._pureFK = pureFK;
    }

    public boolean getNotNullFKColumn() {
        return _notNullFKColumn;
    }

    public void setNotNullFKColumn(boolean notNullFKColumn) {
        this._notNullFKColumn = notNullFKColumn;
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

    public String getFixedCondition() {
        return _fixedCondition;
    }

    public void setFixedCondition(String fixedCondition) {
        _fixedCondition = fixedCondition;
    }

    public FixedConditionResolver getFixedConditionResolver() {
        return _fixedConditionResolver;
    }

    public void setFixedConditionResolver(FixedConditionResolver fixedConditionResolver) {
        _fixedConditionResolver = fixedConditionResolver;
    }

    public boolean isInnerJoin() {
        return _innerJoin;
    }

    public void setInnerJoin(boolean innerJoin) {
        _innerJoin = innerJoin;
    }

    public boolean isUnderInnerJoin() {
        return _underInnerJoin;
    }

    public void setUnderInnerJoin(boolean underInnerJoin) {
        _underInnerJoin = underInnerJoin;
    }

    public boolean isWhereUsedJoin() {
        return _whereUsedJoin;
    }

    public void setWhereUsedJoin(boolean whereUsedJoin) {
        _whereUsedJoin = whereUsedJoin;
    }
}
