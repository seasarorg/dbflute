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
package org.seasar.dbflute.helper.io.prop;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public class DfJavaPropertiesResult {

    protected final Properties _plainProp;
    protected final List<Map<String, Object>> _propertyList;
    protected final List<String> _duplicateKeyList;

    public DfJavaPropertiesResult(Properties plainProp, List<Map<String, Object>> propertyList,
            List<String> duplicateKeyList) {
        _plainProp = plainProp;
        _propertyList = propertyList;
        _duplicateKeyList = duplicateKeyList;
    }

    public Properties getPlainProp() {
        return _plainProp;
    }

    public List<Map<String, Object>> getPropertyList() {
        return _propertyList;
    }

    public List<String> getDuplicateKeyList() {
        return _duplicateKeyList;
    }
}
