package org.seasar.dbflute.logic.jdbc.schemaxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfProcedureExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfTableExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfUniqueKeyExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor.UserTabComments;
import org.seasar.dbflute.logic.jdbc.metadata.comment.factory.DfDbCommentExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.identity.factory.DfIdentityExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfForeignKeyMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfPrimaryKeyMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureSourceInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSequenceMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSynonymMeta;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.factory.DfSequenceExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.factory.DfSynonymExtractorFactory;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.properties.DfAdditionalTableProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfSchemaXmlFacadeProp;
import org.seasar.dbflute.util.DfTypeUtil;
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
    protected final String _historyFile; // NullAllowed
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
    protected final DfTableExtractor _tableExtractor = new DfTableExtractor();
    protected final DfColumnExtractor _columnExtractor = new DfColumnExtractor();
    protected final DfUniqueKeyExtractor _uniqueKeyExtractor = new DfUniqueKeyExtractor();
    protected final DfIndexExtractor _indexExtractor = new DfIndexExtractor();
    protected final DfForeignKeyExtractor _foreignKeyExtractor = new DfForeignKeyExtractor();
    protected final DfAutoIncrementExtractor _autoIncrementExtractor = new DfAutoIncrementExtractor();

    // -----------------------------------------------------
    //                                        Column Comment
    //                                        --------------
    protected Map<String, Map<String, UserColComments>> _columnCommentAllMap; // as temporary cache!

    // -----------------------------------------------------
    //                                      Direct Meta Data
    //                                      ----------------
    protected Map<String, String> _identityMap;
    protected Map<String, DfSynonymMeta> _supplementarySynonymInfoMap;

    // -----------------------------------------------------
    //                                          Check Object
    //                                          ------------
    protected Map<String, DfTableMeta> _generatedTableMap;

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    protected boolean _suppressExceptTarget; // already reflected to regular handlers
    protected boolean _suppressAdditionalSchema; // to check in processes related to additional schema

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param dataSource The data source of the database. (NotNull)
     * @param mainSchema The main schema as unified schema. (NotNull)
     * @param schemaXml The XML file to output meta info of the schema. (NotNull)
     * @param historyFile The history file of schema-diff. (NullAllowed: if null, no action for schema-diff)
     */
    protected DfSchemaXmlSerializer(DataSource dataSource, UnifiedSchema mainSchema, String schemaXml,
            String historyFile) {
        _dataSource = dataSource;
        _mainSchema = mainSchema;
        _schemaXml = schemaXml;
        _historyFile = historyFile;
        _schemaDiff = DfSchemaDiff.createAsFlexible(schemaXml);

        // all diff processes are depends on the DBFlute property
        if (getDocumentProperties().isCheckColumnDefOrderDiff()) {
            _schemaDiff.checkColumnDefOrder();
        }
        if (getDocumentProperties().isCheckDbCommentDiff()) {
            _schemaDiff.checkDbComment();
        }
    }

    /**
     * @param dataSource The data source of the database. (NotNull)
     * @param mainSchema The main schema as unified schema. (NotNull)
     * @return The new instance. (NotNull)
     */
    public static DfSchemaXmlSerializer createAsCore(DataSource dataSource, UnifiedSchema mainSchema) {
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        final DfSchemaXmlFacadeProp facadeProp = basicProp.getSchemaXmlFacadeProp();
        final String schemaXml = facadeProp.getProejctSchemaXMLFile();
        final String historyFile = facadeProp.getProjectSchemaHistoryFile();
        return new DfSchemaXmlSerializer(dataSource, mainSchema, schemaXml, historyFile);
    }

    /**
     * @param dataSource The data source of the database. (NotNull)
     * @param mainSchema The main schema as unified schema. (NotNull)
     * @param schemaXml The XML file to output meta info of the schema. (NotNull)
     * @param historyFile The history file of schema-diff. (NullAllowed: if null, no action for schema-diff)
     * @return The new instance. (NotNull)
     */
    public static DfSchemaXmlSerializer createAsManage(DataSource dataSource, UnifiedSchema mainSchema,
            String schemaXml, String historyFile) {
        final DfSchemaXmlSerializer serializer = new DfSchemaXmlSerializer(dataSource, mainSchema, schemaXml,
                historyFile);
        return serializer.suppressExceptTarget().suppressAdditionalSchema();
    }

    protected DfSchemaXmlSerializer suppressExceptTarget() {
        // contains non-generated tables and procedures
        _tableExtractor.suppressExceptTarget();
        _columnExtractor.suppressExceptTarget();
        _uniqueKeyExtractor.suppressExceptTarget();
        _indexExtractor.suppressExceptTarget();
        _foreignKeyExtractor.suppressExceptTarget();
        _autoIncrementExtractor.suppressExceptTarget();
        _suppressExceptTarget = true;
        return this;
    }

    protected DfSchemaXmlSerializer suppressAdditionalSchema() {
        _suppressAdditionalSchema = true;
        return this;
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
        OutputStreamWriter writer = null;
        try {
            initializeIdentityMapIfNeeds();
            generateXML();

            _log.info("...Serializing XML:");
            _log.info("  filePath = " + filePath);
            _log.info("  encoding = " + encoding);
            final XMLSerializer xmlSerializer;
            {
                mkdirIfNotExists(filePath);
                writer = new OutputStreamWriter(new FileOutputStream(filePath), encoding);
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
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }

        loadNextSchema();
    }

    protected DocumentImpl createDocumentImpl() {
        return new DocumentImpl(createDocumentType());
    }

    protected DocumentType createDocumentType() {
        return new DocumentTypeImpl(null, "database", null, DTDResolver.WEB_SITE_DTD);
    }

    protected void mkdirIfNotExists(String filePath) {
        if (Srl.contains(filePath, "/")) {
            final String baseDirStr = Srl.substringLastFront(filePath, "/");
            final File baseDir = new File(baseDirStr);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
        }
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

        final List<DfTableMeta> tableList = getTableNames(metaData);

        // initialize the map of generated tables
        // this is used by synonym handling and foreign key handling
        // so this process should be before thier processes
        _generatedTableMap = StringKeyMap.createAsCaseInsensitive();
        for (DfTableMeta info : tableList) {
            _generatedTableMap.put(info.getTableName(), info);
        }

        // Load synonym information for merging additional meta data if it needs.
        loadSupplementarySynonymInfoIfNeeds();

        // This should be after loading synonyms so it is executed at this timing!
        // The property 'outOfGenerateTarget' is set here
        processSynonymTable(tableList);

        // The handler of foreign keys for generating.
        // It needs to check whether a reference table is generate-target or not.
        _foreignKeyExtractor.exceptForeignTableNotGenerated(_generatedTableMap);

        // Create database node. (The beginning of schema XML!)
        _databaseNode = _doc.createElement("database");
        _databaseNode.setAttribute("name", _mainSchema.getPureSchema()); // as main schema

        processTable(conn, metaData, tableList);
        final boolean additionalTableExists = setupAddtionalTableIfNeeds();
        if (tableList.isEmpty() && !additionalTableExists) {
            throwSchemaEmptyException();
        }

        processSequence(conn, metaData);

        if (isProcedureMetaEnabled()) {
            processProcedure(conn, metaData);
        }

        _doc.appendChild(_databaseNode);
    }

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    protected void processTable(final Connection conn, final DatabaseMetaData metaData,
            final List<DfTableMeta> tableList) throws SQLException {
        _log.info("");
        _log.info("$ /= = = = = = = = = = = = = = = = = = = = = = = = = =");
        _log.info("$ [Table List]");
        int tableCount = 0;
        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMeta tableInfo = tableList.get(i);
            if (doProcessTable(conn, metaData, tableInfo)) {
                ++tableCount;
            }
        } // end of table loop
        _log.info("$ ");
        _log.info("$ [Table Count]");
        _log.info("$ " + tableCount);
        _log.info("$ = = = = = = = = = =/");
        _log.info("");
    }

    protected boolean doProcessTable(Connection conn, DatabaseMetaData metaData, DfTableMeta tableMeta)
            throws SQLException {
        if (tableMeta.isOutOfGenerateTarget()) {
            // for example, sequence synonym and so on...
            _log.info("$ " + tableMeta.buildTableFullQualifiedName() + " is out of generation target!");
            return false;
        }
        _log.info("$ " + tableMeta.toString());

        final Element tableElement = _doc.createElement("table");
        tableElement.setAttribute("name", tableMeta.getTableName());
        tableElement.setAttribute("type", tableMeta.getTableType());
        final UnifiedSchema unifiedSchema = tableMeta.getUnifiedSchema();
        if (unifiedSchema.hasSchema()) {
            tableElement.setAttribute("schema", unifiedSchema.getIdentifiedSchema());
        }
        final String tableComment = tableMeta.getTableComment();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(tableComment)) {
            tableElement.setAttribute("comment", tableComment);
        }
        final DfPrimaryKeyMeta pkInfo = getPrimaryColumnMetaInfo(metaData, tableMeta);
        final List<DfColumnMeta> columns = getColumns(metaData, tableMeta);
        for (int j = 0; j < columns.size(); j++) {
            final DfColumnMeta columnInfo = columns.get(j);
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
            processAutoIncrement(tableMeta, columnInfo, pkInfo, conn, columnElement);

            tableElement.appendChild(columnElement);
        }

        processForeignKey(metaData, tableMeta, tableElement);
        final Map<String, Map<Integer, String>> uniqueKeyMap = processUniqueKey(metaData, tableMeta, tableElement);
        processIndex(metaData, tableMeta, tableElement, uniqueKeyMap);

        _databaseNode.appendChild(tableElement);
        return true;
    }

    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    protected void processColumnName(final DfColumnMeta columnMeta, final Element columnElement) {
        final String columnName = columnMeta.getColumnName();
        columnElement.setAttribute("name", columnName);
    }

    protected void processColumnType(final DfColumnMeta columnMeta, final Element columnElement) {
        columnElement.setAttribute("type", getColumnJdbcType(columnMeta));
    }

    protected void processColumnDbType(final DfColumnMeta columnMeta, final Element columnElement) {
        columnElement.setAttribute("dbType", columnMeta.getDbTypeName());
    }

    protected void processColumnJavaType(final DfColumnMeta columnMeta, final Element columnElement) {
        final String jdbcType = getColumnJdbcType(columnMeta);
        final int columnSize = columnMeta.getColumnSize();
        final int decimalDigits = columnMeta.getDecimalDigits();
        final String javaNative = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize, decimalDigits);
        columnElement.setAttribute("javaType", javaNative);
    }

    protected String getColumnJdbcType(DfColumnMeta columnMeta) {
        return _columnExtractor.getColumnJdbcType(columnMeta);
    }

    protected void processColumnSize(DfColumnMeta columnMeta, Element columnElement) {
        final int columnSize = columnMeta.getColumnSize();
        final int decimalDigits = columnMeta.getDecimalDigits();
        if (DfColumnExtractor.isColumnSizeValid(columnSize)) {
            if (DfColumnExtractor.isDecimalDigitsValid(decimalDigits)) {
                columnElement.setAttribute("size", columnSize + ", " + decimalDigits);
            } else {
                columnElement.setAttribute("size", String.valueOf(columnSize));
            }
        }
    }

    protected void processRequired(DfColumnMeta columnMeta, Element columnElement) {
        if (columnMeta.isRequired()) {
            columnElement.setAttribute("required", "true");
        }
    }

    protected void processPrimaryKey(DfColumnMeta columnMeta, DfPrimaryKeyMeta pkInfo, Element columnElement) {
        final String columnName = columnMeta.getColumnName();
        if (pkInfo.containsColumn(columnName)) {
            columnElement.setAttribute("primaryKey", "true");
            final String pkName = pkInfo.getPrimaryKeyName(columnName);
            if (pkName != null && pkName.trim().length() > 0) {
                columnElement.setAttribute("pkName", pkInfo.getPrimaryKeyName(columnName));
            }
        }
    }

    protected void processColumnComment(DfColumnMeta columnMeta, Element columnElement) {
        final String columnComment = columnMeta.getColumnComment();
        if (columnComment != null) {
            columnElement.setAttribute("comment", columnComment);
        }
    }

    protected void processDefaultValue(DfColumnMeta columnMeta, Element columnElement) {
        String defaultValue = columnMeta.getDefaultValue();
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

    protected void processAutoIncrement(DfTableMeta tableMeta, DfColumnMeta columnMeta, DfPrimaryKeyMeta pkMeta,
            Connection conn, Element columnElement) throws SQLException {
        final String columnName = columnMeta.getColumnName();
        if (pkMeta.containsColumn(columnName)) {
            if (isAutoIncrementColumn(conn, tableMeta, columnMeta)) {
                columnElement.setAttribute("autoIncrement", "true");
            }
        }
    }

    // -----------------------------------------------------
    //                                            Constraint
    //                                            ----------
    protected void processForeignKey(DatabaseMetaData metaData, DfTableMeta tableMeta, Element tableElement)
            throws SQLException {
        final Map<String, DfForeignKeyMeta> foreignKeyMap = getForeignKeys(metaData, tableMeta);
        final Set<String> foreignKeyKeySet = foreignKeyMap.keySet();
        for (String foreignKeyName : foreignKeyKeySet) {
            final DfForeignKeyMeta foreignKeyMetaInfo = foreignKeyMap.get(foreignKeyName);
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

    protected Map<String, Map<Integer, String>> processUniqueKey(DatabaseMetaData metaData, DfTableMeta tableMeta,
            Element tableElement) throws SQLException {
        final Map<String, Map<Integer, String>> uniqueMap = getUniqueKeyMap(metaData, tableMeta);
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

    protected void processIndex(DatabaseMetaData metaData, DfTableMeta tableMeta, Element tableElement,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final Map<String, Map<Integer, String>> indexMap = getIndexMap(metaData, tableMeta, uniqueKeyMap);
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
        br.addNotice("The schema was empty, which had no table.");
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

    // -----------------------------------------------------
    //                                              Sequence
    //                                              --------
    protected void processSequence(Connection conn, DatabaseMetaData metaData) throws SQLException {
        _log.info("...Getting sequences");
        final Map<String, DfSequenceMeta> sequenceMap = extractSequenceMap();
        if (sequenceMap == null) {
            return;
        }
        _log.info("...Processing sequences: " + sequenceMap.size());
        final Element sequenceGroupElement = _doc.createElement("sequenceGroup");
        for (Entry<String, DfSequenceMeta> entry : sequenceMap.entrySet()) {
            final DfSequenceMeta sequenceMeta = entry.getValue();
            doProcessSequence(sequenceGroupElement, sequenceMeta);
        }
        _databaseNode.appendChild(sequenceGroupElement);
    }

    protected Map<String, DfSequenceMeta> extractSequenceMap() {
        final DfSequenceExtractorFactory factory = createSequenceExtractorFactory(_dataSource);
        final DfSequenceExtractor sequenceExtractor = factory.createSequenceExtractor();
        Map<String, DfSequenceMeta> sequenceMap = null;
        if (sequenceExtractor != null) {
            try {
                sequenceMap = sequenceExtractor.getSequenceMap();
            } catch (RuntimeException continued) { // because of supplement
                _log.info("*Failed to get sequence map: " + continued.getMessage());
            }
        }
        return sequenceMap;
    }

    protected DfSequenceExtractorFactory createSequenceExtractorFactory(DataSource dataSource) {
        final DfDatabaseTypeFacadeProp facadeProp = getProperties().getBasicProperties().getDatabaseTypeFacadeProp();
        final DfDatabaseProperties databaseProp = getDatabaseProperties();
        final DfSequenceExtractorFactory factory = new DfSequenceExtractorFactory(dataSource, facadeProp, databaseProp);
        if (_suppressAdditionalSchema) {
            factory.suppressAdditionalSchema();
        }
        return factory;
    }

    protected void doProcessSequence(Element sequenceGroupElement, final DfSequenceMeta sequenceMeta) {
        final Element sequenceElement = _doc.createElement("sequence");
        sequenceElement.setAttribute("name", sequenceMeta.getSequenceName());
        final String sequenceCatalog = sequenceMeta.getSequenceCatalog();
        final String sequenceSchema = sequenceMeta.getSequenceSchema();
        final UnifiedSchema unifiedSchema = UnifiedSchema.createAsDynamicSchema(sequenceCatalog, sequenceSchema);
        if (unifiedSchema.hasSchema()) {
            sequenceElement.setAttribute("schema", unifiedSchema.getIdentifiedSchema());
        }
        setupSequenceAttributeNumber(sequenceMeta, sequenceElement, "minimumValue", sequenceMeta.getMinimumValue());
        setupSequenceAttributeNumber(sequenceMeta, sequenceElement, "maximumValue", sequenceMeta.getMaximumValue());
        setupSequenceAttributeNumber(sequenceMeta, sequenceElement, "incrementSize", sequenceMeta.getIncrementSize());
        // no sequence meta now (2012/08/21)
        //sequenceElement.setAttribute("comment", null);
        sequenceGroupElement.appendChild(sequenceElement);
    }

    protected void setupSequenceAttributeNumber(DfSequenceMeta sequenceMeta, Element tableElement, String key,
            Number number) {
        if (number != null) {
            tableElement.setAttribute(key, DfTypeUtil.toString(number));
        }
    }

    // -----------------------------------------------------
    //                                             Procedure
    //                                             ---------
    protected boolean isProcedureMetaEnabled() {
        return getDocumentProperties().isCheckProcedureDiff();
    }

    protected void processProcedure(Connection conn, DatabaseMetaData metaData) throws SQLException {
        _log.info("...Extracting procedures");
        final Map<String, DfProcedureMeta> procedureMap = extractProcedureMap();
        if (procedureMap == null) {
            return;
        }
        _log.info("...Processing procedures: " + procedureMap.size());
        final Element procedureGroupElement = _doc.createElement("procedureGroup");
        for (Entry<String, DfProcedureMeta> entry : procedureMap.entrySet()) {
            final DfProcedureMeta procedureMeta = entry.getValue();
            doProcessProcedure(procedureGroupElement, procedureMeta);
        }
        _databaseNode.appendChild(procedureGroupElement);
    }

    protected Map<String, DfProcedureMeta> extractProcedureMap() {
        final DfProcedureExtractor procedureExtractor = createProcedureExtractor();
        Map<String, DfProcedureMeta> procedureMap = null;
        try {
            procedureMap = procedureExtractor.getAvailableProcedureMap(_dataSource);
        } catch (SQLException continued) { // because of supplement
            _log.info("*Failed to get procedure map: " + continued.getMessage());
        } catch (RuntimeException continued) { // because of supplement
            _log.info("*Failed to get procedure map: " + continued.getMessage());
        }
        return procedureMap;
    }

    protected DfProcedureExtractor createProcedureExtractor() {
        final DfProcedureExtractor extractor = new DfProcedureExtractor();
        if (_suppressExceptTarget) {
            extractor.suppressFilterByProperty();
        }
        if (_suppressAdditionalSchema) {
            extractor.suppressAdditionalSchema();
        }
        return extractor;
    }

    protected void doProcessProcedure(Element procedureGroupElement, final DfProcedureMeta procedureMeta) {
        final Element procedureElement = _doc.createElement("procedure");
        procedureElement.setAttribute("name", procedureMeta.getProcedureName());
        final UnifiedSchema unifiedSchema = procedureMeta.getProcedureSchema();
        if (unifiedSchema.hasSchema()) {
            procedureElement.setAttribute("schema", unifiedSchema.getIdentifiedSchema());
        }

        final String noMetaMark = DfSchemaDiff.PROCEDURE_SOURCE_NO_META_MARK;
        final DfProcedureSourceInfo sourceInfo = procedureMeta.getProcedureSourceInfo();
        {
            final Integer sourceLine = sourceInfo != null ? sourceInfo.getSourceLine() : null;
            procedureElement.setAttribute("sourceLine", sourceLine != null ? sourceLine.toString() : noMetaMark);
        }
        {
            final Integer sourceSize = sourceInfo != null ? sourceInfo.getSourceSize() : null;
            procedureElement.setAttribute("sourceSize", sourceSize != null ? sourceSize.toString() : noMetaMark);
        }
        {
            final String sourceHash = sourceInfo != null ? sourceInfo.toSourceHash() : null;
            procedureElement.setAttribute("sourceHash", sourceHash != null ? sourceHash : noMetaMark);
        }

        final String procedureComment = procedureMeta.getProcedureComment();
        if (Srl.is_NotNull_and_NotTrimmedEmpty(procedureComment)) {
            procedureElement.setAttribute("comment", procedureComment);
        }

        procedureGroupElement.appendChild(procedureElement);
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
    public List<DfTableMeta> getTableNames(DatabaseMetaData dbMeta) throws SQLException {
        final List<DfTableMeta> tableList = _tableExtractor.getTableList(dbMeta, _mainSchema);
        helpTableComments(tableList, _mainSchema);
        resolveAdditionalSchema(dbMeta, tableList);
        assertDuplicateTable(tableList);
        return tableList;
    }

    protected void assertDuplicateTable(List<DfTableMeta> tableList) {
        final Set<String> tableNameSet = StringSet.createAsCaseInsensitive();
        final Set<String> duplicateTableSet = StringSet.createAsCaseInsensitive();
        for (DfTableMeta info : tableList) {
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

    protected void resolveAdditionalSchema(DatabaseMetaData dbMeta, List<DfTableMeta> tableList) throws SQLException {
        if (_suppressAdditionalSchema) {
            return;
        }
        final List<UnifiedSchema> schemaList = getDatabaseProperties().getAdditionalSchemaList();
        for (UnifiedSchema additionalSchema : schemaList) {
            final List<DfTableMeta> additionalTableList = _tableExtractor.getTableList(dbMeta, additionalSchema);
            helpTableComments(additionalTableList, additionalSchema);
            tableList.addAll(additionalTableList);
        }
    }

    protected void helpTableComments(List<DfTableMeta> tableList, UnifiedSchema unifiedSchema) {
        final DfDbCommentExtractor extractor = createDbCommentExtractor(unifiedSchema);
        if (extractor != null) {
            final Set<String> tableSet = new HashSet<String>();
            for (DfTableMeta table : tableList) {
                tableSet.add(table.getTableName());
            }
            try {
                final Map<String, UserTabComments> tableCommentMap = extractor.extractTableComment(tableSet);
                for (DfTableMeta table : tableList) {
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
    protected void processSynonymTable(List<DfTableMeta> tableList) {
        judgeOutOfTargetSynonym(tableList);
        helpSynonymTableComments(tableList);
    }

    protected void judgeOutOfTargetSynonym(List<DfTableMeta> tableList) {
        for (DfTableMeta table : tableList) {
            if (canHandleSynonym(table)) {
                final DfSynonymMeta synonym = getSynonymMetaInfo(table);
                if (synonym != null && !synonym.isSelectable()) {
                    table.setOutOfGenerateTarget(true);
                }
            }
        }
    }

    protected void helpSynonymTableComments(List<DfTableMeta> tableList) {
        for (DfTableMeta table : tableList) {
            if (canHandleSynonym(table) && !table.hasTableComment()) {
                final DfSynonymMeta synonym = getSynonymMetaInfo(table);
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
    public List<DfColumnMeta> getColumns(DatabaseMetaData dbMeta, DfTableMeta tableInfo) throws SQLException {
        List<DfColumnMeta> columnList = _columnExtractor.getColumnList(dbMeta, tableInfo);
        columnList = helpColumnAdjustment(dbMeta, tableInfo, columnList);
        helpColumnComments(tableInfo, columnList);
        return columnList;
    }

    protected List<DfColumnMeta> helpColumnAdjustment(DatabaseMetaData dbMeta, DfTableMeta tableInfo,
            List<DfColumnMeta> columnList) {
        if (!canHandleSynonym(tableInfo)) {
            return columnList;
        }
        final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
        if (synonym == null) { // means not synonym or no supplementary info
            return columnList;
        }
        final List<DfColumnMeta> metaInfoList = synonym.getColumnMetaInfoList();
        if (metaInfoList.isEmpty()) {
            return metaInfoList;
        }
        if (synonym.isDBLink() && columnList.isEmpty()) {
            columnList = metaInfoList;
        } else if (metaInfoList.size() != columnList.size()) {
            // for Oracle's bug(?), which is following:
            // /- - - - - - - - - - - - - - - - - - - - - - - - - - -
            // For example, Schema A, B are like this:
            //  A: FOO table
            //  B: FOO table, BAR synonym to A's FOO table
            // BAR synonym's columns are from both A and B's FOO table.
            // (means that BAR synonym has other table's columns)
            // Why? my friend, the Oracle JDBC Driver!
            // - - - - - - - - - -/
            final StringSet columnSet = StringSet.createAsCaseInsensitive();
            for (DfColumnMeta columnMeta : metaInfoList) {
                columnSet.add(columnMeta.getColumnName());
            }
            final List<DfColumnMeta> filteredList = new ArrayList<DfColumnMeta>();
            for (DfColumnMeta columnMeta : columnList) {
                if (columnSet.contains(columnMeta.getColumnName())) {
                    filteredList.add(columnMeta);
                }
            }
            columnList = filteredList;
        }
        return columnList;
    }

    protected void helpColumnComments(DfTableMeta tableInfo, List<DfColumnMeta> columnList) {
        if (_columnCommentAllMap != null) {
            final String tableName = tableInfo.getTableName();
            final Map<String, UserColComments> columnCommentMap = _columnCommentAllMap.get(tableName);
            for (DfColumnMeta column : columnList) {
                column.acceptColumnComment(columnCommentMap);
            }
        }
        helpSynonymColumnComments(tableInfo, columnList);
    }

    protected void helpSynonymColumnComments(DfTableMeta tableInfo, List<DfColumnMeta> columnList) {
        for (DfColumnMeta column : columnList) {
            if (canHandleSynonym(tableInfo) && !column.hasColumnComment()) {
                DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
    protected DfPrimaryKeyMeta getPrimaryColumnMetaInfo(DatabaseMetaData metaData, DfTableMeta tableInfo)
            throws SQLException {
        final DfPrimaryKeyMeta pkInfo = _uniqueKeyExtractor.getPrimaryKey(metaData, tableInfo);
        final List<String> pkList = pkInfo.getPrimaryKeyList();
        if (!canHandleSynonym(tableInfo) || !pkList.isEmpty()) {
            return pkInfo;
        }
        final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
    protected Map<String, Map<Integer, String>> getUniqueKeyMap(DatabaseMetaData metaData, DfTableMeta tableInfo)
            throws SQLException {
        final Map<String, Map<Integer, String>> uniqueKeyMap = _uniqueKeyExtractor.getUniqueKeyMap(metaData, tableInfo);
        if (!canHandleSynonym(tableInfo) || !uniqueKeyMap.isEmpty()) {
            return uniqueKeyMap;
        }
        final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
    protected boolean isAutoIncrementColumn(Connection conn, DfTableMeta tableInfo, DfColumnMeta primaryKeyColumnInfo)
            throws SQLException {
        if (_autoIncrementExtractor.isAutoIncrementColumn(conn, tableInfo, primaryKeyColumnInfo)) {
            return true;
        }
        if (canHandleSynonym(tableInfo)) {
            final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
            _log.info(" -> size=" + _identityMap.size());
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
    protected Map<String, DfForeignKeyMeta> getForeignKeys(DatabaseMetaData metaData, DfTableMeta tableInfo)
            throws SQLException {
        final Map<String, DfForeignKeyMeta> foreignKeyMap = _foreignKeyExtractor.getForeignKeyMap(metaData, tableInfo);
        if (!canHandleSynonym(tableInfo) || !foreignKeyMap.isEmpty()) {
            return foreignKeyMap;
        }
        final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
    protected Map<String, Map<Integer, String>> getIndexMap(DatabaseMetaData metaData, DfTableMeta tableInfo,
            Map<String, Map<Integer, String>> uniqueKeyMap) throws SQLException {
        final Map<String, Map<Integer, String>> indexMap = _indexExtractor.getIndexMap(metaData, tableInfo,
                uniqueKeyMap);
        if (!canHandleSynonym(tableInfo) || !indexMap.isEmpty()) {
            return indexMap;
        }
        final DfSynonymMeta synonym = getSynonymMetaInfo(tableInfo);
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
            final Set<Entry<String, DfSynonymMeta>> entrySet = _supplementarySynonymInfoMap.entrySet();
            for (Entry<String, DfSynonymMeta> entry : entrySet) {
                sb.append(ln()).append(" ").append(entry.getValue().toString());
            }
            _log.info(sb.toString());
        } catch (RuntimeException ignored) {
            _log.info("DfSynonymExtractor.extractSynonymMap() threw the exception!", ignored);
        }
    }

    protected boolean canHandleSynonym(DfTableMeta tableInfo) {
        return _supplementarySynonymInfoMap != null && tableInfo.canHandleSynonym();
    }

    protected DfSynonymMeta getSynonymMetaInfo(DfTableMeta tableInfo) {
        if (!canHandleSynonym(tableInfo)) {
            String msg = "The table meta information should be for synonym: " + tableInfo;
            throw new IllegalStateException(msg);
        }
        String key = tableInfo.buildTableFullQualifiedName();
        DfSynonymMeta info = _supplementarySynonymInfoMap.get(key);
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
        if (_historyFile == null) {
            return;
        }
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
            _log.info(" -> no previous (first time)");
        }
    }

    protected void loadNextSchema() {
        if (_historyFile == null) {
            return;
        }
        try {
            doLoadNextSchema();
        } catch (RuntimeException continued) {
            _log.warn("*Failed to load next schema", continued);
        }
    }

    protected void doLoadNextSchema() {
        if (!_schemaDiff.canReadNext()) {
            return;
        }
        _log.info("...Loading next schema (schema diff process)");
        _schemaDiff.loadNextSchema();
        _schemaDiff.analyzeDiff();
        if (_schemaDiff.hasDiff()) {
            try {
                _log.info(" -> different from previous");
                final DfSchemaHistory schemaHistory = DfSchemaHistory.createAsPlain(_historyFile);
                _log.info("...Serializing schema-diff:");
                _log.info("  filePath = " + schemaHistory.getHistoryFile());
                schemaHistory.serializeSchemaDiff(_schemaDiff);
            } catch (IOException e) {
                String msg = "*Failed to serialize schema-diff";
                throw new IllegalStateException(msg, e);
            }
        } else {
            _log.info(" -> same as previous");
        }
    }

    // ===================================================================================
    //                                                                         Diff Option
    //                                                                         ===========
    public void suppressUnifiedSchema() {
        _schemaDiff.suppressUnifiedSchema();
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

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
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

    public DfSchemaDiff getSchemaDiff() {
        return _schemaDiff;
    }
}
