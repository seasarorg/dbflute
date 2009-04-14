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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.metadata.DfColumnHandler;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.logic.schemahtml.DfSchemaHtmlBuilder;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfBuriProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.util.DfStringUtil;
import org.xml.sax.Attributes;

/**
 * A Class for holding data about a column used in an Application.
 * @author Modified by jflute
 */
public class Column {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static Log _log = LogFactory.getLog(Column.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private Table _table;

    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    private String _name;

    private String _description;

    private boolean _isPrimaryKey = false;

    private boolean _additionalPrimaryKey = false;

    private boolean _isAutoIncrement = false;

    private String _defaultValue;

    private String _comment;

    // -----------------------------------------------------
    //                                                  Type
    //                                                  ----
    private String _javaType;

    private String _torqueType;

    private String _dbType;

    private String _columnSize;

    // -----------------------------------------------------
    //                                            Constraint
    //                                            ----------
    private boolean _isNotNull = false;

    // -----------------------------------------------------
    //                                             Java Name
    //                                             ---------
    private String _javaName = null;

    private String _javaNamingMethod;

    // -----------------------------------------------------
    //                                              Relation
    //                                              --------
    private List<ForeignKey> _referrers;

    // -----------------------------------------------------
    //                                                 Other
    //                                                 -----
    private int _position;

    // only one type is supported currently, which assumes the
    // column either contains the classnames or a key to
    // classnames specified in the schema.  Others may be
    // supported later.
    private String _inheritanceType;

    private boolean _isInheritance;

    private boolean _isEnumeratedClasses;

    private List<Inheritance> _inheritanceList;

    private boolean _needsTransactionInPostgres;

    //    /** class name to do input validation on this column */
    //    private String _inputValidator = null;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    private String _sql2EntityTableName;
    private String _sql2EntityJavaNative;

    // ==============================================================================
    //                                                                    Constructor
    //                                                                    ===========
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
        this._name = name;
    }

    // ==============================================================================
    //                                                                        Loading
    //                                                                        =======
    public static String makeList(List<String> columns) {
        Object obj = columns.get(0);
        boolean isColumnList = (obj instanceof Column);
        if (isColumnList) {
            obj = ((Column) obj).getName();
        }
        StringBuffer buf = new StringBuffer((String) obj);
        for (int i = 1; i < columns.size(); i++) {
            obj = columns.get(i);
            if (isColumnList) {
                obj = ((Column) obj).getName();
            }
            buf.append(", ").append(obj);
        }
        return buf.toString();
    }

    public void loadFromXML(Attributes attrib) {
        // Name
        _name = attrib.getValue("name");

        _javaName = attrib.getValue("javaName");
        _javaType = attrib.getValue("javaType");
        if (_javaType != null && _javaType.length() == 0) {
            _javaType = null;
        }

        // retrieves the method for converting from specified name to
        // a java name.
        _javaNamingMethod = attrib.getValue("javaNamingMethod");
        if (_javaNamingMethod == null) {
            _javaNamingMethod = _table.getDatabase().getDefaultJavaNamingMethod();
        }

        // Primary Key
        {
            final String primaryKey = attrib.getValue("primaryKey");
            _isPrimaryKey = ("true".equals(primaryKey));
        }

        // HELP: Should primary key, index, and/or idMethod="native"
        // affect isNotNull?  If not, please document why here.
        final String notNull = attrib.getValue("required");
        _isNotNull = (notNull != null && "true".equals(notNull));

        // AutoIncrement/Sequences
        final String autoIncrement = attrib.getValue("autoIncrement");
        _isAutoIncrement = ("true".equals(autoIncrement));

        // Column comment.
        _comment = attrib.getValue("comment");

        // Default column value.
        _defaultValue = attrib.getValue("default");

        _columnSize = attrib.getValue("size");

        setTorqueType(attrib.getValue("type"));
        setDbType(attrib.getValue("dbType"));

        _inheritanceType = attrib.getValue("inheritance");
        _isInheritance = (_inheritanceType != null && !_inheritanceType.equals("false"));

        //        this._inputValidator = attrib.getValue("inputValidator");
        _description = attrib.getValue("description");
    }

    public String getFullyQualifiedName() {
        return (_table.getName() + '.' + _name);
    }

    // ==============================================================================
    //                                                              Basic Information
    //                                                              =================
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
    //                                            Alias Name
    //                                            ----------
    public boolean hasAlias() {
        final String alias = getAlias();
        return alias != null && alias.trim().length() > 0;
    }

    public String getAlias() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = _comment;
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
        final String alias = getAlias();
        if (alias == null || alias.trim().length() == 0) {
            return "null";
        }
        return "\"" + alias + "\"";
    }

    // -----------------------------------------------------
    //                                           Description
    //                                           -----------
    // No use at DBFlute
    public String getDescription() {
        return _description;
    }

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

    public String getJavaName() {
        if (_javaName == null) {
            if (needsJavaNameConvert()) {
                _javaName = getDatabaseChecked().convertJavaNameByJdbcNameAsColumn(getName());
            } else {
                _javaName = getName();
            }
            _javaName = filterBuriJavaNameIfNeeds(_javaName); // for Buri
        }
        return _javaName;
    }

    protected String filterBuriJavaNameIfNeeds(String javaName) { // for Buri
        final DfBuriProperties buriProperties = getProperties().getBuriProperties();
        if (buriProperties.isUseBuri() && getTable().isBuriInternal()) {
            final String arranged = buriProperties.arrangeBuriColumnJavaName(_javaName);
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

    public String getJavaBeansRulePropertyNameInitCap() {
        return initCap(getJavaBeansRulePropertyName());
    }

    protected String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // -----------------------------------------------------
    //                                             Java Type
    //                                             ---------
    // /= = = = = = = = = = = = = = = = = = = = = = = = = = = = 
    // Unused at Generate task.
    // Generate task instead uses JavaNative.
    // Basically the value is same as this property 'javaType'.
    // = = = = = = = = = =/ 
    /**
     * Get type to use in Java sources
     */
    public String getJavaType() {
        return _javaType;
    }

    // -----------------------------------------------------
    //                                           Torque Type
    //                                           -----------
    public void setTorqueType(String torqueType) {
        this._torqueType = torqueType;
        if (torqueType.equals("VARBINARY") || torqueType.equals("BLOB")) {
            _needsTransactionInPostgres = true;
        }
    }

    public Object getTorqueType() {
        return _torqueType;
    }

    // -----------------------------------------------------
    //                                              Position
    //                                              --------
    /**
     * Get the location of this column within the table (one-based).
     * @return value of position.
     */
    public int getPosition() {
        return _position;
    }

    /**
     * Get the location of this column within the table (one-based).
     * @param v  Value to assign to position.
     */
    public void setPosition(int v) {
        this._position = v;
    }

    // -----------------------------------------------------
    //                                                 Table
    //                                                 -----
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

    /**
     * Returns the Name of the table the column is in
     */
    public String getTableName() {
        return _table.getName();
    }

    /**
     * A utility function to create a new column
     * from attrib and add it to this table.
     */
    public Inheritance addInheritance(Attributes attrib) {
        Inheritance inh = new Inheritance();
        inh.loadFromXML(attrib);
        addInheritance(inh);

        return inh;
    }

    /**
     * Adds a new inheritance definition to the inheritance list and set the
     * parent column of the inheritance to the current column
     */
    public void addInheritance(Inheritance inh) {
        inh.setColumn(this);
        if (_inheritanceList == null) {
            _inheritanceList = new ArrayList<Inheritance>();
            _isEnumeratedClasses = true;
        }
        _inheritanceList.add(inh);
    }

    /**
     * Get the inheritance definitions.
     */
    public List<Inheritance> getChildren() {
        return _inheritanceList;
    }

    /**
     * Determine if this column is a normal property or specifies a
     * the classes that are represented in the table containing this column.
     */
    public boolean isInheritance() {
        return _isInheritance;
    }

    /**
     * Determine if possible classes have been enumerated in the xml file.
     */
    public boolean isEnumeratedClasses() {
        return _isEnumeratedClasses;
    }

    // =====================================================================================
    //                                                                     Column Definition
    //                                                                     =================
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
        if (isUnique()) {
            plugDelimiterIfNeeds(sb);
            sb.append("UQ");
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
            sb.append("Default=[").append(getDefaultValue() + "]");
        }
        if (isForeignKey()) {
            plugDelimiterIfNeeds(sb);
            sb.append("FK to " + getForeignTableName());
        }
        return sb.toString();
    }

    private void plugDelimiterIfNeeds(StringBuilder sb) {
        if (sb.length() != 0) {
            sb.append(" : ");
        }
    }

    // -----------------------------------------------------
    //                                           Primary Key
    //                                           -----------
    /**
     * Return true if the column is a primary key
     * @return Determination.
     */
    public boolean isPrimaryKey() {
        return _isPrimaryKey;
    }

    /**
     * Set if the column is a primary key or not
     * @param pk Determination.
     */
    public void setPrimaryKey(boolean pk) {
        _isPrimaryKey = pk;
    }

    /**
     * Return true if the column is an additional primary key
     * @return Determination.
     */
    public boolean isAdditionalPrimaryKey() {
        return _additionalPrimaryKey;
    }

    /**
     * Set if the column is an additional primary key or not
     * @param additionalPrimaryKey Determination.
     */
    public void setAdditionalPrimaryKey(boolean additionalPrimaryKey) {
        _additionalPrimaryKey = additionalPrimaryKey;
    }

    // -----------------------------------------------------
    //                                        Auto Increment
    //                                        --------------
    /**
     * Return auto increment/sequence string for the target database. We need to
     * pass in the props for the target database!
     * @return Determination.
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
    //                                            Unique Key
    //                                            ----------
    /**
     * Set true if the column is UNIQUE
     * @param u Determination.
     */
    public void setUnique(boolean u) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the UNIQUE property
     * @return Determination.
     */
    public boolean isUnique() {
        final List<Unique> uniqueList = getTable().getUniqueList();
        for (Unique unique : uniqueList) {
            final Map<Integer, String> uniqueColumnMap = unique.getIndexColumnMap();
            final Set<Integer> ordinalPositionSet = uniqueColumnMap.keySet();
            for (Integer ordinalPosition : ordinalPositionSet) {
                final String columnName = uniqueColumnMap.get(ordinalPosition);
                if (getName().equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true if the column requires a transaction in Postgres SQL
     */
    public boolean requiresTransactionInPostgres() {
        return _needsTransactionInPostgres;
    }

    // -----------------------------------------------------
    //                                                 Index
    //                                                 -----
    public boolean hasIndex() {
        final List<Index> indexList = getTable().getIndexList();
        for (Index index : indexList) {
            if (index.hasSameFirstColumn(this)) {
                return true;
            }
        }
        return false;
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

    // -----------------------------------------------------
    //                                               DB Type
    //                                               -------
    // for documents basically
    public void setDbType(String dbType) {
        this._dbType = dbType;
    }

    public String getDbType() {
        return _dbType;
    }

    public String getDbTypeExpression() {
        return _dbType != null ? _dbType : "UnknownType";
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

    public boolean hasColumnSize() {
        return _columnSize != null && _columnSize.trim().length() > 0;
    }

    protected Integer getIntegerColumnSize() {// Without Decimal Digits!
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
    //                                        Column Comment
    //                                        --------------
    public boolean hasComment() {
        final String comment = getComment();
        return comment != null && comment.trim().length() > 0;
    }

    public String getComment() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.extractCommentFromDbComment(_comment);
        return comment != null ? comment : "";
    }

    public void setComment(String comment) {
        this._comment = comment;
    }

    public String getCommentForSchemaHtml() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveLineSeparatorForSchemaHtml(getComment());
        return comment != null ? comment : "";
    }

    public boolean isCommentForJavaDocValid() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        return hasComment() && prop.isEntityJavaDocDbCommentValid();
    }

    public String getCommentForJavaDoc() {
        final DfDocumentProperties prop = getProperties().getDocumentProperties();
        final String comment = prop.resolveLineSeparatorForJavaDoc(getComment(), "    ");
        return comment != null ? comment : "";
    }

    // -----------------------------------------------------
    //                                         Default Value
    //                                         -------------
    /**
     * Return a string that will give this column a default value.
     */
    public String getDefaultSetting() {
        StringBuffer dflt = new StringBuffer(0);
        if (_defaultValue != null) {
            dflt.append("default ");
            if (TypeMap.isTextType(_torqueType)) {
                // TODO: Properly SQL-escape the text.
                dflt.append('\'').append(_defaultValue).append('\'');
            } else {
                dflt.append(_defaultValue);
            }
        }
        return dflt.toString();
    }

    public void setDefaultValue(String def) {
        _defaultValue = def;
    }

    public boolean hasDefaultValue() {
        return _defaultValue != null && _defaultValue.trim().length() > 0;
    }

    public String getDefaultValue() {
        return _defaultValue;
    }

    // -----------------------------------------------------
    //                                           Foreign Key
    //                                           -----------
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
        ForeignKey fk = getForeignKey();
        if (fk == null) {
            return false;
        }
        String myForeignTableName = fk.getForeignTableName();
        ForeignKey[] fks = _table.getForeignKeys();
        String myColumnName = _name;
        for (int i = 0; i < fks.length; i++) {
            String foreignTableName = fks[i].getForeignTableName();
            if (!myForeignTableName.equalsIgnoreCase(foreignTableName)) {
                continue;
            }
            List<String> columnsNameList = fks[i].getLocalColumns();
            if (!DfStringUtil.containsIgnoreCase(myColumnName, columnsNameList)) {
                return true;
            }
        }
        // No multiple foreign keys.
        return false;
    }

    protected String filterUnderscore(String name) {
        return DfStringUtil.replace(name, "_", "");
    }

    /**
     * get the foreign key object for this column
     * if it is a foreign key or part of a foreign key
     * @return Foreign key. (Nullable)
     */
    public ForeignKey getForeignKey() {
        return _table.getForeignKey(this._name);
    }

    public List<ForeignKey> getForeignKeyList() {
        return _table.getForeignKeyList(_name);
    }

    public String getForeignTableNameCommaStringWithHtmlHref() { // for SchemaHTML
        final StringBuilder sb = new StringBuilder();
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder();
        final String delimiter = ",<br />";
        final Set<String> tableSet = new HashSet<String>();
        final List<ForeignKey> foreignKeyList = getForeignKeyList();
        final int size = foreignKeyList.size();
        if (size == 0) {
            return "&nbsp;";
        }
        for (int i = 0; i < size; i++) {
            final ForeignKey fk = foreignKeyList.get(i);
            final String name = fk.getForeignTableName();
            if (tableSet.contains(name)) {
                continue;
            }
            if (fk.hasFixedCondition()) {
                continue;
            }
            tableSet.add(name);
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, name, delimiter));
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
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
     * @return Determination.
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

    // -----------------------------------------------------
    //                                              Referrer
    //                                              --------
    /**
     * Adds the foreign key from another table that refers to this column.
     */
    public boolean hasReferrer() {
        return !getReferrers().isEmpty();
    }

    /**
     * Adds the foreign key from another table that refers to this column.
     */
    public void addReferrer(ForeignKey fk) {
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        _referrers.add(fk);
    }

    /**
     * Get list of references to this column.
     */
    public List<ForeignKey> getReferrers() {
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        return _referrers;
    }

    protected java.util.List<ForeignKey> _singleKeyRefferrers = null;

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
        final List<ForeignKey> reffererList = getReferrers();
        for (ForeignKey refferer : reffererList) {
            if (!refferer.isSimpleKeyFK()) {
                continue;
            }
            _singleKeyRefferrers.add(refferer);
        }
        return _singleKeyRefferrers;
    }

    public String getReferrerCommaString() {
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        final StringBuffer sb = new StringBuffer();
        for (ForeignKey fk : _referrers) {
            final Table reffererTable = fk.getTable();
            final String name = reffererTable.getName();
            sb.append(", ").append(name);
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public String getReferrerTableCommaStringWithHtmlHref() { // for SchemaHTML
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        final DfSchemaHtmlBuilder schemaHtmlBuilder = new DfSchemaHtmlBuilder();
        final String delimiter = ",<br />";
        final Set<String> tableSet = new HashSet<String>();
        final StringBuffer sb = new StringBuffer();
        for (ForeignKey fk : _referrers) {
            final Table reffererTable = fk.getTable();
            final String name = reffererTable.getName();
            if (tableSet.contains(name)) {
                continue;
            }
            if (fk.hasFixedCondition()) {
                continue;
            }
            tableSet.add(name);
            sb.append(schemaHtmlBuilder.buildRelatedTableLink(fk, name, delimiter));
        }
        sb.delete(0, delimiter.length());
        return sb.toString();
    }

    // ===================================================================================
    //                                                                    Sql2Entity Table
    //                                                                    ================
    public String getSql2EntityTableName() {
        return _sql2EntityTableName;
    }

    public void setSql2EntityTableName(String sql2EntityTableName) {
        _sql2EntityTableName = sql2EntityTableName;
    }

    public String getSql2EntityJavaNative() {
        return _sql2EntityJavaNative;
    }

    public void setSql2EntityJavaNative(String sql2EntityJavaNative) {
        _sql2EntityJavaNative = sql2EntityJavaNative;
    }

    // ===================================================================================
    //                                                                      Checked Getter
    //                                                                      ==============
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

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * String representation of the column. This is an xml representation.
     * @return string representation in xml
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
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

        result.append(" type=\"").append(_torqueType).append('"');

        if (_columnSize != null) {
            result.append(" size=\"").append(_columnSize).append('"');
        }

        if (_defaultValue != null) {
            result.append(" default=\"").append(_defaultValue).append('"');
        }

        if (isInheritance()) {
            result.append(" inheritance=\"").append(_inheritanceType).append('"');
        }

        // Close the column.
        result.append(" />\n");

        return result.toString();
    }

    // ===================================================================================
    //                                                                         Java Native
    //                                                                         ===========
    /**
     * Return a string representation of the native java type which corresponds
     * to the JDBC type of this column. Use in the generation of Base objects.
     * This method is used by torque, so it returns Key types for primaryKey and
     * foreignKey columns
     * @return Java native type used by torque. (NotNull)
     */
    public String getJavaNative() {
        if (_sql2EntityJavaNative != null && _sql2EntityJavaNative.trim().length() > 0) {
            return _sql2EntityJavaNative;
        }
        return TypeMap.findJavaNativeString(_torqueType, getIntegerColumnSize(), getDecimalDigits());
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

    // ===================================================================================
    //                                                                         Flex Native
    //                                                                         ===========
    public String getFlexNative() {
        return TypeMap.findFlexNativeString(getJavaNative());
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================
    public boolean isJavaNativeStringObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeStringList());
    }

    public boolean isJavaNativeNumberObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeNumberList());
    }

    public boolean isJavaNativeDateObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeDateList());
    }

    public boolean isJavaNativeBooleanObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeBooleanList());
    }

    public boolean isJavaNativeBinaryObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeBinaryList());
    }

    public boolean isJavaNativeUUIDObject() {
        if (isJavaNativeStringObject() || isJavaNativeNumberObject() || isJavaNativeDateObject()
                || isJavaNativeBooleanObject() || isJavaNativeBinaryObject()) {
            return false;
        }
        final String dbTypeName = getDbType();
        if (dbTypeName == null) {
            return false;
        }
        return new DfColumnHandler().isUUID(dbTypeName);
    }

    public boolean isJavaNativeStringClob() {
        final String dbTypeName = getDbType();
        if (dbTypeName == null) {
            return false;
        }
        return new DfColumnHandler().isOracleStringClob(dbTypeName);
    }

    public boolean isJavaNativeBytesOid() {
        final String dbTypeName = getDbType();
        if (dbTypeName == null) {
            return false;
        }
        return new DfColumnHandler().isPostgreSQLBytesOid(dbTypeName);
    }

    public boolean isJavaNativeCSharpNullable() {
        return getJavaNative().startsWith("Nullable") || getJavaNative().endsWith("?");
    }

    public boolean isTorqueTypeClob() {// as Pinpoint
        return "CLOB".equals(getTorqueType());
    }

    public boolean isTorqueTypeDate() {// as Pinpoint
        return "DATE".equals(getTorqueType());
    }

    public boolean isTorqueTypeTime() {// as Pinpoint
        return "TIME".equals(getTorqueType());
    }

    public boolean isTorqueTypeTimestamp() {// as Pinpoint
        return "TIMESTAMP".equals(getTorqueType());
    }

    public boolean isTorqueTypeBlob() {// as Pinpoint
        return "BLOB".equals(getTorqueType());
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

    // ===================================================================================
    //                                                                       Include Query
    //                                                                       =============
    protected boolean hasQueryRestrictionByClassification() {
        final DfLittleAdjustmentProperties prop = getTable().getProperties().getLittleAdjustmentProperties();
        return !prop.isMakeConditionQueryClassificationRestriction() && hasClassification();
    }

    protected boolean hasQueryRestrictionByFlgClassification() {
        return hasQueryRestrictionByClassification() && getClassificationMapList().size() <= 2;
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
    public boolean isAvailableStringNotEqual() {
        if (hasQueryRestrictionByFlgClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringNotEqual(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringGreaterThan(getTableName(), getName());
    }

    public boolean isAvailableStringLessThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLessThan(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringGreaterEqual(getTableName(), getName());
    }

    public boolean isAvailableStringLessEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLessEqual(getTableName(), getName());
    }

    public boolean isAvailableStringPrefixSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringPrefixSearch(getTableName(), getName());
    }

    public boolean isAvailableStringLikeSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringLikeSearch(getTableName(), getName());
    }

    public boolean isAvailableStringNotLikeSearch() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringNotLikeSearch(getTableName(), getName());
    }

    public boolean isAvailableStringInScope() {
        return getIncludeQueryProperties().isAvailableStringInScope(getTableName(), getName());
    }

    public boolean isAvailableStringNotInScope() {
        if (hasQueryRestrictionByFlgClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableStringNotInScope(getTableName(), getName());
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    public boolean isAvailableNumberNotEqual() {
        if (hasQueryRestrictionByFlgClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberNotEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberGreaterThan(getTableName(), getName());
    }

    public boolean isAvailableNumberLessThan() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberLessThan(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberGreaterEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberLessEqual() {
        if (hasQueryRestrictionByClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberLessEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberInScope() {
        return getIncludeQueryProperties().isAvailableNumberInScope(getTableName(), getName());
    }

    public boolean isAvailableNumberNotInScope() {
        if (hasQueryRestrictionByFlgClassification()) {
            return false;
        }
        return getIncludeQueryProperties().isAvailableNumberNotInScope(getTableName(), getName());
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public boolean isAvailableDateNotEqual() {
        return getIncludeQueryProperties().isAvailableDateNotEqual(getTableName(), getName());
    }

    public boolean isAvailableDateGreaterThan() {
        return getIncludeQueryProperties().isAvailableDateGreaterThan(getTableName(), getName());
    }

    public boolean isAvailableDateLessThan() {
        return getIncludeQueryProperties().isAvailableDateLessThan(getTableName(), getName());
    }

    public boolean isAvailableDateGreaterEqual() {
        return getIncludeQueryProperties().isAvailableDateGreaterEqual(getTableName(), getName());
    }

    public boolean isAvailableDateLessEqual() {
        return getIncludeQueryProperties().isAvailableDateLessEqual(getTableName(), getName());
    }

    public boolean isAvailableDateFromTo() {
        return getIncludeQueryProperties().isAvailableDateFromTo(getTableName(), getName());
    }

    protected DfIncludeQueryProperties getIncludeQueryProperties() {
        return DfBuildProperties.getInstance().getIncludeQueryProperties();
    }

    // ---------------------------------------
    //                     String Old AsInline
    //                                  ------
    public boolean isAvailableStringEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringNotEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringNotEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringGreaterThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringLessThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringLessThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringGreaterEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringLessEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringLessEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringPrefixSearchOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringPrefixSearchOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringInScopeOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringInScopeOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringNotInScopeOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringNotInScopeOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableStringInScopeSubQueryOldAsInline() {
        return getIncludeQueryProperties().isAvailableStringInScopeSubQueryOldAsInline(getTableName(), getName());
    }

    // ---------------------------------------
    //                                  Number
    //                                  ------
    public boolean isAvailableNumberEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberNotEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberNotEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberGreaterThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberLessThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberLessThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberGreaterEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberLessEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberLessEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberInScopeOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberInScopeOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberNotInScopeOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberNotInScopeOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableNumberInScopeSubQueryOldAsInline() {
        return getIncludeQueryProperties().isAvailableNumberInScopeSubQueryOldAsInline(getTableName(), getName());
    }

    // ---------------------------------------
    //                                    Date
    //                                    ----
    public boolean isAvailableDateEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableDateNotEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateNotEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableDateGreaterThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateGreaterThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableDateLessThanOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateLessThanOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableDateGreaterEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateGreaterEqualOldAsInline(getTableName(), getName());
    }

    public boolean isAvailableDateLessEqualOldAsInline() {
        return getIncludeQueryProperties().isAvailableDateLessEqualOldAsInline(getTableName(), getName());
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    // ===================================================================================
    //                                                                      Classification
    //                                                                      ==============
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        return getTable().getDatabase().getClassificationDeploymentMap();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        return getTable().getDatabase().getClassificationDefinitionMap();
    }

    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // If sql2EntityTableName exists(when sql2entity only), use it at first.
    // Then it would be not found, it uses formal table name of the column.
    // - - - - - - - - - -/

    public boolean hasClassification() {
        final Database database = getTable().getDatabase();
        if (_sql2EntityTableName != null && database.hasClassification(_sql2EntityTableName, getName())) {
            return true;
        }
        return database.hasClassification(getTableName(), getName());
    }

    public boolean hasClassificationName() {
        final Database database = getTable().getDatabase();
        if (_sql2EntityTableName != null && database.hasClassificationName(_sql2EntityTableName, getName())) {
            return true;
        }
        return database.hasClassificationName(getTableName(), getName());
    }

    public boolean hasClassificationAlias() {
        final Database database = getTable().getDatabase();
        if (_sql2EntityTableName != null && database.hasClassificationAlias(_sql2EntityTableName, getName())) {
            return true;
        }
        return database.hasClassificationAlias(getTableName(), getName());
    }

    public String getClassificationName() {
        final Database database = getTable().getDatabase();
        if (_sql2EntityTableName != null) {
            final String classificationName = database.getClassificationName(_sql2EntityTableName, getName());
            if (classificationName != null) {
                return classificationName;
            }
        }
        return database.getClassificationName(getTableName(), getName());
    }

    public List<Map<String, String>> getClassificationMapList() {
        try {
            final Map<String, List<Map<String, String>>> definitionMap = getClassificationDefinitionMap();
            final String classificationName = getClassificationName();
            final List<Map<String, String>> classificationMapList = definitionMap.get(classificationName);
            if (classificationMapList == null) {
                String msg = "The definitionMap did not contain the classificationName:";
                msg = msg + " classificationName=" + classificationName;
                msg = msg + " definitionMap=" + definitionMap;
                throw new IllegalStateException(msg);
            }
            return classificationMapList;
        } catch (RuntimeException e) {
            _log.warn("getClassificationMapList() threw the exception: ", e);
            throw e;
        }
    }

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    public boolean isIdentityOrSequence() { // for Schema HTML
        if (isIdentity()) {
            return true;
        }
        final Table table = getTable();
        if (isPrimaryKey() && table.hasOnlyOnePrimaryKey() && table.isUseSequence()) {
            return true;
        }
        return false;
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

    protected boolean isCommonColumn() {
        if (_commonColumn == null) {
            _commonColumn = false;
            final List<Column> commonColumnList = getTable().getCommonColumnList();
            if (getTable().hasAllCommonColumn()) {
                for (Column column : commonColumnList) {
                    if (column.getName().equals(getName())) {
                        _commonColumn = true;
                        break;
                    }
                }
            }
        }
        return _commonColumn;
    }

    // ===================================================================================
    //                                                                          Version No
    //                                                                          ==========
    public boolean isVersionNo() {
        final String versionNoPropertyName = getTable().getVersionNoPropertyName();
        return getTable().isUseVersionNo() && getJavaName().equalsIgnoreCase(versionNoPropertyName);
    }

    // ===================================================================================
    //                                                                         Update Date
    //                                                                         ===========
    public boolean isUpdateDate() {
        final String updateDatePropertyName = getTable().getUpdateDatePropertyName();
        return getTable().isUseUpdateDate() && getJavaName().equalsIgnoreCase(updateDatePropertyName);
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
    //                                                                       Column Helper
    //                                                                       =============
    protected ColumnHelper _columnHelper;

    protected ColumnHelper helper() {
        if (_columnHelper == null) {
            _columnHelper = new ColumnHelper();
        }
        return _columnHelper;
    }

    protected class ColumnHelper {
        public DfBasicProperties getBasicProperties() {
            return getTable().getProperties().getBasicProperties();
        }

        public DfLanguageDependencyInfo getLanguageDependencyInfo() {
            return getBasicProperties().getLanguageDependencyInfo();
        }
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