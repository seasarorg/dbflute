package org.seasar.dbflute.task.replaceschema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfTakeFinallyTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfTakeFinallyTask.class);

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
            _log.info("{Take Finally Properties}");
            _log.info("environmentType   = " + getEnvironmentType());
            _log.info("loggingInsertSql  = " + getMyProperties().isLoggingInsertSql());
            _log.info("autoCommit        = " + getMyProperties().isAutoCommit());
            _log.info("rollbackOnly      = " + getMyProperties().isRollbackOnly());
            _log.info("errorContinue     = " + getMyProperties().isErrorContinue());
            _log.info("sqlFileEncoding   = " + getMyProperties().getSqlFileEncoding());
            _log.info("beforeTakeFinally = " + getMyProperties().getBeforeTakeFinally());
            _log.info("");
        }

        final DfRunnerInformation runInfo = createRunnerInformation();

        beforeTakeFinally();
        takeFinally(runInfo);
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

    protected String getEnvironmentType() {
        return getMyProperties().getEnvironmentType();
    }

    public boolean isLoggingInsertSql() {
        return getMyProperties().isLoggingInsertSql();
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    // --------------------------------------------
    //                          Before Take Finally
    //                          -------------------
    protected void beforeTakeFinally() {
        String processCommand = getMyProperties().getBeforeTakeFinally();
        if (processCommand == null) {
            return;
        }
        callbackProcess("beforeTakeFinally", processCommand);
    }

    // --------------------------------------------
    //                                 Take Finally
    //                                 ------------
    protected void takeFinally(DfRunnerInformation runInfo) {
        _log.info("* * * * * * * **");
        _log.info("*              *");
        _log.info("* Take Finally *");
        _log.info("*              *");
        _log.info("* * * * * * * **");
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.execute(getSqlFileRunner4TakeFinally(runInfo), getTakeFinallySqlFileList());
        _log.info("");
    }

    protected DfSqlFileRunner getSqlFileRunner4TakeFinally(final DfRunnerInformation runInfo) {
        return new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
            @Override
            protected boolean isSqlTrimAndRemoveLineSeparator() {
                return true;
            }

            @Override
            protected boolean isValidAssertSql() {
                return true;
            }
        };
    }

    protected List<File> getTakeFinallySqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getTakeFinallyNextSqlFileList());
        return fileList;
    }

    protected List<File> getTakeFinallyNextSqlFileList() {
        final String replaceSchemaSqlFileDirectoryName = getTakeFinallySqlFileDirectoryName();
        final File baseDir = new File(replaceSchemaSqlFileDirectoryName);
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(getTakeFinallySqlFileNameWithoutExt())) {
                    if (name.endsWith("." + getTakeFinallySqlFileExt())) {
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

    protected String getTakeFinallySqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getTakeFinallySqlFileNameWithoutExt() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        final String tmp = sqlFileName.substring(sqlFileName.lastIndexOf("/") + 1);
        return tmp.substring(0, tmp.lastIndexOf("."));
    }

    protected String getTakeFinallySqlFileExt() {
        final String sqlFileName = getMyProperties().getTakeFinallySqlFile();
        return sqlFileName.substring(sqlFileName.lastIndexOf(".") + 1);
    }

    // --------------------------------------------
    //                              Callback Helper
    //                              ---------------
    protected void callbackProcess(String timing, String processCommand) {
        _log.info("");
        _log.info("* * * * * * * * * **");
        _log.info("*                  *");
        _log.info("* Process Callback *");
        _log.info("*                  *");
        _log.info("* * * * * * * * * **");
        _log.info("[" + timing + "]: begin --> " + processCommand);
        final ProcessBuilder processBuilder = new ProcessBuilder(processCommand);
        final Process process;
        InputStream stdIn = null;
        InputStream errIn = null;
        try {
            process = processBuilder.start();
            stdIn = process.getInputStream();
            errIn = process.getErrorStream();
            showConsole(stdIn);
            showConsole(errIn);
            int ret = process.waitFor();
            _log.info("[" + timing + "]: end(" + ret + ") --> " + processCommand);
            _log.info("");
        } catch (IOException e) {
            String msg = "Process Callback failed to execute: process=" + processCommand;
            _log.warn(msg + " e.getMessage()=" + e.getMessage());
            // Because of continue.
            // throw new DfReplaceSchemaProcessCallbackException(msg, e);
        } catch (InterruptedException e) {
            String msg = "Process Callback failed to execute: process=" + processCommand;
            _log.warn(msg + " e.getMessage()=" + e.getMessage());
            // Because of continue.
            // throw new DfReplaceSchemaProcessCallbackException(msg, e);
        } finally {
            try {
                if (stdIn != null) {
                    stdIn.close();
                }
                if (errIn != null) {
                    errIn.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected void showConsole(InputStream ins) throws IOException {
        if (ins == null) {
            return;
        }
        // Use default encoding of the environment because of the console!
        final BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        final StringBuilder sb = new StringBuilder();
        String line = null;
        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            sb.append(line + getLineSeparator());
        }
        _log.info(sb.toString().trim());
    }

    public static class DfReplaceSchemaProcessCallbackException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DfReplaceSchemaProcessCallbackException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    protected String getLineSeparator() {
        return "\n";
    }
}
