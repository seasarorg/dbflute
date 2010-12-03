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

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The interface of behavior-readable.
 * @author jflute
 */
public interface BehaviorReadable {

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    String getTableDbName();

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the instance of DBMeta.
     * @return The instance of DBMeta. (NotNull)
     */
    DBMeta getDBMeta();

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /**
     * New entity instance.
     * @return Entity. (NotNull)
     */
    Entity newEntity();

    /**
     * New condition-bean instance.
     * @return Condition-bean. (NotNull)
     */
    ConditionBean newConditionBean();

    // ===================================================================================
    //                                                                    Basic Read Count
    //                                                                    ================
    /**
     * Read count by condition-bean. {dispatched to selectCount(cb)}<br />
     * @param cb The instance of corresponding condition-bean. (NotNull)
     * @return Read count. (NotNull)
     */
    int readCount(ConditionBean cb);

    // ===================================================================================
    //                                                                   Basic Read Entity
    //                                                                   =================
    /**
     * Read entity by condition-bean. {dispatched to selectEntity(cb)}
     * @param cb The instance of corresponding condition-bean. (NotNull)
     * @return Read entity. (Nullalble)
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    Entity readEntity(ConditionBean cb);

    /**
     * Read simple entity by condition-bean with deleted check. {dispatched to selectEntityWithDeletedCheck(cb)}
     * @param cb The instance of corresponding condition-bean. (NotNull)
     * @return Read entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    Entity readEntityWithDeletedCheck(ConditionBean cb);

    // ===================================================================================
    //                                                                     Basic Read List
    //                                                                     ===============
    /**
     * Read list as result-bean. {dispatched to selectList(cb)}
     * @param cb The instance of corresponding condition-bean. (NotNull)
     * @return The list of entity as result-bean. If the select result is zero, it returns empty list. (NotNull)
     */
    ListResultBean<? extends Entity> readList(ConditionBean cb);

    /**
     * Read page as result-bean. {dispatched to selectPage(cb)}
     * @param cb The instance of corresponding condition-bean. (NotNull)
     * @return The page of entity as result-bean. (NotNull)
     */
    PagingResultBean<? extends Entity> readPage(final ConditionBean cb);

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * Read next value of sequence. {dispatched to selectNextVal(cb)}
     * @return The next value of sequence. (NotNull)
     */
    Number readNextVal();

    // ===================================================================================
    //                                                                             Warm Up
    //                                                                             =======
    /**
     * Warm up the command of behavior.
     */
    void warmUpCommand();
}
