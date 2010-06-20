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
package org.seasar.dbflute.cbean.ckey;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;

/**
 * The condition-key of greaterEqual.
 * @author jflute
 */
public class ConditionKeyGreaterEqual extends ConditionKey {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(ConditionKeyGreaterEqual.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    protected ConditionKeyGreaterEqual() {
        _conditionKey = "greaterEqual";
        _operand = ">=";
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * {@inheritDoc}
     */
    public boolean isValidRegistration(ConditionValue conditionValue, Object value, String callerName) {
        if (value == null) {
            return false;
        }
        if (conditionValue.hasGreaterEqual()) {
            if (conditionValue.equalGreaterEqual(value)) {
                final String target = callerName + "." + _conditionKey;
                _log.debug("The value has already registered at " + target + ": value=" + value);
                return false;
            } else {
                conditionValue.overrideGreaterEqual(value);
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<String> conditionList, ColumnRealName columnRealName, ConditionValue value) {
        conditionList.add(buildBindClause(columnRealName, value.getGreaterEqualLocation()));
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<String> conditionList, ColumnRealName columnRealName, ConditionValue value,
            ConditionOption option) {
        throw new UnsupportedOperationException("doAddWhereClause that has ConditionOption is unsupported!!!");
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        conditionValue.setGreaterEqual(value).setGreaterEqualLocation(location);
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location,
            ConditionOption option) {
        throw new UnsupportedOperationException("doSetupConditionValue with condition-option is unsupported!!!");
    }
}
