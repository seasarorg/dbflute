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
    Map<String, Object> getJdbcToJavaNativeMap();

    /**
     * @return The list of suffix for string native type. (NotNull)
     */
    List<String> getStringList();

    /**
     * @return The list of suffix for number native type. (NotNull)
     */
    List<String> getNumberList();

    /**
     * @return The list of suffix for date native type. (NotNull)
     */
    List<String> getDateList();

    /**
     * @return The list of suffix for boolean native type. (NotNull)
     */
    List<String> getBooleanList();

    /**
     * @return The list of suffix for binary native type. (NotNull)
     */
    List<String> getBinaryList();
}
