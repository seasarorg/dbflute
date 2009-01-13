package org.dbflute;

import org.dbflute.jdbc.SqlLogHandler;

/**
 * The context of callback.
 * @author DBFlute(AutoGenerator)
 */
public class CallbackContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<CallbackContext> _threadLocal = new ThreadLocal<CallbackContext>();

    /**
     * Get callback-context on thread.
     * @return The context of callback. (Nullable)
     */
    public static CallbackContext getCallbackContextOnThread() {
        return (CallbackContext) _threadLocal.get();
    }

    /**
     * Set callback-context on thread.
     * @param callbackContext The context of callback. (NotNull)
     */
    public static void setCallbackContextOnThread(CallbackContext callbackContext) {
        if (callbackContext == null) {
            String msg = "The argument[callbackContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(callbackContext);
    }

    /**
     * Is existing callback-context on thread?
     * @return Determination.
     */
    public static boolean isExistCallbackContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear callback-context on thread.
     */
    public static void clearCallbackContextOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SqlLogHandler _sqlLogHandler;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SqlLogHandler getSqlLogHandler() {
        return _sqlLogHandler;
    }

    public void setSqlLogHandler(SqlLogHandler sqlLogHandler) {
        this._sqlLogHandler = sqlLogHandler;
    }
}
