package org.seasar.dbflute.unit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfComponentProvider;

public class DfDBFluteTestCase {

    protected static final Log _log = LogFactory.getLog(DfDBFluteTestCase.class);

    public DfDBFluteTestCase() {
    }

    protected Object getComponent(Class type) {
        return DfComponentProvider.getComponent(type);
    }
}
