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
package org.seasar.dbflute.s2dao.extension;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.helper.beans.DfPropertyAccessor;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.dbflute.s2dao.rowcreator.impl.TnRelationRowCreationResource;
import org.seasar.dbflute.s2dao.rowcreator.impl.TnRelationRowCreatorImpl;

/**
 * @author jflute
 */
public class TnRelationRowCreatorExtension extends TnRelationRowCreatorImpl {

    // ===================================================================================
    //                                                                      Factory Method
    //                                                                      ==============
    public static TnRelationRowCreatorExtension createRelationRowCreator() {
        return new TnRelationRowCreatorExtension();
    }

    // ===================================================================================
    //                                                             Relation KeyValue Setup
    //                                                             =======================
    @Override
    protected void setupRelationKeyValue(TnRelationRowCreationResource res) {
        // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
        // setup of relation key is handled at all-value setup marked as '#RELKEY'
        // so only entity instance creation exists in this method
        // = = = = = = = = = =/
        final TnRelationPropertyType rpt = res.getRelationPropertyType();
        final TnBeanMetaData yourBmd = rpt.getYourBeanMetaData();
        final DBMeta dbmeta = findDBMeta(yourBmd.getBeanClass(), yourBmd.getTableName());
        if (!res.hasRowInstance()) {
            final Object row;
            if (dbmeta != null) {
                row = dbmeta.newEntity();
            } else {
                row = newRelationRow(rpt);
            }
            res.setRow(row);
        }
    }

    protected DBMeta findDBMeta(Class<?> rowType, String tableName) {
        return TnRowCreatorExtension.findCachedDBMeta(rowType, tableName);
    }

    // ===================================================================================
    //                                                             Relation AllValue Setup
    //                                                             =======================
    @Override
    protected void setupRelationAllValue(TnRelationRowCreationResource res) throws SQLException {
        final Map<String, TnPropertyMapping> propertyCacheElement = res.extractPropertyCacheElement();
        final Set<Entry<String, TnPropertyMapping>> entrySet = propertyCacheElement.entrySet();
        for (Entry<String, TnPropertyMapping> entry : entrySet) {
            final TnPropertyMapping pt = entry.getValue();
            res.setCurrentPropertyType(pt);
            if (!isValidRelationPerPropertyLoop(res)) { // always no way unless the method is overridden
                res.clearRowInstance();
                return;
            }
            setupRelationProperty(res);
        }
        if (!isValidRelationAfterPropertyLoop(res)) { // e.g. when all values are null
            res.clearRowInstance();
            return;
        }
        res.clearValidValueCount();
        if (res.hasNextRelationProperty() && (hasConditionBean(res) || res.hasNextRelationLevel())) {
            setupNextRelationRow(res);
        }
    }

    protected void setupRelationProperty(TnRelationRowCreationResource res) throws SQLException {
        final String columnName = res.buildRelationColumnName();
        // already created here, this is S2Dao logic 
        //if (!res.hasRowInstance()) {
        //    res.setRow(newRelationRow(res));
        //}
        registerRelationValue(res, columnName);
    }

    protected void registerRelationValue(TnRelationRowCreationResource res, String columnName) throws SQLException {
        final TnPropertyMapping mapping = res.getCurrentPropertyMapping();
        Object value = null;
        // TODO jflute
        System.out.println("===> " + res.containsRelKeyValueIfExists(columnName) + ", " + columnName);
        if (res.containsRelKeyValueIfExists(columnName)) { // #RELKEY
            // if this column is relation key, it gets the value from relation key values
            // for performance and avoiding twice getting same column value
            value = res.extractRelKeyValue(columnName);
        } else {
            final ValueType valueType = mapping.getValueType();
            final Map<String, Integer> selectIndexMap = res.getSelectIndexMap();
            if (selectIndexMap != null) {
                value = ResourceContext.getValue(res.getResultSet(), columnName, valueType, selectIndexMap);
            } else {
                value = valueType.getValue(res.getResultSet(), columnName);
            }
        }

        if (value != null) {
            res.incrementValidValueCount();
            final DBMeta dbmeta = findDBMeta(res.getRow());
            setValue(res, mapping, dbmeta, value);
        }
    }

    /**
     * @param row The instance of row. (NotNull)
     * @return The interface of DBMeta. (NullAllowed: If it's null, it means NotFound.)
     */
    protected DBMeta findDBMeta(Object row) {
        return TnRowCreatorExtension.findCachedDBMeta(row);
    }

    protected void setValue(TnRelationRowCreationResource res, TnPropertyMapping mapping, DBMeta dbmeta, Object value) {
        final ColumnInfo columnInfo = mapping.getEntityColumnInfo();
        if (columnInfo != null) {
            columnInfo.write((Entity) res.getRow(), value);
        } else {
            final DfPropertyAccessor accessor = mapping.getPropertyAccessor();
            accessor.setValue(res.getRow(), value);
        }
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextRelationRow(TnRelationRowCreationResource res) throws SQLException {
        final TnBeanMetaData nextBmd = res.getRelationBeanMetaData();
        final Object row = res.getRow();
        res.prepareNextRelationInfo();
        try {
            for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
                final TnRelationPropertyType nextRpt = nextBmd.getRelationPropertyType(i);
                setupNextRelationRowElement(res, row, nextRpt);
            }
        } finally {
            res.setRow(row);
            res.closeNextRelationInfo();
        }
    }

    protected void setupNextRelationRowElement(TnRelationRowCreationResource res, Object row,
            TnRelationPropertyType nextRpt) throws SQLException {
        if (nextRpt == null) {
            return;
        }
        res.clearRowInstance();
        res.setRelationPropertyType(nextRpt);
        // TODO jflute relkeys here?

        final String nextRelationNoSuffix = buildRelationNoSuffix(nextRpt);
        res.prepareNextSuffix(nextRelationNoSuffix);
        try {
            final Object relationRow = createRelationRow(res);
            if (relationRow != null) {
                nextRpt.getPropertyDesc().setValue(row, relationRow);
            }
        } finally {
            res.closeNextSuffix();
        }
    }

    // ===================================================================================
    //                                                                Property Cache Setup
    //                                                                ====================
    @Override
    protected void setupPropertyCache(TnRelationRowCreationResource res) throws SQLException {
        // - - - - - - - - - - - 
        // Recursive Call Point!
        // - - - - - - - - - - -
        res.initializePropertyCacheElement();

        // do only selected foreign property for performance if condition-bean exists
        if (hasConditionBean(res) && !hasSelectedForeignInfo(res)) {
            return;
        }

        // set up property cache about current bean meta data
        final TnBeanMetaData nextBmd = res.getRelationBeanMetaData();
        final List<TnPropertyType> ptList = nextBmd.getPropertyTypeList();
        for (TnPropertyType pt : ptList) { // already been filtered as target only
            res.setCurrentPropertyType(pt);
            setupPropertyCacheElement(res);
        }

        // set up next relation
        if (res.hasNextRelationProperty() && (hasConditionBean(res) || res.hasNextRelationLevel())) {
            res.prepareNextRelationInfo();
            try {
                setupNextPropertyCache(res, nextBmd);
            } finally {
                res.closeNextRelationInfo();
            }
        }
    }

    // -----------------------------------------------------
    //                                         Next Relation
    //                                         -------------
    protected void setupNextPropertyCache(TnRelationRowCreationResource res, TnBeanMetaData nextBmd)
            throws SQLException {
        for (int i = 0; i < nextBmd.getRelationPropertyTypeSize(); ++i) {
            final TnRelationPropertyType nextNextRpt = nextBmd.getRelationPropertyType(i);
            res.setRelationPropertyType(nextNextRpt);
            setupNextPropertyCacheElement(res, nextNextRpt);
        }
    }

    protected void setupNextPropertyCacheElement(TnRelationRowCreationResource res, TnRelationPropertyType nextNextRpt)
            throws SQLException {
        final String nextRelationNoSuffix = buildRelationNoSuffix(nextNextRpt);
        res.prepareNextSuffix(nextRelationNoSuffix);
        try {
            setupPropertyCache(res);// Recursive call!
        } finally {
            res.closeNextSuffix();
        }
    }

    // ===================================================================================
    //                                                                     Option Override
    //                                                                     ===============
    @Override
    protected boolean isCreateDeadLink() {
        return false;
    }

    @Override
    protected int getLimitRelationNestLevel() {
        // basically unused on DBFlute because only ConditionBean uses relation row,
        // and ConditionBean supports unlimited relation nest level
        // so this limit size is always used after hasConditionBean()
        return 2; // for Compatible (old parameter)
    }

    // ===================================================================================
    //                                                                       ConditionBean 
    //                                                                       =============
    protected boolean isConditionBeanSelectedRelation(TnRelationRowCreationResource res) {
        if (hasConditionBean(res)) {
            final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
            if (cb.getSqlClause().hasSelectedRelation(res.getRelationNoSuffix())) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasConditionBean(TnRelationRowCreationResource res) {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected boolean hasSelectedForeignInfo(TnRelationRowCreationResource res) {
        final ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb.getSqlClause().hasSelectedRelation(res.getRelationNoSuffix())) {
            return true;
        }
        return false;
    }
}
