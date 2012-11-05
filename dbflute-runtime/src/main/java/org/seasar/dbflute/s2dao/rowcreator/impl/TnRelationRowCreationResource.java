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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.dbflute.s2dao.rshandler.TnRelationKey;
import org.seasar.dbflute.s2dao.rshandler.TnRelationRowCache;

/**
 * The resource for relation row creation. <br />
 * S2Dao logics are modified for DBFlute.
 * @author modified by jflute (originated in S2Dao)
 */
public class TnRelationRowCreationResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /** Result set. */
    protected ResultSet _resultSet;

    /** Relation row. Initialized at first or initialized after. */
    protected Object _row;

    /** Relation property type. */
    protected TnRelationPropertyType _relationPropertyType;

    /** The name map of select column. */
    protected Map<String, String> _selectColumnMap;

    /** The map of relation key values. The key is relation column name. */
    protected Map<String, Object> _relKeyValues;

    /** The map of relation property cache. (keys are relationNoSuffix, columnName) */
    protected Map<String, Map<String, TnPropertyMapping>> _relPropCache;

    /** The cache of relation row. */
    protected TnRelationRowCache _relRowCache;

    /** The suffix of base object. */
    protected String _baseSuffix;

    /** The suffix of relation no. */
    protected String _relationNoSuffix;

    /** The limit of relation nest level. */
    protected int _limitRelationNestLevel;

    /** The current relation nest level. Default is one. */
    protected int _currentRelationNestLevel;

    /** Current property mapping. This variable is temporary. */
    protected TnPropertyMapping _currentPropertyMapping;

    /** The count of valid value. */
    protected int _validValueCount;

    /** Does it create dead link? */
    protected boolean _createDeadLink;

    // -----------------------------------------------------
    //                                                Backup
    //                                                ------
    /** The backup of relation property type. The element type is {@link TnRelationPropertyType}. */
    protected Stack<TnRelationPropertyType> _relationPropertyTypeBackup;

    /** The backup of relation key values. The element type is Map. */
    protected Stack<Map<String, Object>> _relKeyValuesBackup;

    /** The backup of base suffix. The element type is String. */
    protected Stack<String> _baseSuffixBackup;

    /** The backup of base suffix. The element type is String. */
    protected Stack<String> _relationSuffixBackup;

    // -----------------------------------------------------
    //                                          Select Index
    //                                          ------------
    /** The map of select index. (NullAllowed) */
    protected Map<String, Integer> _selectIndexMap;

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    // -----------------------------------------------------
    //                                                   row
    //                                                   ---
    public boolean hasRowInstance() {
        return _row != null;
    }

    public void clearRowInstance() {
        _row = null;
    }

    // -----------------------------------------------------
    //                                  relationPropertyType
    //                                  --------------------
    public TnBeanMetaData getRelationBeanMetaData() {
        return _relationPropertyType.getYourBeanMetaData();
    }

    public boolean hasNextRelationProperty() {
        return getRelationBeanMetaData().getRelationPropertyTypeSize() > 0;
    }

    public void backupRelationPropertyType() {
        getRelationPropertyTypeBackup().push(getRelationPropertyType());
    }

    public void restoreRelationPropertyType() {
        setRelationPropertyType(getRelationPropertyTypeBackup().pop());
    }

    protected Stack<TnRelationPropertyType> getRelationPropertyTypeBackup() {
        if (_relationPropertyTypeBackup == null) {
            _relationPropertyTypeBackup = new Stack<TnRelationPropertyType>();
        }
        return _relationPropertyTypeBackup;
    }

    // -----------------------------------------------------
    //                                       selectColumnSet
    //                                       ---------------
    public boolean containsSelectColumn(String columnName) {
        return _selectColumnMap.containsKey(columnName);
    }

    // -----------------------------------------------------
    //                                          relKeyValues
    //                                          ------------
    public boolean existsRelKeyValues() {
        return _relKeyValues != null;
    }

    public boolean containsRelKeyValue(String key) {
        return _relKeyValues.containsKey(key);
    }

    public boolean containsRelKeyValueIfExists(String key) {
        return existsRelKeyValues() && _relKeyValues.containsKey(key);
    }

    public Object extractRelKeyValue(String key) {
        return _relKeyValues.get(key);
    }

    public void backupRelKeyValues() {
        getRelKeyValuesBackup().push(getRelKeyValues());
    }

    public void restoreRelKeyValues() {
        setRelKeyValues(getRelKeyValuesBackup().pop());
    }

    protected Stack<Map<String, Object>> getRelKeyValuesBackup() {
        if (_relKeyValuesBackup == null) {
            _relKeyValuesBackup = new Stack<Map<String, Object>>();
        }
        return _relKeyValuesBackup;
    }

    // -----------------------------------------------------
    //                                          relPropCache
    //                                          ------------
    // The type of relationPropertyCache is Map<String(relationNoSuffix), Map<String(columnName), PropertyType>>.
    public void initializePropertyCacheElement() {
        _relPropCache.put(_relationNoSuffix, new HashMap<String, TnPropertyMapping>());
    }

    public boolean hasPropertyCacheElement() {
        final Map<String, TnPropertyMapping> propertyCacheElement = extractPropertyCacheElement();
        return propertyCacheElement != null && !propertyCacheElement.isEmpty();
    }

    public Map<String, TnPropertyMapping> extractPropertyCacheElement() {
        return _relPropCache.get(_relationNoSuffix);
    }

    public void savePropertyCacheElement() {
        if (!hasPropertyCacheElement()) {
            initializePropertyCacheElement();
        }
        final Map<String, TnPropertyMapping> propertyCacheElement = extractPropertyCacheElement();
        final String columnName = buildRelationColumnName();
        if (propertyCacheElement.containsKey(columnName)) {
            return;
        }
        propertyCacheElement.put(columnName, _currentPropertyMapping);
    }

    // -----------------------------------------------------
    //                                           relRowCache
    //                                           -----------
    public TnRelationKey prepareRelationKey() throws SQLException {
        final TnRelationKey relKey = doCreateRelationKey();
        if (relKey != null) {
            setRelKeyValues(relKey.getRelKeyValues());
        }
        return relKey;
    }

    protected TnRelationKey doCreateRelationKey() throws SQLException {
        return _relRowCache.createRelationKey(_resultSet, _relationPropertyType, _selectColumnMap, _selectIndexMap,
                _relationNoSuffix);
    }

    // -----------------------------------------------------
    //                                                suffix
    //                                                ------
    public String buildRelationColumnName() {
        return _currentPropertyMapping.getColumnDbName() + _relationNoSuffix;
    }

    public void addRelationNoSuffix(String additionalRelationNoSuffix) {
        _relationNoSuffix = _relationNoSuffix + additionalRelationNoSuffix;
    }

    protected void backupBaseSuffix() {
        getBaseSuffixBackup().push(getBaseSuffix());
    }

    protected void restoreBaseSuffix() {
        setBaseSuffix(getBaseSuffixBackup().pop());
    }

    protected Stack<String> getBaseSuffixBackup() {
        if (_baseSuffixBackup == null) {
            _baseSuffixBackup = new Stack<String>();
        }
        return _baseSuffixBackup;
    }

    protected void backupRelationNoSuffix() {
        getRelationNoSuffixBackup().push(getRelationNoSuffix());
    }

    protected void restoreRelationNoSuffix() {
        setRelationNoSuffix(getRelationNoSuffixBackup().pop());
    }

    protected Stack<String> getRelationNoSuffixBackup() {
        if (_relationSuffixBackup == null) {
            _relationSuffixBackup = new Stack<String>();
        }
        return _relationSuffixBackup;
    }

    // -----------------------------------------------------
    //                                     relationNestLevel
    //                                     -----------------
    public boolean hasNextRelationLevel() {
        return _currentRelationNestLevel < _limitRelationNestLevel;
    }

    protected void incrementCurrentRelationNestLevel() {
        ++_currentRelationNestLevel;
    }

    protected void decrementCurrentRelationNestLevel() {
        --_currentRelationNestLevel;
    }

    // -----------------------------------------------------
    //                                       validValueCount
    //                                       ---------------
    public void incrementValidValueCount() {
        ++_validValueCount;
    }

    public void clearValidValueCount() {
        _validValueCount = 0;
    }

    public boolean hasValidValueCount() {
        return _validValueCount > 0;
    }

    // -----------------------------------------------------
    //                                                Backup
    //                                                ------
    public void prepareNextLevelMapping() {
        backupRelationPropertyType();
        backupRelKeyValues();
        incrementCurrentRelationNestLevel();
    }

    public void closeNextLevelMapping() {
        restoreRelationPropertyType();
        restoreRelKeyValues();
        decrementCurrentRelationNestLevel();
    }

    public void prepareNextRelationProperty(TnRelationPropertyType nextRpt) {
        backupBaseSuffix();
        backupRelationNoSuffix();
        clearRowInstance();
        setRelationPropertyType(nextRpt);
        setBaseSuffix(getRelationNoSuffix()); // current relation to base
        addRelationNoSuffix(nextRpt.getRelationNoSuffixPart());
    }

    public void closeNextRelationProperty() {
        restoreBaseSuffix();
        restoreRelationNoSuffix();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ResultSet getResultSet() {
        return _resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this._resultSet = resultSet;
    }

    public Object getRow() {
        return _row;
    }

    public void setRow(Object row) {
        this._row = row;
    }

    public TnRelationPropertyType getRelationPropertyType() {
        return _relationPropertyType;
    }

    public void setRelationPropertyType(TnRelationPropertyType rpt) {
        this._relationPropertyType = rpt;
    }

    public Map<String, String> getSelectColumnMap() {
        return _selectColumnMap;
    }

    public void setSelectColumnMap(Map<String, String> selectColumnMap) {
        this._selectColumnMap = selectColumnMap;
    }

    public Map<String, Integer> getSelectIndexMap() {
        return _selectIndexMap;
    }

    public void setSelectIndexMap(Map<String, Integer> selectIndexMap) {
        _selectIndexMap = selectIndexMap;
    }

    public Map<String, Object> getRelKeyValues() {
        return this._relKeyValues;
    }

    public void setRelKeyValues(Map<String, Object> relKeyValues) {
        this._relKeyValues = relKeyValues;
    }

    public Map<String, Map<String, TnPropertyMapping>> getRelPropCache() {
        return _relPropCache;
    }

    public void setRelPropCache(Map<String, Map<String, TnPropertyMapping>> relPropCache) {
        this._relPropCache = relPropCache;
    }

    public TnRelationRowCache getRelRowCache() {
        return _relRowCache;
    }

    public void setRelRowCache(TnRelationRowCache relRowCache) {
        this._relRowCache = relRowCache;
    }

    public String getBaseSuffix() {
        return _baseSuffix;
    }

    public void setBaseSuffix(String baseSuffix) {
        this._baseSuffix = baseSuffix;
    }

    public String getRelationNoSuffix() {
        return _relationNoSuffix;
    }

    public void setRelationNoSuffix(String relationNoSuffix) {
        this._relationNoSuffix = relationNoSuffix;
    }

    public int getLimitRelationNestLevel() {
        return _limitRelationNestLevel;
    }

    public void setLimitRelationNestLevel(int limitRelationNestLevel) {
        this._limitRelationNestLevel = limitRelationNestLevel;
    }

    public int getCurrentRelationNestLevel() {
        return _currentRelationNestLevel;
    }

    public void setCurrentRelationNestLevel(int currentRelationNestLevel) {
        this._currentRelationNestLevel = currentRelationNestLevel;
    }

    public TnPropertyMapping getCurrentPropertyMapping() {
        return _currentPropertyMapping;
    }

    public void setCurrentPropertyType(TnPropertyMapping propertyType) {
        this._currentPropertyMapping = propertyType;
    }

    public boolean isCreateDeadLink() {
        return _createDeadLink;
    }

    public void setCreateDeadLink(boolean createDeadLink) {
        this._createDeadLink = createDeadLink;
    }
}
