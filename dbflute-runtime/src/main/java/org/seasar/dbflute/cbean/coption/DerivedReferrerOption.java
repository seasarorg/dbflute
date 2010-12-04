/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.subquery.QueryDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SpecifyDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryIndentProcessor;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryPath;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The option for DerivedReferrer.
 * @author jflute
 */
public class DerivedReferrerOption implements ParameterOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _coalesce;
    protected Object _round;
    protected Object _trunc;
    protected LinkedHashMap<String, ProcessCallback> _callbackMap; // order should be guaranteed
    protected String _parameterKey;
    protected String _parameterMapPath;

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    protected ColumnInfo _targetColumnInfo;
    protected boolean _databaseMySQL;
    protected boolean _databasePostgreSQL;
    protected boolean _databaseSQLServer;
    protected boolean _databaseH2;

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set the value for coalesce function. <br />
     * If you set string value and the derived column is date type, it converts it to a date object internally.
     * For example, "2010-10-30 12:34:56.789", "2010/10/30" and so on ... are acceptable.
     * @param coalesce An alternate value when group function returns null. (Nullable: if null, no coalesce)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption coalesce(Object coalesce) {
        _coalesce = coalesce;
        addProcessCallback("coalesce", new ProcessCallback() {
            public String callback(String functionExp) {
                return processCoalesce(functionExp);
            }
        });
        return this;
    }

    /**
     * Set the value for round function.
     * @param round Decimal digits or date format for round. (Nullable: if null, no round)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption round(Object round) {
        _round = round;
        addProcessCallback("round", new ProcessCallback() {
            public String callback(String functionExp) {
                return processRound(functionExp);
            }
        });
        return this;
    }

    /**
     * Set the value for trunc function.
     * @param trunc Decimal digits or date format for trunc. (Nullable: if null, no trunc)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption trunc(Object trunc) {
        _trunc = trunc;
        addProcessCallback("trunc", new ProcessCallback() {
            public String callback(String functionExp) {
                return processTrunc(functionExp);
            }
        });
        return this;
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    public String filterFunction(String functionExp) {
        String filtered = functionExp;
        final LinkedHashMap<String, ProcessCallback> callbackMap = _callbackMap;
        if (callbackMap != null) {
            final Set<Entry<String, ProcessCallback>> entrySet = callbackMap.entrySet();
            for (Entry<String, ProcessCallback> entry : entrySet) {
                filtered = entry.getValue().callback(filtered);
            }
        }
        return processVarious(filtered);
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
    protected String processCoalesce(String functionExp) {
        if (_coalesce instanceof String && isDateTypeColumn()) {
            _coalesce = DfTypeUtil.toDate(_coalesce);
        }
        final String functionName = "coalesce";
        final String propertyName = functionName;
        return processSimpleFunction(functionExp, _coalesce, functionName, propertyName, null, false);
    }

    protected String processRound(String functionExp) {
        final String functionName = "round";
        final String propertyName = functionName;
        return processSimpleFunction(functionExp, _round, functionName, propertyName, null, false);
    }

    protected String processTrunc(String functionExp) {
        final String functionName;
        final String thirdArg;
        final boolean leftArg;
        if (isTruncTrancate()) {
            functionName = "truncate";
            thirdArg = null;
            leftArg = false;
        } else if (isDatabaseSQLServer()) {
            functionName = "round";
            thirdArg = "1";
            leftArg = false;
        } else if (isDatabasePostgreSQL() && isDateTypeColumn()) {
            functionName = "date_trunc";
            thirdArg = null;
            leftArg = true;
        } else {
            functionName = "trunc";
            thirdArg = null;
            leftArg = false;
        }
        return processSimpleFunction(functionExp, _trunc, functionName, "trunc", thirdArg, leftArg);
    }

    protected boolean isTruncTrancate() {
        return isDatabaseMySQL() || isDatabaseH2();
    }

    protected String processVarious(String functionExp) { // for extension
        return functionExp;
    }

    protected String processSimpleFunction(String functionExp, Object specifiedValue, String functionName,
            String propertyName, String thirdArg, boolean leftArg) {
        if (specifiedValue == null) {
            return functionExp;
        }
        final String bindParameter = buildBindParameter(propertyName);
        final StringBuilder sb = new StringBuilder();
        sb.append(functionName).append("(");
        final String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        final boolean handleSqEnd = needsHandleSubQueryEnd(functionExp);
        final String pureFunction = handleSqEnd ? Srl.substringLastFront(functionExp, sqend) : functionExp;
        if (leftArg) { // for example, PostgreSQL's date_trunc()
            sb.append(bindParameter).append(", ").append(pureFunction);
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

    protected boolean needsHandleSubQueryEnd(String functionExp) {
        final String sqend = SubQueryIndentProcessor.END_MARK_PREFIX;
        if (functionExp.contains(ln())) {
            final String lastLine = Srl.substringLastRear(functionExp, ln());
            if (lastLine.contains(sqend)) {
                return true; // a last line has sub-query end mark
            }
        }
        return false;
    }

    protected String buildBindParameter(String propertyName) {
        return "/*pmb." + _parameterMapPath + "." + _parameterKey + "." + propertyName + "*/null";
    }

    protected boolean isDateTypeColumn() {
        return _targetColumnInfo != null && Date.class.isAssignableFrom(_targetColumnInfo.getPropertyType());
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
            String mainSubQueryIdentity, String aliasName) {
        return new SpecifyDerivedReferrer(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel,
                subQueryClause, subQueryIdentity, subQueryDBMeta, mainSubQueryIdentity, aliasName);
    }

    public QueryDerivedReferrer createQueryDerivedReferrer(SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, String subQueryIdentity, DBMeta subQueryDBMeta,
            String mainSubQueryIdentity, String operand, Object value, String parameterPath) {
        return new QueryDerivedReferrer(subQueryPath, localRealNameProvider, subQuerySqlNameProvider, subQueryLevel,
                subQueryClause, subQueryIdentity, subQueryDBMeta, mainSubQueryIdentity, operand, value, parameterPath);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String ln() {
        return DfSystemUtil.getLineSeparator();
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

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    public void setTargetColumnInfo(ColumnInfo targetColumnInfo) {
        _targetColumnInfo = targetColumnInfo;
    }

    protected boolean isDatabaseMySQL() {
        return _databaseMySQL;
    }

    public void setDatabaseMySQL(boolean databaseMySQL) {
        _databaseMySQL = databaseMySQL;
    }

    protected boolean isDatabasePostgreSQL() {
        return _databasePostgreSQL;
    }

    public void setDatabasePostgreSQL(boolean databasePostgreSQL) {
        _databasePostgreSQL = databasePostgreSQL;
    }

    protected boolean isDatabaseSQLServer() {
        return _databaseSQLServer;
    }

    public void setDatabaseSQLServer(boolean databaseSQLServer) {
        _databaseSQLServer = databaseSQLServer;
    }

    protected boolean isDatabaseH2() {
        return _databaseH2;
    }

    public void setDatabaseH2(boolean databaseH2) {
        _databaseH2 = databaseH2;
    }
}
