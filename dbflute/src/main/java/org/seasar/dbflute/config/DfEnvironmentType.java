/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/26 Tuesday)
 */
public class DfEnvironmentType {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    public static final Log _log = LogFactory.getLog(DfEnvironmentType.class);

    /** The singleton instance of this. */
    private static final DfEnvironmentType _instance = new DfEnvironmentType();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _environmentType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private DfEnvironmentType() {
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    public static DfEnvironmentType getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                    Environment Type
    //                                                                    ================
    public boolean isSpecifiedType() {
        return _environmentType != null;
    }

    /**
     * @return The type of environment. (NotNull)
     */
    public String getEnvironmentType() {
        return _environmentType;
    }

    public void setEnvironmentType(String environmentType) { // called by Ant
        if (environmentType == null || environmentType.trim().length() == 0) {
            return;
        }
        if (environmentType.startsWith("${dfenv}")) {
            return;
        }
        _log.info("...Setting environmentType '" + environmentType + "'");
        _environmentType = environmentType;
    }
}
