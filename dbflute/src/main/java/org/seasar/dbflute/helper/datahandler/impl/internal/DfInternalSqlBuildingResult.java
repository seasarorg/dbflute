package org.seasar.dbflute.helper.datahandler.impl.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DfInternalSqlBuildingResult {
    protected String sql;
    protected List<Object> bindParameters = new ArrayList<Object>();
    protected Map<String, Object> columnValueMap = new LinkedHashMap<String, Object>();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getBindParameters() {
        return bindParameters;
    }

    public void addBindParameters(Object bindParameter) {
        this.bindParameters.add(bindParameter);
    }

    public Map<String, Object> getColumnValueMap() {
        return columnValueMap;
    }

    public void addColumnValue(String columnName, Object value) {
        this.columnValueMap.put(columnName, value);
    }
}
