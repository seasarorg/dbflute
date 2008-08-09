package org.seasar.dbflute.task.replaceschema;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataResultInfo;
import org.seasar.dbflute.helper.datahandler.DfSeparatedDataSeveralHandlingInfo;
import org.seasar.dbflute.helper.datahandler.impl.DfSeparatedDataHandlerImpl;
import org.seasar.dbflute.helper.datahandler.impl.DfXlsDataHandlerImpl;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;

public class DfLoadDataTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataTask.class);

    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
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

    protected String getEnvironmentType() {
        return getMyProperties().getEnvironmentType();
    }

    public boolean isLoggingInsertSql() {
        return getMyProperties().isLoggingInsertSql();
    }

    protected DfReplaceSchemaProperties getMyProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    // --------------------------------------------
    //                               Separated Data
    //                               --------------
    protected void writeDbFromSeparatedFileAsCommonData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getCommonDataDirectoryPath("tsv"), "common");
    }

    protected void writeDbFromSeparatedFileAsAdditionalData(String typeName, String delimter) {
        writeDbFromSeparatedFile(typeName, delimter, getAdditionalDataDirectoryPath(getEnvironmentType(), typeName),
                getEnvironmentType());
    }

    protected void writeDbFromSeparatedFile(String typeName, String delimter, String directoryPath, String location) {
        final DfSeparatedDataHandlerImpl handler = new DfSeparatedDataHandlerImpl();
        handler.setLoggingInsertSql(isLoggingInsertSql());
        handler.setDataSource(getDataSource());
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
        final Set<String> notFoundColumnSet = notFoundColumnMap.keySet();
        for (String tableName : notFoundColumnSet) {
            _log.warn("[" + tableName + "]");
            final Set<String> columnNameList = notFoundColumnMap.get(tableName);
            for (String columnName : columnNameList) {
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
        writeDbFromXls(getCommonDataDirectoryPath("xls"), "common");
    }

    protected void writeDbFromXlsAsAdditionalData() {
        writeDbFromXls(getAdditionalDataDirectoryPath(getEnvironmentType(), "xls"), getEnvironmentType());
    }

    protected void writeDbFromXls(String directoryPath, String typeName) {
        final DfXlsDataHandlerImpl xlsDataHandler = new DfXlsDataHandlerImpl();
        xlsDataHandler.setLoggingInsertSql(isLoggingInsertSql());
        xlsDataHandler.setSchemaName(_schema);// For getting database meta data.
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        if (basicProperties.isDatabaseSqlServer()) {
            xlsDataHandler.writeSeveralDataForSqlServer(directoryPath, getDataSource());
        } else if (basicProperties.isDatabaseSybase()) {
            xlsDataHandler.writeSeveralDataForSybase(directoryPath, getDataSource());
        } else {
            xlsDataHandler.writeSeveralData(directoryPath, getDataSource());
        }
    }
}
