package org.dbflute.bhv;

import java.util.List;

import org.dbflute.Entity;


/**
 * The interface of behavior-writable.
 * @author DBFlute(AutoGenerator)
 */
public interface BehaviorWritable extends BehaviorReadable {

    // =====================================================================================
    //                                                                   Basic Entity Update
    //                                                                   ===================
    /**
     * Create.
     * @param entity Entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void create(org.dbflute.Entity entity);

    /**
     * Modify.
     * @param entity Entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modify(org.dbflute.Entity entity);

    /**
     * Modify non-strict.
     * @param entity Entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modifyNonstrict(Entity entity);

    /**
     * Create or modify. <br />
     * {modify: modified only} <br />
     * This method is faster than createOrModifyAfterSelect().
     * @param entity Entity. This must contain primary-key value at least(Except use identity). (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void createOrModify(org.dbflute.Entity entity);

    /**
     * Create or modify non-strict. <br />
     * {modify: modified only} <br />
     * This method is faster than createOrModifyAfterSelect().
     * @param entity Entity. This must contain primary-key value at least(Except use identity). (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void createOrModifyNonstrict(org.dbflute.Entity entity);

    /**
     * Remove.
     * @param entity Entity. (NotNull)
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void remove(org.dbflute.Entity entity);

    // =====================================================================================
    //                                                                    Basic Batch Update
    //                                                                    ==================
    /**
     * Lump create the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return The array of created count.
     */
    public int[] lumpCreate(List<Entity> entityList);

    /**
     * Lump modify the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return Modified count.
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated. And Only when s2dao's version is over 1.0.47 (contains 1.0.47).
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public int[] lumpModify(List<Entity> entityList);

    /**
     * Lump remove the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return Removed count.
     * @exception org.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated. And Only when s2dao's version is over 1.0.47 (contains 1.0.47).
     * @exception org.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public int[] lumpRemove(List<Entity> entityList);
}
