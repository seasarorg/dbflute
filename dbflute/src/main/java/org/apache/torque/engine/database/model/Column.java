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
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfIncludeQueryProperties;
import org.xml.sax.Attributes;

/**
 * A Class for holding data about a column used in an Application.
 *
 * @author Modified by mkubo
 */
public class Column {
    /** Logging class from commons.logging */
    private static Log _log = LogFactory.getLog(Column.class);

    // ==============================================================================
    //                                                                      Attribute
    //                                                                      =========
    private Table _parentTable;
    
    // ------------------------------------
    //                    Column Definition
    //                    -----------------
    private String _name;

    private String _description;

    private boolean _isPrimaryKey = false;

    private boolean _isAutoIncrement = false;

    private String _defaultValue;

    // ........................
    //                     Type
    //                     ^^^^
    private String _torqueType;

    private String _dbType;

    private String _javaType;

    private Object _columnType;

    private String _size;

    // ........................
    //               Constraint
    //               ^^^^^^^^^^
    private boolean _isNotNull = false;

    // ........................
    //                    Other
    //                    ^^^^^
    private String _javaName = null;

    private String _javaNamingMethod;

    // ------------------------------------
    //                             Relation
    //                             --------
    private List<ForeignKey> _referrers;

    // ------------------------------------
    //                                Other
    //                                -----
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
     *
     * @param name column name
     */
    public Column(String name) {
        this._name = name;
    }

    // ==============================================================================
    //                                                                        Loading
    //                                                                        =======
    public static String makeList(List columns) {
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
        //Name
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
            _javaNamingMethod = _parentTable.getDatabase().getDefaultJavaNamingMethod();
        }

        //Primary Key
        {
            final String primaryKey = attrib.getValue("primaryKey");
            _isPrimaryKey = ("true".equals(primaryKey));
        }

        // HELP: Should primary key, index, and/or idMethod="native"
        // affect isNotNull?  If not, please document why here.
        final String notNull = attrib.getValue("required");
        _isNotNull = (notNull != null && "true".equals(notNull));

        //AutoIncrement/Sequences
        final String autoIncrement = attrib.getValue("autoIncrement");
        _isAutoIncrement = ("true".equals(autoIncrement));

        //Default column value.
        _defaultValue = attrib.getValue("default");

        _size = attrib.getValue("size");

        setTorqueType(attrib.getValue("type"));
        setDbType(attrib.getValue("dbType"));

        _inheritanceType = attrib.getValue("inheritance");
        _isInheritance = (_inheritanceType != null && !_inheritanceType.equals("false"));

        //        this._inputValidator = attrib.getValue("inputValidator");
        _description = attrib.getValue("description");
    }

    public String getFullyQualifiedName() {
        return (_parentTable.getName() + '.' + _name);
    }

    // ==============================================================================
    //                                                                       Accessor
    //                                                                       ========
    public String getName() {
        return _name;
    }

    public void setName(String newName) {
        _name = newName;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String newDescription) {
        _description = newDescription;
    }

    // -----------------------------------------------------
    //                                              JavaName
    //                                              --------
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
        }
        return _javaName;
    }

    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getUncapitalisedJavaName() {
        return StringUtils.uncapitalise(getJavaName());
    }

    /**
     * Get variable name to use in Java sources (= uncapitalised java name)
     */
    public String getJavaBeansRulePropertyName() {
        final Database db = getTable().getDatabase();
        return db.decapitalizePropertyName(db.convertJavaNameByJdbcNameAsTable(getName()));
    }
    
    public String getJavaBeansRulePropertyNameInitCap() {
        final Database db = getTable().getDatabase();
        return initCap(db.decapitalizePropertyName(db.convertJavaNameByJdbcNameAsTable(getName())));
    }

    protected String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Set name to use in Java sources
     */
    public void setJavaName(String javaName) {
        this._javaName = javaName;
    }

    /**
     * Get type to use in Java sources
     */
    public String getJavaType() {
        return _javaType;
    }

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

    /**
     * Set the parent Table of the column
     */
    public void setTable(Table parent) {
        _parentTable = parent;
    }

    /**
     * Get the parent Table of the column
     */
    public Table getTable() {
        if (_parentTable == null) {
            String msg = "This Column did not have 'table': columnName=" + _name;
            throw new IllegalStateException(msg);
        }
        return _parentTable;
    }

    /**
     * Returns the Name of the table the column is in
     */
    public String getTableName() {
        return _parentTable.getName();
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
    public List getChildren() {
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
            sb.append("INC");
        }
        if (isUnique()) {
            plugDelimiterIfNeeds(sb);
            sb.append("UQ");
        }
        plugDelimiterIfNeeds(sb);
        sb.append(getDbType());
        if (getSize() != null) {
            sb.append("(" + getSize() + ")");
        }
        if (isNotNull()) {
            plugDelimiterIfNeeds(sb);
            sb.append("NotNull");
        }
        if (getDefaultValue() != null) {
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

    // -------------------------------------------
    //                                 Primary Key
    //                                 -----------
    /**
     * Set if the column is a primary key or not
     */
    public void setPrimaryKey(boolean pk) {
        _isPrimaryKey = pk;
    }

    /**
     * Return true if the column is a primary key
     */
    public boolean isPrimaryKey() {
        return _isPrimaryKey;
    }

    // -------------------------------------------
    //                               AutoIncrement
    //                               -------------
    /**
     * Return auto increment/sequence string for the target database. We need to
     * pass in the props for the target database!
     */
    public boolean isAutoIncrement() {
        return _isAutoIncrement;
    }

    /**
     * Set the auto increment value.
     * Use isAutoIncrement() to find out if it is set or not.
     */
    public void setAutoIncrement(boolean value) {
        _isAutoIncrement = value;
    }

    // -------------------------------------------
    //                                  Unique Key
    //                                  ----------
    /**
     * Set true if the column is UNIQUE
     */
    public void setUnique(boolean u) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the UNIQUE property
     */
    public boolean isUnique() {
        final List<Unique> uniqueList = getTable().getUniqueList();
        for (Unique unique : uniqueList) {
            final Map<Integer, String> uniqueColumnMap = unique.getUniqueColumnMap();
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
     * Return true if the column requires a transaction in Postgres
     */
    public boolean requiresTransactionInPostgres() {
        return _needsTransactionInPostgres;
    }

    // -------------------------------------------
    //                                     NotNull
    //                                     -------
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

    // -------------------------------------------
    //                                        Size
    //                                        ----
    /**
     * Returns the size of the column
     */
    public String getSize() {
        return _size;
    }

    /**
     * Set the size of the column
     */
    public void setSize(String newSize) {
        _size = newSize;
    }

    /**
     * Return the size in brackets for use in an sql
     * schema if the type is String.  Otherwise return an empty string
     */
    public String printSize() {
        return (_size == null ? "" : '(' + _size + ')');
    }

    // -------------------------------------------
    //                                 Foreign Key
    //                                 -----------
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
        if (fk != null) {
            ForeignKey[] fks = _parentTable.getForeignKeys();
            for (int i = 0; i < fks.length; i++) {
                if (fks[i].getForeignTableName().equals(fk.getForeignTableName())
                        && !fks[i].getLocalColumns().contains(this._name)) {
                    return true;
                }
            }
        }

        // No multiple foreign keys.
        return false;
    }

    /**
     * get the foreign key object for this column
     * if it is a foreign key or part of a foreign key
     * 
     * @return Foreign key. (Nullable)
     */
    public ForeignKey getForeignKey() {
        return _parentTable.getForeignKey(this._name);
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

    // -------------------------------------------
    //                                    Refferer
    //                                    --------
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

    public String getReferrerCommaStringWithHtmlHref() {
        if (_referrers == null) {
            _referrers = new ArrayList<ForeignKey>(5);
        }
        final StringBuffer sb = new StringBuffer();
        for (ForeignKey fk : _referrers) {
            final Table reffererTable = fk.getTable();
            final String name = reffererTable.getName();
            sb.append(", ").append("<a href=\"#" + name + "\">").append(name).append("</a>");
        }
        sb.delete(0, ", ".length());
        return sb.toString();
    }

    public void setTorqueType(String torqueType) {
        this._torqueType = torqueType;
        if (torqueType.equals("VARBINARY") || torqueType.equals("BLOB")) {
            _needsTransactionInPostgres = true;
        }
    }

    public Object getTorqueType() {
        return _torqueType;
    }

    public void setDbType(String dbType) {
        this._dbType = dbType;
    }

    public Object getDbType() {
        return _dbType;
    }

    /**
     * Utility method to see if the column is a string
     */
    public boolean isString() {
        return (_columnType instanceof String);
    }

    /**
     * Utility method to return the value as an element to be usable
     * in an SQL insert statement. This is used from the SQL loader task
     */
    public boolean needEscapedValue() {
        return (_torqueType != null)
                && (_torqueType.equals("VARCHAR") || _torqueType.equals("LONGVARCHAR") || _torqueType.equals("DATE")
                        || _torqueType.equals("DATETIME") || _torqueType.equals("TIMESTAMP") || _torqueType
                        .equals("CHAR"));
    }

    /**
     * Return a string that will give this column a default value.
     * <p>
     * TODO: Properly SQL-escape text values.
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

    /**
     * Set a string that will give this column a default value.
     */
    public void setDefaultValue(String def) {
        _defaultValue = def;
    }

    /**
     * Get a string that will give this column a default value.
     */
    public String getDefaultValue() {
        return _defaultValue;
    }

    // =========================================================================================
    //                                                                            Checked Getter
    //                                                                            ==============
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

    // =========================================================================================
    //                                                                                toString()
    //                                                                                ==========
    /**
     * String representation of the column. This is an xml representation.
     *
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

        if (_size != null) {
            result.append(" size=\"").append(_size).append('"');
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

    /**
     * Set the column type from a string property
     * (normally a string from an sql input file)
     */
    public void setTypeFromString(String typeName, String size) {
        String tn = typeName.toUpperCase();
        setTorqueType(tn);

        if (size != null) {
            this._size = size;
        }

        if (tn.indexOf("CHAR") != -1) {
            _torqueType = "VARCHAR";
            _columnType = "";
        } else if (tn.indexOf("INT") != -1) {
            _torqueType = "INTEGER";
            _columnType = new Integer(0);
        } else if (tn.indexOf("FLOAT") != -1) {
            _torqueType = "FLOAT";
            _columnType = new Float(0);
        } else if (tn.indexOf("DATE") != -1) {
            _torqueType = "DATE";
            _columnType = new Date();
        } else if (tn.indexOf("TIME") != -1) {
            _torqueType = "TIMESTAMP";
            _columnType = new Timestamp(System.currentTimeMillis());
        } else if (tn.indexOf("BINARY") != -1) {
            _torqueType = "LONGVARBINARY";
            _columnType = new Hashtable();
        } else {
            _torqueType = "VARCHAR";
            _columnType = "";
        }
    }

    /**
     * Return a string representation of the primitive java type which
     * corresponds to the JDBC type of this column.
     *
     * @return string representation of the primitive java type
     */
    public String getJavaPrimitive() {
        return TypeMap.getJavaType(_torqueType);
    }

    /**
     * Return a string representation of the native java type which corresponds
     * to the JDBC type of this column. Use in the generation of Base objects.
     * This method is used by torque, so it returns Key types for primaryKey and
     * foreignKey columns
     *
     * @return java datatype used by torque
     */
    public String getJavaNative() {
        return TypeMap.getJavaType(_torqueType);
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================

    protected boolean containsAsEndsWith(String str, List<Object> ls) {
        for (Object current : ls) {
            final String currentString = (String) current;
            if (str.endsWith(currentString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether java native is 'String' object.
     * 
     * @return Determination.
     */
    public boolean isJavaNativeStringObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeStringList());
    }

    /**
     * Determine whether java native is 'Boolean' object.
     * 
     * @return Determination.
     */
    public boolean isJavaNativeBooleanObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeBooleanList());
    }

    /**
     * Determine whether java native is 'Number' object.
     * 
     * @return Determination.
     */
    public boolean isJavaNativeNumberObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeNumberList());
    }

    /**
     * Determine whether java native is 'Date' object.
     * 
     * @return Determination.
     */
    public boolean isJavaNativeDateObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeDateList());
    }

    /**
     * Determine whether java native is 'Binary' object.
     * 
     * @return Determination.
     */
    public boolean isJavaNativeBinaryObject() {
        return containsAsEndsWith(getJavaNative(), getTable().getDatabase().getJavaNativeBinaryList());
    }

    /**
     * Returns true if the column type is boolean in the
     * java object and a numeric (1 or 0) in the db.
     */
    public boolean isBooleanInt() {
        return TypeMap.isBooleanInt(_torqueType);
    }

    /**
     * Returns true if the column type is boolean in the
     * java object and a String ("Y" or "N") in the db.
     */
    public boolean isBooleanChar() {
        return TypeMap.isBooleanChar(_torqueType);
    }

    //    public boolean isUsePrimitive() {
    //        final String s = getJavaType();
    //        return (s != null && s.equals("primitive"))
    //                || (s == null && !"object".equals(getTable().getDatabase().getDefaultJavaType()));
    //    }

    // ===============================================================================
    //                                                              Properties - Query
    //                                                              ==================
    // ---------------------------------------
    //                                  String
    //                                  ------
    public boolean isAvailableStringNotEqual() {
        return getIncludeQueryProperties().isAvailableStringNotEqual(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterThan() {
        return getIncludeQueryProperties().isAvailableStringGreaterThan(getTableName(), getName());
    }

    public boolean isAvailableStringLessThan() {
        return getIncludeQueryProperties().isAvailableStringLessThan(getTableName(), getName());
    }

    public boolean isAvailableStringGreaterEqual() {
        return getIncludeQueryProperties().isAvailableStringGreaterEqual(getTableName(), getName());
    }

    public boolean isAvailableStringLessEqual() {
        return getIncludeQueryProperties().isAvailableStringLessEqual(getTableName(), getName());
    }

    public boolean isAvailableStringPrefixSearch() {
        return getIncludeQueryProperties().isAvailableStringPrefixSearch(getTableName(), getName());
    }

    public boolean isAvailableStringLikeSearch() {
        return getIncludeQueryProperties().isAvailableStringLikeSearch(getTableName(), getName());
    }

    public boolean isAvailableStringInScope() {
        return getIncludeQueryProperties().isAvailableStringInScope(getTableName(), getName());
    }

    public boolean isAvailableStringNotInScope() {
        return getIncludeQueryProperties().isAvailableStringNotInScope(getTableName(), getName());
    }

    // ---------------------------------------
    //                                  Number
    //                                  ------
    public boolean isAvailableNumberNotEqual() {
        return getIncludeQueryProperties().isAvailableNumberNotEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterThan() {
        return getIncludeQueryProperties().isAvailableNumberGreaterThan(getTableName(), getName());
    }

    public boolean isAvailableNumberLessThan() {
        return getIncludeQueryProperties().isAvailableNumberLessThan(getTableName(), getName());
    }

    public boolean isAvailableNumberGreaterEqual() {
        return getIncludeQueryProperties().isAvailableNumberGreaterEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberLessEqual() {
        return getIncludeQueryProperties().isAvailableNumberLessEqual(getTableName(), getName());
    }

    public boolean isAvailableNumberInScope() {
        return getIncludeQueryProperties().isAvailableNumberInScope(getTableName(), getName());
    }

    public boolean isAvailableNumberNotInScope() {
        return getIncludeQueryProperties().isAvailableNumberNotInScope(getTableName(), getName());
    }

    // ---------------------------------------
    //                                    Date
    //                                    ----
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

    // **********************************************************************************************
    //                                                                                     Properties
    //                                                                                     **********
    // ===============================================================================
    //                                                     Properties - Classification
    //                                                     ===========================
    public Map<String, Map<String, String>> getClassificationDeploymentMap() {
        return getTable().getDatabase().getClassificationDeploymentMap();
    }

    public Map<String, List<Map<String, String>>> getClassificationDefinitionMap() {
        return getTable().getDatabase().getClassificationDefinitionMap();
    }

    public boolean hasClassification() {
        return getTable().getDatabase().hasClassification(getTableName(), getName());
    }

    public boolean hasClassificationName() {
        return getTable().getDatabase().hasClassificationName(getTableName(), getName());
    }

    public boolean hasClassificationAlias() {
        return getTable().getDatabase().hasClassificationAlias(getTableName(), getName());
    }

    public String getClassificationName() {
        return getTable().getDatabase().getClassificationName(getTableName(), getName());
    }

    public List<Map<String, String>> getClassificationMapList() {
        try {
            final Map<String, List<Map<String, String>>> definitionMap = getClassificationDefinitionMap();
            final String classificationName = getClassificationName();
            final List<Map<String, String>> classificationMapList = definitionMap.get(classificationName);
            if (classificationMapList == null) {
                String msg = "The definitionMap did not contain the classificationName: ";
                msg = msg + "definitionMap=" + definitionMap + " classificationName=" + classificationName;
                throw new IllegalStateException(msg);
            }
            return classificationMapList;
        } catch (RuntimeException e) {
            _log.warn("getClassificationMapList() threw the exception: ", e);
            throw e;
        }
    }

    // ===============================================================================
    //                                                           Properties - Identity
    //                                                           =====================
    public boolean isIdentity() {
        if (_isAutoIncrement) {
            // It gives priority to auto-increment information of JDBC.
            return true;
        } else {
            return getTable().isUseIdentity()
                    && getUncapitalisedJavaName().equals(getTable().getIdentityPropertyName());
        }
    }
}