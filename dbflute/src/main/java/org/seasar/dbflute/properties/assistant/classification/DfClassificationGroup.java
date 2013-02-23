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

import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfClassificationGroup {

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

    public String getGroupTitleForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_groupComment)) {
            sb.append(_groupComment);
        } else {
            sb.append("(no comment)");
        }
        sb.append(" :: ");
        sb.append(_elementNameList);
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String title = prop.resolveAttributeForSchemaHtml(sb.toString());
        return title != null ? " title=\"" + title + "\"" : "";
    }

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
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
