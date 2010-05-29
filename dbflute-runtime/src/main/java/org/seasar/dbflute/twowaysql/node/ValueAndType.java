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
package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;

/**
 * @author jflute
 */
public class ValueAndType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _targetValue;
    protected Class<?> _targetType;
    protected LikeSearchOption _likeSearchOption;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public void filterValueByOptionIfNeeds() {
        if (_likeSearchOption == null) {
            return;
        }
        if (_targetValue instanceof String) {
            _targetValue = _likeSearchOption.generateRealValue((String) _targetValue);
        } else { // no way
            String msg = "The target value should be string:";
            msg = msg + " " + _targetValue + ", " + _targetType;
            throw new IllegalStateException(msg);
        }
    }

    public String buildRearOptionOnSql() {
        if (_likeSearchOption == null) {
            return null;
        }
        final String rearOption = _likeSearchOption.getRearOption();
        return " " + rearOption.trim() + " ";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getTargetValue() {
        return _targetValue;
    }

    public void setTargetValue(Object targetValue) {
        this._targetValue = targetValue;
    }

    public Class<?> getTargetType() {
        return _targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this._targetType = targetType;
    }

    public LikeSearchOption getLikeSearchOption() {
        return _likeSearchOption;
    }

    public void setLikeSearchOption(LikeSearchOption likeSearchOption) {
        this._likeSearchOption = likeSearchOption;
    }
}
