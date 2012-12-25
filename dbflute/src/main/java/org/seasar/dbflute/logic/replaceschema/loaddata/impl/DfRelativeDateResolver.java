/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.exception.DfLoadDataRegistrationFailureException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.HandyDate;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfReflectionUtil.ReflectionFailureException;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/25 Tuesday)
 */
public class DfRelativeDateResolver {

    private static final String RESOLVED_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

    public String resolveRelativeDate(String relativeDate) {
        // $sysdate
        // $sysdate.addDay(1)
        // $sysdate.addDay(1).moveToDayJust()
        // $sysdate.addMonth(3).moveToMonthTerminal()
        final String currentMark = "$sysdate";
        if (!relativeDate.startsWith(currentMark)) {
            return relativeDate;
        }
        final Date currentDate = DBFluteSystem.currentDate();
        final String calcPart = Srl.substringFirstRear(relativeDate, currentMark).trim();
        if (calcPart.trim().length() > 0) {
            return DfTypeUtil.toString(currentDate, RESOLVED_PATTERN);
        }
        final List<String> methodList = Srl.splitListTrimmed(calcPart, ".");
        HandyDate handyDate = new HandyDate(currentDate);
        for (String methodCall : methodList) {
            final String methodName;
            final List<Object> argList = DfCollectionUtil.newArrayList();
            if (methodCall.contains("(") && methodCall.endsWith(")")) {
                methodName = Srl.substringFirstFront(methodCall, "(");
                final String methodArgsPart = Srl.substringFirstFront(Srl.substringFirstRear(methodCall, "("), ")");
                final List<String> argStrList = Srl.splitListTrimmed(methodArgsPart, ",");
                for (String arg : argStrList) {
                    if (isNumber(arg)) {
                        argList.add(DfTypeUtil.toInteger(arg)); // integer only supported (cannot use long)
                    } else {
                        argList.add(arg);
                    }
                }
            } else {
                methodName = methodCall;
            }
            final List<Class<?>> argTypeList = DfCollectionUtil.newArrayList();
            for (Object arg : argList) {
                argTypeList.add(arg.getClass());
            }
            final Class<?>[] argTypes = argTypeList.toArray(new Class<?>[argTypeList.size()]);
            try {
                final Method method = DfReflectionUtil.getPublicMethod(HandyDate.class, methodName, argTypes);
                handyDate = (HandyDate) DfReflectionUtil.invoke(method, handyDate, argList.toArray());
            } catch (ReflectionFailureException e) {
                throwLoadDataRelativeDateInvokeFailureException(relativeDate, methodName, e);
            }
        }
        return DfTypeUtil.toString(handyDate.getDate(), RESOLVED_PATTERN);
    }

    protected boolean isNumber(String str) { // except decimal
        final String minusRemovedStr = str.startsWith("-") ? Srl.substringFirstRear(str, "-") : str;
        return Srl.isNumberHarf(minusRemovedStr);
    }

    protected void throwLoadDataRelativeDateInvokeFailureException(String relativeDate, String methodName,
            ReflectionFailureException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to invoke the method.");
        br.addItem("Relative Date");
        br.addElement(relativeDate);
        br.addItem("Failed Method");
        br.addElement(methodName);
        br.addItem("Reflection Exception");
        br.addElement(e.getClass());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg);
    }
}
