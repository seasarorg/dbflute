/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfo;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;

/**
 * @author jflute
 */
public interface DfLanguageDependencyInfo {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    String getLanguageTitle();

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    /**
     * @return The information of target language grammar. (NotNull)
     */
    DfGrammarInfo getGrammarInfo();

    /**
     * This method is for JDK-1.4 that has no AUTO BOXING!
     * But now DBFlute does not support JDBC-1.4 so that this method is unused.
     * @param value The integer value. (NotNull)
     * @return The expression of integer conversion. (NotNull)
     */
    String getIntegerConvertExpression(String value);

    /**
     * @return The type of sequence. (NotNull)
     */
    String getSequenceType();

    /**
     * @return The information object of DBFlute dicon. (NotNull)
     */
    DfDBFluteDiconInfo getDBFluteDiconInfo();

    /**
     * @return The meta data of the language. (NotNull)
     */
    LanguageMetaData createLanguageMetaData();

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    /**
     * @return The directory for main program. (NotNull)
     */
    String getMainProgramDirectory();

    /**
     * @return The directory for main resources. (NotNull: might be same as program directory)
     */
    String getMainResourceDirectory();

    /**
     * @param file The file. (NotNull)
     * @return Is the file compile target?
     */
    boolean isCompileTargetFile(File file);

    /**
     * @return Is the flat or omit directory supported?
     */
    boolean isFlatOrOmitDirectorySupported();

    // ===================================================================================
    //                                                                Generate Environment
    //                                                                ====================
    /**
     * @return The path of velocity control file for generate. (NotNull) 
     */
    String getGenerateControl();

    /**
     * @return The path of velocity control file for application behavior generate. (NotNull) 
     */
    String getGenerateControlBhvAp();

    /**
     * @return The path of velocity control file for sql2entity. (NotNull) 
     */
    String getSql2EntityControl();

    /**
     * @return The directory for generate output. (NotNull)
     */
    String getGenerateOutputDirectory();

    /**
     * @return The relative path (from generate output directory) of directory for resource output. (NotNull)
     */
    String getResourceOutputDirectory();

    /**
     * @return The directory for outside SQL. (NotNull)
     */
    String getOutsideSqlDirectory();

    /**
     * @param sqlDirectory The primary SQL directory. (NotNull)
     * @return The converted SQL directory for secondary. (NullAllowed: when no secondary)
     */
    String convertToSecondaryOutsideSqlDirectory(String sqlDirectory);

    /**
     * @return The file extension of a template. (NotNull)
     */
    String getTemplateFileExtension();

    /**
     * @return The information of a generated class package. (NotNull)
     */
    DfGeneratedClassPackageDefault getGeneratedClassPackageInfo();
}
