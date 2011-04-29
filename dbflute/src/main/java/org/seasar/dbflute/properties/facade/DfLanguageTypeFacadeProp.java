package org.seasar.dbflute.properties.facade;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfLanguageTypeFacadeProp {

    protected final DfBasicProperties _basicProp;

    public DfLanguageTypeFacadeProp(DfBasicProperties basicProp) {
        _basicProp = basicProp;
    }

    public String getTargetLanguage() {
        return _basicProp.getTargetLanguage();
    }

    public String getResourceDirectory() {
        return _basicProp.getResourceDirectory();
    }

    public boolean isTargetLanguageMain() {
        return _basicProp.isTargetLanguageMain();
    }

    public boolean isTargetLanguageJava() {
        return _basicProp.isTargetLanguageJava();
    }

    public boolean isTargetLanguageCSharp() {
        return _basicProp.isTargetLanguageCSharp();
    }

    public boolean isTargetLanguagePhp() {
        return _basicProp.isTargetLanguagePhp();
    }

    public DfLanguageDependencyInfo getLanguageDependencyInfo() {
        return _basicProp.getLanguageDependencyInfo();
    }

    public String getTargetLanguageVersion() {
        return _basicProp.getTargetLanguageVersion();
    }

    public boolean isJavaVersionGreaterEqualTiger() {
        return _basicProp.isJavaVersionGreaterEqualTiger();
    }

    public boolean isJavaVersionGreaterEqualMustang() { // sub supported
        return _basicProp.isJavaVersionGreaterEqualMustang();
    }
}
