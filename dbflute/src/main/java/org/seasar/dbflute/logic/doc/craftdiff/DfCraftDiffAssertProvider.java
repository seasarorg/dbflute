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

import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffAssertProvider {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _craftMetaDir;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffAssertProvider(String craftMetaDir) {
        _craftMetaDir = craftMetaDir;
    }

    // ===================================================================================
    //                                                                             Provide
    //                                                                             =======
    /**
     * @param sql The SQL string to assert. (NotNull)
     * @return The handle of CraftDiff assert. (NullAllowed: if null, means not found)
     */
    public DfCraftDiffAssertHandler provideCraftDiffAssertHandler(String sql) {
        if (!sql.contains("--")) {
            return null;
        }
        final String starter = "#df:";
        final String terminator = "#";

        // resolve comment spaces
        sql = DfStringUtil.replace(sql, "-- #", "--#");

        final String methodName = "assertEquals"; // CraftDiff supports only equals
        final String keyPrefix = "--" + starter + methodName + "(";
        if (!sql.contains(keyPrefix)) {
            return null;
        }
        final String rearOfKey = Srl.substringFirstRear(sql, keyPrefix);
        final String keySuffix = ")" + terminator;
        if (!rearOfKey.contains(keySuffix)) {
            return null;
        }
        final String craftTitle = Srl.substringFirstFront(rearOfKey, keySuffix);
        if (Srl.is_Null_or_TrimmedEmpty(craftTitle)) {
            // TODO jflute exception
            throw new IllegalStateException();
        }
        // *unsupported envType on assert definition
        return new DfCraftDiffAssertHandler(_craftMetaDir, craftTitle);
    }
}
