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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.velocity.texen.util.FileUtil;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfDatabaseConfig;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.helper.io.filedelete.OldTableClassDeletor;
import org.seasar.dbflute.helper.jdbc.metadata.DfProcedureHandler.DfProcedureColumnType;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.initializer.IncludeQueryInitializer;
import org.seasar.dbflute.logic.pmb.PmbMetaDataPropertyOptionClassification;
import org.seasar.dbflute.logic.pmb.PmbMetaDataPropertyOptionFinder;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfSelectParamProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties.CommonColumnSetupResource;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties.SequenceDefinitionMapChecker;
import org.seasar.dbflute.task.DfSql2EntityTask.DfParameterBeanMetaData;
import org.seasar.dbflute.torque.DfAdditionalForeignKeyInitializer;
import org.seasar.dbflute.util.DfStringUtil;
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
    protected String _databaseType;

    protected List<Table> _tableList = new ArrayList<Table>(100);

    protected String _name;

    protected String _pkg;// TODO: @jflute -- Unused?

    protected String _defaultIdMethod;

    protected String _defaultJavaType;

    protected String _defaultJavaNamingMethod;

    protected AppData _dbParent;

    protected DfFlexibleNameMap<String, Table> _flexibleTableMap = new DfFlexibleNameMap<String, Table>();

    protected boolean _isHeavyIndexing;

    /** The meta data of parameter bean. */
    protected Map<String, DfParameterBeanMetaData> _pmbMetaDataMap;

    // ===================================================================================
    //                                                                             Loading
    //                                                                             =======
    /**
     * Load the database object from an xml tag.
     * @param attrib the xml attributes
     */
    public void loadFromXML(Attributes attrib) {
        setName(attrib.getValue("name"));
        _pkg = attrib.getValue("package");// TODO: @jflute -- Unused?
        _defaultJavaType = attrib.getValue("defaultJavaType");
        _defaultIdMethod = attrib.getValue("defaultIdMethod");
        _defaultJavaNamingMethod = attrib.getValue("defaultJavaNamingMethod");
        if (_defaultJavaNamingMethod == null) {
            _defaultJavaNamingMethod = NameGenerator.CONV_METHOD_UNDERSCORE;
        }
        _isHeavyIndexing = "true".equals(attrib.getValue("heavyIndexing"));

    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    /**
     * Return an array of all tables
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
     * @param name table name
     * @return A Table object.  If it does not exist it returns null
     */
    public Table getTable(String name) {
        return (Table) _flexibleTableMap.get(name);
    }

    /**
     * An utility method to add a new table from an xml attribute.
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
     * @param tbl the table to add
     */
    public void addTable(Table tbl) {
        tbl.setDatabase(this);
        _tableList.add(tbl);
        _flexibleTableMap.put(tbl.getName(), tbl);
        tbl.setPackage(getPackage());
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

    /**
     * Creats a string representation of this Database.
     * The representation is given in xml format.
     * @return string representation in xml
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("<database name=\"").append(getName()).append('"').append(" package=\"").append(getPackage())
                .append('"').append(" defaultIdMethod=\"").append(getDefaultIdMethod()).append('"').append(">\n");

        for (Iterator<Table> i = _tableList.iterator(); i.hasNext();) {
            result.append(i.next());
        }

        result.append("</database>");
        return result.toString();
    }

    // ===================================================================================
    //                                                                    Check Properties
    //                                                                    ================
    public void checkProperties() {
        getProperties().getSequenceIdentityProperties().checkSequenceDefinitionMap(new SequenceDefinitionMapChecker() {
            public boolean hasTable(String tableName) {
                return getTable(tableName) != null;
            }
        });
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
        IncludeQueryInitializer initializer = new IncludeQueryInitializer();
        initializer.setIncludeQueryProperties(getProperties().getIncludeQueryProperties());
        initializer.setTableFinder(new IncludeQueryInitializer.TableFinder() {
            public Table findTable(String name) {
                return getTable(name);
            }
        });
        initializer.initializeIncludeQuery();
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
        return getBasicProperties().getOutputDirectory();
    }

    // -----------------------------------------------------
    //                                              Database
    //                                              --------
    public boolean isDatabaseOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    public boolean isDatabaseDB2() {
        return getBasicProperties().isDatabaseDB2();
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
        return true;
    }

    public boolean isAvailableGenerics() {
        return true;
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
        return getBasicProperties().getBasePrefix();
    }

    public String getBaseSuffixForEntity() {
        return "";
    }

    // ===============================================================================
    //                                                Properties - Behavior Query Path
    //                                                ================================
    public String getBehaviorQueryPathBeginMark() {
        return getBasicProperties().getBehaviorQueryPathBeginMark();
    }

    public String getBehaviorQueryPathEndMark() {
        return getBasicProperties().getBehaviorQueryPathEndMark();
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

    public String getDBFluteBeansFileName() {
        return getProperties().getDBFluteDiconProperties().getDBFluteBeansFileName();
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

    // ===================================================================================
    //                                                                        Select-Param
    //                                                                        ============
    public DfSelectParamProperties getSelectParamProperties() {
        return getProperties().getSelectParamProperties();
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

    public boolean isMakeEntityTraceRelation() {
        return getProperties().getSourceReductionProperties().isMakeEntityTraceRelation();
    }

    public boolean isMakeBehaviorNoConditionLoadReferrer() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorNoConditionLoadReferrer();
    }

    public boolean isMakeBehaviorLoopUpdate() {
        return getProperties().getSourceReductionProperties().isMakeBehaviorLoopUpdate();
    }

    public boolean isMakeFlatExpansion() {
        return getProperties().getSourceReductionProperties().isMakeFlatExpansion();
    }

    public boolean isMakeDBMetaStaticDefinition() {
        return getProperties().getSourceReductionProperties().isMakeDBMetaStaticDefinition();
    }

    public boolean isMakeDBMetaJDBCSupport() {
        return getProperties().getSourceReductionProperties().isMakeDBMetaJDBCSupport();
    }

    public boolean isMakeDBMetaCommonColumnHandling() {
        return getProperties().getSourceReductionProperties().isMakeDBMetaCommonColumnHandling();
    }

    public boolean isMakeClassificationValueLabelList() {
        return getProperties().getSourceReductionProperties().isMakeClassificationValueLabelList();
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
    public String getBehaviorDelegateModifier() {
        return "protected";
    }

    public boolean isUseBuri() {
        return getProperties().getLittleAdjustmentProperties().isUseBuri();
    }

    public boolean isUseTeeda() {
        return getProperties().getLittleAdjustmentProperties().isUseTeeda();
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

    public String getSql2EntityBaseParameterBeanPackage() {
        return getProperties().getOutsideSqlProperties().getBaseParameterBeanPackage();
    }

    public String getSql2EntityExtendedParameterBeanPackage() {
        return getProperties().getOutsideSqlProperties().getExtendedParameterBeanPackage();
    }

    // ===================================================================================
    //                                                                          Simple DTO
    //                                                                          ==========
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
    //                                                                            Flex DTO
    //                                                                            ========
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
    //                                                                       S2JDBC Entity
    //                                                                       =============
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

    //====================================================================================
    //                                                                     databaseInfoMap
    //                                                                     ===============
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

    protected Map<String, Map<String, String>> getDatabaseDefinitionMap() {
        final DfDatabaseConfig config = new DfDatabaseConfig();
        return config.analyzeDatabaseBaseInfo();
    }

    public String getDaoGenDbName() {
        final Map<String, String> databaseInfoMap = getDatabaseInfoMap();
        final String daoGenDbName = (String) databaseInfoMap.get("daoGenDbName");
        if (daoGenDbName == null || daoGenDbName.trim().length() == 0) {
            String msg = "The database doesn't have daoGenDbName in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return daoGenDbName;
    }

    public String getWildCard() {
        final Map<String, String> databaseInfoMap = getDatabaseInfoMap();
        final String wildCard = (String) databaseInfoMap.get("wildCard");
        if (wildCard == null || wildCard.trim().length() == 0) {
            String msg = "The database doesn't have wildCard in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return wildCard;
    }

    public String getSequenceNextSql() {
        final Map<String, String> databaseInfoMap = getDatabaseInfoMap();
        final String sequenceNextSql = (String) databaseInfoMap.get("sequenceNextSql");
        if (sequenceNextSql == null || sequenceNextSql.trim().length() == 0) {
            String msg = "The database doesn't have sequenceNextSql in the property[databaseInfoMap]: ";
            throw new IllegalStateException(msg + databaseInfoMap);
        }
        return sequenceNextSql;
    }

    // ===================================================================================
    //                                                                           Hard Code
    //                                                                           =========
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

    public String getSharp() {
        return "#";
    }

    public String getDollar() {
        return "$";
    }

    public String getOverrideComment() {
        return "The override.";
    }

    public String getImplementComment() {
        return "The implementation.";
    }

    // ===============================================================================
    //                                                                         Logging
    //                                                                         =======
    public void info(String msg) {
        _log.info(msg);
    }

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
    public String getMapValue(Map<?, ?> map, String key) {
        final Object value = map.get(key);
        return value != null ? (String) value : "";
    }

    // ===============================================================================
    //                                                                         TypeMap
    //                                                                         =======
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

    public void makeDirectory(String packagePath) {
        FileUtil.mkdir(getGeneratorHandler().getOutputPath() + "/" + packagePath);
    }

    // ===============================================================================
    //                                                          Delete Old Table Class
    //                                                          ======================
    public void deleteOldTableClass() {
        if (!getProperties().getLittleAdjustmentProperties().isDeleteOldTableClass()) {
            return;
        }
        info("// /- - - - - - - - - - - - -");
        info("// Delete old table classes!");
        info("// - - - - - - - - - -/");
        deleteOldTableClass_for_BaseBehavior();
        deleteOldTableClass_for_BaseDao();
        deleteOldTableClass_for_BaseEntity();
        deleteOldTableClass_for_DBMeta();
        deleteOldTableClass_for_BaseConditionBean();
        deleteOldTableClass_for_AbstractBaseConditionQuery();
        deleteOldTableClass_for_BaseConditionQuery();
        deleteOldTableClass_for_NestSelectSetupper();
        deleteOldTableClass_for_ExtendedConditionBean();
        deleteOldTableClass_for_ExtendedConditionQuery();
        deleteOldTableClass_for_ExtendedConditionInlineQuery();
        deleteOldTableClass_for_ExtendedBehavior();
        deleteOldTableClass_for_ExtendedDao();
        deleteOldTableClass_for_ExtendedEntity();
        info(" ");
    }

    protected List<String> _deletedOldTableBaseBehaviorList;

    public void deleteOldTableClass_for_BaseBehavior() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getBaseBehaviorClassName();
            }
        };
        final String packagePath = getBaseBehaviorPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "Bhv", setupper);
        _deletedOldTableBaseBehaviorList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseBehaviorList);
    }

    protected List<String> _deletedOldTableBaseDaoList;

    public void deleteOldTableClass_for_BaseDao() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getBaseDaoClassName();
            }
        };
        final String packagePath = getBaseDaoPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "Dao", setupper);
        _deletedOldTableBaseDaoList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseDaoList);
    }

    protected List<String> _deletedOldTableBaseEntityList;

    public void deleteOldTableClass_for_BaseEntity() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getBaseEntityClassName();
            }
        };
        final String packagePath = getBaseEntityPackage();
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, null, setupper);
        _deletedOldTableBaseEntityList = deletor.deleteOldTableClass();
        showDeleteOldTableFile(_deletedOldTableBaseEntityList);
    }

    public void deleteOldTableClass_for_DBMeta() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getDBMetaClassName();
            }
        };
        final String packagePath = getDBMetaPackage();
        final String classPrefix = getProjectPrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "Dbm", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_BaseConditionBean() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getBaseConditionBeanClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".bs";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CB", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_AbstractBaseConditionQuery() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getAbstractBaseConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.bs";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix() + "Abstract" + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_BaseConditionQuery() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getBaseConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.bs";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix() + getBasePrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_NestSelectSetupper() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getNestSelectSetupperClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".nss";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "Nss", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionBean() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionBeanClassName();
            }
        };
        final String packagePath = getConditionBeanPackage();
        final String classPrefix = getProjectPrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CB", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionQuery() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedConditionInlineQuery() {
        final NotDeleteClassNameSetupper setupper = new NotDeleteClassNameSetupper() {
            public String setup(Table table) {
                return table.getExtendedConditionInlineQueryClassName();
            }
        };
        final String packagePath = getConditionBeanPackage() + ".cq.ciq";// TODO: @jflute -- Resolve language
        final String classPrefix = getProjectPrefix();
        final OldTableClassDeletor deletor = createOldTableClassDeletor(packagePath, classPrefix, "CIQ", setupper);
        showDeleteOldTableFile(deletor.deleteOldTableClass());
    }

    public void deleteOldTableClass_for_ExtendedBehavior() {
        if (_deletedOldTableBaseBehaviorList == null || _deletedOldTableBaseBehaviorList.isEmpty()) {
            return;
        }
        final String outputPath = getGeneratorHandler().getOutputPath();
        final String packagePath = getExtendedBehaviorPackage();
        final String dirPath = outputPath + "/" + DfStringUtil.replace(packagePath, ".", "/");
        for (String baseClassName : _deletedOldTableBaseBehaviorList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                _log.info("deleteOldTableClass('" + extendedClassName + "');");
            }
        }
    }

    public void deleteOldTableClass_for_ExtendedDao() {
        if (_deletedOldTableBaseDaoList == null || _deletedOldTableBaseDaoList.isEmpty()) {
            return;
        }
        final String outputPath = getGeneratorHandler().getOutputPath();
        final String packagePath = getExtendedDaoPackage();
        final String dirPath = outputPath + "/" + DfStringUtil.replace(packagePath, ".", "/");
        for (String baseClassName : _deletedOldTableBaseDaoList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                _log.info("deleteOldTableClass('" + extendedClassName + "');");
            }
        }
    }

    public void deleteOldTableClass_for_ExtendedEntity() {
        if (_deletedOldTableBaseEntityList == null || _deletedOldTableBaseEntityList.isEmpty()) {
            return;
        }
        final String outputPath = getGeneratorHandler().getOutputPath();
        final String packagePath = getExtendedEntityPackage();
        final String dirPath = outputPath + "/" + DfStringUtil.replace(packagePath, ".", "/");
        for (String baseClassName : _deletedOldTableBaseEntityList) {
            final int prefixLength = getProjectPrefix().length() + getBasePrefix().length();
            final String extendedClassName = getProjectPrefix() + baseClassName.substring(prefixLength);
            final File file = new File(dirPath + "/" + extendedClassName + "." + getClassFileExtension());
            if (file.exists()) {
                file.delete();
                _log.info("deleteOldTableClass('" + extendedClassName + "');");
            }
        }
    }

    protected void showDeleteOldTableFile(List<String> deletedClassNameList) {
        for (String className : deletedClassNameList) {
            _log.info("deleteOldTableClass('" + className + "');");
        }
    }

    protected OldTableClassDeletor createOldTableClassDeletor(String packagePath, String classPrefix,
            String classSuffix, NotDeleteClassNameSetupper notDeleteClassNameSetupper) {
        final OldTableClassDeletor deletor = new OldTableClassDeletor();
        deletor.setPackagePath(packagePath);
        deletor.setClassPrefix(classPrefix);
        deletor.setClassSuffix(classSuffix);
        deletor.setClassExtension(getClassFileExtension());
        final Set<String> notDeleteClassNameSet = new HashSet<String>();
        final List<Table> tableList = getTableList();
        for (Table table : tableList) {
            final String baseBehaviorClassName = notDeleteClassNameSetupper.setup(table);
            notDeleteClassNameSet.add(baseBehaviorClassName);
        }
        deletor.setNotDeleteClassNameSet(notDeleteClassNameSet);
        return deletor;
    }

    protected static interface NotDeleteClassNameSetupper {
        public String setup(Table table);
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public void enableGenerateOutputDirectory() {
        getGeneratorHandler().setOutputPath(getProperties().getBasicProperties().getOutputDirectory());
    }

    public void enableSql2EntityOutputDirectory() {
        getGeneratorHandler().setOutputPath(getProperties().getOutsideSqlProperties().getSql2EntityOutputDirectory());
    }

    public void enableFlexDtoOutputDirectory() {
        getGeneratorHandler().setOutputPath(getProperties().getFlexDtoProperties().getOutputDirectory());
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    public DfGenerator getGeneratorHandler() {
        return DfGenerator.getInstance();
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

    public String getPackage() {
        return _pkg;
    }

    public void setPackage(String v) {
        this._pkg = v;
    }

    public String getDefaultIdMethod() {
        return _defaultIdMethod;
    }

    public void setDefaultIdMethod(String v) {
        this._defaultIdMethod = v;
    }

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
}