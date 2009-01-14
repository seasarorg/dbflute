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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbflute.helper.StringKeyMap;
import org.dbflute.s2dao.beans.TnBeanDesc;
import org.dbflute.s2dao.beans.TnPropertyDesc;
import org.dbflute.s2dao.beans.exception.TnPropertyNotFoundRuntimeException;
import org.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.dbflute.s2dao.identity.TnIdentifierGeneratorFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.TnModifiedPropertySupport;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.dbflute.s2dao.metadata.TnRelationPropertyTypeFactory;

/**
 * It draws upon S2Dao.
 * @author jflute
 */
public class TnBeanMetaDataImpl extends TnDtoMetaDataImpl implements TnBeanMetaData {

    private String tableName;
    private Map<String, TnPropertyType> columnNamePropertyTypeMap = StringKeyMap.createAsCaseInsensitiveConcurrent();
    private List<TnRelationPropertyType> relationPropertyTypes = new ArrayList<TnRelationPropertyType>();
    private TnPropertyType[] primaryKeys;
    private List<TnIdentifierGenerator> identifierGenerators = new ArrayList<TnIdentifierGenerator>();
    private Map<String, TnIdentifierGenerator> identifierGeneratorsByPropertyName = new HashMap<String, TnIdentifierGenerator>();
    private String versionNoPropertyName;
    private String timestampPropertyName;
    private TnModifiedPropertySupport modifiedPropertySupport;
    private TnRelationPropertyTypeFactory relationPropertyTypeFactory;

    public TnBeanMetaDataImpl() {
    }

    public void initialize() {
        TnBeanDesc beanDesc = TnBeanDescFactory.getBeanDesc(getBeanClass());
        setupTableName(beanDesc);
        setupProperty();
        setupPrimaryKey();
    }

    public String getTableName() {
        return tableName;
    }

    public TnPropertyType getVersionNoPropertyType() throws TnPropertyNotFoundRuntimeException {
        return getPropertyType(getVersionNoPropertyName());
    }

    public TnPropertyType getTimestampPropertyType() throws TnPropertyNotFoundRuntimeException {
        return getPropertyType(getTimestampPropertyName());
    }

    public String getVersionNoPropertyName() {
        return versionNoPropertyName;
    }

    public void setVersionNoPropertyName(String versionNoPropertyName) {
        this.versionNoPropertyName = versionNoPropertyName;
    }

    public String getTimestampPropertyName() {
        return timestampPropertyName;
    }

    public void setTimestampPropertyName(String timestampPropertyName) {
        this.timestampPropertyName = timestampPropertyName;
    }

    public TnPropertyType getPropertyTypeByColumnName(String columnName) {
        TnPropertyType propertyType = (TnPropertyType) columnNamePropertyTypeMap.get(columnName);
        if (propertyType == null) {
            String msg = "The column was not found in the table: table=" + tableName + " column=" + columnName;
            throw new IllegalStateException(msg);
        }
        return propertyType;
    }

    public TnPropertyType getPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return getPropertyTypeByColumnName(alias);
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg, t);
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        return rpt.getBeanMetaData().getPropertyTypeByColumnName(columnName);
    }

    public boolean hasPropertyTypeByColumnName(String columnName) {
        return columnNamePropertyTypeMap.get(columnName) != null;
    }

    public boolean hasPropertyTypeByAliasName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return true;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            return false;
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            return false;
        }
        if (relno >= getRelationPropertyTypeSize()) {
            return false;
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        return rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName);
    }

    public boolean hasVersionNoPropertyType() {
        return hasPropertyType(getVersionNoPropertyName());
    }

    public boolean hasTimestampPropertyType() {
        return hasPropertyType(getTimestampPropertyName());
    }

    public String convertFullColumnName(String alias) {
        if (hasPropertyTypeByColumnName(alias)) {
            return tableName + "." + alias;
        }
        int index = alias.lastIndexOf('_');
        if (index < 0) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        String columnName = alias.substring(0, index);
        String relnoStr = alias.substring(index + 1);
        int relno = -1;
        try {
            relno = Integer.parseInt(relnoStr);
        } catch (Throwable t) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg, t);
        }
        TnRelationPropertyType rpt = getRelationPropertyType(relno);
        if (!rpt.getBeanMetaData().hasPropertyTypeByColumnName(columnName)) {
            String msg = "The alias was not found in the table: table=" + tableName + " alias=" + alias;
            throw new IllegalStateException(msg);
        }
        return rpt.getPropertyName() + "." + columnName;
    }

    public int getRelationPropertyTypeSize() {
        return relationPropertyTypes.size();
    }

    public TnRelationPropertyType getRelationPropertyType(int index) {
        return (TnRelationPropertyType) relationPropertyTypes.get(index);
    }

    public TnRelationPropertyType getRelationPropertyType(String propertyName)
            throws TnPropertyNotFoundRuntimeException {

        for (int i = 0; i < getRelationPropertyTypeSize(); i++) {
            TnRelationPropertyType rpt = (TnRelationPropertyType) relationPropertyTypes.get(i);
            if (rpt != null && rpt.getPropertyName().equalsIgnoreCase(propertyName)) {
                return rpt;
            }
        }
        throw new TnPropertyNotFoundRuntimeException(getBeanClass(), propertyName);
    }

    protected void setupTableName(TnBeanDesc beanDesc) {
        String ta = beanAnnotationReader.getTableAnnotation();
        if (ta != null) {
            tableName = ta;
        } else {
            String msg = "The bean should have a table annotation: " + getBeanClass();
            throw new IllegalStateException(msg);
        }
    }

    protected void setupProperty() {
        TnPropertyType[] propertyTypes = propertyTypeFactory.createBeanPropertyTypes(tableName);
        for (int i = 0; i < propertyTypes.length; i++) {
            TnPropertyType pt = propertyTypes[i];
            addPropertyType(pt);
            columnNamePropertyTypeMap.put(pt.getColumnName(), pt);
        }

        TnRelationPropertyType[] relationPropertyTypes = relationPropertyTypeFactory.createRelationPropertyTypes();
        for (int i = 0; i < relationPropertyTypes.length; i++) {
            TnRelationPropertyType rpt = relationPropertyTypes[i];
            addRelationPropertyType(rpt);
        }
    }

    protected void setupPrimaryKey() {
        List<TnPropertyType> keys = new ArrayList<TnPropertyType>();
        Set<String> keySet = propertyTypeMap.keySet();
        for (String key : keySet) {
            TnPropertyType pt = propertyTypeMap.get(key);
            if (pt.isPrimaryKey()) {
                keys.add(pt);
                setupIdentifierGenerator(pt);
            }
        }
        primaryKeys = (TnPropertyType[]) keys.toArray(new TnPropertyType[keys.size()]);
    }

    protected void setupIdentifierGenerator(TnPropertyType propertyType) {
        TnPropertyDesc pd = propertyType.getPropertyDesc();
        String propertyName = propertyType.getPropertyName();
        String idType = beanAnnotationReader.getId(pd);
        TnIdentifierGenerator generator = TnIdentifierGeneratorFactory.createIdentifierGenerator(propertyType, idType);
        identifierGenerators.add(generator);
        identifierGeneratorsByPropertyName.put(propertyName, generator);
    }

    protected void addRelationPropertyType(TnRelationPropertyType rpt) {
        for (int i = relationPropertyTypes.size(); i <= rpt.getRelationNo(); ++i) {
            relationPropertyTypes.add(null);
        }
        relationPropertyTypes.set(rpt.getRelationNo(), rpt);
    }

    public int getPrimaryKeySize() {
        return primaryKeys.length;
    }

    public String getPrimaryKey(int index) {
        return primaryKeys[index].getColumnName();
    }

    public int getIdentifierGeneratorSize() {
        return identifierGenerators.size();
    }

    public TnIdentifierGenerator getIdentifierGenerator(int index) {
        return (TnIdentifierGenerator) identifierGenerators.get(index);
    }

    public TnIdentifierGenerator getIdentifierGenerator(String propertyName) {
        return (TnIdentifierGenerator) identifierGeneratorsByPropertyName.get(propertyName);
    }

    public TnModifiedPropertySupport getModifiedPropertySupport() {
        return modifiedPropertySupport;
    }

    public void setModifiedPropertySupport(final TnModifiedPropertySupport propertyModifiedSupport) {
        this.modifiedPropertySupport = propertyModifiedSupport;
    }

    public Set<String> getModifiedPropertyNames(final Object bean) {
        return getModifiedPropertySupport().getModifiedPropertyNames(bean);
    }

    public void setRelationPropertyTypeFactory(TnRelationPropertyTypeFactory relationPropertyTypeFactory) {
        this.relationPropertyTypeFactory = relationPropertyTypeFactory;
    }
}
