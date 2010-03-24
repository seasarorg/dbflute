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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
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
     * {@inheritDoc}
     */
    public Map<String, TnPropertyType> createPropertyCache(Set<String> selectColumnSet, TnBeanMetaData beanMetaData)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, TnPropertyType> proprertyCache = newPropertyCache();
        setupPropertyCache(proprertyCache, selectColumnSet, beanMetaData);
        return proprertyCache;
    }

    protected void setupPropertyCache(Map<String, TnPropertyType> proprertyCache, Set<String> selectColumnSet,
            TnBeanMetaData beanMetaData) throws SQLException {
        final List<TnPropertyType> ptList = beanMetaData.getPropertyTypeList();
        for (TnPropertyType pt : ptList) {
            if (!isTargetProperty(pt)) {
                continue;
            }
            setupPropertyCacheElement(proprertyCache, selectColumnSet, pt);
        }
    }

    protected void setupPropertyCacheElement(Map<String, TnPropertyType> proprertyCache, Set<String> selectColumnSet,
            TnPropertyType pt) throws SQLException {
        if (selectColumnSet.contains(pt.getColumnName())) {
            proprertyCache.put(pt.getColumnName(), pt);
        } else if (selectColumnSet.contains(pt.getPropertyName())) {
            proprertyCache.put(pt.getPropertyName(), pt);
        } else if (!pt.isPersistent()) {
            // basically derived column properties defined at extended entity
            setupPropertyCacheNotPersistentElement(proprertyCache, selectColumnSet, pt);
        }
    }

    protected void setupPropertyCacheNotPersistentElement(Map<String, TnPropertyType> proprertyCache,
            Set<String> selectColumnSet, TnPropertyType pt) throws SQLException {
        for (String columnName : selectColumnSet) {
            String columnNameNotUnsco = DfStringUtil.replace(columnName, "_", "");
            if (columnNameNotUnsco.equalsIgnoreCase(pt.getColumnName())) {
                proprertyCache.put(columnName, pt);
                break;
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
