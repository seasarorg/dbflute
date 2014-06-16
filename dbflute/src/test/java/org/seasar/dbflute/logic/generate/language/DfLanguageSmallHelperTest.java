package org.seasar.dbflute.logic.generate.language;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 * @since 1.0.6A (2014/06/16 Monday)
 */
public class DfLanguageSmallHelperTest extends PlainTestCase {

    public void test_extractGenericClassElement_basic() throws Exception {
        // ## Arrange ##
        DfLanguageSmallHelper helper = new DfLanguageSmallHelper();

        // ## Act ##
        String element = helper.extractGenericClassElement("List", "List<String>", "<", ">");

        // ## Assert ##
        assertEquals("String", element);
    }

    public void test_extractGenericClassElement_nested() throws Exception {
        // ## Arrange ##
        DfLanguageSmallHelper helper = new DfLanguageSmallHelper();

        // ## Act ##
        String element = helper.extractGenericClassElement("List", "List<Map<String, Object>>", "<", ">");

        // ## Assert ##
        assertEquals("Map<String, Object>", element);
    }
}
