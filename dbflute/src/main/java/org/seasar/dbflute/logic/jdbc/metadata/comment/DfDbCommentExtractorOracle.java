/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.comment;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * @author jflute
 */
public class DfDbCommentExtractorOracle extends DfDbCommentExtractorBase {

    // ===================================================================================
    //                                                                    Select Meta Data
    //                                                                    ================
    protected List<UserTabComments> selectUserTabComments(Connection conn, Set<String> tableSet) {
        if (!_unifiedSchema.existsPureSchema()) {
            String msg = "Extracting comments from Oracle requires pure schema in unified schema:";
            msg = msg + " unifiedSchema=" + _unifiedSchema;
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from ALL_TAB_COMMENTS");
        sb.append(" where OWNER = '").append(_unifiedSchema.getPureSchema()).append("'");
        sb.append(" order by TABLE_NAME asc");
        final String sql = sb.toString();
        return doSelectUserTabComments(sql, conn, tableSet);
    }

    protected List<UserColComments> selectUserColComments(Connection conn, Set<String> tableSet) {
        if (!_unifiedSchema.existsPureSchema()) {
            String msg = "Extracting comments from Oracle requires pure schema in unified schema:";
            msg = msg + " unifiedSchema=" + _unifiedSchema;
            throw new IllegalStateException(msg);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from ALL_COL_COMMENTS");
        sb.append(" where OWNER = '").append(_unifiedSchema.getPureSchema()).append("'");
        sb.append(" order by TABLE_NAME asc, COLUMN_NAME asc");
        final String sql = sb.toString();
        return doSelectUserColComments(sql, conn, tableSet);
    }
}
