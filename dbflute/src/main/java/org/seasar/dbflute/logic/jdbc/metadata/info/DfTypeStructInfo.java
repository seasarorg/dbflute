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

import org.seasar.dbflute.helper.StringKeyMap;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/18 Thursday)
 */
public class DfTypeStructInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _typeName;
    protected final StringKeyMap<DfColumnMetaInfo> _attributeInfoMap = StringKeyMap.createAsFlexibleOrdered();
    protected String _entityType; // is set after analyzing

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (DfColumnMetaInfo info : _attributeInfoMap.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(info.getColumnName());
        }
        return _typeName + "(" + sb.toString() + ")";
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

    public StringKeyMap<DfColumnMetaInfo> getAttributeInfoMap() {
        return _attributeInfoMap;
    }

    public void putAttributeInfo(DfColumnMetaInfo attributeInfo) {
        _attributeInfoMap.put(attributeInfo.getColumnName(), attributeInfo);
    }

    public String getEntityType() {
        return _entityType;
    }

    public void setEntityType(String entityType) {
        this._entityType = entityType;
    }
}