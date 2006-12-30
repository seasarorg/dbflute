package org.seasar.dbflute.helper.language;

public class DfLanguageDependencyInfoCSharp implements DfLanguageDependencyInfo {
    public String getExtendsStringMark() {
        return ":";
    }
    
    public String getTemplateFileExtension() {
        return "vmnet";
    }
    
    public String getClassFileExtension() {
        return "cs";
    }
}
