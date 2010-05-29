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

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class BindVariableNode extends AbstractNode implements LoopAcceptable {

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
    public BindVariableNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
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
        if (firstName.equals(ForNode.ELEMENT)) { // use loop element
            final Object parameter = loopInfo.getCurrentParameter();
            final LikeSearchOption option = loopInfo.getLikeSearchOption();
            doAccept(ctx, parameter, parameter.getClass(), option);
        } else { // normal
            accept(ctx);
        }
    }

    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType) {
        doAccept(ctx, firstValue, firstType, null);
    }

    protected void doAccept(CommandContext ctx, Object firstValue, Class<?> firstType, LikeSearchOption outerOption) {
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(firstValue);
        valueAndType.setTargetType(firstType);
        setupValueAndType(valueAndType);
        if (outerOption != null) {
            valueAndType.setLikeSearchOption(outerOption); // inherit
        }
        valueAndType.filterValueByOptionIfNeeds();

        if (_blockNullParameter && valueAndType.getTargetValue() == null) {
            throwBindOrEmbeddedParameterNullValueException(valueAndType);
        }
        if (!isInScope()) {
            // main root
            ctx.addSql("?", valueAndType.getTargetValue(), valueAndType.getTargetType());
        } else {
            if (List.class.isAssignableFrom(valueAndType.getTargetType())) {
                bindArray(ctx, ((List<?>) valueAndType.getTargetValue()).toArray());
            } else if (valueAndType.getTargetType().isArray()) {
                bindArray(ctx, valueAndType.getTargetValue());
            } else {
                ctx.addSql("?", valueAndType.getTargetValue(), valueAndType.getTargetType());
            }
        }
        final String rearOption = valueAndType.buildRearOptionOnSql();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(rearOption)) {
            ctx.addSql(rearOption);
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
        final CommentType type = CommentType.BIND;
        final ValueAndTypeSetupper setuper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setuper.setupValueAndType(valueAndType);
    }

    protected void throwBindOrEmbeddedParameterNullValueException(ValueAndType valueAndType) {
        final Class<?> targetType = valueAndType.getTargetType();
        NodeUtil.throwBindOrEmbeddedCommentParameterNullValueException(_expression, targetType, _specifiedSql, true);
    }

    protected boolean isInScope() {
        return _testValue != null && _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    protected void bindArray(CommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedParameterEmptyListException();
        }
        Class<?> clazz = null;
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                clazz = currentElement.getClass();
                break;
            }
        }
        if (clazz == null) {
            throwBindOrEmbeddedParameterNullOnlyListException();
        }
        boolean existsValidElements = false;
        ctx.addSql("(");
        for (int i = 0; i < length; ++i) {
            final Object currentElement = Array.get(array, i);
            if (currentElement != null) {
                if (!existsValidElements) {
                    ctx.addSql("?", currentElement, clazz);
                    existsValidElements = true;
                } else {
                    ctx.addSql(", ?", currentElement, clazz);
                }
            }
        }
        ctx.addSql(")");
    }

    protected void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(ParameterBean pmb) {
        NodeUtil.throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(_expression, _specifiedSql, true,
                pmb);
    }

    protected void throwBindOrEmbeddedParameterEmptyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterEmptyListException(_expression, _specifiedSql, true);
    }

    protected void throwBindOrEmbeddedParameterNullOnlyListException() {
        NodeUtil.throwBindOrEmbeddedCommentParameterNullOnlyListException(_expression, _specifiedSql, true);
    }
}
