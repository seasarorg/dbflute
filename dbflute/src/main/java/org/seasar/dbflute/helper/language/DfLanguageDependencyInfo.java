package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.properties.DfDaoDiconDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

public interface DfLanguageDependencyInfo {
    public DfGrammarInfo getGrammarInfo();
    public String getTemplateFileExtension();
    public DfDaoDiconDefault getDBFluteDiconDefault();
    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo();
}
