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
package org.seasar.dbflute.helper.dataset.types;

import java.util.Arrays;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsBinaryType extends DfDtsObjectType {

    private static final Class<?> TYPE = new byte[0].getClass();

    public DfDtsBinaryType() {
    }

    public Object convert(Object value, String formatPattern) {
        if (value != null && value instanceof String) {
            return DfTypeUtil.toStringBytes((String) value, "UTF-8");
        }
        return value;
    }

    protected boolean doEquals(Object arg1, Object arg2) {
        if (arg1 instanceof byte[] && arg2 instanceof byte[]) {
            return Arrays.equals((byte[]) arg1, (byte[]) arg2);
        }
        return false;
    }

    public Class<?> getType() {
        return TYPE;
    }
}