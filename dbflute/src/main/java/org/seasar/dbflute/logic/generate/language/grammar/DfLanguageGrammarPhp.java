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
public class DfLanguageGrammarPhp implements DfLanguageGrammar {

    // ===================================================================================
    //                                                                       Basic Keyword
    //                                                                       =============
    public String getClassFileExtension() {
        return "php";
    }

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
    }

    public String getPublicDefinition() {
        return "const";
    }

    public String getPublicStaticDefinition() {
        return "const";
    }

    public String getClassTypeLiteral(String className) {
        throw new UnsupportedOperationException("Unsupported at Php");
    }

    // ===================================================================================
    //                                                              Programming Expression
    //                                                              ======================
    public String buildGenericListClassName(String element) {
        throw new UnsupportedOperationException("Unsupported at Php");
    }

    public String buildGenericMapListClassName(String key, String value) {
        throw new UnsupportedOperationException("Unsupported at Php");
    }

    public String buildGenericOneClassHint(String first) {
        return "";
    }

    public String buildGenericTwoClassHint(String first, String second) {
        return "";
    }

    public String buildEntityPropertyGetSet(Column fromCol, Column toCol) {
        throw new UnsupportedOperationException("Unsupported at Php");
    }

    public String buildEntityPropertyName(Column col) {
        return col.getUncapitalisedJavaName();
    }

    public String buildCDefElementValue(String cdefBase, String propertyName, String valueType, boolean toNumber,
            boolean toBoolean) {
        throw new UnsupportedOperationException("Unsupported at Php");
    }
}