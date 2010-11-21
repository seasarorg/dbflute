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
package org.seasar.dbflute.logic.jdbc.metadata.procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
import org.seasar.dbflute.logic.jdbc.metadata.various.array.DfArrayExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.various.struct.DfStructExtractorOracle;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/18 Thursday)
 */
public class DfProcedureSupplementExtractorOracle implements DfProcedureSupplementExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureSupplementExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;

    /** The info map of argument for cache. */
    protected final Map<UnifiedSchema, List<ProcedureArgumentInfo>> _argumentInfoListMap = DfCollectionUtil
            .newHashMap();

    /** The info map of array set for cache. */
    protected final Map<UnifiedSchema, StringSet> _arrayTypeSetMap = DfCollectionUtil.newHashMap();

    /** The info map of STRUCT type for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structInfoMapMap = DfCollectionUtil
            .newHashMap();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedureSupplementExtractorOracle(DataSource dataSource) {
        _dataSource = dataSource;
    }

    // ===================================================================================
    //                                                                            Overload
    //                                                                            ========
    /**
     * Extract the map of overload info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @param unifiedSchema The unified schema. (NotNull)
     * @return The map of array info. {key = (packageName.)procedureName.columnName, value = overloadNo} (NotNull)
     */
    public StringKeyMap<Integer> extractOverloadInfoMap(UnifiedSchema unifiedSchema) {
        final List<ProcedureArgumentInfo> infoList = findProcedureArgumentInfoList(unifiedSchema);
        final StringKeyMap<Integer> infoMap = StringKeyMap.createAsFlexibleOrdered();
        for (int i = 0; i < infoList.size(); i++) {
            final ProcedureArgumentInfo info = infoList.get(i);
            final String argumentName = info.getArgumentName();
            final String overload = info.getOverload();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName) || Srl.is_Null_or_TrimmedEmpty(overload)) {
                continue;
            }
            final String key = generateParameterInfoMapKey(info.getPackageName(), info.getObjectName(), argumentName);
            infoMap.put(key, DfTypeUtil.toInteger(overload));
        }
        return infoMap;
    }

    // ===================================================================================
    //                                                                               Array
    //                                                                               =====
    /**
     * Extract the map of array info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @param unifiedSchema The unified schema. (NotNull)
     * @return The map of array info. {key = (packageName.)procedureName.columnName} (NotNull)
     */
    public StringKeyMap<DfTypeArrayInfo> extractArrayInfoMap(UnifiedSchema unifiedSchema) {
        final List<ProcedureArgumentInfo> infoList = findProcedureArgumentInfoList(unifiedSchema);
        final StringKeyMap<DfTypeArrayInfo> infoMap = StringKeyMap.createAsFlexibleOrdered();
        for (int i = 0; i < infoList.size(); i++) {
            final ProcedureArgumentInfo info = infoList.get(i);
            final String argumentName = info.getArgumentName();
            final String dataType = info.getDataType();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName) || !Srl.containsAnyIgnoreCase(dataType, "TABLE", "VARRAY")) {
                continue;
            }
            final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo();
            final String typeName = info.getTypeName();
            final String typeSubName = info.getTypeSubName();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(typeSubName)) {
                if (Srl.is_NotNull_and_NotTrimmedEmpty(typeName)) {
                    arrayInfo.setTypeName(typeName + "." + typeSubName);
                } else {
                    arrayInfo.setTypeName(typeSubName);
                }
            } else {
                arrayInfo.setTypeName(typeName);
            }
            if (infoList.size() > (i + 1)) { // element type is in data type of next record
                final ProcedureArgumentInfo nextInfo = infoList.get(i + 1);
                final String nextDataType = nextInfo.getDataType();
                if (Srl.equalsIgnoreCase("OBJECT", nextDataType)) { // element is struct type
                    arrayInfo.setElementType(nextInfo.getTypeName());
                } else {
                    arrayInfo.setElementType(nextDataType);
                }
            }
            processStructElement(unifiedSchema, arrayInfo);
            final String key = generateParameterInfoMapKey(info.getPackageName(), info.getObjectName(), argumentName);
            infoMap.put(key, arrayInfo);
        }
        return infoMap;
    }

    protected void processStructElement(UnifiedSchema unifiedSchema, DfTypeArrayInfo arrayInfo) {
        final StringKeyMap<DfTypeStructInfo> structInfoMap = findStructInfoMap(unifiedSchema);
        final DfTypeStructInfo structInfo = structInfoMap.get(arrayInfo.getElementType());
        if (structInfo == null) {
            return;
        }
        arrayInfo.setStructInfo(structInfo);
        // *STRUCT type of additional schema is unsupported for now
    }

    // ===================================================================================
    //                                                                              Struct
    //                                                                              ======
    public StringKeyMap<DfTypeStructInfo> extractStructInfoMap(UnifiedSchema unifiedSchema) {
        return findStructInfoMap(unifiedSchema);
    }

    protected StringKeyMap<DfTypeStructInfo> findStructInfoMap(UnifiedSchema unifiedSchema) {
        StringKeyMap<DfTypeStructInfo> structInfoMap = _structInfoMapMap.get(unifiedSchema);
        if (structInfoMap != null) {
            return structInfoMap;
        }

        // initialize per schema
        final DfStructExtractorOracle extractor = new DfStructExtractorOracle(_dataSource);
        structInfoMap = extractor.extractStructInfoMap(unifiedSchema);
        _structInfoMapMap.put(unifiedSchema, structInfoMap);

        // column's additional info (should be after initialization)
        initializeStructAttributeInfo(unifiedSchema, structInfoMap);

        return _structInfoMapMap.get(unifiedSchema);
    }

    protected void initializeStructAttributeInfo(UnifiedSchema unifiedSchema,
            StringKeyMap<DfTypeStructInfo> structInfoMap) {
        // arrayInfo getting is OK after structInfoMapMap initialization
        // and additional schema's nested things are unsupported, same schema's only
        final StringKeyMap<DfTypeArrayInfo> arrayInfoMap = extractArrayInfoMap(unifiedSchema); // first priority
        final StringSet arrayTypeSet = extractArrayTypeSet(unifiedSchema); // second priority
        for (DfTypeStructInfo structInfo : structInfoMap.values()) {
            for (DfColumnMetaInfo metaInfo : structInfo.getAttributeInfoMap().values()) {
                final String dbTypeName = metaInfo.getDbTypeName();
                final DfTypeArrayInfo nestedArrayInfo = arrayInfoMap.get(dbTypeName);
                if (nestedArrayInfo != null) { // nested array used in procedure parameter
                    metaInfo.setTypeArrayInfo(nestedArrayInfo);
                } else if (arrayTypeSet.contains(dbTypeName)) { // nested array unused in procedure parameter
                    final DfTypeArrayInfo typeArrayInfo = new DfTypeArrayInfo();
                    typeArrayInfo.setTypeName(dbTypeName);
                    typeArrayInfo.setElementType("UNKNOWN"); // *the way to get the info is unknown
                    metaInfo.setTypeArrayInfo(typeArrayInfo);
                }
                final DfTypeStructInfo nestedStructInfo = structInfoMap.get(dbTypeName);
                if (nestedStructInfo != null) { // nested struct
                    metaInfo.setTypeStructInfo(nestedStructInfo);
                }
                metaInfo.setProcedureParameter(true); // for default mapping type
            }
        }
    }

    protected StringSet extractArrayTypeSet(UnifiedSchema unifiedSchema) {
        StringSet arrayInfoSet = _arrayTypeSetMap.get(unifiedSchema);
        if (arrayInfoSet != null) {
            return arrayInfoSet;
        }

        DfArrayExtractorOracle extractor = new DfArrayExtractorOracle(_dataSource);
        _arrayTypeSetMap.put(unifiedSchema, extractor.extractArrayTypeSet(unifiedSchema));
        return _arrayTypeSetMap.get(unifiedSchema);
    }

    // ===================================================================================
    //                                                                       Key Generator
    //                                                                       =============
    public String generateParameterInfoMapKey(String catalog, String procedureName, String parameterName) {
        final StringBuilder keySb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(catalog)) {
            keySb.append(catalog).append(".");
        }
        keySb.append(procedureName).append(".").append(parameterName);
        return keySb.toString();
    }

    // ===================================================================================
    //                                                                       Argument Info
    //                                                                       =============
    protected List<ProcedureArgumentInfo> findProcedureArgumentInfoList(UnifiedSchema unifiedSchema) {
        final List<ProcedureArgumentInfo> cachedList = _argumentInfoListMap.get(unifiedSchema);
        if (cachedList != null) {
            return cachedList;
        }
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String sql = buildProcedureArgumentSql(unifiedSchema);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("PACKAGE_NAME");
        columnList.add("OBJECT_NAME");
        columnList.add("OVERLOAD");
        columnList.add("SEQUENCE");
        columnList.add("ARGUMENT_NAME");
        columnList.add("DATA_TYPE");
        columnList.add("TYPE_NAME");
        columnList.add("TYPE_SUBNAME");
        final List<Map<String, String>> resultList;
        try {
            _log.info(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because of assist info
            _log.info("Failed to select supplement info: " + continued.getMessage());
            return new ArrayList<ProcedureArgumentInfo>();
        }
        final List<ProcedureArgumentInfo> infoList = DfCollectionUtil.newArrayList();
        for (Map<String, String> map : resultList) {
            ProcedureArgumentInfo info = new ProcedureArgumentInfo();
            info.setPackageName(map.get("PACKAGE_NAME"));
            info.setObjectName(map.get("OBJECT_NAME"));
            info.setOverload(map.get("OVERLOAD"));
            info.setSequence(map.get("SEQUENCE"));
            info.setArgumentName(map.get("ARGUMENT_NAME"));
            info.setDataType(map.get("DATA_TYPE"));
            info.setTypeName(map.get("TYPE_NAME"));
            info.setTypeSubName(map.get("TYPE_SUBNAME"));
            infoList.add(info);
        }
        _argumentInfoListMap.put(unifiedSchema, infoList);
        return _argumentInfoListMap.get(unifiedSchema);
    }

    protected String buildProcedureArgumentSql(UnifiedSchema unifiedSchema) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from ALL_ARGUMENTS");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "'");
        sb.append(" order by PACKAGE_NAME, OBJECT_NAME, OVERLOAD, SEQUENCE");
        return sb.toString();
    }

    protected static class ProcedureArgumentInfo {
        protected String _packageName;
        protected String _objectName;
        protected String _overload;
        protected String _sequence;
        protected String _argumentName;
        protected String _dataType;
        protected String _typeName;
        protected String _typeSubName;

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

        public String getDataType() {
            return _dataType;
        }

        public void setDataType(String dataType) {
            this._dataType = dataType;
        }

        public String getTypeName() {
            return _typeName;
        }

        public void setTypeName(String typeName) {
            this._typeName = typeName;
        }

        public String getTypeSubName() {
            return _typeSubName;
        }

        public void setTypeSubName(String typeSubName) {
            this._typeSubName = typeSubName;
        }
    }
}
