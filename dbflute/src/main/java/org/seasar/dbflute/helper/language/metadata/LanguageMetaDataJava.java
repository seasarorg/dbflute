package org.seasar.dbflute.helper.language.metadata;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class LanguageMetaDataJava implements LanguageMetaData {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final Map<String, Object> DEFAULT_EMPTY_MAP = DfCollectionUtil.newLinkedHashMap();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<String> _stringList = newArrayList("String");
    protected final List<String> _numberList = newArrayList("Byte", "Short", "Integer", "Long", "Float", "Double",
            "BigDecimal", "BigInteger");
    protected final List<String> _dateList = newArrayList("Date", "Time", "Timestamp");
    protected final List<String> _booleanList = newArrayList("Boolean");
    protected final List<String> _binaryList = newArrayList("byte[]");

    protected <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        return DfCollectionUtil.newArrayList(elements);
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    public Map<String, Object> getJdbcToJavaNativeMap() {
        // Java's native map is defined at TypeMap
        // so this returns empty. (special handling)
        return DEFAULT_EMPTY_MAP;
    }

    // ===================================================================================
    //                                                                         Suffix List
    //                                                                         ===========
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
