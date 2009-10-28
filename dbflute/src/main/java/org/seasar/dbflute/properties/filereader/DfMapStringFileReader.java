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
package org.seasar.dbflute.properties.filereader;

import java.util.Map;

import org.seasar.dbflute.infra.dfprop.DfPropFileReader;

/**
 * @author jflute
 */
public class DfMapStringFileReader {

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public Map<String, Object> readMap(String path) {
        return new DfPropFileReader().readMap(path);
    }

    public Map<String, String> readMapAsStringValue(String path) {
        return new DfPropFileReader().readMapAsStringValue(path);
    }

    public Map<String, java.util.List<String>> readMapAsListStringValue(String path) {
        return new DfPropFileReader().readMapAsListStringValue(path);
    }

    public Map<String, java.util.Map<String, String>> readMapAsMapValue(String path) {
        return new DfPropFileReader().readMapAsMapStringValue(path);
    }
}