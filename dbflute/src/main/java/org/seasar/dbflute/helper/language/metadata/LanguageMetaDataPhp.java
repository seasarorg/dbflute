package org.seasar.dbflute.helper.language.metadata;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class LanguageMetaDataPhp implements LanguageMetaData {

    public Map<String, Object> getJdbcToJavaNativeMap() {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("CHAR", "string");
        map.put("VARCHAR", "string");
        map.put("LONGVARCHAR", "string");
        map.put("NUMERIC", "integer");
        map.put("DECIMAL", "double");
        map.put("BIT", "integer");
        map.put("TINYINT", "integer");
        map.put("SMALLINT", "integer");
        map.put("INTEGER", "integer");
        map.put("BIGINT", "integer");
        map.put("REAL", "double");
        map.put("FLOAT", "double");
        map.put("DOUBLE", "double");
        map.put("DATE", "string");
        map.put("TIME", "string");
        map.put("TIMESTAMP", "string");
        return map;
    }

    public List<Object> getStringList() {
        return Arrays.asList(new Object[] { "string" });
    }

    public List<Object> getBooleanList() {
        return Arrays.asList(new Object[] { "bool?" });
    }

    public List<Object> getNumberList() {
        return Arrays.asList(new Object[] { "integer" });
    }

    public List<Object> getDateList() {
        return Arrays.asList(new Object[] { "string" });
    }

    public List<Object> getBinaryList() {
        return Arrays.asList(new Object[] { "byte[]" });
    }
}
