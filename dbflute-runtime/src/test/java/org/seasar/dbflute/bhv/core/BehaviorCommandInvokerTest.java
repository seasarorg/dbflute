package org.seasar.dbflute.bhv.core;

import java.util.HashSet;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.FetchNarrowingBeanContext;
import org.seasar.dbflute.jdbc.SqlResultHandler;
import org.seasar.dbflute.mock.MockConditionBean;
import org.seasar.dbflute.mock.MockOutsideSqlContext;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.resource.InternalMapContext;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.1 (2009/02/03 Tuesday)
 */
public class BehaviorCommandInvokerTest extends PlainTestCase {

    public void test_clearContext() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        OutsideSqlContext.setOutsideSqlContextOnThread(new MockOutsideSqlContext());
        FetchNarrowingBeanContext.setFetchNarrowingBeanOnThread(new MockConditionBean());
        ConditionBeanContext.setConditionBeanOnThread(new MockConditionBean());
        ConditionBeanContext.setEntityRowHandlerOnThread(new EntityRowHandler<Entity>() {
            public void handle(Entity entity) {
            }
        });
        InternalMapContext.setObject("dummy", new Object());
        ResourceContext.setResourceContextOnThread(new ResourceContext());

        assertTrue(OutsideSqlContext.isExistOutsideSqlContextOnThread());
        assertTrue(FetchNarrowingBeanContext.isExistFetchNarrowingBeanOnThread());
        assertTrue(ConditionBeanContext.isExistConditionBeanOnThread());
        assertTrue(ConditionBeanContext.isExistEntityRowHandlerOnThread());
        assertTrue(InternalMapContext.isExistInternalMapContextOnThread());
        assertTrue(ResourceContext.isExistResourceContextOnThread());

        // ## Act ##
        invoker.clearContext();

        // ## Assert ##
        assertFalse(OutsideSqlContext.isExistOutsideSqlContextOnThread());
        assertFalse(FetchNarrowingBeanContext.isExistFetchNarrowingBeanOnThread());
        assertFalse(ConditionBeanContext.isExistConditionBeanOnThread());
        assertFalse(ConditionBeanContext.isExistEntityRowHandlerOnThread());
        assertFalse(InternalMapContext.isExistInternalMapContextOnThread());
        assertFalse(ResourceContext.isExistResourceContextOnThread());
    }

    public void test_deriveCommandBeforeAfterTimeIfNeeds() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();

        // ## Act & Assert ##
        assertEquals(0, invoker.deriveCommandBeforeAfterTimeIfNeeds(false, false));
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(true, false) > 0);
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(false, true) > 0);
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(true, true) > 0);
    }

    public void test_callbackSqlResultHanler_basic() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        final long before = 123;
        final long after = 456;
        final Object ret = new Object();
        final HashSet<String> markSet = new HashSet<String>();
        try {
            InternalMapContext.setObject("df:DisplaySql", "select ...");

            // ## Act & Assert ##
            invoker.callbackSqlResultHanler(true, new SqlResultHandler() {
                public void handle(Object result, String displaySql, long actualBefore, long actualAfter) {
                    assertEquals(ret, result);
                    assertEquals("select ...", displaySql);
                    assertEquals(before, actualBefore);
                    assertEquals(after, actualAfter);
                    markSet.add("handle()");
                    log(result + ":" + displaySql + ":" + actualBefore + ":" + actualAfter);
                }
            }, ret, before, after);
            assertTrue(markSet.size() == 1);
            assertTrue(markSet.contains("handle()"));
        } finally {
            InternalMapContext.clearInternalMapContextOnThread();
        }
    }

    public void test_callbackSqlResultHanler_notExistsDisplaySql() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        final long before = 123;
        final long after = 456;
        final Object ret = new Object();
        final HashSet<String> markSet = new HashSet<String>();
        try {
            // ## Act & Assert ##
            invoker.callbackSqlResultHanler(true, new SqlResultHandler() {
                public void handle(Object result, String displaySql, long actualBefore, long actualAfter) {
                    assertEquals(ret, result);
                    assertEquals("select ...", displaySql);
                    assertEquals(before, actualBefore);
                    assertEquals(after, actualAfter);
                    markSet.add("handle()");
                    log(result + ":" + displaySql + ":" + actualBefore + ":" + actualAfter);
                }
            }, ret, before, after);
            assertTrue(markSet.size() == 0);
            assertFalse(markSet.contains("handle()"));
        } finally {
            InternalMapContext.clearInternalMapContextOnThread();
        }
    }

    public void test_systemTime() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();

        // ## Act & Assert ##
        assertTrue(invoker.systemTime() > 0);
    }
}
