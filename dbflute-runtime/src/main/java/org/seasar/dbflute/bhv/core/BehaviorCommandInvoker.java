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
package org.seasar.dbflute.bhv.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.dbflute.CallbackContext;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.XLog;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.cbean.FetchNarrowingBeanContext;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.helper.stacktrace.InvokeNameExtractingResource;
import org.seasar.dbflute.helper.stacktrace.InvokeNameResult;
import org.seasar.dbflute.helper.stacktrace.impl.InvokeNameExtractorImpl;
import org.seasar.dbflute.jdbc.SqlResultHandler;
import org.seasar.dbflute.jdbc.SqlResultInfo;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.resource.InternalMapContext;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.TraceViewUtil;

/**
 * The invoker of behavior command.
 * <pre>
 * public interface methods are as follows:
 *   o clearExecutionCache();
 *   o isExecutionCacheEmpty();
 *   o getExecutionCacheSize();
 *   o injectComponentProperty(BehaviorCommandComponentSetup behaviorCommand);
 *   o invoke(BehaviorCommand behaviorCommand);
 * </pre>
 * @author jflute
 */
public class BehaviorCommandInvoker {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                      Injection Target
    //                                      ----------------
    protected InvokerAssistant _invokerAssistant;

    // -----------------------------------------------------
    //                                       Execution Cache
    //                                       ---------------
    protected final Map<String, SqlExecution> _executionMap = new ConcurrentHashMap<String, SqlExecution>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BehaviorCommandInvoker() {
    }

    // ===================================================================================
    //                                                                     Execution Cache
    //                                                                     ===============
    public void clearExecutionCache() {
        _executionMap.clear();
    }

    public boolean isExecutionCacheEmpty() {
        return _executionMap.isEmpty();
    }

    public int getExecutionCacheSize() {
        return _executionMap.size();
    }

    // ===================================================================================
    //                                                                      Command Set up
    //                                                                      ==============
    /**
     * Inject the properties of component to the command of behavior. {Public Interface}
     * @param behaviorCommand The command of behavior. (NotNull)
     */
    public void injectComponentProperty(BehaviorCommandComponentSetup behaviorCommand) {
        assertInvokerAssistant();
        behaviorCommand.setDataSource(_invokerAssistant.assistDataSource());
        behaviorCommand.setStatementFactory(_invokerAssistant.assistStatementFactory());
        behaviorCommand.setBeanMetaDataFactory(_invokerAssistant.assistBeanMetaDataFactory());
        behaviorCommand.setValueTypeFactory(_invokerAssistant.assistValueTypeFactory());
        behaviorCommand.setSqlFileEncoding(getSqlFileEncoding());
    }

    protected String getSqlFileEncoding() {
        assertInvokerAssistant();
        return _invokerAssistant.assistSqlFileEncoding();
    }

    // ===================================================================================
    //                                                                      Command Invoke
    //                                                                      ==============
    /**
     * Invoke the command of behavior. {Public Interface}
     * This method is an entry point!
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The result object. (Nullable)
     */
    public <RESULT> RESULT invoke(BehaviorCommand<RESULT> behaviorCommand) {
        clearContext();
        try {
            return dispatchInvoking(behaviorCommand);
        } finally {
            clearContext();
        }
    }

    /**
     * @param <RESULT> The type of result.
     * @param behaviorCommand The command of behavior. (NotNull)
     * @return The result object. (Nullable)
     */
    protected <RESULT> RESULT dispatchInvoking(BehaviorCommand<RESULT> behaviorCommand) {
        setupResourceContext();
        final boolean logEnabled = isLogEnabled();

        // - - - - - - - - - - - - -
        // Initialize SQL Execution
        // - - - - - - - - - - - - -
        if (behaviorCommand.isInitializeOnly()) {
            initializeSqlExecution(behaviorCommand);
            return null; // The end! (Initialize Only)
        }
        behaviorCommand.beforeGettingSqlExecution();
        SqlExecution execution = findSqlExecution(behaviorCommand);

        // - - - - - - - - - - -
        // Execute SQL Execution
        // - - - - - - - - - - -
        final SqlResultHandler sqlResultHander = getSqlResultHander();
        final boolean existsSqlResultHandler = sqlResultHander != null;
        final long before = deriveCommandBeforeAfterTimeIfNeeds(logEnabled, existsSqlResultHandler);
        Object ret = null;
        try {
            final Object[] args = behaviorCommand.getSqlExecutionArgument();
            ret = executeSql(execution, args);
        } finally {
            behaviorCommand.afterExecuting();
        }
        final Class<?> retType = behaviorCommand.getCommandReturnType();
        assertRetType(retType, ret);
        final long after = deriveCommandBeforeAfterTimeIfNeeds(logEnabled, existsSqlResultHandler);
        if (logEnabled) {
            logReturn(behaviorCommand, retType, ret, before, after);
        }

        // - - - - - - - - - -
        // Convert and Return!
        // - - - - - - - - - -
        if (retType.isPrimitive()) {
            ret = convertPrimitiveWrapper(retType, ret);
        } else if (Number.class.isAssignableFrom(retType)) {
            ret = convertNumber(retType, ret);
        }

        // - - - - - - - - - - - -
        // Call the handler back!
        // - - - - - - - - - - - -
        callbackSqlResultHanler(behaviorCommand, existsSqlResultHandler, sqlResultHander, ret, before, after);

        // - - - - - - - - -
        // Cast and Return!
        // - - - - - - - - -
        @SuppressWarnings("unchecked")
        final RESULT result = (RESULT) ret;
        return result;
    }

    protected void setupResourceContext() {
        assertInvokerAssistant();
        ResourceContext resourceContext = new ResourceContext();
        resourceContext.setCurrentDBDef(_invokerAssistant.assistCurrentDBDef());
        resourceContext.setDBMetaProvider(_invokerAssistant.assistDBMetaProvider());
        resourceContext.setSqlClauseCreator(_invokerAssistant.assistSqlClauseCreator());
        resourceContext.setResourceParameter(_invokerAssistant.assistResourceParameter());
        ResourceContext.setResourceContextOnThread(resourceContext);
    }

    protected long deriveCommandBeforeAfterTimeIfNeeds(boolean logEnabled, boolean existsSqlResultHandler) {
        long time = 0;
        if (logEnabled || existsSqlResultHandler) {
            time = systemTime();
        }
        return time;
    }

    protected long systemTime() {
        return System.currentTimeMillis(); // for calculating performance
    }

    protected <RESULT> void callbackSqlResultHanler(BehaviorCommand<RESULT> behaviorCommand,
            boolean existsSqlResultHandler, SqlResultHandler sqlResultHander, Object ret, long before, long after) {
        if (existsSqlResultHandler) {
            final String displaySql = (String) InternalMapContext.getObject("df:DisplaySql");
            SqlResultInfo info = new SqlResultInfo();
            info.setResult(ret);
            info.setTableDbName(behaviorCommand.getTableDbName());
            info.setCommandName(behaviorCommand.getCommandName());
            info.setDisplaySql(displaySql);
            info.setBeforeTimeMillis(before);
            info.setAfterTimeMillis(after);
            sqlResultHander.handle(info);
        }
    }

    // ===================================================================================
    //                                                                       SQL Execution
    //                                                                       =============
    protected <RESULT> SqlExecution findSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
        final boolean logEnabled = isLogEnabled();
        SqlExecution execution = null;
        try {
            final String key = behaviorCommand.buildSqlExecutionKey();
            execution = getSqlExecution(key);
            if (execution == null) {
                long beforeCmd = 0;
                if (logEnabled) {
                    beforeCmd = systemTime();
                }
                SqlExecutionCreator creator = behaviorCommand.createSqlExecutionCreator();
                execution = getSqlExecution(key, creator);
                if (logEnabled) {
                    final long afterCmd = systemTime();
                    if (beforeCmd != afterCmd) {
                        logSqlExecution(behaviorCommand, execution, beforeCmd, afterCmd);
                    }
                }
            }
            return execution;
        } finally {
            if (logEnabled) {
                logInvocation(behaviorCommand);
            }
        }
    }

    protected <RESULT> void initializeSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
        final String key = behaviorCommand.buildSqlExecutionKey();
        SqlExecutionCreator creator = behaviorCommand.createSqlExecutionCreator();
        getSqlExecution(key, creator);
    }

    protected SqlExecution getSqlExecution(String key) {
        return _executionMap.get(key);
    }

    /**
     * @param key The key of SQL execution. (NotNull)
     * @param executionCreator The creator of SQL execution. (NotNull)
     * @return The SQL execution that may be created then. (NotNull)
     */
    protected SqlExecution getSqlExecution(String key, SqlExecutionCreator executionCreator) {
        SqlExecution execution = getSqlExecution(key);
        if (execution == null) {
            synchronized (_executionMap) {
                execution = getSqlExecution(key);
                if (execution == null) {
                    if (isLogEnabled()) {
                        log("...Initializing sqlExecution for the key '" + key + "'");
                    }
                    _executionMap.put(key, executionCreator.createSqlExecution());
                } else {
                    if (isLogEnabled()) {
                        log("...Getting sqlExecution as cache because the previous thread have already initialized.");
                    }
                }
            }
            execution = getSqlExecution(key);
            if (execution == null) {
                String msg = "sqlExecutionCreator.createSqlCommand() should not return null:";
                msg = msg + " sqlExecutionCreator=" + executionCreator + " key=" + key;
                throw new IllegalStateException(msg);
            }
            toBeDisposable(); // for HotDeploy
        }
        return execution;
    }

    protected Object executeSql(SqlExecution execution, Object[] args) {
        return execution.execute(args);
    }

    // ===================================================================================
    //                                                                      Log SqlCommand
    //                                                                      ==============
    protected <RESULT> void logSqlExecution(BehaviorCommand<RESULT> behaviorCommand, SqlExecution execution,
            long beforeCmd, long afterCmd) {
        log("SqlExecution Initialization Cost: [" + TraceViewUtil.convertToPerformanceView(afterCmd - beforeCmd) + "]");
    }

    // ===================================================================================
    //                                                                      Log Invocation
    //                                                                      ==============
    protected <RESULT> void logInvocation(BehaviorCommand<RESULT> behaviorCommand) {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        final InvokeNameResult behaviorResult = extractBehaviorInvokeName(stackTrace);
        filterBehaviorResult(behaviorCommand, behaviorResult);

        final String invokeClassName;
        final String invokeMethodName;
        if (!behaviorResult.isEmptyResult()) {
            invokeClassName = behaviorResult.getSimpleClassName();
            invokeMethodName = behaviorResult.getMethodName();
        } else {
            invokeClassName = behaviorCommand.getTableDbName();
            invokeMethodName = behaviorCommand.getCommandName();
        }
        final String expWithoutKakko = buildInvocationExpressionWithoutKakko(behaviorCommand, invokeClassName,
                invokeMethodName);

        // Save behavior invoke name for error message.
        putObjectToMapContext("df:BehaviorInvokeName", expWithoutKakko + "()");

        final String equalBorder = buildFitBorder("", "=", expWithoutKakko, false);
        final String callerExpression = expWithoutKakko + "()";

        log("/=====================================================" + equalBorder + "==");
        log("                                                      " + callerExpression);
        log("                                                      " + equalBorder + "=/");

        logPath(behaviorCommand, stackTrace, behaviorResult);

        if (behaviorCommand.isOutsideSql() && !behaviorCommand.isProcedure()) {
            final OutsideSqlContext outsideSqlContext = getOutsideSqlContext();
            if (outsideSqlContext != null) {
                log("path: " + behaviorCommand.getOutsideSqlPath());
                log("option: " + behaviorCommand.getOutsideSqlOption());
            }
        }
    }

    protected <RESULT> void filterBehaviorResult(BehaviorCommand<RESULT> behaviorCommand,
            InvokeNameResult behaviorResult) {
        final String simpleClassName = behaviorResult.getSimpleClassName();
        if (simpleClassName == null) {
            return;
        }
        if (simpleClassName.contains("Behavior") && simpleClassName.endsWith("$SLFunction")) {
            final String behaviorClassName = findBehaviorClassNameFromDBMeta(behaviorCommand.getTableDbName());
            behaviorResult.setSimpleClassName(behaviorClassName);
            behaviorResult.setMethodName("scalarSelect()." + behaviorResult.getMethodName());
        }
    }

    protected <RESULT> void logPath(BehaviorCommand<RESULT> behaviorCommand, StackTraceElement[] stackTrace,
            InvokeNameResult behaviorResult) {
        final int bhvNextIndex = behaviorResult.getNextStartIndex();
        final InvokeNameResult clientResult = extractClientInvokeName(stackTrace, bhvNextIndex);
        final int clientFirstIndex = clientResult.getFoundFirstIndex();
        final InvokeNameResult byPassResult = extractByPassInvokeName(stackTrace, bhvNextIndex, clientFirstIndex
                - bhvNextIndex);

        final String clientInvokeName = clientResult.getInvokeName();
        final String byPassInvokeName = byPassResult.getInvokeName();
        final String behaviorInvokeName = behaviorResult.getInvokeName();
        if (clientInvokeName.trim().length() == 0 && byPassInvokeName.trim().length() == 0) {
            return;
        }

        // Save client invoke name for error message.
        if (!clientResult.isEmptyResult()) {
            putObjectToMapContext("df:ClientInvokeName", clientInvokeName);
        }
        // Save by-pass invoke name for error message.
        if (!byPassResult.isEmptyResult()) {
            putObjectToMapContext("df:ByPassInvokeName", byPassInvokeName);
        }

        log(clientInvokeName + byPassInvokeName + behaviorInvokeName + "...");
    }

    protected <RESULT> String buildInvocationExpressionWithoutKakko(BehaviorCommand<RESULT> behaviorCommand,
            String invokeClassName, String invokeMethodName) {
        if (invokeClassName.contains("OutsideSql") && invokeClassName.endsWith("Executor")) { // OutsideSql Executor Handling
            try {
                final String originalName = invokeClassName;
                if (behaviorCommand.isOutsideSql()) {
                    final OutsideSqlContext outsideSqlContext = getOutsideSqlContext();
                    final String tableDbName = outsideSqlContext.getTableDbName();
                    final String behaviorClassName = findBehaviorClassNameFromDBMeta(tableDbName);
                    invokeClassName = behaviorClassName + ".outsideSql()";
                    if (originalName.endsWith("OutsideSqlEntityExecutor")) {
                        invokeClassName = invokeClassName + ".entityHandling()";
                    } else if (originalName.endsWith("OutsideSqlPagingExecutor")) {
                        if (outsideSqlContext.isAutoPagingLogging()) {
                            invokeClassName = invokeClassName + ".autoPaging()";
                        } else {
                            invokeClassName = invokeClassName + ".manualPaging()";
                        }
                    } else if (originalName.endsWith("OutsideSqlCursorExecutor")) {
                        invokeClassName = invokeClassName + ".cursorHandling()";
                    }
                } else {
                    invokeClassName = "OutsideSql";
                }
            } catch (RuntimeException ignored) {
                log("Ignored exception occurred: msg=" + ignored.getMessage());
            }
        }
        String callerExpressionWithoutKakko = invokeClassName + "." + invokeMethodName;
        if ("selectPage".equals(invokeMethodName)) { // Special Handling!
            boolean resultTypeInteger = false;
            if (behaviorCommand.isOutsideSql()) {
                final OutsideSqlContext outsideSqlContext = getOutsideSqlContext();
                final Class<?> resultType = outsideSqlContext.getResultType();
                if (resultType != null) {
                    if (Integer.class.isAssignableFrom(resultType)) {
                        resultTypeInteger = true;
                    }
                }
            }
            if (resultTypeInteger || behaviorCommand.isSelectCount()) {
                callerExpressionWithoutKakko = callerExpressionWithoutKakko + "():count";
            } else {
                callerExpressionWithoutKakko = callerExpressionWithoutKakko + "():paging";
            }
        }
        return callerExpressionWithoutKakko;
    }

    protected String buildFitBorder(String prefix, String element, String lengthTargetString, boolean space) {
        final int length = space ? lengthTargetString.length() / 2 : lengthTargetString.length();
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix);
        for (int i = 0; i < length; i++) {
            sb.append(element);
            if (space) {
                sb.append(" ");
            }
        }
        if (space) {
            sb.append(element);
        }
        return sb.toString();
    }

    protected InvokeNameResult extractClientInvokeName(StackTraceElement[] stackTrace, final int startIndex) {
        final List<String> suffixList = Arrays.asList(new String[] { "Page", "Action" });
        final InvokeNameExtractingResource resource = new InvokeNameExtractingResource() {
            public boolean isTargetElement(String className, String methodName) {
                return isClassNameEndsWith(className, suffixList);
            }

            public String filterSimpleClassName(String simpleClassName) {
                return simpleClassName;
            }

            public boolean isUseAdditionalInfo() {
                return true;
            }

            public int getStartIndex() {
                return startIndex;
            }

            public int getLoopSize() {
                return 25;
            }
        };
        return extractInvokeName(resource, stackTrace);
    }

    protected InvokeNameResult extractByPassInvokeName(StackTraceElement[] stackTrace, final int startIndex,
            final int loopSize) {
        final List<String> suffixList = Arrays
                .asList(new String[] { "Service", "ServiceImpl", "Facade", "FacadeImpl" });
        final InvokeNameExtractingResource resource = new InvokeNameExtractingResource() {
            public boolean isTargetElement(String className, String methodName) {
                return isClassNameEndsWith(className, suffixList);
            }

            public String filterSimpleClassName(String simpleClassName) {
                return simpleClassName;
            }

            public boolean isUseAdditionalInfo() {
                return true;
            }

            public int getStartIndex() {
                return startIndex;
            }

            public int getLoopSize() {
                return loopSize >= 0 ? loopSize : 25;
            }
        };
        return extractInvokeName(resource, stackTrace);
    }

    protected InvokeNameResult extractBehaviorInvokeName(StackTraceElement[] stackTrace) {
        final List<String> suffixList = Arrays.asList(new String[] { "Bhv", "BehaviorReadable", "BehaviorWritable",
                "PagingInvoker" });
        final List<String> keywordList = Arrays
                .asList(new String[] { "Bhv$", "BehaviorReadable$", "BehaviorWritable$" });
        final List<String> ousideSql1List = Arrays.asList(new String[] { "OutsideSql" });
        final List<String> ousideSql2List = Arrays.asList(new String[] { "Executor" });
        final List<String> ousideSql3List = Arrays.asList(new String[] { "Executor$" });
        final InvokeNameExtractingResource resource = new InvokeNameExtractingResource() {
            public boolean isTargetElement(String className, String methodName) {
                if (isClassNameEndsWith(className, suffixList)) {
                    return true;
                }
                if (isClassNameContains(className, keywordList)) {
                    return true;
                }
                if (isClassNameContains(className, ousideSql1List)
                        && (isClassNameEndsWith(className, ousideSql2List) || isClassNameContains(className,
                                ousideSql3List))) {
                    return true;
                }
                return false;
            }

            public String filterSimpleClassName(String simpleClassName) {
                return removeBasePrefixFromSimpleClassName(simpleClassName);
            }

            public boolean isUseAdditionalInfo() {
                return false;
            }

            public int getStartIndex() {
                return 0;
            }

            public int getLoopSize() {
                return 25;
            }
        };
        return extractInvokeName(resource, stackTrace);
    }

    protected boolean isClassNameEndsWith(String className, List<String> suffixList) {
        for (String suffix : suffixList) {
            if (className.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isClassNameContains(String className, List<String> keywordList) {
        for (String keyword : keywordList) {
            if (className.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param resource the call-back resource for invoke-name-extracting. (NotNull)
     * @param stackTrace Stack log. (NotNull)
     * @return The result of invoke name. (NotNull: If not found, returns empty string.)
     */
    protected InvokeNameResult extractInvokeName(InvokeNameExtractingResource resource, StackTraceElement[] stackTrace) {
        final InvokeNameExtractorImpl extractor = new InvokeNameExtractorImpl();
        extractor.setStackTrace(stackTrace);
        return extractor.extractInvokeName(resource);
    }

    /**
     * @param simpleClassName The simple class name. (NotNull)
     * @return The simple class name removed the base prefix. (NotNull)
     */
    protected String removeBasePrefixFromSimpleClassName(String simpleClassName) {
        if (!simpleClassName.startsWith("Bs")) {
            return simpleClassName;
        }
        final int prefixLength = "Bs".length();
        if (!Character.isUpperCase(simpleClassName.substring(prefixLength).charAt(0))) {
            return simpleClassName;
        }
        if (simpleClassName.length() <= prefixLength) {
            return simpleClassName;
        }
        return "" + simpleClassName.substring(prefixLength);
    }

    protected String findBehaviorClassNameFromDBMeta(String tableDbName) {
        final DBMeta dbmeta = ResourceContext.provideDBMetaChecked(tableDbName);
        final String behaviorTypeName = dbmeta.getBehaviorTypeName();
        final String behaviorClassName = behaviorTypeName.substring(behaviorTypeName.lastIndexOf(".") + ".".length());
        return removeBasePrefixFromSimpleClassName(behaviorClassName);
    }

    // ===================================================================================
    //                                                                          Log Return
    //                                                                          ==========
    protected <RESULT> void logReturn(BehaviorCommand<RESULT> behaviorCommand, Class<?> retType, Object ret,
            long before, long after) {
        try {
            final String daoResultPrefix = "===========/ [" + TraceViewUtil.convertToPerformanceView(after - before)
                    + " - ";
            if (List.class.isAssignableFrom(retType)) {
                if (ret == null) {
                    log(daoResultPrefix + "Selected list: null]");
                } else {
                    final List<?> ls = (java.util.List<?>) ret;
                    if (ls.isEmpty()) {
                        log(daoResultPrefix + "Selected list: 0]");
                    } else if (ls.size() == 1 && ls.get(0) instanceof Number) {
                        log(daoResultPrefix + "Selected count: " + ls.get(0) + "]");
                    } else {
                        log(daoResultPrefix + "Selected list: " + ls.size() + " first=" + ls.get(0) + "]");
                    }
                }
            } else if (Entity.class.isAssignableFrom(retType)) {
                if (ret == null) {
                    log(daoResultPrefix + "Selected entity: null" + "]");
                } else {
                    final Entity entity = (Entity) ret;
                    log(daoResultPrefix + "Selected entity: " + entity + "]");
                }
            } else if (Entity.class.isAssignableFrom(retType)) {
                if (ret == null) {
                    log(daoResultPrefix + "Selected entity: null" + "]");
                } else {
                    final Entity entity = (Entity) ret;
                    log(daoResultPrefix + "Selected entity: " + entity + "]");
                }
            } else if (int[].class.isAssignableFrom(retType)) {
                if (ret == null) {
                    log(daoResultPrefix + "Selected entity: null" + "]");
                } else {
                    final int[] resultArray = (int[]) ret;
                    if (resultArray.length == 0) {
                        log(daoResultPrefix + "All updated count: 0]");
                    } else {
                        final StringBuilder sb = new StringBuilder();
                        boolean resultExpressionScope = true;
                        int resultCount = 0;
                        int loopCount = 0;
                        for (int element : resultArray) {
                            resultCount = resultCount + element;
                            if (resultExpressionScope) {
                                if (loopCount <= 10) {
                                    if (sb.length() == 0) {
                                        sb.append(element);
                                    } else {
                                        sb.append(",").append(element);
                                    }
                                } else {
                                    sb.append(",").append("...");
                                    resultExpressionScope = false;
                                }
                            }
                            ++loopCount;
                        }
                        sb.insert(0, "{").append("}");
                        log(daoResultPrefix + "All updated count: " + resultCount + " result=" + sb + "]");
                    }
                }
            } else {
                if (behaviorCommand.isSelectCount()) {
                    log(daoResultPrefix + "Selected count: " + ret + "]");
                } else {
                    log(daoResultPrefix + "Result: " + ret + "]");
                }
            }
            log(" ");
        } catch (RuntimeException e) {
            String msg = "Result object debug threw the exception: behaviorCommand=";
            msg = msg + behaviorCommand + " retType=" + retType;
            msg = msg + " ret=" + ret;
            throw e;
        }
    }

    // ===================================================================================
    //                                                                      Context Helper
    //                                                                      ==============
    protected OutsideSqlContext getOutsideSqlContext() {
        if (!OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            return null;
        }
        return OutsideSqlContext.getOutsideSqlContextOnThread();
    }

    protected SqlResultHandler getSqlResultHander() {
        if (!CallbackContext.isExistCallbackContextOnThread()) {
            return null;
        }
        return CallbackContext.getCallbackContextOnThread().getSqlResultHandler();
    }

    protected void putObjectToMapContext(String key, Object value) {
        InternalMapContext.setObject(key, value);
    }

    protected void clearContext() {
        if (OutsideSqlContext.isExistOutsideSqlContextOnThread()) {
            OutsideSqlContext.clearOutsideSqlContextOnThread();
        }
        if (FetchNarrowingBeanContext.isExistFetchNarrowingBeanOnThread()) {
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // Because there is possible that fetch narrowing has been ignored for manualPaging of outsideSql.
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            FetchNarrowingBeanContext.getFetchNarrowingBeanOnThread().restoreIgnoredFetchNarrowing();

            FetchNarrowingBeanContext.clearFetchNarrowingBeanOnThread();
        }
        if (ConditionBeanContext.isExistConditionBeanOnThread()) {
            ConditionBeanContext.clearConditionBeanOnThread();
        }
        if (ConditionBeanContext.isExistEntityRowHandlerOnThread()) {
            ConditionBeanContext.clearEntityRowHandlerOnThread();
        }
        if (InternalMapContext.isExistInternalMapContextOnThread()) {
            InternalMapContext.clearInternalMapContextOnThread();
        }
        if (ResourceContext.isExistResourceContextOnThread()) {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    // ===================================================================================
    //                                                                  Execute Status Log
    //                                                                  ==================
    protected void log(String msg) {
        XLog.log(msg);
    }

    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    protected void toBeDisposable() {
        assertInvokerAssistant();
        _invokerAssistant.toBeDisposable();
    }

    // ===================================================================================
    //                                                                      Convert Helper
    //                                                                      ==============
    protected Object convertPrimitiveWrapper(Class<?> retType, Object ret) {
        return DfTypeUtil.toWrapper(retType, ret);
    }

    protected Object convertNumber(Class<?> retType, Object ret) {
        return DfTypeUtil.toNumber(retType, ret);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertRetType(Class<?> retType, Object ret) {
        if (List.class.isAssignableFrom(retType)) {
            if (ret != null && !(ret instanceof List)) {
                String msg = "The retType is difference from actual return: ";
                msg = msg + "retType=" + retType + " ret.getClass()=" + ret.getClass() + " ref=" + ret;
                throw new IllegalStateException(msg);
            }
        } else if (Entity.class.isAssignableFrom(retType)) {
            if (ret != null && !(ret instanceof Entity)) {
                String msg = "The retType is difference from actual return: ";
                msg = msg + "retType=" + retType + " ret.getClass()=" + ret.getClass() + " ref=" + ret;
                throw new IllegalStateException(msg);
            }
        }
    }

    protected void assertInvokerAssistant() {
        if (_invokerAssistant == null) {
            String msg = "The attribute 'invokerAssistant' should not be null!";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setInvokerAssistant(InvokerAssistant invokerAssistant) {
        _invokerAssistant = invokerAssistant;
    }
}
