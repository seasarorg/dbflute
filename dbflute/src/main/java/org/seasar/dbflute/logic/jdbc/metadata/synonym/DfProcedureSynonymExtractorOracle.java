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
package org.seasar.dbflute.logic.jdbc.metadata.synonym;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.DfAbstractMetaDataExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfProcedureExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureSynonymMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMeta;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeExtractorOracle.ProcedureNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeTranslatorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfDBLinkNativeExtractorOracle.DBLinkNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymNativeExtractorOracle.SynonymNativeInfo;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfProcedureSynonymExtractorOracle extends DfAbstractMetaDataExtractor implements
        DfProcedureSynonymExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureSynonymExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected List<UnifiedSchema> _targetSchemaList;

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public Map<String, DfProcedureSynonymMeta> extractProcedureSynonymMap() {
        _log.info("...Extracting procedure synonym");
        final Map<String, DfProcedureSynonymMeta> procedureSynonymMap = StringKeyMap.createAsFlexibleOrdered();
        final String sql = buildSynonymSelect();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = _dataSource.getConnection();
            final DatabaseMetaData metaData = conn.getMetaData();
            final Map<String, DfProcedureMeta> procedureMap = new LinkedHashMap<String, DfProcedureMeta>();
            final List<DfProcedureMeta> procedureList = new ArrayList<DfProcedureMeta>();
            final DfProcedureExtractor procedureExtractor = new DfProcedureExtractor();
            procedureExtractor.suppressLogging();
            for (UnifiedSchema unifiedSchema : _targetSchemaList) {
                // get new procedure list because different instances is needed at this process
                procedureList.addAll(procedureExtractor.getPlainProcedureList(_dataSource, metaData, unifiedSchema));
            }
            for (DfProcedureMeta metaInfo : procedureList) {
                final String procedureKeyName = metaInfo.getProcedureFullQualifiedName();
                procedureMap.put(procedureKeyName, metaInfo);
            }
            Map<String, Map<String, ProcedureNativeInfo>> dbLinkProcedureNativeMap = null;
            Map<String, Map<String, SynonymNativeInfo>> dbLinkSynonymNativeMap = null;
            st = conn.createStatement();
            _log.info(sql);
            rs = st.executeQuery(sql);
            while (rs.next()) {
                final UnifiedSchema synonymOwner = createAsDynamicSchema(null, rs.getString("OWNER"));
                final String synonymName = rs.getString("SYNONYM_NAME");
                final UnifiedSchema tableOwner = createAsDynamicSchema(null, rs.getString("TABLE_OWNER"));
                final String tableName = rs.getString("TABLE_NAME");
                final String dbLinkName = rs.getString("DB_LINK");

                final DfSynonymMeta synonymMetaInfo = new DfSynonymMeta();

                // Basic
                synonymMetaInfo.setSynonymOwner(synonymOwner);
                synonymMetaInfo.setSynonymName(synonymName);
                synonymMetaInfo.setTableOwner(tableOwner);
                synonymMetaInfo.setTableName(tableName);
                synonymMetaInfo.setDBLinkName(dbLinkName);

                // Select-able?
                judgeSynonymSelectable(synonymMetaInfo);

                if (synonymMetaInfo.isSelectable()) {
                    continue; // select-able synonyms are out of target
                }
                final DfProcedureMeta procedureMeta;
                if (dbLinkName != null && dbLinkName.trim().length() > 0) { // synonym for DB link
                    if (dbLinkProcedureNativeMap == null) { // lazy load
                        dbLinkProcedureNativeMap = extractDBLinkProcedureNativeMap();
                    }
                    if (dbLinkSynonymNativeMap == null) { // lazy load
                        dbLinkSynonymNativeMap = extractDBLinkSynonymNativeMap();
                    }
                    procedureMeta = prepareDBLinkProcedureNative(tableName, dbLinkName, dbLinkProcedureNativeMap,
                            dbLinkSynonymNativeMap);
                    if (procedureMeta == null) {
                        continue;
                    }
                } else {
                    final String procedureKey = tableOwner.buildSchemaQualifiedName(tableName);
                    procedureMeta = procedureMap.get(procedureKey);
                    if (procedureMeta == null) {
                        // Synonym for Package Procedure has several problems.
                        //  o Synonym meta data does not have its package info (needs to trace more)
                        //  o Oracle cannot execute Synonym for Package Procedure *fundamental problem
                        // So it is not supported here.
                        //for (String schemaName : _schemaList) {
                        //    procedureMetaInfo = procedureMap.get(schemaName + "." + procedureKey);
                        //    if (procedureMetaInfo != null) {
                        //        break; // comes first  
                        //    }
                        //}
                        //if (procedureMetaInfo == null) {
                        continue;
                        //}
                    }
                }
                procedureMeta.setProcedureSynonym(true);
                final DfProcedureSynonymMeta procedureSynonymMetaInfo = new DfProcedureSynonymMeta();
                procedureSynonymMetaInfo.setProcedureMetaInfo(procedureMeta);
                procedureSynonymMetaInfo.setSynonymMetaInfo(synonymMetaInfo);
                final String synonymKey = buildSynonymMapKey(synonymOwner, synonymName);
                procedureSynonymMap.put(synonymKey, procedureSynonymMetaInfo);
            }
        } catch (SQLException e) {
            String msg = "Failed to get procedure synonyms: sql=" + sql;
            throw new SQLFailureException(msg, e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return procedureSynonymMap;
    }

    protected String buildSynonymSelect() {
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        for (UnifiedSchema unifiedSchema : _targetSchemaList) {
            if (count > 0) {
                sb.append(", ");
            }
            sb.append("'").append(unifiedSchema.getPureSchema()).append("'");
            ++count;
        }
        final String sql = "select * from ALL_SYNONYMS where OWNER in (" + sb.toString() + ")";
        return sql;
    }

    protected String buildSynonymMapKey(UnifiedSchema synonymOwner, String synonymName) {
        return synonymOwner.buildSchemaQualifiedName(synonymName);
    }

    protected void judgeSynonymSelectable(DfSynonymMeta info) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String synonymSqlName = info.buildSynonymSqlName();
        final String sql = "select * from " + synonymSqlName + " where 0 = 1";
        try {
            final List<String> columnList = new ArrayList<String>();
            columnList.add("dummy");
            facade.selectStringList(sql, columnList);
            info.setSelectable(true);
        } catch (RuntimeException ignored) {
            info.setSelectable(false);
        }
    }

    protected Map<String, Map<String, ProcedureNativeInfo>> extractDBLinkProcedureNativeMap() { // main schema's DB link only
        final DfDBLinkNativeExtractorOracle dbLinkExtractor = createDBLinkNativeExtractor();
        final Map<String, DBLinkNativeInfo> dbLinkInfoMap = dbLinkExtractor.selectDBLinkInfoMap();
        final DfProcedureNativeExtractorOracle nativeExtractor = createProcedureNativeExtractor();
        final Map<String, Map<String, ProcedureNativeInfo>> map = DfCollectionUtil.newLinkedHashMap();
        for (String dbLinkName : dbLinkInfoMap.keySet()) {
            map.put(dbLinkName, nativeExtractor.extractDBLinkProcedureNativeInfoList(dbLinkName));
        }
        return map;
    }

    protected DfDBLinkNativeExtractorOracle createDBLinkNativeExtractor() {
        return new DfDBLinkNativeExtractorOracle(_dataSource, false);
    }

    protected DfProcedureNativeExtractorOracle createProcedureNativeExtractor() {
        return new DfProcedureNativeExtractorOracle(_dataSource, false);
    }

    protected Map<String, Map<String, SynonymNativeInfo>> extractDBLinkSynonymNativeMap() { // main schema's DB link only
        final DfDBLinkNativeExtractorOracle dbLinkExtractor = createDBLinkNativeExtractor();
        final Map<String, DBLinkNativeInfo> dbLinkInfoMap = dbLinkExtractor.selectDBLinkInfoMap();
        final DfSynonymNativeExtractorOracle nativeExtractor = createSynonymNativeExtractor();
        final Map<String, Map<String, SynonymNativeInfo>> map = DfCollectionUtil.newLinkedHashMap();
        for (String dbLinkName : dbLinkInfoMap.keySet()) {
            map.put(dbLinkName, nativeExtractor.selectDBLinkSynonymInfoMap(dbLinkName));
        }
        return map;
    }

    protected DfSynonymNativeExtractorOracle createSynonymNativeExtractor() {
        return new DfSynonymNativeExtractorOracle(_dataSource, false);
    }

    protected DfProcedureMeta prepareDBLinkProcedureNative(String tableName, String dbLinkName,
            Map<String, Map<String, ProcedureNativeInfo>> dbLinkProcedureNativeMap,
            Map<String, Map<String, SynonymNativeInfo>> dbLinkSynonymNativeMap) {
        final Map<String, ProcedureNativeInfo> nativeMap = dbLinkProcedureNativeMap.get(dbLinkName);
        if (nativeMap == null) {
            return null; // it might be next schema DB link
        }
        // Synonym for Package Procedure has several problems.
        //  o Synonym meta data does not have its package info (needs to trace more)
        //  o Oracle cannot execute Synonym for Package Procedure *fundamental problem
        // So it is not supported here.
        final String nativeInfoMapKey = generateNativeInfoMapKey(null, tableName, null);
        ProcedureNativeInfo nativeInfo = nativeMap.get(nativeInfoMapKey);
        if (nativeInfo == null) {
            Map<String, SynonymNativeInfo> synonymNativeMap = dbLinkSynonymNativeMap.get(dbLinkName);
            final SynonymNativeInfo synonymNativeInfo = synonymNativeMap.get(tableName);
            if (synonymNativeInfo == null) { // means the name is not synonym
                return null; // it might be package procedures
            }
            // it's a synonym in the another world
            final String retryKey = generateNativeInfoMapKey(null, synonymNativeInfo.getTableName(), null);
            final ProcedureNativeInfo retryInfo = nativeMap.get(retryKey);
            if (retryInfo == null) {
                return null;
            }
            nativeInfo = retryInfo; // found
        }
        return createDBLinkProcedureMeta(nativeInfo, dbLinkName);
    }

    protected DfProcedureMeta createDBLinkProcedureMeta(ProcedureNativeInfo nativeInfo, String dbLinkName) {
        final DfProcedureNativeTranslatorOracle translator = new DfProcedureNativeTranslatorOracle();
        return translator.createDBLinkProcedureMeta(nativeInfo, dbLinkName);
    }

    protected String generateNativeInfoMapKey(String packageName, String procedureName, String overload) {
        return DfProcedureNativeExtractorOracle.generateNativeInfoMapKey(packageName, procedureName, overload);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setTargetSchemaList(List<UnifiedSchema> targetSchemaList) {
        this._targetSchemaList = targetSchemaList;
    }
}
