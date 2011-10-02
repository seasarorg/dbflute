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

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/29 Monday)
 */
public class DfProcedureParameterNativeExtractorOracle {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureParameterNativeExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;

    protected final boolean _suppressLogging;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureParameterNativeExtractorOracle(DataSource dataSource, boolean suppressLogging) {
        _dataSource = dataSource;
        _suppressLogging = suppressLogging;
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public List<ProcedureArgumentInfo> extractProcedureArgumentInfoList(UnifiedSchema unifiedSchema) { // Oracle dependency
        return selectProcedureArgumentInfoList(unifiedSchema);
    }

    public List<ProcedureArgumentInfo> extractProcedureArgumentInfoToDBLinkList(String dbLinkName) { // Oracle dependency
        return selectProcedureArgumentInfoToDBLinkList(dbLinkName);
    }

    // ===================================================================================
    //                                                                       Argument Info
    //                                                                       =============
    protected List<ProcedureArgumentInfo> selectProcedureArgumentInfoList(UnifiedSchema unifiedSchema) {
        final String sql = buildProcedureArgumentSql(unifiedSchema);
        return filterParameterArgumentInfoList(doSelectProcedureArgumentInfoList(sql));
    }

    protected String buildProcedureArgumentSql(UnifiedSchema unifiedSchema) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from ALL_ARGUMENTS");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "'");
        sb.append(" order by PACKAGE_NAME, OBJECT_NAME, OVERLOAD, SEQUENCE");
        return sb.toString();
    }

    protected List<ProcedureArgumentInfo> selectProcedureArgumentInfoToDBLinkList(String dbLinkName) {
        final String sql = buildProcedureArgumentToDBLinkSql(dbLinkName);
        return filterParameterArgumentInfoList(doSelectProcedureArgumentInfoList(sql));
    }

    protected String buildProcedureArgumentToDBLinkSql(String dbLinkName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from USER_ARGUMENTS@").append(dbLinkName);
        sb.append(" order by PACKAGE_NAME, OBJECT_NAME, OVERLOAD, SEQUENCE");
        return sb.toString();
    }

    protected List<ProcedureArgumentInfo> doSelectProcedureArgumentInfoList(String sql) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("PACKAGE_NAME");
        columnList.add("OBJECT_NAME");
        columnList.add("OVERLOAD");
        columnList.add("SEQUENCE");
        columnList.add("ARGUMENT_NAME");
        columnList.add("IN_OUT");
        columnList.add("DATA_TYPE");
        columnList.add("DATA_LENGTH");
        columnList.add("DATA_PRECISION");
        columnList.add("DATA_SCALE");
        columnList.add("TYPE_OWNER");
        columnList.add("TYPE_NAME");
        columnList.add("TYPE_SUBNAME");
        final List<Map<String, String>> resultList;
        try {
            log(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because it's basically assist info
            log("Failed to select procedure argument info: " + continued.getMessage());
            return DfCollectionUtil.emptyList();
        }
        final List<ProcedureArgumentInfo> infoList = DfCollectionUtil.newArrayList();
        for (Map<String, String> map : resultList) {
            final ProcedureArgumentInfo info = new ProcedureArgumentInfo();
            info.setPackageName(map.get("PACKAGE_NAME"));
            info.setObjectName(map.get("OBJECT_NAME"));
            info.setOverload(map.get("OVERLOAD"));
            info.setSequence(map.get("SEQUENCE"));
            info.setArgumentName(map.get("ARGUMENT_NAME"));
            info.setInOut(map.get("IN_OUT"));
            info.setDataType(map.get("DATA_TYPE"));
            info.setDataLength(map.get("DATA_LENGTH"));
            info.setDataPrecision(map.get("DATA_PRECISION"));
            info.setDataScale(map.get("DATA_SCALE"));
            final String typeOwner = map.get("TYPE_OWNER"); // ARRAY and STRUCT only
            info.setTypeOwner(typeOwner);
            final String typeName = map.get("TYPE_NAME"); // nullable
            if (Srl.is_NotNull_and_NotTrimmedEmpty(typeName)) {
                info.setTypeName(Srl.connectPrefix(typeName, typeOwner, "."));
            }
            info.setTypeSubName(map.get("TYPE_SUBNAME"));
            infoList.add(info);
        }
        return infoList;
    }

    protected List<ProcedureArgumentInfo> filterParameterArgumentInfoList(List<ProcedureArgumentInfo> infoList) {
        final StringKeyMap<ProcedureArgumentInfo> infoMap = StringKeyMap.createAsFlexibleOrdered();
        for (int i = 0; i < infoList.size(); i++) {
            final ProcedureArgumentInfo info = infoList.get(i);
            final String argumentName = info.getArgumentName();
            final String key = generateParameterInfoMapKey(info.getPackageName(), info.getObjectName(), argumentName);
            final ProcedureArgumentInfo alreadyRegistered = infoMap.get(key);
            if (alreadyRegistered != null) { // means overload argument
                continue; // overload should be ordered by ascend
            }
            infoMap.put(key, info);
        }
        return DfCollectionUtil.newArrayList(infoMap.values());
    }

    // DBFlute treats overload methods as one method
    // (overload info is only referred to determinate whether the procedure has overload methods)
    public static String generateParameterInfoMapKey(String catalog, String procedureName, String parameterName) {
        final StringBuilder keySb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(catalog)) {
            keySb.append(catalog).append(".");
        }
        keySb.append(procedureName).append(".").append(parameterName);
        return keySb.toString();
    }

    public static class ProcedureArgumentInfo {
        protected String _packageName;
        protected String _objectName;
        protected String _overload;
        protected String _sequence;
        protected String _argumentName;
        protected String _inOut;
        protected String _dataType;
        protected String _dataLength;
        protected String _dataPrecision;
        protected String _dataScale;
        protected String _typeOwner;
        protected String _typeName;
        protected String _typeSubName;

        public String buildArrayTypeName() {
            final String typeName = getTypeName();
            final String typeSubName = getTypeSubName();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(typeSubName)) {
                // *typeOwner handling is under review
                //final String typeOwner = argInfo.getTypeOwner();
                return typeName + "." + typeSubName;
            } else {
                // *it may need to add typeOwner if additional schema at the future
                return typeName;
            }
        }

        public String getPackageName() {
            return _packageName;
        }

        public void setPackageName(String packageName) {
            this._packageName = packageName;
        }

        public String getObjectName() {
            return _objectName;
        }

        public void setObjectName(String objectName) {
            this._objectName = objectName;
        }

        public String getOverload() {
            return _overload;
        }

        public void setOverload(String overload) {
            this._overload = overload;
        }

        public String getSequence() {
            return _sequence;
        }

        public void setSequence(String sequence) {
            this._sequence = sequence;
        }

        public String getArgumentName() {
            return _argumentName;
        }

        public void setArgumentName(String argumentName) {
            this._argumentName = argumentName;
        }

        public String getInOut() {
            return _inOut;
        }

        public void setInOut(String inOut) {
            this._inOut = inOut;
        }

        public String getDataType() {
            return _dataType;
        }

        public void setDataType(String dataType) {
            this._dataType = dataType;
        }

        public String getDataLength() {
            return _dataLength;
        }

        public void setDataLength(String dataLength) {
            this._dataLength = dataLength;
        }

        public String getDataPrecision() {
            return _dataPrecision;
        }

        public void setDataPrecision(String dataPrecision) {
            this._dataPrecision = dataPrecision;
        }

        public String getDataScale() {
            return _dataScale;
        }

        public void setDataScale(String dataScale) {
            this._dataScale = dataScale;
        }

        public String getTypeName() {
            return _typeName;
        }

        public void setTypeName(String typeName) {
            this._typeName = typeName;
        }

        public String getTypeOwner() {
            return _typeOwner;
        }

        public void setTypeOwner(String typeOwner) {
            this._typeOwner = typeOwner;
        }

        public String getTypeSubName() {
            return _typeSubName;
        }

        public void setTypeSubName(String typeSubName) {
            this._typeSubName = typeSubName;
        }
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
