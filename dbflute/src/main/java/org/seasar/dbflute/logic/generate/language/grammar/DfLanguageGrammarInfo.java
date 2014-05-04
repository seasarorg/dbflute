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

import org.apache.torque.engine.database.model.Column;

/**
 * @author jflute
 */
public interface DfLanguageGrammarInfo {

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

    /**
     * @return The type literal of the class. (NotNull)
     */
    String getClassTypeLiteral(String className);

    /**
     * @return The definition of 'List(element)'. (NotNull)
     */
    String getGenericListClassName(String element);

    /**
     * @return The definition of 'List(Map(key, value))'. (NotNull)
     */
    String getGenericMapListClassName(String key, String value);

    // ===================================================================================
    //                                                              Programming Expression
    //                                                              ======================
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
}