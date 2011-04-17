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
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoJava;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataJava;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDiconJava;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultJava;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoJava implements DfLanguageDependencyInfo {

    public static final String PATH_MAVEN_SRC_MAIN_JAVA = "src/main/java";
    public static final String PATH_MAVEN_SRC_MAIN_RESOURCES = "src/main/resources";

    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoJava();
    }

    public String getTemplateFileExtension() {
        return "vm";
    }

    public DfDefaultDBFluteDicon getDefaultDBFluteDicon() {
        return new DfDefaultDBFluteDiconJava();
    }

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultJava();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataJava();
    }

    public String getDefaultMainProgramDirectory() {
        return PATH_MAVEN_SRC_MAIN_JAVA;
    }

    public String getDefaultMainResourceDirectory() {
        return PATH_MAVEN_SRC_MAIN_RESOURCES;
    }

    public String getDefaultGenerateOutputDirectory() {
        return "../" + PATH_MAVEN_SRC_MAIN_JAVA;
    }

    public String getDefaultResourceOutputDirectory() {
        return "../resources";
    }

    public String getIntegerConvertExpression(String value) {
        return "Integer.valueOf(\"" + value + "\")";
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

    public static boolean containsSrcMainJava(String path) {
        return path.contains(PATH_MAVEN_SRC_MAIN_JAVA);
    }

    /**
     * @param path The path of target. (NotNull)
     * @return Replaced maven path for 'src/main/resources' if it has 'src/main/java'. (NotNull)
     */
    public static String replaceSrcMainJavaToSrcMainResources(String path) {
        if (!path.contains(PATH_MAVEN_SRC_MAIN_JAVA)) {
            return path;
        }
        return Srl.replace(path, PATH_MAVEN_SRC_MAIN_JAVA, PATH_MAVEN_SRC_MAIN_RESOURCES);
    }

    public String getDefaultSequenceType() {
        return "java.math.BigDecimal";
    }
}
