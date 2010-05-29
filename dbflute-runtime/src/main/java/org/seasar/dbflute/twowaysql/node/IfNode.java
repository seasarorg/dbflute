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
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class IfNode extends ContainerNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "IF ";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected ElseNode _elseNode;
    protected String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public IfNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    @Override
    public void accept(CommandContext ctx) {
        doAcceptByEvaluator(ctx, null);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) {
        doAcceptByEvaluator(ctx, loopInfo);
    }

    protected void doAcceptByEvaluator(CommandContext ctx, LoopInfo loopInfo) {
        final IfCommentEvaluator evaluator = createIfCommentEvaluator(ctx);
        final boolean result = evaluator.evaluate();
        if (result) {
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
            ctx.setEnabled(true);
        } else if (_elseNode != null) {
            _elseNode.accept(ctx);
            ctx.setEnabled(true);
        }
    }

    protected IfCommentEvaluator createIfCommentEvaluator(final CommandContext ctx) {
        return new IfCommentEvaluator(new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, _expression, _specifiedSql);
    }

    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }

    public ElseNode getElseNode() {
        return _elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this._elseNode = elseNode;
    }
}
