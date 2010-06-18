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
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/18 Friday)
 * @param <CB> The type of condition-bean for specification.
 */
public class VaryingOption<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<SelfSpecification<CB>> _selfSpecificationList = DfCollectionUtil.newArrayList();
    protected final Map<String, SelfSpecification<CB>> _selfSpecificationMap = StringKeyMap
            .createAsCaseInsensitiveOrdered();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public VaryingOption() {
    }

    // ===================================================================================
    //                                                                    Self Calculation
    //                                                                    ================
    public SelfSpecification<CB> self(SpecifyQuery<CB> specifyQuery) {
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
            final String columnName = cb.getSqlClause().getSpecifiedColumnNameAsOne();
            _selfSpecificationMap.put(Srl.substringLastRear(columnName, "."), specification);
        }
    }

    // ===================================================================================
    //                                                                     Build Statement
    //                                                                     ===============
    public String buildStatement(String columnName) {
        final SelfSpecification<CB> specification = _selfSpecificationMap.get(columnName);
        if (specification != null) {
            return doBuildSelfStatement(columnName, specification);
        }
        // only "self" supported
        return null;
    }

    protected String doBuildSelfStatement(String columnName, SelfSpecification<CB> specification) {
        final List<SelfCalculation> calculationList = specification.getCalculationList();
        if (calculationList.isEmpty()) {
            return null; // should be exception?
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(columnName);
        int index = 0;
        for (SelfCalculation calculation : calculationList) {
            if (index > 0) {
                sb.insert(0, "(").append(")");
            }
            sb.append(" ").append(calculation.getCalculationType().operand());
            ++index;
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       Related Class
    //                                                                       =============
    public static class SelfSpecification<CB extends ConditionBean> {
        protected SpecifyQuery<CB> _specifyQuery;
        protected List<SelfCalculation> _calculationList = DfCollectionUtil.newArrayList();

        public SelfSpecification<CB> plus(Integer plusValue) {
            final SelfCalculation calculation = new SelfCalculation();
            calculation.setCalculationType(CalculationType.PLUS);
            calculation.setCalculationValue(plusValue);
            _calculationList.add(calculation);
            return this;
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

        public void setCalculationList(List<SelfCalculation> calculationList) {
            this._calculationList = calculationList;
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
        PLUS("+"), MINUS("-"), MULTIPLY("*");
        private String _operand;

        private CalculationType(String operand) {
            _operand = operand;
        }

        public String operand() {
            return _operand;
        }
    }
}
