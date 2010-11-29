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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterExtractorOracle.ProcedureArgumentInfo;
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
        final StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = findFlatArrayInfoMap(unifiedSchema);
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
            final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo(foundInfo.getOwner(), foundInfo.getTypeName());
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
            final DfTypeArrayInfo nestedInfo = new DfTypeArrayInfo(foundInfo.getOwner(), foundInfo.getTypeName());
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

    protected String buildArrayTypeName(ProcedureArgumentInfo argInfo) {
        return argInfo.buildArrayTypeName();
    }

    // ===================================================================================
    //                                                                              Struct
    //                                                                              ======
    /**
     * Extract the map of struct info with nested info.
     * @param unifiedSchema The unified schema. (NotNull)
     * @return The map of struct info. {key = schema.struct-type-name} (NotNull)
     */
    public StringKeyMap<DfTypeStructInfo> extractStructInfoMap(UnifiedSchema unifiedSchema) {
        return findStructInfoMap(unifiedSchema);
    }

    protected StringKeyMap<DfTypeStructInfo> findStructInfoMap(UnifiedSchema unifiedSchema) {
        StringKeyMap<DfTypeStructInfo> structInfoMap = _structInfoMapMap.get(unifiedSchema);
        if (structInfoMap != null) {
            return structInfoMap;
        }

        // initialize per schema
        final DfStructExtractorOracle extractor = new DfStructExtractorOracle(_dataSource, _suppressLogging);
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
        final StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = findFlatArrayInfoMap(unifiedSchema);
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
        final DfTypeArrayInfo typeArrayInfo = new DfTypeArrayInfo(foundInfo.getOwner(), foundInfo.getTypeName());
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
    protected StringKeyMap<DfTypeArrayInfo> findFlatArrayInfoMap(UnifiedSchema unifiedSchema) {
        StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap = _flatArrayInfoMapMap.get(unifiedSchema);
        if (flatArrayInfoMap != null) {
            return flatArrayInfoMap;
        }
        final DfArrayExtractorOracle extractor = new DfArrayExtractorOracle(_dataSource, _suppressLogging);
        flatArrayInfoMap = extractor.extractFlatArrayInfoMap(unifiedSchema);
        _flatArrayInfoMapMap.put(unifiedSchema, flatArrayInfoMap);
        return _flatArrayInfoMapMap.get(unifiedSchema); // all arrays are registered
    }

    // ===================================================================================
    //                                                                       Argument Info
    //                                                                       =============
    protected List<ProcedureArgumentInfo> findProcedureArgumentInfoList(UnifiedSchema unifiedSchema) {
        List<ProcedureArgumentInfo> argInfoList = _argumentInfoListMap.get(unifiedSchema);
        if (argInfoList != null) {
            return argInfoList;
        }
        final DfProcedureParameterExtractorOracle extractor = new DfProcedureParameterExtractorOracle(_dataSource);
        if (_suppressLogging) {
            extractor.suppressLogging();
        }
        argInfoList = extractor.extractProcedureArgumentInfoList(unifiedSchema);
        _argumentInfoListMap.put(unifiedSchema, argInfoList);
        return _argumentInfoListMap.get(unifiedSchema);
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
