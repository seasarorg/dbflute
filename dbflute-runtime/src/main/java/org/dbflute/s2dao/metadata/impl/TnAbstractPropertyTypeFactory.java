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

import java.util.ArrayList;
import java.util.List;

import org.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.dbflute.s2dao.metadata.PropertyType;
import org.seasar.extension.jdbc.ValueType;
import org.dbflute.s2dao.metadata.impl.PropertyTypeImpl;
import org.dbflute.s2dao.beans.BeanDesc;
import org.dbflute.s2dao.beans.PropertyDesc;
import org.dbflute.s2dao.beans.factory.BeanDescFactory;

/**
 * @author jflute
 */
public abstract class TnAbstractPropertyTypeFactory implements TnPropertyTypeFactory {

    protected Class<?> beanClass;
    protected TnBeanAnnotationReader beanAnnotationReader;
    protected TnValueTypeFactory valueTypeFactory;

    public TnAbstractPropertyTypeFactory(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader,
            TnValueTypeFactory valueTypeFactory) {
        this.beanClass = beanClass;
        this.beanAnnotationReader = beanAnnotationReader;
        this.valueTypeFactory = valueTypeFactory;
    }

    public PropertyType[] createDtoPropertyTypes() {
        List<PropertyType> list = new ArrayList<PropertyType>();
        BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PropertyType pt = createPropertyType(pd);
            list.add(pt);
        }
        return (PropertyType[]) list.toArray(new PropertyType[list.size()]);
    }

    /**
     * {@link BeanDesc}を返します。
     * 
     * @return {@link BeanDesc}
     */
    protected BeanDesc getBeanDesc() {
        return BeanDescFactory.getBeanDesc(beanClass);
    }

    /**
     * 関連を表すのプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return 関連を表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isRelation(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    /**
     * 主キーを表すプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link PropertyDesc}
     * @return　主キーを表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isPrimaryKey(PropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc) != null;
    }

    protected abstract boolean isPersistent(PropertyType propertyType);

    protected PropertyType createPropertyType(PropertyDesc propertyDesc) {
        final String columnName = getColumnName(propertyDesc);
        final ValueType valueType = getValueType(propertyDesc);
        return new PropertyTypeImpl(propertyDesc, valueType, columnName);
    }

    protected String getColumnName(PropertyDesc propertyDesc) {
        String propertyName = propertyDesc.getPropertyName();
        String name = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        return name != null ? name : propertyName;
    }

    protected ValueType getValueType(PropertyDesc propertyDesc) {
        final String name = beanAnnotationReader.getValueType(propertyDesc);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        Class<?> type = propertyDesc.getPropertyType();
        return valueTypeFactory.getValueTypeByClass(type);
    }
}
