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

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.dbflute.s2dao.metadata.TnDtoMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.metadata.TnPropertyTypeFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnDtoMetaDataImpl implements TnDtoMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> _beanClass;
    protected final StringKeyMap<TnPropertyType> _propertyTypeMap = StringKeyMap.createAsCaseInsensitive();
    protected final List<TnPropertyType> _propertyTypeList = new ArrayList<TnPropertyType>();
    protected TnBeanAnnotationReader _beanAnnotationReader;
    protected TnPropertyTypeFactory _propertyTypeFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDtoMetaDataImpl(Class<?> beanClass) {
        _beanClass = beanClass;
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    public void initialize() { // non thread safe
        setupPropertyType();
    }

    protected void setupPropertyType() {
        final TnPropertyType[] propertyTypes = _propertyTypeFactory.createDtoPropertyTypes();
        for (int i = 0; i < propertyTypes.length; ++i) {
            final TnPropertyType pt = propertyTypes[i];
            addPropertyType(pt);
        }
    }

    protected void addPropertyType(TnPropertyType propertyType) {
        _propertyTypeMap.put(propertyType.getPropertyName(), propertyType);
        _propertyTypeList.add(propertyType);
    }

    // ===================================================================================
    //                                                                          Bean Class
    //                                                                          ==========
    public Class<?> getBeanClass() {
        return _beanClass;
    }

    // ===================================================================================
    //                                                                       Property Type
    //                                                                       =============
    public List<TnPropertyType> getPropertyTypeList() {
        return _propertyTypeList;
    }

    public TnPropertyType getPropertyType(String propertyName) {
        TnPropertyType propertyType = (TnPropertyType) _propertyTypeMap.get(propertyName);
        if (propertyType == null) {
            String msg = "The propertyName was not found in the map:";
            msg = msg + " propertyName=" + propertyName + " propertyTypeMap=" + _propertyTypeMap;
            throw new IllegalStateException(msg);
        }
        return propertyType;
    }

    public boolean hasPropertyType(String propertyName) {
        return _propertyTypeMap.get(propertyName) != null;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBeanAnnotationReader(TnBeanAnnotationReader beanAnnotationReader) {
        this._beanAnnotationReader = beanAnnotationReader;
    }

    public void setPropertyTypeFactory(TnPropertyTypeFactory propertyTypeFactory) {
        this._propertyTypeFactory = propertyTypeFactory;
    }
}
