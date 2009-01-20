package org.seasar.dbflute;

/**
 * @author jflute
 */
public class QLog {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory.getLog(QLog.class);
	protected static boolean _queryLogLevelInfo;
	protected static boolean _locked = true;

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
	public static void log(String sql) {// Very Internal
		if (isQueryLogLevelInfo()) {
	        _log.info(sql);
		} else {
	        _log.debug(sql);
		}
	}
	
	public static boolean isLogEnabled() {
		if (isQueryLogLevelInfo()) {
	        return _log.isInfoEnabled();
		} else {
	        return _log.isDebugEnabled();
		}
	}
	
	protected static boolean isQueryLogLevelInfo() {
	    return _queryLogLevelInfo;
	}

	public static void setQueryLogLevelInfo(boolean queryLogLevelInfo) {
	    assertNotLocked();
		if (_log.isInfoEnabled()) {
		    _log.info("...Setting queryLogLevelInfo: " + queryLogLevelInfo);
		}
	    _queryLogLevelInfo = queryLogLevelInfo;
	}
	
    // ===================================================================================
    //                                                                                Lock
    //                                                                                ====
	public static boolean isLocked() {
	    return _locked;
	}
	
	public static void lock() {
		if (_log.isInfoEnabled()) {
		    _log.info("...Locking the log object for query!");
		}
	    _locked = true;
	}
	
	public static void unlock() {
		if (_log.isInfoEnabled()) {
		    _log.info("...Unlocking the log object for query!");
		}
	    _locked = false;
	}
	
	protected static void assertNotLocked() {
	    if (!isLocked()) {
		    return;
		}
		String msg = "The QLog is locked! Don't access at this timing!";
		throw new IllegalStateException(msg);
	}
}
