/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.generate.language.grammar;

import java.util.List;

import org.apache.torque.engine.database.model.Column;

/**
 * @author jflute
 */
public interface DfLanguageGrammar {

    // ===================================================================================
    //                                                                       Basic Keyword
    //                                                                       =============
    /**
     * @return The file extension of class. (NotNull)
     */
    String getClassFileExtension();

    /**
     * @return The string mark of 'extends'. (NotNull)
     */
    String getExtendsStringMark();

    /**
     * @return The string mark of 'implements'. (NotNull)
     */
    String getImplementsStringMark();

    /**
     * @return The definition of 'public'. (NotNull)
     */
    String getPublicDefinition();

    /**
     * @return The definition of 'public static'. (NotNull)
     */
    String getPublicStaticDefinition();

    // ===================================================================================
    //                                                              Programming Expression
    //                                                              ======================
    /**
     * @param methodName The method name that might not be adjusted. (NotNull)
     * @return The initial-character-adjusted name for the method. (NotNull)
     */
    String adjustMethodInitialChar(String methodName);

    /**
     * @param propertyName The property name that might not be adjusted. (NotNull)
     * @return The initial-character-adjusted name for the method. (NotNull)
     */
    String adjustPropertyInitialChar(String propertyName);

    /**
     * @param propertyName The pure (no call expression) property name. (NotNull)
     * @return The property-suffix-adjusted name for the method. (NotNull)
     */
    String buildPropertyGetterCall(String propertyName);

    /**
     * @param className The name of class. (NotNull)
     * @return The type literal of the class. (NotNull)
     */
    String getClassTypeLiteral(String className);

    /**
     * @param element The element type for list generic. (NotNull)
     * @return The definition of 'List&lt;element&gt;'. (NotNull)
     */
    String buildGenericListClassName(String element);

    /**
     * @param key The key type for map generic. (NotNull)
     * @param value The value type for map generic. (NotNull)
     * @return The definition of 'List&lt;Map&lt;key, value&gt;&gt;'. (NotNull)
     */
    String buildGenericMapListClassName(String key, String value);

    /**
     * @param first The only-one type name for the generic. (NotNull)
     * @return The definition of '&lt;first&gt;'. (NotNull)
     */
    String buildGenericOneClassHint(String first);

    /**
     * @param first The first type name for the generic. (NotNull)
     * @param second The second type name for the generic. (NotNull)
     * @return The definition of '&lt;first, second&gt;'. (NotNull)
     */
    String buildGenericTwoClassHint(String first, String second);

    /**
     * @param fromCol The column object to get. (NotNull)
     * @param toCol The column object to set. (NotNull)
     * @return The string expression of mapping logic by getter and setter. (NotNull)
     */
    String buildEntityPropertyGetSet(Column fromCol, Column toCol);

    /**
     * @param col The column object to build. (NotNull)
     * @return The string expression of entity property name. (NotNUll)
     */
    String buildEntityPropertyName(Column col);

    String buildCDefElementValue(String cdefBase, String propertyName, String valueType, boolean toNumber,
            boolean toBoolean);

    /**
     * @return The new expression of list with elements as one liner. (NotNull)
     */
    String buildOneLinerListNewBackStage(List<String> elementList);

    // ===================================================================================
    //                                                                    Small Adjustment 
    //                                                                    ================
    /**
     * @return Is the column name match with program reserved name?
     */
    boolean isPgReservColumn(String columnName);
}