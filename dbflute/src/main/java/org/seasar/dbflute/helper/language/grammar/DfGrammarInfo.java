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
package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public interface DfGrammarInfo {

    /**
     * @return The file extension of class. (NotNull)
     */
    public String getClassFileExtension();

    /**
     * @return The string mark of 'extends'. (NotNull)
     */
    public String getExtendsStringMark();

    /**
     * @return The string mark of 'implements'. (NotNull)
     */
    public String getImplementsStringMark();

    /**
     * @return The definition of 'public'. (NotNull)
     */
    public String getPublicDefinition();

    /**
     * @return The definition of 'public static'. (NotNull)
     */
    public String getPublicStaticDefinition();

    /**
     * @return The type literal of the class. (NotNull)
     */
    public String getClassTypeLiteral(String className);

    /**
     * @return The definition of 'List(element)'. (NotNull)
     */
    public String getGenericListClassName(String element);

    /**
     * @return The definition of 'List(Map(key, value))'. (NotNull)
     */
    public String getGenericMapListClassName(String key, String value);
}