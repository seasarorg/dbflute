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
package org.seasar.dbflute.s2dao.metadata.impl;

import org.seasar.dbflute.helper.beans.DfPropertyAccessor;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnPropertyTypeImpl implements TnPropertyType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DfPropertyDesc propertyDesc;

    private final String propertyName;

    private String columnName;

    private final ValueType valueType;

    private boolean primaryKey = false;

    private boolean persistent = true;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnPropertyTypeImpl(DfPropertyDesc propertyDesc) {
        this(propertyDesc, TnValueTypes.OBJECT, propertyDesc.getPropertyName());
    }

    public TnPropertyTypeImpl(DfPropertyDesc propertyDesc, ValueType valueType) {
        this(propertyDesc, valueType, propertyDesc.getPropertyName());
    }

    public TnPropertyTypeImpl(DfPropertyDesc propertyDesc, ValueType valueType, String columnName) {
        this.propertyDesc = propertyDesc;
        this.propertyName = propertyDesc.getPropertyName();
        this.valueType = valueType;
        this.columnName = columnName;
    }

    public TnPropertyTypeImpl(String propertyName, ValueType valueType) {
        this(propertyName, valueType, propertyName);
    }

    public TnPropertyTypeImpl(String propertyName, ValueType valueType, String columnName) {
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.columnName = columnName;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return getClass().getSimpleName() + ":{" + propertyName + "(" + columnName + "), "
                + valueType.getClass().getSimpleName() + ", " + primaryKey + ", " + persistent + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfPropertyAccessor getPropertyAccessor() {
        return propertyDesc;
    }

    public DfPropertyDesc getPropertyDesc() {
        return propertyDesc;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}