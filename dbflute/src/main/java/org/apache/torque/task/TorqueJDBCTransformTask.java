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
import java.sql.Types;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.transform.DTDResolver;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.seasar.dbflute.exception.TableNotFoundException;
import org.seasar.dbflute.helper.collection.DfStringSet;
import org.seasar.dbflute.helper.jdbc.metadata.DfAutoIncrementHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfIndexHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfUniqueKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.helper.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.factory.DfDbCommentExtractorFactory;
import org.seasar.dbflute.logic.factory.DfIdentityExtractorFactory;
import org.seasar.dbflute.logic.factory.DfSynonymExtractorFactory;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.task.bs.DfAbstractTask;
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
    //                                         Database Info
    //                                         -------------
    /** Name of XML database schema produced. */
    protected String _xmlSchema;

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
    protected Map<String, DfSynonymMetaInfo> _synonymMap;

    // -----------------------------------------------------
    //                                          Check Object
    //                                          ------------
    protected DfStringSet _refTableCheckSet;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("------------------------------------------------------- [Torque - JDBCToXMLSchema] Start!");
        _log.info("Your DB settings are:");
        _log.info("  driver : " + _driver);
        _log.info("  URL    : " + _url);
        _log.info("  user   : " + _userId);
        _log.info("  schema : " + _schema);
        _log.info("  props  : " + _connectionProperties);

        final DocumentTypeImpl docType = new DocumentTypeImpl(null, "database", null, DTDResolver.WEB_SITE_DTD);
        _doc = new DocumentImpl(docType);
        _doc.appendChild(_doc.createComment(" Autogenerated by JDBCToXMLSchema! "));

        try {
            initializeIdentityMapIfNeeds();
            generateXML();

            // Get encoding from properties
            final String encoding = getBasicProperties().getProejctSchemaXMLEncoding();
            _log.info("$ ");
            _log.info("$ ");
            _log.info("$ /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.info("$ ...Serializing XML: " + _xmlSchema + "(" + encoding + ")");

            final XMLSerializer xmlSerializer;
            {
                final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(_xmlSchema), encoding);
                final OutputFormat outputFormar = new OutputFormat(Method.XML, encoding, true);
                xmlSerializer = new XMLSerializer(writer, outputFormar);
            }
            xmlSerializer.serialize(_doc);

            _log.info("$ * * * * * * * * */");
            _log.info("$ ");
        } catch (RuntimeException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        final DatabaseMetaData dbMetaData = conn.getMetaData();

        _log.info("$ /**************************************************************************");
        _log.info("$ ");
        _log.info("$ dbMetaData.toString(): " + dbMetaData.toString());
        _log.info("$ dbMetaData.getMaxRowSize(): " + dbMetaData.getMaxRowSize());
        _log.info("$ ");
        _log.info(getObjectTypeLogString());
        _log.info(getAdditionalSchemaLogString());
        _log.info("$ ");
        _log.info("$ ...Getting tables");
        final List<DfTableMetaInfo> tableList = getTableNames(dbMetaData);
        _log.info("$ Table Count: " + tableList.size());
        _log.info("$ *************************************/");
        _log.info("$ ");

        if (tableList.isEmpty()) {
            throwTableNotFoundException();
        }

        // Initialize the set collection for reference table check.
        // This should be executed before handling foreign keys.
        _refTableCheckSet = DfStringSet.createAsCaseInsensitive();
        for (DfTableMetaInfo tableMetaInfo : tableList) {
            _refTableCheckSet.add(tableMetaInfo.getTableName());
        }

        // The handler of foreign keys needs the set collection for reference table check.
        _foreignKeyHandler.setRefTableCheckSet(_refTableCheckSet);

        // Load synonym information for merging additional meta data if it needs.
        loadSynonymInfoIfNeeds();

        // This should be after loading synonyms so it is executed at this timing!
        helpSynonymTableComments(tableList);

        // Create database node. (The beginning of schema XML!)
        _databaseNode = _doc.createElement("database");
        _databaseNode.setAttribute("name", _schema);

        // * * * * * *
        // Table Loop
        // * * * * * *
        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMetaInfo tableMataInfo = tableList.get(i);
            _log.info("$ " + tableMataInfo);

            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", tableMataInfo.getTableName());
            tableElement.setAttribute("type", tableMataInfo.getTableType());
            if (tableMataInfo.getTableSchema() != null && tableMataInfo.getTableSchema().trim().length() != 0) {
                tableElement.setAttribute("schema", tableMataInfo.getTableSchema());
            }
            if (tableMataInfo.getTableComment() != null && tableMataInfo.getTableComment().trim().length() != 0) {
                tableElement.setAttribute("comment", tableMataInfo.getTableComment());
            }

            final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMetaData, tableMataInfo);

            final List<DfColumnMetaInfo> columns = getColumns(dbMetaData, tableMataInfo);
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

                if (primaryColumnNameList.contains(columnName)) {
                    columnElement.setAttribute("primaryKey", "true");
                }

                final String columnComment = columnMetaInfo.getColumnComment();
                if (columnComment != null) {
                    columnElement.setAttribute("comment", columnComment);
                }

                String defaultValue = columnMetaInfo.getDefaultValue();
                if (defaultValue != null) {
                    // trim out parens & quotes out of def value.
                    // makes sense for MSSQL. not sure about others.
                    if (defaultValue.startsWith("(") && defaultValue.endsWith(")")) {
                        defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                    }

                    if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                        defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                    }

                    columnElement.setAttribute("default", defaultValue);
                }

                if (primaryColumnNameList.contains(columnName)) {
                    if (isAutoIncrementColumn(conn, tableMataInfo, columnName)) {
                        columnElement.setAttribute("autoIncrement", "true");
                    }
                }

                tableElement.appendChild(columnElement);
            }

            // * * * * * * * * * * * * * * *
            // Foreign keys for this table.
            // * * * * * * * * * * * * * * *
            final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap = getForeignKeys(dbMetaData, tableMataInfo);
            final Set<String> foreignKeyMetaInfoKeySet = foreignKeyMetaInfoMap.keySet();
            for (String foreignKeyName : foreignKeyMetaInfoKeySet) {
                final DfForeignKeyMetaInfo foreignKeyMetaInfo = foreignKeyMetaInfoMap.get(foreignKeyName);
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

            // * * * * * * * * * * * * * * *
            // Unique keys for this table.
            // * * * * * * * * * * * * * * *
            Map<String, Map<Integer, String>> uniqueMapForGettingIndex = null;
            {
                Map<String, Map<Integer, String>> uniqueMap = null;
                try {
                    uniqueMap = getUniqueKeyMap(dbMetaData, tableMataInfo);
                } catch (SQLException e) {
                    _log.warn("Failed to get unique column information! But continue...", e);
                } finally {
                    if (uniqueMap == null) {
                        uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();
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

            // * * * * * * * * * * * * * * *
            // Indeces for this table.
            // * * * * * * * * * * * * * * *
            {
                Map<String, Map<Integer, String>> indexMap = null;
                try {
                    indexMap = getIndexMap(dbMetaData, tableMataInfo, uniqueMapForGettingIndex);
                } catch (SQLException e) {
                    _log.warn("Failed to get unique column information! But continue...", e);
                } finally {
                    if (indexMap == null) {
                        indexMap = new LinkedHashMap<String, Map<Integer, String>>();
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

        setupAddtionalTableIfNeeds(); // since 0.8.0
        _doc.appendChild(_databaseNode);
    }

    protected String getObjectTypeLogString() {
        final List<String> objectTypeTargetList = getDatabaseProperties().getObjectTypeTargetList();
        String typeString = "";
        int i = 0;
        for (String objectType : objectTypeTargetList) {
            if (i == 0) {
                typeString = objectType;
            } else {
                typeString = typeString + ", " + objectType;
            }
            ++i;
        }
        return "$ Object Types: {" + typeString + "}";
    }

    protected String getAdditionalSchemaLogString() {
        final List<String> additionalSchemaList = getDatabaseProperties().getAdditionalSchemaList();
        return "$ Additional Schemas: " + additionalSchemaList;
    }

    protected void throwTableNotFoundException() {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The tables was was Not Found in the schema!" + ln();
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
        msg = msg + " schema = " + _schema + ln();
        msg = msg + " user   = " + _userId + ln();
        msg = msg + "* * * * * * * * * */";
        throw new TableNotFoundException(msg);
    }

    protected void setupColumnType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("type", getColumnTorqueType(columnMetaInfo));
    }

    protected String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo) {
        final DfColumnHandler columnHandler = new DfColumnHandler();
        return columnHandler.getColumnTorqueType(columnMetaInfo);
    }

    protected void setupColumnJavaType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final String jdbcType = getColumnTorqueType(columnMetaInfo);
        final int columnSize = columnMetaInfo.getColumnSize();
        final int decimalDigits = columnMetaInfo.getDecimalDigits();
        final String javaNative = TypeMap.findJavaNativeString(jdbcType, columnSize > 0 ? columnSize : null,
                decimalDigits > 0 ? decimalDigits : null);
        columnElement.setAttribute("javaType", javaNative);
    }

    protected void setupColumnDbType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("dbType", columnMetaInfo.getDbTypeName());
    }

    protected void setupColumnSize(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final int jdbcType = columnMetaInfo.getJdbcType();
        final int columnSize = columnMetaInfo.getColumnSize();
        final int decimalDigits = columnMetaInfo.getDecimalDigits();
        if (columnSize > 0 && isColumnSizeValidSqlType(jdbcType)) {
            if (decimalDigits > 0) {
                columnElement.setAttribute("size", columnSize + ", " + decimalDigits);
            } else {
                columnElement.setAttribute("size", String.valueOf(columnSize));
            }
        }
    }

    protected boolean isColumnSizeValidSqlType(int sqlTypeCode) {
        return sqlTypeCode == Types.CHAR || sqlTypeCode == Types.VARCHAR || sqlTypeCode == Types.LONGVARCHAR
                || sqlTypeCode == Types.DECIMAL || sqlTypeCode == Types.NUMERIC;
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
        final List<DfTableMetaInfo> tableList = _tableHandler.getTableList(dbMeta, _schema);
        helpTableComments(tableList, _schema);
        resolveAdditionalSchema(dbMeta, tableList);
        return tableList;
    }

    protected void resolveAdditionalSchema(DatabaseMetaData dbMeta, List<DfTableMetaInfo> tableList)
            throws SQLException {
        final List<String> additionalSchemaList = getDatabaseProperties().getAdditionalSchemaList();
        for (String additionalSchema : additionalSchemaList) {
            final List<DfTableMetaInfo> additionalTableList = _tableHandler.getTableList(dbMeta, additionalSchema);
            for (DfTableMetaInfo metaInfo : additionalTableList) {
                final String tmp = metaInfo.getTableSchema();
                if (tmp == null || tmp.trim().length() == 0) {
                    metaInfo.setTableSchema(additionalSchema);
                }
            }
            helpTableComments(tableList, additionalSchema);
            tableList.addAll(additionalTableList);
        }
    }

    protected void helpTableComments(List<DfTableMetaInfo> tableList, String schema) {
        final DfDbCommentExtractor extractor = createDbCommentExtractor(schema);
        if (extractor != null) {
            final Set<String> tableSet = new HashSet<String>();
            for (DfTableMetaInfo table : tableList) {
                tableSet.add(table.getTableName());
            }
            try {
                final Map<String, UserTabComments> tableCommentMap = extractor.extractTableComment(tableSet);
                for (DfTableMetaInfo table : tableList) {
                    table.acceptTableComment(tableCommentMap);

                    // *Synonym Helping is after loading synonyms.
                }
            } catch (RuntimeException ignored) {
                _log.debug("Failed to extract table comments: extractor=" + extractor, ignored);
            }
            try {
                if (_columnCommentAllMap == null) {
                    _columnCommentAllMap = extractor.extractColumnComment(tableSet);
                }
            } catch (RuntimeException ignored) {
                _log.debug("Failed to extract column comments: extractor=" + extractor, ignored);
            }
        }
    }

    protected void helpSynonymTableComments(List<DfTableMetaInfo> tableList) { // should be executed after loading synonyms
        for (DfTableMetaInfo table : tableList) {
            if (canHandleSynonym(table) && !table.hasTableComment()) {
                DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
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
        final String schema = getHandlerUseSchema(table);
        List<DfColumnMetaInfo> columnList = _columnHandler.getColumns(dbMeta, schema, table);
        if (canHandleSynonym(table) && columnList.isEmpty()) {
            DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
            if (synonym != null && synonym.isDBLink()) {
                columnList = synonym.getColumnMetaInfoList();
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
     * Retrieves a list of the columns composing the primary key for a given table.
     * @param dbMeta The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return A list of the primary key parts for <code>tableName</code>.
     * @throws SQLException
     */
    protected List<String> getPrimaryColumnNameList(DatabaseMetaData dbMeta, DfTableMetaInfo table) throws SQLException {
        final String schema = getHandlerUseSchema(table);
        final List<String> primaryColumnNameList = _uniqueKeyHandler.getPrimaryColumnNameList(dbMeta, schema, table);
        if (!canHandleSynonym(table) || !primaryColumnNameList.isEmpty()) {
            return primaryColumnNameList;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getPrimaryKeyNameList() : primaryColumnNameList;
    }

    // -----------------------------------------------------
    //                                            Unique Key
    //                                            ----------
    /**
     * Get unique column name list.
     * @param dbMeta The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return The list of unique columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData dbMeta, DfTableMetaInfo table)
            throws SQLException {
        final String schema = getHandlerUseSchema(table);
        final Map<String, Map<Integer, String>> uniqueKeyMap = _uniqueKeyHandler.getUniqueKeyMap(dbMeta, schema, table);
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
     * @param dbMeta The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    protected Map<String, DfForeignKeyMetaInfo> getForeignKeys(DatabaseMetaData dbMeta, DfTableMetaInfo table)
            throws SQLException {
        final String schema = getHandlerUseSchema(table);
        final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfo = _foreignKeyHandler.getForeignKeyMetaInfo(dbMeta,
                schema, table);
        if (!canHandleSynonym(table) || !foreignKeyMetaInfo.isEmpty()) {
            return foreignKeyMetaInfo;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getForeignKeyMetaInfoMap() : foreignKeyMetaInfo;
    }

    protected String getHandlerUseSchema(DfTableMetaInfo tableMetaInfo) {
        return isAdditionalSchemaTable(tableMetaInfo) ? tableMetaInfo.getTableSchema() : _schema;
    }

    protected boolean isAdditionalSchemaTable(DfTableMetaInfo tableMetaInfo) {
        final String schema = tableMetaInfo.getTableSchema();
        if (schema == null || schema.trim().length() == 0) {
            return false;
        }
        final List<String> additionalSchemaList = getDatabaseProperties().getAdditionalSchemaList();
        return additionalSchemaList.contains(schema);
    }

    // -----------------------------------------------------
    //                                                 Index
    //                                                 -----
    /**
     * Get index column name list.
     * @param dbMeta The meta data of a database. (NotNull)
     * @param table The meta information of table. (NotNull)
     * @param uniqueKeyMap The map of unique key. (NotNull)
     * @return The list of index columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData dbMeta, DfTableMetaInfo table,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final String schema = getHandlerUseSchema(table);
        final Map<String, Map<Integer, String>> indexMap = _indexHandler.getIndexMap(dbMeta, schema, table,
                uniqueKeyMap);
        if (!canHandleSynonym(table) || !indexMap.isEmpty()) {
            return indexMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(table);
        return synonym != null ? synonym.getIndexMap() : indexMap;
    }

    // -----------------------------------------------------
    //                                               Synonym
    //                                               -------
    protected void loadSynonymInfoIfNeeds() {
        final DfSynonymExtractor extractor = createSynonymExtractor();
        if (extractor == null) {
            return;
        }
        try {
            _log.info("...Loading synonyms");
            _synonymMap = extractor.extractSynonymMap();
            final Set<String> keySet = _synonymMap.keySet();
            final StringBuilder sb = new StringBuilder();
            sb.append("Finished loading synonyms").append(ln()).append("[Synonym Map]");
            for (String key : keySet) {
                sb.append(ln()).append(" " + key + " = " + _synonymMap.get(key));
            }
            _log.info(sb.toString());
        } catch (Exception ignored) {
            _log.info("DfSynonymExtractor.extractSynonymMap() threw the exception!", ignored);
        }
    }

    protected boolean canHandleSynonym(DfTableMetaInfo table) {
        return _synonymMap != null && table.canHandleSynonym();
    }

    protected DfSynonymMetaInfo getSynonymMetaInfo(DfTableMetaInfo table) {
        if (!canHandleSynonym(table)) {
            String msg = "The table meta information should be for synonym: " + table;
            throw new IllegalStateException(msg);
        }
        return _synonymMap.get(table.getTableName());
    }

    // ===================================================================================
    //                                                                    Additional Table
    //                                                                    ================
    protected void setupAddtionalTableIfNeeds() { // since 0.8.0
        final String tableType = "TABLE";
        final DfAdditionalTableProperties prop = new DfAdditionalTableProperties(getProperties().getProperties());
        final Map<String, Object> tableMap = prop.getAdditionalTableMap();
        final Set<String> tableNameKey = tableMap.keySet();
        for (String tableName : tableNameKey) {
            _log.info("...Processing additional table: " + tableName + "(" + tableType + ")");
            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", tableName);
            tableElement.setAttribute("type", tableType);

            final Map<String, Map<String, String>> columnMap = prop.findColumnMap(tableName);
            final Set<String> columnNameKey = columnMap.keySet();
            for (String columnName : columnNameKey) {
                final Element columnElement = _doc.createElement("column");
                columnElement.setAttribute("name", columnName);

                final String columnType = prop.findColumnType(tableName, columnName);
                final String columnSize = prop.findColumnSize(tableName, columnName);
                final boolean required = prop.isColumnRequired(tableName, columnName);
                final boolean primaryKey = prop.isColumnPrimaryKey(tableName, columnName);
                final boolean autoIncrement = prop.isColumnAutoIncrement(tableName, columnName);
                columnElement.setAttribute("type", columnType);
                columnElement.setAttribute("required", String.valueOf(required));
                if (columnSize != null && columnSize.trim().length() > 0) {
                    columnElement.setAttribute("size", columnSize);
                }
                if (primaryKey) {
                    columnElement.setAttribute("primaryKey", String.valueOf(primaryKey));
                }
                if (autoIncrement) {
                    columnElement.setAttribute("autoIncrement", String.valueOf(autoIncrement));
                }
                tableElement.appendChild(columnElement);
            }
            _databaseNode.appendChild(tableElement);
        }
    }

    // ===================================================================================
    //                                                                           Extractor
    //                                                                           =========
    protected DfDbCommentExtractor createDbCommentExtractor(String schema) {
        final DfDbCommentExtractorFactory factory = createDbCommentExtractorFactory(schema);
        return factory.createDbCommentExtractor();
    }

    protected DfDbCommentExtractorFactory createDbCommentExtractorFactory(String schema) {
        return new DfDbCommentExtractorFactory(getBasicProperties(), getDataSource(), schema);
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
        // The schema to extract is only main schema.
        // The synonym extractor may need the set collection for reference table check.
        return new DfSynonymExtractorFactory(getBasicProperties(), getDataSource(), _schema, _refTableCheckSet);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setOutputFile(String v) {
        _xmlSchema = v;
    }
}