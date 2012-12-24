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
package org.seasar.dbflute.helper.token.file;

import java.util.List;

/**
 * @author jflute
 */
public class FileTokenizingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected FileTokenizingHeaderInfo _headerInfo;
    protected List<String> _valueList;
    protected String _rowString;
    protected int _rowNumber;
    protected int _lineNumber;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    /**
     * Get the header info of the token file.
     * @return The header info of the token file. (NotNull in callback)
     */
    public FileTokenizingHeaderInfo getHeaderInfo() {
        return _headerInfo;
    }

    public void setHeaderInfo(FileTokenizingHeaderInfo headerInfo) {
        _headerInfo = headerInfo;
    }

    /**
     * Get the list of value.
     * @return The list of value. (NotNull, NotEmpty)
     */
    public List<String> getValueList() {
        return _valueList;
    }

    public void setValueList(List<String> valueList) {
        _valueList = valueList;
    }

    public String getRowString() {
        return _rowString;
    }

    public void setRowString(String rowString) {
        _rowString = rowString;
    }

    public int getRowNumber() {
        return _rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        _rowNumber = rowNumber;
    }

    public int getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    @Deprecated
    public FileTokenizingHeaderInfo getFileTokenizingHeaderInfo() {
        return _headerInfo;
    }

    @Deprecated
    public void setFirstLineInfo(FileTokenizingHeaderInfo headerInfo) {
        _headerInfo = headerInfo;
    }
}
