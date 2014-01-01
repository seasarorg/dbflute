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
package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public class DfGrammarInfoCSharp implements DfGrammarInfo {

    public String getClassFileExtension() {
        return "cs";
    }

    public String getExtendsStringMark() {
        return ":";
    }

    public String getImplementsStringMark() {
        return ":";
    }

    public String getPublicDefinition() {
        return "public readonly";
    }

    public String getPublicStaticDefinition() {
        return "public static readonly";
    }

    public String getClassTypeLiteral(String className) {
        return "typeof(" + className + ")";
    }

    public String getGenericListClassName(String element) {
        return "IList<" + element + ">";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "IList<IDictionary<" + key + ", " + value + ">>";
    }
}