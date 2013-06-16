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
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 1.0.4D (2013/06/16 Sunday)
 */
public class SimpleTraceableSqlStringFilter implements SqlStringFilter {

    protected final Method _actionMethod;
    protected final TraceableSqlAdditionalInfoProvider _additionalInfoProvider;
    protected boolean _markingAtFront;

    public SimpleTraceableSqlStringFilter(Method actionMethod, TraceableSqlAdditionalInfoProvider additionalInfoProvider) {
        _actionMethod = actionMethod;
        _additionalInfoProvider = additionalInfoProvider;
    }

    public String filterSelectCB(BehaviorCommandMeta meta, String executedSql) {
        return markingSql(executedSql);
    }

    public String filterEntityUpdate(BehaviorCommandMeta meta, String executedSql) {
        return markingSql(executedSql);
    }

    public String filterQueryUpdate(BehaviorCommandMeta meta, String executedSql) {
        return markingSql(executedSql);
    }

    public String filterOutsideSql(BehaviorCommandMeta meta, String executedSql) {
        // outside-SQL is easy to find caller by SQL
        // and it might have unexpected SQL so no marking
        //return markingSql(executedSql);
        return null;
    }

    public String filterProcedure(BehaviorCommandMeta meta, String executedSql) {
        // procedure call uses JDBC's escape "{" and "}"
        // so it might fail to execute the SQL (actually PostgreSQL)
        //return markingSql(executedSql);
        return null;
    }

    protected String markingSql(String executedSql) {
        final String filtered;
        if (_markingAtFront) {
            filtered = "-- " + buildInvokeMark() + "\n" + executedSql;
        } else { // default here
            filtered = executedSql + "\n-- " + buildInvokeMark();
        }
        return filtered;
    }

    protected String buildInvokeMark() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_actionMethod.getDeclaringClass().getName());
        sb.append("#").append(_actionMethod.getName()).append("()");
        if (_additionalInfoProvider != null) {
            final String addiitonalInfo = _additionalInfoProvider.provide();
            if (addiitonalInfo != null) {
                sb.append(": ").append(Srl.replace(addiitonalInfo, "?", "Q")); // filter bind mark
            }
        }
        return sb.toString();
    }

    public SimpleTraceableSqlStringFilter markingAtFront() {
        _markingAtFront = true;
        return this;
    }
}
