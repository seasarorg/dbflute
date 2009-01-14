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

import java.lang.reflect.Field;

import org.dbflute.s2dao.beans.TnBeanDesc;
import org.dbflute.s2dao.beans.TnPropertyDesc;
import org.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.dbflute.util.DfReflectionUtil;

public class TnFieldBeanAnnotationReader implements TnBeanAnnotationReader {

    public String TABLE = "TABLE";

    public String RELNO_SUFFIX = "_RELNO";

    public String RELKEYS_SUFFIX = "_RELKEYS";

    public String ID_SUFFIX = "_ID";

    public String NO_PERSISTENT_PROPS = "NO_PERSISTENT_PROPS";

    public String VERSION_NO_PROPERTY = "VERSION_NO_PROPERTY";

    public String TIMESTAMP_PROPERTY = "TIMESTAMP_PROPERTY";

    public String COLUMN_SUFFIX = "_COLUMN";

    public String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    private TnBeanDesc beanDesc;

    public TnFieldBeanAnnotationReader(Class<?> beanClass_) {
        this.beanDesc = TnBeanDescFactory.getBeanDesc(beanClass_);
    }

    public String getColumnAnnotation(TnPropertyDesc pd) {
        String propertyName = pd.getPropertyName();
        String columnNameKey = propertyName + COLUMN_SUFFIX;
        return getField(columnNameKey);
    }

    public String getTableAnnotation() {
        if (beanDesc.hasField(TABLE)) {
            Field field = beanDesc.getField(TABLE);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getVersionNoPropertyName() {
        if (beanDesc.hasField(VERSION_NO_PROPERTY)) {
            Field field = beanDesc.getField(VERSION_NO_PROPERTY);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getTimestampPropertyName() {
        if (beanDesc.hasField(TIMESTAMP_PROPERTY)) {
            Field field = beanDesc.getField(TIMESTAMP_PROPERTY);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }

    public String getId(TnPropertyDesc pd) {
        String id = getField(pd.getPropertyName() + ID_SUFFIX);
        if (id != null) {
            return id;
        }
        return getField(pd.getPropertyName() + ID_SUFFIX);
    }


    public String getRelationKey(TnPropertyDesc pd) {
        String propertyName = pd.getPropertyName();
        String relkeysKey = propertyName + RELKEYS_SUFFIX;
        return getField(relkeysKey);
    }

    public int getRelationNo(TnPropertyDesc pd) {
        String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        Field field = beanDesc.getField(relnoKey);
        return (Integer)DfReflectionUtil.getValue(field, null);
    }

    public boolean hasRelationNo(TnPropertyDesc pd) {
        String relnoKey = pd.getPropertyName() + RELNO_SUFFIX;
        return beanDesc.hasField(relnoKey);
    }

    public String getValueType(TnPropertyDesc pd) {
        String valueTypeKey = pd.getPropertyName() + VALUE_TYPE_SUFFIX;
        return getField(valueTypeKey);
    }

    private String getField(String key) {
        if (beanDesc.hasField(key)) {
            Field field = beanDesc.getField(key);
            return (String) DfReflectionUtil.getValue(field, null);
        }
        return null;
    }
}
