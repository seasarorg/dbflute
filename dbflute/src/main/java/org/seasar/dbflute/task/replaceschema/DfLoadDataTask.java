package org.seasar.dbflute.task.replaceschema;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfSeparatedDataResultInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfXlsDataHandler;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfXlsDataHandlerSQLServer;
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

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _validTaskEndInformation = true;
    protected DfXlsDataHandlerImpl _xlsDataHandlerImpl;
    protected DfSeparatedDataHandlerImpl _separatedDataHandlerImpl;

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
        writeDbFromSeparatedFileAsCommonData("tsv", "\t");
        writeDbFromSeparatedFileAsCommonData("csv", ",");
        writeDbFromXlsAsCommonData();
        writeDbFromXlsAsCommonDataAdditional();

        writeDbFromSeparatedFileAsLoadingTypeData("tsv", "\t");
        writeDbFromSeparatedFileAsLoadingTypeData("csv", ",");
        writeDbFromXlsAsLoadingTypeData();
        writeDbFromXlsAsLoadingTypeDataAdditional();
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
    //                               Separated Data
    //                               --------------
    protected void writeDbFromSeparatedFileAsCommonData(String typeName, String delimter) {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String path = doGetCommonDataDirectoryPath(dir, typeName);
        writeDbFromSeparatedFile(path, typeName, delimter);
    }

    protected void writeDbFromSeparatedFileAsLoadingTypeData(String typeName, String delimter) {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, typeName);
        writeDbFromSeparatedFile(path, typeName, delimter);
    }

    protected void writeDbFromSeparatedFile(String directoryPath, String typeName, String delimter) {
        final DfSeparatedDataHandlerImpl handler = getSeparatedDataHandlerImpl();
        final DfSeparatedDataSeveralHandlingInfo handlingInfo = new DfSeparatedDataSeveralHandlingInfo();
        handlingInfo.setBasePath(directoryPath);
        handlingInfo.setTypeName(typeName);
        handlingInfo.setDelimter(delimter);
        handlingInfo.setErrorContinue(true);
        final DfSeparatedDataResultInfo resultInfo = handler.writeSeveralData(handlingInfo);
        showNotFoundColumn(typeName, resultInfo.getNotFoundColumnMap());
    }

    protected DfSeparatedDataHandlerImpl getSeparatedDataHandlerImpl() {
        if (_separatedDataHandlerImpl != null) {
            return _separatedDataHandlerImpl;
        }
        final DfSeparatedDataHandlerImpl handler = new DfSeparatedDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(getDataSource());
        handler.setUnifiedSchema(_mainSchema);
        handler.setSuppressBatchUpdate(isSuppressBatchUpdate());
        _separatedDataHandlerImpl = handler;
        return _separatedDataHandlerImpl;
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
        final String path = doGetCommonDataDirectoryPath(dir, "xls");
        writeDbFromXls(path);
    }

    protected void writeDbFromXlsAsCommonDataAdditional() {
        final String dir = getMyProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(dir)) {
            return;
        }
        final String path = doGetCommonDataDirectoryPath(dir, "xls");
        writeDbFromXls(path);
    }

    protected void writeDbFromXlsAsLoadingTypeData() {
        final String dir = getMyProperties().getReplaceSchemaPlaySqlDirectory();
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, "xls");
        writeDbFromXls(path);
    }

    protected void writeDbFromXlsAsLoadingTypeDataAdditional() {
        final String dir = getMyProperties().getApplicationPlaySqlDirectory();
        if (Srl.is_Null_or_TrimmedEmpty(dir)) {
            return;
        }
        final String envType = getDataLoadingType();
        final String path = doGetLoadingTypeDataDirectoryPath(dir, envType, "xls");
        writeDbFromXls(path);
    }

    protected void writeDbFromXls(String directoryPath) {
        final DfXlsDataHandler xlsDataHandler = getXlsDataHandlerImpl();
        xlsDataHandler.writeSeveralData(directoryPath);
    }

    protected DfXlsDataHandlerImpl getXlsDataHandlerImpl() {
        if (_xlsDataHandlerImpl != null) {
            return _xlsDataHandlerImpl;
        }
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        final DfXlsDataHandlerImpl xlsDataHandler;
        if (basicProperties.isDatabaseSQLServer()) {
            xlsDataHandler = new DfXlsDataHandlerSQLServer(getDataSource());
        } else {
            xlsDataHandler = new DfXlsDataHandlerImpl(getDataSource());
        }
        xlsDataHandler.setUnifiedSchema(_mainSchema); // for getting database meta data
        xlsDataHandler.setLoggingInsertSql(isLoggingInsertSql());
        xlsDataHandler.setSuppressBatchUpdate(isSuppressBatchUpdate());
        xlsDataHandler.setSkipSheet(getMyProperties().getSkipSheet());
        _xlsDataHandlerImpl = xlsDataHandler;
        return _xlsDataHandlerImpl;
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
    //                                                                         Dump Result
    //                                                                         ===========
    // *not used because loading data is not continued if it causes an error
    //protected void dumpContinuedErrorResult() {
    //    final File file = new File(LOG_PATH);
    //    if (file.exists()) {
    //        boolean deleted = file.delete();
    //        if (!deleted) {
    //            return; // skip to dump!
    //        }
    //    }
    //    if (_continuedErrorFileList.isEmpty()) {
    //        return;
    //    }
    //    BufferedWriter bw = null;
    //    try {
    //        final StringBuilder contentsSb = new StringBuilder();
    //        contentsSb.append("{Load Data}: warning files=" + _continuedErrorFileList.size());
    //        List<String> continuedErrorFileList = _continuedErrorFileList;
    //        for (String continuedErrorFile : continuedErrorFileList) {
    //            // remove path because only file names are enough to see the files 
    //            final String fileName = Srl.substringLastRear(continuedErrorFile, "/");
    //            contentsSb.append(ln()).append("x " + fileName);
    //        }
    //        final FileOutputStream fos = new FileOutputStream(file);
    //        bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
    //        bw.write(contentsSb.toString());
    //    } catch (UnsupportedEncodingException e) {
    //        throw new IllegalStateException(e);
    //    } catch (FileNotFoundException e) {
    //        throw new IllegalStateException(e);
    //    } catch (IOException e) {
    //        throw new IllegalStateException(e);
    //    } finally {
    //        if (bw != null) {
    //            try {
    //                bw.close();
    //            } catch (IOException ignored) {
    //            }
    //        }
    //    }
    //}

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this._validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
    }
}
