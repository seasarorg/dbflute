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
        map.put("NUMERIC", "Nullable<decimal>");
        map.put("DECIMAL", "Nullable<decimal>");
        map.put("BIT", "Nullable<bool>");
        map.put("TINYINT", "Nullable<int>");
        map.put("SMALLINT", "Nullable<int>");
        map.put("INTEGER", "Nullable<int>");
        map.put("BIGINT", "Nullable<long>");
        map.put("REAL", "Nullable<decimal>");
        map.put("FLOAT", "Nullable<decimal>");
        map.put("DOUBLE", "Nullable<decimal>");
        map.put("DATE", "Nullable<DateTime>");
        map.put("TIME", "Nullable<DateTime>");
        map.put("TIMESTAMP", "Nullable<DateTime>");
        return map;
    }

    public List<Object> getStringList() {
        return Arrays.asList(new Object[] { "String" });
    }

    public List<Object> getBooleanList() {
        return Arrays.asList(new Object[] { "Nullable<bool>" });
    }

    public List<Object> getNumberList() {
        return Arrays.asList(new Object[] { "Nullable<decimal>", "Nullable<int>", "Nullable<long>" });
    }

    public List<Object> getDateList() {
        return Arrays.asList(new Object[] { "Nullable<DateTime>" });
    }

    public List<Object> getBinaryList() {
        return Arrays.asList(new Object[] { "byte[]" });
    }
}
