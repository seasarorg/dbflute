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
