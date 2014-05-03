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
package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoScala;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataJava;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoScala extends DfLanguageDependencyInfoJava {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String PATH_MAVEN_SRC_MAIN_SCALA = "src/main/scala";

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    @Override
    public String getLanguageTitle() {
        return "Scala";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    @Override
    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoScala();
    }

    @Override
    public String getIntegerConvertExpression(String value) {
        return "Integer.valueOf(\"" + value + "\")"; // #pending jflute Scala's convert
    }

    @Override
    public String getSequenceType() {
        return "java.math.BigDecimal"; // #pending jflute Scala's big decimal
    }

    @Override
    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataJava(); // #pending jflute Scala's type
    }

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    @Override
    public String getMainProgramDirectory() {
        return PATH_MAVEN_SRC_MAIN_SCALA;
    }

    // ===================================================================================
    //                                                                Generate Environment
    //                                                                ====================
    @Override
    public String getGenerateControl() {
        return "om/ControlGenerateScala.vm";
    }

    @Override
    public String getGenerateControlBhvAp() {
        throw new UnsupportedOperationException("Unsupported language Scala");
    }

    @Override
    public String getSql2EntityControl() {
        return "om/ControlSql2EntityScala.vm";
    }

    @Override
    public String getTemplateFileExtension() {
        return "vmsca";
    }
}
