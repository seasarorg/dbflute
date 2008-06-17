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

    public String getDefaultSourceDirectory() {
        return "../" + PATH_MAVEN_SRC_MAIN_PHP;
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
}
