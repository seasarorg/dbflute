/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.exception.handler;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityDuplicatedException;
import org.seasar.dbflute.exception.OptimisticLockColumnValueNullException;
import org.seasar.dbflute.exception.msgbuilder.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class BehaviorExceptionThrower {

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public void throwSelectEntityAlreadyDeletedException(Object searchKey) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was NOT found! it has already been deleted.");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of your target record on your database.");
        br.addElement("Does the target record really created before this operation?");
        br.addElement("Has the target record been deleted by other thread?");
        br.addElement("It is precondition that the record exists on your database.");
        if (searchKey != null && searchKey instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey;
            br.addItem("Display SQL");
            br.addElement(cb.toDisplaySql());
        } else {
            br.addItem("Search Condition");
            br.addElement(searchKey);
        }
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyDeletedException(msg);
    }

    public void throwSelectEntityDuplicatedException(String resultCountExp, Object searchKey, Throwable cause) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was duplicated! It should be the only one.");
        br.addItem("Advice");
        br.addElement("Confirm your search condition. Does it really select the only one?");
        br.addElement("And confirm your database. Does it really exist the only one?");
        br.addItem("Result Count");
        br.addElement(resultCountExp);
        setupSearchKeyElement(br, searchKey);
        final String msg = br.buildExceptionMessage();
        if (cause != null) {
            throw new EntityDuplicatedException(msg, cause);
        } else {
            throw new EntityDuplicatedException(msg);
        }
    }

    protected void setupSearchKeyElement(ExceptionMessageBuilder br, Object searchKey) {
        if (searchKey != null && searchKey instanceof ConditionBean) {
            final ConditionBean cb = (ConditionBean) searchKey;
            br.addItem("Display SQL");
            br.addElement(cb.toDisplaySql());
        } else {
            br.addItem("Search Condition");
            br.addElement(searchKey);
        }
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public <ENTITY extends Entity> void throwUpdateEntityAlreadyDeletedException(ENTITY entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was not found! it has already been deleted.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyDeletedException(msg);
    }

    public <ENTITY extends Entity> void throwUpdateEntityDuplicatedException(ENTITY entity, int count) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity was duplicated. It should be the only one!");
        br.addItem("Count");
        br.addElement(count);
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new EntityDuplicatedException(msg);
    }

    public void throwVersionNoValueNullException(Entity entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the value of 'version no' on the entity!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of the value of 'version no' on the entity.");
        br.addElement("You called the method in which the check for optimistic lock is indispensable.");
        br.addElement("So 'version no' is required on the entity. In addition, please confirm");
        br.addElement("the necessity of optimistic lock. It might possibly be unnecessary.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new OptimisticLockColumnValueNullException(msg);
    }

    public void throwUpdateDateValueNullException(Entity entity) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the value of 'update date' on the entity!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of the value of 'update date' on the entity.");
        br.addElement("You called the method in which the check for optimistic lock is indispensable.");
        br.addElement("So 'update date' is required on the entity. In addition, please confirm");
        br.addElement("the necessity of optimistic lock. It might possibly be unnecessary.");
        setupEntityElement(br, entity);
        final String msg = br.buildExceptionMessage();
        throw new OptimisticLockColumnValueNullException(msg);
    }

    protected void setupEntityElement(ExceptionMessageBuilder br, Entity entity) {
        br.addItem("Entity");
        br.addElement(entity.toString());
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Get the value of line separator.
     * @return The value of line separator. (NotNull)
     */
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }
}
