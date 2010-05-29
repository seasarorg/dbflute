package org.seasar.dbflute.twowaysql;

import org.seasar.dbflute.twowaysql.node.SqlNode;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/08 Wednesday)
 */
public class SqlAnalyzerTest extends PlainTestCase {

    public void test_createSqlNode() {
        // ## Arrange ##
        SqlAnalyzer analyzer = new SqlAnalyzer("foobar", false);

        // ## Act ##
        SqlNode node = analyzer.createSqlNode("foo");

        // ## Assert ##
        assertEquals("foo", node.getSql());
    }

    // *detail tests for analyze() are moved to node tests
}
