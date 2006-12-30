package org.seasar.dbflute.helper.language;

public class DfLanguageDependencyInfoJava implements DfLanguageDependencyInfo {
    public String getExtendsStringMark() {
        return "extends";
    }
    public String getTemplateFileExtension() {
        return "vm";
    }
    
    public String getClassFileExtension() {
        return "java";
    }
}
