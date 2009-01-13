package org.dbflute.resource;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnSqlLogRegistry {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory.getLog(TnSqlLogRegistry.class);

	// ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
	protected static final String NAME_SqlLogRegistryLocator = "org.seasar.extension.jdbc.SqlLogRegistryLocator";
	protected static final String NAME_getInstance = "getInstance";
	protected static final String NAME_setInstance = "setInstance";
	protected static final String NAME_SqlLogRegistry = "org.seasar.extension.jdbc.SqlLogRegistry";
	protected static final String NAME_SqlLogRegistryImpl = "org.seasar.extension.jdbc.impl.SqlLogRegistryImpl";
	protected static final String NAME_SqlLog = "org.seasar.extension.jdbc.SqlLog";
	protected static final String NAME_SqlLogImpl = "org.seasar.extension.jdbc.impl.SqlLogImpl";
	static {
	    forNameContainerSqlLogRegistryLocator();
	}
	
	// ===================================================================================
    //                                                                        Public Entry
    //                                                                        ============
	public static boolean setupSqlLogRegistry() {
	    final Class<?> sqlLogRegistryLocatorType = forNameContainerSqlLogRegistryLocator();
		if (sqlLogRegistryLocatorType == null) {
		    return false;
		}
		final Class<?> sqlLogRegistryType = forNameContainerSqlLogRegistry();
		if (sqlLogRegistryType == null) {
		    return false;
		}
	    final Object sqlLogRegistryImpl = createContainerSqlLogRegistryImpl();
	    if (sqlLogRegistryImpl == null) {
	        return false;
	    }
	    try {
	        final Method method = sqlLogRegistryLocatorType.getMethod(NAME_setInstance, new Class[]{sqlLogRegistryType});
			_log.info("...Setting the registry of sqlLog to the locator.");
		    method.invoke(null, new Object[]{sqlLogRegistryImpl});
		    return true;
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.setupSqlLogRegistry() threw the exception:";
			msg = msg + " sqlLogRegistryLocatorType=" + sqlLogRegistryLocatorType;
			msg = msg + " NAME_setInstance=" + NAME_setInstance;
		    throw new IllegalStateException(msg, e);
		}
	}
	
	public static void clearSqlLogRegistry() {
	    final Class<?> sqlLogRegistryLocatorType = forNameContainerSqlLogRegistryLocator();
		if (sqlLogRegistryLocatorType == null) {
		    return;
		}
	    final Object sqlLogRegistry = findContainerSqlLogRegistry();
	    if (sqlLogRegistry == null) {
	        return;
	    }
	    Class<? extends Object> sqlLogRegistryType = sqlLogRegistry.getClass();
	    try {
	        final Method method = sqlLogRegistryType.getMethod("clear", new Class[]{});
		    method.invoke(sqlLogRegistry, new Object[]{});
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.clearSqlLogRegistry() threw the exception:";
			msg = msg + " sqlLogRegistryLocatorType=" + sqlLogRegistryLocatorType;
		    throw new IllegalStateException(msg, e);
		}
	}

	public static Object findContainerSqlLogRegistry() {
	    final Class<?> sqlLogRegistryLocatorType = forNameContainerSqlLogRegistryLocator();
		if (sqlLogRegistryLocatorType == null) {
		    return null;
		}
	    try {
	        final Method method = sqlLogRegistryLocatorType.getMethod(NAME_getInstance, (Class[])null);
		    return method.invoke(null, (Object[])null);
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.findContainerSqlLogRegistry() threw the exception:";
			msg = msg + " sqlLogRegistryLocatorType=" + sqlLogRegistryLocatorType;
			msg = msg + " NAME_getInstance=" + NAME_getInstance;
		    throw new IllegalStateException(msg, e);
		}
	}
	
	public static void closeRegistration() {
	    final Class<?> sqlLogRegistryLocatorType = forNameContainerSqlLogRegistryLocator();
		if (sqlLogRegistryLocatorType == null) {
		    return;
		}
		final Class<?> sqlLogRegistryType = forNameContainerSqlLogRegistry();
		if (sqlLogRegistryType == null) {
		    return;
		}
	    try {
	        final Method method = sqlLogRegistryLocatorType.getMethod(NAME_setInstance, new Class[]{sqlLogRegistryType});
			_log.info("...Closing the registry of sqlLog.");
		    method.invoke(null, new Object[]{null});
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.closeRegistration() threw the exception:";
			msg = msg + " sqlLogRegistryLocatorType=" + sqlLogRegistryLocatorType;
			msg = msg + " NAME_setInstance=" + NAME_setInstance;
		    throw new IllegalStateException(msg, e);
		}
	}
	
    public static void push(String rawSql, String completeSql, Object[] bindArgs, Class<?>[] bindArgTypes, Object sqlLogRegistry) {
		if (sqlLogRegistry == null) {
		    throw new IllegalArgumentException("sqlLogRegistry should not be null!");
		}
	    final Object sqlLogImpl = createContainerSqlLogImpl(rawSql, completeSql, bindArgs, bindArgTypes);
		reflectSqlLogToContainerSqlLogRegistry(sqlLogImpl, sqlLogRegistry);
    }

	public static String peekCompleteSql() {
	    final Object sqlLogRegistry = findContainerSqlLogRegistry();
		if (sqlLogRegistry == null) {
		    return null;
		}
		final Object sqlLog = findLastContainerSqlLog(sqlLogRegistry);
		if (sqlLog == null) {
		    return null;
		}
		return extractCompleteSqlFromContainerSqlLog(sqlLog);
	}

	// ===================================================================================
    //                                                                Container Reflection
    //                                                                ====================
	protected static Object createContainerSqlLogRegistryImpl() {
	    try {
            final Class<?> clazz = forNameContainerSqlLogRegistryImpl();
            if (clazz == null) {
                return null;
            }
            final Constructor<?> constructor = clazz.getConstructor(int.class);
            return constructor.newInstance(new Object[]{3});
	    } catch (Exception e) {
		    String msg = NAME_SqlLogRegistry + ".class.newInstance threw the exception:";
			msg = msg + " NAME_SqlLogRegistry=" + NAME_SqlLogRegistry;
		    throw new IllegalStateException(msg, e);
	    }
	}

	protected static Object createContainerSqlLogImpl(String rawSql, String completeSql, Object[] bindArgs, Class<?>[] bindArgTypes) {
	    try {
	        final Class<?> sqlLogImplType = Class.forName(NAME_SqlLogImpl);
	        final Class<?>[] argTypes = new Class[]{String.class, String.class, Object[].class, Class[].class};
	        final Constructor<?> constructor = sqlLogImplType.getConstructor(argTypes);
		    return constructor.newInstance(new Object[]{rawSql, completeSql, bindArgs, bindArgTypes});
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.createContainerSqlLogImpl() threw the exception:";
			msg = msg + " completeSql=" + completeSql;
			msg = msg + " NAME_SqlLogImpl=" + NAME_SqlLogImpl;
		    throw new IllegalStateException(msg, e);
		}
	}
	
	protected static void reflectSqlLogToContainerSqlLogRegistry(Object sqlLog, Object sqlLogRegistry) {
		if (sqlLog == null || sqlLogRegistry == null) {
		    return;
		}
	    try {
    		final Class<?> sqlLogRegistryType = sqlLogRegistry.getClass();
    	    final Class<?> sqlLogType = Class.forName(NAME_SqlLog);
    	    final Method method = sqlLogRegistryType.getMethod("add", new Class[]{sqlLogType});
    		method.invoke(sqlLogRegistry, new Object[]{sqlLog});
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.reflectToContainerSqlLogRegistry() threw the exception:";
			msg = msg + " sqlLog=" + sqlLog + " sqlLogRegistry=" + sqlLogRegistry;
			msg = msg + " NAME_SqlLog=" + NAME_SqlLog;
		    throw new IllegalStateException(msg, e);
		}
	}

	protected static Object findLastContainerSqlLog(Object sqlLogRegistry) {
		if (sqlLogRegistry == null) {
		    return null;
		}
	    try {
    		final Class<?> sqlLogRegistryType = sqlLogRegistry.getClass();
    	    final Method method = sqlLogRegistryType.getMethod("getLast", (Class[])null);
    		return method.invoke(sqlLogRegistry, (Object[])null);
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.findLastContainerSqlLog() threw the exception:";
			msg = msg + " sqlLogRegistry=" + sqlLogRegistry;
		    throw new IllegalStateException(msg, e);
		}
	}
	
	protected static String extractCompleteSqlFromContainerSqlLog(Object sqlLog) {
		if (sqlLog == null) {
		    return null;
		}
	    try {
    		final Class<?> sqlLogType = sqlLog.getClass();
    	    final Method method = sqlLogType.getMethod("getCompleteSql", (Class[])null);
    		return (String)method.invoke(sqlLog, (Object[])null);
		} catch (Exception e) {
		    String msg = "InternalSqlLogRegistry.extractCompleteSqlFromContainerSqlLog() threw the exception:";
			msg = msg + " sqlLog=" + sqlLog;
		    throw new IllegalStateException(msg, e);
		}
	}
	
	protected static Class<?> forNameContainerSqlLogRegistryLocator() {
	    Class<?> clazz = null;
	    try {
	        clazz = Class.forName(NAME_SqlLogRegistryLocator);
		} catch (Exception ignored) {
		    return null;
		}
		return clazz;
	}
	
	protected static Class<?> forNameContainerSqlLogRegistry() {
	    Class<?> clazz = null;
	    try {
	        clazz = Class.forName(NAME_SqlLogRegistry);
		} catch (Exception ignored) {
		    return null;
		}
		return clazz;
	}
	
	protected static Class<?> forNameContainerSqlLogRegistryImpl() {
	    Class<?> clazz = null;
	    try {
	        clazz = Class.forName(NAME_SqlLogRegistryImpl);
		} catch (Exception ignored) {
		    return null;
		}
		return clazz;
	}
}
