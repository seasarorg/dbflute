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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterExtractorOracle.ProcedureArgumentInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.1A (2011/09/30 Friday)
 */
public class DfProcedureNativeExtractorOracle {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureNativeExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;

    protected final boolean _suppressLogging;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureNativeExtractorOracle(DataSource dataSource, boolean suppressLogging) {
        _dataSource = dataSource;
        _suppressLogging = suppressLogging;
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public Map<String, ProcedureNativeInfo> extractProcedureNativeInfoMap(UnifiedSchema unifiedSchema) { // Oracle dependency
        return selectProcedureNativeInfoMap(unifiedSchema);
    }

    public Map<String, ProcedureNativeInfo> extractDBLinkProcedureNativeInfoList(String dbLinkName) { // Oracle dependency
        return selectDBLinkProcedureNativeInfoMap(dbLinkName);
    }

    // ===================================================================================
    //                                                                         Native Info
    //                                                                         ===========
    protected Map<String, ProcedureNativeInfo> selectProcedureNativeInfoMap(UnifiedSchema unifiedSchema) {
        final String sql = buildProcedureNativeSql(unifiedSchema);
        final Map<String, ProcedureNativeInfo> nativeInfoMap = doSelectProcedureNativeInfoMap(sql);
        final Map<String, List<ProcedureArgumentInfo>> argInfoMap = selectProcedureArgumentInfoMap(unifiedSchema);
        for (Entry<String, ProcedureNativeInfo> entry : nativeInfoMap.entrySet()) {
            entry.getValue().setArgInfoList(argInfoMap.get(entry.getKey()));
        }
        return nativeInfoMap;
    }

    protected String buildProcedureNativeSql(UnifiedSchema unifiedSchema) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from ALL_PROCEDURES");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "'");
        sb.append(" order by OBJECT_NAME, PROCEDURE_NAME, OVERLOAD");
        return sb.toString();
    }

    protected Map<String, ProcedureNativeInfo> selectDBLinkProcedureNativeInfoMap(String dbLinkName) {
        final String sql = buildDBLinkProcedureNativeSql(dbLinkName);
        final Map<String, ProcedureNativeInfo> nativeInfoMap = doSelectProcedureNativeInfoMap(sql);
        final Map<String, List<ProcedureArgumentInfo>> argInfoMap = selectDBLinkProcedureArgumentInfoMap(dbLinkName);
        for (Entry<String, ProcedureNativeInfo> entry : nativeInfoMap.entrySet()) {
            entry.getValue().setArgInfoList(argInfoMap.get(entry.getKey()));
        }
        return nativeInfoMap;
    }

    protected String buildDBLinkProcedureNativeSql(String dbLinkName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from USER_PROCEDURES@").append(dbLinkName);
        sb.append(" order by OBJECT_NAME, PROCEDURE_NAME, OVERLOAD");
        return sb.toString();
    }

    protected Map<String, ProcedureNativeInfo> doSelectProcedureNativeInfoMap(String sql) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("OBJECT_NAME");
        columnList.add("PROCEDURE_NAME");
        columnList.add("OVERLOAD");
        final List<Map<String, String>> resultList;
        try {
            log(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because it's basically assist info
            log("Failed to select procedure native info: " + continued.getMessage());
            return DfCollectionUtil.emptyMap();
        }
        final Map<String, ProcedureNativeInfo> infoMap = DfCollectionUtil.newLinkedHashMap();
        for (Map<String, String> map : resultList) {
            final ProcedureNativeInfo info = new ProcedureNativeInfo();
            final String objectName = map.get("OBJECT_NAME");
            info.setObjectName(objectName);
            final String procedureName = map.get("PROCEDURE_NAME");
            info.setProcedureName(procedureName);
            final String overload = map.get("OVERLOAD");
            info.setOverload(overload);
            infoMap.put(generateNativeInfoMapKey(objectName, procedureName, overload), info);
        }
        return infoMap;
    }

    public static String generateNativeInfoMapKey(String packageName, String procedureName, String overload) {
        final StringBuilder keySb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(packageName)) {
            keySb.append(packageName).append(".");
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(procedureName)) {
            keySb.append(procedureName).append(".");
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(overload)) {
            keySb.append(overload).append(".");
        }
        return keySb.toString();
    }

    public static class ProcedureNativeInfo {
        protected String _objectName;
        protected String _procedureName;
        protected String _overload;
        protected List<ProcedureArgumentInfo> _argInfoList;

        public String getObjectName() {
            return _objectName;
        }

        public void setObjectName(String objectName) {
            this._objectName = objectName;
        }

        public String getProcedureName() {
            return _procedureName;
        }

        public void setProcedureName(String procedureName) {
            this._procedureName = procedureName;
        }

        public String getOverload() {
            return _overload;
        }

        public void setOverload(String overload) {
            this._overload = overload;
        }

        public List<ProcedureArgumentInfo> getArgInfoList() {
            return _argInfoList;
        }

        public void setArgInfoList(List<ProcedureArgumentInfo> argInfoList) {
            this._argInfoList = argInfoList;
        }
    }

    // ===================================================================================
    //                                                                       Argument Info
    //                                                                       =============
    protected Map<String, List<ProcedureArgumentInfo>> selectProcedureArgumentInfoMap(UnifiedSchema unifiedSchema) {
        final DfProcedureParameterExtractorOracle extractor = new DfProcedureParameterExtractorOracle(_dataSource,
                _suppressLogging);
        final List<ProcedureArgumentInfo> allArgList = extractor.extractProcedureArgumentInfoList(unifiedSchema);
        return arrangeProcedureArgumentInfoMap(allArgList);
    }

    protected Map<String, List<ProcedureArgumentInfo>> selectDBLinkProcedureArgumentInfoMap(String dbLinkName) {
        final DfProcedureParameterExtractorOracle extractor = new DfProcedureParameterExtractorOracle(_dataSource,
                _suppressLogging);
        final List<ProcedureArgumentInfo> allArgList = extractor.extractDBLinkProcedureArgumentInfoList(dbLinkName);
        return arrangeProcedureArgumentInfoMap(allArgList);
    }

    protected Map<String, List<ProcedureArgumentInfo>> arrangeProcedureArgumentInfoMap(
            List<ProcedureArgumentInfo> allArgList) {
        final Map<String, List<ProcedureArgumentInfo>> map = DfCollectionUtil.newLinkedHashMap();
        for (ProcedureArgumentInfo currentArgInfo : allArgList) {
            final String packageName = currentArgInfo.getPackageName();
            final String procedureName = currentArgInfo.getObjectName();
            final String overload = currentArgInfo.getOverload();
            final String key = generateNativeInfoMapKey(packageName, procedureName, overload);
            List<ProcedureArgumentInfo> argList = map.get(key);
            if (argList == null) {
                argList = DfCollectionUtil.newArrayList();
                map.put(key, argList);
            }
            argList.add(currentArgInfo);
        }
        return map;
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
}
