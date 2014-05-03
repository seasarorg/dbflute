/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.properties.facade;

import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfLanguageTypeFacadeProp {

    protected final DfBasicProperties _basicProp;

    public DfLanguageTypeFacadeProp(DfBasicProperties basicProp) {
        _basicProp = basicProp;
    }

    public String getTargetLanguage() {
        return _basicProp.getTargetLanguage();
    }

    public String getResourceDirectory() {
        return _basicProp.getResourceDirectory();
    }

    public boolean isTargetLanguageMain() {
        return _basicProp.isTargetLanguageMain();
    }

    public boolean isTargetLanguageJava() {
        return _basicProp.isTargetLanguageJava();
    }

    public boolean isTargetLanguageCSharp() {
        return _basicProp.isTargetLanguageCSharp();
    }

    public boolean isTargetLanguagePhp() {
        return _basicProp.isTargetLanguagePhp();
    }

    public boolean isTargetSubLanguageScala() {
        return _basicProp.isTargetSubLanguageScala();
    }

    public DfLanguageDependencyInfo getLanguageDependencyInfo() {
        return _basicProp.getLanguageDependencyInfo();
    }

    public String getTargetLanguageVersion() {
        return _basicProp.getTargetLanguageVersion();
    }

    public boolean isJavaVersionGreaterEqualTiger() {
        return _basicProp.isJavaVersionGreaterEqualTiger();
    }

    public boolean isJavaVersionGreaterEqualMustang() { // sub supported
        return _basicProp.isJavaVersionGreaterEqualMustang();
    }
}
