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
package org.seasar.dbflute.logic.sql2entity.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/29 Friday)
 */
public class DfOutsideSqlCollector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfOutsideSqlCollector.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _suppressDirectoryCheck;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfOutsideSqlCollector() {
    }

    // ===================================================================================
    //                                                                             Collect
    //                                                                             =======
    /**
     * Collect outside-SQL containing its file info as pack.
     * @return The pack object for outside-SQL files. (NotNull)
     */
    public DfOutsideSqlPack collectOutsideSql() {
        final DfOutsideSqlPack outsideSqlPack = new DfOutsideSqlPack();
        final List<DfOutsideSqlLocation> sqlDirectoryList = getSqlDirectoryList();
        for (DfOutsideSqlLocation sqlLocation : sqlDirectoryList) {
            final String sqlDirectory = sqlLocation.getSqlDirectory();
            if (existsSqlDir(sqlDirectory)) {
                outsideSqlPack.addAll(collectSqlFile(sqlDirectory, sqlLocation));
                final String srcMainResources = replaceSrcMainJavaToSrcMainResources(sqlDirectory);
                if (!sqlDirectory.equals(srcMainResources)) {
                    try {
                        outsideSqlPack.addAll(collectSqlFile(srcMainResources, sqlLocation));
                    } catch (Exception e) {
                        _log.info("Not found sql directory on resources: " + srcMainResources);
                    }
                }
            } else {
                final boolean suppressCheck = _suppressDirectoryCheck || sqlLocation.isSuppressDirectoryCheck();
                if (containsSrcMainJava(sqlDirectory)) {
                    final String srcMainResources = replaceSrcMainJavaToSrcMainResources(sqlDirectory);
                    if (!sqlDirectory.equals(srcMainResources)) {
                        if (existsSqlDir(srcMainResources)) {
                            outsideSqlPack.addAll(collectSqlFile(srcMainResources, sqlLocation));
                        } else {
                            if (!suppressCheck) {
                                String msg = "The sqlDirectory does not exist: " + srcMainResources;
                                throw new IllegalStateException(msg);
                            }
                        }
                    }
                } else {
                    if (!suppressCheck) {
                        String msg = "The sqlDirectory does not exist: " + sqlDirectory;
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return outsideSqlPack;
    }

    protected List<DfOutsideSqlLocation> getSqlDirectoryList() {
        final DfOutsideSqlProperties prop = getOutsideSqlProperties();
        return prop.getSqlLocationList();
    }

    protected boolean existsSqlDir(String sqlDirPath) {
        return new File(sqlDirPath).exists();
    }

    protected List<DfOutsideSqlFile> collectSqlFile(String realSqlDirectory, DfOutsideSqlLocation sqlLocation) {
        final List<File> sqlFileList = createSqlFileGetter().getSqlFileList(realSqlDirectory);
        final List<DfOutsideSqlFile> outsideSqlList = new ArrayList<DfOutsideSqlFile>();
        for (File sqlFile : sqlFileList) {
            outsideSqlList.add(new DfOutsideSqlFile(sqlFile, sqlLocation));
        }
        return outsideSqlList;
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

    protected boolean containsSrcMainJava(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.containsSrcMainJava(sqlDirectory);
    }

    protected String replaceSrcMainJavaToSrcMainResources(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.replaceSrcMainJavaToSrcMainResources(sqlDirectory);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public void suppressDirectoryCheck() {
        _suppressDirectoryCheck = true;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return getProperties().getOutsideSqlProperties();
    }
}
