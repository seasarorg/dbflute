package org.seasar.dbflute.helper.language;

import java.io.File;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.properties.DfDefaultDBFluteDicon;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

/**
 * @author jflute
 */
public interface DfLanguageDependencyInfo {
    
    /**
     * @return The information of target language grammar. (NotNull)
     */
    public DfGrammarInfo getGrammarInfo();

    /**
     * @return The file extension of a template. (NotNull)
     */
    public String getTemplateFileExtension();

    /**
     * @return The default of a dbflute dicon. (NotNull)
     */
    public DfDefaultDBFluteDicon getDefaultDBFluteDicon();

    /**
     * @return The information of a generated class package. (NotNull)
     */
    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo();

    /**
     * @return The meta data of the language. (NotNull)
     */
    public LanguageMetaData createLanguageMetaData();

    /**
     * @return The default of source directory. (NotNull)
     */
    public String getDefaultSourceDirectory();
    
    /**
     * @param value The integer value. (NotNull)
     * @return The expression of integer cnovertion. (NotNull)
     */
    public String getIntegerConvertExpression(String value);
    
    /**
     * @return The name of condition-bean package. (NotNull)
     */
    public String getConditionBeanPackageName();
    
    /**
     * @param file The file. (NotNull)
     * @return Is the file compile target?
     */
    public boolean isCompileTargetFile(File file);
}
