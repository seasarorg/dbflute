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
package org.seasar.dbflute.cbean.coption;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.subquery.QueryDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SpecifyDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryLevelReflector;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryPath;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The option for DerivedReferrer.
 * @author jflute
 */
public class DerivedReferrerOption implements ParameterOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _coalesce;
    protected Integer _round;
    protected Integer _trunc;
    protected LinkedHashMap<String, ProcessCallback> _callbackMap; // order should be guaranteed
    protected String _parameterKey;
    protected String _parameterMapPath;

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    protected ColumnInfo _targetColumnInfo;

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set the value for coalesce function. <br />
     * If you set string value and the column is date type, convert it to a date object.
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
     * @param round Decimal digits for round. (Nullable: if null, no round)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption round(Integer round) {
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
     * @param trunc Decimal digits for trunc. (Nullable: if null, no trunc)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption trunc(Integer trunc) {
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
        return processSimpleFunction(functionExp, _coalesce, "coalesce");
    }

    protected String processRound(String functionExp) {
        return processSimpleFunction(functionExp, _round, "round");
    }

    protected String processTrunc(String functionExp) {
        return processSimpleFunction(functionExp, _trunc, "trunc");
    }

    protected String processVarious(String functionExp) { // for extension
        return functionExp;
    }

    protected String processSimpleFunction(String functionExp, Object specifiedValue, String functionName) {
        if (specifiedValue == null) {
            return functionExp;
        }
        return functionName + "(" + functionExp + ", " + buildBindParameter(functionName) + ")";
    }

    protected String buildBindParameter(String optionKey) {
        return "/*pmb." + _parameterMapPath + "." + _parameterKey + "." + optionKey + "*/null";
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
    public SpecifyDerivedReferrer createSpecifyDerivedReferrer(SqlClause sqlClause, SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, SubQueryLevelReflector reflector, String subQueryIdentity,
            DBMeta subQueryDBMeta, String mainSubQueryIdentity, String aliasName) {
        return new SpecifyDerivedReferrer(sqlClause, subQueryPath, localRealNameProvider, subQuerySqlNameProvider,
                subQueryLevel, subQueryClause, reflector, subQueryIdentity, subQueryDBMeta, mainSubQueryIdentity,
                aliasName);
    }

    public QueryDerivedReferrer createQueryDerivedReferrer(SqlClause sqlClause, SubQueryPath subQueryPath,
            ColumnRealNameProvider localRealNameProvider, ColumnSqlNameProvider subQuerySqlNameProvider,
            int subQueryLevel, SqlClause subQueryClause, SubQueryLevelReflector reflector, String subQueryIdentity,
            DBMeta subQueryDBMeta, String mainSubQueryIdentity, String operand, Object value, String parameterPath) {
        return new QueryDerivedReferrer(sqlClause, subQueryPath, localRealNameProvider, subQuerySqlNameProvider,
                subQueryLevel, subQueryClause, reflector, subQueryIdentity, subQueryDBMeta, mainSubQueryIdentity,
                operand, value, parameterPath);
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

    public Integer getRound() {
        return _round;
    }

    public Integer getTrunc() {
        return _trunc;
    }

    // -----------------------------------------------------
    //                                    called by internal
    //                                    ------------------
    public void setTargetColumnInfo(ColumnInfo targetColumnInfo) {
        _targetColumnInfo = targetColumnInfo;
    }
}
