package org.seasar.dbflute.logic.replaceschema.process;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfAlterCheckAlterNGMarkFoundException;
import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;
import org.seasar.dbflute.exception.DfAlterCheckRollbackSchemaFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfNextPreviousDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlSerializer;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAlterSchemaFinalInfo;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfAlterCheckProcess extends DfAbstractReplaceSchemaProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfAlterCheckProcess.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;
    protected final CoreProcessPlayer _coreProcessPlayer;

    // -----------------------------------------------------
    //                                          Renamed File
    //                                          ------------
    protected final Map<File, File> _backupPreviousFileMap = new LinkedHashMap<File, File>();
    protected final Map<File, File> _deployedNextFileMap = new LinkedHashMap<File, File>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfAlterCheckProcess(DataSource dataSource, UnifiedSchema mainSchema, CoreProcessPlayer coreProcessPlayer) {
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _coreProcessPlayer = coreProcessPlayer;
    }

    public static DfAlterCheckProcess createAsMain(DataSource dataSource, CoreProcessPlayer coreProcessPlayer) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfAlterCheckProcess(dataSource, mainSchema, coreProcessPlayer);
    }

    public static interface CoreProcessPlayer {
        void play();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public DfAlterSchemaFinalInfo execute() {
        processReady();

        final DfAlterSchemaFinalInfo finalInfo = alterSchema();
        if (finalInfo.isFailure()) {
            return finalInfo;
        }
        replaceSchema();

        final DfSchemaDiff schemaDiff = schemaDiff();
        if (schemaDiff.hasDiff()) {
            processFailure(finalInfo, schemaDiff);
        } else {
            processSuccess();
        }

        // allowed not to be executed when processes before abort
        // for unanticipated accidents (not to delete previous files)
        processClose();

        return finalInfo;
    }

    // ===================================================================================
    //                                                                               Ready
    //                                                                               =====
    protected void processReady() {
        verifyPremise();

        // resources may remain if previous execution aborts
        clearTemporaryResource();
    }

    protected void verifyPremise() {
        if (hasAlterNGMark()) {
            throwAlterCheckAlterNGMarkFoundException();
        }
    }

    protected void throwAlterCheckAlterNGMarkFoundException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the alter-NG mark of AlterCheck.");
        br.addItem("Advice");
        setupFixedAdviceMessage(br);
        String msg = br.buildExceptionMessage();
        throw new DfAlterCheckAlterNGMarkFoundException(msg);
    }

    // ===================================================================================
    //                                                                         AlterSchema
    //                                                                         ===========
    protected DfAlterSchemaFinalInfo alterSchema() {
        _log.info("");
        _log.info("* * * * * * * * *");
        _log.info("*               *");
        _log.info("* Alter Schema  *");
        _log.info("*               *");
        _log.info("* * * * * * * * *");
        final DfAlterSchemaFinalInfo finalInfo = executeAlterSql();
        if (finalInfo.isFailure()) {
            markAlterNG();
            rollbackSchema();
            setupAlterCheckAlterSqlFailureException(finalInfo);
        } else {
            serializeAlteredSchema();
        }
        return finalInfo;
    }

    protected DfAlterSchemaFinalInfo executeAlterSql() {
        final List<File> alterSqlFileList = getMigrationAlterSqlFileList();
        final DfRunnerInformation runInfo = createRunnerInformation();
        final DfSqlFileFireMan fireMan = new DfSqlFileFireMan();
        fireMan.setExecutorName("Alter Schema");
        final DfSqlFileRunnerExecute runner = new DfSqlFileRunnerExecute(runInfo, _dataSource);
        final DfSqlFileFireResult result = fireMan.fire(runner, alterSqlFileList);
        return createFinalInfo(result);
    }

    protected void serializeAlteredSchema() {
        final DfSchemaXmlSerializer serializer = createSchemaXmlSerializer();
        serializer.serialize();
    }

    protected DfSchemaXmlSerializer createSchemaXmlSerializer() {
        final String schemaXml = getMigrationSchemaXml();
        final String diffResult = getMigrationDiffResult();
        return DfSchemaXmlSerializer.createAsManage(_dataSource, _mainSchema, schemaXml, diffResult);
    }

    protected void setupAlterCheckAlterSqlFailureException(DfAlterSchemaFinalInfo finalInfo) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to execute the alter SQL statements.");
        br.addItem("Advice");
        setupFixedAdviceMessage(br);
        br.addElement("Look at the final info in the log for DBFlute task.");
        br.addItem("Message");
        br.addElement(finalInfo.getResultMessage());
        String msg = br.buildExceptionMessage();
        finalInfo.setAlterSqlFailureEx(new DfAlterCheckAlterSqlFailureException(msg));
        finalInfo.setFailure(true);
    }

    // ===================================================================================
    //                                                                       ReplaceSchema
    //                                                                       =============
    protected void replaceSchema() {
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("*                 *");
        _log.info("* Replace Schema  *");
        _log.info("*                 *");
        _log.info("* * * * * * * * * *");
        backupPreviousResource();
        deployNextResource();
        try {
            _coreProcessPlayer.play();
        } catch (RuntimeException e) {
            rollbackSchema();
            throw e;
        }
    }

    protected void backupPreviousResource() {
        doBackupPreviousResource(getMigrationReplaceSchemaSqlFileList(), getReplaceSchemaSqlFileMap());
        doBackupPreviousResource(getMigrationTakeFinallySqlFileList(), getTakeFinallySqlFileMap());
    }

    protected void doBackupPreviousResource(List<File> migrationSqlFileList, Map<String, File> previousSqlFileMap) {
        final String previousDir = getMigrationTemporaryPreviousDirectory();
        new File(previousDir).mkdirs();
        for (File migrationSqlFile : migrationSqlFileList) {
            final File previousFile = previousSqlFileMap.get(migrationSqlFile.getName());
            if (previousFile != null) { // found overridden previous SQL file
                final File backupFile = new File(previousDir + "/" + previousFile.getName());
                _log.info("...Moving the previous file to backup: " + backupFile.getName());
                if (previousFile.renameTo(backupFile)) {
                    _backupPreviousFileMap.put(previousFile, backupFile);
                } else {
                    String msg = "Failed to rename (for backup) to " + backupFile;
                    throw new IllegalStateException(msg);
                }
            }
        }
    }

    protected void deployNextResource() {
        doDeployNextResource(getMigrationReplaceSchemaSqlFileList());
        doDeployNextResource(getMigrationTakeFinallySqlFileList());
    }

    protected void doDeployNextResource(List<File> migrationSqlFileList) {
        final String playSqlDir = getReplaceSchemaPlaySqlDirectory();
        for (File migrationSqlFile : migrationSqlFileList) {
            final File deployTo = new File(playSqlDir + "/" + migrationSqlFile.getName());
            _log.info("...Moving the next file to deployment: " + deployTo.getName());
            if (migrationSqlFile.renameTo(deployTo)) {
                _deployedNextFileMap.put(migrationSqlFile, deployTo);
            } else {
                String msg = "Failed to rename (for deployment) to " + deployTo;
                throw new IllegalStateException(msg);
            }
        }
    }

    // ===================================================================================
    //                                                                          SchemaDiff
    //                                                                          ==========
    protected DfSchemaDiff schemaDiff() {
        _log.info("");
        _log.info("* * * * * * * *");
        _log.info("*             *");
        _log.info("* Schema Diff *");
        _log.info("*             *");
        _log.info("* * * * * * * *");
        final DfSchemaXmlSerializer serializer = createSchemaXmlSerializer();
        serializer.serialize();
        return serializer.getSchemaDiff();
    }

    // ===================================================================================
    //                                                                       Failure Story
    //                                                                       =============
    protected void processFailure(DfAlterSchemaFinalInfo finalInfo, DfSchemaDiff schemaDiff) {
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("*                 *");
        _log.info("* Rollback Schema *");
        _log.info("*                 *");
        _log.info("* * * * * * * * * *");
        markAlterNG();
        rollbackSchema();
        handleAlterDiff(finalInfo, schemaDiff);
    }

    protected void markAlterNG() {
        final String ngMark = getMigrationAlterNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                _log.info("...Marking alter-NG: " + ngMark);
                markFile.createNewFile();
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for alter-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    // -----------------------------------------------------
    //                                      Roll-back Schema
    //                                      ----------------
    protected void rollbackSchema() {
        try {
            revertToPreviousResource();
            _coreProcessPlayer.play();
        } catch (RuntimeException e) {
            markPreviousNG();
            throwAlterCheckRollbackSchemaFailureException(e);
        }
    }

    protected void revertToPreviousResource() {
        for (Entry<File, File> entry : _deployedNextFileMap.entrySet()) {
            final File migration = entry.getKey();
            final File deployment = entry.getValue();
            _log.info("...Moving the next file back to migration: " + migration.getName());
            if (!deployment.renameTo(migration)) {
                String msg = "Failed to rename (for reversion) to " + migration;
                throw new IllegalStateException(msg);
            }
        }
        for (Entry<File, File> entry : _backupPreviousFileMap.entrySet()) {
            final File deployment = entry.getKey();
            final File backup = entry.getValue();
            _log.info("...Moving the previous file back to deployment: " + deployment.getName());
            if (!backup.renameTo(deployment)) {
                String msg = "Failed to rename (for reversion) to " + deployment;
                throw new IllegalStateException(msg);
            }
        }
    }

    protected void markPreviousNG() {
        final String ngMark = getMigrationPreviousNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                _log.info("...Marking previous-NG: " + ngMark);
                markFile.createNewFile();
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for previous-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void throwAlterCheckRollbackSchemaFailureException(RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to rollback to previous schema.");
        br.addItem("Advice");
        br.addElement("The function AlterCheck requires that previous schema is valid.");
        br.addElement("So you should fix the mistakes of the previous schema");
        br.addElement("and replace the schema to previous status by executing ReplaceSchema again.");
        br.addElement("And also delete the previous-NG mark file, which supresses AlterCheck process.");
        br.addElement("In doing so, you can execute AlterCheck again.");
        br.addItem("Exception");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfAlterCheckRollbackSchemaFailureException(msg, e);
    }

    // -----------------------------------------------------
    //                                    AlterDiff Handling
    //                                    ------------------
    protected void handleAlterDiff(DfAlterSchemaFinalInfo finalInfo, DfSchemaDiff schemaDiff) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the differences between alter SQL and create SQL.");
        br.addItem("Advice");
        setupFixedAdviceMessage(br);
        br.addElement("");
        br.addElement("You can confirm them at '" + getMigrationDiffResult() + "'.");
        br.addElement("The latest item is difference between previous schema and altered schema.");
        br.addItem("Diff Date");
        br.addElement(schemaDiff.getDiffDate());
        final DfNextPreviousDiff tableCountDiff = schemaDiff.getTableCount();
        if (tableCountDiff != null) {
            br.addItem("Table Count");
            br.addElement(tableCountDiff.getPrevious() + " to " + tableCountDiff.getNext());
        }
        final String msg = br.buildExceptionMessage();
        finalInfo.setDiffFoundEx(new DfAlterCheckDifferenceFoundException(msg));
        finalInfo.setFailure(true);
        finalInfo.addDetailMessage("x (found diff)");
    }

    public static void setupFixedAdviceMessage(ExceptionMessageBuilder br) {
        br.addElement("Make sure your alter SQL are correct");
        br.addElement("and delete the alter-NG mark file.");
        br.addElement("In doing so, you can execute AlterCheck again.");
    }

    // ===================================================================================
    //                                                                       Success Story
    //                                                                       =============
    protected void processSuccess() {
        _log.info("");
        _log.info("* * * * * * * * *");
        _log.info("*               *");
        _log.info("* Success Story *");
        _log.info("*               *");
        _log.info("* * * * * * * * *");
        deleteAlterNG();
        saveHistory();
        deleteDiffResult();
    }

    protected void deleteAlterNG() {
        deleteFileIfExists("alter-NG mark", getMigrationAlterNGMark());
    }

    protected void saveHistory() {
        final String currentDir = getHistoryCurrentDir();
        final List<File> alterSqlFileList = getMigrationAlterSqlFileList();
        _log.info("...Saving history to " + currentDir);
        for (File sqlFile : alterSqlFileList) {
            final File historyTo = new File(currentDir + "/" + sqlFile.getName());
            _log.info(" " + historyTo.getName());
            sqlFile.renameTo(historyTo); // no check here
        }
    }

    protected String getHistoryCurrentDir() {
        final String historyDir = getMigrationHistoryDirectory();
        final Date currentDate = new Date();
        final String middleDir = DfTypeUtil.toString(currentDate, "yyyy");
        mkdirsIfNotExists(new File(historyDir + "/" + middleDir));
        // e.g. history/2011/20110429_2247
        final String yyyyMMddHHmm = DfTypeUtil.toString(currentDate, "yyyyMMdd_HHmm");
        final String currentDir = historyDir + "/" + middleDir + "/" + yyyyMMddHHmm;
        mkdirsIfNotExists(new File(currentDir));
        return currentDir;
    }

    protected void mkdirsIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected void deleteDiffResult() {
        deleteFileIfExists("diff result file", getMigrationDiffResult());
    }

    // ===================================================================================
    //                                                                             Closing
    //                                                                             =======
    protected void processClose() {
        clearTemporaryResource();
    }

    protected void clearTemporaryResource() {
        deleteSchemaXml();
        deleteTmpPreviousDir();
        deleteTmpDir();
    }

    protected void deleteSchemaXml() {
        deleteFileIfExists("SchemaXML file", getMigrationSchemaXml());
    }

    protected void deleteTmpPreviousDir() {
        deleteFileIfExists("temporary previous directory", getMigrationTemporaryPreviousDirectory());
    }

    protected void deleteTmpDir() {
        deleteFileIfExists("temporary directory", getMigrationTemporaryDirectory());
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    protected DfAlterSchemaFinalInfo createFinalInfo(DfSqlFileFireResult fireResult) {
        final DfAlterSchemaFinalInfo finalInfo = new DfAlterSchemaFinalInfo();
        finalInfo.setResultMessage(fireResult.getResultMessage());
        final List<String> detailMessageList = extractDetailMessageList(fireResult);
        for (String detailMessage : detailMessageList) {
            finalInfo.addDetailMessage(detailMessage);
        }
        finalInfo.setFailure(fireResult.existsError());
        return finalInfo;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected void deleteFileIfExists(String title, String filePath) {
        final File theFile = new File(filePath);
        if (theFile.exists()) {
            if (theFile.isDirectory()) {
                // one level only deleted
                final File[] listFiles = theFile.listFiles(new FileFilter() {
                    public boolean accept(File element) {
                        return element.isFile();
                    }
                });
                if (listFiles != null && listFiles.length > 0) {
                    for (File file : listFiles) {
                        file.delete();
                    }
                }
            }
            _log.info("...Deleting the " + title + ": " + filePath);
            theFile.delete();
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    // -----------------------------------------------------
    //                                         ReplaceSchema
    //                                         -------------
    public String getReplaceSchemaPlaySqlDirectory() {
        return getReplaceSchemaProperties().getReplaceSchemaPlaySqlDirectory();
    }

    public Map<String, File> getReplaceSchemaSqlFileMap() { // without Application's
        return getReplaceSchemaProperties().getReplaceSchemaSqlFileMap();
    }

    public Map<String, File> getTakeFinallySqlFileMap() { // without Application's
        return getReplaceSchemaProperties().getTakeFinallySqlFileMap();
    }

    // -----------------------------------------------------
    //                                             Migration
    //                                             ---------
    public boolean hasAlterNGMark() {
        return getReplaceSchemaProperties().hasMigrationAlterNGMark();
    }

    public String getMigrationAlterNGMark() {
        return getReplaceSchemaProperties().getMigrationAlterNGMark();
    }

    public boolean hasMigrationAlterNGMark() {
        return getReplaceSchemaProperties().hasMigrationAlterNGMark();
    }

    public String getMigrationPreviousNGMark() {
        return getReplaceSchemaProperties().getMigrationPreviousNGMark();
    }

    public boolean hasMigrationPreviousNGMark() {
        return getReplaceSchemaProperties().hasMigrationPreviousNGMark();
    }

    public List<File> getMigrationAlterSqlFileList() {
        return getReplaceSchemaProperties().getMigrationAlterSqlFileList();
    }

    public List<File> getMigrationReplaceSchemaSqlFileList() {
        return getReplaceSchemaProperties().getMigrationReplaceSchemaSqlFileList();
    }

    public List<File> getMigrationTakeFinallySqlFileList() {
        return getReplaceSchemaProperties().getMigrationTakeFinallySqlFileList();
    }

    public boolean hasMigrationAlterSqlResource() {
        return getReplaceSchemaProperties().hasMigrationAlterSqlResource();
    }

    protected String getMigrationSchemaXml() {
        return getReplaceSchemaProperties().getMigrationSchemaXml();
    }

    protected String getMigrationDiffResult() {
        return getReplaceSchemaProperties().getMigrationDiffResult();
    }

    protected String getMigrationHistoryDirectory() {
        return getReplaceSchemaProperties().getMigrationHistoryDirectory();
    }

    public String getMigrationTemporaryDirectory() {
        return getReplaceSchemaProperties().getMigrationTemporaryDirectory();
    }

    public String getMigrationTemporaryPreviousDirectory() {
        return getReplaceSchemaProperties().getMigrationTemporaryPreviousDirectory();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}
