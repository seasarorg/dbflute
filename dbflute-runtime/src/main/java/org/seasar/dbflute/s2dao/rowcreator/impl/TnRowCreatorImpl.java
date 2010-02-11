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
package org.seasar.dbflute.s2dao.rowcreator.impl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnDtoMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.rowcreator.TnRowCreator;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnRowCreatorImpl implements TnRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    protected Object newBean(Class<?> beanClass) {
        return DfReflectionUtil.newInstance(beanClass);
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
        final List<TnPropertyType> ptList = beanMetaData.getPropertyTypeList();
        for (TnPropertyType pt : ptList) {
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
        final List<TnPropertyType> ptList = dtoMetaData.getPropertyTypeList();
        for (TnPropertyType pt : ptList) {
            if (!isTargetProperty(pt)) {
                continue;
            }
            if (columnNames.contains(pt.getColumnName())) {
                proprertyCache.put(pt.getColumnName(), pt);
            } else if (columnNames.contains(pt.getPropertyName())) {
                proprertyCache.put(pt.getPropertyName(), pt);
            } else {
                final String possibleName = DfStringUtil.decamelizePropertyName(pt.getPropertyName());
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
        return StringKeyMap.createAsCaseInsensitive();
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetProperty(TnPropertyType pt) throws SQLException {
        // If the property is not writable, the property is out of target!
        return pt.getPropertyDesc().isWritable();
    }
}
