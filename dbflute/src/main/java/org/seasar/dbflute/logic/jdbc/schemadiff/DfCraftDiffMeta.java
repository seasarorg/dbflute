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
package org.seasar.dbflute.logic.jdbc.schemadiff;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffMeta {

    protected final String _craftKeyName;
    protected final String _craftValue;

    public DfCraftDiffMeta(String craftKeyName, String craftValue) {
        _craftKeyName = craftKeyName;
        _craftValue = craftValue;
    }

    public String getCraftKeyName() {
        return _craftKeyName;
    }

    public String getCraftValue() {
        return _craftValue;
    }
}
