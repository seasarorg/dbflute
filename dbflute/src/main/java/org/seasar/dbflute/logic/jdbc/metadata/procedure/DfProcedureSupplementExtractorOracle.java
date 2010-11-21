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

    /** The info map of ARRAY for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _arrayInfoMapMap = DfCollectionUtil.newHashMap();

    /** The info map of STRUCT type for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structInfoMapMap = DfCollectionUtil
            .newHashMap();

    /** The info map of ARRAY set for cache. */
    protected final Map<UnifiedSchema, StringSet> _arrayTypeSetMap = DfCollectionUtil.newHashMap();

    /** The info map of procedure argument for cache. */
    protected final Map<UnifiedSchema, List<ProcedureArgumentInfo>> _argumentInfoListMap = DfCollectionUtil
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
     * Extract the map of parameter's overload info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @param unifiedSchema The unified schema. (NotNull)
     * @return The map of parameter's array info. {key = (packageName.)procedureName.columnName, value = overloadNo} (NotNull)
     */
    public StringKeyMap<Integer> extractParameterOverloadInfoMap(UnifiedSchema unifiedSchema) {
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
     * Extract the map of parameter's array info. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @param unifiedSchema The unified schema. (NotNull)
     * @return The map of parameter's array info. {key = (packageName.)procedureName.columnName} (NotNull)
     */
    public StringKeyMap<DfTypeArrayInfo> extractParameterArrayInfoMap(UnifiedSchema unifiedSchema) {
        StringKeyMap<DfTypeArrayInfo> parameterArrayInfoMap = _arrayInfoMapMap.get(unifiedSchema);
        if (parameterArrayInfoMap != null) {
            return parameterArrayInfoMap;
        }
        final List<ProcedureArgumentInfo> argInfoList = findProcedureArgumentInfoList(unifiedSchema);
        parameterArrayInfoMap = StringKeyMap.createAsFlexibleOrdered();
        _arrayInfoMapMap.put(unifiedSchema, parameterArrayInfoMap);
        for (int i = 0; i < argInfoList.size(); i++) {
            final ProcedureArgumentInfo argInfo = argInfoList.get(i);
            final String argumentName = argInfo.getArgumentName();
            final String dataType = argInfo.getDataType();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName) || !isDataTypeArray(dataType)) {
                continue;
            }
            final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo();
            final String typeName = argInfo.getTypeName();
            final String typeSubName = argInfo.getTypeSubName();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(typeSubName)) {
                if (Srl.is_NotNull_and_NotTrimmedEmpty(typeName)) {
                    arrayInfo.setTypeName(typeName + "." + typeSubName);
                } else {
                    arrayInfo.setTypeName(typeSubName);
                }
            } else {
                arrayInfo.setTypeName(typeName);
            }
            reflectArrayElementType(argInfoList, i, arrayInfo);
            processArrayStructElement(unifiedSchema, arrayInfo);
            final String key = generateParameterInfoMapKey(argInfo.getPackageName(), argInfo.getObjectName(),
                    argumentName);
            parameterArrayInfoMap.put(key, arrayInfo);
        }
        final StringKeyMap<DfTypeArrayInfo> uniqueArrayInfoMap = StringKeyMap.createAsFlexibleOrdered();
        for (DfTypeArrayInfo arrayInfo : parameterArrayInfoMap.values()) {
            uniqueArrayInfoMap.put(arrayInfo.getTypeName(), arrayInfo);
        }
        resolveNestedTemporaryInfo(uniqueArrayInfoMap); // should be called after argInfo loop
        return _arrayInfoMapMap.get(unifiedSchema);
    }

    // -----------------------------------------------------
    //                                          Element Type
    //                                          ------------
    protected void reflectArrayElementType(List<ProcedureArgumentInfo> argInfoList, int i, DfTypeArrayInfo arrayInfo) {
        if (argInfoList.size() > (i + 1)) { // element type is in data type of next record
            final ProcedureArgumentInfo nextInfo = argInfoList.get(i + 1);
            final String nextDataType = nextInfo.getDataType();
            if (Srl.equalsIgnoreCase("OBJECT", nextDataType)) { // element is struct type
                arrayInfo.setElementType(nextInfo.getTypeName());
            } else if (isDataTypeArray(nextDataType)) { // element is array type
                arrayInfo.setElementType(nextDataType);

                // temporary setup (resolved later)
                // hierarchy resolution is not needed here
                // because next element is processed at next loop
                final String nestedArrayTypeName = nextInfo.getTypeName();
                final DfTypeArrayInfo nestedArrayInfo = new DfTypeArrayInfo();
                nestedArrayInfo.setTypeName(nestedArrayTypeName);
                nestedArrayInfo.setElementType("UNKNOWN");
                arrayInfo.setNestedArrayInfo(nestedArrayInfo);
            } else {
                arrayInfo.setElementType(nextDataType);
            }
        }
    }

    protected boolean isDataTypeArray(String dataType) {
        return Srl.containsAnyIgnoreCase(dataType, "TABLE", "VARRAY");
    }

    protected boolean isDataTypeStruct(String dataType) {
        return Srl.equalsIgnoreCase(dataType, "OBJECT");
    }

    // -----------------------------------------------------
    //                                        Struct Element
    //                                        --------------
    protected void processArrayStructElement(UnifiedSchema unifiedSchema, DfTypeArrayInfo arrayInfo) {
        final StringKeyMap<DfTypeStructInfo> structInfoMap = findStructInfoMap(unifiedSchema);
        final DfTypeStructInfo structInfo = structInfoMap.get(arrayInfo.getElementType());
        if (structInfo == null) {
            return;
        }
        arrayInfo.setElementStructInfo(structInfo);
        // *STRUCT type of additional schema is unsupported for now
    }

    // -----------------------------------------------------
    //                                       Nest Resolution
    //                                       ---------------
    protected void resolveNestedTemporaryInfo(StringKeyMap<DfTypeArrayInfo> uniqueArrayInfoMap) {
        for (DfTypeArrayInfo arrayInfo : uniqueArrayInfoMap.values()) {
            doResolveNestedTemporaryInfo(uniqueArrayInfoMap, arrayInfo);
        }
    }

    protected void doResolveNestedTemporaryInfo(StringKeyMap<DfTypeArrayInfo> uniqueArrayInfoMap,
            DfTypeArrayInfo arrayInfo) {
        if (arrayInfo.hasNestedArray()) {
            final String nestedArrayTypeName = arrayInfo.getNestedArrayInfo().getTypeName();
            final DfTypeArrayInfo foundInfo = uniqueArrayInfoMap.get(nestedArrayTypeName);
            if (foundInfo != null) {
                arrayInfo.setNestedArrayInfo(foundInfo); // override (resolved)
            }
        }
        if (arrayInfo.hasElementStructInfo()) {
            final DfTypeStructInfo elementStructInfo = arrayInfo.getElementStructInfo();
            doResolveNestedTemporaryArray(uniqueArrayInfoMap, elementStructInfo);
        }
    }

    protected void doResolveNestedTemporaryArray(StringKeyMap<DfTypeArrayInfo> uniqueArrayInfoMap,
            DfTypeStructInfo structInfo) {
        final StringKeyMap<DfColumnMetaInfo> attrInfoMap = structInfo.getAttributeInfoMap();
        for (DfColumnMetaInfo columnInfo : attrInfoMap.values()) {
            if (columnInfo.hasTypeArrayInfo()) {
                final DfTypeArrayInfo attrArrayInfo = columnInfo.getTypeArrayInfo();
                final String attrArrayTypeName = attrArrayInfo.getTypeName();
                final DfTypeArrayInfo foundInfo = uniqueArrayInfoMap.get(attrArrayTypeName);
                System.out.println("*point:" + columnInfo.getColumnName() + " / " + attrArrayTypeName);
                if (foundInfo != null) {
                    System.out.println(" -> found");
                    columnInfo.setTypeArrayInfo(foundInfo); // override (resolved)
                }
            }
            if (columnInfo.hasTypeStructInfo()) {
                final DfTypeStructInfo nestedStructInfo = columnInfo.getTypeStructInfo();
                // if self reference, don't call recursive call, or infinity loop
                // but basically no way because Oracle does not support self reference struct
                if (!structInfo.getTypeName().equalsIgnoreCase(nestedStructInfo.getTypeName())) {
                    doResolveNestedTemporaryArray(uniqueArrayInfoMap, nestedStructInfo); // recursive call
                }
            }
        }
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
        resolveStructAttributeInfo(unifiedSchema, structInfoMap);

        return _structInfoMapMap.get(unifiedSchema);
    }

    protected void resolveStructAttributeInfo(UnifiedSchema unifiedSchema, StringKeyMap<DfTypeStructInfo> structInfoMap) {
        // arrayInfo getting is OK after structInfoMapMap initialization
        // and additional schema's nested things are unsupported, same schema's only
        for (DfTypeStructInfo structInfo : structInfoMap.values()) {
            doResolveStructAttributeInfo(unifiedSchema, structInfoMap, structInfo);
        }
    }

    protected void doResolveStructAttributeInfo(UnifiedSchema unifiedSchema,
            StringKeyMap<DfTypeStructInfo> structInfoMap, DfTypeStructInfo structInfo) {
        final StringSet arrayTypeSet = extractArrayTypeSet(unifiedSchema);
        for (DfColumnMetaInfo metaInfo : structInfo.getAttributeInfoMap().values()) {
            final String dbTypeName = metaInfo.getDbTypeName();
            if (arrayTypeSet.contains(dbTypeName)) { // nested array unused in procedure parameter
                // temporary setup (resolved when calling extractArrayInfoMap())
                // same reason as the process in reflectArrayElementType()
                // *the way to get the info from Oracle is unknown in the first plain
                final DfTypeArrayInfo typeArrayInfo = new DfTypeArrayInfo();
                typeArrayInfo.setTypeName(dbTypeName);
                typeArrayInfo.setElementType("UNKNOWN");
                metaInfo.setTypeArrayInfo(typeArrayInfo);
            }
            final DfTypeStructInfo nestedStructInfo = structInfoMap.get(dbTypeName);
            if (nestedStructInfo != null) { // nested struct
                metaInfo.setTypeStructInfo(nestedStructInfo);
            }
            metaInfo.setProcedureParameter(true); // for default mapping type
        }
    }

    protected StringSet extractArrayTypeSet(UnifiedSchema unifiedSchema) {
        StringSet arrayInfoSet = _arrayTypeSetMap.get(unifiedSchema);
        if (arrayInfoSet != null) {
            return arrayInfoSet;
        }

        final DfArrayExtractorOracle extractor = new DfArrayExtractorOracle(_dataSource);
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
