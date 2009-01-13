package org.dbflute.dbmeta.hierarchy;


import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;

/**
 * The basic request of hierarchy.
 * 
 * @author DBFlute(AutoGenerator)
 * @param <LOCAL_ENTITY> The type of local entity.
 * @param <LOCAL_RELATION_TRACE> The type of local relation trace.
 */
@SuppressWarnings("unchecked")
public class HierarchyBasicRequest<LOCAL_ENTITY extends Entity, LOCAL_RELATION_TRACE extends DBMeta.RelationTrace> extends HierarchyRequest<LOCAL_ENTITY> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ColumnInfo _currentSourceColumnInfo;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * 
     * @param localEntityType The type of local entity. (NotNull)
     */
    public HierarchyBasicRequest(Class<LOCAL_ENTITY> localEntityType) {
        super(localEntityType);
    }

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    // -----------------------------------------------------
    //                                                public
    //                                                ------
    /**
     * Set up source.
     * 
     * @param sourceColumnInfo The column info of source. (NotNull)
     * @return Destination relation trace. (NotNull)
     */
    public DestinationRelationTrace<LOCAL_RELATION_TRACE> src(ColumnInfo sourceColumnInfo) {
        this._currentSourceColumnInfo = sourceColumnInfo;
        final HierarchyBasicRequest<LOCAL_ENTITY, LOCAL_RELATION_TRACE> outer = this;
        return new DestinationRelationTrace<LOCAL_RELATION_TRACE>() {
            public LOCAL_RELATION_TRACE dst() {
                return outer.dst();
            }
        };
    }

    /**
     * Set up destination.
     * 
     * @return Local relation trace. (NotNull)
     */
    public LOCAL_RELATION_TRACE dst() {
        final DBMeta.RelationTraceFixHandler handler = new DBMeta.RelationTraceFixHandler() {
            public void handleFixedTrace(DBMeta.RelationTrace relationTrace) {
                mapping(_currentSourceColumnInfo, relationTrace);
            }
        };
        final Object target = destinationDBMeta;
        java.lang.reflect.Method method = null;
        try {
            method = target.getClass().getMethod("createRelationTrace", new Class[]{DBMeta.RelationTraceFixHandler.class});
        } catch (NoSuchMethodException e) {
            String msg = "Not found method: method=createRelationTrace(DBMeta.RelationTraceFixHandler)";
            throw new IllegalStateException(msg, e);
        }
        try {
            return (LOCAL_RELATION_TRACE)method.invoke(target, new Object[]{handler});
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    public static interface DestinationRelationTrace<LOCAL_RELATION_TRACE> {
        public LOCAL_RELATION_TRACE dst();
    }
}
