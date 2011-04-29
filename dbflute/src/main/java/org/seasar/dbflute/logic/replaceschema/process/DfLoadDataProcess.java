package org.seasar.dbflute.logic.replaceschema.process;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.replaceschema.finalinfo.DfLoadDataFinalInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataHandler;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataResource;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfDelimiterDataResultInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedDataInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedFile;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfXlsDataHandler;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfXlsDataResource;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfDelimiterDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.interceotpr.DfDataWritingInterceptor;
import org.seasar.dbflute.logic.replaceschema.loaddata.interceotpr.DfDataWritingInterceptorSQLServer;
import org.seasar.dbflute.logic.replaceschema.loaddata.interceotpr.DfDataWritingInterceptorSybase;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.util.Srl;

public class DfLoadDataProcess extends DfAbstractReplaceSchemaProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataProcess.class);

    public static final String LOG_PATH = "./log/load-data.log";
    protected static final String COMMON_ENV_TYPE = DfLoadedDataInfo.COMMON_ENV_TYPE;
    protected static final String TSV_FILE_TYPE = DfLoadedDataInfo.TSV_FILE_TYPE;
    protected static final String CSV_FILE_TYPE = DfLoadedDataInfo.CSV_FILE_TYPE;
    protected static final String XLS_FILE_TYPE = DfLoadedDataInfo.XLS_FILE_TYPE;
    protected static final String FIRSTXLS_FILE_TYPE = DfLoadedDataInfo.FIRSTXLS_FILE_TYPE;
    protected static final String TSV_DELIMITER = DfLoadedDataInfo.TSV_DELIMITER;
    protected static final String CSV_DELIMITER = DfLoadedDataInfo.CSV_DELIMITER;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;
    protected final boolean _utility; // unused but for future

    // -----------------------------------------------------
    //                                             Load Data
    //                                             ---------
    protected DfXlsDataHandlerImpl _xlsDataHandlerImpl;
    protected DfDelimiterDataHandlerImpl _delimiterDataHandlerImpl;
    protected boolean _success;

    /** The info of loaded data. This info has loaded files when it fails too. */
    protected final DfLoadedDataInfo _loadedDataInfo = new DfLoadedDataInfo();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfLoadDataProcess(DataSource dataSource, UnifiedSchema mainSchema, boolean utility) {
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _utility = utility;
    }

    public static DfLoadDataProcess createAsCore(DataSource dataSource) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfLoadDataProcess(dataSource, mainSchema, false);
    }

    public static DfLoadDataProcess createAsUtility(DataSource dataSource) {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return new DfLoadDataProcess(dataSource, mainSchema, true);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public DfLoadDataFinalInfo execute() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Load Data         *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        RuntimeException loadEx = null;
        try {
            // applicationPlaySql is used only for xls,
            // which is the fixed specification

            // common (firstxls -> tsv -> csv -> xls)
            writeDbFromXlsAsCommonDataFirst();
            writeDbFromXlsAsCommonDataAppFirst();
            writeDbFromDelimiterFileAsCommonData(TSV_FILE_TYPE, TSV_DELIMITER);
            writeDbFromDelimiterFileAsCommonData(CSV_FILE_TYPE, CSV_DELIMITER);
            writeDbFromXlsAsCommonData();
            writeDbFromXlsAsCommonDataApp();

            // specified environment (firstxls -> tsv -> csv -> xls)
            writeDbFromXlsAsLoadingTypeDataFirst();
            writeDbFromXlsAsLoadingTypeDataAppFirst();
            writeDbFromDelimiterFileAsLoadingTypeData(TSV_FILE_TYPE, TSV_DELIMITER);
            writeDbFromDelimiterFileAsLoadingTypeData(CSV_FILE_TYPE, CSV_DELIMITER);
            writeDbFromXlsAsLoadingTypeData();
            writeDbFromXlsAsLoadingTypeDataApp();
            _success = true; // means no exception
        } catch (RuntimeException e) {
            loadEx = e;
        }
        return createFinalInfo(loadEx);
    }

    protected String getDataLoadingType() {
        return getMyProperties().getDataLoadingType();
    }

    public boolean isLoggingInsertSql() {
        return getMyProperties().isLoggingInsertSql();
    }

    public boolean isSuppressBatchUpdate() {
        return getMyProperties().isSuppressBatchUpdate();
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    // --------------------------------------------
    //                               Delimiter Data
    //                               --------------
    protected void writeDbFromDelimiterFileAsCommonData(String fileType, String delimter) {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String path = doGetCommonDataDirectoryPath(dir, fileType);
        writeDbFromDelimiterFile(COMMON_ENV_TYPE, path, fileType, delimter);
    }

    protected void writeDbFromDelimiterFileAsLoadingTypeData(String fileType, String delimter) {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, fileType);
        writeDbFromDelimiterFile(getDataLoadingType(), path, fileType, delimter);
    }

    protected void writeDbFromDelimiterFile(String envType, String directoryPath, String fileType, String delimiter) {
        final DfDelimiterDataResource resource = new DfDelimiterDataResource();
        resource.setEnvType(envType);
        resource.setBasePath(directoryPath);
        resource.setFileType(fileType);
        resource.setDelimiter(delimiter);
        final DfDelimiterDataHandler handler = getDelimiterDataHandlerImpl();
        final DfDelimiterDataResultInfo resultInfo = handler.writeSeveralData(resource, _loadedDataInfo);
        showDelimiterResult(fileType, resultInfo);
    }

    protected DfDelimiterDataHandlerImpl getDelimiterDataHandlerImpl() {
        if (_delimiterDataHandlerImpl != null) {
            return _delimiterDataHandlerImpl;
        }
        final DfDelimiterDataHandlerImpl handler = new DfDelimiterDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(_dataSource);
        handler.setUnifiedSchema(_mainSchema);
        handler.setSuppressBatchUpdate(isSuppressBatchUpdate());
        handler.setDataWritingInterceptor(getDataWritingInterceptor());
        _delimiterDataHandlerImpl = handler;
        return _delimiterDataHandlerImpl;
    }

    protected void showDelimiterResult(String typeName, DfDelimiterDataResultInfo resultInfo) {
        final Map<String, Set<String>> notFoundColumnMap = resultInfo.getNotFoundColumnMap();
        if (!notFoundColumnMap.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("*Found non-persistent columns in ").append(typeName).append(":");
            Set<Entry<String, Set<String>>> entrySet = notFoundColumnMap.entrySet();
            for (Entry<String, Set<String>> entry : entrySet) {
                final String tableName = entry.getKey();
                final Set<String> columnNameSet = entry.getValue();
                sb.append(ln()).append("[").append(tableName).append("]");
                for (String columnName : columnNameSet) {
                    sb.append(ln()).append("    ").append(columnName);
                }
            }
            _log.info(sb.toString());
        }
        final Map<String, List<String>> warningFileMap = resultInfo.getWarningFileMap();
        if (!warningFileMap.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("*Found warned files in ").append(typeName).append(":");
            for (Entry<String, List<String>> entry : warningFileMap.entrySet()) {
                final String key = entry.getKey();
                final List<String> messageList = entry.getValue();
                sb.append(ln()).append("[").append(key).append("]");
                for (String message : messageList) {
                    sb.append(ln()).append("    ").append(message);
                }
            }
            _log.warn(sb.toString());
        }
    }

    // --------------------------------------------
    //                                     Xls Data
    //                                     --------
    protected void writeDbFromXlsAsCommonDataFirst() {
        writeDbFromXls(new XlsWritingResource().commonType().firstXls());
    }

    protected void writeDbFromXlsAsCommonDataAppFirst() {
        writeDbFromXls(new XlsWritingResource().application().commonType().firstXls());
    }

    protected void writeDbFromXlsAsCommonData() {
        writeDbFromXls(new XlsWritingResource().commonType());
    }

    protected void writeDbFromXlsAsCommonDataApp() {
        writeDbFromXls(new XlsWritingResource().application().commonType());
    }

    protected void writeDbFromXlsAsLoadingTypeDataFirst() {
        writeDbFromXls(new XlsWritingResource().firstXls());
    }

    protected void writeDbFromXlsAsLoadingTypeDataAppFirst() {
        writeDbFromXls(new XlsWritingResource().application().firstXls());
    }

    protected void writeDbFromXlsAsLoadingTypeData() {
        writeDbFromXls(new XlsWritingResource());
    }

    protected void writeDbFromXlsAsLoadingTypeDataApp() {
        writeDbFromXls(new XlsWritingResource().application());
    }

    protected static class XlsWritingResource {
        protected boolean _application;
        protected boolean _commonType;
        protected boolean _firstXls;

        public boolean isApplication() {
            return _application;
        }

        public XlsWritingResource application() {
            _application = true;
            return this;
        }

        public boolean isCommonType() {
            return _commonType;
        }

        public XlsWritingResource commonType() {
            _commonType = true;
            return this;
        }

        public boolean isFirstXls() {
            return _firstXls;
        }

        public XlsWritingResource firstXls() {
            _firstXls = true;
            return this;
        }
    }

    protected void writeDbFromXls(XlsWritingResource res) {
        final String repPlaySqlDir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String appPlaySqlDir = getMyProperties().getApplicationPlaySqlDirectory();
        final String dir = res.isApplication() ? appPlaySqlDir : repPlaySqlDir;
        if (Srl.is_Null_or_TrimmedEmpty(dir)) {
            return;
        }
        final String envType = res.isCommonType() ? COMMON_ENV_TYPE : getDataLoadingType();
        final String typeName = res.isFirstXls() ? FIRSTXLS_FILE_TYPE : XLS_FILE_TYPE;
        final String dataDirectory = doGetLoadingTypeDataDirectoryPath(dir, envType, typeName);
        writeDbFromXls(envType, dataDirectory);
    }

    protected void writeDbFromXls(String envType, String dataDirectory) {
        final DfXlsDataResource resource = new DfXlsDataResource();
        resource.setEnvType(envType);
        resource.setDataDirectory(dataDirectory);
        final DfXlsDataHandler handler = getXlsDataHandlerImpl();
        handler.writeSeveralData(resource, _loadedDataInfo);
    }

    protected DfXlsDataHandlerImpl getXlsDataHandlerImpl() {
        if (_xlsDataHandlerImpl != null) {
            return _xlsDataHandlerImpl;
        }
        final DfXlsDataHandlerImpl handler = new DfXlsDataHandlerImpl(_dataSource);
        handler.setUnifiedSchema(_mainSchema); // for getting database meta data
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setSuppressBatchUpdate(isSuppressBatchUpdate());
        handler.setSkipSheet(getMyProperties().getSkipSheet());
        handler.setDataWritingInterceptor(getDataWritingInterceptor());
        _xlsDataHandlerImpl = handler;
        return _xlsDataHandlerImpl;
    }

    // --------------------------------------------
    //                          Writing Interceptor
    //                          -------------------
    protected DfDataWritingInterceptor getDataWritingInterceptor() {
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        if (basicProp.isDatabaseSQLServer()) { // needs identity insert
            return new DfDataWritingInterceptorSQLServer(_dataSource, isLoggingInsertSql());
        } else if (basicProp.isDatabaseSybase()) { // needs identity insert
            return new DfDataWritingInterceptorSybase(_dataSource, isLoggingInsertSql());
        } else {
            return null;
        }
    }

    // --------------------------------------------
    //                                    Directory
    //                                    ---------
    protected String doGetCommonDataDirectoryPath(String dir, String typeName) {
        return getMyProperties().getCommonDataDirectoryPath(dir, typeName);
    }

    protected String doGetLoadingTypeDataDirectoryPath(String dir, String envType, String typeName) {
        return getMyProperties().getLoadingTypeDataDirectoryPath(dir, envType, typeName);
    }

    // ===================================================================================
    //                                                                          Final Info
    //                                                                          ==========
    protected DfLoadDataFinalInfo createFinalInfo(RuntimeException loadEx) {
        final DfLoadDataFinalInfo finalInfo = new DfLoadDataFinalInfo();
        final List<DfLoadedFile> loadedFileList = _loadedDataInfo.getLoadedFileList();
        final int loadedFileCount = loadedFileList.size();
        final String title = "{Load Data}";
        final String resultMessage = title + ": loaded-files=" + loadedFileCount;
        final boolean failure;
        final String detailMessage;
        if (_success) {
            failure = false;
            if (loadedFileCount > 0) {
                final StringBuilder detailMessageSb = new StringBuilder();
                setupDetailMessage(detailMessageSb); // has the last line separator
                detailMessage = Srl.rtrim(detailMessageSb.toString()); // with removing the last line separator
            } else {
                detailMessage = "- (no data file)";
            }
        } else {
            // it is the precondition that LoadData stops at the first failure
            failure = true;
            final StringBuilder detailMessageSb = new StringBuilder();
            if (loadedFileCount > 0) {
                setupDetailMessage(detailMessageSb); // has the last line separator
            }
            detailMessageSb.append("x (failed: Look at the exception message)");
            detailMessage = detailMessageSb.toString();
        }
        finalInfo.setResultMessage(resultMessage);
        finalInfo.addDetailMessage(detailMessage);
        finalInfo.setFailure(failure);
        finalInfo.setLoadEx(loadEx);
        return finalInfo;
    }

    protected void setupDetailMessage(StringBuilder detailMessageSb) {
        final Map<String, Map<String, List<DfLoadedFile>>> hierarchyMap = _loadedDataInfo
                .getLoadedFileListHierarchyMap();

        // order according to registration
        doSetupDetailMessageEnvType(detailMessageSb, COMMON_ENV_TYPE, hierarchyMap.get(COMMON_ENV_TYPE));
        for (Entry<String, Map<String, List<DfLoadedFile>>> entry : hierarchyMap.entrySet()) {
            final String envType = entry.getKey();
            if (COMMON_ENV_TYPE.equals(envType)) {
                continue; // already processed
            }
            doSetupDetailMessageEnvType(detailMessageSb, envType, entry.getValue());
        }
    }

    protected void doSetupDetailMessageEnvType(StringBuilder detailMessageSb, String envType,
            Map<String, List<DfLoadedFile>> fileTypeKeyListMap) {
        if (fileTypeKeyListMap == null || fileTypeKeyListMap.isEmpty()) {
            return;
        }
        detailMessageSb.append("(").append(envType).append(")").append(ln());
        doSetupDetailMessageFileType(detailMessageSb, fileTypeKeyListMap.get(FIRSTXLS_FILE_TYPE), 10);
        doSetupDetailMessageFileType(detailMessageSb, fileTypeKeyListMap.get(TSV_FILE_TYPE), 3);
        doSetupDetailMessageFileType(detailMessageSb, fileTypeKeyListMap.get(CSV_FILE_TYPE), 3);
        doSetupDetailMessageFileType(detailMessageSb, fileTypeKeyListMap.get(XLS_FILE_TYPE), 10);
    }

    protected void doSetupDetailMessageFileType(StringBuilder detailMessageSb, List<DfLoadedFile> loadedFileList,
            int limit) {
        if (loadedFileList == null || loadedFileList.isEmpty()) {
            return; // means no files for the file type
        }
        // for example:
        // 
        // (common)
        // o 10-master.xls
        // (ut)
        // o 10-TABLE_NAME.tsv
        // o (and other tsv files...)
        // o 20-member.xls
        // o 30-product.xls

        String fileType4Etc = null;
        boolean etcExists = false;
        boolean etcWarned = false;
        int index = 0;
        for (DfLoadedFile loadedFile : loadedFileList) {
            if (fileType4Etc == null) { // first loop
                fileType4Etc = loadedFile.getFileType();
            }
            if (index >= limit) {
                etcExists = true;
                if (loadedFile.isWarned()) {
                    etcWarned = true;
                }
                continue;
            }
            final String fileName = loadedFile.getFileName();
            final String mark = loadedFile.isWarned() ? "v " : "o ";
            detailMessageSb.append(mark).append(fileName).append(ln());
            ++index;
        }
        if (etcExists) {
            final String mark = etcWarned ? "v " : "o ";
            detailMessageSb.append(mark).append("(and other ");
            detailMessageSb.append(fileType4Etc).append(" files...)").append(ln());
        }
    }
}
