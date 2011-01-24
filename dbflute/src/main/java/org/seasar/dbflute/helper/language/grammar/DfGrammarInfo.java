package org.seasar.dbflute.helper.language.grammar;

/**
 * @author jflute
 */
public interface DfGrammarInfo {

    /**
     * @return The file extension of class. (NotNull)
     */
    public String getClassFileExtension();

    /**
     * @return The string mark of 'extends'. (NotNull)
     */
    public String getExtendsStringMark();

    /**
     * @return The string mark of 'implements'. (NotNull)
     */
    public String getImplementsStringMark();

    /**
     * @return The definition of 'public'. (NotNull)
     */
    public String getPublicDefinition();

    /**
     * @return The definition of 'public static'. (NotNull)
     */
    public String getPublicStaticDefinition();

    /**
     * @return The type literal of the class. (NotNull)
     */
    public String getClassTypeLiteral(String className);

    /**
     * @return The definition of 'List(element)'. (NotNull)
     */
    public String getGenericListClassName(String element);

    /**
     * @return The definition of 'List(Map(key, value))'. (NotNull)
     */
    public String getGenericMapListClassName(String key, String value);
}