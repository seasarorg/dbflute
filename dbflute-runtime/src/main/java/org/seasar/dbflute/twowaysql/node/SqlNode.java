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
package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class SqlNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _sql;
    private boolean _skipPrefix;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private SqlNode(String sql) {
        this._sql = sql;
    }

    // -----------------------------------------------------
    //                                               Factory
    //                                               -------
    public static SqlNode createSqlNode(String sql) {
        return new SqlNode(sql);
    }

    public static SqlNode createSqlNodeAsSkipPrefix(String sql) {
        return new SqlNode(sql).asSkipPrefix();
    }

    private SqlNode asSkipPrefix() {
        _skipPrefix = true;
        return this;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        ctx.addSql(_sql);
        if (_skipPrefix && isBeginChildAndValidSql(ctx, _sql)) {
            // It does not skipped actually but it has not already needed to skip.
            ctx.setAlreadySkippedPrefix(true);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _sql + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSql() {
        return _sql;
    }
}
