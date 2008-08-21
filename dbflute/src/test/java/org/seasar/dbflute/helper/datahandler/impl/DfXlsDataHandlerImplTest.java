package org.seasar.dbflute.helper.datahandler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.seasar.dbflute.DfDBFluteTestCase;

public class DfXlsDataHandlerImplTest extends DfDBFluteTestCase {

    @Test
    public void test_DfXlsDataHandlerImpl_filterTimestampValue() {
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();
        final String filteredTimestampValue = dfXlsDataHandlerImpl.filterTimestampValue("2007/01/01");
        assertEquals("2007-01-01 00:00:00", filteredTimestampValue);
    }

    @Test
    public void test_DfXlsDataHandlerImpl_isCommentOutSheet() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();

        // ## Act & Assert ##
        assertTrue(dfXlsDataHandlerImpl.isCommentOutSheet("#MST_STATUS"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("MST_STATUS"));
    }

    @Test
    public void test_DfXlsDataHandlerImpl_isSkipSheet() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();
        dfXlsDataHandlerImpl.setSkipSheet("MST.+");

        // ## Act & Assert ##
        assertTrue(dfXlsDataHandlerImpl.isSkipSheet("MST_STATUS"));
        assertTrue(dfXlsDataHandlerImpl.isSkipSheet("MST_"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("MST"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("MS_STATUS"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("AMST_STATUS"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("9MST_STATUS"));
        assertFalse(dfXlsDataHandlerImpl.isSkipSheet("#MST_STATUS"));
    }

    @Test
    public void test_DfXlsDataHandlerImpl_setSkipSheet_SyntaxError() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();

        // ## Act & Assert ##
        try {
            dfXlsDataHandlerImpl.setSkipSheet("MST.*+`*`+*P*`+*}+");
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
