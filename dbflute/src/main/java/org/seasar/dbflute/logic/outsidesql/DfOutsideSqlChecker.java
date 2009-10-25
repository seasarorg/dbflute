package org.seasar.dbflute.logic.outsidesql;

import java.util.List;

import org.seasar.dbflute.exception.DfCustomizeEntityMarkInvalidException;
import org.seasar.dbflute.exception.DfParameterBeanMarkInvalidException;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.node.IfCommentEvaluator;
import org.seasar.dbflute.twowaysql.node.ParameterFinder;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfOutsideSqlChecker {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _suppressIfCommentExpressionCheck;

    // ===================================================================================
    //                                                                             Checker
    //                                                                             =======
    public void check(String fileName, String sql) {
        // check NG mark
        final List<String> splitList = splitList(sql, "\n");
        checkSql2EntityMark(splitList, fileName, sql);

        // check parameter comment easily
        final SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        final List<String> ifCommentList = analyzer.researchIfComment();
        analyzer.analyze(); // should throw an exception

        // check IF comment expression
        checkIfCommentExpression(ifCommentList, sql);
    }

    protected void checkSql2EntityMark(List<String> splitList, String fileName, String sql) {
        for (String line : splitList) {
            line = line.trim();
            if (!line.contains("--")) {
                continue;
            }
            if (line.contains("#df;entity#") || line.contains("#df:pmb#") || line.contains("#df:emtity#")) {
                throwCustomizeEntityMarkInvalidException(line, fileName, sql);
            } else if (line.contains("!df;pmb!") || line.contains("!df:entity!") || line.contains("!df:pnb!")) {
                throwParameterBeanMarkInvalidException(line, fileName, sql);
            }
        }
    }

    protected void checkIfCommentExpression(List<String> ifCommentList, String sql) {
        if (_suppressIfCommentExpressionCheck) {
            return;
        }
        for (String expr : ifCommentList) {
            final IfCommentEvaluator evaluator = new IfCommentEvaluator(new ParameterFinder() {
                public Object find(String name) {
                    return null;
                }
            }, expr, sql);
            evaluator.assertExpression();
        }
    }

    protected void throwCustomizeEntityMarkInvalidException(String line, String fileName, String sql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The customize entity mark was invalid!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your mark." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - -- #df;entity#  *NOT semicolon" + ln();
        msg = msg + "    (x) - -- #df:pmb#     *NOT parameter bean" + ln();
        msg = msg + "    (x) - -- #df;emtity#  *NOT emtity ('entity' is right)" + ln();
        msg = msg + "    (o) - -- #df:entity#" + ln();
        msg = msg + ln();
        msg = msg + "[Customize Entity Mark]" + ln() + line + ln();
        msg = msg + ln();
        msg = msg + "[File]" + ln() + fileName + ln();
        msg = msg + ln();
        msg = msg + "[SQL]" + ln() + sql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfCustomizeEntityMarkInvalidException(msg);
    }

    protected void throwParameterBeanMarkInvalidException(String line, String fileName, String sql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The parameter bean mark was invalid!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your mark." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - -- !df;pmb!     *NOT semicolun" + ln();
        msg = msg + "    (x) - -- !df:entity!  *NOT customize entity" + ln();
        msg = msg + "    (x) - -- !df;pnb!     *NOT pnb ('pmb' is right)" + ln();
        msg = msg + "    (o) - -- !df:pmb!" + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Bean Mark]" + ln() + line + ln();
        msg = msg + ln();
        msg = msg + "[File]" + ln() + fileName + ln();
        msg = msg + ln();
        msg = msg + "[SQL]" + ln() + sql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfParameterBeanMarkInvalidException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected List<String> splitList(String str, String delimiter) {
        return DfStringUtil.splitList(str, delimiter);
    }

    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void suppressIfCommentExpressionCheck() {
        _suppressIfCommentExpressionCheck = true;
    }
}
