package org.seasar.dbflute.logic.replaceschema.migratereps;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.doc.dataxls.DfDataXlsHandler;
import org.seasar.dbflute.logic.doc.dataxls.DfTableOrderAnalyzer;
import org.seasar.dbflute.logic.replaceschema.loaddata.DfLoadedDataInfo;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfLoadDataMigration {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfLoadDataMigration.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfLoadDataMigration(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                          Output Xls
    //                                                                          ==========
    public void outputData(Database database) {
        final List<List<Table>> orderedList = analyzeOrder(database);
        final DfDataXlsHandler handler = new DfDataXlsHandler(_dataSource);
        handler.setContainsCommonColumn(true); // fixed
        int sectionNo = 1;
        for (List<Table> tableList : orderedList) {
            final Map<String, Table> tableInfoMap = new LinkedHashMap<String, Table>();
            _log.info("[Section " + sectionNo + "]");
            for (Table table : tableList) {
                tableInfoMap.put(table.getName(), table);
            }
            final String directoryPath = getCurrentTypeDataDirectoryPath();
            final String number = (sectionNo < 10 ? "0" + sectionNo : String.valueOf(sectionNo));
            final File baseDir = new File(directoryPath);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            final File xlsFile = new File(directoryPath + "/migration-data-section" + number + ".xls");
            handler.outputData(tableInfoMap, DfDataXlsHandler.XLS_LIMIT, xlsFile);
            ++sectionNo;
        }
    }

    protected List<List<Table>> analyzeOrder(Database database) {
        final DfTableOrderAnalyzer analyzer = new DfTableOrderAnalyzer();
        return analyzer.analyzeOrder(database);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getProperties().getReplaceSchemaProperties();
    }

    protected String getCurrentTypeDataDirectoryPath() {
        // output as firstXls
        return getReplaceSchemaProperties().getMainCurrentEnvDataDir(DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }
}
