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

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfPropHtmlPropertyLangElement {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The key of the property. (NotNull) */
    protected final String _propertyKey;

    /** The type of the language. e.g. ja, en (NotNull) */
    protected final String _langType;

    /** The value of the property. e.g. ja, en (NotNull) */
    protected final String _propertyValue;

    /** The comment of the property. (NotNull, EmptyAllowed) */
    protected final String _comment;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropHtmlPropertyLangElement(String propertyKey, String langType, String propertyValue, String comment) {
        _propertyKey = propertyKey;
        _langType = langType;
        _propertyValue = propertyValue;
        _comment = comment != null ? comment : ""; // empty string for velocity template
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProeprtyKey() {
        return _propertyKey;
    }

    public String getLangType() {
        return _langType;
    }

    public String getPropertyValue() {
        return _propertyValue;
    }

    public String getPropertyValueHtmlEncoded() {
        DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        return prop.resolveTextForSchemaHtml(_propertyValue);
    }

    public String getComment() {
        return _comment;
    }

    public String getCommentHtmlEncoded() {
        DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        final String resolved = prop.resolveTextForSchemaHtml(_comment);
        return resolved != null ? resolved : "&nbsp;";
    }
}
