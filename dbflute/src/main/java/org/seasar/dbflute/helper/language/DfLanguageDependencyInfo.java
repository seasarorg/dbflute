package org.seasar.dbflute.helper.language;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

/**
 * @author jflute
 */
public interface DfLanguageDependencyInfo {
    public DfGrammarInfo getGrammarInfo();

    public String getTemplateFileExtension();

    public DfDefaultDBFluteDicon getDefaultDBFluteDicon();

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo();

    public LanguageMetaData createLanguageMetaData();

    public String getDefaultSourceDirectory();
}
