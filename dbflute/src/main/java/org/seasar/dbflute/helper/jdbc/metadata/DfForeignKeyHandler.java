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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class DfForeignKeyHandler extends DfAbstractMetaDataHandler {

    public static final Log _log = LogFactory.getLog(DfForeignKeyHandler.class);

    /**
     * Retrieves a list of foreign key columns for a given table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve FK information.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    public Collection getForeignKeys(DatabaseMetaData dbMeta, String schemaName, String tableName)
            throws SQLException {
        final Hashtable<String, Object[]> fks = new Hashtable<String, Object[]>();
        ResultSet foreignKeys = null;
        try {
            foreignKeys = dbMeta.getImportedKeys(null, schemaName, tableName);
            while (foreignKeys.next()) {
                String refTableName = foreignKeys.getString(3);

                if (isTableExcept(refTableName)) {
                    continue;
                }

                String fkName = foreignKeys.getString(12);
                // if FK has no name - make it up (use tablename instead)
                if (fkName == null) {
                    fkName = refTableName;
                }
                Object[] fk = (Object[]) fks.get(fkName);
                List<String[]> refs;
                if (fk == null) {
                    refs = new ArrayList<String[]>();
                    fk = new Object[2];
                    fk[0] = refTableName; //referenced table name
                    fk[1] = refs;
                    fks.put(fkName, fk);
                } else {
                    refs = (List<String[]>) fk[1];
                }
                String[] ref = new String[2];
                ref[0] = foreignKeys.getString(8); //local column
                ref[1] = foreignKeys.getString(4); //foreign column
                refs.add(ref);
            }
        } finally {
            if (foreignKeys != null) {
                foreignKeys.close();
            }
        }

        return filterSameForeignKey(fks).values();
    }

    protected Map<String, Object[]> filterSameForeignKey(Map<String, Object[]> fks) {
        final Map<String, Object[]> retFksMap = new LinkedHashMap<String, Object[]>();
        final Map<Map<String, Object>, Object> checkMap = new LinkedHashMap<Map<String, Object>, Object>();
        final Set<String> foreignKeyNameSet = fks.keySet();
        for (String foreinKeyName : foreignKeyNameSet) {
            final Object[] objArray = fks.get(foreinKeyName);
            final String refTableName = (String) objArray[0];
            final List<String[]> refs = (List<String[]>) objArray[1];
            final Map<String, Object> checkKeyMap = new LinkedHashMap<String, Object>();
            checkKeyMap.put(refTableName, new Object());
            for (String[] oneColumnElement : refs) {
                checkKeyMap.put("localColumn:" + oneColumnElement[0], new Object());
                checkKeyMap.put("foreignColumn:" + oneColumnElement[1], new Object());
            }
            if (checkMap.containsKey(checkKeyMap)) {
                String msg = "A structural one of the same row already exists.";
                msg = msg + "The skipped foreign-key name is " + foreinKeyName + ".";
                msg = msg + " The columns are " + checkKeyMap + ".";
                _log.warn(msg);
            } else {
                checkMap.put(checkKeyMap, new Object());
                retFksMap.put(foreinKeyName, objArray);
            }
        }
        return retFksMap;
    }
}