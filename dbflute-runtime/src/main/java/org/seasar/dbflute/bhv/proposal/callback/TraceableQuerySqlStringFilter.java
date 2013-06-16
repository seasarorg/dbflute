/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv.proposal.callback;

import java.lang.reflect.Method;

import org.seasar.dbflute.bhv.SqlStringFilter;
import org.seasar.dbflute.bhv.core.BehaviorCommandMeta;

/**
 * @author jflute
 * @since 1.0.4D (2013/06/16 Sunday)
 */
public class TraceableQuerySqlStringFilter implements SqlStringFilter {

    protected final TraceableAllSqlStringFilter _allFilter;

    public TraceableQuerySqlStringFilter(Method actionMethod, TraceableSqlAdditionalInfoProvider additionalInfoProvider) {
        _allFilter = new TraceableAllSqlStringFilter(actionMethod, additionalInfoProvider);
    }

    public String filterSelectCB(BehaviorCommandMeta meta, String executedSql) {
        return _allFilter.filterSelectCB(meta, executedSql);
    }

    public String filterEntityUpdate(BehaviorCommandMeta meta, String executedSql) {
        return null;
    }

    public String filterQueryUpdate(BehaviorCommandMeta meta, String executedSql) {
        return _allFilter.filterQueryUpdate(meta, executedSql);
    }

    public String filterOutsideSql(BehaviorCommandMeta meta, String executedSql) {
        return null;
    }

    public String filterProcedure(BehaviorCommandMeta meta, String executedSql) {
        return null;
    }

    public TraceableQuerySqlStringFilter markingAtFront() {
        _allFilter.markingAtFront();
        return this;
    }
}
