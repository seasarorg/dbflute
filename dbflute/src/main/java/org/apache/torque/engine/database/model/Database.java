package org.apache.torque.engine.database.model;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.texen.util.FileUtil;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.DfComponentProvider;
import org.seasar.dbflute.config.DfDatabaseConfig;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties.CommonColumnSetupResource;
import org.seasar.dbflute.task.DfSql2EntityTask.DfParameterBeanMetaData;
import org.seasar.dbflute.torque.DfAdditionalForeignKeyInitializer;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.xml.sax.Attributes;

/**
 * A class for holding application data structures.
 * 
 * @author Modified by jflute
 */
public class Database {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(Database.class);

    protected String _databaseType;

    protected List<Table> _tableList = new ArrayList<Table>(100);

    protected String _name;

    protected String _pkg;

    protected String _defaultIdMethod;

    protected String _defaultJavaType;

    protected String _defaultJavaNamingMethod;

    protected AppData _dbParent;

    protected Hashtable<String, Table> _tablesByName = new Hashtable<String, Table>();

    protected Hashtable<String, Table> _tablesByJavaName = new Hashtable<String, Table>();

    protected boolean _isHeavyIndexing;

    /** The meta data of parameter bean. */
    protected Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    /**
     * Load the database object from an xml tag.
     *
     * @param attrib the xml attributes
     */
    public void loadFromXML(Attributes attrib) {
        setName(attrib.getValue("name"));
        _pkg = attrib.getValue("package");
        _defaultJavaType = attrib.getValue("defaultJavaType");
        _defaultIdMethod = attrib.getValue("defaultIdMethod");
        _defaultJavaNamingMethod = attrib.getValue("defaultJavaNamingMethod");
        if (_defaultJavaNamingMethod == null) {
            _defaultJavaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE;
        }
        _isHeavyIndexing = "true".equals(attrib.getValue("heavyIndexing"));

    }

    /**
     * Get the name of the Database.
     *
     * @return Name of the Database.
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the name of the Database.
     *
     * @param name Name of the Database.
     */
    public void setName(String name) {
        this._name = (name == null ? "default" : name);
    }

    /**
     * Get the value of package.
     * @return value of package.
     */
    public String getPackage() {
        return _pkg;
    }

    /**
     * Set the value of package.
     * @param v  Value to assign to package.
     */
    public void setPackage(String v) {
        this._pkg = v;
    }

    /**
     * Get the value of defaultIdMethod.
     * @return value of defaultIdMethod.
     */
    public String getDefaultIdMethod() {
        return _defaultIdMethod;
    }

    /**
     * Set the value of defaultIdMethod.
     * @param v Value to assign to defaultIdMethod.
     */
    public void setDefaultIdMethod(String v) {
        this._defaultIdMethod = v;
    }

    /**
     * Get type to use in Java sources (primitive || object)
     *
     * @return the type to use
     */
    public String getDefaultJavaType() {
        return _defaultJavaType;
    }

    /**
     * Get the value of defaultJavaNamingMethod which specifies the
     * method for converting schema names for table and column to Java names.
     *
     * @return The default naming conversion used by this database.
     */
    public String getDefaultJavaNamingMethod() {
        return _defaultJavaNamingMethod;
    }

    /**
     * Set the value of defaultJavaNamingMethod.
     * @param v The default naming conversion for this database to use.
     */
    public void setDefaultJavaNamingMethod(String v) {
        this._defaultJavaNamingMethod = v;
    }

    /**
     * Get the value of heavyIndexing.
     * @return value of heavyIndexing.
     */
    public boolean isHeavyIndexing() {
        return _isHeavyIndexing;
    }

    /**
     * Set the value of heavyIndexing.
     * @param v  Value to assign to heavyIndexing.
     */
    public void setHeavyIndexing(boolean v) {
        this._isHeavyIndexing = v;
    }

    public void setAppData(AppData parent) {
        _dbParent = parent;
    }

    public AppData getAppData() {
        return _dbParent;
    }

    public String getDatabaseType() {
        return _databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this._databaseType = databaseType;
    }

    public Map<String, DfParameterBeanMetaData> getPmbMetaDataMap() {
        return _pmbMetaDataMap;
    }

    public void setPmbMetaDataMap(Map<String, DfParameterBeanMetaData> pmbMetaDataMap) {
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===============================================================================
    //                                                                           Table
    //                                                                           =====
    /**
     * Return an array of all tables
     *
     * @return array of all tables
     */
    public Table[] getTables() {
        int size = _tableList.size();
        Table[] tbls = new Table[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Table) _tableList.get(i);
        }
        return tbls;
    }

    /**
     * Return an array of all tables
     *
     * @return array of all tables
     */
    public List<Table> getTableList() {
        final List<Table> ls = new ArrayList<Table>();
        final Table[] tables = getTables();
        for (Table table : tables) {
            ls.add(table);
        }
        return ls;
    }

    /**
     * Return the table with the specified name.
     *
     * @param name table name
     * @return A Table object.  If it does not exist it returns null
     */
    public Table getTable(String name) {
        return (Table) _tablesByName.get(name);
    }

    /**
     * Return the table with the specified javaName.
     *
     * @param javaName name of the java object representing the table
     * @return A Table object.  If it does not exist it returns null
     */
    public Table getTableByJavaName(String javaName) {
        return (Table) _tablesByJavaName.get(javaName);
    }

    public Table getTableByFlexibleName(String flexibleName) {
        final DfFlexibleNameMap<String, Table> flexibleNameMap = new DfFlexibleNameMap<String, Table>(_tablesByName);
        return flexibleNameMap.get(flexibleName);
    }

    /**
     * An utility method to add a new table from an xml attribute.
     *
     * @param attrib the xml attributes
     * @return the created Table
     */
    public Table addTable(Attributes attrib) {
        Table tbl = new Table();
        tbl.setDatabase(this);
        tbl.loadFromXML(attrib, this.getDefaultIdMethod());
        addTable(tbl);
        return tbl;
    }

    /**
     * Add a table to the list and sets the Database property to this Database
     *
     * @param tbl the table to add
     */
    public void addTable(Table tbl) {
        tbl.setDatabase(this);
        _tableList.add(tbl);
        _tablesByName.put(tbl.getName(), tbl);
        _tablesByJavaName.put(tbl.getJavaName(), tbl);
        tbl.setPackage(getPackage());
    }

    /**
     * Returns the value of the named property from this database's
     * <code>db.props</code> file.
     *
     * @param name The name of the property to retrieve the value of.
     * @return The value of the specified property.
     * @exception EngineException Couldn't access properties.
     */
    protected String getProperty(String name) throws EngineException {
        Properties p = getAppData().getIdiosyncrasies(_databaseType);
        return (p == null ? null : p.getProperty(name));
    }

    /**
     * Determines if this database will be using the
     * <code>IDMethod.ID_BROKER</code> to create ids for torque OM
     * objects.
     * @return true if there is at least one table in this database that
     * uses the <code>IDMethod.ID_BROKER</code> method of generating
     * ids. returns false otherwise.
     */
    public boolean requiresIdTable() {
        Table table[] = getTables();
        for (int i = 0; i < table.length; i++) {
            if (table[i].getIdMethod().equals(IDMethod.ID_BROKER)) {
                return true;
            }
        }
        return false;
    }

    public void doFinalInitialization() throws EngineException {
        Table[] tables = getTables();
        for (int i = 0; i < tables.length; i++) {
            Table currTable = tables[i];

            // check schema integrity
            // if idMethod="autoincrement", make sure a column is
            // specified as autoIncrement="true"
            // FIXME: Handle idMethod="native" via DB adapter.
            if (currTable.getIdMethod().equals("autoincrement")) {
                Column[] columns = currTable.getColumns();
                boolean foundOne = false;
                for (int j = 0; j < columns.length && !foundOne; j++) {
                    foundOne = columns[j].isAutoIncrement();
                }

                if (!foundOne) {
                    String errorMessage = "Table '" + currTable.getName()
                            + "' is marked as autoincrement, but it does not "
                            + "have a column which declared as the one to "
                            + "auto increment (i.e. autoIncrement=\"true\")\n";
                    throw new EngineException("Error in XML schema: " + errorMessage);
                }
            }

            currTable.doFinalInitialization();

            // setup reverse fk relations
            ForeignKey[] fks = currTable.getForeignKeys();
            for (int j = 0; j < fks.length; j++) {
                ForeignKey currFK = fks[j];
                Table foreignTable = getTableByFlexibleName(currFK.getForeignTableName());
                if (foreignTable == null) {
                    throw new EngineException("Attempt to set foreign" + " key to nonexistent table, "
                            + currFK.getForeignTableName());
                } else {
                    final List<ForeignKey> refererList = foreignTable.getRefererList();
                    if ((refererList == null || !refererList.contains(currFK))) {
                        foreignTable.addReferrer(currFK);
                    }

                    // local column references
                    Iterator localColumnNames = currFK.getLocalColumns().iterator();
                    while (localColumnNames.hasNext()) {
                        Column local = currTable.getColumn((String) localColumnNames.next());
                        // give notice of a schema inconsistency.
                        // note we do not prevent the npe as there is nothing
                        // that we can do, if it is to occur.
                        if (local == null) {
                            throw new EngineException("Attempt to define foreign"
                                    + " key with nonexistent column in table, " + currTable.getName());
                        } else {
                            //check for foreign pk's
                            if (local.isPrimaryKey()) {
                                currTable.setContainsForeignPK(true);
                            }
                        }
                    }

                    // foreign column references
                    Iterator foreignColumnNames = currFK.getForeignColumns().iterator();
                    while (foreignColumnNames.hasNext()) {
                        String foreignColumnName = (String) foreignColumnNames.next();
                        Column foreign = foreignTable.getColumn(foreignColumnName);
                        // if the foreign column does not exist, we may have an
                        // external reference or a misspelling
                        if (foreign == null) {
                            throw new EngineException("Attempt to set foreign" + " key to nonexistent column: table="
                                    + currTable.getName() + ", foreign column=" + foreignColumnName);
                        } else {
                            foreign.addReferrer(currFK);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creats a string representation of this Database.
     * The representation is given in xml format.
     *
     * @return string representation in xml
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("<database name=\"").append(getName()).append('"').append(" package=\"").append(getPackage())
                .append('"').append(" defaultIdMethod=\"").append(getDefaultIdMethod()).append('"').append(">\n");

        for (Iterator i = _tableList.iterator(); i.hasNext();) {
            result.append(i.next());
        }

        result.append("</database>");
        return result.toString();
    }

    // ===============================================================================
    //                                                                  Parameter Bean
    //                                                                  ==============
    public boolean isExistPmbMetaData() {
        return _pmbMetaDataMap != null && !_pmbMetaDataMap.isEmpty();
    }

    public Collection<DfParameterBeanMetaData> getPmbMetaDataList() {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty.";
            throw new IllegalStateException(msg);
        }
        return _pmbMetaDataMap.values();
    }

    public String getPmbMetaDataSuperClassDefinition(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        final String superClassName = metaData.getSuperClassName();
        if (superClassName == null || superClassName.trim().length() == 0) {
            return "";
        }
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return languageDependencyInfo.getGrammarInfo().getExtendsStringMark() + " " + superClassName + " ";
    }

    public Map<String, String> getPmbMetaDataPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameTypeMap();
    }

    public Map<String, String> getPmbMetaDataPropertyNameOptionMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameOptionMap();
    }

    private DfParameterBeanMetaData findPmbMetaData(String className) {
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            String msg = "The className has no meta data: className=" + className;
            throw new IllegalStateException(msg);
        }
        return metaData;
    }

    public Set<String> getPmbMetaDataPropertySet(String className) {
        assertArgumentPmbMetaDataClassName(className);
        return getPmbMetaDataPropertyNameTypeMap(className).keySet();
    }

    public String getPmbMetaDataPropertyType(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPmbMetaDataPropertyNameTypeMap(className).get(propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionLikeSearch(String className, String propertyName) {
        final String pmbMetaDataPropertyOption = getPmbMetaDataPropertyOption(className, propertyName);
        return pmbMetaDataPropertyOption != null && pmbMetaDataPropertyOption.trim().equalsIgnoreCase("like");
    }

    protected String getPmbMetaDataPropertyOption(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        final Map<String, String> map = getPmbMetaDataPropertyNameOptionMap(className);
        return map != null ? map.get(propertyName) : null;
    }

    protected void assertArgumentPmbMetaDataClassName(String className) {
        if (className == null || className.trim().length() == 0) {
            String msg = "The className should not be null or empty: [" + className + "]";
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertArgumentPmbMetaDataPropertyName(String propertyName) {
        if (propertyName == null || propertyName.trim().length() == 0) {
            String msg = "The propertyName should not be null or empty: [" + propertyName + "]";
            throw new IllegalArgumentException(msg);
        }
    }

    // ===============================================================================
    //                                                                    CustomizeDao
    //                                                                    ============
    public void initializeCustomizeDao() {
        // Not support so do nothing!
    }

    public java.util.List<Table> getCustomizeTableList() {
        return new ArrayList<Table>(100);
    }

    // ===============================================================================
    //                                                            AdditionalForeignKey
    //                                                            ====================
    public void initializeAdditionalForeignKey() {
        final DfAdditionalForeignKeyInitializer initializer = new DfAdditionalForeignKeyInitializer(this);
        initializer.initializeAdditionalForeignKey();
    }

    public void initializeClassificationDeployment() {
        getClassificationProperties().initializeClassificationDeploymentMap(getTableList());
        getClassificationProperties().initializeClassificationDeploymentMap(getCustomizeTableList());
    }

    // ===============================================================================
    //                                                                    IncludeQuery
    //                                                                    ============
    public void initializeIncludeQuery() {
        _log.debug("/=============================");
        _log.debug("...Initializing customize dao.");
        final Map<String, Map<String, Map<String, List<String>>>> map = getProperties().getIncludeQueryProperties()
                .getIncludeQueryMap();
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            _log.debug(key);
            final Map<String, Map<String, List<String>>> queryElementMap = map.get(key);
            final Set<String> queryElementKeySet = queryElementMap.keySet();
            for (String queryElementKey : queryElementKeySet) {
                _log.debug("    " + queryElementKey);
                final Map<String, List<String>> tableElementMap = queryElementMap.get(queryElementKey);
                final Set<String> tableElementKeySet = tableElementMap.keySet();
                for (String tableName : tableElementKeySet) {
                    _log.debug("        " + tableName);
                    final Table targetTable = getTableByFlexibleName(tableName);
                    if (targetTable == null) {
                        String msg = "The table[" + tableName + "] of includeQueryMap was not found: " + map;
                        throw new IllegalStateException(msg);
                    }
                    final List<String> columnNameList = tableElementMap.get(tableName);
                    for (String columnName : columnNameList) {
                        _log.debug("            " + columnName);
                        final Column targetColumn = targetTable.getColumnByFlexibleName(columnName);
                        if (targetColumn == null) {
                            String msg = "The column[" + targetColumn
                                    + "] of includeQueryMap was not found in the table[" + tableName + "]";
                            throw new IllegalStateException(msg);
                        }
                    }
                }
            }
        }
        _log.debug("========/");
    }

    // ===============================================================================
    //                                                                      Properties
    //                                                                      ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    // ===============================================================================
    //                                                                Basic Properties
    //                                                                ================
    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    // -----------------------------------------------------
    //                                               JavaDir
    //                                               -------
    public String getJavaDir() {
        return getBasicProperties().getJavaDir();
    }

    // -----------------------------------------------------
    //                                              Database
    //                                              --------
    public boolean isDatabaseOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    // -----------------------------------------------------
    //                                              Language
    //                                              --------
    public String getTargetLanguage() {
        return getBasicProperties().getTargetLanguage();
    }

    public String getResourceDirectory() {
        return getBasicProperties().getResourceDirectory();
    }

    public String getTargetLanguageInitCap() {
        final String targetLanguage = getBasicProperties().getTargetLanguage();
        return targetLanguage.substring(0, 1).toUpperCase() + targetLanguage.substring(1);
    }

    public boolean isTargetLanguageJava() {
        return getBasicProperties().isTargetLanguageJava();
    }

    public boolean isTargetLanguageCSharp() {
        return getBasicProperties().isTargetLanguageCSharp();
    }

    public boolean isTargetLanguageCSharpOld() {
        return getBasicProperties().isTargetLanguageCSharpOld();
    }

    public boolean isJavaVersionGreaterEqualTiger() {
        return getBasicProperties().isJavaVersionGreaterEqualTiger();
    }

    public boolean isJavaVersionGreaterEqualMustang() {
        return getBasicProperties().isJavaVersionGreaterEqualMustang();
    }

    // -----------------------------------------------------
    //                                             Extension
    //                                             ---------
    public String getTemplateFileExtension() {
        return getBasicProperties().getTemplateFileExtension();
    }

    public String getClassFileExtension() {
        return getBasicProperties().getClassFileExtension();
    }

    // -----------------------------------------------------
    //                                              Encoding
    //                                              --------
    public String getTemplateFileEncoding() {
        return getBasicProperties().getTemplateFileEncoding();
    }

    // -----------------------------------------------------
    //                                                Author
    //                                                ------
    public String getClassAuthor() {
        return getBasicProperties().getClassAuthor();
    }

    // -----------------------------------------------------
    //                                             Copyright
    //                                             ---------
    public String getAllClassCopyright() {
        return getBasicProperties().getAllClassCopyright();
    }

    // -----------------------------------------------------
    //                                                Naming
    //                                                ------
    public boolean isJavaNameOfTableSameAsDbName() {
        return getBasicProperties().isJavaNameOfTableSameAsDbName();
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return getBasicProperties().isJavaNameOfColumnSameAsDbName();
    }

    // -----------------------------------------------------
    //                                             Available
    //                                             ---------
    public boolean isAvailableBehaviorGeneration() {
        return getBasicProperties().isAvailableBehaviorGeneration();
    }

    public boolean isAvailableGenerics() {
        return getBasicProperties().isAvailableGenerics();
    }

    public String filterGenericsString(String genericsString) {// It is very important!
        return getBasicProperties().filterGenericsString(genericsString);
    }

    public String filterGenericsDowncast(String genericsDowncast) {// It is very important!
        return getBasicProperties().filterGenericsDowncast(genericsDowncast);
    }

    public String filterGenericsParamOutput(String variableName, String description) {
        return getBasicProperties().filterGenericsParamOutput(variableName, description);
    }

    public String filterGenericsGeneralOutput(String genericsGeneralOutput) {// It is very important!
        return getBasicProperties().filterGenericsGeneralOutput(genericsGeneralOutput);
    }

    public String outputOverrideAnnotation() {
        return getBasicProperties().outputOverrideAnnotation();
    }

    public String outputOverrideAnnotationAfterNewLineOutput() {
        return getBasicProperties().outputOverrideAnnotationAfterNewLineOutput();
    }

    public String outputSuppressWarningsAfterLineSeparator() {
        return getBasicProperties().outputSuppressWarningsAfterLineSeparator();
    }

    // ===============================================================================
    //                                                             Properties - Prefix
    //                                                             ===================
    public String getProjectPrefix() {
        return getBasicProperties().getProjectPrefix();
    }

    public String getBasePrefix() {
        return "Bs";
    }

    public String getBaseSuffixForEntity() {
        return "";
    }

    // ===============================================================================
    //                                                          Properties - HotDeploy
    //                                                          ======================
    public boolean isAvailableHotDeploy() {
        return getBasicProperties().isAvailableHotDeploy();
    }

    // ===============================================================================
    //                                                       Properties - DBFluteDicon
    //                                                       =========================
    public String getDaoDiconNamespace() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconNamespace();
    }

    public String getDaoDiconPackageName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconPackageName();
    }

    public String getDBFluteDiconPackageName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconPackageName();
    }

    public List<String> getDBFluteDiconPackageNameList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconPackageNameList();
    }

    public List<String> getDaoDiconPackageNameList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconPackageNameList();
    }

    public String getDBFluteCreatorDiconFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteCreatorDiconFileName();
    }

    public String getDBFluteCustomizerDiconFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteCustomizerDiconFileName();
    }

    public String getDBFluteDiconFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconFileName();
    }

    public String getDaoDiconFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconFileName();
    }

    public String getJdbcDiconResourceName() {
        return getProperties().getDBFluteDiconProperties().getJdbcDiconResourceName();
    }

    public String getRequiredTxComponentName() {
        return getProperties().getDBFluteDiconProperties().getRequiredTxComponentName();
    }

    public String getRequiresNewTxComponentName() {
        return getProperties().getDBFluteDiconProperties().getRequiresNewTxComponentName();
    }

    public List<String> getDBFluteDiconBeforeJ2eeIncludePathList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconBeforeJ2eeIncludePathList();
    }

    public List<String> getDaoDiconBeforeJ2eeIncludePathList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconBeforeJ2eeIncludePathList();
    }

    public List<String> getDBFluteDiconOtherIncludePathList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconOtherIncludePathList();
    }

    public List<String> getDaoDiconOtherIncludePathList() {
        return getProperties().getDBFluteDiconProperties().getDBFluteDiconOtherIncludePathList();
    }

    public Map<String, Map<String, String>> getOriginalDaoComponentMap() {
        return getProperties().getDBFluteDiconProperties().getOriginalDBFluteComponentMap();
    }

    public List<String> getOriginalDaoComponentComponentNameList() {
        return getProperties().getDBFluteDiconProperties().getOriginalDBFluteComponentComponentNameList();
    }

    public String getOriginalDaoComponentClassName(String componentName) {
        return getProperties().getDBFluteDiconProperties().getOriginalDBFluteComponentClassName(componentName);
    }

    public boolean isDaoComponent(String componentName) {
        return getProperties().getDBFluteDiconProperties().isDBFluteComponent(componentName);
    }

    public boolean isAvailableBehaviorRequiresNewTx() {
        return getProperties().getDBFluteDiconProperties().isAvailableBehaviorRequiresNewTx();
    }

    public boolean isAvailableBehaviorRequiredTx() {
        return getProperties().getDBFluteDiconProperties().isAvailableBehaviorRequiredTx();
    }

    // ===============================================================================
    //                                            Properties - Generated Class Package
    //                                            ====================================
    public String getPackageBase() {
        return getProperties().getGeneratedClassPackageProperties().getPackageBase();
    }

    public String getBaseCommonPackage() {
        return getProperties().getGeneratedClassPackageProperties().getBaseCommonPackage();
    }

    public String getBaseBehaviorPackage() {
        return getProperties().getGeneratedClassPackageProperties().getBaseBehaviorPackage();
    }

    public String getBaseDaoPackage() {
        return getProperties().getGeneratedClassPackageProperties().getBaseDaoPackage();
    }

    public String getBaseEntityPackage() {
        return getProperties().getGeneratedClassPackageProperties().getBaseEntityPackage();
    }

    public String getConditionBeanPackage() {
        return getProperties().getGeneratedClassPackageProperties().getConditionBeanPackage();
    }

    public String getExtendedBehaviorPackage() {
        return getProperties().getGeneratedClassPackageProperties().getExtendedBehaviorPackage();
    }

    public String getExtendedDaoPackage() {
        return getProperties().getGeneratedClassPackageProperties().getExtendedDaoPackage();
    }

    public String getExtendedEntityPackage() {
        return getProperties().getGeneratedClassPackageProperties().getExtendedEntityPackage();
    }

    // ===============================================================================
    //                                              Properties - Sequence and Identity
    //                                              ==================================
    public String getSequenceDefinitionMapSequence(String flexibleTableName) {
        return getProperties().getSequenceIdentityProperties().getSequenceDefinitionMapSequence(flexibleTableName);
    }

    public boolean isAvailableBehaviorInsertSequenceInjection() {
        return getProperties().getSequenceIdentityProperties().isAvailableBehaviorInsertSequenceInjection();
    }

    public String getSequenceReturnType() {
        return getProperties().getSequenceIdentityProperties().getSequenceReturnType();
    }

    public String getIdentityDefinitionMapColumnName(String flexibleTableName) {
        return getProperties().getSequenceIdentityProperties().getIdentityDefinitionMapColumnName(flexibleTableName);
    }

    // ===============================================================================
    //                                                    Properties - Optimistic Lock
    //                                                    ============================
    public String getUpdateDateFieldName() {
        return getProperties().getOptimisticLockProperties().getUpdateDateFieldName();
    }

    public boolean isUpdateDateExceptTable(final String tableName) {
        return getProperties().getOptimisticLockProperties().isUpdateDateExceptTable(tableName);
    }

    public String getVersionNoFieldName() {
        return getProperties().getOptimisticLockProperties().getVersionNoFieldName();
    }

    // ===============================================================================
    //                                                      Properties - Common Column
    //                                                      ==========================
    public Map<String, Object> getCommonColumnMap() {
        return getProperties().getCommonColumnProperties().getCommonColumnMap();
    }

    public List<String> getCommonColumnNameList() {
        return getProperties().getCommonColumnProperties().getCommonColumnNameList();
    }

    public List<String> getCommonColumnNameConvertionList() {
        return getProperties().getCommonColumnProperties().getCommonColumnNameConvertionList();
    }

    public boolean isCommonColumnConvertion(String commonColumnName) {
        return getProperties().getCommonColumnProperties().isCommonColumnConvertion(commonColumnName);
    }

    public String filterCommonColumn(String commonColumnName) {
        return getProperties().getCommonColumnProperties().filterCommonColumn(commonColumnName);
    }

    public boolean hasCommonColumn() {
        return !getProperties().getCommonColumnProperties().getCommonColumnNameList().isEmpty();
    }

    /**
     * @deprecated
     * @return Determination.
     */
    public boolean isAvailableCommonColumnSetupInterceptorToBehavior() {
        return getProperties().getCommonColumnProperties().isAvailableCommonColumnSetupInterceptorToBehavior();
    }

    /**
     * @deprecated
     * @return Determination.
     */
    public boolean isAvailableCommonColumnSetupInterceptorToDao() {
        return getProperties().getCommonColumnProperties().isAvailableCommonColumnSetupInterceptorToDao();
    }

    public boolean hasCommonColumnConvertion(String commonColumnName) {
        return getProperties().getCommonColumnProperties().isCommonColumnConvertion(commonColumnName);
    }

    // --------------------------------------
    //                                 insert
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeInsertInterceptorLogicMap() {
        return getProperties().getCommonColumnProperties().getCommonColumnSetupBeforeInsertInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeInsertInterceptorLogicMap(String columnName) {
        return getProperties().getCommonColumnProperties()
                .containsValidColumnNameKeyCommonColumnSetupBeforeInsertInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeInsertInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnProperties()
                .getCommonColumnSetupBeforeInsertInterceptorLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                                 update
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeUpdateInterceptorLogicMap() {
        return getProperties().getCommonColumnProperties().getCommonColumnSetupBeforeUpdateInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeUpdateInterceptorLogicMap(String columnName) {
        return getProperties().getCommonColumnProperties()
                .containsValidColumnNameKeyCommonColumnSetupBeforeUpdateInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeUpdateInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnProperties()
                .getCommonColumnSetupBeforeUpdateInterceptorLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                                 delete
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeDeleteInterceptorLogicMap() {
        return getProperties().getCommonColumnProperties().getCommonColumnSetupBeforeDeleteInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeDeleteInterceptorLogicMap(String columnName) {
        return getProperties().getCommonColumnProperties()
                .containsValidColumnNameKeyCommonColumnSetupBeforeDeleteInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeDeleteInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnProperties()
                .getCommonColumnSetupBeforeDeleteInterceptorLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                               resource
    //                               --------
    public boolean hasCommonColumnSetupResource() {
        return getProperties().getCommonColumnProperties().hasCommonColumnSetupResource();
    }

    public List<CommonColumnSetupResource> getCommonColumnSetupResourceList() {
        return getProperties().getCommonColumnProperties().getCommonColumnSetupResourceList();
    }

    // ===============================================================================
    //                                                     Properties - Classification
    //                                                     ===========================
    public DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }

    // --------------------------------------
    //                             Definition
    //                             ----------
    public String getClassificationTopCodeVariableNamePrefix() {
        return getClassificationProperties().getClassificationTopCodeVariableNamePrefix();
    }

    public String getClassificationCodeVariableNamePrefix() {
        return getClassificationProperties().getClassificationCodeVariableNamePrefix();
    }

    public Map<String, Map<String, String>> getClassificationTopDefinitionMap() {
        return getClassificationProperties().getClassificationTopDefinitionMap();
    }

    public boolean hasClassificationDefinitionMap() {
        return getClassificationProperties().hasClassificationDefinitionMap();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        return getClassificationProperties().getClassificationDefinitionMap();
    }

    public List<String> getClassificationNameList() {
        return getClassificationProperties().getClassificationNameList();
    }

    public List<String> getClassificationNameListValidNameOnly() {
        return getClassificationProperties().getClassificationNameListValidNameOnly();
    }

    public List<String> getClassificationNameListValidAliasOnly() {
        return getClassificationProperties().getClassificationNameListValidAliasOnly();
    }

    public String getClassificationDefinitionMapAsStringRemovedLineSeparatorFilteredQuotation() {
        return getClassificationProperties()
                .getClassificationDefinitionMapAsStringRemovedLineSeparatorFilteredQuotation();
    }

    public List<java.util.Map<String, String>> getClassificationMapList(String classificationName) {
        return getClassificationProperties().getClassificationMapList(classificationName);
    }

    // --------------------------------------
    //                             Deployment
    //                             ----------
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        return getClassificationProperties().getClassificationDeploymentMap();
    }

    public String getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation() {
        return getClassificationProperties()
                .getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation();
    }

    public boolean hasClassification(String tableName, String columnName) {
        return getClassificationProperties().hasClassification(tableName, columnName);
    }

    public String getClassificationName(String tableName, String columnName) {
        return getClassificationProperties().getClassificationName(tableName, columnName);
    }

    public boolean hasClassificationName(String tableName, String columnName) {
        return getClassificationProperties().hasClassificationName(tableName, columnName);
    }

    public boolean hasClassificationAlias(String tableName, String columnName) {
        return getClassificationProperties().hasClassificationAlias(tableName, columnName);
    }

    public Map<String, String> getAllColumnClassificationMap() {
        return getClassificationProperties().getAllColumnClassificationMap();
    }

    public boolean isAllClassificationColumn(String columnName) {
        if (columnName == null) {
            String msg = "The argument[columnName] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        return getClassificationProperties().isAllClassificationColumn(columnName);
    }

    public String getAllClassificationName(String columnName) {
        return getClassificationProperties().getAllClassificationName(columnName);
    }

    // ===============================================================================
    //                                                       Properties - Select Param
    //                                                       =========================
    public DfSelectParamProperties getSelectParamProperties() {
        return getProperties().getSelectParamProperties();
    }

    public String getSelectQueryTimeout() {
        return getSelectParamProperties().getSelectQueryTimeout();
    }

    public boolean isSelectQueryTimeoutValid() {
        return getSelectParamProperties().isSelectQueryTimeoutValid();
    }

    public String getStatementResultSetType() {
        return getSelectParamProperties().getStatementResultSetType();
    }

    public String getStatementResultSetConcurrency() {
        return getSelectParamProperties().getStatementResultSetConcurrency();
    }

    public boolean isStatementResultSetTypeValid() {
        return getSelectParamProperties().isStatementResultSetTypeValid();
    }

    // ===============================================================================
    //                                             Properties - OriginalBehaviorAspect
    //                                             ===================================
    public Map<String, Map<String, String>> getOriginalBehaviorAspectMap() {
        return getProperties().getOriginalBehaviorAspectMap();
    }

    public List<String> getOriginalBehaviorAspectComponentNameList() {
        return getProperties().getOriginalBehaviorAspectComponentNameList();
    }

    public String getOriginalBehaviorAspectClassName(String componentName) {
        return getProperties().getOriginalBehaviorAspectClassName(componentName);
    }

    public String getOriginalBehaviorAspectPointcut(String componentName) {
        return getProperties().getOriginalBehaviorAspectPointcut(componentName);
    }

    // -----------------------------------------------------
    //                                         Making Option
    //                                         -------------
    public boolean isMakeDeprecated() {
        return getProperties().getSourceReductionProperties().isMakeDeprecated();
    }

    public boolean isMakeRecentlyDeprecated() {
        return getProperties().getSourceReductionProperties().isMakeRecentlyDeprecated();
    }

    public boolean isMakeConditionQueryEqualEmptyString() {
        return getProperties().getSourceReductionProperties().isMakeConditionQueryEqualEmptyString();
    }

    public boolean isMakeBehaviorCopyInsert() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorCopyInsert();
    }

    public boolean isMakeBehaviorLoopUpdate() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorLoopUpdate();
    }

    public boolean isMakeTraceablePreparedStatement() {
        return getProperties().getSourceReductionProperties().isMakeTraceablePreparedStatement();
    }

    /**
     * @return Determination.
     * @deprecated
     */
    public boolean isMakeConditionQueryNumericArgumentLong() {
        return getProperties().getSourceReductionProperties().isMakeConditionQueryNumericArgumentLong();
    }

    /**
     * @return Determination.
     * @deprecated
     */
    public boolean isMakeBehaviorForUpdate() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorForUpdate();
    }

    // -----------------------------------------------------
    //                                      S2Dao Adjustment
    //                                      ----------------
    public boolean isVersionAfter1047() {
        return getProperties().getS2DaoAdjustmentProperties().isVersionAfter1047();
    }

    public boolean isVersionAfter1046() {
        return getProperties().getS2DaoAdjustmentProperties().isVersionAfter1046();
    }

    public boolean isVersionAfter1043() {
        return getProperties().getS2DaoAdjustmentProperties().isVersionAfter1043();
    }

    public boolean isVersionAfter1040() {
        return getProperties().getS2DaoAdjustmentProperties().isVersionAfter1040();
    }

    public boolean isAvailableDaoMethodLazyInitializing() {
        return getProperties().getS2DaoAdjustmentProperties().isAvailableDaoMethodLazyInitializing();
    }

    public boolean isAvailableDaoMethodMetaDataInitializing() {
        return getProperties().getS2DaoAdjustmentProperties().isAvailableDaoMethodMetaDataInitializing();
    }

    public boolean isAvailableOtherConnectionDaoInitialization() {
        return getProperties().getS2DaoAdjustmentProperties().isAvailableOtherConnectionDaoInitialization();
    }

    public boolean isAvailableChildNoAnnotationGenerating() {
        return getProperties().getS2DaoAdjustmentProperties().isAvailableChildNoAnnotationGenerating();
    }

    public String getExtendedAnnotationReaderFactoryClassName() {
        return getProperties().getS2DaoAdjustmentProperties().getExtendedAnnotationReaderFactoryClassName();
    }

    public String getExtendedDaoMetaDataFactoryImplClassName() {
        return getProperties().getS2DaoAdjustmentProperties().getExtendedDaoMetaDataFactoryImplClassName();
    }

    public boolean hasDaoSqlFileEncoding() {
        return getProperties().getS2DaoAdjustmentProperties().hasDaoSqlFileEncoding();
    }

    public String getDaoSqlFileEncoding() {
        return getProperties().getS2DaoAdjustmentProperties().getDaoSqlFileEncoding();
    }

    // -----------------------------------------------------
    //                                     Little Adjustment
    //                                     -----------------
    public boolean isCommonColumnInterceptingOnBehaviorFilter() {
        return getProperties().getLittleAdjustmentProperties().isCommonColumnInterceptingOnBehaviorFilter();
    }

    public boolean isOneToManyReturnNullIfNonSelect() {
        return getProperties().getLittleAdjustmentProperties().isOneToManyReturnNullIfNonSelect();
    }

    public String getBehaviorDelegateModifier() {
        final String protectedString = "protected";
        if (isCommonColumnInterceptingOnBehaviorFilter()) {
            return protectedString;
        }
        return "public";

        // TODO: @jflute -- 0.6.0で有効にする。
        // return isAvailableCommonColumnSetupInterceptorToBehavior() ? "public" : protectedString;
    }

    public boolean isUseBuri() {
        return getProperties().getLittleAdjustmentProperties().isUseBuri();
    }

    public boolean isCompatibleNullEqualFalse() {
        return getProperties().getLittleAdjustmentProperties().isCompatibleNullEqualFalse();
    }

    public boolean isCompatibleOldReferrerNotDeprecated() {
        return getProperties().getLittleAdjustmentProperties().isCompatibleOldReferrerNotDeprecated();
    }

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    public boolean isStopGenerateExtendedBhv() {
        return getProperties().getOtherProperties().isStopGenerateExtendedBhv();
    }

    public boolean isStopGenerateExtendedDao() {
        return getProperties().getOtherProperties().isStopGenerateExtendedDao();
    }

    public boolean isStopGenerateExtendedEntity() {
        return getProperties().getOtherProperties().isStopGenerateExtendedEntity();
    }

    public String getExtractAcceptStartBrace() {
        return getProperties().getOtherProperties().getExtractAcceptStartBrace();
    }

    public String getExtractAcceptEndBrace() {
        return getProperties().getOtherProperties().getExtractAcceptEndBrace();
    }

    public String getExtractAcceptDelimiter() {
        return getProperties().getOtherProperties().getExtractAcceptDelimiter();
    }

    public String getExtractAcceptEqual() {
        return getProperties().getOtherProperties().getExtractAcceptEqual();
    }

    // ===================================================================================
    //                                                                        Type Mapping
    //                                                                        ============
    public List<Object> getJavaNativeStringList() {
        return getProperties().getTypeMappingProperties().getJavaNativeStringList();
    }

    public List<Object> getJavaNativeBooleanList() {
        return getProperties().getTypeMappingProperties().getJavaNativeBooleanList();
    }

    public List<Object> getJavaNativeNumberList() {
        return getProperties().getTypeMappingProperties().getJavaNativeNumberList();
    }

    public List<Object> getJavaNativeDateList() {
        return getProperties().getTypeMappingProperties().getJavaNativeDateList();
    }

    public List<Object> getJavaNativeBinaryList() {
        return getProperties().getTypeMappingProperties().getJavaNativeBinaryList();
    }

    public String getJdbcToJavaNativeAsStringRemovedLineSeparator() {
        return getProperties().getTypeMappingProperties().getJdbcToJavaNativeAsStringRemovedLineSeparator();
    }

    // ===============================================================================
    //                      Properties - ToLowerInGeneratorUnderscoreMethod (Internal)
    //                      ==========================================================
    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return getProperties().isAvailableToLowerInGeneratorUnderscoreMethod();
    }

    // ===============================================================================
    //                                                         Properties - sql2entity  
    //                                                         =======================
    public boolean isSql2EntityPlainEntity() {
        return getProperties().getSql2EntityProperties().isPlainEntity();
    }

    public String getSql2EntityBaseEntityPackage() {
        return getProperties().getSql2EntityProperties().getBaseEntityPackage();
    }

    public String getSql2EntityDBMetaPackage() {
        return getProperties().getSql2EntityProperties().getDBMetaPackage();
    }

    public String getSql2EntityExtendedEntityPackage() {
        return getProperties().getSql2EntityProperties().getExtendedEntityPackage();
    }

    public String getSql2EntityBaseParameterBeanPackage() {
        return getProperties().getSql2EntityProperties().getBaseParameterBeanPackage();
    }

    public String getSql2EntityExtendedParameterBeanPackage() {
        return getProperties().getSql2EntityProperties().getExtendedParameterBeanPackage();
    }

    public String filterProjectSuffixForComponentName(String targetName) {
        if (getBasicProperties().isAppendProjectSuffixToComponentName()) {
            final String prefix = getBasicProperties().getProjectPrefix();
            if (prefix == null || prefix.trim().length() == 0) {
                return targetName;
            } else {
                final String filteredPrefix = prefix.substring(0, 1).toLowerCase() + prefix.substring(1);
                return filteredPrefix + targetName.substring(0, 1).toUpperCase() + targetName.substring(1);
            }
        } else {
            return targetName;
        }
    }

    // **********************************************************************************************
    //                                                                             Hard code property
    //                                                                             ******************
    public String getDaoSelectorComponentName() {
        return filterProjectSuffixForComponentName("daoSelector");
    }

    public String getBehaviorSelectorComponentName() {
        return filterProjectSuffixForComponentName("behaviorSelector");
    }

    // ==================================================================
    //                                         databaseInfoMap (Internal)
    //                                         ==========================
    protected Map<String, Map<String, String>> _databaseDefinitionMap;

    public Map<String, Map<String, String>> getDatabaseDefinitionMap() {
        final DfDatabaseConfig config = (DfDatabaseConfig) DfComponentProvider.getComponent(DfDatabaseConfig.class);
        return config.analyzeDatabaseBaseInfo();
    }

    protected Map<String, String> _databaseInfoMap;

    public Map<String, String> getDatabaseInfoMap() {
        if (_databaseInfoMap == null) {
            final Map<String, Map<String, String>> databaseDefinitionMap = getDatabaseDefinitionMap();
            Map<String, String> databaseInfoMap = databaseDefinitionMap.get(getDatabaseType());
            if (databaseInfoMap == null) {
                databaseInfoMap = databaseDefinitionMap.get("default");
                if (databaseInfoMap == null) {
                    String msg = "The property[databaseDefinitionMap] doesn't have the database[";
                    throw new IllegalStateException(msg + getDatabaseType() + "] and default-database.");
                }
            }
            _databaseInfoMap = databaseInfoMap;
        }
        return _databaseInfoMap;
    }

    public String getDaoGenDbName() {
        final Map databaseInfoMap = getDatabaseInfoMap();
        final String daoGenDbName = (String) databaseInfoMap.get("daoGenDbName");
        if (daoGenDbName == null || daoGenDbName.trim().length() == 0) {
            String msg = "The database doesn't have daoGenDbName in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return daoGenDbName;
    }

    public String getWildCard() {
        final Map databaseInfoMap = getDatabaseInfoMap();
        final String wildCard = (String) databaseInfoMap.get("wildCard");
        if (wildCard == null || wildCard.trim().length() == 0) {
            String msg = "The database doesn't have wildCard in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return wildCard;
    }

    public String getSequenceNextSql() {
        final Map databaseInfoMap = getDatabaseInfoMap();
        final String sequenceNextSql = (String) databaseInfoMap.get("sequenceNextSql");
        if (sequenceNextSql == null || sequenceNextSql.trim().length() == 0) {
            String msg = "The database doesn't have sequenceNextSql in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return sequenceNextSql;
    }

    public String getColumnSetupBeforeInsertInterceptorPointcut() {
        if (isAvailableCommonColumnSetupInterceptorToBehavior()) {
            return getColumnSetupBeforeInsertInterceptorToBehaviorPointcut();
        } else {
            return getColumnSetupBeforeInsertInterceptorToDaoPointcut();
        }
    }

    public String getColumnSetupBeforeUpdateInterceptorPointcut() {
        if (isAvailableCommonColumnSetupInterceptorToBehavior()) {
            return getColumnSetupBeforeUpdateInterceptorToBehaviorPointcut();
        } else {
            return getColumnSetupBeforeUpdateInterceptorToDaoPointcut();
        }
    }

    public String getColumnSetupBeforeDeleteInterceptorPointcut() {
        if (isAvailableCommonColumnSetupInterceptorToBehavior()) {
            return getColumnSetupBeforeDeleteInterceptorToBehaviorPointcut();
        } else {
            return getColumnSetupBeforeDeleteInterceptorToDaoPointcut();
        }
    }

    public String getColumnSetupBeforeInsertInterceptorToBehaviorPointcut() {
        return "delegateInsert.*, delegateCreate.*, delegateAdd.*, callInsert.*, callCreate.*, callAdd.*";
    }

    public String getColumnSetupBeforeUpdateInterceptorToBehaviorPointcut() {
        return "delegateUpdate.*, delegateModify.*, delegateStore.*, callUpdate.*, callModify.*, callStore.*";
    }

    public String getColumnSetupBeforeDeleteInterceptorToBehaviorPointcut() {
        return "delegateDelete.*, delegateRemove.*, callDeletee.*, callRemove";
    }

    public String getColumnSetupBeforeInsertInterceptorToDaoPointcut() {
        return "insert.*, create.*, add.*";
    }

    public String getColumnSetupBeforeUpdateInterceptorToDaoPointcut() {
        return "update.*, modify.*, store.*";
    }

    public String getColumnSetupBeforeDeleteInterceptorToDaoPointcut() {
        return "delete.*, remove.*";
    }

    public String getColumnSetupBeforeInsertInterceptorToBehaviorPointcutInitCap() {
        return "DelegateInsert.*, DelegateCreate.*, DelegateAdd.*";
    }

    public String getColumnSetupBeforeUpdateInterceptorToBehaviorPointcutInitCap() {
        return "DelegateUpdate.*, DelegateModify.*, DelegateStore.*";
    }

    public String getColumnSetupBeforeDeleteInterceptorToBehaviorPointcutInitCap() {
        return "DelegateDelete.*, DelegateRemove.*";
    }

    public String getColumnSetupBeforeInsertInterceptorToDaoPointcutInitCap() {
        return "Insert.*, Create.*, Add.*";
    }

    public String getColumnSetupBeforeUpdateInterceptorToDaoPointcutInitCap() {
        return "Update.*, Modify.*, Store.*";
    }

    public String getColumnSetupBeforeDeleteInterceptorToDaoPointcutInitCap() {
        return "Delete.*, Remove.*";
    }

    public String getRequiredTransactionToBehaviorPointcut() {
        return ".*Tx";
    }

    public String getRequiresNewTransactionToBehaviorPointcut() {
        return ".*NewTx";
    }

    public String getOverrideComment() {
        return "The override.";
    }

    public String getImplementComment() {
        return "The implementation.";
    }

    // **********************************************************************************************
    //                                                                                     Helper
    //                                                                                     **********
    // ===============================================================================
    //                                                                         Logging
    //                                                                         =======
    public void debug(String msg) {
        _log.debug(msg);
    }

    // ===============================================================================
    //                                                                          String
    //                                                                          ======
    public String initCap(String str) {
        if (str == null) {
            String msg = "Argument[str] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (str.length() == 0) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // for Seasar's property.
    public String decapitalizePropertyName(String javaName) {
        if (javaName == null || javaName.length() == 0) {
            return javaName;
        }
        if (javaName.length() > 1 && Character.isUpperCase(javaName.charAt(1))
                && Character.isUpperCase(javaName.charAt(0))) {

            return javaName;
        }
        char chars[] = javaName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    // ===============================================================================
    //                                                                             Map
    //                                                                             ===
    public String getMapValue(Map map, String key) {
        final Object value = map.get(key);
        return value != null ? (String) value : "";
    }

    // ===============================================================================
    //                                                                   Common Column
    //                                                                   =============
    public boolean isCommonColumnSetupInvokingLogic(String logic) {
        return logic.startsWith("$");
    }

    public String removeCommonColumnSetupInvokingMark(String logic) {
        return filterInvokingLogic(logic.substring("$".length()));
    }

    public String filterInvokingLogic(String logic) {
        String tmp = DfPropertyUtil.convertAll(logic, "$$Semicolon$$", ";");
        tmp = DfPropertyUtil.convertAll(tmp, "$$StartBrace$$", "{");
        tmp = DfPropertyUtil.convertAll(tmp, "$$EndBrace$$", "}");
        return tmp;
    }

    // ===============================================================================
    //                                                                         TypeMap
    //                                                                         =======
    public String convertJavaNativeByJdbcType(String jdbcType) {
        try {
            return TypeMap.findJavaNativeTypeString(jdbcType, null, null);
        } catch (RuntimeException e) {
            _log.warn("TypeMap.findJavaNativeTypeString(jdbcType, null, null) threw the exception: jdbcType="
                    + jdbcType, e);
            throw e;
        }
    }

    public String convertJavaNameByJdbcNameAsTable(String jdbcName) {
        if (isJavaNameOfTableSameAsDbName()) {
            return jdbcName;
        }
        final List<String> inputs = new ArrayList<String>(2);
        inputs.add(jdbcName);
        inputs.add(getDefaultJavaNamingMethod());
        return StringUtils.capitalise(generateName(NameFactory.JAVA_GENERATOR, inputs));
    }

    public String convertUncapitalisedJavaNameByJdbcNameAsTable(String jdbcName) {
        return StringUtils.uncapitalise(convertJavaNameByJdbcNameAsTable(jdbcName));
    }

    public String convertJavaNameByJdbcNameAsColumn(String jdbcName) {
        if (isJavaNameOfColumnSameAsDbName()) {
            return jdbcName;
        }
        final List<String> inputs = new ArrayList<String>(2);
        inputs.add(jdbcName);
        inputs.add(getDefaultJavaNamingMethod());
        return StringUtils.capitalise(generateName(NameFactory.JAVA_GENERATOR, inputs));
    }

    public String convertUncapitalisedJavaNameByJdbcNameAsColumn(String jdbcName) {
        return StringUtils.uncapitalise(convertJavaNameByJdbcNameAsColumn(jdbcName));
    }

    /**
     * Generate name.
     * 
     * @param algorithmName Algorithm name.
     * @param inputs Inputs.
     * @return Generated name.
     */
    protected String generateName(String algorithmName, List inputs) {
        String javaName = null;
        try {
            javaName = NameFactory.generateName(NameFactory.JAVA_GENERATOR, inputs);
        } catch (EngineException e) {
            String msg = "NameFactory.generateName() threw the exception: inputs=" + inputs;
            _log.warn(msg, e);
            throw new RuntimeException(msg, e);
        } catch (RuntimeException e) {
            String msg = "NameFactory.generateName() threw the exception: inputs=" + inputs;
            _log.warn(msg, e);
            throw new RuntimeException(msg, e);
        }
        if (javaName == null) {
            String msg = "NameFactory.generateName() returned null: inputs=" + inputs;
            _log.warn(msg);
            throw new IllegalStateException(msg);
        }
        return javaName;
    }

    public void makeDirectory(String packagePath) {
        FileUtil.mkdir(Generator.getInstance().getOutputPath() + "/" + packagePath);
    }

    // ===============================================================================
    //                                                                         JavaDir
    //                                                                         =======
    public void setupJavaDir_for_genMetaData() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_lazyLoadContainer() {
        setupJavaDir_for_allcommon();
    }

    public void setupJavaDir_for_daoDicon() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_daoSelector() {
        setupJavaDir_for_allcommon();
    }

    public void setupJavaDir_for_cacheDaoSelector() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_abstractCBean() {
        setupJavaDir_for_gen();
    }

    public void setupJavaDir_for_dbmetaInstanceHandler() {
        setupJavaDir_for_gen();
    }

    public void setupJavaDir_for_interceptor() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_s2daoObject() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_baseCustomizeDao() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_extendedCustomizeDao() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_baseSqlParameter() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_extendedSqlParameter() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_argumentBean() {
        setupJavaDir_for_extended();
    }

    public void setupJavaDir_for_allcommon() {
        setupJavaDir_for_gen();
    }

    public void setupJavaDir_for_base() {
        setupJavaDir_for_main();
    }

    public void setupJavaDir_for_base_cbean() {
        setupJavaDir_for_gen();
    }

    public void setupJavaDir_for_extended_cbean() {
        setupJavaDir_for_main();
    }

    public void setupJavaDir_for_extended() {
        setupJavaDir_for_main();
    }

    public void setupJavaDir_for_sql2entity() {
        Generator.getInstance().setOutputPath(getProperties().getSql2EntityProperties().getOutputDirectory());
    }

    // --------------------------------------------
    //                                  Basic Setup
    //                                  -----------
    protected void setupJavaDir_for_main() {
        Generator.getInstance().setOutputPath(getBasicProperties().getJavaDir_for_main());
    }

    protected void setupJavaDir_for_gen() {
        Generator.getInstance().setOutputPath(getBasicProperties().getJavaDir_for_gen());
    }

    public boolean isJavaDirOnlyOne() {
        return getBasicProperties().isJavaDirOnlyOne();
    }
}