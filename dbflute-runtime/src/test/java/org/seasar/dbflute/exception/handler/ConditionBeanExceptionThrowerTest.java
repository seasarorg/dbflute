package org.seasar.dbflute.exception.handler;

import org.seasar.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ConditionBeanExceptionThrowerTest extends PlainTestCase {

    public void test_throwSetupSelectAfterUnionException() {
        // ## Arrange ##
        String className = "foo";
        String foreignPropertyName = "bar";

        // ## Act ##
        try {
            createTarget().throwSetupSelectAfterUnionException(className, foreignPropertyName);

            // ## Assert ##
            fail();
        } catch (SetupSelectAfterUnionException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains(className));
            String methodName = "setupSelect_" + Srl.initCap(foreignPropertyName) + "()";
            assertTrue(e.getMessage().contains(methodName));
        }
    }

    protected ConditionBeanExceptionThrower createTarget() {
        return new ConditionBeanExceptionThrower();
    }
}
