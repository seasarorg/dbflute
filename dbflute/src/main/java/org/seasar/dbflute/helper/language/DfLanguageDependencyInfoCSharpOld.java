package org.seasar.dbflute.helper.language;

import java.io.File;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoCSharp;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataCSharpOld;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDiconCSharp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultCSharpOld;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoCSharpOld implements DfLanguageDependencyInfo {
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
        return new DfGeneratedClassPackageDefaultCSharpOld();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataCSharpOld();
    }

    public String getDefaultSourceDirectory() {
        return "../source";
    }
    
    public String getIntegerConvertExpression(String value) {
        return "new Nullable<int>(" + value + ")";
    }
    
    public String getConditionBeanPackageName() {
        return "cbean";
    }
    
    public boolean isCompileTargetFile(File file) {
        String absolutePath = file.getAbsolutePath();
        absolutePath = DfStringUtil.replace(absolutePath, "\\", "/");
        if (absolutePath.contains("/bin/") || absolutePath.contains("/obj/")) {
            return false;
        }
        return true;
    }
}
