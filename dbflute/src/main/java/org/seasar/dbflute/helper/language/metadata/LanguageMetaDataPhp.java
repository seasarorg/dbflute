package org.seasar.dbflute.helper.language.metadata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class LanguageMetaDataPhp implements LanguageMetaData {

    protected final List<String> _stringList = newArrayList("string");
    protected final List<String> _numberList = newArrayList("integer");
    protected final List<String> _dateList = newArrayList("string");
    protected final List<String> _booleanList = newArrayList("bool?");
    protected final List<String> _binaryList = newArrayList("byte[]");

    protected <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        return DfCollectionUtil.newArrayList(elements);
    }

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

    public List<String> getStringList() {
        return _stringList;
    }

    public List<String> getNumberList() {
        return _numberList;
    }

    public List<String> getDateList() {
        return _dateList;
    }

    public List<String> getBooleanList() {
        return _booleanList;
    }

    public List<String> getBinaryList() {
        return _binaryList;
    }
}
