/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.synonym;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.facade.DfJdbcFacade;
import org.seasar.dbflute.logic.jdbc.handler.DfAutoIncrementHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfForeignKeyHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfIndexHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfTableHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.DfAbstractMetaDataExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorOracle extends DfAbstractMetaDataExtractor implements DfSynonymExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSynonymExtractorOracle.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected List<UnifiedSchema> _unifiedSchemaList;
    protected Map<String, DfTableMetaInfo> _generatedTableMap;

    // -----------------------------------------------------
    //                                     Meta Data Handler
    //                                     -----------------
    protected DfTableHandler _tableHandler = new DfTableHandler();
    protected DfUniqueKeyHandler _uniqueKeyHandler = new DfUniqueKeyHandler();
    protected DfAutoIncrementHandler _autoIncrementHandler = new DfAutoIncrementHandler();
    protected DfForeignKeyHandler _foreignKeyHandler = new DfForeignKeyHandler() {
        @Override
        public boolean isTableExcept(UnifiedSchema unifiedSchema, String tableName) {
            // All foreign tables are target if the foreign table is except.
            // Because the filtering is executed when translating foreign keys.
            return false;
        }
    };
    protected DfIndexHandler _indexHandler = new DfIndexHandler();

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    public Map<String, DfSynonymMetaInfo> extractSynonymMap() {
        final Map<String, DfSynonymMetaInfo> synonymMap = StringKeyMap.createAsFlexibleOrdered();
        final String sql = buildSynonymSelect();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = _dataSource.getConnection();
            statement = conn.createStatement();
            _log.info(sql);
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                final UnifiedSchema synonymOwner = createAsDynamicSchema(null, rs.getString("OWNER"));
                final String synonymName = rs.getString("SYNONYM_NAME");
                final UnifiedSchema tableOwner = createAsDynamicSchema(null, rs.getString("TABLE_OWNER"));
                final String tableName = rs.getString("TABLE_NAME");
                final String dbLinkName = rs.getString("DB_LINK");

                if (_tableHandler.isTableExcept(synonymOwner, synonymName)) {
                    // because it is not necessary to handle excepted tables 
                    continue;
                }

                final DfSynonymMetaInfo info = new DfSynonymMetaInfo();

                // Basic
                info.setSynonymOwner(synonymOwner);
                info.setSynonymName(synonymName);
                info.setTableOwner(tableOwner);
                info.setTableName(tableName);
                info.setDBLinkName(dbLinkName);

                // Select-able?
                judgeSynonymSelectable(info);

                if (dbLinkName != null && dbLinkName.trim().length() > 0) {
                    // = = = = = = = = = = = = 
                    // It's a DB Link Synonym!
                    // = = = = = = = = = = = = 
                    try {
                        final String synonymKey = buildSynonymMapKey(synonymOwner, synonymName);
                        synonymMap.put(synonymKey, setupDBLinkSynonym(conn, info));
                    } catch (Exception continued) {
                        _log.info("Failed to get meta data of " + synonymName + ": " + continued.getMessage());
                    }
                    continue;
                }
                if (!tableOwner.hasSchema()) {
                    continue; // basically no way because it may be for DB Link Synonym
                }

                // = = = = = = = = = = = = 
                // It's a normal Synonym!
                // = = = = = = = = = = = = 
                // PK, ID, UQ, FK, Index
                try {
                    setupBasicConstraintInfo(info, tableOwner, tableName, conn);
                } catch (Exception continued) {
                    _log.info("Failed to get meta data of " + synonymName + ": " + continued.getMessage());
                    continue;
                }
                final String synonymKey = buildSynonymMapKey(synonymOwner, synonymName);
                synonymMap.put(synonymKey, info);
            }
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
        translateFKTable(synonymMap); // It translates foreign key meta informations. 
        setupTableColumnComment(synonymMap);
        return synonymMap;
    }

    protected String buildSynonymSelect() {
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        for (UnifiedSchema unifiedSchema : _unifiedSchemaList) {
            if (count > 0) {
                sb.append(", ");
            }
            sb.append("'").append(unifiedSchema.getPureSchema()).append("'");
            ++count;
        }
        final String sql = "select * from ALL_SYNONYMS where OWNER in (" + sb.toString() + ")";
        return sql;
    }

    protected String buildSynonymMapKey(UnifiedSchema synonymOwner, String synonymName) {
        return synonymOwner.buildSchemaQualifiedName(synonymName);
    }

    protected void judgeSynonymSelectable(DfSynonymMetaInfo info) {
        final DfJdbcFacade facade = new DfJdbcFacade(_dataSource);
        final String synonymSqlName = info.buildSynonymSqlName();
        final String sql = "select * from " + synonymSqlName + " where 0 = 1";
        try {
            final List<String> columnList = new ArrayList<String>();
            columnList.add("dummy");
            facade.selectStringList(sql, columnList);
            info.setSelectable(true);
        } catch (RuntimeException ignored) {
            info.setSelectable(false);
        }
    }

    protected void setupBasicConstraintInfo(DfSynonymMetaInfo info, UnifiedSchema tableOwner, String tableName,
            Connection conn) throws SQLException {
        final DatabaseMetaData md = conn.getMetaData();
        final DfPrimaryKeyMetaInfo pkInfo = getPKList(md, tableOwner, tableName);
        info.setPrimaryKey(pkInfo);
        final List<String> pkList = pkInfo.getPrimaryKeyList();
        if (info.isSelectable()) { // because it needs a select statement
            for (String primaryKeyName : pkList) {
                final boolean autoIncrement = isAutoIncrement(conn, tableOwner, tableName, primaryKeyName);
                if (autoIncrement) {
                    info.setAutoIncrement(autoIncrement);
                    break;
                }
            }
        }
        {
            final Map<String, Map<Integer, String>> uqMap = getUQMap(md, tableOwner, tableName, pkList);
            info.setUniqueKeyMap(uqMap);
        }
        {
            final Map<String, DfForeignKeyMetaInfo> fkMap = getFKMap(md, tableOwner, tableName);
            info.setForeignKeyMap(fkMap); // It's tentative information at this timing!
        }
        {
            final Map<String, Map<Integer, String>> uqMap = info.getUniqueKeyMap();
            final Map<String, Map<Integer, String>> indexMap = getIndexMap(md, tableOwner, tableName, uqMap);
            info.setIndexMap(indexMap);
        }
    }

    protected DfSynonymMetaInfo setupDBLinkSynonym(Connection conn, DfSynonymMetaInfo info) throws SQLException {
        final UnifiedSchema synonymOwner = info.getSynonymOwner();
        final String synonymName = info.getSynonymName();
        final String tableName = info.getTableName();
        final String dbLinkName = info.getDBLinkName();
        final List<DfColumnMetaInfo> columnMetaInfoList = getDBLinkSynonymColumns(conn, synonymOwner, synonymName);
        info.setColumnMetaInfoList4DBLink(columnMetaInfoList);
        final DfPrimaryKeyMetaInfo pkInfo = getDBLinkSynonymPKInfo(conn, tableName, dbLinkName);
        info.setPrimaryKey(pkInfo);
        final Map<String, Map<Integer, String>> uniqueKeyMap = getDBLinkSynonymUQMap(conn, tableName, dbLinkName);
        info.setUniqueKeyMap(uniqueKeyMap);

        // It does not support Foreign Key of DBLink.
        info.setForeignKeyMap(new LinkedHashMap<String, DfForeignKeyMetaInfo>());

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
    protected DfPrimaryKeyMetaInfo getPKList(DatabaseMetaData metaData, UnifiedSchema unifiedSchema, String tableName) {
        try {
            return _uniqueKeyHandler.getPrimaryKey(metaData, unifiedSchema, tableName);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, Map<Integer, String>> getUQMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, List<String> primaryKeyNameList) {
        try {
            return _uniqueKeyHandler.getUniqueKeyMap(metaData, unifiedSchema, tableName, primaryKeyNameList);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, DfForeignKeyMetaInfo> getFKMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName) {
        try {
            return _foreignKeyHandler.getForeignKeyMap(metaData, unifiedSchema, tableName);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, UnifiedSchema unifiedSchema,
            String tableName, Map<String, Map<Integer, String>> uniqueKeyMap) {
        try {
            return _indexHandler.getIndexMap(metaData, unifiedSchema, tableName, uniqueKeyMap);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected boolean isAutoIncrement(Connection conn, UnifiedSchema tableOwner, String tableName,
            String primaryKeyColumnName) {
        return false; // because Oracle does not support identity
        //try {
        //    final DfTableMetaInfo tableMetaInfo = new DfTableMetaInfo();
        //    tableMetaInfo.setTableName(tableName);
        //    tableMetaInfo.setTableSchema(tableOwner);
        //    return _autoIncrementHandler.isAutoIncrementColumn(conn, tableMetaInfo, primaryKeyColumnName);
        //} catch (RuntimeException continued) { // because the priority is low and it needs select
        //    _log.info(continued.getMessage());
        //    return false;
        //}
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
            final Map<String, DfForeignKeyMetaInfo> fkMap = synonym.getForeignKeyMap();
            if (fkMap == null || fkMap.isEmpty()) {
                continue;
            }
            final Set<String> fkNameSet = fkMap.keySet();
            final Map<String, DfForeignKeyMetaInfo> additionalFKMap = DfCollectionUtil.newLinkedHashMap();
            final Map<String, String> removedFKMap = DfCollectionUtil.newLinkedHashMap();
            for (String fkName : fkNameSet) {
                final DfForeignKeyMetaInfo fk = fkMap.get(fkName);

                // at first translate a local table name
                fk.setLocalTableName(synonym.getSynonymName());

                final String orignalForeignTableName = fk.getForeignTableName();
                final List<String> foreignSynonymList = tableForeignSynonymListMap.get(orignalForeignTableName);
                if (foreignSynonymList == null || foreignSynonymList.isEmpty()) {
                    if (_tableHandler.isTableExcept(synonym.getTableOwner(), orignalForeignTableName)) {
                        removedFKMap.put(fkName, orignalForeignTableName);
                    } else if (!isForeignTableGenerated(orignalForeignTableName)) {
                        removedFKMap.put(fkName, orignalForeignTableName);
                    }
                    continue;
                }
                final String originalForeignKeyName = fk.getForeignKeyName();
                boolean firstDone = false;
                for (int i = 0; i < foreignSynonymList.size(); i++) {
                    final String newForeignKeyName = originalForeignKeyName + "_SYNONYM" + (i + 1);
                    final String newForeignTableName = foreignSynonymList.get(i);
                    if (!firstDone) {
                        // first (switching FK informations)
                        fk.setForeignKeyName(newForeignKeyName);
                        fk.setForeignTableName(newForeignTableName);
                        firstDone = true;
                        continue;
                    }

                    // second or more (creating new FK instance)
                    final DfForeignKeyMetaInfo additionalFK = new DfForeignKeyMetaInfo();
                    additionalFK.setForeignKeyName(newForeignKeyName);
                    additionalFK.setLocalTableName(fk.getLocalTableName());
                    additionalFK.setForeignTableName(newForeignTableName);
                    additionalFK.setColumnNameMap(fk.getColumnNameMap());
                    additionalFKMap.put(additionalFK.getForeignKeyName(), additionalFK);
                }
            }
            fkMap.putAll(additionalFKMap);
            if (!removedFKMap.isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                sb.append("...Excepting foreign keys from the synonym:").append(ln()).append("[Excepted Foreign Key]");
                final Set<String> removedFKKeySet = removedFKMap.keySet();
                for (String removedKey : removedFKKeySet) {
                    sb.append(ln()).append(" ").append(removedKey);
                    sb.append(" (").append(synonym.getSynonymName()).append(" to ");
                    sb.append(removedFKMap.get(removedKey)).append(")");
                    fkMap.remove(removedKey);
                }
                _log.info(sb.toString());
            }
        }
    }

    protected boolean isForeignTableGenerated(String foreignTableName) {
        if (_generatedTableMap == null || _generatedTableMap.isEmpty()) {
            // means no check of generation
            return true;
        }
        final DfTableMetaInfo info = _generatedTableMap.get(foreignTableName);
        if (info == null) {
            return false;
        }
        if (info.isOutOfGenerateTarget()) {
            return false;
        }
        return true;
    }

    /**
     * Set up table and column comment. <br />
     * This does not support DB link synonym.
     * @param synonymMap The map of synonym. (NotNull)
     */
    protected void setupTableColumnComment(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Map<String, Set<String>> ownerTabSetMap = createOwnerTableSetMap(synonymMap);
        final Map<String, Map<String, UserTabComments>> ownerTabCommentMap = DfCollectionUtil.newLinkedHashMap();
        final Map<String, Map<String, Map<String, UserColComments>>> ownerTabColCommentMap = DfCollectionUtil
                .newLinkedHashMap();
        final Set<String> ownerSet = ownerTabSetMap.keySet();
        for (String owner : ownerSet) {
            final Set<String> tableSet = ownerTabSetMap.get(owner);
            final DfDbCommentExtractorOracle extractor = createDbCommentExtractor(createAsDynamicSchema(null, owner));
            final Map<String, UserTabComments> tabCommentMap = extractor.extractTableComment(tableSet);
            final Map<String, Map<String, UserColComments>> tabColCommentMap = extractor.extractColumnComment(tableSet);
            ownerTabCommentMap.put(owner, tabCommentMap);
            ownerTabColCommentMap.put(owner, tabColCommentMap);
        }

        for (DfSynonymMetaInfo synonym : synonymMap.values()) {
            final UnifiedSchema owner = synonym.getTableOwner();
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

    protected DfDbCommentExtractorOracle createDbCommentExtractor(UnifiedSchema schema) {
        final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
        extractor.setDataSource(_dataSource);
        extractor.setUnifiedSchema(schema);
        return extractor;
    }

    protected Map<String, Set<String>> createOwnerTableSetMap(Map<String, DfSynonymMetaInfo> synonymMap) {
        final Map<String, Set<String>> ownerTabSetMap = new LinkedHashMap<String, Set<String>>();
        for (DfSynonymMetaInfo synonym : synonymMap.values()) {
            final UnifiedSchema owner = synonym.getTableOwner();
            if (synonym.isDBLink()) { // Synonym of DB Link is out of target!
                continue;
            }
            Set<String> tableSet = ownerTabSetMap.get(owner);
            if (tableSet == null) {
                tableSet = new LinkedHashSet<String>();
                ownerTabSetMap.put(owner.getPureSchema(), tableSet);
            }
            tableSet.add(synonym.getTableName());
        }
        return ownerTabSetMap;
    }

    // -----------------------------------------------------
    //                                   For DB Link Synonym
    //                                   -------------------
    protected List<DfColumnMetaInfo> getDBLinkSynonymColumns(Connection conn, UnifiedSchema synonymOwner,
            String synonymName) throws SQLException {
        final List<DfColumnMetaInfo> columnList = new ArrayList<DfColumnMetaInfo>();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            final String synonymSqlName = synonymOwner.buildSchemaQualifiedName(synonymName);
            final String sql = "select * from " + synonymSqlName + " where 0=1";
            rs = st.executeQuery(sql);
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
            if (st != null) {
                try {
                    st.close();
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

    protected DfPrimaryKeyMetaInfo getDBLinkSynonymPKInfo(Connection conn, String tableName, String dbLinkName)
            throws SQLException {
        final DfPrimaryKeyMetaInfo pkInfo = new DfPrimaryKeyMetaInfo();
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
                String pkName = rs.getString("CONSTRAINT_NAME");
                pkInfo.addPrimaryKey(columnName, pkName);
            }
            return pkInfo;
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

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setTargetSchemaList(List<UnifiedSchema> unifiedSchemaList) {
        this._unifiedSchemaList = unifiedSchemaList;
    }

    public void setGeneratedTableMap(Map<String, DfTableMetaInfo> generatedTableMap) {
        this._generatedTableMap = generatedTableMap;
    }
}
