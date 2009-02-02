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
package org.seasar.dbflute.cbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * The context of condition-bean.
 * @author jflute
 */
public class ConditionBeanContext {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ConditionBeanContext.class);

    // ===================================================================================
    //                                                             ConditionBean on Thread
    //                                                             =======================
    /** The thread-local for condition-bean. */
    private static final ThreadLocal<ConditionBean> _conditionBeanLocal = new ThreadLocal<ConditionBean>();

    /**
     * Get condition-bean on thread.
     * @return Condition-bean. (Nullable)
     */
    public static ConditionBean getConditionBeanOnThread() {
        return (ConditionBean) _conditionBeanLocal.get();
    }

    /**
     * Set condition-bean on thread.
     * @param cb Condition-bean. (NotNull)
     */
    public static void setConditionBeanOnThread(ConditionBean cb) {
        if (cb == null) {
            String msg = "The argument[cb] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _conditionBeanLocal.set(cb);
    }

    /**
     * Is existing condition-bean on thread?
     * @return Determination.
     */
    public static boolean isExistConditionBeanOnThread() {
        return (_conditionBeanLocal.get() != null);
    }

    /**
     * Clear condition-bean on thread.
     */
    public static void clearConditionBeanOnThread() {
        _conditionBeanLocal.set(null);
    }

    // ===================================================================================
    //                                                          EntityRowHandler on Thread
    //                                                          ==========================
    /** The thread-local for entity row handler. */
    private static final ThreadLocal<EntityRowHandler<? extends Entity>> _entityRowHandlerLocal = new ThreadLocal<EntityRowHandler<? extends Entity>>();

    /**
     * Get the handler of entity row. on thread.
     * @return The handler of entity row. (Nullable)
     */
    public static EntityRowHandler<? extends Entity> getEntityRowHandlerOnThread() {
        return (EntityRowHandler<? extends Entity>) _entityRowHandlerLocal.get();
    }

    /**
     * Set the handler of entity row on thread.
     * @param handler The handler of entity row. (NotNull)
     */
    public static void setEntityRowHandlerOnThread(EntityRowHandler<? extends Entity> handler) {
        if (handler == null) {
            String msg = "The argument[handler] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _entityRowHandlerLocal.set(handler);
    }

    /**
     * Is existing the handler of entity row on thread?
     * @return Determination.
     */
    public static boolean isExistEntityRowHandlerOnThread() {
        return (_entityRowHandlerLocal.get() != null);
    }

    /**
     * Clear the handler of entity row on thread.
     */
    public static void clearEntityRowHandlerOnThread() {
        _entityRowHandlerLocal.set(null);
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================
    /**
     * Is the argument condition-bean?
     * @param dtoInstance DTO instance.
     * @return Determination.
     */
    public static boolean isTheArgumentConditionBean(final Object dtoInstance) {
        return dtoInstance instanceof ConditionBean;
    }

    /**
     * Is the type condition-bean?
     * @param dtoClass DtoClass.
     * @return Determination.
     */
    public static boolean isTheTypeConditionBean(final Class<?> dtoClass) {
        return ConditionBean.class.isAssignableFrom(dtoClass);
    }

    // ===================================================================================
    //                                                                        Cool Classes
    //                                                                        ============
    @SuppressWarnings("unused")
    public static void loadCoolClasses() {
        boolean debugEnabled = false; // If you watch the log, set this true.
        // Against the ClassLoader Headache!
        final StringBuilder sb = new StringBuilder();
        {
            final Class<?> clazz = org.seasar.dbflute.cbean.SimplePagingBean.class;
            if (debugEnabled) {
                sb.append("  ...Loading class of " + clazz.getName() + " by " + clazz.getClassLoader().getClass())
                        .append(getLineSeparator());
            }
        }
        {
            Class<?> clazz = org.seasar.dbflute.AccessContext.class;
            clazz = org.seasar.dbflute.CallbackContext.class;
            clazz = org.seasar.dbflute.cbean.EntityRowHandler.class;
            clazz = org.seasar.dbflute.cbean.coption.FromToOption.class;
            clazz = org.seasar.dbflute.cbean.coption.LikeSearchOption.class;
            clazz = org.seasar.dbflute.cbean.coption.InScopeOption.class;
            clazz = org.seasar.dbflute.cbean.grouping.GroupingOption.class;
            clazz = org.seasar.dbflute.cbean.grouping.GroupingRowEndDeterminer.class;
            clazz = org.seasar.dbflute.cbean.grouping.GroupingRowResource.class;
            clazz = org.seasar.dbflute.cbean.grouping.GroupingRowSetupper.class;
            clazz = org.seasar.dbflute.cbean.pagenavi.PageNumberLink.class;
            clazz = org.seasar.dbflute.cbean.pagenavi.PageNumberLinkSetupper.class;
            clazz = org.seasar.dbflute.jdbc.CursorHandler.class;
            if (debugEnabled) {
                sb.append("  ...Loading class of ...and so on");
            }
        }
        if (debugEnabled) {
            _log.debug("{Initialize against the ClassLoader Headache}" + getLineSeparator() + sb);
        }
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    public static void throwEntityAlreadyDeletedException(Object searchKey4Log) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The entity was Not Found! it has already been deleted!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the existence of your target record on your database." + getLineSeparator();
        msg = msg + "Does the target record really created before this operation?" + getLineSeparator();
        msg = msg + "Has the target record been deleted by other thread?" + getLineSeparator();
        msg = msg + "It is precondition that the record exists on your database." + getLineSeparator();
        msg = msg + getLineSeparator();
        if (searchKey4Log != null && searchKey4Log instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey4Log;
            msg = msg + "[Display SQL]" + getLineSeparator() + cb.toDisplaySql() + getLineSeparator();
        } else {
            msg = msg + "[Search Condition]" + getLineSeparator() + searchKey4Log + getLineSeparator();
        }
        msg = msg + "* * * * * * * * * */";
        throw new org.seasar.dbflute.exception.EntityAlreadyDeletedException(msg);
    }

    public static void throwEntityDuplicatedException(String resultCountString, Object searchKey4Log, Throwable cause) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The entity was Too Many! it has been duplicated. It should be the only one! But the resultCount="
                + resultCountString + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your search condition. Does it really select the only one?" + getLineSeparator();
        msg = msg + "Please confirm your database. Does it really exist the only one?" + getLineSeparator();
        msg = msg + getLineSeparator();
        if (searchKey4Log != null && searchKey4Log instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey4Log;
            msg = msg + "[Display SQL]" + getLineSeparator() + cb.toDisplaySql() + getLineSeparator();
        } else {
            msg = msg + "[Search Condition]" + getLineSeparator() + searchKey4Log + getLineSeparator();
        }
        msg = msg + "* * * * * * * * * */";
        if (cause != null) {
            throw new org.seasar.dbflute.exception.EntityDuplicatedException(msg, cause);
        } else {
            throw new org.seasar.dbflute.exception.EntityDuplicatedException(msg);
        }
    }

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    public static String convertConditionBean2DisplaySql(ConditionBean cb, String logDateFormat,
            String logTimestampFormat) {
        final String twoWaySql = cb.getSqlClause().getClause();
        return SqlAnalyzer.convertTwoWaySql2DisplaySql(twoWaySql, cb, logDateFormat, logTimestampFormat);
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected static String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
