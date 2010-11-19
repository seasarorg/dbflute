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
package org.seasar.dbflute.cbean;

import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.exception.ConditionInvokingFailureException;

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

    // internal getter methods start with 'x'
    // not to be same as column names 

    /**
     * Convert to the column real name. (with real alias name)
     * @param columnDbName The DB name of column. (NotNull)
     * @return the column real name. (NotNull)
     */
    ColumnRealName toColumnRealName(String columnDbName);

    /**
     * Convert to the column SQL name.
     * @param columnDbName The DB name of column. (NotNull)
     * @return the column SQL name. (NotNull)
     */
    ColumnSqlName toColumnSqlName(String columnDbName);

    /**
     * Get the referrer query.
     * @return The referrer query. (Nullable: If null, this is base query)
     */
    ConditionQuery xgetReferrerQuery();

    /**
     * Get the SqlClause.
     * @return The SqlClause. (NotNull)
     */
    SqlClause xgetSqlClause();

    /**
     * Get alias name.
     * @return Alias name. (NotNull)
     */
    String xgetAliasName();

    /**
     * Get nest level.
     * @return Nest level.
     */
    int xgetNestLevel();

    /**
     * Get next nest level.
     * @return Next nest level.
     */
    int xgetNextNestLevel();

    /**
     * Is this a base query?
     * @return Determination.
     */
    boolean isBaseQuery();

    /**
     * Get the level of subQuery.
     * @return The level of subQuery.
     */
    int xgetSubQueryLevel();

    /**
     * Set the level of subQuery.
     * @param subQueryLevel The level of subQuery.
     */
    void xsetSubQueryLevel(int subQueryLevel);

    /**
     * Get the property name of foreign relation.
     * @return The property name of foreign relation. (NotNull)
     */
    String xgetForeignPropertyName();

    /**
     * Get the path of foreign relation. ex) _0_1
     * @return The path of foreign relation. (Nullable)
     */
    String xgetRelationPath();

    /**
     * Get the base location of this condition-query.
     * @return The base location of this condition-query. (NotNull)
     */
    String xgetLocationBase();

    // ===================================================================================
    //                                                                 Reflection Invoking
    //                                                                 ===================
    /**
     * Invoke getting value.
     * @param columnFlexibleName The flexible name of the column. (NotNull and NotEmpty)
     * @return The conditionValue. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the column is not found and the method is failed.
     */
    ConditionValue invokeValue(String columnFlexibleName);

    /**
     * Invoke setting query. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param conditionKeyName The name of the conditionKey. (NotNull)
     * @param value The value of the condition. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the column is not found and the method is failed.
     */
    void invokeQuery(String columnFlexibleName, String conditionKeyName, Object value);

    /**
     * Invoke setting query with option. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param conditionKeyName The name of the conditionKey. (NotNull)
     * @param value The value of the condition. (NotNull)
     * @param option The option of the condition. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the column is not found and the method is failed.
     */
    void invokeQuery(String columnFlexibleName, String conditionKeyName, Object value, ConditionOption option);

    /**
     * Invoke setting query of equal. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param value The value of the condition. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the column is not found and the method is failed.
     */
    void invokeQueryEqual(String columnFlexibleName, Object value);

    /**
     * Invoke adding orderBy. {ResolveRelation}
     * @param columnFlexibleName The flexible name of the column allowed to contain relations. (NotNull and NotEmpty)
     * @param isAsc Is it ascend?
     * @throws ConditionInvokingFailureException When the method to the column is not found and the method is failed.
     */
    void invokeOrderBy(String columnFlexibleName, boolean isAsc);

    /**
     * Invoke getting foreign condition-query. <br />
     * A method with parameters (using fixed condition) is unsupported.
     * @param foreignPropertyName The property name(s), can contain '.' , of the foreign relation. (NotNull and NotEmpty)
     * @return The conditionQuery of the foreign relation as interface. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the property is not found and the method is failed.
     */
    ConditionQuery invokeForeignCQ(String foreignPropertyName);

    /**
     * Invoke determining foreign condition-query existence?
     * A method with parameters (using fixed condition) is unsupported.
     * @param foreignPropertyName The property name(s), can contain '.' , of the foreign relation. (NotNull and NotEmpty)
     * @return The conditionQuery of the foreign relation as interface. (NotNull)
     * @throws ConditionInvokingFailureException When the method to the property is not found and the method is failed.
     */
    boolean invokeHasForeignCQ(String foreignPropertyName);
}
