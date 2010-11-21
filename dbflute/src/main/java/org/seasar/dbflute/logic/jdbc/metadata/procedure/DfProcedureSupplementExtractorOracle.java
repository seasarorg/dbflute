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
import java.util.Map.Entry;

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

    /** The info map of ARRAY as flat for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _flatArrayInfoMapMap = DfCollectionUtil
            .newHashMap();

    /** The info map of STRUCT type for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structInfoMapMap = DfCollectionUtil
            .newHashMap();

    /** The info map of ARRAY set for cache. */
    protected final Map<UnifiedSchema, StringSet> _arrayTypeSetMap = DfCollectionUtil.newHashMap();

    /** The info map of procedure argument for cache. */
    protected final Map<UnifiedSchema, List<ProcedureArgumentInfo>> _argumentInfoListMap = DfCollectionUtil
            .newHashMap();

    protected boolean _suppressLogging;

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
        final StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = extractFlatArrayInfoMap(unifiedSchema);
        for (int i = 0; i < argInfoList.size(); i++) {
            final ProcedureArgumentInfo argInfo = argInfoList.get(i);
            final String argumentName = argInfo.getArgumentName();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName)) {
                continue;
            }
            final String realTypeName = buildArrayTypeName(argInfo);
            final DfTypeArrayInfo foundInfo = flatArrayInfoMap.get(realTypeName);
            if (foundInfo == null) {
                continue;
            }
            final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo();
            arrayInfo.setTypeName(foundInfo.getTypeName());
            arrayInfo.setElementType(foundInfo.getElementType());
            processArrayNestedElement(unifiedSchema, flatArrayInfoMap, arrayInfo);
            final String packageName = argInfo.getPackageName();
            final String objectName = argInfo.getObjectName();
            final String key = generateParameterInfoMapKey(packageName, objectName, argumentName);
            parameterArrayInfoMap.put(key, arrayInfo);
        }
        log("Array Parameter: " + unifiedSchema);
        for (Entry<String, DfTypeArrayInfo> entry : parameterArrayInfoMap.entrySet()) {
            log("  " + entry.getKey() + " = " + entry.getValue());
        }
        _arrayInfoMapMap.put(unifiedSchema, parameterArrayInfoMap);
        return _arrayInfoMapMap.get(unifiedSchema);
    }

    protected void processArrayNestedElement(UnifiedSchema unifiedSchema,
            final StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap, DfTypeArrayInfo arrayInfo) {
        // ARRAY element
        final DfTypeArrayInfo foundInfo = flatArrayInfoMap.get(arrayInfo.getElementType());
        if (foundInfo != null) {
            final DfTypeArrayInfo nestedInfo = new DfTypeArrayInfo();
            nestedInfo.setTypeName(foundInfo.getTypeName());
            nestedInfo.setElementType(foundInfo.getElementType());
            arrayInfo.setNestedArrayInfo(nestedInfo);
            processArrayNestedElement(unifiedSchema, flatArrayInfoMap, nestedInfo); // recursive call
            // *ARRAY type of additional schema is unsupported for now
        }
        // STRUCT element
        final StringKeyMap<DfTypeStructInfo> structInfoMap = findStructInfoMap(unifiedSchema);
        final DfTypeStructInfo structInfo = structInfoMap.get(arrayInfo.getElementType());
        if (structInfo != null) {
            // the structInfo has already been resolved about nested objects
            arrayInfo.setElementStructInfo(structInfo);
            // *STRUCT type of additional schema is unsupported for now
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

        // set up struct attribute's additional info
        resolveStructAttributeInfo(unifiedSchema, structInfoMap);

        log("Struct Info: " + unifiedSchema);
        for (DfTypeStructInfo structInfo : structInfoMap.values()) {
            log("  " + structInfo.toString());
        }
        _structInfoMapMap.put(unifiedSchema, structInfoMap);
        return _structInfoMapMap.get(unifiedSchema);
    }

    protected void resolveStructAttributeInfo(UnifiedSchema unifiedSchema, StringKeyMap<DfTypeStructInfo> structInfoMap) {
        final StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = extractFlatArrayInfoMap(unifiedSchema);
        // and additional schema's nested things are unsupported, same schema's only
        for (DfTypeStructInfo structInfo : structInfoMap.values()) {
            doResolveStructAttributeInfo(unifiedSchema, structInfoMap, flatArrayInfoMap, structInfo);
        }
    }

    protected void doResolveStructAttributeInfo(UnifiedSchema unifiedSchema,
            StringKeyMap<DfTypeStructInfo> structInfoMap, StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap,
            DfTypeStructInfo structInfo) {
        for (DfColumnMetaInfo columnInfo : structInfo.getAttributeInfoMap().values()) {
            doResolveStructAttributeInfo(unifiedSchema, structInfoMap, flatArrayInfoMap, structInfo, columnInfo);
        }
    }

    protected void doResolveStructAttributeInfo(UnifiedSchema unifiedSchema,
            StringKeyMap<DfTypeStructInfo> structInfoMap, StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap,
            DfTypeStructInfo structInfo, DfColumnMetaInfo columnInfo) {
        final String attrTypeName = columnInfo.getDbTypeName();
        final DfTypeArrayInfo arrayInfo = doResolveStructAttributeArray(structInfoMap, flatArrayInfoMap, attrTypeName);
        if (arrayInfo != null) { // array attribute
            columnInfo.setTypeArrayInfo(arrayInfo);
        }
        final DfTypeStructInfo nestedStructInfo = structInfoMap.get(attrTypeName);
        if (nestedStructInfo != null) { // nested struct
            columnInfo.setTypeStructInfo(nestedStructInfo);
        }
        columnInfo.setProcedureParameter(true); // for default mapping type
    }

    protected DfTypeArrayInfo doResolveStructAttributeArray(StringKeyMap<DfTypeStructInfo> structInfoMap,
            StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap, String attrTypeName) {
        if (!flatArrayInfoMap.containsKey(attrTypeName)) {
            return null;
        }
        final DfTypeArrayInfo foundInfo = flatArrayInfoMap.get(attrTypeName);
        final DfTypeArrayInfo typeArrayInfo = new DfTypeArrayInfo();
        typeArrayInfo.setTypeName(foundInfo.getTypeName());
        final String elementType = foundInfo.getElementType();
        typeArrayInfo.setElementType(elementType);
        if (flatArrayInfoMap.containsKey(elementType)) { // array in array in ...
            final DfTypeArrayInfo nestedArrayInfo = doResolveStructAttributeArray(structInfoMap, flatArrayInfoMap,
                    elementType); // recursive call
            typeArrayInfo.setNestedArrayInfo(nestedArrayInfo);
        } else if (structInfoMap.containsKey(elementType)) { // struct in array in ...
            final DfTypeStructInfo elementStructInfo = structInfoMap.get(elementType);
            typeArrayInfo.setElementStructInfo(elementStructInfo);
        }
        return typeArrayInfo;
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
    //                                                                     Flat Array Info
    //                                                                     ===============
    protected StringKeyMap<DfTypeArrayInfo> extractFlatArrayInfoMap(UnifiedSchema unifiedSchema) {
        StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = _flatArrayInfoMapMap.get(unifiedSchema);
        if (flatArrayInfoMap != null) {
            return flatArrayInfoMap;
        }
        flatArrayInfoMap = StringKeyMap.createAsFlexibleOrdered();
        final List<ProcedureArgumentInfo> argInfoList = findProcedureArgumentInfoList(unifiedSchema);
        for (int i = 0; i < argInfoList.size(); i++) {
            final ProcedureArgumentInfo argInfo = argInfoList.get(i);
            final String argumentName = argInfo.getArgumentName();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName)) {
                continue;
            }
            final String dataType = argInfo.getDataType();
            if (!isDataTypeArray(dataType)) {
                continue;
            }
            final String typeName = argInfo.getTypeName();
            if (Srl.is_Null_or_TrimmedEmpty(typeName)) {
                continue;
            }
            setupFlatArrayInfo(flatArrayInfoMap, argInfoList, argInfo, i);
        }
        final StringSet allArrayTypeSet = extractAllArrayTypeSet(unifiedSchema);
        for (String allArrayTypeName : allArrayTypeSet) {
            if (!flatArrayInfoMap.containsKey(allArrayTypeName)) {
                final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo();
                arrayInfo.setTypeName(allArrayTypeName);
                arrayInfo.setElementType("Unknown"); // the way to get the info is also unknown
                flatArrayInfoMap.put(allArrayTypeName, arrayInfo);
            }
        }
        log("Flat All Array: " + unifiedSchema);
        for (DfTypeArrayInfo arrayInfo : flatArrayInfoMap.values()) {
            log("  " + arrayInfo);
        }
        _flatArrayInfoMapMap.put(unifiedSchema, flatArrayInfoMap);
        return _flatArrayInfoMapMap.get(unifiedSchema); // all arrays are registered
    }

    protected void setupFlatArrayInfo(StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap,
            List<ProcedureArgumentInfo> argInfoList, ProcedureArgumentInfo argInfo, int index) {
        final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo();
        final String realTypeName = buildArrayTypeName(argInfo);
        arrayInfo.setTypeName(realTypeName);
        final boolean nestedArray = reflectArrayElementType(argInfoList, index, arrayInfo);
        flatArrayInfoMap.put(realTypeName, arrayInfo);
        if (nestedArray) {
            final int nextIndex = (index + 1);
            final ProcedureArgumentInfo nextArgInfo = argInfoList.get(nextIndex);
            setupFlatArrayInfo(flatArrayInfoMap, argInfoList, nextArgInfo, nextIndex); // recursive call
        }
    }

    protected boolean reflectArrayElementType(List<ProcedureArgumentInfo> argInfoList, int i, DfTypeArrayInfo arrayInfo) {
        boolean nestedArray = false;
        final int nextIndex = (i + 1);
        if (argInfoList.size() > nextIndex) { // element type is in data type of next record
            final ProcedureArgumentInfo nextInfo = argInfoList.get(nextIndex);
            if (Srl.is_Null_or_TrimmedEmpty(nextInfo.getArgumentName())) { // element record's argument is null
                final String typeName = nextInfo.getTypeName();
                final String dataType = nextInfo.getDataType();
                final String elementType;
                if (Srl.is_NotNull_and_NotTrimmedEmpty(typeName)) { // not scalar (array or struct)
                    if (isDataTypeArray(dataType)) { // can get one more record (Oracle's specification)
                        nestedArray = true;
                    }
                    elementType = buildArrayTypeName(nextInfo);
                } else { // scalar element
                    elementType = dataType;
                }
                arrayInfo.setElementType(elementType);
            }
        } else {
            log("*Unexpected, no next record for array meta: " + arrayInfo);
            arrayInfo.setElementType("Unknown"); // basically no way but just in case
        }
        return nestedArray;
    }

    protected StringSet extractAllArrayTypeSet(UnifiedSchema unifiedSchema) {
        StringSet arrayInfoSet = _arrayTypeSetMap.get(unifiedSchema);
        if (arrayInfoSet != null) {
            return arrayInfoSet;
        }
        final DfArrayExtractorOracle extractor = new DfArrayExtractorOracle(_dataSource);
        _arrayTypeSetMap.put(unifiedSchema, extractor.extractArrayTypeSet(unifiedSchema));
        return _arrayTypeSetMap.get(unifiedSchema);
    }

    protected boolean isDataTypeArray(String dataType) {
        return Srl.containsAnyIgnoreCase(dataType, "TABLE", "VARRAY");
    }

    protected boolean isDataTypeStruct(String dataType) {
        return Srl.equalsIgnoreCase(dataType, "OBJECT");
    }

    protected String buildArrayTypeName(ProcedureArgumentInfo argInfo) {
        final String typeName = argInfo.getTypeName();
        final String typeSubName = argInfo.getTypeSubName();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(typeSubName)) {
            // *typeOwner handling is under review
            //final String typeOwner = argInfo.getTypeOwner();
            return typeName + "." + typeSubName;
        } else {
            // *it may need to add typeOwner if additional schema at the future
            return typeName;
        }
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
        columnList.add("TYPE_OWNER");
        columnList.add("TYPE_NAME");
        columnList.add("TYPE_SUBNAME");
        final List<Map<String, String>> resultList;
        try {
            log(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because of assist info
            log("Failed to select supplement info: " + continued.getMessage());
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
            info.setTypeOwner(map.get("TYPE_OWNER"));
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
        protected String _typeOwner;
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

    public void suppressLogging() {
        _suppressLogging = true;
    }
}
