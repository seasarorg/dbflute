package org.dbflute.twowaysql;

import java.util.Stack;

import org.dbflute.exception.EndCommentNotFoundException;
import org.dbflute.exception.IfCommentConditionNotFoundException;
import org.dbflute.twowaysql.context.TnCommandContext;
import org.dbflute.twowaysql.context.TnCommandContextCreator;
import org.dbflute.twowaysql.node.AbstractNode;
import org.dbflute.twowaysql.node.TnBeginNode;
import org.dbflute.twowaysql.node.TnBindVariableNode;
import org.dbflute.twowaysql.node.TnContainerNode;
import org.dbflute.twowaysql.node.ElseNode;
import org.dbflute.twowaysql.node.TnEmbeddedValueNode;
import org.dbflute.twowaysql.node.IfNode;
import org.dbflute.twowaysql.node.Node;
import org.dbflute.twowaysql.node.TnPrefixSqlNode;
import org.dbflute.twowaysql.node.TnSqlNode;
import org.dbflute.util.SimpleStringUtil;
import org.dbflute.util.SimpleSystemUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class SqlParser {

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
    public SqlParser(String sql, boolean blockNullParameter) {
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
        push(new TnContainerNode());
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
            sql = MyStringUtil.replace(sql, "--", "");
        }
        Node node = peek();
        if ((node instanceof IfNode || node instanceof ElseNode) && node.getChildSize() == 0) {

            SqlTokenizer st = new SqlTokenizer(sql);
            st.skipWhitespace();
            String token = st.skipToken();
            st.skipWhitespace();
            if (sql.startsWith(",")) {
                if (sql.startsWith(", ")) {
                    node.addChild(new TnPrefixSqlNode(", ", sql.substring(2)));
                } else {
                    node.addChild(new TnPrefixSqlNode(",", sql.substring(1)));
                }
            } else if ("AND".equalsIgnoreCase(token) || "OR".equalsIgnoreCase(token)) {
                node.addChild(new TnPrefixSqlNode(st.getBefore(), st.getAfter()));
            } else {
                node.addChild(new TnSqlNode(sql));
            }
        } else {
            node.addChild(new TnSqlNode(sql));
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
            peek().addChild(new TnSqlNode(before.substring(before.lastIndexOf("/*"))));
        }
    }

    protected void parseIf() {
        final String condition = tokenizer.getToken().substring(2).trim();
        if (MyStringUtil.isEmpty(condition)) {
            throwIfCommentConditionNotFoundException();
        }
        final TnContainerNode ifNode = createIfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    protected void throwIfCommentConditionNotFoundException() {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The condition of IF comment was Not Found!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the IF comment expression." + getLineSeparator();
        msg = msg + "It may exist the IF comment that DOESN'T have a condition." + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x) -- /*IF*/XXX_ID = /*pmb.xxxId*/3/*END*/" + getLineSeparator();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[IF Comment Expression]" + getLineSeparator() + tokenizer.getToken() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IfCommentConditionNotFoundException(msg);
    }

    protected void parseBegin() {
        TnBeginNode beginNode = new TnBeginNode();
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
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The end comment was Not Found!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the parameter comment logic." + getLineSeparator();
        msg = msg + "It may exist the parameter comment that DOESN'T have an end comment." + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + getLineSeparator();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
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
        return new TnBindVariableNode(expr, testValue, specifiedSql, blockNullParameter);
    }

    protected AbstractNode createEmbeddedValueNode(String expr, String testValue) {// Extension!
        return new TnEmbeddedValueNode(expr, testValue, specifiedSql, blockNullParameter);
    }

    protected TnContainerNode createIfNode(String expr) { // Extension!
        return new IfNode(expr, specifiedSql);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return SimpleSystemUtil.getLineSeparator();
    }

    protected final String replaceString(String text, String fromText, String toText) {
        return SimpleStringUtil.replace(text, fromText, toText);
    }

    // -----------------------------------------------------
    //                                            StringUtil
    //                                            ----------
    protected static class MyStringUtil {

        public static final String[] EMPTY_STRINGS = new String[0];

        private MyStringUtil() {
        }

        public static final boolean isEmpty(String text) {
            return text == null || text.length() == 0;
        }

        public static final String replace(String text, String fromText, String toText) {
            return SimpleStringUtil.replace(text, fromText, toText);
        }

        public static String[] split(String str, String delimiter) {
            return SimpleStringUtil.split(str, delimiter);
        }
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
        final TnCommandContext context;
        {
            final SqlParser parser = new SqlParser(twoWaySql, false);
            final Node node = parser.parse();
            final TnCommandContextCreator creator = new TnCommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        final String preparedSql = context.getSql();
        return CompleteSqlBuilder.getCompleteSql(preparedSql, context.getBindVariables(), logDateFormat,
                logTimestampFormat);
    }
}
