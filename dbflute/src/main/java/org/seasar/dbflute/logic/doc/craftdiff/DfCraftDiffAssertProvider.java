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
package org.seasar.dbflute.logic.doc.craftdiff;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffAssertProvider {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCraftDiffAssertProvider.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _envType;
    protected final String _craftMetaDir;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffAssertProvider(String envType, String craftMetaDir) {
        _envType = envType;
        _craftMetaDir = craftMetaDir;
    }

    // ===================================================================================
    //                                                                             Provide
    //                                                                             =======
    /**
     * @param sql SQL string. (NotNull)
     * @return The handle of craft-diff assert. (NullAllowed)
     */
    public DfCraftDiffAssertHandler provideCraftDiffAssertHandler(String sql) {
        if (!sql.contains("--")) {
            return null;
        }
        final String starter = "#df:";
        final String terminator = "#";
        final String typeAtMark = "@";

        // resolve comment spaces
        sql = DfStringUtil.replace(sql, "-- #", "--#");

        final String methodName = "assertEquals"; // CraftDiff supports only equals
        final String keyPrefix = "--" + starter + methodName + "(";
        if (!sql.contains(keyPrefix)) {
            return null;
        }
        final String rearOfKey = Srl.substringFirstRear(sql, keyPrefix);
        if (!rearOfKey.contains(")")) {
            return null;
        }
        final String parameter = Srl.substringFirstFront(rearOfKey, ")");
        final List<String> parameterList = Srl.splitListTrimmed(parameter, ",");
        if (parameterList.size() >= 2) {
            // TODO jflute exception
            throw new IllegalStateException();
        }
        final String craftTitle = parameterList.remove(0);
        final String keyName = parameterList.remove(0);
        // parameterList becomes diffItemList here TODO jflute necessary?
        final String rearOfParameter = Srl.substringFirstRear(rearOfKey, ")");
        final String option = Srl.substringFirstFront(rearOfParameter, terminator);
        final String envType = option.startsWith(typeAtMark) ? Srl.substringFirstRear(option, typeAtMark) : null;
        if (isDifferentEnvType(envType)) {
            return createDefaultHandler(craftTitle, envType);
        }
        return new DfCraftDiffAssertHandlerImpl(_craftMetaDir, craftTitle, keyName, parameterList);
    }

    protected boolean isDifferentEnvType(final String envType) {
        if (_envType == null && envType == null) {
            return true;
        }
        return _envType != null && !_envType.equals(envType);
    }

    protected DfCraftDiffAssertHandler createDefaultHandler(String craftTitle, String envType) {
        final String msg = "...Skipping for the different envType: " + craftTitle + "@" + envType + "/" + _envType;
        return new DfCraftDiffAssertHandler() {
            public void handle(File sqlFile, Statement st, String sql) throws SQLException {
                _log.info(msg);
            }
        };
    }
}
