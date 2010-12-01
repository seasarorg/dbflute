package org.seasar.dbflute.properties.assistant.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.Srl;

/**
 * Temporary DTO when classification initializing.
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationTop {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String KEY_TOP_COMMENT = "topComment";
    public static final String KEY_CODE_TYPE = "codeType";
    public static final String KEY_DATA_TYPE = "dataType"; // old style

    public static final String CODE_TYPE_STRING = "String";
    public static final String CODE_TYPE_NUMBER = "Number";
    public static final String CODE_TYPE_BOOLEAN = "Boolean";
    public static final String DEFAULT_CODE_TYPE = CODE_TYPE_STRING;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _classificationName;
    protected String _topComment;
    protected String _codeType;
    protected String _relatedColumnName;
    protected List<DfClassificationElement> _elementList = new ArrayList<DfClassificationElement>();

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptClassificationTopElementMap(Map<?, ?> elementMap) {
        acceptTopMap(elementMap, KEY_TOP_COMMENT, KEY_CODE_TYPE, KEY_DATA_TYPE);
    }

    protected void acceptTopMap(Map<?, ?> elementMap, String commentKey, String codeTypeKey, String dataTypeKey) {
        // topComment
        final String topComment = (String) elementMap.get(commentKey);
        if (topComment == null) {
            String msg = "The elementMap should have " + commentKey + ".";
            throw new IllegalStateException(msg);
        }
        this._topComment = topComment;

        // codeType
        final String codeType;
        {
            String tmpType = (String) elementMap.get(codeTypeKey);
            if (Srl.is_Null_or_TrimmedEmpty(tmpType)) {
                // for compatibility
                tmpType = (String) elementMap.get(dataTypeKey);
            }
            codeType = tmpType;
        }
        this._codeType = codeType;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _classificationName + ", " + _topComment + ", " + _codeType + ", " + _relatedColumnName + ", "
                + _elementList + "}";
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

    public String getTopComment() {
        return _topComment;
    }

    public void setTopComment(String topComment) {
        this._topComment = topComment;
    }

    public String getCodeType() {
        return _codeType;
    }

    public void setCodeType(String codeType) {
        _codeType = codeType;
    }

    public String getRelatedColumnName() {
        return _relatedColumnName;
    }

    public void setRelatedColumnName(String relatedColumnName) {
        this._relatedColumnName = relatedColumnName;
    }

    public List<DfClassificationElement> getClassificationElementList() {
        return _elementList;
    }

    public void addClassificationElement(DfClassificationElement classificationElement) {
        this._elementList.add(classificationElement);
    }
}
