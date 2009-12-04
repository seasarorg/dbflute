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
package org.seasar.dbflute.logic.schemainitializer;

import java.sql.Connection;
import java.util.List;

import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;

/**
 * @author jflute
 */
public class DfSchemaInitializerMySQL extends DfSchemaInitializerJdbc {

    // ===================================================================================
    //                                                                    Drop Foreign Key
    //                                                                    ================
    @Override
    protected void dropForeignKey(Connection connection, List<DfTableMetaInfo> tableMetaInfoList) {
        final DfDropForeignKeyByJdbcCallback callback = new DfDropForeignKeyByJdbcCallback() {
            public String buildDropForeignKeySql(DfForeignKeyMetaInfo metaInfo) {
                final String foreignKeyName = metaInfo.getForeignKeyName();
                final String localTableName = filterTableName(metaInfo.getLocalTableName());
                final StringBuilder sb = new StringBuilder();
                sb.append("alter table ").append(localTableName).append(" drop foreign key ").append(foreignKeyName);
                return sb.toString();
            }
        };
        callbackDropForeignKeyByJdbc(connection, tableMetaInfoList, callback);
    }
}