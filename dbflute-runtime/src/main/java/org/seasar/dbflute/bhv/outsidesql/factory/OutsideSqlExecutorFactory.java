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
package org.seasar.dbflute.bhv.outsidesql.factory;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.dbflute.bhv.outsidesql.OutsideSqlBasicExecutor;
import org.seasar.dbflute.jdbc.StatementConfig;

/**
 * @author jflute
 */
public interface OutsideSqlExecutorFactory {

    /**
     * Create the basic executor of outside SQL.
     * @param invoker The invoker of behavior command. (NotNull)
     * @param tableDbName The DB name of table. (NotNull)
     * @param dbdef The definition of DBMS. (NotNull)
     * @param config The default configuration of statement. (Nullable)
     * @return The instance of executor. (NotNull)
     */
    OutsideSqlBasicExecutor createBasic(BehaviorCommandInvoker invoker, String tableDbName, DBDef dbdef,
            StatementConfig config);
}
