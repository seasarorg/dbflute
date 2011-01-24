package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public class DfGrammarInfoCSharp implements DfGrammarInfo {

    public String getClassFileExtension() {
        return "cs";
    }

    public String getExtendsStringMark() {
        return ":";
    }

    public String getImplementsStringMark() {
        return ":";
    }

    public String getPublicDefinition() {
        return "public readonly";
    }

    public String getPublicStaticDefinition() {
        return "public static readonly";
    }

    public String getClassTypeLiteral(String className) {
        return "typeof(" + className + ")";
    }

    public String getGenericListClassName(String element) {
        return "IList<" + element + ">";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "IList<IDictionary<" + key + ", " + value + ">>";
    }
}