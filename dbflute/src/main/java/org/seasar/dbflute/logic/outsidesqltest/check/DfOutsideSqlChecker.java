package org.seasar.dbflute.logic.outsidesqltest.check;

import java.util.List;

import org.seasar.dbflute.exception.DfCustomizeEntityMarkInvalidException;
import org.seasar.dbflute.exception.DfParameterBeanMarkInvalidException;
import org.seasar.dbflute.exception.DfRequiredOutsideSqlDescriptionNotFoundException;
import org.seasar.dbflute.exception.DfRequiredOutsideSqlTitleNotFoundException;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSql2EntityMarkAnalyzer;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.node.IfCommentEvaluator;
import org.seasar.dbflute.twowaysql.node.ParameterFinder;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfOutsideSqlChecker {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _ifCommentExpressionCheck;
    protected boolean _requiredTitleCheck;
    protected boolean _requiredDescriptionCheck;

    // ===================================================================================
    //                                                                             Checker
    //                                                                             =======
    public void check(String fileName, String sql) {
        // check Sql2Entity mark
        final List<String> splitList = splitList(sql, "\n");
        checkSql2EntityMark(splitList, fileName, sql);

        // check parameter comment easily
        final SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);
        final List<String> ifCommentList = analyzer.researchIfComment();
        analyzer.analyze(); // should throw an exception

        // check IF comment expression (option)
        checkIfCommentExpression(ifCommentList, sql);

        // check title and description (option)
        checkRequiredTitle(fileName, sql);
        checkRequiredDescription(fileName, sql);
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
        if (!_ifCommentExpressionCheck) {
            return;
        }
        for (String expr : ifCommentList) {
            final IfCommentEvaluator evaluator = new IfCommentEvaluator(new ParameterFinder() {
                public Object find(String name) {
                    return null;
                }
            }, expr, sql, null);
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

    protected void checkRequiredTitle(String fileName, String sql) {
        if (!_requiredTitleCheck) {
            return;
        }
        final DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        final String title = analyzer.getTitle(sql);
        if (Srl.is_Null_or_TrimmedEmpty(title)) {
            throwRequiredOutsideSqlTitleNotFoundException(fileName, sql);
        }
    }

    protected void throwRequiredOutsideSqlTitleNotFoundException(String fileName, String sql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The outsideSql title was NOT found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "A outsideSql title is required at this project." + ln();
        msg = msg + "(The property 'isRequiredSqlTitle' of outsideSqlDefinition is true)" + ln();
        msg = msg + "  For example: @OutsideSql" + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    /*" + ln();
        msg = msg + "     [df:title]" + ln();
        msg = msg + "     Simple Member Select" + ln();
        msg = msg + "    */" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[File]" + ln() + fileName + ln();
        msg = msg + ln();
        msg = msg + "[SQL]" + ln() + sql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfRequiredOutsideSqlTitleNotFoundException(msg);
    }

    protected void checkRequiredDescription(String fileName, String sql) {
        if (!_requiredDescriptionCheck) {
            return;
        }
        final DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        final String description = analyzer.getDescription(sql);
        if (Srl.is_Null_or_TrimmedEmpty(description)) {
            throwRequiredOutsideSqlDescriptionNotFoundException(fileName, sql);
        }
    }

    protected void throwRequiredOutsideSqlDescriptionNotFoundException(String fileName, String sql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The outsideSql description was NOT found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "A outsideSql description is required at this project." + ln();
        msg = msg + "(The property 'isRequiredSqlDescription' of outsideSqlDefinition is true)" + ln();
        msg = msg + "  For example: @OutsideSql" + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - " + ln();
        msg = msg + "    /*" + ln();
        msg = msg + "     [df:description]" + ln();
        msg = msg + "     This SQL is ..." + ln();
        msg = msg + "     It uses 'union all' for performance ..." + ln();
        msg = msg + "    */" + ln();
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[File]" + ln() + fileName + ln();
        msg = msg + ln();
        msg = msg + "[SQL]" + ln() + sql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfRequiredOutsideSqlDescriptionNotFoundException(msg);
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
    public void enableIfCommentExpressionCheck() {
        _ifCommentExpressionCheck = true;
    }

    public void enableRequiredTitleCheck() {
        _requiredTitleCheck = true;
    }

    public void enableRequiredDescriptionCheck() {
        _requiredDescriptionCheck = true;
    }
}
