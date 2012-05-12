package org.seasar.dbflute.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenManager;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenOutput;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenRequest;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenResource;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenTable;
import org.seasar.dbflute.properties.assistant.freegen.DfFreeGenRequest.DfFreeGenerateResourceType;
import org.seasar.dbflute.properties.assistant.freegen.converter.DfFreeGenMethodConverter;
import org.seasar.dbflute.properties.assistant.freegen.converter.DfFreeGenMethodConverter.DfConvertMethodReflector;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public final class DfFreeGenProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                             Manager
    //                                                                             =======
    protected final DfFreeGenManager _manager = new DfFreeGenManager();

    public DfFreeGenManager getFreeGenManager() {
        return _manager;
    }

    // ===================================================================================
    //                                                                           Converter
    //                                                                           =========
    protected final DfFreeGenMethodConverter _methodConverter = new DfFreeGenMethodConverter();

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    // ; resourceMap = map:{
    //     ; resourceType = XLS
    //     ; resourceFile = ../../...
    // }
    // ; outputMap = map:{
    //     ; templateFile = CsvDto.vm
    //     ; outputDirectory = ../src/main/java
    //     ; package = org.seasar.dbflute...
    //     ; className = FooDto
    // }
    // ; tableMap = map:{
    //     ; sheetName = [sheet-name]
    //     ; rowBeginNumber = 3
    //     ; columnMap = map:{
    //         ; name = 3
    //         ; capName = df:cap(name)
    //         ; uncapName = df:uncap(name)
    //         ; capCamelName = df:capCamel(name)
    //         ; uncapCamelName = df:uncapCamel(name)
    //         ; type = 4
    //     }
    //     ; mappingMap = map:{
    //         ; type = map:{
    //             ; INTEGER = Integer
    //             ; VARCHAR = String
    //         }
    //     }
    // }
    protected Map<String, Object> _freeGenDefinitionMap;

    protected Map<String, Object> getFreeGenDefinitionMap() {
        if (_freeGenDefinitionMap == null) {
            _freeGenDefinitionMap = mapProp("torque.freeGenDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return _freeGenDefinitionMap;
    }

    protected List<DfFreeGenRequest> _freeGenRequestList;

    public List<DfFreeGenRequest> getFreeGenRequestList() {
        if (_freeGenRequestList != null) {
            return _freeGenRequestList;
        }
        _freeGenRequestList = new ArrayList<DfFreeGenRequest>();
        final Map<String, Object> definitionMap = getFreeGenDefinitionMap();
        for (Entry<String, Object> entry : definitionMap.entrySet()) {
            final String requestName = entry.getKey();
            final Object obj = entry.getValue();
            if (!(obj instanceof Map<?, ?>)) {
                String msg = "The property 'freeGenDefinitionMap.value' should be Map: " + obj.getClass();
                throw new DfIllegalPropertyTypeException(msg);
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> elementMap = (Map<String, Object>) obj;
            final DfFreeGenRequest request = createFreeGenerateRequest(requestName, elementMap);

            final Map<String, Object> tableMap = extractTableMap(elementMap);
            try {
                final Map<String, Map<String, String>> mappingMap = extractMappingMap(tableMap);
                final DfFreeGenResource resource = request.getResource();
                if (resource.isResourceTypeXls()) {
                    request.setTable(loadTableFromXls(requestName, resource.getResourceFile(), tableMap, mappingMap));
                } else {
                    String msg = "The resource type is unsupported: " + resource.getResourceType();
                    throw new DfIllegalPropertySettingException(msg);
                }
            } catch (IOException e) {
                String msg = "Failed to load table: request=" + request;
                throw new IllegalStateException(msg, e);
            }

            final DfPackagePathHandler packagePathHandler = new DfPackagePathHandler(getBasicProperties());
            request.setPackagePathHandler(packagePathHandler);
            _freeGenRequestList.add(request);
        }
        return _freeGenRequestList;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> extractTableMap(Map<String, Object> elementMap) {
        final Object obj = elementMap.get("tableMap");
        if (obj == null) {
            String msg = "Not found the tableMap in the FreeGen property: " + elementMap;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
        return (Map<String, Object>) obj;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Map<String, String>> extractMappingMap(Map<String, Object> tableMap) {
        final Object obj = tableMap.get("mappingMap");
        if (obj == null) {
            return DfCollectionUtil.emptyMap();
        }
        return (Map<String, Map<String, String>>) obj;
    }

    protected DfFreeGenRequest createFreeGenerateRequest(String requestName, Map<String, Object> elementMap) {
        final DfFreeGenResource resource;
        {
            @SuppressWarnings("unchecked")
            final Map<String, String> resourceMap = (Map<String, String>) elementMap.get("resourceMap");
            final String resourceTypeStr = resourceMap.get("resourceType");
            final DfFreeGenerateResourceType resourceType = DfFreeGenerateResourceType.valueOf(resourceTypeStr);
            final String resourceFile = resourceMap.get("resourceFile");
            resource = new DfFreeGenResource(resourceType, resourceFile);
        }
        final DfFreeGenOutput output;
        {
            @SuppressWarnings("unchecked")
            final Map<String, String> outputMap = (Map<String, String>) elementMap.get("outputMap");
            final String templateFile = outputMap.get("templateFile");
            final String outputDirectory = outputMap.get("outputDirectory");
            final String pkg = outputMap.get("package");
            final String className = outputMap.get("className");
            output = new DfFreeGenOutput(templateFile, outputDirectory, pkg, className);
        }
        final DfFreeGenRequest request = new DfFreeGenRequest(_manager, requestName, resource, output);
        return request;
    }

    protected DfFreeGenTable loadTableFromXls(String requestName, String resourceFile, Map<String, Object> tableMap,
            Map<String, Map<String, String>> mappingMap) throws IOException {
        final String sheetName = (String) tableMap.get("sheetName");
        if (sheetName == null) {
            String msg = "The sheetName was not found in the FreeGen property: " + requestName;
            throw new DfRequiredPropertyNotFoundException(msg);
        }
        final Integer rowBeginNumber;
        {
            final String numStr = (String) tableMap.get("rowBeginNumber");
            if (numStr == null) {
                String msg = "The rowBeginNumber was not found in the FreeGen property: " + requestName;
                throw new DfRequiredPropertyNotFoundException(msg);
            }
            rowBeginNumber = Integer.valueOf(numStr);
        }
        @SuppressWarnings("unchecked")
        final Map<String, String> columnMap = (Map<String, String>) tableMap.get("columnMap");
        final HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(resourceFile)));
        final HSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            String msg = "Not found the sheet name in the file: name=" + sheetName + " xls=" + resourceFile;
            throw new IllegalStateException(msg);
        }
        final List<Map<String, String>> rowList = new ArrayList<Map<String, String>>();
        for (int i = (rowBeginNumber - 1); i < Integer.MAX_VALUE; i++) {
            final HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            final Map<String, String> resultMap = newLinkedHashMap();
            final List<DfConvertMethodReflector> reflectorList = new ArrayList<DfConvertMethodReflector>();
            boolean exists = false;
            for (Entry<String, String> entry : columnMap.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (value == null) {
                    String msg = "Not found the value of the key in FreeGen " + requestName + ": " + key;
                    throw new DfIllegalPropertySettingException(msg);
                }
                if (processColumnValue(requestName, columnMap, row, resultMap, key, value, reflectorList, mappingMap)) {
                    exists = true;
                }
            }
            if (exists) {
                rowList.add(resultMap);
            } else { // means empty row
                break;
            }
            for (DfConvertMethodReflector reflector : reflectorList) {
                reflector.reflect();
            }
        }
        return new DfFreeGenTable(sheetName, rowList);
    }

    protected boolean processColumnValue(final String requestName, final Map<String, String> columnMap,
            final HSSFRow row, final Map<String, String> resultMap, final String key, final String value,
            List<DfConvertMethodReflector> reflectorList, Map<String, Map<String, String>> mappingMap) {
        if (processConvertMethod(requestName, resultMap, key, value, reflectorList)) {
            return false;
        }
        // normal setting (cell number)
        boolean exists = false;
        final Integer cellNumber;
        try {
            cellNumber = Integer.valueOf(value) - 1;
        } catch (NumberFormatException e) {
            String msg = "The property value should be Integer in FreeGen " + requestName + ":";
            msg = msg + " key=" + key + " value=" + value;
            throw new DfIllegalPropertySettingException(msg);
        }
        final HSSFCell cell = row.getCell(cellNumber);
        if (cell == null) {
            return false;
        }
        final HSSFRichTextString cellValue = cell.getRichStringCellValue();
        if (cellValue == null) {
            return false;
        }
        exists = true;
        String resultValue = cellValue.getString();
        final Map<String, String> mapping = mappingMap.get(key);
        if (mapping != null) {
            final String mappingValue = mapping.get(resultValue);
            if (mappingValue != null) {
                resultValue = mappingValue;
            }
        }
        resultMap.put(key, resultValue);
        return exists;
    }

    protected boolean processConvertMethod(final String requestName, final Map<String, String> resultMap,
            final String key, final String value, List<DfConvertMethodReflector> reflectorList) {
        return _methodConverter.processConvertMethod(requestName, resultMap, key, value, reflectorList);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasSimpleDtoDefinition() {
        return !isEmptyDefinition() && hasDefinitionProperty("baseDtoPackage");
    }

    public boolean hasSimpleCDefDefinition() {
        return !isEmptyDefinition() && hasDefinitionProperty("simpleCDefClass");
    }

    protected boolean isEmptyDefinition() {
        return getFreeGenDefinitionMap().isEmpty();
    }

    protected boolean hasDefinitionProperty(String key) {
        return getFreeGenDefinitionMap().containsKey(key);
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public String getSimpleDtoOutputDirectory() {
        final String value = (String) getFreeGenDefinitionMap().get("simpleDtoOutputDirectory");
        return doGetOutputDirectory(value);
    }

    public String getDtoMapperOutputDirectory() {
        final String value = (String) getFreeGenDefinitionMap().get("dtoMapperOutputDirectory");
        return doGetOutputDirectory(value);
    }

    public String getSimpleCDefOutputDirectory() {
        final String value = (String) getFreeGenDefinitionMap().get("simpleCDefOutputDirectory");
        return doGetOutputDirectory(value);
    }

    protected String doGetOutputDirectory(String value) {
        final String baseDir = getBasicProperties().getGenerateOutputDirectory();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(value)) {
            if (value.startsWith("~/")) {
                return "./" + Srl.substringFirstRear(value, "~/");
            } else {
                return baseDir + "/" + value;
            }
        } else {
            return baseDir;
        }
    }

    // ===================================================================================
    //                                                                            DTO Info
    //                                                                            ========
    public String getBaseDtoPackage() {
        return getPropertyRequired("baseDtoPackage");
    }

    public String getExtendedDtoPackage() {
        return getPropertyRequired("extendedDtoPackage");
    }

    public String getBaseDtoPrefix() {
        return getPropertyIfNullEmpty("baseDtoPrefix");
    }

    public String getBaseDtoSuffix() {
        return getPropertyIfNullEmpty("baseDtoSuffix");
    }

    public String getExtendedDtoPrefix() {
        return getPropertyIfNullEmpty("extendedDtoPrefix");
    }

    public String getExtendedDtoSuffix() {
        return getPropertyIfNullEmpty("extendedDtoSuffix");
    }

    public String deriveExtendedDtoClassName(String baseDtoClassName) {
        String name = Srl.substringFirstRear(baseDtoClassName, getBaseDtoPrefix());
        name = Srl.substringLastFront(name, getBaseDtoSuffix());
        final String prefix = getExtendedDtoPrefix();
        final String suffix = getExtendedDtoSuffix();
        return prefix + name + suffix;
    }

    // ===================================================================================
    //                                                                              Mapper
    //                                                                              ======
    public String getBaseMapperPackage() {
        final String prop = getProperty("baseMapperPackage");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(prop)) {
            return prop;
        }
        return getExtendedMapperPackage() + ".bs"; // compatible
    }

    public String getExtendedMapperPackage() {
        final String prop = getProperty("extendedMapperPackage");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(prop)) {
            return prop;
        }
        return getPropertyIfNullEmpty("dtoMapperPackage"); // old style
    }

    public boolean isUseDtoMapper() {
        final String dtoMapperPackage = getExtendedMapperPackage();
        return dtoMapperPackage != null && dtoMapperPackage.trim().length() > 0;
    }

    public String getMapperSuffix() { // used for building class name
        return "Mapper"; // however NOT uniform management
    }

    public String deriveExtendedMapperClassName(String baseMapperClassName) {
        return Srl.substringFirstRear(baseMapperClassName, getBaseDtoPrefix());
    }

    public boolean isMappingReverseReference() {
        // default is false because cyclic references may have problems
        return isProperty("isMappingReverseReference", false, getFreeGenDefinitionMap());
    }

    // ===================================================================================
    //                                                                           CDef Info
    //                                                                           =========
    public String getSimpleCDefClass() {
        return getPropertyRequired("simpleCDefClass");
    }

    public String getSimpleCDefPackage() {
        return getPropertyRequired("simpleCDefPackage");
    }

    protected Set<String> _simpleCDefTargetSet;

    protected Set<String> getSimpleCDefTargetSet() {
        if (_simpleCDefTargetSet != null) {
            return _simpleCDefTargetSet;
        }
        final Object obj = getFreeGenDefinitionMap().get("simpleCDefTargetList");
        if (obj == null) {
            _simpleCDefTargetSet = DfCollectionUtil.emptySet();
            return _simpleCDefTargetSet;
        }
        @SuppressWarnings("unchecked")
        final List<String> targetList = (List<String>) obj;
        _simpleCDefTargetSet = DfCollectionUtil.newHashSet(targetList);
        return _simpleCDefTargetSet;
    }

    public boolean isSimpleCDefTarget(String classificationName) {
        final Set<String> targetSet = getSimpleCDefTargetSet();
        if (targetSet.isEmpty()) {
            return true;
        }
        return targetSet.contains(classificationName);
    }

    public List<String> getSimpleCDefTargetClassificationNameList() {
        final DfClassificationProperties prop = getClassificationProperties();
        final List<String> classificationNameList = prop.getClassificationNameList();
        final List<String> filteredList = new ArrayList<String>();
        for (String classificationName : classificationNameList) {
            if (isSimpleCDefTarget(classificationName)) {
                filteredList.add(classificationName);
            }
        }
        return filteredList;
    }

    public boolean isClassificationDeployment() { //  if true, SimpleCDef should be true too
        return isProperty("isClassificationDeployment", false, getFreeGenDefinitionMap());
    }

    // ===================================================================================
    //                                                                              JSONIC
    //                                                                              ======
    protected Map<String, String> _jsonicDecorationMap;

    protected Map<String, String> getJSonicDecorationMap() {
        if (_jsonicDecorationMap != null) {
            return _jsonicDecorationMap;
        }
        final String key = "jsonicDecorationMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getFreeGenDefinitionMap().get(key);
        if (map != null) {
            _jsonicDecorationMap = map;
        } else {
            _jsonicDecorationMap = DfCollectionUtil.emptyMap();
        }
        return _jsonicDecorationMap;
    }

    public boolean hasJsonicDecorationDatePattern() {
        return getJsonicDecorationDatePattern() != null;
    }

    public String getJsonicDecorationDatePattern() {
        return getJSonicDecorationMap().get("datePattern");
    }

    public boolean hasJsonicDecorationTimestampPattern() {
        return getJsonicDecorationTimestampPattern() != null;
    }

    public String getJsonicDecorationTimestampPattern() {
        return getJSonicDecorationMap().get("timestampPattern");
    }

    public boolean hasJsonicDecorationTimePattern() {
        return getJsonicDecorationTimePattern() != null;
    }

    public String getJsonicDecorationTimePattern() {
        return getJSonicDecorationMap().get("timePattern");
    }

    // ===================================================================================
    //                                                                      JsonPullParser
    //                                                                      ==============
    protected Map<String, String> _jsonPullParserDecorationMap;

    protected Map<String, String> getJsonPullParserDecorationMap() {
        if (_jsonPullParserDecorationMap != null) {
            return _jsonPullParserDecorationMap;
        }
        final String key = "jsonPullParserDecorationMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getFreeGenDefinitionMap().get(key);
        if (map != null) {
            _jsonPullParserDecorationMap = map;
        } else {
            _jsonPullParserDecorationMap = DfCollectionUtil.emptyMap();
        }
        return _jsonPullParserDecorationMap;
    }

    public boolean isJsonPullParserBasicDecorate() {
        return isProperty("isBasicDecorate", false, getJsonPullParserDecorationMap());
    }

    // ===================================================================================
    //                                                                                 GWT
    //                                                                                 ===
    protected Map<String, String> _gwtDecorationMap;

    protected Map<String, String> getGwtDecorationMap() {
        if (_gwtDecorationMap != null) {
            return _gwtDecorationMap;
        }
        final String key = "gwtDecorationMap";
        @SuppressWarnings("unchecked")
        final Map<String, String> map = (Map<String, String>) getFreeGenDefinitionMap().get(key);
        if (map != null) {
            _gwtDecorationMap = map;
        } else {
            _gwtDecorationMap = DfCollectionUtil.emptyMap();
        }
        return _gwtDecorationMap;
    }

    public boolean isGwtDecorationSuppressJavaDependency() {
        return isProperty("isSuppressJavaDependency", false, getGwtDecorationMap());
    }

    // ===================================================================================
    //                                                                          Field Name
    //                                                                          ==========
    public String getFieldInitCharType() {
        return getPropertyIfNullEmpty("fieldInitCharType");
    }

    public boolean isFieldNonPrefix() {
        return isProperty("isFieldNonPrefix", false);
    }

    public String buildFieldName(String javaName) {
        final String fieldInitCharType = getFieldInitCharType();
        final boolean nonPrefix = isFieldNonPrefix();
        return doBuildFieldName(javaName, fieldInitCharType, nonPrefix);
    }

    protected static String doBuildFieldName(String javaName, String fieldInitCharType, boolean nonPrefix) {
        final String defaultType = "UNCAP";
        if (Srl.is_Null_or_TrimmedEmpty(fieldInitCharType)) {
            fieldInitCharType = defaultType;
        }
        if (Srl.equalsIgnoreCase(fieldInitCharType, "BEANS")) {
            return doBuildFieldName(javaName, true, false, nonPrefix);
        } else if (Srl.equalsIgnoreCase(fieldInitCharType, "CAP")) {
            return doBuildFieldName(javaName, false, true, nonPrefix);
        } else if (Srl.equalsIgnoreCase(fieldInitCharType, defaultType)) {
            return doBuildFieldName(javaName, false, false, nonPrefix);
        } else {
            String msg = "Unknown fieldInitCharType: " + fieldInitCharType;
            throw new DfIllegalPropertySettingException(msg);
        }
    }

    protected static String doBuildFieldName(String javaName, boolean initBeansProp, boolean initCap, boolean nonPrefix) {
        String name = javaName;
        if (initBeansProp) {
            name = Srl.initBeansProp(name);
        } else {
            if (initCap) {
                name = Srl.initCap(name);
            } else {
                name = Srl.initUncap(name);
            }
        }
        if (!nonPrefix) {
            name = Srl.connectPrefix(name, "_", "");
        }
        return name;
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getPropertyRequired(String key) {
        final String value = getProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " simpleDtoDefinitionMap=" + getFreeGenDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getPropertyIfNullEmpty(String key) {
        final String value = getProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getProperty(String key) {
        return (String) getFreeGenDefinitionMap().get(key);
    }

    protected boolean isProperty(String key, boolean defaultValue) {
        return isProperty(key, defaultValue, getFreeGenDefinitionMap());
    }
}