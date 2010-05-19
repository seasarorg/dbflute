/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.loaddata;

/**
 * @author jflute
 */
public class DfSeparatedDataSeveralHandlingInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String basePath;
    protected String typeName;
    protected String delimter;
    protected boolean errorContinue;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDelimter() {
        return delimter;
    }

    public void setDelimter(String delimter) {
        this.delimter = delimter;
    }

    public boolean isErrorContinue() {
        return errorContinue;
    }

    public void setErrorContinue(boolean errorContinue) {
        this.errorContinue = errorContinue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
