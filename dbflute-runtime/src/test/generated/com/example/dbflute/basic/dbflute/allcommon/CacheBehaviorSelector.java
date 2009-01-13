package com.example.dbflute.basic.dbflute.allcommon;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dbflute.BehaviorSelector;
import org.dbflute.bhv.BehaviorReadable;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.util.TraceViewUtil;


/**
 * The implementation of behavior-selector.
 * @author DBFlute(AutoGenerator)
 */
@SuppressWarnings("unchecked")
public class CacheBehaviorSelector extends CacheAbstractSelector implements BehaviorSelector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory.getLog(CacheBehaviorSelector.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The cache of behavior. (It's the generic hell!) */
    protected Map<Class<? extends BehaviorReadable>, BehaviorReadable> _behaviorCache
            = new ConcurrentHashMap<Class<? extends BehaviorReadable>, BehaviorReadable>();

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    /**
     * Initialize condition-bean meta data. <br />
     */
    public void initializeConditionBeanMetaData() {
        final Map<String, DBMeta> dbmetaMap = DBMetaInstanceHandler.getDBMetaMap();
        final Collection<DBMeta> dbmetas = dbmetaMap.values();
        long before = 0;
	    if (_log.isInfoEnabled()) {
		    before = System.currentTimeMillis();
		    _log.info("/= = = = = = = = = = = = = = = = = initializeConditionBeanMetaData()");
		}
        for (DBMeta dbmeta : dbmetas) {
		    final BehaviorReadable bhv = byName(dbmeta.getTableDbName());
            bhv.warmUpCommand();
        }
	    if (_log.isInfoEnabled()) {
		    long after = System.currentTimeMillis();
		    _log.info("Initialized Count: " + dbmetas.size());
		    _log.info("= = = = = = = = = =/ [" + TraceViewUtil.convertToPerformanceView(after - before) + "]");
		}
	}
	
    // ===================================================================================
    //                                                                            Selector
    //                                                                            ========
    /**
     * Select behavior.
     * @param <BEHAVIOR> The type of behavior.
     * @param behaviorType Behavior type. (NotNull)
     * @return Behavior. (NotNull)
     */
    public <BEHAVIOR extends BehaviorReadable> BEHAVIOR select(Class<BEHAVIOR> behaviorType) {
        if (_behaviorCache.containsKey(behaviorType)) {
            return (BEHAVIOR)_behaviorCache.get(behaviorType);
        }
        synchronized (_behaviorCache) {
            if (_behaviorCache.containsKey(behaviorType)) {
                return (BEHAVIOR)_behaviorCache.get(behaviorType);
            }
            final BEHAVIOR bhv = (BEHAVIOR)getComponent(behaviorType);
            _behaviorCache.put(behaviorType, bhv);
            return bhv;
        }
    }

    /**
     * Select behavior-readable by name.
     * @param tableFlexibleName Table flexible-name. (NotNull)
     * @return Behavior-readable. (NotNull)
     */
    public BehaviorReadable byName(String tableFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("tableFlexibleName", tableFlexibleName);
        final DBMeta dbmeta = DBMetaInstanceHandler.findDBMeta(tableFlexibleName);
        return select(getBehaviorType(dbmeta));
    }

    /**
     * Get behavior-type by dbmeta.
     * @param dbmeta Dbmeta. (NotNull)
     * @return Behavior-type. (NotNull)
     */
    protected Class<BehaviorReadable> getBehaviorType(DBMeta dbmeta) {
        final String behaviorTypeName = dbmeta.getBehaviorTypeName();
        if (behaviorTypeName == null) {
            String msg = "The dbmeta.getBehaviorTypeName() should not return null: dbmeta=" + dbmeta;
            throw new IllegalStateException(msg);
        }
        final Class<BehaviorReadable> behaviorType;
        try {
            behaviorType = (Class<BehaviorReadable>)Class.forName(behaviorTypeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("The class does not exist: " + behaviorTypeName, e);
        }
        return behaviorType;
    }
}
