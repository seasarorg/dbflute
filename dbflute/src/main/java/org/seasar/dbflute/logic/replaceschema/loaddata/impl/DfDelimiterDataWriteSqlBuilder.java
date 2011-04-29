package org.seasar.dbflute.logic.replaceschema.loaddata.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfDelimiterDataRegistrationFailureException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMeta;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfDelimiterDataWriteSqlBuilder {

    //====================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableDbName;
    protected Map<String, DfColumnMeta> _columnMap;
    protected List<String> _columnNameList;
    protected List<String> _valueList;
    protected Map<String, Set<String>> _notFoundColumnMap;
    protected Map<String, Map<String, String>> _convertValueMap;
    protected Map<String, String> _defaultValueMap;
    protected Map<String, String> _basicColumnValueMap;
    protected Map<String, String> _allColumnConvertMap;

    // ===================================================================================
    //                                                                           Build SQL
    //                                                                           =========
    public String buildSql() {
        final Map<String, String> columnValueMap = createBasicColumnValueMap();
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sbValues = new StringBuilder();
        for (String columnDbName : columnValueMap.keySet()) {
            final String columnSqlName = quoteColumnNameIfNeeds(columnDbName);
            sb.append(", ").append(columnSqlName);
            sbValues.append(", ?");
        }
        final String tableSqlName = quoteTableNameIfNeeds(_tableDbName);
        sb.delete(0, ", ".length()).insert(0, "insert into " + tableSqlName + " (").append(")");
        sbValues.delete(0, ", ".length()).insert(0, " values(").append(")");
        sb.append(sbValues);
        return sb.toString();
    }

    public Map<String, Object> setupParameter() {
        return resolveColumnValueMap(createBasicColumnValueMap());
    }

    protected String quoteTableNameIfNeeds(String tableDbName) {
        final DfLittleAdjustmentProperties prop = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        return prop.quoteTableNameIfNeedsDirectUse(tableDbName);
    }

    protected String quoteColumnNameIfNeeds(String columnDbName) {
        final DfLittleAdjustmentProperties prop = DfBuildProperties.getInstance().getLittleAdjustmentProperties();
        return prop.quoteColumnNameIfNeedsDirectUse(columnDbName);
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
                Set<String> notFoundColumnSet = _notFoundColumnMap.get(_tableDbName);
                if (notFoundColumnSet == null) {
                    notFoundColumnSet = new LinkedHashSet<String>();
                    _notFoundColumnMap.put(_tableDbName, notFoundColumnSet);
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
            } catch (RuntimeException e) {
                String msg = "valueList.get(columnCount) threw the exception:";
                msg = msg + " tableName=" + _tableDbName + " columnNameList=" + _columnNameList;
                msg = msg + " valueList=" + _valueList + " columnCount=" + columnCount;
                throw new DfDelimiterDataRegistrationFailureException(msg, e);
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
        final Set<String> convertedSet = new HashSet<String>(1);
        for (Entry<String, String> entry : entrySet) {
            final String columnName = entry.getKey();
            final String plainValue = entry.getValue();
            Object resolvedValue = resolveConvertValue(columnName, plainValue, convertedSet);
            if (convertedSet.isEmpty()) { // if no convert
                resolvedValue = filterEmptyAsNull(resolvedValue); // treated as null if empty string
                resolvedValue = resolveDefaultValue(columnName, resolvedValue);
            } else {
                convertedSet.clear(); // recycle
            }
            resolvedColumnValueMap.put(columnName, resolvedValue);
        }
        return resolvedColumnValueMap;
    }

    protected String resolveConvertValue(String columnName, String plainValue, Set<String> convertedSet) {
        String filteredValue = plainValue;
        final Map<String, String> valueMapping = findConvertValueMapping(columnName);
        if (valueMapping == null || valueMapping.isEmpty()) {
            return filteredValue;
        }
        boolean converted = false;
        final String containMark = DfNameHintUtil.CONTAIN_MARK;
        for (Entry<String, String> entry : valueMapping.entrySet()) {
            final String before = entry.getKey();
            final String after = resolveVariable(entry.getValue());
            if (Srl.startsWithIgnoreCase(before, containMark)) {
                final String realBefore = resolveVariable(Srl.substringFirstRear(before, containMark));
                if (filteredValue != null && filteredValue.contains(realBefore)) {
                    filteredValue = Srl.replace(filteredValue, realBefore, (after != null ? after : ""));
                    converted = true;
                }
            } else {
                final String realBefore = resolveVariable(before);
                if (filteredValue != null && filteredValue.equals(realBefore)) {
                    filteredValue = after;
                    converted = true;
                } else if (filteredValue == null && realBefore == null) {
                    filteredValue = after;
                    converted = true;
                }
            }
        }
        if (converted) {
            convertedSet.add("converted");
        }
        return filteredValue;
    }

    protected String resolveVariable(String value) {
        if ("$$empty$$".equalsIgnoreCase(value)) {
            return "";
        }
        if ("$$null$$".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

    protected Object filterEmptyAsNull(Object value) {
        if (value instanceof String && Srl.isEmpty((String) value)) {
            return null;
        }
        return value;
    }

    protected Object resolveDefaultValue(String columnName, Object plainValue) {
        if (plainValue != null) {
            // empty string has already been resolved here
            return plainValue;
        }
        if (!hasDefaultValue(columnName)) {
            return plainValue;
        }
        Object resolvedValue = plainValue;
        final String defaultValue = findDefaultValue(columnName);
        if (Srl.is_Null_or_Empty(defaultValue)) {
            return null; // empty is treated as null
        }
        if (defaultValue.equalsIgnoreCase("sysdate")) {
            resolvedValue = new Timestamp(System.currentTimeMillis());
        } else {
            resolvedValue = defaultValue;
        }
        return resolvedValue;
    }

    // ===================================================================================
    //                                                                       Convert Value
    //                                                                       =============
    private Map<String, String> findConvertValueMapping(String columnName) {
        if (_allColumnConvertMap == null) { // initialize
            _allColumnConvertMap = _convertValueMap.get("$$ALL$$");
            if (_allColumnConvertMap == null) {
                _allColumnConvertMap = new HashMap<String, String>();
            }
        }
        // convertValueMap should be case insensitive (or flexible) map
        // (must be already resolved here)
        final Map<String, String> resultMap = _convertValueMap.get(columnName);
        if (resultMap != null && !resultMap.isEmpty()) {
            if (!_allColumnConvertMap.isEmpty()) {
                final Map<String, String> mergedMap = new HashMap<String, String>();
                mergedMap.putAll(_allColumnConvertMap);
                mergedMap.putAll(resultMap); // override if same value
                return mergedMap;
            } else {
                return resultMap;
            }
        } else {
            return !_allColumnConvertMap.isEmpty() ? _allColumnConvertMap : null;
        }
    }

    // ===================================================================================
    //                                                                       Default Value
    //                                                                       =============
    private boolean hasDefaultValue(String columnName) {
        return _defaultValueMap.containsKey(columnName);
    }

    private String findDefaultValue(String columnName) {
        // defaultValueMap should be case insensitive (or flexible) map
        // (must be already resolved here)
        return _defaultValueMap.get(columnName);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Map<String, DfColumnMeta> getColumnMap() {
        return _columnMap;
    }

    public void setColumnMap(Map<String, DfColumnMeta> columnMap) {
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

    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        this._tableDbName = tableDbName;
    }

    public List<String> getValueList() {
        return _valueList;
    }

    public void setValueList(List<String> valueList) {
        this._valueList = valueList;
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
