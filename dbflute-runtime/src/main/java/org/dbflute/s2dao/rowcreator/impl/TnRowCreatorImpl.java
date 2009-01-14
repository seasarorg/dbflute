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
package org.dbflute.s2dao.rowcreator.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dbflute.s2dao.beans.TnPropertyDesc;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.metadata.TnDtoMetaData;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.rowcreator.TnRowCreator;
import org.dbflute.util.DfStringUtil;
import org.seasar.extension.jdbc.ValueType;
import org.seasar.framework.util.ClassUtil;

/**
 * @author jflute
 */
public class TnRowCreatorImpl implements TnRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @param beanClass Bean class. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    public Object createRow(ResultSet rs, Map<String, TnPropertyType> propertyCache, Class<?> beanClass)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Object row = newBean(beanClass);
        final Set<String> columnNameSet = propertyCache.keySet();
        for (final Iterator<String> ite = columnNameSet.iterator(); ite.hasNext();) {
            final String columnName = ite.next();
            final TnPropertyType pt = (TnPropertyType) propertyCache.get(columnName);
            registerValue(rs, row, pt, columnName);
        }
        return row;
    }

    protected Object newBean(Class<?> beanClass) {
        return ClassUtil.newInstance(beanClass);
    }

    protected void registerValue(ResultSet rs, Object row, TnPropertyType pt, String name) throws SQLException {
        final ValueType valueType = pt.getValueType();
        final Object value = valueType.getValue(rs, name);
        final TnPropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(row, value);
    }

    // ===================================================================================
    //                                                             Property Cache Creation
    //                                                             =======================
    // -----------------------------------------------------
    //                                                  Bean
    //                                                  ----
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param beanMetaData Bean meta data. (NotNull)
     * @return The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    public Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames, TnBeanMetaData beanMetaData)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, TnPropertyType> proprertyCache = newPropertyCache();
        setupPropertyCache(proprertyCache, columnNames, beanMetaData);
        return proprertyCache;
    }

    protected void setupPropertyCache(Map<String, TnPropertyType> proprertyCache, Set<String> columnNames,
            TnBeanMetaData beanMetaData) throws SQLException {
        Map<String, TnPropertyType> propertyTypeMap = beanMetaData.getPropertyTypeMap();
        Set<String> keySet = propertyTypeMap.keySet();
        for (String key : keySet) {
            TnPropertyType pt = propertyTypeMap.get(key);
            if (!isTargetProperty(pt)) {
                continue;
            }
            setupPropertyCacheElement(proprertyCache, columnNames, pt);
        }
    }

    protected void setupPropertyCacheElement(Map<String, TnPropertyType> proprertyCache, Set<String> columnNames,
            TnPropertyType pt) throws SQLException {
        if (columnNames.contains(pt.getColumnName())) {
            proprertyCache.put(pt.getColumnName(), pt);
        } else if (columnNames.contains(pt.getPropertyName())) {
            proprertyCache.put(pt.getPropertyName(), pt);
        } else if (!pt.isPersistent()) {
            setupPropertyCacheNotPersistentElement(proprertyCache, columnNames, pt);
        }
    }

    protected void setupPropertyCacheNotPersistentElement(Map<String, TnPropertyType> proprertyCache,
            Set<String> columnNames, TnPropertyType pt) throws SQLException {
        for (Iterator<String> iter = columnNames.iterator(); iter.hasNext();) {
            String columnName = (String) iter.next();
            String columnName2 = DfStringUtil.replace(columnName, "_", "");
            if (columnName2.equalsIgnoreCase(pt.getColumnName())) {
                proprertyCache.put(columnName, pt);
                break;
            }
        }
    }

    // -----------------------------------------------------
    //                                                   Dto
    //                                                   ---
    /**
     * @param columnNames The set of column name. (NotNull)
     * @param dtoMetaData DTO meta data. (NotNull)
     * @return The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @throws SQLException
     */
    public Map<String, TnPropertyType> createPropertyCache(Set<String> columnNames, TnDtoMetaData dtoMetaData)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, TnPropertyType> proprertyCache = newPropertyCache();
        setupPropertyCache(proprertyCache, columnNames, dtoMetaData);
        return proprertyCache;
    }

    protected void setupPropertyCache(Map<String, TnPropertyType> proprertyCache, Set<String> columnNames,
            TnDtoMetaData dtoMetaData) throws SQLException {
        Map<String, TnPropertyType> propertyTypeMap = dtoMetaData.getPropertyTypeMap();
        Set<String> keySet = propertyTypeMap.keySet();
        for (String key : keySet) {
            TnPropertyType pt = propertyTypeMap.get(key);
            if (!isTargetProperty(pt)) {
                continue;
            }
            if (columnNames.contains(pt.getColumnName())) {
                proprertyCache.put(pt.getColumnName(), pt);
            } else if (columnNames.contains(pt.getPropertyName())) {
                proprertyCache.put(pt.getPropertyName(), pt);
            } else {
                String possibleName = DfStringUtil.fromPropertyNameToColumnName(pt.getPropertyName());
                if (columnNames.contains(possibleName)) {
                    proprertyCache.put(possibleName, pt);
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected Map<String, TnPropertyType> newPropertyCache() {
        return new HashMap<String, TnPropertyType>();
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetProperty(TnPropertyType pt) throws SQLException {
        // If the property is not writable, the property is out of target!
        return pt.getPropertyDesc().isWritable();
    }
}
