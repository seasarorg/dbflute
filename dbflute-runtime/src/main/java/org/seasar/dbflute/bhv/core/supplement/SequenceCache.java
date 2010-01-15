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
package org.seasar.dbflute.bhv.core.supplement;

import java.math.BigDecimal;

import org.seasar.dbflute.XLog;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The handler of sequence cache.
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public class SequenceCache {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final BigDecimal INITIAL_ADDED_COUNT = BigDecimal.ZERO;
    protected static final BigDecimal DEFAULT_ADD_SIZE = BigDecimal.ONE;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The cache size that should be same as increment size of sequence. */
    protected final BigDecimal _cacheSize;

    protected final Class<?> _resultType;
    protected BigDecimal _addedCount = INITIAL_ADDED_COUNT;
    protected BigDecimal _sequenceValue;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SequenceCache(BigDecimal cacheSize, Class<?> resultType) {
        _cacheSize = cacheSize;
        _resultType = resultType;
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    public synchronized Object nextval(SequenceRealExecutor executor) {
        _addedCount = _addedCount.add(getAddSize());
        if (_sequenceValue != null && _addedCount.compareTo(_cacheSize) < 0) {
            if (isLogEnabled()) {
                String msg = "...Getting next value from sequence cache:";
                msg = msg + " (" + _sequenceValue + " + " + _addedCount + ")";
                log(msg);
            }
            return DfTypeUtil.toNumber(_resultType, _sequenceValue.add(_addedCount));
        }
        _sequenceValue = selectSequence(executor);
        _addedCount = INITIAL_ADDED_COUNT;
        return DfTypeUtil.toNumber(_resultType, _sequenceValue);
    }

    protected BigDecimal selectSequence(SequenceRealExecutor executor) {
        final Object obj = executor.execute();
        if (obj == null) {
            String msg = "The sequence real executor should not return null:";
            msg = msg + " executor=" + executor;
            throw new IllegalStateException(msg);
        }
        return DfTypeUtil.toBigDecimal(obj);
    }

    protected BigDecimal getAddSize() {
        return DEFAULT_ADD_SIZE;
    }

    public static interface SequenceRealExecutor {
        Object execute();
    }

    // ===================================================================================
    //                                                                                 Log
    //                                                                                 ===
    protected void log(String msg) {
        XLog.log(msg);
    }

    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + "incrementSize=" + _cacheSize + ", resultType=" + _resultType + "}@" + hashCode();
    }
}
