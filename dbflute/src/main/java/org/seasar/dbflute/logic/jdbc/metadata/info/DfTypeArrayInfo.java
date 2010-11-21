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
    protected String _typeName; // required
    protected String _elementType; // required
    protected DfTypeStructInfo _structInfo; // if element type is STRUCT
    protected String _elementJavaNative; // is set after analyzing

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasStructInfo() { // means the element type is STRUCT
        return _structInfo != null;
    }

    public boolean hasElementJavaNative() {
        return _elementJavaNative != null;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    @Override
    public String toString() {
        return _typeName + "<" + _elementType + ">" + (_structInfo != null ? " (struct)" : "");
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

    public DfTypeStructInfo getStructInfo() {
        return _structInfo;
    }

    public void setStructInfo(DfTypeStructInfo structInfo) {
        this._structInfo = structInfo;
    }

    public String getElementJavaNative() {
        return _elementJavaNative;
    }

    public void setElementJavaNative(String elementJavaNative) {
        this._elementJavaNative = elementJavaNative;
    }
}
