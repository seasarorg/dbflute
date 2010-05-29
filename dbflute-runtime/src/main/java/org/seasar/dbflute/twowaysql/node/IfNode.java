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

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class IfNode extends ContainerNode {

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
        doAcceptByEvaluator(ctx, null, 0, 0);
    }

    public void accept(CommandContext ctx, Object element, int loopSize, int loopIndex) {
        doAcceptByEvaluator(ctx, element, loopSize, loopIndex);
    }

    protected void doAcceptByEvaluator(CommandContext ctx, Object element, int loopSize, int loopIndex) {
        final IfCommentEvaluator evaluator = createIfCommentEvaluator(ctx);
        final boolean result = evaluator.evaluate();
        if (result) {
            final int childSize = getChildSize();
            for (int i = 0; i < childSize; i++) {
                final Node child = getChild(i);
                if (element != null) {
                    if (child instanceof LoopAbstractNode) {
                        ((LoopAbstractNode) child).accept(ctx, loopSize, loopIndex);
                    } else if (child instanceof BindVariableNode) {
                        final LikeSearchOption option = valueAndType.getLikeSearchOption();
                        ((BindVariableNode) child).accept(ctx, element, option);
                    } else if (child instanceof EmbeddedVariableNode) {
                        final LikeSearchOption option = valueAndType.getLikeSearchOption();
                        ((EmbeddedVariableNode) child).accept(ctx, element, option);
                    }
                } else {
                    getChild(i).accept(ctx);
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
