package org.seasar.dbflute.task.replaceschema;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
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

public class DfLoadDataTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataTask.class);
    protected static final String LOG_PATH = "./log/load-data.log";
    protected static final String COMMON_ENV_TYPE = "common";
    protected static final String TSV_FILE_TYPE = "tsv";
    protected static final String CSV_FILE_TYPE = "csv";
    protected static final String XLS_FILE_TYPE = "xls";
    protected static final String TSV_DELIMITER = "\t";
    protected static final String CSV_DELIMITER = ",";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _validTaskEndInformation = true;
    protected DfXlsDataHandlerImpl _xlsDataHandlerImpl;
    protected DfDelimiterDataHandlerImpl _delimiterDataHandlerImpl;
    protected boolean _success;

    /** The info of loaded data. This info has loaded files when it fails too. */
    protected final DfLoadedDataInfo _loadedDataInfo = new DfLoadedDataInfo();

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Load Data         *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        try {
            // common (tsv -> csv -> xls)
            writeDbFromDelimiterFileAsCommonData(TSV_FILE_TYPE, TSV_DELIMITER);
            writeDbFromDelimiterFileAsCommonData(CSV_FILE_TYPE, CSV_DELIMITER);
            writeDbFromXlsAsCommonData();
            // additionalPlaySql is used only for xls
            // this is the fixed specification
            writeDbFromXlsAsCommonDataAdditional();

            // specified environment (tsv -> csv -> xls)
            writeDbFromDelimiterFileAsLoadingTypeData(TSV_FILE_TYPE, TSV_DELIMITER);
            writeDbFromDelimiterFileAsLoadingTypeData(CSV_FILE_TYPE, CSV_DELIMITER);
            writeDbFromXlsAsLoadingTypeData();
            writeDbFromXlsAsLoadingTypeDataAdditional();
            _success = true; // means no exception
        } finally {
            try {
                dumpResult();
            } catch (Throwable ignored) {
                _log.warn("*Failed to dump load-data result", ignored);
            }
        }
    }

    @Override
    protected boolean isValidTaskEndInformation() {
        return _validTaskEndInformation;
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
        writeDbFromDelimiterFile(envType, path, fileType, delimter);
    }

    protected void writeDbFromDelimiterFile(String envType, String directoryPath, String fileType, String delimiter) {
        final DfDelimiterDataResource resource = new DfDelimiterDataResource();
        resource.setEnvType(envType);
        resource.setBasePath(directoryPath);
        resource.setFileType(fileType);
        resource.setDelimiter(delimiter);
        final DfDelimiterDataHandler handler = getDelimiterDataHandlerImpl();
        final DfDelimiterDataResultInfo resultInfo = handler.writeSeveralData(resource, _loadedDataInfo);
        showNotFoundColumn(fileType, resultInfo.getNotFoundColumnMap());
    }

    protected DfDelimiterDataHandlerImpl getDelimiterDataHandlerImpl() {
        if (_delimiterDataHandlerImpl != null) {
            return _delimiterDataHandlerImpl;
        }
        final DfDelimiterDataHandlerImpl handler = new DfDelimiterDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(getDataSource());
        handler.setUnifiedSchema(_mainSchema);
        handler.setSuppressBatchUpdate(isSuppressBatchUpdate());
        handler.setDataWritingInterceptor(getDataWritingInterceptor());
        _delimiterDataHandlerImpl = handler;
        return _delimiterDataHandlerImpl;
    }

    protected void showNotFoundColumn(String typeName, Map<String, Set<String>> notFoundColumnMap) {
        if (notFoundColumnMap.isEmpty()) {
            return;
        }
        _log.warn("* * * * * * * * * * * * * * *");
        _log.warn("Not Persistent Columns in " + typeName);
        _log.warn("* * * * * * * * * * * * * * *");
        Set<Entry<String, Set<String>>> entrySet = notFoundColumnMap.entrySet();
        for (Entry<String, Set<String>> entry : entrySet) {
            String tableName = entry.getKey();
            Set<String> columnNameSet = entry.getValue();
            _log.warn("[" + tableName + "]");
            for (String columnName : columnNameSet) {
                _log.warn("    " + columnName);
            }
            _log.warn(" ");
        }
    }

    // --------------------------------------------
    //                                     Xls Data
    //                                     --------
    protected void writeDbFromXlsAsCommonData() {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String path = doGetCommonDataDirectoryPath(dir, XLS_FILE_TYPE);
        writeDbFromXls(COMMON_ENV_TYPE, path);
    }

    protected void writeDbFromXlsAsCommonDataAdditional() {
        final String dir = getMyProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(dir)) {
            return;
        }
        final String path = doGetCommonDataDirectoryPath(dir, XLS_FILE_TYPE);
        writeDbFromXls(COMMON_ENV_TYPE, path);
    }

    protected void writeDbFromXlsAsLoadingTypeData() {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, XLS_FILE_TYPE);
        writeDbFromXls(envType, path);
    }

    protected void writeDbFromXlsAsLoadingTypeDataAdditional() {
        final String dir = getMyProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(dir)) {
            return;
        }
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, XLS_FILE_TYPE);
        writeDbFromXls(envType, path);
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
        final DfXlsDataHandlerImpl handler = new DfXlsDataHandlerImpl(getDataSource());
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
            return new DfDataWritingInterceptorSQLServer(getDataSource(), isLoggingInsertSql());
        } else if (basicProp.isDatabaseSybase()) { // needs identity insert
            return new DfDataWritingInterceptorSybase(getDataSource(), isLoggingInsertSql());
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
    //                                                                         Result Dump
    //                                                                         ===========
    protected void dumpResult() {
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
            detailMessageSb.append("x (failed: Look the exception message)");
            detailMessage = detailMessageSb.toString();
        }
        final File dumpFile = new File(LOAD_DATA_LOG_PATH);
        dumpProcessResult(dumpFile, resultMessage, failure, detailMessage);
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
        int index = 0;
        for (DfLoadedFile loadedFile : loadedFileList) {
            if (index >= limit) {
                final String fileType = loadedFile.getFileType();
                detailMessageSb.append("o (and other ").append(fileType).append(" files...)").append(ln());
                break;
            }
            final String fileName = loadedFile.getFileName();

            detailMessageSb.append("o ").append(fileName).append(ln());
            ++index;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this._validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
    }
}
