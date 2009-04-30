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
package org.seasar.dbflute.dbway;

import java.util.HashMap;
import java.util.Map;

/**
 * The DB way of PostgreSQL.
 * @author jflute
 */
public class WayOfPostgreSQL implements DBWay {

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return null;
    }
    
    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return "23505".equals(sqlState);
    }
    
    // ===================================================================================
    //                                                                     ENUM Definition
    //                                                                     ===============
    public enum LikeSearchOperand implements ExtensionOperand {
        BASIC("like")
        , CASE_INSENSITIVE("ilike")
        , FULL_TEXT_SEARCH("@@")
        ;
        private static final Map<String, LikeSearchOperand> _codeValueMap = new HashMap<String, LikeSearchOperand>();
        static { for (LikeSearchOperand value : values()) { _codeValueMap.put(value.code().toLowerCase(), value); } }
        private String _code;
        private LikeSearchOperand(String code) { _code = code; }
        public String code() { return _code; }
        public static LikeSearchOperand codeOf(Object code) {
            if (code == null) { return null; } return _codeValueMap.get(code.toString().toLowerCase());
        }
        public String operand() {
            return _code;
        }
    }
}
