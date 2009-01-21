/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute;

import org.seasar.dbflute.jdbc.SqlLogHandler;

/**
 * The context of callback.
 * @author jflute
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
