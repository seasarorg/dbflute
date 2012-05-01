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
import org.seasar.dbflute.properties.assistant.freegenerate.DfFreeGenManager;
import org.seasar.dbflute.properties.assistant.freegenerate.DfFreeGenRequest;
import org.seasar.dbflute.properties.assistant.freegenerate.DfFreeGenRequest.DfFreeGenerateResourceType;
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
    //                                                                      Definition Map
    //                                                                      ==============
    // resourceType = XLS
    // resourceFile = ../../...
    // templateFile = CsvDto.vm
    // outputDirectory = ./freegen/...
    // package = org.seasar.dbflute...
    // className = FooDto
    // tableMap = map:{
    //     ; sheetName = [sheet-name]
    //     ; rowStart = 3
    //     ; attributeMap = map:{
    //         ; column = 3
    //         ; type = 3
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
            final String templateFile = (String) elementMap.get("templateFile");
            final String outputDirectory = (String) elementMap.get("outputDirectory");
            final String pkg = (String) elementMap.get("package");
            final String className = (String) elementMap.get("className");
            request.setTemplateFile(templateFile);
            request.setOutputDirectory(outputDirectory);
            request.setPackage(pkg);
            request.setClassName(className);
            @SuppressWarnings("unchecked")
            final Map<String, Object> tableMap = (Map<String, Object>) elementMap.get("tableMap");
            request.setTableMap(tableMap);
            try {
                request.setAttributeList(loadXls(requestName, request.getResourceFile(), tableMap));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            final DfPackagePathHandler packagePathHandler = new DfPackagePathHandler(getBasicProperties());
            request.setPackagePathHandler(packagePathHandler);
            _freeGenRequestList.add(request);
        }
        return _freeGenRequestList;
    }

    protected DfFreeGenRequest createFreeGenerateRequest(String requestName, Map<String, Object> elementMap) {
        final String typeStr = (String) elementMap.get("resourceType");
        final String resourceFile = (String) elementMap.get("resourceFile");
        final DfFreeGenerateResourceType resourceType = DfFreeGenerateResourceType.valueOf(typeStr);
        final DfFreeGenRequest request = new DfFreeGenRequest(_manager, requestName, resourceType, resourceFile);
        return request;
    }

    protected List<Map<String, String>> loadXls(String requestName, String resourceFile, Map<String, Object> tableMap)
            throws IOException {
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
        final Map<String, String> attributeMap = (Map<String, String>) tableMap.get("attributeMap");
        final HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(new File(resourceFile)));
        final HSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            String msg = "Not found the sheet name in the file: name=" + sheetName + " xls=" + resourceFile;
            throw new IllegalStateException(msg);
        }
        final List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        for (int i = (rowBeginNumber - 1); i < Integer.MAX_VALUE; i++) {
            final HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            final Map<String, String> resultMap = newLinkedHashMap();
            boolean exists = false;
            for (Entry<String, String> entry : attributeMap.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                final Integer cellNumber = Integer.valueOf(value) - 1;
                final HSSFCell cell = row.getCell(cellNumber);
                if (cell == null) {
                    continue;
                }
                final HSSFRichTextString cellValue = cell.getRichStringCellValue();
                if (cellValue == null) {
                    continue;
                }
                exists = true;
                resultMap.put(key, cellValue.getString());
            }
            if (exists) {
                resultList.add(resultMap);
            } else { // means empty row
                break;
            }
        }
        return resultList;
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