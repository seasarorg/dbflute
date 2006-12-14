package org.seasar.dbflute.helper.jdbc.context;

import javax.sql.DataSource;


public class DfDataSourceContext {
	
    /** The thread-local for this. */
    private static ThreadLocal<DataSource> _threadLocal = new ThreadLocal<DataSource>();

    /**
     * Get DataSource on thread.
     * 
     * @return DataSource. (Nullable)
     */
    public static DataSource getDataSource() {
        return (DataSource)_threadLocal.get();
    }

    /**
     * Set DataSource on thread.
     * 
     * @param DataSource DataSource. (NotNull)
     */
    public static void setDataSource(DataSource DataSource) {
        if (DataSource == null) {
            String msg = "The argument[DataSource] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(DataSource);
    }

    /**
     * Is existing DataSource on thread?
     * 
     * @return Determination.
     */
    public static boolean isExistDataSource() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear DataSource on thread.
     */
    public static void clearDataSource() {
        _threadLocal.set(null);
    }
}
