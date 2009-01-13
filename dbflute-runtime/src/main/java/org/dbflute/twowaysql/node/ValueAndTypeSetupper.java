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

import java.lang.reflect.Method;
import java.util.Map;

import org.dbflute.cbean.MapParameterBean;
import org.dbflute.cbean.coption.LikeSearchOption;
import org.dbflute.exception.BindVariableCommentNotFoundPropertyException;
import org.dbflute.exception.EmbeddedValueCommentNotFoundPropertyException;
import org.dbflute.exception.RequiredOptionNotFoundException;
import org.dbflute.util.DfStringUtil;
import org.dbflute.util.DfSystemUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

/**
 * @author jflute
 */
public class ValueAndTypeSetupper {

    protected String _expression;
    protected String[] _names;
    protected String _specifiedSql;
    protected boolean _bind;

    public ValueAndTypeSetupper(String expression, String[] names, String specifiedSql, boolean bind) {
        this._expression = expression;
        this._names = names;
        this._specifiedSql = specifiedSql;
        this._bind = bind;
    }

    protected void setupValueAndType(ValueAndType valueAndType) {
        Object value = valueAndType.getTargetValue();
        Class<?> clazz = valueAndType.getTargetType();

        // LikeSearchOption handling here is for OutsideSql.
        LikeSearchOption likeSearchOption = null;
        String rearOption = null;

        for (int pos = 1; pos < _names.length; ++pos) {
            if (value == null) {
                break;
            }
            final String currentName = _names[pos];
            if (pos == 1) {// at the First Loop
                final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
                if (hasLikeSearchOption(beanDesc, currentName)) {
                    likeSearchOption = getLikeSearchOption(beanDesc, currentName, value);
                }
            }
            if (Map.class.isInstance(value)) {
                final Map<?, ?> map = (Map<?, ?>) value;
                value = map.get(_names[pos]);
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) {// at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : clazz);
                continue;
            }
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
            if (beanDesc.hasPropertyDesc(currentName)) {
                final PropertyDesc pd = beanDesc.getPropertyDesc(currentName);
                value = getPropertyValue(clazz, value, currentName, pd);
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) {// at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : pd.getPropertyType());
                continue;
            }
            final String methodName = "get" + initCap(currentName);
            if (beanDesc.hasMethod(methodName)) {// Is this unused???
                final Method method = beanDesc.getMethod(methodName);
                value = invokeGetter(method, value);
                clazz = method.getReturnType();
                continue;
            }
            if (pos == 1 && MapParameterBean.class.isAssignableFrom(clazz)) {
                final MapParameterBean pmb = (MapParameterBean) value;
                final Map<String, Object> map = pmb.getParameterMap();
                final Object elementValue = (map != null ? map.get(_names[pos]) : null);
                if (elementValue != null) {
                    value = elementValue;
                    clazz = value.getClass();
                    continue;
                }
            }
            throwBindOrEmbeddedCommentNotFoundPropertyException(_expression, clazz, currentName, _specifiedSql, _bind);
        }
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        valueAndType.setRearOption(rearOption);
    }

    // for OutsideSql
    protected boolean isLastLoop4LikeSearch(int pos, LikeSearchOption likeSearchOption) {
        return _names.length == (pos + 1) && likeSearchOption != null;
    }

    protected boolean isValidStringValue(Object value) {
        return value != null && value instanceof String && ((String) value).length() > 0;
    }

    // for OutsideSql
    protected boolean hasLikeSearchOption(BeanDesc beanDesc, String currentName) {
        return beanDesc.hasPropertyDesc(currentName + "InternalLikeSearchOption");
    }

    // for OutsideSql
    protected LikeSearchOption getLikeSearchOption(BeanDesc beanDesc, String currentName, Object resourceBean) {
        final PropertyDesc pb = beanDesc.getPropertyDesc(currentName + "InternalLikeSearchOption");
        final LikeSearchOption option = (LikeSearchOption) pb.getValue(resourceBean);
        if (option == null) {
            throwLikeSearchOptionNotFoundException(resourceBean, currentName);
        }
        if (option.isSplit()) {
            throwOutsideSqlLikeSearchOptionSplitUnsupportedException(option, resourceBean, currentName);
        }
        return option;
    }

    // for OutsideSql
    protected void throwLikeSearchOptionNotFoundException(Object resourceBean, String currentName) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The likeSearchOption was Not Found! (Should not be null!)" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your method call:" + getLineSeparator();
        final String beanName = resourceBean.getClass().getSimpleName();
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target ParameterBean]" + getLineSeparator() + resourceBean + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new RequiredOptionNotFoundException(msg);
    }

    // for OutsideSql
    protected void throwOutsideSqlLikeSearchOptionSplitUnsupportedException(LikeSearchOption option,
            Object resourceBean, String currentName) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The splitByXxx() of LikeSearchOption is unsupported at OutsideSql!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm your method call:" + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    before (x):" + getLineSeparator();
        final String beanName = resourceBean.getClass().getSimpleName();
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "      " + beanName + " pmb = new " + beanName + "();" + getLineSeparator();
        msg = msg + "      LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();"
                + getLineSeparator();
        msg = msg + "      likeSearchOption.splitBySpace(); // *No! Don't invoke this!" + getLineSeparator();
        msg = msg + "      pmb." + methodName + getLineSeparator();
        msg = msg + "    after  (o):" + getLineSeparator();
        msg = msg + "      " + beanName + " pmb = new " + beanName + "();" + getLineSeparator();
        msg = msg + "      LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();"
                + getLineSeparator();
        msg = msg + "      pmb." + methodName + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target LikeSearchOption]" + getLineSeparator() + option + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target ParameterBean]" + getLineSeparator() + resourceBean + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new UnsupportedOperationException(msg);
    }

    protected Object getPropertyValue(Class<?> beanType, Object beanValue, String currentName, PropertyDesc pd) {
        try {
            return pd.getValue(beanValue);
        } catch (RuntimeException e) {
            throwPropertyHandlingFailureException(beanType, beanValue, currentName, _expression, _specifiedSql, _bind,
                    e);
            return null;// Unreachable!
        }
    }

    protected void throwPropertyHandlingFailureException(Class<?> beanType, Object beanValue, String currentName,
            String expression, String specifiedSql, boolean bind, Exception e) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The handlig of the property was failed!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "This is the Framework Exception!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + getLineSeparator()
                + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Bean Type]" + getLineSeparator() + beanType + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Bean Value]" + getLineSeparator() + beanValue + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Property Name]" + getLineSeparator() + currentName + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg, e);
    }

    protected String initCap(String name) {
        return DfStringUtil.initCap(name);
    }

    protected Object invokeGetter(Method method, Object target) {
        try {
            return method.invoke(target, (Object[]) null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void throwBindOrEmbeddedCommentNotFoundPropertyException(String expression, Class<?> targetType,
            String notFoundProperty, String specifiedSql, boolean bind) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The property on the " + (bind ? "bind variable" : "embedded value") + " comment was Not Found!"
                + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "Please confirm the existence of your property on your arguments." + getLineSeparator();
        msg = msg + "Abd has the property had misspelling?" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + getLineSeparator()
                + expression + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[NotFound Property]" + getLineSeparator() + (targetType != null ? targetType.getName() + "#" : "")
                + notFoundProperty + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Specified SQL]" + getLineSeparator() + specifiedSql + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableCommentNotFoundPropertyException(msg);
        } else {
            throw new EmbeddedValueCommentNotFoundPropertyException(msg);
        }
    }

    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
