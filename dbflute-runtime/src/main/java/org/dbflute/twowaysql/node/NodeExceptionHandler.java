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

import org.dbflute.exception.BindVariableParameterNullValueException;
import org.dbflute.exception.EmbeddedValueParameterNullValueException;
import org.dbflute.util.SimpleSystemUtil;

/**
 * @author jflute
 */
public class NodeExceptionHandler {

    public static void throwBindOrEmbeddedParameterNullValueException(String expression, Class<?> targetType,
            String specifiedSql, boolean bind) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The value of " + (bind ? "bind variable" : "embedded value") + " was Null!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Is it within the scope of your assumption?" + getLineSeparator();
        msg = msg + "If the answer is YES, please confirm your application logic about the parameter."
                + getLineSeparator();
        msg = msg + "If the answer is NO, please confirm the logic of parameter comment(especially IF comment)."
                + getLineSeparator();
        msg = msg + "  --> For example:" + getLineSeparator();
        msg = msg + "        before (x) -- XXX_ID = /*pmb.xxxId*/3" + getLineSeparator();
        msg = msg + "        after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + getLineSeparator()
                + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Parameter Property Type]" + getLineSeparator() + targetType + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableParameterNullValueException(msg);
        } else {
            throw new EmbeddedValueParameterNullValueException(msg);
        }
    }

    public static void throwBindOrEmbeddedParameterEmptyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The list of " + (bind ? "bind variable" : "embedded value") + " was empty!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your application logic." + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x):" + getLineSeparator();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + getLineSeparator();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);"
                + getLineSeparator();
        msg = msg + "    after  (o):" + getLineSeparator();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + getLineSeparator();
        msg = msg + "      xxxIdList.add(3);" + getLineSeparator();
        msg = msg + "      xxxIdList.add(7);" + getLineSeparator();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);"
                + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + getLineSeparator()
                + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalArgumentException(msg);
    }

    public static void throwBindOrEmbeddedParameterNullOnlyListException(String expression, String specifiedSql,
            boolean bind) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The list of " + (bind ? "bind variable" : "embedded value") + " was 'Null Only List'!"
                + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your application logic." + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x):" + getLineSeparator();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + getLineSeparator();
        msg = msg + "      xxxIdList.add(null);" + getLineSeparator();
        msg = msg + "      xxxIdList.add(null);" + getLineSeparator();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);"
                + getLineSeparator();
        msg = msg + "    after  (o):" + getLineSeparator();
        msg = msg + "      List<Integer> xxxIdList = new ArrayList<Integer>();" + getLineSeparator();
        msg = msg + "      xxxIdList.add(3);" + getLineSeparator();
        msg = msg + "      xxxIdList.add(7);" + getLineSeparator();
        msg = msg + "      cb.query().setXxxId_InScope(xxxIdList);// Or pmb.setXxxIdList(xxxIdList);"
                + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + getLineSeparator()
                + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalArgumentException(msg);
    }

    protected static String getLineSeparator() {
        return SimpleSystemUtil.getLineSeparator();
    }
}
