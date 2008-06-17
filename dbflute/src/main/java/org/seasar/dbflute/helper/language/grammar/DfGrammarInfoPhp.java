package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public class DfGrammarInfoPhp implements DfGrammarInfo {

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getClassFileExtension() {
        return "php";
    }

    public String getPublicDefinition() {
        return "const";
    }

    public String getPublicStaticDefinition() {
        return "const";
    }
}