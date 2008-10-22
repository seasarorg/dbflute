package org.seasar.dbflute.properties.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 * @since 0.8.2 (2008/10/22 Wednesday)
 */
public class DfClassificationTop {

    protected String _classificationName;
    protected String _topComment;
    protected List<DfClassificationElement> _elementList = new ArrayList<DfClassificationElement>();

    @Override
    public String toString() {
        return "{" + _classificationName + ", " + _topComment + ", " + _elementList + "}";
    }

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

    public List<DfClassificationElement> getClassificationElementList() {
        return _elementList;
    }

    public void addClassificationElement(DfClassificationElement classificationElement) {
        this._elementList.add(classificationElement);
    }
}
