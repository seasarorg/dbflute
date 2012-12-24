/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.properties.assistant.classification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.exception.DfClassificationRequiredAttributeNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
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

    public static final String KEY_CHECK_IMPLICIT_SET = "isCheckImplicitSet";
    public static final String KEY_USE_DOCUMENT_ONLY = "isUseDocumentOnly";
    public static final String KEY_SUPPRESS_AUTO_DEPLOY = "isSuppressAutoDeploy";
    public static final String KEY_DEPRECATED = "isDeprecated";
    public static final String KEY_GROUPING_MAP = "groupingMap";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _classificationName;
    protected String _topComment;
    protected String _codeType = DfClassificationTop.CODE_TYPE_STRING; // as default
    protected String _relatedColumnName;
    protected final List<DfClassificationElement> _elementList = new ArrayList<DfClassificationElement>();
    protected boolean _tableClassification;
    protected boolean _checkImplicitSet;
    protected boolean _useDocumentOnly;
    protected boolean _suppressAutoDeploy;
    protected boolean _deprecated;
    protected final Map<String, Map<String, Object>> _groupingMap = new LinkedHashMap<String, Map<String, Object>>();

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptClassificationTopBasicItemMap(Map<?, ?> elementMap) {
        acceptTopMap(elementMap, KEY_TOP_COMMENT, KEY_CODE_TYPE, KEY_DATA_TYPE);
    }

    protected void acceptTopMap(Map<?, ?> elementMap, String commentKey, String codeTypeKey, String dataTypeKey) {
        // topComment
        final String topComment = (String) elementMap.get(commentKey);
        if (topComment == null) {
            throwClassificationLiteralCommentNotFoundException(_classificationName, elementMap);
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
        if (codeType != null) {
            this._codeType = codeType;
        }
    }

    protected void throwClassificationLiteralCommentNotFoundException(String classificationName, Map<?, ?> elementMap) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The comment attribute of the classification was not found.");
        br.addItem("Advice");
        br.addElement("The classification should have the comment attribute.");
        br.addElement("See the document for the DBFlute property.");
        br.addItem("Classification");
        br.addElement(classificationName);
        br.addItem("Element Map");
        br.addElement(elementMap);
        final String msg = br.buildExceptionMessage();
        throw new DfClassificationRequiredAttributeNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasTopComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_topComment);
    }

    public boolean hasCodeType() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(_codeType);
    }

    public boolean isSisterBooleanHandling() {
        if (_elementList.size() != 2) {
            return false;
        }
        final Set<String> firstSet = new HashSet<String>();
        {
            final String[] firstSisters = _elementList.get(0).getSisters();
            for (String sister : firstSisters) {
                firstSet.add(sister.toLowerCase());
            }
        }
        final Set<String> secondSet = new HashSet<String>();
        {
            final String[] secondSisters = _elementList.get(1).getSisters();
            for (String sister : secondSisters) {
                secondSet.add(sister.toLowerCase());
            }
        }
        return (firstSet.contains("true") && secondSet.contains("false") // first true
        || firstSet.contains("false") && secondSet.contains("true")); // first false
    }

    // ===================================================================================
    //                                                                         SubItem Map
    //                                                                         ===========
    public boolean hasSubItem() {
        final List<DfClassificationElement> elementList = getClassificationElementList();
        for (DfClassificationElement element : elementList) {
            Map<String, Object> subItemMap = element.getSubItemMap();
            if (subItemMap != null && !subItemMap.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<DfClassificationRegularSubItem> getRegularSubItemList() {
        final List<DfClassificationElement> elementList = getClassificationElementList();
        final Map<String, List<Object>> subItemListMap = new LinkedHashMap<String, List<Object>>();
        for (DfClassificationElement element : elementList) {
            final Map<String, Object> subItemMap = element.getSubItemMap();
            if (subItemMap == null) {
                continue;
            }
            for (Entry<String, Object> entry : subItemMap.entrySet()) {
                final String subItemKey = entry.getKey();
                final Object subItemValue = entry.getValue();
                List<Object> subItemList = subItemListMap.get(subItemKey);
                if (subItemList == null) {
                    subItemList = new ArrayList<Object>();
                    subItemListMap.put(subItemKey, subItemList);
                }
                subItemList.add(subItemValue);
            }
        }
        final String typeObject = DfClassificationRegularSubItem.TYPE_OBJECT;
        final String typeString = DfClassificationRegularSubItem.TYPE_STRING;
        final List<DfClassificationRegularSubItem> regularSubItemList = new ArrayList<DfClassificationRegularSubItem>();
        final int elementSize = elementList.size();
        for (Entry<String, List<Object>> entry : subItemListMap.entrySet()) {
            final String subItemKey = entry.getKey();
            final List<Object> subItemList = entry.getValue();
            if (subItemList != null && subItemList.size() == elementSize) {
                String subItemType = null;
                for (Object value : subItemList) {
                    if (value == null) {
                        continue;
                    }
                    if (!(value instanceof String)) {
                        subItemType = typeObject;
                        break;
                    } else if (Srl.startsWith((String) value, "map:", "list:")) {
                        subItemType = typeObject;
                        break;
                    }
                }
                if (subItemType == null) {
                    subItemType = typeString;
                }
                regularSubItemList.add(new DfClassificationRegularSubItem(subItemKey, subItemType));
            }
        }
        return regularSubItemList;
    }

    public static class DfClassificationRegularSubItem {
        // Object or String only supported
        public static final String TYPE_OBJECT = "Object";
        public static final String TYPE_STRING = "String";

        protected final String _subItemName;
        protected final String _subItemType;

        public DfClassificationRegularSubItem(String subItemName, String subItemType) {
            _subItemName = subItemName;
            _subItemType = subItemType;
        }

        public boolean isSubItemTypeObject() {
            return _subItemType.equals(TYPE_OBJECT);
        }

        public boolean isSubItemTypeString() {
            return _subItemType.equals(TYPE_STRING);
        }

        public String getSubItemName() {
            return _subItemName;
        }

        public String getSubItemType() {
            return _subItemType;
        }
    }

    // ===================================================================================
    //                                                                        Grouping Map
    //                                                                        ============
    public List<DfClassificationGroup> getGroupList() {
        final List<DfClassificationGroup> groupList = new ArrayList<DfClassificationGroup>();
        for (Entry<String, Map<String, Object>> entry : _groupingMap.entrySet()) {
            final String groupName = entry.getKey();
            final Map<String, Object> attrMap = entry.getValue();
            final String groupComment = (String) attrMap.get("groupComment");
            @SuppressWarnings("unchecked")
            final List<String> elementList = (List<String>) attrMap.get("elementList");
            if (elementList == null) {
                String msg = "The elementList in grouping map is required: " + getClassificationName();
                throw new DfClassificationRequiredAttributeNotFoundException(msg);
            }
            final DfClassificationGroup group = new DfClassificationGroup(_classificationName, groupName);
            group.setGroupComment(groupComment);
            group.setElementNameList(elementList);
            groupList.add(group);
        }
        return groupList;
    }

    public static class DfClassificationGroup {
        protected final String _classificationName;
        protected final String _groupName;
        protected String _groupComment;
        protected List<String> _elementNameList;

        public DfClassificationGroup(String classificationName, String groupName) {
            _classificationName = classificationName;
            _groupName = groupName;
        }

        public String getGroupNameInitCap() {
            return Srl.initCap(_groupName);
        }

        public boolean hasGroupComment() {
            return _groupComment != null;
        }

        public String buildReturnExpThis() {
            return doBuildReturnExp("this");
        }

        protected String doBuildReturnExp(String target) {
            final StringBuilder sb = new StringBuilder();
            int index = 0;
            for (String elementName : _elementNameList) {
                if (index > 0) {
                    sb.append(" || ");
                }
                sb.append(elementName).append(".equals(").append(target).append(")");
                ++index;
            }
            return sb.toString();
        }

        public String buildCDefArgExp(String cdefClassName) {
            final StringBuilder sb = new StringBuilder();
            int index = 0;
            for (String elementName : _elementNameList) {
                if (index > 0) {
                    sb.append(", ");
                }
                sb.append(cdefClassName).append(".").append(_classificationName);
                sb.append(".").append(elementName);
                ++index;
            }
            return sb.toString();
        }

        public String buildElementDisp() {
            return "The group elements:" + _elementNameList;
        }

        public String getClassificationName() {
            return _classificationName;
        }

        public String getGroupName() {
            return _groupName;
        }

        public String getGroupComment() {
            return _groupComment;
        }

        public void setGroupComment(String groupComment) {
            this._groupComment = groupComment;
        }

        public List<String> getElementNameList() {
            return _elementNameList;
        }

        public void setElementNameList(List<String> elementNameList) {
            this._elementNameList = elementNameList;
        }
    }

    // ===================================================================================
    //                                                              Classification Element
    //                                                              ======================
    public int getElementSize() {
        return _elementList.size();
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
        if (_useDocumentOnly) {
            return _topComment + " (document only)";
        } else {
            return _topComment;
        }
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
        classificationElement.setClassificationTop(this);
        this._elementList.add(classificationElement);
    }

    public void addClassificationElementAll(List<DfClassificationElement> classificationElementList) {
        for (DfClassificationElement element : classificationElementList) {
            element.setClassificationTop(this);
        }
        this._elementList.addAll(classificationElementList);
    }

    public boolean isTableClassification() {
        return _tableClassification;
    }

    public void setTableClassification(boolean tableClassification) {
        this._tableClassification = tableClassification;
    }

    public boolean isCheckImplicitSet() {
        return _checkImplicitSet;
    }

    public void setCheckImplicitSet(boolean checkImplicitSet) {
        this._checkImplicitSet = checkImplicitSet;
    }

    public boolean isUseDocumentOnly() {
        return _useDocumentOnly;
    }

    public void setUseDocumentOnly(boolean useDocumentOnly) {
        this._useDocumentOnly = useDocumentOnly;
    }

    public boolean isSuppressAutoDeploy() {
        return _suppressAutoDeploy;
    }

    public void setSuppressAutoDeploy(boolean suppressAutoDeploy) {
        this._suppressAutoDeploy = suppressAutoDeploy;
    }

    public boolean isDeprecated() {
        return _deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this._deprecated = deprecated;
    }

    public Map<String, Map<String, Object>> getGroupingMap() {
        return this._groupingMap;
    }

    public void putGroupingAll(Map<String, Map<String, Object>> groupingMap) {
        this._groupingMap.putAll(groupingMap);
    }
}
