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
package org.seasar.dbflute.logic.doc.prophtml;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfPropHtmlPropertyEnvElement {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The key of the property. (NotNull) */
    protected final String _propertyKey;

    /** The type of the language. e.g. production, integration (NotNull) */
    protected final String _envType;

    /** The language element of the property. (NotNull) */
    protected final Map<String, DfPropHtmlPropertyLangElement> _langElementMap = DfCollectionUtil.newLinkedHashMap();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropHtmlPropertyEnvElement(String propertyKey, String envType) {
        _propertyKey = propertyKey;
        _envType = envType;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProeprtyKey() {
        return _propertyKey;
    }

    public String getEnvType() {
        return _envType;
    }

    public List<DfPropHtmlPropertyLangElement> getLangElementList() {
        return DfCollectionUtil.newArrayList(_langElementMap.values());
    }

    public void setPropertyValue(String langType, String propertyValue, String comment) {
        _langElementMap.put(langType, createLangElement(langType, propertyValue, comment));
    }

    protected DfPropHtmlPropertyLangElement createLangElement(String langType, String propertyValue, String comment) {
        return new DfPropHtmlPropertyLangElement(_propertyKey, langType, propertyValue, comment);
    }
}
