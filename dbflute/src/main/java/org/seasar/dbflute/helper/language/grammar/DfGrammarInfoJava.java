package org.seasar.dbflute.helper.language.grammar;

public class DfGrammarInfoJava implements DfGrammarInfo {
    public String getExtendsStringMark() {
        return "extends";
    }

    public String getClassFileExtension() {
        return "java";
    }
    
    public String getPublicDefinition() {
        return "public final";
    }
    
    public String getPublicStaticDefinition() {
        return "public static final";
    }
}