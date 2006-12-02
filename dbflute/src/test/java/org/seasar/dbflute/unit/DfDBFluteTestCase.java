package org.seasar.dbflute.unit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.seasar.dbflute.util.config.DfAdditionalForeignKeyConfigTest;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class DfDBFluteTestCase {

    protected static final Log _log = LogFactory.getLog(DfAdditionalForeignKeyConfigTest.class);
    
    private S2Container _container;

    public DfDBFluteTestCase() {
    }

    @Before
    public void setUp() {
        SingletonS2ContainerFactory.init();
        _container = SingletonS2ContainerFactory.getContainer();
    }

    protected S2Container getContainer() {
        return _container;
    }
    
    protected Object getComponent(Class type) {
        return _container.getComponent(type);
    }
}
