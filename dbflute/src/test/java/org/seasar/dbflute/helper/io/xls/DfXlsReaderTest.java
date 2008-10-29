package org.seasar.dbflute.helper.io.xls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.dataset.DataSet;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetterTest;
import org.seasar.dbflute.unit.DfDBFluteTestCase;
import org.seasar.dbflute.util.io.DfResourceUtil;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/24 Monday)
 */
public class DfXlsReaderTest extends DfDBFluteTestCase {

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
        // TODO: @jflute: Assert
        log(dataSet);
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

    protected DfXlsReader createXlsReader(File xlsFile, Pattern skipSheetPattern) {
        final DfFlexibleMap<String, String> tableNameMap = new DfFlexibleMap<String, String>();
        final DfFlexibleMap<String, List<String>> notTrimTableColumnMap = new DfFlexibleMap<String, List<String>>();
        return new DfXlsReader(xlsFile, tableNameMap, notTrimTableColumnMap, skipSheetPattern);
    }

    protected DfXlsReader createEmptyXlsReader(Pattern skipSheetPattern) {
        final DfFlexibleMap<String, String> tableNameMap = new DfFlexibleMap<String, String>();
        final DfFlexibleMap<String, List<String>> notTrimTableColumnMap = new DfFlexibleMap<String, List<String>>();
        return new DfXlsReaderEmpty(tableNameMap, notTrimTableColumnMap, skipSheetPattern);
    }

    protected static class DfXlsReaderEmpty extends DfXlsReader {

        public DfXlsReaderEmpty(DfFlexibleMap<String, String> tableNameMap,
                DfFlexibleMap<String, List<String>> notTrimTableColumnMap, Pattern skipSheetPattern) {
            super(new ByteArrayInputStream(new byte[] {}), tableNameMap, notTrimTableColumnMap, skipSheetPattern);
        }

        @Override
        protected void setupWorkbook(InputStream in) {
            // Nothing!
        }
    }
}
