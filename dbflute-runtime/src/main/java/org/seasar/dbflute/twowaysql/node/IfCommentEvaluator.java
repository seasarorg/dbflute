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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.exception.IfCommentEmptyExpressionException;
import org.seasar.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.dbflute.exception.IfCommentNotFoundMethodException;
import org.seasar.dbflute.exception.IfCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.IfCommentNullPointerException;
import org.seasar.dbflute.exception.IfCommentUnsupportedExpressionException;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class IfCommentEvaluator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String AND = " && ";
    private static final String OR = " || ";
    private static final String EQUAL = " == ";
    private static final String NOT_EQUAL = " != ";
    private static final String BOOLEAN_NOT = "!";
    private static final String METHOD_SUFFIX = "()";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Object _pmb;
    private String _expression;
    private String _specifiedSql;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public IfCommentEvaluator(Object pmb, String expression, String specifiedSql) {
        this._pmb = pmb;
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public boolean evaluate() {
        assertExpression();
        _expression = _expression.trim();
        if (_expression.contains(AND)) {
            final List<String> splitList = splitList(_expression, AND);
            for (String booleanClause : splitList) {
                final boolean result = evaluateBooleanClause(booleanClause);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else if (_expression.contains(OR)) {
            final List<String> splitList = splitList(_expression, OR);
            for (String booleanClause : splitList) {
                final boolean result = evaluateBooleanClause(booleanClause);
                if (result) {
                    return true;
                }
            }
            return false;
        } else {
            return evaluateBooleanClause(_expression);
        }
    }

    public void assertExpression() {
        if (_expression == null || _expression.trim().length() == 0) {
            throwIfCommentEmptyExpressionException();
        }
        String filtered = DfStringUtil.replace(_expression, "()", "");
        if (filtered.contains("(") || filtered.contains(")")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(AND) && _expression.contains(OR)) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains("'") || _expression.contains("\"")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(" < ") || _expression.contains(" > ")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(" <= ") || _expression.contains(" >= ")) {
            throwIfCommentUnsupportedExpressionException();
        }
        if (_expression.contains(" = ") || _expression.contains(" <> ")) {
            throwIfCommentUnsupportedExpressionException();
        }
    }

    protected boolean evaluateBooleanClause(String booleanClause) {
        if (booleanClause.contains(EQUAL)) {
            return evaluateCompareClause(booleanClause, EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    return leftResult != null ? leftResult.equals(rightResult) : rightResult == null;
                }
            });
        } else if (booleanClause.contains(NOT_EQUAL)) {
            return evaluateCompareClause(booleanClause, NOT_EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    return leftResult != null ? !leftResult.equals(rightResult) : rightResult != null;
                }
            });
        } else {
            return evaluateStandAloneValue(booleanClause);
        }
    }

    protected boolean evaluateCompareClause(String booleanClause, String operand, OperandEvaluator evaluator) {
        final String left = booleanClause.substring(0, booleanClause.indexOf(operand)).trim();
        final String right = booleanClause.substring(booleanClause.indexOf(operand) + operand.length()).trim();
        final Object leftResult = evaluateComparePiece(left);
        final Object rightResult = evaluateComparePiece(right);
        return evaluator.evaluate(leftResult, rightResult);
    }

    protected static interface OperandEvaluator {
        boolean evaluate(Object leftResult, Object rightRight);
    }

    protected Object evaluateComparePiece(String piece) {
        piece = piece.trim();
        if ("null".equalsIgnoreCase(piece)) {
            return null;
        }
        final List<String> splitList = splitList(piece, ".");
        final List<String> propertyList = new ArrayList<String>();
        for (int i = 0; i < splitList.size(); i++) {
            if (i == 0) {
                continue;
            }
            propertyList.add(splitList.get(i));
        }
        Object baseObject = _pmb;
        String preProperty = !splitList.isEmpty() ? splitList.get(0) : null;
        for (String property : propertyList) {
            if (baseObject == null) {
                throwIfCommentNullPointerException(preProperty);
            }
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(baseObject.getClass());
            if (property.endsWith(METHOD_SUFFIX)) {
                final String methodName = property.substring(0, property.length() - METHOD_SUFFIX.length());
                try {
                    final Method method = beanDesc.getMethod(methodName);
                    baseObject = DfReflectionUtil.invoke(method, baseObject, (Object[]) null);
                } catch (DfBeanMethodNotFoundException e) {
                    throwIfCommentNotFoundMethodException(methodName);
                }
            } else {
                try {
                    final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(property);
                    baseObject = propertyDesc.getValue(baseObject);
                } catch (DfBeanPropertyNotFoundException e) {
                    throwIfCommentNotFoundPropertyException(property);
                }
            }
            preProperty = property;
        }
        return baseObject;
    }

    protected boolean evaluateStandAloneValue(String piece) {
        piece = piece.trim();
        boolean not = false;
        if (piece.startsWith(BOOLEAN_NOT)) {
            not = true;
            piece = piece.substring(BOOLEAN_NOT.length());
        }
        final List<String> splitList = splitList(piece, ".");
        final List<String> propertyList = new ArrayList<String>();
        for (int i = 0; i < splitList.size(); i++) {
            if (i == 0) {
                continue;
            }
            propertyList.add(splitList.get(i));
        }
        Object baseObject = _pmb;
        String preProperty = !splitList.isEmpty() ? splitList.get(0) : null;
        for (String property : propertyList) {
            if (baseObject == null) {
                throwIfCommentNullPointerException(preProperty);
            }
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(baseObject.getClass());
            if (property.endsWith(METHOD_SUFFIX)) {
                final String methodName = property.substring(0, property.length() - METHOD_SUFFIX.length());
                try {
                    final Method method = beanDesc.getMethod(methodName);
                    baseObject = DfReflectionUtil.invoke(method, baseObject, (Object[]) null);
                } catch (DfBeanMethodNotFoundException e) {
                    throwIfCommentNotFoundMethodException(methodName);
                }
            } else {
                try {
                    final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(property);
                    baseObject = propertyDesc.getValue(baseObject);
                } catch (DfBeanPropertyNotFoundException e) {
                    throwIfCommentNotFoundPropertyException(property);
                }
            }
            preProperty = property;
        }
        if (baseObject == null) {
            throwIfCommentNotBooleanResultException();
        }
        final boolean result = Boolean.valueOf(baseObject.toString());
        return not ? !result : result;
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    protected void throwIfCommentEmptyExpressionException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment expression was empty!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment." + ln();
        msg = msg + "  For example, correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF */" + ln();
        msg = msg + "    (o) - /*IF pmb.fooId != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentEmptyExpressionException(msg);
    }

    protected void throwIfCommentUnsupportedExpressionException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment expression was unsupported!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your unsupported IF comment." + ln();
        msg = msg + "  For example, unsupported examples:" + ln();
        msg = msg + "    (x:andOr) - /*IF (pmb.fooId != null || pmb.barId != null) && pmb.fooName != null*/" + ln();
        msg = msg + "    (x:argsMethod) - /*IF pmb.buildFooId(123)*/" + ln();
        msg = msg + "    (x:numberLiteral) - /*IF pmb.fooId == 3*/" + ln();
        msg = msg + "    (x:stringLiteral) - /*IF pmb.fooName == 'Pixy' || pmb.fooName == \"Pixy\"*/" + ln();
        msg = msg + "    (x:greaterThan) - /*IF pmb.fooId > 3*/" + ln();
        msg = msg + "    (x:lessThan) - /*IF pmb.fooId < 3*/" + ln();
        msg = msg + "    (x:greaterEqual) - /*IF pmb.fooId >= 3*/" + ln();
        msg = msg + "    (x:lessEqual) - /*IF pmb.fooId <= 3*/" + ln();
        msg = msg + "    (x:singleEqual) - /*IF pmb.fooId = null*/ --> /*IF pmb.fooId == null*/" + ln();
        msg = msg + "    (x:anotherNot) - /*IF pmb.fooId <> null*/ --> /*IF pmb.fooId != null*/" + ln();
        msg = msg + "    " + ln();
        msg = msg + "If you want to write a complex condition, write an ExParameterBean property." + ln();
        msg = msg + "And use the property in IF comment." + ln();
        msg = msg + "  For example, ExParameterBean original property:" + ln();
        msg = msg + "    ex) ExParameterBean (your original property)" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    public boolean isOriginalFooProperty() {" + ln();
        msg = msg + "        return (getFooId() != null || getBarId() != null) && getFooName() != null);" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + "    " + ln();
        msg = msg + "    ex) IF comment" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    /*IF pmb.originalFooProperty*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentUnsupportedExpressionException(msg);
    }

    protected void throwIfCommentNotFoundMethodException(String notFoundMethod) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment method was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment properties." + ln();
        msg = msg + "  For example, correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF pmb.getMemborWame() != null*/" + ln();
        msg = msg + "    (o) - /*IF pmb.getMemberName() != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Method]" + ln() + notFoundMethod + "()" + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotFoundMethodException(msg);
    }

    protected void throwIfCommentNotFoundPropertyException(String notFoundProperty) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment property was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment properties." + ln();
        msg = msg + "  For example, correct IF comment is as below:" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    (x) - /*IF pmb.memderBame != null*/" + ln();
        msg = msg + "    (o) - /*IF pmb.memberName != null*/" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Property]" + ln() + notFoundProperty + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotFoundPropertyException(msg);
    }

    protected void throwIfCommentNullPointerException(String nullProperty) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment had the null pointer!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment and its property values." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Null Property]" + ln() + nullProperty + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNullPointerException(msg);
    }

    protected void throwIfCommentNotBooleanResultException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The IF comment was not boolean!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your IF comment property." + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + _expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified ParameterBean]" + ln() + _pmb + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + _specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotBooleanResultException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected List<String> splitList(String str, String delimiter) {
        return DfStringUtil.splitList(str, delimiter);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }
}
