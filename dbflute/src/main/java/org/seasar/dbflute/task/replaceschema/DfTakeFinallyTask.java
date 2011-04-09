package org.seasar.dbflute.task.replaceschema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfCreateSchemaFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyFailureException;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerDispatcher;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.replaceschema.dataassert.DfDataAssertHandler;
import org.seasar.dbflute.logic.replaceschema.dataassert.DfDataAssertProvider;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandler;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.factory.DfSequenceHandlerFactory;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

public class DfTakeFinallyTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfTakeFinallyTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Timestamp _beforeTimestamp; // is set through its property
    protected DfSqlFileFireResult _takeFinallyFireResult;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final DfRunnerInformation runInfo = createRunnerInformation();

        beforeTakeFinally();
        try {
            _takeFinallyFireResult = takeFinally(runInfo);
        } catch (DfTakeFinallyAssertionFailureException e) {
            _takeFinallyFireResult = new DfSqlFileFireResult();
            _takeFinallyFireResult.setExistsError(true);
            _takeFinallyFireResult.setResultMessage("{Take Finally}: *asserted");
            final StringBuilder sb = new StringBuilder();
            sb.append(" >> ").append(DfTypeUtil.toClassTitle(e));
            sb.append(ln()).append(" (Look at the exception message: console or dbflute.log)");
            _takeFinallyFireResult.setDetailMessage(sb.toString());
            throw e;
        }
        incrementSequenceToDataMax();
        handleSchemaFailure();
    }

    @Override
    protected long getTaskBeforeTimeMillis() {
        if (_beforeTimestamp != null) {
            return _beforeTimestamp.getTime();
        } else {
            return super.getTaskBeforeTimeMillis();
        }
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
        runInfo.setAutoCommit(true);
        runInfo.setErrorContinue(getMyProperties().isErrorContinue());
        runInfo.setRollbackOnly(false);
        return runInfo;
    }

    protected String getReplaceSchemaSqlFileEncoding() {
        return getMyProperties().getSqlFileEncoding();
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
    protected DfSqlFileFireResult takeFinally(DfRunnerInformation runInfo) {
        _log.info("");
        _log.info("* * * * * * * **");
        _log.info("*              *");
        _log.info("* Take Finally *");
        _log.info("*              *");
        _log.info("* * * * * * * **");
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.setExecutorName("Take Finally");
        return fireMan.fire(getSqlFileRunner4TakeFinally(runInfo), getTakeFinallySqlFileList());
    }

    protected DfSqlFileRunner getSqlFileRunner4TakeFinally(final DfRunnerInformation runInfo) {
        final DfReplaceSchemaProperties prop = getMyProperties();
        final DfSqlFileRunnerExecute runnerExecute = new DfSqlFileRunnerExecute(runInfo, getDataSource()) {
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
            protected boolean isDbCommentLine(String line) {
                final boolean commentLine = super.isDbCommentLine(line);
                if (commentLine) {
                    return commentLine;
                }
                // for irregular pattern
                return isDbCommentLineForIrregularPattern(line);
            }

            @Override
            protected String getTerminater4Tool() {
                return resolveTerminater4Tool();
            }
        };
        runnerExecute.setDispatcher(new DfSqlFileRunnerDispatcher() {
            public boolean dispatch(File sqlFile, Statement st, String sql) throws SQLException {
                final String dataLoadingType = getMyProperties().getDataLoadingType();
                final DfDataAssertProvider dataAssertProvider = new DfDataAssertProvider(dataLoadingType);
                final DfDataAssertHandler dataAssertHandler = dataAssertProvider.provideDataAssertHandler(sql);
                if (dataAssertHandler == null) {
                    return false;
                }
                dataAssertHandler.handle(sqlFile, st, sql);
                return true;
            }
        });
        return runnerExecute;
    }

    protected List<File> getTakeFinallySqlFileList() {
        final List<File> fileList = new ArrayList<File>();
        fileList.addAll(getTakeFinallyNextSqlFileList());
        fileList.addAll(getTakeFinallyNextSqlFileListAdditional());
        return fileList;
    }

    protected List<File> getTakeFinallyNextSqlFileList() {
        final String path = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        return doGetTakeFinallySqlFileList(path);
    }

    protected List<File> getTakeFinallyNextSqlFileListAdditional() {
        final List<File> fileList = new ArrayList<File>();
        final String path = getMyProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(path)) {
            return DfCollectionUtil.emptyList();
        }
        fileList.addAll(doGetTakeFinallySqlFileList(path));
        return fileList;
    }

    protected List<File> doGetTakeFinallySqlFileList(String directoryPath) {
        final File baseDir = new File(directoryPath);
        final String fileNameWithoutExt = getTakeFinallySqlFileNameWithoutExt();
        final String sqlFileExt = getTakeFinallySqlFileExt();
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith(fileNameWithoutExt) && name.endsWith("." + sqlFileExt)) {
                    return true;
                }
                return false;
            }
        };
        // order by FileName asc
        final Comparator<File> fileNameAscComparator = new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        final TreeSet<File> treeSet = new TreeSet<File>(fileNameAscComparator);

        final String[] targetList = baseDir.list(filter);
        if (targetList == null) {
            return DfCollectionUtil.emptyList();
        }
        for (String targetFileName : targetList) {
            final String targetFilePath = directoryPath + "/" + targetFileName;
            treeSet.add(new File(targetFilePath));
        }
        return new ArrayList<File>(treeSet);
    }

    protected String getTakeFinallySqlFileNameWithoutExt() {
        return getMyProperties().getTakeFinallySqlFileNameWithoutExt();
    }

    protected String getTakeFinallySqlFileExt() {
        return getMyProperties().getTakeFinallySqlFileExt();
    }

    // --------------------------------------------
    //                           Increment Sequence
    //                           ------------------
    protected void incrementSequenceToDataMax() {
        if (!getMyProperties().isIncrementSequenceToDataMax()) {
            return;
        }
        _log.info("");
        _log.info("* * * * * * * * * * **");
        _log.info("*                    *");
        _log.info("* Increment Sequence *");
        _log.info("*                    *");
        _log.info("* * * * * * * * * * **");
        final DfSequenceIdentityProperties sequenceProp = getProperties().getSequenceIdentityProperties();
        final Map<String, String> tableSequenceMap = sequenceProp.getTableSequenceMap();
        final DfSequenceHandlerFactory factory = new DfSequenceHandlerFactory(getDataSource(), getBasicProperties(),
                getDatabaseProperties());
        final DfSequenceHandler sequenceHandler = factory.createSequenceHandler();
        if (sequenceHandler == null) {
            String databaseType = getBasicProperties().getDatabaseType();
            String msg = "Unsupported isIncrementSequenceToDataMax at " + databaseType;
            throw new UnsupportedOperationException(msg);
        }
        sequenceHandler.incrementSequenceToDataMax(tableSequenceMap);
    }

    // --------------------------------------------
    //                               Schema Failure
    //                               --------------
    protected void handleSchemaFailure() { // means continued errors
        final DfReplaceSchemaFinalInfo finalInfo = getReplaceSchemaFinalInfo();
        if (finalInfo.isCreateSchemaFailure()) {
            String msg = "Failed to create schema (Look at the final info)";
            throw new DfCreateSchemaFailureException(msg);
        }
        if (finalInfo.isTakeFinallyFailure()) {
            String msg = "Failed to take finally (Look at the final info)";
            throw new DfTakeFinallyFailureException(msg);
        }
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
            sb.append(line + ln());
        }
        _log.info(sb.toString().trim());
    }

    public static class DfReplaceSchemaProcessCallbackException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DfReplaceSchemaProcessCallbackException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    @Override
    protected DfTakeFinallyFinalInfo getTakeFinallyFinalInfo() {
        return extractTakeFinallyFinalInfo(_takeFinallyFireResult);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBeforeTimestamp(String beforeTimestamp) {
        try {
            _beforeTimestamp = Timestamp.valueOf(beforeTimestamp);
        } catch (RuntimeException ignored) {
            _log.warn("Wrong beforeTimestampExpression: " + beforeTimestamp, ignored);
        }
    }
}
