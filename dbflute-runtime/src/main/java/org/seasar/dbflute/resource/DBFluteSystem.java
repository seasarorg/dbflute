package org.seasar.dbflute.resource;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 */
public class DBFluteSystem {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteSystem.class);

    // ===================================================================================
    //                                                                    Option Attribute
    //                                                                    ================
    protected static DBFluteCurrentProvider _currentProvider;

    protected static boolean _locked = true;

    // ===================================================================================
    //                                                                      Line Separator
    //                                                                      ==============
    public static String getBasicLn() {
        return "\n"; // LF is basic here
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
        // The 'CR + LF' causes many trouble all over the world.
        //  e.g. Oracle stored procedure
        // - - - - - - - - - -/
    }

    // unused on DBFlute
    //public static String getSystemLn() {
    //    return System.getProperty("line.separator");
    //}

    // ===================================================================================
    //                                                                        Current Time
    //                                                                        ============
    public static Date currentDate() {
        return new Date(currentTimeMillis());
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(currentTimeMillis());
    }

    public static long currentTimeMillis() {
        final long millis;
        if (_currentProvider != null) {
            millis = _currentProvider.currentTimeMillis();
        } else {
            millis = System.currentTimeMillis();
        }
        return millis;
    }

    public static interface DBFluteCurrentProvider {
        long currentTimeMillis();
    }

    // ===================================================================================
    //                                                                     Option Accessor
    //                                                                     ===============
    public static void xlock() {
        _locked = true;
    }

    public static void xunlock() {
        _locked = false;
    }

    protected static void assertUnlocked() {
        if (_locked) {
            String msg = "DBFluteSystem was locked.";
            throw new IllegalStateException(msg);
        }
    }

    public static void xsetDBFluteCurrentProvider(DBFluteCurrentProvider currentProvider) {
        assertUnlocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting DBFluteCurrentProvider: " + currentProvider);
        }
        _currentProvider = currentProvider;
        xlock();
    }
}
