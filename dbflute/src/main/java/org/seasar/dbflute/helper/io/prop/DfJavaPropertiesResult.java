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
package org.seasar.dbflute.helper.io.prop;

import java.util.List;
import java.util.Properties;

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfJavaPropertiesResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Properties _plainProp;
    protected final List<DfJavaPropertiesProperty> _propertyList;
    protected final List<String> _duplicateKeyList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfJavaPropertiesResult(Properties plainProp, List<DfJavaPropertiesProperty> propertyList,
            List<String> duplicateKeyList) {
        _plainProp = plainProp;
        _propertyList = propertyList;
        _duplicateKeyList = duplicateKeyList;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Properties getPlainProp() {
        return _plainProp;
    }

    public List<DfJavaPropertiesProperty> getPropertyList() {
        return _propertyList;
    }

    public List<String> getDuplicateKeyList() {
        return _duplicateKeyList;
    }
}
