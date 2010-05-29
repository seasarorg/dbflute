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

/**
 * @author jflute
 */
public abstract class ScopeNode extends AbstractNode {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ScopeNode() {
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    protected void processAcceptingChildren(CommandContext ctx, LoopInfo loopInfo) {
        final int childSize = getChildSize();
        for (int i = 0; i < childSize; i++) {
            final Node child = getChild(i);
            if (loopInfo != null) {
                if (child instanceof LoopAcceptable) {
                    ((LoopAcceptable) child).accept(ctx, loopInfo);
                } else {
                    child.accept(ctx);
                }
            } else {
                child.accept(ctx);
            }
        }
    }
}