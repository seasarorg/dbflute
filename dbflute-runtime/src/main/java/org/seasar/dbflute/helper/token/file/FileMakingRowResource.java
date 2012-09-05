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
package org.seasar.dbflute.helper.token.file;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The row resource of file-making. <br />
 * You can set one record info to this resource
 * as list of string or map of string with header info. <br />
 * null resource or both null property means the end of data.
 * <pre>
 * fileToken.make(new FileOutputStream(tsvFile), new FileMakingCallback() {
 *     public FileMakingRowResource getRowResource() { // null or empty resource means end of data
 *         return new FileMakingRowResource().acceptValueListIterator(iterator); // data only here
 *         // or return new FileMakingRowResource().acceptNameValueMapIterator(iterator); // with header
 *     }
 * }, new FileMakingOption().delimitateByTab().encodeAsUTF8().headerInfo(columnNameList));
 * </pre>
 * @author jflute
 */
public class FileMakingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    // either required, both null means end of data
    protected List<String> _valueList;
    protected Map<String, String> _nameValueMap; // should be fixed-ordered

    // =====================================================================================
    //                                                                           Constructor
    //                                                                           ===========
    public FileMakingRowResource() {
    }

    // =====================================================================================
    //                                                                       Accept Iterator
    //                                                                       ===============
    /**
     * Accept the list of value as one record. {Priority One}
     * @param valueList The list of value. (NotNull, NotEmpty)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptValueList(List<String> valueList) {
        _valueList = valueList;
        return this;
    }

    /**
     * Accept the (fixed-ordered) map of name and value. {Priority Two} <br />
     * If valueList is set, this nameValueMap is ignored.
     * @param nameValueMap The map of name and value. (NotNull, NotEmpty)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptNameValueMap(Map<String, String> nameValueMap) {
        _nameValueMap = nameValueMap;
        return this;
    }

    /**
     * Accept the iterator for value list. {Priority One} <br />
     * If the iterator has the next element, set it to value list. (means next() called) <br />
     * No more element means end of data (no resource is treated as end of data by {@link FileToken}).
     * @param valueListIterator The iterator for value list. (NotNull)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptValueListIterator(Iterator<List<String>> valueListIterator) {
        if (valueListIterator.hasNext()) {
            _valueList = valueListIterator.next();
        }
        return this;
    }

    /**
     * Accept the iterator for name-value (fixed-ordered) map. {Priority Two} <br />
     * If the iterator has the next element, set it to name-value map. (means next() called) <br />
     * No more element means end of data (no resource is treated as end of data by {@link FileToken}). <br />
     * If valueList is set, this nameValueMap is ignored.
     * @param nameValueMapIterator The iterator for name-value map. (NotNull)
     * @return this. (NotNull)
     */
    public FileMakingRowResource acceptNameValueMapIterator(Iterator<Map<String, String>> nameValueMapIterator) {
        if (nameValueMapIterator.hasNext()) {
            _nameValueMap = nameValueMapIterator.next();
        }
        return this;
    }

    // =====================================================================================
    //                                                                       Resource Status
    //                                                                       ===============
    /**
     * Does it have either resource, value list of name-value map?
     * @return The determination, true or false.
     */
    public boolean hasResource() {
        return _valueList != null || _nameValueMap != null;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public List<String> getValueList() {
        return _valueList;
    }

    /**
     * Set the list of value. {Priority One}
     * @param valueList The list of value. (NotNull, NotEmpty)
     */
    public void setValueList(List<String> valueList) { // basic style (for compatible)
        this._valueList = valueList;
    }

    public Map<String, String> getNameValueMap() {
        return _nameValueMap;
    }

    /**
     * Set the (fixed-ordered) map of name and value. {Priority Two} <br />
     * If valueList is set, this nameValueMap is ignored.
     * @param nameValueMap The map of name and value. (NotNull, NotEmpty)
     */
    public void setNameValueMap(Map<String, String> nameValueMap) { // basic style (for compatible)
        this._nameValueMap = nameValueMap;
    }
}
