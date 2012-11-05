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
package org.seasar.dbflute.s2dao.rowcreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.dbflute.s2dao.metadata.TnRelationPropertyType;
import org.seasar.dbflute.s2dao.rshandler.TnRelationRowCache;

/**
 * @author modified by jflute (originated in S2Dao)
 */
public interface TnRelationRowCreator {

    /**
     * Create relation row from first level relation.
     * @param rs Result set. (NotNull)
     * @param rpt The type of relation property. (NotNull)
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @param selectIndexMap The map of select index. map:{selectColumnKeyName = selectIndex} (NullAllowed: null means select index is disabled)
     * @param relKeyValues The map of relation key values. The key is relation column name. (NullAllowed)
     * @param relPropCache The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @param relRowCache The cache of relation row. (NotNull)
     * @return The created row of the relation. (NullAllowed: if null, no data about the relation)
     * @throws SQLException
     */
    Object createRelationRow(ResultSet rs, TnRelationPropertyType rpt, Map<String, String> selectColumnMap,
            Map<String, Integer> selectIndexMap, Map<String, Object> relKeyValues,
            Map<String, Map<String, TnPropertyMapping>> relPropCache, TnRelationRowCache relRowCache)
            throws SQLException;

    /**
     * Create relation property cache.
     * @param selectColumnMap The name map of select column. map:{flexibleName = columnDbName} (NotNull)
     * @param selectIndexMap The map of select index. map:{selectColumnKeyName = selectIndex} (NullAllowed: null means select index is disabled)
     * @param bmd Bean meta data of base object. (NotNull)
     * @return The map of relation property cache. map:{relationNoSuffix = map:{columnName = PropertyMapping}} (NotNull)
     * @throws SQLException
     */
    Map<String, Map<String, TnPropertyMapping>> createPropertyCache(Map<String, String> selectColumnMap,
            Map<String, Integer> selectIndexMap, TnBeanMetaData bmd) throws SQLException;
}
