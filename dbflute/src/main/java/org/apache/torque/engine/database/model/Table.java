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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.torque.DfTorqueColumnListToStringUtil;
import org.seasar.dbflute.util.DfPropertyUtil;
import org.seasar.dbflute.util.DfStringUtil;
import org.xml.sax.Attributes;

/**
 * @author Modified by jflute
 */
public class Table implements IDMethod {
    /** Logging class from commons.logging */
    private static Log _log = LogFactory.getLog(Table.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    //private AttributeListImpl attributes;
    private List<Column> _columnList;

    private List<ForeignKey> _foreignKeys;

    private List<Index> _indices;

    private List<Unique> _unices;

    private List<IdMethodParameter> _idMethodParameters;

    private String _name;

    private String _type;

    private String _schema;

    private String _comment;

    private String _description;

    private String _javaName;

    private String _idMethod;

    protected String _javaNamingMethod;

    private Database _tableParent;

    private List<ForeignKey> _referrers;

    private List<String> _foreignTableNames;

    private boolean _containsForeignPK;

    private Column _inheritanceColumn;

    private boolean _skipSql;

    private boolean _abstractValue;

    private String _alias;

    private String _enterface;

    private String _pkg;

    protected DfFlexibleNameMap<String, Column> _columnMap = new DfFlexibleNameMap<String, Column>();

    private boolean _isNeedsTransactionInPostgres;

    private boolean _isHeavyIndexing;

    private boolean _isForReferenceOnly;

    private boolean _existSameNameTable;

    private boolean _sql2entityTypeSafeCursor;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default Constructor
     */
    public Table() {
        this(null);
    }

    /**
     * Constructs a table object with a name
     *
     * @param name table name
     */
    public Table(String name) {
        this._name = name;
        _columnList = new ArrayList<Column>();
        _foreignKeys = new ArrayList<ForeignKey>(5);
        _referrers = new ArrayList<ForeignKey>(5);
        _indices = new ArrayList<Index>(5);
        _unices = new ArrayList<Unique>(5);
    }

    // ===================================================================================
    //                                                                         Initializer
    //                                                                         ===========
    /**
     * Load the table object from an xml tag.
     *
     * @param attrib xml attributes
     * @param defaultIdMethod defined at db level
     */
    public void loadFromXML(Attributes attrib, String defaultIdMethod) {
        _name = attrib.getValue("name");
        _type = attrib.getValue("type");
        _schema = attrib.getValue("schema");
        _comment = attrib.getValue("comment");

        _javaName = attrib.getValue("javaName");
        _idMethod = attrib.getValue("idMethod");

        //
        // retrieves the method for converting from specified name to a java name.
        // 
        // *Attension
        //   Always use Default-JavaNamingMethod!!!
        // 
        //   // This line is commented out.
        //   _javaNamingMethod = attrib.getValue("javaNamingMethod");
        // 
        _javaNamingMethod = getDatabase().getDefaultJavaNamingMethod();

        if ("null".equals(_idMethod)) {
            _idMethod = defaultIdMethod;
        }
        if ("autoincrement".equals(_idMethod) || "sequence".equals(_idMethod)) {
            _log.warn("The value '" + _idMethod + "' for Torque's "
                    + "table.idMethod attribute has been deprecated in favor " + "of '" + NATIVE
                    + "'.  Please adjust your " + "Torque XML schema accordingly.");
            _idMethod = NATIVE;
        }
        _skipSql = "true".equals(attrib.getValue("skipSql"));
        // pkg = attrib.getValue("package");
        _abstractValue = "true".equals(attrib.getValue("abstract"));
        //        _baseClass = attrib.getValue("baseClass");
        //        _basePeer = attrib.getValue("basePeer");
        _alias = attrib.getValue("alias");
        _isHeavyIndexing = "true".equals(attrib.getValue("heavyIndexing"))
                || (!"false".equals(attrib.getValue("heavyIndexing")) && getDatabase().isHeavyIndexing());
        _description = attrib.getValue("description");
        _enterface = attrib.getValue("interface");
    }

    // ===================================================================================
    //                                                                             Unknown
    //                                                                             =======
    /**
     * <p>A hook for the SAX XML parser to call when this table has
     * been fully loaded from the XML, and all nested elements have
     * been processed.</p>
     *
     * <p>Performs heavy indexing and naming of elements which weren't
     * provided with a name.</p>
     */
    public void doFinalInitialization() {// TODO: @jflute - Unnecessary?
        // Heavy indexing must wait until after all columns composing
        // a table's primary key have been parsed.
        if (_isHeavyIndexing) {
            doHeavyIndexing();
        }

        // Name any indices which are missing a name using the
        // appropriate algorithm.
        doNaming();
    }

    /**
     * <p>Adds extra indices for multi-part primary key columns.</p>
     *
     * <p>For databases like MySQL, values in a where clause must
     * match key part order from the left to right.  So, in the key
     * definition <code>PRIMARY KEY (FOO_ID, BAR_ID)</code>,
     * <code>FOO_ID</code> <i>must</i> be the first element used in
     * the <code>where</code> clause of the SQL query used against
     * this table for the primary key index to be used.  This feature
     * could cause problems under MySQL with heavily indexed tables,
     * as MySQL currently only supports 16 indices per table (i.e. it
     * might cause too many indices to be created).</p>
     *
     * <p>See <a href="http://www.mysql.com/doc/E/X/EXPLAIN.html">the
     * manual</a> for a better description of why heavy indexing is
     * useful for quickly searchable database tables.</p>
     */
    private void doHeavyIndexing() {
        if (_log.isDebugEnabled()) {
            _log.debug("doHeavyIndex() called on table " + getName());
        }

        List<Column> pk = getPrimaryKey();
        int size = pk.size();

        try {
            // We start at an offset of 1 because the entire column
            // list is generally implicitly indexed by the fact that
            // it's a primary key.
            for (int i = 1; i < size; i++) {
                addIndex(new Index(this, pk.subList(i, size)));
            }
        } catch (EngineException e) {
            _log.error(e, e);
        }
    }

    /**
     * Names composing objects which haven't yet been named.  This
     * currently consists of foreign-key and index entities.
     */
    private void doNaming() {
        int i;
        int size;
        String name;

        // Assure names are unique across all databases.
        try {
            for (i = 0, size = _foreignKeys.size(); i < size; i++) {
                ForeignKey fk = (ForeignKey) _foreignKeys.get(i);
                name = fk.getName();
                if (StringUtils.isEmpty(name)) {
                    name = acquireConstraintName("FK", i + 1);
                    fk.setName(name);
                }
            }

            for (i = 0, size = _indices.size(); i < size; i++) {
                Index index = (Index) _indices.get(i);
                name = index.getName();
                if (StringUtils.isEmpty(name)) {
                    name = acquireConstraintName("I", i + 1);
                    index.setName(name);
                }
            }

            // NOTE: Most RDBMSes can apparently name unique column
            // constraints/indices themselves (using MySQL and Oracle
            // as test cases), so we'll assume that we needn't add an
            // entry to the system name list for these.
        } catch (EngineException nameAlreadyInUse) {
            _log.error(nameAlreadyInUse, nameAlreadyInUse);
        }
    }

    /**
     * Macro to a constraint name.
     *
     * @param nameType constraint type
     * @param nbr unique number for this constraint type
     * @return unique name for constraint
     * @throws EngineException
     */
    private final String acquireConstraintName(String nameType, int nbr) throws EngineException {
        List<Object> inputs = new ArrayList<Object>(4);
        inputs.add(getDatabase());
        inputs.add(getName());
        inputs.add(nameType);
        inputs.add(new Integer(nbr));
        return NameFactory.generateName(NameFactory.CONSTRAINT_GENERATOR, inputs);
    }

    // ===================================================================================
    //                                                                          Basic Info
    //                                                                          ==========
    // -----------------------------------------------------
    //                                            Table Name
    //                                            ----------
    /**
     * Get the name of the Table
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the name of the Table
     */
    public void setName(String name) {
        this._name = name;
    }

    // -----------------------------------------------------
    //                                            Table Type
    //                                            ----------
    /**
     * Get the type of the Table
     */
    public String getType() {
        return _type;
    }

    /**
     * Set the type of the Table
     */
    public void setType(String type) {
        this._type = type;
    }

    // -----------------------------------------------------
    //                                          Table Schema
    //                                          ------------
    // Schema名を直接利用することは許さないためCommentOut。
    // 基本的には自動生成時のSchema名なので本番とは食い違う可能性がある。
    // PostgreSQLなどの複数Schema対応時にのみの利用であり、必ず別のMethodを経由して利用する。
    // 
    //    /**
    //     * Get the schema of the Table
    //     */
    //    public String getSchema() {
    //        return _schema;
    //    }

    /**
     * Set the schema of the Table
     */
    public void setSchema(String schema) {
        this._schema = schema;
    }

    // -----------------------------------------------------
    //                                         Table Comment
    //                                         -------------
    /**
     * Get the comment of the Table
     */
    public String getComment() {
        return _comment != null ? _comment : "";
    }

    /**
     * Set the comment of the Table
     */
    public void setComment(String comment) {
        this._comment = comment;
    }

    // -----------------------------------------------------
    //                                Basic Info Disp String
    //                                ----------------------
    public String getBasicInfoDispString() {
        return _name + "(" + _type + ")";
    }

    // -----------------------------------------------------
    //                                  Exclusive Table Name
    //                                  --------------------
    /**
     * Get annotation table name. (for S2Dao)
     * 
     * @return Annotation table name. (NotNull)
     */
    public String getAnnotationTableName() {
        return getTableSqlName();
    }

    /**
     * Get table sql-name.
     * 
     * @return Table sql-name. (NotNull)
     */
    public String getTableSqlName() {
        if (isAvailableAddingSchemaToTableSqlName()) {
            if (_schema != null && _schema.trim().length() != 0) {
                return _schema + "." + _name;
            }
        }
        return _name;
    }

    // -----------------------------------------------------
    //                                           Description
    //                                           -----------
    /**
     * Get the description for the Table
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Set the description for the Table
     *
     * @param newDescription description for the Table
     */
    public void setDescription(String newDescription) {
        _description = newDescription;
    }

    // -----------------------------------------------------
    //                                             Java Name
    //                                             ---------
    protected boolean _needsJavaNameConvert = true;

    public void setupNeedsJavaNameConvertFalse() {
        _needsJavaNameConvert = false;
    }

    public boolean needsJavaNameConvert() {
        return _needsJavaNameConvert;
    }

    /**
     * Get name to use in Java sources
     */
    public String getJavaName() {
        if (_javaName == null) {
            if (needsJavaNameConvert()) {
                _javaName = getDatabase().convertJavaNameByJdbcNameAsTable(getName());
            } else {
                _javaName = getName();// for sql2entity
            }
        }
        return _javaName;
    }

    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getUncapitalisedJavaName() {
        return StringUtils.uncapitalise(getJavaName());
    }

    protected boolean _needsJavaBeansRulePropertyNameConvert = true;

    public void setupNeedsJavaBeansRulePropertyNameConvertFalse() {
        _needsJavaNameConvert = false;
    }

    public boolean needsJavaBeansRulePropertyNameConvert() {
        return _needsJavaNameConvert;
    }

    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getJavaBeansRulePropertyName() {
        if (needsJavaBeansRulePropertyNameConvert()) {
            return getDatabase().decapitalizePropertyName(getDatabase().convertJavaNameByJdbcNameAsTable(getName()));
        } else {
            return getDatabase().decapitalizePropertyName(getJavaName());// for sql2entity
        }
    }

    /**
     * Set name to use in Java sources
     */
    public void setJavaName(String javaName) {
        this._javaName = javaName;
    }

    // -----------------------------------------------------
    //                                            Class Name
    //                                            ----------
    public String getBaseEntityClassName() {
        final String projectPrefix = getDatabase().getProjectPrefix();
        final String basePrefix = getDatabase().getBasePrefix();
        final String baseSuffixForEntity = getDatabase().getBaseSuffixForEntity();
        if (_schema != null && _schema.trim().length() != 0 && isExistSameNameTable()) {
            return projectPrefix + basePrefix + getSchemaPrefix() + getJavaName() + baseSuffixForEntity;
        } else {
            return projectPrefix + basePrefix + getJavaName() + baseSuffixForEntity;
        }
    }

    public String getBaseDaoClassName() {
        return getBaseEntityClassName() + "Dao";
    }

    public String getBaseBehaviorClassName() {
        return getBaseEntityClassName() + "Bhv";
    }

    public String getBaseConditionBeanClassName() {
        return getBaseEntityClassName() + "CB";
    }

    public String getAbstractBaseConditionQueryClassName() {
        final String projectPrefix = getDatabase().getProjectPrefix();
        final String basePrefix = getDatabase().getBasePrefix();
        if (_schema != null && _schema.trim().length() != 0 && isExistSameNameTable()) {
            return projectPrefix + "Abstract" + basePrefix + getSchemaPrefix() + getJavaName() + "CQ";
        } else {
            return projectPrefix + "Abstract" + basePrefix + getJavaName() + "CQ";
        }
    }

    public String getBaseConditionQueryClassName() {
        return getBaseEntityClassName() + "CQ";
    }

    public String getExtendedEntityClassName() {
        final String projectPrefix = getDatabase().getProjectPrefix();
        if (_schema != null && _schema.trim().length() != 0 && isExistSameNameTable()) {
            return projectPrefix + getSchemaPrefix() + getJavaName();
        } else {
            return projectPrefix + getJavaName();
        }
    }

    public String getRelationTraceClassName() {
        if (_schema != null && _schema.trim().length() != 0 && isExistSameNameTable()) {
            return getSchemaPrefix() + getJavaName();
        } else {
            return getJavaName();
        }
    }

    public String getDBMetaClassName() {
        return getExtendedEntityClassName() + "Dbm";
    }

    public String getDBMetaFullClassName() {
        return getDatabase().getBaseEntityPackage() + ".dbmeta." + getDBMetaClassName();
    }

    public String getExtendedDaoClassName() {
        return getExtendedEntityClassName() + "Dao";
    }

    public String getExtendedDaoFullClassName() {
        final String extendedDaoPackage = getDatabase().getExtendedDaoPackage();
        return extendedDaoPackage + "." + getExtendedDaoClassName();
    }

    public String getExtendedBehaviorClassName() {
        return getExtendedEntityClassName() + "Bhv";
    }

    public String getExtendedBehaviorFullClassName() {
        final String extendedBehaviorPackage = getDatabase().getExtendedBehaviorPackage();
        return extendedBehaviorPackage + "." + getExtendedBehaviorClassName();
    }

    public String getExtendedConditionBeanClassName() {
        return getExtendedEntityClassName() + "CB";
    }

    public String getExtendedConditionQueryClassName() {
        return getExtendedEntityClassName() + "CQ";
    }

    public String getExtendedConditionInlineQueryClassName() {
        return getExtendedEntityClassName() + "CIQ";
    }

    public String getNestSelectSetupperClassName() {
        return getExtendedEntityClassName() + "Nss";
    }

    public String getNestSelectSetupperTerminalClassName() {
        return getExtendedEntityClassName() + "Nsst";
    }

    protected String getSchemaPrefix() {
        if (_schema != null && _schema.trim().length() != 0 && isExistSameNameTable()) {
            return DfStringUtil.initCapAfterTrimming(_schema);
        }
        return "";
    }

    protected boolean _alreadyCheckedExistingSameNameTable;

    protected boolean isExistSameNameTable() {
        if (_alreadyCheckedExistingSameNameTable) {
            return _existSameNameTable;
        }
        _alreadyCheckedExistingSameNameTable = true;
        final List<Table> tableList = getDatabase().getTableList();
        int count = 0;
        for (Table table : tableList) {
            final String name = table.getName();
            if (_name.equals(name)) {
                ++count;
                if (count > 1) {
                    _existSameNameTable = true;
                    return _existSameNameTable;
                }
            }
        }
        _existSameNameTable = false;
        return _existSameNameTable;
    }

    // -----------------------------------------------------
    //                                        Component Name
    //                                        --------------
    public String getDaoComponentName() {
        return getDatabase().filterProjectSuffixForComponentName(getUncapitalisedJavaName()) + "Dao";
    }

    public String getBehaviorComponentName() {
        return getDatabase().filterProjectSuffixForComponentName(getUncapitalisedJavaName()) + "Bhv";
    }

    // -----------------------------------------------------
    //                                              IDMethod
    //                                              --------
    /**
     * Get the method for generating pk's
     */
    public String getIdMethod() {
        if (_idMethod == null) {
            return IDMethod.NO_ID_METHOD;
        } else {
            return _idMethod;
        }
    }

    /**
     * Set the method for generating pk's
     */
    public void setIdMethod(String idMethod) {
        this._idMethod = idMethod;
    }

    // -----------------------------------------------------
    //                                               SkipSql
    //                                               -------
    /**
     * Skip generating sql for this table (in the event it should
     * not be created from scratch).
     * @return value of skipSql.
     */
    public boolean isSkipSql() {
        return (_skipSql || isAlias() || isForReferenceOnly());
    }

    /**
     * Set whether this table should have its creation sql generated.
     * @param v  Value to assign to skipSql.
     */
    public void setSkipSql(boolean v) {
        this._skipSql = v;
    }

    // -----------------------------------------------------
    //                                                 Alias
    //                                                 -----
    /**
     * JavaName of om object this entry references.
     * @return value of external.
     */
    public String getAlias() {
        return _alias;
    }

    /**
     * Is this table specified in the schema or is there just
     * a foreign key reference to it.
     * @return value of external.
     */
    public boolean isAlias() {
        return (_alias != null);
    }

    /**
     * Set whether this table specified in the schema or is there just
     * a foreign key reference to it.
     * @param v  Value to assign to alias.
     */
    public void setAlias(String v) {
        this._alias = v;
    }

    /**
     * Interface which objects for this table will implement
     * @return value of interface.
     */
    public String getInterface() {
        return _enterface;
    }

    /**
     * Interface which objects for this table will implement
     * @param v  Value to assign to interface.
     */
    public void setInterface(String v) {
        this._enterface = v;
    }

    /**
     * When a table is abstract, it marks the business object class that is
     * generated as being abstract. If you have a table called "FOO", then the
     * Foo BO will be <code>public abstract class Foo</code>
     * This helps support class hierarchies
     *
     * @return value of abstractValue.
     */
    public boolean isAbstract() {
        return _abstractValue;
    }

    /**
     * When a table is abstract, it marks the business object
     * class that is generated as being abstract. If you have a
     * table called "FOO", then the Foo BO will be
     * <code>public abstract class Foo</code>
     * This helps support class hierarchies
     *
     * @param v  Value to assign to abstractValue.
     */
    public void setAbstract(boolean v) {
        this._abstractValue = v;
    }

    /**
     * Get the value of package.
     *
     * @return value of package.
     */
    public String getPackage() {
        if (_pkg != null) {
            return _pkg;
        } else {
            return this.getDatabase().getPackage();
        }
    }

    /**
     * Set the value of package.
     *
     * @param v  Value to assign to package.
     */
    public void setPackage(String v) {
        this._pkg = v;
    }

    /**
     * Returns a Collection of parameters relevant for the chosen
     * id generation method.
     */
    public List<IdMethodParameter> getIdMethodParameters() {
        return _idMethodParameters;
    }

    // -----------------------------------------------------
    //                                               Unknown
    //                                               -------
    /**
     * Gets the column that subclasses of the class representing this
     * table can be produced from.
     * 
     * @return Children column.
     */
    public Column getChildrenColumn() {
        return _inheritanceColumn;
    }

    /**
     * Get the objects that can be created from this table.
     * 
     * @return Children name list.
     */
    public List<String> getChildrenNames() {
        if (_inheritanceColumn == null || !_inheritanceColumn.isEnumeratedClasses()) {
            return null;
        }
        List<Inheritance> children = _inheritanceColumn.getChildren();
        List<String> names = new ArrayList<String>(children.size());
        for (int i = 0; i < children.size(); i++) {
            names.add(((Inheritance) children.get(i)).getClassName());
        }
        return names;
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * A utility function to create a new column from attrib and add it to this
     * table.
     *
     * @param attrib xml attributes for the column to add
     * @return the added column
     */
    public Column addColumn(Attributes attrib) {
        Column col = new Column();
        col.setTable(this);
        col.loadFromXML(attrib);
        addColumn(col);
        return col;
    }

    /**
     * Adds a new column to the column list and set the
     * parent table of the column to the current table
     *
     * @param col the column to add
     */
    public void addColumn(Column col) {
        col.setTable(this);
        if (col.isInheritance()) {
            _inheritanceColumn = col;
        }
        _columnList.add(col);
        _columnMap.put(col.getName(), col);

        col.setPosition(_columnList.size());
        _isNeedsTransactionInPostgres |= col.requiresTransactionInPostgres();
    }

    /**
     * Returns an Array containing all the columns in the table
     */
    public Column[] getColumns() {
        int size = _columnList.size();
        Column[] tbls = new Column[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Column) _columnList.get(i);
        }
        return tbls;
    }

    // -----------------------------------------------------
    //                                               Arrange
    //                                               -------
    /**
     * Returns an Array containing all the columns in the table
     */
    public String getColumnNameCommaString() {
        final StringBuilder sb = new StringBuilder();

        final List<Column> ls = _columnList;
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            final Column col = (Column) ls.get(i);
            sb.append(", ").append(col.getName());
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    /**
     * Get insert clause values as sql comment. {insertClauseValuesWithSqlComment}
     * <pre>
     * For availableNonPrimaryKeyWritable.
     * </pre>
     * @return Insert clause values with sql comment.
     */
    public String getInsertClauseValuesAsSqlComment() {
        final StringBuilder sb = new StringBuilder();

        final List<Column> ls = _columnList;
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            final Column col = (Column) ls.get(i);
            sb.append(", /*dto.").append(col.getUncapitalisedJavaName()).append("*/null ");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    /**
     * Get insert clause values with sql comment. {insertClauseValuesWithSqlComment}
     * <pre>
     * For availableNonPrimaryKeyWritable.
     * </pre>
     * @return Insert clause values with sql comment.
     */
    public String getInsertClauseValuesAsQuetionMark() {
        final StringBuilder sb = new StringBuilder();

        final List<Column> ls = _columnList;
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            sb.append(", ? ");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    /**
     * Utility method to get the number of columns in this table
     */
    public int getNumColumns() {
        return _columnList.size();
    }

    /**
     * Returns a specified column.
     *
     * @param name name of the column
     * @return Return a Column object or null if it does not exist.
     */
    public Column getColumn(String name) {
        return (Column) _columnMap.get(name);
    }

    // -----------------------------------------------------
    //                                         Determination
    //                                         -------------
    /**
     * Returns true if the table contains a specified column
     * @param name name of the column
     * @return true if the table contains the column
     * @deprecated
     */
    public boolean containsColumn(String name) {
        return _columnMap.containsKey(name);
    }

    /**
     * Returns true if the table contains a specified column
     * @param columnNameList the list of column name.
     * @return true if the table contains the column
     * @deprecated
     */
    public boolean containsColumn(List<String> columnNameList) {
        for (String columnName : columnNameList) {
            if (!containsColumn(columnName)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsColumnsByFlexibleName(List<String> columnNameList) {
        for (String columnName : columnNameList) {
            if (getColumn(columnName) == null) {
                return false;
            }
        }
        return true;
    }

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Returns an Array containing all the FKs in the table
     * 
     * @return Foreign-key array.
     */
    public ForeignKey[] getForeignKeys() {
        int size = _foreignKeys.size();
        ForeignKey[] tbls = new ForeignKey[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (ForeignKey) _foreignKeys.get(i);
        }
        return tbls;
    }

    /**
     * Return the first foreign key that includes col in it's list
     * of local columns.  Eg. Foreign key (a,b,c) refrences tbl(x,y,z)
     * will be returned of col is either a,b or c.
     * @param columnName column name included in the key
     * @return Return a Column object or null if it does not exist.
     */
    public ForeignKey getForeignKey(String columnName) {
        ForeignKey firstFK = null;
        for (Iterator<ForeignKey> iter = _foreignKeys.iterator(); iter.hasNext();) {
            ForeignKey key = iter.next();
            List<String> localColumns = key.getLocalColumns();
            for (String localColumnName : localColumns) {
                if (columnName.equalsIgnoreCase(localColumnName)) {
                    if (firstFK == null) {
                        firstFK = key;
                    } else {
                        // TODO: @jflute -- What's this?
                        // 
                        //System.out.println(col+" is in multiple FKs.  This is not"
                        //                   + " being handled properly.");
                        //throw new IllegalStateException("Cannot call method if " +
                        //    "column is referenced multiple times");
                    }
                    break;
                }
            }
        }
        return firstFK;
    }

    /**
     * A utility function to create a new foreign key
     * from attrib and add it to this table.
     *
     * @param attrib the xml attributes
     * @return the created ForeignKey
     */
    public ForeignKey addForeignKey(Attributes attrib) {
        ForeignKey fk = new ForeignKey();
        fk.loadFromXML(attrib);
        addForeignKey(fk);
        return fk;
    }

    // -----------------------------------------------------
    //                                               Arrange
    //                                               -------
    /**
     * Returns an comma string containing all the foreign table name.
     * 
     * @return Foreign table as comma string.
     */
    public String getForeignTableNameCommaString() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> foreignKeyList = _foreignKeys;
        final int size = foreignKeyList.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String name = fk.getForeignTableName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(fk.getForeignTableName());
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getForeignTableNameCommaStringWithHtmlHref() {// For SchemaHTML
        final StringBuilder sb = new StringBuilder();

        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> foreignKeyList = _foreignKeys;
        final int size = foreignKeyList.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String name = fk.getForeignTableName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append("<a href=\"#" + name + "\">").append(name).append("</a>");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    /**
     * Returns an comma string containing all the foreign property name.
     * 
     * @return Foreign property-name as comma string.
     */
    public String getForeignPropertyNameCommaString() {
        final StringBuilder sb = new StringBuilder();

        final List<ForeignKey> ls = _foreignKeys;
        final int size = ls.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = ls.get(i);
            sb.append(", ").append(fk.getForeignPropertyName());
        }
        final List<ForeignKey> referrerList = _referrers;
        for (ForeignKey fk : referrerList) {
            if (fk.isOneToOne()) {
                sb.append(", ").append(fk.getReferrerPropertyNameAsOne());
            }
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    /**
     * A list of tables referenced by foreign keys in this table
     *
     * @return A list of tables
     */
    public List<String> getForeignTableNames() {
        if (_foreignTableNames == null) {
            _foreignTableNames = new ArrayList<String>(1);
        }
        return _foreignTableNames;
    }

    /**
     * Adds a new FK to the FK list and set the
     * parent table of the column to the current table
     *
     * @param fk A foreign key
     */
    public void addForeignKey(ForeignKey fk) {
        fk.setTable(this);
        _foreignKeys.add(fk);

        if (_foreignTableNames == null) {
            _foreignTableNames = new ArrayList<String>(5);
        }
        if (_foreignTableNames.contains(fk.getForeignTableName())) {
            _foreignTableNames.add(fk.getForeignTableName());
        }
    }

    public boolean isExistForeignKey(String foreignTableName, List<String> localColumnNameList,
            List<String> foreignColumnNameList) {

        final DfFlexibleNameMap<String, Object> localColumnNameSet = createFlexibleNameMapByKeyList(localColumnNameList);
        final DfFlexibleNameMap<String, Object> foreignColumnNameSet = createFlexibleNameMapByKeyList(foreignColumnNameList);

        final ForeignKey[] fkArray = getForeignKeys();
        for (final ForeignKey key : fkArray) {
            if (isSameTableAsFlexible(key.getForeignTableName(), foreignTableName)) {
                final List<String> currentLocalColumnNameList = key.getLocalColumns();
                if (currentLocalColumnNameList == null || currentLocalColumnNameList.isEmpty()) {
                    String msg = "The foreignKey did not have local column name list: " + currentLocalColumnNameList;
                    msg = msg + " key.getForeignTableName()=" + key.getForeignTableName();
                    throw new IllegalStateException(msg);
                }
                final List<String> currentForeignColumnNameList = key.getForeignColumns();
                if (currentForeignColumnNameList == null || currentForeignColumnNameList.isEmpty()) {
                    String msg = "The foreignKey did not have foreign column name list: "
                            + currentForeignColumnNameList;
                    msg = msg + " key.getForeignTableName()=" + key.getForeignTableName();
                    throw new IllegalStateException(msg);
                }

                final Set<String> currentLocalColumnNameSet = new HashSet<String>(currentLocalColumnNameList);
                final Set<String> currentForeignColumnNameSet = new HashSet<String>(currentForeignColumnNameList);

                boolean sameAsLocal = false;
                boolean sameAsForeign = false;
                if (localColumnNameSet.size() == currentLocalColumnNameSet.size()) {
                    for (String currentLocalColumnName : currentLocalColumnNameSet) {
                        if (localColumnNameSet.containsKey(currentLocalColumnName)) {
                            sameAsLocal = true;
                        }
                    }
                }
                if (foreignColumnNameSet.size() == currentForeignColumnNameSet.size()) {
                    for (String currentForeignColumnName : currentForeignColumnNameSet) {
                        if (foreignColumnNameSet.containsKey(currentForeignColumnName)) {
                            sameAsForeign = true;
                        }
                    }
                }
                if (sameAsLocal && sameAsForeign) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isSameTableAsFlexible(String tableOne, String tableTwo) {
        if (tableOne.equalsIgnoreCase(tableTwo)) {
            return true;
        }
        tableOne = DfStringUtil.replace(tableOne, "_", "");
        tableTwo = DfStringUtil.replace(tableTwo, "_", "");
        if (tableOne.equalsIgnoreCase(tableTwo)) {
            return true;
        }
        return false;
    }

    protected <ELEMENT_TYPE> DfFlexibleNameMap<ELEMENT_TYPE, Object> createFlexibleNameMapByKeyList(
            List<ELEMENT_TYPE> keyList) {
        HashMap<ELEMENT_TYPE, Object> map = new HashMap<ELEMENT_TYPE, Object>();
        for (ELEMENT_TYPE name : keyList) {
            map.put(name, null);
        }
        return new DfFlexibleNameMap<ELEMENT_TYPE, Object>(map);
    }

    public boolean hasForeignKey() {
        return (getForeignKeys().length != 0);
    }

    // ===================================================================================
    //                                                                      Relation Index
    //                                                                      ==============
    protected java.util.Map<String, Integer> _relationIndexMap = new java.util.LinkedHashMap<String, Integer>();

    public int resolveForeignIndex(ForeignKey foreignKey) {
        return doResolveRelationIndex(foreignKey, false, false);// Ignore oneToOne
    }

    public int resolveReferrerIndexAsOne(ForeignKey foreignKey) {// oneToOne!
        return doResolveRelationIndex(foreignKey, true, true);
    }

    public int resolveRefererIndexAsOne(ForeignKey foreignKey) {// oneToOne!
        return resolveReferrerIndexAsOne(foreignKey);
    }

    public int resolveReferrerIndex(ForeignKey foreignKey) {
        return doResolveRelationIndex(foreignKey, true, false);
    }

    public int resolveRefererIndex(ForeignKey foreignKey) {
        return resolveReferrerIndex(foreignKey);
    }

    protected int doResolveRelationIndex(ForeignKey foreignKey, boolean referer, boolean oneToOne) {
        try {
            final String relationIndexKey = buildRefererIndexKey(foreignKey, referer, oneToOne);
            final Integer realIndex = _relationIndexMap.get(relationIndexKey);
            if (realIndex != null) {
                return realIndex;
            }
            final int minimumRelationIndex = extractMinimumRelationIndex(_relationIndexMap);
            _relationIndexMap.put(relationIndexKey, minimumRelationIndex);
            return minimumRelationIndex;
        } catch (RuntimeException e) {
            _log.warn("doResolveRelationIndex() threw the exception: " + foreignKey, e);
            throw e;
        }
    }

    protected String buildRefererIndexKey(ForeignKey foreignKey, boolean referer, boolean oneToOne) {
        if (!referer) {
            return foreignKey.getForeignJavaBeansRulePropertyName();
        } else {
            if (oneToOne) {
                return foreignKey.getRefererJavaBeansRulePropertyNameAsOne();
            } else {
                return foreignKey.getRefererJavaBeansRulePropertyName();
            }
        }
    }

    protected int extractMinimumRelationIndex(java.util.Map<String, Integer> relationIndexMap) {
        final Set<String> keySet = relationIndexMap.keySet();
        final List<Integer> indexList = new ArrayList<Integer>();
        for (String key : keySet) {
            final Integer index = relationIndexMap.get(key);
            indexList.add(index);
        }
        if (indexList.isEmpty()) {
            return 0;
        }
        Integer minimumIndex = -1;
        for (Integer currentIndex : indexList) {
            if (minimumIndex + 1 < currentIndex) {
                return minimumIndex + 1;
            }
            minimumIndex = currentIndex;
        }
        return indexList.size();
    }

    // ===================================================================================
    //                                                                      ???
    //                                                                      ==============
    /**
     * Return true if the column requires a transaction in Postgres
     */
    public boolean requiresTransactionInPostgres() {
        return _isNeedsTransactionInPostgres;
    }

    /**
     * A utility function to create a new id method parameter
     * from attrib and add it to this table.
     */
    public IdMethodParameter addIdMethodParameter(Attributes attrib) {
        IdMethodParameter imp = new IdMethodParameter();
        imp.loadFromXML(attrib);
        addIdMethodParameter(imp);
        return imp;
    }

    /**
     * Adds a new ID method parameter to the list and sets the parent
     * table of the column associated with the supplied parameter to this table.
     *
     * @param imp The column to add as an ID method parameter.
     */
    public void addIdMethodParameter(IdMethodParameter imp) {
        imp.setTable(this);
        if (_idMethodParameters == null) {
            _idMethodParameters = new ArrayList<IdMethodParameter>(2);
        }
        _idMethodParameters.add(imp);
    }

    /**
     * Adds a new index to the index list and set the
     * parent table of the column to the current table
     */
    public void addIndex(Index index) {
        index.setTable(this);
        _indices.add(index);
    }

    /**
     * A utility function to create a new index
     * from attrib and add it to this table.
     */
    public Index addIndex(Attributes attrib) {
        Index index = new Index();
        index.loadFromXML(attrib);
        addIndex(index);
        return index;
    }

    /**
     * Adds a new Unique to the Unique list and set the
     * parent table of the column to the current table
     */
    public void addUnique(Unique unique) {
        unique.setTable(this);
        _unices.add(unique);
    }

    /**
     * A utility function to create a new Unique
     * from attrib and add it to this table.
     *
     * @param attrib the xml attributes
     */
    public Unique addUnique(Attributes attrib) {
        Unique unique = new Unique();
        unique.loadFromXML(attrib);
        addUnique(unique);
        return unique;
    }

    public boolean hasForeignKeyOrReferrer() {
        return hasForeignKey() || hasReferrer();
    }

    public boolean hasForeignKeyOrReferer() {
        return hasForeignKeyOrReferrer();
    }

    public boolean hasForeignKeyOrReferrerAsOne() {
        return hasForeignKey() || hasReferrerAsOne();
    }

    public boolean hasForeignKeyOrRefererAsOne() {
        return hasForeignKeyOrReferrerAsOne();
    }

    // ===================================================================================
    //                                                                            Referrer
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Adds the foreign key from another table that refers to this table.
     *
     * @param fk A foreign key refering to this table
     */
    public void addReferrer(ForeignKey fk) {
        if (!fk.isForeignColumnsSameAsForeignTablePrimaryKeys()) {
            return;
        }
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        _referrers.add(fk);
    }

    public List<ForeignKey> getReferrerList() {
        return _referrers;
    }

    public List<ForeignKey> getRefererList() {
        return getReferrerList();
    }

    public List<ForeignKey> getReferrers() {
        return getReferrerList();
    }

    public boolean hasReferrer() {
        return (getReferrerList() != null && !getReferrerList().isEmpty());
    }

    public boolean hasReferrerAsMany() {
        final List<ForeignKey> referrers = getReferrerList();
        if (referrers == null || referrers.isEmpty()) {
            return false;
        }
        for (ForeignKey key : referrers) {
            if (!key.isOneToOne()) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasReferrerAsOne() {
        final List<ForeignKey> referrers = getReferrerList();
        if (referrers == null || referrers.isEmpty()) {
            return false;
        }
        for (ForeignKey key : referrers) {
            if (key.isOneToOne()) {
                return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------
    //                                               Arrange
    //                                               -------
    protected java.util.List<ForeignKey> _singleKeyReferrers = null;

    public boolean hasSingleKeyReferrer() {
        return !getSingleKeyReferrers().isEmpty();
    }

    public List<ForeignKey> getSingleKeyReferrers() {
        if (_singleKeyReferrers != null) {
            return _singleKeyReferrers;
        }
        _singleKeyReferrers = new ArrayList<ForeignKey>(5);
        if (!hasReferrer()) {
            return _singleKeyReferrers;
        }
        final List<ForeignKey> referrerList = getReferrers();
        for (ForeignKey referrer : referrerList) {
            if (!referrer.isSimpleKeyFK()) {
                continue;
            }
            _singleKeyReferrers.add(referrer);
        }
        return _singleKeyReferrers;
    }

    protected java.util.List<ForeignKey> _singleKeyStringOrIntegerReferrers = null;

    public boolean hasSingleKeyStringOrIntegerReferrer() {
        return !getSingleKeyStringOrIntegerReferrers().isEmpty();
    }

    public List<ForeignKey> getSingleKeyStringOrIntegerReferrers() {
        if (_singleKeyStringOrIntegerReferrers != null) {
            return _singleKeyStringOrIntegerReferrers;
        }
        _singleKeyStringOrIntegerReferrers = new ArrayList<ForeignKey>(5);
        if (!hasReferrer()) {
            return _singleKeyStringOrIntegerReferrers;
        }
        final List<ForeignKey> referrerList = getReferrers();
        for (ForeignKey referrer : referrerList) {
            if (!referrer.isSimpleKeyFK()) {
                continue;
            }
            Column localColumn = referrer.getLocalColumnAsOne();
            if (!(localColumn.isJavaNativeStringObject() || localColumn.isJavaNativeNumberObject())) {
                continue;
            }
            _singleKeyStringOrIntegerReferrers.add(referrer);
        }
        return _singleKeyStringOrIntegerReferrers;
    }

    public String getReferrerTableNameCommaString() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> ls = getReferrerList();
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = ls.get(i);
            final String name = fk.getTable().getName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(name);
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getReferrerTableNameCommaStringWithHtmlHref() {// For SchemaHTML
        final StringBuilder sb = new StringBuilder();
        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> referrerList = getReferrerList();
        final int size = referrerList.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = referrerList.get(i);
            final String name = fk.getTable().getName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append("<a href=\"#" + name + "\">").append(name).append("</a>");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getReferrerPropertyNameCommaString() {
        final StringBuilder sb = new StringBuilder();
        final List<ForeignKey> ls = getReferrerList();
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = ls.get(i);
            if (!fk.isOneToOne()) {
                sb.append(", ").append(fk.getReferrerPropertyName());
            }
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public void setContainsForeignPK(boolean b) {
        _containsForeignPK = b;
    }

    public boolean getContainsForeignPK() {
        return _containsForeignPK;
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * A name to use for creating a sequence if one is not specified.
     *
     * @return name of the sequence
     */
    public String getSequenceName() {// TODO: @jflute - Unnecessary?
        String result = null;
        if (getIdMethod().equals(NATIVE)) {
            List<IdMethodParameter> idMethodParams = getIdMethodParameters();
            if (idMethodParams == null) {
                result = getName() + "_SEQ";
            } else {
                result = ((IdMethodParameter) idMethodParams.get(0)).getValue();
            }
        }
        return result;
    }

    // ===================================================================================
    //                                                                               Index
    //                                                                               =====
    /**
     * Returns an Array containing all the indices in the table
     *
     * @return An array containing all the indices
     */
    public Index[] getIndices() {// TODO: @jflute - Unnecessary?
        int size = _indices.size();
        Index[] tbls = new Index[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Index) _indices.get(i);
        }
        return tbls;
    }

    /**
     * Returns an Array containing all the UKs in the table
     *
     * @return An array containing all the UKs
     */
    public Unique[] getUnices() {
        int size = _unices.size();
        Unique[] tbls = new Unique[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Unique) _unices.get(i);
        }
        return tbls;
    }

    public List<Unique> getUniqueList() {
        return _unices;
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    /**
     * Set the parent of the table
     *
     * @param parent the parant database
     */
    public void setDatabase(Database parent) {
        _tableParent = parent;
    }

    /**
     * Get the parent of the table
     *
     * @return the parant database
     */
    public Database getDatabase() {
        return _tableParent;
    }

    /**
     * Flag to determine if code/sql gets created for this table.
     * Table will be skipped, if return true.
     * @return value of forReferenceOnly.
     */
    public boolean isForReferenceOnly() {
        return _isForReferenceOnly;
    }

    /**
     * Flag to determine if code/sql gets created for this table.
     * Table will be skipped, if set to true.
     * @param v  Value to assign to forReferenceOnly.
     */
    public void setForReferenceOnly(boolean v) {
        this._isForReferenceOnly = v;
    }

    /**
     * Has relation? (hasForeignKey() or hasReferrer())
     * 
     * @return Determination.
     */
    public boolean hasRelation() {
        return (hasForeignKey() || hasReferrer());
    }

    // ==============================================================================
    //                                                                     PrimaryKey
    //                                                                     ==========
    /**
     * Returns the collection of Columns which make up the single primary
     * key for this table.
     *
     * @return A list of the primary key parts.
     */
    public List<Column> getPrimaryKey() {
        final List<Column> pk = new ArrayList<Column>(_columnList.size());

        final Iterator<Column> iter = _columnList.iterator();
        while (iter.hasNext()) {
            final Column col = (Column) iter.next();
            if (col.isPrimaryKey()) {
                pk.add(col);
            }
        }
        return pk;
    }

    public Column getPrimaryKeyAsOne() {
        if (getPrimaryKey().size() != 1) {
            String msg = "This method is for only-one primary-key: getPrimaryKey().size()=" + getPrimaryKey().size();
            msg = msg + " table=" + getName();
            throw new IllegalStateException(msg);
        }
        return getPrimaryKey().get(0);
    }

    public String getPrimaryKeyNameAsOne() {
        return getPrimaryKeyAsOne().getName();
    }

    public String getPrimaryKeyJavaNameAsOne() {
        return getPrimaryKeyAsOne().getJavaName();
    }

    public String getPrimaryKeyJavaNativeAsOne() {
        return getPrimaryKeyAsOne().getJavaNative();
    }

    public String getPrimaryKeyColumnDbNameOnlyFirstOne() {
        if (hasPrimaryKey()) {
            return getPrimaryKey().get(0).getName();
        } else {
            return "";
        }
    }

    /**
     * Returns PrimaryKeyArgsString. 
     *     [BigDecimal rcvlcqNo, String sprlptTp]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyArgsString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsString(getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyArgsString. 
     *     [BigDecimal rcvlcqNo, String sprlptTp]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyWhereStringWithSqlComment() {
        final StringBuilder sb = new StringBuilder();
        final List<Column> pk = getPrimaryKey();
        for (Column column : pk) {
            sb.append(" and ");
            sb.append(getName()).append(".").append(column.getName()).append(" = /*");
            sb.append(column.getUncapitalisedJavaName()).append("*/null");
        }
        sb.delete(0, " and ".length());

        return sb.toString();
    }

    /**
     * Returns PrimaryKeyArgsSetupString. 
     *     [setRcvlcqNo(rcvlcqNo);setSprlptTp(sprlptTp);]
     * 
     * @return Generated-String.
     */
    public String getPrimaryKeyArgsSetupString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupString(null, getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyArgsSetupString. 
     *     [beanName.setRcvlcqNo(rcvlcqNo);beanName.setSprlptTp(sprlptTp);]
     * 
     * @return Generated-String.
     */
    public String getPrimaryKeyArgsSetupString(String beanName) {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupString(beanName, getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyArgsSetupStringCSharp. 
     *     [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     *     
     * @return Generated-String.
     */
    public String getPrimaryKeyArgsSetupStringCSharp() {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupStringCSharp(null, getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyArgsSetupStringCSharp. 
     *     [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     *     
     * @return Generated-String.
     */
    public String getPrimaryKeyArgsSetupStringCSharp(String beanName) {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupStringCSharp(beanName, getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyNameCommaString. 
     *     [RCVLCQ_NO, SPRLPT_TP]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnNameCommaString(getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyUncapitalisedJavaNameCommaString. 
     *     [rcvlcqNo, sprlptTp]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyUncapitalisedJavaNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnUncapitalisedJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyJavaNameCommaString. 
     *     [RcvlcqNo, SprlptTp]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyJavaNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyGetterCommaString. 
     *     [getRcvlcqNo(), getSprlptTp()]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyGetterCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnGetterCommaString(getPrimaryKey());
    }

    /**
     * Returns PrimaryKeyOrderByAscString. 
     *     [RCVLCQ_NO asc, SPRLPT_TP asc]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyOrderByAscString() {
        return DfTorqueColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "asc");
    }

    /**
     * Returns PrimaryKeyOrderByDescString. 
     *     [RCVLCQ_NO asc, SPRLPT_TP asc]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyOrderByDescString() {
        return DfTorqueColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "desc");
    }

    /**
     * Returns PrimaryKeyDispValueString. 
     *     [value-value-value...]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyDispValueString() {
        return DfTorqueColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "get");
    }

    /**
     * Returns PrimaryKeyDispValueString. 
     *     [value-value-value...]
     * <p>
     * @return Generated-String.
     */
    public String getPrimaryKeyDispValueStringByGetterInitCap() {
        return DfTorqueColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "Get");
    }

    /**
     * Determine whether this table has a primary key.
     *
     * @return Determination.
     */
    public boolean hasPrimaryKey() {
        return (getPrimaryKey().size() > 0);
    }

    /**
     * Determine whether this table has two or more primary keys.
     *
     * @return Determination.
     */
    public boolean hasOnlyOnePrimaryKey() {
        return (getPrimaryKey().size() == 1);
    }

    /**
     * Determine whether this table has two or more primary keys.
     *
     * @return Determination.
     */
    public boolean hasTwoOrMorePrimaryKeys() {
        return (getPrimaryKey().size() > 1);
    }

    /**
     * Returns all parts of the primary key, separated by commas.
     *
     * @return A CSV list of primary key parts.
     */
    public String printPrimaryKey() {
        return printList(_columnList);
    }

    /**
     * Is this table writable?
     * 
     * @return Determination.
     */
    public boolean isWritable() {
        return hasPrimaryKey();
    }

    // ===================================================================================
    //                                                                 Attached-PrimaryKey
    //                                                                 ===================
    /**
     * Returns AttachedPKArgsSetupString. 
     *     [setRcvlcqNo(pk.rcvlcqNo);setSprlptTp(pk.sprlptTp);]
     * <p>
     * @param attachedPKVariableName
     * @return Generated-String.
     */
    public String getAttachedPKArgsSetupString(String attachedPKVariableName) {
        final List<Column> pkList = this.getPrimaryKey();
        String result = "";
        for (Iterator<Column> ite = pkList.iterator(); ite.hasNext();) {
            Column pk = (Column) ite.next();
            String javaName = pk.getJavaName();
            String pkGetString = attachedPKVariableName + ".get" + javaName + "()";
            String setterString = "set" + javaName + "(" + pkGetString + ");";
            if ("".equals(result)) {
                result = setterString;
            } else {
                result = result + setterString;
            }
        }
        return result;
    }

    // ===================================================================================
    //                                                                             Utility
    //                                                                             =======
    protected String makeJavaName(String fieldName) {
        String result = null;
        List<String> inputs = new ArrayList<String>(2);
        inputs.add(fieldName);
        inputs.add(_javaNamingMethod);
        try {
            result = NameFactory.generateName(NameFactory.JAVA_GENERATOR, inputs);
        } catch (EngineException e) {
            _log.error(e, e);
        }
        return StringUtils.capitalise(result);
    }

    /**
     * Returns the elements of the list, separated by commas.
     *
     * @param list a list of Columns
     * @return A CSV list.
     */
    private String printList(List<Column> list) {
        StringBuilder result = new StringBuilder();
        boolean comma = false;
        for (Iterator<Column> iter = list.iterator(); iter.hasNext();) {
            Column col = (Column) iter.next();
            if (col.isPrimaryKey()) {
                if (comma) {
                    result.append(',');
                } else {
                    comma = true;
                }
                result.append(col.getName());
            }
        }
        return result.toString();
    }

    // **********************************************************************************************
    //                                                                                     Properties
    //                                                                                     **********
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    public boolean hasClassification() {
        final Column[] columns = getColumns();
        for (Column column : columns) {
            if (column.hasClassification()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                     SequenceNextSql
    //                                                                     ===============
    /**
     * Determine whether this table uses a sequence.
     * @return Determination.
     */
    public boolean isUseSequence() {
        // PostgreSQLのSerial型だけ自動で出力
        if (hasPostgreSQLSerialSequenceName()) {
            return true;
        }
        final String sequenceName = getDatabase().getSequenceDefinitionMapSequence(getName());
        if (sequenceName == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get the value of sequence name from definition map.
     * @return Defined sequence name. (NotNull)
     */
    public String getDefinedSequenceName() {
        if (!isUseSequence()) {
            return "";
        }
        final String postgreSQLSerialSequenceName = extractPostgreSQLSerialSequenceName();
        if (postgreSQLSerialSequenceName != null) {
            return postgreSQLSerialSequenceName;
        }
        return getDatabase().getSequenceDefinitionMapSequence(getName());
    }

    /**
     * Get the value of sequence-next-sql as java name.
     * @return Name.　(NotNull)
     */
    public String getSequenceNextSql() {// For String Literal in Program.
        final String sequenceName = getDefinedSequenceName();
        if (sequenceName == null) {
            return "";
        }
        String result = getDatabase().getSequenceNextSql();
        result = DfPropertyUtil.convertAll(result, "$$sequenceName$$", sequenceName);

        // Escape double quotation for String Literal.
        if (result.contains("\"")) {
            result = DfPropertyUtil.convertAll(result, "\"", "\\\"");
        }

        return result;
    }

    public String getSequenceReturnType() {
        final DfSequenceIdentityProperties sequenceIdentityProperties = getProperties().getSequenceIdentityProperties();
        if (sequenceIdentityProperties.hasSequenceReturnType()) {
            return sequenceIdentityProperties.getSequenceReturnType();
        }
        final String sequenceReturnType = sequenceIdentityProperties.getSequenceReturnType();
        if (hasTwoOrMorePrimaryKeys()) {
            return sequenceReturnType;
        }
        final Column primaryKeyAsOne = getPrimaryKeyAsOne();
        if (primaryKeyAsOne.isJavaNativeNumberObject()) {
            return primaryKeyAsOne.getJavaNative();
        }
        return sequenceReturnType;
    }

    /**
     * Has sequence name of postgreSQL serial type column.
     * 
     * @return Determination.
     */
    protected boolean hasPostgreSQLSerialSequenceName() {
        final String postgreSQLSerialSequenceName = extractPostgreSQLSerialSequenceName();
        return postgreSQLSerialSequenceName != null;
    }

    /**
     * Extract sequence name of postgreSQL serial type column.
     * @return Sequence name of postgreSQL serial type column. (Nullable: If null, not found)
     */
    protected String extractPostgreSQLSerialSequenceName() {
        final DfBasicProperties basicProperties = getProperties().getBasicProperties();
        if (!basicProperties.isDatabasePostgreSQL() || !hasAutoIncrementColumn()) {
            return null;
        }
        final Column autoIncrementColumn = getAutoIncrementColumn();
        if (autoIncrementColumn == null) {
            return null;
        }
        final String defaultValue = autoIncrementColumn.getDefaultValue();
        if (defaultValue == null) {
            return null;
        }
        final String prefix = "nextval('";
        if (!defaultValue.startsWith(prefix)) {
            return null;
        }
        final String excludedPrefixString = defaultValue.substring(prefix.length());
        final int endIndex = excludedPrefixString.indexOf("'");
        if (endIndex < 0) {
            return null;
        }
        return excludedPrefixString.substring(0, endIndex);
    }

    public boolean isAvailableSequenceAssignedIdAnnotation() {
        final DfLittleAdjustmentProperties littleAdjustmentProperties = getProperties().getLittleAdjustmentProperties();
        if (littleAdjustmentProperties.isUseBuri()) {
            return true;
        }
        final DfSequenceIdentityProperties sequenceIdentityProperties = getProperties().getSequenceIdentityProperties();
        return sequenceIdentityProperties.isAvailableSequenceAssignedIdAnnotation();
    }

    /**
     * Get the value of assigned property name.
     * @return Assigned property name. (NotNull)
     */
    public String getAssignedPropertyName() {
        final Column primaryKeyAsOne = getPrimaryKeyAsOne();
        return getPropertyNameResolvedLanguage(primaryKeyAsOne);
    }

    protected String getPropertyNameResolvedLanguage(Column col) {
        if (getProperties().getBasicProperties().isTargetLanguageJava()) {
            return col.getJavaBeansRulePropertyName();
        } else if (getProperties().getBasicProperties().isTargetLanguageCSharp()) {
            return col.getJavaName();
        } else {
            return col.getUncapitalisedJavaName();
        }
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    /**
     * Determine whether this table uses an identity.
     * @return Determination.
     */
    public boolean isUseIdentity() {
        final DfBasicProperties basicProperties = getProperties().getBasicProperties();

        // S2DaoはPostgreSQLのIdentity利用をサポートしていないので問答無用でfalse。
        // かつ、Serial型はSequence利用が一般的なので問答無用でfalse。
        if (basicProperties.isDatabasePostgreSQL()) {
            return false;
        }

        // It gives priority to auto-increment information of JDBC.
        if (hasAutoIncrementColumn()) {
            return true;
        }

        return getDatabase().getIdentityDefinitionMapColumnName(getName()) != null;
    }

    public String getIdentityColumnName() {
        final Column column = getIdentityColumn();
        return column != null ? column.getName() : "";
    }

    public String getIdentityPropertyName() {
        final Column column = getIdentityColumn();
        return column != null ? getPropertyNameResolvedLanguage(column) : "";
    }

    protected Column getIdentityColumn() {
        if (!isUseIdentity()) {
            return null;
        }

        // It gives priority to auto-increment information of JDBC.
        final Column autoIncrementColumn = getAutoIncrementColumn();
        if (autoIncrementColumn != null) {
            return autoIncrementColumn;
        }

        final String columnName = (String) getDatabase().getIdentityDefinitionMapColumnName(getName());
        final Column column = getColumn(columnName);
        if (column == null) {
            String msg = "The columnName does not exist in the table: ";
            msg = msg + " tableName=" + getName() + " columnName=" + columnName;
            msg = msg + " columnList=" + getColumnNameCommaString();
            throw new IllegalStateException(msg);
        }
        return column;
    }

    protected boolean hasAutoIncrementColumn() {
        final Column[] columnArray = getColumns();
        for (Column column : columnArray) {
            if (column.isAutoIncrement()) {
                return true;
            }
        }
        return false;
    }

    protected Column getAutoIncrementColumn() {
        final Column[] columnArray = getColumns();
        for (Column column : columnArray) {
            if (column.isAutoIncrement()) {
                return column;
            }
        }
        return null;
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    public boolean hasOptimisticLock() {
        return isUseUpdateDate() || isUseVersionNo();
    }

    // ===================================================================================
    //                                                                          UpdateDate
    //                                                                          ==========
    /**
     * Determine whether this table uses a update date column.
     * @return Determination.
     */
    public boolean isUseUpdateDate() {
        if (getDatabase().isUpdateDateExceptTable(getName())) {
            return false;
        }
        final String updateDateColumnName = getDatabase().getUpdateDateFieldName();
        if ("".equals(updateDateColumnName)) {
            return false;
        }
        final Column column = getColumn(updateDateColumnName);
        if (column == null) {
            return false;
        }
        return true;
    }

    protected Column getUpdateDateColumn() {
        if (!isUseUpdateDate()) {
            return null;
        }
        final String fieldName = getDatabase().getUpdateDateFieldName();
        if (fieldName != null && fieldName.trim().length() != 0) {
            final Column column = getColumn(fieldName);
            return column;
        } else {
            return null;
        }
    }

    public String getUpdateDateColumnName() {
        final Column column = getUpdateDateColumn();
        if (column == null) {
            return "";
        }
        return column.getName();
    }

    public String getUpdateDateJavaName() {
        final Column column = getUpdateDateColumn();
        if (column == null) {
            return "";
        }
        return column.getJavaName();
    }

    public String getUpdateDateUncapitalisedJavaName() {
        return StringUtils.uncapitalise(getUpdateDateJavaName());
    }

    public String getUpdateDatePropertyName() {
        final Column column = getUpdateDateColumn();
        if (column == null) {
            return "";
        }
        return getPropertyNameResolvedLanguage(column);
    }

    /**
     * Get the value of update-date as uncapitalised java name.
     * @return String. (NotNull)
     */
    public String getUpdateDateJavaNative() {
        if (!isUseUpdateDate()) {
            return "";
        }
        final Column column = getColumn(getDatabase().getUpdateDateFieldName());
        return column.getJavaNative();
    }

    // ===================================================================================
    //                                                                           VersionNo
    //                                                                           =========
    protected static final String DEF_VERSION_NO = "version_no";

    protected boolean hasDefaultVersionNoColumn() {
        return getColumn(DEF_VERSION_NO) != null;
    }

    /**
     * Determine whether this table uses a version-no column.
     * 
     * @return Determination.
     */
    public boolean isUseVersionNo() {
        final String versionNoColumnName = getDatabase().getVersionNoFieldName();
        if ("".equals(versionNoColumnName)) {
            return hasDefaultVersionNoColumn();
        }
        final Column column = getColumn(versionNoColumnName);
        if (column == null) {
            return false;
        }
        return true;
    }

    public Column getVersionNoColumn() {
        if (!isUseVersionNo()) {
            return null;
        }
        final String versionNoColumnName = getDatabase().getVersionNoFieldName();
        if ("".equals(versionNoColumnName) && hasDefaultVersionNoColumn()) {
            return getColumn(DEF_VERSION_NO);
        } else {
            return getColumn(versionNoColumnName);
        }
    }

    public String getVersionNoColumnName() {
        final Column column = getVersionNoColumn();
        if (column == null) {
            return "";
        }
        return column.getName();
    }

    public String getVersionNoJavaName() {
        final Column column = getVersionNoColumn();
        if (column == null) {
            return "";
        }
        return column.getJavaName();
    }

    protected String buildVersionNoJavaName(String versionNoFieldName) {
        if (versionNoFieldName != null && versionNoFieldName.trim().length() != 0) {
            final DfBasicProperties basicProperties = getProperties().getBasicProperties();
            if (basicProperties.isJavaNameOfColumnSameAsDbName()) {
                return versionNoFieldName;
            } else {
                return makeJavaName(versionNoFieldName);
            }
        } else {
            return "";
        }
    }

    public String getVersionNoPropertyName() {
        final Column column = getVersionNoColumn();
        if (column == null) {
            return "";
        }
        return getPropertyNameResolvedLanguage(column);
    }

    public String getVersionNoUncapitalisedJavaName() {
        return buildVersionNoUncapitalisedJavaName(getVersionNoJavaName());
    }

    protected String buildVersionNoUncapitalisedJavaName(String versionNoJavaName) {
        return StringUtils.uncapitalise(versionNoJavaName);
    }

    // ===================================================================================
    //                                                                       Common Column
    //                                                                       =============
    /**
     * Is this table defined all common columns?
     * 
     * @return Determination.
     */
    public boolean hasAllCommonColumn() {
        final List<String> commonColumnNameList = getDatabase().getCommonColumnNameList();
        if (commonColumnNameList.isEmpty()) {
            return false;
        }
        final DfCommonColumnProperties commonColumnProperties = getProperties().getCommonColumnProperties();
        for (String commonColumnName : commonColumnNameList) {
            if (commonColumnProperties.isCommonColumnConvertion(commonColumnName)) {
                try {
                    findTargetColumnJavaNameByCommonColumnName(commonColumnName);
                } catch (IllegalStateException e) {
                    return false;
                }
            } else {
                if (!_columnMap.containsKey(commonColumnName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Column> getCommonColumnList() {
        final List<Column> ls = new ArrayList<Column>();
        if (!hasAllCommonColumn()) {
            return ls;
        }
        final List<String> commonColumnNameList = getDatabase().getCommonColumnNameList();
        for (String commonColumnName : commonColumnNameList) {
            ls.add(getColumn(commonColumnName));
        }
        return ls;
    }

    public String findTargetColumnJavaNameByCommonColumnName(String commonColumnName) {
        final DfCommonColumnProperties commonColumnProperties = getProperties().getCommonColumnProperties();
        if (commonColumnProperties.isCommonColumnConvertion(commonColumnName)) {
            String filteredCommonColumn = convertCommonColumnName(commonColumnName, commonColumnProperties);
            final Column column = getCommonColumnConvertion(commonColumnName, filteredCommonColumn);
            return column.getJavaName();
        } else {
            final Column column = getCommonColumnNormal(commonColumnName);
            return column.getJavaName();
        }
    }

    public String findTargetColumnNameByCommonColumnName(String commonColumnName) {
        final DfCommonColumnProperties commonColumnProperties = getProperties().getCommonColumnProperties();
        if (commonColumnProperties.isCommonColumnConvertion(commonColumnName)) {
            String filteredCommonColumn = convertCommonColumnName(commonColumnName, commonColumnProperties);
            final Column column = getCommonColumnConvertion(commonColumnName, filteredCommonColumn);
            return column.getName();
        } else {
            final Column column = getCommonColumnNormal(commonColumnName);
            return column.getName();
        }
    }

    protected Column getCommonColumnNormal(String commonColumnName) {
        final Column column = getColumn(commonColumnName);
        if (column == null) {
            String msg = "Not found column by '" + commonColumnName + "'.";
            throw new IllegalStateException(msg);
        }
        return column;
    }

    protected Column getCommonColumnConvertion(String commonColumnName, String filteredCommonColumn) {
        final Column column = getColumn(filteredCommonColumn);
        if (column == null) {
            String msg = "Not found column by '" + filteredCommonColumn + "'. Original name is '" + commonColumnName
                    + "'.";
            throw new IllegalStateException(msg);
        }
        return column;
    }

    protected String convertCommonColumnName(String commonColumnName,
            final DfCommonColumnProperties commonColumnProperties) {
        String filteredCommonColumn = commonColumnProperties.filterCommonColumn(commonColumnName);
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "TABLE_NAME", getName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "table_name", getName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "TableName", getJavaName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "tablename", getJavaName());
        return filteredCommonColumn;
    }

    // ===============================================================================
    //                                                         Non PrimaryKey Writable
    //                                                         =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        return getProperties().getLittleAdjustmentProperties().isAvailableNonPrimaryKeyWritable();
    }

    // ===============================================================================
    //                                                 Adding Schema to Table Sql-Name
    //                                                 ===============================
    protected boolean isAvailableAddingSchemaToTableSqlName() {
        return getProperties().getLittleAdjustmentProperties().isAvailableAddingSchemaToTableSqlName();
    }

    // ===================================================================================
    //                                                                            Flex DTO
    //                                                                            ========
    public boolean isFlexDtoBindable() {
        return getProperties().getFlexDtoProperties().isBindable(getName());
    }

    // ===================================================================================
    //                                                                     Behavior Filter
    //                                                                     ===============
    public boolean hasBehaviorFilterBeforeColumn() {
        return hasBehaviorFilterBeforeInsertColumn() || hasBehaviorFilterBeforeUpdateColumn();
    }

    protected List<Column> _behaviorFilterBeforeInsertColumnList;

    public boolean hasBehaviorFilterBeforeInsertColumn() {
        return !getBehaviorFilterBeforeInsertColumnList().isEmpty();
    }

    public List<Column> getBehaviorFilterBeforeInsertColumnList() {
        if (_behaviorFilterBeforeInsertColumnList != null) {
            return _behaviorFilterBeforeInsertColumnList;
        }
        DfBehaviorFilterProperties prop = getProperties().getBehaviorFilterProperties();
        Map<String, Object> map = prop.getBeforeInsertMap();
        Set<String> columnNameSet = map.keySet();
        _behaviorFilterBeforeInsertColumnList = new ArrayList<Column>();
        for (String columnName : columnNameSet) {
            Column column = getColumn(columnName);
            if (column != null) {
                _behaviorFilterBeforeInsertColumnList.add(column);
                String expression = (String) map.get(columnName);
                if (expression == null || expression.trim().length() == 0) {
                    String msg = "The value expression was not found in beforeInsertMap: column=" + column;
                    throw new IllegalStateException(msg);
                }
                column.setBehaviorFilterBeforeInsertColumnExpression(expression);
            }
        }
        return _behaviorFilterBeforeInsertColumnList;
    }

    public String getBehaviorFilterBeforeInsertColumnExpression(String columName) {
        DfBehaviorFilterProperties prop = getProperties().getBehaviorFilterProperties();
        Map<String, Object> map = prop.getBeforeInsertMap();
        return (String) map.get(columName);
    }

    protected List<Column> _behaviorFilterBeforeUpdateColumnList;

    public boolean hasBehaviorFilterBeforeUpdateColumn() {
        return !getBehaviorFilterBeforeUpdateColumnList().isEmpty();
    }

    public List<Column> getBehaviorFilterBeforeUpdateColumnList() {
        if (_behaviorFilterBeforeUpdateColumnList != null) {
            return _behaviorFilterBeforeUpdateColumnList;
        }
        DfBehaviorFilterProperties prop = getProperties().getBehaviorFilterProperties();
        Map<String, Object> map = prop.getBeforeUpdateMap();
        Set<String> columnNameSet = map.keySet();
        _behaviorFilterBeforeUpdateColumnList = new ArrayList<Column>();
        for (String columnName : columnNameSet) {
            Column column = getColumn(columnName);
            if (column != null) {
                _behaviorFilterBeforeUpdateColumnList.add(column);
                String expression = (String) map.get(columnName);
                if (expression == null || expression.trim().length() == 0) {
                    String msg = "The value expression was not found in beforeUpdateMap: column=" + column;
                    throw new IllegalStateException(msg);
                }
                column.setBehaviorFilterBeforeUpdateColumnExpression(expression);
            }
        }
        return _behaviorFilterBeforeUpdateColumnList;
    }

    // ===================================================================================
    //                                                                            toString
    //                                                                            ========
    /**
     * Returns a XML representation of this table.
     *
     * @return XML representation of this table
     */
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("<table name=\"").append(getName()).append('\"');

        if (_javaName != null) {
            result.append(" javaName=\"").append(_javaName).append('\"');
        }

        if (_idMethod != null) {
            result.append(" idMethod=\"").append(_idMethod).append('\"');
        }

        if (_skipSql) {
            result.append(" skipSql=\"").append(new Boolean(_skipSql)).append('\"');
        }

        if (_abstractValue) {
            result.append(" abstract=\"").append(new Boolean(_abstractValue)).append('\"');
        }

        //        if (_baseClass != null) {
        //            result.append(" baseClass=\"").append(_baseClass).append('\"');
        //        }
        //
        //        if (_basePeer != null) {
        //            result.append(" basePeer=\"").append(_basePeer).append('\"');
        //        }

        result.append(">\n");

        if (_columnList != null) {
            for (Iterator<Column> iter = _columnList.iterator(); iter.hasNext();) {
                result.append(iter.next());
            }
        }

        if (_foreignKeys != null) {
            for (Iterator<ForeignKey> iter = _foreignKeys.iterator(); iter.hasNext();) {
                result.append(iter.next());
            }
        }

        if (_idMethodParameters != null) {
            Iterator<IdMethodParameter> iter = _idMethodParameters.iterator();
            while (iter.hasNext()) {
                result.append(iter.next());
            }
        }

        result.append("</table>\n");

        return result.toString();
    }

    public boolean isSql2EntityTypeSafeCursor() {
        return _sql2entityTypeSafeCursor;
    }

    public void setSql2EntityTypeSafeCursor(boolean sql2entityTypeSafeCursor) {
        this._sql2entityTypeSafeCursor = sql2entityTypeSafeCursor;
    }

}