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
package org.seasar.dbflute.logic.jdbc.metadata.sequence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSequenceMetaInfo;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/16 Saturday)
 */
public class DfSequenceExtractorPostgreSQL extends DfSequenceExtractorBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceExtractorPostgreSQL.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceExtractorPostgreSQL(DataSource dataSource, List<String> allSchemaList) {
        super(dataSource, allSchemaList);
    }

    // ===================================================================================
    //                                                                        Sequence Map
    //                                                                        ============
    protected Map<String, DfSequenceMetaInfo> doGetSequenceMap() {
        _log.info("...Loading sequence informations");
        final Map<String, DfSequenceMetaInfo> resultMap = new LinkedHashMap<String, DfSequenceMetaInfo>();
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String schemaCondition;
        if (!_allSchemaList.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (String schema : _allSchemaList) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("'").append(schema).append("'");
            }
            schemaCondition = sb.toString();
        } else {
            schemaCondition = "public";
        }
        final String sql = "select * from information_schema.sequences where sequence_schema in (" + schemaCondition
                + ")";
        _log.info(sql);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("sequence_schema");
        columnList.add("sequence_name");
        columnList.add("minimum_value");
        columnList.add("maximum_value");
        columnList.add("increment");
        final List<Map<String, String>> resultList = facade.selectStringList(sql, columnList);
        final StringBuilder logSb = new StringBuilder();
        logSb.append(ln()).append("[SEQUENCE]");
        for (Map<String, String> recordMap : resultList) {
            final DfSequenceMetaInfo info = new DfSequenceMetaInfo();
            final String sequenceOwner = recordMap.get("sequence_schema");
            info.setSequenceOwner(sequenceOwner);
            final String sequenceName = recordMap.get("sequence_name");
            info.setSequenceName(sequenceName);
            final String minValue = recordMap.get("mininum_value");
            info.setMinValue(minValue != null ? new BigDecimal(minValue) : null);
            final String maxValue = recordMap.get("maxinum_value");
            info.setMaxValue(maxValue != null ? new BigDecimal(maxValue) : null);
            final String incrementSize = recordMap.get("increment");
            info.setIncrementSize(incrementSize != null ? Integer.valueOf(incrementSize) : null);
            final String keyOwner = sequenceOwner.equalsIgnoreCase("public") ? null : sequenceOwner;
            resultMap.put(buildSequenceMapKey(keyOwner, sequenceName), info);
            logSb.append(ln()).append(" ").append(info.toString());
        }
        _log.info(logSb.toString());
        return resultMap;
    }
}