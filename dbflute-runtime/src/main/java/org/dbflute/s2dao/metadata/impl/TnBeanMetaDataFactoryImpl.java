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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.dbflute.s2dao.metadata.TnModifiedPropertySupport;
import org.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.dbflute.s2dao.metadata.TnPropertyTypeFactoryBuilder;
import org.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;
import org.dbflute.s2dao.metadata.TnRelationPropertyTypeFactoryBuilder;
import org.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.extension.jdbc.util.ConnectionUtil;
import org.seasar.extension.jdbc.util.DataSourceUtil;
import org.dbflute.s2dao.beans.BeanDesc;
import org.dbflute.s2dao.beans.PropertyDesc;
import org.dbflute.s2dao.beans.factory.BeanDescFactory;

/**
 * @author jflute
 */
public class TnBeanMetaDataFactoryImpl implements TnBeanMetaDataFactory {

    protected DataSource dataSource;
    protected TnValueTypeFactory valueTypeFactory;

    public TnBeanMetaData createBeanMetaData(final Class<?> daoInterface, final Class<?> beanClass) {
        return createBeanMetaData(beanClass);
    }

    public TnBeanMetaData createBeanMetaData(final Class<?> beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public TnBeanMetaData createBeanMetaData(final Class<?> beanClass, final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final Connection con = DataSourceUtil.getConnection(dataSource);
        try {
            final DatabaseMetaData metaData = ConnectionUtil.getMetaData(con);
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } finally {
            ConnectionUtil.close(con);
        }
    }

    public TnBeanMetaData createBeanMetaData(final DatabaseMetaData dbMetaData, final Class<?> beanClass,
            final int relationNestLevel) {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        final TnBeanAnnotationReader bar = createBeanAnnotationReader(beanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(bar);
        final String timestampPropertyName = getTimestampPropertyName(bar);
        final TnPropertyTypeFactory ptf = createPropertyTypeFactory(beanClass, bar, dbMetaData);
        final TnRelationPropertyTypeFactory rptf = createRelationPropertyTypeFactory(beanClass, bar, dbMetaData,
                relationNestLevel, stopRelationCreation);
        final TnBeanMetaDataImpl bmd = createBeanMetaDataImpl();

        bmd.setBeanAnnotationReader(bar);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setBeanClass(beanClass);
        bmd.setPropertyTypeFactory(ptf);
        bmd.setRelationPropertyTypeFactory(rptf);
        bmd.initialize();

        bmd.setModifiedPropertySupport(new TnModifiedPropertySupport() {
            @SuppressWarnings("unchecked")
            public Set<String> getModifiedPropertyNames(Object bean) {
                BeanDesc beanDesc = BeanDescFactory.getBeanDesc(bean.getClass());
                String propertyName = "modifiedPropertyNames";
                if (!beanDesc.hasPropertyDesc(propertyName)) {
                    return Collections.EMPTY_SET;
                } else {
                    PropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyName);
                    Object value = propertyDesc.getValue(bean);
                    Set<String> names = (Set<String>) value;
                    return names;
                }
            }

        });

        return bmd;
    }

    protected TnBeanAnnotationReader createBeanAnnotationReader(Class<?> beanClass) {
        return new TnFieldBeanAnnotationReader(beanClass);
    }

    protected String getVersionNoPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        final String defaultName = "versionNo";
        final String name = beanAnnotationReader.getVersionNoPropertyName();
        return name != null ? name : defaultName;
    }

    protected String getTimestampPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        final String defaultName = "timestamp";
        final String name = beanAnnotationReader.getTimestampPropertyName();
        return name != null ? name : defaultName;
    }

    protected TnPropertyTypeFactory createPropertyTypeFactory(Class<?> originalBeanClass,
            TnBeanAnnotationReader beanAnnotationReader, DatabaseMetaData databaseMetaData) {
        return createPropertyTypeFactoryBuilder().build(originalBeanClass, beanAnnotationReader);
    }

    protected TnPropertyTypeFactoryBuilder createPropertyTypeFactoryBuilder() {
        TnPropertyTypeFactoryBuilderImpl impl = new TnPropertyTypeFactoryBuilderImpl();
        impl.setValueTypeFactory(valueTypeFactory);
        return impl;
    }

    protected TnRelationPropertyTypeFactory createRelationPropertyTypeFactory(Class<?> originalBeanClass,
            TnBeanAnnotationReader beanAnnotationReader, DatabaseMetaData databaseMetaData, int relationNestLevel,
            boolean isStopRelationCreation) {
        return createRelationPropertyTypeFactoryBuilder().build(originalBeanClass, beanAnnotationReader,
                databaseMetaData, relationNestLevel, isStopRelationCreation);
    }

    protected TnRelationPropertyTypeFactoryBuilder createRelationPropertyTypeFactoryBuilder() {
        TnRelationPropertyTypeFactoryBuilderImpl impl = new TnRelationPropertyTypeFactoryBuilderImpl();
        impl.setBeanMetaDataFactory(this);
        return impl;
    }

    protected TnBeanMetaDataImpl createBeanMetaDataImpl() {
        return new TnBeanMetaDataImpl();
    }

    protected boolean isLimitRelationNestLevel(final int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // You can change relation creation range by changing this.
        return 1;
    }

    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setValueTypeFactory(TnValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }
}
