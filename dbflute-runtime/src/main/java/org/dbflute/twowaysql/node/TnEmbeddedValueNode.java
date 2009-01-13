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
package org.dbflute.twowaysql.node;

import java.lang.reflect.Array;
import java.util.List;

import org.dbflute.twowaysql.context.TnCommandContext;
import org.dbflute.util.SimpleStringUtil;

/**
 * @author jflute
 */
public class TnEmbeddedValueNode extends TnAbstractNode {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected String _testValue;
    protected String[] _names;
    protected String _specifiedSql;
    protected boolean _blockNullParameter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnEmbeddedValueNode(String expression, String testValue, String specifiedSql, boolean blockNullParameter) {
        this._expression = expression;
        this._testValue = testValue;
        this._names = SimpleStringUtil.split(expression, ".");
        this._specifiedSql = specifiedSql;
        this._blockNullParameter = blockNullParameter;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void accept(TnCommandContext ctx) {
        final Object value = ctx.getArg(_names[0]);
        final Class<?> clazz = ctx.getArgType(_names[0]);
        final TnValueAndType valueAndType = new TnValueAndType();
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        setupValueAndType(valueAndType);

        if (_blockNullParameter && valueAndType.getTargetValue() == null) {
            throwBindOrEmbeddedParameterNullValueException(valueAndType);
        }
        if (!isInScope()) {
            // Main Root
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // [UnderReview]: Should I make an original exception instead of this exception?
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            if (valueAndType.getTargetValue() != null && valueAndType.getTargetValue().toString().indexOf("?") > -1) {
                String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                throw new IllegalStateException(msg);
            }
            ctx.addSql(valueAndType.getTargetValue().toString());
        } else {
            if (List.class.isAssignableFrom(valueAndType.getTargetType())) {
                embedArray(ctx, ((List<?>) valueAndType.getTargetValue()).toArray());
            } else if (valueAndType.getTargetType().isArray()) {
                embedArray(ctx, valueAndType.getTargetValue());
            } else {
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                // [UnderReview]: Should I make an original exception instead of this exception?
                // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                if (valueAndType.getTargetValue() != null && valueAndType.getTargetValue().toString().indexOf("?") > -1) {
                    String msg = "The value of expression for embedded comment should not contain a question mark '?':";
                    msg = msg + " value=" + valueAndType.getTargetValue() + " expression=" + _expression;
                    throw new IllegalStateException(msg);
                }
                ctx.addSql(valueAndType.getTargetValue().toString());
            }
        }
        if (valueAndType.isValidRearOption()) {
            ctx.addSql(valueAndType.buildRearOptionOnSql());
        }
    }

    protected void setupValueAndType(TnValueAndType valueAndType) {
        final TnValueAndTypeSetupper valueAndTypeSetuper = new TnValueAndTypeSetupper(_expression, _names,
                _specifiedSql, false);
        valueAndTypeSetuper.setupValueAndType(valueAndType);
    }

    protected void throwBindOrEmbeddedParameterNullValueException(TnValueAndType valueAndType) {
        TnNodeExceptionHandler.throwBindOrEmbeddedParameterNullValueException(_expression,
                valueAndType.getTargetType(), _specifiedSql, false);
    }

    protected boolean isInScope() {
        return _testValue != null && _testValue.startsWith("(") && _testValue.endsWith(")");
    }

    protected void embedArray(TnCommandContext ctx, Object array) {
        if (array == null) {
            return;
        }
        final int length = Array.getLength(array);
        if (length == 0) {
            throwBindOrEmbeddedParameterEmptyListException();
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
            throwBindOrEmbeddedParameterNullOnlyListException();
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

    protected void throwBindOrEmbeddedParameterEmptyListException() {
        TnNodeExceptionHandler.throwBindOrEmbeddedParameterEmptyListException(_expression, _specifiedSql, false);
    }

    protected void throwBindOrEmbeddedParameterNullOnlyListException() {
        TnNodeExceptionHandler.throwBindOrEmbeddedParameterNullOnlyListException(_expression, _specifiedSql, false);
    }
}
