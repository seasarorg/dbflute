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
package org.seasar.dbflute.logic.jdbc.metadata.procedure;

import java.util.Map;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/19 Friday)
 */
public interface DfProcedureSupplementExtractor {

    /**
     * Extract the map of overload info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @return The map of array info. (NotNull)
     */
    public Map<String, Integer> extractParameterOverloadInfoMap();

    /**
     * Extract the map of array info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @return The map of array info. (NotNull)
     */
    public Map<String, DfTypeArrayInfo> extractParameterArrayInfoMap();

    /**
     * Extract the map of struct info for procedure. <br />
     * (but it contains structs that are not defined as procedure parameters)
     * @return The map of array info. (NotNull)
     */
    public StringKeyMap<DfTypeStructInfo> extractStructInfoMap();

    /**
     * @param catalog The catalog for procedure. (NullAllowed)
     * @param procedureName The name of procedure. (NotNull)
     * @param parameterName The name of parameter. (NotNull)
     * @return The map key for parameter info. (NotNull)
     */
    public String generateParameterInfoMapKey(String catalog, String procedureName, String parameterName);
}
