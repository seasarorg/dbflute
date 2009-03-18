package org.seasar.dbflute.cbean;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.4 (2009/03/18 Wednesday)
 */
public class AbstractConditionQueryTest extends PlainTestCase {

    public void test_splitInScopeValue_basic() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");
        value.add("6");
        value.add("7");

        // ## Act ##
        List<List<?>> actual = AbstractConditionQuery.splitInScopeValue(value, 3);

        // ## Assert ##
        assertEquals(3, actual.size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(1).get(0));
        assertEquals("5", actual.get(1).get(1));
        assertEquals("6", actual.get(1).get(2));
        assertEquals("7", actual.get(2).get(0));
    }

    public void test_splitInScopeValue_just() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");

        // ## Act ##
        List<List<?>> actual = AbstractConditionQuery.splitInScopeValue(value, 4);

        // ## Assert ##
        assertEquals(1, actual.size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(0).get(3));
    }
}
