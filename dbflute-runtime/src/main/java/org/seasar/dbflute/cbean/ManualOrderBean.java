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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.seasar.dbflute.cbean.chelper.HpCalcSpecification;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.chelper.HpSpecifiedColumn;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ColumnConversionOption;
import org.seasar.dbflute.cbean.coption.DateFromToOption;
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ClassificationCodeType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The bean for manual order.
 * @author jflute
 * @since 0.9.8.2 (2011/04/08 Friday)
 */
public class ManualOrderBean implements HpCalculator {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String THEME_KEY = "ManualOrder";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<CaseWhenElement> _caseWhenAcceptedList = new ArrayList<CaseWhenElement>();
    protected final List<CaseWhenElement> _caseWhenBoundList = new ArrayList<CaseWhenElement>();
    protected HpCalcSpecification<ConditionBean> _calcSpecification;
    protected ConnectionMode _connectionMode; // null means no connection

    // ===================================================================================
    //                                                                           Case When
    //                                                                           =========
    // -----------------------------------------------------
    //                                              when_...
    //                                              --------
    /**
     * Add 'when' element for 'case' statement as Equal.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_Equal(Object orderValue) {
        return doWhen(ConditionKey.CK_EQUAL, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as NotEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_NotEqual(Object orderValue) {
        return doWhen(ConditionKey.CK_NOT_EQUAL_STANDARD, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as GreaterThan.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_GreaterThan(Object orderValue) {
        return doWhen(ConditionKey.CK_GREATER_THAN, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as LessThan.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_LessThan(Object orderValue) {
        return doWhen(ConditionKey.CK_LESS_THAN, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as GreaterEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_GreaterEqual(Object orderValue) {
        return doWhen(ConditionKey.CK_GREATER_EQUAL, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as LessEqual.
     * @param orderValue The value for ordering. (NullAllowed: if null, means invalid condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_LessEqual(Object orderValue) {
        return doWhen(ConditionKey.CK_LESS_EQUAL, orderValue);
    }

    /**
     * Add 'when' element for 'case' statement as IsNull.
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_IsNull() {
        return doWhen(ConditionKey.CK_IS_NULL, null);
    }

    /**
     * Add 'when' element for 'case' statement as IsNotNull.
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_IsNotNull() {
        return doWhen(ConditionKey.CK_IS_NOT_NULL, null);
    }

    /**
     * Add 'when' element for 'case' statement as FromTo.
     * @param fromDate The from-date for ordering. (NullAllowed: if null, means invalid from-condition)
     * @param toDate The to-date for ordering. (NullAllowed: if null, means invalid to-condition)
     * @param option The option of from-to. (NotNull)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_FromTo(Date fromDate, Date toDate, FromToOption option) {
        return doWhen_FromTo(fromDate, toDate, option);
    }

    /**
     * Add 'when' element for 'case' statement as DateFromTo.
     * @param fromDate The from-date for ordering. (NullAllowed: if null, means invalid from-condition)
     * @param toDate The to-date for ordering. (NullAllowed: if null, means invalid to-condition)
     * @return The bean for connected order, which you can set second or more conditions by. (NotNull)
     */
    public ConnectedOrderBean when_DateFromTo(Date fromDate, Date toDate) {
        return doWhen_FromTo(fromDate, toDate, new DateFromToOption());
    }

    // -----------------------------------------------------
    //                                           Order Value
    //                                           -----------
    public void acceptOrderValueList(List<? extends Object> orderValueList) {
        if (orderValueList == null) {
            String msg = "The argument 'orderValueList' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        for (Object orderValue : orderValueList) {
            when_Equal(orderValue);
        }
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected ConnectedOrderBean doWhen_FromTo(Date fromDate, Date toDate, FromToOption option) {
        if (option == null) {
            String msg = "The argument 'option' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final ConditionKey fromDateConditionKey = option.getFromDateConditionKey();
        final ConditionKey toDateConditionKey = option.getToDateConditionKey();
        final Date filteredFromDate = option.filterFromDate(fromDate);
        final Date filteredToDate = option.filterToDate(toDate);
        return doWhen(fromDateConditionKey, filteredFromDate).doAnd(toDateConditionKey, filteredToDate);
    }

    protected ConnectedOrderBean doWhen(ConditionKey conditionKey, Object orderValue) {
        final ConnectedOrderBean resultBean = new ConnectedOrderBean(this);
        if (orderValue == null && !isManualOrderConditionKeyNullHandling(conditionKey)) {
            String msg = "The argument 'orderValue' should not be null: conditionKey=" + conditionKey;
            throw new IllegalArgumentException(msg);
        }
        final CaseWhenElement addedElement = createElement(conditionKey, orderValue);
        if (_connectionMode != null) {
            if (_caseWhenAcceptedList.isEmpty()) {
                String msg = "The connected mode should exist only when previous conditions exist:";
                msg = msg + " conditionKey=" + conditionKey + " orderValue=" + orderValue;
                throw new IllegalStateException(msg);
            }
            addedElement.setConnectionMode(_connectionMode);
            final CaseWhenElement lastElement = _caseWhenAcceptedList.get(_caseWhenAcceptedList.size() - 1);
            final List<CaseWhenElement> connectedElementList = lastElement.getConnectedElementList();
            if (!connectedElementList.isEmpty()) { // check same connectors
                final CaseWhenElement previousConnected = connectedElementList.get(connectedElementList.size() - 1);
                final ConnectionMode previousMode = previousConnected.getConnectionMode();
                if (previousMode != null && !previousMode.equals(addedElement.getConnectionMode())) {
                    throwManualOrderTwoConnectorUnsupportedException(conditionKey, orderValue, lastElement);
                }
            }
            lastElement.addConnectedElement(addedElement);
        } else {
            _caseWhenAcceptedList.add(addedElement);
        }
        return resultBean;
    }

    protected boolean isManualOrderConditionKeyNullHandling(ConditionKey conditionKey) {
        return conditionKey.equals(ConditionKey.CK_IS_NULL) || conditionKey.equals(ConditionKey.CK_IS_NOT_NULL);
    }

    protected void throwManualOrderTwoConnectorUnsupportedException(ConditionKey conditionKey, Object orderValue,
            CaseWhenElement lastElement) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("both two connectors and/or were set.");
        br.addItem("Advice");
        br.addElement("Unsupported using both two connectors and/or in one case.");
        br.addElement("For example:");
        br.addElement("  (o): when FOO > 1 and FOO < 9 then ...");
        br.addElement("  (o): when FOO >= 1 or FOO >= 9 then ...");
        br.addElement("  (x): when FOO >= 1 and FOO >= 9 or FOO = 20 then ...");
        br.addItem("Added ConditionKey");
        br.addElement(conditionKey);
        br.addItem("Added OrderValue");
        br.addElement(orderValue);
        br.addItem("Fixed ConnectionMode");
        br.addElement(lastElement.getConnectionMode());
        final String msg = br.buildExceptionMessage();
        throw new IllegalConditionBeanOperationException(msg);
    }

    // ===================================================================================
    //                                                                         Calculation
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public HpCalculator plus(Number plusValue) {
        assertObjectNotNull("plusValue", plusValue);
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.plus(plusValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator plus(HpSpecifiedColumn plusColumn) {
        assertObjectNotNull("plusColumn", plusColumn);
        assertCalculationColumnNumber(plusColumn);
        assertSpecifiedDreamCruiseTicket(plusColumn);
        initializeCalcSpecificationIfNeeds();
        setupSelectDreamCruiseJourneyLogBookIfUnionExists(plusColumn);
        return _calcSpecification.plus(plusColumn);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator minus(Number minusValue) {
        assertObjectNotNull("minusValue", minusValue);
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.minus(minusValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator minus(HpSpecifiedColumn minusColumn) {
        assertObjectNotNull("minusColumn", minusColumn);
        assertCalculationColumnNumber(minusColumn);
        assertSpecifiedDreamCruiseTicket(minusColumn);
        initializeCalcSpecificationIfNeeds();
        setupSelectDreamCruiseJourneyLogBookIfUnionExists(minusColumn);
        return _calcSpecification.minus(minusColumn);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator multiply(Number multiplyValue) {
        assertObjectNotNull("multiplyValue", multiplyValue);
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.multiply(multiplyValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator multiply(HpSpecifiedColumn multiplyColumn) {
        assertObjectNotNull("multiplyColumn", multiplyColumn);
        assertCalculationColumnNumber(multiplyColumn);
        assertSpecifiedDreamCruiseTicket(multiplyColumn);
        initializeCalcSpecificationIfNeeds();
        setupSelectDreamCruiseJourneyLogBookIfUnionExists(multiplyColumn);
        return _calcSpecification.multiply(multiplyColumn);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator divide(Number divideValue) {
        assertObjectNotNull("divideValue", divideValue);
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.divide(divideValue);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator divide(HpSpecifiedColumn divideColumn) {
        assertObjectNotNull("divideColumn", divideColumn);
        assertCalculationColumnNumber(divideColumn);
        assertSpecifiedDreamCruiseTicket(divideColumn);
        initializeCalcSpecificationIfNeeds();
        setupSelectDreamCruiseJourneyLogBookIfUnionExists(divideColumn);
        return _calcSpecification.divide(divideColumn);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator convert(ColumnConversionOption option) {
        assertObjectNotNull("option", option);
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.convert(option);
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator left() {
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.left();
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator right() {
        initializeCalcSpecificationIfNeeds();
        return _calcSpecification.right();
    }

    protected void initializeCalcSpecificationIfNeeds() {
        if (_calcSpecification == null) {
            _calcSpecification = createCalcSpecification();
        }
    }

    protected void setupSelectDreamCruiseJourneyLogBookIfUnionExists(HpSpecifiedColumn column) {
        column.setupSelectDreamCruiseJourneyLogBookIfUnionExists();
    }

    protected HpCalcSpecification<ConditionBean> createCalcSpecification() {
        return new HpCalcSpecification<ConditionBean>(createEmptySpecifyQuery());
    }

    protected SpecifyQuery<ConditionBean> createEmptySpecifyQuery() {
        return new SpecifyQuery<ConditionBean>() {
            public void specify(ConditionBean cb) {
            }
        };
    }

    public boolean hasCalculationOrder() {
        return _calcSpecification != null;
    }

    public HpCalcSpecification<ConditionBean> getCalculationOrder() {
        return _calcSpecification;
    }

    public void xinitCalculationOrder(ConditionBean baseCB, ConditionBean dreamCruiseCB) {
        if (!dreamCruiseCB.xisDreamCruiseShip()) {
            String msg = "The CB was not dream cruise: " + dreamCruiseCB.getClass();
            throw new IllegalConditionBeanOperationException(msg);
        }
        _calcSpecification.setBaseCB(baseCB);
        _calcSpecification.specify(dreamCruiseCB);
    }

    // ===================================================================================
    //                                                                     Connected Order
    //                                                                     ===============
    public void toBeConnectionModeAsAnd() {
        _connectionMode = ConnectionMode.AND;
    }

    public void toBeConnectionModeAsOr() {
        _connectionMode = ConnectionMode.OR;
    }

    public void clearConnectionMode() {
        _connectionMode = null;
    }

    protected enum ConnectionMode {
        AND("and"), OR("or");
        private String _connector;

        private ConnectionMode(String connector) {
            _connector = connector;
        }

        @Override
        public String toString() {
            return _connector;
        }
    }

    public static class ConnectedOrderBean {
        protected final ManualOrderBean _parentBean;

        public ConnectedOrderBean(ManualOrderBean parentBean) {
            _parentBean = parentBean;
        }

        public ConnectedOrderBean and_Equal(Object orderValue) {
            return doAnd(ConditionKey.CK_EQUAL, orderValue);
        }

        public ConnectedOrderBean and_NotEqual(Object orderValue) {
            return doAnd(ConditionKey.CK_NOT_EQUAL_STANDARD, orderValue);
        }

        public ConnectedOrderBean and_GreaterThan(Object orderValue) {
            return doAnd(ConditionKey.CK_GREATER_THAN, orderValue);
        }

        public ConnectedOrderBean and_LessThan(Object orderValue) {
            return doAnd(ConditionKey.CK_LESS_THAN, orderValue);
        }

        public ConnectedOrderBean and_GreaterEqual(Object orderValue) {
            return doAnd(ConditionKey.CK_GREATER_EQUAL, orderValue);
        }

        public ConnectedOrderBean and_LessEqual(Object orderValue) {
            return doAnd(ConditionKey.CK_LESS_EQUAL, orderValue);
        }

        public ConnectedOrderBean and_IsNull() {
            return doAnd(ConditionKey.CK_IS_NULL, null);
        }

        public ConnectedOrderBean and_IsNotNull() {
            return doAnd(ConditionKey.CK_IS_NOT_NULL, null);
        }

        public ConnectedOrderBean doAnd(ConditionKey conditionKey, Object orderValue) {
            toBeConnectionModeAsAnd();
            try {
                return delegate(conditionKey, orderValue);
            } finally {
                clearConnectionMode();
            }
        }

        public ConnectedOrderBean or_Equal(Object orderValue) {
            return doOr(ConditionKey.CK_EQUAL, orderValue);
        }

        public ConnectedOrderBean or_NotEqual(Object orderValue) {
            return doOr(ConditionKey.CK_NOT_EQUAL_STANDARD, orderValue);
        }

        public ConnectedOrderBean or_GreaterThan(Object orderValue) {
            return doOr(ConditionKey.CK_GREATER_THAN, orderValue);
        }

        public ConnectedOrderBean or_LessThan(Object orderValue) {
            return doOr(ConditionKey.CK_LESS_THAN, orderValue);
        }

        public ConnectedOrderBean or_GreaterEqual(Object orderValue) {
            return doOr(ConditionKey.CK_GREATER_EQUAL, orderValue);
        }

        public ConnectedOrderBean or_LessEqual(Object orderValue) {
            return doOr(ConditionKey.CK_LESS_EQUAL, orderValue);
        }

        public ConnectedOrderBean or_IsNull() {
            return doOr(ConditionKey.CK_IS_NULL, null);
        }

        public ConnectedOrderBean or_IsNotNull() {
            return doOr(ConditionKey.CK_IS_NOT_NULL, null);
        }

        public ConnectedOrderBean doOr(ConditionKey conditionKey, Object orderValue) {
            toBeConnectionModeAsOr();
            try {
                return delegate(conditionKey, orderValue);
            } finally {
                clearConnectionMode();
            }
        }

        protected ConnectedOrderBean delegate(ConditionKey conditionKey, Object orderValue) {
            if (ConditionKey.CK_EQUAL.equals(conditionKey)) {
                _parentBean.when_Equal(orderValue);
            } else if (ConditionKey.CK_NOT_EQUAL_STANDARD.equals(conditionKey)) {
                _parentBean.when_NotEqual(orderValue);
            } else if (ConditionKey.CK_GREATER_THAN.equals(conditionKey)) {
                _parentBean.when_GreaterThan(orderValue);
            } else if (ConditionKey.CK_LESS_THAN.equals(conditionKey)) {
                _parentBean.when_LessThan(orderValue);
            } else if (ConditionKey.CK_GREATER_EQUAL.equals(conditionKey)) {
                _parentBean.when_GreaterEqual(orderValue);
            } else if (ConditionKey.CK_LESS_EQUAL.equals(conditionKey)) {
                _parentBean.when_LessEqual(orderValue);
            } else if (ConditionKey.CK_IS_NULL.equals(conditionKey)) {
                _parentBean.when_IsNull();
            } else if (ConditionKey.CK_IS_NOT_NULL.equals(conditionKey)) {
                _parentBean.when_IsNotNull();
            } else {
                String msg = "Unknown conditionKey: " + conditionKey;
                throw new IllegalStateException(msg);
            }
            return this;
        }

        protected void toBeConnectionModeAsAnd() {
            _parentBean.toBeConnectionModeAsAnd();
        }

        protected void toBeConnectionModeAsOr() {
            _parentBean.toBeConnectionModeAsOr();
        }

        protected void clearConnectionMode() {
            _parentBean.clearConnectionMode();
        }
    }

    // ===================================================================================
    //                                                                    CaseWhen Element 
    //                                                                    ================
    protected CaseWhenElement createElement(ConditionKey conditionKey, Object orderValue) {
        return new CaseWhenElement(conditionKey, orderValue);
    }

    public static class CaseWhenElement {
        protected ConditionKey _conditionKey;
        protected Object _orderValue;
        protected List<CaseWhenElement> _connectedElementList; // top element only
        protected ConnectionMode _connectionMode; // connected elements only

        public CaseWhenElement(ConditionKey conditionKey, Object orderValue) {
            _conditionKey = conditionKey;
            _orderValue = orderValue;
        }

        public void toBeConnectionModeAsAnd() {
            _connectionMode = ConnectionMode.AND;
        }

        public void toBeConnectionModeAsOr() {
            _connectionMode = ConnectionMode.OR;
        }

        public String toConnector() {
            return _connectionMode != null ? _connectionMode.toString() : null;
        }

        @Override
        public String toString() {
            return DfTypeUtil.toClassTitle(this) + ":{" + _conditionKey + ", " + _orderValue + ", "
                    + _connectedElementList + ", " + _connectionMode + "}";
        }

        public ConditionKey getConditionKey() {
            return _conditionKey;
        }

        public Object getOrderValue() {
            return _orderValue;
        }

        @SuppressWarnings("unchecked")
        public List<CaseWhenElement> getConnectedElementList() {
            return _connectedElementList != null ? _connectedElementList : Collections.EMPTY_LIST;
        }

        public void addConnectedElement(CaseWhenElement connectedElement) {
            if (_connectedElementList == null) {
                _connectedElementList = new ArrayList<CaseWhenElement>();
            }
            _connectedElementList.add(connectedElement);
        }

        public ConnectionMode getConnectionMode() {
            return _connectionMode;
        }

        public void setConnectionMode(ConnectionMode connectionMode) {
            _connectionMode = connectionMode;
        }
    }

    // ===================================================================================
    //                                                                     Binding Process
    //                                                                     ===============
    public void bind(FreeParameterManualOrderThemeListHandler handler) {
        if (!hasManualOrder()) {
            return;
        }
        for (CaseWhenElement topElement : _caseWhenAcceptedList) {
            final CaseWhenElement boundTopElement = doBind(handler, topElement);
            final List<CaseWhenElement> connectedList = topElement.getConnectedElementList();
            for (CaseWhenElement connectedElement : connectedList) {
                final CaseWhenElement boundConnectedElement = doBind(handler, connectedElement);
                boundTopElement.addConnectedElement(boundConnectedElement);
            }
            _caseWhenBoundList.add(boundTopElement);
        }
    }

    protected CaseWhenElement doBind(FreeParameterManualOrderThemeListHandler handler, CaseWhenElement element) {
        final Object orderValue = getResolvedOrderValue(element);
        final String bindExp = handler.register(THEME_KEY, orderValue);
        final CaseWhenElement boundElement = createElement(element.getConditionKey(), bindExp);
        boundElement.setConnectionMode(element.getConnectionMode());
        return boundElement;
    }

    protected Object getResolvedOrderValue(CaseWhenElement element) {
        Object orderValue = element.getOrderValue();
        if (orderValue instanceof Classification) {
            final Classification cls = (Classification) orderValue;
            orderValue = handleClassificationOrderValue(cls);
        }
        return orderValue;
    }

    protected Object handleClassificationOrderValue(Classification cls) {
        final Object orderValue;
        final String plainCode = cls.code();
        final ClassificationCodeType codeType = cls.meta().codeType();
        if (ClassificationCodeType.Number.equals(codeType)) {
            if ("true".equalsIgnoreCase(plainCode) || "false".equalsIgnoreCase(plainCode)) {
                // true or false of Number, e.g. MySQL's Boolean
                orderValue = toClassificationBooleanValue(plainCode);
            } else {
                orderValue = toClassificationIntegerValue(plainCode);
            }
        } else if (ClassificationCodeType.Boolean.equals(codeType)) {
            orderValue = toClassificationBooleanValue(plainCode);
        } else {
            orderValue = plainCode;
        }
        return orderValue;
    }

    protected Integer toClassificationIntegerValue(String plainCode) {
        return (Integer) DfTypeUtil.toNumber(plainCode, Integer.class);
    }

    protected Boolean toClassificationBooleanValue(String plainCode) {
        return DfTypeUtil.toBoolean(plainCode);
    }

    public static interface FreeParameterManualOrderThemeListHandler {
        String register(String themeKey, Object orderValue);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasManualOrder() {
        return !_caseWhenAcceptedList.isEmpty() || _calcSpecification != null;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertCalculationColumnNumber(HpSpecifiedColumn specifiedColumn) {
        final ColumnInfo columnInfo = specifiedColumn.getColumnInfo();
        if (columnInfo == null) { // basically not null but just in case
            return;
        }
        if (!columnInfo.isPropertyTypeNumber()) {
            String msg = "The type of the calculation column should be Number: " + specifiedColumn;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertSpecifiedDreamCruiseTicket(HpSpecifiedColumn column) {
        if (!column.isDreamCruiseTicket()) {
            final String msg = "The specified column was not dream cruise ticket: " + column;
            throw new IllegalConditionBeanOperationException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _caseWhenAcceptedList + "}";
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
