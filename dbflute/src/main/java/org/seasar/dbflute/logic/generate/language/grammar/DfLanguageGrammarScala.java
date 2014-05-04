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
public class DfLanguageGrammarScala implements DfLanguageGrammar {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfLanguageGrammarJava _grammarJava = new DfLanguageGrammarJava();

    // ===================================================================================
    //                                                                       Basic Keyword
    //                                                                       =============
    public String getClassFileExtension() {
        return "scala";
    }

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
    }

    public String getPublicDefinition() {
        return "val";
    }

    public String getPublicStaticDefinition() {
        return "val";
    }

    // ===================================================================================
    //                                                              Programming Expression
    //                                                              ======================
    // #pending jflute Scala's collections
    // #pending jflute same as Java for now

    public String adjustMethodInitialChar(String methodName) {
        return _grammarJava.adjustMethodInitialChar(methodName);
    }

    public String adjustPropertyInitialChar(String propertyName) {
        return _grammarJava.adjustPropertyInitialChar(propertyName);
    }

    public String buildPropertyGetterCall(String propertyName) {
        return _grammarJava.buildPropertyGetterCall(propertyName);
    }

    public String getClassTypeLiteral(String className) {
        return "classOf[" + className + "]";
    }

    public String buildGenericListClassName(String element) {
        return _grammarJava.buildGenericListClassName(element);
    }

    public String buildGenericMapListClassName(String key, String value) {
        return _grammarJava.buildGenericMapListClassName(key, value);
    }

    public String buildGenericOneClassHint(String first) {
        return _grammarJava.buildGenericOneClassHint(first);
    }

    public String buildGenericTwoClassHint(String first, String second) {
        return _grammarJava.buildGenericTwoClassHint(first, second);
    }

    public String buildEntityPropertyGetSet(Column fromCol, Column toCol) {
        return _grammarJava.buildEntityPropertyGetSet(fromCol, toCol);
    }

    public String buildEntityPropertyName(Column col) {
        return _grammarJava.buildEntityPropertyName(col);
    }

    public String buildCDefElementValue(String cdefBase, String propertyName, String valueType, boolean toNumber,
            boolean toBoolean) {
        return _grammarJava.buildCDefElementValue(cdefBase, propertyName, valueType, toNumber, toBoolean);
    }

    public String buildOneLinerListNewBackStage(List<String> elementList) {
        return _grammarJava.buildOneLinerListNewBackStage(elementList);
    }

    // ===================================================================================
    //                                                                    Small Adjustment 
    //                                                                    ================
    public boolean isPgReservColumn(String columnName) {
        return _grammarJava.isPgReservColumn(columnName);
    }
}