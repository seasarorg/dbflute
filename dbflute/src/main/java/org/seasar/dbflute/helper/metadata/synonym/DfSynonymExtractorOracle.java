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
package org.seasar.dbflute.helper.metadata.synonym;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.helper.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.helper.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.helper.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.helper.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.helper.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.metadata.DfAutoIncrementHandler;
import org.seasar.dbflute.logic.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.logic.metadata.DfIndexHandler;
import org.seasar.dbflute.logic.metadata.DfTableHandler;
import org.seasar.dbflute.logic.metadata.DfUniqueKeyHandler;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorOracle implements DfSynonymExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSynonymExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected String _schema;
    protected Set<String> _refTableCheckSet;

    protected DfTableHandler _tableHandler = new DfTableHandler();
    protected DfUniqueKeyHandler _uniqueKeyHandler = new DfUniqueKeyHandler();
    protected DfAutoIncrementHandler _autoIncrementHandler = new DfAutoIncrementHandler();
    protected DfForeignKeyHandler _foreignKeyHandler = new DfForeignKeyHandler() {
        @Override
        public boolean isTableExcept(String tableName) {
            // All foreign tables are target if the foreign table is except.
            // Because the filtering is executed when translating foreign keys.
            return false;
        }
    };
    protected DfIndexHandler _indexHandler = new DfIndexHandler();

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public Map<String, DfSynonymMetaInfo> extractSynonymMap() {
        final Connection conn;
        try {
            conn = _dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        final String sql = "select * from ALL_SYNONYMS where OWNER = '" + _schema + "'";
        Statement statement = null;
        ResultSet rs = null;
        try {
            final Map<String, DfSynonymMetaInfo> synonymMap = new LinkedHashMap<String, DfSynonymMetaInfo>();
            statement = conn.createStatement();
            _log.info(sql);
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                final String synonymName = rs.getString("SYNONYM_NAME");
                final String tableOwner = rs.getString("TABLE_OWNER");
                final String tableName = rs.getString("TABLE_NAME");
                final String dbLinkName = rs.getString("DB_LINK");

                if (_tableHandler.isTableExcept(synonymName)) {
                    continue;
                }
                if (dbLinkName != null && dbLinkName.trim().length() > 0) {
                    // = = = = = = = = = = = = 
                    // It's a DB Link Synonym!
                    // = = = = = = = = = = = = 
                    try {
                        synonymMap.put(synonymName, setupDBLinkSynonym(conn, synonymName, tableName, dbLinkName));
                    } catch (Exception continued) {
                        _log.info("Failed to get meta data of " + synonymName + ": " + continued.getMessage());
                    }
                    continue;
                }
                if (tableOwner == null || tableOwner.trim().length() == 0) {
                    continue; // basically no way because it may be for DB Link Synonym
                }

                // = = = = = = = = = = = = 
                // It's a normal Synonym!
                // = = = = = = = = = = = = 
                final DfSynonymMetaInfo info = new DfSynonymMetaInfo();

                // Basic
                info.setSynonymName(synonymName);
                info.setTableOwner(tableOwner);
                info.setTableName(tableName);

                // PK, ID, UQ, FK, Index
                try {
                    final DatabaseMetaData metaData = conn.getMetaData();
                    info.setPrimaryKeyNameList(getPKList(metaData, tableOwner, tableName));
                    final List<String> primaryKeyNameList = info.getPrimaryKeyNameList();
                    for (String primaryKeyName : primaryKeyNameList) {
                        final boolean autoIncrement = isAutoIncrement(conn, tableOwner, tableName, primaryKeyName);
                        if (autoIncrement) {
                            info.setAutoIncrement(autoIncrement);
                            break;
                        }
                    }
                    info.setUniqueKeyMap(getUQMap(metaData, tableOwner, tableName, primaryKeyNameList));
                    info.setForeignKeyMetaInfoMap(getFKMap(metaData, tableOwner, tableName)); // It's tentative information at this timing!
                    final Map<String, Map<Integer, String>> uniqueKeyMap = info.getUniqueKeyMap();
                    info.setIndexMap(_indexHandler.getIndexMap(metaData, tableOwner, tableName, uniqueKeyMap));
                } catch (Exception continued) {
                    _log.info("Failed to get meta data of " + synonymName + ": " + continued.getMessage());
                    continue;
                }
                synonymMap.put(synonymName, info);
            }
            translateFKTable(synonymMap); // It translates foreign key meta informations. 
            judgeSequenceSynonym(synonymMap, conn);
            setupTableColumnComment(synonymMap);
            return synonymMap;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    protected DfSynonymMetaInfo setupDBLinkSynonym(Connection conn, String synonymName, String tableName,
            String dbLinkName) throws SQLException {
        final DfSynonymMetaInfo info = new DfSynonymMetaInfo();
        info.setSynonymName(synonymName);
        info.setTableName(tableName);
        info.setDbLinkName(dbLinkName);
        final List<DfColumnMetaInfo> columnMetaInfoList = getDBLinkSynonymColumns(conn, synonymName);
        info.setColumnMetaInfoList(columnMetaInfoList);
        final List<String> primaryKeyNameList = getDBLinkSynonymPKList(conn, tableName, dbLinkName);
        info.setPrimaryKeyNameList(primaryKeyNameList);
        final Map<String, Map<Integer, String>> uniqueKeyMap = getDBLinkSynonymUQMap(conn, tableName, dbLinkName);
        info.setUniqueKeyMap(uniqueKeyMap);

        // It does not support Foreign Key of DBLink.
        info.setForeignKeyMetaInfoMap(new LinkedHashMap<String, DfForeignKeyMetaInfo>());

        // It does not support Index of DBLink.
        info.setIndexMap(new LinkedHashMap<String, Map<Integer, String>>());

        return info;
    }

    // ===================================================================================
    //                                                                           Meta Data
    //                                                                           =========
    // -----------------------------------------------------
    //                                    For Normal Synonym
    //                                    ------------------
    protected List<String> getPKList(DatabaseMetaData metaData, String tableOwner, String tableName) {
        try {
            return _uniqueKeyHandler.getPrimaryColumnNameList(metaData, tableOwner, tableName);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, Map<Integer, String>> getUQMap(DatabaseMetaData metaData, String tableOwner,
            String tableName, List<String> primaryKeyNameList) {
        try {
            return _uniqueKeyHandler.getUniqueKeyMap(metaData, tableOwner, tableName, primaryKeyNameList);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, DfForeignKeyMetaInfo> getFKMap(DatabaseMetaData metaData, String tableOwner, String tableName) {
        try {
            return _foreignKeyHandler.getForeignKeyMetaInfo(metaData, tableOwner, tableName);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected boolean isAutoIncrement(Connection conn, String tableOwner, String tableName, String primaryKeyColumnName) {
        try {
            final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
            tableMetaInfo.setTableName(tableName);
            tableMetaInfo.setTableSchema(tableOwner);
            return _autoIncrementHandler.isAutoIncrementColumn(conn, tableMetaInfo, primaryKeyColumnName);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void translateFKTable(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Collection<DfSynonymMetaInfo> synonymList = synonymMap.values();
        final Map<String, List<String>> tableForeignSynonymListMap = new LinkedHashMap<String, List<String>>();
        for (DfSynonymMetaInfo synonym : synonymList) {
            final String synonymName = synonym.getSynonymName();
            final String tableName = synonym.getTableName();
            final List<String> foreignSynonymList = tableForeignSynonymListMap.get(tableName);
            if (foreignSynonymList != null) {
                foreignSynonymList.add(synonymName);
            } else {
                final List<String> foreignNewSynonymList = new ArrayList<String>();
                foreignNewSynonymList.add(synonymName);
                tableForeignSynonymListMap.put(tableName, foreignNewSynonymList);
            }
        }
        for (DfSynonymMetaInfo synonym : synonymList) {
            final Map<String, DfForeignKeyMetaInfo> fkMap = synonym.getForeignKeyMetaInfoMap();
            if (fkMap == null || fkMap.isEmpty()) {
                continue;
            }
            final Set<String> fkNameSet = fkMap.keySet();
            final Map<String, DfForeignKeyMetaInfo> additionalFKMap = new LinkedHashMap<String, DfForeignKeyMetaInfo>();
            final Map<String, String> removedFKKeyMap = new LinkedHashMap<String, String>();
            for (String fkName : fkNameSet) {
                final DfForeignKeyMetaInfo fk = fkMap.get(fkName);

                // - - - - - - - - - - - - - - -
                // Translate a local table name.
                // - - - - - - - - - - - - - - -
                fk.setLocalTableName(synonym.getSynonymName());

                // - - - - - - - - - - - - - - - -
                // Translate a foreign table name.
                // - - - - - - - - - - - - - - - -
                final String foreignTableName = fk.getForeignTableName();

                final List<String> foreignSynonymList = tableForeignSynonymListMap.get(foreignTableName);
                if (foreignSynonymList == null || foreignSynonymList.isEmpty()) {
                    if (_tableHandler.isTableExcept(foreignTableName)) {
                        removedFKKeyMap.put(fkName, foreignTableName);
                    } else if (_refTableCheckSet != null && !_refTableCheckSet.contains(foreignTableName)) {
                        removedFKKeyMap.put(fkName, foreignTableName);
                    }
                    continue;
                }
                boolean firstDone = false;
                for (int i = 0; i < foreignSynonymList.size(); i++) {
                    final String foreignSynonymName = foreignSynonymList.get(i);

                    if (!firstDone) {
                        fk.setForeignTableName(foreignSynonymName);
                        firstDone = true;
                        continue;
                    }

                    final DfForeignKeyMetaInfo additionalFK = new DfForeignKeyMetaInfo();
                    additionalFK.setForeignKeyName(fk.getForeignKeyName() + "_" + (i + 1));
                    additionalFK.setLocalTableName(synonym.getSynonymName());
                    additionalFK.setForeignTableName(foreignSynonymName);
                    additionalFK.setColumnNameMap(fk.getColumnNameMap());
                    additionalFKMap.put(additionalFK.getForeignKeyName(), additionalFK);
                }
            }
            fkMap.putAll(additionalFKMap);
            if (!removedFKKeyMap.isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("...Excepting foreign keys from the synonym:").append(ln()).append("[Excepted Foreign Key]");
                final Set<String> removedFKKeySet = removedFKKeyMap.keySet();
                for (String removedKey : removedFKKeySet) {
                    sb.append(ln()).append(" ").append(removedKey);
                    sb.append(" (").append(synonym.getSynonymName()).append(" to ");
                    sb.append(removedFKKeyMap.get(removedKey)).append(")");
                    fkMap.remove(removedKey);
                }
                _log.info(sb.toString());
            }
        }
    }

    /**
     * Judge where it is sequence synonym or not. <br />
     * This does not support DB link synonym.
     * @param synonymMap The map of synonym. (NotNull)
     * @param conn The connection to database. (NotNull)
     */
    protected void judgeSequenceSynonym(Map<String, DfSynonymMetaInfo> synonymMap, Connection conn) {
        final Set<String> ownerSet = createOwnerSet(synonymMap);
        if (ownerSet.isEmpty()) {
            return;
        }
        final StringBuilder inSb = new StringBuilder();
        for (String owner : ownerSet) {
            if (inSb.length() > 0) {
                inSb.append(", ");
            }
            inSb.append("'").append(owner).append("'");
        }
        final Set<String> sequenceNameSet = new HashSet<String>();
        final String metaDataSql = "select * from ALL_SEQUENCES where SEQUENCE_OWNER in (" + inSb.toString() + ")";
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            _log.info(metaDataSql);
            rs = statement.executeQuery(metaDataSql);
            while (rs.next()) {
                final String sequenceOwner = rs.getString("SEQUENCE_OWNER");
                final String sequenceName = rs.getString("SEQUENCE_NAME");
                sequenceNameSet.add(sequenceOwner + "." + sequenceName);
            }
        } catch (SQLException continued) {
            String msg = "*Failed to the SQL:" + ln();
            msg = msg + (continued.getMessage() != null ? continued.getMessage() : null) + ln();
            msg = msg + metaDataSql;
            _log.info(metaDataSql);
            return;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                    _log.info("statement.close() threw the exception!", ignored);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                    _log.info("rs.close() threw the exception!", ignored);
                }
            }
        }
        final StringBuilder logSb = new StringBuilder();
        logSb.append("...Judging sequence synonym: ");
        logSb.append(ln()).append("[Sequence Synonym]");
        boolean exists = false;
        for (String synonymKey : synonymMap.keySet()) {
            final DfSynonymMetaInfo synonym = synonymMap.get(synonymKey);
            if (synonym.isDBLink()) { // Synonym of DB Link is out of target!
                continue;
            }
            final String name = synonym.getTableOwner() + "." + synonym.getTableName();
            if (sequenceNameSet.contains(name)) {
                exists = true;
                logSb.append(ln()).append(" " + name);
                synonym.setSequenceSynonym(true);
            }
        }
        if (exists) {
            _log.info(logSb.toString());
        }
    }

    protected Set<String> createOwnerSet(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Set<String> ownerSet = new LinkedHashSet<String>();
        for (DfSynonymMetaInfo synonym : synonymMap.values()) {
            if (synonym.isDBLink()) { // Synonym of DB Link is out of target!
                continue;
            }
            final String owner = synonym.getTableOwner();
            ownerSet.add(owner);
        }
        return ownerSet;
    }

    /**
     * Set up table and column comment. <br />
     * This does not support DB link synonym.
     * @param synonymMap The map of synonym. (NotNull)
     */
    protected void setupTableColumnComment(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Map<String, Set<String>> ownerTabSetMap = createOwnerTableSetMap(synonymMap);
        final Map<String, Map<String, UserTabComments>> ownerTabCommentMap = new LinkedHashMap<String, Map<String, UserTabComments>>();
        final Map<String, Map<String, Map<String, UserColComments>>> ownerTabColCommentMap = new LinkedHashMap<String, Map<String, Map<String, UserColComments>>>();
        final Set<String> ownerSet = ownerTabSetMap.keySet();
        for (String owner : ownerSet) {
            final Set<String> tableSet = ownerTabSetMap.get(owner);
            final DfDbCommentExtractorOracle extractor = createDbCommentExtractor(owner);
            final Map<String, UserTabComments> tabCommentMap = extractor.extractTableComment(tableSet);
            final Map<String, Map<String, UserColComments>> tabColCommentMap = extractor.extractColumnComment(tableSet);
            ownerTabCommentMap.put(owner, tabCommentMap);
            ownerTabColCommentMap.put(owner, tabColCommentMap);
        }

        for (DfSynonymMetaInfo synonym : synonymMap.values()) {
            final String owner = synonym.getTableOwner();
            final String tableName = synonym.getTableName();
            final Map<String, UserTabComments> tableCommentMap = ownerTabCommentMap.get(owner);
            if (tableCommentMap != null) {
                final UserTabComments userTabComments = tableCommentMap.get(tableName);
                if (userTabComments != null && userTabComments.hasComments()) {
                    synonym.setTableComment(userTabComments.getComments());
                }
            }
            final Map<String, Map<String, UserColComments>> tabColCommentMap = ownerTabColCommentMap.get(owner);
            if (tabColCommentMap != null) {
                final Map<String, UserColComments> colCommentMap = tabColCommentMap.get(tableName);
                if (colCommentMap != null && !colCommentMap.isEmpty()) {
                    synonym.setColumnCommentMap(colCommentMap);
                }
            }
        }
    }

    protected DfDbCommentExtractorOracle createDbCommentExtractor(String schema) {
        final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
        extractor.setDataSource(_dataSource);
        extractor.setSchema(schema);
        return extractor;
    }

    protected Map<String, Set<String>> createOwnerTableSetMap(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Map<String, Set<String>> ownerTabSetMap = new LinkedHashMap<String, Set<String>>();
        for (DfSynonymMetaInfo synonym : synonymMap.values()) {
            final String owner = synonym.getTableOwner();
            if (synonym.isDBLink()) { // Synonym of DB Link is out of target!
                continue;
            }
            Set<String> tableSet = ownerTabSetMap.get(owner);
            if (tableSet == null) {
                tableSet = new LinkedHashSet<String>();
                ownerTabSetMap.put(owner, tableSet);
            }
            tableSet.add(synonym.getTableName());
        }
        return ownerTabSetMap;
    }

    // -----------------------------------------------------
    //                                   For DB Link Synonym
    //                                   -------------------
    protected List<DfColumnMetaInfo> getDBLinkSynonymColumns(Connection conn, String dbLinkSynonymName)
            throws SQLException {
        final List<DfColumnMetaInfo> columnList = new ArrayList<DfColumnMetaInfo>();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("select * from " + dbLinkSynonymName + " where 1=0");
            final ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 0; i < count; i++) {
                int index = i + 1;
                String columnName = metaData.getColumnName(index);
                int columnType = metaData.getColumnType(index);
                String columnTypeName = metaData.getColumnTypeName(index);
                int precision = metaData.getPrecision(index);
                int scale = metaData.getScale(index);
                int nullableType = metaData.isNullable(index);
                DfColumnMetaInfo column = new DfColumnMetaInfo();
                column.setColumnName(columnName);
                column.setJdbcDefValue(columnType);
                column.setDbTypeName(columnTypeName);
                column.setColumnSize(precision);
                column.setDecimalDigits(scale);
                column.setRequired(nullableType == ResultSetMetaData.columnNoNulls);
                columnList.add(column);
            }
            return columnList;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    protected List<String> getDBLinkSynonymPKList(Connection conn, String tableName, String dbLinkName)
            throws SQLException {
        final List<String> columnList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        sb.append("select cols.OWNER, cols.CONSTRAINT_NAME, cols.TABLE_NAME, cols.COLUMN_NAME");
        sb.append("  from USER_CONS_COLUMNS@" + dbLinkName + " cols");
        sb.append("    left outer join USER_CONSTRAINTS@" + dbLinkName + " cons");
        sb.append("      on cols.CONSTRAINT_NAME = cons.CONSTRAINT_NAME");
        sb.append(" where cols.TABLE_NAME = '" + tableName + "'");
        sb.append("   and cons.CONSTRAINT_TYPE = 'P'");
        sb.append(" order by cols.POSITION");
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sb.toString());
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                columnList.add(columnName);
            }
            return columnList;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    protected Map<String, Map<Integer, String>> getDBLinkSynonymUQMap(Connection conn, String tableName,
            String dbLinkName) throws SQLException {
        final Map<String, Map<Integer, String>> uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();
        final StringBuilder sb = new StringBuilder();
        sb.append("select cols.OWNER, cols.CONSTRAINT_NAME, cols.TABLE_NAME, cols.COLUMN_NAME, cols.POSITION");
        sb.append("  from USER_CONS_COLUMNS@" + dbLinkName + " cols");
        sb.append("    left outer join USER_CONSTRAINTS@" + dbLinkName + " cons");
        sb.append("      on cols.CONSTRAINT_NAME = cons.CONSTRAINT_NAME");
        sb.append(" where cols.TABLE_NAME = '" + tableName + "'");
        sb.append("   and cons.CONSTRAINT_TYPE = 'U'");
        sb.append(" order by cols.CONSTRAINT_NAME, cols.POSITION");
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sb.toString());
            while (rs.next()) {
                final String constraintName = rs.getString("CONSTRAINT_NAME");
                final String columnName = rs.getString("COLUMN_NAME");
                final Integer position = rs.getInt("POSITION");
                Map<Integer, String> uniqueElementMap = uniqueMap.get(uniqueMap);
                if (uniqueElementMap == null) {
                    uniqueElementMap = new LinkedHashMap<Integer, String>();
                    uniqueMap.put(constraintName, uniqueElementMap);
                }
                uniqueElementMap.put(position, columnName);
            }
            return uniqueMap;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

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

    public Set<String> getRefTableCheckSet() {
        return _refTableCheckSet;
    }

    public void setRefTableCheckSet(Set<String> refTableCheckSet) {
        this._refTableCheckSet = refTableCheckSet;
    }
}
