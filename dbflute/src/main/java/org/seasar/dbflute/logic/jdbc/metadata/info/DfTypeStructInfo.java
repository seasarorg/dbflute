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

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/18 Thursday)
 */
public class DfTypeStructInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UnifiedSchema _owner; // required at first
    protected String _typeName; // required at first
    protected final StringKeyMap<DfColumnMetaInfo> _attributeInfoMap = StringKeyMap.createAsFlexibleOrdered(); // required at first
    protected String _entityType; // is set after analyzing

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasEntityType() {
        return _entityType != null;
    }

    public boolean hasNestedStructEntityRef() {
        for (DfColumnMetaInfo columnInfo : _attributeInfoMap.values()) {
            if (columnInfo.hasTypeArrayInfo()) { // array in struct
                final DfTypeArrayInfo arrayInfo = columnInfo.getTypeArrayInfo();
                DfTypeArrayInfo nestedArrayInfo = arrayInfo;
                while (true) {
                    if (nestedArrayInfo.hasNestedArray()) { // array in ... in array in struct
                        nestedArrayInfo = nestedArrayInfo.getNestedArrayInfo();
                        continue;
                    }
                    if (nestedArrayInfo.hasElementStructInfo()) {
                        return true; // found, last element of nested array is struct
                    } else {
                        break; // not found in nested array
                    }
                }
            } else if (columnInfo.hasTypeStructInfo()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdditinalSchema() {
        return _owner.isAdditionalSchema();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return doToString(false);
    }

    public String toStringForHtml() {
        return doToString(true);
    }

    public String doToString(boolean escape) {
        return _typeName + ":{" + (escape ? toStringAttributeOnlyForHtml() : toStringAttributeOnly()) + "}";
    }

    public String toStringAttributeOnly() {
        return doToStringAttributeOnly(false);
    }

    public String toStringAttributeOnlyForHtml() {
        return doToStringAttributeOnly(true);
    }

    protected String doToStringAttributeOnly(boolean escape) {
        final StringBuilder sb = new StringBuilder();
        for (DfColumnMetaInfo info : _attributeInfoMap.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(info.getColumnName());
            if (info.hasTypeArrayInfo()) {
                final DfTypeArrayInfo typeArrayInfo = info.getTypeArrayInfo();
                sb.append("(");
                sb.append(escape ? typeArrayInfo.toStringForHtml() : typeArrayInfo.toString());
                sb.append(")");
            } else if (info.hasTypeStructInfo()) {
                final DfTypeStructInfo typeStructInfo = info.getTypeStructInfo();
                sb.append("(").append(typeStructInfo.toStringSimple()).append(")");
            }
        }
        return sb.toString();
    }

    public String toStringSimple() {
        return _typeName + "(" + _attributeInfoMap.size() + ")";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public UnifiedSchema getOwner() {
        return _owner;
    }

    public void setOwner(UnifiedSchema owner) {
        this._owner = owner;
    }

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
