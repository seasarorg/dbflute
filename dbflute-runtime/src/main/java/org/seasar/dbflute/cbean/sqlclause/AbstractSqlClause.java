/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.cbean.sqlclause;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.OrderByClause.ManumalOrderInfo;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.util.DfAssertUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The abstract class of SQL clause.
 * @author jflute
 */
public abstract class AbstractSqlClause implements SqlClause, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    protected static final SelectClauseType DEFAULT_SELECT_CLAUSE_TYPE = SelectClauseType.COLUMNS;
    protected static final String SELECT_HINT = "/*$pmb.selectHint*/";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /** The DB name of table. */
    protected final String _tableDbName;

    /** The DB meta of table. (basically NotNull: null only when treated as dummy) */
    protected DBMeta _dbmeta;

    /** The DB meta of target table. (basically NotNull: null only when treated as dummy) */
    protected DBMetaProvider _dbmetaProvider;

    /** The cache map of DB meta for basically related tables. */
    protected final Map<String, DBMeta> _cachedDBMetaMap = StringKeyMap.createAsFlexible();

    // -----------------------------------------------------
    //                                       Clause Resource
    //                                       ---------------
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // The resources that are not often used to are lazy-loaded for performance.
    // - - - - - - - - - -/
    /** Selected select column map. map:{tableAliasName : map:{columnName : selectColumnInfo}} */
    protected final Map<String, Map<String, SelectedSelectColumnInfo>> _selectedSelectColumnMap = new LinkedHashMap<String, Map<String, SelectedSelectColumnInfo>>();

    /** Specified select column map. map:{ tableAliasName = map:{ columnName : null } } (Nullable: This is lazy-loaded) */
    protected Map<String, Map<String, String>> _specifiedSelectColumnMap; // [DBFlute-0.7.4]

    /** Specified select column map for backup. map:{ tableAliasName = map:{ columnName : null } } (Nullable: This is lazy-loaded) */
    protected Map<String, Map<String, String>> _backupSpecifiedSelectColumnMap; // [DBFlute-0.9.5.3]

    /** Specified derive sub-query map. (Nullable: This is lazy-loaded) */
    protected Map<String, String> _specifiedDerivingSubQueryMap; // [DBFlute-0.7.4]

    /** The map of real column and alias of select clause. map:{realColumnName : aliasName} */
    protected final Map<String, String> _selectClauseRealColumnAliasMap = new HashMap<String, String>(); // order no needed

    /** The type of select clause. (NotNull) */
    protected SelectClauseType _selectClauseType = DEFAULT_SELECT_CLAUSE_TYPE;

    /** The previous type of select clause. (Nullable: The default is null) */
    protected SelectClauseType _previousSelectClauseType;

    /** The map of select index. {key:columnName, value:selectIndex} (Nullable) */
    protected Map<String, Integer> _selectIndexMap;

    /** The reverse map of select index. {key:selectIndex, value:columnName} (Nullable) */
    protected Map<String, String> _selectIndexReverseMap;

    /** Is use select index? Default value is true. */
    protected boolean _useSelectIndex = true;

    /** The map of outer join. */
    protected final Map<String, LeftOuterJoinInfo> _outerJoinMap = new LinkedHashMap<String, LeftOuterJoinInfo>();

    /** Is inner-join effective? Default value is false. */
    protected boolean _innerJoinEffective = false;

    /** The list of where clause. */
    protected final List<String> _whereList = new ArrayList<String>();

    /** The list of in-line where clause for base table. */
    protected final List<String> _baseTableInlineWhereList = new ArrayList<String>(2); // because of minor

    /** The clause of order-by. (NotNull) */
    protected final OrderByClause _orderByClause = new OrderByClause();

    /** The list of union clause. (Nullable: This is lazy-loaded) */
    protected List<UnionQueryInfo> _unionQueryInfoList;

    /** Is order-by effective? Default value is false. */
    protected boolean _orderByEffective = false;

    // -----------------------------------------------------
    //                                        Fetch Property
    //                                        --------------
    /** Fetch start index. (for fetchXxx()) */
    protected int _fetchStartIndex = 0;

    /** Fetch size. (for fetchXxx()) */
    protected int _fetchSize = 0;

    /** Fetch page number. (for fetchXxx()) This value should be plus. */
    protected int _fetchPageNumber = 1;

    /** Is fetch-narrowing effective? Default value is false. */
    protected boolean _fetchScopeEffective = false;

    // -----------------------------------------------------
    //                                          OrScopeQuery
    //                                          ------------
    /** Is or-query scope effective?*/
    protected boolean _orScopeQueryEffective;

    /** The current temporary information of or-query scope?*/
    protected TmpOrScopeQueryInfo _currentTmpOrScopeQueryInfo;

    /** Is or-query scope in and-part?*/
    protected boolean _orScopeQueryAndPart;

    // -----------------------------------------------------
    //                               WhereClauseSimpleFilter
    //                               -----------------------
    /** The filter for where clause. */
    protected List<WhereClauseSimpleFilter> _whereClauseSimpleFilterList;

    // -----------------------------------------------------
    //                                 Selected Foreign Info
    //                                 ---------------------
    /** The information of selected foreign table. */
    protected Map<String, String> _selectedForeignInfo;

    // -----------------------------------------------------
    //                                         Optional Info
    //                                         -------------
    protected boolean _formatClause = true;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public AbstractSqlClause(String tableDbName) {
        if (tableDbName == null) {
            String msg = "The argument 'tableDbName' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        _tableDbName = tableDbName;
    }

    /**
     * Set the provider of DB meta. <br />
     * If you want to use all functions, this method is required.
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     * @return this. (NotNull)
     */
    public SqlClause provider(DBMetaProvider dbmetaProvider) {
        if (dbmetaProvider == null) {
            String msg = "The argument 'dbmetaProvider' should not be null:";
            msg = msg + " tableDbName=" + _tableDbName;
            throw new IllegalArgumentException(msg);
        }
        _dbmetaProvider = dbmetaProvider;
        _dbmeta = findDBMeta(_tableDbName);
        return this;
    }

    // ===================================================================================
    //                                                                         Main Clause
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    public String getClause() {
        final StringBuilder sb = new StringBuilder(512);
        String selectClause = getSelectClause();
        sb.append(selectClause);
        sb.append(" ");
        buildClauseWithoutMainSelect(sb, selectClause);
        String sql = sb.toString();
        sql = filterEnclosingClause(sql);
        sql = filterSubQueryIndent(sql);
        return sql;
    }

    protected void buildClauseWithoutMainSelect(StringBuilder sb, String selectClause) {
        buildFromClause(sb);
        sb.append(getFromHint());
        sb.append(" ");
        buildWhereClause(sb);
        String unionClause = prepareUnionClause(selectClause);
        unionClause = deleteUnionWhereTemplateMark(unionClause); // required
        sb.append(unionClause);
        if (!needsUnionNormalSelectEnclosing()) {
            sb.append(prepareClauseOrderBy());
            sb.append(prepareClauseSqlSuffix());
        }
    }

    protected String deleteUnionWhereTemplateMark(String unionClause) {
        if (unionClause != null && unionClause.trim().length() > 0) {
            unionClause = replaceString(unionClause, getUnionWhereClauseMark(), "");
            unionClause = replaceString(unionClause, getUnionWhereFirstConditionMark(), "");
        }
        return unionClause;
    }

    // -----------------------------------------------------
    //                                       Fragment Clause
    //                                       ---------------
    public String getClauseFromWhereWithUnionTemplate() {
        return buildClauseFromWhereAsTemplate(false);
    }

    public String getClauseFromWhereWithWhereUnionTemplate() {
        return buildClauseFromWhereAsTemplate(true);
    }

    protected String buildClauseFromWhereAsTemplate(boolean template) {
        StringBuilder sb = new StringBuilder(256);
        buildFromClause(sb);
        sb.append(getFromHint());
        sb.append(" ");
        buildWhereClause(sb, template);
        sb.append(prepareUnionClause(getUnionSelectClauseMark()));
        return sb.toString();
    }

    protected String prepareUnionClause(String selectClause) {
        if (!hasUnionQuery()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (Iterator<UnionQueryInfo> ite = _unionQueryInfoList.iterator(); ite.hasNext();) {
            UnionQueryInfo unionQueryInfo = (UnionQueryInfo) ite.next();
            String unionQueryClause = unionQueryInfo.getUnionQueryClause();
            boolean unionAll = unionQueryInfo.isUnionAll();
            sb.append(ln());
            sb.append(unionAll ? " union all " : " union ");
            sb.append(ln());
            sb.append(selectClause).append(" ").append(unionQueryClause);
        }
        return sb.toString();
    }

    protected String prepareClauseOrderBy() {
        if (!_orderByEffective || _orderByClause.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(getOrderByClause());
        return sb.toString();
    }

    protected String prepareClauseSqlSuffix() {
        String sqlSuffix = getSqlSuffix();
        if (sqlSuffix == null || sqlSuffix.trim().length() == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(sqlSuffix);
        return sb.toString();
    }

    protected String filterEnclosingClause(String sql) {
        sql = filterUnionNormalSelectEnclosing(sql);
        sql = filterUnionCountOrScalarEnclosing(sql);
        return sql;
    }

    protected String filterUnionNormalSelectEnclosing(String sql) {
        if (!needsUnionNormalSelectEnclosing()) {
            return sql;
        }
        final String selectClause = "select" + SELECT_HINT + " *";
        final String ln = ln();
        final String beginMark = resolveSubQueryBeginMark("dfmain") + ln;
        final String endMark = resolveSubQueryEndMark("dfmain");
        String clause = selectClause + ln + "  from (" + beginMark + sql + ln + "       ) dfmain" + endMark;
        clause = clause + prepareClauseOrderBy() + prepareClauseSqlSuffix();
        return clause;
    }

    protected String filterUnionCountOrScalarEnclosing(String sql) {
        if (!needsUnionCountOrScalarEnclosing()) {
            return sql;
        }
        final String selectClause = buildSelectClauseCountOrScalar("dfmain");
        final String ln = ln();
        final String beginMark = resolveSubQueryBeginMark("dfmain") + ln;
        final String endMark = resolveSubQueryEndMark("dfmain");
        return selectClause + ln + "  from (" + beginMark + sql + ln + "       ) dfmain" + endMark;
    }

    protected boolean needsUnionNormalSelectEnclosing() {
        if (!isUnionNormalSelectEnclosingRequired()) {
            return false;
        }
        return hasUnionQuery() && !isSelectClauseTypeCountOrScalar();
    }

    protected boolean isUnionNormalSelectEnclosingRequired() { // for extension
        return false; // false as default
    }

    protected boolean needsUnionCountOrScalarEnclosing() {
        return hasUnionQuery() && isSelectClauseTypeCountOrScalar();
    }

    // ===================================================================================
    //                                                                        Clause Parts
    //                                                                        ============
    // -----------------------------------------------------
    //                                         Select Clause
    //                                         -------------
    public String getSelectClause() {
        // [DBFlute-0.8.6]
        if (isSelectClauseTypeCountOrScalar() && !hasUnionQuery()) {
            return buildSelectClauseCountOrScalar("dflocal");
        }
        // /- - - - - - - - - - - - - - - - - - - - - - - - 
        // The type of select clause is COLUMNS since here.
        // - - - - - - - - - -/
        final StringBuilder sb = new StringBuilder();
        final DBMeta dbmeta = getDBMeta();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();

        Map<String, String> localSpecifiedMap = null;
        if (_specifiedSelectColumnMap != null) {
            localSpecifiedMap = _specifiedSelectColumnMap.get(getLocalTableAliasName());
        }
        final boolean existsSpecifiedLocal = localSpecifiedMap != null && !localSpecifiedMap.isEmpty();

        Integer selectIndex = 0; // because 1 origin in JDBC
        if (_useSelectIndex) {
            _selectIndexMap = createSelectIndexMap();
        }

        // Columns of local table.
        boolean needsDelimiter = false;
        for (ColumnInfo columnInfo : columnInfoList) {
            final String columnName = columnInfo.getColumnSqlName();

            // [DBFlute-0.7.4]
            if (existsSpecifiedLocal && !localSpecifiedMap.containsKey(columnName)) {
                if (isSelectClauseTypeCountOrScalar() && hasUnionQuery()) {
                    // Here it must be with union query.
                    // So the primary Key is target for saving unique.
                    // But if it does not have primary keys, all column is target.
                    if (dbmeta.hasPrimaryKey()) {
                        if (!columnInfo.isPrimary()) {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }

            if (needsDelimiter) {
                sb.append(", ");
            } else {
                sb.append("select");
                appendSelectHint(sb);
                sb.append(" ");
                needsDelimiter = true;
            }
            final String realColumnName = getLocalTableAliasName() + "." + columnName;
            final String onQueryName;
            ++selectIndex;
            if (_useSelectIndex) {
                _selectIndexMap.put(columnName, selectIndex);
                onQueryName = buildSelectIndexAliasName(selectIndex);
            } else {
                onQueryName = columnName;
            }
            sb.append(realColumnName).append(" as ").append(onQueryName);
            _selectClauseRealColumnAliasMap.put(realColumnName, onQueryName);
        }

        // Columns of foreign tables.
        final Set<String> tableAliasNameSet = _selectedSelectColumnMap.keySet();
        for (String tableAliasName : tableAliasNameSet) {
            final Map<String, SelectedSelectColumnInfo> map = _selectedSelectColumnMap.get(tableAliasName);
            final Collection<SelectedSelectColumnInfo> selectColumnInfoList = map.values();
            Map<String, String> foreginSpecifiedMap = null;
            if (_specifiedSelectColumnMap != null) {
                foreginSpecifiedMap = _specifiedSelectColumnMap.get(tableAliasName);
            }
            final boolean existsSpecifiedForeign = foreginSpecifiedMap != null && !foreginSpecifiedMap.isEmpty();
            boolean finishedForeignIndent = false;
            for (SelectedSelectColumnInfo selectColumnInfo : selectColumnInfoList) {
                if (existsSpecifiedForeign && !foreginSpecifiedMap.containsKey(selectColumnInfo.getColumnDbName())) {
                    continue;
                }

                final String realColumnName = selectColumnInfo.buildRealColumnSqlName();
                final String columnAliasName = selectColumnInfo.getColumnAliasName();
                final String onQueryName;
                ++selectIndex;
                if (_useSelectIndex) {
                    _selectIndexMap.put(columnAliasName, selectIndex);
                    onQueryName = buildSelectIndexAliasName(selectIndex);
                } else {
                    onQueryName = columnAliasName;
                }
                if (!finishedForeignIndent) {
                    sb.append(ln()).append("     ");
                    finishedForeignIndent = true;
                }
                sb.append(", ").append(realColumnName).append(" as ").append(onQueryName);
                _selectClauseRealColumnAliasMap.put(realColumnName, onQueryName);
            }
        }

        // [DBFlute-0.7.4]
        if (_specifiedDerivingSubQueryMap != null && !_specifiedDerivingSubQueryMap.isEmpty()) {
            final Collection<String> deriveSubQuerySet = _specifiedDerivingSubQueryMap.values();
            for (String deriveSubQuery : deriveSubQuerySet) {
                sb.append(ln()).append("     ");
                sb.append(", ").append(deriveSubQuery);

                // [DBFlute-0.8.3]
                final int beginIndex = deriveSubQuery.lastIndexOf(" as ");
                if (beginIndex >= 0) { // basically true
                    String aliasName = deriveSubQuery.substring(beginIndex + " as ".length());
                    final int endIndex = aliasName.indexOf("--df:");
                    if (endIndex >= 0) { // basically true
                        aliasName = aliasName.substring(0, endIndex);
                    }
                    // for SpecifiedDerivedOrderBy
                    _selectClauseRealColumnAliasMap.put(aliasName, aliasName);
                }
            }
        }

        return sb.toString();
    }

    // -----------------------------------------------------
    //                                       Count or Scalar
    //                                       ---------------
    protected boolean isSelectClauseTypeCountOrScalar() {
        if (_selectClauseType.equals(SelectClauseType.COUNT)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.MAX)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.MIN)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.SUM)) {
            return true;
        } else if (_selectClauseType.equals(SelectClauseType.AVG)) {
            return true;
        }
        return false;
    }

    protected String buildSelectClauseCountOrScalar(String aliasName) {
        if (_selectClauseType.equals(SelectClauseType.COUNT)) {
            return buildSelectClauseCount();
        } else if (_selectClauseType.equals(SelectClauseType.MAX)) {
            return buildSelectClauseMax(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.MIN)) {
            return buildSelectClauseMin(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.SUM)) {
            return buildSelectClauseSum(aliasName);
        } else if (_selectClauseType.equals(SelectClauseType.AVG)) {
            return buildSelectClauseAvg(aliasName);
        }
        String msg = "The type of select clause is not for scalar:";
        msg = msg + " type=" + _selectClauseType;
        throw new IllegalStateException(msg);
    }

    protected String buildSelectClauseCount() {
        return "select count(*)";
    }

    protected String buildSelectClauseMax(String aliasName) {
        final String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select max(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseMin(String aliasName) {
        final String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select min(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseSum(String aliasName) {
        final String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select sum(" + aliasName + "." + columnName + ")";
    }

    protected String buildSelectClauseAvg(String aliasName) {
        final String columnName = getSpecifiedColumnNameAsOne();
        assertScalarSelectSpecifiedColumnOnlyOne(columnName);
        return "select avg(" + aliasName + "." + columnName + ")";
    }

    protected void assertScalarSelectSpecifiedColumnOnlyOne(String columnName) {
        if (columnName != null) {
            return;
        }
        String msg = "The specified column exists one";
        msg = msg + " when the type of select clause is for scalar:";
        msg = msg + " specifiedSelectColumnMap=" + _specifiedSelectColumnMap;
        throw new IllegalStateException(msg);
    }

    // -----------------------------------------------------
    //                                          Select Index
    //                                          ------------
    public Map<String, Integer> getSelectIndexMap() {
        return _selectIndexMap;
    }

    public Map<String, String> getSelectIndexReverseMap() {
        if (_selectIndexReverseMap != null) {
            return _selectIndexReverseMap;
        }
        if (_selectIndexMap == null) {
            return null;
        }
        _selectIndexReverseMap = createSelectIndexMap(); // same style as select index map
        final Set<Entry<String, Integer>> entrySet = _selectIndexMap.entrySet();
        for (Entry<String, Integer> entry : entrySet) {
            final String columnName = entry.getKey();
            final Integer selectIndex = entry.getValue();
            _selectIndexReverseMap.put(buildSelectIndexAliasName(selectIndex), columnName);
        }
        return _selectIndexReverseMap;
    }

    protected <VALUE> Map<String, VALUE> createSelectIndexMap() {
        // flexible for resolving non-compilable connectors and reservation words
        // (and it does not need to be ordered)
        return StringKeyMap.createAsFlexible();
    }

    protected String buildSelectIndexAliasName(Integer selectIndex) {
        return "c" + selectIndex;
    }

    public void disableSelectIndex() {
        _useSelectIndex = false;
    }

    // -----------------------------------------------------
    //                                           Select Hint
    //                                           -----------
    public String getSelectHint() {
        return createSelectHint();
    }

    protected void appendSelectHint(StringBuilder sb) { // for extension
        sb.append(SELECT_HINT);
    }

    // -----------------------------------------------------
    //                                           From Clause
    //                                           -----------
    public String getFromClause() {
        final StringBuilder sb = new StringBuilder();
        buildFromClause(sb);
        return sb.toString();
    }

    protected void buildFromClause(StringBuilder sb) {
        sb.append(ln()).append("  ");
        sb.append("from ");
        if (isJoinInParentheses()) {
            for (int i = 0; i < _outerJoinMap.size(); i++) {
                sb.append("(");
            }
        }
        final String tableSqlName = getDBMeta().getTableSqlName();
        if (_baseTableInlineWhereList.isEmpty()) {
            sb.append(tableSqlName).append(" dflocal");
        } else {
            sb.append(getInlineViewClause(tableSqlName, _baseTableInlineWhereList)).append(" dflocal");
        }
        sb.append(getFromBaseTableHint());
        sb.append(getLeftOuterJoinClause());
    }

    protected String getLeftOuterJoinClause() {
        final String fixedConditionKey = getFixedConditionKey();
        final StringBuilder sb = new StringBuilder();
        final Set<Entry<String, LeftOuterJoinInfo>> outerJoinSet = _outerJoinMap.entrySet();
        for (Entry<String, LeftOuterJoinInfo> outerJoinEntry : outerJoinSet) {
            final String aliasName = outerJoinEntry.getKey();
            final LeftOuterJoinInfo joinInfo = outerJoinEntry.getValue();
            final String joinTableDbName = joinInfo.getJoinTableDbName();
            final Map<String, String> joinOnMap = joinInfo.getJoinOnMap();
            assertJoinOnMapNotEmpty(joinOnMap, aliasName);

            sb.append(ln()).append("   ");
            if (joinInfo.isInnerJoin()) {
                sb.append(" inner join ");
            } else {
                sb.append(" left outer join "); // is main!
            }
            final DBMeta joinDBMeta = findDBMeta(joinTableDbName);
            final String joinTableSqlName = joinDBMeta.getTableSqlName();
            final List<String> inlineWhereClauseList = joinInfo.getInlineWhereClauseList();
            if (inlineWhereClauseList.isEmpty()) {
                sb.append(joinTableSqlName);
            } else {
                sb.append(getInlineViewClause(joinTableSqlName, inlineWhereClauseList));
            }
            sb.append(" ").append(aliasName);
            if (joinInfo.hasInlineOrOnClause() || joinOnMap.containsKey(fixedConditionKey)) {
                sb.append(ln()).append("     "); // only when additional conditions exist
            }
            sb.append(" on ");
            int count = 0;
            final Set<Entry<String, String>> joinOnSet = joinOnMap.entrySet();
            for (Entry<String, String> joinOnEntry : joinOnSet) {
                final String localColumnName = joinOnEntry.getKey();
                final String foreignColumnName = joinOnEntry.getValue();
                if (localColumnName.equals(fixedConditionKey)) { // if fixed condition
                    if (count > 0) { // basically true because basic join-condition exists before
                        sb.append(ln()).append("    ");
                        sb.append(" and ");
                    }
                    sb.append(foreignColumnName); // foreignColumnName has just conditions
                } else {
                    if (count > 0) {
                        sb.append(" and ");
                    }
                    final DBMeta baseDBMeta = findDBMeta(joinInfo.getBaseTableDbName());
                    final String localSqlName = toSqlName(baseDBMeta, localColumnName);
                    final String foreignSqlName = toSqlName(joinDBMeta, foreignColumnName);
                    sb.append(localSqlName).append(" = ").append(foreignSqlName);
                }
                ++count;
            }
            final List<String> additionalOnClauseList = joinInfo.getAdditionalOnClauseList();
            for (String additionalOnClause : additionalOnClauseList) {
                sb.append(ln()).append("    ");
                sb.append(" and ").append(additionalOnClause);
            }
            if (isJoinInParentheses()) {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    protected boolean isJoinInParentheses() { // for DBMS that needs to join in parentheses
        return false; // as default
    }

    protected String getInlineViewClause(String joinTableName, List<String> inlineWhereClauseList) {
        StringBuilder sb = new StringBuilder();
        sb.append("(select * from ").append(joinTableName).append(" where ");
        int count = 0;
        for (final Iterator<String> ite = inlineWhereClauseList.iterator(); ite.hasNext();) {
            String clauseElement = ite.next();
            clauseElement = filterWhereClauseSimply(clauseElement);
            if (count > 0) {
                sb.append(" and ");
            }
            sb.append(clauseElement);
            ++count;
        }
        sb.append(")");
        return sb.toString();
    }

    public String getFromBaseTableHint() {
        return createFromBaseTableHint();
    }

    // -----------------------------------------------------
    //                                             From Hint
    //                                             ---------
    public String getFromHint() {
        return createFromHint();
    }

    // -----------------------------------------------------
    //                                          Where Clause
    //                                          ------------
    public String getWhereClause() {
        StringBuilder sb = new StringBuilder();
        buildWhereClause(sb);
        return sb.toString();
    }

    protected void buildWhereClause(StringBuilder sb) {
        buildWhereClause(sb, false);
    }

    protected void buildWhereClause(StringBuilder sb, boolean template) {
        if (_whereList.isEmpty()) {
            if (template) {
                sb.append(getWhereClauseMark());
            }
            return;
        }
        int count = 0;
        for (Iterator<String> ite = _whereList.iterator(); ite.hasNext(); count++) {
            String clauseElement = (String) ite.next();
            clauseElement = filterWhereClauseSimply(clauseElement);
            if (count == 0) {
                sb.append(ln()).append(" ");
                sb.append("where ").append(template ? getWhereFirstConditionMark() : "").append(clauseElement);
            } else {
                sb.append(ln()).append("  ");
                sb.append(" and ").append(clauseElement);
            }
        }
    }

    // -----------------------------------------------------
    //                                        OrderBy Clause
    //                                        --------------
    public String getOrderByClause() {
        String orderByClause = null;
        if (hasUnionQuery()) {
            if (_selectClauseRealColumnAliasMap == null || _selectClauseRealColumnAliasMap.isEmpty()) {
                String msg = "The selectClauseColumnAliasMap should not be null or empty when union query exists.";
                throw new IllegalStateException(msg);
            }
            orderByClause = _orderByClause.getOrderByClause(_selectClauseRealColumnAliasMap);
        } else {
            orderByClause = _orderByClause.getOrderByClause();
        }
        if (orderByClause != null && orderByClause.trim().length() > 0) {
            return ln() + " " + orderByClause;
        } else {
            return orderByClause;
        }
    }

    // -----------------------------------------------------
    //                                            SQL Suffix
    //                                            ----------
    public String getSqlSuffix() {
        String sqlSuffix = createSqlSuffix();
        if (sqlSuffix != null && sqlSuffix.trim().length() > 0) {
            return ln() + sqlSuffix;
        } else {
            return sqlSuffix;
        }
    }

    // ===================================================================================
    //                                                                SelectedSelectColumn
    //                                                                ====================
    /**
     * Register selected select column.
     * 
     * @param foreignTableAliasName The alias name of foreign table. (NotNull)
     * @param localTableName The table name of local. (NotNull)
     * @param foreignPropertyName The property name of foreign table. (NotNull)
     * @param localRelationPath The path of local relation. (Nullable)
     */
    public void registerSelectedSelectColumn(String foreignTableAliasName, String localTableName,
            String foreignPropertyName, String localRelationPath) {
        _selectedSelectColumnMap.put(foreignTableAliasName, createSelectedSelectColumnInfo(foreignTableAliasName,
                localTableName, foreignPropertyName, localRelationPath));
    }

    protected Map<String, SelectedSelectColumnInfo> createSelectedSelectColumnInfo(String foreignTableAliasName,
            String localTableName, String foreignPropertyName, String localRelationPath) {
        final DBMeta dbmeta = findDBMeta(localTableName);
        final ForeignInfo foreignInfo = dbmeta.findForeignInfo(foreignPropertyName);
        final int relationNo = foreignInfo.getRelationNo();
        String nextRelationPath = "_" + relationNo;
        if (localRelationPath != null) {
            nextRelationPath = localRelationPath + nextRelationPath;
        }
        final Map<String, SelectedSelectColumnInfo> resultMap = new LinkedHashMap<String, SelectedSelectColumnInfo>();
        final DBMeta foreignDBMeta = foreignInfo.getForeignDBMeta();
        final List<ColumnInfo> columnInfoList = foreignDBMeta.getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            final String columnDbName = columnInfo.getColumnDbName();
            final String columnSqlName = columnInfo.getColumnSqlName();
            final SelectedSelectColumnInfo selectColumnInfo = new SelectedSelectColumnInfo();
            selectColumnInfo.setTableAliasName(foreignTableAliasName);
            selectColumnInfo.setColumnDbName(columnDbName);
            selectColumnInfo.setColumnSqlName(columnSqlName);
            selectColumnInfo.setColumnAliasName(columnDbName + nextRelationPath);
            resultMap.put(columnDbName, selectColumnInfo);
        }
        return resultMap;
    }

    public static class SelectedSelectColumnInfo {
        protected String tableAliasName;
        protected String columnDbName;
        protected String columnSqlName;
        protected String columnAliasName;

        public String buildRealColumnSqlName() {
            if (tableAliasName != null) {
                return tableAliasName + "." + columnSqlName;
            } else {
                return columnSqlName;
            }
        }

        public String getTableAliasName() {
            return tableAliasName;
        }

        public void setTableAliasName(String tableAliasName) {
            this.tableAliasName = tableAliasName;
        }

        public String getColumnDbName() {
            return columnDbName;
        }

        public void setColumnDbName(String columnName) {
            this.columnDbName = columnName;
        }

        public String getColumnSqlName() {
            return columnSqlName;
        }

        public void setColumnSqlName(String columnSqlName) {
            this.columnSqlName = columnSqlName;
        }

        public String getColumnAliasName() {
            return columnAliasName;
        }

        public void setColumnAliasName(String columnAliasName) {
            this.columnAliasName = columnAliasName;
        }
    }

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public void registerOuterJoin(String baseTableDbName, String joinTableDbName, String aliasName,
            Map<String, String> joinOnMap) {
        assertAlreadyOuterJoin(aliasName);
        assertJoinOnMapNotEmpty(joinOnMap, aliasName);
        final LeftOuterJoinInfo joinInfo = new LeftOuterJoinInfo();
        joinInfo.setAliasName(aliasName);
        joinInfo.setBaseTableDbName(baseTableDbName);
        joinInfo.setJoinTableDbName(joinTableDbName);
        joinInfo.setJoinOnMap(joinOnMap);
        if (_innerJoinEffective) { // basically false
            joinInfo.setInnerJoin(true);
        }
        _outerJoinMap.put(aliasName, joinInfo);
    }

    /**
     * {@inheritDoc}
     */
    public void changeToInnerJoin(String aliasName) {
        final LeftOuterJoinInfo joinInfo = _outerJoinMap.get(aliasName);
        if (joinInfo == null) {
            String msg = "The aliasName should be registered:";
            msg = msg + " aliasName=" + aliasName + " outerJoinMap=" + _outerJoinMap.keySet();
            throw new IllegalStateException(msg);
        }
        joinInfo.setInnerJoin(true);
    }

    public SqlClause makeInnerJoinEffective() {
        _innerJoinEffective = true;
        return this;
    }

    public SqlClause backToOuterJoin() {
        _innerJoinEffective = false;
        return this;
    }

    public String getFixedConditionKey() {
        return "$$fixedCondition$$";
    }

    protected static class LeftOuterJoinInfo {
        protected String _aliasName;
        protected String _baseTableDbName;
        protected String _joinTableDbName;
        protected final List<String> _inlineWhereClauseList = new ArrayList<String>();
        protected final List<String> _additionalOnClauseList = new ArrayList<String>();
        protected Map<String, String> _joinOnMap;
        protected boolean _innerJoin;

        public boolean hasInlineOrOnClause() {
            return !_inlineWhereClauseList.isEmpty() || !_additionalOnClauseList.isEmpty();
        }

        public String getAliasName() {
            return _aliasName;
        }

        public void setAliasName(String value) {
            _aliasName = value;
        }

        public String getBaseTableDbName() {
            return _baseTableDbName;
        }

        public void setBaseTableDbName(String value) {
            _baseTableDbName = value;
        }

        public String getJoinTableDbName() {
            return _joinTableDbName;
        }

        public void setJoinTableDbName(String value) {
            _joinTableDbName = value;
        }

        public List<String> getInlineWhereClauseList() {
            return _inlineWhereClauseList;
        }

        public void addInlineWhereClause(String value) {
            _inlineWhereClauseList.add(value);
        }

        public List<String> getAdditionalOnClauseList() {
            return _additionalOnClauseList;
        }

        public void addAdditionalOnClause(String value) {
            _additionalOnClauseList.add(value);
        }

        public Map<String, String> getJoinOnMap() {
            return _joinOnMap;
        }

        public void setJoinOnMap(Map<String, String> value) {
            _joinOnMap = value;
        }

        public boolean isInnerJoin() {
            return _innerJoin;
        }

        public void setInnerJoin(boolean value) {
            _innerJoin = value;
        }
    }

    protected void assertAlreadyOuterJoin(String aliasName) {
        if (_outerJoinMap.containsKey(aliasName)) {
            String msg = "The alias name have already registered in outer join: " + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertJoinOnMapNotEmpty(Map<String, String> joinOnMap, String aliasName) {
        if (joinOnMap.isEmpty()) {
            String msg = "The joinOnMap should not be empty: aliasName=" + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                               Where
    //                                                                               =====
    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value) {
        assertStringNotNullAndNotTrimmedEmpty("columnFullName", columnFullName);
        final List<String> clauseList = getWhereClauseList4Register();
        doRegisterWhereClause(clauseList, columnFullName, key, value);
    }

    public void registerWhereClause(String columnFullName, ConditionKey key, ConditionValue value,
            ConditionOption option) {
        assertStringNotNullAndNotTrimmedEmpty("columnFullName", columnFullName);
        assertObjectNotNull("option of " + columnFullName, option);
        final List<String> clauseList = getWhereClauseList4Register();
        doRegisterWhereClause(clauseList, columnFullName, key, value, option);
    }

    public void registerWhereClause(String clause) {
        assertStringNotNullAndNotTrimmedEmpty("clause", clause);
        final List<String> clauseList = getWhereClauseList4Register();
        doRegisterWhereClause(clauseList, clause);
    }

    protected void doRegisterWhereClause(List<String> clauseList, String columnName, ConditionKey key,
            ConditionValue value) {
        key.addWhereClause(clauseList, columnName, value);
        markOrScopeQueryAndPart(clauseList);
    }

    protected void doRegisterWhereClause(List<String> clauseList, String columnName, ConditionKey key,
            ConditionValue value, ConditionOption option) {
        key.addWhereClause(clauseList, columnName, value, option);
        markOrScopeQueryAndPart(clauseList);
    }

    protected void doRegisterWhereClause(List<String> clauseList, String clause) {
        clauseList.add(clause);
        markOrScopeQueryAndPart(clauseList);
    }

    protected List<String> getWhereClauseList4Register() {
        if (_orScopeQueryEffective) {
            return getTmpOrWhereList();
        } else {
            return _whereList;
        }
    }

    public void exchangeFirstWhereClauseForLastOne() {
        if (_whereList.size() > 1) {
            final String first = (String) _whereList.get(0);
            final String last = (String) _whereList.get(_whereList.size() - 1);
            _whereList.set(0, last);
            _whereList.set(_whereList.size() - 1, first);
        }
    }

    public boolean hasWhereClause() {
        return _whereList != null && !_whereList.isEmpty();
    }

    // ===================================================================================
    //                                                                         InlineWhere
    //                                                                         ===========
    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value) {
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        final List<String> clauseList = getBaseTableInlineWhereClauseList4Register();
        doRegisterWhereClause(clauseList, columnName, key, value);
    }

    public void registerBaseTableInlineWhereClause(String columnName, ConditionKey key, ConditionValue value,
            ConditionOption option) {
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        assertObjectNotNull("option of " + columnName, option);
        final List<String> clauseList = getBaseTableInlineWhereClauseList4Register();
        doRegisterWhereClause(clauseList, columnName, key, value, option);
    }

    public void registerBaseTableInlineWhereClause(String value) {
        final List<String> clauseList = getBaseTableInlineWhereClauseList4Register();
        doRegisterWhereClause(clauseList, value);
    }

    protected List<String> getBaseTableInlineWhereClauseList4Register() {
        if (_orScopeQueryEffective) {
            return getTmpOrBaseTableInlineWhereList();
        } else {
            return _baseTableInlineWhereList;
        }
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, boolean onClause) {
        assertNotYetOuterJoin(aliasName);
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        final List<String> clauseList = getOuterJoinInlineWhereClauseList4Register(aliasName, onClause);
        final String realColumnName = (onClause ? aliasName + "." : "") + columnName;
        doRegisterWhereClause(clauseList, realColumnName, key, value);
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String columnName, ConditionKey key,
            ConditionValue value, ConditionOption option, boolean onClause) {
        assertNotYetOuterJoin(aliasName);
        assertStringNotNullAndNotTrimmedEmpty("columnName", columnName);
        final List<String> clauseList = getOuterJoinInlineWhereClauseList4Register(aliasName, onClause);
        final String realColumnName = (onClause ? aliasName + "." : "") + columnName;
        doRegisterWhereClause(clauseList, realColumnName, key, value, option);
    }

    public void registerOuterJoinInlineWhereClause(String aliasName, String clause, boolean onClause) {
        assertNotYetOuterJoin(aliasName);
        final List<String> clauseList = getOuterJoinInlineWhereClauseList4Register(aliasName, onClause);
        doRegisterWhereClause(clauseList, clause);
    }

    protected List<String> getOuterJoinInlineWhereClauseList4Register(String aliasName, boolean onClause) {
        final LeftOuterJoinInfo joinInfo = _outerJoinMap.get(aliasName);
        final List<String> clauseList;
        if (onClause) {
            if (_orScopeQueryEffective) {
                clauseList = getTmpOrAdditionalOnClauseList(aliasName);
            } else {
                clauseList = joinInfo.getAdditionalOnClauseList();
            }
        } else {
            if (_orScopeQueryEffective) {
                clauseList = getTmpOrOuterJoinInlineClauseList(aliasName);
            } else {
                clauseList = joinInfo.getInlineWhereClauseList();
            }
        }
        return clauseList;
    }

    protected void assertNotYetOuterJoin(String aliasName) {
        if (!_outerJoinMap.containsKey(aliasName)) {
            String msg = "The alias name have not registered in outer join yet: " + aliasName;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                        OrScopeQuery
    //                                                                        ============
    public void makeOrScopeQueryEffective() {
        final TmpOrScopeQueryInfo tmpOrScopeQueryInfo = new TmpOrScopeQueryInfo();
        if (_currentTmpOrScopeQueryInfo != null) {
            _currentTmpOrScopeQueryInfo.addChildInfo(tmpOrScopeQueryInfo);
        }
        _currentTmpOrScopeQueryInfo = tmpOrScopeQueryInfo;
        _orScopeQueryEffective = true;
    }

    public void closeOrScopeQuery() {
        assertCurrentTmpOrScopeQueryInfo();
        final TmpOrScopeQueryInfo parentInfo = _currentTmpOrScopeQueryInfo.getParentInfo();
        if (parentInfo != null) {
            _currentTmpOrScopeQueryInfo = parentInfo;
        } else {
            reflectTmpOrClauseToRealObject(_currentTmpOrScopeQueryInfo);
            clearOrScopeQuery();
        }
    }

    protected void clearOrScopeQuery() {
        _currentTmpOrScopeQueryInfo = null;
        _orScopeQueryEffective = false;
        _orScopeQueryAndPart = false;
    }

    protected void reflectTmpOrClauseToRealObject(TmpOrScopeQueryInfo localInfo) {
        {
            final List<TmpOrScopeQueryGroupInfo> groupList = setupTmpOrListList(localInfo,
                    new TmpOrClauseListProvider() {
                        public List<String> provide(TmpOrScopeQueryInfo tmpOrScopeQueryInfo) {
                            return tmpOrScopeQueryInfo.getTmpOrWhereList();
                        }
                    });
            setupOrScopeQuery(groupList, _whereList, true);
        }
        {
            final List<TmpOrScopeQueryGroupInfo> groupList = setupTmpOrListList(localInfo,
                    new TmpOrClauseListProvider() {
                        public List<String> provide(TmpOrScopeQueryInfo tmpOrScopeQueryInfo) {
                            return tmpOrScopeQueryInfo.getTmpOrBaseTableInlineWhereList();
                        }
                    });
            setupOrScopeQuery(groupList, _baseTableInlineWhereList, false);
        }
        {
            final Set<Entry<String, LeftOuterJoinInfo>> entrySet = _outerJoinMap.entrySet();
            for (Entry<String, LeftOuterJoinInfo> entry : entrySet) {
                final String aliasName = entry.getKey();
                final LeftOuterJoinInfo joinInfo = entry.getValue();
                final List<TmpOrScopeQueryGroupInfo> groupList = new ArrayList<TmpOrScopeQueryGroupInfo>();
                groupList.addAll(setupTmpOrListList(localInfo, new TmpOrClauseListProvider() {
                    public List<String> provide(TmpOrScopeQueryInfo tmpOrScopeQueryInfo) {
                        return tmpOrScopeQueryInfo.getTmpOrAdditionalOnClauseList(aliasName);
                    }
                }));
                setupOrScopeQuery(groupList, joinInfo.getAdditionalOnClauseList(), false);
            }
        }
        {
            final Set<Entry<String, LeftOuterJoinInfo>> entrySet = _outerJoinMap.entrySet();
            for (Entry<String, LeftOuterJoinInfo> entry : entrySet) {
                final String aliasName = entry.getKey();
                final LeftOuterJoinInfo joinInfo = entry.getValue();
                final List<TmpOrScopeQueryGroupInfo> groupList = new ArrayList<TmpOrScopeQueryGroupInfo>();
                groupList.addAll(setupTmpOrListList(localInfo, new TmpOrClauseListProvider() {
                    public List<String> provide(TmpOrScopeQueryInfo tmpOrScopeQueryInfo) {
                        return tmpOrScopeQueryInfo.getTmpOrOuterJoinInlineClauseList(aliasName);
                    }
                }));
                setupOrScopeQuery(groupList, joinInfo.getInlineWhereClauseList(), false);
            }
        }
    }

    protected List<TmpOrScopeQueryGroupInfo> setupTmpOrListList(TmpOrScopeQueryInfo parentInfo,
            TmpOrClauseListProvider provider) {
        final List<TmpOrScopeQueryGroupInfo> resultList = new ArrayList<TmpOrScopeQueryGroupInfo>();
        final TmpOrScopeQueryGroupInfo groupInfo = new TmpOrScopeQueryGroupInfo();
        groupInfo.setOrClauseList(provider.provide(parentInfo));
        resultList.add(groupInfo);
        if (parentInfo.hasChildInfo()) {
            for (TmpOrScopeQueryInfo childInfo : parentInfo.getChildInfoList()) {
                resultList.addAll(setupTmpOrListList(childInfo, provider)); // recursive call
            }
        }
        return resultList;
    }

    protected static interface TmpOrClauseListProvider {
        List<String> provide(TmpOrScopeQueryInfo tmpOrScopeQueryInfo);
    }

    protected void setupOrScopeQuery(List<TmpOrScopeQueryGroupInfo> tmpOrGroupList, List<String> realList, boolean line) {
        if (tmpOrGroupList == null || tmpOrGroupList.isEmpty()) {
            return;
        }
        final String or = " or ";
        final String and = " and ";
        final String lnIndentOr = line ? ln() + "   " : "";
        final String lnIndentAnd = ""; // no line separator either way
        final String andPartMark = getOrScopeQueryAndPartMark();
        final StringBuilder sb = new StringBuilder();
        boolean exists = false;
        int validCount = 0;
        int groupListIndex = 0;
        for (TmpOrScopeQueryGroupInfo groupInfo : tmpOrGroupList) {
            final List<String> orClauseList = groupInfo.getOrClauseList();
            if (orClauseList == null || orClauseList.isEmpty()) {
                continue; // not increment index
            }
            int listIndex = 0;
            boolean inAndPart = false;
            for (String orClause : orClauseList) {
                final boolean currentAndPart = orClause.startsWith(andPartMark);
                final boolean beginAndPart;
                final boolean secondAndPart;
                if (currentAndPart) {
                    if (inAndPart) { // already begin
                        beginAndPart = false;
                        secondAndPart = true;
                    } else {
                        beginAndPart = true;
                        secondAndPart = false;
                        inAndPart = true;
                    }
                    orClause = orClause.substring(andPartMark.length());
                } else {
                    if (inAndPart) {
                        sb.append(")");
                        inAndPart = false;
                    }
                    beginAndPart = false;
                    secondAndPart = false;
                }
                if (groupListIndex == 0) { // first list
                    if (listIndex == 0) {
                        sb.append("(");
                    } else {
                        sb.append(secondAndPart ? lnIndentAnd : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                } else { // second or more list
                    if (listIndex == 0) {
                        // always 'or' here
                        sb.append(lnIndentOr);
                        sb.append(or);
                        sb.append("(");
                    } else {
                        sb.append(secondAndPart ? lnIndentAnd : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                }
                sb.append(beginAndPart ? "(" : "");
                sb.append(orClause);
                ++validCount;
                if (!exists) {
                    exists = true;
                }
                ++listIndex;
            }
            if (inAndPart) {
                sb.append(")");
                inAndPart = false;
            }
            if (groupListIndex > 0) { // second or more list
                sb.append(")");
            }
            ++groupListIndex;
        }
        if (exists) {
            sb.append(line && validCount > 1 ? ln() + "       " : "").append(")");
            realList.add(sb.toString());
        }
    }

    public boolean isOrScopeQueryEffective() {
        return _orScopeQueryEffective;
    }

    protected List<String> getTmpOrWhereList() {
        assertCurrentTmpOrScopeQueryInfo();
        return _currentTmpOrScopeQueryInfo.getTmpOrWhereList();
    }

    protected List<String> getTmpOrBaseTableInlineWhereList() {
        assertCurrentTmpOrScopeQueryInfo();
        return _currentTmpOrScopeQueryInfo.getTmpOrBaseTableInlineWhereList();
    }

    protected List<String> getTmpOrAdditionalOnClauseList(String aliasName) {
        assertCurrentTmpOrScopeQueryInfo();
        return _currentTmpOrScopeQueryInfo.getTmpOrAdditionalOnClauseList(aliasName);
    }

    protected List<String> getTmpOrOuterJoinInlineClauseList(String aliasName) {
        assertCurrentTmpOrScopeQueryInfo();
        return _currentTmpOrScopeQueryInfo.getTmpOrOuterJoinInlineClauseList(aliasName);
    }

    protected static class TmpOrScopeQueryInfo {
        protected List<String> _tmpOrWhereList;
        protected List<String> _tmpOrBaseTableInlineWhereList;
        protected Map<String, List<String>> _tmpOrAdditionalOnClauseListMap;
        protected Map<String, List<String>> _tmpOrOuterJoinInlineClauseListMap;
        protected TmpOrScopeQueryInfo _parentInfo; // null means base point
        protected List<TmpOrScopeQueryInfo> _childInfoList;

        public List<String> getTmpOrAdditionalOnClauseList(String aliasName) {
            List<String> orClauseList = getTmpOrAdditionalOnClauseListMap().get(aliasName);
            if (orClauseList != null) {
                return orClauseList;
            }
            orClauseList = new ArrayList<String>();
            _tmpOrAdditionalOnClauseListMap.put(aliasName, orClauseList);
            return orClauseList;
        }

        public List<String> getTmpOrOuterJoinInlineClauseList(String aliasName) {
            List<String> orClauseList = getTmpOrOuterJoinInlineClauseListMap().get(aliasName);
            if (orClauseList != null) {
                return orClauseList;
            }
            orClauseList = new ArrayList<String>();
            _tmpOrOuterJoinInlineClauseListMap.put(aliasName, orClauseList);
            return orClauseList;
        }

        public List<String> getTmpOrWhereList() {
            if (_tmpOrWhereList == null) {
                _tmpOrWhereList = new ArrayList<String>();
            }
            return _tmpOrWhereList;
        }

        public void setTmpOrWhereList(List<String> tmpOrWhereList) {
            this._tmpOrWhereList = tmpOrWhereList;
        }

        public List<String> getTmpOrBaseTableInlineWhereList() {
            if (_tmpOrBaseTableInlineWhereList == null) {
                _tmpOrBaseTableInlineWhereList = new ArrayList<String>();
            }
            return _tmpOrBaseTableInlineWhereList;
        }

        public void setTmpOrBaseTableInlineWhereList(List<String> tmpOrBaseTableInlineWhereList) {
            this._tmpOrBaseTableInlineWhereList = tmpOrBaseTableInlineWhereList;
        }

        public Map<String, List<String>> getTmpOrAdditionalOnClauseListMap() {
            if (_tmpOrAdditionalOnClauseListMap == null) {
                _tmpOrAdditionalOnClauseListMap = new LinkedHashMap<String, List<String>>();
            }
            return _tmpOrAdditionalOnClauseListMap;
        }

        public void setTmpOrAdditionalOnClauseListMap(Map<String, List<String>> tmpOrAdditionalOnClauseListMap) {
            this._tmpOrAdditionalOnClauseListMap = tmpOrAdditionalOnClauseListMap;
        }

        public Map<String, List<String>> getTmpOrOuterJoinInlineClauseListMap() {
            if (_tmpOrOuterJoinInlineClauseListMap == null) {
                _tmpOrOuterJoinInlineClauseListMap = new LinkedHashMap<String, List<String>>();
            }
            return _tmpOrOuterJoinInlineClauseListMap;
        }

        public void setTmpOrOuterJoinInlineClauseListMap(Map<String, List<String>> tmpOrOuterJoinInlineClauseListMap) {
            this._tmpOrOuterJoinInlineClauseListMap = tmpOrOuterJoinInlineClauseListMap;
        }

        public boolean hasChildInfo() {
            return _childInfoList != null && !_childInfoList.isEmpty();
        }

        public TmpOrScopeQueryInfo getParentInfo() {
            return _parentInfo;
        }

        public void setParentInfo(TmpOrScopeQueryInfo parentInfo) {
            _parentInfo = parentInfo;
        }

        public List<TmpOrScopeQueryInfo> getChildInfoList() {
            if (_childInfoList == null) {
                _childInfoList = new ArrayList<TmpOrScopeQueryInfo>();
            }
            return _childInfoList;
        }

        public void addChildInfo(TmpOrScopeQueryInfo childInfo) {
            childInfo.setParentInfo(this);
            getChildInfoList().add(childInfo);
        }
    }

    protected static class TmpOrScopeQueryGroupInfo {
        protected List<String> _orClauseList;

        @Override
        public String toString() {
            return "{orClauseList=" + (_orClauseList != null ? _orClauseList.size() : "null") + "}";
        }

        public List<String> getOrClauseList() {
            return _orClauseList;
        }

        public void setOrClauseList(List<String> orClauseList) {
            this._orClauseList = orClauseList;
        }
    }

    public void beginOrScopeQueryAndPart() {
        assertCurrentTmpOrScopeQueryInfo();
        _orScopeQueryAndPart = true;
    }

    public void endOrScopeQueryAndPart() {
        assertCurrentTmpOrScopeQueryInfo();
        _orScopeQueryAndPart = false;
    }

    protected void markOrScopeQueryAndPart(List<String> clauseList) {
        if (_orScopeQueryEffective && _orScopeQueryAndPart && !clauseList.isEmpty()) {
            final String original = clauseList.remove(clauseList.size() - 1); // as latest
            clauseList.add(getOrScopeQueryAndPartMark() + original);
        }
    }

    protected String getOrScopeQueryAndPartMark() {
        return "$$df:AndPart$$";
    }

    protected void assertCurrentTmpOrScopeQueryInfo() {
        if (_currentTmpOrScopeQueryInfo == null) {
            String msg = "The attribute 'currentTmpOrScopeQueryInfo' should not be null in or-scope query:";
            msg = msg + " orScopeQueryEffective=" + _orScopeQueryEffective;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                             OrderBy
    //                                                                             =======
    public OrderByClause getSqlComponentOfOrderByClause() {
        return _orderByClause;
    }

    public SqlClause clearOrderBy() {
        _orderByEffective = false;
        _orderByClause.clear();
        return this;
    }

    public SqlClause makeOrderByEffective() {
        if (!_orderByClause.isEmpty()) {
            _orderByEffective = true;
        }
        return this;
    }

    public SqlClause ignoreOrderBy() {
        _orderByEffective = false;
        return this;
    }

    public void reverseOrderBy_Or_OverrideOrderBy(String orderByProperty, String registeredOrderByProperty,
            boolean ascOrDesc) {
        _orderByEffective = true;
        if (!_orderByClause.isSameOrderByColumn(orderByProperty)) {
            clearOrderBy();
            registerOrderBy(orderByProperty, registeredOrderByProperty, ascOrDesc);
        } else {
            _orderByClause.reverseAll();
        }
    }

    public void registerOrderBy(String orderByProperty, String registeredOrderByProperty, boolean ascOrDesc) {
        try {
            _orderByEffective = true;
            final List<String> orderByList = new ArrayList<String>();
            {
                final StringTokenizer st = new StringTokenizer(orderByProperty, "/");
                while (st.hasMoreElements()) {
                    orderByList.add(st.nextToken());
                }
            }

            if (registeredOrderByProperty == null || registeredOrderByProperty.trim().length() == 0) {
                registeredOrderByProperty = orderByProperty;
            }

            final List<String> registeredOrderByList = new ArrayList<String>();
            {
                final StringTokenizer st = new StringTokenizer(registeredOrderByProperty, "/");
                while (st.hasMoreElements()) {
                    registeredOrderByList.add(st.nextToken());
                }
            }

            int count = 0;
            for (final Iterator<String> ite = orderByList.iterator(); ite.hasNext();) {
                String orderBy = ite.next();
                String registeredOrderBy = (String) registeredOrderByList.get(count);

                _orderByEffective = true;
                String aliasName = null;
                String columnName = null;
                String registeredAliasName = null;
                String registeredColumnName = null;

                if (orderBy.indexOf(".") < 0) {
                    columnName = orderBy;
                } else {
                    aliasName = orderBy.substring(0, orderBy.lastIndexOf("."));
                    columnName = orderBy.substring(orderBy.lastIndexOf(".") + 1);
                }

                if (registeredOrderBy.indexOf(".") < 0) {
                    registeredColumnName = registeredOrderBy;
                } else {
                    registeredAliasName = registeredOrderBy.substring(0, registeredOrderBy.lastIndexOf("."));
                    registeredColumnName = registeredOrderBy.substring(registeredOrderBy.lastIndexOf(".") + 1);
                }

                OrderByElement element = new OrderByElement();
                element.setAliasName(aliasName);
                element.setColumnName(columnName);
                element.setRegisteredAliasName(registeredAliasName);
                element.setRegisteredColumnName(registeredColumnName);
                if (ascOrDesc) {
                    element.setupAsc();
                } else {
                    element.setupDesc();
                }
                _orderByClause.addOrderByElement(element);

                count++;
            }
        } catch (RuntimeException e) {
            String msg = "registerOrderBy() threw the exception: orderByProperty=" + orderByProperty;
            msg = msg + " registeredColumnFullName=" + registeredOrderByProperty;
            msg = msg + " ascOrDesc=" + ascOrDesc;
            msg = msg + " sqlClause=" + this.toString();
            throw new RuntimeException(msg, e);
        }
    }

    public void addNullsFirstToPreviousOrderBy() {
        _orderByClause.addNullsFirstToPreviousOrderByElement(createOrderByNullsSetupper());
    }

    public void addNullsLastToPreviousOrderBy() {
        _orderByClause.addNullsLastToPreviousOrderByElement(createOrderByNullsSetupper());
    }

    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {// As Default
        return new OrderByClause.OrderByNullsSetupper() {
            public String setup(String columnName, String orderByElementClause, boolean nullsFirst) {
                return orderByElementClause + " nulls " + (nullsFirst ? "first" : "last");
            }
        };
    }

    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupperByCaseWhen() {// Helper For Nulls Unsupported Database
        return new OrderByClause.OrderByNullsSetupper() {
            public String setup(String columnName, String orderByElementClause, boolean nullsFirst) {
                final String thenNumber = nullsFirst ? "1" : "0";
                final String elseNumber = nullsFirst ? "0" : "1";
                final String caseWhen = "case when " + columnName + " is not null then " + thenNumber + " else "
                        + elseNumber + " end asc";
                return caseWhen + ", " + orderByElementClause;
            }
        };
    }

    public void addManualOrderToPreviousOrderByElement(ManumalOrderInfo manumalOrderInfo) {
        assertObjectNotNull("manumalOrderInfo", manumalOrderInfo);
        if (hasUnionQuery()) {
            String msg = "Manual Order with Union is unavailable: " + manumalOrderInfo.getManualValueList();
            throw new IllegalConditionBeanOperationException(msg);
        }
        _orderByClause.addManualOrderByElement(manumalOrderInfo);
    }

    public boolean hasOrderByClause() {
        return _orderByClause != null && !_orderByClause.isEmpty();
    }

    // ===================================================================================
    //                                                                          UnionQuery
    //                                                                          ==========
    public void registerUnionQuery(String unionQueryClause, boolean unionAll) {
        assertStringNotNullAndNotTrimmedEmpty("unionQueryClause", unionQueryClause);
        UnionQueryInfo unionQueryInfo = new UnionQueryInfo();
        unionQueryInfo.setUnionQueryClause(unionQueryClause);
        unionQueryInfo.setUnionAll(unionAll);
        addUnionQueryInfo(unionQueryInfo);
    }

    protected void addUnionQueryInfo(UnionQueryInfo unionQueryInfo) {
        if (_unionQueryInfoList == null) {
            _unionQueryInfoList = new ArrayList<UnionQueryInfo>();
        }
        _unionQueryInfoList.add(unionQueryInfo);
    }

    public boolean hasUnionQuery() {
        return _unionQueryInfoList != null && !_unionQueryInfoList.isEmpty();
    }

    protected static class UnionQueryInfo {
        protected String _unionQueryClause;
        protected boolean _unionAll;

        public String getUnionQueryClause() {
            return _unionQueryClause;
        }

        public void setUnionQueryClause(String unionQueryClause) {
            _unionQueryClause = unionQueryClause;
        }

        public boolean isUnionAll() {
            return _unionAll;
        }

        public void setUnionAll(boolean unionAll) {
            _unionAll = unionAll;
        }
    }

    // ===================================================================================
    //                                                                          FetchScope
    //                                                                          ==========
    /**
     * @param fetchSize Fetch-size. (NotMinus & NotZero)
     * @return this. (NotNull)
     */
    public SqlClause fetchFirst(int fetchSize) {
        _fetchScopeEffective = true;
        if (fetchSize <= 0) {
            String msg = "Argument[fetchSize] should be plus: " + fetchSize;
            throw new IllegalArgumentException(msg);
        }
        _fetchStartIndex = 0;
        _fetchSize = fetchSize;
        _fetchPageNumber = 1;
        doClearFetchPageClause();
        doFetchFirst();
        return this;
    }

    /**
     * @param fetchStartIndex Fetch-start-index. 0 origin. (NotMinus)
     * @param fetchSize Fetch size. (NotMinus)
     * @return this. (NotNull)
     */
    public SqlClause fetchScope(int fetchStartIndex, int fetchSize) {
        _fetchScopeEffective = true;
        if (fetchStartIndex < 0) {
            String msg = "Argument[fetchStartIndex] must be plus or zero: " + fetchStartIndex;
            throw new IllegalArgumentException(msg);
        }
        if (fetchSize <= 0) {
            String msg = "Argument[fetchSize] should be plus: " + fetchSize;
            throw new IllegalArgumentException(msg);
        }
        _fetchStartIndex = fetchStartIndex;
        _fetchSize = fetchSize;
        return fetchPage(1);
    }

    /**
     * @param fetchPageNumber Page-number. 1 origin. (NotMinus & NotZero: If minus or zero, set one.)
     * @return this. (NotNull)
     */
    public SqlClause fetchPage(int fetchPageNumber) {
        _fetchScopeEffective = true;
        if (fetchPageNumber <= 0) {
            fetchPageNumber = 1;
        }
        if (_fetchSize <= 0) {
            throwFetchSizeNotPlusException(fetchPageNumber);
        }
        _fetchPageNumber = fetchPageNumber;
        if (_fetchPageNumber == 1 && _fetchStartIndex == 0) {
            return fetchFirst(_fetchSize);
        }
        doClearFetchPageClause();
        doFetchPage();
        return this;
    }

    protected void throwFetchSizeNotPlusException(int fetchPageNumber) { // as system exception
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Fetch size should not be minus or zero!" + ln();
        msg = msg + ln();
        msg = msg + "[Fetch Size]" + ln();
        msg = msg + "fetchSize=" + _fetchSize + ln();
        msg = msg + ln();
        msg = msg + "[Fetch Page Number]" + ln();
        msg = msg + "fetchPageNumber=" + fetchPageNumber + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    abstract protected void doFetchFirst();

    abstract protected void doFetchPage();

    abstract protected void doClearFetchPageClause();

    protected class RownumPagingProcessor {
        protected String _rownumExpression;
        protected String _selectHint = "";
        protected String _sqlSuffix = "";
        protected Integer _pagingBindFrom;
        protected Integer _pagingBindTo;
        protected boolean _bind;

        public RownumPagingProcessor(String rownumExpression) {
            _rownumExpression = rownumExpression;
        }

        public void useBindVariable() {
            _bind = true;
        }

        public void processRowNumberPaging() {
            final boolean offset = isFetchStartIndexSupported();
            final boolean limit = isFetchSizeSupported();
            if (!offset && !limit) {
                return;
            }

            final StringBuilder hintSb = new StringBuilder();
            final String rownum = _rownumExpression;
            hintSb.append(" *").append(ln());
            hintSb.append("  from (").append(ln());
            hintSb.append("select plain.*, ").append(rownum).append(" as rn").append(ln());
            hintSb.append("  from (").append(ln());
            hintSb.append("select"); // main select

            final StringBuilder suffixSb = new StringBuilder();
            final String fromEnd = "       ) plain" + ln() + "       ) ext" + ln();
            if (offset) {
                final int pageStartIndex = getPageStartIndex();
                _pagingBindFrom = pageStartIndex;
                final String exp = _bind ? "/*pmb.sqlClause.pagingBindFrom*/" : String.valueOf(pageStartIndex);
                suffixSb.append(fromEnd).append(" where ext.rn > ").append(exp);
            }
            if (limit) {
                final int pageEndIndex = getPageEndIndex();
                _pagingBindTo = pageEndIndex;
                final String exp = _bind ? "/*pmb.sqlClause.pagingBindTo*/" : String.valueOf(pageEndIndex);
                if (offset) {
                    suffixSb.append(ln()).append("   and ext.rn <= ").append(exp);
                } else {
                    suffixSb.append(fromEnd).append(" where ext.rn <= ").append(exp);
                }
            }

            _selectHint = hintSb.toString();
            _sqlSuffix = suffixSb.toString();
        }

        public String getSelectHint() {
            return _selectHint;
        }

        public String getSqlSuffix() {
            return _sqlSuffix;
        }

        public Integer getPagingBindFrom() {
            return _pagingBindFrom;
        }

        public Integer getPagingBindTo() {
            return _pagingBindTo;
        }
    }

    public int getFetchStartIndex() {
        return _fetchStartIndex;
    }

    public int getFetchSize() {
        return _fetchSize;
    }

    public int getFetchPageNumber() {
        return _fetchPageNumber;
    }

    /**
     * @return Page start index. 0 origin. (NotMinus)
     */
    public int getPageStartIndex() {
        if (_fetchPageNumber <= 0) {
            String msg = "_fetchPageNumber must be plus: " + _fetchPageNumber;
            throw new IllegalStateException(msg);
        }
        return _fetchStartIndex + (_fetchSize * (_fetchPageNumber - 1));
    }

    /**
     * @return Page end index. 0 origin. (NotMinus)
     */
    public int getPageEndIndex() {
        if (_fetchPageNumber <= 0) {
            String msg = "_fetchPageNumber must be plus: " + _fetchPageNumber;
            throw new IllegalStateException(msg);
        }
        return _fetchStartIndex + (_fetchSize * _fetchPageNumber);
    }

    public boolean isFetchScopeEffective() {
        return _fetchScopeEffective;
    }

    public SqlClause ignoreFetchScope() {
        _fetchScopeEffective = false;
        doClearFetchPageClause();
        return this;
    }

    public SqlClause makeFetchScopeEffective() {
        if (getFetchSize() > 0 && getFetchPageNumber() > 0) {
            fetchPage(getFetchPageNumber());
        }
        return this;
    }

    public boolean isFetchStartIndexSupported() {
        return true; // as default
    }

    public boolean isFetchSizeSupported() {
        return true; // as default
    }

    abstract protected String createSelectHint();

    abstract protected String createFromBaseTableHint();

    abstract protected String createFromHint();

    abstract protected String createSqlSuffix();

    // ===================================================================================
    //                                                                     Fetch Narrowing
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getPageStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingLoopCount() {
        return getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingEffective() {
        return _fetchScopeEffective;
    }

    // ===================================================================================
    //                                                                            Resolver
    //                                                                            ========
    public String resolveJoinAliasName(String relationPath, int cqNestNo) {
        return resolveNestLevelExpression("dfrelation" + relationPath, cqNestNo);
    }

    public String resolveNestLevelExpression(String name, int cqNestNo) {
        // *comment out old style 
        //if (cqNestNo > 1) {
        //    return name + "_n" + cqNestNo;
        //} else {
        //    return name;
        //}
        return name;
    }

    public int resolveRelationNo(String localTableName, String foreignPropertyName) {
        final DBMeta dbmeta = findDBMeta(localTableName);
        final ForeignInfo foreignInfo = dbmeta.findForeignInfo(foreignPropertyName);
        return foreignInfo.getRelationNo();
    }

    // ===================================================================================
    //                                                                    Table Alias Info
    //                                                                    ================
    public String getLocalTableAliasName() {
        return "dflocal";
    }

    public String getForeignTableAliasPrefix() {
        return "dfrelation";
    }

    // ===================================================================================
    //                                                                       Template Mark
    //                                                                       =============
    public String getWhereClauseMark() {
        return "#df:whereClause#";
    }

    public String getWhereFirstConditionMark() {
        return "#df:whereFirstCondition#";
    }

    public String getUnionSelectClauseMark() {
        return "#df:unionSelectClause#";
    }

    public String getUnionWhereClauseMark() {
        return "#df:unionWhereClause#";
    }

    public String getUnionWhereFirstConditionMark() {
        return "#df:unionWhereFirstCondition#";
    }

    // =====================================================================================
    //                                                            Where Clause Simple Filter
    //                                                            ==========================
    public void addWhereClauseSimpleFilter(WhereClauseSimpleFilter whereClauseSimpleFilter) {
        if (_whereClauseSimpleFilterList == null) {
            _whereClauseSimpleFilterList = new ArrayList<WhereClauseSimpleFilter>();
        }
        _whereClauseSimpleFilterList.add(whereClauseSimpleFilter);
    }

    protected String filterWhereClauseSimply(String clauseElement) {
        if (_whereClauseSimpleFilterList == null || _whereClauseSimpleFilterList.isEmpty()) {
            return clauseElement;
        }
        for (final Iterator<WhereClauseSimpleFilter> ite = _whereClauseSimpleFilterList.iterator(); ite.hasNext();) {
            final WhereClauseSimpleFilter filter = ite.next();
            if (filter == null) {
                String msg = "The list of filter should not have null: _whereClauseSimpleFilterList="
                        + _whereClauseSimpleFilterList;
                throw new IllegalStateException(msg);
            }
            clauseElement = filter.filterClauseElement(clauseElement);
        }
        return clauseElement;
    }

    // =====================================================================================
    //                                                                 Selected Foreign Info
    //                                                                 =====================
    public boolean isSelectedForeignInfoEmpty() {
        if (_selectedForeignInfo == null) {
            return true;
        }
        return _selectedForeignInfo.isEmpty();
    }

    public boolean hasSelectedForeignInfo(String relationPath) {
        if (_selectedForeignInfo == null) {
            return false;
        }
        return _selectedForeignInfo.containsKey(relationPath);
    }

    public void registerSelectedForeignInfo(String relationPath, String foreignPropertyName) {
        if (_selectedForeignInfo == null) {
            _selectedForeignInfo = new HashMap<String, String>();
        }
        _selectedForeignInfo.put(relationPath, foreignPropertyName);
    }

    // ===================================================================================
    //                                                                    Sub Query Indent
    //                                                                    ================
    public String resolveSubQueryBeginMark(String subQueryIdentity) {
        return getSubQueryBeginMarkPrefix() + subQueryIdentity + getSubQueryIdentityTerminal();
    }

    public String resolveSubQueryEndMark(String subQueryIdentity) {
        return getSubQueryEndMarkPrefix() + subQueryIdentity + getSubQueryIdentityTerminal();
    }

    protected String getSubQueryBeginMarkPrefix() {
        return "--df:SubQueryBegin#";
    }

    protected String getSubQueryEndMarkPrefix() {
        return "--df:SubQueryEnd#";
    }

    protected String getSubQueryIdentityTerminal() {
        return "#IdentityTerminal#";
    }

    public String filterSubQueryIndent(String sql) {
        return filterSubQueryIndent(sql, "", sql);
    }

    protected String filterSubQueryIndent(String sql, String preIndent, String originalSql) {
        if (!sql.contains(getSubQueryBeginMarkPrefix())) {
            return sql;
        }
        final String[] lines = sql.split(ln());
        final String beginMarkPrefix = getSubQueryBeginMarkPrefix();
        final String endMarkPrefix = getSubQueryEndMarkPrefix();
        final String identityTerminal = getSubQueryIdentityTerminal();
        final int terminalLength = identityTerminal.length();
        StringBuilder mainSb = new StringBuilder();
        StringBuilder subSb = null;
        boolean throughBegin = false;
        boolean throughBeginFirst = false;
        String subQueryIdentity = null;
        String indent = null;
        for (String line : lines) {
            if (!throughBegin) {
                if (line.contains(beginMarkPrefix)) {
                    throughBegin = true;
                    subSb = new StringBuilder();
                    final int markIndex = line.indexOf(beginMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was not found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex) + line.substring(terminalIndex + terminalLength);
                    subQueryIdentity = line.substring(markIndex + beginMarkPrefix.length(), terminalIndex);
                    subSb.append(clause);
                    indent = buildSpaceBar(markIndex - preIndent.length());
                } else {
                    mainSb.append(line).append(ln());
                }
            } else {
                // - - - - - - - -
                // In begin to end
                // - - - - - - - -
                if (line.contains(endMarkPrefix + subQueryIdentity)) { // The end
                    final int markIndex = line.indexOf(endMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was not found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex) + line.substring(terminalIndex + terminalLength);
                    subSb.append(clause).append(ln());
                    final String currentSql = filterSubQueryIndent(subSb.toString(), preIndent + indent, originalSql);
                    mainSb.append(currentSql);
                    throughBegin = false;
                    throughBeginFirst = false;
                } else {
                    if (!throughBeginFirst) {
                        subSb.append(line.trim()).append(ln());
                        throughBeginFirst = true;
                    } else {
                        subSb.append(indent).append(line).append(ln());
                    }
                }
            }
        }
        final String filteredSql = mainSb.toString();

        if (throughBegin) {
            String msg = "End Mark not found!";
            msg = msg + ln() + "[Current SubQueryIdentity]" + ln();
            msg = msg + subQueryIdentity + ln();
            msg = msg + ln() + "[Before Filter]" + ln() + sql;
            msg = msg + ln() + "[After Filter]" + ln() + filteredSql;
            msg = msg + ln() + "[Original SQL]" + ln() + originalSql;
            throw new SubQueryIndentFailureException(msg);
        }
        if (filteredSql.contains(beginMarkPrefix)) {
            String msg = "Any begin marks are not filtered!";
            msg = msg + ln() + "[Current SubQueryIdentity]" + ln();
            msg = msg + subQueryIdentity + ln();
            msg = msg + ln() + "[Before Filter]" + ln() + sql;
            msg = msg + ln() + "[After Filter]" + ln() + filteredSql;
            msg = msg + ln() + "[Original SQL]" + ln() + originalSql;
            throw new SubQueryIndentFailureException(msg);
        }
        return filteredSql;
    }

    protected String buildSpaceBar(int size) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static class SubQueryIndentFailureException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public SubQueryIndentFailureException(String msg) {
            super(msg);
        }
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                       Specification
    //                                                                       =============
    public void specifySelectColumn(String tableAliasName, String columnName, String tableDbName) {
        if (_specifiedSelectColumnMap == null) {
            _specifiedSelectColumnMap = new HashMap<String, Map<String, String>>(); // not needs order
        }
        if (!_specifiedSelectColumnMap.containsKey(tableAliasName)) {
            _specifiedSelectColumnMap.put(tableAliasName, new LinkedHashMap<String, String>());
        }
        final Map<String, String> elementMap = _specifiedSelectColumnMap.get(tableAliasName);
        elementMap.put(columnName, tableDbName); // this tableDbName is unused actually, this is for future
    }

    public void specifyDerivingSubQuery(String aliasName, String deriveSubQuery) {
        if (_specifiedDerivingSubQueryMap == null) {
            _specifiedDerivingSubQueryMap = new LinkedHashMap<String, String>();
        }
        _specifiedDerivingSubQueryMap.put(aliasName, deriveSubQuery);
    }

    public boolean hasSpecifiedDerivingSubQuery(String aliasName) {
        if (_specifiedDerivingSubQueryMap == null) {
            return false;
        }
        return _specifiedDerivingSubQueryMap.containsKey(aliasName);
    }

    public List<String> getSpecifiedDerivingAliasList() {
        if (_specifiedDerivingSubQueryMap == null) {
            @SuppressWarnings("unchecked")
            final List<String> emptyList = Collections.EMPTY_LIST;
            return emptyList;
        }
        return new ArrayList<String>(_specifiedDerivingSubQueryMap.keySet());
    }

    public String getSpecifiedColumnNameAsOne() {
        if (_specifiedSelectColumnMap != null && _specifiedSelectColumnMap.size() == 1) {
            Map<String, String> elementMap = _specifiedSelectColumnMap.values().iterator().next();
            if (elementMap != null && elementMap.size() == 1) {
                return elementMap.keySet().iterator().next();
            }
        }
        return null;
    }

    public String getSpecifiedColumnTableDbNameAsOne() {
        if (_specifiedSelectColumnMap != null && _specifiedSelectColumnMap.size() == 1) {
            Map<String, String> elementMap = _specifiedSelectColumnMap.values().iterator().next();
            if (elementMap != null && elementMap.size() == 1) {
                return elementMap.values().iterator().next();
            }
        }
        return null;
    }

    public String getSpecifiedColumnRealNameAsOne() {
        return doGetSpecifiedColumnRealNameAsOne(false);
    }

    public String removeSpecifiedColumnRealNameAsOne() {
        return doGetSpecifiedColumnRealNameAsOne(true);
    }

    private String doGetSpecifiedColumnRealNameAsOne(boolean remove) {
        if (_specifiedSelectColumnMap != null && _specifiedSelectColumnMap.size() == 1) {
            String tableAliasName = _specifiedSelectColumnMap.keySet().iterator().next();
            Map<String, String> elementMap = _specifiedSelectColumnMap.get(tableAliasName);
            if (elementMap != null && elementMap.size() == 1) {
                String columnName = elementMap.keySet().iterator().next();
                String realName = tableAliasName + "." + columnName;
                if (remove) {
                    elementMap.remove(columnName);
                }
                return realName;
            }
        }
        return null;
    }

    public void backupSpecifiedSelectColumn() {
        _backupSpecifiedSelectColumnMap = _specifiedSelectColumnMap;
    }

    public void restoreSpecifiedSelectColumn() {
        _specifiedSelectColumnMap = _backupSpecifiedSelectColumnMap;
        _backupSpecifiedSelectColumnMap = null;
    }

    public void clearSpecifiedSelectColumn() {
        if (_specifiedSelectColumnMap != null) {
            _specifiedSelectColumnMap.clear();
            _specifiedSelectColumnMap = null;
        }
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    public String getClauseQueryUpdate(Map<String, String> columnParameterMap) {
        if (columnParameterMap.isEmpty()) {
            return null;
        }
        final String aliasName = getLocalTableAliasName();
        final DBMeta dbmeta = getDBMeta();
        final String tableSqlName = dbmeta.getTableSqlName();
        final String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
        final String selectClause = "select " + aliasName + "." + primaryKeyName;
        String fromWhereClause = getClauseFromWhereWithUnionTemplate();

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getUnionSelectClauseMark(), selectClause);
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereFirstConditionMark(), "");

        final StringBuilder sb = new StringBuilder();
        String ln = ln();
        sb.append("update ").append(tableSqlName).append(ln);
        int index = 0;
        // It is guaranteed that the map has one or more elements.
        final Set<Entry<String, String>> entrySet = columnParameterMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            final String columnName = entry.getKey();
            final String parameter = entry.getValue();
            final ColumnInfo columnInfo = dbmeta.findColumnInfo(columnName);
            final String columnSqlName = columnInfo.getColumnSqlName();
            if (index == 0) {
                sb.append("   set ").append(columnSqlName).append(" = ").append(parameter).append(ln);
            } else {
                sb.append("     , ").append(columnSqlName).append(" = ").append(parameter).append(ln);
            }
            ++index;
        }
        if (isUpdateSubQueryUseLocalTableSupported() && !dbmeta.hasTwoOrMorePrimaryKeys()) {
            final String subQuery = filterSubQueryIndent(selectClause + " " + fromWhereClause);
            sb.append(" where ").append(primaryKeyName);
            sb.append(" in (").append(ln).append(subQuery).append(ln).append(")");
            return sb.toString();
        } else {
            if (_outerJoinMap != null && !_outerJoinMap.isEmpty()) {
                String msg = "The queryUpdate() with outer join is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableDbName=" + getDBMeta().getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            if (_unionQueryInfoList != null && !_unionQueryInfoList.isEmpty()) {
                String msg = "The queryUpdate() with union is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableDbName=" + getDBMeta().getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            String subQuery = filterSubQueryIndent(fromWhereClause);
            subQuery = replaceString(subQuery, aliasName + ".", "");
            subQuery = replaceString(subQuery, " " + aliasName + " ", " ");
            int whereIndex = subQuery.indexOf("where ");
            if (whereIndex < 0) {
                return sb.toString();
            }
            subQuery = subQuery.substring(whereIndex);
            sb.append(" ").append(subQuery);
            return sb.toString();
        }
    }

    public String getClauseQueryDelete() {
        final String aliasName = getLocalTableAliasName();
        final DBMeta dbmeta = getDBMeta();
        final String tableSqlName = dbmeta.getTableSqlName();
        final String primaryKeyName = dbmeta.getPrimaryUniqueInfo().getFirstColumn().getColumnSqlName();
        final String selectClause = "select " + aliasName + "." + primaryKeyName;
        String fromWhereClause = getClauseFromWhereWithUnionTemplate();

        // Replace template marks. These are very important!
        fromWhereClause = replaceString(fromWhereClause, getUnionSelectClauseMark(), selectClause);
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereClauseMark(), "");
        fromWhereClause = replaceString(fromWhereClause, getUnionWhereFirstConditionMark(), "");

        if (isUpdateSubQueryUseLocalTableSupported() && !dbmeta.hasTwoOrMorePrimaryKeys()) {
            final String subQuery = filterSubQueryIndent(selectClause + " " + fromWhereClause);
            final StringBuilder sb = new StringBuilder();
            String ln = ln();
            sb.append("delete from ").append(tableSqlName).append(ln);
            sb.append(" where ").append(primaryKeyName);
            sb.append(" in (").append(ln).append(subQuery).append(ln).append(")");
            return sb.toString();
        } else { // unsupported or two-or-more primary keys
            if (_outerJoinMap != null && !_outerJoinMap.isEmpty()) {
                String msg = "The queryDelete() with outer join is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableDbName=" + getDBMeta().getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            if (_unionQueryInfoList != null && !_unionQueryInfoList.isEmpty()) {
                String msg = "The queryDelete() with union is unavailable";
                msg = msg + " because your DB does not support it or the table has two-or-more primary keys:";
                msg = msg + " tableDbName=" + getDBMeta().getTableDbName();
                throw new IllegalConditionBeanOperationException(msg);
            }
            String subQuery = filterSubQueryIndent(fromWhereClause);
            subQuery = replaceString(subQuery, aliasName + ".", "");
            subQuery = replaceString(subQuery, " " + aliasName + " ", " ");
            subQuery = subQuery.substring(subQuery.indexOf("from "));
            return "delete " + subQuery;
        }
    }

    protected boolean isUpdateSubQueryUseLocalTableSupported() {
        return true;
    }

    // [DBFlute-0.8.6]
    // ===================================================================================
    //                                                                  Select Clause Type
    //                                                                  ==================
    public void classifySelectClauseType(SelectClauseType selectClauseType) {
        changeSelectClauseType(selectClauseType);
    }

    protected void changeSelectClauseType(SelectClauseType selectClauseType) {
        savePreviousSelectClauseType();
        _selectClauseType = selectClauseType;
    }

    protected void savePreviousSelectClauseType() {
        _previousSelectClauseType = _selectClauseType;
    }

    public void rollbackSelectClauseType() {
        _selectClauseType = _previousSelectClauseType != null ? _previousSelectClauseType : DEFAULT_SELECT_CLAUSE_TYPE;
    }

    // [DBFlute-0.9.4]
    // ===================================================================================
    //                                                                       InScope Limit
    //                                                                       =============
    public int getInScopeLimit() {
        return 0; // as default
    }

    // ===================================================================================
    //                                                                       DBMeta Helper
    //                                                                       =============
    protected DBMeta getDBMeta() {
        if (_dbmeta == null) {
            String msg = "The DB meta of local table should not be null when using getDBMeta():";
            msg = msg + " tableDbName=" + _tableDbName;
            throw new IllegalStateException(msg);
        }
        return _dbmeta;
    }

    protected DBMeta findDBMeta(String tableDbName) {
        DBMeta dbmeta = _cachedDBMetaMap.get(tableDbName);
        if (dbmeta != null) {
            return dbmeta;
        }
        if (_dbmetaProvider == null) {
            String msg = "The DB meta provider should not be null when using findDBMeta():";
            msg = msg + " tableDbName=" + tableDbName;
            throw new IllegalStateException(msg);
        }
        dbmeta = _dbmetaProvider.provideDBMetaChecked(tableDbName);
        _cachedDBMetaMap.put(tableDbName, dbmeta);
        return dbmeta;
    }

    protected String toSqlName(DBMeta dbmeta, String name) {
        final int dotIndex = name.lastIndexOf(".");
        final String prefix = name.substring(0, dotIndex);
        final String pureName = name.substring(dotIndex + ".".length());
        final ColumnInfo columnInfo = dbmeta.findColumnInfo(pureName);
        return prefix + "." + columnInfo.getColumnSqlName();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    protected void assertObjectNotNull(String variableName, Object value) {
        DfAssertUtil.assertObjectNotNull(variableName, value);
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        DfAssertUtil.assertStringNotNullAndNotTrimmedEmpty(variableName, value);
    }
}
