package org.seasar.dbflute.logic.doc.dataxls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.helper.mapstring.MapListString;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/23 Saturday)
 */
public class DfDataXlsProcess {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDataXlsProcess.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfDataXlsGenerator _templateGenerator;
    protected final String _outputDir;
    protected final String _fileTitle;
    protected final int _limit;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDataXlsProcess(DfDataXlsGenerator templateGenerator, String outputDir, String fileTitle, int limit) {
        _templateGenerator = templateGenerator;
        _outputDir = outputDir;
        _fileTitle = fileTitle;
        _limit = limit;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public void execute(Database database) {
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
            _templateGenerator.outputData(tableInfoMap, _limit, xlsFile);
            ++sectionNo;
        }
        final Map<String, Table> tableNameMap = _templateGenerator.getTableNameMap();
        if (!tableNameMap.isEmpty()) {
            outputTableNameMap(tableNameMap);
        }
    }

    // ===================================================================================
    //                                                                       Analyze Order
    //                                                                       =============
    protected List<List<Table>> analyzeOrder(Database database) {
        final DfTableOrderAnalyzer analyzer = new DfTableOrderAnalyzer();
        return analyzer.analyzeOrder(database);
    }

    // ===================================================================================
    //                                                                      Table Name Map
    //                                                                      ==============
    protected void outputTableNameMap(Map<String, Table> tableNameMap) {
        final Map<String, String> map = new LinkedHashMap<String, String>();
        for (Entry<String, Table> entry : tableNameMap.entrySet()) {
            final String sheetName = entry.getKey();
            final Table table = entry.getValue();
            map.put(sheetName, table.getTableSqlName());
        }
        final String mapString = new MapListString().buildMapString(map);
        final File dataPropFile = new File(_outputDir + "/tableNameMap.dataprop");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPropFile), "UTF-8"));
            bw.write(mapString);
            bw.flush();
        } catch (IOException e) {
            String msg = "Failed to write tableNameMap.dataprop: " + dataPropFile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
