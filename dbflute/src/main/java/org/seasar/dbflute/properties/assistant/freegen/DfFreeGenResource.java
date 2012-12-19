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
package org.seasar.dbflute.properties.assistant.freegen;

import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenRequest.DfFreeGenerateResourceType;

/**
 * @author jflute
 */
public class DfFreeGenResource {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfFreeGenerateResourceType _resourceType;
    protected final String _resourceFile;
    protected final String _encoding;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenResource(DfFreeGenerateResourceType resourceType, String resourceFile, String encoding) {
        _resourceType = resourceType;
        _resourceFile = resourceFile;
        _encoding = encoding;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isResourceTypeProp() {
        return DfFreeGenerateResourceType.PROP.equals(_resourceType);
    }

    public boolean isResourceTypeXls() {
        return DfFreeGenerateResourceType.XLS.equals(_resourceType);
    }

    public boolean isResourceTypeFilePath() {
        return DfFreeGenerateResourceType.FILE_PATH.equals(_resourceType);
    }

    public boolean isResourceTypeJsonKey() {
        return DfFreeGenerateResourceType.JSON_KEY.equals(_resourceType);
    }

    public boolean isResourceTypeSolr() {
        return DfFreeGenerateResourceType.SOLR.equals(_resourceType);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{resourceType=" + _resourceType + ", resourceFile=" + _resourceFile + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DfFreeGenerateResourceType getResourceType() {
        return _resourceType;
    }

    public String getResourceFile() {
        return _resourceFile;
    }

    public boolean hasEncoding() {
        return _encoding != null;
    }

    public String getEncoding() {
        return _encoding;
    }
}
