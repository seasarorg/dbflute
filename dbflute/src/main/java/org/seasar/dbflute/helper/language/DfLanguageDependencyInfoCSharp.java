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
package org.seasar.dbflute.helper.language;

import java.io.File;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoCSharp;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataCSharp;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDiconCSharp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultCSharp;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoCSharp implements DfLanguageDependencyInfo {

    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoCSharp();
    }

    public String getTemplateFileExtension() {
        return "vmnet";
    }

    public DfDefaultDBFluteDicon getDefaultDBFluteDicon() {
        return new DfDefaultDBFluteDiconCSharp();
    }

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultCSharp();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataCSharp();
    }

    public String getDefaultMainProgramDirectory() {
        return "source";
    }

    public String getDefaultMainResourceDirectory() {
        return "source";
    }

    public String getDefaultGenerateOutputDirectory() {
        return "../source";
    }

    public String getDefaultResourceOutputDirectory() {
        return "../source/${topNamespace}/Resources"; // basically unused
    }

    public String getIntegerConvertExpression(String value) {
        return "new int?(" + value + ")";
    }

    public String getConditionBeanPackageName() {
        return "CBean";
    }

    public boolean isCompileTargetFile(File file) {
        String absolutePath = file.getAbsolutePath();
        absolutePath = Srl.replace(absolutePath, "\\", "/");
        if (absolutePath.contains("/bin/") || absolutePath.contains("/obj/")) {
            return false;
        }
        return true;
    }

    public boolean isFlatOrOmitDirectorySupported() {
        return true;
    }

    public String getDefaultSequenceType() {
        return "int?";
    }
}
