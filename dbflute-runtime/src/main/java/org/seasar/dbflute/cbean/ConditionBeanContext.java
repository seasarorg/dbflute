package org.seasar.dbflute.cbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.util.DfSystemUtil;


/**
 * The context of condition-bean.
 * @author DBFlute(AutoGenerator)
 */
public class ConditionBeanContext {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ConditionBeanContext.class);

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<ConditionBean> _threadLocal = new ThreadLocal<ConditionBean>();

    /**
     * Get condition-bean on thread.
     * @return Condition-bean context. (Nullable)
     */
    public static ConditionBean getConditionBeanOnThread() {
        return (ConditionBean)_threadLocal.get();
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
        _threadLocal.set(cb);
    }

    /**
     * Is existing condition-bean on thread?
     * @return Determination.
     */
    public static boolean isExistConditionBeanOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear condition-bean on thread.
     */
    public static void clearConditionBeanOnThread() {
        _threadLocal.set(null);
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
            if (debugEnabled) { sb.append("  ...Loading class of " + clazz.getName() + " by " + clazz.getClassLoader().getClass()).append(getLineSeparator()); }
        }
		{
            Class<?> clazz = org.seasar.dbflute.AccessContext.class;
            clazz = org.seasar.dbflute.CallbackContext.class;
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
            if (debugEnabled) { sb.append("  ...Loading class of ...and so on"); }
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
		    final ConditionBean cb = (ConditionBean)searchKey4Log;
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
        msg = msg + "The entity was Too Many! it has been duplicated. It should be the only one! But the resultCount=" + resultCountString + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your search condition. Does it really select the only one?" + getLineSeparator();
        msg = msg + "Please confirm your database. Does it really exist the only one?" + getLineSeparator();
        msg = msg + getLineSeparator();
		if (searchKey4Log != null && searchKey4Log instanceof ConditionBean) {
		    final ConditionBean cb = (ConditionBean)searchKey4Log;
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
	public static String convertConditionBean2DisplaySql(ConditionBean cb
	                                                   , String logDateFormat
	                                                   , String logTimestampFormat) {
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
