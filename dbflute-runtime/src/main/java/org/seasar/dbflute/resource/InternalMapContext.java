package org.seasar.dbflute.resource;

import java.util.Map;
import java.util.HashMap;

/**
 * The context of internal map.
 * @author jflute
 */
public class InternalMapContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

	protected static void initialize() {
        if (threadLocal.get() != null) {
            return;
        }
        threadLocal.set(new HashMap<String, Object>());
    }
		
    /**
     * Get the value of the object by the key.
	 * @param key The key of the object. (NotNull)
     * @return The value of the object. (Nullable)
     */
    public static Object getObject(String key) {
	    initialize();
        return threadLocal.get().get(key);
    }

    /**
     * Set the value of the object.
     * @param key The key of the object. (NotNull)
	 * @param value The value of the object. (Nullable)
     */
    public static void setObject(String key, Object value) {
	    initialize();
        threadLocal.get().put(key, value);
    }

    /**
     * Is existing internal-map-context on thread?
     * @return Determination.
     */
    public static boolean isExistInternalMapContextOnThread() {
        return (threadLocal.get() != null);
    }

    /**
     * Clear internal-map-context on thread.
     */
    public static void clearInternalMapContextOnThread() {
        threadLocal.set(null);
    }
}
