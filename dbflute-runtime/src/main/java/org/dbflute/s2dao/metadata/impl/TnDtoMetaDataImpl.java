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

import java.util.Map;

import org.dbflute.helper.StringKeyMap;
import org.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.dbflute.s2dao.metadata.TnDtoMetaData;
import org.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.extension.jdbc.PropertyType;

/**
 * @author jflute
 */
public class TnDtoMetaDataImpl implements TnDtoMetaData {

    protected Class<?> beanClass;
    protected StringKeyMap<PropertyType> propertyTypeMap = StringKeyMap.createAsCaseInsensitiveConcurrent();
    protected TnBeanAnnotationReader beanAnnotationReader;
    protected TnPropertyTypeFactory propertyTypeFactory;

    public TnDtoMetaDataImpl() {
    }

    public void initialize() {
        setupPropertyType();
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Map<String, PropertyType> getPropertyTypeMap() {
        return propertyTypeMap;
    }

    public PropertyType getPropertyType(String propertyName) {
        PropertyType propertyType = (PropertyType) propertyTypeMap.get(propertyName);
        if (propertyType == null) {
            String msg = "The propertyName was not found in the map:";
            msg = msg + " propertyName=" + propertyName + " propertyTypeMap=" + propertyTypeMap;
            throw new IllegalStateException(msg);
        }
        return propertyType;
    }

    public boolean hasPropertyType(String propertyName) {
        return propertyTypeMap.get(propertyName) != null;
    }

    protected void setupPropertyType() {
        PropertyType[] propertyTypes = propertyTypeFactory.createDtoPropertyTypes();
        for (int i = 0; i < propertyTypes.length; ++i) {
            PropertyType pt = propertyTypes[i];
            addPropertyType(pt);
        }
    }

    protected void addPropertyType(PropertyType propertyType) {
        propertyTypeMap.put(propertyType.getPropertyName(), propertyType);
    }

    public void setBeanAnnotationReader(TnBeanAnnotationReader beanAnnotationReader) {
        this.beanAnnotationReader = beanAnnotationReader;
    }

    public void setPropertyTypeFactory(TnPropertyTypeFactory propertyTypeFactory) {
        this.propertyTypeFactory = propertyTypeFactory;
    }
}
