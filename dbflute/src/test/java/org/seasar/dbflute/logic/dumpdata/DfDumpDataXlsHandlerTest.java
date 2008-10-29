package org.seasar.dbflute.logic.dumpdata;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.seasar.dbflute.helper.dataset.DataColumn;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.io.xls.DfXlsReader;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/29 Wednesday)
 */
public class DfDumpDataXlsHandlerTest extends DfDBFluteTestCase {

    @Test
    public void test_transferToXls() {
        // ## Arrange ##
        final DfDumpDataXlsHandler target = createTarget(null);
        final File dir = new File(PATH_TMP_DBFLUTE_TEST);
        if (!dir.exists()) {
            dir.mkdir();
        }
        final File xlsFile = new File(PATH_TMP_DBFLUTE_TEST + "/DfDumpDataXlsHandlerTest.xls");
        if (xlsFile.exists()) {
            xlsFile.delete();
        }
        final Map<String, List<String>> tableColumnMap = new LinkedHashMap<String, List<String>>();
        {
            final List<String> columnNameList = new ArrayList<String>();
            columnNameList.add("AAA");
            columnNameList.add("BBB");
            columnNameList.add("CCC");
            tableColumnMap.put("TEST_TABLE", columnNameList);
        }
        final Map<String, List<Map<String, String>>> dumpDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        {
            final List<Map<String, String>> columnValueMapList = new ArrayList<Map<String, String>>();
            {
                final Map<String, String> columnValueMap = new LinkedHashMap<String, String>();
                columnValueMap.put("AAA", "AAA_VALUE");
                columnValueMap.put("BBB", "BBB_VALUE");
                columnValueMap.put("CCC", "CCC_VALUE");
                columnValueMapList.add(columnValueMap);
            }
            dumpDataMap.put("TEST_TABLE", columnValueMapList);
        }

        // ## Act ##
        target.transferToXls(tableColumnMap, dumpDataMap, xlsFile);

        // ## Assert ##
        assertTrue(xlsFile.exists());
        final DfXlsReader xlsReader = new DfXlsReader(xlsFile);
        final DataSet dataSet = xlsReader.read();
        log("[DataSet]:" + getLineSeparator() + dataSet);
        final int tableSize = dataSet.getTableSize();
        assertTrue(tableSize > 0);
        for (int i = 0; i < tableSize; i++) {
            final DataTable dataTable = dataSet.getTable(i);
            final int columnSize = dataTable.getColumnSize();
            assertTrue(columnSize > 0);
            final int rowSize = dataTable.getRowSize();
            assertTrue(rowSize > 0);
            for (int j = 0; j < rowSize; j++) {
                final DataRow dataRow = dataTable.getRow(j);
                for (int k = 0; k < rowSize; k++) {
                    final DataColumn dataColumn = dataTable.getColumn(k);
                    final Object value = dataRow.getValue(dataColumn.getColumnName());
                    assertNotNull(value);
                }
            }
        }
    }

    protected DfDumpDataXlsHandler createTarget(DataSource dataSource) {
        return new DfDumpDataXlsHandler(dataSource);
    }
}
