package org.seasar.dbflute.logic.urlanalyzer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.jdbc.urlanalyzer.DfUrlAnalyzerMSAccess;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfUrlAnalyzerMSAccessTest extends PlainTestCase {

    @Test
    public void test_extractCatalog() throws Exception {
        // ## Arrange ##
        DfUrlAnalyzer analyzer = createTarget("jdbc:odbc:exampledb");

        // ## Act ##
        String catalog = analyzer.extractCatalog();

        // ## Assert ##
        assertEquals("exampledb", catalog);
    }

    protected DfUrlAnalyzer createTarget(String url) {
        return new DfUrlAnalyzerMSAccess(url);
    }
}
