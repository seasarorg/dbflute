package org.seasar.dbflute.bhv.core;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.cbean.EntityRowHandler;
import org.seasar.dbflute.cbean.FetchNarrowingBeanContext;
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
}
