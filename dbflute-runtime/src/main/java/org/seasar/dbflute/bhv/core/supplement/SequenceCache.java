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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    /** The cache size that should be same as increment size of sequence. (NotNull) */
    protected final BigDecimal _cacheSize;

    /** The result type of sequence next value. (NotNull) */
    protected final Class<?> _resultType;

    /** The added count. If cached list is valid, this value is unused. (NotNull) */
    protected volatile BigDecimal _addedCount = INITIAL_ADDED_COUNT;

    /** The sequence value as base point. (Nullable: only at first null) */
    protected volatile BigDecimal _sequenceValue;

    protected final List<BigDecimal> _cachedList = new ArrayList<BigDecimal>();
    protected final SortedSet<BigDecimal> _tmpSortedSet = new TreeSet<BigDecimal>(new Comparator<BigDecimal>() {
        public int compare(BigDecimal arg0, BigDecimal arg1) {
            return arg0.compareTo(arg1);
        }
    });

    protected volatile boolean _batchWay;

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
        if (_batchWay) {
            if (!_cachedList.isEmpty()) {
                if (isLogEnabled()) {
                    String msg = "...Getting next value from cached list:";
                    msg = msg + " (" + _sequenceValue + " + x)";
                    log(msg);
                }
                return toResultType(_cachedList.remove(0));
            }
        } else { // incrementWay
            _addedCount = _addedCount.add(getAddSize());
            if (_sequenceValue != null && _addedCount.compareTo(_cacheSize) < 0) {
                if (isLogEnabled()) {
                    String msg = "...Getting next value from added count:";
                    msg = msg + " (" + _sequenceValue + " + " + _addedCount + ")";
                    log(msg);
                }
                return toResultType(_sequenceValue.add(_addedCount));
            }
        }
        if (isLogEnabled()) {
            String msg = "...Selecting next value and cache values:";
            msg = msg + " cacheSize=" + _cacheSize;
            log(msg);
        }
        setupSequence(executor);
        return toResultType(_sequenceValue);
    }

    protected BigDecimal getAddSize() {
        return DEFAULT_ADD_SIZE;
    }

    protected void setupSequence(SequenceRealExecutor executor) {
        initialize();
        final Object obj = executor.execute();
        assertSequenceRealExecutorReturnsNotNull(obj, executor);
        if (obj instanceof List<?>) { // batchWay
            final List<?> selectedList = (List<?>) obj; // no guarantee of order
            assertSequenceRealExecutorReturnsNotEmptyList(selectedList, executor);
            for (Object element : selectedList) {
                _tmpSortedSet.add(toInternalType(element)); // order ascend
            }
            _cachedList.addAll(_tmpSortedSet); // setting up cached list (ordered)
            _sequenceValue = _cachedList.remove(0);
            _batchWay = true;
        } else { // incrementWay
            _sequenceValue = toInternalType(obj);
            _batchWay = false;
        }
    }

    protected void initialize() {
        _addedCount = INITIAL_ADDED_COUNT;
        _cachedList.clear();
        _tmpSortedSet.clear();
    }

    protected void assertSequenceRealExecutorReturnsNotNull(Object obj, SequenceRealExecutor executor) {
        if (obj == null) {
            String msg = "The sequence real executor should not return null:";
            msg = msg + " executor=" + executor;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertSequenceRealExecutorReturnsNotEmptyList(List<?> selectedList, SequenceRealExecutor executor) {
        if (selectedList.isEmpty()) {
            String msg = "The sequence real executor should not return empty list:";
            msg = msg + " executor=" + executor;
            throw new IllegalStateException(msg);
        }
    }

    public static interface SequenceRealExecutor {
        Object execute();
    }

    protected BigDecimal toInternalType(Object value) {
        return DfTypeUtil.toBigDecimal(value);
    }

    protected Object toResultType(BigDecimal value) {
        return DfTypeUtil.toNumber(_resultType, value);
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
        final String hash = Integer.toHexString(hashCode());
        return "{" + "cacheSize=" + _cacheSize + ", resultType=" + _resultType + "}@" + hash;
    }
}
