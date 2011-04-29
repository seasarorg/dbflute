package org.seasar.dbflute.logic.replaceschema.process;

import java.io.File;
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
        clearTemporary();
        final DfAlterSchemaFinalInfo finalInfo = alterSchema();
        if (finalInfo.isFailure()) {
            return finalInfo;
        }
        deployNextResource();
        try {
            _coreProcessPlayer.play();
        } catch (RuntimeException e) {
            rollbackSchema();
            throw e;
        }
        final DfSchemaDiff schemaDiff = serializeReplacedSchema();
        if (schemaDiff.hasDiff()) { // wrong alter
            markAlterNG();
            rollbackSchema();
            handleAlterFailure(schemaDiff);
        } else { // success
            removeAlterNG();
            saveHistory();
        }
        clearTemporary();
        return finalInfo;
    }

    // -----------------------------------------------------
    //                                          Alter Schema
    //                                          ------------
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
        } else {
            serializeAlteredSchema();
        }
        _log.info(""); // for space line
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
        final String historyFile = getMigrationHistoryFile();
        return DfSchemaXmlSerializer.createAsPlain(_dataSource, _mainSchema, schemaXml, historyFile);
    }

    // -----------------------------------------------------
    //                                        Replace Schema
    //                                        --------------
    protected void replaceSchema() {
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
        final List<File> migrationSqlFileList = getMigrationCreateSchemaSqlFileList();
        final Map<String, File> previousSqlFileMap = getReplaceSchemaSqlFileMap();
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
        final String playSqlDir = getReplaceSchemaPlaySqlDirectory();
        final List<File> migrationSqlFileList = getMigrationCreateSchemaSqlFileList();
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

    // -----------------------------------------------------
    //                                           Diff Schema
    //                                           -----------
    protected DfSchemaDiff serializeReplacedSchema() {
        final DfSchemaXmlSerializer serializer = createSchemaXmlSerializer();
        serializer.serialize();
        return serializer.getSchemaDiff();
    }

    // -----------------------------------------------------
    //                                               NG Mark
    //                                               -------
    protected void markAlterNG() {
        final String ngMark = getMigrationAlterNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                markFile.createNewFile();
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for alter-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void removeAlterNG() {
        final String ngMark = getMigrationAlterNGMark();
        final File markFile = new File(ngMark);
        if (markFile.exists()) {
            markFile.delete();
        }
    }

    // -----------------------------------------------------
    //                                      Roll-back Schema
    //                                      ----------------
    protected void rollbackSchema() {
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("*                 *");
        _log.info("* Rollback Schema *");
        _log.info("*                 *");
        _log.info("* * * * * * * * * *");
        try {
            revertToPreviousResource();
            _coreProcessPlayer.play();
        } catch (RuntimeException e) {
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

    protected void throwAlterCheckRollbackSchemaFailureException(RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to rollback to previous schema.");
        br.addItem("Advice");
        br.addElement("The function AlterCheck requires that previous schema is valid.");
        br.addElement("So you should fix the mistakes of the previous schema.");
        br.addElement("And you should move alter SQL files anywhere temporarily");
        br.addElement("when you execute ReplaceSchema task for the previous schema.");
        br.addItem("Exception");
        br.addElement(e.getClass().getName());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfAlterCheckRollbackSchemaFailureException(msg, e);
    }

    protected void handleAlterFailure(DfSchemaDiff schemaDiff) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Found the differences between alter SQL and create SQL.");
        br.addItem("Advice");
        br.addElement("Make sure your alter SQL are correct.");
        br.addElement("And delete the '" + getMigrationAlterNGMark() + "' file");
        br.addElement("after you fix them all. (it supresses AlterCheck if it remains)");
        br.addElement("");
        br.addElement("You can confirm them at '" + getMigrationHistoryFile() + "'.");
        br.addElement("The first history is difference between previous schema and altered schema.");
        br.addElement("The second history is difference between altered schema and replaced schema.");
        br.addItem("Diff Date");
        br.addElement(schemaDiff.getDiffDate());
        final DfNextPreviousDiff tableCountDiff = schemaDiff.getTableCount();
        if (tableCountDiff != null) {
            br.addItem("Table Count");
            br.addElement(tableCountDiff.getPrevious() + " to " + tableCountDiff.getNext());
        }
        final String msg = br.buildExceptionMessage();
        throw new DfAlterCheckDifferenceFoundException(msg);
    }

    // -----------------------------------------------------
    //                                          Save History
    //                                          ------------
    protected void saveHistory() {
        final String currentDir = getHistoryCurrentDir();
        final List<File> alterSqlFileList = getMigrationAlterSqlFileList();
        for (File sqlFile : alterSqlFileList) {
            final File historyTo = new File(currentDir + "/" + sqlFile.getName());
            sqlFile.renameTo(historyTo);
        }
    }

    protected String getHistoryCurrentDir() {
        final String historyDir = getMigrationHistoryDirectory();
        final Date currentDate = new Date();
        final String yyyy = DfTypeUtil.toString(currentDate, "yyyy");
        mkdirsIfNotExists(new File(historyDir + "/" + yyyy));
        // e.g. history/2011/20110429_2247
        final String yyyyMMddHHmm = DfTypeUtil.toString(currentDate, "yyyyMMdd_HHmm");
        final String currentDir = historyDir + "/" + yyyy + "/" + yyyyMMddHHmm;
        mkdirsIfNotExists(new File(currentDir));
        return currentDir;
    }

    protected void mkdirsIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // -----------------------------------------------------
    //                                         Temporary Dir
    //                                         -------------
    protected void clearTemporary() {
        final File tmpDir = new File(getMigrationTemporaryDirectory());
        if (!tmpDir.exists()) {
            tmpDir.delete();
        }
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

    public List<File> getTakeFinallySqlFileList() { // without Application's
        return getReplaceSchemaProperties().getTakeFinallySqlFileList();
    }

    // -----------------------------------------------------
    //                                             Migration
    //                                             ---------
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

    public List<File> getMigrationCreateSchemaSqlFileList() {
        return getReplaceSchemaProperties().getMigrationCreateSchemaSqlFileList();
    }

    public boolean hasMigrationAlterSqlResource() {
        return getReplaceSchemaProperties().hasMigrationAlterSqlResource();
    }

    protected String getMigrationSchemaXml() {
        return getReplaceSchemaProperties().getMigrationSchemaXml();
    }

    protected String getMigrationHistoryFile() {
        return getReplaceSchemaProperties().getMigrationHistoryFile();
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
