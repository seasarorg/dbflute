package org.seasar.dbflute.logic.replaceschema.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerDispatcher;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.replaceschema.dataassert.DfDataAssertHandler;
import org.seasar.dbflute.logic.replaceschema.dataassert.DfDataAssertProvider;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandler;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.factory.DfSequenceHandlerFactory;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfTakeFinallyProcess extends DfAbstractReplaceSchemaProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfTakeFinallyProcess.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;

    protected Timestamp _beforeTimestamp; // is set through its property
    protected DfSqlFileFireResult _takeFinallyFireResult;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfTakeFinallyProcess(DataSource dataSource, UnifiedSchema mainSchema) {
        _dataSource = dataSource;
        _mainSchema = mainSchema;
    }

    public static DfTakeFinallyProcess createAsCore(DataSource dataSource) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfTakeFinallyProcess(dataSource, mainSchema);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public DfTakeFinallyFinalInfo execute() {
        final DfRunnerInformation runInfo = createRunnerInformation();

        beforeTakeFinally();
        DfSqlFileFireResult fireResult = null;
        DfTakeFinallyAssertionFailureException assertionEx = null;
        try {
            fireResult = takeFinally(runInfo);
        } catch (DfTakeFinallyAssertionFailureException e) {
            _takeFinallyFireResult = new DfSqlFileFireResult();
            _takeFinallyFireResult.setExistsError(true);
            _takeFinallyFireResult.setResultMessage("{Take Finally}: *asserted");
            final StringBuilder sb = new StringBuilder();
            sb.append(" >> ").append(DfTypeUtil.toClassTitle(e));
            sb.append(ln()).append(" (Look at the exception message: console or dbflute.log)");
            _takeFinallyFireResult.setDetailMessage(sb.toString());
            assertionEx = e;
        }
        final DfTakeFinallyFinalInfo finalInfo = createFinalInfo(fireResult, assertionEx);
        incrementSequenceToDataMax();
        return finalInfo;
    }

    // --------------------------------------------
    //                          Before Take Finally
    //                          -------------------
    protected void beforeTakeFinally() {
        String processCommand = getReplaceSchemaProperties().getBeforeTakeFinally();
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
        final DfReplaceSchemaProperties prop = getReplaceSchemaProperties();
        final DfSqlFileRunnerExecute runnerExecute = new DfSqlFileRunnerExecute(runInfo, _dataSource) {
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
                final String dataLoadingType = getReplaceSchemaProperties().getDataLoadingType();
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
        fileList.addAll(getReplaceSchemaProperties().getTakeFinallySqlFileList());
        fileList.addAll(getReplaceSchemaProperties().getAppcalitionTakeFinallySqlFileList());
        return fileList;
    }

    // --------------------------------------------
    //                           Increment Sequence
    //                           ------------------
    protected void incrementSequenceToDataMax() {
        if (!getReplaceSchemaProperties().isIncrementSequenceToDataMax()) {
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
        final DfSequenceHandlerFactory factory = new DfSequenceHandlerFactory(_dataSource, getDatabaseTypeFacadeProp(),
                getDatabaseProperties());
        final DfSequenceHandler sequenceHandler = factory.createSequenceHandler();
        if (sequenceHandler == null) {
            String databaseType = getDatabaseTypeFacadeProp().getTargetDatabase();
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

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    protected DfTakeFinallyFinalInfo createFinalInfo(DfSqlFileFireResult fireResult,
            DfTakeFinallyAssertionFailureException assertionEx) {
        final DfTakeFinallyFinalInfo finalInfo = new DfTakeFinallyFinalInfo();
        if (fireResult != null) {
            finalInfo.setResultMessage(fireResult.getResultMessage());
            final List<String> detailMessageList = extractDetailMessageList(fireResult);
            for (String detailMessage : detailMessageList) {
                finalInfo.addDetailMessage(detailMessage);
            }
            finalInfo.setFailure(fireResult.existsError());
        }
        finalInfo.setAssertionEx(assertionEx);
        return finalInfo;
    }
}
