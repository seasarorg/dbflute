package org.seasar.dbflute.helper.language.metadata;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public interface LanguageMetaData {

    /**
     * @return The map of 'JDBC to Java Native'. (NotNull)
     */
    public Map<String, Object> getJdbcToJavaNativeMap();

    /**
     * @return The list of string type. (NotNull)
     */
    public List<Object> getStringList();

    /**
     * @return The list of boolean type. (NotNull)
     */
    public List<Object> getBooleanList();

    /**
     * @return The list of number type. (NotNull)
     */
    public List<Object> getNumberList();

    /**
     * @return The list of date type. (NotNull)
     */
    public List<Object> getDateList();

    /**
     * @return The list of binary type. (NotNull)
     */
    public List<Object> getBinaryList();
}
