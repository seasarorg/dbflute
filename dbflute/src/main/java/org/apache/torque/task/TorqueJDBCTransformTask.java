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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.torque.engine.database.model.TypeMap;
import org.apache.torque.engine.database.transform.DTDResolver;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.TorqueTaskUtil;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

/**
 * This class generates an XML schema of an existing database from JDBC metadata..
 * <p>
 * @author mkubo
 * @version $Revision$ $Date$
 */
public class TorqueJDBCTransformTask extends Task {

    public static final Log _log = LogFactory.getLog(TorqueJDBCTransformTask.class);

    public static final int IDX_COLUMN_NAME = 0;

    public static final int IDX_COLUMN_TYPE = 1;

    public static final int IDX_COLUMN_SIZE = 2;

    public static final int IDX_COLUMN_NULL_TYPE = 3;

    public static final int IDX_COLUMN_DEFAULT_VALUE = 4;

    /** Name of XML database schema produced. */
    protected String _xmlSchema;

    /** JDBC URL. */
    protected String _dbUrl;

    /** JDBC driver. */
    protected String _dbDriver;

    /** JDBC user name. */
    protected String _dbUser;

    /** JDBC password. */
    protected String _dbPassword;

    /** DB schema to use. */
    protected String _dbSchema;

    /** DOM document produced. */
    protected DocumentImpl _doc;

    /** The document root element. */
    protected Element _databaseNode;

    /** Hashtable to track what table a column belongs to. */
    protected Hashtable<String, String> _columnTableMap;

    /** Is same java name? */
    protected boolean _isSameJavaName;

    /** List for except table. */
    protected List _tableExceptList;

    /** List for target table. */
    protected List _tableTargetList;

    // ==============================================================================
    //                                                                  Getter Setter
    //                                                                  =============
    public String getDbSchema() {
        return _dbSchema;
    }

    public void setDbSchema(String dbSchema) {
        this._dbSchema = dbSchema;
    }

    public void setDbUrl(String v) {
        _dbUrl = v;
    }

    public void setDbDriver(String v) {
        _dbDriver = v;
    }

    public void setDbUser(String v) {
        _dbUser = v;
    }

    public void setDbPassword(String v) {
        _dbPassword = v;
    }

    public void setOutputFile(String v) {
        _xmlSchema = v;
    }

    public void setSameJavaName(boolean v) {
        this._isSameJavaName = v;
    }

    public boolean isSameJavaName() {
        return this._isSameJavaName;
    }

    public void setContextProperties(String file) {
        final Properties prop = TorqueTaskUtil.getBuildProperties(file, super.project);
        TorqueBuildProperties.getInstance().setContextProperties(prop);
    }

    /**
     * Get context-properties for Torque.
     * 
     * @return Context-properties.
     */
    public TorqueBuildProperties getProperties() {
        return TorqueBuildProperties.getInstance();
    }

    public List<String> getTableExceptList() {
        return getProperties().getTableExceptList();
    }

    public List getTableTargetList() {
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().listProp("torque.table.target.list", new ArrayList<Object>());
        }
        return _tableTargetList;
    }

    // ==============================================================================
    //                                                                    Main Method
    //                                                                    ===========
    /**
     * Execute task.
     * 
     * @throws BuildException
     */
    public void execute() throws BuildException {
        _log.info("------------------------------------------------------- [Torque - JDBCToXMLSchema] Start!");
        _log.info("Your DB settings are:");
        _log.info("  driver : " + _dbDriver);
        _log.info("  URL    : " + _dbUrl);
        _log.info("  user   : " + _dbUser);
        _log.info("  schema : " + _dbSchema);

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
        _log.debug("------------------------------------------------------- [Torque - JDBCToXMLSchema] Finish!");
    }

    /**
     * Generates an XML database schema from JDBC metadata.
     * <p>
     * @throws Exception a generic exception.
     */
    protected void generateXML() throws Exception {
        _log.info("...Instantiate DB-driver");
        Class.forName(_dbDriver);

        _log.info("...Getting DB-connection");
        final Connection conn = DriverManager.getConnection(_dbUrl, _dbUser, _dbPassword);

        _log.info("...Getting DB-meta-data");
        final DatabaseMetaData dbMetaData = conn.getMetaData();

        _log.info("$ /**************************************************************************");
        _log.info("$ ");
        _log.info("$ dbMetaData.toString(): " + dbMetaData.toString());
        _log.info("$ dbMetaData.getMaxRowSize(): " + dbMetaData.getMaxRowSize());
        _log.info("$ ");
        _log.info("$ /------------------------------------ ...Getting table list");

        final List tableList = getTableNames(dbMetaData);

        _log.info("$ ");
        _log.info("$ TableCount: " + tableList.size());
        _log.info("$ ---------------------- /");
        _log.info("$ ");
        _log.info("$ *************************************/");

        _databaseNode = _doc.createElement("database");
        _databaseNode.setAttribute("name", _dbSchema);

        // Build a database-wide column -> table map.
        setupColumnTableMap(dbMetaData, tableList);

        for (int i = 0; i < tableList.size(); i++) {
            final String currentTable = (String) tableList.get(i);

            _log.info("...Processing table: " + currentTable);

            final Element tableElement = _doc.createElement("table");
            tableElement.setAttribute("name", currentTable);
            if (isSameJavaName()) {
                tableElement.setAttribute("javaName", currentTable);
            }

            final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMetaData, currentTable);

            final List columns = getColumns(dbMetaData, currentTable);
            for (int j = 0; j < columns.size(); j++) {
                final List col = (List) columns.get(j);
                final String name = (String) col.get(IDX_COLUMN_NAME);
                final Integer type = ((Integer) col.get(IDX_COLUMN_TYPE));
                final int size = ((Integer) col.get(IDX_COLUMN_SIZE)).intValue();

                // Memo from DatabaseMetaData.java
                //
                // Indicates column might not allow NULL values.  Huh?
                // Might? Boy, that's a definitive answer.
                /* int columnNoNulls = 0; */
                // 
                // Indicates column definitely allows NULL values.
                /* int columnNullable = 1; */
                //
                // Indicates NULLABILITY of column is unknown.
                /* int columnNullableUnknown = 2; */

                final Integer nullType = (Integer) col.get(IDX_COLUMN_NULL_TYPE);
                String defaultValue = (String) col.get(IDX_COLUMN_DEFAULT_VALUE);

                final Element columnElement = _doc.createElement("column");
                columnElement.setAttribute("name", name);
                if (isSameJavaName()) {
                    columnElement.setAttribute("javaName", name);
                }
                columnElement.setAttribute("type", TypeMap.getTorqueType(type));

                if (size > 0
                        && (type.intValue() == Types.CHAR || type.intValue() == Types.VARCHAR
                                || type.intValue() == Types.LONGVARCHAR || type.intValue() == Types.DECIMAL || type
                                .intValue() == Types.NUMERIC)) {
                    columnElement.setAttribute("size", String.valueOf(size));
                }

                if (nullType.intValue() == 0) {
                    columnElement.setAttribute("required", "true");
                }

                if (primaryColumnNameList.contains(name)) {
                    columnElement.setAttribute("primaryKey", "true");
                }

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

                if (primaryColumnNameList.contains(name)) {
                    if (isAutoIncrementColumn(dbMetaData, currentTable, name, conn)) {
                        columnElement.setAttribute("autoIncrement", "true");
                    }
                }

                tableElement.appendChild(columnElement);
            }

            // Foreign keys for this table.
            final Collection foreignKeys = getForeignKeys(dbMetaData, currentTable);
            for (final Iterator ite = foreignKeys.iterator(); ite.hasNext();) {
                final Object[] forKey = (Object[]) ite.next();
                final String foreignKeyTable = (String) forKey[0];
                final List refs = (List) forKey[1];
                final Element foreignKeyElement = _doc.createElement("foreign-key");
                foreignKeyElement.setAttribute("foreignTable", foreignKeyTable);
                for (int m = 0; m < refs.size(); m++) {
                    final Element referenceElement = _doc.createElement("reference");
                    final String[] refData = (String[]) refs.get(m);
                    referenceElement.setAttribute("local", refData[0]);
                    referenceElement.setAttribute("foreign", refData[1]);
                    foreignKeyElement.appendChild(referenceElement);
                }
                tableElement.appendChild(foreignKeyElement);
            }

            // Unique keys for this table.
            final Map<String, Map<Integer, String>> uniqueMap = getUniqueColumnNameList(dbMetaData, currentTable);
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

    /**
     * Set up column-table map. 
     * <p>
     * @param dbMetaData JDBC metadata.
     * @param tableList A list of table-name.
     * @throws SQLException
     */
    protected void setupColumnTableMap(DatabaseMetaData dbMetaData, List tableList) throws SQLException {
        // Build a database-wide column -> table map.
        _columnTableMap = new Hashtable<String, String>();
        for (int i = 0; i < tableList.size(); i++) {
            final String curTable = (String) tableList.get(i);
            final List columns = getColumns(dbMetaData, curTable);

            for (int j = 0; j < columns.size(); j++) {
                final List col = (List) columns.get(j);
                final String name = (String) col.get(IDX_COLUMN_NAME);

                _columnTableMap.put(name, curTable);
            }
        }
    }

    /**
     * Retrieves a list of the columns composing the primary key for a given table.
     * <p>
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @return A list of the primary key parts for <code>tableName</code>.
     * @throws SQLException
     */
    protected List<String> getPrimaryColumnNameList(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        final List<String> primaryKeyColumnNameList = new ArrayList<String>();
        ResultSet parts = null;
        try {
            parts = getPrimaryKeyResultSetFromDBMeta(dbMeta, tableName);
            while (parts.next()) {
                primaryKeyColumnNameList.add(getPrimaryKeyColumnNameFromDBMeta(parts));
            }
        } finally {
            if (parts != null) {
                parts.close();
            }
        }
        return primaryKeyColumnNameList;
    }

    protected ResultSet getPrimaryKeyResultSetFromDBMeta(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        return dbMeta.getPrimaryKeys(null, _dbSchema, tableName);
    }

    protected String getPrimaryKeyColumnNameFromDBMeta(ResultSet resultSet) throws SQLException {
        return resultSet.getString(4);
    }

    // {WEBから抜粋}
    // 
    //テーブルのインデックスと統計情報の記述を取得します。 NON_UNIQUE、TYPE、INDEX_NAME、ORDINAL_POSITION の順に並べます。
    //インデックス列の記述には以下のカラムがあります。
    //
    //   1. TABLE_CAT String => テーブル カタログ (null の場合もあります)。
    //   2. TABLE_SCHEM String => テーブル スキーマ (null の場合もあります)。
    //   3. TABLE_NAME String => テーブル名。
    //   4. NON_UNIQUE boolean => 一意でないインデックスを許可するかどうか。TYPE が tableIndexStatistic の場合は false。
    //   5. INDEX_QUALIFIER String => インデックス カタログ (null の場合もあります)。TYPE が tableIndexStatistic の場合は null。
    //   6. INDEX_NAME String => インデックス名。TYPE が tableIndexStatistic の場合は null。
    //   7. TYPE short => インデックス タイプ。
    //          * tableIndexStatistic - テーブルのインデックス記述と共に返されるテーブルの統計情報を識別。
    //          * tableIndexClustered - クラスタ化されたインデックス。
    //          * tableIndexHashed - ハッシュ化されたインデックス。
    //          * tableIndexOther - ほかの形式のインデックス。 
    //   8. ORDINAL_POSITION short => インデックス内の列の連番。TYPE が tableIndexStatistic の場合は 0。
    //   9. COLUMN_NAME String => 列名。TYPE が tableIndexStatistic の場合は null。
    //  10. ASC_OR_DESC String => 列のソート順。"A" => 昇順。"D" => 降順。ソート順をサポートしていない場合は null。TYPE が tableIndexStatistic の場合は null。
    //  11. CARDINALITY int => TYPE が tableIndexStatistic の場合は、テーブル内の行数。そのほかの場合は、インデックス内の一意の値の数。
    //  12. PAGES int => TYPE が tableIndexStatistic の場合は、テーブルのページ数。そのほかの場合は、現在のインデックスのページ数。
    //  13. FILTER_CONDITION String => フィルタがある場合は、そのフィルタの状態 (null の場合もあります)。 
    //
    protected Map<String, Map<Integer, String>> getUniqueColumnNameList(DatabaseMetaData dbMeta, String tableName)
            throws SQLException {
        final List<String> primaryColumnNameList = getPrimaryColumnNameList(dbMeta, tableName);
        final Map<String, Map<Integer, String>> uniqueMap = new LinkedHashMap<String, Map<Integer, String>>();

        ResultSet parts = null;
        try {
            parts = dbMeta.getIndexInfo(null, _dbSchema, tableName, true, true);
            while (parts.next()) {
                final boolean isNonUnique;
                {
                    final String nonUnique = parts.getString(4);
                    isNonUnique = (nonUnique != null && nonUnique.equals("true") ? true : false);
                }
                if (isNonUnique) {
                    continue;
                }
                
                final String indexType;
                {
                    indexType = parts.getString(7);
                }

                final String columnName = parts.getString(9);
                if (columnName == null || columnName.trim().length() == 0) {
                    continue;
                }

                if (primaryColumnNameList.contains(columnName)) {
                    continue;
                }
                
                final String indexName = parts.getString(6);
                final Integer ordinalPosition;
                {
                    final String ordinalPositionString = parts.getString(8);
                    if (ordinalPositionString == null) {
                        String msg = "The unique columnName should have ordinal-position but null: ";
                        msg = msg + " columnName=" + columnName + " indexType=" + indexType;
                        _log.warn(msg);
                        continue;
                    }
                    try {
                        ordinalPosition = Integer.parseInt(ordinalPositionString);
                    } catch (NumberFormatException e) {
                        String msg = "The unique column should have ordinal-position as number but: ";
                        msg = msg + ordinalPositionString + " columnName=" + columnName + " indexType=" + indexType;
                        _log.warn(msg);
                        continue;
                    }
                }

                if (uniqueMap.containsKey(indexName)) {
                    final Map<Integer, String> uniqueElementMap = uniqueMap.get(indexName);
                    uniqueElementMap.put(ordinalPosition, columnName);
                } else {
                    final Map<Integer, String> uniqueElementMap = new LinkedHashMap<Integer, String>();
                    uniqueElementMap.put(ordinalPosition, columnName);
                    uniqueMap.put(indexName, uniqueElementMap);
                }
            }
        } finally {
            if (parts != null) {
                parts.close();
            }
        }
        return uniqueMap;
    }

    protected ResultSet getUniqueIndexInfoResultSetFromDBMeta(DatabaseMetaData dbMeta, String tableName)
            throws SQLException {
        return dbMeta.getIndexInfo(null, _dbSchema, tableName, true, true);
    }

    //    protected String getPrimaryKeyColumnNameFromDBMeta(ResultSet resultSet) throws SQLException {
    //        return resultSet.getString(4);
    //    }

    /**
     * Get auto-increment column name.
     * <p>
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @param primaryKeyColumnName Primary-key column-name.
     * @param conn Connection.
     * @return Auto-increment column name. (Nullable)
     * @throws SQLException
     */
    protected boolean isAutoIncrementColumn(DatabaseMetaData dbMeta, String tableName, String primaryKeyColumnName,
            Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT " + primaryKeyColumnName + " FROM " + tableName);
            final ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++) {
                final String currentColumnName = md.getColumnName(i);
                if (primaryKeyColumnName.equals(currentColumnName)) {
                    return md.isAutoIncrement(i);
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        String msg = "The primaryKeyColumnName is not found in the table: ";
        msg = msg + tableName + " - " + primaryKeyColumnName;
        throw new RuntimeException(msg);
    }

    /**
     * Retrieves a list of foreign key columns for a given table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve FK information.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    protected Collection getForeignKeys(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        Hashtable<String, Object[]> fks = new Hashtable<String, Object[]>();
        ResultSet foreignKeys = null;
        try {
            foreignKeys = dbMeta.getImportedKeys(null, _dbSchema, tableName);
            while (foreignKeys.next()) {
                String refTableName = foreignKeys.getString(3);
                String fkName = foreignKeys.getString(12);
                // if FK has no name - make it up (use tablename instead)
                if (fkName == null) {
                    fkName = refTableName;
                }
                Object[] fk = (Object[]) fks.get(fkName);
                List<String[]> refs;
                if (fk == null) {
                    fk = new Object[2];
                    fk[0] = refTableName; //referenced table name
                    refs = new ArrayList<String[]>();
                    fk[1] = refs;
                    fks.put(fkName, fk);
                } else {
                    refs = (List<String[]>) fk[1];
                }
                String[] ref = new String[2];
                ref[0] = foreignKeys.getString(8); //local column
                ref[1] = foreignKeys.getString(4); //foreign column
                refs.add(ref);
            }
        } finally {
            if (foreignKeys != null) {
                foreignKeys.close();
            }
        }
        return fks.values();
    }

    /**
     * Get all the table names in the current database that are not
     * system tables.
     * 
     * @param dbMeta JDBC database metadata.
     * @return The list of all the tables in a database.
     * @throws SQLException
     */
    public List getTableNames(DatabaseMetaData dbMeta) throws SQLException {
        // /---------------------------------------------------- [My Extension]
        // Get DatabaseTypes from ContextProperties.
        // These are the entity types we want from the database
        final String[] types = getDatabaseTypeStringArray();
        logDatabaseTypes(types);
        // -------------------/

        final List<String> tables = new ArrayList<String>();
        ResultSet resultSet = null;
        try {
            resultSet = dbMeta.getTables(null, _dbSchema, "%", types);
            while (resultSet.next()) {
                final String tableName = resultSet.getString(3);
                // final String databaseType = resultSet.getString(4);

                if (isTableExcept(tableName)) {
                    _log.debug("$ isTableExcept(" + tableName + ") == true");
                    continue;
                }
                tables.add(tableName);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return tables;
    }

    /**
     * Get database-type-string-array.
     * 
     * @return Database-type-string-array.
     */
    protected String[] getDatabaseTypeStringArray() {
        final List<Object> defaultList = new ArrayList<Object>();
        defaultList.add("TABLE");
        defaultList.add("VIEW");
        final List ls = getProperties().listProp("torque.database.type.list", defaultList);
        final String[] result = new String[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            result[i] = (String) ls.get(i);
        }
        return result;
    }

    /**
     * Log database-types. {This is a mere helper method.}
     * 
     * @param types Database-types. (NotNull)
     */
    protected void logDatabaseTypes(String[] types) {
        String typeString = "";
        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                typeString = types[i];
            } else {
                typeString = typeString + " - " + types[i];
            }
        }
        _log.info("$ DatabaseTypes are '" + typeString + "'");
    }

    /**
     * Is table out of sight?
     * 
     * @param tableName Table-name.
     * @return Determination.
     */
    public boolean isTableExcept(final String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Argument[tableName] is required.");
        }

        final List targetList = getTableTargetList();
        if (targetList == null) {
            throw new IllegalStateException("getTableTargetList() must not return null: + " + tableName);
        }

        if (!targetList.isEmpty()) {
            for (final Iterator ite = targetList.iterator(); ite.hasNext();) {
                final String targetTableHint = (String) ite.next();
                if (isHitTableHint(tableName, targetTableHint)) {
                    return false;
                }
            }
            return true;
        }

        final List exceptList = getTableExceptList();
        if (exceptList == null) {
            throw new IllegalStateException("getTableExceptList() must not return null: + " + tableName);
        }

        for (final Iterator ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (isHitTableHint(tableName, tableHint)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isHitTableHint(String tableName, String tableHint) {
        // TODO: I want to refactor this judgement logic for hint someday.
        final String prefixMark = "prefix:";
        final String suffixMark = "suffix:";

        if (tableHint.toLowerCase().startsWith(prefixMark.toLowerCase())) {
            final String pureTableHint = tableHint.substring(prefixMark.length(), tableHint.length());
            if (tableName.toLowerCase().startsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else if (tableHint.toLowerCase().startsWith(suffixMark.toLowerCase())) {
            final String pureTableHint = tableHint.substring(suffixMark.length(), tableHint.length());
            if (tableName.toLowerCase().endsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else {
            if (tableName.equalsIgnoreCase(tableHint)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.  It returns a List of Lists.  Each element
     * of the returned List is a List with:
     *
     * element 0 => a String object for the column name.
     * element 1 => an Integer object for the column type.
     * element 2 => size of the column.
     * element 3 => null type.
     * 
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     * @return The list of columns in <code>tableName</code>.
     * @throws SQLException
     */
    public List getColumns(DatabaseMetaData dbMeta, String tableName) throws SQLException {
        final List<List<Object>> columns = new ArrayList<List<Object>>();
        ResultSet columnResultSet = null;
        try {
            columnResultSet = dbMeta.getColumns(null, _dbSchema, tableName, null);
            while (columnResultSet.next()) {
                final String name = columnResultSet.getString(4);
                final Integer sqlType = new Integer(columnResultSet.getString(5));
                final Integer size = new Integer(columnResultSet.getInt(7));
                final Integer nullType = new Integer(columnResultSet.getInt(11));
                final String defValue = columnResultSet.getString(13);

                final List<Object> col = new ArrayList<Object>(5);
                col.add(name);
                col.add(sqlType);
                col.add(size);
                col.add(nullType);
                col.add(defValue);
                columns.add(col);
            }
        } finally {
            if (columnResultSet != null) {
                columnResultSet.close();
            }
        }
        return columns;
    }

}