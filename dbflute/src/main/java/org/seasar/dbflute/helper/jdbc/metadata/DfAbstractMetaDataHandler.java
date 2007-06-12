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

import java.util.ArrayList;
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

    /** Simple list for except column. */
    protected List<String> _simpleColumnExceptList;

    //========================================================================================
    //                                                                                Property
    //                                                                                ========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected List<String> getTableExceptList() {
        if (_tableExceptList == null) {
            _tableExceptList = getProperties().getBasicProperties().getTableExceptList();
        }
        return _tableExceptList;
    }

    protected List<String> getTableTargetList() {
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().getBasicProperties().getTableTargetList();
        }
        return _tableTargetList;
    }

    protected List<String> getSimpleColumnExceptList() {
        if (_simpleColumnExceptList == null) {
            _simpleColumnExceptList = getProperties().getBasicProperties().getSimpleColumnExceptList();
        }
        return _simpleColumnExceptList;
    }

    //========================================================================================
    //                                                                           Determination
    //                                                                           =======-=====
    /**
     * Is the table name out of sight?
     * 
     * @param tableName Table name. (NotNull)
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

        final List<String> exceptList = getTableExceptList();
        return isExceptByHint(tableName, targetList, exceptList);
    }

    /**
     * Is the column name out of sight?
     * 
     * @param columnName Column name. (NotNull)
     * @return Determination.
     */
    protected boolean isColumnExcept(final String columnName) {
        if (columnName == null) {
            throw new NullPointerException("Argument[columnName] is required.");
        }

        final List<String> columnExceptSimpleList = getSimpleColumnExceptList();
        return isExceptByHint(columnName, new ArrayList<String>(), columnExceptSimpleList);
    }

    protected boolean isExceptByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        if (!targetList.isEmpty()) {
            for (final Iterator ite = targetList.iterator(); ite.hasNext();) {
                final String targetTableHint = (String) ite.next();
                if (isHintMatchTheName(name, targetTableHint)) {
                    return false;
                }
            }
            return true;
        }

        for (final Iterator ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (isHintMatchTheName(name, tableHint)) {
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