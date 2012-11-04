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
package org.seasar.dbflute.s2dao.rowcreator.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.extension.TnRelationRowCreatorExtension;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.dbflute.util.DfReflectionUtil;

/**
 * The implementation as S2Dao of creator of relation row. <br />
 * This class has sub-class extended by DBFlute.
 * <pre>
 * {@link TnRelationRowCreatorImpl} is close to S2Dao logic
 * {@link TnRelationRowCreatorExtension} has DBFlute logic
 * </pre>
 * DBFlute depended on S2Dao before 0.9.0. <br />
 * It saves these structure to be easy to know what DBFlute extends it.
 * However several S2Dao's logics are deleted as abstract methods.
 * @author modified by jflute (originated in S2Dao)
 */
public abstract class TnRelationRowCreatorImpl implements TnRelationRowCreator {

    // ===================================================================================
    //                                                                        Row Creation
    //                                                                        ============
    /**
     * {@inheritDoc}
     */
    public Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Map<String, String> selectColumnMap,
            Map<String, Object> relKeyValues, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache)
            throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        return createRelationRow(createResourceForRow(rs, rpt, selectColumnMap, relKeyValues, relationPropertyCache));
    }

    protected TnRelationRowCreationResource createResourceForRow(ResultSet rs, TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Object> relKeyValues,
            Map<String, Map<String, TnPropertyMapping>> relationPropertyCache) throws SQLException {
        // the resource class is already customized for DBFlute
        final TnRelationRowCreationResource res = new TnRelationRowCreationResource();
        res.setResultSet(rs);
        res.setRelationPropertyType(rpt);
        res.setSelectColumnMap(selectColumnMap);
        res.setRelKeyValues(relKeyValues);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix(""); // as default
        res.setRelationNoSuffix(buildRelationNoSuffix(rpt));
        res.setLimitRelationNestLevel(getLimitRelationNestLevel());
        res.setCurrentRelationNestLevel(1);// as Default
        res.setCreateDeadLink(isCreateDeadLink());
        res.setSelectIndexMap(ResourceContext.getSelectIndexMap());
        return res;
    }

    /**
     * @param res The resource of relation row creation. (NotNull)
     * @return Created relation row. (NullAllowed)
     * @throws SQLException
     */
    protected Object createRelationRow(TnRelationRowCreationResource res) throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        if (!res.hasPropertyCacheElement()) {
            return null;
        }
        setupRelationKeyValue(res);
        setupRelationAllValue(res);
        return res.getRow();
    }

    protected abstract void setupRelationKeyValue(TnRelationRowCreationResource res);

    protected abstract void setupRelationAllValue(TnRelationRowCreationResource res) throws SQLException;

    protected boolean isValidRelationPerPropertyLoop(TnRelationRowCreationResource res) throws SQLException {
        return true; // always true as default (this is for override)
    }

    protected boolean isValidRelationAfterPropertyLoop(TnRelationRowCreationResource res) throws SQLException {
        if (res.isCreateDeadLink()) {
            return true;
        }
        return res.hasValidValueCount();
    }

    // ===================================================================================
    //                                                             Property Cache Creation
    //                                                             =======================
    /**
     * {@inheritDoc}
     */
    public Map<String, Map<String, TnPropertyMapping>> createPropertyCache(Map<String, String> selectColumnMap,
            TnBeanMetaData bmd) throws SQLException {
        // - - - - - - - 
        // Entry Point!
        // - - - - - - -
        final Map<String, Map<String, TnPropertyMapping>> relationPropertyCache = newRelationPropertyCache();
        for (int i = 0; i < bmd.getRelationPropertyTypeSize(); ++i) {
            final TnRelationPropertyType rpt = bmd.getRelationPropertyType(i);
            final String baseSuffix = "";
            final String relationNoSuffix = buildRelationNoSuffix(rpt);
            final TnRelationRowCreationResource res = createResourceForPropertyCache(rpt, selectColumnMap,
                    relationPropertyCache, baseSuffix, relationNoSuffix, getLimitRelationNestLevel());
            if (rpt == null) {
                continue;
            }
            setupPropertyCache(res);
        }
        return relationPropertyCache;
    }

    protected Map<String, Map<String, TnPropertyMapping>> newRelationPropertyCache() {
        return StringKeyMap.createAsCaseInsensitive();
    }

    protected TnRelationRowCreationResource createResourceForPropertyCache(TnRelationPropertyType rpt,
            Map<String, String> selectColumnMap, Map<String, Map<String, TnPropertyMapping>> relationPropertyCache,
            String baseSuffix, String relationNoSuffix, int limitRelationNestLevel) throws SQLException {
        // the resource class is already customized for DBFlute
        final TnRelationRowCreationResource res = new TnRelationRowCreationResource();
        res.setRelationPropertyType(rpt);
        res.setSelectColumnMap(selectColumnMap);
        res.setRelationPropertyCache(relationPropertyCache);
        res.setBaseSuffix(baseSuffix);
        res.setRelationNoSuffix(relationNoSuffix);
        res.setLimitRelationNestLevel(limitRelationNestLevel);
        res.setCurrentRelationNestLevel(1); // as default
        res.setSelectIndexMap(ResourceContext.getSelectIndexMap());
        return res;
    }

    protected abstract void setupPropertyCache(TnRelationRowCreationResource res) throws SQLException;

    protected void setupPropertyCacheElement(TnRelationRowCreationResource res) throws SQLException {
        final String columnName = res.buildRelationColumnName();
        if (!res.containsSelectColumn(columnName)) {
            return;
        }
        res.savePropertyCacheElement();
    }

    // ===================================================================================
    //                                                                        Common Logic
    //                                                                        ============
    protected String buildRelationNoSuffix(TnRelationPropertyType rpt) {
        return "_" + rpt.getRelationNo();
    }

    protected Object newRelationRow(TnRelationPropertyType rpt) { // for non DBFlute entity
        return DfReflectionUtil.newInstance(rpt.getPropertyDesc().getPropertyType());
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    protected boolean isTargetRelation(TnRelationRowCreationResource res) throws SQLException {
        return true;
    }

    protected abstract boolean isCreateDeadLink();

    protected abstract int getLimitRelationNestLevel();
}
