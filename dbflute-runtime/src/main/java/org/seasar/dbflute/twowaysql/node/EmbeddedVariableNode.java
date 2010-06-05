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

import java.lang.reflect.Array;
import java.util.List;

import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class EmbeddedVariableNode extends VariableNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "$";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmbeddedVariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        super(expression, testValue, specifiedSql, blockNullParameter);
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType, LoopInfo loopInfo) {
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(firstValue);
        valueAndType.setTargetType(firstType);
        setupValueAndType(valueAndType);
        if (isQuotedScalar() && isAcceptableLike()) {
            valueAndType.inheritLikeSearchOptionIfNeeds(loopInfo);
            valueAndType.filterValueByOptionIfNeeds();
        }

        final Object finalValue = valueAndType.getTargetValue();
        final Class<?> finalType = valueAndType.getTargetType();
        if (finalValue == null) {
            if (_blockNullParameter) {
                throwBindOrEmbeddedCommentParameterNullValueException(valueAndType);
            }
            return;
        }
        if (isInScope()) {
            if (List.class.isAssignableFrom(finalType)) {
                embedArray(ctx, ((List<?>) finalValue).toArray());
            } else if (finalType.isArray()) {
                embedArray(ctx, finalValue);
            } else {
                throwBindOrEmbeddedCommentInScopeNotListException(valueAndType);
            }
        } else {
            final String embeddedString = finalValue.toString();
            if (embeddedString.indexOf("?") > -1) {
                String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                throw new IllegalStateException(msg);
            }
            if (isQuotedScalar()) {
                ctx.addSql("'" + embeddedString + "'");
                if (isAcceptableLike()) {
                    final String rearOption = valueAndType.buildRearOptionOnSql();
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(rearOption)) {
                        ctx.addSql(rearOption);
                    }
                }
                return;
            }
            if (processDynamicBinding(ctx, firstValue, firstType, embeddedString)) {
                return;
            }
            ctx.addSql(embeddedString);
        }
    }

    protected void embedArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedCommentParameterEmptyListException();
        }
        final String quote = isQuotedInScope() ? "'" : "";
        ctx.addSql("(");
        int validCount = 0;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (validCount == 0) {
                    ctx.addSql(quote + currentElement + quote);
                } else {
                    ctx.addSql(", " + quote + currentElement + quote);
                }
                ++validCount;
            }
        }
        if (validCount == 0) {
            throwBindOrEmbeddedCommentParameterNullOnlyListException();
        }
        ctx.addSql(")");
    }

    protected boolean isQuotedScalar() {
        if (_testValue == null) {
            return false;
        }
        return Srl.count(_testValue, "'") > 1 && _testValue.startsWith("'") && _testValue.endsWith("'");
    }

    protected boolean isQuotedInScope() {
        if (!isInScope()) {
            return false;
        }
        return Srl.count(_testValue, "'") > 1;
    }

    protected boolean processDynamicBinding(CommandContext ctx, Object firstValue, Class<?> firstType,
            String embeddedString) {
        final ScopeInfo first = Srl.extractScopeFirst(embeddedString, "/*", "*/");
        if (first == null) {
            return false;
        }
        final SqlAnalyzer analyzer = new SqlAnalyzer(embeddedString, _blockNullParameter);
        final Node rootNode = analyzer.analyze();
        final CommandContextCreator creator = new CommandContextCreator(new String[] { "pmb" },
                new Class<?>[] { firstType });
        final CommandContext rootCtx = creator.createCommandContext(new Object[] { firstValue });
        rootNode.accept(rootCtx);
        final String sql = rootCtx.getSql();
        ctx.addSql(sql, rootCtx.getBindVariables(), rootCtx.getBindVariableTypes());
        return true;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    @Override
    protected CommentType getCommentType() {
        return CommentType.EMBEDDED;
    }
}
