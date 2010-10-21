package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfWriteSqlBuilder {

    //====================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableName;
    protected Map<String, DfColumnMetaInfo> _columnMap;
    protected List<String> _columnNameList;
    protected List<String> _valueList;
    protected Map<String, Set<String>> _notFoundColumnMap;
    protected Map<String, String> _targetConvertColumnNameKeyToLowerMap;
    protected Map<String, String> _additionalDefaultColumnNameToLowerMap;
    protected Map<String, Map<String, String>> _convertValueMap;
    protected Map<String, String> _defaultValueMap;
    protected Map<String, String> _basicColumnValueMap;

    // ===================================================================================
    //                                                                           Build SQL
    //                                                                           =========
    public String buildSql() {
        final Map<String, String> columnValueMap = createBasicColumnValueMap();
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sbValues = new StringBuilder();
        for (String columnName : columnValueMap.keySet()) {
            sb.append(", ").append(columnName);
            sbValues.append(", ?");
        }
        sb.delete(0, ", ".length()).insert(0, "insert into " + _tableName + " (").append(")");
        sbValues.delete(0, ", ".length()).insert(0, " values(").append(")");
        sb.append(sbValues);
        return sb.toString();
    }

    public Map<String, Object> setupParameter() {
        return resolveColumnValueMap(createBasicColumnValueMap());
    }

    // ===================================================================================
    //                                                                           SQL Parts
    //                                                                           =========
    protected Map<String, String> createBasicColumnValueMap() {
        if (_basicColumnValueMap != null) {
            return _basicColumnValueMap;
        }
        _basicColumnValueMap = new LinkedHashMap<String, String>();
        int columnCount = -1;
        for (String columnName : _columnNameList) {
            columnCount++;
            if (!_columnMap.isEmpty() && !_columnMap.containsKey(columnName)) {
                if (hasDefaultValue(columnName)) {
                    continue;
                }
                Set<String> notFoundColumnSet = _notFoundColumnMap.get(_tableName);
                if (notFoundColumnSet == null) {
                    notFoundColumnSet = new LinkedHashSet<String>();
                    _notFoundColumnMap.put(_tableName, notFoundColumnSet);
                }
                notFoundColumnSet.add(columnName);
                continue;
            }
            final String value;
            try {
                if (columnCount < _valueList.size()) {
                    value = _valueList.get(columnCount);
                } else {
                    value = null;
                }
            } catch (java.lang.RuntimeException e) {
                throw new RuntimeException("valueList.get(columnCount) threw the exception: valueList=" + _valueList
                        + " columnCount=" + columnCount, e);
            }
            if (!_columnMap.isEmpty() && _columnMap.containsKey(columnName)) {
                String realDbName = _columnMap.get(columnName).getColumnName();
                _basicColumnValueMap.put(realDbName, value);
            } else {
                _basicColumnValueMap.put(columnName, value);
            }
        }
        return _basicColumnValueMap;
    }

    protected Map<String, Object> resolveColumnValueMap(Map<String, String> basicColumnValueMap) {
        final Map<String, Object> resolvedColumnValueMap = new LinkedHashMap<String, Object>();
        final Set<Entry<String, String>> entrySet = basicColumnValueMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            final String columnName = entry.getKey();
            final String plainValue = entry.getValue();
            Object resolvedValue = null;
            if (Srl.is_Null_or_Empty(plainValue) && hasDefaultValue(columnName)) {
                final String defaultValue = findDefaultValue(columnName);
                if (defaultValue.equalsIgnoreCase("sysdate")) {
                    final Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    resolvedValue = currentTimestamp;
                } else {
                    resolvedValue = defaultValue;
                }
            }
            if (Srl.is_NotNull_and_NotEmpty(plainValue) && hasConvertValue(columnName)) {
                final Map<String, String> convertValueMapping = findConvertValueMapping(columnName);
                final String mappingKey = plainValue.trim();
                if (convertValueMapping.containsKey(mappingKey)) {
                    resolvedValue = convertValueMapping.get(mappingKey);
                }
            }
            if (resolvedValue == null) {
                resolvedValue = plainValue;
            }
            resolvedColumnValueMap.put(columnName, resolvedValue);
        }
        return resolvedColumnValueMap;
    }

    // ===================================================================================
    //                                                                       Convert Value
    //                                                                       =============
    private boolean hasConvertValue(String columnName) {
        return findConvertValueMapping(columnName) != null;
    }

    private Map<String, String> findConvertValueMapping(String columnName) {
        if (!_convertValueMap.containsKey(columnName)) {
            if (_targetConvertColumnNameKeyToLowerMap.containsKey(columnName.toLowerCase())) {
                final String realColumnName = _targetConvertColumnNameKeyToLowerMap.get(columnName.toLowerCase());
                if (_convertValueMap.containsKey(realColumnName)) {
                    return _convertValueMap.get(realColumnName);
                }
            }
            return null;
        }
        return _convertValueMap.get(columnName);
    }

    // ===================================================================================
    //                                                                       Default Value
    //                                                                       =============
    private boolean hasDefaultValue(String columnName) {
        return findDefaultValue(columnName) != null;
    }

    private String findDefaultValue(String columnName) {
        if (!_defaultValueMap.containsKey(columnName)) {
            if (_additionalDefaultColumnNameToLowerMap.containsKey(columnName.toLowerCase())) {
                final String realColumnName = _additionalDefaultColumnNameToLowerMap.get(columnName.toLowerCase());
                if (_defaultValueMap.containsKey(realColumnName)) {
                    return _defaultValueMap.get(realColumnName);
                }
            }
            return null;
        }
        return _defaultValueMap.get(columnName);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Map<String, DfColumnMetaInfo> getColumnMap() {
        return _columnMap;
    }

    public void setColumnMap(Map<String, DfColumnMetaInfo> columnMap) {
        this._columnMap = columnMap;
    }

    public List<String> getColumnNameList() {
        return _columnNameList;
    }

    public void setColumnNameList(List<String> columnNameList) {
        this._columnNameList = columnNameList;
    }

    public Map<String, Set<String>> getNotFoundColumnMap() {
        return _notFoundColumnMap;
    }

    public void setNotFoundColumnMap(Map<String, Set<String>> notFoundColumnMap) {
        this._notFoundColumnMap = notFoundColumnMap;
    }

    public String getTableName() {
        return _tableName;
    }

    public void setTableName(String tableName) {
        this._tableName = tableName;
    }

    public List<String> getValueList() {
        return _valueList;
    }

    public void setValueList(List<String> valueList) {
        this._valueList = valueList;
    }

    public Map<String, String> getTargetConvertColumnNameKeyToLowerMap() {
        return _targetConvertColumnNameKeyToLowerMap;
    }

    public void setTargetConvertColumnNameKeyToLowerMap(Map<String, String> targetConvertColumnNameKeyToLowerMap) {
        this._targetConvertColumnNameKeyToLowerMap = targetConvertColumnNameKeyToLowerMap;
    }

    public Map<String, String> getAdditionalDefaultColumnNameToLowerMap() {
        return _additionalDefaultColumnNameToLowerMap;
    }

    public void setAdditionalDefaultColumnNameToLowerMap(Map<String, String> additionalDefaultColumnNameToLowerMap) {
        this._additionalDefaultColumnNameToLowerMap = additionalDefaultColumnNameToLowerMap;
    }

    public Map<String, Map<String, String>> getConvertValueMap() {
        return _convertValueMap;
    }

    public void setConvertValueMap(Map<String, Map<String, String>> convertValueMap) {
        this._convertValueMap = convertValueMap;
    }

    public Map<String, String> getDefaultValueMap() {
        return _defaultValueMap;
    }

    public void setDefaultValueMap(Map<String, String> defaultValueMap) {
        this._defaultValueMap = defaultValueMap;
    }
}
