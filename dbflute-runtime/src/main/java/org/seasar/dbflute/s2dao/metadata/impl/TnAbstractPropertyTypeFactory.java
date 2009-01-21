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

import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.beans.TnBeanDesc;
import org.seasar.dbflute.s2dao.beans.TnPropertyDesc;
import org.seasar.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * {Refers to a S2Dao's class and Extends it}
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

    public TnPropertyType[] createDtoPropertyTypes() {
        List<TnPropertyType> list = new ArrayList<TnPropertyType>();
        TnBeanDesc beanDesc = getBeanDesc();
        List<String> proppertyNameList = beanDesc.getProppertyNameList();
        for (String proppertyName : proppertyNameList) {
            TnPropertyDesc pd = beanDesc.getPropertyDesc(proppertyName);
            TnPropertyType pt = createPropertyType(pd);
            list.add(pt);
        }
        return (TnPropertyType[]) list.toArray(new TnPropertyType[list.size()]);
    }

    /**
     * {@link TnBeanDesc}を返します。
     * 
     * @return {@link TnBeanDesc}
     */
    protected TnBeanDesc getBeanDesc() {
        return TnBeanDescFactory.getBeanDesc(beanClass);
    }

    /**
     * 関連を表すのプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link TnPropertyDesc}
     * @return 関連を表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isRelation(TnPropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    /**
     * 主キーを表すプロパティである場合<code>true</code>を返します。
     * 
     * @param propertyDesc {@link TnPropertyDesc}
     * @return　主キーを表すプロパティである場合<code>true</code>、そうでない場合<code>false</code>
     */
    protected boolean isPrimaryKey(TnPropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc) != null;
    }

    protected abstract boolean isPersistent(TnPropertyType propertyType);

    protected TnPropertyType createPropertyType(TnPropertyDesc propertyDesc) {
        final String columnName = getColumnName(propertyDesc);
        final ValueType valueType = getValueType(propertyDesc);
        return new TnPropertyTypeImpl(propertyDesc, valueType, columnName);
    }

    protected String getColumnName(TnPropertyDesc propertyDesc) {
        String propertyName = propertyDesc.getPropertyName();
        String name = beanAnnotationReader.getColumnAnnotation(propertyDesc);
        return name != null ? name : propertyName;
    }

    protected ValueType getValueType(TnPropertyDesc propertyDesc) {
        final String name = beanAnnotationReader.getValueType(propertyDesc);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        Class<?> type = propertyDesc.getPropertyType();
        return valueTypeFactory.getValueTypeByClass(type);
    }
}
