package org.seasar.dbflute.properties.assistant.freegen.prop;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfPropTableLoaderTest extends PlainTestCase {

    @Test
    public void test_loadConvert() {
        // ## Arrange ##
        DfPropTableLoader loader = new DfPropTableLoader();
        String text = "\u938c\u5009\u306e\u3044\u306c";

        // ## Act ##
        String actual = loader.loadConvert(text);

        // ## Assert ##
        log(actual);
        assertEquals("鎌倉のいぬ", actual);
    }
}
