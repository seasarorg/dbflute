package org.seasar.dbflute.properties.assistant.classification;

import java.util.Map;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/07/03 Friday)
 */
public class DfClassificationInfo {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String KEY_CODE = "code";
    public static final String KEY_NAME = "name";
    public static final String KEY_ALIAS = "alias";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_TABLE = "table";
    public static final String KEY_TOP_CODE = "topCode";
    public static final String KEY_TOP_COMMENT = "topComment";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String classificationName;
    protected String table;
    protected String code;
    protected String name;
    protected String alias;
    protected String comment;
    protected boolean group;

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptBasicClassificationMap(Map<?, ?> elementMap) {
        acceptMap(elementMap, KEY_CODE, KEY_NAME, KEY_ALIAS, KEY_COMMENT, false);
    }

    public void acceptMetaClassificationMap(Map<?, ?> elementMap) {
        group = true;
        acceptMap(elementMap, KEY_TOP_CODE, null, null, KEY_TOP_COMMENT, true);
    }

    protected void acceptMap(Map<?, ?> elementMap, String codeKey, String nameKey, String aliasKey, String commentKey,
            boolean group) {
        final String code = (String) elementMap.get(codeKey);
        if (!group && code == null) {
            String msg = "The elementMap should have " + codeKey + ".";
            throw new IllegalStateException(msg);
        }
        this.code = code;

        // name
        String name = (String) elementMap.get(nameKey);
        name = (name != null ? name : code);
        this.name = name;

        // alias
        String alias = (String) elementMap.get(aliasKey);
        alias = (alias != null ? alias : name);
        this.alias = alias;

        // comment
        final String comment = (String) elementMap.get(commentKey);
        this.comment = comment;
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }
}
