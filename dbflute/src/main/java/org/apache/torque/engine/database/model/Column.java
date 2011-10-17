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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfClassificationDeploymentClassificationNotFoundException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.doc.schemahtml.DfSchemaHtmlBuilder;
import org.seasar.dbflute.logic.jdbc.metadata.basic.DfColumnExtractor;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties.NonCompilableChecker;
import org.seasar.dbflute.properties.DfSequenceIdentityProperties;
import org.seasar.dbflute.properties.DfTypeMappingProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.xml.sax.Attributes;

/**
 * A Class for holding data about a column used in an Application.
 * @author Modified by jflute
 */
public class Column {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final DfColumnExtractor _columnHandler = new DfColumnExtractor();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
    private Table _table;

    // -----------------------------------------------------
    //                                     Column Definition
    //                                     -----------------
    private String _name;
    private String _synonym;
    private String _dbType;
    private String _columnSize;
    private boolean _isNotNull;
    private boolean _isAutoIncrement;
    private String _defaultValue;
    private String _plainComment;

    // -----------------------------------------------------
    //                                           Primary Key
    //                                           -----------
    private boolean _isPrimaryKey;
    private String _primaryKeyName;
    private boolean _additionalPrimaryKey;

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
    private List<ForeignKey> _referrerList;

    // -----------------------------------------------------
    //                                       Java Definition
    //                                       ---------------
    private String _javaName;
    private String _jdbcType;

    // -----------------------------------------------------
    //                                 Sql2Entity Definition
    //                                 ---------------------
    private Table _sql2EntityRelatedTable;
    private Column _sql2EntityRelatedColumn;
    private String _sql2EntityForcedJavaNative;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Creates a new instance with a <code>null</code> name.
     */
    public Column() {
        this(null);
    }

    /**
     * Creates a new column and set the name
     * @param name column name
     */
    public Column(String name) {
        _name = name;
    }

    // -----------------------------------------------------
    //                                         Load from XML
    //                                         -------------
    public void loadFromXML(Attributes attrib) {
        // name
        _name = attrib.getValue("name"); // column name
        _javaName = attrib.getValue("javaName");

        // primary key
        _isPrimaryKey = ("true".equals(attrib.getValue("primaryKey")));
        _primaryKeyName = attrib.getValue("pkName");

        // data type and size
        _jdbcType = attrib.getValue("type");
        _dbType = attrib.getValue("dbType");
        _columnSize = attrib.getValue("size");

        // It is not necessary to use this value on XML
        // because it uses the JavaNative value.
        // The value javaType on XML is for various purposes.
        //_javaType = attrib.getValue("javaType");
        //if (_javaType != null && _javaType.length() == 0) {
        //    _javaType = null;
        //}

        // not null
        final String notNull = attrib.getValue("required");
        _isNotNull = (notNull != null && "true".equals(notNull));

        // auto-increment
        final String autoIncrement = attrib.getValue("autoIncrement");
        _isAutoIncrement = ("true".equals(autoIncrement));

        // others
        _defaultValue = attrib.getValue("default");
        _plainComment = attrib.getValue("comment");

        handleProgramReservationWord();
    }

    protected void handleProgramReservationWord() {
        final DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        if (prop.isPgReservColumn(_name)) {
            _synonym = prop.resolvePgReservColumn(_name);
            _plainComment = _plainComment + " (using DBFlute synonym)";
        }
    }

    public String getFullyQualifiedName() {
        return (_table.getName() + '.' + _name);
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    /**
     * Set the parent Table of the column
     */
    public void setTable(Table parent) {
        _table = parent;
    }

    /**
     * Get the parent Table of the column
     */
    public Table getTable() {
        if (_table == null) {
            String msg = "This Column did not have 'table': columnName=" + _name;
            throw new IllegalStateException(msg);
        }
        return _table;
    }

    protected Database getDatabaseChecked() {
        final Table tbl = getTable();
        if (tbl == null) {
            throw new IllegalStateException("getTable() should not be null at " + getName());
        }
        final Database db = tbl.getDatabase();
        if (db == null) {
            throw new IllegalStateException("getTable().getDatabase() should not be null at " + getName());
        }
        return db;
    }

    /**
     * Returns the Name of the table the column is in
     */
    public String getTableName() {
        return _table.getName();
    }

    // ===================================================================================
    //                                                                   Column Definition
    //                                                                   =================
    // -----------------------------------------------------
    //                                           Column Name
    //                                           -----------
    public String getName() {
        return _name;
    }

    public void setName(String newName) {
        _name = newName;
    }

    // -----------------------------------------------------
    //                                              SQL Name
    //                                              --------
    public String getColumnSqlName() {
        return quoteColumnNameIfNeeds(getResourceNameForSqlName());
    }

    public String getColumnSqlNameDirectUse() {
        return quoteColumnNameIfNeedsDirectUse(getResourceNameForSqlName());
    }

    protected String getResourceNameForSqlName() {
        return isSqlNameUpperCase() ? getName().toUpperCase() : getName();
    }

    protected boolean isSqlNameUpperCase() {
        if (getTable().isSql2EntityCustomize()) { // Sql2Entity may be on the camel case basis
            return false;
        }
        return getLittleAdjustmentProperties().isColumnSqlNameUpperCase();
    }

    protected String quoteColumnNameIfNeeds(String columnName) {
        final DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        return prop.quoteColumnNameIfNeeds(columnName);
    }

    protected String quoteColumnNameIfNeedsDirectUse(String columnName) {
        final DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        return prop.quoteColumnNameIfNeedsDirectUse(columnName);
    }

    // -----------------------------------------------------
    //                                               Synonym
    //                                               -------
    public String getSynonym() {
        return _synonym;
    }

    public String getSynonymSettingExpression() {
        return _synonym != null ? "\"" + _synonym + "\"" : "null";
    }

    // -----------------------------------------------------
    //                                                 Alias
    //                                                 -----
    public boolean hasAlias() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getAlias());
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

    public String getAliasSettingExpression() {
        return hasAlias() ? "\"" + getAlias() + "\"" : "null";
    }

    // -----------------------------------------------------
    //                                               DB Type
    //                                               -------
    public void setDbType(String dbType) {
        this._dbType = dbType;
    }

    public String getDbType() {
        return _dbType;
    }

    public boolean hasDbType() {
        return _dbType != null && _dbType.trim().length() > 0;
    }

    public String getDbTypeExpression() {
        return hasDbType() ? _dbType : "UnknownType";
    }

    public boolean isDbTypeChar() {
        return hasDbType() && (_dbType.startsWith("char"));
    }

    public boolean isDbTypeCharOrVarchar() {
        return hasDbType() && (_dbType.startsWith("char") || _dbType.startsWith("varchar"));
    }

    public boolean isDbTypeNCharOrNVarchar() {
        return hasDbType() && (_dbType.startsWith("nchar") || _dbType.startsWith("nvarchar"));
    }

    public boolean isDbTypeStringClob() { // as pinpoint
        return hasDbType() && _columnHandler.isConceptTypeStringClob(_dbType);
    }

    public boolean isDbTypeBytesOid() { // as pinpoint
        return hasDbType() && _columnHandler.isConceptTypeBytesOid(_dbType);
    }

    public boolean isDbTypeOracleDate() { // as pinpoint
        return hasDbType() && _columnHandler.isOracleDate(_dbType);
    }

    public boolean isSQLServerUniqueIdentifier() { // as pinpoint
        return hasDbType() && _columnHandler.isSQLServerUniqueIdentifier(_dbType);
    }

    // -----------------------------------------------------
    //                                           Column Size
    //                                           -----------
    public String getColumnSize() {
        return _columnSize;
    }

    public void setColumnSize(String columnSize) {
        _columnSize = columnSize;
    }

    public void setupColumnSize(int columnSize, int decimalDigits) {
        if (DfColumnExtractor.isColumnSizeValid(columnSize)) {
            if (DfColumnExtractor.isDecimalDigitsValid(decimalDigits)) {
                setColumnSize(columnSize + ", " + decimalDigits);
            } else {
                setColumnSize(String.valueOf(columnSize));
            }
        }
    }

    public boolean hasColumnSize() {
        return _columnSize != null && _columnSize.trim().length() > 0;
    }

    protected Integer getIntegerColumnSize() { // without decimal digits!
        if (_columnSize == null) {
            return null;
        }
        final String realSize;
        if (_columnSize.contains(",")) {
            realSize = _columnSize.split(",")[0];
        } else {
            realSize = _columnSize;
        }
        try {
            return Integer.parseInt(realSize);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Integer getDecimalDigits() {
        if (_columnSize == null) {
            return null;
        }
        if (!_columnSize.contains(",")) {
            return 0;
        }
        return Integer.parseInt(_columnSize.split(",")[1].trim());
    }

    public String getColumnSizeSettingExpression() {
        final Integer columnSize = getIntegerColumnSize();
        if (columnSize == null) {
            return "null";
        }
        return String.valueOf(columnSize);
    }

    // -----------------------------------------------------
    //                                        Decimal Digits
    //                                        --------------
    public String getColumnDecimalDigitsSettingExpression() {
        final Integer decimalDigits = getDecimalDigits();
        if (decimalDigits == null) {
            return "null";
        }
        return String.valueOf(decimalDigits);
    }

    // -----------------------------------------------------
    //                                               NotNull
    //                                               -------
    /**
     * Return the isNotNull property of the column
     */
    public boolean isNotNull() {
        return _isNotNull;
    }

    /**
     * Set the isNotNull property of the column
     */
    public void setNotNull(boolean status) {
        _isNotNull = status;
    }

    public boolean isMakeIsNullOrEmpty() {
        if (isNotNull() || isPrimaryKey()) {
            return false;
        }
        return isJavaNativeStringObject() && !isDbTypeChar() && !isDbTypeStringClob();
    }

    // -----------------------------------------------------
    //                                        Auto Increment
    //                                        --------------
    /**
     * Return auto increment/sequence string for the target database. We need to
     * pass in the props for the target database!
     * @return The determination, true or false.
     */
    public boolean isAutoIncrement() {
        return _isAutoIncrement;
    }

    /**
     * Set the auto increment value.
     * Use isAutoIncrement() to find out if it is set or not.
     * @param value Determination.
     */
    public void setAutoIncrement(boolean value) {
        _isAutoIncrement = value;
    }

    // -----------------------------------------------------
    //                                         Default Value
    //                                         -------------
    public void setDefaultValue(String def) {
        _defaultValue = def;
    }

    public boolean hasDefaultValue() {
        return _defaultValue != null && _defaultValue.trim().length() > 0;
    }

    public boolean hasDefaultValueExceptAutoIncrement() {
        return !isIdentityOrSequence() && hasDefaultValue();
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    // -----------------------------------------------------
    //                                        Column Comment
    //                                        --------------
    public String getPlainComment() {
        return _plainComment;
    }

    public void setPlainComment(String plainComment) {
        this._plainComment = plainComment;
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

    public void setComment(String comment) {
        this._plainComment = comment;
    }

    public String getCommentForSchemaHtml() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveTextForSchemaHtml(getComment());
        return comment != null ? comment : "";
    }

    public boolean isCommentForJavaDocValid() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return hasComment() && prop.isEntityJavaDocDbCommentValid();
    }

    public String getCommentForJavaDoc() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveTextForJavaDoc(getComment(), "    ");
        return comment != null ? comment : "";
    }

    public boolean isCommentForDBMetaValid() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return hasComment() && prop.isEntityDBMetaDbCommentValid();
    }

    public String getCommentForDBMetaSettingExpression() {
        if (!isCommentForDBMetaValid()) {
            return "null";
        }
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveTextForDBMeta(getComment());
        return comment != null ? "\"" + comment + "\"" : "null";
    }

    // -----------------------------------------------------
    //                                               Display
    //                                               -------
    public String getColumnDefinitionLineDisp() {
        final StringBuilder sb = new StringBuilder();
        if (isPrimaryKey()) {
            plugDelimiterIfNeeds(sb);
            sb.append("PK");
        }
        if (isAutoIncrement()) {
            plugDelimiterIfNeeds(sb);
            sb.append("ID");
        }
        if (hasTopColumnUnique()) {
            plugDelimiterIfNeeds(sb);
            sb.append("UQ");
        } else {
            if (isUnique()) {
                plugDelimiterIfNeeds(sb);
                sb.append("UQ+");
            }
        }
        if (hasTopColumnIndex()) {
            plugDelimiterIfNeeds(sb);
            sb.append("IX");
        } else {
            if (hasIndex()) {
                plugDelimiterIfNeeds(sb);
                sb.append("IX+");
            }
        }
        if (isNotNull()) {
            plugDelimiterIfNeeds(sb);
            sb.append("NotNull");
        }
        plugDelimiterIfNeeds(sb);
        sb.append(getDbTypeExpression());
        if (getColumnSize() != null && getColumnSize().trim().length() > 0) {
            sb.append("(" + getColumnSize() + ")");
        }
        if (getDefaultValue() != null && getDefaultValue().trim().length() > 0 && !isAutoIncrement()) {
            plugDelimiterIfNeeds(sb);
            sb.append("default=[").append(getDefaultValue() + "]");
        }
        if (isForeignKey()) {
            plugDelimiterIfNeeds(sb);
            sb.append("FK to " + getForeignTableName());
        }
        if (hasSql2EntityRelatedTable()) {
            plugDelimiterIfNeeds(sb);
            sb.append("refers to ").append(getSql2EntityRelatedTable().getName());
            if (hasSql2EntityRelatedColumn()) {
                sb.append(".").append(getSql2EntityRelatedColumn().getName());
            }
        }
        if (hasClassification()) {
            plugDelimiterIfNeeds(sb);
            sb.append("classification=").append(getClassificationName());
        }
        return sb.toString();
    }

    private void plugDelimiterIfNeeds(StringBuilder sb) {
        if (sb.length() != 0) {
            sb.append(", ");
        }
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    public boolean isPrimaryKey() {
        return _isPrimaryKey;
    }

    public void setPrimaryKey(boolean pk) {
        _isPrimaryKey = pk;
    }

    public String getPrimaryKeyName() {
        return _primaryKeyName;
    }

    public void setPrimaryKeyName(String primaryKeyName) {
        _primaryKeyName = primaryKeyName;
    }

    public boolean isAdditionalPrimaryKey() {
        return _additionalPrimaryKey;
    }

    public void setAdditionalPrimaryKey(boolean additionalPrimaryKey) {
        _additionalPrimaryKey = additionalPrimaryKey;
    }

    public boolean isTwoOrMoreColumnPrimaryKey() {
        return getTable().getPrimaryKey().size() > 1;
    }

    public String getPrimaryKeyMarkForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (isPrimaryKey()) {
            sb.append("o");
            if (isTwoOrMoreColumnPrimaryKey()) {
                sb.append("<span class=\"flgplus\">+</span>");
            }
        } else {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    public String getPrimaryKeyTitleForSchemaHtml() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String value = prop.resolveAttributeForSchemaHtml(_primaryKeyName);
        if (value == null) {
            return "";
        }
        final Table table = getTable();
        final String title;
        if (table.isUseSequence()) {
            final String sequenceName = table.getDefinedSequenceName();
            final BigDecimal minimumValue = table.getSequenceMinimumValue();
            final StringBuilder optionSb = new StringBuilder();
            if (minimumValue != null) {
                if (optionSb.length() > 0) {
                    optionSb.append(",");
                }
                optionSb.append("minimum(" + minimumValue + ")");
            }
            final BigDecimal maximumValue = table.getSequenceMaximumValue();
            if (maximumValue != null) {
                if (optionSb.length() > 0) {
                    optionSb.append(",");
                }
                optionSb.append("maximum(" + maximumValue + ")");
            }
            final Integer incrementSize = table.getSequenceIncrementSize();
            if (incrementSize != null) {
                if (optionSb.length() > 0) {
                    optionSb.append(",");
                }
                optionSb.append("increment(" + incrementSize + ")");
            }
            final Integer cacheSize = table.getSequenceCacheSize();
            if (cacheSize != null) {
                if (optionSb.length() > 0) {
                    optionSb.append(",");
                }
                optionSb.append("dfcache(" + cacheSize + ")");
            }
            if (optionSb.length() > 0) {
                optionSb.insert(0, ":");
            }
            title = _primaryKeyName + " :: sequence=" + sequenceName + optionSb;
        } else {
            title = _primaryKeyName;
        }
        return " title=\"" + prop.resolveAttributeForSchemaHtml(title) + "\"";
    }

    // ===================================================================================
    //                                                                         Foreign Key
    //                                                                         ===========
    /**
     * Utility method to determine if this column is a foreign key.
     */
    public boolean isForeignKey() {
        return (getForeignKey() != null);
    }

    /**
     * Determine if this column is a foreign key that refers to the
     * same table as another foreign key column in this table.
     */
    public boolean isMultipleFK() {
        final ForeignKey fk = getForeignKey();
        if (fk == null) {
            return false;
        }
        final String myForeignTableName = fk.getForeignTableName();
        final ForeignKey[] fks = _table.getForeignKeys();
        final String myColumnName = _name;
        for (int i = 0; i < fks.length; i++) {
            final String foreignTableName = fks[i].getForeignTableName();
            if (!myForeignTableName.equalsIgnoreCase(foreignTableName)) {
                continue;
            }
            // same table reference was found
            final List<String> columnsNameList = fks[i].getLocalColumnNameList();

            // the bug exists but it doesn't have heavy problem so not fixed for compatibility
            //  if FOO_ID, BAR_ID, QUX_ID : FK_ONE(FOO_ID, BAR_ID), FK_TWO(BAR_ID, QUX_ID)
            //  then BAR_ID column returns false here (actually it also be multiple FK)
            if (!Srl.containsElementIgnoreCase(columnsNameList, myColumnName)) {
                return true;
            }
        }
        // No multiple foreign keys.
        return false;
    }

    protected String filterUnderscore(String name) {
        return Srl.replace(name, "_", "");
    }

    /**
     * get the foreign key object for this column
     * if it is a foreign key or part of a foreign key
     * @return Foreign key. (NullAllowed)
     */
    public ForeignKey getForeignKey() {
        return _table.getForeignKey(this._name);
    }

    public List<ForeignKey> getForeignKeyList() {
        return _table.getForeignKeyList(_name);
    }

    public String getForeignTableNameCommaStringWithHtmlHref() { // mainly for SchemaHTML
        final StringBuilder sb = new StringBuilder();
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder(prop);
        final String delimiter = ",<br />";
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        final int size = foreignKeyList.size();
        if (size == 0) {
            return "&nbsp;";
        }
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String foreignTableName = fk.getForeignTable().getTableDispName();
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, foreignTableName, delimiter));
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
    }

    /**
     * It contains one-to-one relations.
     * @return The property names of foreign relation as comma string for literal. (NotNull)
     */
    public String getForeignPropertyNameCommaStringLiteralExpression() { // mainly for ColumnInfo constructor
        final StringBuilder sb = new StringBuilder();
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        final int size = foreignKeyList.size();
        if (size == 0) {
            return "null";
        }
        final String delimiter = ",";
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String foreignPropertyName = fk.getForeignJavaBeansRulePropertyName();
            sb.append(delimiter).append(foreignPropertyName);
        }
        final List<ForeignKey> referrerList = getReferrerList();
        for (ForeignKey referrer : referrerList) {
            if (!referrer.isOneToOne()) {
                continue;
            }
            String propertyNameAsOne = referrer.getReferrerJavaBeansRulePropertyNameAsOne();
            sb.append(delimiter).append(propertyNameAsOne);
        }
        sb.delete(0, delimiter.length());
        return "\"" + sb.toString() + "\"";
    }

    /**
     * Utility method to get the related table of this column if it is a foreign
     * key or part of a foreign key
     */
    public String getRelatedTableName() {
        ForeignKey fk = getForeignKey();
        return (fk == null ? null : fk.getForeignTableName());
    }

    /**
     * Utility method to get the related table of this column if it is a foreign
     * key or part of a foreign key
     */
    public String getForeignTableName() {
        final ForeignKey fk = getForeignKey();
        return (fk == null ? "" : fk.getForeignTableName());
    }

    /**
     * Adds the foreign key from another table that refers to this column.
     * 
     * @return The determination, true or false.
     */
    public boolean isSingleKeyForeignKey() {
        final ForeignKey fk = getForeignKey();
        return (fk == null ? false : fk.isSimpleKeyFK());
    }

    /**
     * Utility method to get the related column of this local column if this
     * column is a foreign key or part of a foreign key.
     */
    public String getRelatedColumnName() {
        ForeignKey fk = getForeignKey();
        if (fk == null) {
            return null;
        } else {
            return fk.getLocalForeignMapping().get(this._name).toString();
        }
    }

    public boolean isDifferentJavaNativeFK() {
        if (!isForeignKey()) {
            return false;
        }
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        for (ForeignKey fk : foreignKeyList) {
            final Column foreignColumn = fk.getForeignColumnByLocalColumn(this);
            if (!getJavaNative().equals(foreignColumn.getJavaNative())) {
                return true; // if one at least exists, returns true
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                            Referrer
    //                                                                            ========
    /**
     * Adds the foreign key from another table that refers to this column.
     */
    public boolean hasReferrer() {
        return !getReferrerList().isEmpty();
    }

    /**
     * Adds the foreign key from another table that refers to this column.
     */
    public void addReferrer(ForeignKey fk) {
        if (_referrerList == null) {
            _referrerList = new ArrayList<ForeignKey>(5);
        }
        _referrerList.add(fk);
    }

    /**
     * Get list of references to this column.
     */
    public List<ForeignKey> getReferrerList() {
        if (_referrerList == null) {
            _referrerList = new ArrayList<ForeignKey>(5);
        }
        return _referrerList;
    }

    /**
     * Get list of references to this column.
     */
    public List<ForeignKey> getReferrers() { // old style
        return getReferrerList();
    }

    protected List<ForeignKey> _singleKeyRefferrers = null;

    /**
     * Adds the foreign key from another table that refers to this column.
     */
    public boolean hasSingleKeyReferrer() {
        return !getSingleKeyReferrers().isEmpty();
    }

    /**
     * Get list of references to this column.
     */
    public List<ForeignKey> getSingleKeyReferrers() {
        if (_singleKeyRefferrers != null) {
            return _singleKeyRefferrers;
        }
        _singleKeyRefferrers = new ArrayList<ForeignKey>(5);
        if (!hasReferrer()) {
            return _singleKeyRefferrers;
        }
        final List<ForeignKey> referrerList = getReferrers();
        for (ForeignKey referrer : referrerList) {
            if (!referrer.isSimpleKeyFK()) {
                continue;
            }
            _singleKeyRefferrers.add(referrer);
        }
        return _singleKeyRefferrers;
    }

    public String getReferrerCommaString() {
        if (_referrerList == null) {
            _referrerList = new ArrayList<ForeignKey>(5);
        }
        final StringBuffer sb = new StringBuffer();
        for (ForeignKey fk : _referrerList) {
            final Table reffererTable = fk.getTable();
            final String name = reffererTable.getName();
            sb.append(", ").append(name);
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getReferrerTableCommaStringWithHtmlHref() { // mainly for SchemaHTML
        if (_referrerList == null) {
            _referrerList = new ArrayList<ForeignKey>(5);
        }
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder(prop);
        final String delimiter = ",<br />";
        final StringBuffer sb = new StringBuffer();
        for (ForeignKey fk : _referrerList) {
            final Table referrerTable = fk.getTable();
            final String referrerTableName = referrerTable.getTableDispName();
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, referrerTableName, delimiter));
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
    }

    /**
     * It does NOT contain one-to-one relations.
     * @return The property names of referrer relation as comma string for literal. (NotNull)
     */
    public String getReferrerPropertyNameCommaStringLiteralExpression() { // mainly for ColumnInfo constructor
        final StringBuilder sb = new StringBuilder();
        final List<ForeignKey> referrerList = getReferrers();
        final int size = referrerList.size();
        if (size == 0) {
            return "null";
        }
        final String delimiter = ",";
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = referrerList.get(i);
            if (fk.isOneToOne()) {
                continue;
            }
            final String referrerPropertyName = fk.getReferrerJavaBeansRulePropertyName();
            sb.append(delimiter).append(referrerPropertyName);
        }
        sb.delete(0, delimiter.length());
        return "\"" + sb.toString() + "\"";
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    public boolean isUnique() { // means this column is contained to one of unique constraints.
        final List<Unique> uniqueList = getTable().getUniqueList();
        for (Unique unique : uniqueList) {
            if (unique.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUniqueAllAdditional() {
        final List<Unique> uniqueList = getTable().getUniqueList();
        boolean exists = false;
        for (Unique unique : uniqueList) {
            if (unique.hasSameColumn(this)) {
                if (!unique.isAdditional()) {
                    return false;
                }
                exists = true;
            }
        }
        return exists;
    }

    public boolean hasOnlyOneColumnUnique() {
        final List<Unique> uniqueList = getTable().getOnlyOneColumnUniqueList();
        for (Unique unique : uniqueList) {
            if (unique.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTwoOrMoreColumnUnique() {
        final List<Unique> uniqueList = getTable().getTwoOrMoreColumnUniqueList();
        for (Unique unique : uniqueList) {
            if (unique.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTopColumnUnique() {
        if (hasOnlyOneColumnUnique()) {
            return true;
        }
        if (hasTwoOrMoreColumnUnique()) {
            final List<Unique> uniqueList = getTable().getTwoOrMoreColumnUniqueList();
            for (Unique unique : uniqueList) {
                if (unique.hasSameFirstColumn(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUniqueKeyMarkForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (isUnique()) {
            sb.append("o");
            if (hasTwoOrMoreColumnUnique()) {
                sb.append("<span class=\"flgplus\">+</span>");
            }
        } else {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    public String getUniqueKeyTitleForSchemaHtml() {
        if (!isUnique()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final List<Unique> uniqueList = getTable().getUniqueList();
        for (Unique unique : uniqueList) {
            if (!unique.hasSameColumn(this)) {
                continue;
            }
            final String uniqueKeyName = unique.getName();
            sb.append(sb.length() > 0 ? ", " : "");
            if (uniqueKeyName != null && uniqueKeyName.trim().length() > 0) {
                sb.append(uniqueKeyName + "(");
            } else {
                sb.append("(");
            }
            final Map<Integer, String> indexColumnMap = unique.getIndexColumnMap();
            final Set<Entry<Integer, String>> entrySet = indexColumnMap.entrySet();
            final StringBuilder oneUniqueSb = new StringBuilder();
            for (Entry<Integer, String> entry : entrySet) {
                final String columnName = entry.getValue();
                if (oneUniqueSb.length() > 0) {
                    oneUniqueSb.append(", ");
                }
                oneUniqueSb.append(columnName);
            }
            sb.append(oneUniqueSb);
            sb.append(")");
        }
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String title = prop.resolveAttributeForSchemaHtml(sb.toString());
        return title != null ? " title=\"" + title + "\"" : "";
    }

    // ===================================================================================
    //                                                                               Index
    //                                                                               =====
    public boolean hasIndex() { // means this column is contained to one of indexes.
        final List<Index> indexList = getTable().getIndexList();
        for (Index index : indexList) {
            if (index.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOnlyOneColumnIndex() {
        final List<Index> indexList = getTable().getOnlyOneColumnIndexList();
        for (Index index : indexList) {
            if (index.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTwoOrMoreColumnIndex() {
        final List<Index> indexList = getTable().getTwoOrMoreColumnIndexList();
        for (Index index : indexList) {
            if (index.hasSameColumn(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTopColumnIndex() {
        if (hasOnlyOneColumnIndex()) {
            return true;
        }
        if (hasTwoOrMoreColumnIndex()) {
            final List<Index> indexList = getTable().getTwoOrMoreColumnIndexList();
            for (Index index : indexList) {
                if (index.hasSameFirstColumn(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getIndexMarkForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (hasIndex()) {
            sb.append("o");
            if (hasTwoOrMoreColumnIndex()) {
                sb.append("<span class=\"flgplus\">+</span>");
            }
        } else {
            sb.append("&nbsp;");
        }
        return sb.toString();
    }

    public String getIndexTitleForSchemaHtml() {
        if (!hasIndex()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        final List<Index> indexList = getTable().getIndexList();
        for (Index index : indexList) {
            if (!index.hasSameColumn(this)) {
                continue;
            }
            final String indexName = index.getName();
            sb.append(sb.length() > 0 ? ", " : "");
            if (indexName != null && indexName.trim().length() > 0) {
                sb.append(indexName + "(");
            } else {
                sb.append("(");
            }
            final Map<Integer, String> indexColumnMap = index.getIndexColumnMap();
            final Set<Entry<Integer, String>> entrySet = indexColumnMap.entrySet();
            final StringBuilder oneIndexSb = new StringBuilder();
            for (Entry<Integer, String> entry : entrySet) {
                final String columnName = entry.getValue();
                if (oneIndexSb.length() > 0) {
                    oneIndexSb.append(", ");
                }
                oneIndexSb.append(columnName);
            }
            sb.append(oneIndexSb);
            sb.append(")");
        }
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String title = prop.resolveAttributeForSchemaHtml(sb.toString());
        return title != null ? " title=\"" + title + "\"" : "";
    }

    // ===================================================================================
    //                                                                     Java Definition
    //                                                                     ===============
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

    public String getJavaName() { // lazy load
        if (_javaName != null) {
            return _javaName;
        }
        final String resourceName = (_synonym != null ? _synonym : getName());
        if (needsJavaNameConvert()) {
            _javaName = getDatabaseChecked().convertJavaNameByJdbcNameAsColumn(resourceName);
        } else {
            // initial-capitalize only
            _javaName = initCap(resourceName);
        }
        _javaName = filterJavaNameBuriStyleIfNeeds(_javaName); // for Buri
        _javaName = filterJavaNameNonCompilableConnector(_javaName); // for example, "SPACE EXISTS"
        return _javaName;
    }

    protected String filterJavaNameBuriStyleIfNeeds(String javaName) { // for Buri
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        if (buriProperties.isUseBuri() && getTable().isBuriInternal()) {
            final String arranged = buriProperties.arrangeBuriColumnJavaName(_javaName);
            if (arranged != null) {
                return arranged;
            }
        }
        return javaName;
    }

    protected String filterJavaNameNonCompilableConnector(String javaName) {
        final DfLittleAdjustmentProperties prop = getLittleAdjustmentProperties();
        return prop.filterJavaNameNonCompilableConnector(javaName, new NonCompilableChecker() {
            public String name() {
                return getName();
            }

            public String disp() {
                return getTable().getName() + "." + getName() + ": " + getColumnDefinitionLineDisp();
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
    //                               Uncapitalised Java Name
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
     * Get variable name to use in Java sources (= uncapitalized java name)
     */
    public String getJavaBeansRulePropertyName() {
        return Srl.initBeansProp(getJavaName());
    }

    public String getJavaBeansRulePropertyNameInitCap() {
        return initCap(getJavaBeansRulePropertyName());
    }

    protected String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // -----------------------------------------------------
    //                                             JDBC Type
    //                                             ---------
    public void setJdbcType(String jdbcType) {
        this._jdbcType = jdbcType;
    }

    public String getJdbcType() {
        return _jdbcType;
    }

    public boolean isJdbcTypeChar() { // as pinpoint
        return TypeMap.isJdbcTypeChar(getJdbcType());
    }

    public boolean isJdbcTypeClob() { // as pinpoint
        return TypeMap.isJdbcTypeClob(getJdbcType());
    }

    public boolean isJdbcTypeDate() { // as pinpoint
        return TypeMap.isJdbcTypeDate(getJdbcType());
    }

    public boolean isJdbcTypeTimestamp() { // as pinpoint
        return TypeMap.isJdbcTypeTimestamp(getJdbcType());
    }

    public boolean isJdbcTypeTime() { // as pinpoint
        return TypeMap.isJdbcTypeTime(getJdbcType());
    }

    public boolean isJdbcTypeBlob() { // as pinpoint
        return TypeMap.isJdbcTypeBlob(getJdbcType());
    }

    // -----------------------------------------------------
    //                                           Java Native
    //                                           -----------
    /**
     * Return a string representation of the native java type which corresponds
     * to the JDBC type of this column. Use in the generation of Base objects.
     * This method is used by torque, so it returns Key types for primaryKey and
     * foreignKey columns
     * @return Java native type used by torque. (NotNull)
     */
    public String getJavaNative() {
        if (_sql2EntityForcedJavaNative != null && _sql2EntityForcedJavaNative.trim().length() > 0) {
            return _sql2EntityForcedJavaNative;
        }
        if (Srl.is_Null_or_TrimmedEmpty(_jdbcType)) {
            ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Not found JDBC type of the column.");
            br.addItem("Column");
            br.addElement(getTable().getName() + "." + getName());
            String msg = br.buildExceptionMessage();
            throw new IllegalStateException(msg);
        }
        return TypeMap.findJavaNativeByJdbcType(_jdbcType, getIntegerColumnSize(), getDecimalDigits());
    }

    public String getJavaNativeTypeLiteral() {
        final String javaNative = getJavaNative();
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        final String pureNative = Srl.substringFirstFront(javaNative, "<"); // for example, List<String>
        return grammarInfo.getClassTypeLiteral(pureNative);
    }

    public String getJavaNativeRemovedPackage() { // for SchemaHTML
        final String javaNative = getJavaNative();
        if (!javaNative.contains(".")) {
            return javaNative;
        }
        return javaNative.substring(javaNative.lastIndexOf(".") + ".".length());
    }

    public String getJavaNativeRemovedCSharpNullable() { // for CSharp
        final String javaNative = getJavaNative();
        if (javaNative.endsWith("?")) {
            return javaNative.substring(0, javaNative.length() - "?".length());
        }
        return javaNative;
    }

    public boolean isJavaNativeStringObject() {
        return getTypeMappingProperties().isJavaNativeStringObject(getJavaNative());
    }

    public boolean isJavaNativeNumberObject() {
        return getTypeMappingProperties().isJavaNativeNumberObject(getJavaNative());
    }

    public boolean isJavaNativeDateObject() {
        return getTypeMappingProperties().isJavaNativeDateObject(getJavaNative());
    }

    public boolean isJavaNativeBooleanObject() {
        return getTypeMappingProperties().isJavaNativeBooleanObject(getJavaNative());
    }

    public boolean isJavaNativeBinaryObject() {
        return getTypeMappingProperties().isJavaNativeBinaryObject(getJavaNative());
    }

    // - - - - - -
    // [Java Only]
    // - - - - - -
    public boolean isJavaNativeInteger() { // as pinpoint
        return getJavaNative().equals("Integer");
    }

    public boolean isJavaNativeLong() { // as pinpoint
        return getJavaNative().equals("Long");
    }

    public boolean isJavaNativeBigDecimal() { // as pinpoint
        return getJavaNative().equals("java.math.BigDecimal");
    }

    public boolean isJavaNativeUtilDate() { // as pinpoint
        return getJavaNative().equals("java.util.Date");
    }

    public boolean isJavaNativeTimestamp() { // as pinpoint
        return getJavaNative().equals("java.sql.Timestamp");
    }

    public boolean isJavaNativeTime() { // as pinpoint
        return getJavaNative().equals("java.sql.Time");
    }

    public boolean isJavaNativeByteArray() { // as pinpoint
        return getJavaNative().equals("byte[]");
    }

    public boolean isJavaNativeUUIDObject() { // as pinpoint
        return getJavaNative().equals("java.util.UUID");
    }

    public boolean isJavaNativeUtilList() { // only for array type
        return getJavaNative().equals("java.util.List")
                || (Srl.startsWith(getJavaNative(), "List<") && Srl.endsWith(getJavaNative(), ">"));
    }

    public boolean isJavaNativeValueOfAbleObject() { // Java Only: valueOf-able by String
        List<String> ls = DfCollectionUtil.newArrayList("Integer", "Long", "Short", "Byte", "Boolean", "Character");
        return Srl.endsWith(getJavaNative(), ls.toArray(new String[] {}));

        // BigDecimal does not have valueOf(String)
    }

    // - - - - - - -
    // [CSharp Only]
    // - - - - - - -
    public boolean isJavaNativeCSharpNullable() {
        return getJavaNative().startsWith("Nullable") || getJavaNative().endsWith("?");
    }

    protected boolean containsAsEndsWith(String str, List<Object> ls) {
        for (Object current : ls) {
            final String currentString = (String) current;
            if (str.endsWith(currentString)) {
                return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------
    //                                           Flex Native
    //                                           -----------
    public String getFlexNative() {
        return TypeMap.findFlexNativeByJavaNative(getJavaNative());
    }

    // -----------------------------------------------------
    //                                    ValueType Handling
    //                                    ------------------
    public boolean needsStringClobHandling() {
        return isDbTypeStringClob();
    }

    public boolean needsBytesOidHandling() {
        return isDbTypeBytesOid();
    }

    // ===================================================================================
    //                                                               Sql2Entity Definition
    //                                                               =====================
    public Table getSql2EntityRelatedTable() {
        return _sql2EntityRelatedTable;
    }

    /**
     * Set the related table for Sql2Entity. <br />
     * This is used at supplementary information and LoadReferrer for customize entity.
     * @param sql2EntityRelatedTable The related table for Sql2Entity. (NullAllowed)
     */
    public void setSql2EntityRelatedTable(Table sql2EntityRelatedTable) {
        _sql2EntityRelatedTable = sql2EntityRelatedTable;
    }

    public boolean hasSql2EntityRelatedTable() {
        return _sql2EntityRelatedTable != null;
    }

    public Column getSql2EntityRelatedColumn() {
        return _sql2EntityRelatedColumn;
    }

    /**
     * Set the related column for Sql2Entity. <br />
     * This is used at supplementary information and LoadReferrer for customize entity.
     * @param sql2EntityRelatedColumn The related column for Sql2Entity. (NullAllowed)
     */
    public void setSql2EntityRelatedColumn(Column sql2EntityRelatedColumn) {
        _sql2EntityRelatedColumn = sql2EntityRelatedColumn;
    }

    public boolean hasSql2EntityRelatedColumn() {
        return _sql2EntityRelatedColumn != null;
    }

    /**
     * Set the forced java native type for Sql2Entity. <br />
     * This is used at getting java native type as high priority.
     * @param sql2EntityForcedJavaNative The forced java native type for Sql2Entity. (NullAllowed)
     */
    public void setSql2EntityForcedJavaNative(String sql2EntityForcedJavaNative) {
        _sql2EntityForcedJavaNative = sql2EntityForcedJavaNative;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * String representation of the column. This is an xml representation.
     * @return string representation in xml
     */
    @Override
    public String toString() { // basically no maintenance
        final StringBuilder result = new StringBuilder();
        result.append("    <column name=\"").append(_name).append('"');

        if (_javaName != null) {
            result.append(" javaName=\"").append(_javaName).append('"');
        }

        if (_isPrimaryKey) {
            result.append(" primaryKey=\"").append(_isPrimaryKey).append('"');
        }

        if (_isNotNull) {
            result.append(" required=\"true\"");
        } else {
            result.append(" required=\"false\"");
        }

        result.append(" type=\"").append(_jdbcType).append('"');

        if (_columnSize != null) {
            result.append(" size=\"").append(_columnSize).append('"');
        }

        if (_defaultValue != null) {
            result.append(" default=\"").append(_defaultValue).append('"');
        }

        // Close the column.
        result.append(" />\n");

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

    protected DfClassificationProperties getClassificationProperties() {
        return getProperties().getClassificationProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    protected DfSequenceIdentityProperties getSequenceIdentityProperties() {
        return getProperties().getSequenceIdentityProperties();
    }

    protected DfTypeMappingProperties getTypeMappingProperties() {
        return getProperties().getTypeMappingProperties();
    }

    // ===================================================================================
    //                                                                       Include Query
    //                                                                       =============
    protected boolean hasQueryRestrictionByClassification() {
        // basically classification is not allowed to greater and less condition
        return hasClassification();
    }

    protected boolean hasQueryRestrictionByFlgClassification() {
        return hasQueryRestrictionByClassification() && getClassificationMapList().size() <= 2;
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
    public boolean isAvailableStringNotEqual() {
        // *because of being simplistic
        //if (hasQueryRestrictionByFlgClassification()) {
        //    return false;
        //}
        return getIncludeQueryProperties().isAvailableStringNotEqual(this);
    }

    public boolean isAvailableStringGreaterThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringGreaterThan(this);
    }

    public boolean isAvailableStringLessThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLessThan(this);
    }

    public boolean isAvailableStringGreaterEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringGreaterEqual(this);
    }

    public boolean isAvailableStringLessEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLessEqual(this);
    }

    public boolean isAvailableStringPrefixSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringPrefixSearch(this);
    }

    public boolean isAvailableStringLikeSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLikeSearch(this);
    }

    public boolean isAvailableStringNotLikeSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringNotLikeSearch(this);
    }

    public boolean isAvailableStringInScope() {
        if (isForeignKey() || isPrimaryKey()) {
            // if PK, it's very basic condition for primary key
            // if FK, it may be used by LoadReferrer
            return true;
        }
        // It's available even if it's flag because this is so basic comparison.
        return getIncludeQueryProperties().isAvailableStringInScope(this);
    }

    public boolean isAvailableStringNotInScope() {
        // *because of being simplistic
        //if (hasQueryRestrictionByFlgClassification()) {
        //    return false;
        //}
        return getIncludeQueryProperties().isAvailableStringNotInScope(this);
    }

    public boolean isAvailableStringEmptyString() {
        if (hasQueryRestrictionByFlgClassification()) {
            return false;
        }
        if (!getDatabaseChecked().isMakeConditionQueryEqualEmptyString()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringEmptyString(this);
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    public boolean isAvailableNumberNotEqual() {
        // *because of being simplistic
        //if (hasQueryRestrictionByFlgClassification()) {
        //    return false;
        //}
        return getIncludeQueryProperties().isAvailableNumberNotEqual(this);
    }

    public boolean isAvailableNumberGreaterThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberGreaterThan(this);
    }

    public boolean isAvailableNumberLessThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberLessThan(this);
    }

    public boolean isAvailableNumberGreaterEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberGreaterEqual(this);
    }

    public boolean isAvailableNumberLessEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberLessEqual(this);
    }

    public boolean isAvailableNumberInScope() {
        if (isForeignKey() || isPrimaryKey()) {
            // if PK, it's very basic condition for primary key
            // if FK, it may be used by LoadReferrer
            return true;
        }
        // It's available even if it's flag because this is so basic comparison.
        return getIncludeQueryProperties().isAvailableNumberInScope(this);
    }

    public boolean isAvailableNumberNotInScope() {
        // *because of being simplistic
        //if (hasQueryRestrictionByFlgClassification()) {
        //    return false;
        //}
        return getIncludeQueryProperties().isAvailableNumberNotInScope(this);
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public boolean isAvailableDateNotEqual() {
        return getIncludeQueryProperties().isAvailableDateNotEqual(this);
    }

    public boolean isAvailableDateGreaterThan() {
        return getIncludeQueryProperties().isAvailableDateGreaterThan(this);
    }

    public boolean isAvailableDateLessThan() {
        return getIncludeQueryProperties().isAvailableDateLessThan(this);
    }

    public boolean isAvailableDateGreaterEqual() {
        return getIncludeQueryProperties().isAvailableDateGreaterEqual(this);
    }

    public boolean isAvailableDateLessEqual() {
        return getIncludeQueryProperties().isAvailableDateLessEqual(this);
    }

    public boolean isAvailableDateFromTo() { // means FromTo of Date type
        if (isJdbcTypeTime()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableDateFromTo(this);
    }

    public boolean isAvailableDateDateFromTo() { // means DateFromTo of Date type
        if (isJdbcTypeTime()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableDateDateFromTo(this);
    }

    public boolean isAvailableDateInScope() {
        if (isForeignKey() || isPrimaryKey()) {
            // if PK, it's very basic condition for primary key
            // if FK, it may be used by LoadReferrer
            return true;
        }
        return getIncludeQueryProperties().isAvailableDateInScope(this);
    }

    public boolean isAvailableDateNotInScope() {
        return getIncludeQueryProperties().isAvailableDateNotInScope(this);
    }

    protected DfIncludeQueryProperties getIncludeQueryProperties() {
        return DfBuildProperties.getInstance().getIncludeQueryProperties();
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        return getClassificationProperties().getClassificationDeploymentMap();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        return getClassificationProperties().getClassificationDefinitionMap();
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // If sql2EntityTableName exists(when sql2entity only), use it at first.
    // Then it would be not found, it uses formal table name of the column.
    // - - - - - - - - - -/

    public boolean hasClassification() {
        if (hasSql2EntityRelatedTableClassification()) {
            return true;
        }
        return getClassificationProperties().hasClassification(getTableName(), getName());
    }

    public boolean isTableClassification() {
        if (!hasClassification()) {
            return false;
        }
        return getClassificationProperties().isTableClassification(getClassificationName());
    }

    public boolean hasClassificationName() {
        if (hasSql2EntityRelatedTableClassificationName()) {
            return true;
        }
        return getClassificationProperties().hasClassificationName(getTableName(), getName());
    }

    public boolean hasClassificationAlias() {
        if (hasSql2EntityRelatedTableClassificationAlias()) {
            return true;
        }
        return getClassificationProperties().hasClassificationAlias(getTableName(), getName());
    }

    public String getClassificationName() {
        final String classificationName = getSql2EntityRelatedTableClassificationName();
        if (classificationName != null) {
            return classificationName;
        }
        return getClassificationProperties().getClassificationName(getTableName(), getName());
    }

    public String getClassificationMetaSettingExpression() { // for DBMeta
        if (!hasClassification()) {
            return "null";
        }
        final String classificationName = getClassificationName();
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        return projectPrefix + "CDef.DefMeta." + classificationName;
    }

    public boolean isCheckSelectedClassification() {
        final DfLittleAdjustmentProperties littleProp = getLittleAdjustmentProperties();
        return littleProp.isCheckSelectedClassification() && hasClassification();
    }

    public boolean isForceClassificationSetting() {
        final DfLittleAdjustmentProperties littleProp = getLittleAdjustmentProperties();
        return littleProp.isForceClassificationSetting() && hasClassification();
    }

    public String getPropertySettingModifier() {
        return isForceClassificationSetting() ? "protected" : "public";
    }

    public List<Map<String, String>> getClassificationMapList() {
        final Map<String, List<Map<String, String>>> definitionMap = getClassificationDefinitionMap();
        final String classificationName = getClassificationName();
        final List<Map<String, String>> classificationMapList = definitionMap.get(classificationName);
        if (classificationMapList == null) {
            throwClassificationDeploymentClassificationNotFoundException(classificationName);
        }
        return classificationMapList;
    }

    protected void throwClassificationDeploymentClassificationNotFoundException(String classificationName) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The classification of the column was not found in the DBFlute property.");
        br.addItem("Advice");
        br.addElement("Make sure classificationDefinitionMap.dfprop and");
        br.addElement("classificationDeploymentMap.dfprop are correct each other.");
        br.addElement("For example, a classification name is case sensitive.");
        br.addElement("See the document for the DBFlute properties.");
        br.addItem("Column");
        br.addElement(getName());
        br.addItem("Related Classification");
        br.addElement(classificationName);
        br.addItem("Defined Classification List");
        br.addElement(getClassificationDefinitionMap().keySet());
        final String msg = br.buildExceptionMessage();
        throw new DfClassificationDeploymentClassificationNotFoundException(msg);
    }

    protected boolean hasSql2EntityRelatedTableClassification() {
        if (!hasSql2EntityRelatedTable()) {
            return false;
        }
        final String tableName = getSql2EntityRelatedTable().getName();
        return getClassificationProperties().hasClassification(tableName, getName());
    }

    protected boolean hasSql2EntityRelatedTableClassificationName() {
        if (!hasSql2EntityRelatedTable()) {
            return false;
        }
        final String tableName = getSql2EntityRelatedTable().getName();
        return getClassificationProperties().hasClassificationName(tableName, getName());
    }

    protected boolean hasSql2EntityRelatedTableClassificationAlias() {
        if (!hasSql2EntityRelatedTable()) {
            return false;
        }
        final String tableName = getSql2EntityRelatedTable().getName();
        return getClassificationProperties().hasClassificationAlias(tableName, getName());
    }

    protected String getSql2EntityRelatedTableClassificationName() {
        if (!hasSql2EntityRelatedTable()) {
            return null;
        }
        final String tableName = getSql2EntityRelatedTable().getName();
        return getClassificationProperties().getClassificationName(tableName, getName());
    }

    public boolean hasCheckImplicitSetClassification() {
        if (!hasClassification()) {
            return false;
        }
        final String classificationName = getClassificationName();
        if (classificationName == null) {
            return false;
        }
        return getClassificationProperties().isCheckImplicitSet(classificationName);
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    public boolean isIdentityOrSequence() { // for Schema HTML
        if (isIdentity()) {
            return true;
        }
        final Table table = getTable();
        if (isPrimaryKey() && table.hasSinglePrimaryKey() && table.isUseSequence()) {
            return true;
        }
        return false;
    }

    public String getSubColumnSequenceName() {
        final DfSequenceIdentityProperties prop = getSequenceIdentityProperties();
        return prop.getSubColumnSequenceName(getTableName(), getName());
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    public boolean isIdentity() {
        if (_isAutoIncrement) {
            // It gives priority to auto-increment information of JDBC.
            return true;
        } else {
            final String identityPropertyName = getTable().getIdentityPropertyName();
            return getTable().isUseIdentity() && getJavaName().equalsIgnoreCase(identityPropertyName);
        }
    }

    // ===================================================================================
    //                                                                       Common Column
    //                                                                       =============
    protected Boolean _commonColumn;

    public boolean isCommonColumn() {
        if (_commonColumn != null) {
            return _commonColumn;
        }
        _commonColumn = false;
        if (getTable().hasAllCommonColumn()) {
            final List<Column> commonColumnList = getTable().getCommonColumnList();
            for (Column column : commonColumnList) {
                if (column.getName().equalsIgnoreCase(getName())) {
                    _commonColumn = true;
                    break;
                }
            }
        }
        return _commonColumn;
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    public boolean isOptimisticLock() {
        return isVersionNo() || isUpdateDate();
    }

    public boolean isVersionNo() {
        final String versionNoPropertyName = getTable().getVersionNoPropertyName();
        return getTable().isUseVersionNo() && getJavaName().equalsIgnoreCase(versionNoPropertyName);
    }

    public boolean isUpdateDate() {
        final String updateDatePropertyName = getTable().getUpdateDatePropertyName();
        return getTable().isUseUpdateDate() && getJavaName().equalsIgnoreCase(updateDatePropertyName);
    }

    public String getOptimistickLockExpression() {
        if (isVersionNo()) {
            return "OptimisticLockType.VERSION_NO";
        } else if (isUpdateDate()) {
            return "OptimisticLockType.UPDATE_DATE";
        } else {
            return "null";
        }
    }

    public String getOptimistickLockExpressionNotNull() { // basically for C#
        if (isVersionNo()) {
            return "OptimisticLockType.VERSION_NO";
        } else if (isUpdateDate()) {
            return "OptimisticLockType.UPDATE_DATE";
        } else {
            return "OptimisticLockType.NONE";
        }
    }

    // ===================================================================================
    //                                                                        Empty String
    //                                                                        ============
    public boolean isEntityConvertEmptyStringToNull() {
        if (!isJavaNativeStringObject()) {
            return false;
        }
        return getLittleAdjustmentProperties().isEntityConvertEmptyStringToNull();
    }

    // ===================================================================================
    //                                                                          Simple DTO
    //                                                                          ==========
    public String getSimpleDtoVariableName() {
        return getProperties().getSimpleDtoProperties().buildFieldName(getJavaName());
    }

    // -----------------------------------------------------
    //                                     JSONIC Decoration
    //                                     -----------------
    public boolean hasSimpleDtoJsonicDecoration() {
        // add a determination element when a new decoration is added
        return hasSimpleDtoJsonicDecorationDatePattern() // Date
                || hasSimpleDtoJsonicDecorationTimestampPattern() // Timestamp
                || hasSimpleDtoJsonicDecorationTimePattern() // Time
        ;
    }

    public boolean hasSimpleDtoJsonicDecorationDatePattern() {
        if (!isJavaNativeUtilDate()) {
            return false;
        }
        return getProperties().getSimpleDtoProperties().hasJsonicDecorationDatePattern();
    }

    public String getSimpleDtoJsonicDecorationDatePattern() {
        return getProperties().getSimpleDtoProperties().getJsonicDecorationDatePattern();
    }

    public boolean hasSimpleDtoJsonicDecorationTimestampPattern() {
        if (!isJavaNativeTimestamp()) {
            return false;
        }
        return getProperties().getSimpleDtoProperties().hasJsonicDecorationTimestampPattern();
    }

    public String getSimpleDtoJsonicDecorationTimestampPattern() {
        return getProperties().getSimpleDtoProperties().getJsonicDecorationTimestampPattern();
    }

    public boolean hasSimpleDtoJsonicDecorationTimePattern() {
        if (!isJavaNativeTime()) {
            return false;
        }
        return getProperties().getSimpleDtoProperties().hasJsonicDecorationTimePattern();
    }

    public String getSimpleDtoJsonicDecorationTimePattern() {
        return getProperties().getSimpleDtoProperties().getJsonicDecorationTimePattern();
    }

    // ===================================================================================
    //                                                                     Behavior Filter
    //                                                                     ===============
    private String _behaviorFilterBeforeInsertColumnExpression;

    public String getBehaviorFilterBeforeInsertColumnExpression() {
        return _behaviorFilterBeforeInsertColumnExpression;
    }

    public void setBehaviorFilterBeforeInsertColumnExpression(String expression) {
        _behaviorFilterBeforeInsertColumnExpression = expression;
    }

    private String _behaviorFilterBeforeUpdateColumnExpression;

    public String getBehaviorFilterBeforeUpdateColumnExpression() {
        return _behaviorFilterBeforeUpdateColumnExpression;
    }

    public void setBehaviorFilterBeforeUpdateColumnExpression(String expression) {
        _behaviorFilterBeforeUpdateColumnExpression = expression;
    }

    // ===================================================================================
    //                                                                           CSS Class
    //                                                                           =========
    public boolean hasSchemaHtmlColumnNameCssClass() {
        return isCommonColumn() || isVersionNo() || isUpdateDate();
    }

    public String getSchemaHtmlColumnNameCssClass() {
        final String delimiter = " ";
        final StringBuilder sb = new StringBuilder();
        if (isCommonColumn()) {
            sb.append("comcolcell");
        }
        if (isVersionNo() || isUpdateDate()) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append("optcell");
        }
        return sb.toString();
    }

    public String getSchemaHtmlColumnAliasCssClass() {
        final String delimiter = " ";
        final StringBuilder sb = new StringBuilder();
        sb.append("aliascell");
        if (isCommonColumn()) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append("comcolcell");
        }
        if (isVersionNo() || isUpdateDate()) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }
            sb.append("optcell");
        }
        return sb.toString();
    }
}