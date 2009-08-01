package org.seasar.dbflute.properties.assistant.classification;

import java.util.Map;

/**
 * Temporary DTO when classification initializing.
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationElement {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String KEY_CODE = "code";
    public static final String KEY_NAME = "name";
    public static final String KEY_ALIAS = "alias";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_TABLE = "table";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String classificationName;
    protected String _code;
    protected String _name;
    protected String _alias;
    protected String _comment;
    protected String _table;

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptClassificationBasicElementMap(Map<?, ?> elementMap) {
        acceptBasicMap(elementMap, KEY_CODE, KEY_NAME, KEY_ALIAS, KEY_COMMENT);
    }

    protected void acceptBasicMap(Map<?, ?> elementMap, String codeKey, String nameKey, String aliasKey,
            String commentKey) {
        final String code = (String) elementMap.get(codeKey);
        if (code == null) {
            String msg = "The elementMap should have " + codeKey + ".";
            throw new IllegalStateException(msg);
        }
        this._code = code;

        // name
        String name = (String) elementMap.get(nameKey);
        name = (name != null ? name : code);
        this._name = name;

        // alias
        String alias = (String) elementMap.get(aliasKey);
        alias = (alias != null ? alias : name);
        this._alias = alias;

        // comment
        final String comment = (String) elementMap.get(commentKey);
        this._comment = comment;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _code + ", " + _name + ", " + _alias + ", " + _comment + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(String code) {
        this._code = code;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getAlias() {
        return _alias;
    }

    public void setAlias(String alias) {
        this._alias = alias;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        this._comment = comment;
    }

    public String getTable() {
        return _table;
    }

    public void setTable(String table) {
        this._table = table;
    }
}
