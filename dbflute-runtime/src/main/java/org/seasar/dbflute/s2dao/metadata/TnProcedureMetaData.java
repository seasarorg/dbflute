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
package org.seasar.dbflute.s2dao.metadata;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final String _procedureName;
    private final Map<String, TnProcedureParameterType> _parameterTypeMap = createParameterTypeMap();
    private final SortedSet<TnProcedureParameterType> _parameterTypeSortedSet = createParameterTypeSet();
    private TnProcedureParameterType _returnParameterType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureMetaData(final String procedureName) {
        this._procedureName = procedureName;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    public String createSql() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        int size = getParameterTypeSortedSet().size();
        if (hasReturnParameterType()) {
            sb.append("? = ");
            size--;
        }
        sb.append("call ").append(getProcedureName()).append("(");
        for (int i = 0; i < size; i++) {
            sb.append("?, ");
        }
        if (size > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    protected Map<String, TnProcedureParameterType> createParameterTypeMap() {
        return new HashMap<String, TnProcedureParameterType>(); // unordered
    }

    protected SortedSet<TnProcedureParameterType> createParameterTypeSet() {
        return new TreeSet<TnProcedureParameterType>(new Comparator<TnProcedureParameterType>() {
            public int compare(TnProcedureParameterType parameterType1, TnProcedureParameterType parameterType2) {
                final Integer parameterIndex1 = parameterType1.getParameterIndex();
                final Integer parameterIndex2 = parameterType2.getParameterIndex();
                return parameterIndex1.compareTo(parameterIndex2);
            }
        });
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProcedureName() {
        return _procedureName;
    }

    public SortedSet<TnProcedureParameterType> getParameterTypeSortedSet() {
        return _parameterTypeSortedSet;
    }

    public void addParameterType(TnProcedureParameterType parameterType) {
        final String name = parameterType.getParameterName();
        _parameterTypeMap.put(name, parameterType);
        _parameterTypeSortedSet.add(parameterType);
        if (parameterType.isReturnType()) {
            _returnParameterType = parameterType;
        }
    }

    public boolean hasReturnParameterType() {
        return _returnParameterType != null;
    }

    public TnProcedureParameterType getReturnParameterType() {
        return _returnParameterType;
    }
}
