package org.seasar.dbflute.dbway;

import junit.framework.TestCase;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/12 Thursday)
 */
public class WayOfMySQLTest extends TestCase {

    public void test_escapeLiteralValue() throws Exception {
        // ## Arrange ##
        WayOfMySQL way = new WayOfMySQL();

        // ## Act & Assert ##
        assertEquals("foo", way.escapeLiteralValue("foo"));
        assertEquals("f\'oo", way.escapeLiteralValue("f'oo"));
        assertEquals("f\\\\\'oo", way.escapeLiteralValue("f\\'oo"));
        assertEquals("f\\\\\\\\oo", way.escapeLiteralValue("f\\\\oo"));
        assertEquals("f\\\\o\'o", way.escapeLiteralValue("f\\o'o"));
        assertEquals("f\\\\o\'o\'", way.escapeLiteralValue("f\\o'o'"));
    }
}
