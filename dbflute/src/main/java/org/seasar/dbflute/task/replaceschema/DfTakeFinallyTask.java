package org.seasar.dbflute.task.replaceschema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerDispatcher;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan.FireResult;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.dbflute.helper.token.line.impl.LineTokenImpl;
import org.seasar.dbflute.logic.factory.DfSequenceHandlerFactory;
import org.seasar.dbflute.logic.generate.dataassert.DfDataAssertHandler;
import org.seasar.dbflute.logic.generate.dataassert.DfDataAssertProvider;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandler;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
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
    protected Timestamp beforeTimestamp;

    /** The result of take-finally for final information. */
    protected FireResult takeFinallyResult;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        final DfRunnerInformation runInfo = createRunnerInformation();

        beforeTakeFinally();
        takeFinallyResult = takeFinally(runInfo);
        incrementSequenceToDataMax();
    }

    @Override
    protected long getTaskBeforeTimeMillis() {
        if (beforeTimestamp != null) {
            return beforeTimestamp.getTime();
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
    protected FireResult takeFinally(DfRunnerInformation runInfo) {
        _log.info("");
        _log.info("* * * * * * * **");
        _log.info("*              *");
        _log.info("* Take Finally *");
        _log.info("*              *");
        _log.info("* * * * * * * **");
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.setExecutorName("Take Finally");
        return fireMan.execute(getSqlFileRunner4TakeFinally(runInfo), getTakeFinallySqlFileList());
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

    // --------------------------------------------
    //                            Final Information
    //                            -----------------
    @Override
    protected String getFinalInformation() {
        return buildFinalInformation(takeFinallyResult); // The argument cannot be null!
    }

    /**
     * @param result The fire result for take-finally. (NotNull)
     * @return The final information. (Nullable)
     */
    protected String buildFinalInformation(FireResult result) {
        final File file = new File(DfCreateSchemaTask.LOG_PATH);
        if (!file.exists()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstDone = false;
        boolean failure = false;

        // Create Schema
        final CreateSchemaFinalInfo createSchemaFinalInfo = buildCreateSchemaFinalInfo();
        if (createSchemaFinalInfo != null) {
            if (firstDone) {
                sb.append(ln()).append(ln());
            } else {
                firstDone = true;
            }
            sb.append(createSchemaFinalInfo.getMessage());
            if (createSchemaFinalInfo.isFailure()) {
                failure = true;
            }
        }

        // Take Finally
        if (result != null) {
            if (firstDone) {
                sb.append(ln()).append(ln());
            } else {
                firstDone = true;
            }
            sb.append(" ").append(result.getResultMessage());
            final String detailMessage = result.getDetailMessage();
            if (detailMessage != null && detailMessage.trim().length() > 0) {
                final LineToken lineToken = new LineTokenImpl();
                final LineTokenizingOption lineTokenizingOption = new LineTokenizingOption();
                lineTokenizingOption.setDelimiter(ln());
                final List<String> tokenizedList = lineToken.tokenize(detailMessage, lineTokenizingOption);
                for (String tokenizedElement : tokenizedList) {
                    sb.append(ln()).append("  ").append(tokenizedElement);
                }
            }
            if (result.isExistsError()) {
                failure = true;
            }
        } else {
            failure = true; // means take-finally was not finished
        }

        if (failure) { // exists error anywhere
            sb.append(ln()).append("    * * * * * *");
            sb.append(ln()).append("    * Failure *");
            sb.append(ln()).append("    * * * * * *");
        }
        return sb.toString();
    }

    protected CreateSchemaFinalInfo buildCreateSchemaFinalInfo() {
        final File file = new File(DfCreateSchemaTask.LOG_PATH);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = null;
        try {
            final FileInputStream fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            // - - - - - - - - - - - -
            // line1: resultMessage
            // line2: existsError
            // line3-x: detailMessage
            // - - - - - - - - - - - -
            final String line = br.readLine();
            if (line == null) {
                return null;
            }
            final String line2 = br.readLine();
            final List<String> detailList = new ArrayList<String>();
            if (line2 != null) {
                while (true) {
                    String line3 = br.readLine();
                    if (line3 == null) {
                        break;
                    }
                    detailList.add(line3);
                }
            }

            final StringBuilder sb = new StringBuilder();
            sb.append(ln()).append(" ").append(line);
            for (String detail : detailList) {
                sb.append(ln()).append("  ").append(detail);
            }

            final CreateSchemaFinalInfo info = new CreateSchemaFinalInfo();
            info.setMessage(sb.toString());
            info.setFailure(isLine2True(line2));
            return info;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    // ignored
                }
            }
        }
    }

    protected boolean isLine2True(String line2) {
        return line2 != null && line2.trim().equalsIgnoreCase("true");
    }

    protected static class CreateSchemaFinalInfo {
        protected String _message;
        protected boolean _failure;

        public String getMessage() {
            return _message;
        }

        public void setMessage(String message) {
            this._message = message;
        }

        public boolean isFailure() {
            return _failure;
        }

        public void setFailure(boolean failure) {
            this._failure = failure;
        }
    }

    // *not used because loading data is not continued if it causes an error
    //protected LoadDataFinalInfo buildLoadDataFinalInfo() {
    //    final File file = new File(DfLoadDataTask.LOG_PATH);
    //    if (!file.exists()) {
    //        return null;
    //    }
    //    BufferedReader br = null;
    //    try {
    //        final FileInputStream fis = new FileInputStream(file);
    //        br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
    //
    //        final StringBuilder sb = new StringBuilder();
    //        int index = 0;
    //        while (true) {
    //            final String line = br.readLine();
    //            if (line == null) {
    //                break;
    //            }
    //            if (index == 0) { // title
    //                sb.append(" ");
    //            } else { // warning file
    //                sb.append(ln()).append("  ");
    //            }
    //            sb.append(line);
    //            ++index;
    //        }
    //        final LoadDataFinalInfo loadDataFinalInfo = new LoadDataFinalInfo();
    //        loadDataFinalInfo.setMessage(sb.toString());
    //        loadDataFinalInfo.setFailure(true); // loadDataFinalInfo has only error info
    //        return loadDataFinalInfo;
    //    } catch (UnsupportedEncodingException e) {
    //        throw new IllegalStateException(e);
    //    } catch (FileNotFoundException e) {
    //        throw new IllegalStateException(e);
    //    } catch (IOException e) {
    //        throw new IllegalStateException(e);
    //    } finally {
    //        if (br != null) {
    //            try {
    //                br.close();
    //            } catch (IOException ignored) {
    //            }
    //        }
    //        if (file.exists()) {
    //            boolean deleted = file.delete();
    //            if (!deleted) {
    //                // ignored
    //            }
    //        }
    //    }
    //}
    //
    //protected static class LoadDataFinalInfo {
    //    protected String _message;
    //    protected boolean _failure;
    //
    //    public String getMessage() {
    //        return _message;
    //    }
    //
    //    public void setMessage(String message) {
    //        this._message = message;
    //    }
    //
    //    public boolean isFailure() {
    //        return _failure;
    //    }
    //
    //    public void setFailure(boolean failure) {
    //        this._failure = failure;
    //    }
    //}

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBeforeTimestamp(String beforeTimestamp) {
        try {
            this.beforeTimestamp = Timestamp.valueOf(beforeTimestamp);
        } catch (RuntimeException ignored) {
            _log.warn("Wrong beforeTimestampExpression: " + beforeTimestamp, ignored);
        }
    }
}
