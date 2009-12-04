package org.seasar.dbflute.helper.io.data.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfXlsDataHandlerImplTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    @Test
    public void test_removeDoubleQuotation() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl();

        // ## Act & Assert ##
        assertEquals("aaa", impl.removeDoubleQuotation("\"aaa\""));
        assertEquals("a", impl.removeDoubleQuotation("\"a\""));
        assertEquals("", impl.removeDoubleQuotation("\"\""));
    }

    // ===================================================================================
    //                                                                    Process per Type
    //                                                                    ================
    // -----------------------------------------------------
    //                                     NotNull NotString
    //                                     -----------------
    @Test
    public void test_DfXlsDataHandlerImpl_isNotNullNotString() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl();

        // ## Act & Assert ##
        assertFalse(impl.isNotNullNotString(null));
        assertFalse(impl.isNotNullNotString("abc"));
        assertTrue(impl.isNotNullNotString(new Date()));
        assertTrue(impl.isNotNullNotString(new Timestamp(System.currentTimeMillis())));
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    @Test
    public void test_filterTimeValue() {
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl();
        assertEquals("12:34:56", impl.filterTimeValue("12:34:56"));
        assertEquals("02:34:56", impl.filterTimeValue("2:34:56"));
        assertEquals("12:34:56", impl.filterTimeValue("12:34:56.123"));
        assertEquals("02:34:56", impl.filterTimeValue("2:34:56.123"));
    }

    @Test
    public void test_isTimeValue() {
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl();
        assertTrue(impl.isTimeValue("12:34:56"));
        assertTrue(impl.isTimeValue("23:42:35"));
        assertTrue(impl.isTimeValue("03:42:35"));
        assertTrue(impl.isTimeValue("3:42:35"));
        assertTrue(impl.isTimeValue("01:00:00"));
        assertTrue(impl.isTimeValue("00:00:00"));
        assertFalse(impl.isTimeValue("2007/12/12 12:34:56"));
        assertFalse(impl.isTimeValue("12:34:56.123"));
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    @Test
    public void test_filterTimestampValue() {
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();
        final String filteredTimestampValue = dfXlsDataHandlerImpl.filterTimestampValue("2007/01/01");
        assertEquals("2007-01-01 00:00:00", filteredTimestampValue);
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    @Test
    public void test_processBoolean() throws Exception {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl() {
            @Override
            protected Class<?> getColumnType4Judgement(DfColumnMetaInfo columnMetaInfo) {
                return BigDecimal.class;
            }
        };
        DfFlexibleMap<String, DfColumnMetaInfo> columnMetaInfoMap = new DfFlexibleMap<String, DfColumnMetaInfo>();
        DfColumnMetaInfo info = new DfColumnMetaInfo();
        info.setColumnName("foo");
        info.setColumnSize(3);
        info.setJdbcDefValue(Types.NUMERIC);
        columnMetaInfoMap.put("foo", info);

        // ## Act ##
        boolean actual = impl.processBoolean("foo", "0", null, 0, columnMetaInfoMap);

        // ## Assert ##
        log("actual=" + actual);
        assertFalse(actual);
    }

    // ===================================================================================
    //                                                                          Skip Sheet
    //                                                                          ==========
    @Test
    public void test_DfXlsDataHandlerImpl_setSkipSheet_SyntaxError() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl();

        // ## Act & Assert ##
        try {
            impl.setSkipSheet("MST.*+`*`+*P*`+*}+");
            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
            assertNotNull(e.getCause());
            log(e.getCause().getMessage());
            assertTrue(e.getCause() instanceof PatternSyntaxException);
        }
    }
}
