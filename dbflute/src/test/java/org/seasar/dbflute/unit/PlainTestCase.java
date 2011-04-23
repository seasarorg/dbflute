package org.seasar.dbflute.unit;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.seasar.dbflute.util.DfResourceUtil;

/**
 * @author jflute
 */
public abstract class PlainTestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(PlainTestCase.class);

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void log(Object msg) {
        _log.debug(msg);
    }

    protected Date currentDate() {
        return new Date();
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                           IO Helper
    //                                                                           =========
    protected String getCanonicalPath() {
        final File buildDir = DfResourceUtil.getBuildDir(this.getClass());
        final String canonicalPath;
        try {
            canonicalPath = buildDir.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return canonicalPath;
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
