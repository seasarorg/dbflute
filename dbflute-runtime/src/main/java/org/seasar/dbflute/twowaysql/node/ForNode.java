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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.exception.ForCommentIllegalParameterBeanSpecificationException;
import org.seasar.dbflute.twowaysql.exception.ForCommentParameterNotListException;
import org.seasar.dbflute.twowaysql.node.NodeUtil.IllegalParameterBeanHandler;
import org.seasar.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The node for FOR (loop). <br />
 * FOR comment is evaluated before analyzing nodes,
 * so it is not related to container node.
 * @author jflute
 */
public class ForNode extends ContainerNode {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PREFIX = "FOR ";
    public static final String ELEMENT = "#current";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected List<String> _nameList;
    protected String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ForNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._nameList = Srl.splitList(expression, ".");
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(CommandContext ctx) {
        final String firstName = _nameList.get(0);
        assertFirstName(ctx, firstName);
        final Object value = ctx.getArg(firstName);
        final Class<?> clazz = ctx.getArgType(firstName);
        final ValueAndType valueAndType = new ValueAndType();
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        setupValueAndType(valueAndType);
        final Object targetValue = valueAndType.getTargetValue();
        if (targetValue == null) {
            return;
        }
        assertParameterList(targetValue);
        final List<?> parameterList = (List<?>) targetValue;
        final int loopSize = parameterList.size();
        final LoopInfo loopInfo = new LoopInfo();
        loopInfo.setParameterList(parameterList);
        loopInfo.setLoopSize(loopSize);
        loopInfo.setLikeSearchOption(valueAndType.getLikeSearchOption());
        for (int loopIndex = 0; loopIndex < loopSize; loopIndex++) {
            loopInfo.setLoopIndex(loopIndex);
            processChildNode(ctx, loopInfo);
        }
        if (loopSize > 0) {
            ctx.setEnabled(true);
        }
    }

    protected void assertFirstName(final CommandContext ctx, String firstName) {
        NodeUtil.assertParameterBeanName(firstName, new ParameterFinder() {
            public Object find(String name) {
                return ctx.getArg(name);
            }
        }, new IllegalParameterBeanHandler() {
            public void handle(ParameterBean pmb) {
                throwForCommentIllegalParameterBeanSpecificationException(pmb);
            }
        });
    }

    protected void throwForCommentIllegalParameterBeanSpecificationException(ParameterBean pmb) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The FOR comment had the illegal parameter-bean specification!");
        br.addItem("Advice");
        br.addElement("Please confirm your FOR comment.");
        br.addElement("For example:");
        br.addElement("  (x) - /*FOR pmb,memberId*/");
        br.addElement("  (x) - /*FOR p mb,memberId*/");
        br.addElement("  (x) - /*FOR pmb:memberId*/");
        br.addElement("  (x) - /*FOR pmb,memberId*/");
        br.addElement("  (o) - /*FOR pmb,memberId*/");
        br.addItem("FOR Comment Expression");
        br.addElement(_expression);
        // *debug to this exception does not need contents of the parameter-bean
        //  (and for security to application data)
        //br.addItem("ParameterBean");
        //br.addElement(pmb);
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new ForCommentIllegalParameterBeanSpecificationException(msg);
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        final CommentType type = CommentType.FORCOMMENT;
        final ValueAndTypeSetupper setuper = new ValueAndTypeSetupper(_nameList, _expression, _specifiedSql, type);
        setuper.setupValueAndType(valueAndType);
    }

    protected void assertParameterList(Object targetValue) {
        if (!List.class.isInstance(targetValue)) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("The parameter for FOR coment was not list.");
            br.addItem("FOR Comment");
            br.addElement("/*FOR " + _expression + "*/");
            br.addItem("Parameter");
            br.addElement(targetValue.getClass());
            br.addElement(targetValue);
            br.addItem("Specified SQL");
            br.addElement(_specifiedSql);
            String msg = br.buildExceptionMessage();
            throw new ForCommentParameterNotListException(msg);
        }
    }

    // ===================================================================================
    //                                                                       Loop Variable
    //                                                                       =============
    public enum LoopVariableType {
        FIRST("first", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopFirstNode(expression, specifiedSql);
            }
        }), NEXT("next", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopNextNode(expression, specifiedSql);
            }
        }), LAST("last", new LoopVariableNodeFactory() {
            public LoopAbstractNode create(String expression, String specifiedSql) {
                return new LoopLastNode(expression, specifiedSql);
            }
        });
        private static final Map<String, LoopVariableType> _codeValueMap = new HashMap<String, LoopVariableType>();
        static {
            for (LoopVariableType value : values()) {
                _codeValueMap.put(value.code().toLowerCase(), value);
            }
        }
        private String _code;
        private LoopVariableNodeFactory _factory;

        private LoopVariableType(String code, LoopVariableNodeFactory factory) {
            _code = code;
            _factory = factory;
        }

        public String code() {
            return _code;
        }

        public static LoopVariableType codeOf(Object code) {
            if (code == null) {
                return null;
            }
            if (code instanceof LoopVariableType) {
                return (LoopVariableType) code;
            }
            return _codeValueMap.get(code.toString().toLowerCase());
        }

        public LoopAbstractNode createNode(String expression, String specifiedSql) {
            return _factory.create(expression, specifiedSql);
        }
    }

    public interface LoopVariableNodeFactory {
        LoopAbstractNode create(String expression, String specifiedSql);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _expression + "}";
    }
}
