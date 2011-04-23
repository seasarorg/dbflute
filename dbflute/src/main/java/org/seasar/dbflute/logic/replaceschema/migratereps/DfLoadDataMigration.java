package org.seasar.dbflute.logic.replaceschema.migratereps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.ForeignKey;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.doc.dataxls.DfDataXlsTemplateHandler;
import org.seasar.dbflute.logic.doc.dataxls.DfTemplateDataTableInfo;
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
        final DfDataXlsTemplateHandler handler = new DfDataXlsTemplateHandler(_dataSource);
        handler.setupOverLimitTruncated();
        int sectionNo = 1;
        for (List<Table> tableList : orderedList) {
            final Map<String, DfTemplateDataTableInfo> tableInfoMap = new LinkedHashMap<String, DfTemplateDataTableInfo>();
            _log.info("[Section " + sectionNo + "]");
            for (Table table : tableList) {
                if (table.isAdditionalSchema()) {
                    continue; // because tables on main schema only are target
                }
                final DfTemplateDataTableInfo tableInfo = new DfTemplateDataTableInfo();
                tableInfo.setTableDbName(table.getName());
                tableInfo.setTableSqlName(table.getTableSqlNameDirectUse());
                tableInfo.setColumnList(table.getColumnList());
                tableInfoMap.put(table.getName(), tableInfo);
            }
            final String directoryPath = getCurrentTypeDataDirectoryPath();
            final String number = (sectionNo < 10 ? "0" + sectionNo : String.valueOf(sectionNo));
            final File baseDir = new File(directoryPath);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            final File xlsFile = new File(directoryPath + "/migration-data-section" + number + ".xls");
            handler.outputData(tableInfoMap, DfDataXlsTemplateHandler.XLS_LIMIT, xlsFile);
            ++sectionNo;
        }
    }

    protected List<List<Table>> analyzeOrder(Database database) {
        final Set<String> alreadyRegisteredSet = new HashSet<String>();
        final List<List<Table>> outputOrderedList = new ArrayList<List<Table>>();

        List<Table> tableList = database.getTableList();
        while (true) {
            final int beforeSize = tableList.size();
            tableList = doAnalyzeOrder(tableList, alreadyRegisteredSet, outputOrderedList);
            if (tableList.isEmpty()) {
                break;
            }
            final int afterSize = tableList.size();
            if (beforeSize == afterSize) { // means it cannot analyze more
                outputOrderedList.add(tableList);
                break;
            }
        }
        return outputOrderedList;
    }

    /**
     * @param tableList The list of table, which may be registered. (NotNull)
     * @param alreadyRegisteredSet The name set of already registered table. (NotNull)
     * @param outputOrderedList The ordered list of table for output. (NotNull)
     * @return The list of unregistered table. (NotNull)
     */
    protected List<Table> doAnalyzeOrder(List<Table> tableList, Set<String> alreadyRegisteredSet,
            List<List<Table>> outputOrderedList) {
        final List<Table> unregisteredTableList = new ArrayList<Table>();
        final List<Table> elementList = new ArrayList<Table>();
        for (Table table : tableList) {
            if (table.isTypeView()) {
                continue;
            }
            final List<ForeignKey> foreignKeyList = table.getForeignKeyList();
            boolean independent = true;
            for (ForeignKey fk : foreignKeyList) {
                final String foreignTableName = fk.getForeignTableName();
                if (!fk.isSelfReference() && !alreadyRegisteredSet.contains(foreignTableName)) {
                    // found parent non-registered
                    independent = false;
                    break;
                }
            }
            if (independent) {
                elementList.add(table);
                alreadyRegisteredSet.add(table.getName());
            } else {
                unregisteredTableList.add(table);
            }
        }
        if (!elementList.isEmpty()) {
            outputOrderedList.add(elementList);
        }
        return unregisteredTableList;
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
        return getReplaceSchemaProperties().getCurrentTypeDataDirectoryPath(DfLoadedDataInfo.FIRSTXLS_FILE_TYPE);
    }
}
