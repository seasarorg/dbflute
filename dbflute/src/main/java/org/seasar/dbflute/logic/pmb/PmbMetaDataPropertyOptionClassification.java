package org.seasar.dbflute.logic.pmb;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.properties.DfClassificationProperties;

/**
 * @author jflute
 * @since 0.6.3 (2008/02/05 Tuesday)
 */
public class PmbMetaDataPropertyOptionClassification {
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
            String msg = "The classification was Not Found: " + _className + " " + _propertyName + ":cls("
                    + classificationName + ")";
            throw new IllegalStateException(msg);
        }
        return _classificationProperties.getClassificationMapList(classificationName);
    }

    protected String extractClassificationNameFromOption(String className, String propertyName, boolean check) {
        final String pmbMetaDataPropertyOption = getPmbMetaDataPropertyOption();
        if (pmbMetaDataPropertyOption == null) {
            System.out.println("1: " + className + "." + propertyName);
            if (check) {
                String msg = "The property name don't have Option: " + className + "." + propertyName;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        final String option = pmbMetaDataPropertyOption.trim();
        if (option.length() == 0) {
            System.out.println("2: " + className + "." + propertyName);
            if (check) {
                String msg = "The option of the property name should not be empty: " + className + "."
                        + propertyName;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        if (option.startsWith("cls(") && option.endsWith(")")) {
            System.out.println("3: " + className + "." + propertyName + ":{" + option + "}");
            if (check) {
                String msg = "The option of class name and the property name should be 'cls(xxx)': " + className
                        + "." + propertyName + " - " + option;
                throw new IllegalStateException(msg);
            } else {
                return null;
            }
        }
        return option.substring("cls(".length())
                + option.substring("cls(".length(), option.length() - ")".length());
    }

    protected String getPmbMetaDataPropertyOption() {
        return _pmbMetaDataPropertyOptionFinder.findPmbMetaDataPropertyOption(_className, _propertyName);
    }
}
