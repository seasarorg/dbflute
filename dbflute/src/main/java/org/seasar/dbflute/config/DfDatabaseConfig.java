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
package org.seasar.dbflute.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.mapstring.DfMapListString;
import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

/**
 * @author jflute
 */
public class DfDatabaseConfig {

    // ===============================================================================
    //                                                                       Attribute
    //                                                                       =========
    protected String _databaseBaseInfo;
    {
        _databaseBaseInfo = "map:{"
                + "; derby      = map:{daoGenDbName = Derby      ; wildCard = % ; sequenceNextSql = Unsupported}"
                + "; h2         = map:{daoGenDbName = H2         ; wildCard = % ; sequenceNextSql = select next value for $$sequenceName$$}"
                + "; firebird   = map:{daoGenDbName = Firebird   ; wildCard = % ; sequenceNextSql = select gen_id($$sequenceName$$, 1) from RDB$DATABASE}"
                + "; oracle     = map:{daoGenDbName = Oracle     ; wildCard = % ; sequenceNextSql = select $$sequenceName$$.nextval from dual}"
                + "; mysql      = map:{daoGenDbName = MySql      ; wildCard = % ; sequenceNextSql = Unsupported}"
                + "; postgresql = map:{daoGenDbName = PostgreSql ; wildCard = % ; sequenceNextSql = select nextval ('$$sequenceName$$')}"
                + "; mssql      = map:{daoGenDbName = SqlServer  ; wildCard = % ; sequenceNextSql = Unsupported}"
                + "; db2        = map:{daoGenDbName = Db2        ; wildCard = % ; sequenceNextSql = values nextval for $$sequenceName$$}"
                + "; interbase  = map:{daoGenDbName = Interbase  ; wildCard = % ; sequenceNextSql = select gen_id($$sequenceName$$, 1) from RDB$DATABASE}"
                + "; default    = map:{daoGenDbName = Default    ; wildCard = % ; sequenceNextSql = Unsupported}" + "}";
    }

    // ===============================================================================
    //                                                                       Analyzing
    //                                                                       =========
    /**
     * Analyze database base-info.
     * @return Database base-info. (NotNull)
     */
    public Map<String, Map<String, String>> analyzeDatabaseBaseInfo() {
        final DfMapListString mapListString = new DfMapListStringImpl();
        mapListString.setDelimiter(";");
        final Map<String, Object> map = mapListString.generateMap(_databaseBaseInfo);
        final Map<String, Map<String, String>> realMap = new LinkedHashMap<String, Map<String, String>>();
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            final Map<?, ?> elementMap = (Map<?, ?>) map.get(key);
            final Map<String, String> elementRealMap = new LinkedHashMap<String, String>();
            final Set<?> elementKeySet = elementMap.keySet();
            for (Object elementKey : elementKeySet) {
                final Object elementValue = elementMap.get(elementKey);
                elementRealMap.put((String) elementKey, (String) elementValue);
            }
            realMap.put(key, elementRealMap);
        }
        return realMap;
    }

    // ===============================================================================
    //                                                                        Accessor
    //                                                                        ========
    public String getDatabaseBaseInfo() {
        return _databaseBaseInfo;
    }

    public void setDatabaseBaseInfo(String databaseBaseInfo) {
        _databaseBaseInfo = databaseBaseInfo;
    }
}
