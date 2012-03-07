package org.seasar.dbflute.properties.assistant.classification;

import java.util.Map;

import org.seasar.dbflute.exception.DfClassificationRequiredAttributeNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;

/**
 * Temporary DTO when classification initializing.
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationElement {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String KEY_TABLE = "table";
    public static final String KEY_CODE = "code";
    public static final String KEY_NAME = "name";
    public static final String KEY_ALIAS = "alias";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_SUB_ITEM_MAP = "subItemMap";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _classificationName;
    protected String _table;
    protected String _code;
    protected String _name;
    protected String _alias;
    protected String _comment;
    protected Map<String, Object> _subItemMap;

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptClassificationBasicElementMap(Map<?, ?> elementMap) {
        acceptBasicMap(elementMap, KEY_CODE, KEY_NAME, KEY_ALIAS, KEY_COMMENT, KEY_SUB_ITEM_MAP);
    }

    protected void acceptBasicMap(Map<?, ?> elementMap, String codeKey, String nameKey, String aliasKey,
            String commentKey, String subItemMapKey) {
        final String code = (String) elementMap.get(codeKey);
        if (code == null) {
            throwClassificationRequiredAttributeNotFoundException(elementMap);
        }
        this._code = code;

        // name
        String name = (String) elementMap.get(nameKey);
        name = (name != null ? name : code);
        this._name = name;

        // alias (same as name if null)
        String alias = (String) elementMap.get(aliasKey);
        alias = (alias != null ? alias : name);
        this._alias = alias;

        // comment
        final String comment = (String) elementMap.get(commentKey);
        this._comment = comment;

        // subItemMap
        @SuppressWarnings("unchecked")
        final Map<String, Object> subItemMap = (Map<String, Object>) elementMap.get(subItemMapKey);
        this._subItemMap = subItemMap;
    }

    protected void throwClassificationRequiredAttributeNotFoundException(Map<?, ?> elementMap) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The element map did not have a 'code' attribute.");
        br.addItem("Advice");
        br.addElement("An element map requires 'code' attribute like this:");
        br.addElement("  (o): map:{table=MEMBER_STATUS; code=MEMBER_STATUS_CODE; ...}");
        br.addItem("Classification");
        br.addElement(_classificationName);
        if (_table != null) {
            br.addItem("Table");
            br.addElement(_table);
        }
        br.addItem("ElementMap");
        br.addElement(elementMap);
        final String msg = br.buildExceptionMessage();
        throw new DfClassificationRequiredAttributeNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _classificationName + ":{" + _table + ", " + _code + ", " + _name + ", " + _alias + ", " + _comment
                + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getClassificationName() {
        return _classificationName;
    }

    public void setClassificationName(String classificationName) {
        this._classificationName = classificationName;
    }

    public String getTable() {
        return _table;
    }

    public void setTable(String table) {
        this._table = table;
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

    public Map<String, Object> getSubItemMap() {
        return _subItemMap;
    }

    public void setSubItemMap(Map<String, Object> subItemMap) {
        this._subItemMap = subItemMap;
    }
}
