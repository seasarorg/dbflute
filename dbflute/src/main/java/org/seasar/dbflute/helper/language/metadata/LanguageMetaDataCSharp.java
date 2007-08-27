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
        map.put("NUMERIC", "Nullables.NullableDecimal");
        map.put("DECIMAL", "Nullables.NullableDecimal");
        map.put("BIT", "Nullables.NullableBoolean");
        map.put("TINYINT", "Nullables.NullableDecimal");
        map.put("SMALLINT", "Nullables.NullableDecimal");
        map.put("INTEGER", "Nullables.NullableDecimal");
        map.put("BIGINT", "Nullables.NullableDecimal");
        map.put("REAL", "Nullables.NullableDecimal");
        map.put("FLOAT", "Nullables.NullableDecimal");
        map.put("DOUBLE", "Nullables.NullableDecimal");
        map.put("DATE", "Nullables.NullableDateTime");
        map.put("TIME", "Nullables.NullableDateTime");
        map.put("TIMESTAMP", "Nullables.NullableDateTime");
        return map;
    }

    public List<Object> getStringList() {
        return Arrays.asList(new Object[] { "String" });
    }

    public List<Object> getBooleanList() {
        return Arrays.asList(new Object[] { "Nullables.NullableBoolean" });
    }

    public List<Object> getNumberList() {
        return Arrays.asList(new Object[] { "Nullables.NullableDecimal" });
    }

    public List<Object> getDateList() {
        return Arrays.asList(new Object[] { "Nullables.NullableDateTime" });
    }

    public List<Object> getBinaryList() {
        return Arrays.asList(new Object[] { "byte[]" });
    }
}
