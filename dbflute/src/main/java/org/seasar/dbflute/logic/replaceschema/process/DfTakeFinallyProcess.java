package org.seasar.dbflute.logic.replaceschema.process;

import java.io.File;
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
    protected final String _sqlRootDir;
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;

    protected Timestamp _beforeTimestamp; // is set through its property
    protected DfSqlFileFireResult _takeFinallyFireResult;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfTakeFinallyProcess(String sqlRootDir, DataSource dataSource, UnifiedSchema mainSchema) {
        _sqlRootDir = sqlRootDir;
        _dataSource = dataSource;
        _mainSchema = mainSchema;
    }

    public static DfTakeFinallyProcess createAsCore(String sqlRootDir, DataSource dataSource) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfTakeFinallyProcess(sqlRootDir, dataSource, mainSchema);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public DfTakeFinallyFinalInfo execute() {
        final DfRunnerInformation runInfo = createRunnerInformation();
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
        fileList.addAll(getReplaceSchemaProperties().getTakeFinallySqlFileList(_sqlRootDir));
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
