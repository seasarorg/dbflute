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
package org.seasar.dbflute.helper.jdbc.metadata.comment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author jflute
 * @since 0.9.5.3 (2009/08/06 Thursday)
 */
public class DfDbCommentExtractorSqlServer extends DfDbCommentExtractorBase {

    // ===================================================================================
    //                                                                    Select Meta Data
    //                                                                    ================
    protected List<UserTabComments> selectUserTabComments(Connection conn, Set<String> tableSet) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select objtype as OBJECT_TYPE, objname as TABLE_NAME, value as COMMENTS");
        sb.append(" from fn_listextendedproperty");
        sb.append("('MS_Description', '").append(_schema).append("'");
        sb.append(", 'dbo', 'table', default, default, default)");
        sb.append(" order by TABLE_NAME asc");
        final String sql = sb.toString();
        return doSelectUserTabComments(sql, conn, tableSet);
    }

    protected List<UserColComments> selectUserColComments(Connection conn, Set<String> tableSet) {
        final List<UserColComments> resultList = new ArrayList<UserColComments>();
        for (String tableName : tableSet) {
            final String sql = buildUserColCommentsSql(tableName);
            final List<UserColComments> userColComments = doSelectUserColComments(sql, conn, tableSet);
            resultList.addAll(userColComments);
        }
        return resultList;
    }

    protected String buildUserColCommentsSql(String tableName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select '").append(tableName).append("' as TABLE_NAME");
        sb.append(", objname as COLUMN_NAME, value as COMMENTS");
        sb.append(" from fn_listextendedproperty");
        sb.append("('MS_Description', '").append(_schema).append("'");
        sb.append(", 'dbo', 'table', '").append(tableName).append(", 'column', default)");
        sb.append(" order by TABLE_NAME asc, COLUMN_NAME asc");
        return sb.toString();
    }
}
