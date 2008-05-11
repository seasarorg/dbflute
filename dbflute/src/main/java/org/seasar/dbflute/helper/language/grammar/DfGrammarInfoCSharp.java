package org.seasar.dbflute.helper.language.grammar;

public class DfGrammarInfoCSharp implements DfGrammarInfo {
    public String getExtendsStringMark() {
        return ":";
    }

    public String getClassFileExtension() {
        return "cs";
    }
    
    public String getPublicDefinition() {
        return "public readonly";
    }
    
    public String getPublicStaticDefinition() {
        return "public static readonly";
    }
}