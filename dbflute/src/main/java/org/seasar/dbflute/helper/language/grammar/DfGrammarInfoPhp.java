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
public class DfGrammarInfoPhp implements DfGrammarInfo {

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
    }

    public String getClassFileExtension() {
        return "php";
    }

    public String getPublicDefinition() {
        return "const";
    }

    public String getPublicStaticDefinition() {
        return "const";
    }

    public String getClassTypeLiteral(String className) {
        return "Unsupported!";
    }

    public String getGenericListClassName(String element) {
        return "Unsupported!";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "Unsupported!";
    }
}