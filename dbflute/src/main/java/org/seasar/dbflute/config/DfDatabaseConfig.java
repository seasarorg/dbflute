package org.seasar.dbflute.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.mapstring.DfMapListString;
import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

public class DfDatabaseConfig {

    protected String _databaseBaseInfo;

    public String getDatabaseBaseInfo() {
        return _databaseBaseInfo;
    }

    public void setDatabaseBaseInfo(String databaseBaseInfo) {
        _databaseBaseInfo = databaseBaseInfo;
    }

    public Map<String, Map<String, String>> analyzeDatabaseBaseInfo() {
        final DfMapListString mapListString = new DfMapListStringImpl();
        mapListString.setDelimiter(";");
        final Map<String, Object> map = mapListString.generateMap(_databaseBaseInfo);
        final Map<String, Map<String, String>> realMap = new LinkedHashMap<String, Map<String, String>>();
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            final Map elementMap = (Map) map.get(key);
            final Map<String, String> elementRealMap = new LinkedHashMap<String, String>();

            final Set elementKeySet = elementMap.keySet();
            for (Object elementKey : elementKeySet) {
                final Object elementValue = elementMap.get(elementKey);
                elementRealMap.put((String) elementKey, (String) elementValue);
            }
            realMap.put(key, elementRealMap);
        }
        return realMap;
    }

    // TODO: OgnlTest
    protected Map<String, Map<String, String>> _databaseBaseInfoOgnlTest;

    public Map<String, Map<String, String>> getDatabaseBaseInfoOgnlTest() {
        return _databaseBaseInfoOgnlTest;
    }

    public void setDatabaseBaseInfoOgnlTest(Map<String, Map<String, String>> databaseBaseInfoOgnlTest) {
        _databaseBaseInfoOgnlTest = databaseBaseInfoOgnlTest;
    }

}
