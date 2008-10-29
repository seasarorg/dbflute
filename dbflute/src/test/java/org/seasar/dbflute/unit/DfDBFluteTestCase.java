package org.seasar.dbflute.unit;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

/**
 * @author jflute
 */
public abstract class DfDBFluteTestCase {
    
    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfDBFluteTestCase.class);
    protected static final String PATH_TMP_DBFLUTE_TEST = "/tmp/dbflute/test";

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected void log(Object msg) {
        _log.debug(msg);
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    protected static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected <T> void assertListEmtpy(List<T> ls) {
        if (!ls.isEmpty()) {
            Assert.fail("The list shuold be empty: ls=" + ls);
        }
    }

    protected <T> void assertListNotEmtpy(List<T> ls) {
        if (ls.isEmpty()) {
            Assert.fail("The list shuold not be empty: ls=" + ls);
        }
    }
}
