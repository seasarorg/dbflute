/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfGeneratedClassPackageProperties;
import org.seasar.framework.util.StringUtil;

public class DfOutsideSqlTestTask extends DfInvokeSqlDirectoryTask {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfSql2EntityTask.class);

    @Override
    protected List<File> getSqlFileList() {
        final String sqlDirectory = getSqlDirectory();
        final DfSqlFileGetter getter = new DfSqlFileGetter();
        final List<File> sqlFileList = getter.getSqlFileList(sqlDirectory);
        if (!sqlDirectory.contains("src/main/java/")) {
            return sqlFileList;
        }
        final String srcMainResources = StringUtil.replace(sqlDirectory, "src/main/java/", "src/main/resources/");
        try {
            final List<File> resourcesSqlFileList = new DfSqlFileGetter().getSqlFileList(srcMainResources);
            sqlFileList.addAll(resourcesSqlFileList);
        } catch (Exception e) {
            _log.debug("Not found sql directory on resources: " + srcMainResources);
        }
        return sqlFileList;
    }

    @Override
    protected String getSqlDirectory() {
        final DfBuildProperties prop = DfBuildProperties.getInstance();
        final DfBasicProperties basicProp = prop.getBasicProperties();
        final DfGeneratedClassPackageProperties packageProp = prop.getGeneratedClassPackageProperties();
        final String javaDir = basicProp.getJavaDir_for_main();
        final String extendedDaoPackage = packageProp.getExtendedDaoPackage();
        return javaDir + "/" + extendedDaoPackage.replace('.', '/');
    }

    protected boolean isAutoCommit() {
        return false;
    }

    protected boolean isErrorContinue() {
        return false;
    }

    protected boolean isRollbackOnly() {
        return true;
    }
}
