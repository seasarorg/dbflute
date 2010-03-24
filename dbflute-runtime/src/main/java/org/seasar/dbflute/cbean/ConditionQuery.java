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
package org.seasar.dbflute.cbean;

import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

/**
 * The condition-query as interface.
 * @author jflute
 */
public interface ConditionQuery {

    // ===================================================================================
    //                                                                  Important Accessor
    //                                                                  ==================
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    String getTableDbName();

    /**
     * Get real alias name (that has nest level mark).
     * @return Real alias name. (NotNull)
     */
    String getRealAliasName();

    /**
     * Get real column name (with real alias name).
     * @param columnName Column name without alias name. (NotNull)
     * @return Real column name. (NotNull)
     */
    String getRealColumnName(String columnName);

    /**
     * Get the referrer query.
     * @return The referrer query. (Nullable: If null, this is base query)
     */
    ConditionQuery getReferrerQuery();

    /**
     * Get the SqlClause.
     * @return The SqlClause. (NotNull)
     */
    SqlClause getSqlClause();

    /**
     * Get alias name.
     * @return Alias name. (NotNull)
     */
    String getAliasName();

    /**
     * Get nest level.
     * @return Nest level.
     */
    int getNestLevel();

    /**
     * Get next nest level.
     * @return Next nest level.
     */
    int getNextNestLevel();

    /**
     * Is this a base query?
     * @return Determination.
     */
    boolean isBaseQuery();

    /**
     * Get the level of subQuery.
     * @return The level of subQuery.
     */
    int getSubQueryLevel();

    /**
     * Get the property name of foreign relation.
     * @return The property name of foreign relation. (NotNull)
     */
    String getForeignPropertyName();

    /**
     * Get the path of foreign relation.
     * @return The path of foreign relation. (NotNull)
     */
    String getRelationPath();

    // ===================================================================================
    //                                                                 Reflection Invoking
    //                                                                 ===================
    /**
     * Invoke getting value.
     * @param columnFlexibleName The flexible name of the column. (NotNull and NotEmpty)
     * @return The conditionValue. (NotNull)
     */
    ConditionValue invokeValue(String columnFlexibleName);

    /**
     * Invoke setting query. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param conditionKeyName The name of the conditionKey. (NotNull)
     * @param value The value of the condition. (NotNull)
     */
    void invokeQuery(String columnFlexibleName, String conditionKeyName, Object value);

    /**
     * Invoke adding orderBy. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param isAsc Is it ascend?
     */
    void invokeOrderBy(String columnFlexibleName, boolean isAsc);

    /**
     * Invoke getting foreign conditionQuery. <br />
     * A method with parameters (using fixed condition) is unsupported.
     * @param foreignPropertyName The property name of the foreign relation. (NotNull and NotEmpty)
     * @return The conditionQuery of the foreign relation as interface. (NotNull)
     */
    ConditionQuery invokeForeignCQ(String foreignPropertyName);
}
