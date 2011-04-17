/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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

    public String getDefaultMainProgramDirectory();

    public String getDefaultMainResourceDirectory();

    /**
     * @return The default of generate output directory. (NotNull)
     */
    public String getDefaultGenerateOutputDirectory();

    /**
     * @return The default of resource output directory. (NotNull)
     */
    public String getDefaultResourceOutputDirectory();

    /**
     * This method is for JDK-1.4 that has no AUTO BOXING!
     * But now DBFlute does not support JDBC-1.4 so that this method is unused.
     * @param value The integer value. (NotNull)
     * @return The expression of integer convertion. (NotNull)
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

    /**
     * @return Is the flat or omit directory supported?
     */
    public boolean isFlatOrOmitDirectorySupported();

    /**
     * @return The default type of sequence. (NotNull)
     */
    public String getDefaultSequenceType();
}
