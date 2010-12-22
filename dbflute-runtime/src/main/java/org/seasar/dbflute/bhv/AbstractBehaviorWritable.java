/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.core.CommonColumnAutoSetupper;
import org.seasar.dbflute.bhv.core.command.AbstractListEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchDeleteEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchDeleteNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchInsertEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchUpdateEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchUpdateNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.DeleteEntityCommand;
import org.seasar.dbflute.bhv.core.command.DeleteNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.QueryDeleteCBCommand;
import org.seasar.dbflute.bhv.core.command.QueryUpdateEntityCBCommand;
import org.seasar.dbflute.bhv.core.command.UpdateEntityCommand;
import org.seasar.dbflute.bhv.core.command.UpdateNonstrictEntityCommand;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.IllegalBehaviorStateException;
import org.seasar.dbflute.exception.OptimisticLockColumnValueNullException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;

/**
 * The abstract class of writable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorWritable extends AbstractBehaviorReadable implements BehaviorWritable {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected CommonColumnAutoSetupper _commonColumnAutoSetupper;

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    // -----------------------------------------------------
    //                                                Create
    //                                                ------
    /**
     * Create.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void create(Entity entity) {
        doCreate(entity);
    }

    protected abstract void doCreate(Entity entity);

    // -----------------------------------------------------
    //                                                Modify
    //                                                ------
    /**
     * Modify.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modify(Entity entity) {
        doModify(entity);
    }

    protected abstract void doModify(Entity entity);

    /**
     * Modify non strict.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modifyNonstrict(Entity entity) {
        doModifyNonstrict(entity);
    }

    protected abstract void doModifyNonstrict(Entity entity);

    // -----------------------------------------------------
    //                                      Create or Modify
    //                                      ----------------
    /**
     * {@inheritDoc}
     */
    public void createOrModify(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doCreateOrUpdate(entity);
    }

    protected abstract void doCreateOrUpdate(Entity entity);

    /**
     * {@inheritDoc}
     */
    public void createOrModifyNonstrict(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doCreateOrUpdateNonstrict(entity);
    }

    protected abstract void doCreateOrUpdateNonstrict(Entity entity);

    // -----------------------------------------------------
    //                                                Remove
    //                                                ------
    /**
     * Remove.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void remove(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doRemove(entity);
    }

    protected abstract void doRemove(Entity entity);

    // ===================================================================================
    //                                                       Entity Update Internal Helper
    //                                                       =============================
    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected <ENTITY extends Entity> void helpUpdateInternally(ENTITY entity, InternalUpdateCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasOptimisticLockValue(entity);
        final int updatedCount = callback.callbackDelegateUpdate(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdate(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpUpdateNonstrictInternally(ENTITY entity,
            InternalUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int updatedCount = callback.callbackDelegateUpdateNonstrict(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdateNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityAlreadyDeletedException(ENTITY entity) {
        createBhvExThrower().throwUpdateEntityAlreadyDeletedException(entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityDuplicatedException(ENTITY entity, int count) {
        createBhvExThrower().throwUpdateEntityDuplicatedException(entity, count);
    }

    // -----------------------------------------------------
    //                                        InsertOrUpdate
    //                                        --------------
    protected <ENTITY extends Entity, CB_TYPE extends ConditionBean> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateCallback<ENTITY, CB_TYPE> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            RuntimeException updateException = null;
            try {
                callback.callbackUpdate(entity);
            } catch (EntityAlreadyUpdatedException e) { // already updated (or means not found)
                updateException = e;
            } catch (EntityAlreadyDeletedException e) { // means not found
                updateException = e;
            } catch (OptimisticLockColumnValueNullException e) { // means insert?
                updateException = e;
            }
            if (updateException != null) {
                final CB_TYPE cb = callback.callbackNewMyConditionBean();
                cb.acceptPrimaryKeyMap(getDBMeta().extractPrimaryKeyMap(entity));
                if (callback.callbackSelectCount(cb) == 0) { // anyway if not found, insert
                    callback.callbackInsert(entity);
                } else {
                    throw updateException;
                }
            }
        }
    }

    protected static interface InternalInsertOrUpdateCallback<ENTITY extends Entity, CB_TYPE extends ConditionBean> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdate(ENTITY entity);

        public CB_TYPE callbackNewMyConditionBean();

        public int callbackSelectCount(CB_TYPE cb);
    }

    protected <ENTITY extends Entity> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            try {
                callback.callbackUpdateNonstrict(entity);
            } catch (EntityAlreadyDeletedException ignored) { // means not found
                callback.callbackInsert(entity);
            }
        }
    }

    protected static interface InternalInsertOrUpdateNonstrictCallback<ENTITY extends Entity> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdateNonstrict(ENTITY entity);
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    protected <ENTITY extends Entity> void helpDeleteInternally(ENTITY entity, InternalDeleteCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasOptimisticLockValue(entity);
        final int deletedCount = callback.callbackDelegateDelete(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteCallback<ENTITY extends Entity> {
        public int callbackDelegateDelete(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictInternally(ENTITY entity,
            InternalDeleteNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictIgnoreDeletedInternally(ENTITY entity,
            InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            return;
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    // ===================================================================================
    //                                                                         Lump Update
    //                                                                         ===========
    /**
     * Lump create the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return The array of created count.
     */
    public int[] lumpCreate(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callCreateList(entityList);
    }

    /**
     * Lump Modify the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return Modified count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException If s2dao's version is over 1.0.47 (contains 1.0.47).
     */
    public int[] lumpModify(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callModifyList(entityList);
    }

    /**
     * Lump remove the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return Removed count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException If s2dao's version is over 1.0.47 (contains 1.0.47).
     */
    public int[] lumpRemove(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callRemoveList(entityList);
    }

    /**
     * Inject sequence to primary key if it needs.
     * @param entity Entity. (NotNull)
     */
    protected void injectSequenceToPrimaryKeyIfNeeds(Entity entity) {
        final DBMeta dbmeta = entity.getDBMeta();
        if (!dbmeta.hasSequence() || dbmeta.hasCompoundPrimaryKey() || entity.hasPrimaryKeyValue()) {
            return;
        }
        // basically property(column) type is same as next value type
        // so there is NOT type conversion cost when writing to the entity
        dbmeta.getPrimaryUniqueInfo().getFirstColumn().write(entity, readNextVal());
    }

    // =====================================================================================
    //                                                                        Process Method
    //                                                                        ==============
    // -----------------------------------------------------
    //                                                Insert
    //                                                ------
    /**
     * Process before insert.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
        if (!determineExecuteInsert(entity, option)) {
            return false;
        }
        assertEntityNotNull(entity); // primary key is checked later
        frameworkFilterEntityOfInsert(entity, option);
        filterEntityOfInsert(entity, option);
        assertEntityOfInsert(entity, option);
        // check primary key after filtering at an insert process
        // because a primary key value may be set in filtering process
        // (for example, sequence)
        if (!entity.getDBMeta().hasIdentity()) { // identity does not need primary key value here
            assertEntityNotNullAndHasPrimaryKeyValue(entity);
        }
        return true;
    }

    /**
     * Determine execution of insert. (for extension)
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of insert.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     */
    protected void frameworkFilterEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
        injectSequenceToPrimaryKeyIfNeeds(entity);
        setupCommonColumnOfInsertIfNeeds(entity, option);
    }

    /**
     * Set up common columns of insert if it needs.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     */
    protected void setupCommonColumnOfInsertIfNeeds(Entity entity, InsertOption<? extends ConditionBean> option) {
        if (option != null && option.isCommonColumnAutoSetupDisabled()) {
            return;
        }
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfInsertIfNeeds(entity);
    }

    private void assertCommonColumnAutoSetupperNotNull() {
        if (_commonColumnAutoSetupper != null) {
            return;
        }
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the auto set-upper of common column in the behavior!");
        br.addItem("Advice");
        br.addElement("Please confirm the definition of the set-upper at your component configuration of DBFlute.");
        br.addItem("Behavior");
        br.addElement("Behavior for " + getTableDbName());
        br.addItem("Attribute");
        br.addElement("behaviorCommandInvoker   : " + _behaviorCommandInvoker);
        br.addElement("behaviorSelector         : " + _behaviorSelector);
        br.addElement("commonColumnAutoSetupper : " + _commonColumnAutoSetupper);
        final String msg = br.buildExceptionMessage();
        throw new IllegalBehaviorStateException(msg);
    }

    /**
     * Filter the entity of insert. (for extension)
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     */
    protected void filterEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of insert. (for extension)
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (Nullable)
     */
    protected void assertEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the insert option is not null.
     * @param option The option of insert. (NotNull)
     */
    protected void assertInsertOptionNotNull(InsertOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for insert)", option);
    }

    /**
     * Assert that the insert option is legal status.
     * @param option The option of insert. (NotNull)
     */
    protected void assertInsertOptionStatus(InsertOption<? extends ConditionBean> option) {
        if (option.isCommonColumnAutoSetupDisabled() && !getDBMeta().hasCommonColumn()) {
            String msg = "The common column auto-setup disabling was set to the table not defined common columns:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
        if (option.isPrimaryKeyIdentityDisabled() && !getDBMeta().hasIdentity()) {
            String msg = "The identity disabling was set to the table not defined identity:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    /**
     * Process before update.
     * @param entity The entity for update that has primary key. (NotNull)
     * @param option The option of update. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
        if (!determineExecuteUpdate(entity, option)) {
            return false;
        }
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfUpdate(entity, option);
        filterEntityOfUpdate(entity, option);
        assertEntityOfUpdate(entity, option);
        return true;
    }

    /**
     * Process before query-update.
     * @param entity The entity for update that is not needed primary key. (NotNull)
     * @param cb The condition-bean for query. (NotNull)
     * @param option The option of update. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeQueryUpdate(Entity entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        if (!determineExecuteUpdate(entity, option)) {
            return false;
        }
        assertEntityNotNull(entity); // query-update doesn't need to check primary key
        assertCBNotNull(cb);
        frameworkFilterEntityOfUpdate(entity, option);
        filterEntityOfUpdate(entity, option);
        assertEntityOfUpdate(entity, option);
        assertQueryUpdateStatus(entity, cb, option);
        return true;
    }

    /**
     * Determine execution of update. (for extension)
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of update.
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (Nullable)
     */
    protected void frameworkFilterEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
        setupCommonColumnOfUpdateIfNeeds(entity, option);
    }

    /**
     * Set up common columns of update if it needs.
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (Nullable)
     */
    protected void setupCommonColumnOfUpdateIfNeeds(Entity entity, UpdateOption<? extends ConditionBean> option) {
        if (option != null && option.isCommonColumnAutoSetupDisabled()) {
            return;
        }
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfUpdateIfNeeds(entity);
    }

    /**
     * Filter the entity of update. (for extension)
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (Nullable)
     */
    protected void filterEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of update. (for extension)
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (Nullable)
     */
    protected void assertEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the update column specification is not null.
     * @param updateColumnSpec The SpecifyQuery implementation for update columns. (NotNull)
     */
    protected void assertUpdateColumnSpecificationNotNull(SpecifyQuery<? extends ConditionBean> updateColumnSpec) {
        assertObjectNotNull("updateColumnSpec", updateColumnSpec);
    }

    /**
     * Assert that the update option is not null.
     * @param option The option of update. (NotNull)
     */
    protected void assertUpdateOptionNotNull(UpdateOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for update)", option);
    }

    /**
     * Assert that the update option is legal status.
     * @param option The option of update. (NotNull)
     */
    protected void assertUpdateOptionStatus(UpdateOption<? extends ConditionBean> option) {
        if (option.isCommonColumnAutoSetupDisabled() && !getDBMeta().hasCommonColumn()) {
            String msg = "The common column auto-setup disabling was set to the table not defined common columns:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Assert that the query-update is legal status.
     * @param entity The entity for query-update. (NotNull)
     * @param cb The condition-bean for query-update. (NotNull)
     * @param option The option of update. (Nullable)
     */
    protected void assertQueryUpdateStatus(Entity entity, ConditionBean cb, UpdateOption<? extends ConditionBean> option) {
        if (option != null && option.isNonQueryUpdateAllowed()) {
            return;
        }
        if (!cb.hasWhereClause()) {
            createBhvExThrower().throwNonQueryUpdateNotAllowedException(entity, cb, option);
        }
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    /**
     * Process before delete.
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
        if (!determineExecuteDelete(entity, option)) {
            return false;
        }
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfDelete(entity, option);
        filterEntityOfDelete(entity, option);
        assertEntityOfDelete(entity, option);
        return true;
    }

    /**
     * Process before query-delete.
     * @param cb The condition-bean for query. (NotNull)
     * @param option The option of delete. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeQueryDelete(ConditionBean cb, DeleteOption<? extends ConditionBean> option) {
        assertCBNotNull(cb);
        assertQueryDeleteStatus(cb, option);
        return true;
    }

    /**
     * Determine execution of delete. (for extension) {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (Nullable)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of delete. {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (Nullable)
     */
    protected void frameworkFilterEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Filter the entity of delete. (for extension) {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (Nullable)
     */
    protected void filterEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of delete. (for extension) {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (Nullable)
     */
    protected void assertEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the delete option is not null.
     * @param option The option of delete. (NotNull)
     */
    protected void assertDeleteOptionNotNull(DeleteOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for delete)", option);
    }

    /**
     * Assert that the delete option is legal status.
     * @param option The option of delete. (NotNull)
     */
    protected void assertDeleteOptionStatus(DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the query-delete is legal status.
     * @param cb The condition-bean for query-delete. (NotNull)
     * @param option The option of delete. (Nullable)
     */
    protected void assertQueryDeleteStatus(ConditionBean cb, DeleteOption<? extends ConditionBean> option) {
        if (option != null && option.isNonQueryDeleteAllowed()) {
            return;
        }
        if (!cb.hasWhereClause()) {
            createBhvExThrower().throwNonQueryDeleteNotAllowedException(cb, option);
        }
    }

    // -----------------------------------------------------
    //                                                 Batch
    //                                                 -----
    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Inserted count.
     */
    protected int[] callCreateList(List<Entity> entityList) {
        return doCreateList(entityList);
    }

    protected abstract int[] doCreateList(List<Entity> entityList);

    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Updated count.
     */
    protected int[] callModifyList(List<Entity> entityList) {
        return doModifyList(entityList);
    }

    protected abstract int[] doModifyList(List<Entity> entityList);

    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Deleted count.
     */
    protected int[] callRemoveList(List<Entity> entityList) {
        return doRemoveList(entityList);
    }

    protected abstract int[] doRemoveList(List<Entity> entityList);

    protected void assertEntityHasOptimisticLockValue(Entity entity) {
        assertEntityHasVersionNoValue(entity);
        assertEntityHasUpdateDateValue(entity);
    }

    protected void assertEntityHasVersionNoValue(Entity entity) {
        if (!getDBMeta().hasVersionNo()) {
            return;
        }
        if (hasVersionNoValue(entity)) {
            return;
        }
        throwVersionNoValueNullException(entity);
    }

    protected void throwVersionNoValueNullException(Entity entity) {
        createBhvExThrower().throwVersionNoValueNullException(entity);
    }

    protected void assertEntityHasUpdateDateValue(Entity entity) {
        if (!getDBMeta().hasUpdateDate()) {
            return;
        }
        if (hasUpdateDateValue(entity)) {
            return;
        }
        throwUpdateDateValueNullException(entity);
    }

    protected void throwUpdateDateValueNullException(Entity entity) {
        createBhvExThrower().throwUpdateDateValueNullException(entity);
    }

    // ===================================================================================
    //                                                     Delegate Method Internal Helper
    //                                                     ===============================
    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            InsertOption<? extends ConditionBean> option) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeInsert(entity, option)) {
                continue;
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            UpdateOption<? extends ConditionBean> option, boolean nonstrict) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeUpdate(entity, option)) {
                continue;
            }
            if (!nonstrict) {
                assertEntityHasOptimisticLockValue(entity);
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            DeleteOption<? extends ConditionBean> option, boolean nonstrict) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeDelete(entity, option)) {
                continue;
            }
            if (!nonstrict) {
                assertEntityHasOptimisticLockValue(entity);
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    // an insert command creation is defined on the readable interface for non-primary key value

    protected UpdateEntityCommand createUpdateEntityCommand(Entity entity, UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createUpdateEntityCommand");
        final UpdateEntityCommand cmd = xsetupEntityCommand(new UpdateEntityCommand(), entity);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected UpdateNonstrictEntityCommand createUpdateNonstrictEntityCommand(Entity entity,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createUpdateNonstrictEntityCommand");
        final UpdateNonstrictEntityCommand cmd = xsetupEntityCommand(new UpdateNonstrictEntityCommand(), entity);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected DeleteEntityCommand createDeleteEntityCommand(Entity entity, DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createDeleteEntityCommand");
        final DeleteEntityCommand cmd = xsetupEntityCommand(new DeleteEntityCommand(), entity);
        cmd.setDeleteOption(option);
        return cmd;
    }

    protected DeleteNonstrictEntityCommand createDeleteNonstrictEntityCommand(Entity entity,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createDeleteNonstrictEntityCommand");
        final DeleteNonstrictEntityCommand cmd = xsetupEntityCommand(new DeleteNonstrictEntityCommand(), entity);
        cmd.setDeleteOption(option);
        return cmd;
    }

    // -----------------------------------------------------
    //                                                 Batch
    //                                                 -----
    protected BatchInsertEntityCommand createBatchInsertEntityCommand(List<? extends Entity> entityList,
            InsertOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchInsertEntityCommand");
        final BatchInsertEntityCommand cmd = xsetupListEntityCommand(new BatchInsertEntityCommand(), entityList);
        cmd.setInsertOption(option);
        return cmd;
    }

    protected BatchUpdateEntityCommand createBatchUpdateEntityCommand(List<? extends Entity> entityList,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchUpdateEntityCommand");
        final BatchUpdateEntityCommand cmd = xsetupListEntityCommand(new BatchUpdateEntityCommand(), entityList);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected BatchUpdateNonstrictEntityCommand createBatchUpdateNonstrictEntityCommand(
            List<? extends Entity> entityList, UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchUpdateNonstrictEntityCommand");
        final BatchUpdateNonstrictEntityCommand cmd = xsetupListEntityCommand(new BatchUpdateNonstrictEntityCommand(),
                entityList);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected BatchDeleteEntityCommand createBatchDeleteEntityCommand(List<? extends Entity> entityList,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchDeleteEntityCommand");
        final BatchDeleteEntityCommand cmd = xsetupListEntityCommand(new BatchDeleteEntityCommand(), entityList);
        cmd.setDeleteOption(option);
        return cmd;
    }

    protected BatchDeleteNonstrictEntityCommand createBatchDeleteNonstrictEntityCommand(
            List<? extends Entity> entityList, DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchDeleteNonstrictEntityCommand");
        final BatchDeleteNonstrictEntityCommand cmd = xsetupListEntityCommand(new BatchDeleteNonstrictEntityCommand(),
                entityList);
        cmd.setDeleteOption(option);
        return cmd;
    }

    /**
     * @param <COMMAND> The type of behavior command for list entity.
     * @param command The command of behavior. (NotNull)
     * @param entityList The list of entity. (NotNull, NotEmpty)
     * @return The command of behavior. (NotNull)
     */
    protected <COMMAND extends AbstractListEntityCommand> COMMAND xsetupListEntityCommand(COMMAND command,
            List<? extends Entity> entityList) {
        if (entityList.isEmpty()) {
            String msg = "The argument 'entityList' should not be empty: " + entityList;
            throw new IllegalStateException(msg);
        }
        command.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setEntityType(entityList.get(0).getClass()); // *The list should not be empty!
        command.setEntityList(entityList);
        return command;
    }

    // -----------------------------------------------------
    //                                                 Query
    //                                                 -----
    protected QueryUpdateEntityCBCommand createQueryUpdateEntityCBCommand(Entity entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createQueryUpdateEntityCBCommand");
        final QueryUpdateEntityCBCommand cmd = new QueryUpdateEntityCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setConditionBeanType(cb.getClass());
        cmd.setConditionBean(cb);
        cmd.setEntityType(entity.getClass());
        cmd.setEntity(entity);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected QueryDeleteCBCommand createQueryDeleteCBCommand(ConditionBean cb,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createQueryDeleteCBCommand");
        final QueryDeleteCBCommand cmd = new QueryDeleteCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setConditionBeanType(cb.getClass());
        cmd.setConditionBean(cb);
        cmd.setDeleteOption(option);
        return cmd;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected <CB extends ConditionBean> UpdateOption<CB> createSpecifiedUpdateOption(
            SpecifyQuery<CB> updateColumnSpec, CB cb) {
        assertUpdateColumnSpecificationNotNull(updateColumnSpec);
        assertCBNotNull(cb);
        final UpdateOption<CB> option = new UpdateOption<CB>();
        option.specify(updateColumnSpec);
        option.resolveUpdateColumnSpecification(cb);
        option.xacceptForcedSpecifiedUpdateColumn(getDBMeta().getCommonColumnInfoBeforeUpdateList());
        return option;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the auto set-upper of common column.
     * @return The auto set-upper of common column. (Nullable: But normally NotNull)
     */
    protected CommonColumnAutoSetupper getCommonColumnAutoSetupper() {
        return _commonColumnAutoSetupper;
    }

    /**
     * Set the auto set-upper of common column.
     * @param commonColumnAutoSetupper The auto set-upper of common column. (NotNull)
     */
    public void setCommonColumnAutoSetupper(CommonColumnAutoSetupper commonColumnAutoSetupper) {
        this._commonColumnAutoSetupper = commonColumnAutoSetupper;
    }
}
