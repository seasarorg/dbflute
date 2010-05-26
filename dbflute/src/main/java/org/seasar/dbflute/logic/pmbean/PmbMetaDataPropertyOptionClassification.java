package org.seasar.dbflute.logic.pmbean;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class PmbMetaDataPropertyOptionClassification {

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
    protected PmbMetaDataPropertyOptionFinder _pmbMetaDataPropertyOptionFinder;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PmbMetaDataPropertyOptionClassification(String className, String propertyName,
            DfClassificationProperties classificationProperties,
            PmbMetaDataPropertyOptionFinder pmbMetaDataPropertyOptionFinder) {
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

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList() {
        final String classificationName = extractClassificationNameFromOption(_className, _propertyName, true);
        final List<Map<String, String>> classificationMapList = _classificationProperties
                .getClassificationMapList(classificationName);
        if (classificationMapList == null) {
            String msg = "Look the message below:" + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + ln();
            msg = msg + "The classification was not found:" + ln();
            msg = msg + " " + _className + " " + _propertyName;
            msg = msg + ":" + OPTION_PREFIX + classificationName + OPTION_SUFFIX + ln();
            msg = msg + "* * * * * * * * * */";
            throw new IllegalStateException(msg);
        }
        return classificationMapList;
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
        return PmbMetaDataPropertyOptionFinder.splitOption(option);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}