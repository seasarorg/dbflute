package org.seasar.dbflute.task.replaceschema;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan.FireResult;
import org.seasar.dbflute.logic.factory.DfSchemaInitializerFactory;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.util.basic.DfStringUtil;

public class DfCreateSchemaTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCreateSchemaTask.class);

    protected static final String LOG_PATH = "./log/create-schema.log";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean validTaskEndInformation = true;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("");
        _log.info("{Replace Schema Properties}");
        _log.info("autoCommit        = " + getMyProperties().isAutoCommit());
        _log.info("rollbackOnly      = " + getMyProperties().isRollbackOnly());
        _log.info("errorContinue     = " + getMyProperties().isErrorContinue());
        _log.info("sqlFileEncoding   = " + getMyProperties().getSqlFileEncoding());
        initializeSchema();

        final DfRunnerInformation runInfo = createRunnerInformation();
        createSchema(runInfo);
    }

    @Override
    protected boolean isValidTaskEndInformation() {
        return validTaskEndInformation;
    }

    // --------------------------------------------
    //                            Initialize Schema
    //                            -----------------
    protected void initializeSchema() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Initialize Schema *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final DfSchemaInitializer initializer = createSchemaInitializer(false);
        if (initializer != null) {
            initializer.initializeSchema();
        }
        _log.info("");
        initializeSchemaOnceMore();
    }

    protected void initializeSchemaOnceMore() {
        final String schema = getMyProperties().getOnceMoreDropDefinitionSchema();
        if (schema == null || schema.trim().length() == 0) {
            return;
        }
        // /= = = = = = = = = = = = = = = = = 
        // Unsupported at MySQL and SQLServer
        // = = = = = = = = = =/
        if (getBasicProperties().isDatabaseMySQL() || getBasicProperties().isDatabaseSqlServer()) {
            String msg = "OnceMoreDropDefinitionSchema is unsupported at MySQL and SQLServer!";
            throw new UnsupportedOperationException(msg);
        }
        _log.info("* * * * * * * * * * * * * * * *");
        _log.info("*                             *");
        _log.info("* Initialize Schema Once More *");
        _log.info("*                             *");
        _log.info("* * * * * * * * * * * * * * * *");
        final DfSchemaInitializer initializer = createSchemaInitializer(true);
        if (initializer != null) {
            initializer.initializeSchema();
        }
        _log.info("");
    }

    protected DfSchemaInitializer createSchemaInitializer(boolean onceMore) {
        final DfSchemaInitializerFactory factory = createSchemaInitializerFactory(onceMore);
        return factory.createSchemaInitializer();
    }

    protected DfSchemaInitializerFactory createSchemaInitializerFactory(boolean onceMore) {
        return new DfSchemaInitializerFactory(getDataSource(), getBasicProperties(), getMyProperties(), onceMore);
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
        final FireResult result = fireMan.execute(getSqlFileRunner(runInfo), getReplaceSchemaSqlFileList());
        try {
            dumpFireResult(result);
        } catch (Throwable ignored) {
            _log.info("Failed to dump create-schema result: " + result, ignored);
        }
        if (_log.isInfoEnabled()) {
            _log.info("");
        }
    }

    protected void dumpFireResult(FireResult result) {
        final File file = new File(LOG_PATH);
        if (file.exists()) {
            file.delete();
        }
        final String resultMessage = result.getResultMessage();
        if (resultMessage == null || resultMessage.trim().length() == 0) {
            return; // nothing to dump!
        }
        BufferedWriter bw = null;
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            bw.write(resultMessage + getLineSeparator() + result.isExistsError());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected DfSqlFileRunner getSqlFileRunner(final DfRunnerInformation runInfo) {
        final DfReplaceSchemaProperties prop = getMyProperties();
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected String filterSql(String sql) {
                sql = super.filterSql(sql);
                sql = prop.resolveFilterVariablesIfNeeds(sql);
                return sql;
            }

            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return true;
            }

            @Override
            protected boolean isHandlingCommentOnLineSeparator() {
                return true;
            }

            @Override
            protected String getTerminater4Tool() {
                return resolveTerminater4Tool();
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

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this.validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
        ;
    }
}
