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

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTop;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class DfPmbPropertyOptionClassification {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String OPTION_PREFIX = "cls(";
    protected static final String OPTION_SUFFIX = ")";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _className;
    protected String _propertyName;
    protected DfClassificationProperties _classificationProperties;
    protected DfPmbPropertyOptionFinder _pmbMetaDataPropertyOptionFinder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPmbPropertyOptionClassification(String className, String propertyName,
            DfClassificationProperties classificationProperties,
            DfPmbPropertyOptionFinder pmbMetaDataPropertyOptionFinder) {
        _className = className;
        _propertyName = propertyName;
        _classificationProperties = classificationProperties;
        _pmbMetaDataPropertyOptionFinder = pmbMetaDataPropertyOptionFinder;
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public boolean isPmbMetaDataPropertyOptionClassification() {
        return extractClassificationNameFromOption(_className, _propertyName, false) != null;
    }

    public String getPmbMetaDataPropertyOptionClassificationName() {
        return extractClassificationNameFromOption(_className, _propertyName, true);
    }

    public String getPmbMetaDataPropertyOptionClassificationCodeType() {
        final String classificationName = getPmbMetaDataPropertyOptionClassificationName();
        final Map<String, Map<String, String>> allMap = _classificationProperties.getClassificationTopDefinitionMap();
        final Map<String, String> elementMap = allMap.get(classificationName);
        if (elementMap == null) {
            throwClassificationNotFoundException(classificationName);
        }
        final String codeType = elementMap.get(DfClassificationTop.KEY_CODE_TYPE);
        return codeType != null ? codeType : DfClassificationTop.DEFAULT_CODE_TYPE;
    }

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList() {
        final String classificationName = extractClassificationNameFromOption(_className, _propertyName, true);
        final List<Map<String, String>> classificationMapList = _classificationProperties
                .getClassificationMapList(classificationName);
        if (classificationMapList == null) {
            throwClassificationNotFoundException(classificationName);
        }
        return classificationMapList;
    }

    protected void throwClassificationNotFoundException(String classificationName) {
        String msg = "Look the message below:" + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + ln();
        msg = msg + "The classification was not found:" + ln();
        msg = msg + " " + _className + " " + _propertyName;
        msg = msg + ":" + OPTION_PREFIX + classificationName + OPTION_SUFFIX + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg);
    }

    protected String extractClassificationNameFromOption(String className, String propertyName, boolean check) {
        final String pmbMetaDataPropertyOption = getPmbMetaDataPropertyOption();
        if (pmbMetaDataPropertyOption == null) {
            if (check) {
                String msg = "The property name didn't have its option:";
                msg = msg + " " + className + "." + propertyName;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        String option = pmbMetaDataPropertyOption.trim();
        {
            if (option.trim().length() == 0) {
                if (check) {
                    String msg = "The option of the property name should not be empty:";
                    msg = msg + " property=" + className + "." + propertyName;
                    throw new IllegalStateException(msg);
                } else {
                    return null;
                }
            }
            final List<String> splitOption = splitOption(option);
            String firstOption = null;
            for (String element : splitOption) {
                element = element.trim();
                if (element.startsWith(OPTION_PREFIX) && element.endsWith(OPTION_SUFFIX)) {
                    firstOption = element;
                    break;
                }
            }
            if (firstOption == null) {
                if (check) {
                    String msg = "The option of class name and the property name should be 'cls(xxx)':";
                    msg = msg + " property=" + className + "." + propertyName + ":" + option;
                    throw new IllegalStateException(msg);
                } else {
                    return null;
                }
            }
            option = firstOption;
        }
        final int clsIdx = OPTION_PREFIX.length();
        final int clsEndIdx = option.length() - OPTION_SUFFIX.length();
        try {
            return option.substring(clsIdx, clsEndIdx);
        } catch (StringIndexOutOfBoundsException e) {
            String msg = "Look the message below:" + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + ln();
            msg = msg + "IndexOutOfBounds ocurred:" + ln();
            msg = msg + " " + _className + " " + _propertyName;
            msg = msg + ":" + option + ln();
            msg = msg + "{" + option + "}.substring(" + clsIdx + ", " + clsEndIdx + ")" + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String getPmbMetaDataPropertyOption() {
        return _pmbMetaDataPropertyOptionFinder.findPmbMetaDataPropertyOption(_className, _propertyName);
    }

    protected List<String> splitOption(String option) {
        return DfPmbPropertyOptionFinder.splitOption(option);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
