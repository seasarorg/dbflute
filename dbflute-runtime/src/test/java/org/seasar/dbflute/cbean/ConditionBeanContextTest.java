package org.seasar.dbflute.cbean;

import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/09 Thursday)
 */
public class ConditionBeanContextTest extends PlainTestCase {

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    // -----------------------------------------------------
    //                                                Entity
    //                                                ------
    public void test_throwEntityAlreadyDeletedException() {
        try {
            ConditionBeanContext.throwEntityAlreadyDeletedException("foo");
            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    public void test_throwEntityDuplicatedException() {
        try {
            ConditionBeanContext.throwEntityDuplicatedException("123", "foo", new Exception());
            ;
            fail();
        } catch (EntityDuplicatedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("123"));
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    // -----------------------------------------------------
    //                                         Set up Select
    //                                         -------------
    public void test_throwSetupSelectAfterUnionException() {
        // ## Arrange ##
        String className = "foo";
        String foreignPropertyName = "bar";

        // ## Act ##
        try {
            ConditionBeanContext.throwSetupSelectAfterUnionException(className, foreignPropertyName);

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
}
