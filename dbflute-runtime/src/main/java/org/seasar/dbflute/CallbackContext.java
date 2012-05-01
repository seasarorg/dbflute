/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.bhv.core.BehaviorCommandHook;
import org.seasar.dbflute.jdbc.SqlLogHandler;
import org.seasar.dbflute.jdbc.SqlResultHandler;

/**
 * The context of call-back in DBFlute internal logic.
 * @author jflute
 */
public class CallbackContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<CallbackContext> _threadLocal = new ThreadLocal<CallbackContext>();

    /**
     * Get call-back context on thread.
     * @return The context of call-back. (NullAllowed)
     */
    public static CallbackContext getCallbackContextOnThread() {
        return _threadLocal.get();
    }

    /**
     * Set call-back context on thread. <br />
     * You can use setting methods per interface instead of this method.
     * @param callbackContext The context of call-back. (NotNull)
     */
    public static void setCallbackContextOnThread(CallbackContext callbackContext) {
        if (callbackContext == null) {
            String msg = "The argument[callbackContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(callbackContext);
    }

    /**
     * Is existing call-back context on thread? <br />
     * You can use determination methods per interface instead of this method.
     * @return The determination, true or false.
     */
    public static boolean isExistCallbackContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear call-back context on thread. <br />
     * Basically you should call other clear methods per interfaces,
     * because this clear method clears all interfaces. 
     */
    public static void clearCallbackContextOnThread() {
        _threadLocal.set(null);
    }

    // -----------------------------------------------------
    //                                   BehaviorCommandHook
    //                                   -------------------
    /**
     * Set the hook interface of behavior commands. <br />
     * This hook interface is called back before executing behavior commands and finally. <br /> 
     * The hook methods may be called by nested process
     * so you should pay attention to it when you implements this.
     * <pre>
     * context.setBehaviorCommandHook(new BehaviorCommandHook() {
     *     public void hookBefore(BehaviorCommandMeta meta) {
     *         // You can implement your favorite call-back here.
     *     }
     *     public void hookFinally(BehaviorCommandMeta meta, RuntimeException cause) {
     *         // You can implement your favorite call-back here.
     *     }
     * });
     * </pre>
     * @param behaviorCommandHook The hook interface of behavior commands. (NullAllowed)
     */
    public static void setBehaviorCommandHookOnThread(BehaviorCommandHook behaviorCommandHook) {
        final CallbackContext context = getOrCreateContext();
        context.setBehaviorCommandHook(behaviorCommandHook);
    }

    /**
     * Is existing the hook interface of behavior commands on thread?
     * @return The determination, true or false.
     */
    public static boolean isExistBehaviorCommandHookOnThread() {
        return isExistCallbackContextOnThread() && getCallbackContextOnThread().getBehaviorCommandHook() != null;
    }

    /**
     * Clear the hook interface of behavior commands from call-back context on thread. <br />
     * If the call-back context has had the interface only, the context will also removed from thread.
     */
    public static void clearBehaviorCommandHookOnThread() {
        if (isExistCallbackContextOnThread()) {
            final CallbackContext context = getCallbackContextOnThread();
            context.setBehaviorCommandHook(null);
            if (!context.hasAnyInterface()) {
                clearCallbackContextOnThread();
            }
        }
    }

    // -----------------------------------------------------
    //                                         SqlLogHandler
    //                                         -------------
    /**
     * Set the handler of SQL log. <br />
     * This handler is called back before executing the SQL.
     * <pre>
     * context.setSqlLogHandler(new SqlLogHandler() {
     *     public void handle(SqlLogInfo info) {
     *         // You can get your SQL string here.
     *     }
     * });
     * </pre>
     * @param sqlLogHandler The handler of SQL log. (NullAllowed)
     */
    public static void setSqlLogHandlerOnThread(SqlLogHandler sqlLogHandler) {
        final CallbackContext context = getOrCreateContext();
        context.setSqlLogHandler(sqlLogHandler);
    }

    /**
     * Is existing the handler of SQL log on thread?
     * @return The determination, true or false.
     */
    public static boolean isExistSqlLogHandlerOnThread() {
        return isExistCallbackContextOnThread() && getCallbackContextOnThread().getSqlLogHandler() != null;
    }

    /**
     * Clear the handler of SQL log from call-back context on thread. <br />
     * If the call-back context has had the interface only, the context will also removed from thread.
     */
    public static void clearSqlLogHandlerOnThread() {
        if (isExistCallbackContextOnThread()) {
            final CallbackContext context = getCallbackContextOnThread();
            context.setSqlLogHandler(null);
            if (!context.hasAnyInterface()) {
                clearCallbackContextOnThread();
            }
        }
    }

    // -----------------------------------------------------
    //                                      SqlResultHandler
    //                                      ----------------
    /**
     * Set the handler of SQL result. <br />
     * This handler is called back before executing the SQL. 
     * <pre>
     * context.setSqlResultHandler(new SqlResultHandler() {
     *     public void handle(SqlResultInfo info) {
     *         // You can get your SQL result information here.
     *     }
     * });
     * </pre>
     * @param sqlResultHandler The handler of SQL result. (NullAllowed)
     */
    public static void setSqlResultHandlerOnThread(SqlResultHandler sqlResultHandler) {
        final CallbackContext context = getOrCreateContext();
        context.setSqlResultHandler(sqlResultHandler);
    }

    /**
     * Is existing the handler of SQL result on thread?
     * @return The determination, true or false.
     */
    public static boolean isExistSqlResultHandlerOnThread() {
        return isExistCallbackContextOnThread() && getCallbackContextOnThread().getSqlResultHandler() != null;
    }

    /**
     * Clear the handler of SQL result from call-back context on thread. <br />
     * If the call-back context has had the interface only, the context will also removed from thread.
     */
    public static void clearSqlResultHandlerOnThread() {
        if (isExistCallbackContextOnThread()) {
            final CallbackContext context = getCallbackContextOnThread();
            context.setSqlResultHandler(null);
            if (!context.hasAnyInterface()) {
                clearCallbackContextOnThread();
            }
        }
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected static CallbackContext getOrCreateContext() {
        if (isExistCallbackContextOnThread()) {
            return getCallbackContextOnThread();
        } else {
            final CallbackContext context = new CallbackContext();
            setCallbackContextOnThread(context);
            return context;
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BehaviorCommandHook _behaviorCommandHook;
    protected SqlLogHandler _sqlLogHandler;
    protected SqlResultHandler _sqlResultHandler;

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasAnyInterface() {
        return _behaviorCommandHook != null || _sqlLogHandler != null || _sqlResultHandler != null;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                   BehaviorCommandHook
    //                                   -------------------
    public BehaviorCommandHook getBehaviorCommandHook() {
        return _behaviorCommandHook;
    }

    /**
     * Set the hook interface of behavior commands. <br />
     * This hook interface is called back before executing behavior commands and finally. <br /> 
     * The hook methods may be called by nested process
     * so you should pay attention to it when you implements this.
     * <pre>
     * context.setBehaviorCommandHook(new BehaviorCommandHook() {
     *     public void hookBefore(BehaviorCommandMeta meta) {
     *         // You can implement your favorite call-back here.
     *     }
     *     public void hookFinally(BehaviorCommandMeta meta, RuntimeException cause) {
     *         // You can implement your favorite call-back here.
     *     }
     * });
     * </pre>
     * @param behaviorCommandHook The hook interface of behavior commands. (NullAllowed)
     */
    public void setBehaviorCommandHook(BehaviorCommandHook behaviorCommandHook) {
        this._behaviorCommandHook = behaviorCommandHook;
    }

    // -----------------------------------------------------
    //                                         SqlLogHandler
    //                                         -------------
    public SqlLogHandler getSqlLogHandler() {
        return _sqlLogHandler;
    }

    /**
     * Set the handler of SQL log. <br />
     * This handler is called back before executing the SQL. 
     * <pre>
     * context.setSqlLogHandler(new SqlLogHandler() {
     *     public void handle(String executedSql, String displaySql
     *                      , Object[] args, Class&lt;?&gt;[] argTypes) {
     *         // You can get your SQL string here.
     *     }
     * });
     * </pre>
     * @param sqlLogHandler The handler of SQL log. (NullAllowed)
     */
    public void setSqlLogHandler(SqlLogHandler sqlLogHandler) {
        this._sqlLogHandler = sqlLogHandler;
    }

    // -----------------------------------------------------
    //                                      SqlResultHandler
    //                                      ----------------
    public SqlResultHandler getSqlResultHandler() {
        return _sqlResultHandler;
    }

    /**
     * Set the handler of SQL result. <br />
     * This handler is called back before executing the SQL. 
     * <pre>
     * context.setSqlResultHandler(new SqlResultHandler() {
     *     public void handle(SqlResultInfo info) {
     *         // You can get your SQL result information here.
     *     }
     * });
     * </pre>
     * @param sqlResultHandler The handler of SQL result. (NullAllowed)
     */
    public void setSqlResultHandler(SqlResultHandler sqlResultHandler) {
        this._sqlResultHandler = sqlResultHandler;
    }
}
