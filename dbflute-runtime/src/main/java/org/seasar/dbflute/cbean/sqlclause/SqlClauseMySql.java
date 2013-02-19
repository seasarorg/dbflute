/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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

import java.util.List;

import org.seasar.dbflute.cbean.sqlclause.orderby.OrderByClause;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClauseArranger;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbway.DBWay;
import org.seasar.dbflute.dbway.WayOfMySQL;
import org.seasar.dbflute.dbway.WayOfMySQL.FullTextSearchModifier;
import org.seasar.dbflute.util.Srl;

/**
 * SqlClause for MySQL.
 * @author jflute
 */
public class SqlClauseMySql extends AbstractSqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** The instance of DBWay. */
    protected static final DBWay _dbway = new WayOfMySQL();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-scope as sql-suffix. */
    protected String _fetchScopeSqlSuffix = "";

    /** String of lock as sql-suffix. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public SqlClauseMySql(String tableDbName) {
        super(tableDbName);
    }

    // ===================================================================================
    //                                                                       Main Override
    //                                                                       =============
    // -----------------------------------------------------
    //                                       Complete Clause
    //                                       ---------------
    @Override
    public String getClause() {
        if (canFoundRows()) {
            return "select found_rows()";
            // and sql_calc_found_rows is implemented at select-hint process
        }
        return super.getClause();
    }

    protected boolean canFoundRows() {
        return canPagingCountLater() && isSelectClauseTypeNonUnionCount();
    }

    // ===================================================================================
    //                                                                    OrderBy Override
    //                                                                    ================
    @Override
    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {
        return createOrderByNullsSetupperByCaseWhen();
    }

    // ===================================================================================
    //                                                                 FetchScope Override
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    protected void doFetchFirst() {
        doFetchPage();
    }

    /**
     * {@inheritDoc}
     */
    protected void doFetchPage() {
        _fetchScopeSqlSuffix = " limit " + getPageStartIndex() + ", " + getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    protected void doClearFetchPageClause() {
        _fetchScopeSqlSuffix = "";
    }

    // ===================================================================================
    //                                                                       Lock Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public void lockForUpdate() {
        _lockSqlSuffix = " for update";
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    protected String createSelectHint() {
        final StringBuilder sb = new StringBuilder();
        if (canSqlCalcFoundRows()) {
            sb.append(" sql_calc_found_rows");
            // and found_rows() is implemented at getClause override
        }
        return sb.toString();
    }

    protected boolean canSqlCalcFoundRows() {
        return canPagingCountLater() && isSelectClauseNonUnionSelect();
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createSqlSuffix() {
        return _fetchScopeSqlSuffix + _lockSqlSuffix;
    }

    // [DBFlute-0.7.5]
    // ===================================================================================
    //                                                               Query Update Override
    //                                                               =====================
    @Override
    protected boolean isUpdateSubQueryUseLocalTableSupported() {
        return false;
    }

    @Override
    protected boolean isUpdateDirectJoinSupported() {
        return true;
    }

    @Override
    protected boolean isUpdateTableAliasNameSupported() {
        return true; // almost unsupported (unknown)
    }

    // [DBFlute-0.9.9.1C]
    // ===================================================================================
    //                                                                      Collate Clause
    //                                                                      ==============
    public static class CollateUTF8UnicodeArranger implements QueryClauseArranger {
        public String arrange(ColumnRealName columnRealName, String operand, String bindExpression, String rearOption) {
            return columnRealName + " collate utf8_unicode_ci " + operand + " " + bindExpression + rearOption;
        }
    }

    public static class CollateUTF8GeneralArranger implements QueryClauseArranger {
        public String arrange(ColumnRealName columnRealName, String operand, String bindExpression, String rearOption) {
            return columnRealName + " collate utf8_general_ci " + operand + " " + bindExpression + rearOption;
        }
    }

    // [DBFlute-0.9.5]
    // ===================================================================================
    //                                                                    Full-Text Search
    //                                                                    ================
    /**
     * Build a condition string of match statement for full-text search. <br />
     * Bind variable is unused because the condition value should be literal in MySQL.
     * @param textColumnList The list of text column. (NotNull, NotEmpty, StringColumn, TargetTableColumn)
     * @param conditionValue The condition value embedded without binding (by MySQL restriction) but escaped. (NotNull)
     * @param modifier The modifier of full-text search. (NullAllowed: If the value is null, No modifier specified)
     * @param tableDbName The DB name of the target table. (NotNull)
     * @param aliasName The alias name of the target table. (NotNull)
     * @return The condition string of match statement. (NotNull)
     */
    public String buildMatchCondition(List<ColumnInfo> textColumnList, String conditionValue,
            FullTextSearchModifier modifier, String tableDbName, String aliasName) {
        assertTextColumnList(textColumnList);
        assertVariousTextSearchResource(conditionValue, modifier, tableDbName, aliasName);
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (ColumnInfo columnInfo : textColumnList) {
            if (columnInfo == null) {
                continue;
            }
            assertTextColumnTable(tableDbName, columnInfo);
            assertTextColumnType(tableDbName, columnInfo);
            final ColumnSqlName columnSqlName = columnInfo.getColumnSqlName();
            if (index > 0) {
                sb.append(",");
            }
            sb.append(aliasName).append(".").append(columnSqlName);
            ++index;
        }
        sb.insert(0, "match(").append(") against ('");
        sb.append(escapeMatchConditionValue(conditionValue)).append("'");
        if (modifier != null) {
            sb.append(" ").append(modifier.code());
        }
        sb.append(")");
        return sb.toString();
    }

    protected String escapeMatchConditionValue(String conditionValue) {
        conditionValue = Srl.replace(conditionValue, "\\", "\\\\");
        conditionValue = Srl.replace(conditionValue, "'", "''");
        return conditionValue;
    }

    protected void assertTextColumnList(List<ColumnInfo> textColumnList) {
        if (textColumnList == null) {
            String msg = "The argument 'textColumnList' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (textColumnList.isEmpty()) {
            String msg = "The argument 'textColumnList' should not be empty list.";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertVariousTextSearchResource(String conditionValue, FullTextSearchModifier modifier,
            String tableDbName, String aliasName) {
        if (conditionValue == null || conditionValue.length() == 0) {
            String msg = "The argument 'conditionValue' should not be null or empty: " + conditionValue;
            throw new IllegalArgumentException(msg);
        }
        if (tableDbName == null || tableDbName.trim().length() == 0) {
            String msg = "The argument 'tableDbName' should not be null or trimmed-empty: " + tableDbName;
            throw new IllegalArgumentException(msg);
        }
        if (aliasName == null || aliasName.trim().length() == 0) {
            String msg = "The argument 'aliasName' should not be null or trimmed-empty: " + aliasName;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertTextColumnTable(String tableDbName, ColumnInfo columnInfo) {
        final String tableOfColumn = columnInfo.getDBMeta().getTableDbName();
        if (!tableOfColumn.equalsIgnoreCase(tableDbName)) {
            String msg = "The table of the text column should be '" + tableDbName + "'";
            msg = msg + " but the table is '" + tableOfColumn + "': column=" + columnInfo;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertTextColumnType(String tableDbName, ColumnInfo columnInfo) {
        if (!columnInfo.isPropertyTypeString()) {
            String msg = "The text column should be String type:";
            msg = msg + " column=" + columnInfo;
            throw new IllegalArgumentException(msg);
        }
    }

    // [DBFlute-0.9.8.4]
    // ===================================================================================
    //                                                                               DBWay
    //                                                                               =====
    public DBWay dbway() {
        return _dbway;
    }
}
