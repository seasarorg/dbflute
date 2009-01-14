package org.seasar.dbflute.resource;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The context of resource.
 * @author DBFlute(AutoGenerator)
 */
public class ResourceContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<ResourceContext> threadLocal = new ThreadLocal<ResourceContext>();

    /**
     * Get the context of resource by the key.
     * @return The context of resource. (Nullable)
     */
    public static ResourceContext getResourceContextOnThread() {
        return threadLocal.get();
    }

    /**
     * Set the context of resource.
     * @param resourceCountext The context of resource. (NotNull)
     */
    public static void setResourceContextOnThread(ResourceContext resourceCountext) {
        threadLocal.set(resourceCountext);
    }

    /**
     * Is existing the context of resource on thread?
     * @return Determination.
     */
    public static boolean isExistResourceContextOnThread() {
        return (threadLocal.get() != null);
    }

    /**
     * Clear the context of resource on thread.
     */
    public static void clearResourceContextOnThread() {
        threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    /**
     * @return The current database definition. (NotNull)
     */
    public static DBDef currentDBDef() {
        if (!isExistResourceContextOnThread()) {
            return DBDef.Unknown;
        }
        DBDef currentDBDef = getResourceContextOnThread().getCurrentDBDef();
        if (currentDBDef == null) {
            return DBDef.Unknown;
        }
        return currentDBDef;
    }

    public static boolean isCurrentDBDef(DBDef targetDBDef) {
        return currentDBDef().equals(targetDBDef);
    }

    /**
     * @return The provider of DB meta. (NotNull)
     */
    public static DBMetaProvider dbmetaProvider() {
        if (!isExistResourceContextOnThread()) {
            String msg = "The resource context should exist!";
            throw new IllegalStateException(msg);
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            String msg = "The provider of DB meta should exist!";
            throw new IllegalStateException(msg);
        }
        return provider;
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (Nullable)
     */
    public static DBMeta provideDBMeta(String tableFlexibleName) {
        if (!isExistResourceContextOnThread()) {
            return null;
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            return null;
        }
        return provider.provideDBMeta(tableFlexibleName);
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     */
    public static DBMeta provideDBMetaChecked(String tableFlexibleName) {
        if (!isExistResourceContextOnThread()) {
            String msg = "The resource context should exist: " + tableFlexibleName;
            throw new IllegalStateException(msg);
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            String msg = "The provider of DB meta should exist: " + tableFlexibleName;
            throw new IllegalStateException(msg);
        }
        return provider.provideDBMetaChecked(tableFlexibleName);
    }

    /**
     * Is the SQLException from unique constraint? {Use both SQLState and ErrorCode}
     * @param sqlState SQLState of the SQLException. (Nullable)
     * @param errorCode ErrorCode of the SQLException. (Nullable)
     * @return Is the SQLException from unique constraint?
     */
    public static boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        if (!isExistResourceContextOnThread()) {
            return false;
        }
        SqlClauseCreator sqlClauseCreator = getResourceContextOnThread().getSqlClauseCreator();
        if (sqlClauseCreator == null) {
            return false;
        }
        return sqlClauseCreator.createSqlClause("dummy").isUniqueConstraintException(sqlState, errorCode);
    }
    
    public static String getOutsideSqlPackage() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getOutsideSqlPackage();
    }
    
    public static String getLogDateFormat() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getLogDateFormat();
    }
    
    public static String getLogTimestampFormat() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getLogTimestampFormat();
    }
    
    protected static ResourceParameter resourceParameter() {
        if (!isExistResourceContextOnThread()) {
            return null;
        }
        ResourceParameter resourceParameter = getResourceContextOnThread().getResourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBDef _currentDBDef;
    protected DBMetaProvider _dbmetaProvider;
    protected SqlClauseCreator _sqlClauseCreator;
    protected ResourceParameter _resourceParameter;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBDef getCurrentDBDef() {
        return _currentDBDef;
    }

    public void setCurrentDBDef(DBDef currentDBDef) {
        _currentDBDef = currentDBDef;
    }

    public DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    public void setDBMetaProvider(DBMetaProvider dbmetaProvider) {
        _dbmetaProvider = dbmetaProvider;
    }

    public SqlClauseCreator getSqlClauseCreator() {
        return _sqlClauseCreator;
    }

    public void setSqlClauseCreator(SqlClauseCreator sqlClauseCreator) {
        _sqlClauseCreator = sqlClauseCreator;
    }

    public ResourceParameter getResourceParameter() {
        return _resourceParameter;
    }

    public void setResourceParameter(ResourceParameter resourceParameter) {
        _resourceParameter = resourceParameter;
    }
}
