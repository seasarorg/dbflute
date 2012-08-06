package org.seasar.dbflute.cbean.chelper;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.chelper.HpCalcElement.CalculationType;
import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.cbean.coption.ColumnConversionOption;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @param <CB> The type of condition-bean for column specification. 
 */
public class HpCalcSpecification<CB extends ConditionBean> implements HpCalculator, HpCalcStatement {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SpecifyQuery<CB> _specifyQuery;
    protected ConditionBean _baseCB; // to judge database type and save parameters of conversion
    protected CB _specifedCB;
    protected final List<HpCalcElement> _calculationList = DfCollectionUtil.newArrayList();
    protected boolean _leftMode;
    protected HpCalcSpecification<CB> _leftCalcSp;
    protected boolean _convert;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpCalcSpecification(SpecifyQuery<CB> specifyQuery) { // e.g. called by Update Calculation, ManualOrder, DerivedReferrer
        _specifyQuery = specifyQuery;
    }

    public HpCalcSpecification(SpecifyQuery<CB> specifyQuery, ConditionBean baseCB) { // e.g. called by ColumnQuery Calculation
        _specifyQuery = specifyQuery;
        _baseCB = baseCB;
    }

    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    public void specify(CB cb) {
        _specifyQuery.specify(cb);
        _specifedCB = cb; // saves for handling the specified column
        if (_baseCB == null) { // means base CB is same as specified one
            _baseCB = cb;
        }
    }

    // -----------------------------------------------------
    //                                           Column Info
    //                                           -----------
    // if column info is null, cipher adjustment is unsupported
    /**
     * @return The column info of specified column. (NullAllowed)
     */
    public ColumnInfo getSpecifiedColumnInfo() { // only when plain (or dream cruise)
        checkSpecifiedCB();
        if (_specifedCB.xhasDreamCruiseTicket()) {
            return _specifedCB.xshowDreamCruiseTicket().getColumnInfo();
        }
        return _specifedCB.getSqlClause().getSpecifiedColumnInfoAsOne();
    }

    /**
     * @return The column info of specified deriving column. (NullAllowed)
     */
    public ColumnInfo getSpecifiedDerivingColumnInfo() { // only when deriving sub-query
        checkSpecifiedCB();
        return _specifedCB.getSqlClause().getSpecifiedDerivingColumnInfoAsOne();
    }

    /**
     * @return The column info of specified resolved column. (NullAllowed)
     */
    public ColumnInfo getResolvedSpecifiedColumnInfo() { // resolved plain or deriving sub-query
        checkSpecifiedCB();
        final ColumnInfo columnInfo = getSpecifiedColumnInfo();
        return columnInfo != null ? columnInfo : getSpecifiedDerivingColumnInfo();
    }

    // -----------------------------------------------------
    //                                           Column Name
    //                                           -----------
    /**
     * @return The column DB name of specified resolved column. (NullAllowed)
     */
    public String getResolvedSpecifiedColumnDbName() { // resolved plain or deriving sub-query
        checkSpecifiedCB();
        if (_specifedCB.xhasDreamCruiseTicket()) {
            final HpSpecifiedColumn ticket = _specifedCB.xshowDreamCruiseTicket();
            return ticket.getColumnDbName();
        }
        final ColumnInfo columnInfo = getResolvedSpecifiedColumnInfo();
        return columnInfo != null ? columnInfo.getColumnDbName() : null;
    }

    /**
     * @return The column real name of specified resolved column. (NullAllowed)
     */
    public ColumnRealName getResolvedSpecifiedColumnRealName() { // resolved plain or deriving sub-query
        checkSpecifiedCB();
        if (_specifedCB.xhasDreamCruiseTicket()) {
            final HpSpecifiedColumn ticket = _specifedCB.xshowDreamCruiseTicket();
            return ticket.toColumnRealName();
        }
        final ColumnRealName columnRealName = _specifedCB.getSqlClause().getSpecifiedColumnRealNameAsOne();
        if (columnRealName != null) {
            return columnRealName;
        }
        final String subQuery = _specifedCB.getSqlClause().getSpecifiedDerivingSubQueryAsOne();
        if (subQuery != null) { // basically for (Specify)DerivedReferrer in ColumnQuery
            return ColumnRealName.create(null, new ColumnSqlName(subQuery));
        }
        return null;
    }

    /**
     * @return The column SQL name of specified resolved column. (NullAllowed)
     */
    public ColumnSqlName getResolvedSpecifiedColumnSqlName() { // resolved plain or deriving sub-query
        checkSpecifiedCB();
        if (_specifedCB.xhasDreamCruiseTicket()) {
            final HpSpecifiedColumn ticket = _specifedCB.xshowDreamCruiseTicket();
            return ticket.toColumnSqlName();
        }
        final ColumnSqlName columnSqlName = _specifedCB.getSqlClause().getSpecifiedColumnSqlNameAsOne();
        if (columnSqlName != null) {
            return columnSqlName;
        }
        final String subQuery = _specifedCB.getSqlClause().getSpecifiedDerivingSubQueryAsOne();
        if (subQuery != null) { // basically for (Specify)DerivedReferrer in ColumnQuery
            return new ColumnSqlName(subQuery);
        }
        return null;
    }

    // -----------------------------------------------------
    //                                           Table Alias
    //                                           -----------
    public String getResolvedSpecifiedTableAliasName() { // resolved plain or deriving sub-query
        checkSpecifiedCB();
        if (_specifedCB.xhasDreamCruiseTicket()) {
            final HpSpecifiedColumn ticket = _specifedCB.xshowDreamCruiseTicket();
            return ticket.getTableAliasName();
        }
        final ColumnRealName columnRealName = _specifedCB.getSqlClause().getSpecifiedColumnRealNameAsOne();
        if (columnRealName != null) {
            return columnRealName.getTableAliasName();
        }
        return _specifedCB.getSqlClause().getSpecifiedDerivingAliasNameAsOne();
    }

    // -----------------------------------------------------
    //                                          Check Status
    //                                          ------------
    protected void checkSpecifiedCB() {
        if (_specifedCB == null) {
            throwSpecifiedConditionBeanNotFoundException();
        }
    }

    protected void throwSpecifiedConditionBeanNotFoundException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the specified condition-bean.");
        br.addItem("Advice");
        br.addElement("You should call specify(cb) before building statements.");
        br.addItem("Specify Query");
        br.addElement(_specifyQuery);
        br.addItem("Calculation List");
        if (!_calculationList.isEmpty()) {
            for (HpCalcElement element : _calculationList) {
                br.addElement(element);
            }
        } else {
            br.addElement("*No calculation");
        }
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
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.plus(plusValue); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.PLUS, plusValue); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator plus(HpSpecifiedColumn plusColumn) {
        assertObjectNotNull("plusColumn", plusColumn);
        assertCalculationColumnNumber(plusColumn);
        assertSpecifiedDreamCruiseTicket(plusColumn);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.plus(plusColumn); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.PLUS, plusColumn); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator minus(Number minusValue) {
        assertObjectNotNull("minusValue", minusValue);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.minus(minusValue); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.MINUS, minusValue); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator minus(HpSpecifiedColumn minusColumn) {
        assertObjectNotNull("minusColumn", minusColumn);
        assertCalculationColumnNumber(minusColumn);
        assertSpecifiedDreamCruiseTicket(minusColumn);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.plus(minusColumn); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.MINUS, minusColumn); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator multiply(Number multiplyValue) {
        assertObjectNotNull("multiplyValue", multiplyValue);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.multiply(multiplyValue); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.MULTIPLY, multiplyValue); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator multiply(HpSpecifiedColumn multiplyColumn) {
        assertObjectNotNull("multiplyColumn", multiplyColumn);
        assertCalculationColumnNumber(multiplyColumn);
        assertSpecifiedDreamCruiseTicket(multiplyColumn);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.multiply(multiplyColumn); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.MULTIPLY, multiplyColumn); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator divide(Number divideValue) {
        assertObjectNotNull("divideValue", divideValue);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.divide(divideValue); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.DIVIDE, divideValue); // main process
        }
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator divide(HpSpecifiedColumn divideColumn) {
        assertObjectNotNull("divideColumn", divideColumn);
        assertCalculationColumnNumber(divideColumn);
        assertSpecifiedDreamCruiseTicket(divideColumn);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.divide(divideColumn); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.DIVIDE, divideColumn); // main process
        }
    }

    protected HpCalculator register(CalculationType type, Number value) {
        assertObjectNotNull("type", type);
        if (value == null) {
            String msg = "The null value was specified as " + type + ".";
            throw new IllegalArgumentException(msg);
        }
        final HpCalcElement calculation = new HpCalcElement();
        calculation.setCalculationType(type);
        calculation.setCalculationValue(value);
        _calculationList.add(calculation);
        return this;
    }

    protected HpCalculator register(CalculationType type, HpSpecifiedColumn column) {
        assertObjectNotNull("type", type);
        if (column == null) {
            String msg = "The null column was specified as " + type + ".";
            throw new IllegalArgumentException(msg);
        }
        final HpCalcElement calculation = new HpCalcElement();
        calculation.setCalculationType(type);
        calculation.setCalculationColumn(column);
        _calculationList.add(calculation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator convert(ColumnConversionOption option) {
        assertObjectNotNull("option", option);
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.convert(option); // dispatch to nested one
            return this;
        } else {
            return registerConv(option); // main process
        }
    }

    protected HpCalculator registerConv(ColumnConversionOption option) {
        if (option == null) {
            String msg = "The null value was specified as conversion option.";
            throw new IllegalArgumentException(msg);
        }
        final HpCalcElement calculation = new HpCalcElement();
        calculation.setCalculationType(CalculationType.CONV);
        calculation.setColumnConversionOption(option);
        _calculationList.add(calculation);
        // called later for VaryingUpdate
        //prepareConvOption(option);
        _convert = true;
        return this;
    }

    protected void prepareConvOption(ColumnConversionOption option) {
        option.xjudgeDatabase(_baseCB.getSqlClause());
        option.xsetTargetColumnInfo(getResolvedSpecifiedColumnInfo());
        _baseCB.localCQ().xregisterParameterOption(option);
    }

    protected void assertLeftCalcSp() {
        if (_leftCalcSp == null) {
            throwCalculationLeftColumnUnsupportedException();
        }
    }

    protected void throwCalculationLeftColumnUnsupportedException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The calculation for left column is unsupported at the function.");
        br.addItem("Advice");
        br.addElement("For example, ColumnQuery supports it);");
        br.addElement("but UpdateOption does not.");
        final String msg = br.buildExceptionMessage();
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                     Left/Right Mode
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    public HpCalculator left() {
        _leftMode = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator right() {
        _leftMode = false;
        return this;
    }

    // ===================================================================================
    //                                                                           Statement
    //                                                                           =========
    /**
     * {@inheritDoc}
     */
    public String buildStatementAsSqlName() { // e.g. VaryingUpdate
        final ColumnSqlName columnSqlName = getResolvedSpecifiedColumnSqlName();
        final String columnExp = columnSqlName.toString();
        return doBuildStatement(columnExp, null);
    }

    /**
     * {@inheritDoc}
     */
    public String buildStatementToSpecifidName(String columnExp) { // e.g. ColumnQuery, DerivedReferrer
        return doBuildStatement(columnExp, null);
    }

    /**
     * {@inheritDoc}
     */
    public String buildStatementToSpecifidName(String columnExp, Map<String, String> columnAliasMap) { // e.g. ManualOrder
        return doBuildStatement(columnExp, columnAliasMap);
    }

    protected String doBuildStatement(String columnExp, Map<String, String> columnAliasMap) {
        if (_calculationList.isEmpty()) {
            return null;
        }
        // columnAliasMap means, e.g. union, already handled cipher 
        String targetExp = columnAliasMap != null ? columnExp : decryptIfNeeds(columnExp);
        int index = 0;
        for (HpCalcElement calculation : _calculationList) {
            if (index > 0) {
                targetExp = "(" + targetExp + ")";
            }
            if (!calculation.isPreparedConvOption()) {
                final ColumnConversionOption option = calculation.getColumnConversionOption();
                if (option != null) {
                    prepareConvOption(option);
                    calculation.setPreparedConvOption(true);
                }
            }
            targetExp = buildCalculationExp(targetExp, columnAliasMap, calculation);
            ++index;
        }
        return targetExp;
    }

    /**
     * @param targetExp The expression of target column already handled cipher. (NotNull)
     * @param columnAliasMap The map of column alias. (NullAllowed)
     * @param calculation The element of calculation. (NotNull)
     * @return The expression of calculation statement. (NotNull)
     */
    protected String buildCalculationExp(String targetExp, Map<String, String> columnAliasMap, HpCalcElement calculation) {
        final CalculationType calculationType = calculation.getCalculationType();
        if (calculationType.equals(CalculationType.CONV)) { // convert
            final ColumnConversionOption columnConversionOption = calculation.getColumnConversionOption();
            return columnConversionOption.filterFunction(targetExp);
        }
        // number value or number column here
        final Object calcValueExp;
        if (calculation.hasCalculationValue()) { // number value
            calcValueExp = calculation.getCalculationValue();
        } else if (calculation.hasCalculationColumn()) { // number column
            final HpSpecifiedColumn calculationColumn = calculation.getCalculationColumn();
            final String columnExp = calculationColumn.toColumnRealName().toString();
            if (columnAliasMap != null) { // e.g. ManualOrder on union
                final String mappedAlias = columnAliasMap.get(columnExp);
                calcValueExp = mappedAlias != null ? mappedAlias : columnExp;
            } else { // e.g. ColumnQuery, UpdateOption, non-union ManualOrder, DerivedReferrer
                final ColumnInfo columnInfo = calculationColumn.getColumnInfo();
                calcValueExp = !calculationColumn.isDerived() ? decryptIfNeeds(columnInfo, columnExp) : columnExp;
            }
        } else {
            throwCalculationElementIllegalStateException(targetExp);
            return null; // unreachable
        }
        return targetExp + " " + calculationType.operand() + " " + calcValueExp;
    }

    protected void throwCalculationElementIllegalStateException(String targetExp) {
        String msg = "The either calculationValue or calculationColumn should exist: targetExp=" + targetExp;
        throw new IllegalStateException(msg);
    }

    protected String decryptIfNeeds(String valueExp) {
        return decryptIfNeeds(getSpecifiedColumnInfo(), valueExp);
    }

    protected String decryptIfNeeds(ColumnInfo columnInfo, String valueExp) {
        if (columnInfo == null) { // means sub-query
            return valueExp;
        }
        final ColumnFunctionCipher cipher = _baseCB.getSqlClause().findColumnFunctionCipher(columnInfo);
        return cipher != null ? cipher.decrypt(valueExp) : valueExp;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isSpecifyColumn() {
        return getSpecifiedColumnInfo() != null;
    }

    public boolean isDerivedReferrer() {
        return getSpecifiedDerivingColumnInfo() != null;
    }

    public boolean mayNullRevived() { // basically for auto-detect of inner-join
        if ((_specifedCB != null && _specifedCB.xhasDreamCruiseTicket()) || isDerivedReferrer()) {
            return true; // because it is so difficult to judge it accurately
        }
        for (HpCalcElement calcElement : _calculationList) {
            if (calcElement.getCalculationColumn() != null) {
                return true; // because it is so difficult to judge it accurately
            }
            final ColumnConversionOption option = calcElement.getColumnConversionOption();
            if (option != null && option.mayNullRevived()) {
                return true; // e.g. coalesce()
            }
        }
        return false;
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
    //                                                                            Accessor
    //                                                                            ========
    public SpecifyQuery<CB> getSpecifyQuery() {
        return _specifyQuery;
    }

    public void setBaseCB(ConditionBean baseCB) {
        _baseCB = baseCB;
    }

    public List<HpCalcElement> getCalculationList() {
        return _calculationList;
    }

    public boolean hasConvert() {
        return _convert;
    }

    public HpCalcSpecification<CB> getLeftCalcSp() {
        return _leftCalcSp;
    }

    public void setLeftCalcSp(HpCalcSpecification<CB> leftCalcSp) {
        _leftCalcSp = leftCalcSp;
    }
}
