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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * @since 0.9.5.2 (2009/07/09 Thursday)
 */
public class DfSequenceHandlerOracle extends DfSequenceHandlerJdbc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceHandlerOracle.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceHandlerOracle(DataSource dataSource, String schema, List<String> allSchemaList) {
        super(dataSource, schema, allSchemaList);
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    @Override
    protected Integer selectNextVal(Statement st, String sequenceName) throws SQLException {
        ResultSet rs = null;
        try {
            rs = st.executeQuery("select " + sequenceName + ".nextval from dual");
            rs.next();
            return rs.getInt(1);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("ResultSet.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                        Sequence Map
    //                                                                        ============
    public Map<String, DfSequenceMetaInfo> getSequenceMap() {
        final Map<String, DfSequenceMetaInfo> resultMap = new LinkedHashMap<String, DfSequenceMetaInfo>();
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String schemaCondition;
        {
            final StringBuilder sb = new StringBuilder();
            for (String schema : _allSchemaList) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("'").append(schema).append("'");
            }
            schemaCondition = sb.toString();
        }
        final String sql = "select * from ALL_SEQUENCES where SEQUENCE_OWNER in (" + schemaCondition + ")";
        _log.info(sql);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("SEQUENCE_OWNER");
        columnList.add("SEQUENCE_NAME");
        columnList.add("MIN_VALUE");
        columnList.add("MAX_VALUE");
        columnList.add("INCREMENT_BY");
        final List<Map<String, String>> resultList = facade.selectStringList(sql, columnList);
        final DfSequenceMetaInfo info = new DfSequenceMetaInfo();
        final StringBuilder logSb = new StringBuilder();
        logSb.append(ln()).append("[SEQUENCE]");
        for (Map<String, String> recordMap : resultList) {
            final String sequenceOwner = recordMap.get("SEQUENCE_OWNER");
            info.setSequenceOwner(sequenceOwner);
            final String sequenceName = recordMap.get("SEQUENCE_NAME");
            info.setSequenceName(sequenceName);
            final String minValue = recordMap.get("MIN_VALUE");
            info.setMinValue(minValue != null ? new BigDecimal(minValue) : null);
            final String maxValue = recordMap.get("MAX_VALUE");
            info.setMaxValue(maxValue != null ? new BigDecimal(maxValue) : null);
            final String incrementSize = recordMap.get("INCREMENT_BY");
            info.setIncrementSize(incrementSize != null ? Integer.valueOf(incrementSize) : null);
            resultMap.put((sequenceOwner != null ? sequenceOwner + "." : "") + sequenceName, info);
            logSb.append(ln()).append(" ").append(info.toString());
        }
        _log.info(logSb.toString());
        return resultMap;
    }
}