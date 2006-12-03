package org.seasar.dbflute.unit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfConfigProvider;
import org.seasar.dbflute.DfDBFluteProvider;
import org.seasar.dbflute.config.DfAdditionalForeignKeyConfigTest;
import org.seasar.framework.container.S2Container;

public class DfDBFluteTestCase {

    protected static final Log _log = LogFactory.getLog(DfAdditionalForeignKeyConfigTest.class);

    public DfDBFluteTestCase() {
    }

    protected Object getComponent(Class type) {
        return DfDBFluteProvider.getComponent(type);
    }

    protected Object getConfig(Class type) {
        return DfConfigProvider.getComponent(type);
    }
}
