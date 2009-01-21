/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.token.file;

/**
 * @author jflute
 */
public class FileMakingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected java.util.List<String> _valueList;

    protected java.util.LinkedHashMap<String, String> _nameValueMap;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public java.util.List<String> getValueList() {
        return _valueList;
    }

    /**
     * Set the list of value. {Priority One}
     * 
     * @param valueList The list of value. (NotNull and NotEmpty)
     */
    public void setValueList(java.util.List<String> valueList) {
        this._valueList = valueList;
    }

    public java.util.LinkedHashMap<String, String> getNameValueMap() {
        return _nameValueMap;
    }

    /**
     * Set the map of name and value. {Priority Two} <br />
     * If valueList is set, This nameValueMap is ignored.
     * 
     * @param nameValueMap The map of name and value. (NotNull and NotEmpty)
     */
    public void setNameValueMap(java.util.LinkedHashMap<String, String> nameValueMap) {
        this._nameValueMap = nameValueMap;
    }
}
