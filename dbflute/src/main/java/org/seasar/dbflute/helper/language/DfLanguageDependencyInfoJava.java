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

import java.io.File;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoJava;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataJava;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfo;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconJavaInfo;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultJava;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoJava implements DfLanguageDependencyInfo {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String PATH_MAVEN_SRC_MAIN_JAVA = "src/main/java";
    protected static final String PATH_MAVEN_SRC_MAIN_RESOURCES = "src/main/resources";

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public String getLanguageTitle() {
        return "Java";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoJava();
    }

    public String getIntegerConvertExpression(String value) {
        return "Integer.valueOf(\"" + value + "\")";
    }

    public String getSequenceType() {
        return "java.math.BigDecimal";
    }

    public DfDBFluteDiconInfo getDBFluteDiconInfo() {
        return new DfDBFluteDiconJavaInfo();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataJava();
    }

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    public String getMainProgramDirectory() {
        return PATH_MAVEN_SRC_MAIN_JAVA;
    }

    public String getMainResourceDirectory() {
        return PATH_MAVEN_SRC_MAIN_RESOURCES;
    }

    public boolean isCompileTargetFile(File file) {
        return true;
    }

    public boolean isFlatOrOmitDirectorySupported() {
        return false;
    }

    // ===================================================================================
    //                                                                Generate Environment
    //                                                                ====================
    public String getGenerateControl() {
        return "om/ControlGenerateJava.vm";
    }

    public String getGenerateControlBhvAp() {
        return "om/java/plugin/bhvap/ControlBhvApJava.vm";
    }

    public String getSql2EntityControl() {
        return "om/ControlSql2EntityJava.vm";
    }

    public String getGenerateOutputDirectory() {
        return "../" + getMainProgramDirectory();
    }

    public String getResourceOutputDirectory() {
        return "../resources";
    }

    public String getOutsideSqlDirectory() {
        // returns program directory
        // because it is possible that resources directory does not prepared
        // and resources directory is resolved later
        return getMainProgramDirectory();
    }

    public String convertToSecondaryOutsideSqlDirectory(String sqlDirectory) {
        final String mainProgramDirectory = getMainProgramDirectory();
        if (!sqlDirectory.contains(mainProgramDirectory)) {
            return null; // no secondary
        }
        return Srl.replace(sqlDirectory, mainProgramDirectory, getMainResourceDirectory());
    }

    public String getTemplateFileExtension() {
        return "vm";
    }

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultJava();
    }
}
