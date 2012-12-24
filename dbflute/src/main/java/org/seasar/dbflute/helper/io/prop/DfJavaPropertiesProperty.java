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

/**
 * @author jflute
 * @since 1.0.1 (2012/12/21 Friday)
 */
public class DfJavaPropertiesProperty {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _propertyKey; // errors.required
    protected final String _propertyValue;
    protected String _defName; // e.g. ERRORS_REQUIRED
    protected String _camelizedName;
    protected String _capCamelName;
    protected String _uncapCamelName;
    protected String _variableArgDef;
    protected String _variableArgSet;
    protected List<Integer> _variableNumberList;
    protected String _comment;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfJavaPropertiesProperty(String propertyKey, String propertyValue) {
        _propertyKey = propertyKey;
        _propertyValue = propertyValue;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPropertyKey() {
        return _propertyKey;
    }

    public String getPropertyValue() {
        return _propertyValue;
    }

    public String getDefName() {
        return _defName;
    }

    public void setDefName(String defName) {
        _defName = defName;
    }

    public String getCamelizedName() {
        return _camelizedName;
    }

    public void setCamelizedName(String camelizedName) {
        _camelizedName = camelizedName;
    }

    public String getCapCamelName() {
        return _capCamelName;
    }

    public void setCapCamelName(String capCamelName) {
        _capCamelName = capCamelName;
    }

    public String getUncapCamelName() {
        return _uncapCamelName;
    }

    public void setUncapCamelName(String uncapCamelName) {
        _uncapCamelName = uncapCamelName;
    }

    public String getVariableArgDef() {
        return _variableArgDef;
    }

    public void setVariableArgDef(String variableArgDef) {
        _variableArgDef = variableArgDef;
    }

    public String getVariableArgSet() {
        return _variableArgSet;
    }

    public void setVariableArgSet(String variableArgSet) {
        _variableArgSet = variableArgSet;
    }

    public List<Integer> getVariableNumberList() {
        return _variableNumberList;
    }

    public void setVariableNumberList(List<Integer> variableNumberList) {
        _variableNumberList = variableNumberList;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        _comment = comment;
    }
}
