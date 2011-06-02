package org.seasar.dbflute.logic.replaceschema.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.FileUtils;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.DfAlterCheckAlterScriptSQLException;
import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDataSourceNotFoundException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;
import org.seasar.dbflute.exception.DfAlterCheckReplaceSchemaFailureException;
import org.seasar.dbflute.exception.DfAlterCheckRollbackSchemaFailureException;
import org.seasar.dbflute.exception.DfAlterCheckSavePreviousFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.jdbc.DfRunnerInformation;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireMan;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileFireResult;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunner;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerResult;
import org.seasar.dbflute.helper.process.ProcessResult;
import org.seasar.dbflute.helper.process.SystemScript;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfNextPreviousDiff;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlSerializer;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfAlterCheckFinalInfo;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

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

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfAlterCheckProcess(DataSource dataSource, UnifiedSchema mainSchema, CoreProcessPlayer coreProcessPlayer) {
        if (dataSource == null) { // for example, ReplaceSchema may have lazy connection
            throwAlterCheckDataSourceNotFoundException();
        }
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _coreProcessPlayer = coreProcessPlayer;
    }

    protected void throwAlterCheckDataSourceNotFoundException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the data source for AlterCheck (or ChangeOutput).");
        br.addItem("Advice");
        br.addElement("Make sure your database process works");
        br.addElement("or your connection settings are correct.");
        String msg = br.buildExceptionMessage();
        throw new DfAlterCheckDataSourceNotFoundException(msg);
    }

    public static DfAlterCheckProcess createAsMain(DataSource dataSource, CoreProcessPlayer coreProcessPlayer) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfAlterCheckProcess(dataSource, mainSchema, coreProcessPlayer);
    }

    public static interface CoreProcessPlayer {
        void play(String sqlRootDir);
    }

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    public DfAlterCheckFinalInfo savePrevious() {
        return doSavePrevious();
    }

    public DfAlterCheckFinalInfo checkAlter() {
        processReady();

        // to be previous DB
        rollbackSchema();

        final DfAlterCheckFinalInfo finalInfo = alterSchema();
        if (finalInfo.isFailure()) {
            return finalInfo;
        }

        replaceSchema(finalInfo);
        if (finalInfo.isFailure()) {
            return finalInfo;
        }

        deleteAlterCheckResultDiff(); // to replace the result file
        final DfSchemaDiff schemaDiff = schemaDiff();
        if (schemaDiff.hasDiff()) {
            processDifference(finalInfo, schemaDiff);
        } else {
            processSuccess();
        }

        processClosing();
        return finalInfo;
    }

    // ===================================================================================
    //                                                                Save Previous Schema
    //                                                                ====================
    protected DfAlterCheckFinalInfo doSavePrevious() {
        _log.info("");
        _log.info("* * * * * * * * *");
        _log.info("*               *");
        _log.info("* Save Previous *");
        _log.info("*               *");
        _log.info("* * * * * * * * *");
        processReady();
        final DfAlterCheckFinalInfo finalInfo = new DfAlterCheckFinalInfo();
        finalInfo.setResultMessage("{Save Previous}");
        deletePreviousResource();
        final List<File> copyToFileList = copyToPreviousResource();
        checkSavedResource(finalInfo);
        if (finalInfo.isFailure()) {
            finalInfo.addDetailMessage("x (save failure)");
            return finalInfo;
        }
        markPreviousOK(copyToFileList);
        deleteSavePreviousMark();
        finalInfo.addDetailMessage("o (all resources saved)");
        return finalInfo;
    }

    protected void deletePreviousResource() {
        final List<File> previousFileList = findHierarchyFileList(getMigrationPreviousDir());
        if (!previousFileList.isEmpty()) {
            _log.info("...Deleting the previous resources");
            for (File previousFile : previousFileList) {
                deleteFile(previousFile, null);
            }
        }
    }

    protected List<File> copyToPreviousResource() {
        final List<File> copyToFileList = new ArrayList<File>();
        final String previousDir = getMigrationPreviousDir();
        final String playSqlDirSymbol = getPlaySqlDir() + "/";
        final Map<String, File> replaceSchemaSqlFileMap = getReplaceSchemaSqlFileMap();
        for (File mainFile : replaceSchemaSqlFileMap.values()) {
            doMoveToPreviousResource(mainFile, previousDir, playSqlDirSymbol, copyToFileList);
        }
        final Map<String, File> takeFinallySqlFileMap = getTakeFinallySqlFileMap();
        for (File mainFile : takeFinallySqlFileMap.values()) {
            doMoveToPreviousResource(mainFile, previousDir, playSqlDirSymbol, copyToFileList);
        }
        final List<File> dataFileList = findHierarchyFileList(getSchemaDataDir());
        for (File dataFile : dataFileList) {
            doMoveToPreviousResource(dataFile, previousDir, playSqlDirSymbol, copyToFileList);
        }
        return copyToFileList;
    }

    protected void doMoveToPreviousResource(File mainFile, String previousDir, String playSqlDirSymbol,
            List<File> copyToFileList) {
        final String relativePath = Srl.substringLastRear(mainFile.getPath(), playSqlDirSymbol);
        final File moveToFile = new File(previousDir + "/" + relativePath);
        final File moveToDir = new File(Srl.substringLastFront(moveToFile.getPath(), "/"));
        if (!moveToDir.exists()) {
            moveToDir.mkdirs();
        }
        if (moveToFile.exists()) {
            moveToFile.delete();
        }
        _log.info("...Saving the file to " + moveToFile.getPath());
        copyFile(mainFile, moveToFile);
        copyToFileList.add(moveToFile);
    }

    protected void checkSavedResource(DfAlterCheckFinalInfo finalInfo) {
        _log.info("...Checking the previous resources by replacing");
        try {
            playPreviousSchema();
        } catch (RuntimeException threwLater) { // basically no way because of checked before saving
            markPreviousNG(getAlterCheckSavePreviousFailureNotice());
            setupAlterCheckSavePreviousFailureException(finalInfo, threwLater);
        }
    }

    protected void markPreviousOK(List<File> copyToFileList) {
        final String okMark = getMigrationPreviousOKMark();
        try {
            final File markFile = new File(okMark);
            _log.info("...Marking previous-OK: " + okMark);
            markFile.createNewFile();
            final StringBuilder sb = new StringBuilder();
            sb.append("[Saved Previous Resources]: " + currentDate());
            for (File moveToFile : copyToFileList) {
                sb.append(ln()).append(moveToFile.getPath());
            }
            sb.append(ln()).append("(" + copyToFileList.size() + " files)");
            sb.append(ln());
            writeNotice(markFile, sb.toString());
        } catch (IOException e) {
            String msg = "Failed to create a file for previous-OK mark: " + okMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void setupAlterCheckSavePreviousFailureException(DfAlterCheckFinalInfo finalInfo,
            RuntimeException threwLater) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(getAlterCheckSavePreviousFailureNotice());
        br.addItem("Advice");
        br.addElement("Make sure your DDL and data for ReplaceSchema,");
        br.addElement("resources just below 'playsql' directory, are correct.");
        br.addElement("and after that, execute ReplaceSchema again to save previous");
        String msg = br.buildExceptionMessage();
        finalInfo.setSavePreviousFailureEx(new DfAlterCheckSavePreviousFailureException(msg, threwLater));
        finalInfo.setFailure(true);
    }

    protected String getAlterCheckSavePreviousFailureNotice() {
        return "Failed to replace by saved resources for previous schema.";
    }

    protected void deleteSavePreviousMark() {
        final String mark = getMigrationSavePreviousMark();
        deleteFile(new File(mark), "...Deleting the save-previous mark");
    }

    // ===================================================================================
    //                                                                               Ready
    //                                                                               =====
    protected void processReady() {
        deleteAllNGMark();
        deleteSchemaXml(); // resources may remain if previous execution aborts
    }

    // ===================================================================================
    //                                                                    Roll-back Schema
    //                                                                    ================
    protected void rollbackSchema() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Roll-back Schema  *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        try {
            playPreviousSchema();
        } catch (RuntimeException e) { // basically no way because of checked before saving
            markPreviousNG(getAlterCheckRollbackSchemaFailureNotice());
            throwAlterCheckRollbackSchemaFailureException(e);
        }
    }

    protected void playPreviousSchema() {
        _coreProcessPlayer.play(getMigrationPreviousDir());
    }

    protected void markPreviousNG(String notice) {
        deletePreviousOKMark();
        final String ngMark = getMigrationPreviousNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                _log.info("...Marking previous-NG: " + ngMark);
                markFile.createNewFile();
                writeNotice(markFile, notice);
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for previous-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void throwAlterCheckRollbackSchemaFailureException(RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(getAlterCheckRollbackSchemaFailureNotice());
        br.addItem("Advice");
        br.addElement("The AlterCheck requires that PreviousDDL are correct.");
        br.addElement("So you should prepare the PreviousDDL again.");
        final String msg = br.buildExceptionMessage();
        throw new DfAlterCheckRollbackSchemaFailureException(msg, e);
    }

    protected String getAlterCheckRollbackSchemaFailureNotice() {
        return "Failed to rollback the schema to previous state.";
    }

    // ===================================================================================
    //                                                                         AlterSchema
    //                                                                         ===========
    protected DfAlterCheckFinalInfo alterSchema() {
        _log.info("");
        _log.info("* * * * * * * * *");
        _log.info("*               *");
        _log.info("* Alter Schema  *");
        _log.info("*               *");
        _log.info("* * * * * * * * *");
        final DfAlterCheckFinalInfo finalInfo = executeAlterSql();
        if (finalInfo.isFailure()) {
            markAlterNG(getAlterCheckAlterSqlFailureNotice());
            setupAlterCheckAlterSqlFailureException(finalInfo);
        } else {
            serializeAlteredSchema();
        }
        return finalInfo;
    }

    protected DfAlterCheckFinalInfo executeAlterSql() {
        final List<File> alterSqlFileList = getMigrationAlterSqlFileList();
        final DfRunnerInformation runInfo = createRunnerInformation();
        final DfSqlFileFireMan fireMan = createSqlFileFireMan();
        fireMan.setExecutorName("Alter Schema");
        final DfSqlFileRunner runner = new DfSqlFileRunnerExecute(runInfo, _dataSource);
        final DfSqlFileFireResult result = fireMan.fire(runner, alterSqlFileList);
        return createFinalInfo(result);
    }

    protected DfSqlFileFireMan createSqlFileFireMan() {
        final String[] scriptExtAry = SystemScript.getSupportedExtList().toArray(new String[] {});
        final SystemScript script = new SystemScript();
        return new DfSqlFileFireMan() {
            @Override
            protected DfSqlFileRunnerResult processSqlFile(DfSqlFileRunner runner, File sqlFile) {
                final String path = sqlFile.getPath();
                if (!Srl.endsWith(path, scriptExtAry)) { // SQL file
                    return super.processSqlFile(runner, sqlFile);
                }
                // script file
                final String resolvedPath = Srl.replace(path, "\\", "/");
                final String baseDir = Srl.substringLastFront(resolvedPath, "/");
                final String scriptName = Srl.substringLastRear(resolvedPath, "/");
                final ProcessResult processResult = script.execute(new File(baseDir), scriptName);
                if (processResult.isSystemMismatch()) {
                    _log.info("...Skipping the script for system mismatch: " + scriptName);
                    return null;
                }
                final String console = processResult.getConsole();
                if (Srl.is_NotNull_and_NotTrimmedEmpty(console)) {
                    _log.info("...Reading console for " + scriptName + ":" + ln() + console);
                }
                final DfSqlFileRunnerResult runnerResult = new DfSqlFileRunnerResult(sqlFile);
                runnerResult.setTotalSqlCount(1);
                final int exitCode = processResult.getExitCode();
                if (exitCode != 0) {
                    final String msg = "The script failed: " + scriptName + " exitCode=" + exitCode;
                    final SQLException sqlEx = new DfAlterCheckAlterScriptSQLException(msg);
                    final String sqlExp = "(commands on the script)";
                    runnerResult.addErrorContinuedSql(sqlExp, sqlEx);
                    return runnerResult;
                } else {
                    runnerResult.setGoodSqlCount(1);
                    return runnerResult;
                }
            }
        };
    }

    protected void serializeAlteredSchema() {
        final DfSchemaXmlSerializer serializer = createSchemaXmlSerializer();
        serializer.serialize();
    }

    protected DfSchemaXmlSerializer createSchemaXmlSerializer() {
        final String schemaXml = getMigrationAlterCheckSchemaXml();
        final String diffFile = getMigrationAlterCheckResultDiff();
        return DfSchemaXmlSerializer.createAsManage(_dataSource, _mainSchema, schemaXml, diffFile);
    }

    protected DfSchemaXmlSerializer createSchemaXmlSerializer(String diffFile) {
        final String schemaXml = getMigrationAlterCheckSchemaXml();
        return DfSchemaXmlSerializer.createAsManage(_dataSource, _mainSchema, schemaXml, diffFile);
    }

    protected void setupAlterCheckAlterSqlFailureException(DfAlterCheckFinalInfo finalInfo) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(getAlterCheckAlterSqlFailureNotice());
        br.addItem("Advice");
        setupFixedAlterAdviceMessage(br);
        br.addElement("Look at the final info in the log for DBFlute task.");
        br.addItem("Message");
        br.addElement(finalInfo.getResultMessage());
        String msg = br.buildExceptionMessage();
        finalInfo.setAlterSqlFailureEx(new DfAlterCheckAlterSqlFailureException(msg));
        finalInfo.setFailure(true);
    }

    protected String getAlterCheckAlterSqlFailureNotice() {
        return "Failed to execute the AlterDDL statements.";
    }

    // ===================================================================================
    //                                                                       ReplaceSchema
    //                                                                       =============
    protected void replaceSchema(DfAlterCheckFinalInfo finalInfo) {
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("*                 *");
        _log.info("* Replace Schema  *");
        _log.info("*                 *");
        _log.info("* * * * * * * * * *");
        try {
            playMainProcess();
        } catch (RuntimeException threwLater) {
            markNextNG(getAlterCheckReplaceSchemaFailureNotice());
            setupAlterCheckReplaceSchemaFailureException(finalInfo, threwLater);
        }
    }

    protected void playMainProcess() {
        _coreProcessPlayer.play(getPlaySqlDir());
    }

    protected void markNextNG(String notice) {
        final String ngMark = getMigrationNextNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                _log.info("...Marking next-NG: " + ngMark);
                markFile.createNewFile();
                writeNotice(markFile, notice);
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for next-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void setupAlterCheckReplaceSchemaFailureException(DfAlterCheckFinalInfo finalInfo, RuntimeException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(getAlterCheckReplaceSchemaFailureNotice());
        br.addItem("Advice");
        br.addElement("Make sure your NextDDL or data for ReplaceSchema are correct,");
        br.addElement("resources just below 'playsql' directory, are correct.");
        br.addElement("and after that, execute ReplaceSchema again to check AlterDDL");
        String msg = br.buildExceptionMessage();
        finalInfo.setReplaceSchemaFailureEx(new DfAlterCheckReplaceSchemaFailureException(msg, e));
        finalInfo.setFailure(true);
        finalInfo.addDetailMessage("x (replace failure)");
    }

    protected String getAlterCheckReplaceSchemaFailureNotice() {
        return "Failed to replace the schema using NextDDL.";
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
    //                                                                     Different Story
    //                                                                     ===============
    protected void processDifference(DfAlterCheckFinalInfo finalInfo, DfSchemaDiff schemaDiff) {
        _log.info("");
        _log.info("* * * * * * * * * *");
        _log.info("*                 *");
        _log.info("* Different Story *");
        _log.info("*                 *");
        _log.info("* * * * * * * * * *");
        markAlterNG(getAlterDiffNotice());
        handleAlterDiff(finalInfo, schemaDiff);
    }

    protected void markAlterNG(String notice) {
        final String ngMark = getMigrationAlterNGMark();
        try {
            final File markFile = new File(ngMark);
            if (!markFile.exists()) {
                _log.info("...Marking alter-NG: " + ngMark);
                markFile.createNewFile();
                writeNotice(markFile, notice);
            }
        } catch (IOException e) {
            String msg = "Failed to create a file for alter-NG mark: " + ngMark;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void handleAlterDiff(DfAlterCheckFinalInfo finalInfo, DfSchemaDiff schemaDiff) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(getAlterDiffNotice());
        br.addItem("Advice");
        setupFixedAlterAdviceMessage(br);
        br.addElement("");
        br.addElement("You can see the details at");
        br.addElement(" '" + getMigrationAlterCheckResultDiff() + "',");
        br.addItem("Diff Date");
        br.addElement(schemaDiff.getDiffDate());
        final DfNextPreviousDiff tableCountDiff = schemaDiff.getTableCount();
        if (tableCountDiff != null && tableCountDiff.hasDiff()) {
            br.addItem("Table Count");
            br.addElement(tableCountDiff.getPrevious() + " to " + tableCountDiff.getNext());
        }
        final String msg = br.buildExceptionMessage();
        finalInfo.setDiffFoundEx(new DfAlterCheckDifferenceFoundException(msg));
        finalInfo.setFailure(true);
        finalInfo.addDetailMessage("x (found diff)");
    }

    protected String getAlterDiffNotice() {
        return "Found the differences between AlterDDL and NextDDL.";
    }

    protected void setupFixedAlterAdviceMessage(ExceptionMessageBuilder br) {
        br.addElement("Make sure your AlterDDL are correct,");
        br.addElement("and after that, execute ReplaceSchema again.");
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
        saveHistory();
        deleteAllNGMark();
        deleteDiffResult();
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
        final String historyDir = getMigrationHistoryDir();
        final Date currentDate = new Date();
        final String middleDir = DfTypeUtil.toString(currentDate, "yyyyMM");
        mkdirsDirIfNotExists(historyDir + "/" + middleDir);
        // e.g. history/201104/20110429_2247
        final String yyyyMMddHHmm = DfTypeUtil.toString(currentDate, "yyyyMMdd_HHmm");
        final String currentDir = historyDir + "/" + middleDir + "/" + yyyyMMddHHmm;
        mkdirsDirIfNotExists(currentDir);
        return currentDir;
    }

    protected void mkdirsDirIfNotExists(String dirPath) {
        final File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected void mkdirsFileIfNotExists(String filePath) {
        final File dir = new File(Srl.substringLastFront(filePath, "/"));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ===================================================================================
    //                                                                       Mark Resource
    //                                                                       =============
    protected void deleteAllNGMark() {
        deleteNextNGMark();
        deleteAlterNGMark();
        deletePreviousNGMark();
    }

    protected void deletePreviousOKMark() {
        final String previousOKMark = getMigrationPreviousOKMark();
        deleteFile(new File(previousOKMark), "...Deleting the previous-OK mark");
    }

    protected void deleteNextNGMark() {
        final String replaceNGMark = getMigrationNextNGMark();
        deleteFile(new File(replaceNGMark), "...Deleting the next-NG mark");
    }

    protected void deleteAlterNGMark() {
        final String alterNGMark = getMigrationAlterNGMark();
        deleteFile(new File(alterNGMark), "...Deleting the alter-NG mark");
    }

    protected void deletePreviousNGMark() {
        final String previousNGMark = getMigrationPreviousNGMark();
        deleteFile(new File(previousNGMark), "...Deleting the previous-NG mark");
    }

    protected void deleteDiffResult() {
        deleteAlterCheckResultDiff();
    }

    protected void deleteAlterCheckResultDiff() {
        final String diff = getMigrationAlterCheckResultDiff();
        deleteFile(new File(diff), "...Deleting the AlterCheck result diff");
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    protected DfAlterCheckFinalInfo createFinalInfo(DfSqlFileFireResult fireResult) {
        final DfAlterCheckFinalInfo finalInfo = new DfAlterCheckFinalInfo();
        finalInfo.setResultMessage(fireResult.getResultMessage());
        final List<String> detailMessageList = extractDetailMessageList(fireResult);
        for (String detailMessage : detailMessageList) {
            finalInfo.addDetailMessage(detailMessage);
        }
        finalInfo.setFailure(fireResult.existsError());
        return finalInfo;
    }

    // ===================================================================================
    //                                                                             Closing
    //                                                                             =======
    protected void processClosing() {
        deleteSchemaXml();
    }

    protected void deleteSchemaXml() {
        final String schemaXml = getMigrationAlterCheckSchemaXml();
        deleteFile(new File(schemaXml), "...Deleting the SchemaXml file");
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String currentDate() {
        return DfTypeUtil.toString(DBFluteSystem.currentDate(), "yyyy/MM/dd HH:mm:ss");
    }

    protected List<File> findHierarchyFileList(String rootDir) {
        final List<File> fileList = new ArrayList<File>();
        doFindHierarchyFileList(fileList, new File(rootDir));
        return fileList;
    }

    protected void doFindHierarchyFileList(final List<File> fileList, File baseDir) {
        if (baseDir.getName().startsWith(".")) { // closed directory
            return; // e.g. .svn
        }
        final File[] listFiles = baseDir.listFiles(new FileFilter() {
            public boolean accept(File subFile) {
                if (subFile.isDirectory()) {
                    doFindHierarchyFileList(fileList, subFile);
                    return false;
                }
                return true;
            }
        });
        if (listFiles != null) {
            fileList.addAll(Arrays.asList(listFiles));
        }
    }

    protected void deleteFile(File file, String msg) {
        if (file.exists()) {
            if (msg != null) {
                _log.info(msg + ": " + file.getPath());
            }
            file.delete();
        }
    }

    protected void copyFile(File src, File dest) {
        try {
            FileUtils.getFileUtils().copyFile(src, dest);
        } catch (IOException e) {
            String msg = "Failed to copy file: " + src + " to " + dest;
            throw new IllegalStateException(msg);
        }
    }

    protected void writeNotice(File file, String notice) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(notice + ln() + "Look at the log for detail.");
            bw.flush();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    // -----------------------------------------------------
    //                                         ReplaceSchema
    //                                         -------------
    protected String getPlaySqlDir() {
        return getReplaceSchemaProperties().getPlaySqlDir();
    }

    protected Map<String, File> getReplaceSchemaSqlFileMap() {
        return getReplaceSchemaProperties().getReplaceSchemaSqlFileMap(getPlaySqlDir());
    }

    protected Map<String, File> getTakeFinallySqlFileMap() {
        return getReplaceSchemaProperties().getTakeFinallySqlFileMap(getPlaySqlDir());
    }

    protected String getSchemaDataDir() {
        return getReplaceSchemaProperties().getSchemaDataDir(getPlaySqlDir());
    }

    // -----------------------------------------------------
    //                                        Alter Resource
    //                                        --------------
    protected List<File> getMigrationAlterSqlFileList() {
        return getReplaceSchemaProperties().getMigrationAlterSqlFileList();
    }

    // -----------------------------------------------------
    //                                     Previous Resource
    //                                     -----------------
    protected String getMigrationPreviousDir() {
        return getReplaceSchemaProperties().getMigrationPreviousDir();
    }

    protected Map<String, File> getMigrationPreviousReplaceSchemaSqlFileMap() {
        return getReplaceSchemaProperties().getMigrationPreviousReplaceSchemaSqlFileMap();
    }

    protected Map<String, File> getMigrationPreviousTakeFinallySqlFileMap() {
        return getReplaceSchemaProperties().getMigrationPreviousTakeFinallySqlFileMap();
    }

    // -----------------------------------------------------
    //                                      History Resource
    //                                      ----------------
    protected String getMigrationHistoryDir() {
        return getReplaceSchemaProperties().getMigrationHistoryDir();
    }

    // -----------------------------------------------------
    //                                       Schema Resource
    //                                       ---------------
    protected String getMigrationAlterCheckResultDiff() {
        return getReplaceSchemaProperties().getMigrationAlterCheckResultDiff();
    }

    protected String getMigrationAlterCheckSchemaXml() {
        return getReplaceSchemaProperties().getMigrationAlterCheckSchemaXml();
    }

    // -----------------------------------------------------
    //                                         Mark Resource
    //                                         -------------
    protected String getMigrationSavePreviousMark() {
        return getReplaceSchemaProperties().getMigrationSavePreviousMark();
    }

    protected String getMigrationPreviousOKMark() {
        return getReplaceSchemaProperties().getMigrationPreviousOKMark();
    }

    protected String getMigrationNextNGMark() {
        return getReplaceSchemaProperties().getMigrationNextNGMark();
    }

    protected String getMigrationAlterNGMark() {
        return getReplaceSchemaProperties().getMigrationAlterNGMark();
    }

    protected String getMigrationPreviousNGMark() {
        return getReplaceSchemaProperties().getMigrationPreviousNGMark();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}
