/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.valuetype.impl;

import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.dbflute.s2dao.valuetype.plugin.TnBytesOidType;
import org.seasar.dbflute.s2dao.valuetype.plugin.TnStringClobType;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class TnValueTypeFactoryImpl implements TnValueTypeFactory {

    // ===================================================================================
    //                                                                   Plug-in ValueType
    //                                                                   =================
    {
        TnValueTypes.registerPluginValueType("stringClobType", new TnStringClobType());
        TnValueTypes.registerPluginValueType("bytesOidType", new TnBytesOidType());
        
        // for compatible
        TnValueTypes.registerPluginValueType("dbfluteStringClobType", new TnStringClobType());
        TnValueTypes.registerPluginValueType("dbfluteBytesOidType", new TnBytesOidType());
    }

    // ===================================================================================
    //                                                                   ValueType Getting
    //                                                                   =================
    public ValueType getValueTypeByName(String valueTypeName) {
        return TnValueTypes.getPluginValueType(valueTypeName);
    }

    public ValueType getValueTypeByClass(Class<?> clazz) {
        return TnValueTypes.getValueType(clazz);
    }
}
