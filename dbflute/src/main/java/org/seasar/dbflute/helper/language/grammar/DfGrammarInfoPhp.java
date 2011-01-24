package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public class DfGrammarInfoPhp implements DfGrammarInfo {

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
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

    public String getClassTypeLiteral(String className) {
        return "Unsupported!";
    }

    public String getGenericListClassName(String element) {
        return "Unsupported!";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "Unsupported!";
    }
}