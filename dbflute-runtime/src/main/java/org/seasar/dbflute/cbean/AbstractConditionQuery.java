/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.cbean.ManualOrderBean.FreeParameterManualOrderThemeListHandler;
import org.seasar.dbflute.cbean.chelper.HpDerivingSubQueryInfo;
import org.seasar.dbflute.cbean.chelper.HpFixedConditionQueryResolver;
import org.seasar.dbflute.cbean.chelper.HpInvalidQueryInfo;
import org.seasar.dbflute.cbean.chelper.HpQDRParameter;
import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.ckey.ConditionKeyInScope;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.ParameterOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.cvalue.ConditionValue.QueryModeProvider;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseMySql;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseOracle;
import org.seasar.dbflute.cbean.sqlclause.join.FixedConditionResolver;
import org.seasar.dbflute.cbean.sqlclause.query.QueryClauseArranger;
import org.seasar.dbflute.cbean.sqlclause.subquery.ExistsReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.InScopeRelation;
import org.seasar.dbflute.cbean.sqlclause.subquery.QueryDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.ScalarCondition;
import org.seasar.dbflute.cbean.sqlclause.subquery.SpecifyDerivedReferrer;
import org.seasar.dbflute.cbean.sqlclause.subquery.SubQueryPath;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnRealNameProvider;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlNameProvider;
import org.seasar.dbflute.dbway.ExtensionOperand;
import org.seasar.dbflute.dbway.WayOfMySQL;
import org.seasar.dbflute.exception.ConditionInvokingFailureException;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.exception.OrScopeQueryAndPartUnsupportedOperationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ParameterUtil;
import org.seasar.dbflute.jdbc.ParameterUtil.ShortCharHandlingMode;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil.ReflectionFailureException;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The abstract class of condition-query.
 * @author jflute
 */
public abstract class AbstractConditionQuery implements ConditionQuery {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final ConditionKey CK_EQ = ConditionKey.CK_EQUAL;
    protected static final ConditionKey CK_NES = ConditionKey.CK_NOT_EQUAL_STANDARD;
    protected static final ConditionKey CK_NET = ConditionKey.CK_NOT_EQUAL_TRADITION;
    protected static final ConditionKey CK_GT = ConditionKey.CK_GREATER_THAN;
    protected static final ConditionKey CK_LT = ConditionKey.CK_LESS_THAN;
    protected static final ConditionKey CK_GE = ConditionKey.CK_GREATER_EQUAL;
    protected static final ConditionKey CK_LE = ConditionKey.CK_LESS_EQUAL;
    protected static final ConditionKey CK_INS = ConditionKey.CK_IN_SCOPE;
    protected static final ConditionKey CK_NINS = ConditionKey.CK_NOT_IN_SCOPE;
    protected static final ConditionKey CK_LS = ConditionKey.CK_LIKE_SEARCH;
    protected static final ConditionKey CK_NLS = ConditionKey.CK_NOT_LIKE_SEARCH;
    protected static final ConditionKey CK_ISN = ConditionKey.CK_IS_NULL;
    protected static final ConditionKey CK_ISNOE = ConditionKey.CK_IS_NULL_OR_EMPTY;
    protected static final ConditionKey CK_ISNN = ConditionKey.CK_IS_NOT_NULL;

    /** Object for DUMMY. */
    protected static final Object DOBJ = new Object();

    /** The property of condition-query. */
    protected static final String CQ_PROPERTY = "conditionQuery";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** SQL clause. */
    protected final SqlClause _sqlClause;

    /** My alias name. */
    protected final String _aliasName;

    /** The nest level of relation. */
    protected final int _nestLevel;

    /** The level of subQuery. */
    protected int _subQueryLevel;

    // -----------------------------------------------------
    //                                          Foreign Info
    //                                          ------------
    /** The property name of foreign. */
    protected String _foreignPropertyName;

    /** The path of relation. */
    protected String _relationPath;

    /** The referrer query. */
    protected final ConditionQuery _referrerQuery;

    // -----------------------------------------------------
    //                                                Inline
    //                                                ------
    /** Is it the in-line. */
    protected boolean _inline;

    /** Is it on-clause. */
    protected boolean _onClause;

    // -----------------------------------------------------
    //                                      Parameter Option
    //                                      ----------------
    /** The map of parameter option for parameter comment. */
    protected Map<String, ParameterOption> _parameterOptionMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param referrerQuery The instance of referrer query. (NullAllowed: If null, this is base query)
     * @param sqlClause The instance of SQL clause. (NotNull)
     * @param aliasName The alias name for this query. (NotNull)
     * @param nestLevel The nest level of this query. (If zero, this is base query)
     */
    public AbstractConditionQuery(ConditionQuery referrerQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        _referrerQuery = referrerQuery;
        _sqlClause = sqlClause;
        _aliasName = aliasName;
        _nestLevel = nestLevel;
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    /**
     * Get the provider of DB meta.
     * @return The provider of DB meta. (NotNull)
     */
    protected abstract DBMetaProvider xgetDBMetaProvider();

    /**
     * Find the DB meta.
     * @param tableFlexibleName The table flexible name. (NotNull)
     * @return The DB meta of the table. (NotNull)
     */
    protected DBMeta findDBMeta(String tableFlexibleName) {
        return xgetDBMetaProvider().provideDBMetaChecked(tableFlexibleName);
    }

    // ===================================================================================
    //                                                                  Important Accessor
    //                                                                  ==================
    /**
     * {@inheritDoc}
     */
    public ConditionQuery xgetReferrerQuery() {
        return _referrerQuery;
    }

    /**
     * {@inheritDoc}
     */
    public SqlClause xgetSqlClause() {
        return _sqlClause;
    }

    /**
     * {@inheritDoc}
     */
    public String xgetAliasName() {
        return _aliasName;
    }

    /**
     * {@inheritDoc}
     */
    public int xgetNestLevel() {
        return _nestLevel;
    }

    /**
     * {@inheritDoc}
     */
    public int xgetNextNestLevel() {
        return _nestLevel + 1;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBaseQuery() {
        return (xgetReferrerQuery() == null);
    }

    // -----------------------------------------------------
    //                                             Real Name
    //                                             ---------
    /**
     * {@inheritDoc}
     */
    public ColumnRealName toColumnRealName(String columnDbName) {
        return new ColumnRealName(xgetAliasName(), toColumnSqlName(columnDbName));
    }

    /**
     * {@inheritDoc}
     */
    public ColumnSqlName toColumnSqlName(String columnDbName) {
        return findDBMeta(getTableDbName()).findColumnInfo(columnDbName).getColumnSqlName();
    }

    // -----------------------------------------------------
    //                                          Foreign Info
    //                                          ------------
    /**
     * {@inheritDoc}
     */
    public String xgetForeignPropertyName() {
        return _foreignPropertyName;
    }

    public void xsetForeignPropertyName(String foreignPropertyName) {
        this._foreignPropertyName = foreignPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    public String xgetRelationPath() {
        return _relationPath;
    }

    public void xsetRelationPath(String relationPath) {
        this._relationPath = relationPath;
    }

    // -----------------------------------------------------
    //                                                Inline
    //                                                ------
    public void xsetOnClause(boolean onClause) {
        _onClause = onClause;
    }

    // -----------------------------------------------------
    //                                              Location
    //                                              --------
    /**
     * {@inheritDoc}
     */
    public String xgetLocationBase() {
        final StringBuilder sb = new StringBuilder();
        ConditionQuery query = this;
        while (true) {
            if (query.isBaseQuery()) {
                sb.insert(0, CQ_PROPERTY + ".");
                break;
            } else {
                final String foreignPropertyName = query.xgetForeignPropertyName();
                if (foreignPropertyName == null) {
                    String msg = "The foreignPropertyName of the query should not be null:";
                    msg = msg + " query=" + query;
                    throw new IllegalStateException(msg);
                }
                sb.insert(0, CQ_PROPERTY + initCap(foreignPropertyName) + ".");
            }
            query = query.xgetReferrerQuery();
        }
        return sb.toString();
    }

    /**
     * Get the location of the property.
     * @param propertyName The name of property. (NotNull)
     * @return The location of the property as path. (NotNull)
     */
    protected String xgetLocation(String propertyName) {
        return xgetLocationBase() + propertyName;
    }

    // ===================================================================================
    //                                                                  Nested SetupSelect
    //                                                                  ==================
    public void doNss(NssCall callback) { // very internal
        final String foreignPropertyName = callback.qf().xgetForeignPropertyName();
        final String foreignTableAliasName = callback.qf().xgetAliasName();
        final String localRelationPath = xgetRelationPath();
        final String foreignRelationPath = callback.qf().xgetRelationPath();
        xgetSqlClause().registerSelectedRelation(foreignTableAliasName, getTableDbName(), foreignPropertyName,
                localRelationPath, foreignRelationPath);
    }

    public static interface NssCall { // very internal
        public ConditionQuery qf();
    }

    // ===================================================================================
    //                                                                           OuterJoin
    //                                                                           =========
    /**
     * Register outer-join. (no fixed condition)
     * @param foreignCQ The condition-query for foreign table. (NotNull)
     * @param joinOnResourceMap The resource map of join condition on on-clause. (NotNull)
     * @param foreignPropertyName The property name of foreign corresponding to this join. (NotNull)
     */
    protected void registerOuterJoin(ConditionQuery foreignCQ, Map<String, String> joinOnResourceMap,
            String foreignPropertyName) {
        registerOuterJoin(foreignCQ, joinOnResourceMap, foreignPropertyName, null);
    }

    /**
     * Register outer-join with fixed-condition.
     * @param foreignCQ The condition-query for foreign table. (NotNull)
     * @param joinOnResourceMap The resource map of join condition on on-clause. (NotNull)
     * @param foreignPropertyName The property name of foreign relation corresponding to this join. (NotNull)
     * @param fixedCondition The plain fixed condition. (NullAllowed: if null, no fixed condition)
     */
    protected void registerOuterJoin(ConditionQuery foreignCQ, Map<String, String> joinOnResourceMap,
            String foreignPropertyName, String fixedCondition) {
        // translate join-on map using column real name
        final Map<ColumnRealName, ColumnRealName> joinOnMap = newLinkedHashMap();
        final Set<Entry<String, String>> entrySet = joinOnResourceMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            final String local = entry.getKey();
            final String foreign = entry.getValue();
            joinOnMap.put(toColumnRealName(local), foreignCQ.toColumnRealName(foreign));
        }
        final String foreignAlias = foreignCQ.xgetAliasName();
        final String foreignTable = foreignCQ.getTableDbName();
        final String localAlias = xgetAliasName();
        final String localTable = getTableDbName();
        final ForeignInfo foreignInfo = findDBMeta(getTableDbName()).findForeignInfo(foreignPropertyName);
        final FixedConditionResolver resolver = createFixedConditionResolver(foreignCQ, joinOnMap);
        xgetSqlClause().registerOuterJoin(foreignAlias, foreignTable, localAlias, localTable // basic
                , joinOnMap, foreignInfo // join objects
                , fixedCondition, resolver); // fixed condition
    }

    protected FixedConditionResolver createFixedConditionResolver(ConditionQuery foreignCQ,
            Map<ColumnRealName, ColumnRealName> joinOnMap) {
        return new HpFixedConditionQueryResolver(this, foreignCQ, xgetDBMetaProvider());
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    /** The map parameter-bean of union query. */
    protected SimpleMapPmb<ConditionQuery> _unionQueryMap;

    /**
     * Get the map parameter-bean of union query. (for parameter comment) {Internal}
     * @return The instance of map parameter-bean. (NotNull)
     */
    public SimpleMapPmb<ConditionQuery> getInternalUnionQueryMap() {
        if (_unionQueryMap == null) {
            _unionQueryMap = xcreateUnionMapPmb();
        }
        return _unionQueryMap;
    }

    /**
     * Set union query. {Internal}
     * @param unionQuery Union query. (NotNull)
     */
    public void xsetUnionQuery(ConditionQuery unionQuery) {
        xsetupUnion(unionQuery, false, getInternalUnionQueryMap());
    }

    /** The map parameter-bean of union all query. */
    protected SimpleMapPmb<ConditionQuery> _unionAllQueryMap;

    /**
     * Get the map parameter-bean of union all query. (for parameter comment) {Internal}
     * @return The instance of map parameter-bean. (NotNull)
     */
    public SimpleMapPmb<ConditionQuery> getInternalUnionAllQueryMap() {
        if (_unionAllQueryMap == null) {
            _unionAllQueryMap = xcreateUnionMapPmb();
        }
        return _unionAllQueryMap;
    }

    protected SimpleMapPmb<ConditionQuery> xcreateUnionMapPmb() {
        return new SimpleMapPmb<ConditionQuery>();
    }

    /**
     * Set union all query. {Internal}
     * @param unionAllQuery Union all query. (NotNull)
     */
    public void xsetUnionAllQuery(ConditionQuery unionAllQuery) {
        xsetupUnion(unionAllQuery, true, getInternalUnionAllQueryMap());
    }

    protected void xsetupUnion(ConditionQuery unionQuery, boolean unionAll, SimpleMapPmb<ConditionQuery> unionQueryMap) {
        if (unionQuery == null) {
            String msg = "The argument[unionQuery] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        reflectRelationOnUnionQuery(this, unionQuery); // Reflect Relation!
        final String key = (unionAll ? "unionAllQuery" : "unionQuery") + unionQueryMap.size();
        unionQueryMap.addParameter(key, unionQuery);
        final String propName = "internalUnion" + (unionAll ? "All" : "") + "QueryMap." + key;
        registerUnionQuery(unionQuery, unionAll, propName);
    }

    /**
     * Reflect relation on union query.
     * @param baseQueryAsSuper Base query as super. (NotNull)
     * @param unionQueryAsSuper Union query as super. (NotNull)
     */
    protected abstract void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper,
            ConditionQuery unionQueryAsSuper);

    /**
     * Has union query or union all query?
     * @return The determination, true or false.
     */
    public boolean hasUnionQueryOrUnionAllQuery() {
        return (_unionQueryMap != null && !_unionQueryMap.isEmpty())
                || (_unionAllQueryMap != null && !_unionAllQueryMap.isEmpty());
    }

    // ===================================================================================
    //                                                                      Register Query
    //                                                                      ==============
    // -----------------------------------------------------
    //                                          Normal Query
    //                                          ------------
    protected void regQ(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName) {
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        setupConditionValueAndRegisterWhereClause(key, value, cvalue, columnDbName);
    }

    protected void regQ(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName,
            ConditionOption option) {
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        setupConditionValueAndRegisterWhereClause(key, value, cvalue, columnDbName, option);
    }

    protected boolean isValidQueryChecked(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName) {
        return xdoIsValidQuery(key, value, cvalue, columnDbName, true);
    }

    protected boolean isValidQueryNoCheck(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName) {
        return xdoIsValidQuery(key, value, cvalue, columnDbName, false);
    }

    protected boolean xdoIsValidQuery(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName,
            boolean checked) {
        final ColumnRealName callerName = toColumnRealName(columnDbName); // logging only
        if (key.isValidRegistration(xcreateQueryModeProvider(), cvalue, value, callerName)) {
            return true;
        } else {
            if (checked) {
                handleInvalidQuery(key, value, columnDbName);
            }
            return false;
        }
    }

    protected void handleInvalidQuery(ConditionKey key, Object value, String columnDbName) {
        final HpInvalidQueryInfo invalidQueryInfo = xcreateInvalidQueryInfo(key, value, columnDbName);
        xdoHandleInvalidQuery(columnDbName, invalidQueryInfo);
    }

    protected void handleInvalidQueryList(List<ConditionKey> keyList, List<? extends Object> valueList,
            String columnDbName) {
        if (keyList.size() != valueList.size()) {
            String msg = "The argument 'keyList' should have the same size as 'valueList'";
            throw new IllegalArgumentException(msg);
        }
        final HpInvalidQueryInfo[] invalidQueryInfoAry = new HpInvalidQueryInfo[keyList.size()];
        int index = 0;
        for (ConditionKey key : keyList) {
            final Object value = valueList.get(index);
            invalidQueryInfoAry[index] = xcreateInvalidQueryInfo(key, value, columnDbName);
            ++index;
        }
        xdoHandleInvalidQuery(columnDbName, invalidQueryInfoAry);
    }

    protected void xdoHandleInvalidQuery(String columnDbName, HpInvalidQueryInfo... invalidQueryInfoAry) {
        if (xgetSqlClause().isInvalidQueryChecked()) {
            throwInvalidQueryRegisteredException(invalidQueryInfoAry);
        } else {
            for (HpInvalidQueryInfo invalidQueryInfo : invalidQueryInfoAry) {
                xgetSqlClause().saveInvalidQuery(invalidQueryInfo);
            }
        }
    }

    protected HpInvalidQueryInfo xcreateInvalidQueryInfo(ConditionKey key, Object value, String columnDbName) {
        final String locationBase = xgetLocationBase();
        final ColumnInfo targetColumn = findDBMeta(getTableDbName()).findColumnInfo(columnDbName);
        final HpInvalidQueryInfo invalidQueryInfo = new HpInvalidQueryInfo(locationBase, targetColumn, key, value);
        if (_inline) {
            invalidQueryInfo.inlineView();
        } else if (_onClause) {
            invalidQueryInfo.onClause();
        }
        return invalidQueryInfo;
    }

    protected QueryModeProvider xcreateQueryModeProvider() {
        return new QueryModeProvider() {
            public boolean isOrScopeQuery() {
                return xgetSqlClause().isOrScopeQueryEffective();
            }

            public boolean isInline() {
                return _inline;
            }

            public boolean isOnClause() {
                return _onClause;
            }
        };
    }

    protected void throwInvalidQueryRegisteredException(HpInvalidQueryInfo... invalidQueryInfoAry) {
        createCBExThrower().throwInvalidQueryRegisteredException(invalidQueryInfoAry);
    }

    // -----------------------------------------------------
    //                                         InScope Query
    //                                         -------------
    protected void regINS(ConditionKey key, List<?> value, ConditionValue cvalue, String columnDbName) {
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        final int inScopeLimit = xgetSqlClause().getInScopeLimit();
        if (inScopeLimit > 0 && value.size() > inScopeLimit) {
            // if the key is for inScope, it should be split as 'or'
            // (if the key is for notInScope, it should be split as 'and')
            final boolean orScopeQuery = xgetSqlClause().isOrScopeQueryEffective();
            final boolean orScopeQueryAndPart = xgetSqlClause().isOrScopeQueryAndPartEffective();
            final boolean needsAndPart = orScopeQuery && !orScopeQueryAndPart;
            if (isConditionKeyInScope(key)) {
                // if or-scope query has already been effective, create new or-scope
                xgetSqlClause().makeOrScopeQueryEffective();
            } else {
                if (needsAndPart) {
                    xgetSqlClause().beginOrScopeQueryAndPart();
                }
            }

            try {
                // split the condition
                @SuppressWarnings("unchecked")
                final List<Object> objectList = (List<Object>) value;
                final List<List<Object>> valueList = DfCollectionUtil.splitByLimit(objectList, inScopeLimit);
                for (int i = 0; i < valueList.size(); i++) {
                    final List<Object> currentValue = valueList.get(i);
                    if (i == 0) {
                        setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, columnDbName);
                    } else {
                        invokeQuery(columnDbName, key.getConditionKey(), currentValue);
                    }
                }
            } finally {
                if (isConditionKeyInScope(key)) {
                    xgetSqlClause().closeOrScopeQuery();
                } else {
                    if (needsAndPart) {
                        xgetSqlClause().endOrScopeQueryAndPart();
                    }
                }
            }
        } else {
            setupConditionValueAndRegisterWhereClause(key, value, cvalue, columnDbName);
        }
    }

    static boolean isConditionKeyInScope(ConditionKey key) { // default scope for test 
        return ConditionKeyInScope.class.isAssignableFrom(key.getClass());
    }

    // -----------------------------------------------------
    //                                          FromTo Query
    //                                          ------------
    protected void regFTQ(Date fromDate, Date toDate, ConditionValue cvalue, String columnDbName, FromToOption option) {
        final ConditionKey fromKey = option.getFromDateConditionKey();
        boolean fromInvalid = false;
        final Date filteredFromDate = option.filterFromDate(fromDate);
        if (isValidQueryNoCheck(fromKey, filteredFromDate, cvalue, columnDbName)) {
            setupConditionValueAndRegisterWhereClause(fromKey, filteredFromDate, cvalue, columnDbName);
        } else {
            fromInvalid = true;
        }
        final ConditionKey toKey = option.getToDateConditionKey();
        final Date filteredToDate = option.filterToDate(toDate);
        if (isValidQueryNoCheck(toKey, filteredToDate, cvalue, columnDbName)) {
            setupConditionValueAndRegisterWhereClause(toKey, filteredToDate, cvalue, columnDbName);
        } else {
            if (fromInvalid) { // means both queries are invalid
                final List<ConditionKey> keyList = newArrayList(fromKey, toKey);
                final List<Date> valueList = newArrayList(fromDate, toDate);
                handleInvalidQueryList(keyList, valueList, columnDbName);
            }
        }
    }

    // -----------------------------------------------------
    //                                      LikeSearch Query
    //                                      ----------------
    protected void regLSQ(ConditionKey key, String value, ConditionValue cvalue, String columnDbName,
            LikeSearchOption option) {
        registerLikeSearchQuery(key, value, cvalue, columnDbName, option);
    }

    protected void registerLikeSearchQuery(ConditionKey key, String value, ConditionValue cvalue, String columnDbName,
            LikeSearchOption option) {
        if (option == null) {
            throwLikeSearchOptionNotFoundException(columnDbName, value);
            return; // unreachable
        }
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        if (xsuppressEscape()) {
            option.notEscape();
        }
        // basically for DBMS that has original wild-cards
        xgetSqlClause().adjustLikeSearchEscape(option);

        if (value == null || !option.isSplit()) {
            // as normal condition
            setupConditionValueAndRegisterWhereClause(key, value, cvalue, columnDbName, option);
            return;
        }
        // - - - - - - - - -
        // Use splitByXxx().
        // - - - - - - - - -
        // these values should be valid only (already filtered before)
        // and invalid values are ignored even at the check mode
        // but if all elements are invalid, it is an exception
        final String[] strArray = option.generateSplitValueArray(value);
        if (strArray.length == 0) {
            handleInvalidQuery(key, value, columnDbName);
            return;
        }
        final boolean orScopeQuery = xgetSqlClause().isOrScopeQueryEffective();
        final boolean orScopeQueryAndPart = xgetSqlClause().isOrScopeQueryAndPartEffective();
        if (!option.isAsOrSplit()) {
            // as 'and' condition
            final boolean needsAndPart = orScopeQuery && !orScopeQueryAndPart;
            if (needsAndPart) {
                xgetSqlClause().beginOrScopeQueryAndPart();
            }
            try {
                for (int i = 0; i < strArray.length; i++) {
                    final String currentValue = strArray[i];
                    setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, columnDbName, option);
                }
            } finally {
                if (needsAndPart) {
                    xgetSqlClause().endOrScopeQueryAndPart();
                }
            }
        } else {
            // as 'or' condition
            if (orScopeQueryAndPart) {
                // limit because of so complex
                String msg = "The AsOrSplit in and-part is unsupported: " + getTableDbName();
                throw new OrScopeQueryAndPartUnsupportedOperationException(msg);
            }
            final boolean needsNewOrScope = !orScopeQuery;
            if (needsNewOrScope) {
                xgetSqlClause().makeOrScopeQueryEffective();
            }
            try {
                for (int i = 0; i < strArray.length; i++) {
                    final String currentValue = strArray[i];
                    if (i == 0) {
                        setupConditionValueAndRegisterWhereClause(key, currentValue, cvalue, columnDbName, option);
                    } else {
                        invokeQueryLikeSearch(columnDbName, currentValue, option);
                    }
                }
            } finally {
                if (needsNewOrScope) {
                    xgetSqlClause().closeOrScopeQuery();
                }
            }
        }
    }

    protected boolean xsuppressEscape() { // for override
        return false; // as default
    }

    protected void throwLikeSearchOptionNotFoundException(String columnDbName, String value) {
        final DBMeta dbmeta = xgetDBMetaProvider().provideDBMeta(getTableDbName());
        createCBExThrower().throwLikeSearchOptionNotFoundException(columnDbName, value, dbmeta);
    }

    protected void invokeQueryLikeSearch(String columnFlexibleName, Object value, LikeSearchOption option) {
        invokeQuery(columnFlexibleName, "likeSearch", value, option);
    }

    // -----------------------------------------------------
    //                                          Inline Query
    //                                          ------------
    protected void regIQ(ConditionKey key, Object value, ConditionValue cvalue, String columnDbName) {
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        final DBMeta dbmeta = xgetDBMetaProvider().provideDBMetaChecked(getTableDbName());
        final ColumnInfo columnInfo = dbmeta.findColumnInfo(columnDbName);
        final String propertyName = columnInfo.getPropertyName();
        final String uncapPropName = initUncap(propertyName);
        // If Java, it is necessary to use uncapPropName!
        key.setupConditionValue(xcreateQueryModeProvider(), cvalue, value, xgetLocation(uncapPropName));
        final ColumnSqlName columnSqlName = columnInfo.getColumnSqlName();
        final ColumnFunctionCipher cipher = xgetSqlClause().findColumnFunctionCipher(columnInfo);
        if (isBaseQuery()) {
            xgetSqlClause().registerBaseTableInlineWhereClause(columnSqlName, key, cvalue, cipher);
        } else {
            final String aliasName = xgetAliasName();
            xgetSqlClause()
                    .registerOuterJoinInlineWhereClause(aliasName, columnSqlName, key, cvalue, cipher, _onClause);
        }
    }

    protected void regIQ(final ConditionKey key, final Object value, final ConditionValue cvalue,
            final String columnDbName, final ConditionOption option) {
        if (!isValidQueryChecked(key, value, cvalue, columnDbName)) {
            return;
        }
        final DBMeta dbmeta = xgetDBMetaProvider().provideDBMetaChecked(getTableDbName());
        final ColumnInfo columnInfo = dbmeta.findColumnInfo(columnDbName);
        final String propertyName = columnInfo.getPropertyName();
        final String uncapPropName = initUncap(propertyName);
        // If Java, it is necessary to use uncapPropName!
        final String location = xgetLocation(uncapPropName);
        key.setupConditionValue(xcreateQueryModeProvider(), cvalue, value, location, option);
        final ColumnSqlName columnSqlName = columnInfo.getColumnSqlName();
        final ColumnFunctionCipher cipher = xgetSqlClause().findColumnFunctionCipher(columnInfo);
        if (isBaseQuery()) {
            xgetSqlClause().registerBaseTableInlineWhereClause(columnSqlName, key, cvalue, cipher, option);
        } else {
            final String aliasName = xgetAliasName();
            xgetSqlClause().registerOuterJoinInlineWhereClause(aliasName, columnSqlName, key, cvalue, cipher, option,
                    _onClause);
        }
    }

    // -----------------------------------------------------
    //                                        ExistsReferrer
    //                                        --------------
    protected void registerExistsReferrer(ConditionQuery subQuery, String columnDbName, String relatedColumnDbName,
            String propertyName) {
        registerExistsReferrer(subQuery, columnDbName, relatedColumnDbName, propertyName, false);
    }

    protected void registerNotExistsReferrer(ConditionQuery subQuery, String columnDbName, String relatedColumnDbName,
            String propertyName) {
        registerExistsReferrer(subQuery, columnDbName, relatedColumnDbName, propertyName, true);
    }

    protected void registerExistsReferrer(final ConditionQuery subQuery, String columnDbName,
            String relatedColumnDbName, String propertyName, boolean notExists) {
        assertSubQueryNotNull("ExistsReferrer", relatedColumnDbName, subQuery);
        final SubQueryPath subQueryPath = new SubQueryPath(xgetLocation(propertyName));
        final GeneralColumnRealNameProvider localRealNameProvider = new GeneralColumnRealNameProvider();
        final int subQueryLevel = subQuery.xgetSqlClause().getSubQueryLevel();
        final SqlClause subQueryClause = subQuery.xgetSqlClause();
        final String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        final ColumnSqlNameProvider subQuerySqlNameProvider = new ColumnSqlNameProvider() {
            public ColumnSqlName provide(String columnDbName) {
                return subQuery.toColumnSqlName(columnDbName);
            }
        };
        final DBMeta subQueryDBMeta = findDBMeta(subQuery.getTableDbName());
        final GearedCipherManager cipherManager = xgetSqlClause().getGearedCipherManager();
        final ExistsReferrer existsReferrer = new ExistsReferrer(subQueryPath, localRealNameProvider,
                subQuerySqlNameProvider, subQueryLevel, subQueryClause, subQueryIdentity, subQueryDBMeta, cipherManager);
        final String existsOption = notExists ? "not" : null;
        final String clause = existsReferrer.buildExistsReferrer(columnDbName, relatedColumnDbName, existsOption);

        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
        // Exists -> possible to be inner
        // NotExists -> no way to be inner
        //
        // for example, the following SQL is no way to be inner
        // (suppose if PURCHASE refers WITHDRAWAL)
        // 
        // select mb.MEMBER_ID, mb.MEMBER_NAME
        //      , mb.MEMBER_STATUS_CODE, wd.MEMBER_ID as WD_MEMBER_ID
        //   from MEMBER mb
        //     left outer join MEMBER_WITHDRAWAL wd on mb.MEMBER_ID = wd.MEMBER_ID
        //  where not exists (select pc.PURCHASE_ID
        //                      from PURCHASE pc
        //                     where pc.MEMBER_ID = wd.MEMBER_ID
        //        )
        //  order by mb.MEMBER_ID
        // = = = = = = = = = =/
        final boolean noWayInner = notExists; // but 'exists' allowed
        registerWhereClause(clause, noWayInner);
    }

    // *unsupported ExistsReferrer as in-line because it's so dangerous

    // -----------------------------------------------------
    //                                       InScopeRelation
    //                                       ---------------
    // {Modified at DBFlute-0.7.5}
    protected void registerInScopeRelation(ConditionQuery subQuery, String columnDbName, String relatedColumnDbName,
            String propertyName) {
        registerInScopeRelation(subQuery, columnDbName, relatedColumnDbName, propertyName, null);
    }

    protected void registerNotInScopeRelation(ConditionQuery subQuery, String columnDbName, String relatedColumnDbName,
            String propertyName) {
        registerInScopeRelation(subQuery, columnDbName, relatedColumnDbName, propertyName, "not");
    }

    protected void registerInScopeRelation(final ConditionQuery subQuery, String columnDbName,
            String relatedColumnDbName, String propertyName, String inScopeOption) {
        assertSubQueryNotNull("InScopeRelation", columnDbName, subQuery);
        final SubQueryPath subQueryPath = new SubQueryPath(xgetLocation(propertyName));
        final GeneralColumnRealNameProvider localRealNameProvider = new GeneralColumnRealNameProvider();
        final int subQueryLevel = subQuery.xgetSqlClause().getSubQueryLevel();
        final SqlClause subQueryClause = subQuery.xgetSqlClause();
        final String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        final ColumnSqlNameProvider subQuerySqlNameProvider = new ColumnSqlNameProvider() {
            public ColumnSqlName provide(String columnDbName) {
                return subQuery.toColumnSqlName(columnDbName);
            }
        };
        final DBMeta subQueryDBMeta = findDBMeta(subQuery.getTableDbName());
        final GearedCipherManager cipherManager = xgetSqlClause().getGearedCipherManager();
        final boolean suppressLocalAliasName = isInScopeRelationSuppressLocalAliasName();
        final InScopeRelation inScopeRelation = new InScopeRelation(subQueryPath, localRealNameProvider,
                subQuerySqlNameProvider, subQueryLevel, subQueryClause, subQueryIdentity, subQueryDBMeta,
                cipherManager, suppressLocalAliasName);
        final String clause = inScopeRelation.buildInScopeRelation(columnDbName, relatedColumnDbName, inScopeOption);
        registerWhereClause(clause);
    }

    protected boolean isInScopeRelationSuppressLocalAliasName() {
        // no alias name at InlineView
        return false; // as default
    }

    // [DBFlute-0.7.4]
    // -----------------------------------------------------
    //                              (Specify)DerivedReferrer
    //                              ------------------------
    protected void registerSpecifyDerivedReferrer(String function, final ConditionQuery subQuery, String columnDbName,
            String relatedColumnDbName, String propertyName, String aliasName, DerivedReferrerOption option) {
        assertFunctionNotNull("SpecifyDerivedReferrer", columnDbName, function);
        assertSubQueryNotNull("SpecifyDerivedReferrer", columnDbName, subQuery);
        if (option == null) {
            option = new DerivedReferrerOption(); // as default
        }
        final SubQueryPath subQueryPath = new SubQueryPath(xgetLocation(propertyName));
        final GeneralColumnRealNameProvider localRealNameProvider = new GeneralColumnRealNameProvider();
        final int subQueryLevel = subQuery.xgetSqlClause().getSubQueryLevel();
        final SqlClause subQueryClause = subQuery.xgetSqlClause();
        final String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        final ColumnSqlNameProvider subQuerySqlNameProvider = new ColumnSqlNameProvider() {
            public ColumnSqlName provide(String columnDbName) {
                return subQuery.toColumnSqlName(columnDbName);
            }
        };
        final DBMeta subQueryDBMeta = findDBMeta(subQuery.getTableDbName());
        final GearedCipherManager cipherManager = xgetSqlClause().getGearedCipherManager();
        final String mainSubQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
        final SpecifyDerivedReferrer derivedReferrer = option.createSpecifyDerivedReferrer(subQueryPath,
                localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQueryClause, subQueryIdentity,
                subQueryDBMeta, cipherManager, mainSubQueryIdentity, aliasName);
        xregisterParameterOption(option);
        final String clause = derivedReferrer.buildDerivedReferrer(function, columnDbName, relatedColumnDbName, option);
        final HpDerivingSubQueryInfo subQueryInfo = new HpDerivingSubQueryInfo(aliasName, clause, derivedReferrer);
        xgetSqlClause().specifyDerivingSubQuery(subQueryInfo);
    }

    // [DBFlute-0.8.8.1]
    // -----------------------------------------------------
    //                                (Query)DerivedReferrer
    //                                ----------------------
    protected void registerQueryDerivedReferrer(String function, final ConditionQuery subQuery, String columnDbName,
            String relatedColumnDbName, String propertyName, String operand, Object value,
            String parameterPropertyName, DerivedReferrerOption option) {
        assertFunctionNotNull("QueryDerivedReferrer", columnDbName, function);
        assertSubQueryNotNull("QueryDerivedReferrer", columnDbName, subQuery);
        if (option == null) {
            option = new DerivedReferrerOption(); // as default
        }
        final SubQueryPath subQueryPath = new SubQueryPath(xgetLocation(propertyName));
        final GeneralColumnRealNameProvider localRealNameProvider = new GeneralColumnRealNameProvider();
        final int subQueryLevel = subQuery.xgetSqlClause().getSubQueryLevel();
        final SqlClause subQueryClause = subQuery.xgetSqlClause();
        final String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        final ColumnSqlNameProvider subQuerySqlNameProvider = new ColumnSqlNameProvider() {
            public ColumnSqlName provide(String columnDbName) {
                return subQuery.toColumnSqlName(columnDbName);
            }
        };
        final DBMeta subQueryDBMeta = findDBMeta(subQuery.getTableDbName());
        final GearedCipherManager cipherManager = xgetSqlClause().getGearedCipherManager();
        final String mainSubQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
        final String parameterPath = xgetLocation(parameterPropertyName);
        final QueryDerivedReferrer derivedReferrer = option.createQueryDerivedReferrer(subQueryPath,
                localRealNameProvider, subQuerySqlNameProvider, subQueryLevel, subQueryClause, subQueryIdentity,
                subQueryDBMeta, cipherManager, mainSubQueryIdentity, operand, value, parameterPath);
        xregisterParameterOption(option);
        final String clause = derivedReferrer.buildDerivedReferrer(function, columnDbName, relatedColumnDbName, option);

        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
        // is null or null-revived conversion (coalesce) -> no way to be inner
        // 
        // for example, the following SQL is no way to be inner
        // (suppose if PURCHASE refers WITHDRAWAL)
        // 
        // select mb.MEMBER_ID, mb.MEMBER_NAME
        //      , mb.MEMBER_STATUS_CODE, wd.MEMBER_ID as WD_MEMBER_ID
        //   from MEMBER mb
        //     left outer join MEMBER_WITHDRAWAL wd on mb.MEMBER_ID = wd.MEMBER_ID
        //  where (select max(pc.PURCHASE_PRICE)
        //           from PURCHASE pc
        //          where pc.MEMBER_ID = wd.MEMBER_ID -- may null
        //        ) is null
        //  order by mb.MEMBER_ID
        // 
        // and using coalesce means it may select records that have null value
        // so using coalesce is no way in spite of operand
        // = = = = = = = = = =/
        final boolean noWayInner = HpQDRParameter.isOperandIsNull(operand) || option.mayNullRevived();
        registerWhereClause(clause, noWayInner);
    }

    // [DBFlute-0.8.8]
    // -----------------------------------------------------
    //                                       ScalarCondition
    //                                       ---------------
    protected void registerScalarCondition(String function, final ConditionQuery subQuery, String propertyName,
            String operand) {
        assertSubQueryNotNull("ScalarCondition", propertyName, subQuery);
        final SubQueryPath subQueryPath = new SubQueryPath(xgetLocation(propertyName));
        final GeneralColumnRealNameProvider localRealNameProvider = new GeneralColumnRealNameProvider();
        final int subQueryLevel = subQuery.xgetSqlClause().getSubQueryLevel();
        final SqlClause subQueryClause = subQuery.xgetSqlClause();
        final String subQueryIdentity = propertyName + "[" + subQueryLevel + "]";
        final ColumnSqlNameProvider subQuerySqlNameProvider = new ColumnSqlNameProvider() {
            public ColumnSqlName provide(String columnDbName) {
                return subQuery.toColumnSqlName(columnDbName);
            }
        };
        final DBMeta subQueryDBMeta = findDBMeta(subQuery.getTableDbName());
        final GearedCipherManager cipherManager = xgetSqlClause().getGearedCipherManager();
        final String mainSubQueryIdentity = propertyName + "[" + subQueryLevel + ":subquerymain]";
        final ScalarCondition scalarCondition = new ScalarCondition(subQueryPath, localRealNameProvider,
                subQuerySqlNameProvider, subQueryLevel, subQueryClause, subQueryIdentity, subQueryDBMeta,
                cipherManager, mainSubQueryIdentity, operand);
        final String clause = scalarCondition.buildScalarCondition(function);
        registerWhereClause(clause);
    }

    // -----------------------------------------------------
    //                                         MyselfInScope
    //                                         -------------
    protected void registerMyselfInScope(ConditionQuery subQuery, String subQueryPropertyName) {
        final String relatedColumnDbName;
        {
            final String specifiedDbName = subQuery.xgetSqlClause().getSpecifiedColumnDbNameAsOne();
            if (specifiedDbName != null) {
                relatedColumnDbName = specifiedDbName;
            } else { // as default
                // this function is only allowed when only-one PK
                final UniqueInfo primaryUniqueInfo = findDBMeta(subQuery.getTableDbName()).getPrimaryUniqueInfo();
                final ColumnInfo primaryColumnInfo = primaryUniqueInfo.getFirstColumn();
                relatedColumnDbName = primaryColumnInfo.getColumnDbName();
            }
        }
        registerInScopeRelation(subQuery, relatedColumnDbName, relatedColumnDbName, subQueryPropertyName);
    }

    // -----------------------------------------------------
    //                                       SubQuery Common
    //                                       ---------------
    protected class GeneralColumnRealNameProvider implements ColumnRealNameProvider {
        public ColumnRealName provide(String columnDbName) {
            return toColumnRealName(columnDbName);
        }
    }

    // these assertions are basically for internal
    protected void assertSubQueryNotNull(String title, String columnDbName, ConditionQuery subQuery) {
        if (subQuery == null) {
            String msg = "The condition-query for the sub-query should not be null:";
            msg = msg + " " + title + "(" + columnDbName + ")";
            throw new IllegalStateException(msg);
        }
    }

    protected void assertFunctionNotNull(String title, String columnDbName, String function) {
        if (function == null) {
            String msg = "The function for the sub-query should not be null:";
            msg = msg + " " + title + "(" + columnDbName + ")";
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                          Where Clause
    //                                          ------------
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey key, Object value, ConditionValue cvalue,
            String columnDbName) {
        setupConditionValueAndRegisterWhereClause(key, value, cvalue, columnDbName, null);
    }

    protected void setupConditionValueAndRegisterWhereClause(final ConditionKey key, final Object value,
            final ConditionValue cvalue, final String columnDbName, final ConditionOption option) {
        final DBMeta dbmeta = findDBMeta(getTableDbName());
        final ColumnInfo columnInfo = dbmeta.findColumnInfo(columnDbName);
        final String propertyName = columnInfo.getPropertyName();
        final String uncapPropName = initUncap(propertyName);
        // if Java, it is necessary to use uncapPropName
        final String location = xgetLocation(uncapPropName);
        key.setupConditionValue(xcreateQueryModeProvider(), cvalue, value, location, option);
        final ColumnRealName columnRealName = toColumnRealName(columnDbName);
        final ColumnFunctionCipher cipher = xgetSqlClause().findColumnFunctionCipher(columnInfo);
        final String usedAliasName = xgetAliasName();
        xgetSqlClause().registerWhereClause(columnRealName, key, cvalue, cipher, option, usedAliasName);
    }

    protected void registerWhereClause(String whereClause) {
        registerWhereClause(whereClause, false);
    }

    protected void registerWhereClause(String whereClause, boolean noWayInner) {
        final String usedAliasName = xgetAliasName();
        xgetSqlClause().registerWhereClause(whereClause, usedAliasName, noWayInner);
    }

    protected void registerInlineWhereClause(String whereClause) {
        if (isBaseQuery()) {
            xgetSqlClause().registerBaseTableInlineWhereClause(whereClause);
        } else {
            xgetSqlClause().registerOuterJoinInlineWhereClause(xgetAliasName(), whereClause, _onClause);
        }
    }

    // -----------------------------------------------------
    //                                           Union Query
    //                                           -----------
    public void registerUnionQuery(ConditionQuery unionQuery, boolean unionAll, String unionQueryPropertyName) {
        final String unionQueryClause = xgetUnionQuerySql(unionQuery, unionQueryPropertyName);

        // At the future, building SQL will be moved to sqlClause.
        xgetSqlClause().registerUnionQuery(unionQueryClause, unionAll);
    }

    protected String xgetUnionQuerySql(ConditionQuery unionQuery, String unionQueryPropertyName) {
        final String fromClause = unionQuery.xgetSqlClause().getFromClause();
        final String whereClause = unionQuery.xgetSqlClause().getWhereClause();
        final String unionQueryClause;
        if (whereClause.trim().length() <= 0) {
            unionQueryClause = fromClause + " " + xgetSqlClause().getUnionWhereClauseMark();
        } else {
            final int whereIndex = whereClause.indexOf("where ");
            if (whereIndex < 0) {
                String msg = "The whereClause should have 'where' string: " + whereClause;
                throw new IllegalStateException(msg);
            }
            final int clauseIndex = whereIndex + "where ".length();
            final String mark = xgetSqlClause().getUnionWhereFirstConditionMark();
            final String markedClause = whereClause.substring(0, clauseIndex) + mark
                    + whereClause.substring(clauseIndex);
            unionQueryClause = fromClause + " " + markedClause;
        }
        final String oldStr = "/*pmb.conditionQuery.";
        final String newStr = "/*pmb.conditionQuery." + unionQueryPropertyName + ".";
        return replaceString(unionQueryClause, oldStr, newStr);
    }

    // -----------------------------------------------------
    //                                            Inner Join
    //                                            ----------
    /**
     * Change the join type for this relation to inner join. <br />
     * This method is for PERFORMANCE TUNING basically.
     */
    public void innerJoin() {
        if (isBaseQuery()) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The method 'innerJoin()' should be called for a relation query.");
            br.addItem("Advice");
            br.addElement("Please confirm your program.");
            br.addElement("For example:");
            br.addElement("  (x) - cb.query().innerJoin();");
            br.addElement("  (o) - cb.query().queryMemberStatus().innerJoin();");
            br.addItem("Base Table");
            br.addElement(getTableDbName());
            final String msg = br.buildExceptionMessage();
            throw new IllegalConditionBeanOperationException(msg);
        }
        xgetSqlClause().changeToInnerJoin(xgetAliasName());
    }

    // -----------------------------------------------------
    //                                               OrderBy
    //                                               -------
    protected void registerOrderBy(String columnDbName, boolean ascOrDesc) {
        xgetSqlClause().registerOrderBy(toColumnRealName(columnDbName).toString(), ascOrDesc);
    }

    protected void regOBA(String columnName) {
        assertOrderByPurpose(columnName);
        registerOrderBy(columnName, true);
    }

    protected void regOBD(String columnName) {
        assertOrderByPurpose(columnName);
        registerOrderBy(columnName, false);
    }

    protected void assertOrderByPurpose(String columnName) {
        if (xgetSqlClause().getPurpose().isNoOrderBy()) {
            throwOrderByIllegalPurposeException(columnName);
        }
    }

    protected void throwOrderByIllegalPurposeException(String columnName) {
        createCBExThrower().throwOrderByIllegalPurposeException(xgetSqlClause().getPurpose(), getTableDbName(),
                columnName);
    }

    /**
     * Order with the keyword 'nulls first'.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #FD4747">withNullsFirst()</span>;
     * <span style="color: #3F7E5E">// order by BIRTHDATE asc nulls first</span>
     * </pre>
     */
    public void withNullsFirst() { // is user public!
        xgetSqlClause().addNullsFirstToPreviousOrderBy();
    }

    /**
     * Order with the keyword 'nulls last'.
     * <pre>
     * MemberCB cb = new MemberCB();
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #FD4747">withNullsLast()</span>;
     * <span style="color: #3F7E5E">// order by BIRTHDATE asc nulls last</span>
     * </pre>
     */
    public void withNullsLast() { // is user public!
        xgetSqlClause().addNullsLastToPreviousOrderBy();
    }

    /**
     * Order along the list of manual values. <br />
     * This function with Union is unsupported! <br />
     * The order values are bound (treated as bind parameter).
     * <pre>
     * MemberCB cb = new MemberCB();
     * List&lt;String&gt; statusCodeList = Arrays.asList("WDL", "FML", "PRV");
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #FD4747">withManualOrder(statusCodeList)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * @param orderValueList The list of order values for manual ordering. (NotNull)
     */
    public void withManualOrder(List<? extends Object> orderValueList) { // is user public!
        assertObjectNotNull("withManualOrder(orderValueList)", orderValueList);
        final ManualOrderBean manualOrderBean = new ManualOrderBean();
        manualOrderBean.acceptOrderValueList(orderValueList);
        withManualOrder(manualOrderBean);
    }

    /**
     * Order along manual ordering information. <br />
     * This function with Union is unsupported! <br />
     * The order values are bound (treated as bind parameter).
     * <pre>
     * MemberCB cb = new MemberCB();
     * ManualOrderBean mob = new ManualOrderBean();
     * mob.when_GreaterEqual(priorityDate); <span style="color: #3F7E5E">// 2000/01/01</span>
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #FD4747">withManualOrder(mob)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when BIRTHDATE &gt;= '2000/01/01' then 0</span>
     * <span style="color: #3F7E5E">//     else 1</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * @param manualOrderBean The bean of manual order containing order values. (NotNull)
     */
    public void withManualOrder(ManualOrderBean manualOrderBean) { // is user public!
        assertObjectNotNull("withManualOrder(manualOrderBean)", manualOrderBean);
        manualOrderBean.bind(new FreeParameterManualOrderThemeListHandler() {
            public String register(String themeKey, Object orderValue) {
                return xregisterManualOrderParameterToThemeList(themeKey, orderValue);
            }
        });
        xgetSqlClause().addManualOrderToPreviousOrderByElement(manualOrderBean);
    }

    protected void registerSpecifiedDerivedOrderBy_Asc(String aliasName) {
        if (!xgetSqlClause().hasSpecifiedDerivingSubQuery(aliasName)) {
            throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
        }
        xgetSqlClause().registerOrderBy(aliasName, true);
    }

    protected void registerSpecifiedDerivedOrderBy_Desc(String aliasName) {
        if (!xgetSqlClause().hasSpecifiedDerivingSubQuery(aliasName)) {
            throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
        }
        xgetSqlClause().registerOrderBy(aliasName, false);
    }

    protected void throwSpecifiedDerivedOrderByAliasNameNotFoundException(String aliasName) {
        createCBExThrower().throwSpecifiedDerivedOrderByAliasNameNotFoundException(aliasName);
    }

    // ===================================================================================
    //                                                                       Name Resolver
    //                                                                       =============
    /**
     * Resolve alias name for join table.
     * @param relationPath Relation path. (NotNull)
     * @param nestLevel The nest No of condition query.
     * @return The resolved name. (NotNull)
     */
    protected String resolveJoinAliasName(String relationPath, int nestLevel) {
        return xgetSqlClause().resolveJoinAliasName(relationPath, nestLevel);
    }

    /**
     * Resolve relation no.
     * @param localTableName The name of local table. (NotNull)
     * @param foreignPropertyName The property name of foreign relation. (NotNull)
     * @return The resolved relation No.
     */
    protected String resolveNextRelationPath(String localTableName, String foreignPropertyName) {
        final int relationNo = xgetSqlClause().resolveRelationNo(localTableName, foreignPropertyName);
        String nextRelationPath = "_" + relationNo;
        if (_relationPath != null) {
            nextRelationPath = _relationPath + nextRelationPath;
        }
        return nextRelationPath;
    }

    // ===================================================================================
    //                                                                 Reflection Invoking
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    public ConditionValue invokeValue(String columnFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        final DBMeta dbmeta = findDBMeta(getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(columnFlexibleName));
        final String methodName = "get" + columnCapPropName;
        final Method method = helpGettingCQMethod(this, methodName, new Class<?>[] {});
        if (method == null) {
            throwConditionInvokingGetMethodNotFoundException(columnFlexibleName, methodName);
            return null; // unreachable
        }
        try {
            return (ConditionValue) helpInvokingCQMethod(this, method, new Object[] {});
        } catch (ReflectionFailureException e) {
            throwConditionInvokingGetReflectionFailureException(columnFlexibleName, methodName, e);
            return null; // unreachable
        }
    }

    protected void throwConditionInvokingGetMethodNotFoundException(String columnFlexibleName, String methodName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the method for getting the condition.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg);
    }

    protected void throwConditionInvokingGetReflectionFailureException(String columnFlexibleName, String methodName,
            ReflectionFailureException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to invoke the method for getting value.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg, e);
    }

    /**
     * {@inheritDoc}
     */
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue) {
        doInvokeQuery(columnFlexibleName, conditionKeyName, conditionValue, null);
    }

    /**
     * {@inheritDoc}
     */
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue,
            ConditionOption conditionOption) {
        assertObjectNotNull("conditionOption", conditionOption);
        doInvokeQuery(columnFlexibleName, conditionKeyName, conditionValue, conditionOption);
    }

    protected void doInvokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue,
            ConditionOption conditionOption) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        assertStringNotNullAndNotTrimmedEmpty("conditionKeyName", conditionKeyName);
        if (conditionValue == null) {
            return;
        }
        final PropertyNameCQContainer container = helpExtractingPropertyNameCQContainer(columnFlexibleName);
        final String flexibleName = container.getFlexibleName();
        final ConditionQuery cq = container.getConditionQuery();
        final DBMeta dbmeta = findDBMeta(cq.getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(flexibleName));
        final String methodName = "set" + columnCapPropName + "_" + initCap(conditionKeyName);
        final Class<?> type = conditionValue.getClass();
        final Class<?>[] parameterTypes;
        if (conditionOption != null) {
            parameterTypes = new Class<?>[] { type, conditionOption.getClass() };
        } else {
            parameterTypes = new Class<?>[] { type };
        }
        final Method method = helpGettingCQMethod(cq, methodName, parameterTypes);
        if (method == null) {
            throwConditionInvokingSetMethodNotFoundException(columnFlexibleName, conditionKeyName, conditionValue,
                    conditionOption, methodName);
        }
        try {
            final Object[] args;
            if (conditionOption != null) {
                args = new Object[] { conditionValue, conditionOption };
            } else {
                args = new Object[] { conditionValue };
            }
            helpInvokingCQMethod(cq, method, args);
        } catch (ReflectionFailureException e) {
            throwConditionInvokingSetReflectionFailureException(columnFlexibleName, conditionKeyName, conditionValue,
                    conditionOption, methodName, e);
        }
    }

    protected void throwConditionInvokingSetMethodNotFoundException(String columnFlexibleName, String conditionKeyName,
            Object conditionValue, ConditionOption conditionOption, String methodName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the method for setting the condition.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("conditionKeyName");
        br.addElement(conditionKeyName);
        br.addItem("conditionValue");
        br.addElement(conditionValue);
        br.addItem("conditionOption");
        br.addElement(conditionOption);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg);
    }

    protected void throwConditionInvokingSetReflectionFailureException(String columnFlexibleName,
            String conditionKeyName, Object conditionValue, ConditionOption conditionOption, String methodName,
            ReflectionFailureException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to invoke the method for setting the condition.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("conditionKeyName");
        br.addElement(conditionKeyName);
        br.addItem("conditionValue");
        br.addElement(conditionValue);
        br.addItem("conditionOption");
        br.addElement(conditionOption);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg, e);
    }

    /**
     * {@inheritDoc}
     */
    public void invokeQueryEqual(String columnFlexibleName, Object value) {
        invokeQuery(columnFlexibleName, CK_EQ.getConditionKey(), value);
    }

    /**
     * {@inheritDoc}
     */
    public void invokeOrderBy(String columnFlexibleName, boolean isAsc) {
        assertStringNotNullAndNotTrimmedEmpty("columnFlexibleName", columnFlexibleName);
        final PropertyNameCQContainer container = helpExtractingPropertyNameCQContainer(columnFlexibleName);
        final String flexibleName = container.getFlexibleName();
        final ConditionQuery cq = container.getConditionQuery();
        final String ascDesc = isAsc ? "Asc" : "Desc";
        final DBMeta dbmeta = findDBMeta(cq.getTableDbName());
        final String columnCapPropName = initCap(dbmeta.findPropertyName(flexibleName));
        final String methodName = "addOrderBy_" + columnCapPropName + "_" + ascDesc;
        final Method method = helpGettingCQMethod(cq, methodName, new Class<?>[] {});
        if (method == null) {
            throwConditionInvokingOrderMethodNotFoundException(columnFlexibleName, isAsc, methodName);
        }
        helpInvokingCQMethod(cq, method, new Object[] {});
        try {
            helpInvokingCQMethod(cq, method, new Object[] {});
        } catch (ReflectionFailureException e) {
            throwConditionInvokingOrderReflectionFailureException(columnFlexibleName, isAsc, methodName, e);
        }
    }

    protected void throwConditionInvokingOrderMethodNotFoundException(String columnFlexibleName, boolean isAsc,
            String methodName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the method for adding the order-by condition.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("isAsc");
        br.addElement(isAsc);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg);
    }

    protected void throwConditionInvokingOrderReflectionFailureException(String columnFlexibleName, boolean isAsc,
            String methodName, ReflectionFailureException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to invoke the method for setting the order-by condition.");
        br.addItem("columnFlexibleName");
        br.addElement(columnFlexibleName);
        br.addItem("isAsc");
        br.addElement(isAsc);
        br.addItem("methodName");
        br.addElement(methodName);
        final String msg = br.buildExceptionMessage();
        throw new ConditionInvokingFailureException(msg, e);
    }

    /**
     * {@inheritDoc}
     */
    public ConditionQuery invokeForeignCQ(String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final List<String> splitList = Srl.splitList(foreignPropertyName, ".");
        ConditionQuery foreignCQ = this;
        for (String elementName : splitList) {
            foreignCQ = doInvokeForeignCQ(foreignCQ, elementName);
        }
        return foreignCQ;
    }

    protected ConditionQuery doInvokeForeignCQ(ConditionQuery cq, String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final String methodName = "query" + initCap(foreignPropertyName);
        final Method method = helpGettingCQMethod(cq, methodName, new Class<?>[] {});
        if (method == null) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Not found the method for getting a foreign condition query.");
            br.addItem("foreignPropertyName");
            br.addElement(foreignPropertyName);
            br.addItem("methodName");
            br.addElement(methodName);
            br.addItem("ConditionQuery");
            br.addElement(DfTypeUtil.toClassTitle(cq));
            final String msg = br.buildExceptionMessage();
            throw new ConditionInvokingFailureException(msg);
        }
        try {
            return (ConditionQuery) helpInvokingCQMethod(cq, method, new Object[] {});
        } catch (ReflectionFailureException e) {
            String msg = "Failed to invoke the method for setting a condition(query):";
            msg = msg + " foreignPropertyName=" + foreignPropertyName;
            msg = msg + " methodName=" + methodName + " table=" + getTableDbName();
            throw new ConditionInvokingFailureException(msg, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean invokeHasForeignCQ(String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final List<String> splitList = Srl.splitList(foreignPropertyName, ".");
        ConditionQuery foreignCQ = this;
        final int splitLength = splitList.size();
        int index = 0;
        for (String elementName : splitList) {
            if (!doInvokeHasForeignCQ(foreignCQ, elementName)) {
                return false;
            }
            if ((index + 1) < splitLength) { // last loop
                foreignCQ = foreignCQ.invokeForeignCQ(elementName);
            }
            ++index;
        }
        return true;
    }

    protected boolean doInvokeHasForeignCQ(ConditionQuery cq, String foreignPropertyName) {
        assertStringNotNullAndNotTrimmedEmpty("foreignPropertyName", foreignPropertyName);
        final String methodName = "hasConditionQuery" + initCap(foreignPropertyName);
        final Method method = helpGettingCQMethod(cq, methodName, new Class<?>[] {});
        if (method == null) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Not found the method for determining a foreign condition query.");
            br.addItem("foreignPropertyName");
            br.addElement(foreignPropertyName);
            br.addItem("methodName");
            br.addElement(methodName);
            br.addItem("ConditionQuery");
            br.addElement(DfTypeUtil.toClassTitle(cq));
            final String msg = br.buildExceptionMessage();
            throw new ConditionInvokingFailureException(msg);
        }
        try {
            return (Boolean) helpInvokingCQMethod(cq, method, new Object[] {});
        } catch (ReflectionFailureException e) {
            String msg = "Failed to invoke the method for determining a condition(query):";
            msg = msg + " foreignPropertyName=" + foreignPropertyName;
            msg = msg + " methodName=" + methodName + " table=" + getTableDbName();
            throw new ConditionInvokingFailureException(msg, e);
        }
    }

    private PropertyNameCQContainer helpExtractingPropertyNameCQContainer(String name) {
        final String[] strings = name.split("\\.");
        final int length = strings.length;
        String propertyName = null;
        ConditionQuery cq = this;
        int index = 0;
        for (String element : strings) {
            if (length == (index + 1)) { // at last loop!
                propertyName = element;
                break;
            }
            cq = cq.invokeForeignCQ(element);
            ++index;
        }
        return new PropertyNameCQContainer(propertyName, cq);
    }

    private static class PropertyNameCQContainer {
        protected String _flexibleName;
        protected ConditionQuery _cq;

        public PropertyNameCQContainer(String flexibleName, ConditionQuery cq) {
            this._flexibleName = flexibleName;
            this._cq = cq;
        }

        public String getFlexibleName() {
            return _flexibleName;
        }

        public ConditionQuery getConditionQuery() {
            return _cq;
        }
    }

    private Method helpGettingCQMethod(ConditionQuery cq, String methodName, Class<?>[] argTypes) {
        Class<? extends ConditionQuery> clazz = cq.getClass();
        Method method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, argTypes);
        if (method == null && argTypes != null) {
            if (argTypes.length == 1 && Collection.class.isAssignableFrom(argTypes[0])) {
                method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, new Class[] { Collection.class });
            } else if (argTypes.length == 2 && ConditionOption.class.isAssignableFrom(argTypes[1])) {
                Class<?> superType = argTypes[1].getSuperclass();
                method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, new Class[] { superType });
                if (method == null) { // only once more
                    superType = argTypes[1].getSuperclass();
                    method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, new Class[] { superType });
                }
            } else if (argTypes.length == 3 && ConditionOption.class.isAssignableFrom(argTypes[2])) {
                Class<?> superType = argTypes[2].getSuperclass();
                method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, new Class[] { superType });
                if (method == null) { // only once more
                    superType = argTypes[2].getSuperclass();
                    method = DfReflectionUtil.getAccessibleMethod(clazz, methodName, new Class[] { superType });
                }
            }
        }
        return method;
    }

    private Object helpInvokingCQMethod(ConditionQuery cq, Method method, Object[] args) {
        return DfReflectionUtil.invokeForcedly(method, cq, args);
    }

    // ===================================================================================
    //                                                                     Condition Value
    //                                                                     ===============
    protected ConditionValue nCV() {
        return newConditionValue();
    }

    protected ConditionValue newConditionValue() {
        return new ConditionValue();
    }

    // ===================================================================================
    //                                                                        Filter Value
    //                                                                        ============
    /**
     * Delegate to filterRemoveEmptyString(). {Internal}
     * @param value The string value for query. (NullAllowed)
     * @return Filtered value. (NullAllowed)
     */
    protected String fRES(String value) {
        return filterRemoveEmptyString(value);
    }

    /**
     * Filter removing an empty string as null. <br />
     * You can extend this to use an empty string value as condition.
     * @param value The string value for query. (NullAllowed)
     * @return Filtered value. (NullAllowed)
     */
    protected String filterRemoveEmptyString(String value) {
        if (isEmptyStringQueryAllowed()) {
            return value;
        }
        return ((value != null && !"".equals(value)) ? value : null);
    }

    /**
     * Does it allowed an empty string to set for query?
     * @return The determination, true or false.
     */
    protected boolean isEmptyStringQueryAllowed() {
        return xgetSqlClause().isEmptyStringQueryAllowed();
    }

    /**
     * Delegate to filterConvertToPureDate().
     * @param date The instance of date for query. (NullAllowed)
     * @return Filtered date. (NullAllowed)
     */
    protected java.util.Date fCTPD(java.util.Date date) {
        return filterConvertToPureDate(date);
    }

    /**
     * Filter converting the date to a pure date.
     * @param date The instance of date for query. (NullAllowed)
     * @return Filtered value. (NullAllowed)
     */
    protected java.util.Date filterConvertToPureDate(java.util.Date date) {
        return DfTypeUtil.toDate(date);
    }

    // ===================================================================================
    //                                                                       Create Option
    //                                                                       =============
    /**
     * create the option of like search as prefix search.
     * @return The option of like search as prefix search. (NotNull)
     */
    protected LikeSearchOption cLSOP() { // createLikeSearchOption
        return new LikeSearchOption().likePrefix();
    }

    // ===================================================================================
    //                                                                       Convert Value
    //                                                                       =============
    /**
     * @param obj The object of the property. (NullAllowed)
     * @param type The type instance of the property. (NullAllowed)
     * @param <PROPERTY> The type of property.
     * @return The number type result of the property. (NullAllowed: if null, returns null)
     */
    @SuppressWarnings("unchecked")
    protected <PROPERTY extends Number> PROPERTY cTNum(Object obj, Class<PROPERTY> type) { // convert to number
        return (PROPERTY) DfTypeUtil.toNumber(obj, type);
    }

    /**
     * @param col The collection of the property. (NullAllowed)
     * @param <PROPERTY> The type of property.
     * @return The list of the property. (NullAllowed: if null, returns null)
     */
    protected <PROPERTY> List<PROPERTY> cTL(Collection<PROPERTY> col) { // convert to list
        return convertToList(col);
    }

    protected List<String> cTStrL(Collection<? extends Classification> col) { // convert to string list
        if (col == null) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        for (Classification cls : col) {
            if (cls != null) {
                list.add(cls.code());
            }
        }
        return list;
    }

    protected <PROPERTY extends Number> List<PROPERTY> cTNumL(Collection<? extends Classification> col,
            Class<PROPERTY> type) { // convert to number list
        if (col == null) {
            return null;
        }
        final List<PROPERTY> list = new ArrayList<PROPERTY>();
        for (Classification cls : col) {
            if (cls != null) {
                @SuppressWarnings("unchecked")
                final PROPERTY value = (PROPERTY) DfTypeUtil.toNumber(cls.code(), type);
                list.add(value);
            }
        }
        return list;
    }

    /**
     * @param col The collection of property. (NullAllowed)
     * @param <PROPERTY> The type of property.
     * @return The list of the property. (NullAllowed: if null, returns null)
     */
    private <PROPERTY> List<PROPERTY> convertToList(Collection<PROPERTY> col) {
        if (col == null) {
            return null;
        }
        if (col instanceof List<?>) {
            return filterRemoveNullOrEmptyValueFromList((List<PROPERTY>) col);
        }
        return filterRemoveNullOrEmptyValueFromList(new ArrayList<PROPERTY>(col));
    }

    private <PROPERTY_TYPE> List<PROPERTY_TYPE> filterRemoveNullOrEmptyValueFromList(List<PROPERTY_TYPE> ls) {
        if (ls == null) {
            return null;
        }
        final List<PROPERTY_TYPE> newList = new ArrayList<PROPERTY_TYPE>();
        for (Iterator<PROPERTY_TYPE> ite = ls.iterator(); ite.hasNext();) {
            final PROPERTY_TYPE element = ite.next();
            if (element == null) {
                continue;
            }
            if (element instanceof String) {
                if (((String) element).length() == 0) {
                    continue;
                }
            }
            newList.add(element);
        }
        return newList;
    }

    // ===================================================================================
    //                                                                     Short Character
    //                                                                     ===============
    // handleShortChar()
    protected String hSC(String columnName, String value, Integer size, String modeCode) {
        final ShortCharHandlingMode mode = ShortCharHandlingMode.codeOf(modeCode);
        if (mode == null) {
            String msg = "The mode was not found by the code: ";
            msg = msg + " columnName=" + columnName + " modeCode=" + modeCode;
            throw new IllegalStateException(msg);
        }
        return ParameterUtil.handleShortChar(columnName, value, size, mode);
    }

    // ===================================================================================
    //                                                                    Full Text Search
    //                                                                    ================
    // -----------------------------------------------------
    //                                                 MySQL
    //                                                 -----
    protected void xdoMatchForMySQL(List<ColumnInfo> textColumnList, String conditionValue,
            WayOfMySQL.FullTextSearchModifier modifier) {
        if (conditionValue == null || conditionValue.length() == 0) {
            return; // ignored according to condition-bean rule
        }
        final String clause = ((SqlClauseMySql) xgetSqlClause()).buildMatchCondition(textColumnList, conditionValue,
                modifier, getTableDbName(), xgetAliasName());
        registerWhereClause(clause);
    }

    // -----------------------------------------------------
    //                                     PostgreSQL/Oracle
    //                                     -----------------
    protected void xdoMatchByLikeSearch(List<ColumnInfo> textColumnList, String conditionValue) {
        if (conditionValue == null || conditionValue.length() == 0) {
            return;
        }
        assertObjectNotNull("textColumnList", textColumnList);
        if (textColumnList.isEmpty()) {
            String msg = "The argument 'textColumnList' should not be empty list.";
            throw new IllegalArgumentException(msg);
        }
        conditionValue = xescapeFullTextSearchValue(conditionValue);
        int index = 0;
        xgetSqlClause().makeOrScopeQueryEffective();
        try {
            for (ColumnInfo columnInfo : textColumnList) {
                if (columnInfo == null) {
                    continue;
                }
                final String tableOfColumn = columnInfo.getDBMeta().getTableDbName();
                if (!tableOfColumn.equalsIgnoreCase(getTableDbName())) {
                    String msg = "The table of the text column should be '" + getTableDbName() + "'";
                    msg = msg + " but the table is '" + tableOfColumn + "': column=" + columnInfo;
                    throw new IllegalArgumentException(msg);
                }
                if (!columnInfo.isPropertyTypeString()) {
                    String msg = "The text column should be String type:";
                    msg = msg + " column=" + columnInfo;
                    throw new IllegalArgumentException(msg);
                }
                invokeQueryLikeSearch(columnInfo.getColumnDbName(), conditionValue, xcreateMatchLikeSearch());
                ++index;
            }
        } finally {
            xgetSqlClause().closeOrScopeQuery();
        }
    }

    protected String xescapeFullTextSearchValue(String conditionValue) {
        String msg = "You should override this method.";
        throw new UnsupportedOperationException(msg);
    }

    protected String xescapeOracleFullTextSearchValue(String conditionValue) {
        return ((SqlClauseOracle) xgetSqlClause()).escapeFullTextSearchValue(conditionValue);
    }

    protected LikeSearchOption xcreateMatchLikeSearch() {
        String msg = "You should override this method.";
        throw new UnsupportedOperationException(msg);
    }

    protected LikeSearchOption xcreatePostgreSQLMatchLikeSearch() {
        return new PostgreSQLMatchLikeSearch();
    }

    public class PostgreSQLMatchLikeSearch extends LikeSearchOption {
        private static final long serialVersionUID = 1L;

        @Override
        public ExtensionOperand getExtensionOperand() {
            return xgetPostgreSQLMatchOperand();
        }
    }

    protected ExtensionOperand xgetPostgreSQLMatchOperand() {
        String msg = "You should override this method.";
        throw new UnsupportedOperationException(msg);
    }

    protected LikeSearchOption xcreateOracleMatchLikeSearch() {
        return new OracleMatchLikeSearch();
    }

    public class OracleMatchLikeSearch extends LikeSearchOption {
        private static final long serialVersionUID = 1L;

        @Override
        public QueryClauseArranger getWhereClauseArranger() {
            return ((SqlClauseOracle) xgetSqlClause()).createFullTextSearchClauseArranger();
        }
    }

    // ===================================================================================
    //                                                                  ColumnQuery Object
    //                                                                  ==================
    /**
     * Get the condition-bean map of ColumnQuery for parameter comment. {Internal}. <br />
     * This is basically for (Specify)DerivedReferrer's bind conditions in ColumnQuery. <br />
     * The value is treated as Object type because this will be only called from parameter comment.
     * @return The instance of the map. (NullAllowed)
     */
    public Map<String, Object> getColQyCBMap() {
        return xgetSqlClause().getColumnQueryObjectMap();
    }

    protected String xregisterColumyQueryObjectToThemeList(String themeKey, Object addedValue) {
        return xgetSqlClause().registerColumnQueryObjectToThemeList(themeKey, addedValue);
    }

    // ===================================================================================
    //                                                               ManualOrder Parameter
    //                                                               =====================
    /**
     * Get the parameter map of ManualOrder for parameter comment. {Internal}.
     * @return The instance of the map. (NullAllowed)
     */
    public Map<String, Object> getMnuOdrPrmMap() {
        return xgetSqlClause().getManualOrderParameterMap();
    }

    protected String xregisterManualOrderParameterToThemeList(String themeKey, Object addedValue) {
        return xgetSqlClause().registerManualOrderParameterToThemeList(themeKey, addedValue);
    }

    // ===================================================================================
    //                                                                      Free Parameter
    //                                                                      ==============
    /**
     * Get the map of free parameter for parameter comment. {Internal}.
     * @return The instance of the map. (NullAllowed)
     */
    public Map<String, Object> getFreePrmMap() {
        return xgetSqlClause().getFreeParameterMap();
    }

    // 'public' modifier for versatility
    //  e.g. called by compound PK's LoadReferrer
    public String xregisterFreeParameterToThemeList(String themeKey, Object addedValue) {
        return xgetSqlClause().registerFreeParameterToThemeList(themeKey, addedValue);
    }

    // ===================================================================================
    //                                                                    Option Parameter
    //                                                                    ================
    public void xregisterParameterOption(ParameterOption option) {
        if (option == null) {
            return;
        }
        if (_parameterOptionMap == null) {
            _parameterOptionMap = newHashMap();
        }
        final String parameterKey = "option" + _parameterOptionMap.size();
        _parameterOptionMap.put(parameterKey, option);
        final String parameterMapPath = xgetLocationBase() + "optionParameterMap";
        option.acceptParameterKey(parameterKey, parameterMapPath);
    }

    public Map<String, ParameterOption> getOptionParameterMap() { // for parameter comment
        return _parameterOptionMap;
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    // -----------------------------------------------------
    //                                                String
    //                                                ------
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // -----------------------------------------------------
    //                                            Collection
    //                                            ----------
    protected <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return DfCollectionUtil.newHashMap();
    }

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return DfCollectionUtil.newLinkedHashMap();
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList() {
        return DfCollectionUtil.newArrayList();
    }

    protected <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        return DfCollectionUtil.newArrayList(elements);
    }

    protected <ELEMENT> ArrayList<ELEMENT> newArrayList(Collection<ELEMENT> collection) {
        return DfCollectionUtil.newArrayList(collection);
    }

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

    /**
     * Assert that the column-name is not null and is not empty and does not contain comma.
     * @param columnName Column-name. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertColumnName(String columnName) {
        if (columnName == null) {
            String msg = "The columnName should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (columnName.trim().length() == 0) {
            String msg = "The columnName should not be empty-string.";
            throw new IllegalArgumentException(msg);
        }
        if (columnName.indexOf(",") >= 0) {
            String msg = "The columnName should not contain comma ',': " + columnName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the alias-name is not null and is not empty and does not contain comma.
     * @param aliasName Alias-name. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertAliasName(String aliasName) {
        if (aliasName == null) {
            String msg = "The aliasName should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (aliasName.trim().length() == 0) {
            String msg = "The aliasName should not be empty-string.";
            throw new IllegalArgumentException(msg);
        }
        if (aliasName.indexOf(",") >= 0) {
            String msg = "The aliasName should not contain comma ',': " + aliasName;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the string is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull("value", value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String titleName = DfTypeUtil.toClassTitle(this);
        return titleName + ":{aliasName=" + _aliasName + ", nestLevel=" + _nestLevel + ", subQueryLevel="
                + _subQueryLevel + ", foreignPropertyName=" + _foreignPropertyName + ", relationPath=" + _relationPath
                + ", onClauseInline=" + _onClause + "}";
    }
}
