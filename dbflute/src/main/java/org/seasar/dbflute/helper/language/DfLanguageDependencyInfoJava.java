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
import org.seasar.dbflute.util.DfStringUtil;

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

    public String getDefaultSourceDirectory() {
        return "../" + PATH_MAVEN_SRC_MAIN_JAVA;
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
        return DfStringUtil.replace(path, PATH_MAVEN_SRC_MAIN_JAVA, PATH_MAVEN_SRC_MAIN_RESOURCES);
    }
}
