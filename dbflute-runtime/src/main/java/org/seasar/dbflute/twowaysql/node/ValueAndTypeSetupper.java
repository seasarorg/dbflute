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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.EmbeddedValueCommentNotFoundPropertyException;
import org.seasar.dbflute.exception.IllegalOutsideSqlOperationException;
import org.seasar.dbflute.exception.RequiredOptionNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.twowaysql.pmbean.MapParameterBean;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ValueAndTypeSetupper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String LIKE_SEARCH_OPTION_SUFFIX = "InternalLikeSearchOption";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected List<String> _nameList;
    protected String _specifiedSql;
    protected boolean _bind;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ValueAndTypeSetupper(String expression, List<String> nameList, String specifiedSql, boolean bind) {
        this._expression = expression;
        this._nameList = nameList;
        this._specifiedSql = specifiedSql;
        this._bind = bind;
    }

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupValueAndType(ValueAndType valueAndType) {
        Object value = valueAndType.getTargetValue();
        Class<?> clazz = valueAndType.getTargetType();

        // LikeSearchOption handling here is for OutsideSql.
        LikeSearchOption likeSearchOption = null;
        String rearOption = null;

        for (int pos = 1; pos < _nameList.size(); ++pos) {
            if (value == null) {
                break;
            }
            final String currentName = _nameList.get(pos);
            if (pos == 1) { // at the First Loop
                final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
                if (hasLikeSearchOption(beanDesc, currentName)) {
                    likeSearchOption = getLikeSearchOption(beanDesc, currentName, value);
                }
            }
            if (Map.class.isInstance(value)) {
                final Map<?, ?> map = (Map<?, ?>) value;
                value = map.get(_nameList.get(pos));
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) { // at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : clazz);
                continue;
            }
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
            if (beanDesc.hasPropertyDesc(currentName)) {
                final DfPropertyDesc pd = beanDesc.getPropertyDesc(currentName);
                value = getPropertyValue(clazz, value, currentName, pd);
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) { // at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : pd.getPropertyType());
                continue;
            }
            final String methodName = "get" + initCap(currentName);
            if (beanDesc.hasMethod(methodName)) { // basically unused because of using propertyDesc before
                final Method method = beanDesc.getMethod(methodName);
                value = invokeGetter(method, value);
                clazz = method.getReturnType();
                continue;
            }
            if (pos == 1 && MapParameterBean.class.isAssignableFrom(clazz)) {
                final MapParameterBean pmb = (MapParameterBean) value;
                final Map<String, Object> map = pmb.getParameterMap();
                final Object elementValue = (map != null ? map.get(_nameList.get(pos)) : null);
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
        return _nameList.size() == (pos + 1) && likeSearchOption != null;
    }

    protected boolean isValidStringValue(Object value) {
        return value != null && value instanceof String && ((String) value).length() > 0;
    }

    // for OutsideSql
    protected boolean hasLikeSearchOption(DfBeanDesc beanDesc, String currentName) {
        return beanDesc.hasPropertyDesc(currentName + LIKE_SEARCH_OPTION_SUFFIX);
    }

    // for OutsideSql
    protected LikeSearchOption getLikeSearchOption(DfBeanDesc beanDesc, String currentName, Object pmb) {
        final DfPropertyDesc pb = beanDesc.getPropertyDesc(currentName + LIKE_SEARCH_OPTION_SUFFIX);
        final LikeSearchOption option = (LikeSearchOption) pb.getValue(pmb);
        if (option == null) { // basically no way because of check in parameter-bean
            throwLikeSearchOptionNotFoundException(pmb, currentName);
        }
        if (option.isSplit()) { // basically no way because of check in parameter-bean
            throwOutsideSqlLikeSearchOptionSplitUnavailableException(option, pmb, currentName);
        }
        return option;
    }

    // for OutsideSql
    protected void throwLikeSearchOptionNotFoundException(Object pmb, String currentName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The likeSearchOption was not found! (Should not be null!)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        final String beanName = DfTypeUtil.toClassTitle(pmb);
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + ln();
        // *because basically it does not come here by checking in parameter-bean
        //  (and for security to application data)
        //msg = msg + ln();
        //msg = msg + "[ParameterBean]" + ln() + pmb + ln();
        msg = msg + "* * * * * * * * * */";
        throw new RequiredOptionNotFoundException(msg);
    }

    // for OutsideSql
    protected void throwOutsideSqlLikeSearchOptionSplitUnavailableException(LikeSearchOption option, Object pmb,
            String currentName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The splitByXxx() of LikeSearchOption is unavailable at OutsideSql!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        msg = msg + "For example:" + ln();
        msg = msg + "  (x):" + ln();
        final String beanName = DfTypeUtil.toClassTitle(pmb.getClass().getName());
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "    " + beanName + " pmb = new " + beanName + "();" + ln();
        msg = msg + "    LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();" + ln();
        msg = msg + "    likeSearchOption.splitBySpace(); // *No! Don't invoke this!" + ln();
        msg = msg + "    pmb." + methodName + ln();
        msg = msg + "  (o):" + ln();
        msg = msg + "    " + beanName + " pmb = new " + beanName + "();" + ln();
        msg = msg + "    LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();" + ln();
        msg = msg + "    pmb." + methodName + ln();
        msg = msg + ln();
        msg = msg + "[LikeSearchOption]" + ln() + option + ln();
        // *because basically it does not come here by checking in parameter-bean
        //  (and for security to application data)
        //msg = msg + ln();
        //msg = msg + "[ParameterBean]" + ln() + pmb + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected Object getPropertyValue(Class<?> beanType, Object beanValue, String currentName, DfPropertyDesc pd) {
        return pd.getValue(beanValue);
    }

    protected Object invokeGetter(Method method, Object target) {
        return DfReflectionUtil.invoke(method, target, null);
    }

    protected void throwBindOrEmbeddedCommentNotFoundPropertyException(String expression, Class<?> targetType,
            String notFoundProperty, String specifiedSql, boolean bind) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The property on the " + (bind ? "bind variable" : "embedded value") + " comment was not found!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of your property on your arguments.");
        br.addElement("And has the property had misspelling?");
        br.addItem((bind ? "Bind Variable" : "Embedded Value") + " Comment");
        br.addElement(expression);
        br.addItem("NotFound Property");
        br.addElement((targetType != null ? targetType.getName() + "#" : "") + notFoundProperty);
        br.addItem("Specified SQL");
        br.addElement(specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (bind) {
            throw new BindVariableCommentNotFoundPropertyException(msg);
        } else {
            throw new EmbeddedValueCommentNotFoundPropertyException(msg);
        }
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String name) {
        return Srl.initCap(name);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
