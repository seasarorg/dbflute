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

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public Map<String, String> extractTableComment(Set<String> tableSet) {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<UserTabComments> userTabCommentsList = selectUserTabComments(conn, tableSet);
            for (UserTabComments userTabComments : userTabCommentsList) {
                final String tableName = userTabComments.getTableName();
                final String comments = userTabComments.getComments();
                resultMap.put(tableName, comments);
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

    public Map<String, String> extractColumnComment(String tableName) {
        Map<String, String> resultMap = new LinkedHashMap<String, String>();
        Connection conn = null;
        try {
            conn = _dataSource.getConnection();
            final List<UserColComments> userColCommentsList = selectUserColComments(conn, tableName);
            for (UserColComments userColComments : userColCommentsList) {
                final String columnName = userColComments.getColumnName();
                final String comments = userColComments.getComments();
                resultMap.put(columnName, comments);
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
        final String sql = "select * from USER_TAB_COMMENTS order by TABLE_NAME asc";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            final List<UserTabComments> resultList = new ArrayList<UserTabComments>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!tableSet.contains(tableName)) {
                    continue;
                }
                String comments = rs.getString("COMMENTS");
                UserTabComments userTabComments = new UserTabComments();
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

    protected List<UserColComments> selectUserColComments(Connection conn, String tableName) {
        final String sql = "select * from USER_COL_COMMENTS where TABLE_NAME = '" + tableName + "' order by TABLE_NAME asc, COLUMN_NAME asc";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            final List<UserColComments> resultList = new ArrayList<UserColComments>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String comments = rs.getString("COMMENTS");
                UserColComments userColComments = new UserColComments();
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

    protected static class UserTabComments {
        protected String tableName;
        protected String comments;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    protected static class UserColComments {
        protected String tableName;
        protected String columnName;
        protected String comments;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
}
