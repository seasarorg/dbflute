package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoJava;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataJava;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDiconJava;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultJava;

/**
 * The language depndency info of Java.
 * 
 * @author jflute
 */
public class DfLanguageDependencyInfoJava implements DfLanguageDependencyInfo {

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
        return "../src/main/java";
    }
    
    public String getIntegerConvertExpression(String value) {
        return "Integer.valueOf(\"" + value + "\")";
    }
}
