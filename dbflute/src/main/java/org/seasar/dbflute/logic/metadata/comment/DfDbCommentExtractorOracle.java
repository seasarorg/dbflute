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
package org.seasar.dbflute.logic.metadata.comment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 */
public class DfDbCommentExtractorOracle implements DfDbCommentExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfDbCommentExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected String _schema;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public Map<String, UserTabComments> extractTableComment(Set<String> tableSet) {
        Map<String, UserTabComments> resultMap = new LinkedHashMap<String, UserTabComments>();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<UserTabComments> userTabCommentsList = selectUserTabComments(conn, tableSet);
            for (UserTabComments userTabComments : userTabCommentsList) {
                resultMap.put(userTabComments.getTableName(), userTabComments);
            }
            return resultMap;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    public Map<String, Map<String, UserColComments>> extractColumnComment(Set<String> tableSet) {
        Map<String, Map<String, UserColComments>> resultMap = new LinkedHashMap<String, Map<String, UserColComments>>();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<UserColComments> userColCommentsList = selectUserColComments(conn, tableSet);
            String previousTableName = null;
            Map<String, UserColComments> elementMap = null;
            for (UserColComments userColComments : userColCommentsList) {
                final String tableName = userColComments.getTableName();
                if (previousTableName == null || !previousTableName.equals(tableName)) {
                    previousTableName = tableName;
                    elementMap = new LinkedHashMap<String, UserColComments>();
                    resultMap.put(tableName, elementMap);
                }
                final String columnName = userColComments.getColumnName();
                elementMap.put(columnName, userColComments);
            }
            return resultMap;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    _log.info("connection.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected List<UserTabComments> selectUserTabComments(Connection conn, Set<String> tableSet) {
        final String sql = "select * from ALL_TAB_COMMENTS where OWNER = '" + _schema + "' order by TABLE_NAME asc";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            _log.info(sql);
            rs = statement.executeQuery(sql);
            final List<UserTabComments> resultList = new ArrayList<UserTabComments>();
            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                if (!tableSet.contains(tableName)) {
                    continue;
                }
                final String comments = rs.getString("COMMENTS");
                final UserTabComments userTabComments = new UserTabComments();
                userTabComments.setTableName(tableName);
                userTabComments.setComments(comments);
                resultList.add(userTabComments);
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    protected List<UserColComments> selectUserColComments(Connection conn, Set<String> tableSet) {
        final String sql = "select * from ALL_COL_COMMENTS where OWNER = '" + _schema + "'"
                + " order by TABLE_NAME asc, COLUMN_NAME asc";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            _log.info(sql);
            rs = statement.executeQuery(sql);
            final List<UserColComments> resultList = new ArrayList<UserColComments>();
            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                if (!tableSet.contains(tableName)) {
                    continue;
                }
                final String columnName = rs.getString("COLUMN_NAME");
                final String comments = rs.getString("COMMENTS");
                final UserColComments userColComments = new UserColComments();
                userColComments.setTableName(tableName);
                userColComments.setColumnName(columnName);
                userColComments.setComments(comments);
                resultList.add(userColComments);
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public String getSchema() {
        return _schema;
    }

    public void setSchema(String schema) {
        this._schema = schema;
    }
}
