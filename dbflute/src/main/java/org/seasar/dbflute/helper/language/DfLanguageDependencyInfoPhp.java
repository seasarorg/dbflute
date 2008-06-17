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
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoPhp implements DfLanguageDependencyInfo {

    public static final String PATH_MAVEN_SRC_MAIN_PHP = "src/main/php";
    public static final String PATH_MAVEN_SRC_MAIN_RESOURCES = "src/main/resources";

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

    public static boolean containsSrcMainJava(String path) {
        return path.contains(PATH_MAVEN_SRC_MAIN_PHP);
    }

    /**
     * @param path The path of target. (NotNull)
     * @return Replaced maven path for 'src/main/resources' if it has 'src/main/php'. (NotNull)
     */
    public static String replaceSrcMainJavaToSrcMainResources(String path) {
        if (!path.contains(PATH_MAVEN_SRC_MAIN_PHP)) {
            return path;
        }
        return DfStringUtil.replace(path, PATH_MAVEN_SRC_MAIN_PHP, PATH_MAVEN_SRC_MAIN_RESOURCES);
    }
}
