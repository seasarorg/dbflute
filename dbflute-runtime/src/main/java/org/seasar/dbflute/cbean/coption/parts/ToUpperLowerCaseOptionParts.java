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
package org.seasar.dbflute.cbean.coption.parts;

import java.io.Serializable;

/**
 * The class of condition-option-parts about toUpperCase/toLowerCase.
 * @author jflute
 */
public class ToUpperLowerCaseOptionParts implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _toUpperCase;
    protected boolean _toLowerCase;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public void toUpperCase() {
        _toUpperCase = true;
        _toLowerCase = false;
    }

    public void toLowerCase() {
        _toUpperCase = false;
        _toLowerCase = true;
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    public String generateRealValue(String value) {
        if (value == null) {
            return value;
        }

        // To Upper/Lower Case
        if (_toUpperCase) {
            value = value.toUpperCase();
        }
        if (_toLowerCase) {
            value = value.toLowerCase();
        }
        return value;
    }

    // ===================================================================================
    //                                                                           Deep Copy
    //                                                                           =========
    public Object createDeepCopy() {
        final ToUpperLowerCaseOptionParts deepCopy = new ToUpperLowerCaseOptionParts();
        deepCopy._toUpperCase = _toUpperCase;
        deepCopy._toLowerCase = _toLowerCase;
        return deepCopy;
    }
}
