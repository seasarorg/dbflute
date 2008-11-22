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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.velocity.texen.util.FileUtil;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfDatabaseConfig;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler.DfProcedureColumnType;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.deletefile.DfOldClassHandler;
import org.seasar.dbflute.logic.initializer.IncludeQueryInitializer;
import org.seasar.dbflute.logic.pathhandling.DfPackagePathHandler;
import org.seasar.dbflute.logic.pmb.PmbMetaDataPropertyOptionClassification;
import org.seasar.dbflute.logic.pmb.PmbMetaDataPropertyOptionFinder;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties.CommonColumnSetupResource;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties.SequenceDefinitionMapChecker;
import org.seasar.dbflute.task.DfSql2EntityTask.DfParameterBeanMetaData;
import org.seasar.dbflute.torque.DfAdditionalForeignKeyInitializer;
import org.seasar.dbflute.torque.DfAdditionalPrimaryKeyInitializer;
import org.seasar.dbflute.velocity.DfGenerator;
import org.xml.sax.Attributes;

/**
 * A class for holding application data structures.
 * @author Modified by jflute
 */
public class Database {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(Database.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected String _name;

    // -----------------------------------------------------
    //                                               AppData
    //                                               -------
    protected AppData _appData;

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    protected List<Table> _tableList = new ArrayList<Table>(100);
    protected DfFlexibleMap<String, Table> _flexibleTableMap = new DfFlexibleMap<String, Table>();

    // -----------------------------------------------------
    //                                        for Sql2Entity
    //                                        --------------
    /** The meta data of parameter bean. */
    protected Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    protected String _databaseType;
    protected String _defaultJavaNamingMethod;

    // [Unused on DBFlute]
    // protected String _pkg;
    // protected String _defaultIdMethod;
    // protected String _defaultJavaType;
    // protected boolean _isHeavyIndexing;

    // ===================================================================================
    //                                                                             Loading
    //                                                                             =======
    /**
     * Load the database object from an XML tag.
     * @param attrib the XML attributes
     */
    public void loadFromXML(Attributes attrib) {
        setName(attrib.getValue("name"));
        _defaultJavaNamingMethod = attrib.getValue("defaultJavaNamingMethod"); // Basically Null
        if (_defaultJavaNamingMethod == null) {
            _defaultJavaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE; // Basically Here!
        }

    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    public Table[] getTables() {
        int size = _tableList.size();
        Table[] tbls = new Table[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Table) _tableList.get(i);
        }
        return tbls;
    }

    public List<Table> getTableList() {
        final List<Table> ls = new ArrayList<Table>();
        final Table[] tables = getTables();
        for (Table table : tables) {
            ls.add(table);
        }
        return ls;
    }

    public List<Table> getTableDisplaySortedList() {
        final TreeSet<Table> tableSet = new TreeSet<Table>(new Comparator<Table>() {
            public int compare(Table table1, Table table2) {
                final String type1 = table1.getType();
                final String type2 = table2.getType();
                if (!type1.equals(type2)) {
                    if (table1.isTypeTable()) {
                        return -1;
                    }
                    if (table2.isTypeTable()) {
                        return 1;
                    }
                    return type1.compareTo(type2);
                }
                final String name1 = table1.getName();
                final String name2 = table2.getName();
                return name1.compareTo(name2);
            }
        });
        tableSet.addAll(getTableList());
        return new ArrayList<Table>(tableSet);
    }

    public Table getTable(String name) {
        return (Table) _flexibleTableMap.get(name);
    }

    public Table addTable(Attributes attrib) {
        Table tbl = new Table();
        tbl.setDatabase(this);
        tbl.loadFromXML(attrib);
        addTable(tbl);
        return tbl;
    }

    public void addTable(Table tbl) {
        tbl.setDatabase(this);
        _tableList.add(tbl);
        _flexibleTableMap.put(tbl.getName(), tbl);
    }

    /**
     * Returns the value of the named property from this database's
     * <code>db.props</code> file.
     * @param name The name of the property to retrieve the value of.
     * @return The value of the specified property.
     * @exception EngineException Couldn't access properties.
     */
    protected String getProperty(String name) throws EngineException {
        Properties p = getAppData().getIdiosyncrasies(_databaseType);
        return (p == null ? null : p.getProperty(name));
    }

    public void doFinalInitialization() throws EngineException { // Unused on DBFlute
        Table[] tables = getTables();
        for (int i = 0; i < tables.length; i++) {
            Table currTable = tables[i];

            // check schema integrity
            // if idMethod="autoincrement", make sure a column is
            // specified as autoIncrement="true"
            // FIXME: Handle idMethod="native" via DB adapter.
            // [Unused on DBFlute]
            // if (currTable.getIdMethod().equals("autoincrement")) {
            //     Column[] columns = currTable.getColumns();
            //     boolean foundOne = false;
            //     for (int j = 0; j < columns.length && !foundOne; j++) {
            //         foundOne = columns[j].isAutoIncrement();
            //     }
            // 
            //     if (!foundOne) {
            //         String errorMessage = "Table '" + currTable.getName()
            //                 + "' is marked as autoincrement, but it does not "
            //                 + "have a column which declared as the one to "
            //                 + "auto increment (i.e. autoIncrement=\"true\")\n";
            //         throw new EngineException("Error in XML schema: " + errorMessage);
            //     }
            // }

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
                    final List<ForeignKey> refererList = foreignTable.getRefererList();
                    if ((refererList == null || !refererList.contains(currFK))) {
                        foreignTable.addReferrer(currFK);
                    }

                    // local column references
                    Iterator<String> localColumnNames = currFK.getLocalColumns().iterator();
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
                    Iterator<String> foreignColumnNames = currFK.getForeignColumns().iterator();
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

    // ===================================================================================
    //                                                                      Parameter Bean
    //                                                                      ==============
    // -----------------------------------------------------
    //                                           PmbMetaData
    //                                           -----------
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

    public Map<String, String> getPmbMetaDataPropertyNameColumnNameMap(String className) {
        assertArgumentPmbMetaDataClassName(className);
        final DfParameterBeanMetaData metaData = findPmbMetaData(className);
        return metaData.getPropertyNameColumnNameMap();
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

    public String getPmbMetaDataPropertyColumnName(String className, String propertyName) {
        assertArgumentPmbMetaDataClassName(className);
        assertArgumentPmbMetaDataPropertyName(propertyName);
        return getPmbMetaDataPropertyNameColumnNameMap(className).get(propertyName);
    }

    protected String findPmbMetaDataPropertyOption(String className, String propertyName) {
        PmbMetaDataPropertyOptionFinder finder = createPmbMetaDataPropertyOptionFinder(className, propertyName);
        return finder.findPmbMetaDataPropertyOption(className, propertyName);
    }

    protected PmbMetaDataPropertyOptionFinder createPmbMetaDataPropertyOptionFinder(String className,
            String propertyName) {
        return new PmbMetaDataPropertyOptionFinder(className, propertyName, _pmbMetaDataMap);
    }

    public boolean isPmbMetaDataForProcedure(String className) {
        return findPmbMetaData(className).getProcedureName() != null;
    }

    public String getPmbMetaDataProcedureName(String className) {
        return findPmbMetaData(className).getProcedureName();
    }

    // -----------------------------------------------------
    //                                    Option LikeSeasrch
    //                                    ------------------
    public boolean isPmbMetaDataPropertyOptionLikeSearch(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase("like");
    }

    // -----------------------------------------------------
    //                                 Option Classification
    //                                 ---------------------
    public boolean isPmbMetaDataPropertyOptionClassification(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.isPmbMetaDataPropertyOptionClassification();
    }

    public String getPmbMetaDataPropertyOptionClassificationName(String className, String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationName();
    }

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList(String className,
            String propertyName) {
        PmbMetaDataPropertyOptionClassification obj = createPmbMetaDataPropertyOptionClassification(className,
                propertyName);
        return obj.getPmbMetaDataPropertyOptionClassificationMapList();
    }

    protected PmbMetaDataPropertyOptionClassification createPmbMetaDataPropertyOptionClassification(String className,
            String propertyName) {
        DfClassificationProperties classificationProperties = getProperties().getClassificationProperties();
        PmbMetaDataPropertyOptionFinder finder = createPmbMetaDataPropertyOptionFinder(className, propertyName);
        return new PmbMetaDataPropertyOptionClassification(className, propertyName, classificationProperties, finder);
    }

    // -----------------------------------------------------
    //                                    Option LikeSeasrch
    //                                    ------------------
    public boolean isPmbMetaDataPropertyOptionProcedureParameterIn(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnIn.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterOut(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnOut.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnInOut.toString());
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        String option = findPmbMetaDataPropertyOption(className, propertyName);
        return option != null && option.trim().equalsIgnoreCase(DfProcedureColumnType.procedureColumnReturn.toString());
    }

    // -----------------------------------------------------
    //                                                Assert
    //                                                ------
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

    // ===================================================================================
    //                                                                         Initializer
    //                                                                         ===========
    // -----------------------------------------------------
    //                                  AdditionalPrimaryKey
    //                                  --------------------
    public void initializeAdditionalPrimaryKey() {
        final DfAdditionalPrimaryKeyInitializer initializer = new DfAdditionalPrimaryKeyInitializer(this);
        initializer.initializeAdditionalPrimaryKey();
    }

    // -----------------------------------------------------
    //                                  AdditionalForeignKey
    //                                  --------------------
    public void initializeAdditionalForeignKey() {
        final DfAdditionalForeignKeyInitializer initializer = new DfAdditionalForeignKeyInitializer(this);
        initializer.initializeAdditionalForeignKey();
    }

    // -----------------------------------------------------
    //                              ClassificationDeployment
    //                              ------------------------
    public void initializeClassificationDeployment() {
        getClassificationProperties().initializeClassificationDeploymentMap(getTableList());
        getClassificationProperties().initializeClassificationDefinition(); // Together!
    }

    // -----------------------------------------------------
    //                                          IncludeQuery
    //                                          ------------
    public void initializeIncludeQuery() {
        IncludeQueryInitializer initializer = new IncludeQueryInitializer();
        initializer.setIncludeQueryProperties(getProperties().getIncludeQueryProperties());
        initializer.setTableFinder(new IncludeQueryInitializer.TableFinder() {
            public Table findTable(String name) {
                return getTable(name);
            }
        });
        initializer.initializeIncludeQuery();
    }

    // ===================================================================================
    //                                                                    Check Properties
    //                                                                    ================
    /**
     * Check properties as mutually related validation.
     */
    public void checkProperties() {
        getProperties().getSequenceIdentityProperties().checkSequenceDefinitionMap(new SequenceDefinitionMapChecker() {
            public boolean hasTable(String tableName) {
                return getTable(tableName) != null;
            }
        });
        getProperties().getLittleAdjustmentProperties().checkDirectoryPackage();
    }

    // ===================================================================================
    //                                                              Delete Old Table Class
    //                                                              ======================
    public void deleteOldTableClass() {
        if (!getProperties().getLittleAdjustmentProperties().isDeleteOldTableClass()) {
            return;
        }
        final DfOldClassHandler handler = createOldClassHandler();
        handler.deleteOldTableClass();
    }

    public void deleteOldCustomizeClass() {
        if (!getProperties().getLittleAdjustmentProperties().isDeleteOldTableClass()) {
            return;
        }
        final DfOldClassHandler handler = createOldClassHandler();
        handler.setPmbMetaDataMap(_pmbMetaDataMap);
        handler.deleteOldCustomizeClass();
    }

    protected DfOldClassHandler createOldClassHandler() {
        return new DfOldClassHandler(getGeneratorInstance(), getBasicProperties(), getProperties()
                .getGeneratedClassPackageProperties(), getProperties().getLittleAdjustmentProperties(), getTableList());
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public void enableGenerateOutputDirectory() {
        getGeneratorInstance().setOutputPath(getProperties().getBasicProperties().getOutputDirectory());
    }

    public void enableSql2EntityOutputDirectory() {
        getGeneratorInstance().setOutputPath(getProperties().getOutsideSqlProperties().getSql2EntityOutputDirectory());
    }

    public void enableFlexDtoOutputDirectory() {
        getGeneratorInstance().setOutputPath(getProperties().getFlexDtoProperties().getOutputDirectory());
    }

    public void enableBuriBaoOutputDirectory() {
        getGeneratorInstance().setOutputPath(getProperties().getBuriProperties().getOutputDirectory());
    }

    // ===================================================================================
    //                                                                           Generator
    //                                                                           =========
    public DfGenerator getGeneratorInstance() {
        return DfGenerator.getInstance();
    }

    //====================================================================================
    //                                                                 Database Definition
    //                                                                 ===================
    protected Map<String, String> _databaseDefinitionMap;

    public Map<String, String> getDatabaseDefinitionMap() {
        if (_databaseDefinitionMap == null) {
            final DfDatabaseConfig config = new DfDatabaseConfig();
            final Map<String, Map<String, String>> databaseConfigMap = config.analyzeDatabaseBaseInfo();
            Map<String, String> databaseDefinitionMap = databaseConfigMap.get(getDatabaseType());
            if (databaseDefinitionMap == null) {
                databaseDefinitionMap = databaseConfigMap.get("default");
                if (databaseDefinitionMap == null) {
                    String msg = "The property[databaseDefinitionMap] doesn't have the database[";
                    throw new IllegalStateException(msg + getDatabaseType() + "] and default-database.");
                }
            }
            _databaseDefinitionMap = databaseDefinitionMap;
        }
        return _databaseDefinitionMap;
    }

    public String getDaoGenDbName() { // for SqlClause. It's Old Style method.
        return getGenerateDbName();
    }

    public String getGenerateDbName() {
        final Map<String, String> databaseInfoMap = getDatabaseDefinitionMap();
        final String dbName = (String) databaseInfoMap.get("dbName");
        if (dbName == null || dbName.trim().length() == 0) {
            String msg = "The database doesn't have dbName in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return dbName;
    }

    public String getWildCard() {
        final Map<String, String> databaseInfoMap = getDatabaseDefinitionMap();
        final String wildCard = (String) databaseInfoMap.get("wildCard");
        if (wildCard == null || wildCard.trim().length() == 0) {
            String msg = "The database doesn't have wildCard in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return wildCard;
    }

    public String getSequenceNextSql() {
        final Map<String, String> databaseInfoMap = getDatabaseDefinitionMap();
        final String sequenceNextSql = (String) databaseInfoMap.get("sequenceNextSql");
        if (sequenceNextSql == null || sequenceNextSql.trim().length() == 0) {
            String msg = "The database doesn't have sequenceNextSql in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return sequenceNextSql;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    // ===================================================================================
    //                                                                    Basic Properties
    //                                                                    ================
    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    // -----------------------------------------------------
    //                                               Project
    //                                               -------
    public String getProjectName() {
        return getBasicProperties().getProjectName();
    }

    // -----------------------------------------------------
    //                                              Database
    //                                              --------
    public String getDatabaseName() {
        return getBasicProperties().getDatabaseName();
    }

    public boolean isDatabaseOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    public boolean isDatabasePostgreSQL() {
        return getBasicProperties().isDatabasePostgreSQL();
    }

    public boolean isDatabaseDB2() {
        return getBasicProperties().isDatabaseDB2();
    }

    public boolean isDatabaseMySQL() {
        return getBasicProperties().isDatabaseMySQL();
    }

    public boolean isDatabaseSQLServer() {
        return getBasicProperties().isDatabaseSqlServer();
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

    public boolean isTargetLanguagePhp() {
        return getBasicProperties().isTargetLanguagePhp();
    }

    public boolean isJavaVersionGreaterEqualTiger() {
        return getBasicProperties().isJavaVersionGreaterEqualTiger();
    }

    public boolean isJavaVersionGreaterEqualMustang() {
        return getBasicProperties().isJavaVersionGreaterEqualMustang();
    }

    // -----------------------------------------------------
    //                                             Container
    //                                             ---------
    public String getTargetContainerName() {
        return getBasicProperties().getTargetContainerName();
    }

    public boolean isTargetContainerSeasar() {
        return getBasicProperties().isTargetContainerSeasar();
    }

    public boolean isTargetContainerSpring() {
        return getBasicProperties().isTargetContainerSpring();
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
    //                                               JavaDir
    //                                               -------
    public String getJavaDir() {
        return getBasicProperties().getOutputDirectory();
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
    //                                         Prefix/Suffix
    //                                         -------------
    public String getProjectPrefix() {
        return getBasicProperties().getProjectPrefix();
    }

    public String getBasePrefix() {
        return getBasicProperties().getBasePrefix();
    }

    public String getBaseSuffixForEntity() {
        return "";
    }

    // -----------------------------------------------------
    //                                                Filter
    //                                                ------
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

    // -----------------------------------------------------
    //                                   Behavior Query Path
    //                                   -------------------
    public String getBehaviorQueryPathBeginMark() {
        return getBasicProperties().getBehaviorQueryPathBeginMark();
    }

    public String getBehaviorQueryPathEndMark() {
        return getBasicProperties().getBehaviorQueryPathEndMark();
    }

    // -----------------------------------------------------
    //                                            Hot Deploy
    //                                            ----------
    public boolean isAvailableHotDeploy() {
        return getBasicProperties().isAvailableHotDeploy();
    }

    // ===================================================================================
    //                                                                    Dicon Properties
    //                                                                    ================
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

    public String getDBFluteBeansFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteBeansFileName();
    }

    public boolean isQuillDataSourceNameValid() {
        return getProperties().getDBFluteDiconProperties().isQuillDataSourceNameValid();
    }

    public String getQuillDataSourceName() {
        return getProperties().getDBFluteDiconProperties().getQuillDataSourceName();
    }

    // ===================================================================================
    //                                                  Generated Class Package Properties
    //                                                  ==================================
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

    public String getDBMetaPackage() {
        return getProperties().getGeneratedClassPackageProperties().getDBMetaPackage();
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

    // ===================================================================================
    //                                                        Sequence/Identity Properties
    //                                                        ============================
    public String getSequenceDefinitionMapSequence(String flexibleTableName) {
        return getProperties().getSequenceIdentityProperties().getSequenceDefinitionMapSequence(flexibleTableName);
    }

    public String getSequenceReturnType() {
        return getProperties().getSequenceIdentityProperties().getSequenceReturnType();
    }

    public String getIdentityDefinitionMapColumnName(String flexibleTableName) {
        return getProperties().getSequenceIdentityProperties().getIdentityDefinitionMapColumnName(flexibleTableName);
    }

    // ===================================================================================
    //                                                          Optimistic Lock Properties
    //                                                          ==========================
    public String getUpdateDateFieldName() {
        return getProperties().getOptimisticLockProperties().getUpdateDateFieldName();
    }

    public boolean isUpdateDateExceptTable(final String tableName) {
        return getProperties().getOptimisticLockProperties().isUpdateDateExceptTable(tableName);
    }

    public String getVersionNoFieldName() {
        return getProperties().getOptimisticLockProperties().getVersionNoFieldName();
    }

    // ===================================================================================
    //                                                            Common Column Properties
    //                                                            ========================
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

    public boolean isExistCommonColumnSetupElement() {
        return getProperties().getCommonColumnProperties().isExistCommonColumnSetupElement();
    }

    public boolean hasCommonColumnConvertion(String commonColumnName) {
        return getProperties().getCommonColumnProperties().isCommonColumnConvertion(commonColumnName);
    }

    // --------------------------------------
    //                                 insert
    //                                 ------
    public Map<String, Object> getCommonColumnSetupBeforeInsertInterceptorLogicMap() {
        return getProperties().getCommonColumnProperties().getBeforeInsertMap();
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
        return getProperties().getCommonColumnProperties().getBeforeUpdateMap();
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
        return getProperties().getCommonColumnProperties().getBeforeDeleteMap();
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

    // --------------------------------------
    //                         logic handling
    //                         --------------
    public boolean isCommonColumnSetupInvokingLogic(String logic) {
        return getProperties().getCommonColumnProperties().isCommonColumnSetupInvokingLogic(logic);
    }

    public String removeCommonColumnSetupInvokingMark(String logic) {
        return getProperties().getCommonColumnProperties().removeCommonColumnSetupInvokingMark(logic);
    }

    // ===================================================================================
    //                                                           Classification Properties
    //                                                           =========================
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

    public String buildClassificationApplicationComment(Map<String, String> classificationMap) {
        return getClassificationProperties().buildClassificationApplicationComment(classificationMap);
    }
    
    public String buildClassificationCodeAliasVariables(Map<String, String> classificationMap) {
        return getClassificationProperties().buildClassificationCodeAliasVariables(classificationMap);
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

    public Map<String, String> getAllColumnClassificationMap() { // for EntityDefinedCommonColumn
        return getClassificationProperties().getAllColumnClassificationMap();
    }

    public boolean isAllClassificationColumn(String columnName) { // for EntityDefinedCommonColumn
        return getClassificationProperties().isAllClassificationColumn(columnName);
    }

    public String getAllClassificationName(String columnName) { // for EntityDefinedCommonColumn
        return getClassificationProperties().getAllClassificationName(columnName);
    }

    // ===================================================================================
    //                                                         S2Dao Adjustment Properties
    //                                                         ===========================
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

    public String getExtendedAnnotationReaderFactoryClassName() {
        return getProperties().getS2DaoAdjustmentProperties().getExtendedAnnotationReaderFactoryClassName();
    }

    public String getExtendedDaoMetaDataFactoryImplClassName() {
        return getProperties().getS2DaoAdjustmentProperties().getExtendedDaoMetaDataFactoryImplClassName();
    }

    public boolean hasDaoSqlFileEncoding() {
        return getProperties().getOutsideSqlProperties().hasSqlFileEncoding();
    }

    public String getDaoSqlFileEncoding() {
        return getProperties().getOutsideSqlProperties().getSqlFileEncoding();
    }

    // ===================================================================================
    //                                                        Little Adjustment Properties
    //                                                        ============================
    public boolean isAvailableDatabaseDependency() {
        return getProperties().getLittleAdjustmentProperties().isAvailableDatabaseDependency();
    }

    public boolean isAvailableNonPrimaryKeyWritable() {
        return getProperties().getLittleAdjustmentProperties().isAvailableNonPrimaryKeyWritable();
    }

    public boolean isCompatibleSQLExceptionHandlingOldStyle() { // This is for compatibility!
        return getProperties().getLittleAdjustmentProperties().isCompatibleSQLExceptionHandlingOldStyle();
    }

    public boolean isCompatibleS2DaoSQLAnnotationValid() { // This is for compatibility!
        return getProperties().getLittleAdjustmentProperties().isCompatibleS2DaoSQLAnnotationValid();
    }

    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return getProperties().getLittleAdjustmentProperties().isAvailableToLowerInGeneratorUnderscoreMethod();
    }

    // ===================================================================================
    //                                                            Making Option Properties
    //                                                            ========================
    public boolean isMakeDeprecated() {
        return getProperties().getMakingOptionProperties().isMakeDeprecated();
    }

    public boolean isMakeRecentlyDeprecated() {
        return getProperties().getMakingOptionProperties().isMakeRecentlyDeprecated();
    }

    public boolean isMakeConditionQueryEqualEmptyString() {
        return getProperties().getMakingOptionProperties().isMakeConditionQueryEqualEmptyString();
    }

    public boolean isMakeEntityTraceRelation() {
        return getProperties().getMakingOptionProperties().isMakeEntityTraceRelation();
    }

    public boolean isMakeFlatExpansion() {
        return getProperties().getMakingOptionProperties().isMakeFlatExpansion();
    }

    public boolean isMakeDaoInterface() {
        return getProperties().getMakingOptionProperties().isMakeDaoInterface();
    }

    public boolean isMakeClassificationValueLabelList() {
        return getProperties().getMakingOptionProperties().isMakeClassificationValueLabelList();
    }

    // ===================================================================================
    //                                                         Select Parameter Properties
    //                                                         ===========================
    protected DfSelectParamProperties getSelectParamProperties() {
        return getProperties().getSelectParamProperties();
    }

    public boolean isStatementResultSetTypeValid() {
        return getSelectParamProperties().isStatementResultSetTypeValid();
    }

    public String getStatementResultSetType() {
        return getSelectParamProperties().getStatementResultSetType();
    }

    public String getStatementResultSetConcurrency() {
        return getSelectParamProperties().getStatementResultSetConcurrency();
    }

    // ===================================================================================
    //                                                                     Buri Properties
    //                                                                     ===============
    public boolean isBuriGenerateBao() {
        return getProperties().getBuriProperties().isGenerateBao();
    }

    public String getBuriBaseBaoPackage() {
        return getProperties().getBuriProperties().getBaseBaoPackage();
    }

    public String getBuriExtendedBaoPackage() {
        return getProperties().getBuriProperties().getExtendedBaoPackage();
    }

    public List<Table> getBuriTargetTableList() {
        final ArrayList<Table> buriTargetTableList = new ArrayList<Table>();
        final List<Table> tableList = getTableList();
        for (Table table : tableList) {
            if (table.isBuriTarget()) {
                buriTargetTableList.add(table);
            }
        }
        return buriTargetTableList;
    }

    // ===================================================================================
    //                                                         SQL Log Registry Properties
    //                                                         ===========================
    public boolean isSqlLogRegistryValid() {
        return getProperties().getSqlLogRegistryProperties().isValid();
    }

    public int getSqlLogRegistryLimitSize() {
        return getProperties().getSqlLogRegistryProperties().getLimitSize();
    }

    // ===================================================================================
    //                                                             Type Mapping Properties
    //                                                             =======================
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

    // ===================================================================================
    //                                                               OutsideSql Properties
    //                                                               =====================
    public boolean hasSqlFileEncoding() {
        return getProperties().getOutsideSqlProperties().hasSqlFileEncoding();
    }

    public String getSqlFileEncoding() {
        return getProperties().getOutsideSqlProperties().getSqlFileEncoding();
    }

    public boolean isOutsideSqlPackageValid() {
        return getProperties().getOutsideSqlProperties().isSqlPackageValid();
    }

    public String getOutsideSqlPackage() {
        return getProperties().getOutsideSqlProperties().getSqlPackage();
    }

    public boolean isDefaultPackageValid() {
        return getProperties().getOutsideSqlProperties().isDefaultPackageValid();
    }

    public String getDefaultPackage() {
        return getProperties().getOutsideSqlProperties().getDefaultPackage();
    }

    public boolean isOmitResourcePathPackageValid() {
        return getProperties().getOutsideSqlProperties().isOmitResourcePathPackageValid();
    }

    public String getOmitResourcePathPackage() {
        return getProperties().getOutsideSqlProperties().getOmitResourcePathPackage();
    }

    public boolean isOmitFileSystemPathPackageValid() {
        return getProperties().getOutsideSqlProperties().isOmitFileSystemPathPackageValid();
    }

    public String getOmitFileSystemPathPackage() {
        return getProperties().getOutsideSqlProperties().getOmitFileSystemPathPackage();
    }

    public boolean isOmitDirectoryPackageValid() {
        return getProperties().getLittleAdjustmentProperties().isOmitDirectoryPackageValid();
    }

    public String getOmitDirectoryPackage() {
        return getProperties().getLittleAdjustmentProperties().getOmitDirectoryPackage();
    }

    public boolean isFlatDirectoryPackageValid() {
        return getProperties().getLittleAdjustmentProperties().isFlatDirectoryPackageValid();
    }

    public String getFlatDirectoryPackage() {
        return getProperties().getLittleAdjustmentProperties().getFlatDirectoryPackage();
    }

    public boolean isSql2EntityPlainEntity() {
        return false;
    }

    public String getSql2EntityBaseEntityPackage() {
        return getProperties().getOutsideSqlProperties().getBaseEntityPackage();
    }

    public String getSql2EntityDBMetaPackage() {
        return getProperties().getOutsideSqlProperties().getDBMetaPackage();
    }

    public String getSql2EntityExtendedEntityPackage() {
        return getProperties().getOutsideSqlProperties().getExtendedEntityPackage();
    }

    public String getSql2EntityBaseCursorPackage() {
        return getProperties().getOutsideSqlProperties().getBaseCursorPackage();
    }

    public String getSql2EntityExtendedCursorPackage() {
        return getProperties().getOutsideSqlProperties().getExtendedCursorPackage();
    }

    public String getSql2EntityBaseParameterBeanPackage() {
        return getProperties().getOutsideSqlProperties().getBaseParameterBeanPackage();
    }

    public String getSql2EntityExtendedParameterBeanPackage() {
        return getProperties().getOutsideSqlProperties().getExtendedParameterBeanPackage();
    }

    // ===================================================================================
    //                                                                 Document Properties
    //                                                                 ===================
    public boolean isAliasDelimiterInDbCommentValid() {
        return getProperties().getDocumentProperties().isAliasDelimiterInDbCommentValid();
    }

    public boolean isEntityJavaDocDbCommentValid() {
        return getProperties().getDocumentProperties().isEntityJavaDocDbCommentValid();
    }

    // ===================================================================================
    //                                                               Simple DTO Properties
    //                                                               =====================
    public boolean hasSimpleDtoDefinition() {
        return getProperties().getSimpleDtoProperties().hasSimpleDtoDefinition();
    }

    public String getSimpleDtoBaseDtoPackage() {
        return getProperties().getSimpleDtoProperties().getBaseDtoPackage();
    }

    public String getSimpleDtoExtendedDtoPackage() {
        return getProperties().getSimpleDtoProperties().getExtendedDtoPackage();
    }

    public String getSimpleDtoBaseDtoPrefix() {
        return getProperties().getSimpleDtoProperties().getBaseDtoPrefix();
    }

    public String getSimpleDtoBaseDtoSuffix() {
        return getProperties().getSimpleDtoProperties().getBaseDtoSuffix();
    }

    public String getSimpleDtoExtendedDtoPrefix() {
        return getProperties().getSimpleDtoProperties().getExtendedDtoPrefix();
    }

    public String getSimpleDtoExtendedDtoSuffix() {
        return getProperties().getSimpleDtoProperties().getExtendedDtoSuffix();
    }

    public String getSimpleDtoDtoMapperPackage() {
        return getProperties().getSimpleDtoProperties().getMapperPackage();
    }

    public boolean isSimpleDtoUseDtoMapper() {
        return getProperties().getSimpleDtoProperties().isUseDtoMapper();
    }

    // ===================================================================================
    //                                                                 Flex DTO Properties
    //                                                                 ===================
    public boolean hasFlexDtoDefinition() {
        return getProperties().getFlexDtoProperties().hasFlexDtoDefinition();
    }

    public boolean isFlexDtoOverrideExtended() {
        return getProperties().getFlexDtoProperties().isOverrideExtended();
    }

    public String getFlexDtoBaseDtoPackage() {
        return getProperties().getFlexDtoProperties().getBaseDtoPackage();
    }

    public String getFlexDtoExtendedDtoPackage() {
        return getProperties().getFlexDtoProperties().getExtendedDtoPackage();
    }

    public String getFlexDtoBaseDtoPrefix() {
        return getProperties().getFlexDtoProperties().getBaseDtoPrefix();
    }

    public String getFlexDtoBaseDtoSuffix() {
        return getProperties().getFlexDtoProperties().getBaseDtoSuffix();
    }

    public String getFlexDtoExtendedDtoPrefix() {
        return getProperties().getFlexDtoProperties().getExtendedDtoPrefix();
    }

    public String getFlexDtoExtendedDtoSuffix() {
        return getProperties().getFlexDtoProperties().getExtendedDtoSuffix();
    }

    // ===================================================================================
    //                                                                   S2JDBC Properties
    //                                                                   =================
    public boolean hasS2jdbcDefinition() {
        return getProperties().getS2jdbcProperties().hasS2jdbcDefinition();
    }

    public String getS2jdbcBaseEntityPackage() {
        return getProperties().getS2jdbcProperties().getBaseEntityPackage();
    }

    public String getS2jdbcExtendedEntityPackage() {
        return getProperties().getS2jdbcProperties().getExtendedEntityPackage();
    }

    public String getS2jdbcBaseEntityPrefix() {
        return getProperties().getS2jdbcProperties().getBaseEntityPrefix();
    }

    public String getS2jdbcBaseEntitySuffix() {
        return getProperties().getS2jdbcProperties().getBaseEntitySuffix();
    }

    // ===================================================================================
    //                                                                    Other Properties
    //                                                                    ================
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
    //                                                  Component Name Helper for Template
    //                                                  ==================================
    public String getDaoSelectorComponentName() {
        return filterProjectSuffixForComponentName("daoSelector");
    }

    public String getBehaviorSelectorComponentName() {
        return filterProjectSuffixForComponentName("behaviorSelector");
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

    // ===================================================================================
    //                                                        Type Map Helper for Template
    //                                                        ============================
    public String convertJavaNativeByJdbcType(String jdbcType) {
        try {
            return TypeMap.findJavaNativeString(jdbcType, null, null);
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
     * @param algorithmName Algorithm name.
     * @param inputs Inputs.
     * @return Generated name.
     */
    protected String generateName(String algorithmName, List<?> inputs) {
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

    // ===================================================================================
    //                                                         General Helper for Template
    //                                                         ===========================
    // -----------------------------------------------------
    //                                             Character
    //                                             ---------
    public String getSharp() {
        return "#";
    }

    public String getDollar() {
        return "$";
    }

    // -----------------------------------------------------
    //                                               Comment
    //                                               -------
    public String getOverrideComment() {
        return "The override.";
    }

    public String getImplementComment() {
        return "The implementation.";
    }

    // -----------------------------------------------------
    //                                               Logging
    //                                               -------
    public void info(String msg) {
        _log.info(msg);
    }

    public void debug(String msg) {
        _log.debug(msg);
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    public String getTimestampExpression() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(timestamp);
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
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

    public boolean isInitNumber(String str) {
        if (str == null) {
            String msg = "Argument[str] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (str.length() == 0) {
            return false;
        }
        try {
            Integer.valueOf(str.substring(0, 1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // -----------------------------------------------------
    //                                                   Map
    //                                                   ---
    public String getMapValue(Map<?, ?> map, String key) {
        final Object value = map.get(key);
        return value != null ? (String) value : "";
    }

    // -----------------------------------------------------
    //                                                   I/O
    //                                                   ---
    public void makeDirectory(String packagePath) {
        FileUtil.mkdir(getGeneratorInstance().getOutputPath() + "/" + packagePath);
    }

    public String getPackageAsPath(String pckge) {
        final DfPackagePathHandler handler = new DfPackagePathHandler(getProperties().getLittleAdjustmentProperties());
        return handler.getPackageAsPath(pckge);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Creates a string representation of this Database.
     * The representation is given in xml format.
     * @return String representation in XML. (NotNull)
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<database name=\"").append(getName()).append('"').append(">\n");
        for (Iterator<Table> i = _tableList.iterator(); i.hasNext();) {
            sb.append(i.next());
        }
        sb.append("</database>");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = (name == null ? "default" : name);
    }

    /**
     * Get the value of defaultJavaNamingMethod which specifies the
     * method for converting schema names for table and column to Java names.
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

    public void setAppData(AppData appData) {
        _appData = appData;
    }

    public AppData getAppData() {
        return _appData;
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
}