/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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

    @SuppressWarnings("unchecked")
    protected <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        final Object obj = DfCollectionUtil.newArrayList(elements);
        return (List<ELEMENT>) obj; // to avoid the warning between JDK6 and JDK7
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
