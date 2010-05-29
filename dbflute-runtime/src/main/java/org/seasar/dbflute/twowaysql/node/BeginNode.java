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
import org.seasar.dbflute.twowaysql.context.impl.CommandContextImpl;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class BeginNode extends ContainerNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String MARK = "BEGIN";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BeginNode() {
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    @Override
    public void accept(CommandContext ctx) {
        final CommandContext childCtx = CommandContextImpl.createCommandContextImplAsBeginChild(ctx);
        super.accept(childCtx);
        if (childCtx.isEnabled()) {
            ctx.addSql(childCtx.getSql(), childCtx.getBindVariables(), childCtx.getBindVariableTypes());
            if (ctx.isBeginChild()) { // means nested begin-node
                // to tell parent begin-node whether
                // nested begin-node is enabled or not
                ctx.setEnabled(true);
            }
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{}";
    }
}