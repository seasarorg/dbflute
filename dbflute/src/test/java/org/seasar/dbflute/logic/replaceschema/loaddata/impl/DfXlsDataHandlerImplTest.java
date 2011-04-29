package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMeta;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfXlsDataHandlerImplTest extends PlainTestCase {

    // ===================================================================================
    //                                                                    Process per Type
    //                                                                    ================
    // -----------------------------------------------------
    //                                     NotNull NotString
    //                                     -----------------
    @Test
    public void test_DfXlsDataHandlerImpl_isNotNullNotString() {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = createHandler();

        // ## Act & Assert ##
        assertFalse(impl.isNotNullNotString(null));
        assertFalse(impl.isNotNullNotString("abc"));
        assertTrue(impl.isNotNullNotString(new Date()));
        assertTrue(impl.isNotNullNotString(new Timestamp(System.currentTimeMillis())));
    }

    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    @Test
    public void test_processBoolean() throws Exception {
        // ## Arrange ##
        final DfXlsDataHandlerImpl impl = new DfXlsDataHandlerImpl(null) {
            @Override
            protected Class<?> getBindType(String tableName, DfColumnMeta columnMetaInfo) {
                return BigDecimal.class;
            }
        };
        Map<String, DfColumnMeta> columnMetaInfoMap = StringKeyMap.createAsCaseInsensitive();
        DfColumnMeta info = new DfColumnMeta();
        info.setColumnName("foo");
        info.setColumnSize(3);
        info.setJdbcDefValue(Types.NUMERIC);
        columnMetaInfoMap.put("foo", info);

        // ## Act ##
        boolean actual = impl.processBoolean("tbl", "foo", "0", null, null, 0, columnMetaInfoMap);

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
        final DfXlsDataHandlerImpl impl = createHandler();

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

    protected DfXlsDataHandlerImpl createHandler() {
        return new DfXlsDataHandlerImpl(null);
    }
}
