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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.task.TorqueJDBCTransformTask;
import org.seasar.dbflute.DfBuildProperties;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class DfAbstractMetaDataHandler {

    public static final Log _log = LogFactory.getLog(TorqueJDBCTransformTask.class);

    /** List for except table. */
    protected List _tableExceptList;

    /** List for target table. */
    protected List _tableTargetList;

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    public List getTableTargetList() {
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().listProp("torque.table.target.list", new ArrayList<Object>());
        }
        return _tableTargetList;
    }

    public List<String> getTableExceptList() {
        return getProperties().getTableExceptList();
    }

    /**
     * Is table out of sight?
     * 
     * @param tableName Table-name.
     * @return Determination.
     */
    public boolean isTableExcept(final String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Argument[tableName] is required.");
        }

        final List targetList = getTableTargetList();
        if (targetList == null) {
            throw new IllegalStateException("getTableTargetList() must not return null: + " + tableName);
        }

        if (!targetList.isEmpty()) {
            for (final Iterator ite = targetList.iterator(); ite.hasNext();) {
                final String targetTableHint = (String) ite.next();
                if (isHitTableHint(tableName, targetTableHint)) {
                    return false;
                }
            }
            return true;
        }

        final List exceptList = getTableExceptList();
        if (exceptList == null) {
            throw new IllegalStateException("getTableExceptList() must not return null: + " + tableName);
        }

        for (final Iterator ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (isHitTableHint(tableName, tableHint)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isHitTableHint(String tableName, String tableHint) {
        // TODO: I want to refactor this judgement logic for hint someday.
        final String prefixMark = "prefix:";
        final String suffixMark = "suffix:";

        if (tableHint.toLowerCase().startsWith(prefixMark.toLowerCase())) {
            final String pureTableHint = tableHint.substring(prefixMark.length(), tableHint.length());
            if (tableName.toLowerCase().startsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else if (tableHint.toLowerCase().startsWith(suffixMark.toLowerCase())) {
            final String pureTableHint = tableHint.substring(suffixMark.length(), tableHint.length());
            if (tableName.toLowerCase().endsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else {
            if (tableName.equalsIgnoreCase(tableHint)) {
                return true;
            }
        }
        return false;
    }

}