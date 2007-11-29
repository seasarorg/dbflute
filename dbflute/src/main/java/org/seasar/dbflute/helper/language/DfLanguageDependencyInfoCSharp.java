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
import org.seasar.dbflute.util.DfStringUtil;

/**
 * The language dependency info of CSharp.
 * 
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

    public String getDefaultSourceDirectory() {
        return "../source";
    }
    
    public String getIntegerConvertExpression(String value) {
        return "new Nullable<int>(" + value + ")";
    }
    
    public String getConditionBeanPackageName() {
        return "CBean";
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
