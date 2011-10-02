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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
import org.seasar.dbflute.logic.jdbc.metadata.procedure.DfProcedureParameterNativeExtractorOracle.ProcedureArgumentInfo;
import org.seasar.dbflute.logic.jdbc.metadata.various.array.DfArrayExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.various.struct.DfStructExtractorOracle;
import org.seasar.dbflute.properties.DfDatabaseProperties;
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
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected final DataSource _dataSource;

    // -----------------------------------------------------
    //                                       Basic GreatWall
    //                                       ---------------
    /** The info map of ARRAY for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _arrayInfoMapMap = newHashMap();

    /** The info map of ARRAY as flat for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _flatArrayInfoMapMap = newHashMap();

    /** The info map of STRUCT type for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structInfoMapMap = newHashMap();

    /** The info map of ARRAY set for cache. */
    protected final Map<UnifiedSchema, StringSet> _arrayTypeSetMap = newHashMap();

    // -----------------------------------------------------
    //                                      DBLink GreatWall
    //                                      ----------------
    /** The info map of ARRAY to DB link for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _arrayInfoMapToDBLinkMap = newHashMap();

    /** The info map of ARRAY as flat to DB link for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _flatArrayInfoMapToDBLinkMap = newHashMap();

    /** The info map of STRUCT type to DB link for cache. */
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structInfoMapToDBLinkMap = newHashMap();

    /** The info map of ARRAY set to DB link for cache. */
    protected final Map<UnifiedSchema, StringSet> _arrayTypeSetToDBLinkMap = newHashMap();

    // -----------------------------------------------------
    //                                         Argument Info
    //                                         -------------
    /** The info map of procedure argument for cache. */
    protected final Map<UnifiedSchema, List<ProcedureArgumentInfo>> _argumentInfoListMap = newHashMap();

    /** The info map of procedure argument to DB link for cache. */
    protected final Map<String, List<ProcedureArgumentInfo>> _argumentInfoListToDBLinkMap = newHashMap();

    // -----------------------------------------------------
    //                                       ResultMap Cache
    //                                       ---------------
    protected final Map<UnifiedSchema, StringKeyMap<Integer>> _parameterOverloadResultMapMap = newHashMap();
    protected final Map<String, StringKeyMap<Integer>> _parameterOverloadToDBLinkResultMapMap = newHashMap();
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeArrayInfo>> _parameterArrayResultMapMap = newHashMap();
    protected final Map<UnifiedSchema, StringKeyMap<DfTypeStructInfo>> _structResultMapMap = newHashMap();

    // DBLink procedure's GreatWalls are unsupported yet
    //protected final Map<String, StringKeyMap<DfTypeArrayInfo>> _parameterArrayInfoToDBLinkResultMapMap = newHashMap();
    //protected final Map<String, StringKeyMap<DfTypeStructInfo>> _structInfoToDBLinkResultMapMap = newHashMap();

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
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
     * {@inheritDoc}
     */
    public StringKeyMap<Integer> extractParameterOverloadInfoMap(UnifiedSchema unifiedSchema) {
        final StringKeyMap<Integer> overloadInfoToDBLinkMap = _parameterOverloadResultMapMap.get(unifiedSchema);
        if (overloadInfoToDBLinkMap == null) {
            _parameterOverloadResultMapMap.put(unifiedSchema, findParameterOverloadInfoMap(unifiedSchema));
        }
        // {key = (packageName.)procedureName.columnName, value = overloadNo}
        return _parameterOverloadResultMapMap.get(unifiedSchema);
    }

    /**
     * Extract the map of parameter's overload info for DB link. <br />
     * Same name and different type parameters of overload are unsupported. 
     * @param dbLinkName The name of DB link to extract. (NotNull)
     * @return The map of parameter's array info. {key = (packageName.)procedureName.columnName, value = overloadNo} (NotNull)
     */
    public StringKeyMap<Integer> extractParameterOverloadInfoToDBLinkMap(String dbLinkName) {
        final StringKeyMap<Integer> overloadInfoToDBLinkMap = _parameterOverloadToDBLinkResultMapMap.get(dbLinkName);
        if (overloadInfoToDBLinkMap == null) {
            _parameterOverloadToDBLinkResultMapMap.put(dbLinkName, findParameterOverloadInfoToDBLinkMap(dbLinkName));
        }
        return _parameterOverloadToDBLinkResultMapMap.get(dbLinkName);
    }

    protected StringKeyMap<Integer> findParameterOverloadInfoMap(UnifiedSchema unifiedSchema) {
        return doFindParameterOverloadInfoMap(findProcedureArgumentInfoList(unifiedSchema));
    }

    protected StringKeyMap<Integer> findParameterOverloadInfoToDBLinkMap(String dbLinkName) {
        return doFindParameterOverloadInfoMap(findProcedureArgumentInfoToDBLinkList(dbLinkName));
    }

    protected StringKeyMap<Integer> doFindParameterOverloadInfoMap(List<ProcedureArgumentInfo> infoList) {
        final StringKeyMap<Integer> infoMap = StringKeyMap.createAsFlexibleOrdered();
        for (int i = 0; i < infoList.size(); i++) {
            final ProcedureArgumentInfo info = infoList.get(i);
            final String argumentName = info.getArgumentName();
            final String overload = info.getOverload();
            if (Srl.is_Null_or_TrimmedEmpty(argumentName) || Srl.is_Null_or_TrimmedEmpty(overload)) {
                continue;
            }
            // this is not correct but DBFlute treats overload methods as one method
            // (overload info is only referred to determinate whether the procedure has overload methods)
            final String key = generateParameterInfoMapKey(info.getPackageName(), info.getObjectName(), argumentName);
            infoMap.put(key, DfTypeUtil.toInteger(overload));
        }
        return infoMap;
    }

    // ===================================================================================
    //                                                                               Array
    //                                                                               =====
    /**
     * {@inheritDoc}
     */
    public StringKeyMap<DfTypeArrayInfo> extractParameterArrayInfoMap(UnifiedSchema unifiedSchema) {
        final StringKeyMap<DfTypeArrayInfo> overloadInfoToDBLinkMap = _parameterArrayResultMapMap.get(unifiedSchema);
        if (overloadInfoToDBLinkMap == null) {
            _parameterArrayResultMapMap.put(unifiedSchema, findParameterArrayInfoMap(unifiedSchema));
        }
        return _parameterArrayResultMapMap.get(unifiedSchema);
    }

    protected StringKeyMap<DfTypeArrayInfo> findParameterArrayInfoMap(UnifiedSchema unifiedSchema) {
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
            final DfTypeArrayInfo arrayInfo = new DfTypeArrayInfo(foundInfo.getUnifiedSchema(), foundInfo.getTypeName());
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
            final DfTypeArrayInfo nestedInfo = new DfTypeArrayInfo(foundInfo.getUnifiedSchema(),
                    foundInfo.getTypeName());
            nestedInfo.setElementType(foundInfo.getElementType());
            arrayInfo.setNestedArrayInfo(nestedInfo);
            processArrayNestedElement(unifiedSchema, flatArrayInfoMap, nestedInfo); // recursive call
            // *ARRAY type of additional schema is unsupported for now
        }
        // STRUCT element
        final StringKeyMap<DfTypeStructInfo> structInfoMap = findParameterStructInfoMap(unifiedSchema);
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
     * {@inheritDoc}
     */
    public StringKeyMap<DfTypeStructInfo> extractStructInfoMap(UnifiedSchema unifiedSchema) {
        final StringKeyMap<DfTypeStructInfo> structInfoToDBLinkMap = _structResultMapMap.get(unifiedSchema);
        if (structInfoToDBLinkMap == null) {
            _structResultMapMap.put(unifiedSchema, findParameterStructInfoMap(unifiedSchema));
        }
        return _structResultMapMap.get(unifiedSchema);
    }

    protected StringKeyMap<DfTypeStructInfo> findParameterStructInfoMap(UnifiedSchema unifiedSchema) {
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
        for (DfColumnMeta columnInfo : structInfo.getAttributeInfoMap().values()) {
            doResolveStructAttributeInfo(unifiedSchema, structInfoMap, flatArrayInfoMap, structInfo, columnInfo);
        }
    }

    protected void doResolveStructAttributeInfo(UnifiedSchema unifiedSchema,
            StringKeyMap<DfTypeStructInfo> structInfoMap, StringKeyMap<DfTypeArrayInfo> flatArrayInfoMap,
            DfTypeStructInfo structInfo, DfColumnMeta columnInfo) {
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
        final DfTypeArrayInfo typeArrayInfo = new DfTypeArrayInfo(foundInfo.getUnifiedSchema(), foundInfo.getTypeName());
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
        return DfProcedureParameterNativeExtractorOracle.generateParameterInfoMapKey(catalog, procedureName,
                parameterName);
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
        final DfProcedureParameterNativeExtractorOracle extractor = createProcedureParameterExtractorOracle();
        argInfoList = extractor.extractProcedureArgumentInfoList(unifiedSchema);
        _argumentInfoListMap.put(unifiedSchema, argInfoList);
        return _argumentInfoListMap.get(unifiedSchema);
    }

    protected List<ProcedureArgumentInfo> findProcedureArgumentInfoToDBLinkList(String dbLinkName) {
        List<ProcedureArgumentInfo> argInfoList = _argumentInfoListToDBLinkMap.get(dbLinkName);
        if (argInfoList != null) {
            return argInfoList;
        }
        final DfProcedureParameterNativeExtractorOracle extractor = createProcedureParameterExtractorOracle();
        argInfoList = extractor.extractProcedureArgumentInfoToDBLinkList(dbLinkName);
        _argumentInfoListToDBLinkMap.put(dbLinkName, argInfoList);
        return _argumentInfoListToDBLinkMap.get(dbLinkName);
    }

    protected DfProcedureParameterNativeExtractorOracle createProcedureParameterExtractorOracle() {
        return new DfProcedureParameterNativeExtractorOracle(_dataSource, _suppressLogging);
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
