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
package org.seasar.dbflute.s2dao.metadata.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.exception.handler.SQLExceptionHandler;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.extension.TnBeanMetaDataFactoryExtension;
import org.seasar.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.dbflute.s2dao.metadata.TnModifiedPropertySupport;
import org.seasar.dbflute.s2dao.metadata.TnPropertyTypeFactory;
import org.seasar.dbflute.s2dao.metadata.TnPropertyTypeFactoryBuilder;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyTypeFactoryBuilder;

/**
 * The implementation as S2Dao of factory of bean meta data. <br />
 * This class has sub-class extended by DBFlute.
 * <pre>
 * {@link TnBeanMetaDataFactoryImpl} is close to S2Dao logic
 * {@link TnBeanMetaDataFactoryExtension} has DBFlute logic
 * </pre>
 * DBFlute depended on S2Dao before 0.9.0. <br />
 * It saves these structure to be easy to know what DBFlute extends it. <br />
 * (However this class already has several DBFlute logic...)
 * @author modified by jflute (originated in S2Dao)
 */
public class TnBeanMetaDataFactoryImpl implements TnBeanMetaDataFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The property name of modified property. (S2Dao's specification) */
    protected static final String MODIFIED_PROPERTY_PROPERTY_NAME = "modifiedPropertyNames";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;

    // ===================================================================================
    //                                                                            Creation
    //                                                                            ========
    // /= = = = = = = = = = = = = = = = = = = = = = = = = =
    // these methods are overridden at the extension class 
    // = = = = = = = = = =/
    public TnBeanMetaData createBeanMetaData(Class<?> beanClass) {
        return createBeanMetaData(beanClass, 0);
    }

    public TnBeanMetaData createBeanMetaData(Class<?> beanClass, int relationNestLevel) {
        if (beanClass == null) {
            throw new IllegalArgumentException("The argument 'beanClass' should not be null.");
        }
        Connection conn = null;
        try {
            conn = _dataSource.getConnection(); // for meta data
            final DatabaseMetaData metaData = conn.getMetaData();
            return createBeanMetaData(metaData, beanClass, relationNestLevel);
        } catch (SQLException e) {
            handleSQLException(e);
            return null; // unreachable
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            }
        }
    }

    protected void handleSQLException(SQLException e) {
        createSQLExceptionHandler().handleSQLException(e);
    }

    protected SQLExceptionHandler createSQLExceptionHandler() {
        return ResourceContext.createSQLExceptionHandler();
    }

    public TnBeanMetaData createBeanMetaData(DatabaseMetaData dbMetaData, Class<?> beanClass, int relationNestLevel) {
        if (dbMetaData == null) {
            throw new IllegalArgumentException("The argument 'dbMetaData' should not be null.");
        }
        if (beanClass == null) {
            throw new IllegalArgumentException("The argument 'beanClass' should not be null.");
        }
        final TnBeanMetaDataImpl bmd = createBeanMetaDataImpl(beanClass);
        final TnBeanAnnotationReader beanAnnotationReader = createBeanAnnotationReader(beanClass);
        final String versionNoPropertyName = getVersionNoPropertyName(beanAnnotationReader);
        final String timestampPropertyName = getTimestampPropertyName(beanAnnotationReader);
        bmd.setBeanAnnotationReader(beanAnnotationReader);
        bmd.setVersionNoPropertyName(versionNoPropertyName);
        bmd.setTimestampPropertyName(timestampPropertyName);
        bmd.setPropertyTypeFactory(createPropertyTypeFactory(beanClass, beanAnnotationReader, dbMetaData));

        final boolean stopRelationCreation = isLimitRelationNestLevel(relationNestLevel);
        bmd.setRelationPropertyTypeFactory(createRelationPropertyTypeFactory(beanClass, bmd, beanAnnotationReader,
                dbMetaData, relationNestLevel, stopRelationCreation));

        bmd.setModifiedPropertySupport(createModifiedPropertySupport());
        bmd.initialize();
        return bmd;
    }

    protected TnModifiedPropertySupport createModifiedPropertySupport() {
        return new TnModifiedPropertySupport() {
            @SuppressWarnings("unchecked")
            public Set<String> getModifiedPropertyNames(Object bean) {
                if (bean instanceof Entity) { // all entities of DBFlute are here
                    return ((Entity) bean).modifiedProperties();
                } else { // basically no way on DBFlute (S2Dao's route)
                    final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(bean.getClass());
                    final String propertyName = MODIFIED_PROPERTY_PROPERTY_NAME;
                    if (!beanDesc.hasPropertyDesc(propertyName)) {
                        return Collections.EMPTY_SET;
                    } else {
                        final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyName);
                        final Object value = propertyDesc.getValue(bean);
                        return (Set<String>) value;
                    }
                }
            }
        };
    }

    protected TnBeanMetaDataImpl createBeanMetaDataImpl(Class<?> beanClass) {
        // this is S2Dao's area so DBMeta is null
        return new TnBeanMetaDataImpl(beanClass, null);
    }

    // ===================================================================================
    //                                                                   Annotation Reader
    //                                                                   =================
    protected TnBeanAnnotationReader createBeanAnnotationReader(Class<?> beanClass) {
        return new TnFieldBeanAnnotationReader(beanClass);
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    protected String getVersionNoPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        final String defaultName = "versionNo"; // VERSION_NO is special name
        final String name = beanAnnotationReader.getVersionNoPropertyName();
        return name != null ? name : defaultName;
    }

    protected String getTimestampPropertyName(TnBeanAnnotationReader beanAnnotationReader) {
        return beanAnnotationReader.getTimestampPropertyName(); // has no default name
    }

    // ===================================================================================
    //                                                                       Property Type
    //                                                                       =============
    protected TnPropertyTypeFactory createPropertyTypeFactory(Class<?> beanClass,
            TnBeanAnnotationReader beanAnnotationReader, DatabaseMetaData databaseMetaData) {
        return createPropertyTypeFactoryBuilder().build(beanClass, beanAnnotationReader);
    }

    protected TnPropertyTypeFactoryBuilder createPropertyTypeFactoryBuilder() {
        return new TnPropertyTypeFactoryBuilderImpl();
    }

    protected TnRelationPropertyTypeFactory createRelationPropertyTypeFactory(Class<?> beanClass,
            TnBeanMetaDataImpl localBeanMetaData, TnBeanAnnotationReader beanAnnotationReader,
            DatabaseMetaData dbMetaData, int relationNestLevel, boolean stopRelationCreation) {
        final TnRelationPropertyTypeFactoryBuilder builder = createRelationPropertyTypeFactoryBuilder();
        return builder.build(beanClass, localBeanMetaData, beanAnnotationReader, dbMetaData, relationNestLevel,
                stopRelationCreation);
    }

    protected TnRelationPropertyTypeFactoryBuilder createRelationPropertyTypeFactoryBuilder() {
        final TnRelationPropertyTypeFactoryBuilderImpl impl = new TnRelationPropertyTypeFactoryBuilderImpl();
        impl.setBeanMetaDataFactory(this);
        return impl;
    }

    // ===================================================================================
    //                                                                 Relation Next Level
    //                                                                 ===================
    protected boolean isLimitRelationNestLevel(int relationNestLevel) {
        return relationNestLevel == getLimitRelationNestLevel();
    }

    protected int getLimitRelationNestLevel() {
        // you can change relation creation range by changing this
        // (this comment is for S2Dao, DBFlute overrides this)
        return 1;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
}
