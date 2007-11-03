package org.seasar.dbflute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author jflute
 * @since 0.5.7 (2007/11/03 Saturday)
 */
public abstract class DfDBFluteTestBase {

    private static final Log _log = LogFactory.getLog(DfDBFluteTestBase.class);

    protected void log(Object msg) {
        _log.debug(msg);
    }
}
