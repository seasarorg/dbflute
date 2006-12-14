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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.torque.DfTorqueColumnListToStringUtil;
import org.xml.sax.Attributes;

/**
 * A class for information about foreign keys of a table.
 * <p>
 * @author Modified by mkubo
 */
public class ForeignKey {

    private static final Log _log = LogFactory.getLog(ForeignKey.class);

    private Table _baseTable;

    private String _foreignTableName;

    private String _name;

    private List<String> _localColumns = new ArrayList<String>(3);

    private List<String> _foreignColumns = new ArrayList<String>(3);
    
    private String _foreignPropertyNamePrefix;

    /**
     * Imports foreign key from an XML specification
     *
     * @param attrib the xml attributes
     */
    public void loadFromXML(Attributes attrib) {
        _foreignTableName = attrib.getValue("foreignTable");
        _name = attrib.getValue("name");
    }

    /**
     * Set the base Table of the foreign key
     *
     * @param baseTable the table
     */
    public void setTable(Table baseTable) {
        _baseTable = baseTable;
    }

    /**
     * Get the base Table of the foreign key
     *
     * @return the base table
     */
    public Table getTable() {
        return _baseTable;
    }

    public void setForeignPropertyNamePrefix(String propertyNamePrefix) {
        _foreignPropertyNamePrefix = propertyNamePrefix;
    }
    
    /**
     * TODO: To write Detail Comment.
     * 
     * @return Determination.
     */
    public boolean isForeignColumnsSameAsForeignTablePrimaryKeys() {
        final List<String> foreginTablePrimaryKeyNameList = new ArrayList<String>();
        {
            final Table fkTable = _baseTable.getDatabase().getTable(_foreignTableName);
            final List<Column> foreignTablePrimaryKeyList = fkTable.getPrimaryKey();
            for (Column column : foreignTablePrimaryKeyList) {
                foreginTablePrimaryKeyNameList.add(column.getName());
            }
        }
        final Set<String> foreginTablePrimaryKeyNameSet = new HashSet<String>(foreginTablePrimaryKeyNameList);
        final Set<String> foreignColumnsSet = new HashSet<String>(_foreignColumns);
        return foreginTablePrimaryKeyNameSet.equals(foreignColumnsSet);
    }

    /**
     * Get the foreignTableName of the FK
     *
     * @return the name of the foreign table
     */
    public String getForeignTableName() {
        return _foreignTableName;
    }

    /**
     * Set the foreignTableName of the FK
     *
     * @param tableName the name of the foreign table
     */
    public void setForeignTableName(String tableName) {
        _foreignTableName = tableName;
    }

    /**
     * Returns the name attribute.
     *
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name attribute.
     *
     * @param name the name
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Adds a new reference entry to the foreign key
     *
     * @param attrib the xml attributes
     */
    public void addReference(Attributes attrib) {
        addReference(attrib.getValue("local"), attrib.getValue("foreign"));
    }

    /**
     * Adds a new reference entry to the foreign key
     *
     * @param local name of the local column
     * @param foreign name of the foreign column
     */
    public void addReference(String local, String foreign) {
        _localColumns.add(local);
        _foreignColumns.add(foreign);
    }

    /**
     * Adds a new reference entry to the foreign key
     *
     * @param localColumnNameList Name list  of the local column
     * @param foreignColumnNameList Name list of the foreign column
     */
    public void addReference(List<String> localColumnNameList, List<String> foreignColumnNameList) {
        _localColumns.addAll(localColumnNameList);
        _foreignColumns.addAll(foreignColumnNameList);
    }

    // ==========================================================================================
    //                                                                              Determination
    //                                                                              =============
    /**
     * Is this relation 'one-to-one'?
     * 
     * @return Determination.
     */
    public boolean isOneToOne() {
        final List<Column> localColumnList = getLocalColumnObjectList();
        final List<Column> localPrimaryColumnList = getTable().getPrimaryKey();
        if (localColumnList.equals(localPrimaryColumnList)) {
            return true;
        } else {
            final List<Unique> uniqueList = getTable().getUniqueList();
            for (final Unique unique : uniqueList) {
                if (unique.hasSameColumnSet(localColumnList)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSimpleKeyFK() {
        return _localColumns.size() == 1;
    }

    // ==========================================================================================
    //                                                            Get Columns & ColumnList Method
    //                                                            ===============================

    /**
     * Returns the list of local column names. You should not edit this List.
     * 
     * @return the local columns
     */
    public List<String> getLocalColumns() {
        return _localColumns;
    }

    public String getLocalColumnNameAsOne() {
        if (getLocalColumns().size() != 1) {
            String msg = "This method is for only-one foreign-key: getForeignColumns().size()="
                    + getLocalColumns().size();
            msg = msg + " baseTable=" + getTable().getName() + " foreignTable=" + getForeignTable().getName();
            throw new IllegalStateException(msg);
        }
        return getLocalColumns().get(0);
    }

    /**
     * Returns the list of local column names. You should not edit this List.
     * 
     * @return the local columns
     */
    public String getLocalColumnJavaNameAsOne() {
        final String columnName = getLocalColumnNameAsOne();
        final Table localTable = getTable();
        return localTable.getColumn(columnName).getJavaName();
    }

    /**
     * Returns the list of local column objects. You should not edit this List.
     * 
     * @return the local objects
     */
    public List<Column> getLocalColumnObjectList() {
        final List<String> columnList = getLocalColumns();
        if (columnList == null || columnList.isEmpty()) {
            String msg = "The localColumnList is null or empty." + columnList;
            throw new IllegalStateException(msg);
        }
        final List<Column> resultList = new ArrayList<Column>();
        for (final Iterator ite = columnList.iterator(); ite.hasNext();) {
            final String name = (String) ite.next();
            final Column col = getTable().getColumn(name);
            if (col == null) {
                String msg = "The columnName is not existing at the table: ";
                msg = msg + "columnName=" + name + " tableName=" + getTable().getName();
                throw new IllegalStateException(msg);
            }
            resultList.add(col);
        }
        return resultList;
    }

    /**
     * Returns the list of foreign column names. You should not edit this List.
     * 
     * @return the foreign columns
     */
    public List<String> getForeignColumns() {
        return _foreignColumns;
    }

    public String getForeignColumnNameAsOne() {
        if (getForeignColumns().size() != 1) {
            String msg = "This method is for only-one foreign-key: getForeignColumns().size()="
                    + getForeignColumns().size();
            msg = msg + " baseTable=" + getTable().getName() + " foreignTable=" + getForeignTable().getName();
            throw new IllegalStateException(msg);
        }
        return getForeignColumns().get(0);
    }

    public String getForeignColumnJavaNameAsOne() {
        final String columnName = getForeignColumnNameAsOne();
        final Table foreignTable = getForeignTable();
        return foreignTable.getColumn(columnName).getJavaName();
    }

    /**
     * Returns the list of foreign column objects. You should not edit this List.
     * 
     * @return the foreign objects
     */
    public List<Column> getForeignColumnObjectList() {
        final Table foreignTable = getTable().getDatabase().getTable(getForeignTableName());
        final List<String> columnList = getForeignColumns();
        if (columnList == null || columnList.isEmpty()) {
            String msg = "The getForeignColumns() is null or empty." + columnList;
            throw new IllegalStateException(msg);
        }
        final List<Column> resultList = new ArrayList<Column>();
        for (Iterator ite = columnList.iterator(); ite.hasNext();) {
            final String name = (String) ite.next();
            final Column foreignCol = foreignTable.getColumn(name);
            resultList.add(foreignCol);
        }
        return resultList;
    }

    /**
     * Get foreign table.
     * <p>
     * @return Foreign table.
     */
    public Table getForeignTable() {
        final Table foreignTable = getTable().getDatabase().getTable(getForeignTableName());
        if (foreignTable == null) {
            String msg = "The database does not contain the foreign table name: " + getForeignTableName();
            throw new IllegalStateException(msg);
        }
        return foreignTable;
    }

    // ==========================================================================================
    //                                                                  Get Column Mapping Method
    //                                                                  =========================

    /**
     * Utility method to get local column names to foreign column names
     * mapping for this foreign key.
     *
     * @return table mapping foreign names to local names
     */
    public Hashtable<String, String> getLocalForeignMapping() {
        final Hashtable<String, String> resultHash = new Hashtable<String, String>();
        for (int i = 0; i < _localColumns.size(); i++) {
            resultHash.put(_localColumns.get(i), _foreignColumns.get(i));
        }
        return resultHash;
    }

    /**
     * Utility method to get foreign column names to local column names
     * mapping for this foreign key.
     *
     * @return table mapping local names to foreign names
     */
    public Hashtable<String, String> getForeignLocalMapping() {
        final Hashtable<String, String> resultHash = new Hashtable<String, String>();
        for (int i = 0; i < _localColumns.size(); i++) {
            resultHash.put(_foreignColumns.get(i), _localColumns.get(i));
        }
        return resultHash;
    }

    /**
     * Utility method to get local column objects to foreign column objects
     * mapping for this foreign key.
     * <p>
     * <pre>
     * Example)
     * - for (final Iterator ite = getLocalColumnObjectList(); ite.hasNext();) {
     * -     final Column localCol = (Column) ite.next();
     * -     final Column foreignCol = (Column) getLocalForeignColumnObjectMapping().get(localCol);
     * </pre>
     * @return table mapping foreign objects to local objects
     */
    public Hashtable<Column, Column> getLocalForeignColumnObjectMapping() {
        final Hashtable<Column, Column> resultHash = new Hashtable<Column, Column>();
        final List<Column> localList = getLocalColumnObjectList();
        final List<Column> foreignList = getForeignColumnObjectList();
        for (int i = 0; i < localList.size(); i++) {
            resultHash.put(localList.get(i), foreignList.get(i));
        }
        return resultHash;
    }

    /**
     * Utility method to get foreign column objects to local column objects
     * mapping for this foreign key.
     * <p>
     * <pre>
     * Example)
     * - for (final Iterator ite = getForeignColumnObjectList(); ite.hasNext();) {
     * -     final Column foreignCol = (Column) ite.next();
     * -     final Column localCol = (Column) getForeignLocalColumnObjectMapping().get(foreignCol);
     * </pre>
     * @return table mapping foreign objects to local objects
     */
    public Hashtable<Column, Column> getForeignLocalColumnObjectMapping() {
        final Hashtable<Column, Column> resultHash = new Hashtable<Column, Column>();
        final List<Column> localList = getLocalColumnObjectList();
        final List<Column> foreignList = getForeignColumnObjectList();
        for (int i = 0; i < localList.size(); i++) {
            resultHash.put(foreignList.get(i), localList.get(i));
        }
        return resultHash;
    }

    // ==========================================================================================
    //                                                                     Generate String Method
    //                                                                     ======================
    /**
     * Returns a comma delimited string of local column names
     * <p>
     * @return Generated string.
     */
    public String getLocalColumnNames() {
        return Column.makeList(getLocalColumns());
    }

    /**
     * Returns a comma delimited string of foreign column names
     * <p>
     * @return Generated string.
     */
    public String getForeignColumnNames() {
        return Column.makeList(getForeignColumns());
    }

    /**
     * Returns first local column name.
     * 
     * @return Fisrt local column name.
     */
    public String getFirstLocalColumnName() {
        return getLocalColumns().get(0);
    }

    /**
     * Get the value of foreign property name.
     * 
     * @return Generated string.
     */
    public String getForeignPropertyName() {
        return getForeignPropertyName(false);
    }

    /**
     * Get the value of foreign property name.
     * 
     * @return Generated string.
     */
    public String getForeignJavaBeansRulePropertyName() {
        return getForeignPropertyName(true);
    }

    /**
     * Get the value of foreign property name.
     * 
     * @param isJavaBeansRule Is java-beans rule.
     * @return Generated string.
     */
    protected String getForeignPropertyName(boolean isJavaBeansRule) {
        try {
            final List localColumnList = getLocalColumnObjectList();

            String result = "";
            for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
                final Column col = (Column) ite.next();

                if (col.isMultipleFK()) {
                    result = result + col.getJavaName();
                }
            }
            if (result.trim().length() != 0) {
                result = "By" + result;
            }
            if (getForeignTable().getName().equals(getTable().getName())) {
                result = result + "Self";
            }
            if (isJavaBeansRule) {
                result = getForeignTable().getJavaBeansRulePropertyName() + result;
            } else {
                result = getForeignTable().getUncapitalisedJavaName() + result;
            }
            if (_foreignPropertyNamePrefix != null) {
                result = _foreignPropertyNamePrefix + result;
            }

            return result;
        } catch (RuntimeException e) {
            String msg = "getForeignPropertyName() threw the exception";
            msg = msg + ": localColumns=" + _localColumns;
            msg = msg + ": foreignTableName=" + _foreignTableName;
            _log.warn(msg, e);
            throw e;
        }
    }

    /**
     * Get thr value of refferer property name.
     * 
     * @return Generated string.
     */
    public String getReffererPropertyName() {
        return getReffererPropertyName(false);
    }

    /**
     * Get the value of foreign property name.
     * 
     * @return Generated string.
     */
    public String getReffererJavaBeansRulePropertyName() {
        return getReffererPropertyName(true);
    }

    /**
     * Get thr value of refferer property name.
     * 
     * @param isJavaBeansRule Is java-beans rule.
     * @return Generated string.
     */
    public String getReffererPropertyName(boolean isJavaBeansRule) {
        final List localColumnList = getLocalColumnObjectList();

        String result = "";
        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();

            if (col.isMultipleFK()) {
                result = result + col.getJavaName();
            }
        }
        if (result.trim().length() != 0) {
            result = "By" + result;
        }
        if (getTable().getName().equals(getForeignTable().getName())) {
            result = result + "Self";
        }
        if (isJavaBeansRule) {
            return getTable().getJavaBeansRulePropertyName() + result + "List";
        } else {
            return getTable().getUncapitalisedJavaName() + result + "List";
        }
    }

    /**
     * Get thr value of refferer property name.
     * 
     * @return Generated string.
     */
    public String getReffererPropertyNameAsOne() {
        return getReffererPropertyNameAsOne(false);
    }

    /**
     * Get the value of foreign property name.
     * 
     * @return Generated string.
     */
    public String getReffererJavaBeansRulePropertyNameAsOne() {
        return getReffererPropertyNameAsOne(true);
    }

    /**
     * Get thr value of refferer property name.
     * 
     * @param isJavaBeansRule Is java-beans rule.
     * @return Generated string.
     */
    public String getReffererPropertyNameAsOne(boolean isJavaBeansRule) {
        final List localColumnList = getLocalColumnObjectList();

        String result = "";
        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();

            if (col.isMultipleFK()) {
                result = result + col.getJavaName();
            }
        }
        if (result.trim().length() != 0) {
            result = "By" + result;
        }
        if (getTable().getName().equals(getForeignTable().getName())) {
            result = result + "Self";
        }
        if (isJavaBeansRule) {
            return getTable().getJavaBeansRulePropertyName() + result + "AsOne";
        } else {
            return getTable().getUncapitalisedJavaName() + result + "AsOne";
        }
    }

    /**
     * Get the value of foreign property name with capitalising the initial character.
     * 
     * @return String.
     */
    public String getForeignPropertyNameInitCap() {
        final String foreignPropertyName = getForeignPropertyName();
        return foreignPropertyName.substring(0, 1).toUpperCase() + foreignPropertyName.substring(1);
    }

    /**
     * Get the value of refferer property name with capitalising the initial character.
     * 
     * @return Generated string.
     */
    public String getReffererPropertyNameInitCap() {
        final String reffererPropertyName = getReffererPropertyName();
        return reffererPropertyName.substring(0, 1).toUpperCase() + reffererPropertyName.substring(1);
    }

    /**
     * Get the value of refferer property name with capitalising the initial character.
     * 
     * @return Generated string.
     */
    public String getReffererPropertyNameInitCapAsOne() {
        final String reffererPropertyName = getReffererPropertyNameAsOne();
        return reffererPropertyName.substring(0, 1).toUpperCase() + reffererPropertyName.substring(1);
    }

    /**
     * Returns comma-string for local column name.
     * 
     * @return Generated string.
     */
    public String getLocalColumnNameCommaString() {
        return DfTorqueColumnListToStringUtil.getColumnNameCommaString(getLocalColumns());
    }

    /**
     * Returns LocalColumn-Getter-CommaString. 
     *     [getRcvlcqNo(), getSprlptTp()]
     * <p>
     * @return Generated string.
     */
    public String getLocalColumnGetterCommaString() {
        final List localColumnList = getLocalColumnObjectList();
        String result = "";
        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column col = (Column) ite.next();
            final String getterString = "get" + col.getJavaName() + "()";
            if ("".equals(result)) {
                result = getterString;
            } else {
                result = result + ", " + getterString;
            }
        }
        return result;
    }

    /**
     * Returns ForeignTable-BeanSetupString. 
     *     [setRcvlcqNo_Suffix(getRcvlcqNo()).setSprlptTp_Suffix(getSprlptTp())]
     * <p>
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getForeignTableBeanSetupString(String setterSuffix) {
        return getForeignTableBeanSetupString(setterSuffix, "set");
    }

    public String getForeignTableBeanSetupString(String setterSuffix, String setterPrefix) {
        final List localColumnList = getLocalColumnObjectList();
        String result = "";
        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = (Column) getLocalForeignColumnObjectMapping().get(localCol);
            final String setterName = setterPrefix + foreignCol.getJavaName() + setterSuffix;
            final String getterName = "(_" + localCol.getUncapitalisedJavaName() + ")";
            if ("".equals(result)) {
                result = setterName + getterName;
            } else {
                result = result + "." + setterName + getterName;
            }
        }
        return result;
    }

    /**
     * Returns ChildrenTable-BeanSetupString. 
     *     [setRcvlcqNo_Suffix(getRcvlcqNo()).setSprlptTp_Suffix(getSprlptTp());]
     * <p>
     * Abount ForeginKey that Table#getRefferer() returns, Local means children.
     * <p>
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getChildrenTableBeanSetupString(String setterSuffix) {
        return getChildrenTableBeanSetupString(setterSuffix, "set");
    }

    public String getChildrenTableBeanSetupString(String setterSuffix, String setterPrefix) {
        List localColumnList = getLocalColumnObjectList();
        String result = "";

        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = (Column) getLocalForeignColumnObjectMapping().get(localCol);
            final String setterName = setterPrefix + localCol.getJavaName() + setterSuffix;
            final String getterName = "(_" + foreignCol.getUncapitalisedJavaName() + ")";
            if ("".equals(result)) {
                result = setterName + getterName;
            } else {
                result = result + "." + setterName + getterName;
            }
        }
        return result;
    }

    /**
     * Returns RelationKeysCommaString. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (LOCAL:FOREIGN)
     * 
     * @return Generated string.
     */
    public String getRelationKeysCommaString() {
        final List localColumnList = getLocalColumnObjectList();
        String result = "";
        for (final Iterator ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = (Column) getLocalForeignColumnObjectMapping().get(localCol);
            final String localName = localCol.getName();
            final String foreignName = foreignCol.getName();
            if ("".equals(result)) {
                result = localName + ":" + foreignName;
            } else {
                result = result + ", " + localName + ":" + foreignName;
            }
        }
        return result;
    }

    /**
     * Returns RelationKeysCommaString for OneToOneRefferer. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (FOREIGN:LOCAL)
     * 
     * @return Generated string.
     */
    public String getRelationKeysCommaStringForOneToOneRefferer() {
        final List foreignColumnList = getForeignColumnObjectList();
        String result = "";
        for (final Iterator ite = foreignColumnList.iterator(); ite.hasNext();) {
            final Column foreignCol = (Column) ite.next();
            final Column localCol = (Column) getForeignLocalColumnObjectMapping().get(foreignCol);
            final String foreignName = foreignCol.getName();
            final String localName = localCol.getName();

            if ("".equals(result)) {
                result = foreignName + ":" + localName;
            } else {
                result = result + ", " + foreignName + ":" + localName;
            }
        }
        return result;
    }

    // ==========================================================================================
    //                                                                            toString Method
    //                                                                            ===============
    /**
     * String representation of the foreign key. This is an xml representation.
     *
     * @return string representation in xml
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("    <foreign-key foreignTable=\"").append(getForeignTableName()).append("\" name=\"").append(
                getName()).append("\">\n");

        for (int i = 0; i < _localColumns.size(); i++) {
            result.append("        <reference local=\"").append(_localColumns.get(i)).append("\" foreign=\"").append(
                    _foreignColumns.get(i)).append("\"/>\n");
        }
        result.append("    </foreign-key>\n");
        return result.toString();
    }


}