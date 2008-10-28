package org.seasar.dbflute.logic.dumpdata;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDumpDataXlsHandler {

    /**
     * @param dumpDataMap The map of dump data. (NotNull)
     */
    public void dumpToExcel(Map<String, List<Map<String, String>>> dumpDataMap) {
        final Set<String> tableNameSet = dumpDataMap.keySet();
        for (String tableName : tableNameSet) {
            final List<Map<String, String>> recordList = dumpDataMap.get(tableName);
            for (Map<String, String> recordMap : recordList) {
                recordMap.toString();
                // TODO: @jflute: to excel
            }
        }
    }
}
