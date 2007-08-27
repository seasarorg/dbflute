package org.seasar.dbflute.helper.language.metadata;

import java.util.List;
import java.util.Map;

public interface LanguageMetaData {

    public Map<String, Object> getJdbcToJavaNativeMap();

    public List<Object> getStringList();

    public List<Object> getBooleanList();

    public List<Object> getNumberList();

    public List<Object> getDateList();

    public List<Object> getBinaryList();
}
