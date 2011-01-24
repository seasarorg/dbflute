package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public class DfGrammarInfoJava implements DfGrammarInfo {

    public String getClassFileExtension() {
        return "java";
    }

    public String getExtendsStringMark() {
        return "extends";
    }

    public String getImplementsStringMark() {
        return "implements";
    }

    public String getPublicDefinition() {
        return "public final";
    }

    public String getPublicStaticDefinition() {
        return "public static final";
    }

    public String getClassTypeLiteral(String className) {
        return className + ".class";
    }

    public String getGenericListClassName(String element) {
        return "List<" + element + ">";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "List<Map<" + key + ", " + value + ">>";
    }
}