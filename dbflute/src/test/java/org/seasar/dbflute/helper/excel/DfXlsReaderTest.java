package org.seasar.dbflute.helper.excel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/24 Monday)
 */
public class DfXlsReaderTest {

    @Test
    public void test_DfXlsDataHandlerImpl_isCommentOutSheet() {
        // ## Arrange ##
        final DfXlsReader reader = createXlsReader(null);

        // ## Act & Assert ##
        assertTrue(reader.isCommentOutSheet("#MST_STATUS"));
        assertFalse(reader.isSkipSheet("MST_STATUS"));
    }

    @Test
    public void test_DfXlsDataHandlerImpl_isSkipSheet() {
        // ## Arrange ##
        final DfXlsReader reader = createXlsReader(Pattern.compile("MST.+"));

        // ## Act & Assert ##
        assertTrue(reader.isSkipSheet("MST_STATUS"));
        assertTrue(reader.isSkipSheet("MST_"));
        assertFalse(reader.isSkipSheet("MST"));
        assertFalse(reader.isSkipSheet("MS_STATUS"));
        assertFalse(reader.isSkipSheet("AMST_STATUS"));
        assertFalse(reader.isSkipSheet("9MST_STATUS"));
        assertFalse(reader.isSkipSheet("#MST_STATUS"));
    }

    protected DfXlsReader createXlsReader(Pattern skipSheetPattern) {
        return new DfXlsReaderEmpty(new DfFlexibleNameMap<String, String>(),
                new DfFlexibleNameMap<String, List<String>>(), skipSheetPattern);
    }

    protected static class DfXlsReaderEmpty extends DfXlsReader {

        public DfXlsReaderEmpty(DfFlexibleNameMap<String, String> tableNameMap,
                DfFlexibleNameMap<String, List<String>> notTrimTableColumnMap, Pattern skipSheetPattern) {
            super(new ByteArrayInputStream(new byte[] {}), tableNameMap, notTrimTableColumnMap, skipSheetPattern);
        }

        @Override
        protected void setupWorkbook(InputStream in) {
            // Nothing!
        }
    }
}
