package org.seasar.dbflute.logic.dataxls;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
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
import org.seasar.dbflute.logic.dataxls.DfTemplateDataXlsHandler;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/29 Wednesday)
 */
public class DfDumpDataXlsHandlerTest extends PlainTestCase {

    @Test
    public void test_transferToXls() throws IOException {
        // ## Arrange ##
        final DfTemplateDataXlsHandler target = createDumpDataXlsHandler(null);
        final String canonicalPath = getCanonicalPath();

        final File dir = new File(canonicalPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        final File xlsFile = new File(canonicalPath + "/DfDumpDataXlsHandlerTest.xls");
        if (xlsFile.exists()) {
            xlsFile.delete();
        }
        final Map<String, List<String>> tableColumnMap = new LinkedHashMap<String, List<String>>();
        {
            final List<String> columnNameList = new ArrayList<String>();
            columnNameList.add("AAA");
            columnNameList.add("BBB");
            columnNameList.add("CCC");
            columnNameList.add("DDD");
            tableColumnMap.put("TEST_TABLE", columnNameList);
        }
        final Map<String, List<Map<String, String>>> dumpDataMap = new LinkedHashMap<String, List<Map<String, String>>>();
        {
            final List<Map<String, String>> columnValueMapList = new ArrayList<Map<String, String>>();
            {
                final Map<String, String> columnValueMap = new LinkedHashMap<String, String>();
                columnValueMap.put("AAA", "AAA_VALUE");
                columnValueMap.put("BBB", "BBB_VALUE");
                columnValueMap.put("CCC", currentTimestamp().toString());
                columnValueMap.put("DDD", "あいうえお"); // for Japanese Test
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
        boolean existsJapaneseColumn = false;
        for (int i = 0; i < tableSize; i++) {
            final DataTable dataTable = dataSet.getTable(i);
            final int columnSize = dataTable.getColumnSize();
            assertTrue(columnSize > 0);
            final int rowSize = dataTable.getRowSize();
            assertTrue(rowSize > 0);
            for (int j = 0; j < rowSize; j++) {
                final DataRow dataRow = dataTable.getRow(j);
                for (int k = 0; k < columnSize; k++) {
                    final DataColumn dataColumn = dataTable.getColumn(k);
                    final String columnName = dataColumn.getColumnName();
                    final Object value = dataRow.getValue(columnName);
                    assertNotNull(value);
                    log(columnName + " = " + value);
                    if ("DDD".equals(columnName)) {
                        if (value != null && "あいうえお".equals(value)) {
                            existsJapaneseColumn = true;
                        }
                    }
                }
            }
        }
        assertTrue(existsJapaneseColumn);
    }

    protected DfTemplateDataXlsHandler createDumpDataXlsHandler(DataSource dataSource) {
        return new DfTemplateDataXlsHandler(dataSource);
    }
}
