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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSequenceMetaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/16 Saturday)
 */
public class DfSequenceExtractorOracle extends DfSequenceExtractorBase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceExtractorOracle.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceExtractorOracle(DataSource dataSource, List<String> allSchemaList) {
        super(dataSource, allSchemaList);
    }

    // ===================================================================================
    //                                                                        Sequence Map
    //                                                                        ============
    protected Map<String, DfSequenceMetaInfo> doGetSequenceMap() {
        _log.info("...Loading sequence informations");
        final Map<String, DfSequenceMetaInfo> resultMap = StringKeyMap.createAsFlexibleOrdered();
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String sql = buildMetaSelectSql();
        if (sql == null) {
            return DfCollectionUtil.emptyMap();
        }
        _log.info(sql);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("SEQUENCE_OWNER");
        columnList.add("SEQUENCE_NAME");
        columnList.add("MIN_VALUE");
        columnList.add("MAX_VALUE");
        columnList.add("INCREMENT_BY");
        final List<Map<String, String>> resultList = facade.selectStringList(sql, columnList);
        final StringBuilder logSb = new StringBuilder();
        logSb.append(ln()).append("[SEQUENCE]");
        for (Map<String, String> recordMap : resultList) {
            final DfSequenceMetaInfo info = new DfSequenceMetaInfo();
            final String sequenceSchema = recordMap.get("SEQUENCE_OWNER");
            info.setSequenceSchema(sequenceSchema);
            final String sequenceName = recordMap.get("SEQUENCE_NAME");
            info.setSequenceName(sequenceName);
            final String minValue = recordMap.get("MIN_VALUE");
            info.setMinimumValue(minValue != null ? new BigDecimal(minValue) : null);
            final String maxValue = recordMap.get("MAX_VALUE");
            info.setMaximumValue(maxValue != null ? new BigDecimal(maxValue) : null);
            final String incrementSize = recordMap.get("INCREMENT_BY");
            info.setIncrementSize(incrementSize != null ? Integer.valueOf(incrementSize) : null);
            final String key = buildSequenceMapKey(sequenceSchema, sequenceName);
            resultMap.put(key, info);
            logSb.append(ln()).append(" ").append(key).append(" = ").append(info.toString());
        }
        _log.info(logSb.toString());
        return resultMap;
    }

    protected String buildMetaSelectSql() {
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
            return null;
        }
        return "select * from ALL_SEQUENCES where SEQUENCE_OWNER in (" + schemaCondition + ")";
    }
}