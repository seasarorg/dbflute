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
package org.dbflute.s2dao.metadata.impl;

import org.dbflute.jdbc.TnValueType;
import org.dbflute.s2dao.beans.TnPropertyDesc;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class TnPropertyTypeImpl implements TnPropertyType {

    private TnPropertyDesc propertyDesc;

    private String propertyName;

    private String columnName;

    private TnValueType valueType;

    private boolean primaryKey = false;

    private boolean persistent = true;

    public TnPropertyTypeImpl(TnPropertyDesc propertyDesc) {
        this(propertyDesc, TnValueTypes.OBJECT, propertyDesc.getPropertyName());
    }

    public TnPropertyTypeImpl(TnPropertyDesc propertyDesc, TnValueType valueType) {
        this(propertyDesc, valueType, propertyDesc.getPropertyName());
    }

    public TnPropertyTypeImpl(TnPropertyDesc propertyDesc, TnValueType valueType, String columnName) {
        this.propertyDesc = propertyDesc;
        this.propertyName = propertyDesc.getPropertyName();
        this.valueType = valueType;
        this.columnName = columnName;
    }

    public TnPropertyTypeImpl(String propertyName, TnValueType valueType) {
        this(propertyName, valueType, propertyName);
    }

    public TnPropertyTypeImpl(String propertyName, TnValueType valueType, String columnName) {
        this.propertyName = propertyName;
        this.valueType = valueType;
        this.columnName = columnName;
    }

    public TnPropertyDesc getPropertyDesc() {
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

    public TnValueType getValueType() {
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