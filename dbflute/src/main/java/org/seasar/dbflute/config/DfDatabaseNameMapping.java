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
import java.util.Map.Entry;

import org.seasar.dbflute.helper.mapstring.MapListString;
import org.seasar.dbflute.helper.mapstring.impl.MapListStringImpl;

/**
 * @author jflute
 */
public class DfDatabaseNameMapping {

    // ===============================================================================
    //                                                                       Attribute
    //                                                                       =========
    protected String _databaseNameMapping;
    {
        _databaseNameMapping = "map:{" + "; derby      = map:{dbName = Derby}" + "; h2         = map:{dbName = H2}"
                + "; firebird   = map:{dbName = Firebird}" + "; oracle     = map:{dbName = Oracle}"
                + "; mysql      = map:{dbName = MySql}" + "; postgresql = map:{dbName = PostgreSql}"
                + "; mssql      = map:{dbName = SqlServer}" + "; db2        = map:{dbName = Db2}"
                + "; interbase  = map:{dbName = Interbase}" + "; default    = map:{dbName = Default}" + "}";
    }

    // ===============================================================================
    //                                                                       Analyzing
    //                                                                       =========
    /**
     * Analyze database base-info.
     * @return Database base-info. (NotNull)
     */
    public Map<String, Map<String, String>> analyzeDatabaseBaseInfo() {
        final MapListString mapListString = new MapListStringImpl();
        mapListString.setDelimiter(";");
        final Map<String, Object> map = mapListString.generateMap(_databaseNameMapping);
        final Map<String, Map<String, String>> realMap = new LinkedHashMap<String, Map<String, String>>();
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            final String key = entry.getKey();
            final Map<?, ?> elementMap = (Map<?, ?>) entry.getValue();
            final Map<String, String> elementRealMap = new LinkedHashMap<String, String>();
            final Set<?> elementEntrySet = elementMap.entrySet();
            for (Object object : elementEntrySet) {
                @SuppressWarnings("unchecked")
                final Entry<Object, Object> elementEntry = (Entry<Object, Object>) object;
                final Object elementKey = elementEntry.getKey();
                final Object elementValue = elementEntry.getValue();
                elementRealMap.put((String) elementKey, (String) elementValue);
            }
            realMap.put(key, elementRealMap);
        }
        return realMap;
    }

    // ===============================================================================
    //                                                                        Accessor
    //                                                                        ========
    public String getDatabaseNameMapping() {
        return _databaseNameMapping;
    }

    public void setDatabaseNameMapping(String databaseNameMapping) {
        _databaseNameMapping = databaseNameMapping;
    }
}
