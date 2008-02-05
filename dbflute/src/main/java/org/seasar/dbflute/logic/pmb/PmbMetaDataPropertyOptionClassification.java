package org.seasar.dbflute.logic.pmb;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.DfClassificationProperties;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class PmbMetaDataPropertyOptionClassification {

    protected static final String OPTION_PREFIX = "cls(";
    protected static final String OPTION_SUFFIX = ")";

    protected String _className;
    protected String _propertyName;
    protected DfClassificationProperties _classificationProperties;
    protected PmbMetaDataPropertyOptionFinder _pmbMetaDataPropertyOptionFinder;

    public PmbMetaDataPropertyOptionClassification(String className, String propertyName,
            DfClassificationProperties classificationProperties,
            PmbMetaDataPropertyOptionFinder pmbMetaDataPropertyOptionFinder) {
        _className = className;
        _propertyName = propertyName;
        _classificationProperties = classificationProperties;
        _pmbMetaDataPropertyOptionFinder = pmbMetaDataPropertyOptionFinder;
    }

    public boolean isPmbMetaDataPropertyOptionClassification() {
        return extractClassificationNameFromOption(_className, _propertyName, false) != null;
    }

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList() {
        final String classificationName = extractClassificationNameFromOption(_className, _propertyName, true);
        final List<Map<String, String>> classificationMapList = _classificationProperties
                .getClassificationMapList(classificationName);
        if (classificationMapList == null) {
            String msg = "The classification was Not Found:";
            msg = msg + " " + _className + " " + _propertyName;
            msg = msg + ":" + OPTION_PREFIX + classificationName + OPTION_SUFFIX;
            throw new IllegalStateException(msg);
        }
        return _classificationProperties.getClassificationMapList(classificationName);
    }

    protected String extractClassificationNameFromOption(String className, String propertyName, boolean check) {
        final String pmbMetaDataPropertyOption = getPmbMetaDataPropertyOption();
        if (pmbMetaDataPropertyOption == null) {
            if (check) {
                String msg = "The property name don't have Option:";
                msg = msg + " " + className + "." + propertyName;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        final String option = pmbMetaDataPropertyOption.trim();
        if (option.length() == 0) {
            if (check) {
                String msg = "The option of the property name should not be empty:";
                msg = msg + " " + className + "." + propertyName;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        if (!option.startsWith(OPTION_PREFIX) || !option.endsWith(")")) {
            if (check) {
                String msg = "The option of class name and the property name should be 'cls(xxx)':";
                msg = msg + " " + className + "." + propertyName + ":" + option;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        final int clsIdx = OPTION_PREFIX.length();
        final int clsEndIdx = option.length() - OPTION_SUFFIX.length();
        return option.substring(clsIdx) + option.substring(clsIdx, clsEndIdx);
    }

    protected String getPmbMetaDataPropertyOption() {
        return _pmbMetaDataPropertyOptionFinder.findPmbMetaDataPropertyOption(_className, _propertyName);
    }
}
