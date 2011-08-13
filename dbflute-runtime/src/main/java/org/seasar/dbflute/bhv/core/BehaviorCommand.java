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

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;

/**
 * @author jflute
 * @param <RESULT> The type of result.
 */
public interface BehaviorCommand<RESULT> extends BehaviorCommandMeta {

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    void beforeGettingSqlExecution();

    void afterExecuting();

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    String buildSqlExecutionKey();

    SqlExecutionCreator createSqlExecutionCreator();

    Object[] getSqlExecutionArgument();

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    /**
     * Get the instance of condition-bean specified as argument if it exists.
     * @return The instance of condition-bean. (NullAllowed)
     */
    ConditionBean getConditionBean();

    /**
     * Get the path of outside-SQL if it's outside-SQL.
     * @return The path of outside-SQL. (NullAllowed)
     */
    String getOutsideSqlPath();

    /**
     * Get the parameter-bean for outside-SQL if it's outside-SQL.
     * @return The parameter-bean for outside-SQL. (NullAllowed)
     */
    Object getParameterBean();

    /**
     * Get the option of outside-SQL if it's outside-SQL.
     * @return The option of outside-SQL. (NullAllowed)
     */
    OutsideSqlOption getOutsideSqlOption();
}
