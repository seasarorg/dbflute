package org.seasar.dbflute.logic.jdbc.schemaxml;

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
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

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
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfSchemaEmptyException;
import org.seasar.dbflute.exception.DfTableDuplicateException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.doc.historyhtml.DfSchemaHistory;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfAutoIncrementExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfForeignKeyExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfIndexExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfTableExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfUniqueKeyExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.factory.DfDbCommentExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.identity.factory.DfIdentityExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.factory.DfSynonymExtractorFactory;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfSchemaXmlFacadeProp;
import org.seasar.dbflute.util.Srl;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class DfSchemaXmlSerializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSchemaXmlSerializer.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        Basic Resource
    //                                        --------------
    protected final DataSource _dataSource;
    protected final UnifiedSchema _mainSchema;
    protected final String _schemaXml;
    protected final String _historyFile;
    protected final DfSchemaDiff _schemaDiff;

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
    protected DfTableExtractor _tableHandler = new DfTableExtractor();
    protected DfColumnExtractor _columnHandler = new DfColumnExtractor();
    protected DfUniqueKeyExtractor _uniqueKeyHandler = new DfUniqueKeyExtractor();
    protected DfIndexExtractor _indexHandler = new DfIndexExtractor();
    protected DfForeignKeyExtractor _foreignKeyHandler = new DfForeignKeyExtractor();
    protected DfAutoIncrementExtractor _autoIncrementHandler = new DfAutoIncrementExtractor();

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
    //                                                                         Constructor
    //                                                                         ===========
    protected DfSchemaXmlSerializer(DataSource dataSource, UnifiedSchema mainSchema, String schemaXml,
            String historyFile) {
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _schemaXml = schemaXml;
        _historyFile = historyFile;
        _schemaDiff = DfSchemaDiff.createAsFlexible(schemaXml);
    }

    public static DfSchemaXmlSerializer createAsCore(DataSource dataSource, UnifiedSchema mainSchema) {
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        final DfSchemaXmlFacadeProp facadeProp = basicProp.getSchemaXmlFacadeProp();
        final String schemaXml = facadeProp.getProejctSchemaXMLFilePath();
        final String historyFile = facadeProp.getProjectSchemaHistoryFilePath();
        return new DfSchemaXmlSerializer(dataSource, mainSchema, schemaXml, historyFile);
    }

    public static DfSchemaXmlSerializer createAsPlain(DataSource dataSource, UnifiedSchema mainSchema,
            String schemaXml, String historyFile) {
        return new DfSchemaXmlSerializer(dataSource, mainSchema, schemaXml, historyFile);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public void serialize() {
        _log.info("");
        _log.info("...Starting to process JDBC to SchemaXML");

        loadPreviousSchema();

        _doc = createDocumentImpl();
        _doc.appendChild(_doc.createComment(" Auto-generated by JDBC task! "));

        final String filePath = _schemaXml;
        final String encoding = getSchemaXmlEncoding();
        try {
            initializeIdentityMapIfNeeds();
            generateXML();

            _log.info("...Serializing XML:");
            _log.info("  filePath = " + filePath);
            _log.info("  encoding = " + encoding);
            final XMLSerializer xmlSerializer;
            {
                final FileOutputStream fis = new FileOutputStream(filePath);
                final OutputStreamWriter writer = new OutputStreamWriter(fis, encoding);
                final OutputFormat outputFormar = new OutputFormat(Method.XML, encoding, true);
                xmlSerializer = new XMLSerializer(writer, outputFormar);
            }
            xmlSerializer.serialize(_doc);
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

        loadNextSchema();
    }

    protected DocumentImpl createDocumentImpl() {
        return new DocumentImpl(createDocumentType());
    }

    protected DocumentType createDocumentType() {
        return new DocumentTypeImpl(null, "database", null, DTDResolver.WEB_SITE_DTD);
    }

    /**
     * Generates an XML database schema from JDBC meta data.
     * @throws Exception a generic exception.
     */
    protected void generateXML() throws Exception {
        _log.info("...Getting DB-connection");
        final Connection conn = _dataSource.getConnection();

        _log.info("...Getting DB-meta-data");
        final DatabaseMetaData metaData = conn.getMetaData();

        final List<DfTableMetaInfo> tableList = getTableNames(metaData);

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

        _log.info("");
        _log.info("$ /= = = = = = = = = = = = = = = = = = = = = = = = = =");
        _log.info("$ [Table List]");
        int tableCount = 0;
        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMetaInfo tableInfo = tableList.get(i);
            if (processTable(conn, metaData, tableInfo)) {
                ++tableCount;
            }
        } // end of table loop
        _log.info("$ ");
        _log.info("$ [Table Count]");
        _log.info("$ " + tableCount);
        _log.info("$ = = = = = = = = = =/");
        _log.info("");

        final boolean additionalTableExists = setupAddtionalTableIfNeeds();
        if (tableList.isEmpty() && !additionalTableExists) {
            throwSchemaEmptyException();
        }
        _doc.appendChild(_databaseNode);
    }

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    protected boolean processTable(Connection conn, DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        if (tableInfo.isOutOfGenerateTarget()) {
            // for example, sequence synonym and so on...
            _log.info("$ " + tableInfo.buildTableFullQualifiedName() + " is out of generation target!");
            return false;
        }
        _log.info("$ " + tableInfo.toString());

        final Element tableElement = _doc.createElement("table");
        tableElement.setAttribute("name", tableInfo.getTableName());
        tableElement.setAttribute("type", tableInfo.getTableType());
        final UnifiedSchema unifiedSchema = tableInfo.getUnifiedSchema();
        if (unifiedSchema.hasSchema()) {
            tableElement.setAttribute("schema", unifiedSchema.getIdentifiedSchema());
        }
        final String tableComment = tableInfo.getTableComment();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(tableComment)) {
            tableElement.setAttribute("comment", tableComment);
        }
        final DfPrimaryKeyMetaInfo pkInfo = getPrimaryColumnMetaInfo(metaData, tableInfo);
        final List<DfColumnMetaInfo> columns = getColumns(metaData, tableInfo);
        for (int j = 0; j < columns.size(); j++) {
            final DfColumnMetaInfo columnInfo = columns.get(j);
            final Element columnElement = _doc.createElement("column");

            processColumnName(columnInfo, columnElement);
            processColumnType(columnInfo, columnElement);
            processColumnDbType(columnInfo, columnElement);
            processColumnJavaType(columnInfo, columnElement);
            processColumnSize(columnInfo, columnElement);
            processRequired(columnInfo, columnElement);
            processPrimaryKey(columnInfo, pkInfo, columnElement);
            processColumnComment(columnInfo, columnElement);
            processDefaultValue(columnInfo, columnElement);
            processAutoIncrement(tableInfo, columnInfo, pkInfo, conn, columnElement);

            tableElement.appendChild(columnElement);
        }

        processForeignKey(metaData, tableInfo, tableElement);
        final Map<String, Map<Integer, String>> uniqueKeyMap = processUniqueKey(metaData, tableInfo, tableElement);
        processIndex(metaData, tableInfo, tableElement, uniqueKeyMap);

        _databaseNode.appendChild(tableElement);
        return true;
    }

    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    protected void processColumnName(final DfColumnMetaInfo columnInfo, final Element columnElement) {
        final String columnName = columnInfo.getColumnName();
        columnElement.setAttribute("name", columnName);
    }

    protected void processColumnType(final DfColumnMetaInfo columnInfo, final Element columnElement) {
        columnElement.setAttribute("type", getColumnJdbcType(columnInfo));
    }

    protected void processColumnDbType(final DfColumnMetaInfo columnInfo, final Element columnElement) {
        columnElement.setAttribute("dbType", columnInfo.getDbTypeName());
    }

    protected void processColumnJavaType(final DfColumnMetaInfo columnInfo, final Element columnElement) {
        final String jdbcType = getColumnJdbcType(columnInfo);
        final int columnSize = columnInfo.getColumnSize();
        final int decimalDigits = columnInfo.getDecimalDigits();
        final String javaNative = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize, decimalDigits);
        columnElement.setAttribute("javaType", javaNative);
    }

    protected String getColumnJdbcType(DfColumnMetaInfo columnInfo) {
        return _columnHandler.getColumnJdbcType(columnInfo);
    }

    protected void processColumnSize(DfColumnMetaInfo columnInfo, Element columnElement) {
        final int columnSize = columnInfo.getColumnSize();
        final int decimalDigits = columnInfo.getDecimalDigits();
        if (DfColumnExtractor.isColumnSizeValid(columnSize)) {
            if (DfColumnExtractor.isDecimalDigitsValid(decimalDigits)) {
                columnElement.setAttribute("size", columnSize + ", " + decimalDigits);
            } else {
                columnElement.setAttribute("size", String.valueOf(columnSize));
            }
        }
    }

    protected void processRequired(DfColumnMetaInfo columnInfo, Element columnElement) {
        if (columnInfo.isRequired()) {
            columnElement.setAttribute("required", "true");
        }
    }

    protected void processPrimaryKey(DfColumnMetaInfo columnInfo, DfPrimaryKeyMetaInfo pkInfo, Element columnElement) {
        final String columnName = columnInfo.getColumnName();
        if (pkInfo.containsColumn(columnName)) {
            columnElement.setAttribute("primaryKey", "true");
            final String pkName = pkInfo.getPrimaryKeyName(columnName);
            if (pkName != null && pkName.trim().length() > 0) {
                columnElement.setAttribute("pkName", pkInfo.getPrimaryKeyName(columnName));
            }
        }
    }

    protected void processColumnComment(DfColumnMetaInfo columnInfo, Element columnElement) {
        final String columnComment = columnInfo.getColumnComment();
        if (columnComment != null) {
            columnElement.setAttribute("comment", columnComment);
        }
    }

    protected void processDefaultValue(DfColumnMetaInfo columnInfo, Element columnElement) {
        String defaultValue = columnInfo.getDefaultValue();
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
    }

    protected void processAutoIncrement(DfTableMetaInfo tableInfo, DfColumnMetaInfo columnInfo,
            DfPrimaryKeyMetaInfo pkInfo, Connection conn, Element columnElement) throws SQLException {
        final String columnName = columnInfo.getColumnName();
        if (pkInfo.containsColumn(columnName)) {
            if (isAutoIncrementColumn(conn, tableInfo, columnInfo)) {
                columnElement.setAttribute("autoIncrement", "true");
            }
        }
    }

    // -----------------------------------------------------
    //                                            Constraint
    //                                            ----------
    protected void processForeignKey(DatabaseMetaData metaData, DfTableMetaInfo tableInfo, Element tableElement)
            throws SQLException {
        final Map<String, DfForeignKeyMetaInfo> foreignKeyMap = getForeignKeys(metaData, tableInfo);
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
    }

    protected Map<String, Map<Integer, String>> processUniqueKey(DatabaseMetaData metaData, DfTableMetaInfo tableInfo,
            Element tableElement) throws SQLException {
        final Map<String, Map<Integer, String>> uniqueMap = getUniqueKeyMap(metaData, tableInfo);
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
        return uniqueMap;
    }

    protected void processIndex(DatabaseMetaData metaData, DfTableMetaInfo tableInfo, Element tableElement,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final Map<String, Map<Integer, String>> indexMap = getIndexMap(metaData, tableInfo, uniqueKeyMap);
        final java.util.Set<String> indexKeySet = indexMap.keySet();
        for (final String indexName : indexKeySet) {
            final Map<Integer, String> indexElementMap = indexMap.get(indexName);
            if (indexElementMap.isEmpty()) {
                String msg = "The index has no elements: " + indexName + " : " + indexMap;
                throw new IllegalStateException(msg);
            }
            final Element indexElement = _doc.createElement("index");
            indexElement.setAttribute("name", indexName);
            final Set<Integer> uniqueElementKeySet = indexElementMap.keySet();
            for (final Integer ordinalPosition : uniqueElementKeySet) {
                final String columnName = indexElementMap.get(ordinalPosition);
                final Element uniqueColumnElement = _doc.createElement("index-column");
                uniqueColumnElement.setAttribute("name", columnName);
                uniqueColumnElement.setAttribute("position", ordinalPosition.toString());
                indexElement.appendChild(uniqueColumnElement);
            }
            tableElement.appendChild(indexElement);
        }
    }

    protected void throwSchemaEmptyException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The schema was empty (does not have a table).");
        br.addItem("Advice");
        br.addElement("Please confirm the database connection settings.");
        br.addElement("If you've not created the schema yet, please create it.");
        br.addElement("You can create easily by using replace-schema.");
        br.addElement("Set up ./playsql/replace-schema.sql and execute ReplaceSchema task");
        br.addItem("Connected Schema");
        br.addElement("schema = " + _mainSchema);
        final String msg = br.buildExceptionMessage();
        throw new DfSchemaEmptyException(msg);
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
     * @param tableInfo The meta information of table. (NotNull)
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, DfTableMetaInfo tableInfo) throws SQLException {
        List<DfColumnMetaInfo> columnList = _columnHandler.getColumnList(dbMeta, tableInfo);
        if (canHandleSynonym(tableInfo) && columnList.isEmpty()) {
            DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
            if (synonym != null && synonym.isDBLink()) {
                columnList = synonym.getColumnMetaInfoList4DBLink();
            }
        }

        helpColumnComments(tableInfo, columnList);
        return columnList;
    }

    protected void helpColumnComments(DfTableMetaInfo tableInfo, List<DfColumnMetaInfo> columnList) {
        if (_columnCommentAllMap != null) {
            final String tableName = tableInfo.getTableName();
            final Map<String, UserColComments> columnCommentMap = _columnCommentAllMap.get(tableName);
            for (DfColumnMetaInfo column : columnList) {
                column.acceptColumnComment(columnCommentMap);
            }
        }
        helpSynonymColumnComments(tableInfo, columnList);
    }

    protected void helpSynonymColumnComments(DfTableMetaInfo tableInfo, List<DfColumnMetaInfo> columnList) {
        for (DfColumnMetaInfo column : columnList) {
            if (canHandleSynonym(tableInfo) && !column.hasColumnComment()) {
                DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
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
     * @param tableInfo The meta information of table. (NotNull)
     * @return The meta information of primary key. (NotNull)
     * @throws SQLException
     */
    protected DfPrimaryKeyMetaInfo getPrimaryColumnMetaInfo(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final DfPrimaryKeyMetaInfo pkInfo = _uniqueKeyHandler.getPrimaryKey(metaData, tableInfo);
        final List<String> pkList = pkInfo.getPrimaryKeyList();
        if (!canHandleSynonym(tableInfo) || !pkList.isEmpty()) {
            return pkInfo;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
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
     * @param tableInfo The meta information of table. (NotNull)
     * @return The list of unique columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final Map<String, Map<Integer, String>> uniqueKeyMap = _uniqueKeyHandler.getUniqueKeyMap(metaData, tableInfo);
        if (!canHandleSynonym(tableInfo) || !uniqueKeyMap.isEmpty()) {
            return uniqueKeyMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
        return synonym != null ? synonym.getUniqueKeyMap() : uniqueKeyMap;
    }

    // -----------------------------------------------------
    //                                        Auto Increment
    //                                        --------------
    /**
     * Get auto-increment column name.
     * @param tableInfo The meta information of table from which to retrieve PK information.
     * @param primaryKeyColumnInfo The meta information of primary-key column.
     * @param conn Connection.
     * @return Auto-increment column name. (NullAllowed)
     * @throws SQLException
     */
    protected boolean isAutoIncrementColumn(Connection conn, DfTableMetaInfo tableInfo,
            DfColumnMetaInfo primaryKeyColumnInfo) throws SQLException {
        if (_autoIncrementHandler.isAutoIncrementColumn(conn, tableInfo, primaryKeyColumnInfo)) {
            return true;
        }
        if (canHandleSynonym(tableInfo)) {
            final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
            if (synonym != null && synonym.isAutoIncrement()) {
                return true;
            }
        }
        if (_identityMap == null) {
            return false;
        }
        final String primaryKeyColumnName = primaryKeyColumnInfo.getColumnName();
        final String columnName = _identityMap.get(tableInfo.getTableName());
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
     * @param tableInfo The meta information of table. (NotNull)
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    protected Map<String, DfForeignKeyMetaInfo> getForeignKeys(DatabaseMetaData metaData, DfTableMetaInfo tableInfo)
            throws SQLException {
        final Map<String, DfForeignKeyMetaInfo> foreignKeyMap = _foreignKeyHandler
                .getForeignKeyMap(metaData, tableInfo);
        if (!canHandleSynonym(tableInfo) || !foreignKeyMap.isEmpty()) {
            return foreignKeyMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
        return synonym != null ? synonym.getForeignKeyMap() : foreignKeyMap;
    }

    // -----------------------------------------------------
    //                                                 Index
    //                                                 -----
    /**
     * Get index column name list.
     * @param metaData The meta data of a database. (NotNull)
     * @param tableInfo The meta information of table. (NotNull)
     * @param uniqueKeyMap The map of unique key. (NotNull)
     * @return The list of index columns. (NotNull)
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, DfTableMetaInfo tableInfo,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final Map<String, Map<Integer, String>> indexMap = _indexHandler.getIndexMap(metaData, tableInfo, uniqueKeyMap);
        if (!canHandleSynonym(tableInfo) || !indexMap.isEmpty()) {
            return indexMap;
        }
        final DfSynonymMetaInfo synonym = getSynonymMetaInfo(tableInfo);
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

    protected boolean canHandleSynonym(DfTableMetaInfo tableInfo) {
        return _supplementarySynonymInfoMap != null && tableInfo.canHandleSynonym();
    }

    protected DfSynonymMetaInfo getSynonymMetaInfo(DfTableMetaInfo tableInfo) {
        if (!canHandleSynonym(tableInfo)) {
            String msg = "The table meta information should be for synonym: " + tableInfo;
            throw new IllegalStateException(msg);
        }
        String key = tableInfo.buildTableFullQualifiedName();
        DfSynonymMetaInfo info = _supplementarySynonymInfoMap.get(key);
        if (info != null) {
            return info;
        }
        key = tableInfo.buildSchemaQualifiedName();
        info = _supplementarySynonymInfoMap.get(key);
        if (info != null) {
            return info;
        }
        return null;
    }

    // ===================================================================================
    //                                                                    Additional Table
    //                                                                    ================
    protected boolean setupAddtionalTableIfNeeds() {
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
        return new DfDbCommentExtractorFactory(_dataSource, unifiedSchema, getDatabaseTypeFacadeProp());
    }

    protected DfIdentityExtractor createIdentityExtractor() {
        final DfIdentityExtractorFactory factory = createIdentityExtractorFactory();
        return factory.createIdentityExtractor();
    }

    protected DfIdentityExtractorFactory createIdentityExtractorFactory() {
        return new DfIdentityExtractorFactory(_dataSource, getDatabaseTypeFacadeProp());
    }

    protected DfSynonymExtractor createSynonymExtractor() {
        final DfSynonymExtractorFactory factory = createSynonymExtractorFactory();
        return factory.createSynonymExtractor();
    }

    protected DfSynonymExtractorFactory createSynonymExtractorFactory() {
        // The synonym extractor needs the map of generated tables for reference table check.
        return new DfSynonymExtractorFactory(_dataSource, getDatabaseTypeFacadeProp(), getDatabaseProperties(),
                _generatedTableMap);
    }

    // ===================================================================================
    //                                                                         Schema Diff
    //                                                                         ===========
    protected void loadPreviousSchema() {
        try {
            doLoadPreviousSchema();
        } catch (RuntimeException continued) {
            _log.warn("*Failed to load previous schema", continued);
        }
    }

    protected void doLoadPreviousSchema() {
        _log.info("...Loading previous schema (schema diff process)");
        _schemaDiff.loadPreviousSchema();
        if (_schemaDiff.isFirstTime()) {
            _log.info("  -> no previous (first time)");
        }
    }

    protected void loadNextSchema() {
        try {
            doLoadNextSchema();
        } catch (RuntimeException continued) {
            _log.warn("*Failed to load next schema", continued);
        }
    }

    protected void doLoadNextSchema() {
        if (_schemaDiff.isFirstTime() || _schemaDiff.isLoadingFailure()) {
            return;
        }
        _log.info("...Loading next schema (schema diff process)");
        _schemaDiff.loadNextSchema();
        _schemaDiff.analyzeDiff();
        if (_schemaDiff.hasDiff()) {
            try {
                _log.info("  -> different from previous");
                final DfSchemaHistory schemaHistory = DfSchemaHistory.createAsPlain(_historyFile);
                _log.info("...Serializing schema-diff:");
                _log.info("  filePath = " + schemaHistory.getHistoryFile());
                schemaHistory.serializeSchemaDiff(_schemaDiff);
            } catch (IOException e) {
                String msg = "*Failed to serialize schema-diff";
                throw new IllegalStateException(msg, e);
            }
        } else {
            _log.info("  -> same as previous");
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfDatabaseTypeFacadeProp getDatabaseTypeFacadeProp() {
        return getProperties().getBasicProperties().getDatabaseTypeFacadeProp();
    }

    protected DfSchemaXmlFacadeProp getSchemaXmlFacadeProp() {
        return getProperties().getBasicProperties().getSchemaXmlFacadeProp();
    }

    protected String getSchemaXmlEncoding() {
        return getSchemaXmlFacadeProp().getProejctSchemaXMLEncoding();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
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
    public String getSchemaXml() {
        return _schemaXml;
    }
}
