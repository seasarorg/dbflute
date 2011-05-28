package org.seasar.dbflute.logic.doc.dataxls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.model.Table;
import org.seasar.dbflute.helper.mapstring.MapListString;
import org.seasar.dbflute.util.Srl;

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
            final String mainName = extractMainName(tableList);
            final String filePath = _outputDir + "/" + _fileTitle + number + "-" + mainName + ".xls";
            final File xlsFile = new File(filePath);
            _templateGenerator.outputData(tableInfoMap, _limit, xlsFile);
            ++sectionNo;
        }
        final Map<String, Table> tableNameMap = _templateGenerator.getTableNameMap();
        if (!tableNameMap.isEmpty()) {
            outputTableNameMap(tableNameMap);
        }
    }

    protected String extractMainName(List<Table> tableList) {
        final String miscName = "misc";
        if (tableList.size() < 2) {
            return miscName;
        }
        final Map<String, Integer> prefixMap = new HashMap<String, Integer>();
        for (Table table : tableList) {
            final String prefix = Srl.substringFirstFront(table.getName(), "_");
            final Integer count = prefixMap.get(prefix);
            if (count != null) {
                prefixMap.put(prefix, count + 1);
            } else {
                prefixMap.put(prefix, 1);
            }
        }
        if (prefixMap.size() == 1) {
            return prefixMap.keySet().iterator().next();
        } else if (prefixMap.size() == 2 && tableList.size() > 2) {
            final Iterator<String> ite = prefixMap.keySet().iterator();
            final String first = ite.next();
            final String second = ite.next();
            final Integer firstSize = prefixMap.get(first);
            final Integer secondSize = prefixMap.get(second);
            if (firstSize > secondSize) {
                return first + "-plus";
            } else if (firstSize < secondSize) {
                return second + "-plus";
            } else {
                return first + "-" + second;
            }
        } else {
            return miscName;
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
