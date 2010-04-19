/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.torque.task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.apache.torque.engine.database.transform.DTDResolver;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.seasar.dbflute.exception.DfTableDuplicateException;
import org.seasar.dbflute.exception.DfTableNotFoundException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.factory.DfDbCommentExtractorFactory;
import org.seasar.dbflute.logic.factory.DfIdentityExtractorFactory;
import org.seasar.dbflute.logic.factory.DfSynonymExtractorFactory;
import org.seasar.dbflute.logic.jdbc.handler.DfAutoIncrementHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfForeignKeyHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfIndexHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfTableHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfUniqueKeyHandler;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.w3c.dom.Element;

/**
 * @author Modified by jflute
 */
public class TorqueJDBCTransformTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(TorqueJDBCTransformTask.class);

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                         Document Info
    //                                         -------------
    /** DOM document produced. */
    protected DocumentImpl _doc;

    /** The document root element. */
    protected Element _databaseNode;

    // -----------------------------------------------------
    //                                               Handler
    //                                               -------
    protected DfTableHandler _tableHandler = new DfTableHandler();
    protected DfColumnHandler _columnHandler = new DfColumnHandler();
    protected DfUniqueKeyHandler _uniqueKeyHandler = new DfUniqueKeyHandler();
    protected DfIndexHandler _indexHandler = new DfIndexHandler();
    protected DfForeignKeyHandler _foreignKeyHandler = new DfForeignKeyHandler();
    protected DfAutoIncrementHandler _autoIncrementHandler = new DfAutoIncrementHandler();

    // -----------------------------------------------------
    //                                        Column Comment
    //                                        --------------
    protected Map<String, Map<String, UserColComments>> _columnCommentAllMap; // as temporary cache!

    // -----------------------------------------------------
    //                                      Direct Meta Data
    //                                      ----------------
    protected Map<String, String> _identityMap;
    protected Map<String, DfSynonymMetaInfo> _supplementarySynonymInfoMap;

    // -----------------------------------------------------
    //                                          Check Object
    //                                          ------------
    protected Map<String, DfTableMetaInfo> _generatedTableMap;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("");
        _log.info("...Starting to process JDBC to SchemaXML");

        final DocumentTypeImpl docType = new DocumentTypeImpl(null, "database", null, DTDResolver.WEB_SITE_DTD);
        _doc = new DocumentImpl(docType);
        _doc.appendChild(_doc.createComment(" Auto-generated by JDBC task! "));

        final String filePath = getBasicProperties().getProejctSchemaXMLFilePath();
        final String encoding = getBasicProperties().getProejctSchemaXMLEncoding();
        try {
            initializeIdentityMapIfNeeds();
            generateXML();

            _log.info("$ ");
            _log.info("$ ");
            _log.info("$ /* * * * * * * * * * * * * * * * * * * * * * * *");
            _log.info("$ ...Serializing XML: " + filePath + "(" + encoding + ")");

            final XMLSerializer xmlSerializer;
            {
                final FileOutputStream fis = new FileOutputStream(filePath);
                final OutputStreamWriter writer = new OutputStreamWriter(fis, encoding);
                final OutputFormat outputFormar = new OutputFormat(Method.XML, encoding, true);
                xmlSerializer = new XMLSerializer(writer, outputFormar);
            }
            xmlSerializer.serialize(_doc);

            _log.info("$ * * * * * * * * * */");
            _log.info("$ ");
        } catch (UnsupportedEncodingException e) {
            String msg = "Unsupported encoding: " + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "Not found file: " + filePath;
            throw new IllegalStateException(msg, e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        _log.info("------------------------------------------------------- [Torque - JDBCToXMLSchema] Finish!");
    }

    /**
     * Generates an XML database schema from JDBC meta data.
     * @throws Exception a generic exception.
     */
    protected void generateXML() throws Exception {
        _log.info("...Instantiate DB-driver");
        Class.forName(_driver);

        _log.info("...Getting DB-connection");
        final Connection conn = getDataSource().getConnection();

        _log.info("...Getting DB-meta-data");
        final DatabaseMetaData metaData = conn.getMetaData();

        _log.info("$ /**************************************************************************");
        _log.info("$ ");
        _log.info("$ dbMetaData.toString(): " + metaData.toString());
        _log.info("$ dbMetaData.getMaxRowSize(): " + metaData.getMaxRowSize());
        _log.info("$ ");
        final List<DfTableMetaInfo> tableList = getTableNames(metaData);
        _log.info("$ Table Count: " + tableList.size());
        _log.info("$ *************************************/");
        _log.info("$ ");

        // initialize the map of generated tables
        // this is used by synonym handling and foreign key handling
        // so this process should be before thier processes
        _generatedTableMap = StringKeyMap.createAsCaseInsensitive();
        for (DfTableMetaInfo info : tableList) {
            _generatedTableMap.put(info.getTableName(), info);
        }

        // Load synonym information for merging additional meta data if it needs.
        loadSupplementarySynonymInfoIfNeeds();

        // This should be after loading synonyms so it is executed at this timing!
        // The property 'outOfGenerateTarget' is set here
        processSynonymTable(tableList);

        // The handler of foreign keys for generating.
        // It needs to check whether a reference table is generate-target or not.
        _foreignKeyHandler.exceptForeignTableNotGenerated(_generatedTableMap);

        // Create database node. (The beginning of schema XML!)
        _databaseNode = _doc.createElement("database");
        _databaseNode.setAttribute("name", _mainSchema.getPureSchema()); // as main schema

        _log.info("$ /= = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMetaInfo tableMataInfo = tableList.get(i);
            if (tableMataInfo.isOutOfGenerateTarget()) {
                // for example, sequence synonym and so on...
                _log.info("$ " + tableMataInfo.buildTableDisplayName() + " is out of generate target!");
                continue;
            }
            _log.info("$ " + tableMataInfo);

            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", tableMataInfo.getTableName());
            tableElement.setAttribute("type", tableMataInfo.getTableType());
            final UnifiedSchema unifiedSchema = tableMataInfo.getUnifiedSchema();
            if (unifiedSchema.hasSchema()) {
                tableElement.setAttribute("schema", unifiedSchema.getIdentifiedSchema());
            }
            final String tableComment = tableMataInfo.getTableComment();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(tableComment)) {
                tableElement.setAttribute("comment", tableComment);
            }
            final DfPrimaryKeyMetaInfo pkInfo = getPrimaryColumnMetaInfo(metaData, tableMataInfo);
            final List<DfColumnMetaInfo> columns = getColumns(metaData, tableMataInfo);
            for (int j = 0; j < columns.size(); j++) {
                final DfColumnMetaInfo columnMetaInfo = columns.get(j);
                final String columnName = columnMetaInfo.getColumnName();

                final Element columnElement = _doc.createElement("column");
                columnElement.setAttribute("name", columnName);

                setupColumnType(columnMetaInfo, columnElement);
                setupColumnDbType(columnMetaInfo, columnElement);
                setupColumnJavaType(columnMetaInfo, columnElement);
                setupColumnSize(columnMetaInfo, columnElement);

                if (columnMetaInfo.isRequired()) {
                    columnElement.setAttribute("required", "true");
                }
                if (pkInfo.containsColumn(columnName)) {
                    columnElement.setAttribute("primaryKey", "true");
                    final String pkName = pkInfo.getPrimaryKeyName(columnName);
                    if (pkName != null && pkName.trim().length() > 0) {
                        columnElement.setAttribute("pkName", pkInfo.getPrimaryKeyName(columnName));
                    }
                }

                final String columnComment = columnMetaInfo.getColumnComment();
                if (columnComment != null) {
                    columnElement.setAttribute("comment", columnComment);
                }

                String defaultValue = columnMetaInfo.getDefaultValue();
                if (defaultValue != null) {
                    // trim out parens & quotes out of default value.
                    // makes sense for MSSQL. not sure about others.
                    if (defaultValue.startsWith("(") && defaultValue.endsWith(")")) {
                        defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                    }

                    if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                        defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                    }

                    columnElement.setAttribute("default", defaultValue);
                }

                if (pkInfo.containsColumn(columnName)) {
                    if (isAutoIncrementColumn(conn, tableMataInfo, columnName)) {
                        columnElement.setAttribute("autoIncrement", "true");
                    }
                }

                tableElement.appendChild(columnElement);
            }

            // /= = = = = = = = = = = = = =
            // Foreign keys for this table.
            // = = = = = = = = = =/
            final Map<String, DfForeignKeyMetaInfo> foreignKeyMap = getForeignKeys(metaData, tableMataInfo);
            final Set<String> foreignKeyKeySet = foreignKeyMap.keySet();
            for (String foreignKeyName : foreignKeyKeySet) {
                final DfForeignKeyMetaInfo foreignKeyMetaInfo = foreignKeyMap.get(foreignKeyName);
                final Element foreignKeyElement = _doc.createElement("foreign-key");
                foreignKeyElement.setAttribute("foreignTable", foreignKeyMetaInfo.getForeignTableName());
                foreignKeyElement.setAttribute("name", foreignKeyMetaInfo.getForeignKeyName());
                final Map<String, String> columnNameMap = foreignKeyMetaInfo.getColumnNameMap();
                final Set<String> columnNameKeySet = columnNameMap.keySet();
                for (String localColumnName : columnNameKeySet) {
                    final String foreignColumnName = columnNameMap.get(localColumnName);
                    final Element referenceElement = _doc.createElement("reference");
                    referenceElement.setAttribute("local", localColumnName);
                    referenceElement.setAttribute("foreign", foreignColumnName);
                    foreignKeyElement.appendChild(referenceElement);
                }
                tableElement.appendChild(foreignKeyElement);
            }

            // /= = = = = = = = = = = = = = = =
            // Unique keys for this table.
            // = = = = = = = = = =/
            Map<String, Map<Integer, String>> uniqueMapForGettingIndex = null;
            {
                Map<String, Map<Integer, String>> uniqueMap = null;
                try {
                    uniqueMap = getUniqueKeyMap(metaData, tableMataInfo);
                } catch (SQLException e) {
                    _log.warn("Failed to get unique column information! But continue...", e);
                } finally {
                    if (uniqueMap == null) {
                        uniqueMap = DfCollectionUtil.newLinkedHashMap();
                    }
                }
                uniqueMapForGettingIndex = uniqueMap;
                final java.util.Set<String> uniqueKeySet = uniqueMap.keySet();
                for (final String uniqueIndexName : uniqueKeySet) {
                    final Map<Integer, String> uniqueElementMap = uniqueMap.get(uniqueIndexName);
                    if (uniqueElementMap.isEmpty()) {
                        String msg = "The uniqueKey has no elements: " + uniqueIndexName + " : " + uniqueMap;
                        throw new IllegalStateException(msg);
                    }
                    final Element uniqueKeyElement = _doc.createElement("unique");
                    uniqueKeyElement.setAttribute("name", uniqueIndexName);
                    final Set<Integer> uniqueElementKeySet = uniqueElementMap.keySet();
                    for (final Integer ordinalPosition : uniqueElementKeySet) {
                        final String columnName = uniqueElementMap.get(ordinalPosition);
                        final Element uniqueColumnElement = _doc.createElement("unique-column");
                        uniqueColumnElement.setAttribute("name", columnName);
                        uniqueColumnElement.setAttribute("position", ordinalPosition.toString());
                        uniqueKeyElement.appendChild(uniqueColumnElement);
                    }
                    tableElement.appendChild(uniqueKeyElement);
                }
            }

            // /= = = = = = = = = = = =
            // Indexes for this table.
            // = = = = = = = = = =/
            {
                Map<String, Map<Integer, String>> indexMap = null;
                try {
                    indexMap = getIndexMap(metaData, tableMataInfo, uniqueMapForGettingIndex);
                } catch (SQLException e) {
                    _log.warn("Failed to get unique column information! But continue...", e);
                } finally {
                    if (indexMap == null) {
                        indexMap = DfCollectionUtil.newLinkedHashMap();
                    }
                }
                final java.util.Set<String> indexKeySet = indexMap.keySet();
                for (final String indexName : indexKeySet) {
                    final Map<Integer, String> indexElementMap = indexMap.get(indexName);
                    if (indexElementMap.isEmpty()) {
                        String msg = "The index has no elements: " + indexName + " : " + indexMap;
                        throw new IllegalStateException(msg);
                    }
                    final Element uniqueKeyElement = _doc.createElement("index");
                    uniqueKeyElement.setAttribute("name", indexName);
                    final Set<Integer> uniqueElementKeySet = indexElementMap.keySet();
                    for (final Integer ordinalPosition : uniqueElementKeySet) {
                        final String columnName = indexElementMap.get(ordinalPosition);
                        final Element uniqueColumnElement = _doc.createElement("index-column");
                        uniqueColumnElement.setAttribute("name", columnName);
                        uniqueColumnElement.setAttribute("position", ordinalPosition.toString());
                        uniqueKeyElement.appendChild(uniqueColumnElement);
                    }
                    tableElement.appendChild(uniqueKeyElement);
                }
            }

            _databaseNode.appendChild(tableElement);
        } // End of Table Loop
        _log.info("$ = = = = = = = = = =/");

        final boolean exists = setupAddtionalTableIfNeeds(); // since 0.8.0
        if (tableList.isEmpty() && !exists) {
            throwTableNotFoundException();
        }
        _doc.appendChild(_databaseNode);
    }

    protected void throwTableNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "A table was NOT FOUND in the schema!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the database connection settings." + ln();
        msg = msg + "If you've not created the schema yet, please create it." + ln();
        msg = msg + "You can create easily by using replace-schema." + ln();
        msg = msg + "Set up ./playsql/replace-schema.sql and execute ReplaceSchema task." + ln();
        msg = msg + ln();
        msg = msg + "[Connection Settings]" + ln();
        msg = msg + " driver = " + _driver + ln();
        msg = msg + " url    = " + _url + ln();
        msg = msg + " schema = " + _mainSchema + ln();
        msg = msg + " user   = " + _userId + ln();
        msg = msg + "* * * * * * * * * */";
        throw new DfTableNotFoundException(msg);
    }

    protected void setupColumnType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("type", getColumnJdbcType(columnMetaInfo));
    }

    protected String getColumnJdbcType(final DfColumnMetaInfo columnMetaInfo) {
        return _columnHandler.getColumnJdbcType(columnMetaInfo);
    }

    protected void setupColumnJavaType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final String jdbcType = getColumnJdbcType(columnMetaInfo);
        final int columnSize = columnMetaInfo.getColumnSize();
        final int decimalDigits = columnMetaInfo.getDecimalDigits();
        final String javaNative = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize > 0 ? columnSize : null,
                decimalDigits > 0 ? decimalDigits : null);
        columnElement.setAttribute("javaType", javaNative);
    }

    protected void setupColumnDbType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("dbType", columnMetaInfo.getDbTypeName());
    }

    protected void setupColumnSize(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final int columnSize = columnMetaInfo.getColumnSize();
        final int decimalDigits = columnMetaInfo.getDecimalDigits();
        if (DfColumnHandler.isColumnSizeValid(columnSize)) {
            if (DfColumnHandler.isDecimalDigitsValid(decimalDigits)) {
                columnElement.setAttribute("size", columnSize + ", " + decimalDigits);
            } else {
                columnElement.setAttribute("size", String.valueOf(columnSize));
            }
        }
    }

    // ===================================================================================
    //                                                                   Meta Data Handler
    //                                                                   =================
    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    /**
     * Get all the table names in the current database that are not system tables.
     * @param dbMeta The meta data of a database. (NotNull)
     * @return The list of all the tables in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableNames(DatabaseMetaData dbMeta) throws SQLException {
        final List<DfTableMetaInfo> tableList = _tableHandler.getTableList(dbMeta, _mainSchema);
        helpTableComments(tableList, _mainSchema);
        resolveAdditionalSchema(dbMeta, tableList);
        assertDuplicateTable(tableList);
        return tableList;
    }

    protected void assertDuplicateTable(List<DfTableMetaInfo> tableList) {
        final Set<String> tableNameSet = StringSet.createAsCaseInsensitive();
        final Set<String> duplicateTableSet = StringSet.createAsCaseInsensitive();
        for (DfTableMetaInfo info : tableList) {
            final String tableName = info.getTableName();
            if (tableNameSet.contains(tableName)) {
                duplicateTableSet.add(tableName);
            } else {
                tableNameSet.add(tableName);
            }
        }
        // obviously unsupported
        if (!duplicateTableSet.isEmpty()) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
            msg = msg + "The same-name table between different schema is unsupported!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Use view or synonym (or alias) that refers to the table." + ln();
            msg = msg + ln();
            msg = msg + "[Duplicate Table]" + ln() + duplicateTableSet + ln();
            msg = msg + "* * * * * * * * * */";
            throw new DfTableDuplicateException(msg);
        }
    }

    protected void resolveAdditionalSchema(DatabaseMetaData dbMeta, List<DfTableMetaInfo> tableList)
            throws SQLException {
        final List<UnifiedSchema> schemaList = getDatabaseProperties().getAdditionalSchemaList();
        for (UnifiedSchema additionalSchema : schemaList) {
            final List<DfTableMetaInfo> additionalTableList = _tableHandler.getTableList(dbMeta, additionalSchema);
            helpTableComments(additionalTableList, additionalSchema);
            tableList.addAll(additionalTableList);
        }
    }

    protected void helpTableComments(List<DfTableMetaInfo> tableList, UnifiedSchema unifiedSchema) {
        final DfDbCommentExtractor extractor = createDbCommentExtractor(unifiedSchema);
        if (extractor != null) {
            final Set<String> tableSet = new HashSet<String>();
            for (DfTableMetaInfo table : tableList) {
                tableSet.add(table.getTableName());
            }
            try {
                final Map<String, UserTabComments> tableCommentMap = extractor.extractTableComment(tableSet);
                for (DfTableMetaInfo table : tableList) {
                    table.acceptTableComment(tableCommentMap);

                    // *Synonym Processing is after loading synonyms.
                }
            } catch (RuntimeException ignored) {
                _log.info("Failed to extract table comments: extractor=" + extractor, ignored);
            }
            try {
                if (_columnCommentAllMap == null) {
                    _columnCommentAllMap = extractor.extractColumnComment(tableSet);
                } else {
                    _columnCommentAllMap.putAll(extractor.extractColumnComment(tableSet)); // Merge
                }
            } catch (RuntimeException ignored) {
                _log.info("Failed to extract column comments: extractor=" + extractor, ignored);
            }
        }
    }

    // /= = = = = = = = = = = = = = = = = = = = = = = =
    // These should be executed after loading synonyms
    // = = = = = = = = = =/

    /**
     * Process helper execution about synonym table. <br />
     * This should be executed after loading synonyms!
     * @param tableList The list of meta information of table. (NotNull)
     */
    protected void processSynonymTable(List<DfTableMetaInfo> tableList) {
        judgeOutOfTargetSynonym(tableList);
        helpSynonymTableComments(tableList);
    }

    protected void judgeOutOfTargetSynonym(List<DfTableMetaInfo> tableList) {
        for (DfTableMetaInfo table : tableList) {
            if (canHandleSynonym(table)) {
                final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
                if (synonym != null && !synonym.isSelectable()) {
                    table.setOutOfGenerateTarget(true);
                }
            }
        }
    }

    protected void helpSynonymTableComments(List<DfTableMetaInfo> tableList) {
        for (DfTableMetaInfo table : tableList) {
            if (canHandleSynonym(table) && !table.hasTableComment()) {
                final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
                if (synonym != null && synonym.hasTableComment()) {
                    table.setTableComment(synonym.getTableComment());
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /**
     * Retrieves all the column names and types for a given table from
     * JDBC meta data.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     * @param dbMeta The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, DfTableMetaInfo table) throws SQLException {
        List<DfColumnMetaInfo> columnList = _columnHandler.getColumnList(dbMeta, table);
        if (canHandleSynonym(table) && columnList.isEmpty()) {
            DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
            if (synonym != null && synonym.isDBLink()) {
                columnList = synonym.getColumnMetaInfoList4DBLink();
            }
        }

        helpColumnComments(table, columnList);
        return columnList;
    }

    protected void helpColumnComments(DfTableMetaInfo table, List<DfColumnMetaInfo> columnList) {
        if (_columnCommentAllMap != null) {
            final String tableName = table.getTableName();
            final Map<String, UserColComments> columnCommentMap = _columnCommentAllMap.get(tableName);
            for (DfColumnMetaInfo column : columnList) {
                column.acceptColumnComment(columnCommentMap);
            }
        }
        helpSynonymColumnComments(table, columnList);
    }

    protected void helpSynonymColumnComments(DfTableMetaInfo table, List<DfColumnMetaInfo> columnList) {
        for (DfColumnMetaInfo column : columnList) {
            if (canHandleSynonym(table) && !column.hasColumnComment()) {
                DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
                if (synonym != null && synonym.hasColumnCommentMap()) {
                    UserColComments userColComments = synonym.getColumnCommentMap().get(column.getColumnName());
                    if (userColComments != null && userColComments.hasComments()) {
                        column.setColumnComment(userColComments.getComments());
                    }
                }
            }
        }
    }

    // -----------------------------------------------------
    //                                           Primary Key
    //                                           -----------
    /**
     * Get the meta information of primary key.
     * @param metaData The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return The meta information of primary key. (NotNull)
     * @throws SQLException
     */
    protected DfPrimaryKeyMetaInfo getPrimaryColumnMetaInfo(DatabaseMetaData metaData, DfTableMetaInfo table)
            throws SQLException {
        final DfPrimaryKeyMetaInfo pkInfo = _uniqueKeyHandler.getPrimaryKey(metaData, table);
        final List<String> pkList = pkInfo.getPrimaryKeyList();
        if (!canHandleSynonym(table) || !pkList.isEmpty()) {
            return pkInfo;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        if (synonym != null) {
            return synonym.getPrimaryKey();
        } else {
            return pkInfo;
        }
    }

    // -----------------------------------------------------
    //                                            Unique Key
    //                                            ----------
    /**
     * Get unique column name list.
     * @param metaData The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return The list of unique columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData metaData, DfTableMetaInfo table)
            throws SQLException {
        final Map<String, Map<Integer, String>> uniqueKeyMap = _uniqueKeyHandler.getUniqueKeyMap(metaData, table);
        if (!canHandleSynonym(table) || !uniqueKeyMap.isEmpty()) {
            return uniqueKeyMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getUniqueKeyMap() : uniqueKeyMap;
    }

    // -----------------------------------------------------
    //                                        Auto Increment
    //                                        --------------
    /**
     * Get auto-increment column name.
     * @param tableMetaInfo The meta information of table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @param conn Connection.
     * @return Auto-increment column name. (Nullable)
     * @throws SQLException
     */
    protected boolean isAutoIncrementColumn(Connection conn, DfTableMetaInfo tableMetaInfo, String primaryKeyColumnName)
            throws SQLException {
        if (_autoIncrementHandler.isAutoIncrementColumn(conn, tableMetaInfo, primaryKeyColumnName)) {
            return true;
        }
        if (canHandleSynonym(tableMetaInfo)) {
            final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableMetaInfo);
            if (synonym != null && synonym.isAutoIncrement()) {
                return true;
            }
        }
        if (_identityMap == null) {
            return false;
        }
        String columnName = _identityMap.get(tableMetaInfo.getTableName());
        return primaryKeyColumnName.equals(columnName);
    }

    protected void initializeIdentityMapIfNeeds() {
        DfIdentityExtractor extractor = createIdentityExtractor();
        if (extractor == null) {
            return;
        }
        try {
            _log.info("...Initializing identity map");
            _identityMap = extractor.extractIdentityMap();
            _log.info("  -> size=" + _identityMap.size());
        } catch (Exception ignored) {
            _log.info("DfIdentityExtractor.extractIdentityMap() threw the exception!", ignored);
        }
    }

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
    /**
     * Retrieves a list of foreign key columns for a given table.
     * @param metaData The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    protected Map<String, DfForeignKeyMetaInfo> getForeignKeys(DatabaseMetaData metaData, DfTableMetaInfo table)
            throws SQLException {
        final Map<String, DfForeignKeyMetaInfo> foreignKeyMap = _foreignKeyHandler.getForeignKeyMap(metaData, table);
        if (!canHandleSynonym(table) || !foreignKeyMap.isEmpty()) {
            return foreignKeyMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getForeignKeyMap() : foreignKeyMap;
    }

    // -----------------------------------------------------
    //                                                 Index
    //                                                 -----
    /**
     * Get index column name list.
     * @param metaData The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @param uniqueKeyMap The map of unique key. (NotNull)
     * @return The list of index columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, DfTableMetaInfo table,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final Map<String, Map<Integer, String>> indexMap = _indexHandler.getIndexMap(metaData, table, uniqueKeyMap);
        if (!canHandleSynonym(table) || !indexMap.isEmpty()) {
            return indexMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getIndexMap() : indexMap;
    }

    // -----------------------------------------------------
    //                                               Synonym
    //                                               -------
    protected void loadSupplementarySynonymInfoIfNeeds() { // is only for main schema
        final DfSynonymExtractor extractor = createSynonymExtractor();
        if (extractor == null) {
            return;
        }
        try {
            _log.info("...Loading supplementary synonym informations");
            _supplementarySynonymInfoMap = extractor.extractSynonymMap();
            final StringBuilder sb = new StringBuilder();
            sb.append("Finished loading synonyms:").append(ln()).append("[Supplementary Synonyms]");
            final Set<Entry<String, DfSynonymMetaInfo>> entrySet = _supplementarySynonymInfoMap.entrySet();
            for (Entry<String, DfSynonymMetaInfo> entry : entrySet) {
                sb.append(ln()).append(" ").append(entry.getValue().toString());
            }
            _log.info(sb.toString());
        } catch (RuntimeException ignored) {
            _log.info("DfSynonymExtractor.extractSynonymMap() threw the exception!", ignored);
        }
    }

    protected boolean canHandleSynonym(DfTableMetaInfo table) {
        return _supplementarySynonymInfoMap != null && table.canHandleSynonym();
    }

    protected DfSynonymMetaInfo getSynonymMetaInfo(DfTableMetaInfo table) {
        if (!canHandleSynonym(table)) {
            String msg = "The table meta information should be for synonym: " + table;
            throw new IllegalStateException(msg);
        }
        String key = table.buildTableFullQualifiedName();
        DfSynonymMetaInfo info = _supplementarySynonymInfoMap.get(key);
        if (info != null) {
            return info;
        }
        key = table.buildSchemaQualifiedName();
        info = _supplementarySynonymInfoMap.get(key);
        if (info != null) {
            return info;
        }
        return null;
    }

    // ===================================================================================
    //                                                                    Additional Table
    //                                                                    ================
    protected boolean setupAddtionalTableIfNeeds() { // since 0.8.0
        boolean exists = false;
        final String tableType = "TABLE";
        final DfAdditionalTableProperties prop = getProperties().getAdditionalTableProperties();
        final Map<String, Object> tableMap = prop.getAdditionalTableMap();
        final Set<String> tableNameKey = tableMap.keySet();
        for (String tableName : tableNameKey) {
            _log.info("...Processing additional table: " + tableName + "(" + tableType + ")");
            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", tableName);
            tableElement.setAttribute("type", tableType);

            final Map<String, Map<String, String>> columnMap = prop.findColumnMap(tableName);
            final String tableComment = prop.findTableComment(tableName);
            if (tableComment != null && tableComment.trim().length() > 0) {
                tableElement.setAttribute("comment", tableComment);
            }
            final Set<String> columnNameKey = columnMap.keySet();
            for (String columnName : columnNameKey) {
                final Element columnElement = _doc.createElement("column");
                columnElement.setAttribute("name", columnName);

                final String columnType = prop.findColumnType(tableName, columnName);
                final String columnDbType = prop.findColumnDbType(tableName, columnName);
                final String columnSize = prop.findColumnSize(tableName, columnName);
                final boolean required = prop.isColumnRequired(tableName, columnName);
                final boolean primaryKey = prop.isColumnPrimaryKey(tableName, columnName);
                final String pkName = prop.findColumnPKName(tableName, columnName);
                final boolean autoIncrement = prop.isColumnAutoIncrement(tableName, columnName);
                final String columnDefault = prop.findColumnDefault(tableName, columnName);
                final String columnComment = prop.findColumnComment(tableName, columnName);
                setupAdditionalTableColumnAttribute(columnElement, "type", columnType);
                setupAdditionalTableColumnAttribute(columnElement, "dbType", columnDbType);
                setupAdditionalTableColumnAttribute(columnElement, "size", columnSize);
                setupAdditionalTableColumnAttribute(columnElement, "required", String.valueOf(required));
                setupAdditionalTableColumnAttribute(columnElement, "primaryKey", String.valueOf(primaryKey));
                setupAdditionalTableColumnAttribute(columnElement, "pkName", pkName);
                setupAdditionalTableColumnAttribute(columnElement, "autoIncrement", String.valueOf(autoIncrement));
                setupAdditionalTableColumnAttribute(columnElement, "default", columnDefault);
                setupAdditionalTableColumnAttribute(columnElement, "comment", columnComment);
                tableElement.appendChild(columnElement);
            }
            exists = true;
            _databaseNode.appendChild(tableElement);
        }
        return exists;
    }

    protected void setupAdditionalTableColumnAttribute(Element columnElement, String key, String value) {
        if (value != null && value.trim().length() > 0) {
            columnElement.setAttribute(key, value);
        }
    }

    // ===================================================================================
    //                                                                           Extractor
    //                                                                           =========
    protected DfDbCommentExtractor createDbCommentExtractor(UnifiedSchema unifiedSchema) {
        final DfDbCommentExtractorFactory factory = createDbCommentExtractorFactory(unifiedSchema);
        return factory.createDbCommentExtractor();
    }

    protected DfDbCommentExtractorFactory createDbCommentExtractorFactory(UnifiedSchema unifiedSchema) {
        return new DfDbCommentExtractorFactory(getBasicProperties(), getDataSource(), unifiedSchema);
    }

    protected DfIdentityExtractor createIdentityExtractor() {
        final DfIdentityExtractorFactory factory = createIdentityExtractorFactory();
        return factory.createIdentityExtractor();
    }

    protected DfIdentityExtractorFactory createIdentityExtractorFactory() {
        return new DfIdentityExtractorFactory(getBasicProperties(), getDataSource());
    }

    protected DfSynonymExtractor createSynonymExtractor() {
        final DfSynonymExtractorFactory factory = createSynonymExtractorFactory();
        return factory.createSynonymExtractor();
    }

    protected DfSynonymExtractorFactory createSynonymExtractorFactory() {
        // The synonym extractor needs the map of generated tables for reference table check.
        return new DfSynonymExtractorFactory(getDataSource(), getBasicProperties(), getDatabaseProperties(),
                _generatedTableMap);
    }
}