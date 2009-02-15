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

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.s2dao.beans.TnBeanDesc;
import org.seasar.dbflute.s2dao.beans.TnPropertyDesc;
import org.seasar.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanAnnotationReader;
import org.seasar.dbflute.util.DfReflectionUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnDBMetaBeanAnnotationReader implements TnBeanAnnotationReader {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final boolean simpleType;
    protected final TnFieldBeanAnnotationReader fieldBeanAnnotationReader;
    protected final DBMeta dbmeta;
    protected final TnBeanDesc beanDesc;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDBMetaBeanAnnotationReader(Class<?> beanClass) {
        simpleType = isSimpleType(beanClass);
        if (simpleType) {
            dbmeta = null;
            beanDesc = null;
            fieldBeanAnnotationReader = null;
            return;
        }
        if (!Entity.class.isAssignableFrom(beanClass)) {
            fieldBeanAnnotationReader = new TnFieldBeanAnnotationReader(beanClass);
            dbmeta = null;
            beanDesc = null;
        } else {
            fieldBeanAnnotationReader = null;
            this.dbmeta = ((Entity) DfReflectionUtil.newInstance(beanClass)).getDBMeta();
            this.beanDesc = TnBeanDescFactory.getBeanDesc(beanClass);
        }
    }

    protected boolean isSimpleType(Class<?> clazz) {
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * {@inheritDoc}}
     * @param pd The description of property. (NotNull)
     * @return The DB name of the column. (Nullable)
     */
    public String getColumnAnnotation(TnPropertyDesc pd) {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getColumnAnnotation(pd);
        }
        if (!dbmeta.hasColumn(pd.getPropertyName())) {
            return null;
        }
        return dbmeta.findColumnInfo(pd.getPropertyName()).getColumnDbName();
    }

    /**
     * {@inheritDoc}}
     * @return The DB name of the table. (Nullable) 
     */
    public String getTableAnnotation() {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getTableAnnotation();
        }
        return dbmeta.getTableDbName();
    }

    public String getVersionNoPropertyName() {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getVersionNoPropertyName();
        }
        if (!dbmeta.hasVersionNo()) {
            return null;
        }
        return dbmeta.getVersionNoColumnInfo().getPropertyName();
    }

    public String getTimestampPropertyName() {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getTimestampPropertyName();
        }
        if (!dbmeta.hasUpdateDate()) {
            return null;
        }
        return dbmeta.getUpdateDateColumnInfo().getPropertyName();
    }

    public String getId(TnPropertyDesc pd) {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getId(pd);
        }
        if (!dbmeta.hasColumn(pd.getPropertyName())) {
            return null;
        }
        ColumnInfo columnInfo = dbmeta.findColumnInfo(pd.getPropertyName());

        // Identity only here because Sequence is handled by an other component.
        if (dbmeta.hasIdentity() && columnInfo.isAutoIncrement()) {
            return "identity";
        }
        return null;
    }

    public String getRelationKey(TnPropertyDesc pd) {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getRelationKey(pd);
        }
        if (!dbmeta.hasForeign(pd.getPropertyName())) {
            return null;
        }
        ForeignInfo foreignInfo = dbmeta.findForeignInfo(pd.getPropertyName());
        Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap = foreignInfo.getLocalForeignColumnInfoMap();
        Set<ColumnInfo> keySet = localForeignColumnInfoMap.keySet();
        StringBuilder sb = new StringBuilder();
        for (ColumnInfo localColumnInfo : keySet) {
            ColumnInfo foreignColumnInfo = localForeignColumnInfoMap.get(localColumnInfo);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(localColumnInfo.getColumnDbName());
            sb.append(":").append(foreignColumnInfo.getColumnDbName());
        }
        return sb.toString();
    }

    public int getRelationNo(TnPropertyDesc pd) {
        if (simpleType) {
            return 0;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getRelationNo(pd);
        }
        ForeignInfo foreignInfo = dbmeta.findForeignInfo(pd.getPropertyName());
        return foreignInfo.getRelationNo();
    }

    public boolean hasRelationNo(TnPropertyDesc pd) {
        if (simpleType) {
            return false;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.hasRelationNo(pd);
        }
        return dbmeta.hasForeign(pd.getPropertyName());
    }

    public String getValueType(TnPropertyDesc pd) {
        if (simpleType) {
            return null;
        }
        if (fieldBeanAnnotationReader != null) {
            return fieldBeanAnnotationReader.getValueType(pd);
        }

        // ValueType is for user customization so this should not be handled by DBMeta.
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
