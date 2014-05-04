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
import org.seasar.dbflute.logic.generate.language.grammar.DfLanguageGrammarCSharp;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageDBFluteDicon;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageDBFluteDiconCSharp;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageGeneratedClassPackage;
import org.seasar.dbflute.logic.generate.language.location.DfLanguageGeneratedClassPackageCSharp;
import org.seasar.dbflute.logic.generate.language.typemapping.DfLanguageTypeMapping;
import org.seasar.dbflute.logic.generate.language.typemapping.DfLanguageTypeMappingCSharp;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfLanguageDependencyCSharp implements DfLanguageDependency {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public String getLanguageTitle() {
        return "CSharp";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    public DfLanguageGrammar getLanguageGrammar() {
        return new DfLanguageGrammarCSharp();
    }

    public DfLanguageTypeMapping getLanguageTypeMapping() {
        return new DfLanguageTypeMappingCSharp();
    }

    public DfLanguageDBFluteDicon getLanguageDBFluteDicon() {
        return new DfLanguageDBFluteDiconCSharp();
    }

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    public String getMainProgramDirectory() {
        return "source";
    }

    public String getMainResourceDirectory() {
        return getMainProgramDirectory();
    }

    public boolean isCompileTargetFile(File file) {
        final String absolutePath = Srl.replace(file.getAbsolutePath(), "\\", "/");
        if (absolutePath.contains("/bin/") || absolutePath.contains("/obj/")) {
            return false;
        }
        return true;
    }

    public boolean isFlatOrOmitDirectorySupported() {
        return true;
    }

    // ===================================================================================
    //                                                                Generate Environment
    //                                                                ====================
    public String getGenerateControl() {
        return "om/ControlGenerateCSharp.vm";
    }

    public String getGenerateControlBhvAp() {
        return "om/csharp/plugin/bhvap/ControlBhvApCSharp.vm";
    }

    public String getSql2EntityControl() {
        return "om/ControlSql2EntityCSharp.vm";
    }

    public String getGenerateOutputDirectory() {
        return "../" + getMainProgramDirectory();
    }

    public String getResourceOutputDirectory() {
        return "../source/${topNamespace}/Resources"; // basically unused
    }

    public String getOutsideSqlDirectory() {
        return getMainProgramDirectory();
    }

    public String convertToSecondaryOutsideSqlDirectory(String sqlDirectory) {
        return null; // no secondary
    }

    public String getTemplateFileExtension() {
        return "vmnet";
    }

    public DfLanguageGeneratedClassPackage getGeneratedClassPackage() {
        return new DfLanguageGeneratedClassPackageCSharp();
    }

    // ===================================================================================
    //                                                                    Small Adjustment
    //                                                                    ================
    public boolean isIfCommentExpressionCheckEnabled() {
        return false; // different specification for now but new DBFlute.NET ...
    }

    public boolean isTypedParameterBeanEnabled() {
        return false; // unsupported for now but new DBFlute.NET ...
    }
}
