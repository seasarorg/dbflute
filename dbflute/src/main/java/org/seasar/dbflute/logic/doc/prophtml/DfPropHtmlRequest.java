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
package org.seasar.dbflute.logic.doc.prophtml;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfPropHtmlRequest {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String MASKING_VALUE = "********";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _requestName;
    protected final Map<String, DfPropHtmlFileAttribute> _fileAttributeMap = DfCollectionUtil.newLinkedHashMap();
    protected final Map<String, DfPropHtmlProperty> _propertyMap = DfCollectionUtil.newLinkedHashMap();
    protected final Set<String> _diffIgnoredKeySet = DfCollectionUtil.newLinkedHashSet();
    protected final Set<String> _maskedKeySet = DfCollectionUtil.newLinkedHashSet();
    protected final String _extendsPropRequest;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropHtmlRequest(String requestName, List<String> diffIgnoredKeyList, List<String> maskedKeyList,
            String extendsPropRequest) {
        _requestName = requestName;
        addDiffIgnoredKeyAll(diffIgnoredKeyList);
        addMaskedKeyAll(maskedKeyList);
        _extendsPropRequest = extendsPropRequest;
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

    public DfPropHtmlFileAttribute getFileAttribute(String envType, String langType) {
        return _fileAttributeMap.get(generateFileAttributeKey(envType, langType));
    }

    public List<DfPropHtmlFileAttribute> getFileAttributeList() {
        return DfCollectionUtil.newArrayList(_fileAttributeMap.values());
    }

    public void addFileAttribute(DfPropHtmlFileAttribute attribute) {
        final String envType = attribute.getEnvType();
        final String langType = attribute.getLangType();
        _fileAttributeMap.put(generateFileAttributeKey(envType, langType), attribute);
    }

    protected String generateFileAttributeKey(final String envType, final String langType) {
        return envType + ":" + langType;
    }

    public DfPropHtmlProperty getProperty(String propertyKey) {
        return _propertyMap.get(propertyKey);
    }

    public List<DfPropHtmlProperty> getPropertyList() {
        return DfCollectionUtil.newArrayList(_propertyMap.values());
    }

    public void addProperty(String propertyKey, String envType, String langType, String propertyValue, String comment,
            boolean override) {
        DfPropHtmlProperty property = _propertyMap.get(propertyKey);
        if (property == null) {
            property = new DfPropHtmlProperty(propertyKey);
            _propertyMap.put(propertyKey, property);
        }
        final String registeredValue;
        if (_maskedKeySet.contains(propertyKey)) { // maskedKeySet should be set before
            registeredValue = MASKING_VALUE; // masked here
        } else {
            registeredValue = propertyValue;
        }
        property.setPropertyValue(envType, langType, registeredValue, comment, override);
    }

    public Set<String> getDiffIgnoredKeySet() {
        return _diffIgnoredKeySet;
    }

    protected void addDiffIgnoredKeyAll(List<String> diffIgnoredKeyList) {
        _diffIgnoredKeySet.addAll(diffIgnoredKeyList);
    }

    public Set<String> getMaskedKeySet() {
        return _maskedKeySet;
    }

    protected void addMaskedKeyAll(List<String> maskedKeyList) {
        _maskedKeySet.addAll(maskedKeyList);
    }

    public String getExtendsPropRequest() {
        return _extendsPropRequest;
    }
}
