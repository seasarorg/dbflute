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
public class DfPropHtmlRequest {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _requestName;
    protected final List<DfPropHtmlFileAttribute> _fileAttributeList = DfCollectionUtil.newArrayList();
    protected final Map<String, String> _langFileMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, String> _envFileMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, DfPropHtmlProperty> _propertyMap = DfCollectionUtil.newLinkedHashMap();
    protected final List<String> _diffIgnoredKeyList = DfCollectionUtil.newArrayList();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropHtmlRequest(String requestName) {
        _requestName = requestName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getRequestName() {
        return _requestName;
    }

    public String getRequestLowerName() {
        return _requestName.toLowerCase();
    }

    public List<DfPropHtmlFileAttribute> getFileAttributeList() {
        return _fileAttributeList;
    }

    public void addFileAttribute(DfPropHtmlFileAttribute attribute) {
        _fileAttributeList.add(attribute);
    }

    public DfPropHtmlProperty getProperty(String propertyKey) {
        return _propertyMap.get(propertyKey);
    }

    public List<DfPropHtmlProperty> getPropertyList() {
        return DfCollectionUtil.newArrayList(_propertyMap.values());
    }

    public void addProperty(String propertyKey, String envType, String langType, String propertyValue, String comment) {
        DfPropHtmlProperty property = _propertyMap.get(propertyKey);
        if (property == null) {
            property = new DfPropHtmlProperty(propertyKey);
            _propertyMap.put(propertyKey, property);
        }
        property.setPropertyValue(envType, langType, propertyValue, comment);
    }

    public List<String> getDiffIgnoredKeyList() {
        return _diffIgnoredKeyList;
    }

    public void addDiffIgnoredKeyAll(List<String> diffIgnoredKeyList) {
        _diffIgnoredKeyList.addAll(diffIgnoredKeyList);
    }
}
