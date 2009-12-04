package org.seasar.dbflute.logic.pmb;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.model.Column;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.util.DfSystemUtil;

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

    public String getPmbMetaDataSuperClassDefinition(String className) {
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

    public boolean hasPmbMetaDataSafetyResultDefitinion(String className) {
        if (isPmbMetaDataForProcedure(className)) {
            return false;
        }
        final String classDefinition = getPmbMetaDataSuperClassDefinition(className);
        return classDefinition == null || classDefinition.trim().length() == 0;
    }

    public Map<String, String> getPmbMetaDataPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameTypeMap();
    }

    public Map<String, String> getPmbMetaDataPropertyNameColumnNameMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnNameMap();
    }

    public Map<String, String> getPmbMetaDataPropertyNameOptionMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameOptionMap();
    }

    private DfParameterBeanMetaData findPmbMetaData(String className) {
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

    public Set<String> getPmbMetaDataPropertySet(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return getPmbMetaDataPropertyNameTypeMap(className).keySet();
    }

    public String getPmbMetaDataPropertyType(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPmbMetaDataPropertyNameTypeMap(className).get(propertyName);
    }

    public String getPmbMetaDataPropertyColumnName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPmbMetaDataPropertyNameColumnNameMap(className).get(propertyName);
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    public boolean isPmbMetaDataForProcedure(String className) {
        return findPmbMetaData(className).getProcedureName() != null;
    }

    public String getPmbMetaDataProcedureName(String className) {
        return findPmbMetaData(className).getProcedureName();
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterIn(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnIn.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterOut(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnOut.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnInOut.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnReturn.toString());
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public boolean hasPmbMetaDataPropertyOptionOriginalOnlyOneSetter(String className, String propertyName) {
        return hasPmbMetaDataPropertyOptionAnyLikeSearch(className, propertyName)
                || hasPmbMetaDataPropertyOptionAnyFromTo(className, propertyName);
    }

    // -----------------------------------------------------
    //                                           LikeSeasrch
    //                                           -----------
    public boolean hasPmbMetaDataPropertyOptionAnyLikeSearch(String className) {
        DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPmbMetaDataPropertyOptionAnyLikeSearch(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPmbMetaDataPropertyOptionAnyLikeSearch(String className, String propertyName) {
        return isPmbMetaDataPropertyOptionLikeSearch(className, propertyName)
                || isPmbMetaDataPropertyOptionPrefixSearch(className, propertyName)
                || isPmbMetaDataPropertyOptionContainSearch(className, propertyName)
                || isPmbMetaDataPropertyOptionSuffixSearch(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionLikeSearch(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "like");
    }

    public boolean isPmbMetaDataPropertyOptionPrefixSearch(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "likePrefix");
    }

    public boolean isPmbMetaDataPropertyOptionContainSearch(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "likeContain");
    }

    public boolean isPmbMetaDataPropertyOptionSuffixSearch(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "likeSuffix");
    }

    // -----------------------------------------------------
    //                                                FromTo
    //                                                ------
    public boolean hasPmbMetaDataPropertyOptionAnyFromTo(String className) {
        DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPmbMetaDataPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPmbMetaDataPropertyOptionAnyFromTo(String className, String propertyName) {
        return isPmbMetaDataPropertyOptionFromDate(className, propertyName)
                || isPmbMetaDataPropertyOptionToDate(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionFromDate(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "fromDate");
    }

    public boolean isPmbMetaDataPropertyOptionToDate(String className, String propertyName) {
        return isPmbMetaDataPropertyOption(className, propertyName, "toDate");
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    public boolean isPmbMetaDataPropertyOptionClassification(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.isPmbMetaDataPropertyOptionClassification();
    }

    public String getPmbMetaDataPropertyOptionClassificationName(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationName();
    }

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList(String className,
            String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationMapList();
    }

    // -----------------------------------------------------
    //                                             Reference
    //                                             ---------
    public boolean hasPmbMetaDataPropertyOptionReference(String className) {
        DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            return false;
        }
        final Set<String> propertyNameSet = metaData.getPropertyNameTypeMap().keySet();
        for (String propertyName : propertyNameSet) {
            if (hasPmbMetaDataPropertyOptionAnyFromTo(className, propertyName)) {
                return true;
            }
        }
        return false;
    }

    public String getPmbMetaDataPropertyRefName(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getName() : "";
    }

    public String getPmbMetaDataPropertyRefAlias(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getAliasExpression() : "";
    }

    public String getPmbMetaDataPropertyRefLineDisp(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? "{" + column.getColumnDefinitionLineDisp() + "}" : "";
    }

    public boolean isPmbMetaDataPropertyRefColumnChar(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.isJdbcTypeChar() : false;
    }

    public String getPmbMetaDataPropertyRefDbType(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getDbType() : "";
    }

    public String getPmbMetaDataPropertyRefSize(String className, String propertyName, AppData appData) {
        Column column = getPmbMetaDataPropertyOptionReferenceColumn(className, propertyName, appData);
        return column != null ? column.getColumnSizeSettingExpression() : "";
    }

    protected Column getPmbMetaDataPropertyOptionReferenceColumn(String className, String propertyName, AppData appData) {
        PmbMetaDataPropertyOptionReference reference = createPmbMetaDataPropertyOptionReference(className, propertyName);
        return reference.getPmbMetaDataPropertyOptionReferenceColumn(appData);
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean isPmbMetaDataPropertyOption(String className, String propertyName, String option) {
        final String specified = findPmbMetaDataPropertyOption(className, propertyName);
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

    protected PmbMetaDataPropertyOptionClassification createPmbMetaDataPropertyOptionClassification(String className,
            String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPmbMetaDataPropertyOptionFinder(className, propertyName);
        return new PmbMetaDataPropertyOptionClassification(className, propertyName, _classificationProperties, finder);
    }

    protected PmbMetaDataPropertyOptionReference createPmbMetaDataPropertyOptionReference(String className,
            String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPmbMetaDataPropertyOptionFinder(className, propertyName);
        return new PmbMetaDataPropertyOptionReference(className, propertyName, finder);
    }

    protected String findPmbMetaDataPropertyOption(String className, String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPmbMetaDataPropertyOptionFinder(className, propertyName);
        return finder.findPmbMetaDataPropertyOption(className, propertyName);
    }

    protected PmbMetaDataPropertyOptionFinder createPmbMetaDataPropertyOptionFinder(String className,
            String propertyName) {
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
