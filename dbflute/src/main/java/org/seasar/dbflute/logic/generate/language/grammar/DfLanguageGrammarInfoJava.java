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
public class DfLanguageGrammarInfoJava implements DfLanguageGrammarInfo {

    // ===================================================================================
    //                                                                       Basic Keyword
    //                                                                       =============
    public String getClassFileExtension() {
        return "java";
    }

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
    }

    public String getPublicDefinition() {
        return "public final";
    }

    public String getPublicStaticDefinition() {
        return "public static final";
    }

    public String getClassTypeLiteral(String className) {
        return className + ".class";
    }

    public String getGenericListClassName(String element) {
        return "List<" + element + ">";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "List<Map<" + key + ", " + value + ">>";
    }

    // ===================================================================================
    //                                                              Programming Expression
    //                                                              ======================
    public String buildEntityPropertyGetSet(Column fromCol, Column toCol) {
        final String fromPropName = fromCol.getJavaBeansRulePropertyNameInitCap();
        final String toPropName = toCol.getJavaBeansRulePropertyNameInitCap();
        return "set" + toPropName + "(get" + fromPropName + "())";
    }

    public String buildEntityPropertyName(Column col) {
        return col.getJavaBeansRulePropertyName();
    }

    public String buildCDefElementValue(String cdefBase, String propertyName, String valueType, boolean toNumber,
            boolean toBoolean) {
        final String cdefCode = cdefBase + ".code()";
        if (toNumber) {
            return "toNumber(" + cdefCode + ", " + valueType + ".class)";
        } else if (toBoolean) {
            return "toBoolean(" + cdefCode + ")";
        } else {
            return cdefCode;
        }
    }
}