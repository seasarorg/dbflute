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
package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.torque.engine.database.model.AppData;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.cbean.SimplePagingBean;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTop;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfPmbGenerationHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The meta data of parameter bean. */
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap;

    // helper
    protected final DfColumnExtractor _columnHandler = new DfColumnExtractor();
    protected final DfBehaviorQueryPathSetupper _bqpSetupper = new DfBehaviorQueryPathSetupper();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbGenerationHandler(Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                          Basic Info
    //                                                                          ==========
    public Collection<DfPmbMetaData> getPmbMetaDataList() {
        return _pmbMetaDataMap != null ? _pmbMetaDataMap.values() : new ArrayList<DfPmbMetaData>();
    }

    public boolean isExistPmbMetaData() {
        return _pmbMetaDataMap != null && !_pmbMetaDataMap.isEmpty();
    }

    public String getBusinessName(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return findPmbMetaData(className).getBusinessName();
    }

    public String getAbstractDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return existsAlternateBooleanMethodNameSet(className) ? " abstract" : "";
    }

    public String getSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (!hasSuperClassDefinition(className)) {
            return "";
        }
        final DfPmbMetaData metaData = findPmbMetaData(className);
        final String superClassName = metaData.getSuperClassName();
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        final String extendsStringMark = languageDependencyInfo.getGrammarInfo().getExtendsStringMark();
        return " " + extendsStringMark + " " + superClassName;
    }

    public String getInterfaceDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (!getBasicProperties().isTargetLanguageJava()) {
            // if C#, interfaces are contained to super class definition 
            return "";
        }
        // here Java only
        final StringBuilder sb = new StringBuilder();
        if (isTypedParameterBean(className)) {
            final String behaviorClassName = getBehaviorClassName(className);
            final String customizeEntityType;
            if (isRelatedToCustomizeEntity(className)) {
                customizeEntityType = getCustomizeEntityType(className);
            } else {
                customizeEntityType = null; // no used
            }
            final String entityGenericDef = "<" + behaviorClassName + ", " + customizeEntityType + ">";
            final String noResultGenericDef = "<" + behaviorClassName + ">";

            // several typed interfaces can be implemented
            if (isTypedListHandling(className)) {
                sb.append(", ").append("ListHandlingPmb").append(entityGenericDef);
            }
            if (isTypedEntityHandling(className)) {
                sb.append(", ").append("EntityHandlingPmb").append(entityGenericDef);
            }
            if (isTypedManualPagingHandling(className)) {
                sb.append(", ").append("ManualPagingHandlingPmb").append(entityGenericDef);
            }
            if (isTypedAutoPagingHandling(className)) {
                sb.append(", ").append("AutoPagingHandlingPmb").append(entityGenericDef);
            }
            if (isTypedCursorHandling(className)) {
                sb.append(", ").append("CursorHandlingPmb").append(noResultGenericDef);
            }
            if (isTypedExecuteHandling(className)) {
                sb.append(", ").append("ExecuteHandlingPmb").append(noResultGenericDef);
            }
        }
        if (sb.length() > 0) {
            sb.delete(0, ", ".length());
        } else {
            sb.append("ParameterBean");
        }
        sb.append(", ").append("FetchBean");
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        final String implementsStringMark = languageDependencyInfo.getGrammarInfo().getImplementsStringMark();
        return " " + implementsStringMark + " " + sb.toString();
    }

    public boolean hasSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        String superClassName = metaData.getSuperClassName();
        return superClassName != null && superClassName.trim().length() > 0;
    }

    public boolean hasPagingExtension(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (!hasSuperClassDefinition(className)) {
            return false;
        }
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPagingType() != null;
    }

    public boolean hasPmbMetaDataCheckSafetyResult(String className) {
        final String definition = getSuperClassDefinition(className);
        return definition.contains(DfTypeUtil.toClassTitle(SimplePagingBean.class));
    }

    public Map<String, String> getPropertyNameOptionMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameOptionMap();
    }

    protected DfPmbMetaData findPmbMetaData(String className) {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfPmbMetaData metaData = _pmbMetaDataMap.get(className);
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
        return findPmbMetaData(className).getPropertyType(propertyName);
    }

    protected Map<String, String> getPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfPmbMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameTypeMap();
    }

    public String getPropertyTypeRemovedCSharpNullable(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final String propertyType = getPropertyType(className, propertyName);
        return propertyType.endsWith("?") ? Srl.substringLastFront(propertyType, "?") : propertyType;
    }

    public boolean isPmbMetaDataPropertyJavaNativeStringObject(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyJavaNativeStringObject(propertyName);
    }

    public boolean isPmbMetaDataPropertyJavaNativeNumberObject(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyJavaNativeNumberObject(propertyName);
    }

    public boolean isPmbMetaDataPropertyJavaNativeBooleanObject(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyJavaNativeBooleanObject(propertyName);
    }

    public String getBehaviorClassName(String className) {
        return findPmbMetaData(className).getBehaviorClassName();
    }

    public String getBehaviorQueryPath(String className) {
        return findPmbMetaData(className).getBehaviorQueryPath();
    }

    public String getCustomizeEntityType(String className) {
        return findPmbMetaData(className).getCustomizeEntityType();
    }

    public String getCustomizeEntityLineDisp(String className) {
        return findPmbMetaData(className).getCustomizeEntityLineDisp();
    }

    protected boolean isRelatedToBehaviorQuery(String className) {
        return findPmbMetaData(className).isRelatedToBehaviorQuery();
    }

    protected boolean isRelatedToCustomizeEntity(String className) {
        return findPmbMetaData(className).isRelatedToCustomizeEntity();
    }

    // ===================================================================================
    //                                                                          Typed Info
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public boolean isTypedParameterBean(String className) {
        return findPmbMetaData(className).isTypedParameterBean();
    }

    public boolean isTypedSelectPmb(String className) {
        return findPmbMetaData(className).isTypedSelectPmb();
    }

    public boolean isTypedUpdatePmb(String className) {
        return findPmbMetaData(className).isTypedUpdatePmb();
    }

    public boolean isTypedReturnEntityPmb(String className) {
        return findPmbMetaData(className).isTypedReturnEntityPmb();
    }

    public boolean isTypedReturnCustomizeEntityPmb(String className) {
        return findPmbMetaData(className).isTypedReturnCustomizeEntityPmb();
    }

    public boolean isTypedReturnDomainEntityPmb(String className) {
        return findPmbMetaData(className).isTypedReturnDomainEntityPmb();
    }

    // -----------------------------------------------------
    //                                              Handling
    //                                              --------
    public boolean isTypedListHandling(String className) {
        return findPmbMetaData(className).isTypedListHandling();
    }

    public boolean isTypedEntityHandling(String className) {
        return findPmbMetaData(className).isTypedEntityHandling();
    }

    public boolean isTypedPagingHandling(String className) { // abstract judgment
        return findPmbMetaData(className).isTypedPagingHandling();
    }

    public boolean isTypedManualPagingHandling(String className) {
        return findPmbMetaData(className).isTypedManualPagingHandling();
    }

    public boolean isTypedAutoPagingHandling(String className) {
        return findPmbMetaData(className).isTypedAutoPagingHandling();
    }

    public boolean isTypedCursorHandling(String className) {
        return findPmbMetaData(className).isTypedCursorHandling();
    }

    public boolean isTypedExecuteHandling(String className) {
        return findPmbMetaData(className).isTypedExecuteHandling();
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    public boolean hasPropertyOptionOriginalOnlyOneSetter(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).hasPropertyOptionOriginalOnlyOneSetter(propertyName, schemaData);
    }

    // -----------------------------------------------------
    //                                           LikeSeasrch
    //                                           -----------
    public boolean hasPropertyOptionAnyLikeSearch(String className) {
        return findPmbMetaData(className).hasPropertyOptionAnyLikeSearch();
    }

    public boolean hasPropertyOptionAnyLikeSearch(String className, String propertyName) {
        return findPmbMetaData(className).hasPropertyOptionAnyLikeSearch(propertyName);
    }

    public boolean isPropertyOptionLikeSearch(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionLikeSearch(propertyName);
    }

    public boolean isPropertyOptionPrefixSearch(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionPrefixSearch(propertyName);
    }

    public boolean isPropertyOptionContainSearch(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionContainSearch(propertyName);
    }

    public boolean isPropertyOptionSuffixSearch(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionSuffixSearch(propertyName);
    }

    // -----------------------------------------------------
    //                                                FromTo
    //                                                ------
    public boolean hasPropertyOptionAnyFromTo(String className) {
        return findPmbMetaData(className).hasPropertyOptionAnyFromTo();
    }

    public boolean hasPropertyOptionAnyFromTo(String className, String propertyName) {
        return findPmbMetaData(className).hasPropertyOptionAnyFromTo(propertyName);
    }

    public boolean isPropertyOptionFromDate(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionFromDate(propertyName);
    }

    public boolean isPropertyOptionFromDateOption(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionFromDateOption(propertyName);
    }

    public boolean isPropertyOptionToDate(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionToDate(propertyName);
    }

    public boolean isPropertyOptionToDateOption(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionToDateOption(propertyName);
    }

    // -----------------------------------------------------
    //                                        Classification
    //                                        --------------
    public boolean isPropertyOptionClassification(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).isPropertyOptionClassification(propertyName, schemaData);
    }

    public boolean isPropertyOptionClassificationFixedElement(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionClassificationFixedElement(propertyName);
    }

    public boolean isPropertyOptionClassificationFixedElementList(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionClassificationFixedElementList(propertyName);
    }

    public boolean isPropertyOptionClassificationSetter(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).isPropertyOptionClassificationSetter(propertyName, schemaData);
    }

    public String getPropertyOptionClassificationName(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyOptionClassificationName(propertyName, schemaData);
    }

    public String getPropertyOptionClassificationFixedElementValueExp(String className, String propertyName) {
        return findPmbMetaData(className).getPropertyOptionClassificationFixedElementValueExp(propertyName);
    }

    public DfClassificationTop getPropertyOptionClassificationTop(String className, String propertyName,
            AppData schemaData) {
        return findPmbMetaData(className).getPropertyOptionClassificationTop(propertyName, schemaData);
    }

    public String getPropertyOptionClassificationSettingElementValueExp(String className, String propertyName,
            String element, AppData schemaData) {
        return findPmbMetaData(className).getPropertyOptionClassificationSettingElementValueExp(propertyName, element,
                schemaData);
    }

    // -----------------------------------------------------
    //                                      Alternate Method
    //                                      ----------------
    public boolean existsAlternateBooleanMethodNameSet(String className) {
        return !getAlternateBooleanMethodNameSet(className).isEmpty();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAlternateBooleanMethodNameSet(String className) {
        final Set<String> nameSet = findPmbMetaData(className).getAlternateMethodBooleanNameSet();
        return nameSet != null ? nameSet : Collections.EMPTY_SET;
    }

    // -----------------------------------------------------
    //                                             Reference
    //                                             ---------
    public boolean hasPropertyOptionReference(String className) {
        return findPmbMetaData(className).hasPropertyOptionReference();
    }

    public String getPropertyRefName(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefName(propertyName, schemaData);
    }

    public String getPropertyRefAlias(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefAlias(propertyName, schemaData);
    }

    public String getPropertyRefLineDisp(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefLineDisp(propertyName, schemaData);
    }

    public boolean isPropertyRefColumnChar(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).isPropertyRefColumnChar(propertyName, schemaData);
    }

    public String getPropertyRefDbType(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefDbType(propertyName, schemaData);
    }

    public String getPropertyRefSize(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefSize(propertyName, schemaData);
    }

    // -----------------------------------------------------
    //                                               Comment
    //                                               -------
    public boolean hasPropertyOptionComment(String className, String propertyName) {
        return findPmbMetaData(className).hasPropertyOptionComment(propertyName);
    }

    public String getPropertyOptionComment(String className, String propertyName) {
        return findPmbMetaData(className).getPropertyOptionComment(propertyName);
    }

    // -----------------------------------------------------
    //                                               Display
    //                                               -------
    public String getPropertyRefColumnInfo(String className, String propertyName, AppData schemaData) {
        return findPmbMetaData(className).getPropertyRefColumnInfo(propertyName, schemaData);
    }

    // ===================================================================================
    //                                                                           Procedure
    //                                                                           =========
    public boolean isForProcedure(String className) {
        return findPmbMetaData(className).isRelatedToProcedure();
    }

    public String getProcedureName(String className) {
        return findPmbMetaData(className).getProcedureName();
    }

    public boolean isProcedureCalledBySelect(String className) {
        return findPmbMetaData(className).isProcedureCalledBySelect();
    }

    public boolean isProcedureRefCustomizeEntity(String className) {
        return findPmbMetaData(className).isProcedureRefCustomizeEntity();
    }

    public boolean hasProcedureOverload(String className) {
        return findPmbMetaData(className).hasProcedureOverload();
    }

    public boolean isPropertyOptionProcedureParameterIn(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionProcedureParameterIn(propertyName);
    }

    public boolean isPropertyOptionProcedureParameterOut(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionProcedureParameterOut(propertyName);
    }

    public boolean isPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionProcedureParameterInOut(propertyName);
    }

    public boolean isPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionProcedureParameterReturn(propertyName);
    }

    public boolean isPropertyOptionProcedureParameterResult(String className, String propertyName) {
        return findPmbMetaData(className).isPropertyOptionProcedureParameterResult(propertyName);
    }

    public String getPropertyColumnName(String className, String propertyName) {
        return findPmbMetaData(className).getPropertyColumnName(propertyName);
    }

    // -----------------------------------------------------
    //                                Handling Determination
    //                                ----------------------
    public boolean needsStringClobHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsStringClobHandling(propertyName);
    }

    public boolean needsBytesOidHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsBytesOidHandling(propertyName);
    }

    public boolean needsFixedLengthStringHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsFixedLengthStringHandling(propertyName);
    }

    public boolean needsObjectBindingBigDecimalHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsObjectBindingBigDecimalHandling(propertyName);
    }

    public boolean needsOracleArrayHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsOracleArrayHandling(propertyName);
    }

    public boolean needsOracleStructHandling(String className, String propertyName) {
        return findPmbMetaData(className).needsOracleStructHandling(propertyName);
    }

    // -----------------------------------------------------
    //                                           Oracle Type
    //                                           -----------
    public String getProcedureParameterOracleArrayTypeName(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleArrayTypeName(propertyName);
    }

    public String getProcedureParameterOracleArrayElementTypeName(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleArrayElementTypeName(propertyName);
    }

    public String getProcedureParameterOracleArrayElementJavaNative(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleArrayElementJavaNative(propertyName);
    }

    public String getProcedureParameterOracleArrayElementJavaNativeTypeLiteral(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleArrayElementJavaNativeTypeLiteral(propertyName);
    }

    public String getProcedureParameterOracleStructTypeName(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleStructTypeName(propertyName);
    }

    public String getProcedureParameterOracleStructEntityType(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleStructEntityType(propertyName);
    }

    public String getProcedureParameterOracleStructEntityTypeTypeLiteral(String className, String propertyName) {
        return findPmbMetaData(className).getProcedureParameterOracleStructEntityTypeTypeLiteral(propertyName);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    public String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    public String getSlashPath(File file) {
        return replaceString(file.getPath(), getFileSeparator(), "/");
    }

    public String getFileSeparator() {
        return File.separator;
    }

    protected String ln() {
        return "\n";
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
            String msg = "The className should not be null or empty: " + className;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertArgumentPmbMetaDataPropertyName(String propertyName) {
        if (propertyName == null || propertyName.trim().length() == 0) {
            String msg = "The propertyName should not be null or empty: " + propertyName;
            throw new IllegalArgumentException(msg);
        }
    }
}
