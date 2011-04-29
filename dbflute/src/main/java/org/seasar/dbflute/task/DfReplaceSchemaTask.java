package org.seasar.dbflute.task;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfCreateSchemaFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAbstractSchemaTaskFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAlterSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfCreateSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfAlterCheckProcess.CoreProcessPlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaProcess.CreatingDataSourcePlayer;
import org.seasar.dbflute.logic.replaceschema.process.DfLoadDataProcess;
import org.seasar.dbflute.logic.replaceschema.process.DfTakeFinallyProcess;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
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
    protected boolean _lazyConnection = false;
    protected DfReplaceSchemaFinalInfo _replaceSchemaFinalInfo;
    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfLoadDataFinalInfo _loadDataFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;
    protected DfAlterSchemaFinalInfo _alterSchemaFinalInfo;

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
        if (isAlterCheck()) {
            processAlterCheck();
        } else {
            processMain();
        }
    }

    protected boolean isAlterCheck() {
        if (hasAlterNGMark()) {
            _log.info("...Ignoring AlterCheck by alter NG mark");
            return false;
        } else {
            return hasAlterSqlResource();
        }
    }

    protected void processMain() {
        executeCoreProcess();
    }

    protected void processAlterCheck() {
        final DfAlterCheckProcess process = DfAlterCheckProcess.createAsMain(getDataSource(), new CoreProcessPlayer() {
            public void play() {
                executeCoreProcess();
            }
        });
        try {
            _alterSchemaFinalInfo = process.execute();
            if (_alterSchemaFinalInfo.isFailure()) {
                throwAlterCheckAlterSqlFailureException(_alterSchemaFinalInfo);
            }
        } finally {
            // because the alter check process
            // may output alter NG mark file
            try {
                refreshResources();
            } catch (RuntimeException continued) {
                _log.warn("*Failed to refresh resources: " + continued.getMessage());
            }
        }
    }

    protected void throwAlterCheckAlterSqlFailureException(DfAlterSchemaFinalInfo finalInfo) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to execute the alter SQL statements.");
        br.addItem("Advice");
        br.addElement("Fix the mistakes of the alter SQL.");
        br.addElement("Look at the final info in the log for DBFlute task.");
        br.addItem("Message");
        br.addElement(finalInfo.getResultMessage());
        String msg = br.buildExceptionMessage();
        throw new DfAlterCheckAlterSqlFailureException(msg);
    }

    // ===================================================================================
    //                                                                        Core Process
    //                                                                        ============
    protected void executeCoreProcess() {
        createSchema();
        loadData();
        takeFinally();
    }

    protected void createSchema() {
        final DfCreateSchemaProcess process = DfCreateSchemaProcess.createAsCore(new CreatingDataSourcePlayer() {
            public DataSource callbackGetDataSource() {
                return getDataSource();
            }

            public void callbackSetupDataSource() throws SQLException {
                setupDataSource();
            }
        }, _lazyConnection);
        _createSchemaFinalInfo = process.execute();
    }

    protected void loadData() {
        final DfLoadDataProcess process = DfLoadDataProcess.createAsCore(getDataSource());
        _loadDataFinalInfo = process.execute();
        final RuntimeException loadEx = _loadDataFinalInfo.getLoadEx();
        if (loadEx != null) { // high priority exception
            throw loadEx;
        }
    }

    protected void takeFinally() {
        final DfTakeFinallyProcess process = DfTakeFinallyProcess.createAsCore(getDataSource());
        _takeFinallyFinalInfo = process.execute();
        final DfTakeFinallyAssertionFailureException assertionEx = _takeFinallyFinalInfo.getAssertionEx();
        if (assertionEx != null) { // high priority exception
            throw assertionEx;
        }
    }

    // ===================================================================================
    //                                                                      Schema Failure
    //                                                                      ==============
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

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    public String getFinalInformation() {
        return buildReplaceSchemaFinalMessage(getReplaceSchemaFinalInfo()); // The argument cannot be null!
    }

    protected DfReplaceSchemaFinalInfo getReplaceSchemaFinalInfo() {
        if (_replaceSchemaFinalInfo != null) {
            return _replaceSchemaFinalInfo;
        }
        _replaceSchemaFinalInfo = createReplaceSchemaFinalInfo();
        return _replaceSchemaFinalInfo;
    }

    protected DfReplaceSchemaFinalInfo createReplaceSchemaFinalInfo() {
        return new DfReplaceSchemaFinalInfo(_createSchemaFinalInfo, _loadDataFinalInfo, _takeFinallyFinalInfo,
                _alterSchemaFinalInfo);
    }

    protected String buildReplaceSchemaFinalMessage(DfReplaceSchemaFinalInfo replaceSchemaFinalInfo) {
        if (replaceSchemaFinalInfo == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstDone = false;

        // Alter Schema
        boolean alterFailure = false;
        {
            final DfAlterSchemaFinalInfo alterSchemaFinalInfo = replaceSchemaFinalInfo.getAlterSchemaFinalInfo();
            if (alterSchemaFinalInfo != null && alterSchemaFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, alterSchemaFinalInfo);
                alterFailure = alterSchemaFinalInfo.isFailure();
            }
        }

        // Create Schema
        {
            final DfCreateSchemaFinalInfo createSchemaFinalInfo = replaceSchemaFinalInfo.getCreateSchemaFinalInfo();
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

        // Load Data
        {
            final DfLoadDataFinalInfo loadDataFinalInfo = replaceSchemaFinalInfo.getLoadDataFinalInfo();
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

        // Take Finally
        {
            final DfTakeFinallyFinalInfo takeFinallyFinalInfo = replaceSchemaFinalInfo.getTakeFinallyFinalInfo();
            if (takeFinallyFinalInfo != null && takeFinallyFinalInfo.isValidInfo()) {
                if (!alterFailure || takeFinallyFinalInfo.isFailure()) {
                    if (firstDone) {
                        sb.append(ln()).append(ln());
                    }
                    firstDone = true;
                    buildSchemaTaskContents(sb, takeFinallyFinalInfo);
                }
            }
        }

        if (replaceSchemaFinalInfo.hasFailure()) {
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

    public boolean hasAlterNGMark() {
        return getReplaceSchemaProperties().hasMigrationAlterNGMark();
    }

    public boolean hasAlterSqlResource() {
        return getReplaceSchemaProperties().hasMigrationAlterSqlResource();
    }
}
