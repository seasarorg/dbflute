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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
import org.apache.velocity.texen.util.FileUtil;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.config.DfDatabaseNameMapping;
import org.seasar.dbflute.exception.DfColumnNotFoundException;
import org.seasar.dbflute.friends.velocity.DfGenerator;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.jdbc.context.DfDataSourceContext;
import org.seasar.dbflute.logic.doc.historyhtml.DfSchemaHistory;
import org.seasar.dbflute.logic.generate.deletefile.DfOldClassHandler;
import org.seasar.dbflute.logic.generate.exmange.DfCopyrightResolver;
import org.seasar.dbflute.logic.generate.exmange.DfSerialVersionUIDResolver;
import org.seasar.dbflute.logic.generate.packagepath.DfPackagePathHandler;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfProcedureExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMeta;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlCollector;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfOutsideSqlPack;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbGenerationHandler;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPmbMetaData;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties.SequenceDefinitionMapChecker;
import org.seasar.dbflute.properties.assistant.DfTableFinder;
import org.seasar.dbflute.properties.assistant.commoncolumn.CommonColumnSetupResource;
import org.seasar.dbflute.properties.initializer.DfAdditionalForeignKeyInitializer;
import org.seasar.dbflute.properties.initializer.DfAdditionalPrimaryKeyInitializer;
import org.seasar.dbflute.properties.initializer.DfAdditionalUniqueKeyInitializer;
import org.seasar.dbflute.properties.initializer.DfIncludeQueryInitializer;
import org.seasar.dbflute.util.Srl;
import org.xml.sax.Attributes;

/**
 * A class for holding application data structures. <br />
 * DBFlute treats all tables containing other schema's as one database object.
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
    protected Integer _version;
    protected String _name;

    // -----------------------------------------------------
    //                                               AppData
    //                                               -------
    protected AppData _appData;
    protected AppData _sql2entitySchemaData; // when sql2entity only

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    // use duplicate collection to suppress a little performance cost
    // because tables are frequently referred
    protected List<Table> _tableList = new ArrayList<Table>(100); // for ordering
    protected StringKeyMap<Table> _tableMap = StringKeyMap.createAsFlexible(); // for key-map

    // -----------------------------------------------------
    //                                         ParameterBean
    //                                         -------------
    /** The meta data of parameter bean. */
    protected Map<String, DfPmbMetaData> _pmbMetaDataMap; // when sql2entity only

    // -----------------------------------------------------
    //                                        Schema History
    //                                        --------------
    protected DfSchemaHistory _schemaHistory; // when doc only

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    protected String _databaseType;
    protected String _defaultJavaNamingMethod;
    protected boolean _skipDeleteOldClass;

    // *unused on DBFlute
    //protected String _pkg;
    //protected String _defaultIdMethod;
    //protected String _defaultJavaType;
    //protected boolean _isHeavyIndexing;

    // ===================================================================================
    //                                                                             Version
    //                                                                             =======
    public void initializeVersion(Integer version) {
        DfBuildProperties.getInstance().setVersion(version);
    }

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
    public List<Table> getTableList() {
        return _tableList;
    }

    public Table[] getTables() { // old style, for compatibility
        final List<Table> tableList = getTableList();
        return tableList.toArray(new Table[tableList.size()]);
    }

    public List<Table> getTableDisplaySortedList() {
        final Comparator<Table> tableDisplayOrderBy = getProperties().getDocumentProperties().getTableDisplayOrderBy();
        final TreeSet<Table> tableSet = new TreeSet<Table>(tableDisplayOrderBy);
        tableSet.addAll(getTableList());
        return new ArrayList<Table>(tableSet);
    }

    public Table getTable(String name) {
        return _tableMap.get(name);
    }

    /**
     * Add table from attributes of SchemaXML.
     * @param attrib The attributes of SchemaXML. (NotNull)
     * @param tableFilter The filter of table. (NullAllowed)
     * @return The instance of added table. (NullAllowed: if null, means the table is excepted) 
     */
    public Table addTable(Attributes attrib, XmlReadingTableFilter tableFilter) {
        final Table tbl = new Table();
        tbl.setDatabase(this);
        if (!tbl.loadFromXML(attrib, tableFilter)) {
            return null;
        }
        addTable(tbl);
        return tbl;
    }

    public void addTable(Table tbl) {
        tbl.setDatabase(this);
        _tableList.add(tbl);
        _tableMap.put(tbl.getName(), tbl);
    }

    /**
     * Initialize detail points after loading as final process.
     */
    public void doFinalInitialization() {
        final List<Table> tableList = getTableList();
        for (Table table : tableList) {
            table.doFinalInitialization();

            // setup reverse relations and check existences
            final List<ForeignKey> fkList = table.getForeignKeyList();
            for (ForeignKey fk : fkList) {
                final String foreignTableName = fk.getForeignTableName();
                final Table foreignTable = getTable(foreignTableName);

                // check an existence of foreign table
                if (foreignTable == null) { // may be except table generate-only
                    table.removeForeignKey(fk);
                    continue;
                }

                // adjust reverse relation
                final List<ForeignKey> refererList = foreignTable.getRefererList();
                if ((refererList == null || !refererList.contains(fk))) {
                    foreignTable.addReferrer(fk);
                }

                // local column references
                final List<String> localColumnNameList = fk.getLocalColumnNameList();
                for (String localColumnName : localColumnNameList) {
                    final Column localColumn = table.getColumn(localColumnName);
                    // give notice of a schema inconsistency.
                    // note we do not prevent the npe as there is nothing
                    // that we can do, if it is to occur.
                    if (localColumn == null) {
                        String msg = "Not found the column in the table:";
                        msg = msg + " fk=" + fk.getName() + " foreignTableName=" + foreignTableName;
                        msg = msg + " table=" + table + " localColumn=" + localColumnName;
                        throw new DfColumnNotFoundException(msg);
                    }
                    // column has no information of its foreign keys
                }

                // foreign column references
                final List<String> foreignColumnNameList = fk.getForeignColumnNameList();
                for (String foreignColumnName : foreignColumnNameList) {
                    final Column foreignColumn = foreignTable.getColumn(foreignColumnName);
                    // if the foreign column does not exist, we may have an
                    // external reference or a misspelling
                    if (foreignColumn == null) {
                        String msg = "Not found the column in the table:";
                        msg = msg + " fk=" + fk.getName() + " foreignTableName=" + foreignTableName;
                        msg = msg + " table=" + table + " foreignColumn=" + foreignColumnName;
                        throw new DfColumnNotFoundException(msg);
                    } else {
                        foreignColumn.addReferrer(fk);
                    }
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      Parameter Bean
    //                                                                      ==============
    protected DfPmbGenerationHandler _pmbBasicHandler;

    protected DfPmbGenerationHandler getPmbBasicHandler() {
        if (_pmbBasicHandler != null) {
            return _pmbBasicHandler;
        }
        _pmbBasicHandler = new DfPmbGenerationHandler(_pmbMetaDataMap);
        return _pmbBasicHandler;
    }

    // -----------------------------------------------------
    //                                              MetaData
    //                                              --------
    public Collection<DfPmbMetaData> getPmbMetaDataList() {
        return getPmbBasicHandler().getPmbMetaDataList();
    }

    public boolean isExistPmbMetaData() {
        return getPmbBasicHandler().isExistPmbMetaData();
    }

    public String getPmbMetaDataBusinessName(String className) {
        return getPmbBasicHandler().getBusinessName(className);
    }

    public String getPmbMetaDataAbstractDefinition(String className) {
        return getPmbBasicHandler().getAbstractDefinition(className);
    }

    public String getPmbMetaDataSuperClassDefinition(String className) {
        return getPmbBasicHandler().getSuperClassDefinition(className);
    }

    public String getPmbMetaDataInterfaceDefinition(String className) {
        return getPmbBasicHandler().getInterfaceDefinition(className);
    }

    public boolean hasPmbMetaDataPagingExtension(String className) {
        return getPmbBasicHandler().hasPagingExtension(className);
    }

    public boolean hasPmbMetaDataCheckSafetyResult(String className) {
        return getPmbBasicHandler().hasPmbMetaDataCheckSafetyResult(className);
    }

    public Set<String> getPmbMetaDataPropertySet(String className) {
        return getPmbBasicHandler().getPropertySet(className);
    }

    public String getPmbMetaDataPropertyType(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyType(className, propertyName);
    }

    public String getPmbMetaDataPropertyColumnName(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyColumnName(className, propertyName);
    }

    public String getPmbMetaDataPropertyTypeRemovedCSharpNullable(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyTypeRemovedCSharpNullable(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyJavaNativeStringObject(String className, String propertyName) {
        return getPmbBasicHandler().isPmbMetaDataPropertyJavaNativeStringObject(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyJavaNativeNumberObject(String className, String propertyName) {
        return getPmbBasicHandler().isPmbMetaDataPropertyJavaNativeNumberObject(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyJavaNativeBooleanObject(String className, String propertyName) {
        return getPmbBasicHandler().isPmbMetaDataPropertyJavaNativeBooleanObject(className, propertyName);
    }

    // -----------------------------------------------------
    //                                            Typed Info
    //                                            ----------
    public boolean isPmbMetaDataTypedParameterBean(String className) {
        return getPmbBasicHandler().isTypedParameterBean(className);
    }

    public boolean isPmbMetaDataTypedSelectPmb(String className) {
        return getPmbBasicHandler().isTypedSelectPmb(className);
    }

    public boolean isPmbMetaDataTypedUpdatePmb(String className) {
        return getPmbBasicHandler().isTypedUpdatePmb(className);
    }

    public boolean isPmbMetaDataTypedReturnEntityPmb(String className) {
        return getPmbBasicHandler().isTypedReturnEntityPmb(className);
    }

    public boolean isPmbMetaDataTypedReturnCustomizeEntityPmb(String className) {
        return getPmbBasicHandler().isTypedReturnCustomizeEntityPmb(className);
    }

    public boolean isPmbMetaDataTypedReturnDomainEntityPmb(String className) {
        return getPmbBasicHandler().isTypedReturnDomainEntityPmb(className);
    }

    public String getPmbMetaDataBehaviorClassName(String className) {
        return getPmbBasicHandler().getBehaviorClassName(className);
    }

    public String getPmbMetaDataBehaviorQueryPath(String className) {
        return getPmbBasicHandler().getBehaviorQueryPath(className);
    }

    public String getPmbMetaDataCustomizeEntityType(String className) {
        return getPmbBasicHandler().getCustomizeEntityType(className);
    }

    // -----------------------------------------------------
    //                                             Procedure
    //                                             ---------
    public boolean isPmbMetaDataForProcedure(String className) {
        return getPmbBasicHandler().isForProcedure(className);
    }

    public String getPmbMetaDataProcedureName(String className) {
        return getPmbBasicHandler().getProcedureName(className);
    }

    public boolean isPmbMetaDataProcedureCalledBySelect(String className) {
        return getPmbBasicHandler().isProcedureCalledBySelect(className);
    }

    public boolean isPmbMetaDataProcedureRefCustomizeEntity(String className) {
        return getPmbBasicHandler().isProcedureRefCustomizeEntity(className);
    }

    public boolean hasPmbMetaDataProcedureOverload(String className) {
        return getPmbBasicHandler().hasProcedureOverload(className);
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterIn(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionProcedureParameterIn(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterOut(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionProcedureParameterOut(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterInOut(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionProcedureParameterInOut(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterReturn(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionProcedureParameterReturn(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionProcedureParameterResult(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionProcedureParameterResult(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterStringClobHandling(String className, String propertyName) {
        return getPmbBasicHandler().needsStringClobHandling(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterBytesOidHandling(String className, String propertyName) {
        return getPmbBasicHandler().needsBytesOidHandling(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterFixedLengthStringHandling(String className, String propertyName) {
        return getPmbBasicHandler().needsFixedLengthStringHandling(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterObjectBindingBigDecimalHandling(String className,
            String propertyName) {
        return getPmbBasicHandler().needsObjectBindingBigDecimalHandling(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterOracleArrayHandling(String className, String propertyName) {
        return getPmbBasicHandler().needsOracleArrayHandling(className, propertyName);
    }

    public boolean needsPmbMetaDataProcedureParameterOracleStructHandling(String className, String propertyName) {
        return getPmbBasicHandler().needsOracleStructHandling(className, propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleArrayTypeName(String className, String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleArrayTypeName(className, propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleArrayElementJavaNative(String className, String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleArrayElementJavaNative(className, propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleArrayElementJavaNativeTypeLiteral(String className,
            String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleArrayElementJavaNativeTypeLiteral(className,
                propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleStructTypeName(String className, String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleStructTypeName(className, propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleStructEntityType(String className, String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleStructEntityType(className, propertyName);
    }

    public String getPmbMetaDataProcedureParameterOracleStructEntityTypeTypeLiteral(String className,
            String propertyName) {
        return getPmbBasicHandler().getProcedureParameterOracleStructEntityTypeTypeLiteral(className, propertyName);
    }

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    public boolean hasPmbMetaDataPropertyOptionOriginalOnlyOneSetter(String className, String propertyName) {
        return getPmbBasicHandler().hasPropertyOptionOriginalOnlyOneSetter(className, propertyName);
    }

    // -----------------------------------------------------
    //                                    Option LikeSeasrch
    //                                    ------------------
    public boolean hasPmbMetaDataPropertyOptionAnyLikeSearch(String className) {
        return getPmbBasicHandler().hasPropertyOptionAnyLikeSearch(className);
    }

    public boolean hasPmbMetaDataPropertyOptionAnyLikeSearch(String className, String propertyName) {
        return getPmbBasicHandler().hasPropertyOptionAnyLikeSearch(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionLikeSearch(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionLikeSearch(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionPrefixSearch(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionPrefixSearch(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionContainSearch(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionContainSearch(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionSuffixSearch(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionSuffixSearch(className, propertyName);
    }

    // -----------------------------------------------------
    //                                         Option FromTo
    //                                         -------------
    public boolean hasPmbMetaDataPropertyOptionAnyFromTo(String className) {
        return getPmbBasicHandler().hasPropertyOptionAnyFromTo(className);
    }

    public boolean hasPmbMetaDataPropertyOptionAnyFromTo(String className, String propertyName) {
        return getPmbBasicHandler().hasPropertyOptionAnyFromTo(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionFromDate(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionFromDate(className, propertyName);
    }

    public boolean isPmbMetaDataPropertyOptionToDate(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionToDate(className, propertyName);
    }

    // -----------------------------------------------------
    //                                 Option Classification
    //                                 ---------------------
    public boolean isPmbMetaDataPropertyOptionClassificationSetter(String className, String propertyName) {
        return getPmbBasicHandler()
                .isPropertyOptionClassificationSetter(className, propertyName, _sql2entitySchemaData);
    }

    public boolean isPmbMetaDataPropertyOptionClassification(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyOptionClassification(className, propertyName, _sql2entitySchemaData);
    }

    public String getPmbMetaDataPropertyOptionClassificationName(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyOptionClassificationName(className, propertyName, _sql2entitySchemaData);
    }

    protected String getPmbMetaDataPropertyOptionClassificationCodeType(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyOptionClassificationName(className, propertyName, _sql2entitySchemaData);
    }

    public List<Map<String, String>> getPmbMetaDataPropertyOptionClassificationMapList(String className,
            String propertyName) {
        return getPmbBasicHandler().getPropertyOptionClassificationMapList(className, propertyName,
                _sql2entitySchemaData);
    }

    // -----------------------------------------------------
    //                              Alternate Boolean Method
    //                              ------------------------
    public boolean existsPmbMetaDataAlternateBooleanMethodNameSet(String className) {
        return getPmbBasicHandler().existsAlternateBooleanMethodNameSet(className);
    }

    public Set<String> getPmbMetaDataAlternateBooleanMethodNameSet(String className) {
        return getPmbBasicHandler().getAlternateBooleanMethodNameSet(className);
    }

    // -----------------------------------------------------
    //                                               Display
    //                                               -------
    public String getPmbMetaDataPropertyRefColumnInfo(String className, String propertyName) {
        try {
            final DfPmbGenerationHandler handler = getPmbBasicHandler();
            return handler.getPropertyRefColumnInfo(className, propertyName, _sql2entitySchemaData);
        } catch (RuntimeException e) { // just in case
            String msg = "Failed to get ref-column info:";
            msg = msg + " " + className + "." + propertyName;
            _log.debug(msg, e);
            throw e;
        }
    }

    public boolean isPmbMetaDataPropertyRefColumnChar(String className, String propertyName) {
        return getPmbBasicHandler().isPropertyRefColumnChar(className, propertyName, _sql2entitySchemaData);
    }

    public String getPmbMetaDataPropertyRefSize(String className, String propertyName) {
        return getPmbBasicHandler().getPropertyRefSize(className, propertyName, _sql2entitySchemaData);
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
    //                                   AdditionalUniqueKey
    //                                   -------------------
    public void initializeAdditionalUniqueKey() {
        final DfAdditionalUniqueKeyInitializer initializer = new DfAdditionalUniqueKeyInitializer(this);
        initializer.initializeAdditionalUniqueKey();
    }

    // -----------------------------------------------------
    //                                  AdditionalForeignKey
    //                                  --------------------
    /**
     * Initialize additional foreign key. <br />
     * This is for Generate task. (Not Sql2Entity)
     */
    public void initializeAdditionalForeignKey() {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Set up implicit foreign key for Buri before initializing
        // - - - - - - - - - -/
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        buriProperties.setupImplicitAdditionalForeignKey(new DfTableFinder() {
            public Table findTable(String tableName) {
                return getTable(tableName);
            }
        });
        // /- - - - - - - - - - - - - - - - -
        // Initialize additional foreign key
        // - - - - - - - - - -/
        final DfAdditionalForeignKeyInitializer initializer = new DfAdditionalForeignKeyInitializer(this);
        initializer.initializeAdditionalForeignKey();
    }

    // -----------------------------------------------------
    //                              ClassificationDeployment
    //                              ------------------------
    public void initializeClassificationDeployment() {
        final DfClassificationProperties clsProp = getClassificationProperties();

        // Initialize classification definition before initializing deployment.
        clsProp.initializeClassificationDefinition(); // Together!

        // Initialize current target database.
        clsProp.initializeClassificationDeployment(this);

        // If this is in sql2entity task, initialize schema database.
        if (_sql2entitySchemaData != null) {
            clsProp.initializeClassificationDeployment(_sql2entitySchemaData.getDatabase());
        }
    }

    // -----------------------------------------------------
    //                                          IncludeQuery
    //                                          ------------
    public void initializeIncludeQuery() {
        DfIncludeQueryInitializer initializer = new DfIncludeQueryInitializer();
        initializer.setIncludeQueryProperties(getProperties().getIncludeQueryProperties());
        initializer.setTableFinder(new DfTableFinder() {
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

            public boolean hasTableColumn(String tableName, String columnName) {
                if (!hasTable(tableName)) {
                    return false;
                }
                return getTable(tableName).getColumn(columnName) != null;
            }
        });
        getProperties().getBasicProperties().checkDirectoryPackage();
    }

    // ===================================================================================
    //                                                              Delete Old Table Class
    //                                                              ======================
    public void deleteOldTableClass() {
        if (_skipDeleteOldClass) {
            return;
        }
        if (getProperties().getLittleAdjustmentProperties().isDeleteOldTableClass()) {
            final DfOldClassHandler handler = createOldClassHandler();
            handler.deleteOldTableClass();
        }
    }

    public void deleteOldCustomizeClass() {
        if (_skipDeleteOldClass) {
            return;
        }
        if (getProperties().getLittleAdjustmentProperties().isDeleteOldTableClass()) {
            final DfOldClassHandler handler = createOldClassHandler();
            handler.setCustomizeTableList(getTableList());
            handler.setPmbMetaDataMap(_pmbMetaDataMap);
            handler.deleteOldCustomizeClass();
        }
    }

    protected DfOldClassHandler createOldClassHandler() {
        return new DfOldClassHandler(getGeneratorInstance(), getBasicProperties(), getProperties()
                .getLittleAdjustmentProperties(), getTableList());
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    public void enableGenerateOutputDirectory() {
        doEnableGenerateOutputDirectory(true);
    }

    public void backToGenerateOutputDirectory() {
        doEnableGenerateOutputDirectory(false);
    }

    protected void doEnableGenerateOutputDirectory(boolean logging) {
        final String outputDirectory = getProperties().getBasicProperties().getGenerateOutputDirectory();
        if (logging) {
            _log.info("...Setting up generateOutputDirectory: " + outputDirectory);
        }
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void enableSql2EntityOutputDirectory() {
        doEnableSql2EntityOutputDirectory(true);
    }

    public void backToSql2EntityOutputDirectory() {
        doEnableSql2EntityOutputDirectory(false);
    }

    protected void doEnableSql2EntityOutputDirectory(boolean logging) {
        final String outputDirectory = getProperties().getOutsideSqlProperties().getSql2EntityOutputDirectory();
        if (logging) {
            _log.info("...Setting up sql2EntityOutputDirectory: " + outputDirectory);
        }
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void enableDocumentOutputDirectory() {
        final String outputDirectory = getProperties().getDocumentProperties().getDocumentOutputDirectory();
        _log.info("...Setting up documentOutputDirectory: " + outputDirectory);
        final File dir = new File(outputDirectory);
        if (!dir.exists()) {
            _log.info("...Making directories for documentOutputDirectory: " + dir);
            dir.mkdirs(); // because this directory is NOT user setting basically 
        }
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void enableSimpleDtoOutputDirectory() {
        final String outputDirectory = getProperties().getSimpleDtoProperties().getSimpleDtoOutputDirectory();
        _log.info("...Setting up simpleDtoOutputDirectory: " + outputDirectory);
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void enableDtoMapperOutputDirectory() {
        final String outputDirectory = getProperties().getSimpleDtoProperties().getDtoMapperOutputDirectory();
        _log.info("...Setting up dtoMapperOutputDirectory: " + outputDirectory);
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    public void enableFlexDtoOutputDirectory() {
        final String outputDirectory = getProperties().getFlexDtoProperties().getOutputDirectory();
        _log.info("...Setting up flexDtoOutputDirectory: " + outputDirectory);
        getGeneratorInstance().setOutputPath(outputDirectory);
    }

    // ===================================================================================
    //                                                                           Generator
    //                                                                           =========
    public DfGenerator getGeneratorInstance() {
        return DfGenerator.getInstance();
    }

    //====================================================================================
    //                                                               Database Name Mapping
    //                                                               =====================
    public String getDefaultDBDef() { // for DBCurrent
        return getBasicProperties().getCurrentDBDef().code();
    }

    public String getGenerateDbName() { // for Class Name
        return DfDatabaseNameMapping.getInstance().findGenerateName(getDatabaseType());
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - -
    // basically return types of property methods are
    // String or boolean or List (not Number and Date) 
    // because Velocity templates use them.
    // - - - - - - - - - -/

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

    public boolean isApplicationBehaviorProject() {
        return getBasicProperties().isApplicationBehaviorProject();
    }

    // -----------------------------------------------------
    //                                              Database
    //                                              --------
    public String getDatabaseName() {
        return getBasicProperties().getTargetDatabase();
    }

    public boolean isDatabaseMySQL() {
        return getBasicProperties().isDatabaseMySQL();
    }

    public boolean isDatabasePostgreSQL() {
        return getBasicProperties().isDatabasePostgreSQL();
    }

    public boolean isDatabaseOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    public boolean isDatabaseDB2() {
        return getBasicProperties().isDatabaseDB2();
    }

    public boolean isDatabaseSQLServer() {
        return getBasicProperties().isDatabaseSQLServer();
    }

    public boolean isDatabaseDerby() {
        return getBasicProperties().isDatabaseDerby();
    }

    public boolean isDatabaseH2() {
        return getBasicProperties().isDatabaseH2();
    }

    public boolean isDatabaseMSAccess() {
        return getBasicProperties().isDatabaseMSAccess();
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

    public boolean isTargetContainerLucy() {
        return getBasicProperties().isTargetContainerLucy();
    }

    public boolean isTargetContainerGuice() {
        return getBasicProperties().isTargetContainerGuice();
    }

    public boolean isTargetContainerSlim3() {
        return getBasicProperties().isTargetContainerSlim3();
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
        return getBasicProperties().getGenerateOutputDirectory();
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
        return getProperties().getAllClassCopyrightProperties().getAllClassCopyright();
    }

    public void reflectAllExCopyright(String path) {
        final String outputPath = DfGenerator.getInstance().getOutputPath();
        final String absolutePath = outputPath + "/" + path;
        final String sourceCodeEncoding = getTemplateFileEncoding();
        final String sourceCodeLn = getBasicProperties().getSourceCodeLineSeparator();
        final DfCopyrightResolver resolver = new DfCopyrightResolver(sourceCodeEncoding, sourceCodeLn);
        final String copyright = getProperties().getAllClassCopyrightProperties().getAllClassCopyright();
        resolver.reflectAllExCopyright(absolutePath, copyright);
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
    //                                      Generate Package
    //                                      ----------------
    public String getPackageBase() {
        return getProperties().getBasicProperties().getPackageBase();
    }

    public String getBaseCommonPackage() {
        return getProperties().getBasicProperties().getBaseCommonPackage();
    }

    public String getBaseBehaviorPackage() {
        return getProperties().getBasicProperties().getBaseBehaviorPackage();
    }

    public String getBaseDaoPackage() {
        return getProperties().getBasicProperties().getBaseDaoPackage();
    }

    public String getBaseEntityPackage() {
        return getProperties().getBasicProperties().getBaseEntityPackage();
    }

    public String getDBMetaPackage() {
        return getProperties().getBasicProperties().getDBMetaPackage();
    }

    public String getConditionBeanPackage() {
        return getProperties().getBasicProperties().getConditionBeanPackage();
    }

    public String getExtendedConditionBeanPackage() {
        return getProperties().getBasicProperties().getExtendedConditionBeanPackage();
    }

    public String getExtendedBehaviorPackage() {
        return getProperties().getBasicProperties().getExtendedBehaviorPackage();
    }

    public String getExtendedDaoPackage() {
        return getProperties().getBasicProperties().getExtendedDaoPackage();
    }

    public String getExtendedEntityPackage() {
        return getProperties().getBasicProperties().getExtendedEntityPackage();
    }

    public String getLibraryAllCommonPackage() { // for Application Behavior
        return getBasicProperties().getLibraryAllCommonPackage();
    }

    public String getLibraryBehaviorPackage() { // for Application Behavior
        return getBasicProperties().getLibraryBehaviorPackage();
    }

    public String getLibraryEntityPackage() { // for Application Behavior
        return getBasicProperties().getLibraryEntityPackage();
    }

    public String getLibraryProjectPrefix() { // for Application Behavior
        return getBasicProperties().getLibraryProjectPrefix();
    }

    public String getApplicationAllCommonPackage() { // for Application Behavior
        return getBasicProperties().getApplicationAllCommonPackage();
    }

    // -----------------------------------------------------
    //                                             Flat/Omit
    //                                             ---------
    // CSharp Only
    public boolean isFlatDirectoryPackageValid() {
        return getProperties().getBasicProperties().isFlatDirectoryPackageValid();
    }

    public String getFlatDirectoryPackage() {
        return getProperties().getBasicProperties().getFlatDirectoryPackage();
    }

    public boolean isOmitDirectoryPackageValid() {
        return getProperties().getBasicProperties().isOmitDirectoryPackageValid();
    }

    public String getOmitDirectoryPackage() {
        return getProperties().getBasicProperties().getOmitDirectoryPackage();
    }

    // -----------------------------------------------------
    //                                            Hot Deploy
    //                                            ----------
    public boolean isAvailableHotDeploy() {
        return getBasicProperties().isAvailableHotDeploy();
    }

    // -----------------------------------------------------
    //                                        Begin/End Mark
    //                                        --------------
    public String getBehaviorQueryPathBeginMark() {
        return getBasicProperties().getBehaviorQueryPathBeginMark();
    }

    public String getBehaviorQueryPathEndMark() {
        return getBasicProperties().getBehaviorQueryPathEndMark();
    }

    public String getExtendedClassDescriptionBeginMark() {
        return getBasicProperties().getExtendedClassDescriptionBeginMark();
    }

    public String getExtendedClassDescriptionEndMark() {
        return getBasicProperties().getExtendedClassDescriptionEndMark();
    }

    // -----------------------------------------------------
    //                                    Serial Version UID
    //                                    ------------------
    public void reflectAllExSerialVersionUID(String path) {
        // basically for parameter-bean
        // because it has become to need it since 0.9.7.0
        // (supported classes since older versions don't need this)
        final String outputPath = DfGenerator.getInstance().getOutputPath();
        final String absolutePath = outputPath + "/" + path;
        final String sourceCodeEncoding = getTemplateFileEncoding();
        final String sourceCodeLn = getBasicProperties().getSourceCodeLineSeparator();
        final DfSerialVersionUIDResolver resolver = new DfSerialVersionUIDResolver(sourceCodeEncoding, sourceCodeLn);
        resolver.reflectAllExSerialUID(absolutePath);
    }

    // ===================================================================================
    //                                                                 Database Properties
    //                                                                 ===================
    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    public UnifiedSchema getDatabaseSchema() {
        return getDatabaseProperties().getDatabaseSchema();
    }

    public boolean hasDatabaseSchema() {
        return getDatabaseSchema().hasSchema();
    }

    public boolean hasAdditionalSchema() {
        return getDatabaseProperties().hasAdditionalSchema();
    }

    public boolean hasCatalogAdditionalSchema() {
        return getDatabaseProperties().hasCatalogAdditionalSchema();
    }

    // ===================================================================================
    //                                                                Dependency Injection
    //                                                                ====================
    // -----------------------------------------------------
    //                                                Seasar
    //                                                ------
    public String getDBFluteDiconNamespace() {
        return getProperties().getDependencyInjectionProperties().getDBFluteDiconNamespace();
    }

    public List<String> getDBFluteDiconPackageNameList() {
        final String resourceOutputDirectory = getBasicProperties().getResourceOutputDirectory();
        if (resourceOutputDirectory != null) {
            final List<String> resulList = new ArrayList<String>();
            resulList.add(resourceOutputDirectory);
            return resulList;
        }

        // for compatibility and default value
        final List<String> diconPackageNameList = getProperties().getDependencyInjectionProperties()
                .getDBFluteDiconPackageNameList();
        if (diconPackageNameList != null && !diconPackageNameList.isEmpty()) {
            return diconPackageNameList;
        } else {
            final List<String> resulList = new ArrayList<String>();
            resulList.add(getBasicProperties().getDefaultResourceOutputDirectory());
            return resulList;
        }
    }

    public String getDBFluteCreatorDiconFileName() {
        return getProperties().getDependencyInjectionProperties().getDBFluteCreatorDiconFileName();
    }

    public String getDBFluteCustomizerDiconFileName() {
        return getProperties().getDependencyInjectionProperties().getDBFluteCustomizerDiconFileName();
    }

    public String getDBFluteDiconFileName() {
        return getProperties().getDependencyInjectionProperties().getDBFluteDiconFileName();
    }

    public String getJ2eeDiconResourceName() {
        return getProperties().getDependencyInjectionProperties().getJ2eeDiconResourceName();
    }

    public List<String> getDBFluteDiconBeforeJ2eeIncludePathList() {
        return getProperties().getDependencyInjectionProperties().getDBFluteDiconBeforeJ2eeIncludePathList();
    }

    public List<String> getDBFluteDiconOtherIncludePathList() {
        return getProperties().getDependencyInjectionProperties().getDBFluteDiconOtherIncludePathList();
    }

    public String filterDBFluteDiconBhvAp(String filePath) { // as utility for application behavior
        if (filePath.endsWith(".dicon")) {
            filePath = Srl.replace(filePath, ".dicon", "++.dicon");
        }
        return filePath;
    }

    // -----------------------------------------------------
    //                                         Spring & Lucy
    //                                         -------------
    public List<String> getDBFluteBeansPackageNameList() {
        final String resourceOutputDirectory = getBasicProperties().getResourceOutputDirectory();
        if (resourceOutputDirectory != null) {
            final List<String> resulList = new ArrayList<String>();
            resulList.add(resourceOutputDirectory);
            return resulList;
        }

        // for compatibility and default value
        final List<String> diconPackageNameList = getProperties().getDependencyInjectionProperties()
                .getDBFluteBeansPackageNameList();
        if (diconPackageNameList != null && !diconPackageNameList.isEmpty()) {
            return diconPackageNameList;
        } else {
            final List<String> resulList = new ArrayList<String>();
            resulList.add(getBasicProperties().getDefaultResourceOutputDirectory());
            return resulList;
        }
    }

    public String getDBFluteBeansFileName() {
        return getProperties().getDependencyInjectionProperties().getDBFluteBeansFileName();
    }

    public String getDBFluteBeansDataSourceName() {
        return getProperties().getDependencyInjectionProperties().getDBFluteBeansDataSourceName();
    }

    public String filterDBFluteBeansBhvAp(String filePath) { // as utility for application behavior
        if (filePath.endsWith(".xml")) {
            filePath = Srl.replace(filePath, ".xml", "BhvAp.xml");
        }
        return filePath;
    }

    // -----------------------------------------------------
    //                                                 Guice
    //                                                 -----
    public String getDBFluteModuleBhvApClassName() {
        return getProjectPrefix() + "DBFluteModuleBhvAp";
    }

    public String filterDBFluteModuleBhvAp(String filePath) { // as utility for application behavior
        if (filePath.endsWith(".java")) {
            filePath = Srl.replace(filePath, ".java", "BhvAp.java");
        }
        return filePath;
    }

    // -----------------------------------------------------
    //                                                 Quill
    //                                                 -----
    public boolean isQuillDataSourceNameValid() {
        return getProperties().getDependencyInjectionProperties().isQuillDataSourceNameValid();
    }

    public String getQuillDataSourceName() {
        return getProperties().getDependencyInjectionProperties().getQuillDataSourceName();
    }

    // ===================================================================================
    //                                                        Sequence/Identity Properties
    //                                                        ============================
    public String getSequenceReturnType() {
        return getProperties().getSequenceIdentityProperties().getSequenceReturnType();
    }

    // ===================================================================================
    //                                                            Common Column Properties
    //                                                            ========================
    public Map<String, String> getCommonColumnMap() {
        return getProperties().getCommonColumnProperties().getCommonColumnMap();
    }

    public List<String> getCommonColumnNameList() {
        return getProperties().getCommonColumnProperties().getCommonColumnNameList();
    }

    public List<String> getCommonColumnNameConversionList() {
        return getProperties().getCommonColumnProperties().getCommonColumnNameConversionList();
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
        return getProperties().getCommonColumnProperties().isCommonColumnConversion(commonColumnName);
    }

    // --------------------------------------
    //                                 insert
    //                                 ------
    public boolean hasCommonColumnBeforeInsertLogic(String columnName) {
        return getProperties().getCommonColumnProperties().hasCommonColumnBeforeInsertLogic(columnName);
    }

    public String getCommonColumnBeforeInsertLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnProperties().getCommonColumnBeforeInsertLogicByColumnName(columnName);
    }

    // --------------------------------------
    //                                 update
    //                                 ------
    public boolean hasCommonColumnBeforeUpdateLogic(String columnName) {
        return getProperties().getCommonColumnProperties().hasCommonColumnBeforeUpdateLogic(columnName);
    }

    public String getCommonColumnBeforeUpdateLogicByColumnName(String columnName) {
        return getProperties().getCommonColumnProperties().getCommonColumnBeforeUpdateLogicByColumnName(columnName);
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

    public String buildClassificationCodeNameAliasVariables(Map<String, String> classificationMap) {
        return getClassificationProperties().buildClassificationCodeNameAliasVariables(classificationMap);
    }

    public boolean isTableClassification(String classificationName) {
        return getClassificationProperties().isTableClassification(classificationName);
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

    public boolean hasClassificationAlias(String classificationName) {
        return getClassificationProperties().hasClassificationAlias(classificationName);
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
    //                                                        Little Adjustment Properties
    //                                                        ============================
    public boolean isAvailableDatabaseDependency() {
        return getProperties().getLittleAdjustmentProperties().isAvailableDatabaseDependency();
    }

    public boolean isAvailableNonPrimaryKeyWritable() {
        return getProperties().getLittleAdjustmentProperties().isAvailableNonPrimaryKeyWritable();
    }

    public boolean isCheckSelectedClassification() {
        return getProperties().getLittleAdjustmentProperties().isCheckSelectedClassification();
    }

    public boolean isForceClassificationSetting() {
        return getProperties().getLittleAdjustmentProperties().isForceClassificationSetting();
    }

    public boolean isCDefToStringReturnsName() {
        return getProperties().getLittleAdjustmentProperties().isCDefToStringReturnsName();
    }

    public boolean isMakeEntityOldStyleClassify() {
        return getProperties().getLittleAdjustmentProperties().isMakeEntityOldStyleClassify();
    }

    public boolean isMakeEntityChaseRelation() {
        return getProperties().getLittleAdjustmentProperties().isMakeEntityChaseRelation();
    }

    public boolean isEntityConvertEmptyStringToNull() {
        return getProperties().getLittleAdjustmentProperties().isEntityConvertEmptyStringToNull();
    }

    public boolean isMakeConditionQueryEqualEmptyString() {
        return getProperties().getLittleAdjustmentProperties().isMakeConditionQueryEqualEmptyString();
    }

    public String getConditionQueryNotEqualDefinitionName() {
        return getProperties().getLittleAdjustmentProperties().getConditionQueryNotEqualDefinitionName();
    }

    public boolean isAvailableDatabaseNativeJDBC() {
        return getProperties().getLittleAdjustmentProperties().isAvailableDatabaseNativeJDBC();
    }

    public boolean isAvailableOracleNativeJDBC() { // Oracle facade
        return isDatabaseOracle() && isAvailableDatabaseNativeJDBC();
    }

    public boolean isMakeDeprecated() {
        return getProperties().getLittleAdjustmentProperties().isMakeDeprecated();
    }

    public boolean isMakeRecentlyDeprecated() {
        return getProperties().getLittleAdjustmentProperties().isMakeRecentlyDeprecated();
    }

    public boolean hasExtendedImplementedInvokerAssistantClass() {
        return getProperties().getLittleAdjustmentProperties().hasExtendedImplementedInvokerAssistantClass();
    }

    public String getExtendedImplementedInvokerAssistantClass() {
        return getProperties().getLittleAdjustmentProperties().getExtendedImplementedInvokerAssistantClass();
    }

    public boolean hasExtendedImplementedCommonColumnAutoSetupperClass() {
        return getProperties().getLittleAdjustmentProperties().hasExtendedImplementedCommonColumnAutoSetupperClass();
    }

    public String getExtendedImplementedCommonColumnAutoSetupperClass() {
        return getProperties().getLittleAdjustmentProperties().getExtendedImplementedCommonColumnAutoSetupperClass();
    }

    public boolean hasExtendedS2DaoSettingClassValid() {
        return getProperties().getLittleAdjustmentProperties().hasExtendedS2DaoSettingClassValid();
    }

    public String getExtendedS2DaoSettingClass() {
        return getProperties().getLittleAdjustmentProperties().getExtendedS2DaoSettingClass();
    }

    public boolean isShortCharHandlingValid() {
        return getProperties().getLittleAdjustmentProperties().isShortCharHandlingValid();
    }

    public String getShortCharHandlingMode() {
        return getProperties().getLittleAdjustmentProperties().getShortCharHandlingMode();
    }

    public String getShortCharHandlingModeCode() {
        return getProperties().getLittleAdjustmentProperties().getShortCharHandlingModeCode();
    }

    public boolean isStopGenerateExtendedBhv() {
        return getProperties().getLittleAdjustmentProperties().isStopGenerateExtendedBhv();
    }

    public boolean isStopGenerateExtendedDao() {
        return getProperties().getLittleAdjustmentProperties().isStopGenerateExtendedDao();
    }

    public boolean isStopGenerateExtendedEntity() {
        return getProperties().getLittleAdjustmentProperties().isStopGenerateExtendedEntity();
    }

    public boolean isMakeFlatExpansion() {
        return getProperties().getLittleAdjustmentProperties().isMakeFlatExpansion();
    }

    public boolean isMakeDaoInterface() {
        return getProperties().getLittleAdjustmentProperties().isMakeDaoInterface();
    }

    public boolean isAvailableToLowerInGeneratorUnderscoreMethod() {
        return getProperties().getLittleAdjustmentProperties().isAvailableToLowerInGeneratorUnderscoreMethod();
    }

    // ===================================================================================
    //                                                                     Buri Properties
    //                                                                     ===============
    public boolean isUseBuri() {
        return getProperties().getBuriProperties().isUseBuri();
    }

    public List<String> getBuriPackageList() {
        return new ArrayList<String>(getProperties().getBuriProperties().getActivityDefinitionMap().keySet());
    }

    public List<String> getBuriProcessList(String packageName) {
        return new ArrayList<String>(getProperties().getBuriProperties().getProcessMap(packageName).keySet());
    }

    public List<String> getBuriStatusList(String packageName, String processName) {
        return getProperties().getBuriProperties().getStatusList(packageName, processName);
    }

    public List<String> getBuriActionList(String packageName, String processName) {
        return getProperties().getBuriProperties().getActionList(packageName, processName);
    }

    public boolean hasBuriAllRoundStateHistory() {
        return getProperties().getBuriProperties().hasBuriAllRoundStateHistory(new DfTableFinder() {
            public Table findTable(String name) {
                return getTable(name);
            }
        });
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
    //                                                               OutsideSql Properties
    //                                                               =====================
    public boolean isGenerateProcedureParameterBean() {
        return getProperties().getOutsideSqlProperties().isGenerateProcedureParameterBean();
    }

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

    public String getSchemaHtmlFileName() {
        final String projectName = getProjectName();
        return getProperties().getDocumentProperties().getSchemaHtmlFileName(projectName);
    }

    public boolean isSchemaHtmlOutsideSqlValid() {
        if (getProperties().getDocumentProperties().isSuppressSchemaHtmlOutsideSql()) {
            return false;
        }
        return hasTableBqpMap() || isGenerateProcedureParameterBean();
    }

    public String getHistoryHtmlFileName() {
        final String projectName = getProjectName();
        return getProperties().getDocumentProperties().getHistoryHtmlFileName(projectName);
    }

    public void deleteOldSchemaHtmlFile() {
        final String currentFileName = getSchemaHtmlFileName();
        final String outputDirectory = getProperties().getDocumentProperties().getDocumentOutputDirectory();
        final String oldFileName = outputDirectory + "/project-schema-" + getProjectName() + ".html";
        if (currentFileName.equalsIgnoreCase(oldFileName)) {
            return; // if current is same as old
        }
        final File file = new File(oldFileName);
        if (file.exists()) {
            try {
                file.delete();
            } catch (RuntimeException continued) {
                _log.info("*Failed to delete old schemaHtml: " + file, continued);
            }
        }
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
    //                                                                Hibernate Properties
    //                                                                ====================
    public boolean hasHibernateDefinition() {
        return getProperties().getHibernateProperties().hasHibernateDefinition();
    }

    public String getHibernateBaseEntityPackage() {
        return getBaseEntityPackage();
    }

    public String getHibernateExtendedEntityPackage() {
        return getExtendedEntityPackage();
    }

    public String getHibernateBaseEntityPrefix() {
        return getBasePrefix();
    }

    public String getHibernateManyToOneFetch() {
        return getProperties().getHibernateProperties().getManyToOneFetch();
    }

    public String getHibernateOneToOneFetch() {
        return getProperties().getHibernateProperties().getOneToOneFetch();
    }

    public String getHibernateOneToManyFetch() {
        return getProperties().getHibernateProperties().getOneToManyFetch();
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

    // ===================================================================================
    //                                                  Component Name Helper for Template
    //                                                  ==================================
    // -----------------------------------------------------
    //                                   AllCommon Component
    //                                   -------------------
    // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
    // These methods for all-common components are used when it needs to identity their components.
    // For example when the DI container is Seasar, These methods are not used
    // because S2Container has name-space in the DI architecture.
    // = = = = = = = = = =/

    public String getDBFluteInitializerComponentName() {
        return filterComponentNameWithProjectPrefix("introduction");
    }

    public String getInvokerAssistantComponentName() {
        return filterComponentNameWithProjectPrefix("invokerAssistant");
    }

    public String getCommonColumnAutoSetupperComponentName() {
        return filterComponentNameWithProjectPrefix("commonColumnAutoSetupper");
    }

    public String getBehaviorSelectorComponentName() {
        return filterComponentNameWithProjectPrefix("behaviorSelector");
    }

    public String getBehaviorCommandInvokerComponentName() {
        return filterComponentNameWithProjectPrefix("behaviorCommandInvoker");
    }

    // -----------------------------------------------------
    //                                     Filtering Utility
    //                                     -----------------
    /**
     * Filter a component name with a project prefix.
     * @param componentName The name of component. (NotNull)
     * @return A filtered component name with project prefix. (NotNull)
     */
    public String filterComponentNameWithProjectPrefix(String componentName) {
        final String prefix = getBasicProperties().getProjectPrefix();
        if (prefix == null || prefix.trim().length() == 0) {
            return componentName;
        }
        final String filteredPrefix = prefix.substring(0, 1).toLowerCase() + prefix.substring(1);
        return filteredPrefix + componentName.substring(0, 1).toUpperCase() + componentName.substring(1);
    }

    // ===================================================================================
    //                                                                 Type Mapping Helper
    //                                                                 ===================
    public String convertJavaNativeByJdbcType(String jdbcType) {
        try {
            return TypeMap.findJavaNativeByJdbcType(jdbcType, null, null);
        } catch (RuntimeException e) {
            _log.warn("TypeMap.findJavaNativeTypeString(jdbcType, null, null) threw the exception: jdbcType="
                    + jdbcType, e);
            throw e;
        }
    }

    // ===================================================================================
    //                                                                 Name Convert Helper
    //                                                                 ===================
    public String convertJavaNameByJdbcNameAsTable(String jdbcName) {
        // Don't use Srl.camelize() because
        // it saves compatible and here simple is best.
        // (Srl.camelize() is used at parameter bean and other sub objects)
        if (getBasicProperties().isTableNameCamelCase()) {
            // initial-capitalize only
            return initCap(jdbcName);
        }
        final List<String> inputs = new ArrayList<String>(2);
        inputs.add(jdbcName);
        inputs.add(getDefaultJavaNamingMethod());
        return initCap(generateName(inputs)); // use title case
    }

    public String convertJavaNameByJdbcNameAsColumn(String jdbcName) {
        // same policy as table naming
        if (getBasicProperties().isColumnNameCamelCase()) {
            // initial-capitalize only
            return initCap(jdbcName);
        }
        final List<String> inputs = new ArrayList<String>(2);
        inputs.add(jdbcName);
        inputs.add(getDefaultJavaNamingMethod());
        return initCap(generateName(inputs)); // use title case
    }

    public String convertUncapitalisedJavaNameByJdbcNameAsColumn(String jdbcName) {
        return Srl.initUncap(convertJavaNameByJdbcNameAsColumn(jdbcName));
    }

    /**
     * Generate name.
     * @param inputs Inputs.
     * @return Generated name.
     */
    protected String generateName(List<?> inputs) {
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
    public String getWildCard() {
        return "%";
    }

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
        return "{@inheritDoc}";
    }

    public String getImplementComment() {
        return "{@inheritDoc}";
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
        return Srl.initCap(str);
    }

    public String initUncap(String str) {
        return Srl.initUncap(str);
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
        final DfPackagePathHandler handler = new DfPackagePathHandler(getBasicProperties());
        return handler.getPackageAsPath(pckge);
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    protected Map<String, Map<String, Map<String, String>>> _tableBqpMap;

    public boolean hasTableBqpMap() { // basically for SchemaHTML
        return !getTableBqpMap().isEmpty();
    }

    protected Map<String, Map<String, Map<String, String>>> getTableBqpMap() {
        if (_tableBqpMap != null) {
            return _tableBqpMap;
        }
        final DfBehaviorQueryPathSetupper setupper = new DfBehaviorQueryPathSetupper();
        try {
            _tableBqpMap = setupper.extractTableBqpMap(collectOutsideSql());
        } catch (RuntimeException e) {
            _log.warn("Failed to extract the map of table behavior query path!", e);
            _tableBqpMap = new HashMap<String, Map<String, Map<String, String>>>();
        }
        return _tableBqpMap;
    }

    protected DfOutsideSqlPack collectOutsideSql() {
        final DfOutsideSqlCollector outsideSqlCollector = new DfOutsideSqlCollector();
        outsideSqlCollector.suppressDirectoryCheck();
        return outsideSqlCollector.collectOutsideSql();
    }

    // ===================================================================================
    //                                                                  Procedure Document
    //                                                                  ==================
    protected List<DfProcedureMeta> _procedureMetaInfoList;

    public List<DfProcedureMeta> getAvailableProcedureList() throws SQLException {
        if (_procedureMetaInfoList != null) {
            return _procedureMetaInfoList;
        }
        _log.info(" ");
        _log.info("...Setting up procedures for documents");
        final DfProcedureExtractor handler = new DfProcedureExtractor();
        handler.includeProcedureSynonym(getDataSource());
        final DataSource dataSource = getDataSource();
        _procedureMetaInfoList = handler.getAvailableProcedureList(dataSource);
        return _procedureMetaInfoList;
    }

    // ===================================================================================
    //                                                                      Schema History
    //                                                                      ==============
    public void loadSchemaHistory() { // for HiostoryHtml
        _log.info("...Loading schema history");
        _schemaHistory = DfSchemaHistory.createAsCore();
        _schemaHistory.loadHistory();
        if (existsSchemaHistory()) {
            _log.info("  -> found history: count=" + getSchemaDiffList().size());
        } else {
            _log.info("  -> no history");
        }
    }

    public boolean existsSchemaHistory() {
        return _schemaHistory.existsHistory();
    }

    public List<DfSchemaDiff> getSchemaDiffList() {
        return _schemaHistory.getSchemaDiffList();
    }

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    protected DataSource getDataSource() {
        return DfDataSourceContext.getDataSource();
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
        final StringBuilder sb = new StringBuilder();
        sb.append("<database name=\"").append(getName()).append('"').append(">\n");
        final List<Table> tableList = getTableList();
        for (Table table : tableList) {
            sb.append(table);
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

    public void setSql2EntitySchemaData(AppData sql2entitySchemaData) {
        _sql2entitySchemaData = sql2entitySchemaData;
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

    public Map<String, DfPmbMetaData> getPmbMetaDataMap() {
        return _pmbMetaDataMap;
    }

    public void setPmbMetaDataMap(Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    public void setSkipDeleteOldClass(boolean skipDeleteOldClass) {
        _skipDeleteOldClass = skipDeleteOldClass;
    }
}