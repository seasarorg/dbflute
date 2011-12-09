package org.seasar.dbflute.cbean.coption;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class RangeOfOptionTest extends PlainTestCase {

    public void test_ConditionKey_basic() throws Exception {
        // ## Arrange ##
        RangeOfOption option = new RangeOfOption();

        // ## Act & Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getMaxNumberConditionKey());
        option.greaterThan();
        assertEquals(ConditionKey.CK_GREATER_THAN, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getMaxNumberConditionKey());
        option.lessThan();
        assertEquals(ConditionKey.CK_GREATER_THAN, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN, option.getMaxNumberConditionKey());
    }

    public void test_ConditionKey_orIsNull() throws Exception {
        // ## Arrange ##
        RangeOfOption option = new RangeOfOption();

        // ## Act & Assert ##
        assertEquals(ConditionKey.CK_GREATER_EQUAL, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL, option.getMaxNumberConditionKey());
        option.orIsNull();
        assertEquals(ConditionKey.CK_GREATER_EQUAL_OR_IS_NULL, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_EQUAL_OR_IS_NULL, option.getMaxNumberConditionKey());
        option.greaterThan().lessThan();
        assertEquals(ConditionKey.CK_GREATER_THAN_OR_IS_NULL, option.getMinNumberConditionKey());
        assertEquals(ConditionKey.CK_LESS_THAN_OR_IS_NULL, option.getMaxNumberConditionKey());
    }
}
