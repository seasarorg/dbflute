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

import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.subquery.QueryDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SpecifyDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryLevelReflector;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryPath;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;

/**
 * The option for DerivedReferrer.
 * @author jflute
 */
public class DerivedReferrerOption implements ParameterOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _coalesce;
    protected String _parameterKey;
    protected String _parameterMapPath;

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    /**
     * Set the value for coalesce function.
     * @param coalesce An alternate value when group function returns null. (Nullable)
     * @return this. (NotNull)
     */
    public DerivedReferrerOption coalesce(Object coalesce) {
        _coalesce = coalesce;
        return this;
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    public String filterFunction(String functionExp) {
        return processCoalesce(functionExp);
    }

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    protected String processCoalesce(String functionExp) {
        if (_coalesce == null) {
            return functionExp;
        }
        final String coalesce;
        if (_parameterKey != null) {
            coalesce = buildBindParameter("coalesce");
        } else {
            final String plain = _coalesce.toString();
            if (_coalesce instanceof Number) {
                coalesce = plain;
            } else {
                coalesce = "'" + plain + "'";
            }
        }
        return "coalesce(" + functionExp + ", " + coalesce + ")";
    }

    protected String buildBindParameter(String optionKey) {
        return "/*pmb." + _parameterMapPath + "." + _parameterKey + "." + optionKey + "*/null";
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
    //                                                                            Accessor
    //                                                                            ========
    public Object getCoalesce() {
        return _coalesce;
    }
}
