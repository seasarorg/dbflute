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
import org.seasar.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 */
public class EmbeddedVariableNode extends AbstractNode implements LoopAcceptable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "$";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected String _testValue;
    protected List<String> _nameList;
    protected String _specifiedSql;
    protected boolean _blockNullParameter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmbeddedVariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        this._expression = expression;
        this._testValue = testValue;
        this._nameList = Srl.splitList(expression, ".");
        this._specifiedSql = specifiedSql;
        this._blockNullParameter = blockNullParameter;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        final String firstName = _nameList.get(0);
        assertFirstName(ctx, firstName);
        final Object firstValue = ctx.getArg(firstName);
        final Class<?> firstType = ctx.getArgType(firstName);
        doAccept(ctx, firstValue, firstType);
    }

    public void accept(CommandContext ctx, LoopInfo loopInfo) { // for FOR comment
        final String firstName = _nameList.get(0);
        if (firstName.equals(ForNode.CURRENT_PARAMETER)) { // use loop element
            final Object parameter = loopInfo.getCurrentParameter();
            doAccept(ctx, parameter, parameter.getClass());

        } else { // normal
            accept(ctx);
        }
    }

    // *like-search option is unsupported in embedded comment

    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType) {
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(firstValue);
        valueAndType.setTargetType(firstType);
        setupValueAndType(valueAndType);

        final Object targetValue = valueAndType.getTargetValue();
        if (targetValue == null) {
            if (_blockNullParameter) {
                throwBindOrEmbeddedParameterNullValueException(valueAndType);
            }
            return;
        }
        final String embeddedString = targetValue.toString();
        if (processDynamicBinding(ctx, firstValue, firstType, embeddedString)) {
            return;
        }
        if (!isInScope()) {
            // main root
            if (embeddedString.indexOf("?") > -1) {
                String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                throw new IllegalStateException(msg);
            }
            ctx.addSql(embeddedString);
        } else {
            if (List.class.isAssignableFrom(valueAndType.getTargetType())) {
                embedArray(ctx, ((List<?>) valueAndType.getTargetValue()).toArray());
            } else if (valueAndType.getTargetType().isArray()) {
                embedArray(ctx, valueAndType.getTargetValue());
            } else {
                if (embeddedString.indexOf("?") > -1) {
                    String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                    msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                    throw new IllegalStateException(msg);
                }
                ctx.addSql(embeddedString.toString());
            }
        }
    }

    protected void assertFirstName(final CommandContext ctx, String firstName) {
        NodeUtil.assertParameterBeanName(firstName, new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, new IllegalParameterBeanHandler() {
            public void handle(ParameterBean pmb) {
                throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(pmb);
            }
        });
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final CommentType type = CommentType.EMBEDDED;
        final ValueAndTypeSetupper setupper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setupper.setupValueAndType(valueAndType);
    }

    protected void throwBindOrEmbeddedParameterNullValueException(ValueAndType valueAndType) {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullValueException(_expression, valueAndType.getTargetType(),
                _specifiedSql, false);
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

    protected boolean isInScope() {
        return _testValue != null && _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    protected void embedArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedCommentParameterEmptyListException();
        }
        String quote = null;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                quote = !(currentElement instanceof Number) ? "'" : "";
                break;
            }
        }
        if (quote == null) {
            throwBindOrEmbeddedCommentParameterNullOnlyListException();
        }
        boolean existsValidElements = false;
        ctx.addSql("(");
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (!existsValidElements) {
                    ctx.addSql(quote + currentElement + quote);
                    existsValidElements = true;
                } else {
                    ctx.addSql(", " + quote + currentElement + quote);
                }
            }
        }
        ctx.addSql(")");
    }

    protected void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(ParameterBean pmb) {
        NodeUtil.throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(_expression, _specifiedSql,
                false, pmb);
    }

    protected void throwBindOrEmbeddedCommentParameterEmptyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterEmptyListException(_expression, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedCommentParameterNullOnlyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullOnlyListException(_expression, _specifiedSql, false);
    }
}
