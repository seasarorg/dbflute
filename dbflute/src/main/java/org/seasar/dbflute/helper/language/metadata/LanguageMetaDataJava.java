package org.seasar.dbflute.helper.language.metadata;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class LanguageMetaDataJava implements LanguageMetaData {

    protected static final Map<String, Object> DEFAULT_EMPTY_MAP = new LinkedHashMap<String, Object>();

    public Map<String, Object> getJdbcToJavaNativeMap() {
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        // JavaのNativeMapはTypeMapにてべたっと定義されているのでここでは空っぽ
        // (Javaだけ特別な扱いになっている)
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        return DEFAULT_EMPTY_MAP;
    }

    public List<Object> getStringList() {
        return Arrays.asList(new Object[] { "String" });
    }

    public List<Object> getBooleanList() {
        return Arrays.asList(new Object[] { "Boolean" });
    }

    public List<Object> getNumberList() {
        return Arrays.asList(new Object[] { "Byte", "Short", "Integer", "Long", "Float", "Double", "BigDecimal",
                "BigInteger" });
    }

    public List<Object> getDateList() {
        return Arrays.asList(new Object[] { "Date", "Time", "Timestamp" });
    }

    public List<Object> getBinaryList() {
        return Arrays.asList(new Object[] { "byte[]" });
    }
}
