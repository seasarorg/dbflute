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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The context of DB access.
 * @author jflute
 */
public class AccessContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<AccessContext> _threadLocal = new ThreadLocal<AccessContext>();

    /**
     * Get access-context on thread.
     * @return The context of DB access. (Nullable)
     */
    public static AccessContext getAccessContextOnThread() {
        return (AccessContext) _threadLocal.get();
    }

    /**
     * Set access-context on thread.
     * @param accessContext The context of DB access. (NotNull)
     */
    public static void setAccessContextOnThread(AccessContext accessContext) {
        if (accessContext == null) {
            String msg = "The argument[accessContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(accessContext);
    }

    /**
     * Is existing access-context on thread?
     * @return Determination.
     */
    public static boolean isExistAccessContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear access-context on thread.
     */
    public static void clearAccessContextOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                  Access Information
    //                                                                  ==================
    /**
     * Get access user on thread.
     * @return Access user. (NotNull)
     * @exception IllegalStateException When it couldn't get access user.
     */
    public static String getAccessUserOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessUser = userContextOnThread.getAccessUser();
            if (accessUser != null) {
                return accessUser;
            }
        }
        String msg;
        if (isExistAccessContextOnThread()) {
            msg = "The access user was not found in AccessContext on thread: " + getAccessContextOnThread();
        } else {
            msg = "The AccessContext was not found on thread!";
        }
        throw new IllegalStateException(msg);
    }

    /**
     * Get access process on thread.
     * @return Access process. (NotNull)
     * @exception IllegalStateException When it couldn't get access process.
     */
    public static String getAccessProcessOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessProcess = userContextOnThread.getAccessProcess();
            if (accessProcess != null) {
                return accessProcess;
            }
        }
        String msg;
        if (isExistAccessContextOnThread()) {
            msg = "The access process was not found in AccessContext on thread: " + getAccessContextOnThread();
        } else {
            msg = "The AccessContext was not found on thread!";
        }
        throw new IllegalStateException(msg);
    }

    /**
     * Get access module on thread.
     * @return Access module. (NotNull)
     * @exception IllegalStateException When it couldn't get access module.
     */
    public static String getAccessModuleOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessModule = userContextOnThread.getAccessModule();
            if (accessModule != null) {
                return accessModule;
            }
        }
        String msg;
        if (isExistAccessContextOnThread()) {
            msg = "The access module was not found in AccessContext on thread: " + getAccessContextOnThread();
        } else {
            msg = "The AccessContext was not found on thread!";
        }
        throw new IllegalStateException(msg);
    }

    /**
     * Get access date on thread. <br />
     * If it couldn't get access date from access-context, it returns application current date!
     * @return Access date. (NotNull)
     */
    public static Date getAccessDateOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final java.util.Date accessDate = userContextOnThread.getAccessDate();
            if (accessDate != null) {
                return accessDate;
            }
            if (userContextOnThread.getAccessDateProvider() != null) {
                return userContextOnThread.getAccessDateProvider().getAccessDate();
            }
        }
        return new Date();
    }

    /**
     * Get access time-stamp on thread. <br />
     * If it couldn't get access time-stamp from access-context, it returns application current time-stamp!
     * @return Access time-stamp. (NotNull)
     */
    public static Timestamp getAccessTimestampOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Timestamp accessTimestamp = userContextOnThread.getAccessTimestamp();
            if (accessTimestamp != null) {
                return accessTimestamp;
            }
            if (userContextOnThread.getAccessTimestampProvider() != null) {
                return userContextOnThread.getAccessTimestampProvider().getAccessTimestamp();
            }
        }
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get access value on thread.
     * @param key Key. (NotNull)
     * @return Access value. (Nullable: If the key has null value, it returns null)
     * @exception IllegalStateException When it couldn't get access value.
     */
    public static Object getAccessValueOnThread(String key) {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Map<String, Object> accessValueMap = userContextOnThread.getAccessValueMap();
            if (accessValueMap != null) {
                return accessValueMap.get(key);
            }
        }
        String msg;
        if (isExistAccessContextOnThread()) {
            msg = "The access value was not found in AccessContext on thread:";
            msg = msg + " key=" + key + " " + getAccessContextOnThread();
        } else {
            msg = "The AccessContext was not found on thread: key=" + key;
        }
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String accessUser;
    protected String accessProcess;
    protected String accessModule;
    protected java.util.Date accessDate;
    protected AccessDateProvider accessDateProvider;
    protected java.sql.Timestamp accessTimestamp;
    protected AccessTimestampProvider accessTimestampProvider;
    protected Map<String, Object> accessValueMap;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + accessUser + ", " + accessProcess + ", " + accessModule + ", " + accessDate + ", "
                + accessDateProvider + ", " + accessTimestamp + ", " + accessTimestampProvider + ", " + accessValueMap
                + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAccessUser() {
        return accessUser;
    }

    public void setAccessUser(String accessUser) {
        this.accessUser = accessUser;
    }

    public String getAccessProcess() {
        return accessProcess;
    }

    public void setAccessProcess(String accessProcess) {
        this.accessProcess = accessProcess;
    }

    public String getAccessModule() {
        return accessModule;
    }

    public void setAccessModule(String accessModule) {
        this.accessModule = accessModule;
    }

    public java.util.Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(java.util.Date accessDate) {
        this.accessDate = accessDate;
    }

    public AccessDateProvider getAccessDateProvider() {
        return accessDateProvider;
    }

    public void setAccessDateProvider(AccessDateProvider accessDateProvider) {
        this.accessDateProvider = accessDateProvider;
    }

    public java.sql.Timestamp getAccessTimestamp() {
        return accessTimestamp;
    }

    public void setAccessTimestamp(java.sql.Timestamp accessTimestamp) {
        this.accessTimestamp = accessTimestamp;
    }

    public AccessTimestampProvider getAccessTimestampProvider() {
        return accessTimestampProvider;
    }

    public void setAccessTimestampProvider(AccessTimestampProvider accessTimestampProvider) {
        this.accessTimestampProvider = accessTimestampProvider;
    }

    public Map<String, Object> getAccessValueMap() {
        return accessValueMap;
    }

    public void registerAccessValue(String key, Object value) {
        if (accessValueMap == null) {
            accessValueMap = new HashMap<String, Object>();
        }
        accessValueMap.put(key, value);
    }

    // ===================================================================================
    //                                                                  Provider Interface
    //                                                                  ==================
    /**
     * The provider interface of access date.
     */
    public static interface AccessDateProvider {

        /**
         * Get access date.
         * @return Access date. (NotNull)
         */
        public java.util.Date getAccessDate();
    }

    /**
     * The provider interface of access date.
     */
    public static interface AccessTimestampProvider {

        /**
         * Get access timestamp.
         * @return Access timestamp. (NotNull)
         */
        public java.sql.Timestamp getAccessTimestamp();
    }
}
