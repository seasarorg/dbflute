package org.seasar.dbflute.helper.datahandler.impl.internal;

import java.util.ArrayList;
import java.util.List;

public class DfInternalSqlBuildingResult {
    protected String sql;
    protected List<Object> bindParameters = new ArrayList<Object>();

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
}
