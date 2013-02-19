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
package org.seasar.dbflute.cbean.coption;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.cbean.chelper.HpSpecifiedColumn;
import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseDb2;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseDerby;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseH2;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseMySql;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseOracle;
import org.seasar.dbflute.cbean.sqlclause.SqlClausePostgreSql;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseSqlServer;
import org.seasar.dbflute.cbean.sqlclause.subquery.QueryDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SpecifyDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryIndentProcessor;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryPath;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The option to filter by function. <br />
 * You can filter an aggregate function by scalar function filters.
 * @author jflute
 */
public class FunctionFilterOption implements ParameterOption {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String DATE_TRUNC_MONTH = "df:month";
    protected static final String DATE_TRUNC_DAY = "df:day";
    protected static final String DATE_TRUNC_TIME = "df:time";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _coalesce;
    protected Object _round;
    protected Object _trunc;
    protected Integer _addYear;
    protected Integer _addMonth;
    protected Integer _addDay;
    protected Integer _addHour;
    protected Integer _addMinute;
    protected Integer _addSecond;
    protected LinkedHashMap<String, ProcessCallback> _callbackMap; // order should be guaranteed
    protected String _parameterKey;
    protected String _parameterMapPath;

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    protected ColumnInfo _targetColumnInfo; // not required
    protected boolean _databaseMySQL;
    protected boolean _databasePostgreSQL;
    protected boolean _databaseOracle;
    protected boolean _databaseDB2;
    protected boolean _databaseSQLServer;
    protected boolean _databaseH2;
    protected boolean _databaseDerby;

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    protected void doCoalesce(Object coalesce) {
        _coalesce = coalesce;
        addProcessCallback("coalesce", new ProcessCallback() {
            public String callback(String functionExp) {
                return processCoalesce(functionExp);
            }
        });
    }

    protected void doRound(Object round) {
        _round = round;
        addProcessCallback("round", new ProcessCallback() {
            public String callback(String functionExp) {
                return processRound(functionExp);
            }
        });
    }

    protected void doTrunc(Object trunc) {
        _trunc = trunc;
        addProcessCallback("trunc", new ProcessCallback() {
            public String callback(String functionExp) {
                return processTrunc(functionExp);
            }
        });
    }

    protected void doTruncMonth() {
        doTrunc(DATE_TRUNC_MONTH);
    }

    protected void doTruncDay() {
        doTrunc(DATE_TRUNC_DAY);
    }

    protected void doTruncTime() {
        doTrunc(DATE_TRUNC_TIME);
    }

    protected void doAddYear(Integer addedYear) {
        _addYear = addedYear;
        addProcessCallback("addYear", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddYear(functionExp);
            }
        });
    }

    protected void doAddMonth(Integer addedMonth) {
        _addMonth = addedMonth;
        addProcessCallback("addMonth", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddMonth(functionExp);
            }
        });
    }

    protected void doAddDay(Integer addedDay) {
        _addDay = addedDay;
        addProcessCallback("addDay", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddDay(functionExp);
            }
        });
    }

    protected void doAddHour(Integer addedHour) {
        _addHour = addedHour;
        addProcessCallback("addHour", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddHour(functionExp);
            }
        });
    }

    protected void doAddMinute(Integer addedMinute) {
        _addMinute = addedMinute;
        addProcessCallback("addMinute", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddMinute(functionExp);
            }
        });
    }

    protected void doAddSecond(Integer addedSecond) {
        _addSecond = addedSecond;
        addProcessCallback("addSecond", new ProcessCallback() {
            public String callback(String functionExp) {
                return processAddSecond(functionExp);
            }
        });
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    /**
     * Filter the expression of function part. <br />
     * For example, an expression is like: max(foo.FOO_DATE), sum(bar.BAR_PRICE), ...
     * @param functionExp The expression of function part that is not filtered. (NotNull) 
     * @return The filtered expression. (NotNull)
     */
    public String filterFunction(String functionExp) {
        String filtered = functionExp;
        final LinkedHashMap<String, ProcessCallback> callbackMap = _callbackMap;
        if (callbackMap != null) {
            final Set<Entry<String, ProcessCallback>> entrySet = callbackMap.entrySet();
            for (Entry<String, ProcessCallback> entry : entrySet) {
                filtered = entry.getValue().callback(filtered);
            }
        }
        return processVarious(processCalculation(filtered));
    }

    protected static interface ProcessCallback {
        String callback(String functionExp);
    }

    protected void addProcessCallback(String functionKey, ProcessCallback callback) {
        if (_callbackMap == null) {
            _callbackMap = new LinkedHashMap<String, ProcessCallback>();
        }
        if (_callbackMap.containsKey(functionKey)) {
            String msg = "The function has been already set up: ";
            msg = msg + "function=" + functionKey + "() option=" + toString();
            throw new IllegalConditionBeanOperationException(msg);
        }
        _callbackMap.put(functionKey, callback);
    }

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    // -----------------------------------------------------
    //                                              Coalesce
    //                                              --------
    protected String processCoalesce(String functionExp) {
        if (_coalesce == null) {
            return functionExp;
        }
        if (_coalesce instanceof String && isDateTypeColumn()) {
            _coalesce = DfTypeUtil.toDate(_coalesce);
        }
        final String functionName = "coalesce";
        final String propertyName = functionName;
        return processSimpleFunction(functionExp, functionName, propertyName, null, false);
    }

    // -----------------------------------------------------
    //                                                 Round
    //                                                 -----
    protected String processRound(String functionExp) {
        if (_round == null) {
            return functionExp;
        }
        final String functionName = "round";
        final String propertyName = functionName;
        return processSimpleFunction(functionExp, functionName, propertyName, null, false);
    }

    // -----------------------------------------------------
    //                                                 Trunc
    //                                                 -----
    protected String processTrunc(String functionExp) {
        if (_trunc == null) {
            return functionExp;
        }
        // process purpose case
        if (isDateTypeColumn()) {
            final String processed = doProcessTruncPurposeDateType(functionExp);
            if (processed != null) {
                return processed;
            }
        }
        // process simple case
        return doProcessTruncSimpleCase(functionExp);
    }

    protected String doProcessTruncPurposeDateType(String functionExp) {
        final String processed;
        if (isDatabaseMySQL()) {
            processed = doProcessTruncPurposeDateTypeMySQL(functionExp);
        } else if (isDatabasePostgreSQL()) {
            processed = doProcessTruncPurposeDateTypePostgreSQL(functionExp);
        } else if (isDatabaseOracle()) {
            processed = doProcessTruncPurposeDateTypeOracle(functionExp);
        } else if (isDatabaseDB2()) {
            processed = doProcessTruncPurposeDateTypeDB2(functionExp);
        } else if (isDatabaseSQLServer()) {
            processed = doProcessTruncPurposeDateTypeSQLServer(functionExp);
        } else { // as default
            processed = doProcessTruncPurposeDateTypeDefault(functionExp);
        }
        return processed; // null means not processed or simple case (switched)
    }

    protected String doProcessTruncPurposeDateTypeMySQL(String functionExp) {
        if (isDateTruncMonth()) {
            return "cast(concat(substring(" + functionExp + ", 1, 4), '-01-01') as date)";
        } else if (isDateTruncDay()) {
            return "cast(concat(substring(" + functionExp + ", 1, 7), '-01') as date)";
        } else if (isDateTruncTime()) {
            return "cast(substring(" + functionExp + ", 1, 10) as date)";
        }
        return null;
        // e.g. cast(concat(substring(FOO_DATE, 1, 7), '-01') as date)
    }

    protected String doProcessTruncPurposeDateTypePostgreSQL(String functionExp) {
        // PostgreSQL can treat it as simple case by only switching
        if (isDateTruncMonth()) {
            _trunc = "year";
        } else if (isDateTruncDay()) {
            _trunc = "month";
        } else if (isDateTruncTime()) {
            _trunc = "day";
        }
        return null;
        // e.g. trunc(FOO_DATE, 'month')
    }

    protected String doProcessTruncPurposeDateTypeOracle(String functionExp) {
        // Oracle can treat it as simple case by only switching
        if (isDateTruncMonth()) {
            _trunc = "YYYY";
        } else if (isDateTruncDay()) {
            _trunc = "MM";
        } else if (isDateTruncTime()) {
            _trunc = "DD";
        }
        return null;
        // e.g. trunc(FOO_DATE, 'MM')
    }

    protected String doProcessTruncPurposeDateTypeDB2(String functionExp) {
        // DB2 is interested in difference between date and time-stamp
        final String baseExp = "cast(to_char(" + functionExp + ", 'yyyy";
        final String timePartBasicSuffix = isJustDateTypeColumn() ? "" : " 00:00:00";
        final String finalType = isJustDateTypeColumn() ? "date" : "timestamp";
        if (isDateTruncMonth()) {
            return baseExp + "') || '-01-01" + timePartBasicSuffix + "' as " + finalType + ")";
        } else if (isDateTruncDay()) {
            return baseExp + "-MM') || '-01" + timePartBasicSuffix + "' as " + finalType + ")";
        } else if (isDateTruncTime()) {
            final String timePartConnectSuffix = isJustDateTypeColumn() ? "" : " || ' 00:00:00'";
            return baseExp + "-MM-dd')" + timePartConnectSuffix + " as " + finalType + ")";
        }
        return null;
        // e.g. cast(to_char(FOO_DATE || '-01') as date)
    }

    protected String doProcessTruncPurposeDateTypeSQLServer(String functionExp) {
        final String baseExp = "cast(substring(convert(nvarchar, ";
        final String finalType = "datetime";
        if (isDateTruncMonth()) {
            return baseExp + functionExp + ", 120), 1, 4) + '-01-01' as " + finalType + ")";
        } else if (isDateTruncDay()) {
            return baseExp + functionExp + ", 120), 1, 7) + '-01' as " + finalType + ")";
        } else if (isDateTruncTime()) {
            return baseExp + functionExp + ", 120), 1, 10) as " + finalType + ")";
        }
        return null;
        // e.g. cast(substring(convert(nvarchar, FOO_DATE, 120), 1, 7) + '-01' as datetime)
    }

    protected String doProcessTruncPurposeDateTypeDefault(String functionExp) {
        final String baseExp = "cast(substring(";
        final String finalType = "date";
        if (isDateTruncMonth()) {
            return baseExp + functionExp + ", 1, 4) || '-01-01' as " + finalType + ")";
        } else if (isDateTruncDay()) {
            return baseExp + functionExp + ", 1, 7) || '-01' as " + finalType + ")";
        } else if (isDateTruncTime()) {
            return baseExp + functionExp + ", 1, 10) as " + finalType + ")";
        }
        return null;
        // e.g. cast(substring(FOO_DATE, 1, 7) || '-01' as date)
    }

    protected boolean isDateTruncMonth() {
        return _trunc.equals(DATE_TRUNC_MONTH);
    }

    protected boolean isDateTruncDay() {
        return _trunc.equals(DATE_TRUNC_DAY);
    }

    protected boolean isDateTruncTime() {
        return _trunc.equals(DATE_TRUNC_TIME);
    }

    protected String doProcessTruncSimpleCase(String functionExp) {
        final String functionName;
        String thirdArg = null;
        boolean leftArg = false;
        if (isTruncNamedTruncate()) {
            functionName = "truncate";
        } else if (isDatabaseSQLServer()) {
            functionName = "round";
            thirdArg = "1";
        } else if (isDatabasePostgreSQL() && isDateTypeColumn()) {
            functionName = "date_trunc";
            leftArg = true;
        } else {
            functionName = "trunc";
        }
        return processSimpleFunction(functionExp, functionName, "trunc", thirdArg, leftArg);
    }

    protected boolean isTruncNamedTruncate() {
        return isDatabaseMySQL() || isDatabaseH2();
    }

    // -----------------------------------------------------
    //                                               DateAdd
    //                                               -------
    protected String processAddYear(String functionExp) {
        return doProcessDateAdd(functionExp, _addYear, "addYear");
    }

    protected String processAddMonth(String functionExp) {
        return doProcessDateAdd(functionExp, _addMonth, "addMonth");
    }

    protected String processAddDay(String functionExp) {
        return doProcessDateAdd(functionExp, _addDay, "addDay");
    }

    protected String processAddHour(String functionExp) {
        return doProcessDateAdd(functionExp, _addHour, "addHour");
    }

    protected String processAddMinute(String functionExp) {
        return doProcessDateAdd(functionExp, _addMinute, "addMinute");
    }

    protected String processAddSecond(String functionExp) {
        return doProcessDateAdd(functionExp, _addSecond, "addSecond");
    }

    protected String doProcessDateAdd(String functionExp, Integer addedValue, String propertyName) {
        if (addedValue == null) {
            return functionExp;
        }
        if (!isDateTypeColumn()) { // basically no way
            String msg = "The column should be Date type for the function addXxx():";
            msg = msg + " column=" + _targetColumnInfo;
            throw new IllegalStateException(msg);
        }
        if (isDatabaseMySQL()) {
            return doProcessDateAddMySQL(functionExp, addedValue, propertyName);
        } else if (isDatabasePostgreSQL()) {
            return doProcessDateAddPostgreSQL(functionExp, addedValue, propertyName);
        } else if (isDatabaseOracle()) {
            return doProcessDateAddOracle(functionExp, addedValue, propertyName);
        } else if (isDatabaseDB2()) {
            return doProcessDateAddDB2(functionExp, addedValue, propertyName);
        } else if (isDatabaseSQLServer()) {
            return doProcessDateAddSQLServer(functionExp, addedValue, propertyName);
        } else if (isDatabaseH2()) { // same as SQLServer
            return doProcessDateAddSQLServer(functionExp, addedValue, propertyName);
        } else {
            String msg = "Unsupported database to the function addXxx(): " + propertyName;
            throw new IllegalStateException(msg);
        }
    }

    protected String doProcessDateAddMySQL(String functionExp, Integer addedValue, String propertyName) {
        final String type = buildDateAddExpType(propertyName, null, false);
        final String bindParameter = buildBindParameter(propertyName);
        return "date_add(" + functionExp + ", interval " + bindParameter + " " + type + ")";
        // e.g. date_add(FOO_DATE, interval 1 month)
    }

    protected String doProcessDateAddPostgreSQL(String functionExp, Integer addedValue, String propertyName) {
        // no binding because it does not allowed
        final String type = buildDateAddExpType(propertyName, null, true);
        if (isJustDateTypeColumn()) {
            return "cast(" + functionExp + " as timestamp) + '" + addedValue + " " + type + "'";
        } else {
            return functionExp + " + '" + addedValue + " " + type + "'";
        }
        // e.g.
        //  o cast(FOO_DATE as timestamp) + '1 months'
        //  o FOO_DATE + '1 months'
    }

    protected String doProcessDateAddOracle(String functionExp, Integer addedValue, String propertyName) {
        final String bindParameter = buildBindParameter(propertyName);
        if (isPropertyAddYear(propertyName)) {
            return "add_months(" + functionExp + ", 12 * " + bindParameter + ")";
        } else if (isPropertyAddMonth(propertyName)) {
            return "add_months(" + functionExp + ", " + bindParameter + ")";
        } else if (isPropertyAddDay(propertyName)) {
            return functionExp + " + " + bindParameter;
        } else if (isPropertyAddHour(propertyName)) {
            return functionExp + " + " + bindParameter + " / 24";
        } else if (isPropertyAddMinute(propertyName)) {
            return functionExp + " + " + bindParameter + " / 1440";
        } else if (isPropertyAddSecond(propertyName)) {
            return functionExp + " + " + bindParameter + " / 86400";
        } else {
            String msg = "Unknown property for date-add: " + propertyName;
            throw new IllegalStateException(msg);
        }
        // e.g.
        //  o add_months(FOO_DATE, 1)
        //  o FOO_DATE + 1
        //  o FOO_DATE + 1 / 24
    }

    protected String doProcessDateAddDB2(String functionExp, Integer addedValue, String propertyName) {
        final String bindParameter = buildBindParameter(propertyName);
        final String type = buildDateAddExpType(propertyName, null, false);
        return functionExp + " + " + bindParameter + " " + type;
        // e.g. FOO_DATE + 1 month
    }

    protected String doProcessDateAddSQLServer(String functionExp, Integer addedValue, String propertyName) {
        // no binding for correct formatting (related to leftArg binding problem)
        // it does not need to bind here in the first place because of specified as Integer
        final String type = buildDateAddExpType(propertyName, null, false);
        return "dateadd(" + type + ", " + addedValue + ", " + functionExp + ")";
        // e.g. dateadd(month, 1, FOO_DATE)
    }

    protected String buildDateAddExpType(String propertyName, String prefix, boolean plural) {
        prefix = (prefix != null ? prefix : "");
        final String suffix = plural ? "s" : "";
        final String type;
        if (isPropertyAddYear(propertyName)) {
            type = prefix + "year" + suffix;
        } else if (isPropertyAddMonth(propertyName)) {
            type = prefix + "month" + suffix;
        } else if (isPropertyAddDay(propertyName)) {
            type = prefix + "day" + suffix;
        } else if (isPropertyAddHour(propertyName)) {
            type = prefix + "hour" + suffix;
        } else if (isPropertyAddMinute(propertyName)) {
            type = prefix + "minute" + suffix;
        } else if (isPropertyAddSecond(propertyName)) {
            type = prefix + "second" + suffix;
        } else {
            String msg = "Unknown property for date-add: " + propertyName;
            throw new IllegalStateException(msg);
        }
        return type;
    }

    protected boolean isPropertyAddYear(String propertyName) {
        return "addYear".equals(propertyName);
    }

    protected boolean isPropertyAddMonth(String propertyName) {
        return "addMonth".equals(propertyName);
    }

    protected boolean isPropertyAddDay(String propertyName) {
        return "addDay".equals(propertyName);
    }

    protected boolean isPropertyAddHour(String propertyName) {
        return "addHour".equals(propertyName);
    }

    protected boolean isPropertyAddMinute(String propertyName) {
        return "addMinute".equals(propertyName);
    }

    protected boolean isPropertyAddSecond(String propertyName) {
        return "addSecond".equals(propertyName);
    }

    // -----------------------------------------------------
    //                                               Various
    //                                               -------
    /**
     * Process calculation filters defined by sub-class. (for extension)
     * @param functionExp The expression of derived function. (NotNull)
     * @return The filtered expression. (NotNull)
     */
    protected String processCalculation(String functionExp) { // for extension
        return functionExp;
    }

    /**
     * Process various filters defined by user. (for extension)
     * @param functionExp The expression of derived function. (NotNull)
     * @return The filtered expression. (NotNull)
     */
    protected String processVarious(String functionExp) { // for extension
        return functionExp;
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected String processSimpleFunction(String functionExp, String functionName, String propertyName,
            String thirdArg, boolean leftArg) {
        final String bindParameter = buildBindParameter(propertyName);
        final StringBuilder sb = new StringBuilder();
        sb.append(functionName).append("(");
        final String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        final boolean handleSqEnd = hasSubQueryEndOnLastLine(functionExp);
        final String pureFunction = handleSqEnd ? Srl.substringLastFront(functionExp, sqend) : functionExp;
        if (leftArg) { // for example, PostgreSQL's date_trunc()
            sb.append(bindParameter);
            if (handleSqEnd) {
                // leftArg binding breaks formatting so add line here
                // it's not perfect but almost OK
                sb.append(ln()).append("       ");
            }
            sb.append(", ").append(pureFunction);
        } else { // normal
            sb.append(pureFunction).append(", ").append(bindParameter);
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(thirdArg)) {
            sb.append(", ").append(thirdArg);
        }
        sb.append(")");
        if (handleSqEnd) {
            sb.append(sqend).append(Srl.substringLastRear(functionExp, sqend));
        }
        return sb.toString();
    }

    protected boolean hasSubQueryEndOnLastLine(String functionExp) {
        return SubQueryIndentProcessor.hasSubQueryEndOnLastLine(functionExp);
    }

    protected String buildBindParameter(String propertyName) {
        return "/*pmb." + _parameterMapPath + "." + _parameterKey + "." + propertyName + "*/null";
    }

    protected boolean isDateTypeColumn() {
        return _targetColumnInfo != null && Date.class.isAssignableFrom(_targetColumnInfo.getPropertyType());
    }

    protected boolean isJustDateTypeColumn() {
        return _targetColumnInfo != null && Date.class.equals(_targetColumnInfo.getPropertyType());
    }

    // ===================================================================================
    //                                                                    Parameter Option
    //                                                                    ================
    public void acceptParameterKey(String parameterKey, String parameterMapPath) {
        _parameterKey = parameterKey;
        _parameterMapPath = parameterMapPath;
    }

    // ===================================================================================
    //                                                                    Create Processor
    //                                                                    ================
    public SpecifyDerivedReferrer createSpecifyDerivedReferrer(SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, String subQueryIdentity, DBMeta subQueryDBMeta,
            GearedCipherManager cipherManager, String mainSubQueryIdentity, String aliasName) {
        return new SpecifyDerivedReferrer(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel,
                subQueryClause, subQueryIdentity, subQueryDBMeta, cipherManager, mainSubQueryIdentity, aliasName);
    }

    public QueryDerivedReferrer createQueryDerivedReferrer(SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, String subQueryIdentity, DBMeta subQueryDBMeta,
            GearedCipherManager cipherManager, String mainSubQueryIdentity, String operand, Object value,
            String parameterPath) {
        return new QueryDerivedReferrer(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel,
                subQueryClause, subQueryIdentity, subQueryDBMeta, cipherManager, mainSubQueryIdentity, operand, value,
                parameterPath);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean mayNullRevived() { // basically for auto-detect of inner-join
        // coalesce can change a null value to an existing value
        return _coalesce != null;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertCalculationColumnNumber(HpSpecifiedColumn specifiedColumn) {
        final ColumnInfo columnInfo = specifiedColumn.getColumnInfo();
        if (columnInfo == null) { // basically not null but just in case
            return;
        }
        if (!columnInfo.isPropertyTypeNumber()) {
            String msg = "The type of the calculation column should be Number: " + specifiedColumn;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertSpecifiedDreamCruiseTicket(HpSpecifiedColumn column) {
        if (!column.isDreamCruiseTicket()) {
            final String msg = "The specified column was not dream cruise ticket: " + column;
            throw new IllegalConditionBeanOperationException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        return title + ":{coalesce=" + _coalesce + ", round=" + _round + ", trunc=" + _trunc + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getCoalesce() {
        return _coalesce;
    }

    public Object getRound() {
        return _round;
    }

    public Object getTrunc() {
        return _trunc;
    }

    public Object getAddYear() {
        return _addYear;
    }

    public Object getAddMonth() {
        return _addMonth;
    }

    public Object getAddDay() {
        return _addDay;
    }

    public Object getAddHour() {
        return _addHour;
    }

    public Object getAddMinute() {
        return _addMinute;
    }

    public Object getAddSecond() {
        return _addSecond;
    }

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    public void xsetTargetColumnInfo(ColumnInfo targetColumnInfo) {
        _targetColumnInfo = targetColumnInfo;
    }

    public void xjudgeDatabase(SqlClause sqlClause) {
        setDatabaseMySQL(sqlClause instanceof SqlClauseMySql);
        setDatabasePostgreSQL(sqlClause instanceof SqlClausePostgreSql);
        setDatabaseOracle(sqlClause instanceof SqlClauseOracle);
        setDatabaseDB2(sqlClause instanceof SqlClauseDb2);
        setDatabaseSQLServer(sqlClause instanceof SqlClauseSqlServer);
        setDatabaseH2(sqlClause instanceof SqlClauseH2);
        setDatabaseDerby(sqlClause instanceof SqlClauseDerby);
    }

    protected boolean isDatabaseMySQL() {
        return _databaseMySQL;
    }

    protected void setDatabaseMySQL(boolean databaseMySQL) {
        _databaseMySQL = databaseMySQL;
    }

    protected boolean isDatabasePostgreSQL() {
        return _databasePostgreSQL;
    }

    protected void setDatabasePostgreSQL(boolean databasePostgreSQL) {
        _databasePostgreSQL = databasePostgreSQL;
    }

    protected boolean isDatabaseOracle() {
        return _databaseOracle;
    }

    protected void setDatabaseOracle(boolean databaseOracle) {
        _databaseOracle = databaseOracle;
    }

    protected boolean isDatabaseDB2() {
        return _databaseDB2;
    }

    protected void setDatabaseDB2(boolean databaseDB2) {
        _databaseDB2 = databaseDB2;
    }

    protected boolean isDatabaseSQLServer() {
        return _databaseSQLServer;
    }

    protected void setDatabaseSQLServer(boolean databaseSQLServer) {
        _databaseSQLServer = databaseSQLServer;
    }

    protected boolean isDatabaseH2() {
        return _databaseH2;
    }

    protected void setDatabaseH2(boolean databaseH2) {
        _databaseH2 = databaseH2;
    }

    protected boolean isDatabaseDerby() {
        return _databaseDerby;
    }

    protected void setDatabaseDerby(boolean databaseDerby) {
        _databaseDerby = databaseDerby;
    }
}
