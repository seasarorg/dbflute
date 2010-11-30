package org.seasar.dbflute.logic.jdbc.urlanalyzer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/26 Friday)
 */
public class DfUrlAnalyzerSybaseTest extends PlainTestCase {

    @Test
    public void test_extractCatalog_basic() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sybase:Tds:localhost:2638/exampledb");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    @Test
    public void test_extractCatalog_option_ampersand() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sybase:Tds:localhost:2638/exampledb&charSet=UTF-8");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    @Test
    public void test_extractCatalog_option_question() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:sybase:Tds:localhost:5432/exampledb?charSet=UTF-8");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    protected DfUrlAnalyzer createTarget(String url) {
        return new DfUrlAnalyzerSybase(url);
    }
}
