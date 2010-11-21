/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.info;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/18 Thursday)
 */
public class DfTypeArrayInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _typeName; // required at first
    protected String _elementType; // required at first (if unknown, the value is "UNKNOWN")
    protected DfTypeArrayInfo _nestedArrayInfo; // if element type is ARRAY (nested)
    protected DfTypeStructInfo _elementStructInfo; // if element type is STRUCT
    protected String _elementJavaNative; // is set after analyzing

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasNestedArray() { // means the element type is ARRAY
        return _nestedArrayInfo != null;
    }

    public boolean hasElementStructInfo() { // means the element type is STRUCT
        return _elementStructInfo != null;
    }

    public boolean hasElementJavaNative() {
        return _elementJavaNative != null;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_typeName).append("<").append(_elementType).append(">");
        if (_elementStructInfo != null) {
            sb.append(":{").append(_elementStructInfo.getTypeName());
            sb.append("(").append(_elementStructInfo.getAttributeInfoMap().size()).append("}");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTypeName() {
        return _typeName;
    }

    public void setTypeName(String typeName) {
        this._typeName = typeName;
    }

    public String getElementType() {
        return _elementType;
    }

    public void setElementType(String elementType) {
        this._elementType = elementType;
    }

    public DfTypeArrayInfo getNestedArrayInfo() {
        return _nestedArrayInfo;
    }

    public void setNestedArrayInfo(DfTypeArrayInfo nestedArrayInfo) {
        this._nestedArrayInfo = nestedArrayInfo;
    }

    public DfTypeStructInfo getElementStructInfo() {
        return _elementStructInfo;
    }

    public void setElementStructInfo(DfTypeStructInfo structInfo) {
        this._elementStructInfo = structInfo;
    }

    public String getElementJavaNative() {
        return _elementJavaNative;
    }

    public void setElementJavaNative(String elementJavaNative) {
        this._elementJavaNative = elementJavaNative;
    }
}
