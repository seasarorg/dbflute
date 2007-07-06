package org.apache.torque.task;

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

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.transform.DTDResolver;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.seasar.dbflute.helper.jdbc.metadata.DfAutoIncrementHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfUniqueKeyHandler;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler.DfColumnMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.DfForeignKeyHandler.DfForeignKeyMetaInfo;
import org.seasar.dbflute.helper.jdbc.metadata.DfTableNameHandler.DfTableMetaInfo;
import org.seasar.dbflute.task.bs.DfAbstractTask;
import org.w3c.dom.Element;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class TorqueJDBCTransformTask extends DfAbstractTask {

    private static final Log _log = LogFactory.getLog(TorqueJDBCTransformTask.class);

    public static final int IDX_COLUMN_NAME = 0;

    public static final int IDX_COLUMN_TYPE = 1;

    public static final int IDX_COLUMN_SIZE = 2;

    public static final int IDX_COLUMN_NULL_TYPE = 3;

    public static final int IDX_COLUMN_DEFAULT_VALUE = 4;

    protected boolean isUseDataSource() {
        return false;
    }

    // ==============================================================================
    //                                                                      Attribute
    //                                                                      =========
    // ------------------------------------
    //                        Database Info
    //                        -------------
    /** Name of XML database schema produced. */
    protected String _xmlSchema;

    /** Is same java name? */
    protected boolean _isSameJavaName;

    // ------------------------------------
    //                        Document Info
    //                        -------------
    /** DOM document produced. */
    protected DocumentImpl _doc;

    /** The document root element. */
    protected Element _databaseNode;

    /** Hashtable to track what table a column belongs to. */
    protected Hashtable<String, String> _columnTableMap;

    // ------------------------------------
    //                              Handler
    //                              -------
    protected DfTableNameHandler _tableNameHandler = new DfTableNameHandler();
    protected DfColumnHandler _columnHandler = new DfColumnHandler();
    protected DfUniqueKeyHandler _uniqueKeyHandler = new DfUniqueKeyHandler();
    protected DfForeignKeyHandler _foreignKeyHandler = new DfForeignKeyHandler();
    protected DfAutoIncrementHandler _autoIncrementHandler = new DfAutoIncrementHandler();

    // ==============================================================================
    //                                                                       Accessor
    //                                                                       ========
    public void setOutputFile(String v) {
        _xmlSchema = v;
    }

    public void setSameJavaName(boolean v) {
        this._isSameJavaName = v;
    }

    public boolean isSameJavaName() {
        return this._isSameJavaName;
    }

    // ==============================================================================
    //                                                                    Main Method
    //                                                                    ===========
    @Override
    protected void doExecute() {
        _log.info("------------------------------------------------------- [Torque - JDBCToXMLSchema] Start!");
        _log.info("Your DB settings are:");
        _log.info("  driver : " + _driver);
        _log.info("  URL    : " + _url);
        _log.info("  user   : " + _userId);
        _log.info("  schema : " + _schema);

        final DocumentTypeImpl docType = new DocumentTypeImpl(null, "database", null, DTDResolver.WEB_SITE_DTD);
        _doc = new DocumentImpl(docType);
        _doc.appendChild(_doc.createComment(" Autogenerated by JDBCToXMLSchema! "));

        try {
            generateXML();

            _log.info("$ ");
            _log.info("$ ");
            _log.info("$ /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.info("$ ...Serializing XML: " + _xmlSchema);

            final XMLSerializer xmlSerializer;
            {
                final PrintWriter printWriter = new PrintWriter(new FileOutputStream(_xmlSchema));
                final OutputFormat outputFormar = new OutputFormat(Method.XML, null, true);
                xmlSerializer = new XMLSerializer(printWriter, outputFormar);
            }
            xmlSerializer.serialize(_doc);

            _log.info("$ * * * * * * * * */");
            _log.info("$ ");

        } catch (Exception e) {
            _log.error("JDBCToXMLSchema failed: ", e);
            throw new BuildException(e);
        }
        _log.info("------------------------------------------------------- [Torque - JDBCToXMLSchema] Finish!");
    }

    /**
     * Generates an XML database schema from JDBC metadata.
     * <p>
     * @throws Exception a generic exception.
     */
    protected void generateXML() throws Exception {
        _log.info("...Instantiate DB-driver");
        Class.forName(_driver);

        _log.info("...Getting DB-connection");
        final Connection conn = DriverManager.getConnection(_url, _userId, _password);

        _log.info("...Getting DB-meta-data");
        final DatabaseMetaData dbMetaData = conn.getMetaData();

        _log.info("$ /**************************************************************************");
        _log.info("$ ");
        _log.info("$ dbMetaData.toString(): " + dbMetaData.toString());
        _log.info("$ dbMetaData.getMaxRowSize(): " + dbMetaData.getMaxRowSize());
        _log.info("$ ");
        _log.info("$ /------------------------------------ ...Getting table list");

        final List<DfTableMetaInfo> tableList = getTableNames(dbMetaData);

        _log.info("$ ");
        _log.info("$ TableCount: " + tableList.size());
        _log.info("$ ---------------------- /");
        _log.info("$ ");
        _log.info("$ *************************************/");

        _databaseNode = _doc.createElement("database");
        _databaseNode.setAttribute("name", _schema);

        // Build a database-wide column -> table map.
        setupColumnTableMap(dbMetaData, tableList);

        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMetaInfo tableMataInfo = tableList.get(i);
            final String currentTable = (String) tableMataInfo.getTableName();

            _log.info("...Processing table: " + currentTable);

            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", currentTable);
            if (isSameJavaName()) {
                tableElement.setAttribute("javaName", currentTable);
            }

            final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMetaData, currentTable);

            final List<DfColumnMetaInfo> columns = getColumns(dbMetaData, currentTable);
            for (int j = 0; j < columns.size(); j++) {
                final DfColumnMetaInfo columnMetaInfo = columns.get(j);
                final String columnName = columnMetaInfo.getColumnName();

                final Element columnElement = _doc.createElement("column");
                columnElement.setAttribute("name", columnName);
                if (isSameJavaName()) {
                    columnElement.setAttribute("javaName", columnName);
                }

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
                    if (isAutoIncrementColumn(conn, currentTable, columnName)) {
                        columnElement.setAttribute("autoIncrement", "true");
                    }
                }

                tableElement.appendChild(columnElement);
            }

            // Foreign keys for this table.
            final Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap = getForeignKeys(dbMetaData, currentTable);
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

            // Unique keys for this table.
            Map<String, Map<Integer, String>> uniqueMap = null;
            try {
                uniqueMap = getUniqueColumnNameList(dbMetaData, tableMataInfo);
            } catch (SQLException e) {
                _log.warn("Failed to get unique column information! But continue...", e);
            } finally {
                if (uniqueMap == null) {
                    uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();
                }
            }
            final java.util.Set<String> uniqueKeySet = uniqueMap.keySet();
            for (final String uniqueIndexName : uniqueKeySet) {
                final Map<Integer, String> uniqueElementMap = uniqueMap.get(uniqueIndexName);
                if (uniqueElementMap.isEmpty()) {
                    throw new IllegalStateException("The uniqueKey has no elements: " + uniqueIndexName + " : "
                            + uniqueMap);
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

            _databaseNode.appendChild(tableElement);
        }
        _doc.appendChild(_databaseNode);
    }

    protected void setupColumnType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("type", getColumnTorqueType(columnMetaInfo, columnElement));
    }

    protected String getColumnTorqueType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final int sqlTypeCode = columnMetaInfo.getJdbcTypeCode();
        if (Types.OTHER != sqlTypeCode) {
            try {
                return TypeMap.getTorqueType(sqlTypeCode);
            } catch (RuntimeException e) {
                String msg = "Not found the sqlTypeCode in TypeMap: sqlTypeCode=";
                msg = msg + sqlTypeCode + " message=" + e.getMessage();
                _log.warn(msg);
            }
        }

        // If other
        final String dbTypeName = columnMetaInfo.getDbTypeName();
        if (dbTypeName == null) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("char")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("date")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.DATE);
            return torqueType;
        } else if (dbTypeName.toLowerCase().contains("timestamp")) {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.TIMESTAMP);
            return torqueType;
        } else {
            final String torqueType = TypeMap.getTorqueType(java.sql.Types.VARCHAR);
            return torqueType;
        }
    }

    protected void setupColumnJavaType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final String javaType = TypeMap.getJavaType(getColumnTorqueType(columnMetaInfo, columnElement));
        columnElement.setAttribute("javaType", javaType);
    }

    protected void setupColumnDbType(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        columnElement.setAttribute("dbType", columnMetaInfo.getDbTypeName());
    }

    protected void setupColumnSize(final DfColumnMetaInfo columnMetaInfo, final Element columnElement) {
        final int sqlTypeCode = columnMetaInfo.getJdbcTypeCode();
        final int columnSize = columnMetaInfo.getColumnSize();
        if (columnSize > 0
                && (sqlTypeCode == Types.CHAR || sqlTypeCode == Types.VARCHAR || sqlTypeCode == Types.LONGVARCHAR
                        || sqlTypeCode == Types.DECIMAL || sqlTypeCode == Types.NUMERIC)) {
            columnElement.setAttribute("size", String.valueOf(columnSize));
        }
    }

    /**
     * Set up column-table map. 
     * <p>
     * @param dbMetaData JDBC metadata.
     * @param tableList A list of table-name.
     * @throws SQLException
     */
    protected void setupColumnTableMap(DatabaseMetaData dbMetaData, List<DfTableMetaInfo> tableList)
            throws SQLException {
        // Build a database-wide column -> table map.
        _columnTableMap = new Hashtable<String, String>();
        for (int i = 0; i < tableList.size(); i++) {
            final DfTableMetaInfo tableMetaInfo = tableList.get(i);
            final String curTable = tableMetaInfo.getTableName();
            final List<DfColumnMetaInfo> columns = getColumns(dbMetaData, curTable);

            for (int j = 0; j < columns.size(); j++) {
                final DfColumnMetaInfo columnMetaInfo = columns.get(j);
                _columnTableMap.put(columnMetaInfo.getColumnName(), curTable);
            }
        }
    }

    /**
     * Retrieves a list of the columns composing the primary key for a given table.
     * 
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @return A list of the primary key parts for <code>tableName</code>.
     * @throws SQLException
     */
    protected List<String> getPrimaryColumnNameList(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        return _uniqueKeyHandler.getPrimaryColumnNameList(dbMeta, _schema, tableName);
    }

    /**
     * Get unique column name list.
     * 
     * @param dbMeta
     * @param tableMetaInfo
     * @return Unique column name list.
     * @throws SQLException
     */
    protected Map<String, Map<Integer, String>> getUniqueColumnNameList(DatabaseMetaData dbMeta,
            DfTableMetaInfo tableMetaInfo) throws SQLException {
        return _uniqueKeyHandler.getUniqueColumnNameList(dbMeta, _schema, tableMetaInfo);
    }

    /**
     * Get auto-increment column name.
     * 
     * @param tableName Table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @param conn Connection.
     * @return Auto-increment column name. (Nullable)
     * @throws SQLException
     */
    protected boolean isAutoIncrementColumn(Connection conn, String tableName, String primaryKeyColumnName)
            throws SQLException {
        return _autoIncrementHandler.isAutoIncrementColumn(conn, tableName, primaryKeyColumnName);
    }

    /**
     * Retrieves a list of foreign key columns for a given table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve FK information.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    protected Map<String, DfForeignKeyMetaInfo> getForeignKeys(DatabaseMetaData dbMeta, String tableName)
            throws SQLException {
        return _foreignKeyHandler.getForeignKeyMetaInfo(dbMeta, _schema, tableName);
    }

    /**
     * Get all the table names in the current database that are not
     * system tables.
     * 
     * @param dbMeta JDBC database metadata.
     * @return The list of all the tables in a database.
     * @throws SQLException
     */
    public List<DfTableMetaInfo> getTableNames(DatabaseMetaData dbMeta) throws SQLException {
        return _tableNameHandler.getTableNames(dbMeta, _schema);
    }

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List<DfColumnMetaInfo> getColumns(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        return _columnHandler.getColumns(dbMeta, _schema, tableName);
    }
}