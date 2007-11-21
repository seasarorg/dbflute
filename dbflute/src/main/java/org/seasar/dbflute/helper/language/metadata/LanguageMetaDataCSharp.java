package org.seasar.dbflute.helper.language.metadata;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class LanguageMetaDataCSharp implements LanguageMetaData {

    public Map<String, Object> getJdbcToJavaNativeMap() {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("CHAR", "String");
        map.put("VARCHAR", "String");
        map.put("LONGVARCHAR", "String");
        map.put("NUMERIC", "decimal?");
        map.put("DECIMAL", "decimal?");
        map.put("BIT", "bool?");
        map.put("TINYINT", "int?");
        map.put("SMALLINT", "int?");
        map.put("INTEGER", "int?");
        map.put("BIGINT", "long?");
        map.put("REAL", "decimal?");
        map.put("FLOAT", "decimal?");
        map.put("DOUBLE", "decimal?");
        map.put("DATE", "DateTime?");
        map.put("TIME", "DateTime?");
        map.put("TIMESTAMP", "DateTime?");
        return map;
    }

    public List<Object> getStringList() {
        return Arrays.asList(new Object[] { "String" });
    }

    public List<Object> getBooleanList() {
        return Arrays.asList(new Object[] { "bool?" });
    }

    public List<Object> getNumberList() {
        return Arrays.asList(new Object[] { "decimal?", "int?", "long?" });
    }

    public List<Object> getDateList() {
        return Arrays.asList(new Object[] { "DateTime?" });
    }

    public List<Object> getBinaryList() {
        return Arrays.asList(new Object[] { "byte[]" });
    }
}
