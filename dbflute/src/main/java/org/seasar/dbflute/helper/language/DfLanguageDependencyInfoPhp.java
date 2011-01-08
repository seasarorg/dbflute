/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoPhp;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataPhp;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDiconPhp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultPhp;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoPhp implements DfLanguageDependencyInfo {

    public static final String PATH_MAVEN_SRC_MAIN_PHP = "src/main/php";

    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoPhp();
    }

    public String getTemplateFileExtension() {
        return "vmphp";
    }

    public DfDefaultDBFluteDicon getDefaultDBFluteDicon() {
        return new DfDefaultDBFluteDiconPhp();
    }

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultPhp();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataPhp();
    }

    public String getDefaultGenerateOutputDirectory() {
        return "../" + PATH_MAVEN_SRC_MAIN_PHP;
    }

    public String getDefaultResourceOutputDirectory() {
        return "";
    }

    public String getIntegerConvertExpression(String value) {
        return value;
    }

    public String getConditionBeanPackageName() {
        return "cbean";
    }

    public boolean isCompileTargetFile(File file) {
        return true;
    }

    public boolean isFlatOrOmitDirectorySupported() {
        return false;
    }

    public String getDefaultSequenceType() {
        return "";
    }
}
