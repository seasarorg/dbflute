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
 * The if-else child node of prefix SQL.
 * @author jflute
 */
public class PrefixSqlNode extends AbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _prefix;
    private String _sql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PrefixSqlNode(String prefix, String sql) {
        this._prefix = prefix;
        this._sql = sql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        if (ctx.isEnabled() || ctx.isAlreadySkippedPrefix()) {
            ctx.addSql(_prefix);
        } else if (isBeginChildAndValidSql(ctx, _sql)) {
            // To skip prefix should be done only once
            // so it marks that a prefix already skipped.
            ctx.setAlreadySkippedPrefix(true);
        }
        ctx.addSql(_sql);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _prefix + ", " + _sql + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPrefix() {
        return _prefix;
    }

    public String getSql() {
        return _sql;
    }
}
