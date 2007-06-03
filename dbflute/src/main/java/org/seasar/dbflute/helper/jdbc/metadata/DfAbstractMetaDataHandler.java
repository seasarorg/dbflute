/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.util.Iterator;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;

/**
 * @author jflute
 */
public class DfAbstractMetaDataHandler {

    //========================================================================================
    //                                                                               Attribute
    //                                                                               =========
    /** List for except table. */
    protected List<String> _tableExceptList;

    /** List for target table. */
    protected List<String> _tableTargetList;

    //========================================================================================
    //                                                                                Property
    //                                                                                ========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected List<String> getTableExceptList() {
        if (_tableExceptList == null) {
            final List<String> tableExceptList = getProperties().getBasicProperties().getTableExceptList();
            _tableExceptList = tableExceptList;
        }
        return _tableExceptList;
    }

    protected List<String> getTableTargetList() {
        if (_tableTargetList == null) {
            final List<String> tableTargetList = getProperties().getBasicProperties().getTableTargetList();
            _tableTargetList = tableTargetList;
        }
        return _tableTargetList;
    }

    //========================================================================================
    //                                                                           Determination
    //                                                                           =======-=====
    /**
     * Is the table out of sight?
     * 
     * @param tableName Table-name. (NotNull)
     * @return Determination.
     */
    protected boolean isTableExcept(final String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Argument[tableName] is required.");
        }

        final List<String> targetList = getTableTargetList();
        if (targetList == null) {
            throw new IllegalStateException("getTableTargetList() must not return null: + " + tableName);
        }

        if (!targetList.isEmpty()) {
            for (final Iterator ite = targetList.iterator(); ite.hasNext();) {
                final String targetTableHint = (String) ite.next();
                if (isHintMatchTheName(tableName, targetTableHint)) {
                    return false;
                }
            }
            return true;
        }

        final List<String> exceptList = getTableExceptList();
        if (exceptList == null) {
            throw new IllegalStateException("getTableExceptList() must not return null: + " + tableName);
        }

        for (final Iterator ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (isHintMatchTheName(tableName, tableHint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the hint match the name?
     * 
     * @param name Target name. (NotNull)
     * @param hint Hint-string that contains prefix-mark or suffix-mark. (NotNull)
     * @return Determination.
     */
    protected boolean isHintMatchTheName(String name, String hint) {
        // TODO: I want to refactor this judgement logic for hint someday.
        final String prefixMark = "prefix:";
        final String suffixMark = "suffix:";

        if (hint.toLowerCase().startsWith(prefixMark.toLowerCase())) {
            final String pureTableHint = hint.substring(prefixMark.length(), hint.length());
            if (name.toLowerCase().startsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else if (hint.toLowerCase().startsWith(suffixMark.toLowerCase())) {
            final String pureTableHint = hint.substring(suffixMark.length(), hint.length());
            if (name.toLowerCase().endsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else {
            if (name.equalsIgnoreCase(hint)) {
                return true;
            }
        }
        return false;
    }

}