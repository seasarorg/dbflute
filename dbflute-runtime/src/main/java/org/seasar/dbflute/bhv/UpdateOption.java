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
package org.seasar.dbflute.bhv;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.SpecifyQuery;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/18 Friday)
 * @param <CB> The type of condition-bean for specification.
 */
public class UpdateOption<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<SelfSpecification<CB>> _selfSpecificationList = DfCollectionUtil.newArrayList();
    protected final Map<String, SelfSpecification<CB>> _selfSpecificationMap = StringKeyMap.createAsFlexibleOrdered();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UpdateOption() {
    }

    // ===================================================================================
    //                                                                    Self Calculation
    //                                                                    ================
    public SpecificationCalculation self(SpecifyQuery<CB> specifyQuery) {
        if (specifyQuery == null) {
            String msg = "The argument 'specifyQuery' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final SelfSpecification<CB> specification = new SelfSpecification<CB>();
        specification.setSpecifyQuery(specifyQuery);
        _selfSpecificationList.add(specification);
        return specification;
    }

    // ===================================================================================
    //                                                               Resolve Specification
    //                                                               =====================
    public void resolveSpeicification(CB cb) {
        for (SelfSpecification<CB> specification : _selfSpecificationList) {
            final SpecifyQuery<CB> specifyQuery = specification.getSpecifyQuery();
            specifyQuery.specify(cb);
            final String columnDbName = getSpecifiedColumnNameAsOne(cb);
            _selfSpecificationMap.put(Srl.substringLastRear(columnDbName, "."), specification);
        }
    }

    protected String getSpecifiedColumnNameAsOne(CB cb) {
        return cb.getSqlClause().getSpecifiedColumnDbNameAsOne(); // it's column DB name
    }

    // ===================================================================================
    //                                                                     Build Statement
    //                                                                     ===============
    public boolean hasStatement(String columnDbName) {
        return findSpecification(columnDbName) != null;
    }

    public String buildStatement(String columnDbName, ColumnSqlName columnSqlName) {
        final SpecificationStatement statement = findSpecification(columnDbName);
        if (statement == null) {
            return null;
        }
        return statement.buildStatement(columnSqlName);
    }

    protected SpecificationStatement findSpecification(String columnDbName) {
        // only "self" supported
        return _selfSpecificationMap.get(columnDbName);
    }

    // ===================================================================================
    //                                                                       Related Class
    //                                                                       =============
    public static interface SpecificationStatement {
        String buildStatement(ColumnSqlName columnSqlsName);
    }

    public static interface SpecificationCalculation {
        SpecificationCalculation plus(Integer plusValue);

        SpecificationCalculation minus(Integer minusValue);

        SpecificationCalculation multiply(Integer multiplyValue);

        SpecificationCalculation divide(Integer divideValue);
    }

    public static class SelfSpecification<CB extends ConditionBean> implements SpecificationCalculation,
            SpecificationStatement {
        protected SpecifyQuery<CB> _specifyQuery;
        protected final List<SelfCalculation> _calculationList = DfCollectionUtil.newArrayList();

        public SpecificationCalculation plus(Integer plusValue) {
            return register(CalculationType.PLUS, plusValue);
        }

        public SpecificationCalculation minus(Integer minusValue) {
            return register(CalculationType.MINUS, minusValue);
        }

        public SpecificationCalculation multiply(Integer multiplyValue) {
            return register(CalculationType.MULTIPLY, multiplyValue);
        }

        public SpecificationCalculation divide(Integer divideValue) {
            return register(CalculationType.DIVIDE, divideValue);
        }

        protected SelfSpecification<CB> register(CalculationType type, Integer value) {
            final SelfCalculation calculation = new SelfCalculation();
            calculation.setCalculationType(type);
            calculation.setCalculationValue(value);
            _calculationList.add(calculation);
            return this;
        }

        public String buildStatement(ColumnSqlName columnSqlName) {
            final List<SelfCalculation> calculationList = getCalculationList();
            if (calculationList.isEmpty()) {
                String msg = "Not found calculation of the columnName: " + columnSqlName;
                throw new IllegalStateException(msg);
            }
            final StringBuilder sb = new StringBuilder();
            sb.append(columnSqlName);
            int index = 0;
            for (SelfCalculation calculation : calculationList) {
                if (index > 0) {
                    sb.insert(0, "(").append(")");
                }
                sb.append(" ").append(calculation.getCalculationType().operand());
                sb.append(" ").append(calculation.getCalculationValue());
                ++index;
            }
            return sb.toString();
        }

        public SpecifyQuery<CB> getSpecifyQuery() {
            return _specifyQuery;
        }

        public void setSpecifyQuery(SpecifyQuery<CB> specifyQuery) {
            this._specifyQuery = specifyQuery;
        }

        public List<SelfCalculation> getCalculationList() {
            return _calculationList;
        }
    }

    public static class SelfCalculation {
        protected CalculationType _calculationType;
        protected Integer _calculationValue;

        public CalculationType getCalculationType() {
            return _calculationType;
        }

        public void setCalculationType(CalculationType calculationType) {
            this._calculationType = calculationType;
        }

        public Integer getCalculationValue() {
            return _calculationValue;
        }

        public void setCalculationValue(Integer calculationValue) {
            this._calculationValue = calculationValue;
        }
    }

    public static enum CalculationType {
        PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/");
        private String _operand;

        private CalculationType(String operand) {
            _operand = operand;
        }

        public String operand() {
            return _operand;
        }
    }
}
