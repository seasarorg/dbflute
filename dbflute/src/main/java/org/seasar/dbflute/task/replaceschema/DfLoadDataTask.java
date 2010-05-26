package org.seasar.dbflute.task.replaceschema;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfSeparatedDataResultInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.logic.replaceschema.loaddata.impl.DfXlsDataHandlerSQLServer;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;

public class DfLoadDataTask extends DfAbstractReplaceSchemaTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean validTaskEndInformation = true;
    protected DfXlsDataHandlerImpl xlsDataHandlerImpl;

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

        writeDbFromSeparatedFileAsAdditionalData("tsv", "\t");
        writeDbFromSeparatedFileAsAdditionalData("csv", ",");
        writeDbFromXlsAsAdditionalData();
    }

    @Override
    protected boolean isValidTaskEndInformation() {
        return validTaskEndInformation;
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
        writeDbFromSeparatedFile(typeName, delimter, getCommonDataDirectoryPath(typeName), "common");
    }

    protected void writeDbFromSeparatedFileAsAdditionalData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getAdditionalDataDirectoryPath(getDataLoadingType(), typeName),
                getDataLoadingType());
    }

    protected void writeDbFromSeparatedFile(String typeName, String delimter, String directoryPath, String location) {
        final DfSeparatedDataHandlerImpl handler = new DfSeparatedDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(getDataSource());
        handler.setUnifiedSchema(_mainSchema);
        final DfSeparatedDataSeveralHandlingInfo handlingInfo = new DfSeparatedDataSeveralHandlingInfo();
        handlingInfo.setBasePath(directoryPath);
        handlingInfo.setTypeName(typeName);
        handlingInfo.setDelimter(delimter);
        handlingInfo.setErrorContinue(true);
        final DfSeparatedDataResultInfo resultInfo = handler.writeSeveralData(handlingInfo);
        showNotFoundColumn(typeName, resultInfo.getNotFoundColumnMap());
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

    protected String getReplaceSchemaSqlFileDirectoryName() {
        final String sqlFileName = getMyProperties().getReplaceSchemaSqlFile();
        return sqlFileName.substring(0, sqlFileName.lastIndexOf("/"));
    }

    protected String getCommonDataDirectoryPath(final String typeName) {
        return getReplaceSchemaSqlFileDirectoryName() + "/data/common/" + typeName;
    }

    protected String getAdditionalDataDirectoryPath(final String envType, final String typeName) {
        return getReplaceSchemaSqlFileDirectoryName() + "/data/" + envType + "/" + typeName;
    }

    // --------------------------------------------
    //                                     Xls Data
    //                                     --------
    protected void writeDbFromXlsAsCommonData() {
        writeDbFromXls(getCommonDataDirectoryPath("xls"));
    }

    protected void writeDbFromXlsAsAdditionalData() {
        writeDbFromXls(getAdditionalDataDirectoryPath(getDataLoadingType(), "xls"));
    }

    protected void writeDbFromXls(String directoryPath) {
        final DfXlsDataHandlerImpl xlsDataHandler = getXlsDataHandlerImpl();
        xlsDataHandler.writeSeveralData(directoryPath);
    }

    protected DfXlsDataHandlerImpl getXlsDataHandlerImpl() {
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        if (xlsDataHandlerImpl == null) {
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
            xlsDataHandlerImpl = xlsDataHandler;
        }
        return xlsDataHandlerImpl;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setValidTaskEndInformation(String validTaskEndInformation) {
        this.validTaskEndInformation = validTaskEndInformation != null
                && validTaskEndInformation.trim().equalsIgnoreCase("true");
    }
}
