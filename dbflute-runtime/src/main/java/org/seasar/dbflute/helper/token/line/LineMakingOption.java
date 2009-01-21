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
package org.seasar.dbflute.helper.token.line;

/**
 * @author jflute
 */
public class LineMakingOption {

    protected String _delimiter;

    protected boolean _quoteByDoubleQuotation;

    protected boolean _trimSpace;

    public LineMakingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public LineMakingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public LineMakingOption quoteByDoubleQuotation() {
        _quoteByDoubleQuotation = true;
        return this;
    }

    public boolean isQuoteByDoubleQuotation() {
        return _quoteByDoubleQuotation;
    }

    public LineMakingOption trimSpace() {
        _trimSpace = true;
        return this;
    }

    public boolean isTrimSpace() {
        return _trimSpace;
    }
}