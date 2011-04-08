/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DateFromToOption;
import org.seasar.dbflute.cbean.coption.FromToOption;

/**
 * The bean for manual order.
 * @author jflute
 * @since 0.9.8.2 (2011/04/08 Friday)
 */
public class ManualOrderBean {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String THEME_KEY = "ManualOrder";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<CaseWhenElement> _caseWhenAcceptedList = new ArrayList<CaseWhenElement>();
    protected final List<CaseWhenElement> _caseWhenBoundList = new ArrayList<CaseWhenElement>();

    // ===================================================================================
    //                                                                      User Interface
    //                                                                      ==============
    /**
     * Add 'when' element for 'case' statement as Equal.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_Equal(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_EQUAL, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as NotEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_NotEqual(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_NOT_EQUAL_STANDARD, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as GreaterThan.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_GreaterThan(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_GREATER_THAN, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as LessThan.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_LessThan(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_LESS_THAN, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as GreaterEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_GreaterEqual(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_GREATER_EQUAL, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as LessEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     */
    public void when_LessEqual(Object orderValue) {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_LESS_EQUAL, orderValue));
    }

    /**
     * Add 'when' element for 'case' statement as IsNull.
     */
    public void when_IsNull() {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_IS_NULL, null));
    }

    /**
     * Add 'when' element for 'case' statement as IsNotNull.
     */
    public void when_IsNotNull() {
        _caseWhenAcceptedList.add(createElement(ConditionKey.CK_IS_NOT_NULL, null));
    }

    /**
     * Add 'when' element for 'case' statement as FromTo.
     * @param fromDate The from-date for ordering. (NullAllowed: if null, means invalid from-condition)
     * @param toDate The to-date for ordering. (NullAllowed: if null, means invalid to-condition)
     * @param option The option of from-to. (NotNull)
     */
    public void when_FromTo(Date fromDate, Date toDate, FromToOption option) {
        doWhen_FromTo(fromDate, toDate, option);
    }

    /**
     * Add 'when' element for 'case' statement as DateFromTo.
     * @param fromDate The from-date for ordering. (NullAllowed: if null, means invalid from-condition)
     * @param toDate The to-date for ordering. (NullAllowed: if null, means invalid to-condition)
     */
    public void when_DateFromTo(Date fromDate, Date toDate) {
        doWhen_FromTo(fromDate, toDate, new DateFromToOption());
    }

    protected void doWhen_FromTo(Date fromDate, Date toDate, FromToOption option) {
        if (option == null) {
            String msg = "The argument 'option' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final ConditionKey fromDateConditionKey = option.getFromDateConditionKey();
        _caseWhenAcceptedList.add(createElement(fromDateConditionKey, option.filterFromDate(fromDate)));
        final ConditionKey toDateConditionKey = option.getToDateConditionKey();
        _caseWhenAcceptedList.add(createElement(toDateConditionKey, option.filterToDate(toDate)));
    }

    public void acceptOrderValueList(List<? extends Object> orderValueList) {
        if (orderValueList == null) {
            String msg = "The argument 'orderValueList' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        for (Object orderValue : orderValueList) {
            when_Equal(orderValue);
        }
    }

    public boolean hasManualOrder() {
        return !_caseWhenAcceptedList.isEmpty();
    }

    // ===================================================================================
    //                                                                     Binding Process
    //                                                                     ===============
    public void bind(FreeParameterManualOrderThemeListHandler handler) {
        if (!hasManualOrder()) {
            return;
        }
        for (CaseWhenElement element : _caseWhenAcceptedList) {
            final String bindExp = handler.register(THEME_KEY, element.getOrderValue());
            _caseWhenBoundList.add(createElement(element.getConditionKey(), bindExp));
        }
    }

    public static interface FreeParameterManualOrderThemeListHandler {
        String register(String themeKey, Object orderValue);
    }

    // ===================================================================================
    //                                                                       Element Class
    //                                                                       =============
    protected CaseWhenElement createElement(ConditionKey conditionKey, Object orderValue) {
        return new CaseWhenElement(conditionKey, orderValue);
    }

    public static class CaseWhenElement {
        protected ConditionKey _conditionKey;
        protected Object _orderValue;

        public CaseWhenElement(ConditionKey conditionKey, Object orderValue) {
            _conditionKey = conditionKey;
            _orderValue = orderValue;
        }

        public ConditionKey getConditionKey() {
            return _conditionKey;
        }

        public Object getOrderValue() {
            return _orderValue;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<CaseWhenElement> getCaseWhenAcceptedList() {
        return _caseWhenAcceptedList;
    }

    public List<CaseWhenElement> getCaseWhenBoundList() {
        return _caseWhenBoundList;
    }
}
