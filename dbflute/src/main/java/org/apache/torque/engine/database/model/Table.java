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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.EngineException;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.doc.schemahtml.DfSchemaHtmlBuilder;
import org.seasar.dbflute.logic.generate.column.DfColumnListToStringUtil;
import org.seasar.dbflute.logic.sql2entity.bqp.DfBehaviorQueryPathSetupper;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBehaviorFilterProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfCommonColumnProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties.NonCompilableChecker;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.DfSimpleDtoProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
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
    // -----------------------------------------------------
    //                                              Database
    //                                              --------
    private Database _database;

    // -----------------------------------------------------
    //                                      Table Definition
    //                                      ----------------
    private String _name;
    private String _type;
    private UnifiedSchema _unifiedSchema;
    private String _plainComment;
    private boolean _existSameNameTable;

    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    private final List<Column> _columnList = new ArrayList<Column>();
    private final StringKeyMap<Column> _columnMap = StringKeyMap.createAsFlexible(); // only used as key-value

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
    // map style because of removing in the final initialization
    // and names of foreign key should be unique in a table
    private final Map<String, ForeignKey> _foreignKeyMap = StringKeyMap.createAsFlexibleOrdered();

    // on the other hand, names of referrer are not
    // always unique because a referrer may be synonym
    // (fortunately, removing is not required about referrer)
    private final List<ForeignKey> _referrerList = new ArrayList<ForeignKey>(5);

    // -----------------------------------------------------
    //                                                Unique
    //                                                ------
    private final List<Unique> _unices = new ArrayList<Unique>(5);

    // -----------------------------------------------------
    //                                                 Index
    //                                                 -----
    private final List<Index> _indices = new ArrayList<Index>(5);

    // -----------------------------------------------------
    //                                       Java Definition
    //                                       ---------------
    private String _javaName;

    // -----------------------------------------------------
    //                                 Sql2Entity Definition
    //                                 ---------------------
    private boolean _sql2EntityCustomize;
    private boolean _sql2EntityCustomizeHasNested;
    private boolean _sql2EntityTypeSafeCursor;
    private String _sql2EntityOutputDirectory;

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
        _name = name;
    }

    // -----------------------------------------------------
    //                                         Load from XML
    //                                         -------------
    /**
     * Load the table object from an XML tag.
     * @param attrib XML attributes. (NotNull)
     * @param tableFilter The filter of table by name when reading XML. (NullAllowed)
     * @return Should be the table excepted?
     */
    public boolean loadFromXML(Attributes attrib, XmlReadingTableFilter tableFilter) {
        _name = attrib.getValue("name"); // table name
        _type = attrib.getValue("type"); // TABLE, VIEW, SYNONYM...
        _unifiedSchema = UnifiedSchema.createAsDynamicSchema(attrib.getValue("schema"));
        if (tableFilter != null && tableFilter.isExcept(_unifiedSchema, _name)) {
            return false;
        }
        _plainComment = attrib.getValue("comment");
        _javaName = attrib.getValue("javaName");
        return true;
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    /**
     * Set the parent of the table
     * @param parent the parant database
     */
    public void setDatabase(Database parent) {
        _database = parent;
    }

    /**
     * Get the parent of the table
     * @return the parant database
     */
    public Database getDatabase() {
        return _database;
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
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
    //                                           Custom Name
    //                                           -----------
    /**
     * Get annotation table name. (for S2Dao)
     * @return Annotation table name. (NotNull)
     */
    public String getAnnotationTableName() {
        return getTableSqlName();
    }

    /**
     * Get table SQL name.
     * @return Table SQL name. (NotNull)
     */
    public String getTableSqlName() {
        final String tableName = quoteTableNameIfNeeds(_name);
        return filterSchemaSqlPrefix(tableName);
    }

    public String getTableSqlNameDirectUse() {
        final String tableName = quoteTableNameIfNeedsDirectUse(_name);
        return filterSchemaSqlPrefix(tableName);
    }

    protected String filterSchemaSqlPrefix(String tableName) {
        if (hasSchema()) {
            return _unifiedSchema.buildSqlName(tableName);
        }
        return tableName;
    }

    protected String quoteTableNameIfNeeds(String tableName) {
        final DfLittleAdjustmentProperties prop = getProperties().getLittleAdjustmentProperties();
        return prop.quoteTableNameIfNeeds(tableName);
    }

    protected String quoteTableNameIfNeedsDirectUse(String tableName) {
        final DfLittleAdjustmentProperties prop = getProperties().getLittleAdjustmentProperties();
        return prop.quoteTableNameIfNeedsDirectUse(tableName);
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

    public boolean isTypeView() {
        return _type != null && _type.equalsIgnoreCase("view");
    }

    // -----------------------------------------------------
    //                                          Table Schema
    //                                          ------------
    public UnifiedSchema getUnifiedSchema() {
        return _unifiedSchema != null ? _unifiedSchema : null;
    }

    public void setUnifiedSchema(UnifiedSchema unifiedSchema) { // basically for Sql2Entity
        _unifiedSchema = unifiedSchema;
    }

    public String getDocumentSchema() {
        if (_unifiedSchema == null) {
            return "";
        }
        if (getDatabase().hasCatalogAdditionalSchema()) {
            return _unifiedSchema.getCatalogSchema();
        } else {
            return _unifiedSchema.getPureSchema();
        }
    }

    protected String getPureCatalog() { // NOT contain catalog name
        return _unifiedSchema != null ? _unifiedSchema.getPureSchema() : null;
    }

    protected String getPureSchema() { // NOT contain catalog name
        return _unifiedSchema != null ? _unifiedSchema.getPureSchema() : null;
    }

    public boolean hasSchema() {
        return _unifiedSchema != null ? _unifiedSchema.hasSchema() : false;
    }

    public boolean isMainSchema() {
        return hasSchema() && getUnifiedSchema().isMainSchema();
    }

    public boolean isAdditionalSchema() {
        return hasSchema() && getUnifiedSchema().isAdditionalSchema();
    }

    public boolean isCatalogAdditionalSchema() {
        return hasSchema() && getUnifiedSchema().isCatalogAdditionalSchema();
    }

    // -----------------------------------------------------
    //                                         Table Comment
    //                                         -------------
    public String getPlainComment() { // may contain its alias name
        return _plainComment;
    }

    public boolean hasComment() { // means resolved comment (not plain)
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
    //                                               Display
    //                                               -------
    public String getBasicInfoDispString() {
        final String type = getType();
        return getAliasExpression() + getName() + (type != null ? " as " + type : "");
    }

    public String getTitleForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        sb.append("type=").append(_type);
        if (isAdditionalSchema()) {
            sb.append(", schema=").append(getDocumentSchema());
        }
        sb.append(", primaryKey={").append(getPrimaryKeyNameCommaString()).append("}");
        sb.append(", nameLength=").append(getName().length());
        sb.append(", columnCount=").append(getColumns().length);
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return " title=\"" + prop.resolveAttributeForSchemaHtml(sb.toString()) + "\"";
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
        _columnList.add(col);
        _columnMap.put(col.getName(), col);
        final String synonym = col.getSynonym();
        if (synonym != null) {
            _columnMap.put(synonym, col); // to find by synonym name
        }
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
        return _columnList.toArray(new Column[] {});
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
        if (columnNameList.isEmpty()) {
            return false;
        }
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

    public boolean hasDefaultValueExceptAutoIncrement() {
        for (Column column : getColumnList()) {
            if (column.hasDefaultValueExceptAutoIncrement()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasColumnComment() { // means resolved comment (not plain)
        for (Column column : getColumnList()) {
            if (column.hasComment()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    public String getPrimaryKeyConstraintName() {
        final List<Column> columnList = getColumnList();
        for (Column column : columnList) {
            if (column.isPrimaryKey()) {
                return column.getPrimaryKeyName();
            }
        }
        return null;
    }

    /**
     * Returns the collection of Columns which make up the single primary
     * key for this table.
     * @return A list of the primary key parts.
     */
    public List<Column> getPrimaryKey() {
        final List<Column> pk = new ArrayList<Column>(_columnList.size());
        for (Column column : _columnList) {
            if (column.isPrimaryKey()) {
                pk.add(column);
            }
        }
        return pk;
    }

    public Column getPrimaryKeyAsOne() {
        if (getPrimaryKey().size() != 1) {
            String msg = "This method is for only-one primary-key:";
            msg = msg + " getPrimaryKey().size()=" + getPrimaryKey().size();
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
        return DfColumnListToStringUtil.getColumnArgsString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsJavaDocString. [AtMarkparam rcvlcqNo The one of primary key. (NotNull)...]
     * @return The value of primaryKeyArgsJavaDocString. (NotNull)
     */
    public String getPrimaryKeyArgsJavaDocString() {
        final String ln = getBasicProperties().getSourceCodeLineSeparator();
        return DfColumnListToStringUtil.getColumnArgsJavaDocString(getPrimaryKey(), ln);
    }

    /**
     * Returns primaryKeyArgsAssertString. [assertObjectNotNull("rcvlcqNo", rcvlcqNo); assert...;]
     * @return The value of primaryKeyArgsAssertString. (NotNull)
     */
    public String getPrimaryKeyArgsAssertString() {
        return DfColumnListToStringUtil.getColumnArgsAssertString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsAssertStringCSharp. [AssertObjectNotNull("rcvlcqNo", rcvlcqNo); assert...;]
     * @return The value of primaryKeyArgsAssertStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsAssertStringCSharp() {
        return DfColumnListToStringUtil.getColumnArgsAssertStringCSharp(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupString. [setRcvlcqNo(rcvlcqNo);setSprlptTp(sprlptTp);]
     * @return The value of primaryKeyArgsSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsSetupString() {
        return DfColumnListToStringUtil.getColumnArgsSetupString(null, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupString. [beanName.setRcvlcqNo(rcvlcqNo);beanName.setSprlptTp(sprlptTp);]
     * @param beanName The name of bean. (NullAllowed)
     * @return The value of primaryKeyArgsSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsSetupString(String beanName) {
        return DfColumnListToStringUtil.getColumnArgsSetupString(beanName, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupStringCSharp. [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     * @return The value of primaryKeyArgsSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsSetupStringCSharp() {
        return DfColumnListToStringUtil.getColumnArgsSetupStringCSharp(null, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsSetupStringCSharp. [beanName.RcvlcqNo = rcvlcqNo;beanName.SprlptTp = sprlptTp;]
     * @param beanName The name of bean. (NullAllowed)
     * @return The value of primaryKeyArgsSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsSetupStringCSharp(String beanName) {
        return DfColumnListToStringUtil.getColumnArgsSetupStringCSharp(beanName, getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsConditionSetupString. [cb.query().setRcvlcqNo_Equal(rcvlcqNo);cb.query()...;]
     * @return The value of primaryKeyArgsConditionSetupString. (NotNull)
     */
    public String getPrimaryKeyArgsConditionSetupString() {
        return DfColumnListToStringUtil.getColumnArgsConditionSetupString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyArgsConditionSetupStringCSharp. [cb.Query().SetRcvlcqNo_Equal(rcvlcqNo);cb.Query()...;]
     * @return The value of primaryKeyArgsConditionSetupStringCSharp. (NotNull)
     */
    public String getPrimaryKeyArgsConditionSetupStringCSharp() {
        return DfColumnListToStringUtil.getColumnArgsConditionSetupStringCSharp(getPrimaryKey());
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
        return DfColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "asc");
    }

    /**
     * Returns primaryKeyOrderByDescString. [RCVLCQ_NO asc, SPRLPT_TP asc]
     * @return Generated string.
     */
    public String getPrimaryKeyOrderByDescString() {
        return DfColumnListToStringUtil.getColumnOrderByString(getPrimaryKey(), "desc");
    }

    // -----------------------------------------------------
    //                                        Display String
    //                                        --------------
    /**
     * Returns primaryKeyDispValueString. [value-value-value...]
     * @return Generated string.
     */
    public String getPrimaryKeyDispValueString() {
        return DfColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "get");
    }

    /**
     * Returns primaryKeyDispValueString. [value-value-value...]
     * @return Generated string.
     */
    public String getPrimaryKeyDispValueStringByGetterInitCap() {
        return DfColumnListToStringUtil.getColumnDispValueString(getPrimaryKey(), "Get");
    }

    // -----------------------------------------------------
    //                                    Basic Comma String
    //                                    ------------------
    /**
     * Returns primaryKeyNameCommaString. [RCVLCQ_NO, SPRLPT_TP]
     * @return Generated string.
     */
    public String getPrimaryKeyNameCommaString() {
        return DfColumnListToStringUtil.getColumnNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyUncapitalisedJavaNameCommaString. [rcvlcqNo, sprlptTp]
     * @return Generated string.
     */
    public String getPrimaryKeyUncapitalisedJavaNameCommaString() {
        return DfColumnListToStringUtil.getColumnUncapitalisedJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyJavaNameCommaString. [RcvlcqNo, SprlptTp]
     * @return Generated string.
     */
    public String getPrimaryKeyJavaNameCommaString() {
        return DfColumnListToStringUtil.getColumnJavaNameCommaString(getPrimaryKey());
    }

    /**
     * Returns primaryKeyGetterCommaString. [getRcvlcqNo(), getSprlptTp()]
     * @return Generated string.
     */
    public String getPrimaryKeyGetterCommaString() {
        return DfColumnListToStringUtil.getColumnGetterCommaString(getPrimaryKey());
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
     * Determine whether this table has a single primary key.
     * @return Determination.
     */
    public boolean hasSinglePrimaryKey() {
        return (getPrimaryKey().size() == 1);
    }

    /**
     * Determine whether this table has a compound primary key.
     * @return Determination.
     */
    public boolean hasCompoundPrimaryKey() {
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
    //                                                                         Foreign Key
    //                                                                         ===========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Returns a List containing all the FKs in the table
     * @return Foreign-key list.
     */
    public List<ForeignKey> getForeignKeyList() {
        return new ArrayList<ForeignKey>(_foreignKeyMap.values());
    }

    /**
     * Returns an Array containing all the FKs in the table
     * @return Foreign-key array.
     */
    public ForeignKey[] getForeignKeys() {
        return _foreignKeyMap.values().toArray(new ForeignKey[_foreignKeyMap.size()]);
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
        for (ForeignKey fk : _foreignKeyMap.values()) {
            final List<String> localColumns = fk.getLocalColumnNameList();
            if (Srl.containsElementIgnoreCase(localColumns, columnName)) {
                if (firstFK == null) {
                    firstFK = fk;
                }
            }
        }
        return firstFK;
    }

    public List<ForeignKey> getForeignKeyList(String columnName) {
        final List<ForeignKey> fkList = new ArrayList<ForeignKey>();
        for (ForeignKey fk : _foreignKeyMap.values()) {
            final List<String> localColumns = fk.getLocalColumnNameList();
            if (Srl.containsElementIgnoreCase(localColumns, columnName)) {
                fkList.add(fk);
            }
        }
        return fkList;
    }

    public ForeignKey getSelfReferenceForeignKey() { // returns first found
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        for (ForeignKey fk : foreignKeyList) {
            if (fk.isSelfReference()) {
                return fk;
            }
        }
        return null;
    }

    /**
     * A utility function to create a new foreign key
     * from attrib and add it to this table.
     * @param attrib the xml attributes
     * @return the created ForeignKey. (NotNull)
     */
    public ForeignKey addForeignKey(Attributes attrib) {
        final ForeignKey fk = new ForeignKey();
        fk.loadFromXML(attrib);
        addForeignKey(fk);
        return fk;
    }

    /**
     * Adds a new FK to the FK list and set the
     * parent table of the column to the current table
     * @param fk A foreign key
     */
    public void addForeignKey(ForeignKey fk) {
        fk.setTable(this);
        _foreignKeyMap.put(fk.getName(), fk);
    }

    /**
     * Remove the foreign key, for example, foreign table is excepted.
     * @param fk The removed foreign key. (NotNull)
     */
    public void removeForeignKey(ForeignKey fk) {
        _foreignKeyMap.remove(fk.getName());
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
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        for (int i = 0; i < foreignKeyList.size(); i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String name = fk.getForeignTableName();
            if (tableSet.contains(name)) {
                continue;
            }
            tableSet.add(name);
            sb.append(", ").append(name).append(fk.hasFixedSuffix() ? "(" + fk.getFixedSuffix() + ")" : "");
        }
        for (ForeignKey referrer : _referrerList) {
            if (!referrer.isOneToOne()) {
                continue;
            }
            final String name = referrer.getTable().getName();
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
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
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

        final List<ForeignKey> ls = getForeignKeyList();
        final int size = ls.size();
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = ls.get(i);
            sb.append(", ").append(fk.getForeignPropertyName());
        }
        for (ForeignKey referrer : _referrerList) {
            if (referrer.isOneToOne()) {
                sb.append(", ").append(referrer.getReferrerPropertyNameAsOne());
            }
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                         Determination
    //                                         -------------
    public boolean existsForeignKey(String foreignTableName, List<String> localColumnNameList,
            List<String> foreignColumnNameList, String fixedSuffix) {
        final StringSet localColumnNameSet = StringSet.createAsFlexibleOrdered();
        localColumnNameSet.addAll(localColumnNameList);
        final StringSet foreignColumnNameSet = StringSet.createAsFlexibleOrdered();
        foreignColumnNameSet.addAll(foreignColumnNameList);

        final ForeignKey[] fks = getForeignKeys();
        for (final ForeignKey key : fks) {
            if (!Srl.equalsFlexible(foreignTableName, key.getForeignTableName())) {
                continue;
            }
            if (!Srl.equalsFlexible(fixedSuffix, key.getFixedSuffix())) {
                continue;
            }
            final StringSet currentLocalColumnNameSet = StringSet.createAsFlexibleOrdered();
            currentLocalColumnNameSet.addAll(key.getLocalColumnNameList());
            if (!localColumnNameSet.equalsUnderCharOption(currentLocalColumnNameSet)) {
                continue;
            }
            final StringSet currentForeignColumnNameSet = StringSet.createAsFlexibleOrdered();
            currentForeignColumnNameSet.addAll(key.getForeignColumnNameList());
            if (!foreignColumnNameSet.equalsUnderCharOption(currentForeignColumnNameSet)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public boolean hasForeignKey() {
        return (getForeignKeys().length != 0);
    }

    /**
     * Has relation? (hasForeignKey() or hasReferrer())
     * @return Determination.
     */
    public boolean hasRelation() {
        return (hasForeignKey() || hasReferrer());
    }

    public boolean hasSelfReference() {
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        for (ForeignKey fk : foreignKeyList) {
            if (fk.isSelfReference()) {
                return true;
            }
        }
        return false;
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

    public boolean hasForeignKeyOrReferrer() {
        return hasForeignKey() || hasReferrer();
    }

    public boolean hasForeignKeyOrReferrerAsOne() {
        return hasForeignKey() || hasReferrerAsOne();
    }

    public boolean hasCompoundKeyReferrer() {
        return doHasCompoundKeyReferrer(getPrimaryKey()) || doHasCompoundKeyReferrer(getUniqueColumnList());
    }

    protected boolean doHasCompoundKeyReferrer(List<Column> columnList) {
        for (Column col : columnList) {
            for (ForeignKey referrer : col.getReferrers()) {
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
        _referrerList.add(fk);
        return true;
    }

    public List<ForeignKey> getReferrerList() {
        return _referrerList;
    }

    public List<ForeignKey> getReferrerAsManyList() {
        return getReferrerAsWhatList(false);
    }

    public List<ForeignKey> getReferrerAsOneList() {
        return getReferrerAsWhatList(true);
    }

    protected List<ForeignKey> getReferrerAsWhatList(boolean oneToOne) {
        final List<ForeignKey> referrerList = getReferrerList();
        if (referrerList == null || referrerList.isEmpty()) {
            return referrerList;
        }
        List<ForeignKey> referrerListAsWhat = DfCollectionUtil.newArrayList();
        for (ForeignKey key : referrerList) {
            if (oneToOne) {
                if (key.isOneToOne()) {
                    referrerListAsWhat.add(key);
                }
            } else {
                if (!key.isOneToOne()) {
                    referrerListAsWhat.add(key);
                }
            }
        }
        return referrerListAsWhat;
    }

    public List<ForeignKey> getRefererList() { // for compatibility (spell miss)
        return getReferrerList();
    }

    public List<ForeignKey> getReferrers() { // for compatibility (old style)
        return getReferrerList();
    }

    public boolean hasReferrer() {
        return (getReferrerList() != null && !getReferrerList().isEmpty());
    }

    public boolean hasReferrerAsMany() {
        final List<ForeignKey> manyList = getReferrerAsManyList();
        return manyList != null && !manyList.isEmpty();
    }

    protected boolean hasReferrerAsOne() {
        final List<ForeignKey> oneList = getReferrerAsOneList();
        return oneList != null && !oneList.isEmpty();
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

    public List<Unique> getOnlyOneColumnUniqueList() {
        final List<Unique> uniqueList = getUniqueList();
        final List<Unique> resultList = DfCollectionUtil.newArrayList();
        for (Unique unique : uniqueList) {
            if (unique.isOnlyOneColumn()) {
                resultList.add(unique);
            }
        }
        return resultList;
    }

    public List<Unique> getTwoOrMoreColumnUniqueList() {
        final List<Unique> uniqueList = getUniqueList();
        final List<Unique> resultList = DfCollectionUtil.newArrayList();
        for (Unique unique : uniqueList) {
            if (unique.isTwoOrMoreColumn()) {
                resultList.add(unique);
            }
        }
        return resultList;
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

    public List<Column> getUniqueColumnList() {
        final StringKeyMap<Column> keyMap = StringKeyMap.createAsCaseInsensitiveOrdered();
        for (Column column : _columnList) {
            keyMap.put(column.getName(), column);
        }
        return new ArrayList<Column>(keyMap.values());
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

    public List<Index> getOnlyOneColumnIndexList() {
        final List<Index> indexList = getIndexList();
        final List<Index> resultList = DfCollectionUtil.newArrayList();
        for (Index index : indexList) {
            if (index.isOnlyOneColumn()) {
                resultList.add(index);
            }
        }
        return resultList;

    }

    public List<Index> getTwoOrMoreColumnIndexList() {
        final List<Index> indexList = getIndexList();
        final List<Index> resultList = DfCollectionUtil.newArrayList();
        for (Index index : indexList) {
            if (index.isTwoOrMoreColumn()) {
                resultList.add(index);
            }
        }
        return resultList;
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

    // ===================================================================================
    //                                                                     Java Definition
    //                                                                     ===============
    // -----------------------------------------------------
    //                                             Java Name
    //                                             ---------
    protected boolean _needsJavaNameConvert = true;

    public void suppressJavaNameConvert() {
        _needsJavaNameConvert = false;
    }

    public boolean needsJavaNameConvert() {
        return _needsJavaNameConvert;
    }

    /**
     * Get name to use in Java sources
     */
    public String getJavaName() {
        if (_javaName != null) {
            return _javaName;
        }
        if (needsJavaNameConvert()) {
            _javaName = getDatabase().convertJavaNameByJdbcNameAsTable(getName());
        } else {
            _javaName = getName(); // for sql2entity mainly
        }
        _javaName = filterBuriJavaNameIfNeeds(_javaName);
        _javaName = filterJavaNameNonCompilableConnector(_javaName);
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

    protected String filterJavaNameNonCompilableConnector(String javaName) {
        final DfLittleAdjustmentProperties prop = getProperties().getLittleAdjustmentProperties();
        return prop.filterJavaNameNonCompilableConnector(javaName, new NonCompilableChecker() {
            public String name() {
                return getName();
            }

            public String disp() {
                return getBasicInfoDispString();
            }
        });
    }

    /**
     * Set name to use in Java sources
     */
    public void setJavaName(String javaName) {
        this._javaName = javaName;
    }

    // -----------------------------------------------------
    //                               Uncapitalized Java Name
    //                               -----------------------
    /**
     * Get variable name to use in Java sources (= uncapitalized java name)
     */
    public String getUncapitalisedJavaName() { // allowed spell miss
        return Srl.initUncap(getJavaName());
    }

    // -----------------------------------------------------
    //                         Java Beans Rule Property Name
    //                         -----------------------------
    /**
     * Get property name to use in Java sources (according to java beans rule)
     */
    public String getJavaBeansRulePropertyName() {
        return Srl.initBeansProp(getJavaName());
    }

    // -----------------------------------------------------
    //                                       Base Class Name
    //                                       ---------------
    public String getBaseEntityClassName() {
        final String projectPrefix = getDatabase().getProjectPrefix();
        final String basePrefix = getDatabase().getBasePrefix();
        final String baseSuffixForEntity = getDatabase().getBaseSuffixForEntity();
        return projectPrefix + basePrefix + getSchemaClassPrefix() + getJavaName() + baseSuffixForEntity;
    }

    public String getBaseDaoClassName() {
        return getBaseEntityClassName() + "Dao";
    }

    public String getBaseBehaviorClassName() {
        return getBaseEntityClassName() + "Bhv";
    }

    public String getBaseBehaviorApClassName() {
        final String suffix = getBasicProperties().getApplicationBehaviorAdditionalSuffix();
        return getBaseBehaviorClassName() + suffix;
    }

    public String getBaseConditionBeanClassName() {
        return getBaseEntityClassName() + "CB";
    }

    public String getAbstractBaseConditionQueryClassName() {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final String basePrefix = getBasicProperties().getBasePrefix();
        return projectPrefix + "Abstract" + basePrefix + getSchemaClassPrefix() + getJavaName() + "CQ";
    }

    public String getBaseConditionQueryClassName() {
        return getBaseEntityClassName() + "CQ";
    }

    // -----------------------------------------------------
    //                                   Extended Class Name
    //                                   -------------------
    public String getExtendedEntityClassName() {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        return buildExtendedEntityClassName(projectPrefix);
    }

    protected String buildExtendedEntityClassName(String projectPrefix) {
        return projectPrefix + getSchemaClassPrefix() + getJavaName();
    }

    public String getRelationTraceClassName() {
        return getSchemaClassPrefix() + getJavaName();
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
        final String extendedDaoPackage = getBasicProperties().getExtendedDaoPackage();
        return extendedDaoPackage + "." + getExtendedDaoClassName();
    }

    public String getExtendedBehaviorClassName() {
        return getExtendedEntityClassName() + "Bhv";
    }

    public String getExtendedBehaviorApClassName() {
        final String suffix = getBasicProperties().getApplicationBehaviorAdditionalSuffix();
        return getExtendedBehaviorClassName() + suffix;
    }

    public String getExtendedBehaviorLibClassName() {
        final String projectPrefix = getBasicProperties().getLibraryProjectPrefix();
        return buildExtendedEntityClassName(projectPrefix) + "Bhv";
    }

    public String getExtendedBehaviorFullClassName() {
        final String extendedBehaviorPackage = getBasicProperties().getExtendedBehaviorPackage();
        return extendedBehaviorPackage + "." + getExtendedBehaviorClassName();
    }

    public String getExtendedBehaviorApFullClassName() {
        final String extendedBehaviorPackage = getBasicProperties().getExtendedBehaviorPackage();
        return extendedBehaviorPackage + "." + getExtendedBehaviorApClassName();
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

    protected String getSchemaClassPrefix() {
        // *however same-name tables between different schemas are unsupported at 0.9.6.8
        if (hasSchema() && isExistSameNameTable()) {
            // schema of DB2 may have space either size
            final String prefix;
            if (isCatalogAdditionalSchema()) {
                String pureCatalog = getPureCatalog();
                pureCatalog = pureCatalog != null ? Srl.initCapTrimmed(pureCatalog.trim().toLowerCase()) : "";
                String pureSchema = getPureSchema();
                pureSchema = pureSchema != null ? Srl.initCapTrimmed(pureSchema.trim().toLowerCase()) : "";
                prefix = pureCatalog + pureSchema;
            } else {
                String pureSchema = getPureSchema();
                pureSchema = pureSchema != null ? Srl.initCapTrimmed(pureSchema.trim().toLowerCase()) : "";
                prefix = pureSchema;
            }
            return prefix;
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
            if (_name.equalsIgnoreCase(name)) {
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
        final String uncapName = getUncapitalisedJavaName();
        final String componentName = getDatabase().filterComponentNameWithProjectPrefix(uncapName) + "Bhv";

        // remove "$" because a component name that has a dollar mark may be unsupported
        // for example, in Spring Framework case:
        //   -> SAXParseException: Attribute value FOO$BAR of type ID must be a name.
        return Srl.replace(componentName, "$", "");
    }

    public String getBehaviorApComponentName() {
        final String suffix = getBasicProperties().getApplicationBehaviorAdditionalSuffix();
        return getBehaviorComponentName() + suffix;
    }

    // ===================================================================================
    //                                                               Sql2Entity Definition
    //                                                               =====================
    public boolean isSql2EntityCustomize() {
        return _sql2EntityCustomize;
    }

    public void setSql2EntityCustomize(boolean sql2EntityCustomize) {
        _sql2EntityCustomize = sql2EntityCustomize;
    }

    public boolean isSql2EntityCustomizeHasNested() {
        return _sql2EntityCustomizeHasNested;
    }

    public void setSql2EntityCustomizeHasNested(boolean sql2EntityCustomizeHasNested) {
        _sql2EntityCustomizeHasNested = sql2EntityCustomizeHasNested;
    }

    public boolean isSql2EntityTypeSafeCursor() {
        return _sql2EntityTypeSafeCursor;
    }

    public void setSql2EntityTypeSafeCursor(boolean sql2EntityTypeSafeCursor) {
        this._sql2EntityTypeSafeCursor = sql2EntityTypeSafeCursor;
    }

    public boolean isLoadableCustomizeEntity() {
        final Table domain = getLoadableCustomizeDomain();
        return domain != null && domain.hasReferrerAsMany();
    }

    public Table getLoadableCustomizeDomain() {
        if (!isSql2EntityCustomize() || !hasPrimaryKey()) {
            return null;
        }
        final List<Column> primaryKeyList = getPrimaryKey();
        for (Column pk : primaryKeyList) {
            // check whether the related column is also primary key if it exists
            final Column relatedColumn = pk.getSql2EntityRelatedColumn();
            if (relatedColumn != null && !relatedColumn.isPrimaryKey()) {
                return null;
            }
        }
        return primaryKeyList.get(0).getSql2EntityRelatedTable();
    }

    public List<String> getLoadableCustomizePrimaryKeySettingExpressionList() {
        final Table domain = getLoadableCustomizeDomain();
        if (domain == null) {
            return DfCollectionUtil.emptyList();
        }
        final List<Column> primaryKeyList = getPrimaryKey();
        final List<String> settingList = DfCollectionUtil.newArrayList();
        final boolean hasRelatedColumn; // true if all PKs have related columns
        {
            boolean notFoundExists = false;
            for (Column pk : primaryKeyList) {
                if (!pk.hasSql2EntityRelatedColumn()) {
                    notFoundExists = true; // for example, PostgreSQL
                    break;
                }
            }
            hasRelatedColumn = !notFoundExists;
        }
        int index = 0;
        for (Column pk : primaryKeyList) {
            final Column relatedColumn;
            if (hasRelatedColumn) {
                relatedColumn = pk.getSql2EntityRelatedColumn();
            } else {
                // if there are not related columns, it uses a key order
                relatedColumn = domain.getPrimaryKey().get(index);
            }
            if (getBasicProperties().isTargetLanguageJava()) {
                final String fromPropName = pk.getJavaBeansRulePropertyNameInitCap();
                final String toPropName = relatedColumn.getJavaBeansRulePropertyNameInitCap();
                settingList.add("set" + toPropName + "(get" + fromPropName + "())");
            } else if (getBasicProperties().isTargetLanguageCSharp()) {
                settingList.add(relatedColumn.getJavaName() + " = this." + pk.getJavaName());
            } else {
                String msg = "Unsupported language for this method: " + getBasicProperties().getTargetLanguage();
                throw new UnsupportedOperationException(msg);
            }
            ++index;
        }
        return settingList;
    }

    /**
     * @return The output directory for Sql2Entity. (NotNull)
     */
    public String getSql2EntityOutputDirectory() {
        if (_sql2EntityOutputDirectory != null) {
            return _sql2EntityOutputDirectory;
        } else {
            return getProperties().getOutsideSqlProperties().getSql2EntityOutputDirectory();
        }
    }

    public void setSql2EntityOutputDirectory(String sql2EntityOutputDirectory) {
        this._sql2EntityOutputDirectory = sql2EntityOutputDirectory;
    }

    public void switchSql2EntityOutputDirectory() {
        getProperties().getOutsideSqlProperties().switchSql2EntityOutputDirectory(_sql2EntityOutputDirectory);
    }

    // ===================================================================================
    //                                                                             Utility
    //                                                                             =======
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

    protected DfCommonColumnProperties getCommonColumnProperties() {
        return getProperties().getCommonColumnProperties();
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

    public boolean hasTableClassification() {
        final Column[] columns = getColumns();
        for (Column column : columns) {
            if (column.isTableClassification()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasImplicitClassification() {
        final Column[] columns = getColumns();
        for (Column column : columns) {
            if (!column.isTableClassification()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPrimaryKeyForcedClassificationSetting() {
        final List<Column> columns = getPrimaryKey();
        for (Column column : columns) {
            if (column.isForceClassificationSetting()) {
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
     * Get the value of sequence name defined at definition map.
     * @return The string as name. (NotNull: If a sequence is not found, return empty string.)
     */
    public String getDefinedSequenceName() {
        if (!isUseSequence()) {
            return "";
        }
        final String sequenceName = getSequenceIdentityProperties().getSequenceName(getName());
        if (Srl.is_Null_or_TrimmedEmpty(sequenceName)) {
            final String serialSequenceName = extractPostgreSQLSerialSequenceName();
            if (Srl.is_NotNull_and_NotTrimmedEmpty(serialSequenceName)) {
                return serialSequenceName;
            }
            return ""; // if it uses sequence, unreachable
        }
        return sequenceName;
    }

    /**
     * Get the value of sequence name for SQL.
     * @return The string as name. (NotNull: If a sequence is not found, return empty string.)
     */
    public String getSequenceSqlName() {
        if (!isUseSequence()) {
            return "";
        }
        final String sequenceName = getSequenceIdentityProperties().getSequenceName(getName());
        if (Srl.is_Null_or_TrimmedEmpty(sequenceName)) {
            final String serialSequenceName = extractPostgreSQLSerialSequenceName();
            if (Srl.is_Null_or_TrimmedEmpty(serialSequenceName)) {
                String msg = "The sequence for serial type should exist when isUseSequence() is true!";
                throw new IllegalStateException(msg);
            }
            // the schema prefix of sequence for serial type has already been resolved here
            // (the name in default value has schema prefix)
            return serialSequenceName;
        }
        return sequenceName;
    }

    /**
     * Get the SQL for next value of sequence.
     * @return The SQL for next value of sequence. (NotNull: If a sequence is not found, return empty string.)
     */
    public String getSequenceNextValSql() { // basically for C#
        if (!isUseSequence()) {
            return "";
        }
        final DBDef dbdef = getBasicProperties().getCurrentDBDef();
        final String sequenceName = getSequenceSqlName();
        final String sql = dbdef.dbway().buildSequenceNextValSql(sequenceName);
        return sql != null ? sql : "";
    }

    public BigDecimal getSequenceMinimumValue() {
        if (!isUseSequence()) {
            return null;
        }
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        final DataSource ds = getDatabase().getDataSource();
        BigDecimal value = prop.getSequenceMinimumValueByTableName(ds, getUnifiedSchema(), getName());
        if (value == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                value = prop.getSequenceMinimumValueBySequenceName(ds, getUnifiedSchema(), sequenceName);
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
        BigDecimal value = prop.getSequenceMaximumValueByTableName(ds, getUnifiedSchema(), getName());
        if (value == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                value = prop.getSequenceMaximumValueBySequenceName(ds, getUnifiedSchema(), sequenceName);
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
        Integer size = prop.getSequenceIncrementSizeByTableName(ds, getUnifiedSchema(), getName());
        if (size == null) {
            final String sequenceName = extractPostgreSQLSerialSequenceName();
            if (sequenceName != null && sequenceName.trim().length() > 0) {
                size = prop.getSequenceIncrementSizeBySequenceName(ds, getUnifiedSchema(), sequenceName);
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
        return prop.getSequenceCacheSize(ds, getUnifiedSchema(), getName());
    }

    public String getSequenceCacheSizeExpression() {
        final Integer value = getSequenceCacheSize();
        return value != null ? value.toString() : "null";
    }

    public String getSequenceReturnType() {
        final DfSequenceIdentityProperties sequenceIdentityProperties = getProperties().getSequenceIdentityProperties();
        final String sequenceReturnType = sequenceIdentityProperties.getSequenceReturnType();
        if (hasCompoundPrimaryKey()) {
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
     * @return Sequence name of postgreSQL serial type column. (NullAllowed: If null, not found)
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

    protected List<Column> _subColumnSequenceColumnList;

    public boolean isUseSubColumnSequence() {
        return !getSubColumnSequenceColumnList().isEmpty();
    }

    public List<Column> getSubColumnSequenceColumnList() {
        if (_subColumnSequenceColumnList != null) {
            return _subColumnSequenceColumnList;
        }
        _subColumnSequenceColumnList = DfCollectionUtil.newArrayList();
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        if (!prop.hasSubColumnSequence()) {
            return _subColumnSequenceColumnList;
        }
        final List<Column> columnList = getColumnList();
        for (Column column : columnList) {
            final String sequenceName = prop.getSubColumnSequenceName(getName(), column.getName());
            if (sequenceName != null) {
                _subColumnSequenceColumnList.add(column);
            }
        }
        return _subColumnSequenceColumnList;
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
        return Srl.initUncap(getUpdateDateJavaName());
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
        return Srl.initUncap(versionNoJavaName);
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
            final DfDatabaseProperties prop = getDatabaseProperties();
            final DfAdditionalSchemaInfo schemaInfo = prop.getAdditionalSchemaInfo(_unifiedSchema);
            if (schemaInfo.isSuppressCommonColumn()) {
                return false;
            }
        }
        final List<String> commonColumnNameList = getCommonColumnProperties().getCommonColumnNameList();
        if (commonColumnNameList.isEmpty()) {
            return false;
        }
        for (String commonColumnName : commonColumnNameList) {
            if (getCommonColumnProperties().isCommonColumnConversion(commonColumnName)) {
                final Column col = findMyCommonColumn(commonColumnName);
                if (col == null) {
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
        final List<String> commonColumnNameList = getCommonColumnProperties().getCommonColumnNameList();
        for (String commonColumnName : commonColumnNameList) {
            ls.add(findMyCommonColumn(commonColumnName));
        }
        return ls;
    }

    public String getCommonColumnListSetupExpression() {
        return buildCommonColumnListSetupExpression(getCommonColumnList());
    }

    public List<Column> getCommonColumnBeforeInsertList() {
        final List<Column> ls = new ArrayList<Column>();
        if (!hasAllCommonColumn()) {
            return ls;
        }
        final List<String> commonColumnNameList = getCommonColumnProperties().getCommonColumnNameList();
        for (String commonColumnName : commonColumnNameList) {
            if (getCommonColumnProperties().hasCommonColumnBeforeInsertLogic(commonColumnName)) {
                ls.add(findMyCommonColumn(commonColumnName));
            }
        }
        return ls;
    }

    public String getCommonColumnBeforeInsertListSetupExpression() {
        return buildCommonColumnListSetupExpression(getCommonColumnBeforeInsertList());
    }

    public List<Column> getCommonColumnBeforeUpdateList() {
        final List<Column> ls = new ArrayList<Column>();
        if (!hasAllCommonColumn()) {
            return ls;
        }
        final List<String> commonColumnNameList = getCommonColumnProperties().getCommonColumnNameList();
        for (String commonColumnName : commonColumnNameList) {
            if (getCommonColumnProperties().hasCommonColumnBeforeUpdateLogic(commonColumnName)) {
                ls.add(findMyCommonColumn(commonColumnName));
            }
        }
        return ls;
    }

    public String getCommonColumnBeforeUpdateListSetupExpression() {
        return buildCommonColumnListSetupExpression(getCommonColumnBeforeUpdateList());
    }

    protected String buildCommonColumnListSetupExpression(List<Column> commonColumnList) {
        final String prefix;
        final String suffix;
        if (getBasicProperties().isTargetLanguageCSharp()) {
            prefix = "Column";
            suffix = "";
        } else {
            prefix = "column";
            suffix = "()";
        }
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Column column : commonColumnList) {
            if (index > 0) {
                sb.append(", ");
            }
            sb.append(prefix + column.getJavaName() + suffix);
            ++index;
        }
        return sb.toString();
    }

    public String findTargetColumnJavaNameByCommonColumnName(String commonColumnName) { // called by templates
        final Column column = findMyCommonColumn(commonColumnName);
        return column != null ? column.getJavaName() : null;
    }

    public String findTargetColumnNameByCommonColumnName(String commonColumnName) { // called by templates
        final Column column = findMyCommonColumn(commonColumnName);
        return column != null ? column.getName() : null;
    }

    protected Column findMyCommonColumn(String commonColumnName) {
        final Column column;
        if (getCommonColumnProperties().isCommonColumnConversion(commonColumnName)) {
            column = getColumn(convertCommonColumnName(commonColumnName));
        } else {
            column = getColumn(commonColumnName);
        }
        return column;
    }

    protected Column getCommonColumnNormal(String commonColumnName) {
        return getColumn(commonColumnName);
    }

    protected String convertCommonColumnName(String commonColumnName) {
        String filteredCommonColumn = getCommonColumnProperties().filterCommonColumn(commonColumnName);
        filteredCommonColumn = Srl.replace(filteredCommonColumn, "TABLE_NAME", getName());
        filteredCommonColumn = Srl.replace(filteredCommonColumn, "table_name", getName());
        filteredCommonColumn = Srl.replace(filteredCommonColumn, "TableName", getJavaName());
        filteredCommonColumn = Srl.replace(filteredCommonColumn, "tablename", getJavaName());
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

    protected boolean isAvailableAddingCatalogToTableSqlName() {
        return getProperties().getLittleAdjustmentProperties().isAvailableAddingCatalogToTableSqlName();
    }

    // ===================================================================================
    //                                                                        Empty String
    //                                                                        ============
    public boolean hasEntityConvertEmptyStringToNull() {
        final List<Column> columnList = getColumnList();
        for (Column column : columnList) {
            if (column.isEntityConvertEmptyStringToNull()) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                          Simple DTO
    //                                                                          ==========
    public String getBaseSimpleDtoClassName() {
        final DfSimpleDtoProperties prop = getProperties().getSimpleDtoProperties();
        final String prefix = prop.getBaseDtoPrefix();
        final String suffix = prop.getBaseDtoSuffix();
        return prefix + getJavaName() + suffix;
    }

    public String getExtendedSimpleDtoClassName() {
        final DfSimpleDtoProperties prop = getProperties().getSimpleDtoProperties();
        final String prefix = prop.getExtendedDtoPrefix();
        final String suffix = prop.getExtendedDtoSuffix();
        return prefix + getJavaName() + suffix;
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
        if (!hasSinglePrimaryKey()) {
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
        final Map<String, Map<String, String>> bqpMap = getBehaviorQueryPathMap();
        return new ArrayList<String>(bqpMap.keySet());
    }

    protected Map<String, String> getBehaviorQueryPathElementMap(String behaviorQueryPath) {
        final Map<String, Map<String, String>> bqpMap = getBehaviorQueryPathMap();
        return bqpMap.get(behaviorQueryPath);
    }

    public String getBehaviorQueryPathDisplayName(String behaviorQueryPath) {
        final String subDirectoryPath = getBehaviorQueryPathSubDirectoryPath(behaviorQueryPath);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(subDirectoryPath)) {
            final String connector = "_";
            return Srl.replace(subDirectoryPath, "/", connector) + connector + behaviorQueryPath;
        } else {
            return behaviorQueryPath;
        }
    }

    public String getBehaviorQueryPathFileName(String behaviorQueryPath) {
        final String path = getBehaviorQueryPathPath(behaviorQueryPath);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(path)) {
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
        return Srl.is_NotNull_and_NotTrimmedEmpty(subDirectoryPath) ? subDirectoryPath : "";
    }

    public String getBehaviorQueryPathPath(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String path = elementMap.get(DfBehaviorQueryPathSetupper.KEY_PATH);
        return Srl.is_NotNull_and_NotTrimmedEmpty(path) ? path : "";
    }

    public boolean hasBehaviorQueryPathCustomizeEntity(String behaviorQueryPath) {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getBehaviorQueryPathCustomizeEntity(behaviorQueryPath));
    }

    public String getBehaviorQueryPathCustomizeEntity(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String customizeEntity = elementMap.get("customizeEntity");
        return Srl.is_NotNull_and_NotTrimmedEmpty(customizeEntity) ? customizeEntity : "";
    }

    public boolean hasBehaviorQueryPathParameterBean(String behaviorQueryPath) {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getBehaviorQueryPathParameterBean(behaviorQueryPath));
    }

    public String getBehaviorQueryPathParameterBean(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String parameterBean = elementMap.get("parameterBean");
        return Srl.is_NotNull_and_NotTrimmedEmpty(parameterBean) ? parameterBean : "";
    }

    public boolean hasBehaviorQueryPathCursor(String behaviorQueryPath) {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getBehaviorQueryPathCursor(behaviorQueryPath));
    }

    public String getBehaviorQueryPathCursor(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String cursor = elementMap.get("cursor");
        return Srl.is_NotNull_and_NotTrimmedEmpty(cursor) ? cursor : "";
    }

    public String getBehaviorQueryPathCursorForSchemaHtml(String behaviorQueryPath) {
        final String cursor = getBehaviorQueryPathCursor(behaviorQueryPath);
        return Srl.is_NotNull_and_NotTrimmedEmpty(cursor) ? " *" + cursor : "";
    }

    public String getBehaviorQueryPathTitle(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String title = elementMap.get("title");
        return Srl.is_NotNull_and_NotTrimmedEmpty(title) ? title : "";
    }

    public String getBehaviorQueryPathTitleForSchemaHtml(String behaviorQueryPath) {
        String title = getBehaviorQueryPathTitle(behaviorQueryPath);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(title)) {
            final DfDocumentProperties prop = getProperties().getDocumentProperties();
            title = prop.resolveTextForSchemaHtml(title);
            return "(" + title + ")";
        } else {
            return "&nbsp;";
        }
    }

    public boolean hasBehaviorQueryPathDescription(String behaviorQueryPath) {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getBehaviorQueryPathDescription(behaviorQueryPath));
    }

    public String getBehaviorQueryPathDescription(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String description = elementMap.get("description");
        return Srl.is_NotNull_and_NotTrimmedEmpty(description) ? description : "";
    }

    public String getBehaviorQueryPathDescriptionForSchemaHtml(String behaviorQueryPath) {
        String description = getBehaviorQueryPathDescription(behaviorQueryPath);
        if (Srl.is_NotNull_and_NotTrimmedEmpty(description)) {
            final DfDocumentProperties prop = getProperties().getDocumentProperties();
            description = prop.resolvePreTextForSchemaHtml(description);
            return description;
        } else {
            return "&nbsp;";
        }
    }

    public boolean isBehaviorQueryPathSqlAp(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String sqlAp = elementMap.get(DfBehaviorQueryPathSetupper.KEY_SQLAP);
        return Srl.is_NotNull_and_NotTrimmedEmpty(sqlAp) ? "true".equals(sqlAp) : false;
    }

    public String getBehaviorQueryPathSqlApProjectName(String behaviorQueryPath) {
        final Map<String, String> elementMap = getBehaviorQueryPathElementMap(behaviorQueryPath);
        final String sqlApProjectName = elementMap.get(DfBehaviorQueryPathSetupper.KEY_SQLAP_PROJECT_NAME);
        return Srl.is_NotNull_and_NotTrimmedEmpty(sqlApProjectName) ? sqlApProjectName : "";
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
        final StringBuilder result = new StringBuilder();
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
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        if (!foreignKeyList.isEmpty()) {
            for (ForeignKey fk : foreignKeyList) {
                result.append(fk);
            }
        }
        result.append("</table>\n");
        return result.toString();
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
            final List<ForeignKey> foreignKeyList = getForeignKeyList();
            for (i = 0, size = foreignKeyList.size(); i < size; i++) {
                final ForeignKey fk = (ForeignKey) foreignKeyList.get(i);
                name = fk.getName();
                if (Srl.is_Null_or_Empty(name)) {
                    name = acquireConstraintName("FK", i + 1);
                    fk.setName(name);
                }
            }

            for (i = 0, size = _indices.size(); i < size; i++) {
                Index index = (Index) _indices.get(i);
                name = index.getName();
                if (Srl.is_Null_or_Empty(name)) {
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
        final List<Object> inputs = new ArrayList<Object>(4);
        inputs.add(getDatabase());
        inputs.add(getName());
        inputs.add(nameType);
        inputs.add(new Integer(nbr));
        return NameFactory.generateName(NameFactory.CONSTRAINT_GENERATOR, inputs);
    }
}