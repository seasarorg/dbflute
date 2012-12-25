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
import java.util.Arrays;
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

    public static final String CURRENT_MARK = "$sysdate";
    private static final String RESOLVED_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

    protected final String _tableName;
    protected final String _columnName;

    public DfRelativeDateResolver(String tableName, String columnName) {
        _tableName = tableName;
        _columnName = columnName;
    }

    public String resolveRelativeDate(String relativeDate) {
        // $sysdate
        // $sysdate.addDay(1)
        // $sysdate.addDay(1).moveToDayJust()
        // $sysdate.addMonth(3).moveToMonthTerminal()
        final String currentMark = CURRENT_MARK;
        if (!relativeDate.startsWith(currentMark)) { // already checked but just in case
            return relativeDate;
        }
        final Date currentDate = DBFluteSystem.currentDate();
        final String calcPart = Srl.substringFirstRear(relativeDate, currentMark).trim();
        if (calcPart.trim().length() == 0) {
            return DfTypeUtil.toString(currentDate, RESOLVED_PATTERN);
        }
        final List<String> methodList = Srl.splitListTrimmed(Srl.trim(calcPart, "."), ".");
        HandyDate handyDate = new HandyDate(currentDate);
        for (String methodCall : methodList) {
            handyDate = invokeMethod(relativeDate, handyDate, methodCall);
        }
        return DfTypeUtil.toString(handyDate.getDate(), RESOLVED_PATTERN);
    }

    protected HandyDate invokeMethod(String relativeDate, HandyDate handyDate, String methodCall) {
        if (!methodCall.contains("(") || !methodCall.endsWith(")")) {
            throwLoadDataRelativeDateMethodArgPartNotFoundException(relativeDate);
        }
        final String methodName = Srl.substringFirstFront(methodCall, "(");
        final String methodArgsPart = Srl.substringFirstFront(Srl.substringFirstRear(methodCall, "("), ")");
        final List<String> argElementList;
        if (Srl.is_NotNull_and_NotTrimmedEmpty(methodArgsPart)) {
            argElementList = Srl.splitListTrimmed(methodArgsPart, ",");
        } else {
            argElementList = DfCollectionUtil.emptyList();
        }
        final List<Object> argValueList = DfCollectionUtil.newArrayList();
        for (String arg : argElementList) {
            if (isNumber(arg)) {
                argValueList.add(DfTypeUtil.toInteger(arg)); // int only supported (cannot use long)
            } else {
                argValueList.add(arg);
            }
        }
        final List<Class<?>> argTypeList = DfCollectionUtil.newArrayList();
        for (Object argValue : argValueList) {
            final Class<? extends Object> argType = argValue.getClass();
            if (Integer.class.equals(argType)) {
                // even if the argument value is int type, getClass() returns Integer type
                argTypeList.add(int.class);
            } else {
                argTypeList.add(argType);
            }
        }
        final Class<?>[] argTypes = argTypeList.toArray(new Class<?>[argTypeList.size()]);
        final Class<HandyDate> handyDateType = HandyDate.class;
        final Method method = DfReflectionUtil.getPublicMethod(handyDateType, methodName, argTypes);
        if (method == null) {
            throwLoadDataRelativeDateMethodNotFoundException(relativeDate, handyDateType, methodName, argTypes);
        }
        try {
            handyDate = (HandyDate) DfReflectionUtil.invoke(method, handyDate, argValueList.toArray());
        } catch (ReflectionFailureException e) {
            throwLoadDataRelativeDateInvokeFailureException(relativeDate, handyDateType, methodName, e);
        }
        return handyDate;
    }

    protected boolean isNumber(String str) { // except decimal
        final String minusRemovedStr = str.startsWith("-") ? Srl.substringFirstRear(str, "-") : str;
        return Srl.isNumberHarf(minusRemovedStr);
    }

    protected void throwLoadDataRelativeDateMethodArgPartNotFoundException(String relativeDate) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the method argument part for RelativeDate on LoadData.");
        br.addItem("Advice");
        br.addElement("You should add '()' at method rear.");
        br.addElement("For example:");
        br.addElement("  (x): $sysdate.addDay");
        br.addElement("  (x): $sysdate.addDay.moveTo...");
        br.addElement("  (o): $sysdate.addDay(7)");
        br.addElement("  (o): $sysdate.addDay(7).moveTo...");
        br.addItem("Table Name");
        br.addElement(_tableName);
        br.addItem("Column Name");
        br.addElement(_columnName);
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg);
    }

    protected void throwLoadDataRelativeDateMethodNotFoundException(String relativeDate,
            Class<HandyDate> handyDateType, String methodName, Class<?>[] argTypes) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the method to invoke for RelativeDate on LoadData.");
        br.addItem("Table Name");
        br.addElement(_tableName);
        br.addItem("Column Name");
        br.addElement(_columnName);
        br.addItem("Relative Date");
        br.addElement(relativeDate);
        br.addItem("HandyDate Type");
        br.addElement(handyDateType);
        br.addItem("NotFound Method");
        br.addElement(methodName);
        br.addItem("Argument Type");
        br.addElement(Arrays.asList(argTypes));
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg);
    }

    protected void throwLoadDataRelativeDateInvokeFailureException(String relativeDate, Class<HandyDate> targetType,
            String methodName, ReflectionFailureException e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Failed to invoke the method for RelativeDate on LoadData.");
        br.addItem("Table Name");
        br.addElement(_tableName);
        br.addItem("Column Name");
        br.addElement(_columnName);
        br.addItem("Relative Date");
        br.addElement(relativeDate);
        br.addItem("HandyDate Type");
        br.addElement(targetType);
        br.addItem("Failed Method");
        br.addElement(methodName);
        br.addItem("Reflection Exception");
        br.addElement(e.getClass());
        br.addElement(e.getMessage());
        final String msg = br.buildExceptionMessage();
        throw new DfLoadDataRegistrationFailureException(msg);
    }
}
