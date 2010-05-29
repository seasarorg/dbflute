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

import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.exception.BindVariableCommentIllegalParameterBeanSpecificationException;
import org.seasar.dbflute.twowaysql.exception.BindVariableCommentParameterNullValueException;
import org.seasar.dbflute.twowaysql.exception.EmbeddedVariableCommentIllegalParameterBeanSpecificationException;
import org.seasar.dbflute.twowaysql.exception.EmbeddedVariableCommentParameterNullValueException;
import org.seasar.dbflute.twowaysql.exception.LoopCurrentVariableOutOfForCommentException;
import org.seasar.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class NodeUtil {

    public static boolean isCurrentVariableOutOfScope(String firstName, boolean inLoop) {
        return !inLoop && firstName.equals(ForNode.CURRENT_VARIABLE);
    }

    public static boolean isWrongParameterBeanName(String firstName, CommandContext ctx) {
        final Object firstArg = ctx.getArg(firstName);
        return isWrongParameterBeanName(firstName, firstArg);
    }

    public static boolean isWrongParameterBeanName(String firstName, Object firstArg) {
        return firstArg instanceof ParameterBean && !"pmb".equals(firstName);
    }

    public static void throwBindOrEmbeddedCommentParameterNullValueException(String expression, Class<?> targetType,
            String specifiedSql, boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The value of " + (bind ? "bind" : "embedded") + " variable was null!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Is it within the scope of your assumption?" + ln();
        msg = msg + "If the answer is YES, please confirm your application logic about the parameter." + ln();
        msg = msg + "If the answer is NO, please confirm the logic of parameter comment(especially IF comment)." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x) - XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "  (o) - /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Property Type]" + ln() + targetType + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableCommentParameterNullValueException(msg);
        } else {
            throw new EmbeddedVariableCommentParameterNullValueException(msg);
        }
    }

    public static void throwBindOrEmbeddedCommentIllegalParameterBeanSpecificationException(String expression,
            String specifiedSql, boolean bind) {
        String name = (bind ? "bind variable" : "embedded variable");
        String emmark = (bind ? "" : "$");
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The " + name + " comment had the illegal parameter-bean specification!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your comment." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x) - /*" + emmark + "pmb,memberId*/" + ln();
        msg = msg + "  (x) - /*" + emmark + "p mb.memberId*/" + ln();
        msg = msg + "  (x) - /*" + emmark + "pmb:memberId*/" + ln();
        msg = msg + "  (x) - /*" + emmark + "pnb.memberId*/" + ln();
        msg = msg + "  (o) - /*" + emmark + "pmb.memberId*/" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        // *debug to this exception does not need contents of the parameter-bean
        //  (and for security to application data)
        //msg = msg + "[ParameterBean]" + ln() + pmb + ln();
        //msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableCommentIllegalParameterBeanSpecificationException(msg);
        } else {
            throw new EmbeddedVariableCommentIllegalParameterBeanSpecificationException(msg);
        }
    }

    public static void throwBindOrEmbeddedCommentParameterEmptyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind" : "embedded") + " variable was empty!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(3);" + ln();
        msg = msg + "    xxxIdList.add(7);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    public static void throwBindOrEmbeddedCommentParameterNullOnlyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind" : "embedded") + " variable was null-only list'!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(null);" + ln();
        msg = msg + "    xxxIdList.add(null);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "    xxxIdList.add(3);" + ln();
        msg = msg + "    xxxIdList.add(7);" + ln();
        msg = msg + "    cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[Comment Expression]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    public static void throwLoopCurrentVariableOutOfForCommentException(String expression, String specifiedSql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Loop's current variable was out of FOR comment scope!");
        br.addItem("Advice");
        br.addElement("Loop's current variables are allowed in FOR comment scope.");
        br.addElement("For example:");
        br.addElement("  (x):");
        br.addElement("    /*#current*/");
        br.addElement("    /*FOR*/.../*END*/");
        br.addElement("  (o):");
        br.addElement("    /*FOR*/");
        br.addElement("    /*#current*/");
        br.addElement("    /*END*/");
        br.addItem("Comment Expression");
        br.addElement(expression);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        throw new LoopCurrentVariableOutOfForCommentException(msg);
    }

    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
