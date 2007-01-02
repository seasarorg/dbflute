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
import org.seasar.dbflute.DfDBFluteProvider;
import org.seasar.dbflute.config.DfDatabaseConfig;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.task.DfSql2EntityTask.DfParameterBeanMetaData;
import org.seasar.dbflute.torque.DfAdditionalForeignKeyInitializer;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.xml.sax.Attributes;

/**
 * A class for holding application data structures.
 * 
 * @author Modified by mkubo
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
                Table foreignTable = getTable(currFK.getForeignTableName());
                if (foreignTable == null) {
                    throw new EngineException("Attempt to set foreign" + " key to nonexistent table, "
                            + currFK.getForeignTableName());
                } else {
                    List referrers = foreignTable.getReferrers();
                    if ((referrers == null || !referrers.contains(currFK))) {
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
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            String msg = "The className has no meta data: className=" + className;
            throw new IllegalStateException(msg);
        }
        final String superClassName = metaData.getSuperClassName();
        if (superClassName == null || superClassName.trim().length() == 0) {
            return "";
        }
        final DfLanguageDependencyInfo languageDependencyInfo = getBasicProperties().getLanguageDependencyInfo();
        return languageDependencyInfo.getGrammarInfo().getExtendsStringMark() + " " + superClassName + " ";
    }

    public Map<String, String> getPmbMetaDataPropertyNameTypeMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        if (_pmbMetaDataMap == null || _pmbMetaDataMap.isEmpty()) {
            String msg = "The pmbMetaDataMap should not be null or empty: className=" + className;
            throw new IllegalStateException(msg);
        }
        final DfParameterBeanMetaData metaData = _pmbMetaDataMap.get(className);
        if (metaData == null) {
            String msg = "The className has no meta data: className=" + className;
            throw new IllegalStateException(msg);
        }
        return metaData.getPropertyNameTypeMap();
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
    protected java.util.List<Table> _customizeTableList = new ArrayList<Table>(100);

    public void initializeCustomizeDao() {
        _log.debug("/=============================");
        _log.debug("...Initializing customize dao.");

        final Map<String, Map<String, Map<String, String>>> customizeDaoDefinitionMap = getCustomizeDaoDefinitionMap();
        final Set<String> tableNameKeySet = customizeDaoDefinitionMap.keySet();
        for (String tableName : tableNameKeySet) {
            final Table table = new Table(tableName);
            _customizeTableList.add(table);
            table.setDatabase(this);
            table.setCustomizeDaoMethodMap(getCustomizeDaoComponentMethodMap(tableName));
            table.setCustomizeDaoImportMap(getCustomizeDaoComponentImportMap(tableName));
            table.setCustomizeDaoRelationMap(getCustomizeDaoComponentRelationMap(tableName));

            final Map<String, String> columnMap = getCustomizeDaoComponentColumnMap(tableName);
            final Set<String> columnNameKeySet = columnMap.keySet();
            for (String columnName : columnNameKeySet) {
                final String columnType = columnMap.get(columnName);
                boolean isPrimaryKey = false;
                if (columnName.startsWith("*")) {
                    columnName = columnName.substring("*".length());
                    isPrimaryKey = true;
                }
                final Column col = new Column(columnName);
                col.setName(columnName);
                col.setPrimaryKey(isPrimaryKey);
                col.setTorqueType(columnType);
                col.setTable(table);
                table.addColumn(col);
            }
            _log.debug("    " + table.getName());
        }
        _log.debug("========/");
    }

    public java.util.List<Table> getCustomizeTableList() {
        return _customizeTableList;
    }

    // ===============================================================================
    //                                                            AdditionalForeignKey
    //                                                            ====================
    public void initializeAdditionalForeignKey() {
        final DfAdditionalForeignKeyInitializer initializer = new DfAdditionalForeignKeyInitializer(this);
        initializer.initializeAdditionalForeignKey();
    }

    public void initializeClassificationDeployment() {
        getProperties().initializeClassificationDeploymentMap(getTableList());
        getProperties().initializeClassificationDeploymentMap(getCustomizeTableList());
    }

    // **********************************************************************************************
    //                                                                                     Properties
    //                                                                                     **********

    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    // ===============================================================================
    //                                                            Properties - JavaDir
    //                                                            ====================
    public String getJavaDir() {
        return getBasicProperties().getJavaDir();
    }

    // ===============================================================================
    //                                                           Properties - Language
    //                                                           =====================
    public String getTargetLanguage() {
        return getBasicProperties().getTargetLanguage();
    }

    public boolean isTargetLanguageJava() {
        return getBasicProperties().isTargetLanguageJava();
    }

    public boolean isTargetLanguageCSharp() {
        return getBasicProperties().isTargetLanguageCSharp();
    }

    // ===============================================================================
    //                                                          Properties - Extension
    //                                                          ======================
    public String getTemplateFileExtension() {
        return getBasicProperties().getTemplateFileExtension();
    }

    public String getClassFileExtension() {
        return getBasicProperties().getClassFileExtension();
    }

    // ===============================================================================
    //                                                           Properties - Encoding
    //                                                           =====================
    public String getTemplateFileEncoding() {
        return getBasicProperties().getTemplateFileEncoding();
    }

    // ===============================================================================
    //                                                             Properties - Author
    //                                                             ===================
    public String getClassAuthor() {
        return getBasicProperties().getClassAuthor();
    }

    // ===============================================================================
    //                                                             Properties - SameAs
    //                                                             ===================
    public boolean isJavaNameOfTableSameAsDbName() {
        return getBasicProperties().isJavaNameOfTableSameAsDbName();
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return getBasicProperties().isJavaNameOfColumnSameAsDbName();
    }

    // ===============================================================================
    //                                                          Properties - Available
    //                                                          ======================
    public boolean isAvailableEntityLazyLoad() {
        return getBasicProperties().isAvailableEntityLazyLoad();
    }

    public boolean isAvailableBehaviorGeneration() {
        return getBasicProperties().isAvailableBehaviorGeneration();
    }

    public boolean isAvailableCommonColumnSetupInterceptorToBehavior() {
        return getBasicProperties().isAvailableCommonColumnSetupInterceptorToBehavior();
    }

    public boolean isAvailableCommonColumnSetupInterceptorToDao() {
        return getBasicProperties().isAvailableCommonColumnSetupInterceptorToDao();
    }

    public boolean isAvailableGenerics() {
        return getBasicProperties().isAvailableGenerics();
    }

    public String filterGenericsString(String genericsString) {
        return getBasicProperties().filterGenericsString(genericsString);
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
    //                                                           Properties - DaoDicon
    //                                                           =====================
    public String getDaoDiconNamespace() {
        return getProperties().getDaoDiconProperties().getDaoDiconNamespace();
    }

    public String getDaoDiconPackageName() {
        return getProperties().getDaoDiconProperties().getDaoDiconPackageName();
    }

    public String getDaoDiconFileName() {
        return getProperties().getDaoDiconProperties().getDaoDiconFileName();
    }

    public String getJdbcDiconResourceName() {
        return getProperties().getDaoDiconProperties().getJdbcDiconResourceName();
    }

    public String getRequiredTxComponentName() {
        return getProperties().getDaoDiconProperties().getRequiredTxComponentName();
    }

    public String getRequiresNewTxComponentName() {
        return getProperties().getDaoDiconProperties().getRequiresNewTxComponentName();
    }

    public List<String> getDaoDiconOtherIncludePathList() {
        return getProperties().getDaoDiconProperties().getDaoDiconOtherIncludePathList();
    }

    public Map<String, Map<String, String>> getOriginalDaoComponentMap() {
        return getProperties().getDaoDiconProperties().getOriginalDaoComponentMap();
    }

    public List<String> getOriginalDaoComponentComponentNameList() {
        return getProperties().getDaoDiconProperties().getOriginalDaoComponentComponentNameList();
    }

    public String getOriginalDaoComponentClassName(String componentName) {
        return getProperties().getDaoDiconProperties().getOriginalDaoComponentClassName(componentName);
    }

    public boolean isDaoComponent(String componentName) {
        return getProperties().getDaoDiconProperties().isDaoComponent(componentName);
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
    public Map<String, Object> getSequenceDefinitionMap() {
        return getProperties().getSequenceDefinitionMap();
    }

    public Map<String, Object> getIdentityDefinitionMap() {
        return getProperties().getIdentityDefinitionMap();
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
    //                                                      Properties - Common-Column
    //                                                      ==========================
    public Map<String, Object> getCommonColumnMap() {
        return getProperties().getCommonColumnMap();
    }

    public List<String> getCommonColumnNameList() {
        return getProperties().getCommonColumnNameList();
    }

    // --------------------------------------
    //                                 insert
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeInsertInterceptorLogicMap() {
        return getProperties().getCommonColumnSetupBeforeInsertInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeInsertInterceptorLogicMap(String columnName) {
        return getProperties().containsValidColumnNameKeyCommonColumnSetupBeforeInsertInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeInsertInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnSetupBeforeInsertInterceptorLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                                 update
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeUpdateInterceptorLogicMap() {
        return getProperties().getCommonColumnSetupBeforeUpdateInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeUpdateInterceptorLogicMap(String columnName) {
        return getProperties().containsValidColumnNameKeyCommonColumnSetupBeforeUpdateInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeUpdateInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnSetupBeforeUpdateInterceptorLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                                 delete
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeDeleteInterceptorLogicMap() {
        return getProperties().getCommonColumnSetupBeforeDeleteInterceptorLogicMap();
    }

    public boolean containsValidColumnNameKeyCommonColumnSetupBeforeDeleteInterceptorLogicMap(String columnName) {
        return getProperties().containsValidColumnNameKeyCommonColumnSetupBeforeDeleteInterceptorLogicMap(columnName);
    }

    public String getCommonColumnSetupBeforeDeleteInterceptorLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnSetupBeforeDeleteInterceptorLogicByColumnName(columnName);
    }

    // ===============================================================================
    //                                                     Properties - Logical-Delete
    //                                                     ===========================
    public Map<String, Object> getLogicalDeleteColumnValueMap() {
        return getProperties().getLogicalDeleteColumnValueMap();
    }

    public List<String> getLogicalDeleteColumnNameList() {
        return getProperties().getLogicalDeleteColumnNameList();
    }

    // ===============================================================================
    //                                        Properties - Revival from Logical-Delete
    //                                        ========================================
    public Map<String, Object> getRevivalFromLogicalDeleteColumnValueMap() {
        return getProperties().getRevivalFromLogicalDeleteColumnValueMap();
    }

    public List<String> getRevivalFromLogicalDeleteColumnNameList() {
        return getProperties().getRevivalFromLogicalDeleteColumnNameList();
    }

    // ===============================================================================
    //                                                     Properties - Classification
    //                                                     ===========================
    // --------------------------------------
    //                             Definition
    //                             ----------
    public boolean hasClassificationDefinitionMap() {
        return getProperties().hasClassificationDefinitionMap();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        return getProperties().getClassificationDefinitionMap();
    }

    public List<String> getClassificationNameList() {
        return getProperties().getClassificationNameList();
    }

    public List<String> getClassificationNameListValidNameOnly() {
        return getProperties().getClassificationNameListValidNameOnly();
    }

    public List<String> getClassificationNameListValidAliasOnly() {
        return getProperties().getClassificationNameListValidAliasOnly();
    }

    public String getClassificationDefinitionMapAsStringRemovedLineSeparatorFilteredQuotation() {
        return getProperties().getClassificationDefinitionMapAsStringRemovedLineSeparatorFilteredQuotation();
    }

    public List<java.util.Map<String, String>> getClassificationMapList(String classificationName) {
        return getProperties().getClassificationMapList(classificationName);
    }

    // --------------------------------------
    //                             Deployment
    //                             ----------
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        return getProperties().getClassificationDeploymentMap();
    }

    public String getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation() {
        return getProperties().getClassificationDeploymentMapAsStringRemovedLineSeparatorFilteredQuotation();
    }

    public boolean hasClassification(String tableName, String columnName) {
        return getProperties().hasClassification(tableName, columnName);
    }

    public String getClassificationName(String tableName, String columnName) {
        return getProperties().getClassificationName(tableName, columnName);
    }

    public boolean hasClassificationName(String tableName, String columnName) {
        return getProperties().hasClassificationName(tableName, columnName);
    }

    public boolean hasClassificationAlias(String tableName, String columnName) {
        return getProperties().hasClassificationAlias(tableName, columnName);
    }

    public Map<String, String> getAllColumnClassificationMap() {
        return getProperties().getAllColumnClassificationMap();
    }

    public boolean isAllClassificationColumn(String columnName) {
        if (columnName == null) {
            String msg = "The argument[columnName] should not be null.";
            throw new IllegalArgumentException(msg);
        }
        return getProperties().isAllClassificationColumn(columnName);
    }

    public String getAllClassificationName(String columnName) {
        return getProperties().getAllClassificationName(columnName);
    }

    // ===============================================================================
    //                                                       Properties - Select Param
    //                                                       =========================
    public String getSelectQueryTimeout() {
        return getProperties().getSelectQueryTimeout();
    }

    public boolean isSelectQueryTimeoutValid() {
        return getProperties().isSelectQueryTimeoutValid();
    }

    public String getStatementResultSetType() {
        return getProperties().getStatementResultSetType();
    }

    public String getStatementResultSetConcurrency() {
        return getProperties().getStatementResultSetConcurrency();
    }

    public boolean isStatementResultSetTypeValid() {
        return getProperties().isStatementResultSetTypeValid();
    }

    // ===============================================================================
    //                                                       Properties - CustomizeDao
    //                                                       =========================
    public Map<String, Map<String, Map<String, String>>> getCustomizeDaoDefinitionMap() {
        return getProperties().getCustomizeDaoDifinitionMap();
    }

    public Map<String, String> getCustomizeDaoComponentColumnMap(String tableName) {
        return getProperties().getCustomizeDaoComponentColumnMap(tableName);
    }

    public Map<String, String> getCustomizeDaoComponentMethodMap(String tableName) {
        return getProperties().getCustomizeDaoComponentMethodMap(tableName);
    }

    public String getCustomizeDaoComponentMethodArgumentVariableCommaString(String tableName, String methodName) {
        return getProperties().getCustomizeDaoComponentMethodArgumentVariableCommaString(tableName, methodName);
    }

    public Map<String, String> getCustomizeDaoComponentImportMap(String tableName) {
        return getProperties().getCustomizeDaoComponentImportMap(tableName);
    }

    public Map<String, String> getCustomizeDaoComponentRelationMap(String tableName) {
        return getProperties().getCustomizeDaoComponentRelationMap(tableName);
    }

    public boolean isAvailableCustomizeDaoGeneration() {
        return getProperties().isAvailableCustomizeDaoGeneration();
    }

    // ===============================================================================
    //                                                   Properties - SqlParameterBean
    //                                                   =============================
    public String getSqlParameterBeanPackage() {
        return getProperties().getSqlParameterBeanPackage();
    }

    public Map<String, Object> getSqlParameterBeanDefinitionMap() {
        return getProperties().getSqlParameterBeanDefinitionMap();
    }

    public List<String> getSqlParameterBeanClassNameList() {
        return getProperties().getSqlParameterBeanClassNameList();
    }

    public Map<String, String> getSqlParameterBeanClassDefinitionMap(String className) {
        return getProperties().getSqlParameterBeanClassDefinitionMap(className);
    }

    public String getSqlParameterBeanPropertyType(String className, String property) {
        return getProperties().getSqlParameterBeanPropertyType(className, property);
    }

    public boolean isSqlParameterBeanPropertyDefaultValueEffective(String className, String property) {
        return getProperties().isSqlParameterBeanPropertyDefaultValueEffective(className, property);
    }

    public String getSqlParameterBeanPropertyDefaultValue(String className, String property) {
        return getProperties().getSqlParameterBeanPropertyDefaultValue(className, property);
    }

    public boolean isAvailableSqlParameterBeanGeneration() {
        return getProperties().isAvailableSqlParameterBeanGeneration();
    }

    public boolean isSqlParameterBeanHaveTheProperty(String className, String property) {
        return getProperties().isSqlParameterBeanHaveTheProperty(className, property);
    }

    // ===============================================================================
    //                                                       Properties - ArgumentBean
    //                                                       =========================
    public String getArgumentBeanPackage() {
        return getProperties().getArgumentBeanPackage();
    }

    public Map<String, Object> getArgumentBeanDefinitionMap() {
        return getProperties().getArgumentBeanDefinitionMap();
    }

    public List<String> getArgumentBeanClassNameList() {
        return getProperties().getArgumentBeanClassNameList();
    }

    public Map<String, String> getArgumentBeanClassDefinitionMap(String className) {
        return getProperties().getArgumentBeanClassDefinitionMap(className);
    }

    public String getArgumentBeanPropertyType(String className, String property) {
        return getProperties().getArgumentBeanPropertyType(className, property);
    }

    public boolean isArgumentBeanPropertyDefaultValueEffective(String className, String property) {
        return getProperties().isArgumentBeanPropertyDefaultValueEffective(className, property);
    }

    public String getArgumentBeanPropertyDefaultValue(String className, String property) {
        return getProperties().getArgumentBeanPropertyDefaultValue(className, property);
    }

    public boolean isAvailableArgumentBeanGeneration() {
        return getProperties().isAvailableArgumentBeanGeneration();
    }

    public Map<String, Object> getArgumentBeanRelatedSqlParameterMap() {
        return getProperties().getArgumentBeanRelatedSqlParameterMap();
    }

    public List<String> getArgumentBeanRelatedSqlParameterSqlParameterNameList(String argumentBeanName) {
        return getProperties().getArgumentBeanRelatedSqlParameterSqlParameterNameList(argumentBeanName);
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

    // ===============================================================================
    //                                                      Properties - ExtractAccept
    //                                                      ==========================

    public String getExtractAcceptStartBrace() {
        return getProperties().getExtractAcceptStartBrace();
    }

    public String getExtractAcceptEndBrace() {
        return getProperties().getExtractAcceptEndBrace();
    }

    public String getExtractAcceptDelimiter() {
        return getProperties().getExtractAcceptDelimiter();
    }

    public String getExtractAcceptEqual() {
        return getProperties().getExtractAcceptEqual();
    }

    // ===============================================================================
    //                                                   Properties - Source Reduction
    //                                                   =============================
    public boolean isMakeDeprecated() {
        return getProperties().getSourceReductionProperties().isMakeDeprecated();
    }

    public boolean isMakeBehaviorForUpdate() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorForUpdate();
    }

    // ===============================================================================
    //                                                              Properties - Other
    //                                                              ==================
    public boolean isStopGenerateExtendedBhv() {
        return getProperties().getOtherProperties().isStopGenerateExtendedBhv();
    }

    public boolean isStopGenerateExtendedDao() {
        return getProperties().getOtherProperties().isStopGenerateExtendedDao();
    }

    public boolean isStopGenerateExtendedEntity() {
        return getProperties().getOtherProperties().isStopGenerateExtendedEntity();
    }

    public boolean isVersionAfter1040() {
        return getProperties().getOtherProperties().isVersionAfter1040();
    }

    // ===============================================================================
    //                                        Properties - jdbcToJavaNative (Internal)
    //                                        ===============-========================
    public String getJdbcToJavaNativeAsStringRemovedLineSeparator() {
        return getProperties().getJdbcToJavaNativeAsStringRemovedLineSeparator();
    }

    public List<Object> getJavaNativeStringList() {
        return getProperties().getJavaNativeStringList();
    }

    public List<Object> getJavaNativeBooleanList() {
        return getProperties().getJavaNativeBooleanList();
    }

    public List<Object> getJavaNativeNumberList() {
        return getProperties().getJavaNativeNumberList();
    }

    public List<Object> getJavaNativeDateList() {
        return getProperties().getJavaNativeDateList();
    }

    public List<Object> getJavaNativeBinaryList() {
        return getProperties().getJavaNativeBinaryList();
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

    // **********************************************************************************************
    //                                                                             Hard code property
    //                                                                             ******************
    // ==================================================================
    //                                         databaseInfoMap (Internal)
    //                                         ==========================
    protected Map<String, Map<String, String>> _databaseDefinitionMap;

    public Map<String, Map<String, String>> getDatabaseDefinitionMap() {
        final DfDatabaseConfig config = (DfDatabaseConfig) DfDBFluteProvider.getComponent(DfDatabaseConfig.class);
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
        return "This method overrides the method that is declared at super.";
    }

    public String getImplementComment() {
        return "This method implements the method that is declared at super.";
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
    //                                                                   Common-Column
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
            return TypeMap.getJavaNative(jdbcType);
        } catch (RuntimeException e) {
            _log.warn("TypeMap.getJavaNative(jdbcType) threw the exception: jdbcType=" + jdbcType, e);
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