package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.util.DfSystemUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfParameterBeanBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The meta data of parameter bean. */
    protected Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    protected DfBasicProperties _basicProperties;
    protected DfClassificationProperties _classificationProperties;

    private static DfColumnHandler _columnHandler = new DfColumnHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfParameterBeanBasicHandler(Map<String, DfParameterBeanMetaData> pmbMetaDataMap,
            DfBasicProperties basicProperties, DfClassificationProperties classificationProperties) {
        _pmbMetaDataMap = pmbMetaDataMap;
        _basicProperties = basicProperties;
        _classificationProperties = classificationProperties;
    }

    // ===================================================================================
    //                                                                            MetaData
    //                                                                            ========
    public boolean isExistPmbMetaData() {
        return _pmbMetaDataMap != null && !_pmbMetaDataMap.isEmpty();
    }

    public Collection<DfParameterBeanMetaData> getPmbMetaDataList() {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty.";
            throw new IllegalStateException(msg);
        }
        return _pmbMetaDataMap.values();
    }

    public String getSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        String superClassName = metaData.getSuperClassName();
        if (superClassName == null || superClassName.trim().length() == 0) {
            return "";
        }
        if (DfBuildProperties.getInstance().isVersionJavaOverNinety()) { // as patch for 90
            if (superClassName.contains("SimplePagingBean")) {
                superClassName = "org.seasar.dbflute.cbean.SimplePagingBean";
            }
        }
        final DfLanguageDependencyInfo languageDependencyInfo = _basicProperties.getLanguageDependencyInfo();
        return " " + languageDependencyInfo.getGrammarInfo().getExtendsStringMark() + " " + superClassName;
    }

    public boolean hasSafetyResultDefitinion(String className) {
        if (isForProcedure(className)) {
            return false;
        }
        final String classDefinition = getSuperClassDefinition(className);
        return classDefinition == null || classDefinition.trim().length() == 0;
    }

    public Map<String, String> getPropertyNameOptionMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameOptionMap();
    }

    protected DfParameterBeanMetaData findPmbMetaData(String className) {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            String msg = "The className has no meta data: className=" + className;
            throw new IllegalStateException(msg);
        }
        return metaData;
    }

    public Set<String> getPropertySet(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return getPropertyNameTypeMap(className).keySet();
    }

    public String getPropertyType(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPropertyNameTypeMap(className).get(propertyName);
    }

    protected Map<String, String> getPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameTypeMap();
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    public boolean isForProcedure(String className) {
        return findPmbMetaData(className).getProcedureName() != null;
    }

    public String getProcedureName(String className) {
        return findPmbMetaData(className).getProcedureName();
    }

    public boolean isRefCustomizeEntity(String className) {
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        return metaData.isRefCustomizeEntity();
    }

    public boolean isPropertyOptionProcedureParameterIn(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnIn.toString());
    }

    public boolean isPropertyOptionProcedureParameterOut(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnInOut.toString());
    }

    public boolean isPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnReturn.toString());
    }

    public boolean isPropertyOptionProcedureParameterResult(String className, String propertyName) {
        String option = findPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnResult.toString());
    }

    public String getPropertyColumnName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        return getPropertyNameColumnNameMap(className).get(propertyName);
    }

    protected Map<String, String> getPropertyNameColumnNameMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnNameMap();
    }

    public boolean needsStringClobHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getPropertyNameColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeStringClob(metaInfo.getDbTypeName());
    }

    public boolean needsBytesOidHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getPropertyNameColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeBytesOid(metaInfo.getDbTypeName());
    }

    public boolean needsFixedLengthStringHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getPropertyNameColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeFixedLengthString(metaInfo.getDbTypeName());
    }

    public boolean needsObjectBindingBigDecimalHandling(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final DfProcedureColumnMetaInfo metaInfo = getPropertyNameColumnInfo(className, propertyName);
        if (metaInfo == null) {
            return false;
        }
        return _columnHandler.isConceptTypeObjectBindingBigDecimal(metaInfo.getDbTypeName());
    }

    protected DfProcedureColumnMetaInfo getPropertyNameColumnInfo(String className, String propertyName) {
        assertArgumentPmbMetaDataClassPropertyName(className, propertyName);
        final Map<String, DfProcedureColumnMetaInfo> columnInfoMap = getPropertyNameColumnInfoMap(className);
        if (columnInfoMap == null) {
            return null;
        }
        return columnInfoMap.get(propertyName);
    }

    protected Map<String, DfProcedureColumnMetaInfo> getPropertyNameColumnInfoMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnInfoMap();
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public boolean hasPropertyOptionOriginalOnlyOneSetter(String className, String propertyName) {
        return hasPropertyOptionAnyLikeSearch(className, propertyName)
                || hasPropertyOptionAnyFromTo(className, propertyName);
    }

    // -----------------------------------------------------
    //                                           LikeSeasrch
    //                                           -----------
    public boolean hasPropertyOptionAnyLikeSearch(String className) {
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyLikeSearch(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyLikeSearch(String className, String propertyName) {
        return isPropertyOptionLikeSearch(className, propertyName)
                || isPropertyOptionPrefixSearch(className, propertyName)
                || isPropertyOptionContainSearch(className, propertyName)
                || isPropertyOptionSuffixSearch(className, propertyName);
    }

    public boolean isPropertyOptionLikeSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "like");
    }

    public boolean isPropertyOptionPrefixSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likePrefix");
    }

    public boolean isPropertyOptionContainSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likeContain");
    }

    public boolean isPropertyOptionSuffixSearch(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "likeSuffix");
    }

    // -----------------------------------------------------
    //                                                FromTo
    //                                                ------
    public boolean hasPropertyOptionAnyFromTo(String className) {
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPropertyOptionAnyFromTo(String className, String propertyName) {
        return isPropertyOptionFromDate(className, propertyName) || isPropertyOptionToDate(className, propertyName);
    }

    public boolean isPropertyOptionFromDate(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "fromDate");
    }

    public boolean isPropertyOptionToDate(String className, String propertyName) {
        return containsPropertyOption(className, propertyName, "toDate");
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    public boolean isPropertyOptionClassification(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
        return obj.isPmbMetaDataPropertyOptionClassification();
    }

    public String getPropertyOptionClassificationName(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationName();
    }

    public List<Map<String, String>> getPropertyOptionClassificationMapList(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPropertyOptionClassification(className, propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationMapList();
    }

    // -----------------------------------------------------
    //                                             Reference
    //                                             ---------
    public boolean hasPropertyOptionReference(String className) {
        DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public String getPropertyRefColumnInfo(String className, String propertyName, AppData appData) {
        final String name = getPropertyRefName(className, propertyName, appData);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(name)) { // basically normal parameter-bean
            final String alias = getPropertyRefAlias(className, propertyName, appData);
            final String lineDisp = getPropertyRefLineDisp(className, propertyName, appData);
            return " :: refers to " + alias + name + ": " + lineDisp;
        } else { // basically procedure parameters
            final DfProcedureColumnMetaInfo metaInfo = getPropertyNameColumnInfo(className, propertyName);
            if (metaInfo == null) {
                return "";
            }
            return ": {" + metaInfo.getColumnDefinitionLineDisp() + "}";
        }
    }

    public String getPropertyRefName(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getName() : "";
    }

    public String getPropertyRefAlias(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getAliasExpression() : "";
    }

    public String getPropertyRefLineDisp(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? "{" + column.getColumnDefinitionLineDisp() + "}" : "";
    }

    public boolean isPropertyRefColumnChar(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.isJdbcTypeChar() : false;
    }

    public String getPropertyRefDbType(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getDbType() : "";
    }

    public String getPropertyRefSize(String className, String propertyName, AppData appData) {
        final Column column = getPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getColumnSizeSettingExpression() : "";
    }

    protected Column getPropertyOptionReferenceColumn(String className, String propertyName, AppData appData) {
        final PmbMetaDataPropertyOptionReference reference = createPropertyOptionReference(className, propertyName);
        return reference.getPmbMetaDataPropertyOptionReferenceColumn(appData);
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean containsPropertyOption(String className, String propertyName, String option) {
        final String specified = findPropertyOption(className, propertyName);
        if (specified == null) {
            return false;
        }
        final List<String> splitList = splitOption(specified);
        for (String element : splitList) {
            if (element.trim().equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> splitOption(String option) {
        return PmbMetaDataPropertyOptionFinder.splitOption(option);
    }

    protected PmbMetaDataPropertyOptionClassification createPropertyOptionClassification(String className,
            String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        return new PmbMetaDataPropertyOptionClassification(className, propertyName, _classificationProperties, finder);
    }

    protected PmbMetaDataPropertyOptionReference createPropertyOptionReference(String className, String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        return new PmbMetaDataPropertyOptionReference(className, propertyName, finder);
    }

    protected String findPropertyOption(String className, String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPropertyOptionFinder(className, propertyName);
        return finder.findPmbMetaDataPropertyOption(className, propertyName);
    }

    protected PmbMetaDataPropertyOptionFinder createPropertyOptionFinder(String className, String propertyName) {
        return new PmbMetaDataPropertyOptionFinder(className, propertyName, _pmbMetaDataMap);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentPmbMetaDataClassPropertyName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
    }

    protected void assertArgumentPmbMetaDataClassName(String className) {
        if (className == null || className.trim().length() == 0) {
            String msg = "The className should not be null or empty: [" + className + "]";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertArgumentPmbMetaDataPropertyName(String propertyName) {
        if (propertyName == null || propertyName.trim().length() == 0) {
            String msg = "The propertyName should not be null or empty: [" + propertyName + "]";
            throw new IllegalArgumentException(msg);
        }
    }
}
