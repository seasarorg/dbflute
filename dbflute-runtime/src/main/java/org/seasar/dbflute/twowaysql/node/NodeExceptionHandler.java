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

import org.seasar.dbflute.exception.BindVariableParameterNullValueException;
import org.seasar.dbflute.exception.EmbeddedValueParameterNullValueException;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class NodeExceptionHandler {

    public static void throwBindOrEmbeddedParameterNullValueException(String expression, Class<?> targetType,
            String specifiedSql, boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The value of " + (bind ? "bind variable" : "embedded value") + " was Null!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Is it within the scope of your assumption?" + ln();
        msg = msg + "If the answer is YES, please confirm your application logic about the parameter." + ln();
        msg = msg + "If the answer is NO, please confirm the logic of parameter comment(especially IF comment)." + ln();
        msg = msg + "  --> For example:" + ln();
        msg = msg + "        before (x) -- XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "        after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + ln() + expression
                + ln();
        msg = msg + ln();
        msg = msg + "[Parameter Property Type]" + ln() + targetType + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableParameterNullValueException(msg);
        } else {
            throw new EmbeddedValueParameterNullValueException(msg);
        }
    }

    public static void throwBindOrEmbeddedParameterEmptyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind variable" : "embedded value") + " was empty!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x):" + ln();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "    after  (o):" + ln();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "      xxxIdList.add(3);" + ln();
        msg = msg + "      xxxIdList.add(7);" + ln();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + ln() + expression
                + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalArgumentException(msg);
    }

    public static void throwBindOrEmbeddedParameterNullOnlyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The list of " + (bind ? "bind variable" : "embedded value") + " was 'Null Only List'!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your application logic." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x):" + ln();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "      xxxIdList.add(null);" + ln();
        msg = msg + "      xxxIdList.add(null);" + ln();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + "    after  (o):" + ln();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + ln();
        msg = msg + "      xxxIdList.add(3);" + ln();
        msg = msg + "      xxxIdList.add(7);" + ln();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);" + ln();
        msg = msg + ln();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + ln() + expression
                + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalArgumentException(msg);
    }

    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
