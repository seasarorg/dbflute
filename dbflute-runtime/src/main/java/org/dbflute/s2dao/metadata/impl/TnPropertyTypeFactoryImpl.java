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

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.extension.jdbc.PropertyType;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;

/**
 * @author jflute
 */
public class TnPropertyTypeFactoryImpl extends TnAbstractPropertyTypeFactory {

    protected DBMeta _dbmeta;

    public TnPropertyTypeFactoryImpl(Class<?> beanClass, TnBeanAnnotationReader beanAnnotationReader,
            TnValueTypeFactory valueTypeFactory) {
        super(beanClass, beanAnnotationReader, valueTypeFactory);
        initializeResources();
    }

    protected void initializeResources() {
        if (isEntity()) {
            _dbmeta = findDBMeta();
        }
    }

    protected boolean isEntity() {
        return Entity.class.isAssignableFrom(beanClass);
    }

    protected boolean hasDBMeta() {
        return _dbmeta != null;
    }

    protected DBMeta findDBMeta() {
        try {
            final Entity entity = (Entity) beanClass.newInstance();
            return entity.getDBMeta();
        } catch (Exception e) {
            String msg = "beanClass.newInstance() threw the exception: beanClass=" + beanClass;
            throw new RuntimeException(msg, e);
        }
    }

    public PropertyType[] createBeanPropertyTypes(String tableName) {
        final List<PropertyType> list = new ArrayList<PropertyType>();
        final BeanDesc beanDesc = getBeanDesc();
        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
            final PropertyDesc pd = beanDesc.getPropertyDesc(i);

            // Read-only property is unnecessary!
            if (!pd.hasWriteMethod()) {
                continue;
            }

            // Relation property is unnecessary!
            if (isRelation(pd)) {
                continue;
            }

            final PropertyType pt = createPropertyType(pd);
            pt.setPrimaryKey(isPrimaryKey(pd));
            pt.setPersistent(isPersistent(pt));
            list.add(pt);
        }
        return list.toArray(new PropertyType[list.size()]);
    }

    @Override
    protected boolean isRelation(PropertyDesc propertyDesc) {
        final String propertyName = propertyDesc.getPropertyName();
        if (hasDBMeta() && (_dbmeta.hasForeign(propertyName) || _dbmeta.hasReferrer(propertyName))) {
            return true;
        }
        return hasRelationNoAnnotation(propertyDesc);
    }

    protected boolean hasRelationNoAnnotation(PropertyDesc propertyDesc) {
        return beanAnnotationReader.hasRelationNo(propertyDesc);
    }

    @Override
    protected boolean isPrimaryKey(PropertyDesc propertyDesc) {
        final String propertyName = propertyDesc.getPropertyName();
        if (hasDBMeta() && _dbmeta.hasPrimaryKey() && _dbmeta.hasColumn(propertyName)) {
            if (_dbmeta.findColumnInfo(propertyName).isPrimary()) {
                return true;
            }
        }
        return hasIdAnnotation(propertyDesc);
    }

    protected boolean hasIdAnnotation(PropertyDesc propertyDesc) {
        return beanAnnotationReader.getId(propertyDesc) != null;
    }

    @Override
    protected boolean isPersistent(PropertyType propertyType) {
        final String propertyName = propertyType.getPropertyName();
        final PropertyDesc propertyDesc = propertyType.getPropertyDesc();
        if ((hasDBMeta() && _dbmeta.hasColumn(propertyName)) || hasColumnAnnotation(propertyDesc)) {
            return true;
        }
        return false;
    }

    protected boolean hasColumnAnnotation(PropertyDesc propertyDesc) {
        return beanAnnotationReader.getColumnAnnotation(propertyDesc) != null;
    }
}
