/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.collection;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public interface DfMapListString {

    public static final String DEFAULT_MAP_MARK = "map:";

    public static final String DEFAULT_LIST_MARK = "list:";

    public static final String DEFAULT_DELIMITER = ";";

    public static final String DEFAULT_START_BRACE = "{";

    public static final String DEFAULT_END_BRACE = "}";

    public static final String DEFAULT_EQUAL = "=";

    // ==========================================================================================
    //                                                                                     Setter
    //                                                                                     ======
    /**
     * Set delimiter.
     * @param delimiter Delimiter.
     */
    public void setDelimiter(String delimiter);

    /**
     * Set start brace.
     * @param startBrace Start brace.
     */
    public void setStartBrace(String startBrace);

    /**
     * Set end brace.
     * @param endBrace End brace.
     */
    public void setEndBrace(String endBrace);

    // ==========================================================================================
    //                                                                                   Generate
    //                                                                                   ========
    /**
     * Generate map from map-string.
     * @param mapString Map-string (NotNull)
     * @return Generated map. (NotNull)
     */
    public Map<String, Object> generateMap(String mapString);

    /**
     * Generate map from list-string. {Implement}
     * @param listString List-string (NotNull)
     * @return Generated list. (NotNull)
     */
    public List<Object> generateList(String listString);
}