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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.friends.torque.DfTorqueColumnListToStringUtil;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.schemahtml.DfSchemaHtmlBuilder;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfStringUtil;
import org.xml.sax.Attributes;

/**
 * @author Modified by jflute
 */
public class Table {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(Table.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private List<Column> _columnList;
    private List<ForeignKey> _foreignKeys;
    private List<Index> _indices;
    private List<Unique> _unices;
    private List<IdMethodParameter> _idMethodParameters;
    private String _name;
    private String _type;
    private String _schema;
    private String _plainComment;
    private String _description;
    private String _javaName;
    protected String _javaNamingMethod;
    private Database _tableParent;
    private List<ForeignKey> _referrers;
    private List<String> _foreignTableNames;
    private boolean _containsForeignPK;
    private Column _inheritanceColumn;
    protected StringKeyMap<Column> _columnMap = StringKeyMap.createAsFlexibleOrdered();
    private boolean _isForReferenceOnly;
    private boolean _existSameNameTable;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    private boolean _sql2entityTypeSafeCursor;

    // [Unused on DBFlute]
    // private String _idMethod;
    // private AttributeListImpl attributes;
    // private boolean _skipSql;
    // private boolean _abstractValue;
    // private boolean _isHeavyIndexing;
    // private String _alias;
    // private String _interface;
    // private String _pkg;

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
    //                                                                         XML Loading
    //                                                                         ===========
    /**
     * Load the table object from an XML tag.
     * @param attrib XML attributes. (NotNull)
     */
    public void loadFromXML(Attributes attrib) {
        _name = attrib.getValue("name");
        _type = attrib.getValue("type");
        _schema = attrib.getValue("schema");
        _plainComment = attrib.getValue("comment");
        _javaName = attrib.getValue("javaName");

        // It retrieves the method for converting from specified name to a java name.
        // *Attention: Always use Default-JavaNamingMethod!!!
        _javaNamingMethod = getDatabase().getDefaultJavaNamingMethod();

        // [Unused on DBFlute]
        // _idMethod = attrib.getValue("idMethod");
        // if ("null".equals(_idMethod)) {
        //     _idMethod = defaultIdMethod;
        // }
        // if ("autoincrement".equals(_idMethod) || "sequence".equals(_idMethod)) {
        //     _log.warn("The value '" + _idMethod + "' for Torque's "
        //             + "table.idMethod attribute has been deprecated in favor " + "of '" + NATIVE
        //             + "'.  Please adjust your " + "Torque XML schema accordingly.");
        //     _idMethod = NATIVE;
        // }
        // _skipSql = "true".equals(attrib.getValue("skipSql"));
        // _pkg = attrib.getValue("package");
        // _alias = attrib.getValue("alias");
        // _interface = attrib.getValue("interface");
        // _abstractValue = "true".equals(attrib.getValue("abstract"));
        // _baseClass = attrib.getValue("baseClass");
        // _basePeer = attrib.getValue("basePeer");

        // These are unused on DBFlute
        _description = attrib.getValue("description");
    }

    // ===================================================================================
    //                                                                             Unknown
    //                                                                             =======
    /**
     * <p>A hook for the SAX XML parser to call when this table has
     * been fully loaded from the XML, and all nested elements have
     * been processed.</p>
     * <p>Performs heavy indexing and naming of elements which weren't
     * provided with a name.</p>
     */
    public void doFinalInitialization() {
        // Heavy indexing must wait until after all columns composing
        // a table's primary key have been parsed.
        // [Unused on DBFlute]
        // if (_isHeavyIndexing) {
        //     doHeavyIndexing();
        // }

        // Name any indices which are missing a name using the
        // appropriate algorithm.
        doNaming();
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
    //                                            Alias Name
    //                                            ----------
    public boolean hasAlias() {
        final String alias = getAlias();
        return alias != null && alias.trim().length() > 0;
    }

    public String getAlias() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = _plainComment;
        if (comment != null) {
            final String alias = prop.extractAliasFromDbComment(comment);
            if (alias != null) {
                return alias;
            }
        }
        return "";
    }

    public String getAliasExpression() { // for expression '(alias)name'
        final String alias = getAlias();
        if (alias == null || alias.trim().length() == 0) {
            return "";
        }
        return "(" + alias + ")";
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

    public boolean isTypeTable() {
        return _type != null && _type.equalsIgnoreCase("table");
    }

    // -----------------------------------------------------
    //                                          Table Schema
    //                                          ------------
    /**
     * Get the schema of the Table
     * @return The schema. (Nullable)
     */
    public String getSchema() {
        return _schema;
    }

    /**
     * Set the schema of the Table
     * @param schema The name of schema. (Nullable)
     */
    public void setSchema(String schema) {
        this._schema = schema;
    }

    public boolean hasSchema() {
        return _schema != null && _schema.trim().length() > 0;
    }

    public boolean isMainSchema() {
        return !isAdditionalSchema();
    }

    public boolean isAdditionalSchema() {
        if (_schema != null && _schema.trim().length() > 0) {
            return getDatabaseProperties().isAdditionalSchema(_schema);
        }
        return false;
    }

    // -----------------------------------------------------
    //                                         Table Comment
    //                                         -------------
    public String getPlainComment() {
        return _plainComment;
    }

    public void setPlainComment(String plainComment) {
        this._plainComment = plainComment;
    }

    public boolean hasComment() {
        final String comment = getComment();
        return comment != null && comment.trim().length() > 0;
    }

    public String getComment() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.extractCommentFromDbComment(_plainComment);
        return comment != null ? comment : "";
    }

    public String getCommentForSchemaHtml() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        String comment = prop.resolveTextForSchemaHtml(getComment());
        return comment != null ? comment : "";
    }

    public boolean isCommentForJavaDocValid() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return hasComment() && prop.isEntityJavaDocDbCommentValid();
    }

    public String getCommentForJavaDoc() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveTextForJavaDoc(getComment(), "");
        return comment != null ? comment : "";
    }

    public boolean isCommentForDBMetaValid() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return hasComment() && prop.isEntityDBMetaDbCommentValid();
    }

    public String getCommentForDBMeta() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveTextForDBMeta(getComment());
        return comment != null ? comment : "";
    }

    // -----------------------------------------------------
    //                                        Display String
    //                                        --------------
    public String getBasicInfoDispString() {
        final String type = getType();
        return getAliasExpression() + getName() + (type != null ? " that is " + getType() : "");
    }

    public String getTitleForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        sb.append("type=").append(_type);
        if (isAdditionalSchema()) {
            sb.append(", schema=").append(_schema);
        }
        sb.append(", primaryKey={").append(getPrimaryKeyNameCommaString()).append("}");
        sb.append(", nameLength=").append(getName().length());
        sb.append(", columnCount=").append(getColumns().length);
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return " title=\"" + prop.resolveAttributeForSchemaHtml(sb.toString()) + "\"";
    }

    // -----------------------------------------------------
    //                                   Especial Table Name
    //                                   -------------------
    /**
     * Get annotation table name. (for S2Dao)
     * @return Annotation table name. (NotNull)
     */
    public String getAnnotationTableName() {
        return getTableSqlName();
    }

    /**
     * Get table SQL-name.
     * @return Table SQL-name. (NotNull)
     */
    public String getTableSqlName() {
        if (isAvailableAddingSchemaToTableSqlName()) {
            if (_schema != null && _schema.trim().length() > 0) {
                return _schema + "." + _name;
            }
        }
        if (isAdditionalSchema()) { // for resolving additional schema
            return _schema + "." + _name;
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
                _javaName = getName(); // for sql2entity mainly
            }
            _javaName = filterBuriJavaNameIfNeeds(_javaName);
        }
        return _javaName;
    }

    protected String filterBuriJavaNameIfNeeds(String javaName) { // for Buri
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        if (buriProperties.isUseBuri() && isBuriInternal()) {
            final String arranged = buriProperties.arrangeBuriTableJavaName(_javaName);
            if (arranged != null) {
                return arranged;
            }
        }
        return javaName;
    }

    /**
     * Set name to use in Java sources
     */
    public void setJavaName(String javaName) {
        this._javaName = javaName;
    }

    // -----------------------------------------------------
    //                               Uncapitalised Java Name
    //                               -----------------------
    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getUncapitalisedJavaName() {
        return StringUtils.uncapitalise(getJavaName());
    }

    // -----------------------------------------------------
    //                         Java Beans Rule Property Name
    //                         -----------------------------
    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getJavaBeansRulePropertyName() {
        return DfStringUtil.decapitalizePropertyName(getJavaName());
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
        return getDatabase().filterComponentNameWithProjectPrefix(getUncapitalisedJavaName()) + "Dao";
    }

    public String getBehaviorComponentName() {
        return getDatabase().filterComponentNameWithProjectPrefix(getUncapitalisedJavaName()) + "Bhv";
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
     * A utility function to create a new column from attrib and add it to this table.
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
    }

    /**
     * Returns a List containing all the columns in the table
     */
    public List<Column> getColumnList() {
        return _columnList;
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

    public String getPropertyNameCommaString() {
        final StringBuilder sb = new StringBuilder();

        final List<Column> ls = _columnList;
        int size = ls.size();
        for (int i = 0; i < size; i++) {
            final Column col = (Column) ls.get(i);
            sb.append(", ").append(col.getJavaBeansRulePropertyName());
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
            sb.append(", /*pmb.").append(col.getUncapitalisedJavaName()).append("*/null ");
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
     * @param name name of the column
     * @return Return a Column object or null if it does not exist.
     */
    public Column getColumn(String name) {
        return _columnMap.get(name);
    }

    // -----------------------------------------------------
    //                                         Determination
    //                                         -------------
    public boolean containsColumn(List<String> columnNameList) {
        for (String columnName : columnNameList) {
            if (getColumn(columnName) == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasUtilDateColumn() {
        for (Column column : _columnList) {
            if (column.isJavaNativeUtilDate()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasByteArrayColumn() {
        for (Column column : _columnList) {
            if (column.isJavaNativeByteArray()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasByteArrayColumnInEqualsHashcode() {
        for (Column column : getEqualsHashcodeColumnList()) {
            if (column.isJavaNativeByteArray()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Returns an Array containing all the FKs in the table
     * @return Foreign-key array.
     */
    public ForeignKey[] getForeignKeys() {
        final int size = _foreignKeys.size();
        final ForeignKey[] tbls = new ForeignKey[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (ForeignKey) _foreignKeys.get(i);
        }
        return tbls;
    }

    /**
     * Return the first foreign key that includes column in it's list
     * of local columns.  Eg. Foreign key (a,b,c) references table(x,y,z)
     * will be returned of column is either a,b or c.
     * @param columnName column name included in the key
     * @return Return a Column object or null if it does not exist.
     */
    public ForeignKey getForeignKey(String columnName) {
        ForeignKey firstFK = null;
        for (Iterator<ForeignKey> iter = _foreignKeys.iterator(); iter.hasNext();) {
            ForeignKey key = iter.next();
            List<String> localColumns = key.getLocalColumns();
            if (DfStringUtil.containsIgnoreCase(columnName, localColumns)) {
                if (firstFK == null) {
                    firstFK = key;
                }
            }
        }
        return firstFK;
    }

    public List<ForeignKey> getForeignKeyList(String columnName) {
        List<ForeignKey> fkList = new ArrayList<ForeignKey>();
        for (Iterator<ForeignKey> iter = _foreignKeys.iterator(); iter.hasNext();) {
            ForeignKey key = iter.next();
            List<String> localColumns = key.getLocalColumns();
            if (DfStringUtil.containsIgnoreCase(columnName, localColumns)) {
                fkList.add(key);
            }
        }
        return fkList;
    }

    /**
     * A utility function to create a new foreign key
     * from attrib and add it to this table.
     * @param attrib the xml attributes
     * @return the created ForeignKey
     */
    public ForeignKey addForeignKey(Attributes attrib) {
        final ForeignKey fk = new ForeignKey();
        fk.loadFromXML(attrib);
        addForeignKey(fk);
        return fk;
    }

    // -----------------------------------------------------
    //                                               Arrange
    //                                               -------
    /**
     * Returns an comma string containing all the foreign table name. <br />
     * And contains one-to-one table.
     * @return Foreign table as comma string.
     */
    public String getForeignTableNameCommaString() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> foreignKeyList = _foreignKeys;
        for (int i = 0; i < foreignKeyList.size(); i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String name = fk.getForeignTableName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(name).append(fk.hasFixedSuffix() ? "(" + fk.getFixedSuffix() + ")" : "");
        }
        List<ForeignKey> referrerList = _referrers;
        for (int i = 0; i < referrerList.size(); i++) {
            final ForeignKey fk = referrerList.get(i);
            if (!fk.isOneToOne()) {
                continue;
            }
            final String name = fk.getTable().getName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(name).append("(AsOne)");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getForeignTableNameCommaStringWithHtmlHref() { // for SchemaHTML
        final StringBuilder sb = new StringBuilder();
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder(prop);
        final String delimiter = ", ";
        final List<ForeignKey> foreignKeyList = _foreignKeys;
        final int size = foreignKeyList.size();
        if (size == 0) {
            return "&nbsp;";
        }
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String foreignTableName = fk.getForeignTableName();
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, foreignTableName, delimiter));
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
    }

    /**
     * Returns an comma string containing all the foreign property name.
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
        final Set<String> localColumnNameSet = createFlexibleSet(localColumnNameList);
        final Set<String> foreignColumnNameSet = createFlexibleSet(foreignColumnNameList);
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
                        if (localColumnNameSet.contains(currentLocalColumnName)) {
                            sameAsLocal = true;
                        }
                    }
                }
                if (foreignColumnNameSet.size() == currentForeignColumnNameSet.size()) {
                    for (String currentForeignColumnName : currentForeignColumnNameSet) {
                        if (foreignColumnNameSet.contains(currentForeignColumnName)) {
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

    protected Set<String> createFlexibleSet(List<String> keyList) {
        final Set<String> flset = StringSet.createAsFlexibleOrdered();
        flset.addAll(keyList);
        return flset;
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
                return foreignKey.getReferrerJavaBeansRulePropertyNameAsOne();
            } else {
                return foreignKey.getReferrerJavaBeansRulePropertyName();
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
    //                                                                                 ???
    //                                                                                 ===
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

    public boolean hasForeignKeyOrReferrerAsOne() {
        return hasForeignKey() || hasReferrerAsOne();
    }

    public boolean hasTwoOrMoreKeyReferrer() {
        List<Column> primaryKeyList = getPrimaryKey();
        for (Column primaryKey : primaryKeyList) {
            List<ForeignKey> referrers = primaryKey.getReferrers();
            for (ForeignKey referrer : referrers) {
                if (!referrer.isSimpleKeyFK()) {
                    return true;
                }
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Referrer
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Adds the foreign key from another table that refers to this table.
     * @param fk A foreign key referring to this table
     * @return Can the foreign key be referrer?
     */
    public boolean addReferrer(ForeignKey fk) {
        if (!fk.canBeReferrer()) {
            return false;
        }
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        _referrers.add(fk);
        return true;
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
            if (fk.isOneToOne()) {
                continue;
            }
            final String name = fk.getTable().getName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(name);
        }
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = ls.get(i);
            if (!fk.isOneToOne()) {
                continue;
            }
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

    public String getReferrerTableNameCommaStringWithHtmlHref() { // for SchemaHTML
        final StringBuilder sb = new StringBuilder();
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder(prop);
        final String delimiter = ", ";
        final List<ForeignKey> referrerList = getReferrerList();
        final int size = referrerList.size();
        if (size == 0) {
            return "&nbsp;";
        }
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = referrerList.get(i);
            final String referrerTableName = fk.getTable().getName();
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, referrerTableName, delimiter));
        }
        sb.delete(0, delimiter.length());
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
    //                                                                          Unique Key
    //                                                                          ==========
    /**
     * Returns an Array containing all the UKs in the table
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
    //                                                                               Index
    //                                                                               =====
    /**
     * Returns an Array containing all the indices in the table
     * @return An array containing all the indices
     */
    public Index[] getIndices() {
        int size = _indices.size();
        Index[] tbls = new Index[size];
        for (int i = 0; i < size; i++) {
            tbls[i] = (Index) _indices.get(i);
        }
        return tbls;
    }

    public List<Index> getIndexList() {
        return _indices;
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    /**
     * Set the parent of the table
     * @param parent the parant database
     */
    public void setDatabase(Database parent) {
        _tableParent = parent;
    }

    /**
     * Get the parent of the table
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
    public boolean isForReferenceOnly() { // Unused on DBFlute but template uses...
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

    public List<Column> getEqualsHashcodeColumnList() {
        if (hasPrimaryKey()) {
            return getPrimaryKey();
        } else {
            return getColumnList();
        }
    }

    // -----------------------------------------------------
    //                                      Arguments String
    //                                      ----------------
    /**
     * Returns primaryKeyArgsString. [BigDecimal rcvlcqNo, String sprlptTp]
     * @return The value of primaryKeyArgsString. (NotNull)
     */
    public String getPrimaryKeyArgsString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsJavaDocString. [AtMarkparam rcvlcqNo The one of primary key. (NotNull)...]
     * @return The value of primaryKeyArgsJavaDocString. (NotNull)
     */
    public String getPrimaryKeyArgsJavaDocString() {
        final String ln = getBasicProperties().getSourceCodeLineSeparator();
        return DfTorqueColumnListToStringUtil.getColumnArgsJavaDocString(getPrimaryKey(), ln);
    }

    /**
     * Returns primaryKeyArgsAssertString. [assertObjectNotNull("rcvlcqNo", rcvlcqNo); assert...;]
     * @return The value of primaryKeyArgsAssertString. (NotNull)
     */
    public String getPrimaryKeyArgsAssertString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsAssertString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsAssertStringCSharp. [AssertObjectNotNull("rcvlcqNo", rcvlcqNo); assert...;]
     * @return The value of primaryKeyArgsAssertStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsAssertStringCSharp() {
        return DfTorqueColumnListToStringUtil.getColumnArgsAssertStringCSharp(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupString. [setRcvlcqNo(rcvlcqNo);setSprlptTp(sprlptTp);]
     * @return The value of primaryKeyArgsSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsSetupString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupString(null, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupString. [beanName.setRcvlcqNo(rcvlcqNo);beanName.setSprlptTp(sprlptTp);]
     * @param beanName The name of bean. (Nullable)
     * @return The value of primaryKeyArgsSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsSetupString(String beanName) {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupString(beanName, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupStringCSharp. [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     * @return The value of primaryKeyArgsSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsSetupStringCSharp() {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupStringCSharp(null, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupStringCSharp. [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     * @param beanName The name of bean. (Nullable)
     * @return The value of primaryKeyArgsSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsSetupStringCSharp(String beanName) {
        return DfTorqueColumnListToStringUtil.getColumnArgsSetupStringCSharp(beanName, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsConditionSetupString. [cb.query().setRcvlcqNo_Equal(rcvlcqNo);cb.query()...;]
     * @return The value of primaryKeyArgsConditionSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsConditionSetupString() {
        return DfTorqueColumnListToStringUtil.getColumnArgsConditionSetupString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsConditionSetupStringCSharp. [cb.Query().SetRcvlcqNo_Equal(rcvlcqNo);cb.Query()...;]
     * @return The value of primaryKeyArgsConditionSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsConditionSetupStringCSharp() {
        return DfTorqueColumnListToStringUtil.getColumnArgsConditionSetupStringCSharp(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsCallingString. [rcvlcqNo, sprlptTp]
     * @return The value of primaryKeyArgsCallingString. (NotNull)
     */
    public String getPrimaryKeyArgsCallingString() {
        return getPrimaryKeyUncapitalisedJavaNameCommaString();
    }

    // -----------------------------------------------------
    //                               ParameterComment String
    //                               -----------------------
    /**
     * Returns primaryKeyWhereStringWithSqlComment. [BigDecimal rcvlcqNo, String sprlptTp]
     * @return The value of primaryKeyWhereStringWithSqlComment. (NotNull)
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

    // -----------------------------------------------------
    //                                       Order-By String
    //                                       ---------------
    /**
     * Returns primaryKeyOrderByAscString. [RCVLCQ_NO asc, SPRLPT_TP asc]
     * @return Generated string.
     */
    public String getPrimaryKeyOrderByAscString() {
        return DfTorqueColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "asc");
    }

    /**
     * Returns primaryKeyOrderByDescString. [RCVLCQ_NO asc, SPRLPT_TP asc]
     * @return Generated string.
     */
    public String getPrimaryKeyOrderByDescString() {
        return DfTorqueColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "desc");
    }

    // -----------------------------------------------------
    //                                        Display String
    //                                        --------------
    /**
     * Returns primaryKeyDispValueString. [value-value-value...]
     * @return Generated string.
     */
    public String getPrimaryKeyDispValueString() {
        return DfTorqueColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "get");
    }

    /**
     * Returns primaryKeyDispValueString. [value-value-value...]
     * @return Generated string.
     */
    public String getPrimaryKeyDispValueStringByGetterInitCap() {
        return DfTorqueColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "Get");
    }

    // -----------------------------------------------------
    //                                    Basic Comma String
    //                                    ------------------
    /**
     * Returns primaryKeyNameCommaString. [RCVLCQ_NO, SPRLPT_TP]
     * @return Generated string.
     */
    public String getPrimaryKeyNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyUncapitalisedJavaNameCommaString. [rcvlcqNo, sprlptTp]
     * @return Generated string.
     */
    public String getPrimaryKeyUncapitalisedJavaNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnUncapitalisedJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyJavaNameCommaString. [RcvlcqNo, SprlptTp]
     * @return Generated string.
     */
    public String getPrimaryKeyJavaNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyGetterCommaString. [getRcvlcqNo(), getSprlptTp()]
     * @return Generated string.
     */
    public String getPrimaryKeyGetterCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnGetterCommaString(getPrimaryKey());
    }

    // -----------------------------------------------------
    //                                         Determination
    //                                         -------------
    /**
     * Determine whether this table has a primary key.
     * @return Determination.
     */
    public boolean hasPrimaryKey() {
        return (getPrimaryKey().size() > 0);
    }

    /**
     * Determine whether this table has two or more primary keys.
     * @return Determination.
     */
    public boolean hasOnlyOnePrimaryKey() {
        return (getPrimaryKey().size() == 1);
    }

    /**
     * Determine whether this table has two or more primary keys.
     * @return Determination.
     */
    public boolean hasTwoOrMorePrimaryKeys() {
        return (getPrimaryKey().size() > 1);
    }

    /**
     * Returns all parts of the primary key, separated by commas.
     * @return A CSV list of primary key parts.
     */
    public String printPrimaryKey() {
        return printList(_columnList);
    }

    /**
     * Is this table writable?
     * @return Determination.
     */
    public boolean isWritable() {
        return hasPrimaryKey();
    }

    // ===================================================================================
    //                                                                 Attached PrimaryKey
    //                                                                 ===================
    /**
     * Returns AttachedPKArgsSetupString. [setRcvlcqNo(pk.rcvlcqNo);setSprlptTp(pk.sprlptTp);]
     * @param attachedPKVariableName
     * @return Generated string.
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

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    protected DfSequenceIdentityProperties getSequenceIdentityProperties() {
        return getProperties().getSequenceIdentityProperties();
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
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
    //                                                                            Sequence
    //                                                                            ========
    /**
     * Determine whether this table uses a sequence.
     * @return Determination.
     */
    public boolean isUseSequence() {
        final String sequenceName = getSequenceIdentityProperties().getSequenceName(getName());
        if (sequenceName == null || sequenceName.trim().length() == 0) {
            if (hasPostgreSQLSerialSequenceName()) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get the value of sequence name from definition map.
     * @return The defined sequence name. (NotNull: If a sequence is not found, return empty string.)
     */
    public String getDefinedSequenceName() {
        if (!isUseSequence()) {
            return "";
        }
        final String sequenceName = getSequenceIdentityProperties().getSequenceName(getName());
        if (sequenceName == null || sequenceName.trim().length() == 0) {
            final String serialSequenceName = extractPostgreSQLSerialSequenceName();
            if (serialSequenceName != null && serialSequenceName.trim().length() > 0) {
                return serialSequenceName;
            }
            return ""; // if it uses sequence, unreachable
        }
        return sequenceName;
    }

    /**
     * Get the SQL for next value of sequence.
     * @return The SQL for next value of sequence. (NotNull: If a sequence is not found, return empty string.)
     */
    public String getSequenceNextValSql() {
        if (!isUseSequence()) {
            return "";
        }
        final DBDef dbdef = getBasicProperties().getCurrentDBDef();
        final String sequenceName = getDefinedSequenceName();
        final String sql = dbdef.dbway().buildSequenceNextValSql(sequenceName);
        return sql != null ? sql : "";
    }

    public BigDecimal getSequenceMinimumValue() {
        if (!isUseSequence()) {
            return null;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final DataSource ds = getDatabase().getDataSource();
        BigDecimal value = prop.getSequenceMinimumValueByTableName(ds, getSchema(), getName());
        if (value == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                value = prop.getSequenceMinimumValueBySequenceName(ds, getSchema(), sequenceName);
            }
        }
        return value;
    }

    public String getSequenceMinimumValueExpression() {
        final BigDecimal value = getSequenceMinimumValue();
        return value != null ? value.toString() : "null";
    }

    public BigDecimal getSequenceMaximumValue() {
        if (!isUseSequence()) {
            return null;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final DataSource ds = getDatabase().getDataSource();
        BigDecimal value = prop.getSequenceMaximumValueByTableName(ds, getSchema(), getName());
        if (value == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                value = prop.getSequenceMaximumValueBySequenceName(ds, getSchema(), sequenceName);
            }
        }
        return value;
    }

    public String getSequenceMaximumValueExpression() {
        final BigDecimal value = getSequenceMaximumValue();
        return value != null ? value.toString() : "null";
    }

    public Integer getSequenceIncrementSize() {
        if (!isUseSequence()) {
            return null;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final DataSource ds = getDatabase().getDataSource();
        Integer size = prop.getSequenceIncrementSizeByTableName(ds, getSchema(), getName());
        if (size == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                size = prop.getSequenceIncrementSizeBySequenceName(ds, getSchema(), sequenceName);
            }
        }
        return size;
    }

    public String getSequenceIncrementSizeExpression() {
        final Integer value = getSequenceIncrementSize();
        return value != null ? value.toString() : "null";
    }

    public Integer getSequenceCacheSize() {
        if (!isUseSequence()) {
            return null;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final DataSource ds = getDatabase().getDataSource();
        return prop.getSequenceCacheSize(ds, getSchema(), getName());
    }

    public String getSequenceCacheSizeExpression() {
        final Integer value = getSequenceCacheSize();
        return value != null ? value.toString() : "null";
    }

    public String getSequenceReturnType() {
        final DfSequenceIdentityProperties sequenceIdentityProperties = getProperties().getSequenceIdentityProperties();
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
        final DfBasicProperties basicProperties = getBasicProperties();
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
        return isBuriTarget();
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
        if (getBasicProperties().isTargetLanguageJava()) {
            return col.getJavaBeansRulePropertyName();
        } else if (getBasicProperties().isTargetLanguageCSharp()) {
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
        final DfBasicProperties basicProperties = getBasicProperties();

        // because serial type is treated as sequence 
        if (basicProperties.isDatabasePostgreSQL()) {
            return false;
        }

        // It gives priority to auto-increment information of JDBC.
        if (hasAutoIncrementColumn()) {
            return true;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        return prop.getIdentityColumnName(getName()) != null;
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
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final String columnName = prop.getIdentityColumnName(getName());
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
        final String updateDateColumnName = getProperties().getOptimisticLockProperties().getUpdateDateFieldName();
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
        final String fieldName = getProperties().getOptimisticLockProperties().getUpdateDateFieldName();
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
        final Column column = getColumn(getProperties().getOptimisticLockProperties().getUpdateDateFieldName());
        return column.getJavaNative();
    }

    // ===================================================================================
    //                                                                           VersionNo
    //                                                                           =========
    /**
     * Determine whether this table uses a version-no column.
     * @return Determination.
     */
    public boolean isUseVersionNo() {
        final String versionNoColumnName = getProperties().getOptimisticLockProperties().getVersionNoFieldName();
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
        final String versionNoColumnName = getProperties().getOptimisticLockProperties().getVersionNoFieldName();
        return getColumn(versionNoColumnName);
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
            final DfBasicProperties basicProperties = getBasicProperties();
            if (basicProperties.isColumnNameCamelCase()) {
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
     * @return Determination.
     */
    public boolean hasAllCommonColumn() {
        try {
            return doHasAllCommonColumn();
        } catch (RuntimeException e) {
            _log.debug("Failed to execute 'Table.hasAllCommonColumn()'!", e);
            throw e;
        }
    }

    protected boolean doHasAllCommonColumn() {
        if (!isWritable()) {
            return false;
        }
        if (isAdditionalSchema()) {
            final Map<String, DfAdditionalSchemaInfo> schemaMap = getDatabaseProperties().getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = schemaMap.get(_schema);
            if (schemaInfo.isSuppressCommonColumn()) {
                return false;
            }
        }
        final List<String> commonColumnNameList = getDatabase().getCommonColumnNameList();
        if (commonColumnNameList.isEmpty()) {
            return false;
        }
        for (String commonColumnName : commonColumnNameList) {
            if (getProperties().getCommonColumnProperties().isCommonColumnConversion(commonColumnName)) {
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
        if (!isWritable()) {
            return new ArrayList<Column>();
        }
        final List<Column> ls = new ArrayList<Column>();
        if (!hasAllCommonColumn()) {
            return ls;
        }
        final List<String> commonColumnNameList = getDatabase().getCommonColumnNameList();
        for (String commonColumnName : commonColumnNameList) {
            ls.add(getColumn(findTargetColumnNameByCommonColumnName(commonColumnName)));
        }
        return ls;
    }

    public String findTargetColumnJavaNameByCommonColumnName(String commonColumnName) {
        final DfCommonColumnProperties prop = getProperties().getCommonColumnProperties();
        if (prop.isCommonColumnConversion(commonColumnName)) {
            final String filteredCommonColumn = convertCommonColumnName(commonColumnName, prop);
            final Column column = getCommonColumnConversion(commonColumnName, filteredCommonColumn);
            return column.getJavaName();
        } else {
            final Column column = getCommonColumnNormal(commonColumnName);
            return column.getJavaName();
        }
    }

    public String findTargetColumnNameByCommonColumnName(String commonColumnName) {
        final DfCommonColumnProperties prop = getProperties().getCommonColumnProperties();
        if (prop.isCommonColumnConversion(commonColumnName)) {
            final String filteredCommonColumn = convertCommonColumnName(commonColumnName, prop);
            final Column column = getCommonColumnConversion(commonColumnName, filteredCommonColumn);
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

    protected Column getCommonColumnConversion(String commonColumnName, String filteredCommonColumn) {
        final Column column = getColumn(filteredCommonColumn);
        if (column == null) {
            String msg = "Not found column by '" + filteredCommonColumn + "': ";
            msg = msg + "original=" + commonColumnName;
            throw new IllegalStateException(msg);
        }
        return column;
    }

    protected String convertCommonColumnName(String commonColumnName, DfCommonColumnProperties prop) {
        String filteredCommonColumn = prop.filterCommonColumn(commonColumnName);
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "TABLE_NAME", getName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "table_name", getName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "TableName", getJavaName());
        filteredCommonColumn = DfStringUtil.replace(filteredCommonColumn, "tablename", getJavaName());
        return filteredCommonColumn;
    }

    // ===================================================================================
    //                                                             Non PrimaryKey Writable
    //                                                             =======================
    public boolean isAvailableNonPrimaryKeyWritable() {
        if (hasPrimaryKey()) {
            return false;
        }
        return getProperties().getLittleAdjustmentProperties().isAvailableNonPrimaryKeyWritable();
    }

    // ===================================================================================
    //                                                     Adding Schema to Table SQL-Name
    //                                                     ===============================
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
    //                                                            Buri(Friendly Framework)
    //                                                            ========================
    public boolean isBuriTarget() {
        if (!hasOnlyOnePrimaryKey()) {
            return false;
        }
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        return buriProperties.isUseBuri() && buriProperties.isTargetTable(getName()) && hasTableProcess();
    }

    protected boolean hasTableProcess() {
        return !getTableProcessForMethodNameList().isEmpty();
    }

    public boolean isBuriInternal() {
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        return buriProperties.isUseBuri() && buriProperties.isBuriInternalTable(getJavaName());
    }

    public List<String> getTableProcessForMethodNameList() {
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        return buriProperties.getTableProcessForMethodNameList(getName());
    }

    public boolean isBuriAllRoundStateHistory() {
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        return buriProperties.isBuriAllRoundStateHistory(getName());
    }

    // ===================================================================================
    //                                                                     Behavior Filter
    //                                                                     ===============
    public boolean hasBehaviorFilterBeforeColumn() {
        try {
            return hasBehaviorFilterBeforeInsertColumn() || hasBehaviorFilterBeforeUpdateColumn();
        } catch (RuntimeException e) {
            _log.debug("Failed to execute 'Table.hasBehaviorFilterBeforeColumn()'!", e);
            throw e;
        }
    }

    protected List<Column> _behaviorFilterBeforeInsertColumnList;

    public boolean hasBehaviorFilterBeforeInsertColumn() {
        return !getBehaviorFilterBeforeInsertColumnList().isEmpty();
    }

    public List<Column> getBehaviorFilterBeforeInsertColumnList() {
        if (_behaviorFilterBeforeInsertColumnList != null) {
            return _behaviorFilterBeforeInsertColumnList;
        }
        final DfBehaviorFilterProperties prop = getProperties().getBehaviorFilterProperties();
        final Map<String, Object> map = prop.getBeforeInsertMap();
        final Set<String> columnNameSet = map.keySet();
        _behaviorFilterBeforeInsertColumnList = new ArrayList<Column>();
        final Set<String> commonColumnNameSet = new HashSet<String>();
        if (hasAllCommonColumn()) {
            final List<Column> commonColumnList = getCommonColumnList();
            for (Column commonColumn : commonColumnList) {
                commonColumnNameSet.add(commonColumn.getName());
            }
        }
        for (String columnName : columnNameSet) {
            Column column = getColumn(columnName);
            if (column != null && !commonColumnNameSet.contains(columnName)) {
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
        Set<String> commonColumnNameSet = new HashSet<String>();
        if (hasAllCommonColumn()) {
            List<Column> commonColumnList = getCommonColumnList();
            for (Column commonColumn : commonColumnList) {
                commonColumnNameSet.add(commonColumn.getName());
            }
        }
        for (String columnName : columnNameSet) {
            Column column = getColumn(columnName);
            if (column != null && !commonColumnNameSet.contains(columnName)) {
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
    //                                                                 Behavior Query Path
    //                                                                 ===================
    protected Map<String, Map<String, String>> getBehaviorQueryPathMap() {
        final Map<String, Map<String, Map<String, String>>> tableBqpMap = getDatabase().getTableBqpMap();
        final Map<String, Map<String, String>> elementMap = tableBqpMap.get(getName());
        return elementMap != null ? elementMap : new HashMap<String, Map<String, String>>();
    }

    public boolean hasBehaviorQueryPath() {
        return !getBehaviorQueryPathList().isEmpty();
    }

    public List<String> getBehaviorQueryPathList() {
        final Map<String, Map<String, String>> tableBqpMap = getBehaviorQueryPathMap();
        return new ArrayList<String>(tableBqpMap.keySet());
    }

    protected Map<String, String> getBehaviorQueryPathElementMap(String behaviorQueryPath) {
        final Map<String, Map<String, String>> tableBqpMap = getBehaviorQueryPathMap();
        return tableBqpMap.get(behaviorQueryPath);
    }

    public String getBehaviorQueryPathDisplayName(String behaviorQueryPath) {
        final String subDirectoryPath = getBehaviorQueryPathSubDirectoryPath(behaviorQueryPath);
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(subDirectoryPath)) {
            final String connector = "_";
            return DfStringUtil.replace(subDirectoryPath, "/", connector) + connector + behaviorQueryPath;
        } else {
            return behaviorQueryPath;
        }
    }

    public String getBehaviorQueryPathFileName(String behaviorQueryPath) {
        final String path = getBehaviorQueryPathPath(behaviorQueryPath);
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(path)) {
            final int fileNameIndex = path.lastIndexOf("/");
            if (fileNameIndex >= 0) {
                return path.substring(fileNameIndex + "/".length());
            } else {
                return path;
            }
        } else {
            return "";
        }
    }

    public String getBehaviorQueryPathSubDirectoryPath(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String subDirectoryPath = elementMap.get("subDirectoryPath");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(subDirectoryPath) ? subDirectoryPath : "";
    }

    public String getBehaviorQueryPathPath(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String path = elementMap.get("path");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(path) ? path : "";
    }

    public boolean hasBehaviorQueryPathCustomizeEntity(String behaviorQueryPath) {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(getBehaviorQueryPathCustomizeEntity(behaviorQueryPath));
    }

    public String getBehaviorQueryPathCustomizeEntity(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String customizeEntity = elementMap.get("customizeEntity");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(customizeEntity) ? customizeEntity : "";
    }

    public boolean hasBehaviorQueryPathParameterBean(String behaviorQueryPath) {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(getBehaviorQueryPathParameterBean(behaviorQueryPath));
    }

    public String getBehaviorQueryPathParameterBean(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String parameterBean = elementMap.get("parameterBean");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(parameterBean) ? parameterBean : "";
    }

    public boolean hasBehaviorQueryPathCursor(String behaviorQueryPath) {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(getBehaviorQueryPathCursor(behaviorQueryPath));
    }

    public String getBehaviorQueryPathCursor(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String cursor = elementMap.get("cursor");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(cursor) ? cursor : "";
    }

    public String getBehaviorQueryPathCursorForSchemaHtml(String behaviorQueryPath) {
        final String cursor = getBehaviorQueryPathCursor(behaviorQueryPath);
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(cursor) ? " *" + cursor : "";
    }

    public String getBehaviorQueryPathTitle(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String title = elementMap.get("title");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(title) ? title : "";
    }

    public String getBehaviorQueryPathTitleForSchemaHtml(String behaviorQueryPath) {
        String title = getBehaviorQueryPathTitle(behaviorQueryPath);
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(title)) {
            final DfDocumentProperties prop = getProperties().getDocumentProperties();
            title = prop.resolveTextForSchemaHtml(title);
            return "(" + title + ")";
        } else {
            return "&nbsp;";
        }
    }

    public boolean hasBehaviorQueryPathDescription(String behaviorQueryPath) {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(getBehaviorQueryPathDescription(behaviorQueryPath));
    }

    public String getBehaviorQueryPathDescription(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String description = elementMap.get("description");
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(description) ? description : "";
    }

    public String getBehaviorQueryPathDescriptionForSchemaHtml(String behaviorQueryPath) {
        String description = getBehaviorQueryPathDescription(behaviorQueryPath);
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(description)) {
            final DfDocumentProperties prop = getProperties().getDocumentProperties();
            description = prop.resolvePreTextForSchemaHtml(description);
            return description;
        } else {
            return "&nbsp;";
        }
    }

    // This method is not necessary because sql2entity cannot use this.
    //public List<String> getBehaviorQueryPathDefinitionList() {
    //}

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Returns a XML representation of this table.
     * @return XML representation of this table
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<table name=\"").append(getName()).append('\"');
        if (_javaName != null) {
            result.append(" javaName=\"").append(_javaName).append('\"');
        }
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

    // ===================================================================================
    //                                                         Sql2Entity Type Safe Cursor
    //                                                         ===========================
    public boolean isSql2EntityTypeSafeCursor() {
        return _sql2entityTypeSafeCursor;
    }

    public void setSql2EntityTypeSafeCursor(boolean sql2entityTypeSafeCursor) {
        this._sql2entityTypeSafeCursor = sql2entityTypeSafeCursor;
    }
}