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
package org.dbflute.s2dao.beans.impl;

import org.dbflute.s2dao.beans.ParameterizedClassDesc;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class ParameterizedClassDescImpl implements ParameterizedClassDesc {

    protected Class<?> rawClass;

    protected ParameterizedClassDesc[] arguments;

    public ParameterizedClassDescImpl() {
    }

    public ParameterizedClassDescImpl(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    public ParameterizedClassDescImpl(final Class<?> rawClass, final ParameterizedClassDesc[] arguments) {
        this.rawClass = rawClass;
        this.arguments = arguments;
    }

    public boolean isParameterizedClass() {
        return arguments != null;
    }

    public Class<?> getRawClass() {
        return rawClass;
    }

    public void setRawClass(final Class<?> rawClass) {
        this.rawClass = rawClass;
    }

    public ParameterizedClassDesc[] getArguments() {
        return arguments;
    }

    public void setArguments(final ParameterizedClassDesc[] arguments) {
        this.arguments = arguments;
    }

}
