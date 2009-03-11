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
     * @return The context of DB access.. (Nullable)
     */
    public static AccessContext getAccessContextOnThread() {
        return (AccessContext) _threadLocal.get();
    }

    /**
     * Set access-context on thread.
     * @param accessContext The context of DB access.. (NotNull)
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
     * <p>
     * If it can't get access user from access-context, 
     * returns 'Anonymous' as default value!
     * </p>
     * @return Access user. (NotNull)
     */
    public static String getAccessUserOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessUser = userContextOnThread.getAccessUser();
            if (accessUser != null) {
                return accessUser;
            }
        }
        return "Anonymous"; // as Default
    }

    /**
     * Get access process on thread.
     * <p>
     * If it can't get access process from access-context, 
     * returns 'Anonymous' as default value!
     * </p>
     * @return Access process. (NotNull)
     */
    public static String getAccessProcessOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessProcess = userContextOnThread.getAccessProcess();
            if (accessProcess != null) {
                return accessProcess;
            }
        }
        return "Anonymous"; // as Default
    }

    /**
     * Get access module on thread.
     * <p>
     * If it can't get access module from access-context, 
     * returns 'Anonymous' as default value!
     * </p>
     * @return Access module. (NotNull)
     */
    public static String getAccessModuleOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessModule = userContextOnThread.getAccessModule();
            if (accessModule != null) {
                return accessModule;
            }
        }
        return "Anonymous"; // as Default
    }

    /**
     * Get access date on thread.
     * <p>
     * If it can't get access date from access-context, 
     * returns application current time as default value!
     * </p>
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
        return new Date(); // as Default
    }

    /**
     * Get access time-stamp on thread.
     * <p>
     * If it can't get access time-stamp from access-context, 
     * returns application current time as default value!
     * </p>
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
        return new Timestamp(System.currentTimeMillis()); // as Default
    }

    /**
     * Get access value on thread.
     * <p>
     * If it can't get access value from access-context, 
     * returns null as default value!
     * </p>
     * @param key Key. (NotNull)
     * @return Access value. (Nullable)
     */
    public static Object getAccessValueOnThread(String key) {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Map<String, Object> accessValueMap = userContextOnThread.getAccessValueMap();
            if (accessValueMap != null) {
                return accessValueMap.get(key);
            }
        }
        return null; // as Default
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
