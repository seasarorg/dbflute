/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.cbean.coption;

import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The option of range-of scope for Number type.
 * @author jflute
 */
public class RangeOfOption implements ConditionOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _greaterThan;
    protected boolean _lessThan;
    protected boolean _orIsNull;

    // ===================================================================================
    //                                                            Interface Implementation
    //                                                            ========================
    public String getRearOption() {
        String msg = "Thie option does not use getRearOption().";
        throw new UnsupportedOperationException(msg);
    }

    // ===================================================================================
    //                                                                   Manual Adjustment
    //                                                                   =================
    // -----------------------------------------------------
    //                                               Operand
    //                                               -------
    /**
     * Set up operand for min number as greater-than. <br />
     * This is for manual adjustment.
     * @return this. (NotNull)
     */
    public RangeOfOption greaterThan() {
        _greaterThan = true;
        return this;
    }

    /**
     * Set up operand for max number as less-than. <br />
     * This is for manual adjustment.
     * @return this. (NotNull)
     */
    public RangeOfOption lessThan() {
        _lessThan = true;
        return this;
    }

    protected void clearOperand() {
        _greaterThan = false;
        _lessThan = false;
    }

    // ===================================================================================
    //                                                                      Plug-in Option
    //                                                                      ==============
    /**
     * Add 'or is null' to range-of conditions.
     * @return this. (NotNull)
     */
    public RangeOfOption orIsNull() {
        _orIsNull = true;
        return this;
    }

    /**
     * Get the condition-key of the min number.
     * @return The condition-key of the min number. (NotNull)
     */
    public ConditionKey getMinNumberConditionKey() {
        if (_greaterThan) {
            return _orIsNull ? ConditionKey.CK_GREATER_THAN_OR_IS_NULL : ConditionKey.CK_GREATER_THAN;
        } else { // as default
            return _orIsNull ? ConditionKey.CK_GREATER_EQUAL_OR_IS_NULL : ConditionKey.CK_GREATER_EQUAL;
        }
    }

    /**
     * Get the condition-key of the max number.
     * @return The condition-key of the max number. (NotNull)
     */
    public ConditionKey getMaxNumberConditionKey() {
        if (_lessThan) {
            return _orIsNull ? ConditionKey.CK_LESS_THAN_OR_IS_NULL : ConditionKey.CK_LESS_THAN;
        } else { // as default
            return _orIsNull ? ConditionKey.CK_LESS_EQUAL_OR_IS_NULL : ConditionKey.CK_LESS_EQUAL;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        final StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(":{greaterThan=").append(_greaterThan).append(", lessThan=").append(_lessThan);
        sb.append(", orIsNull=").append(_orIsNull).append("}");
        return sb.toString();
    }
}
