package org.seasar.dbflute.cbean.chelper;

import java.util.List;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.cbean.cipher.ColumnFunctionCipher;
import org.seasar.dbflute.cbean.coption.ColumnConversionOption;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
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
    protected ConditionBean _baseCB;
    protected CB _specifedCB;
    protected final List<CalculationElement> _calculationList = DfCollectionUtil.newArrayList();
    protected boolean _leftMode;
    protected HpCalcSpecification<CB> _leftCalcSp;
    protected boolean _convert;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpCalcSpecification(SpecifyQuery<CB> specifyQuery) {
        _specifyQuery = specifyQuery;
    }

    public HpCalcSpecification(SpecifyQuery<CB> specifyQuery, ConditionBean baseCB) {
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

    public ColumnInfo getSpecifiedColumnInfo() { // only when plain
        return _specifedCB.getSqlClause().getSpecifiedColumnInfoAsOne();
    }

    public ColumnInfo getSpecifiedDerivingColumnInfo() { // only when deriving sub-query
        return _specifedCB.getSqlClause().getSpecifiedDerivingColumnInfoAsOne();
    }

    public ColumnInfo getResolvedSpecifiedColumnInfo() { // resolved plain or deriving sub-query
        ColumnInfo columnInfo = getSpecifiedColumnInfo();
        return columnInfo != null ? columnInfo : getSpecifiedDerivingColumnInfo();
    }

    public ColumnRealName getResolvedSpecifiedColumnRealName() { // resolved plain or deriving sub-query
        final ColumnRealName columnRealName = _specifedCB.getSqlClause().getSpecifiedColumnRealNameAsOne();
        if (columnRealName != null) {
            return columnRealName;
        }
        final String subQuery = _specifedCB.getSqlClause().getSpecifiedDerivingSubQueryAsOne();
        if (subQuery != null) {
            // basically for (Specify)DerivedReferrer in ColumnQuery
            return new ColumnRealName(null, new ColumnSqlName(subQuery));
        }
        return null;
    }

    public ColumnSqlName getResolvedSpecifiedColumnSqlName() { // resolved plain or deriving sub-query
        final ColumnSqlName columnSqlName = _specifedCB.getSqlClause().getSpecifiedColumnSqlNameAsOne();
        if (columnSqlName != null) {
            return columnSqlName;
        }
        final String subQuery = _specifedCB.getSqlClause().getSpecifiedDerivingSubQueryAsOne();
        if (subQuery != null) {
            // basically for (Specify)DerivedReferrer in ColumnQuery
            return new ColumnSqlName(subQuery);
        }
        return null;
    }

    // ===================================================================================
    //                                                                         Calculation
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public HpCalculator plus(Number plusValue) {
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
    public HpCalculator minus(Number minusValue) {
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
    public HpCalculator multiply(Number multiplyValue) {
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
    public HpCalculator divide(Number divideValue) {
        if (_leftMode) {
            assertLeftCalcSp();
            _leftCalcSp.divide(divideValue); // dispatch to nested one
            return this;
        } else {
            return register(CalculationType.DIVIDE, divideValue); // main process
        }
    }

    protected HpCalculator register(CalculationType type, Number value) {
        if (value == null) {
            String msg = "The null value was specified as " + type + ": " + _specifyQuery;
            throw new IllegalArgumentException(msg);
        }
        final CalculationElement calculation = new CalculationElement();
        calculation.setCalculationType(type);
        calculation.setCalculationValue(value);
        _calculationList.add(calculation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public HpCalculator convert(ColumnConversionOption option) {
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
            String msg = "The null value was specified as conversion option: " + _specifyQuery;
            throw new IllegalArgumentException(msg);
        }
        final CalculationElement calculation = new CalculationElement();
        calculation.setCalculationType(CalculationType.CONV);
        calculation.setColumnConversionOption(option);
        _calculationList.add(calculation);
        prepareConvOption(option);
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
    public String buildStatementAsSqlName() {
        final ColumnSqlName columnSqlName = getResolvedSpecifiedColumnSqlName();
        final String columnExp = columnSqlName.toString();
        return doBuildStatement(columnExp);
    }

    /**
     * {@inheritDoc}
     */
    public String buildStatementToSpecifidName(String columnExp) {
        return doBuildStatement(columnExp);
    }

    protected String doBuildStatement(String columnExp) {
        final List<CalculationElement> calculationList = getCalculationList();
        if (calculationList.isEmpty()) {
            return null;
        }
        String targetExp = decryptIfNeeds(columnExp);
        int index = 0;
        for (CalculationElement calculation : calculationList) {
            if (index > 0) {
                targetExp = "(" + targetExp + ")";
            }
            targetExp = calculation.buildExp(targetExp);
            ++index;
        }
        return targetExp;
    }

    protected String decryptIfNeeds(String valueExp) {
        final ColumnInfo columnInfo = getSpecifiedColumnInfo();
        if (columnInfo == null) { // means sub-query
            return valueExp;
        }
        final ColumnFunctionCipher cipher = _specifedCB.getSqlClause().findColumnFunctionCipher(columnInfo);
        return cipher != null ? cipher.decrypt(valueExp) : valueExp;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SpecifyQuery<CB> getSpecifyQuery() {
        return _specifyQuery;
    }

    public List<CalculationElement> getCalculationList() {
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
