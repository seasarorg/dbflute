/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.exception.AccessContextNoValueException;
import org.seasar.dbflute.exception.AccessContextNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.resource.DBFluteSystem;

/**
 * The context of DB access. (basically for CommonColumnAutoSetup)
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
     * @return The context of DB access. (NullAllowed)
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
            String msg = "The argument 'accessContext' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(accessContext);
    }

    /**
     * Is existing access-context on thread?
     * @return The determination, true or false.
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
     * Get access date on thread. <br />
     * If it couldn't get access date from access-context, it returns current date of {@link DBFluteSystem}.
     * @return The date that specifies access time. (NotNull)
     */
    public static Date getAccessDateOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final java.util.Date accessDate = userContextOnThread.getAccessDate();
            if (accessDate != null) {
                return accessDate;
            }
            final AccessDateProvider provider = userContextOnThread.getAccessDateProvider();
            if (provider != null) {
                final Date provided = provider.getAccessDate();
                if (provided != null) {
                    return provided;
                }
            }
        }
        return DBFluteSystem.currentDate();
    }

    /**
     * Get access time-stamp on thread. <br />
     * If it couldn't get access time-stamp from access-context, it returns current time-stamp of {@link DBFluteSystem}.
     * @return The time-stamp that specifies access time. (NotNull)
     */
    public static Timestamp getAccessTimestampOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Timestamp accessTimestamp = userContextOnThread.getAccessTimestamp();
            if (accessTimestamp != null) {
                return accessTimestamp;
            }
            final AccessTimestampProvider provider = userContextOnThread.getAccessTimestampProvider();
            if (provider != null) {
                final Timestamp provided = provider.getAccessTimestamp();
                if (provided != null) {
                    return provided;
                }
            }
        }
        return DBFluteSystem.currentTimestamp();
    }

    /**
     * Get access user on thread.
     * @return The expression for access user. (NotNull)
     */
    public static String getAccessUserOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessUser = userContextOnThread.getAccessUser();
            if (accessUser != null) {
                return accessUser;
            }
            final AccessUserProvider provider = userContextOnThread.getAccessUserProvider();
            if (provider != null) {
                final String user = provider.getAccessUser();
                if (user != null) {
                    return user;
                }
            }
        }
        final String methodName = "getAccessUserOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessUser", "user");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access process on thread.
     * @return The expression for access module. (NotNull)
     */
    public static String getAccessProcessOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessProcess = userContextOnThread.getAccessProcess();
            if (accessProcess != null) {
                return accessProcess;
            }
            final AccessProcessProvider provider = userContextOnThread.getAccessProcessProvider();
            if (provider != null) {
                final String provided = provider.getAccessProcess();
                if (provided != null) {
                    return provided;
                }
            }
        }
        final String methodName = "getAccessProcessOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessProcess", "process");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access module on thread.
     * @return The expression for access module. (NotNull)
     */
    public static String getAccessModuleOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessModule = userContextOnThread.getAccessModule();
            if (accessModule != null) {
                return accessModule;
            }
            final AccessModuleProvider provider = userContextOnThread.getAccessModuleProvider();
            if (provider != null) {
                final String provided = provider.getAccessModule();
                if (provided != null) {
                    return provided;
                }
            }
        }
        final String methodName = "getAccessModuleOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessModule", "module");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access value on thread.
     * @param key Key. (NotNull)
     * @return The object of access value. (NotNull)
     */
    public static Object getAccessValueOnThread(String key) {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Map<String, Object> accessValueMap = userContextOnThread.getAccessValueMap();
            if (accessValueMap != null) {
                final Object value = accessValueMap.get(key);
                if (value != null) {
                    return value;
                }
            }
        }
        final String methodName = "getAccessValueOnThread(\"" + key + "\")";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessValue", "value");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    protected static void throwAccessContextNotFoundException(String methodName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The access context was not found on thread.");
        br.addItem("Advice");
        br.addElement("Set up the value before DB access (using common column auto set-up)");
        br.addElement("You should set up it at your application's interceptor or filter.");
        br.addElement("For example:");
        br.addElement("  try {");
        br.addElement("      AccessContext context = new AccessContext();");
        br.addElement("      context.setAccessTimestamp(accessTimestamp);");
        br.addElement("      context.setAccessUser(accessUser);");
        br.addElement("      context.setAccessProcess(accessProcess);");
        br.addElement("      AccessContext.setAccessContextOnThread(context);");
        br.addElement("      return invocation.proceed();");
        br.addElement("  } finally {");
        br.addElement("      AccessContext.clearAccessContextOnThread();");
        br.addElement("  }");
        final String msg = br.buildExceptionMessage();
        throw new AccessContextNotFoundException(msg);
    }

    protected static void throwAccessContextNoValueException(String methodName, String capPropName, String aliasName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to get the access " + aliasName + " in access context on thread.");
        br.addItem("Advice");
        br.addElement("Set up the value before DB access (using common column auto set-up)");
        br.addElement("You should set up it at your application's interceptor or filter.");
        br.addElement("For example:");
        br.addElement("  try {");
        br.addElement("      AccessContext context = new AccessContext();");
        br.addElement("      context.setAccessTimestamp(accessTimestamp);");
        br.addElement("      context.setAccessUser(accessUser);");
        br.addElement("      context.setAccessProcess(accessProcess);");
        br.addElement("      AccessContext.setAccessContextOnThread(context);");
        br.addElement("      return invocation.proceed();");
        br.addElement("  } finally {");
        br.addElement("      AccessContext.clearAccessContextOnThread();");
        br.addElement("  }");
        final String msg = br.buildExceptionMessage();
        throw new AccessContextNoValueException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Date _accessDate;
    protected AccessDateProvider _accessDateProvider;

    protected Timestamp _accessTimestamp;
    protected AccessTimestampProvider _accessTimestampProvider;

    protected String _accessUser;
    protected AccessUserProvider _accessUserProvider;

    protected String _accessProcess;
    protected AccessProcessProvider _accessProcessProvider;

    protected String _accessModule;
    protected AccessModuleProvider _accessModuleProvider;

    protected Map<String, Object> _accessValueMap;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _accessDate + ", " + _accessTimestamp + ", " + _accessUser + ", " + _accessProcess + ", "
                + _accessModule + ", " + _accessValueMap + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Date getAccessDate() {
        return _accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this._accessDate = accessDate;
    }

    public AccessDateProvider getAccessDateProvider() {
        return _accessDateProvider;
    }

    public void setAccessDateProvider(AccessDateProvider accessDateProvider) {
        this._accessDateProvider = accessDateProvider;
    }

    public Timestamp getAccessTimestamp() {
        return _accessTimestamp;
    }

    public void setAccessTimestamp(Timestamp accessTimestamp) {
        this._accessTimestamp = accessTimestamp;
    }

    public AccessTimestampProvider getAccessTimestampProvider() {
        return _accessTimestampProvider;
    }

    public void setAccessTimestampProvider(AccessTimestampProvider accessTimestampProvider) {
        this._accessTimestampProvider = accessTimestampProvider;
    }

    public String getAccessUser() {
        return _accessUser;
    }

    public void setAccessUser(String accessUser) {
        this._accessUser = accessUser;
    }

    public AccessUserProvider getAccessUserProvider() {
        return _accessUserProvider;
    }

    public void setAccessUserProvider(AccessUserProvider accessUserProvider) {
        this._accessUserProvider = accessUserProvider;
    }

    public String getAccessProcess() {
        return _accessProcess;
    }

    public void setAccessProcess(String accessProcess) {
        this._accessProcess = accessProcess;
    }

    public AccessProcessProvider getAccessProcessProvider() {
        return _accessProcessProvider;
    }

    public void setAccessProcessProvider(AccessProcessProvider accessProcessProvider) {
        this._accessProcessProvider = accessProcessProvider;
    }

    public String getAccessModule() {
        return _accessModule;
    }

    public void setAccessModule(String accessModule) {
        this._accessModule = accessModule;
    }

    public AccessModuleProvider getAccessModuleProvider() {
        return _accessModuleProvider;
    }

    public void setAccessModuleProvider(AccessModuleProvider accessModuleProvider) {
        this._accessModuleProvider = accessModuleProvider;
    }

    public Map<String, Object> getAccessValueMap() {
        return _accessValueMap;
    }

    public void registerAccessValue(String key, Object value) {
        if (_accessValueMap == null) {
            _accessValueMap = new HashMap<String, Object>();
        }
        _accessValueMap.put(key, value);
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
         * @return The date that specifies access time. (NotNull)
         */
        Date getAccessDate();
    }

    /**
     * The provider interface of access time-stamp.
     */
    public static interface AccessTimestampProvider {

        /**
         * Get access time-stamp.
         * @return The time-stamp that specifies access time. (NotNull)
         */
        Timestamp getAccessTimestamp();
    }

    /**
     * The provider interface of access user.
     */
    public static interface AccessUserProvider {

        /**
         * Get access user.
         * @return The expression for access user. (NotNull)
         */
        String getAccessUser();
    }

    /**
     * The provider interface of access process.
     */
    public static interface AccessProcessProvider {

        /**
         * Get access process.
         * @return The expression for access process. (NotNull)
         */
        String getAccessProcess();
    }

    /**
     * The provider interface of access module.
     */
    public static interface AccessModuleProvider {

        /**
         * Get access module.
         * @return The expression for access module. (NotNull)
         */
        String getAccessModule();
    }
}
