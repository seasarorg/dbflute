package org.seasar.dbflute.helper.io.xls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.dataset.DataColumn;
import org.seasar.dbflute.helper.dataset.DataRow;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.dataset.DataTable;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetterTest;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.io.DfResourceUtil;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/24 Monday)
 */
public class DfXlsReaderTest extends PlainTestCase {

    @Test
    public void test_read() throws IOException {
        // ## Arrange ##
        final File buildDir = DfResourceUtil.getBuildDir(DfSqlFileGetterTest.class);
        final String canonicalPath = buildDir.getCanonicalPath();
        final String packageName = DfXlsReaderTest.class.getPackage().getName();
        final String className = getClass().getSimpleName();
        final String path = canonicalPath + "/" + packageName.replace('.', '/') + "/" + className + ".xls";
        final File xlsFile = new File(path);
        final DfXlsReader reader = createXlsReader(xlsFile, null);

        // ## Act ##
        final DataSet dataSet = reader.read();

        // ## Assert ##
        log("[DataSet]:" + getLineSeparator() + dataSet);
        final int tableSize = dataSet.getTableSize();
        assertTrue(tableSize > 0);
        boolean existsNull = false;
        boolean existsTrimmed = false;
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
                    final Object value = dataRow.getValue(dataColumn.getColumnName());
                    if (dataColumn.getColumnName().equals("AAA")) {
                        assertNotNull(value);
                    } else if (dataColumn.getColumnName().equals("BBB")) {
                        existsNull = true;
                    } else if (dataColumn.getColumnName().equals("CCC")) {
                        assertNotNull(value);
                    } else if (dataColumn.getColumnName().equals("DDD")) {
                        assertNotNull(value);
                        assertEquals(((String) value).length(), ((String) value).trim().length());
                    } else if (dataColumn.getColumnName().equals("EEE")) {
                        assertNotNull(value);
                        String str = (String) value;
                        if (str.startsWith("\"") && str.endsWith("\"")) {
                            str = str.substring(1);
                            str = str.substring(0, str.length() - 1);
                            if (str.length() != str.trim().length()) {
                                existsTrimmed = true;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(existsNull);
        assertTrue(existsTrimmed);
    }

    protected DfXlsReader createXlsReader(File xlsFile, Pattern skipSheetPattern) {
        final DfFlexibleMap<String, String> tableNameMap = new DfFlexibleMap<String, String>();
        final DfFlexibleMap<String, List<String>> notTrimTableColumnMap = new DfFlexibleMap<String, List<String>>();
        notTrimTableColumnMap.put("TEST_TABLE", Arrays.asList("EEE"));
        final DfFlexibleMap<String, List<String>> stringEmptyTableColumnMap = new DfFlexibleMap<String, List<String>>();
        stringEmptyTableColumnMap.put("TEST_TABLE", Arrays.asList("CCC"));
        return new DfXlsReader(xlsFile, tableNameMap, notTrimTableColumnMap, stringEmptyTableColumnMap,
                skipSheetPattern);
    }

    @Test
    public void test_DfXlsDataHandlerImpl_isCommentOutSheet() {
        // ## Arrange ##
        final DfXlsReader reader = createEmptyXlsReader(null);

        // ## Act & Assert ##
        assertTrue(reader.isCommentOutSheet("#MST_STATUS"));
        assertFalse(reader.isSkipSheet("MST_STATUS"));
    }

    @Test
    public void test_DfXlsDataHandlerImpl_isSkipSheet() {
        // ## Arrange ##
        final DfXlsReader reader = createEmptyXlsReader(Pattern.compile("MST.+"));

        // ## Act & Assert ##
        assertTrue(reader.isSkipSheet("MST_STATUS"));
        assertTrue(reader.isSkipSheet("MST_"));
        assertFalse(reader.isSkipSheet("MST"));
        assertFalse(reader.isSkipSheet("MS_STATUS"));
        assertFalse(reader.isSkipSheet("AMST_STATUS"));
        assertFalse(reader.isSkipSheet("9MST_STATUS"));
        assertFalse(reader.isSkipSheet("#MST_STATUS"));
    }

    protected DfXlsReader createEmptyXlsReader(Pattern skipSheetPattern) {
        final DfFlexibleMap<String, String> tableNameMap = new DfFlexibleMap<String, String>();
        final DfFlexibleMap<String, List<String>> notTrimTableColumnMap = new DfFlexibleMap<String, List<String>>();
        final DfFlexibleMap<String, List<String>> stringEmptyTableColumnMap = new DfFlexibleMap<String, List<String>>();
        return new DfXlsReaderEmpty(tableNameMap, notTrimTableColumnMap, stringEmptyTableColumnMap, skipSheetPattern);
    }

    protected static class DfXlsReaderEmpty extends DfXlsReader {

        public DfXlsReaderEmpty(DfFlexibleMap<String, String> tableNameMap,
                DfFlexibleMap<String, List<String>> notTrimTableColumnMap,
                DfFlexibleMap<String, List<String>> stringEmptyTableColumnMap, Pattern skipSheetPattern) {
            super(new ByteArrayInputStream(new byte[] {}), tableNameMap, notTrimTableColumnMap,
                    stringEmptyTableColumnMap, skipSheetPattern);
        }

        @Override
        protected void setupWorkbook(InputStream in) {
            // Nothing!
        }
    }
}
