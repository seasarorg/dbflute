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

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractPropertyTypeFactory implements TnPropertyTypeFactory {

    protected Class<?> beanClass;
    protected TnBeanAnnotationReader beanAnnotationReader;

    public TnAbstractPropertyTypeFactory(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
    }

    public TnPropertyType[] createDtoPropertyTypes() {
        List<TnPropertyType> list = new ArrayList<TnPropertyType>();
        DfBeanDesc beanDesc = getBeanDesc();
        List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            DfPropertyDesc pd = beanDesc.getPropertyDesc(proppertyName);
            TnPropertyType pt = createPropertyType(pd);
            list.add(pt);
        }
        return (TnPropertyType[]) list.toArray(new TnPropertyType[list.size()]);
    }

    protected DfBeanDesc getBeanDesc() {
        return DfBeanDescFactory.getBeanDesc(beanClass);
    }

    protected boolean isRelation(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    protected boolean isPrimaryKey(DfPropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc) != null;
    }

    protected abstract boolean isPersistent(TnPropertyType propertyType);

    protected TnPropertyType createPropertyType(DfPropertyDesc propertyDesc) {
        final String columnName = getColumnName(propertyDesc);
        final ValueType valueType = getValueType(propertyDesc);
        return new TnPropertyTypeImpl(propertyDesc, valueType, columnName);
    }

    protected String getColumnName(DfPropertyDesc propertyDesc) {
        String propertyName = propertyDesc.getPropertyName();
        String name = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        return name != null ? name : propertyName;
    }

    protected ValueType getValueType(DfPropertyDesc propertyDesc) {
        final String name = beanAnnotationReader.getValueType(propertyDesc);
        if (name != null) {
            return TnValueTypes.getPluginValueType(name);
        }
        final Class<?> type = propertyDesc.getPropertyType();
        return TnValueTypes.getValueType(type);
    }
}
