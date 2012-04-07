/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.core;

/**
 * The meta information interface of behavior commands. <br />
 * You can get what the behavior command is.
 * @author jflute
 */
public interface BehaviorCommandMeta {

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    /**
     * Get the DB name of table corresponding to the executed behavior.
     * @return The DB name of table. (NotNull)
     */
    String getTableDbName();

    /**
     * Get the name of the command, e.g. selectList, update.
     * @return The name of the command. (NotNull)
     */
    String getCommandName();

    /**
     * Get the return type of command.
     * This type is not related to generic type because this is for conversion and check only.
     * @return The return type of command. (NotNull)
     */
    Class<?> getCommandReturnType();

    /**
     * Is the command only for initialization?
     * @return The determination, true or false.
     */
    boolean isInitializeOnly();

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    /**
     * Does the command use condition-bean? <br />
     * e.g. selectList(cb), queryUpdate(entity, cb)
     * @return The determination, true or false.
     */
    boolean isConditionBean();

    /**
     * Does the command use outside-SQL? <br />
     * It contains procedure calls. <br />
     * e.g. outsideSql().selectList(pmb), outsideSql().execute(pmb)
     * @return The determination, true or false.
     */
    boolean isOutsideSql();

    /**
     * Does the command call procedure? <br />
     * But if an outside-SQL containing procedure call in it, it returns false. <br />
     * e.g. outsideSql().call(pmb)
     * @return The determination, true or false.
     */
    boolean isProcedure();

    /**
     * Does the command return selected records? <br />
     * But if it's a procedure, it returns false. <br />
     * e.g. selectList(cb), outsideSql().select(pmb)
     * @return The determination, true or false.
     */
    boolean isSelect();

    /**
     * Does the command return selected record count?
     * All outside-SQL return false, even if it has 'select count(*)' in the outside-SQL.
     * e.g. selectCount(cb)
     * @return The determination, true or false.
     */
    boolean isSelectCount();

    /**
     * Does the command handle its cursor?
     * e.g. selectCursor(cb), outsideSql().cursorHandling().selectCursor(pmb)
     * @return The determination, true or false.
     */
    boolean isSelectCursor();
}
