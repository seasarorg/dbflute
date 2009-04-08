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
package org.seasar.dbflute.twowaysql;

import java.util.Stack;

import org.seasar.dbflute.exception.EndCommentNotFoundException;
import org.seasar.dbflute.exception.IfCommentConditionNotFoundException;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.AbstractNode;
import org.seasar.dbflute.twowaysql.node.BeginNode;
import org.seasar.dbflute.twowaysql.node.BindVariableNode;
import org.seasar.dbflute.twowaysql.node.ContainerNode;
import org.seasar.dbflute.twowaysql.node.ElseNode;
import org.seasar.dbflute.twowaysql.node.EmbeddedValueNode;
import org.seasar.dbflute.twowaysql.node.IfNode;
import org.seasar.dbflute.twowaysql.node.Node;
import org.seasar.dbflute.twowaysql.node.PrefixSqlNode;
import org.seasar.dbflute.twowaysql.node.SqlNode;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SqlAnalyzer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String specifiedSql;
    protected boolean blockNullParameter;
    protected SqlTokenizer tokenizer;
    protected Stack<Node> nodeStack = new Stack<Node>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlAnalyzer(String sql, boolean blockNullParameter) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        specifiedSql = sql;
        this.blockNullParameter = blockNullParameter;
        tokenizer = new SqlTokenizer(sql);
    }

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    public Node parse() {
        push(new ContainerNode());
        while (SqlTokenizer.EOF != tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    protected void parseToken() {
        switch (tokenizer.getTokenType()) {
        case SqlTokenizer.SQL:
            parseSql();
            break;
        case SqlTokenizer.COMMENT:
            parseComment();
            break;
        case SqlTokenizer.ELSE:
            parseElse();
            break;
        case SqlTokenizer.BIND_VARIABLE:
            parseBindVariable();
            break;
        }
    }

    protected void parseSql() {
        String sql = tokenizer.getToken();
        if (isElseMode()) {
            sql = replaceString(sql, "--", "");
        }
        Node node = peek();
        if ((node instanceof IfNode || node instanceof ElseNode) && node.getChildSize() == 0) {
            SqlTokenizer st = new SqlTokenizer(sql);
            st.skipWhitespace();
            String token = st.skipToken();
            st.skipWhitespace();
            if (sql.startsWith(",")) { // is prefix
                if (sql.startsWith(", ")) {
                    node.addChild(createPrefixSqlNode(", ", sql.substring(2)));
                } else {
                    node.addChild(createPrefixSqlNode(",", sql.substring(1)));
                }
            } else if ("AND".equalsIgnoreCase(token) || "OR".equalsIgnoreCase(token)) { // is prefix
                node.addChild(createPrefixSqlNode(st.getBefore(), st.getAfter()));
            } else { // is not prefix
                node.addChild(createSqlNodeIfElseChildNode(sql));
            }
        } else {
            node.addChild(createSqlNode(sql));
        }
    }

    protected void parseComment() {
        final String comment = tokenizer.getToken();
        if (isTargetComment(comment)) {
            if (isIfComment(comment)) {
                parseIf();
            } else if (isBeginComment(comment)) {
                parseBegin();
            } else if (isEndComment(comment)) {
                return;
            } else {
                parseCommentBindVariable();
            }
        } else if (comment != null && 0 < comment.length()) {
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // [UnderReview]: Should I resolve bind character on scope comment(normal comment)?
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            String before = tokenizer.getBefore();
            peek().addChild(createSqlNode(before.substring(before.lastIndexOf("/*"))));
        }
    }

    protected void parseIf() {
        final String condition = tokenizer.getToken().substring(2).trim();
        if (DfStringUtil.isEmpty(condition)) {
            throwIfCommentConditionNotFoundException();
        }
        final ContainerNode ifNode = createIfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    protected void throwIfCommentConditionNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The condition of IF comment was Not Found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the IF comment expression." + ln();
        msg = msg + "It may exist the IF comment that DOESN'T have a condition." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x) -- /*IF*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[IF Comment Expression]" + ln() + tokenizer.getToken() + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentConditionNotFoundException(msg);
    }

    protected void parseBegin() {
        BeginNode beginNode = new BeginNode();
        peek().addChild(beginNode);
        push(beginNode);
        parseEnd();
    }

    protected void parseEnd() {
        while (SqlTokenizer.EOF != tokenizer.next()) {
            if (tokenizer.getTokenType() == SqlTokenizer.COMMENT && isEndComment(tokenizer.getToken())) {
                pop();
                return;
            }
            parseToken();
        }
        throwEndCommentNotFoundException();
    }

    protected void throwEndCommentNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The end comment was Not Found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the parameter comment logic." + ln();
        msg = msg + "It may exist the parameter comment that DOESN'T have an end comment." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new EndCommentNotFoundException(msg);
    }

    protected void parseElse() {
        final Node parent = peek();
        if (!(parent instanceof IfNode)) {
            return;
        }
        final IfNode ifNode = (IfNode) pop();
        final ElseNode elseNode = new ElseNode();
        ifNode.setElseNode(elseNode);
        push(elseNode);
        tokenizer.skipWhitespace();
    }

    protected void parseCommentBindVariable() {
        final String expr = tokenizer.getToken();
        final String s = tokenizer.skipToken();
        if (expr.startsWith("$")) {
            peek().addChild(createEmbeddedValueNode(expr.substring(1), s));// Extension!
        } else {
            peek().addChild(createBindVariableNode(expr, s));// Extension!
        }
    }

    protected void parseBindVariable() {
        final String expr = tokenizer.getToken();
        peek().addChild(createBindVariableNode(expr, null));// Extension!
    }

    protected Node pop() {
        return (Node) nodeStack.pop();
    }

    protected Node peek() {
        return (Node) nodeStack.peek();
    }

    protected void push(Node node) {
        nodeStack.push(node);
    }

    protected boolean isElseMode() {
        for (int i = 0; i < nodeStack.size(); ++i) {
            if (nodeStack.get(i) instanceof ElseNode) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTargetComment(String comment) {
        return comment != null && comment.length() > 0 && Character.isJavaIdentifierStart(comment.charAt(0));
    }

    private static boolean isIfComment(String comment) {
        return comment.startsWith("IF");
    }

    private static boolean isBeginComment(String content) {
        return content != null && "BEGIN".equals(content);
    }

    private static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }

    protected AbstractNode createBindVariableNode(String expr, String testValue) {// Extension!
        return new BindVariableNode(expr, testValue, specifiedSql, blockNullParameter);
    }

    protected AbstractNode createEmbeddedValueNode(String expr, String testValue) {// Extension!
        return new EmbeddedValueNode(expr, testValue, specifiedSql, blockNullParameter);
    }

    protected ContainerNode createIfNode(String expr) { // Extension!
        return new IfNode(expr, specifiedSql);
    }

    protected SqlNode createSqlNode(String sql) {
        return new SqlNode(sql);
    }

    protected SqlNode createSqlNodeIfElseChildNode(String sql) {
        return new SqlNode(sql, true);
    }

    protected PrefixSqlNode createPrefixSqlNode(String prefix, String sql) {
        return new PrefixSqlNode(prefix, sql);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    protected final String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                             Convert
    //                                                                             =======
    public static String convertTwoWaySql2DisplaySql(String twoWaySql, Object arg, String logDateFormat,
            String logTimestampFormat) {
        final String[] argNames = new String[] { "dto" };
        final Class<?>[] argTypes = new Class<?>[] { arg.getClass() };
        final Object[] args = new Object[] { arg };
        return convertTwoWaySql2DisplaySql(twoWaySql, argNames, argTypes, args, logDateFormat, logTimestampFormat);
    }

    public static String convertTwoWaySql2DisplaySql(String twoWaySql, String[] argNames, Class<?>[] argTypes,
            Object[] args, String logDateFormat, String logTimestampFormat) {
        final CommandContext context;
        {
            final SqlAnalyzer parser = new SqlAnalyzer(twoWaySql, false);
            final Node node = parser.parse();
            final CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        final String preparedSql = context.getSql();
        return CompleteSqlBuilder.getCompleteSql(preparedSql, context.getBindVariables(), logDateFormat,
                logTimestampFormat);
    }
}
