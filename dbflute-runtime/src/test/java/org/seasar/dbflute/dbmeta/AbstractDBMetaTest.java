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
public class AbstractDBMetaTest extends PlainTestCase {

    public void test_MapStringValueAnalyzer_analyzeOther() throws Exception {
        // ## Arrange ##
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("FOO_NAME", "bar");
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(valueMap);
        analyzer.init("FOO_NAME", "fooName", "FooName");

        // ## Act ##
        MockClassification actual = analyzer.analyzeOther(MockClassification.class);

        // ## Assert ##
        assertEquals(MockClassification.BAR, actual);
    }

    private static enum MockClassification implements Classification {
        FOO, BAR;
        public String alias() {
            return null;
        }

        public String code() {
            return null;
        }

        public static MockClassification codeOf(Object obj) {
            return obj instanceof String && obj.equals("bar") ? BAR : null;
        }
    }
}
