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

import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfProcedureExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMeta.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta.DfProcedureType;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureNativeExtractorOracle.ProcedureNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterNativeExtractorOracle.ProcedureArgumentInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfDBLinkNativeExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfDBLinkNativeExtractorOracle.DBLinkNativeInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymNativeExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymNativeExtractorOracle.SynonymNativeInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.1A (2011/09/30 Friday)
 */
public class DfProcedureNativeTranslatorOracle {

    protected final DataSource _dataSource;
    protected final DfColumnExtractor _columnExtractor = new DfColumnExtractor();
    protected Map<String, Map<String, ProcedureNativeInfo>> _dbLinkProcedureNativeMap;
    protected Map<String, Map<String, SynonymNativeInfo>> _dbLinkSynonymNativeMap;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureNativeTranslatorOracle(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                    DBLink Procedure
    //                                                                    ================
    public DfProcedureMeta translateProcedureToDBLink(String packageName, String procedureName, String dbLinkName,
            DfProcedureExtractor procedureExtractor) {
        if (_dbLinkProcedureNativeMap == null) { // lazy load
            _dbLinkProcedureNativeMap = extractDBLinkProcedureNativeMap();
        }
        if (_dbLinkSynonymNativeMap == null) { // lazy load
            _dbLinkSynonymNativeMap = extractDBLinkSynonymNativeMap();
        }
        final Map<String, ProcedureNativeInfo> nativeMap = _dbLinkProcedureNativeMap.get(dbLinkName);
        if (nativeMap == null) {
            return null; // it might be next schema DB link
        }
        // Synonym for Package Procedure has several problems. (so unsupported)
        //  o Synonym meta data does not have its schema info
        //  o Oracle cannot execute Synonym for Package Procedure *fundamental problem
        final String nativeInfoMapKey = generateNativeInfoMapKey(packageName, procedureName);
        ProcedureNativeInfo nativeInfo = nativeMap.get(nativeInfoMapKey);
        if (nativeInfo == null) {
            final Map<String, SynonymNativeInfo> synonymNativeMap = _dbLinkSynonymNativeMap.get(dbLinkName);
            final SynonymNativeInfo synonymNativeInfo = synonymNativeMap.get(procedureName);
            if (synonymNativeInfo == null) { // means the name is not synonym
                return null; // it might be package procedures
            }
            // it's a synonym in the another world
            final String retryKey = generateNativeInfoMapKey(null, synonymNativeInfo.getTableName());
            final ProcedureNativeInfo retryInfo = nativeMap.get(retryKey);
            if (retryInfo == null) {
                return null;
            }
            nativeInfo = retryInfo; // found
        }
        return createDBLinkProcedureMeta(nativeInfo, dbLinkName);
    }

    protected String generateNativeInfoMapKey(String packageName, String procedureName) {
        return DfProcedureNativeExtractorOracle.generateNativeInfoMapKey(packageName, procedureName);
    }

    protected DfProcedureMeta createDBLinkProcedureMeta(ProcedureNativeInfo nativeInfo, String dbLinkName) {
        final DfProcedureMeta procedureMeta = new DfProcedureMeta();
        procedureMeta.setProcedureCatalog(null); // because of Oracle
        final String packageName = nativeInfo.getPackageName();
        final String procedureName;
        if (Srl.is_NotNull_and_NotTrimmedEmpty(packageName)) { // package
            procedureName = packageName + "." + nativeInfo.getProcedureName();
        } else {
            procedureName = nativeInfo.getProcedureName();
        }
        procedureMeta.setProcedureName(procedureName);
        final String linkedName = procedureName + "@" + dbLinkName;
        procedureMeta.setProcedureFullQualifiedName(linkedName);
        procedureMeta.setProcedureSchemaQualifiedName(linkedName);
        procedureMeta.setProcedureSqlName(linkedName);
        procedureMeta.setProcedureType(DfProcedureType.procedureResultUnknown);
        final List<ProcedureArgumentInfo> argInfoList = nativeInfo.getArgInfoList();
        for (ProcedureArgumentInfo argInfo : argInfoList) {
            final DfProcedureColumnMeta columnMeta = new DfProcedureColumnMeta();
            columnMeta.setColumnName(argInfo.getArgumentName());

            final String dataType = argInfo.getDataType();
            columnMeta.setDbTypeName(dataType);
            final String jdbcType = _columnExtractor.getColumnJdbcType(Types.OTHER, dataType);
            final Integer jdbcDefValue = TypeMap.getJdbcDefValueByJdbcType(jdbcType);
            columnMeta.setJdbcDefType(jdbcDefValue);

            final String overload = argInfo.getOverload();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(overload)) {
                columnMeta.setOverloadNo(Integer.valueOf(overload));
            }

            final String inOut = argInfo.getInOut();
            if ("in".equalsIgnoreCase(inOut)) {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnIn);
            } else if ("out".equalsIgnoreCase(inOut)) {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnOut);
            } else if ("inout".equalsIgnoreCase(inOut) || "in/out".equalsIgnoreCase(inOut)) {
                // two pattern condition just in case
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnInOut);
            } else {
                columnMeta.setProcedureColumnType(DfProcedureColumnType.procedureColumnUnknown);
            }

            final String dataLength = argInfo.getDataLength();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(dataLength)) {
                columnMeta.setColumnSize(Integer.valueOf(dataLength));
            }
            final String dataScale = argInfo.getDataScale();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(dataScale)) {
                columnMeta.setDecimalDigits(Integer.valueOf(dataScale));
            }
            procedureMeta.addProcedureColumn(columnMeta);
        }
        // assist info (e.g. great walls) is not set here (set later)

        return procedureMeta;
    }

    // ===================================================================================
    //                                                                  DBLink Native Info
    //                                                                  ==================
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
}
