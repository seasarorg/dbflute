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
package org.seasar.dbflute.logic.jdbc.metadata.various.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/20 Saturday)
 */
public class DfArrayExtractorOracle {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfArrayExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfArrayExtractorOracle(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public StringSet extractArrayTypeSet(UnifiedSchema unifiedSchema) {
        final List<Map<String, String>> resultList = selectArray(unifiedSchema);
        final StringSet arrayTypeSet = StringSet.createAsFlexibleOrdered();
        for (Map<String, String> map : resultList) {
            // filter because TYPE_NAME might have its schema prefix
            final String typeName = Srl.substringFirstRear(map.get("TYPE_NAME"), ".");
            arrayTypeSet.add(typeName);
        }
        return arrayTypeSet;
    }

    protected List<Map<String, String>> selectArray(UnifiedSchema unifiedSchema) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("TYPE_NAME");
        final String sql = buildArraySql(unifiedSchema);
        final List<Map<String, String>> resultList;
        try {
            _log.info(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because of assist info
            _log.info("Failed to select supplement info: " + continued.getMessage());
            return DfCollectionUtil.emptyList();
        }
        return resultList;
    }

    protected String buildArraySql(UnifiedSchema unifiedSchema) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from ALL_TYPES");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "'");
        sb.append(" and TYPECODE = 'COLLECTION'");
        sb.append(" order by TYPE_NAME");
        return sb.toString();
    }
}
