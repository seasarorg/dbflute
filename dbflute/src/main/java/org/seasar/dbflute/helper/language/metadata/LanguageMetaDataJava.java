/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
