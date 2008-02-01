/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.task;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 */
public class DfOutsideSqlTestTask extends DfInvokeSqlDirectoryTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected void customizeRunnerInformation(DfRunnerInformation runInfo) {
        runInfo.setEncoding(getProperties().getS2DaoAdjustmentProperties().getDaoSqlFileEncoding());
    }

    @Override
    protected List<File> getSqlFileList() {
        final String sqlDirectory = getSqlDirectory();
        final List<File> sqlFileList = collectSqlFile(sqlDirectory);
        if (!DfLanguageDependencyInfoJava.containsSrcMainJava(sqlDirectory)) {
            return sqlFileList;
        }
        final String srcMainResources = DfLanguageDependencyInfoJava.replaceSrcMainJavaToSrcMainResources(sqlDirectory);
        try {
            final List<File> resourcesSqlFileList = new DfSqlFileGetter().getSqlFileList(srcMainResources);
            sqlFileList.addAll(resourcesSqlFileList);
        } catch (Exception e) {
            _log.debug("Not found sql directory on resources: " + srcMainResources);
        }
        return sqlFileList;
    }

    protected List<File> collectSqlFile(String sqlDirectory) {
        return createSqlFileGetter().getSqlFileList(sqlDirectory);
    }

    protected DfSqlFileGetter createSqlFileGetter() {
        final DfLanguageDependencyInfo dependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return new DfSqlFileGetter() {
            @Override
            protected boolean acceptSqlFile(File file) {
                if (!dependencyInfo.isCompileTargetFile(file)) {
                    return false;
                }
                return super.acceptSqlFile(file);
            }
        };
    }

    @Override
    protected String getSqlDirectory() {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final DfBasicProperties basicProp = prop.getBasicProperties();
        final String javaDir = basicProp.getJavaDir_for_main();
        return javaDir;
    }

    @Override
    protected boolean isAutoCommit() {
        return false;
    }

    @Override
    protected boolean isErrorContinue() {
        return false;
    }

    @Override
    protected boolean isRollbackOnly() {
        return true;
    }
}
