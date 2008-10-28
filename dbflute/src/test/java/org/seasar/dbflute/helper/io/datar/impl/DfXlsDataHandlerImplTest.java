package org.seasar.dbflute.helper.io.datar.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.seasar.dbflute.helper.io.data.impl.DfXlsDataHandlerImpl;

public class DfXlsDataHandlerImplTest {

    private static final Log _log = LogFactory.getLog(DfXlsDataHandlerImplTest.class);

    @Test
    public void test_DfXlsDataHandlerImpl_filterTimestampValue() {
        final DfXlsDataHandlerImpl dfXlsDataHandlerImpl = new DfXlsDataHandlerImpl();
        final String filteredTimestampValue = dfXlsDataHandlerImpl.filterTimestampValue("2007/01/01");
        assertEquals("2007-01-01 00:00:00", filteredTimestampValue);
    }

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

    protected void log(Object msg) {
        _log.debug(msg);
    }
}
