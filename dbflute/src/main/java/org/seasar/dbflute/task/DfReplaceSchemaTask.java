package org.seasar.dbflute.task;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfCreateSchemaFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyFailureException;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAbstractSchemaTaskFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfCreateSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfReplaceSchemaFinalInfo;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfTakeFinallyFinalInfo;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaMain;
import org.seasar.dbflute.logic.replaceschema.process.DfLoadDataMain;
import org.seasar.dbflute.logic.replaceschema.process.DfTakeFinallyMain;
import org.seasar.dbflute.logic.replaceschema.process.DfCreateSchemaMain.CreatingDataSourcePlayer;
import org.seasar.dbflute.task.bs.DfAbstractTask;

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
    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfLoadDataFinalInfo _loadDataFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;
    protected DfReplaceSchemaFinalInfo _replaceSchemaFinalInfo;

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
        createSchema();
        loadData();
        takeFinally();
        handleSchemaFailure();
    }

    protected void createSchema() {
        final DfCreateSchemaMain main = DfCreateSchemaMain.createAsCore(new CreatingDataSourcePlayer() {
            public DataSource callbackGetDataSource() {
                return getDataSource();
            }

            public void callbackSetupDataSource() throws SQLException {
                setupDataSource();
            }
        }, _lazyConnection);
        _createSchemaFinalInfo = main.execute();
    }

    protected void loadData() {
        final DfLoadDataMain main = DfLoadDataMain.createAsCore(getDataSource());
        _loadDataFinalInfo = main.execute();
        final RuntimeException loadEx = _loadDataFinalInfo.getLoadEx();
        if (loadEx != null) { // high priority exception
            throw loadEx;
        }
    }

    protected void takeFinally() {
        final DfTakeFinallyMain main = DfTakeFinallyMain.createAsCore(getDataSource());
        _takeFinallyFinalInfo = main.execute();
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
        return new DfReplaceSchemaFinalInfo(_createSchemaFinalInfo, _loadDataFinalInfo, _takeFinallyFinalInfo);
    }

    protected String buildReplaceSchemaFinalMessage(DfReplaceSchemaFinalInfo replaceSchemaFinalInfo) {
        if (replaceSchemaFinalInfo == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean firstDone = false;

        // Create Schema
        {
            final DfCreateSchemaFinalInfo createSchemaFinalInfo = replaceSchemaFinalInfo.getCreateSchemaFinalInfo();
            if (createSchemaFinalInfo != null && createSchemaFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, createSchemaFinalInfo);
            }
        }

        // Load Data
        {
            final DfLoadDataFinalInfo loadDataFinalInfo = replaceSchemaFinalInfo.getLoadDataFinalInfo();
            if (loadDataFinalInfo != null && loadDataFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, loadDataFinalInfo);
            }
        }

        // Take Finally
        {
            final DfTakeFinallyFinalInfo takeFinallyFinalInfo = replaceSchemaFinalInfo.getTakeFinallyFinalInfo();
            if (takeFinallyFinalInfo != null && takeFinallyFinalInfo.isValidInfo()) {
                if (firstDone) {
                    sb.append(ln()).append(ln());
                }
                firstDone = true;
                buildSchemaTaskContents(sb, takeFinallyFinalInfo);
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
}
