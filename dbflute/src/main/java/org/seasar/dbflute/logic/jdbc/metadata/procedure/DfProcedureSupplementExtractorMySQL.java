/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureSourceInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.7F (2012/08/21 Tuesday)
 */
public class DfProcedureSupplementExtractorMySQL implements DfProcedureSupplementExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureSupplementExtractorMySQL.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final DataSource _dataSource;

    // -----------------------------------------------------
    //                                       ResultMap Cache
    //                                       ---------------
    protected final Map<UnifiedSchema, Map<String, DfProcedureSourceInfo>> _procedureSourceMapMap = newHashMap();

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    protected boolean _suppressLogging;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureSupplementExtractorMySQL(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                   No Implementation
    //                                                                   =================
    public Map<String, Integer> extractParameterOverloadInfoMap(UnifiedSchema unifiedSchema) {
        return DfCollectionUtil.emptyMap();
    }

    public Map<String, DfTypeArrayInfo> extractParameterArrayInfoMap(UnifiedSchema unifiedSchema) {
        return DfCollectionUtil.emptyMap();
    }

    public Map<String, DfTypeStructInfo> extractStructInfoMap(UnifiedSchema unifiedSchema) {
        return DfCollectionUtil.emptyMap();
    }

    public String generateParameterInfoMapKey(String catalog, String procedureName, String parameterName) {
        return null;
    }

    // ===================================================================================
    //                                                                         Source Info
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public Map<String, DfProcedureSourceInfo> extractProcedureSourceInfo(UnifiedSchema unifiedSchema) {
        final Map<String, DfProcedureSourceInfo> cachedMap = _procedureSourceMapMap.get(unifiedSchema);
        if (cachedMap != null) {
            return cachedMap;
        }
        final List<Map<String, String>> sourceList = selectProcedureSourceList(unifiedSchema);
        final Map<String, DfProcedureSourceInfo> resultMap = StringKeyMap.createAsFlexibleOrdered();
        for (Map<String, String> sourceMap : sourceList) {
            final String name = sourceMap.get("ROUTINE_NAME");
            if (name == null) { // just in case
                continue;
            }
            final DfProcedureSourceInfo sourceInfo = new DfProcedureSourceInfo();

            // ROUTINES does not have parameter list
            final String body = sourceMap.get("ROUTINE_DEFINITION");
            sourceInfo.setSourceCode(body);

            // body part only
            sourceInfo.setSourceLine(calculateSourceLine(null, body));
            sourceInfo.setSourceSize(calculateSourceSize(null, body));

            resultMap.put(name, sourceInfo);
        }
        _procedureSourceMapMap.put(unifiedSchema, resultMap);
        return _procedureSourceMapMap.get(unifiedSchema);
    }

    protected List<Map<String, String>> selectProcedureSourceList(UnifiedSchema unifiedSchema) {
        // mysql.proc can be accessed only by root so it uses information schema
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        StringBuilder sb = new StringBuilder();
        sb.append("select * from INFORMATION_SCHEMA.ROUTINES");
        sb.append(" where ROUTINE_SCHEMA = '").append(unifiedSchema.getPureCatalog()).append("'");
        sb.append(" order by ROUTINE_NAME");
        String sql = sb.toString();
        final List<String> columnList = new ArrayList<String>();
        columnList.add("ROUTINE_SCHEMA");
        columnList.add("ROUTINE_NAME");
        columnList.add("ROUTINE_DEFINITION"); // body only (no parameter info)
        final List<Map<String, String>> resultList;
        try {
            log(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because it's basically assist info
            log("Failed to select procedure source info: " + continued.getMessage());
            return DfCollectionUtil.emptyList();
        }
        return resultList;
    }

    protected Integer calculateSourceLine(String paramList, String body) {
        int line = 0;
        if (paramList != null) {
            line = line + Srl.count(paramList, "\n");
        } else {
            ++line;
        }
        if (body != null) {
            line = line + Srl.count(body, "\n");
        } else { // no way?
            ++line;
        }
        return line;
    }

    protected Integer calculateSourceSize(String paramList, String body) {
        return (paramList != null ? paramList.length() : 0) + (body != null ? body.length() : 0);
    }

    protected String calculateSourceHash(String paramList, String body) {
        final String source = (paramList != null ? paramList : "") + (body != null ? body : "");
        return Integer.toHexString(source.hashCode()); // not perfect but allowed here
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected UnifiedSchema getMainSchema() {
        return getDatabaseProperties().getDatabaseSchema();
    }

    protected List<UnifiedSchema> getAdditionalSchemaList() {
        return getDatabaseProperties().getAdditionalSchemaList();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return DfBuildProperties.getInstance().getDatabaseProperties();
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    protected void log(String msg) {
        if (_suppressLogging) {
            return;
        }
        _log.info(msg);
    }

    public void suppressLogging() {
        _suppressLogging = true;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return DfCollectionUtil.newHashMap();
    }
}
