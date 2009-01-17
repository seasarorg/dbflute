package org.seasar.dbflute.unit;

import java.sql.Timestamp;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The test base of simple test for Basic Example.
 * @author jflute
 * @since 0.6.0 (2008/01/17 Thursday)
 */
public abstract class PlainTestCase extends TestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for sub class. */
    private static final Log _log = LogFactory.getLog(PlainTestCase.class);

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void log(Object msg) {
        _log.debug(msg);
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    protected static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
