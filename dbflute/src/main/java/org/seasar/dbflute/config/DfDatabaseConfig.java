package org.seasar.dbflute.config;

import java.util.Map;

public class DfDatabaseConfig {

    protected Map<String, Map<String, String>> _databaseBaseInfo;

    public Map<String, Map<String, String>> getDatabaseBaseInfo() {
        return _databaseBaseInfo;
    }

    public void setDatabaseBaseInfo(Map<String, Map<String, String>> databaseBaseInfo) {
        _databaseBaseInfo = databaseBaseInfo;
    }
}
