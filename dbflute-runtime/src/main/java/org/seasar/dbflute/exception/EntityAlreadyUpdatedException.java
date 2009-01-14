package org.seasar.dbflute.exception;

/**
 * The exception of when the entity has already been updated by other thread.
 * @author DBFlute(AutoGenerator)
 */
public class EntityAlreadyUpdatedException extends SQLFailureException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;
    
    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Object bean;

    private int rows;
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param bean Bean. (NotNull)
     * @param rows Rows.
     */
    public EntityAlreadyUpdatedException(Object bean, int rows) {
        super("The entity already been updated: rows=" + rows + ", bean=" + bean, null);
        this.bean = bean;
        this.rows = rows;
    }
    
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getBean() {
        return bean;
    }

    public int getRows() {
        return rows;
    }
}
