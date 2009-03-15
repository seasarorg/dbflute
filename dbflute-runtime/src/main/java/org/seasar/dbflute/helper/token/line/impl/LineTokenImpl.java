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
package org.seasar.dbflute.helper.token.line.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.dbflute.helper.token.line.LineMakingOption;
import org.seasar.dbflute.helper.token.line.LineToken;
import org.seasar.dbflute.helper.token.line.LineTokenizingOption;

/**
 * @author jflute
 */
public class LineTokenImpl implements LineToken {

    // ===================================================================================
    //                                                                       Tokenize Line
    //                                                                       =============
    public List<String> tokenize(String lineString, LineTokenizingOption lineTokenizingOption) {
        final String delimiter = lineTokenizingOption.getDelimiter();
        final List<String> list = new ArrayList<String>();
        int i = 0;
        int j = lineString.indexOf(delimiter);
        for (int h = 0; j >= 0; h++) {
            final String pureValue = lineString.substring(i, j);
            if (lineTokenizingOption.isTrimDoubleQuotation()) {
                final String before = pureValue;
                if (before.length() > 1 && before.startsWith("\"") && before.endsWith("\"")) {
                    final String after = before.substring(1, before.length() - 1);
                    list.add(filterHandlingEmptyAsNull(after, lineTokenizingOption));
                } else {
                    list.add(filterHandlingEmptyAsNull(before, lineTokenizingOption));
                }
            } else {
                list.add(filterHandlingEmptyAsNull(pureValue, lineTokenizingOption));
            }
            i = j + delimiter.length();
            j = lineString.indexOf(delimiter, i);
        }
        String lastElement = lineString.substring(i);
        if (lineTokenizingOption.isTrimDoubleQuotation()) {
            if (lastElement.length() > 1 && lastElement.startsWith("\"") && lastElement.endsWith("\"")) {
                lastElement = lastElement.substring(1, lastElement.length() - 1);
            }
        }
        list.add(filterHandlingEmptyAsNull(lastElement, lineTokenizingOption));
        return list;
    }

    protected String filterHandlingEmptyAsNull(String target, LineTokenizingOption lineTokenizingOption) {
        if (target == null) {
            return null;
        }
        if (lineTokenizingOption.isHandleEmtpyAsNull() && "".equals(target)) {
            return null;
        }
        return target;
    }

    // ===================================================================================
    //                                                                           Make Line
    //                                                                           =========
    public String make(java.util.List<String> valueList, LineMakingOption lineMakingOption) {
        assertObjectNotNull("valueList", valueList);
        assertObjectNotNull("lineMakingOption", lineMakingOption);
        final String delimiter = lineMakingOption.getDelimiter();
        assertObjectNotNull("lineMakingOption.getDelimiter()", delimiter);
        return createLineString(valueList, delimiter, lineMakingOption.isQuoteByDoubleQuotation(), lineMakingOption
                .isTrimSpace());
    }

    protected String createLineString(List<String> valueList, String delimiter, boolean quoteByDoubleQuotation,
            boolean trimSpace) {
        final StringBuffer sb = new StringBuffer();
        for (final Iterator<String> ite = valueList.iterator(); ite.hasNext();) {
            String value = (String) ite.next();
            value = (value != null ? value : "");
            if (trimSpace) {
                value = value.trim();
            }
            if (quoteByDoubleQuotation) {
                sb.append(delimiter).append("\"").append(value).append("\"");
            } else {
                sb.append(delimiter).append(value);
            }
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull(variableName, value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }
}