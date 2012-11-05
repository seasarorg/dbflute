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
package org.seasar.dbflute.s2dao.rshandler;

import java.util.Map;

/**
 * @author modified by jflute (originated in S2Dao)
 */
public final class TnRelationKey {

    private final Object[] _plainValues; // to compare
    private final Map<String, Object> _relKeyValues; // with column info
    private final int _hashCode;

    public TnRelationKey(Object[] plainValues, Map<String, Object> relKeyValues) {
        _plainValues = plainValues;
        _relKeyValues = relKeyValues;
        int calcHash = 0;
        for (int i = 0; i < plainValues.length; ++i) {
            calcHash += plainValues[i].hashCode();
        }
        _hashCode = calcHash;
    }

    public Object[] getPlainValues() {
        return _plainValues;
    }

    public Map<String, Object> getRelKeyValues() {
        return _relKeyValues;
    }

    @Override
    public int hashCode() {
        return _hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TnRelationKey)) {
            return false;
        }
        final Object[] otherValues = ((TnRelationKey) o)._plainValues;
        if (_plainValues.length != otherValues.length) {
            return false;
        }
        for (int i = 0; i < _plainValues.length; ++i) {
            if (!_plainValues[i].equals(otherValues[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return _relKeyValues.toString();
    }
}
