package org.seasar.dbflute.helper.io.data.impl.internal;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;

/**
 * @author jflute
 */
public class DfInternalSqlBuilder {

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

    // ===================================================================================
    //                                                                           Build SQL
    //                                                                           =========
    public DfInternalSqlBuildingResult buildSql() {
        final DfInternalSqlBuildingResult sqlBuildingResult = new DfInternalSqlBuildingResult();
        final Map<String, Object> columnValueMap = setupColumnValueMap();
        final StringBuilder sb = new StringBuilder();
        final Set<String> columnNameSet = columnValueMap.keySet();
        for (String columnName : columnNameSet) {
            sb.append(", ").append(columnName);
        }
        sb.delete(0, ", ".length()).insert(0, "insert into " + _tableName + " (").append(")");
        sb.append(setupValuesStringAndParameter(columnNameSet, columnValueMap, sqlBuildingResult));
        sqlBuildingResult.setSql(sb.toString());
        return sqlBuildingResult;
    }

    // ===================================================================================
    //                                                                    Set up SQL Parts
    //                                                                    ================
    protected Map<String, Object> setupColumnValueMap() {
        final Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
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
                columnValueMap.put(realDbName, value);
            } else {
                columnValueMap.put(columnName, value);
            }
        }
        return columnValueMap;
    }

    protected String setupValuesStringAndParameter(final Set<String> columnNameSet, Map<String, Object> columnValueMap,
            DfInternalSqlBuildingResult sqlBuildingResult) {
        final StringBuilder sbValues = new StringBuilder();
        for (String columnName : columnNameSet) {
            if (hasDefaultValue(columnName)) {
                final String defaultValue = findDefaultValue(columnName);
                sbValues.append(", ").append("?");
                if (defaultValue.equalsIgnoreCase("sysdate")) {
                    final Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    sqlBuildingResult.addColumnValue(columnName, currentTimestamp);
                } else {
                    sqlBuildingResult.addColumnValue(columnName, defaultValue);
                }
            } else {
                Object value = columnValueMap.get(columnName);
                if (hasConvertValue(columnName)) {
                    final Map<String, String> convertValueMapping = findConvertValueMapping(columnName);
                    value = (value != null && (value instanceof String)) ? ((String) value).trim() : value;
                    if (convertValueMapping.containsKey(value)) {
                        value = convertValueMapping.get(value);
                    }
                }
                sbValues.append(", ?");
                sqlBuildingResult.addColumnValue(columnName, value);
            }
        }
        sbValues.delete(0, ", ".length()).insert(0, " values(").append(")");
        return sbValues.toString();
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
