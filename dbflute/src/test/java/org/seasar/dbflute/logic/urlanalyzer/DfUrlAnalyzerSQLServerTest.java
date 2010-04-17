package org.seasar.dbflute.logic.urlanalyzer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfUrlAnalyzerSQLServerTest extends PlainTestCase {

    @Test
    public void test_extractCatalog_basic() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sqlserver://localhost:1433;DatabaseName=exampledb;");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    @Test
    public void test_extractCatalog_option() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sqlserver://localhost:1433;DatabaseName=exampledb;charSet=UTF-8");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    @Test
    public void test_extractCatalog_nohost() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sqlserver:;DatabaseName=exampledb;");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    protected DfUrlAnalyzer createTarget(String url) {
        return new DfUrlAnalyzerSQLServer(url);
    }
}
