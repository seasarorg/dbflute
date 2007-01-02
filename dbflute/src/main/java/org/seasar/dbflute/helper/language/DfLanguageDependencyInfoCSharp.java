package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoCSharp;
import org.seasar.dbflute.helper.language.properties.DfDaoDiconDefault;
import org.seasar.dbflute.helper.language.properties.DfDaoDiconDefaultCSharp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultCSharp;

public class DfLanguageDependencyInfoCSharp implements DfLanguageDependencyInfo {

    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoCSharp();
    }

    public String getTemplateFileExtension() {
        return "vmnet";
    }

    public DfDaoDiconDefault getDBFluteDiconDefault() {
        return new DfDaoDiconDefaultCSharp();
    }
    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultCSharp();
    }

}
