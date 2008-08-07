package org.seasar.dbflute.task.replaceschema;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSqlServer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfCreateSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCreateSchemaTask.class);

    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        if (_log.isInfoEnabled()) {
            _log.info("");
            _log.info("{Create Schema Properties}");
            _log.info("autoCommit        = " + getMyProperties().isAutoCommit());
            _log.info("rollbackOnly      = " + getMyProperties().isRollbackOnly());
            _log.info("errorContinue     = " + getMyProperties().isErrorContinue());
            _log.info("sqlFileEncoding   = " + getMyProperties().getSqlFileEncoding());
            _log.info("");
        }
        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        createSchema(runInfo);
    }

    // --------------------------------------------
    //                            Initialize Schema
    //                            -----------------
    protected void initializeSchema() {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * * * * *");
            _log.info("*                   *");
            _log.info("* Initialize Schema *");
            _log.info("*                   *");
            _log.info("* * * * * * * * * * *");
        }
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        final DfSchemaInitializer initializer;
        if (basicProperties.isDatabaseMySQL()) {
            initializer = createSchemaInitializerMySQL();
        } else if (basicProperties.isDatabaseSqlServer()) {
            initializer = createSchemaInitializerSqlServer();
        } else {
            initializer = createSchemaInitializerJdbc();
        }
        if (initializer != null) {
            initializer.initializeSchema();
        }
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected DfSchemaInitializer createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSqlServer() {
        final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
        initializer.setDataSource(getDataSource());
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerJdbc() {
        final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
        initializer.setDataSource(getDataSource());
        initializer.setSchema(getBasicProperties().getDatabaseSchema());
        return initializer;
    }

    // --------------------------------------------
    //                                Create Schema
    //                                -------------
    protected DfRunnerInformation createRunnerInformation() {
        final DfRunnerInformation runInfo = new DfRunnerInformation();
        runInfo.setDriver(_driver);
        runInfo.setUrl(_url);
        runInfo.setUser(_userId);
        runInfo.setPassword(_password);
        runInfo.setEncoding(getReplaceSchemaSqlFileEncoding());
        runInfo.setAutoCommit(getMyProperties().isAutoCommit());
        runInfo.setErrorContinue(getMyProperties().isErrorContinue());
        runInfo.setRollbackOnly(getMyProperties().isRollbackOnly());
        return runInfo;
    }

    protected String getReplaceSchemaSqlFileEncoding() {
        return getMyProperties().getSqlFileEncoding();
    }

    protected void createSchema(DfRunnerInformation runInfo) {
        if (_log.isInfoEnabled()) {
            _log.info("* * * * * * * * *");
            _log.info("*               *");
            _log.info("* Create Schema *");
            _log.info("*               *");
            _log.info("* * * * * * * * *");
        }
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return true;
            }
        };
    }

    protected List<File> getReplaceSchemaSqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getReplaceSchemaNextSqlFileList());
        return fileList;
    }

    protected List<File> getReplaceSchemaNextSqlFileList() {
        final String replaceSchemaSqlFileDirectoryName = getReplaceSchemaSqlFileDirectoryName();
        final File baseDir = new File(replaceSchemaSqlFileDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(getReplaceSchemaSqlFileNameWithoutExt())) {
                    if (name.endsWith("." + getReplaceSchemaSqlFileExt())) {
                        return true;
                    }
                }
                return false;
            }
        };

        // Order by FileName Asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return new ArrayList<File>();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = replaceSchemaSqlFileDirectoryName + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getReplaceSchemaSqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getReplaceSchemaSqlFileNameWithoutExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        final String tmp = sqlFileName.substring(sqlFileName.lastIndexOf("/") + 1);
        return tmp.substring(0, tmp.lastIndexOf("."));
    }

    protected String getReplaceSchemaSqlFileExt() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(sqlFileName.lastIndexOf(".") + 1);
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

}
