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

import org.seasar.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.dbflute.exception.IfCommentWrongExpressionException;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.util.DfOgnlUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class IfNode extends ContainerNode {
    
    private String _expression;
    private Object _parsedExpression;
    private ElseNode _elseNode;
    private String _specifiedSql;

    public IfNode(String expression, String specifiedSql) {
        this._expression = expression;
        this._parsedExpression = DfOgnlUtil.parseExpression(expression);
        this._specifiedSql = specifiedSql;
    }

    public String getExpression() {
        return _expression;
    }

    public ElseNode getElseNode() {
        return _elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this._elseNode = elseNode;
    }

    public void accept(CommandContext ctx) {
        Object result = null;
        try {
            result = DfOgnlUtil.getValue(_parsedExpression, ctx);
        } catch (RuntimeException e) {
            if (!_expression.contains("pmb.")) {
                throwIfCommentWrongExpressionException(_expression, e, _specifiedSql);
            }
            final String replaced = replace(_expression, "pmb.", "pmb.parameterMap.");
            final Object secondParsedExpression = DfOgnlUtil.parseExpression(replaced);
            try {
                result = DfOgnlUtil.getValue(secondParsedExpression, ctx);
            } catch (RuntimeException ignored) {
                throwIfCommentWrongExpressionException(_expression, e, _specifiedSql);
            }
            if (result == null) {
                throwIfCommentWrongExpressionException(_expression, e, _specifiedSql);
            }
            _parsedExpression = secondParsedExpression;
        }
        if (result != null && result instanceof Boolean) {
            if (((Boolean) result).booleanValue()) {
                super.accept(ctx);
                ctx.setEnabled(true);
            } else if (_elseNode != null) {
                _elseNode.accept(ctx);
                ctx.setEnabled(true);
            }
        } else {
            throwIfCommentNotBooleanResultException(_expression, result, _specifiedSql);
        }
    }

    protected void throwIfCommentWrongExpressionException(String expression, RuntimeException cause, String specifiedSql) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The IF comment of your specified SQL was Wrong!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the existence of your property on your arguments." + getLineSeparator();
        msg = msg + "And confirm the IF comment of your specified SQL." + getLineSeparator();
        msg = msg + "  For example, correct IF comment is as below:" + getLineSeparator();
        msg = msg + "    /*IF pmb.xxxId != null*/XXX_ID = .../*END*/" + getLineSeparator();
        msg = msg + "    /*IF pmb.isPaging()*/.../*END*/" + getLineSeparator();
        msg = msg + "    /*IF pmb.xxxId == null && pmb.xxxName != null*/.../*END*/" + getLineSeparator();
        msg = msg + "    /*IF pmb.xxxId == null || pmb.xxxName != null*/.../*END*/" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[IF Comment Expression]" + getLineSeparator() + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Cause Message]" + getLineSeparator();
        msg = msg + cause.getClass() + ":" + getLineSeparator();
        msg = msg + "  --> " + cause.getMessage() + getLineSeparator();
        final Throwable nestedCause = cause.getCause();
        if (nestedCause != null) {
            msg = msg + nestedCause.getClass() + ":" + getLineSeparator();
            msg = msg + "  --> " + nestedCause.getMessage() + getLineSeparator();
        }
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentWrongExpressionException(msg, cause);
    }

    protected void throwIfCommentNotBooleanResultException(String expression, Object result, String specifiedSql) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The boolean expression on IF comment of your specified SQL was Wrong!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the grammar of your IF comment. Does it really express boolean?" + getLineSeparator();
        msg = msg + "And confirm the existence of your property on your arguments if you use parameterMap." + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[IF Comment Expression]" + getLineSeparator() + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[IF Comment Result Value]" + getLineSeparator() + result + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentNotBooleanResultException(msg);
    }
    
    protected String replace(String text, String fromText,  String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
    
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
