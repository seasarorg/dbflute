package org.seasar.dbflute.jdbc;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class StatementConfigTest extends PlainTestCase {

    public void test_buildResultSetTypeDisp_basic() throws Exception {
        StatementConfig config = new StatementConfig();
        assertEquals("default", config.buildResultSetTypeDisp());
        config.typeForwardOnly();
        assertEquals("forward", config.buildResultSetTypeDisp());
        config.typeScrollInsensitive();
        assertEquals("scroll(ins)", config.buildResultSetTypeDisp());
        config.typeScrollSensitive();
        assertEquals("scroll(sen)", config.buildResultSetTypeDisp());
    }
}
