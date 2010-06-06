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
package org.seasar.dbflute.helper.mapstring;

import java.util.List;

/**
 * The builder of map-string.
 * @author jflute
 */
public interface ColumnValueMapString {

    String buildByDelimiter(String values, String delimiter);

    String buildFromList(List<String> valueList);

    public static class DifferentDelimiterCountException extends RuntimeException {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        protected List<String> _columnNameList;
        protected List<String> _valueList;

        public DifferentDelimiterCountException(String msg, List<String> columnNameList,
                java.util.List<String> valueList) {
            super(msg);
            _columnNameList = columnNameList;
            _valueList = valueList;
        }

        public java.util.List<String> getColumnNameList() {
            return _columnNameList;
        }

        public java.util.List<String> getValueList() {
            return _valueList;
        }
    }
}
