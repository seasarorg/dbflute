/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.metadata.impl;

import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyType;

/**
 * @author modified by jflute (originated in S2Dao)
 */
public class TnRelationPropertyTypeImpl extends TnPropertyTypeImpl implements TnRelationPropertyType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final int _relationNo;
    protected final String _relationNoSuffixPart;
    protected final String[] _myKeys;
    protected final String[] _yourKeys;
    protected final TnBeanMetaData _myBeanMetaData;
    protected final TnBeanMetaData _yourBeanMetaData;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnRelationPropertyTypeImpl(DfPropertyDesc propertyDesc, int relationNo, String[] myKeys, String[] yourKeys,
            TnBeanMetaData myBeanMetaData, TnBeanMetaData yourBeanMetaData) {
        super(propertyDesc);
        _relationNo = relationNo;
        _relationNoSuffixPart = buildRelationNoSuffixPart(relationNo);
        _myKeys = myKeys;
        _yourKeys = yourKeys;
        _myBeanMetaData = myBeanMetaData;
        _yourBeanMetaData = yourBeanMetaData;
    }

    protected String buildRelationNoSuffixPart(int relationNo) {
        return "_" + relationNo;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getRelationNo() {
        return _relationNo;
    }

    public String getRelationNoSuffixPart() {
        return _relationNoSuffixPart;
    }

    public int getKeySize() {
        if (_myKeys.length > 0) {
            return _myKeys.length;
        } else {
            return _yourBeanMetaData.getPrimaryKeySize();
        }

    }

    public String getMyKey(int index) {
        if (_myKeys.length > 0) {
            return _myKeys[index];
        } else {
            return _yourBeanMetaData.getPrimaryKeyDbName(index);
        }
    }

    public String getYourKey(int index) {
        if (_yourKeys.length > 0) {
            return _yourKeys[index];
        } else {
            return _yourBeanMetaData.getPrimaryKeyDbName(index);
        }
    }

    public boolean isYourKey(String columnName) {
        for (int i = 0; i < getKeySize(); ++i) {
            if (columnName.equalsIgnoreCase(getYourKey(i))) {
                return true;
            }
        }
        return false;
    }

    public TnBeanMetaData getMyBeanMetaData() {
        return _myBeanMetaData;
    }

    public TnBeanMetaData getYourBeanMetaData() {
        return _yourBeanMetaData;
    }
}