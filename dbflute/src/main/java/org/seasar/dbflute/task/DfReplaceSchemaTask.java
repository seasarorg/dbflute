package org.seasar.dbflute.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfCreateSchemaFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyFailureException;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAbstractSchemaTaskFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAlterCheckFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfCreateSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess.CoreProcessPlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfArrangeBeforeRepsProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess.CreatingDataSourcePlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfLoadDataProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfTakeFinallyProcess;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.DfDBFluteTaskStatus.TaskType;
import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfReplaceSchemaTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfReplaceSchemaTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _lazyConnection;
    protected DfReplaceSchemaFinalInfo _replaceSchemaFinalInfo;
    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfLoadDataFinalInfo _loadDataFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;
    protected DfAlterCheckFinalInfo _alterCheckFinalInfo;
    protected boolean _cancelled;

    // ===================================================================================
    //                                                                           Beginning
    //                                                                           =========
    @Override
    protected void begin() {
        _log.info("+------------------------------------------+");
        _log.info("|                                          |");
        _log.info("|              ReplaceSchema               |");
        _log.info("|                                          |");
        _log.info("+------------------------------------------+");
        DfDBFluteTaskStatus.getInstance().setTaskType(TaskType.ReplaceSchema);
    }

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                         Change User
    //                                                                         ===========
    @Override
    protected void setupDataSource() throws SQLException {
        try {
            super.setupDataSource();
            getDataSource().getConnection(); // check
        } catch (SQLException e) {
            setupLazyConnection(e);
        }
    }

    protected void setupLazyConnection(SQLException e) throws SQLException {
        if (_lazyConnection) { // already lazy
            throw e;
        }
        String msg = e.getMessage();
        if (msg.length() > 50) {
            msg = msg.substring(0, 47) + "...";
        }
        _log.info("...Being a lazy connection: " + msg);
        destroyDataSource();
        _lazyConnection = true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        // TODO jflute impl
        //final boolean letsGo = waitBeforeReps();
        //if (!letsGo) {
        //    _log.info("*The execution of ReplaceSchema was cancelled.");
        //    _cancelled = true;
        //    return;
        //}
        arrangeBeforeReps();
        if (isAlterCheck()) {
            processAlterCheck();
        } else {
            processMain();
        }
    }

    protected boolean isAlterCheck() {
        return hasMigrationSavePreviousMark() || hasMigrationAlterSqlResource();
    }

    protected void processAlterCheck() {
        doProcessAlterCheck();
    }

    protected void doProcessAlterCheck() {
        final DfAlterCheckProcess process = createAlterCheckProcess();
        try {
            if (hasMigrationSavePreviousMark()) {
                _alterCheckFinalInfo = process.savePrevious();
            } else { // has alter-SQL resources
                _alterCheckFinalInfo = process.checkAlter();
            }
            _alterCheckFinalInfo.throwAlterCheckExceptionIfExists();
        } finally {
            // because the alter check process
            // may output alter NG mark file
            refreshResources();
        }
    }

    protected DfAlterCheckProcess createAlterCheckProcess() {
        return DfAlterCheckProcess.createAsMain(getDataSource(), new CoreProcessPlayer() {
            public void play(String sqlRootDirectory) {
                executeCoreProcess(sqlRootDirectory);
            }
        });
    }

    protected void processMain() {
        executeCoreProcess(getPlaySqlDir());
    }

    // ===================================================================================
    //                                                                        Core Process
    //                                                                        ============
    protected void executeCoreProcess(String sqlRootDir) {
        doExecuteCoreProcess(sqlRootDir);
    }

    protected void doExecuteCoreProcess(String sqlRootDir) {
        try {
            createSchema(sqlRootDir);
            loadData(sqlRootDir);
            takeFinally(sqlRootDir);
        } finally {
            setupReplaceSchemaFinalInfo();
        }
        handleSchemaContinuedFailure();
    }

    protected void createSchema(String sqlRootDir) {
        final DfCreateSchemaProcess process = createCreateSchemaProcess(sqlRootDir);
        _createSchemaFinalInfo = process.execute();
        final SQLFailureException breakCause = _createSchemaFinalInfo.getBreakCause();
        if (breakCause != null) { // high priority exception
            throw breakCause;
        }
    }

    protected DfCreateSchemaProcess createCreateSchemaProcess(String sqlRootDir) {
        final CreatingDataSourcePlayer player = createCreatingDataSourcePlayer();
        return DfCreateSchemaProcess.createAsCore(sqlRootDir, player, _lazyConnection);
    }

    protected CreatingDataSourcePlayer createCreatingDataSourcePlayer() {
        return new CreatingDataSourcePlayer() {
            public DataSource callbackGetDataSource() {
                return getDataSource();
            }

            public void callbackSetupDataSource() throws SQLException {
                setupDataSource();
            }
        };
    }

    protected void loadData(String sqlRootDir) {
        final DfLoadDataProcess process = createLoadDataProcess(sqlRootDir);
        _loadDataFinalInfo = process.execute();
        final RuntimeException loadEx = _loadDataFinalInfo.getLoadEx();
        if (loadEx != null) { // high priority exception
            throw loadEx;
        }
    }

    protected DfLoadDataProcess createLoadDataProcess(String sqlRootDir) {
        return DfLoadDataProcess.createAsCore(sqlRootDir, getDataSource());
    }

    protected void takeFinally(String sqlRootDir) {
        final DfTakeFinallyProcess process = createTakeFinallyProcess(sqlRootDir);
        _takeFinallyFinalInfo = process.execute();
        final SQLFailureException breakCause = _takeFinallyFinalInfo.getBreakCause();
        if (breakCause != null) { // high priority exception
            throw breakCause;
        }
        final DfTakeFinallyAssertionFailureException assertionEx = _takeFinallyFinalInfo.getAssertionEx();
        if (assertionEx != null) { // high priority exception
            throw assertionEx;
        }
    }

    protected DfTakeFinallyProcess createTakeFinallyProcess(String sqlRootDir) {
        return DfTakeFinallyProcess.createAsCore(sqlRootDir, getDataSource());
    }

    protected void setupReplaceSchemaFinalInfo() {
        _replaceSchemaFinalInfo = createReplaceSchemaFinalInfo();
    }

    protected DfReplaceSchemaFinalInfo createReplaceSchemaFinalInfo() {
        return new DfReplaceSchemaFinalInfo(_createSchemaFinalInfo, _loadDataFinalInfo, _takeFinallyFinalInfo);
    }

    protected void handleSchemaContinuedFailure() { // means continued errors
        final DfReplaceSchemaFinalInfo finalInfo = _replaceSchemaFinalInfo;
        if (finalInfo.isCreateSchemaFailure()) {
            String msg = "Failed to create schema (Look at the final info)";
            throw new DfCreateSchemaFailureException(msg);
        }
        if (finalInfo.isTakeFinallyFailure()) {
            String msg = "Failed to take finally (Look at the final info)";
            throw new DfTakeFinallyFailureException(msg);
        }
    }

    // ===================================================================================
    //                                                           Wait before ReplaceSchema 
    //                                                           =========================
    protected boolean waitBeforeReps() {
        _log.info("...Waiting for your GO SIGN from stdin before ReplaceSchema:");
        systemOutPrintLn("/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        systemOutPrintLn(getDatabaseProperties().getDatabaseUrl());
        systemOutPrintLn("- - - - - - - - - -/");
        systemOutPrintLn("(input on your console)");
        systemOutPrint("The schema will be initialized. Are you ready? (y or n): ");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            final String line = br.readLine();
            return line != null && "y".equals(line);
        } catch (IOException e) {
            String msg = "Failed to read system input.";
            throw new IllegalStateException(msg, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected void systemOutPrint(Object msg) {
        System.out.print(msg);
    }

    protected void systemOutPrintLn(Object msg) {
        System.out.println(msg);
    }

    // ===================================================================================
    //                                                        Arrange before ReplaceSchema
    //                                                        ============================
    protected void arrangeBeforeReps() {
        final DfArrangeBeforeRepsProcess process = new DfArrangeBeforeRepsProcess();
        process.arrangeBeforeReps();
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    @Override
    public String getFinalInformation() {
        return buildReplaceSchemaFinalMessage();
    }

    protected String buildReplaceSchemaFinalMessage() {
        final StringBuilder sb = new StringBuilder();
        if (_cancelled) {
            sb.append("    * * * * * * *").append(ln());
            sb.append("    * Cancelled *").append(ln());
            sb.append("    * * * * * * *");
            return sb.toString();
        }
        final DfReplaceSchemaFinalInfo finalInfo = _replaceSchemaFinalInfo; // null allowed
        boolean firstDone = false;

        // AlterSchema
        boolean alterFailure = false;
        {
            final DfAlterCheckFinalInfo alterCheckFinalInfo = _alterCheckFinalInfo;
            if (alterCheckFinalInfo != null && alterCheckFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, alterCheckFinalInfo);
                alterFailure = alterCheckFinalInfo.isFailure();
            }
        }

        // CreateSchema
        if (finalInfo != null) {
            final DfCreateSchemaFinalInfo createSchemaFinalInfo = finalInfo.getCreateSchemaFinalInfo();
            if (createSchemaFinalInfo != null && createSchemaFinalInfo.isValidInfo()) {
                if (!alterFailure || createSchemaFinalInfo.isFailure()) {
                    if (firstDone) {
                        sb.append(ln()).append(ln());
                    }
                    firstDone = true;
                    buildSchemaTaskContents(sb, createSchemaFinalInfo);
                }
            }
        }

        // LoadData
        if (finalInfo != null) {
            final DfLoadDataFinalInfo loadDataFinalInfo = finalInfo.getLoadDataFinalInfo();
            if (loadDataFinalInfo != null && loadDataFinalInfo.isValidInfo()) {
                if (!alterFailure || loadDataFinalInfo.isFailure()) {
                    if (firstDone) {
                        sb.append(ln()).append(ln());
                    }
                    firstDone = true;
                    buildSchemaTaskContents(sb, loadDataFinalInfo);
                }
            }
        }

        // TakeFinally
        boolean assertionFailure = false;
        if (finalInfo != null) {
            final DfTakeFinallyFinalInfo takeFinallyFinalInfo = finalInfo.getTakeFinallyFinalInfo();
            if (takeFinallyFinalInfo != null) {
                assertionFailure = (takeFinallyFinalInfo.getAssertionEx() != null);
                if (takeFinallyFinalInfo.isValidInfo()) {
                    if (!alterFailure || takeFinallyFinalInfo.isFailure()) {
                        if (firstDone) {
                            sb.append(ln()).append(ln());
                        }
                        firstDone = true;
                        buildSchemaTaskContents(sb, takeFinallyFinalInfo);
                    }
                }
            }
        }

        if (alterFailure) { // alter or create in AlterCheck
            sb.append(ln()).append("    * * * * * * * * * * *");
            sb.append(ln()).append("    * Migration Failure *");
            sb.append(ln()).append("    * * * * * * * * * * *");
        } else if (assertionFailure) { // assertion in normal time
            sb.append(ln()).append("    * * * * * * * * * * *");
            sb.append(ln()).append("    * Assertion Failure *");
            sb.append(ln()).append("    * * * * * * * * * * *");
        } else if (finalInfo != null && finalInfo.hasFailure()) { // as default
            sb.append(ln()).append("    * * * * * *");
            sb.append(ln()).append("    * Failure *");
            sb.append(ln()).append("    * * * * * *");
        }
        return sb.toString();
    }

    protected void buildSchemaTaskContents(StringBuilder sb, DfAbstractSchemaTaskFinalInfo finalInfo) {
        sb.append(" ").append(finalInfo.getResultMessage());
        final List<String> detailMessageList = finalInfo.getDetailMessageList();
        for (String detailMessage : detailMessageList) {
            sb.append(ln()).append("  ").append(detailMessage);
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getProperties().getReplaceSchemaProperties();
    }

    protected String getPlaySqlDir() {
        return getReplaceSchemaProperties().getPlaySqlDir();
    }

    public boolean hasMigrationAlterSqlResource() {
        return getReplaceSchemaProperties().hasMigrationAlterSqlResource();
    }

    public boolean hasMigrationSavePreviousMark() {
        return getReplaceSchemaProperties().hasMigrationSavePreviousMark();
    }
}
