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
public class FileMakingOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The encoding for the file. (Required) */
    protected String _encoding;

    /** The delimiter of data. (Required) */
    protected String _delimiter;

    /** The line separator for the file. (NotRequired) */
    protected String _lineSeparator;

    /** Does it quote values minimally? (NotRequired) */
    protected boolean _quoteMinimally;

    /** The header info of file-making. (NotRequired) */
    protected FileMakingHeaderInfo _headerInfo;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public FileMakingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public FileMakingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public FileMakingOption encodeAsUTF8() {
        _encoding = "UTF-8";
        return this;
    }

    public FileMakingOption encodeAsWindows31J() {
        _encoding = "Windows-31J";
        return this;
    }

    public FileMakingOption separateCrLf() {
        _lineSeparator = "\r\n";
        return this;
    }

    public FileMakingOption separateLf() {
        _lineSeparator = "\n";
        return this;
    }

    public FileMakingOption quoteMinimally() {
        _quoteMinimally = true;
        return this;
    }

    public FileMakingOption headerInfo(List<String> columnNameList) {
        if (columnNameList == null) {
            String msg = "The argument 'columnNameList' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        final FileMakingHeaderInfo headerInfo = new FileMakingHeaderInfo();
        headerInfo.acceptColumnNameList(columnNameList);
        _headerInfo = headerInfo;
        return this;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(String encoding) {
        _encoding = encoding;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public String getLineSeparator() {
        return _lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        _lineSeparator = lineSeparator;
    }

    public boolean isQuoteMinimally() {
        return _quoteMinimally;
    }

    public FileMakingHeaderInfo getFileMakingHeaderInfo() {
        return _headerInfo;
    }

    public void setFileMakingHeaderInfo(FileMakingHeaderInfo headerInfo) {
        _headerInfo = headerInfo;
    }
}