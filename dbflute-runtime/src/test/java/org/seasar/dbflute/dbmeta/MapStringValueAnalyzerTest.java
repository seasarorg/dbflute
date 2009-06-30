package org.seasar.dbflute.dbmeta;

import java.util.HashMap;
import java.util.Map;

import org.seasar.dbflute.dbmeta.AbstractDBMeta.MapStringValueAnalyzer;
import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/30 Tuesday)
 */
public class MapStringValueAnalyzerTest extends PlainTestCase {

    public void test_analyzeOther() throws Exception {
        // ## Arrange ##
        Map<String, MockClassification> valueMap = new HashMap<String, MockClassification>();
        valueMap.put("FOO_NAME", new MockClassification());
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(valueMap);
        analyzer.init("FOO_NAME", "fooName", "FooName");

        // ## Act ##
        Object actual = analyzer.analyzeOther(MockClassification.class);

        // ## Assert ##
        assertEquals("codeOf", actual);
    }

    private static class MockClassification implements Classification {

        public String alias() {
            return null;
        }

        public String code() {
            return null;
        }

        public String name() {
            return null;
        }

        public static String codeOf(Object obj) {
            return "codeOf";
        }
    }
}
