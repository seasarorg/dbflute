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

import org.seasar.dbflute.exception.DfCraftDiffCraftTitleNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.Srl;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 0.9.9.8 (2012/09/04 Tuesday)
 */
public class DfCraftDiffAssertProvider {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _craftMetaDir;
    protected final DfCraftDiffAssertDirection _nextDirection;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCraftDiffAssertProvider(String craftMetaDir, DfCraftDiffAssertDirection nextDirection) {
        _craftMetaDir = craftMetaDir;
        _nextDirection = nextDirection;
    }

    // ===================================================================================
    //                                                                             Provide
    //                                                                             =======
    /**
     * @param sqlFile The text file that has the specified SQL. (NotNull) 
     * @param sql The SQL string to assert. (NotNull)
     * @return The handle of CraftDiff assert. (NullAllowed: if null, means not found)
     */
    public DfCraftDiffAssertHandler provideCraftDiffAssertHandler(File sqlFile, String sql) {
        if (!sql.contains("--")) {
            return null;
        }
        // resolve comment spaces
        final String resolvedSql = DfStringUtil.replace(sql, "-- #", "--#");

        // CraftDiff supports only equals
        final String keyPrefix = "--#df:assertEquals(";
        final String keySuffix = ")#";
        final ScopeInfo scopeFirst = Srl.extractScopeFirst(resolvedSql, keyPrefix, keySuffix);
        if (scopeFirst == null) {
            return null; // not found
        }
        final String craftTitle = scopeFirst.getContent().trim();
        if (Srl.is_Null_or_TrimmedEmpty(craftTitle)) {
            throwCraftDiffCraftTitleNotFoundException(sqlFile, sql);
        }
        // *unsupported envType on assert definition
        return new DfCraftDiffAssertHandler(_craftMetaDir, _nextDirection, craftTitle);
    }

    protected void throwCraftDiffCraftTitleNotFoundException(File sqlFile, String sql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the craft title in the SQL.");
        br.addItem("Advice");
        br.addElement("The assertion should have its title like this:");
        br.addElement("  -- #df:assertEquals([craft-title])#");
        br.addElement("");
        br.addElement("For example:");
        br.addElement("  (o): -- #df:assertEquals(Trigger)#");
        br.addElement("  (x): -- #df:assertEquals()#");
        br.addItem("SQL File");
        br.addElement(sqlFile.getPath());
        br.addItem("Assertion SQL");
        br.addElement(sql);
        final String msg = br.buildExceptionMessage();
        throw new DfCraftDiffCraftTitleNotFoundException(msg);
    }
}
