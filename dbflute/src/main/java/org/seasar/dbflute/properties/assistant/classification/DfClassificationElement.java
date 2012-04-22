package org.seasar.dbflute.properties.assistant.classification;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.exception.DfClassificationRequiredAttributeNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.Srl;

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
    public static final String KEY_SISTER_CODE = "sisterCode";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_SUB_ITEM_MAP = "subItemMap";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _classificationName; // required
    protected String _table; // table classification only

    // basic items
    protected String _code;
    protected String _name;
    protected String _alias;
    protected String _comment;
    protected String[] _sisters = new String[] {}; // as default
    protected Map<String, Object> _subItemMap;

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptBasicItemMap(Map<?, ?> elementMap) {
        doAcceptBasicItemMap(elementMap, KEY_CODE, KEY_NAME, KEY_ALIAS, KEY_COMMENT, KEY_SISTER_CODE, KEY_SUB_ITEM_MAP);
    }

    protected void doAcceptBasicItemMap(Map<?, ?> elementMap, String codeKey, String nameKey, String aliasKey,
            String commentKey, String sisterCodeKey, String subItemMapKey) {
        final String code = (String) elementMap.get(codeKey);
        if (code == null) {
            throwClassificationRequiredAttributeNotFoundException(elementMap);
        }
        this._code = code;

        final String name = (String) elementMap.get(nameKey);
        this._name = (name != null ? name : code); // same as code if null

        final String alias = (String) elementMap.get(aliasKey);
        this._alias = (alias != null ? alias : name); // same as name if null

        this._comment = (String) elementMap.get(commentKey);

        final Object sisterCodeObj = elementMap.get(sisterCodeKey);
        if (sisterCodeObj != null) {
            if (sisterCodeObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                final List<String> sisterCodeList = (List<String>) sisterCodeObj;
                this._sisters = sisterCodeList.toArray(new String[sisterCodeList.size()]);
            } else {
                this._sisters = new String[] { (String) sisterCodeObj };
            }
        } else {
            this._sisters = new String[] {};
        }

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
    //                                                                       Determination
    //                                                                       =============
    public boolean hasAlias() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_alias);
    }

    public boolean hasComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_comment);
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

    public String[] getSisters() {
        return _sisters;
    }

    public void setSisters(String[] sisters) {
        this._sisters = sisters;
    }

    public Map<String, Object> getSubItemMap() {
        return _subItemMap;
    }

    public void setSubItemMap(Map<String, Object> subItemMap) {
        this._subItemMap = subItemMap;
    }
}
