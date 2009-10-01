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

import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class DeterminationParser {

    private static final String AND = " && ";
    private static final String OR = " || ";
    private static final String EQUAL = " == ";
    private static final String NOT_EQUAL = " != ";
    private static final String GREATER_THAN = " > ";
    private static final String LESS_THAN = " < ";
    private static final String GREATER_EQUAL = " >= ";
    private static final String LESS_EQUAL = " <= ";
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
    public DeterminationParser(Object pmb, String expression, String specifiedSql) {
        this._pmb = pmb;
        this._expression = expression;
        this._specifiedSql = specifiedSql;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public boolean parse() {
        if (_expression == null || _expression.trim().length() == 0) {
            // ex
        }
        _expression = _expression.trim();
        if (_expression.contains("(") || _expression.contains(")")) {
            // ex
        }
        if (_expression.contains(AND) && _expression.contains(OR)) {
            // ex
            throw new IllegalStateException("TODO");
        }
        if (_expression.contains(AND)) {
            final List<String> splitList = splitList(_expression, AND);
            for (String booleanClause : splitList) {
                final boolean result = parseBooleanClause(booleanClause);
                if (!result) {
                    return false;
                }
            }
            return true;
        } else if (_expression.contains(OR)) {
            final List<String> splitList = splitList(_expression, OR);
            for (String booleanClause : splitList) {
                final boolean result = parseBooleanClause(booleanClause);
                if (result) {
                    return true;
                }
            }
            return false;
        } else {
            return parseBooleanClause(_expression);
        }
    }

    protected boolean parseBooleanClause(String booleanClause) {
        if (booleanClause.contains(EQUAL)) {
            return evaluate(booleanClause, EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    return leftResult != null ? leftResult.equals(rightResult) : rightResult == null;
                }
            });
        } else if (booleanClause.contains(NOT_EQUAL)) {
            return evaluate(booleanClause, NOT_EQUAL, new OperandEvaluator() {
                public boolean evaluate(Object leftResult, Object rightResult) {
                    return leftResult != null ? !leftResult.equals(rightResult) : rightResult != null;
                }
            });
        } else {
            return parseStandAloneValue(booleanClause);
        }
    }

    protected boolean evaluate(String booleanClause, String operand, OperandEvaluator evaluator) {
        final String left = booleanClause.substring(0, booleanClause.indexOf(operand)).trim();
        final String right = booleanClause.substring(booleanClause.indexOf(operand) + operand.length()).trim();
        final Object leftResult = parseCompareValue(left);
        final Object rightResult = parseCompareValue(right);
        return evaluator.evaluate(leftResult, rightResult);
    }

    protected static interface OperandEvaluator {
        boolean evaluate(Object leftResult, Object rightRight);
    }

    protected Object parseCompareValue(String piece) {
        piece = piece.trim();
        if (piece.startsWith("'") && piece.endsWith("'")) {
            return piece.substring("'".length(), piece.length() - "'".length());
        }
        if ("null".equalsIgnoreCase(piece)) {
            return null;
        }
        if ("true".equalsIgnoreCase(piece)) {
            return true;
        }
        if ("false".equalsIgnoreCase(piece)) {
            return false;
        }
        if (piece.startsWith(BOOLEAN_NOT)) {
            // ng
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
        int index = 0;
        for (String property : propertyList) {
            if (baseObject == null) {
                return null;
            }
            final String methodName;
            if (property.endsWith(METHOD_SUFFIX)) {
                methodName = property;
            } else {
                methodName = "get" + DfStringUtil.initCap(property);
            }
            final Method method = DfReflectionUtil.getMethod(baseObject.getClass(), methodName, (Class[]) null);
            baseObject = DfReflectionUtil.invoke(method, baseObject, (Object[]) null);
            ++index;
        }
        return baseObject;
    }

    protected boolean parseStandAloneValue(String piece) {
        if (piece.startsWith("'") && piece.endsWith("'")) {
            // ng
        }
        if ("null".equalsIgnoreCase(piece)) {
            // ng
        }
        if ("true".equalsIgnoreCase(piece)) {
            return true;
        }
        if ("false".equalsIgnoreCase(piece)) {
            return false;
        }
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
        int index = 0;
        for (String property : propertyList) {
            if (baseObject == null) {
                // ng
            }
            final String methodName;
            if (property.endsWith(METHOD_SUFFIX)) {
                methodName = property;
            } else {
                final boolean lastLoop = (index == (propertyList.size() - 1));
                methodName = (lastLoop ? "is" : "get") + DfStringUtil.initCap(property);
            }
            final Method method = DfReflectionUtil.getMethod(baseObject.getClass(), methodName, (Class[]) null);
            baseObject = DfReflectionUtil.invoke(method, baseObject, (Object[]) null);
            ++index;
        }
        if (baseObject == null) {
            // ng
        }
        final boolean result = Boolean.valueOf(baseObject.toString());
        return not ? !result : result;
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
