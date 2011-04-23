package org.seasar.dbflute.logic.doc.dataxls;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.Column;
import org.apache.torque.engine.database.model.TypeMap;
import org.junit.Test;
import org.seasar.dbflute.helper.dataset.DfDataColumn;
import org.seasar.dbflute.helper.dataset.DfDataRow;
import org.seasar.dbflute.helper.dataset.DfDataSet;
import org.seasar.dbflute.helper.dataset.DfDataTable;
import org.seasar.dbflute.helper.io.xls.DfXlsReader;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.8.3 (2008/10/29 Wednesday)
 */
public class DfDataXlsTemplateHandlerTest extends PlainTestCase {

    @Test
    public void test_transferToXls() throws IOException {
        // ## Arrange ##
        final DfDataXlsTemplateHandler target = createDumpDataXlsHandler(null);
        final String canonicalPath = getCanonicalPath();

        final File dir = new File(canonicalPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        final File xlsFile = new File(canonicalPath + "/DfDumpDataXlsHandlerTest.xls");
        if (xlsFile.exists()) {
            xlsFile.delete();
        }
        final Map<String, DfTemplateDataTableInfo> tableColumnMap = new LinkedHashMap<String, DfTemplateDataTableInfo>();
        {
            final List<Column> columnList = new ArrayList<Column>();
            {
                Column column = new Column();
                column.setName("AAA");
                column.setJdbcType(TypeMap.VARCHAR);
                columnList.add(column);
            }
            {
                Column column = new Column();
                column.setName("BBB");
                column.setJdbcType(TypeMap.VARCHAR);
                columnList.add(column);
            }
            {
                Column column = new Column();
                column.setName("CCC");
                column.setJdbcType(TypeMap.TIMESTAMP);
                columnList.add(column);
            }
            {
                Column column = new Column();
                column.setName("DDD");
                column.setJdbcType(TypeMap.VARCHAR);
                columnList.add(column);
            }
            final DfTemplateDataTableInfo tableInfo = new DfTemplateDataTableInfo();
            final String tableDbName = "TEST_TABLE";
            tableInfo.setTableDbName(tableDbName);
            tableInfo.setTableSqlName("next." + tableDbName);
            tableInfo.setColumnList(columnList);
            tableColumnMap.put(tableDbName, tableInfo);
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
        final DfDataSet dataSet = xlsReader.read();
        log("[DataSet]:" + ln() + dataSet);
        final int tableSize = dataSet.getTableSize();
        assertTrue(tableSize > 0);
        boolean existsJapaneseColumn = false;
        for (int i = 0; i < tableSize; i++) {
            final DfDataTable dataTable = dataSet.getTable(i);
            final int columnSize = dataTable.getColumnSize();
            assertTrue(columnSize > 0);
            final int rowSize = dataTable.getRowSize();
            assertTrue(rowSize > 0);
            for (int j = 0; j < rowSize; j++) {
                final DfDataRow dataRow = dataTable.getRow(j);
                for (int k = 0; k < columnSize; k++) {
                    final DfDataColumn dataColumn = dataTable.getColumn(k);
                    final String columnName = dataColumn.getColumnDbName();
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

    protected DfDataXlsTemplateHandler createDumpDataXlsHandler(DataSource dataSource) {
        return new DfDataXlsTemplateHandler(dataSource);
    }
}
