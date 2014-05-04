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
package org.seasar.dbflute.logic.generate.language;

import java.io.File;

import org.seasar.dbflute.logic.generate.language.grammar.DfLanguageGrammar;
import org.seasar.dbflute.logic.generate.language.grammar.DfLanguageGrammarPhp;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageDBFluteDicon;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageDBFluteDiconPhp;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageGeneratedClassPackage;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageGeneratedClassPackagePhp;
import org.seasar.dbflute.logic.generate.language.typemapping.DfLanguageTypeMapping;
import org.seasar.dbflute.logic.generate.language.typemapping.DfLanguageTypeMappingPhp;

/**
 * @author jflute
 */
public class DfLanguageDependencyPhp implements DfLanguageDependency {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String PATH_MAVEN_SRC_MAIN_PHP = "src/main/php";

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public String getLanguageTitle() {
        return "Php";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    public DfLanguageGrammar getLanguageGrammar() {
        return new DfLanguageGrammarPhp();
    }

    public DfLanguageTypeMapping getLanguageTypeMapping() {
        return new DfLanguageTypeMappingPhp();
    }

    public DfLanguageDBFluteDicon getLanguageDBFluteDicon() {
        return new DfLanguageDBFluteDiconPhp();
    }

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    public String getMainProgramDirectory() {
        return PATH_MAVEN_SRC_MAIN_PHP;
    }

    public String getMainResourceDirectory() {
        return getMainProgramDirectory();
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
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getGenerateControlBhvAp() {
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getSql2EntityControl() {
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getOutsideSqlDirectory() {
        return getMainProgramDirectory();
    }

    public String convertToSecondaryOutsideSqlDirectory(String sqlDirectory) {
        return null; // no secondary
    }

    public String getGenerateOutputDirectory() {
        return "../" + getMainProgramDirectory();
    }

    public String getResourceOutputDirectory() {
        return "";
    }

    public String getTemplateFileExtension() {
        return "vmphp";
    }

    public DfLanguageGeneratedClassPackage getGeneratedClassPackage() {
        return new DfLanguageGeneratedClassPackagePhp();
    }

    // ===================================================================================
    //                                                                    Small Adjustment
    //                                                                    ================
    public boolean isIfCommentExpressionCheckEnabled() {
        return false;
    }

    public boolean isTypedParameterBeanEnabled() {
        return false;
    }
}
