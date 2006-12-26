package org.seasar.dbflute.helper.datahandler.impl.internal;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DfInternalSqlBuilder {

    protected String _tableName;
    protected Map _columnMap;
    protected List<String> _columnNameList;
    protected List<String> _valueList;
    protected Map<String, Set<String>> _notFoundColumnMap;
    protected List<String> _appendDefaultColumnNameList;
    protected Map<String, String> _defaultValueMap;

    public Map getColumnMap() {
        return _columnMap;
    }

    public void setColumnMap(Map columnMap) {
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

    public List<String> getAppendDefaultSysdateList() {
        return _appendDefaultColumnNameList;
    }

    public Map<String, String> getDefaultValueMap() {
        return _defaultValueMap;
    }

    public void setDefaultValueMap(Map<String, String> defaultValueMap) {
        this._defaultValueMap = defaultValueMap;
    }

    public void setAppendDefaultSysdateList(List<String> appendDefaultSysdateList) {
        this._appendDefaultColumnNameList = appendDefaultSysdateList;
    }

    public DfInternalSqlBuildingResult buildSql() {
        final DfInternalSqlBuildingResult sqlBuildingResult = new DfInternalSqlBuildingResult();
        final Map<String, Object> columnValueMap = getColumnValueMap();
        final StringBuilder sb = new StringBuilder();
        final Set<String> columnNameSet = columnValueMap.keySet();
        for (String columnName : columnNameSet) {
            sb.append(", ").append(columnName);
        }
        sb.delete(0, ", ".length()).insert(0, "insert into " + _tableName + "(").append(")");
        sb.append(getValuesString(columnNameSet, columnValueMap, sqlBuildingResult));
        sqlBuildingResult.setSql(sb.toString());
        return sqlBuildingResult;
    }

    protected Map<String, Object> getColumnValueMap() {
        final Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();
        int columnCount = -1;
        for (String columnName : _columnNameList) {
            columnCount++;
            if (!_columnMap.isEmpty() && !_columnMap.containsKey(columnName)) {
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
            columnValueMap.put(columnName.toLowerCase(), value);
        }
        return columnValueMap;
    }

    protected String getValuesString(final Set<String> columnNameSet, Map<String, Object> columnValueMap,
            DfInternalSqlBuildingResult sqlBuildingResult) {
        final StringBuilder sbValues = new StringBuilder();
        for (String columnName : columnNameSet) {
            if (_appendDefaultColumnNameList.contains(columnName)) {
                sbValues.append(", ").append("?");
                final String defaultValue = getDefaultValue(columnName);
                if (defaultValue.equalsIgnoreCase("sysdate")) {
                    sqlBuildingResult.addBindParameters(new Timestamp(System.currentTimeMillis()));
                } else {
                    sqlBuildingResult.addBindParameters(defaultValue);
                }
            } else {
                final Object value = columnValueMap.get(columnName);
                if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
                    sbValues.append(", ").append("null");
                } else {
                    sbValues.append(", ?");
                    sqlBuildingResult.addBindParameters(value);
                }
            }
        }
        sbValues.delete(0, ", ".length()).insert(0, " values(").append(");");
        return sbValues.toString();
    }

    private String getDefaultValue(String columnName) {
        final Set<String> keySet = _defaultValueMap.keySet();
        for (String key : keySet) {
            if (key.toLowerCase().equals(columnName.toLowerCase())) {
                return _defaultValueMap.get(key);
            }
        }
        String msg = "defaultValueMap.get(columnName) returned null: ";
        throw new IllegalStateException(msg + "columnName=" + columnName + " defaultValueMap=" + _defaultValueMap);
    }

}
