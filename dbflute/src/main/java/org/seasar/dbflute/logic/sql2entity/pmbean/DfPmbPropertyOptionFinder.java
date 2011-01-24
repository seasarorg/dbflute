/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class DfPmbPropertyOptionFinder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _className;
    protected final String _propertyName;
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbPropertyOptionFinder(String className, String propertyName, Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _className = className;
        _propertyName = propertyName;
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                         Find Option
    //                                                                         ===========
    public String findPmbMetaDataPropertyOption(String className, String propertyName) {
        final DfPmbMetaData meta = _pmbMetaDataMap.get(_className);
        if (meta == null) {
            return null;
        }
        final Map<String, String> optionMap = meta.getPropertyNameOptionMap();
        return optionMap != null ? optionMap.get(propertyName) : null;
    }

    // ===================================================================================
    //                                                                         Option Util
    //                                                                         ===========
    public static List<String> splitOption(String option) {
        final String delimiter = "|";
        return DfStringUtil.splitList(option, delimiter);
    }
}
