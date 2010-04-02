package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
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

    public String getGenericListClassName(String element) {
        return "IList<" + element + ">";
    }

    public String getGenericMapListClassName(String key, String value) {
        return "IList<IDictionary<" + key + ", " + value + ">>";
    }
}