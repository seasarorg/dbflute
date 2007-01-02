package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoJava;
import org.seasar.dbflute.helper.language.properties.DfDaoDiconDefault;
import org.seasar.dbflute.helper.language.properties.DfDaoDiconDefaultJava;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultJava;

public class DfLanguageDependencyInfoJava implements DfLanguageDependencyInfo {
    
    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoJava();
    }
    
    public String getTemplateFileExtension() {
        return "vm";
    }
    
    public DfDaoDiconDefault getDBFluteDiconDefault() {
        return new DfDaoDiconDefaultJava();
    }
    
    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultJava();
    }
}
