/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.various;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/18 Thursday)
 */
public class DfStructExtractorOracle {

    private static final Log _log = LogFactory.getLog(DfStructExtractorOracle.class);

    protected final DataSource _dataSource;

    public DfStructExtractorOracle(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public Map<String, List<OracleStructAttributeInfo>> assistStructInfoMap(UnifiedSchema unifiedSchema) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final List<String> columnList = new ArrayList<String>();
        columnList.add("TYPE_NAME");
        columnList.add("ATTR_NAME");
        columnList.add("ATTR_TYPE_NAME");
        final String sql = buildStructAttributeSql(unifiedSchema);
        final List<Map<String, String>> resultList;
        try {
            _log.info(sql);
            resultList = facade.selectStringList(sql, columnList);
        } catch (Exception continued) {
            // because of assist info
            _log.info("Failed to select supplement info: " + continued.getMessage());
            return DfCollectionUtil.newHashMap();
        }
        final Map<String, List<OracleStructAttributeInfo>> structInfoMap = StringKeyMap.createAsFlexibleOrdered();
        for (Map<String, String> map : resultList) {
            final String typeName = map.get("TYPE_NAME");
            List<OracleStructAttributeInfo> infoList = structInfoMap.get(typeName);
            if (infoList == null) {
                infoList = DfCollectionUtil.newArrayList();
                structInfoMap.put(typeName, infoList);
            }
            final OracleStructAttributeInfo info = new OracleStructAttributeInfo();
            info.setTypeName(typeName);
            info.setAttrName(map.get("ATTR_NAME"));
            info.setAttrTypeName(map.get("ATTR_TYPE_NAME"));
            infoList.add(info);
        }
        return structInfoMap;
    }

    protected String buildStructAttributeSql(UnifiedSchema unifiedSchema) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select *");
        sb.append(" from ALL_TYPE_ATTRS");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "'");
        sb.append(" and TYPE_NAME in (");
        sb.append("select TYPE_NAME from ALL_TYPES");
        sb.append(" where OWNER = '" + unifiedSchema.getPureSchema() + "' and TYPECODE = 'OBJECT'");
        sb.append(")");
        sb.append(" order by TYPE_NAME, ATTR_NO");
        return sb.toString();
    }

    public static class OracleStructAttributeInfo {
        protected String _typeName;
        protected String _attrName;
        protected String _attrTypeName;

        public String getTypeName() {
            return _typeName;
        }

        public void setTypeName(String typeName) {
            this._typeName = typeName;
        }

        public String getAttrName() {
            return _attrName;
        }

        public void setAttrName(String attrName) {
            this._attrName = attrName;
        }

        public String getAttrTypeName() {
            return _attrTypeName;
        }

        public void setAttrTypeName(String attrTypeName) {
            this._attrTypeName = attrTypeName;
        }
    }
}
