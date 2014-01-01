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
package org.seasar.dbflute.helper.language.properties;

/**
 * @author jflute
 */
public class DfGeneratedClassPackageDefaultCSharp implements DfGeneratedClassPackageDefault {

    public String getBaseCommonPackage() {
        return "AllCommon";
    }

    public String getBaseBehaviorPackage() {
        return "BsBhv";
    }

    public String getBaseDaoPackage() {
        return "BsDao";
    }

    public String getCursorSimplePackageName() {
        return "Cursor";
    }

    public String getParameterBeanSimplePackageName() {
        return "PmBean";
    }

    public String getBaseEntityPackage() {
        return "BsEntity";
    }

    public String getCustomizeEntitySimplePackageName() {
        return "Customize";
    }

    public String getDBMetaSimplePackageName() {
        return "Dbm";
    }

    public String getConditionBeanPackage() {
        return "CBean";
    }

    public String getExtendedBehaviorPackage() {
        return "ExBhv";
    }

    public String getExtendedDaoPackage() {
        return "ExDao";
    }

    public String getExtendedEntityPackage() {
        return "ExEntity";
    }
}