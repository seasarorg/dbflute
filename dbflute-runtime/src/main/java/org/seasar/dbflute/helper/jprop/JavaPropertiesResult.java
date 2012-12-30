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
package org.seasar.dbflute.helper.jprop;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class JavaPropertiesResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Properties _plainProp;
    protected final List<JavaPropertiesProperty> _propertyList; // merged list
    protected final List<JavaPropertiesProperty> _propertyBasePointOnlyList;
    protected final List<JavaPropertiesProperty> _propertyExtendsOnlyList;
    protected final List<String> _duplicateKeyList;
    protected final JavaPropertiesResult _extendsPropResult;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public JavaPropertiesResult(Properties plainProp, List<JavaPropertiesProperty> propertyList,
            List<String> duplicateKeyList) {
        this(plainProp, propertyList, duplicateKeyList, null);
    }

    public JavaPropertiesResult(Properties plainProp, List<JavaPropertiesProperty> propertyList,
            List<String> duplicateKeyList, JavaPropertiesResult extendsPropResult) {
        _plainProp = plainProp;
        _propertyList = propertyList;
        _propertyBasePointOnlyList = new ArrayList<JavaPropertiesProperty>();
        _propertyExtendsOnlyList = new ArrayList<JavaPropertiesProperty>();
        for (JavaPropertiesProperty property : propertyList) {
            if (property.isExtends()) {
                _propertyExtendsOnlyList.add(property);
            } else {
                _propertyBasePointOnlyList.add(property);
            }
        }
        _duplicateKeyList = duplicateKeyList;
        _extendsPropResult = extendsPropResult;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Properties getPlainProp() {
        return _plainProp;
    }

    public List<JavaPropertiesProperty> getPropertyList() {
        return _propertyList;
    }

    public List<JavaPropertiesProperty> getPropertyBasePointOnlyList() {
        return _propertyBasePointOnlyList;
    }

    public List<JavaPropertiesProperty> getPropertyExtendsOnlyList() {
        return _propertyExtendsOnlyList;
    }

    public List<String> getDuplicateKeyList() {
        return _duplicateKeyList;
    }

    public JavaPropertiesResult getExtendsPropResult() {
        return _extendsPropResult;
    }
}
