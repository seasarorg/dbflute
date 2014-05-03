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
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoCSharp;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataCSharp;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfo;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfoCSharp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultCSharp;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoCSharp implements DfLanguageDependencyInfo {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public String getLanguageTitle() {
        return "CSharp";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoCSharp();
    }

    public String getIntegerConvertExpression(String value) {
        return "new int?(" + value + ")";
    }

    public String getSequenceType() {
        return "int?";
    }

    public DfDBFluteDiconInfo getDBFluteDiconInfo() {
        return new DfDBFluteDiconInfoCSharp();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataCSharp();
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

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultCSharp();
    }
}
