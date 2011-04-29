package org.seasar.dbflute.logic.doc.dataxls;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfDataXlsGenerator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDataXlsGenerator.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfDataXlsHandler _templatehandler;
    protected final String _outputDir;
    protected final String _fileTitle;
    protected final int _limit;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataXlsGenerator(DfDataXlsHandler templatehandler, String outputDir, String fileTitle, int limit) {
        _templatehandler = templatehandler;
        _outputDir = outputDir;
        _fileTitle = fileTitle;
        _limit = limit;
    }

    // ===================================================================================
    //                                                                          Output Xls
    //                                                                          ==========
    public void outputData(Database database) {
        final List<List<Table>> orderedList = analyzeOrder(database);
        int sectionNo = 1;
        for (List<Table> tableList : orderedList) {
            final Map<String, Table> tableInfoMap = new LinkedHashMap<String, Table>();
            _log.info("[Section " + sectionNo + "]");
            for (Table table : tableList) {
                tableInfoMap.put(table.getName(), table);
            }
            final String number = (sectionNo < 10 ? "0" + sectionNo : String.valueOf(sectionNo));
            final File baseDir = new File(_outputDir);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
            final File xlsFile = new File(_outputDir + "/" + _fileTitle + "-section" + number + ".xls");
            _templatehandler.outputData(tableInfoMap, _limit, xlsFile);
            ++sectionNo;
        }
    }

    protected List<List<Table>> analyzeOrder(Database database) {
        final DfTableOrderAnalyzer analyzer = new DfTableOrderAnalyzer();
        return analyzer.analyzeOrder(database);
    }
}
