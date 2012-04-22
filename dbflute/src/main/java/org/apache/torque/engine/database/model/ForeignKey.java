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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfFixedConditionInvalidClassificationEmbeddedCommentException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.generate.column.DfColumnListToStringUtil;
import org.seasar.dbflute.logic.sql2entity.pmbean.DfPropertyTypePackageResolver;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfMultipleFKPropertyProperties;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationElement;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTop;
import org.seasar.dbflute.resource.DBFluteSystem;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;
import org.xml.sax.Attributes;

/**
 * A class for information about foreign keys of a table.
 * @author Modified by jflute
 */
public class ForeignKey {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    protected String _name; // constraint name (no change because it's used by templates)

    protected Table _localTable;
    protected String _foreignTableName; // may be user input (if additional FK)

    protected String _fixedCondition;
    protected String _fixedSuffix;
    protected boolean _fixedInline;
    protected String _comment;

    // -----------------------------------------------------
    //                                            Additional
    //                                            ----------
    protected boolean _additionalForeignKey;
    protected String _foreignPropertyNamePrefix;

    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    protected List<Column> _localColumnList; // lazy-loaded
    protected List<Column> _foreignColumnList; // lazy-loaded
    protected final List<String> _localColumnNameList = new ArrayList<String>(3);
    protected final List<String> _foreignColumnNameList = new ArrayList<String>(3);

    // -----------------------------------------------------
    //                                               Mapping
    //                                               -------
    protected final Map<String, String> _localForeignMap = StringKeyMap.createAsFlexibleOrdered();
    protected final Map<String, String> _foreignLocalMap = StringKeyMap.createAsFlexibleOrdered();
    protected final Map<String, String> _dynamicFixedConditionMap = DfCollectionUtil.newLinkedHashMap();

    // -----------------------------------------------------
    //                                    Lazy Determination
    //                                    ------------------
    protected Boolean _oneToOne;
    protected Boolean _bizOneToOne;
    protected Boolean _bizManyToOne;

    // ===================================================================================
    //                                                                       Load from XML
    //                                                                       =============
    /**
     * Imports foreign key from an XML specification
     * @param attrib the XML attributes
     */
    public void loadFromXML(Attributes attrib) {
        _foreignTableName = attrib.getValue("foreignTable");
        _name = attrib.getValue("name"); // constraint name for this FK
    }

    public void setForeignPropertyNamePrefix(String propertyNamePrefix) {
        _foreignPropertyNamePrefix = propertyNamePrefix;
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param attrib the xml attributes
     */
    public void addReference(Attributes attrib) {
        final String localColumn = attrib.getValue("local");
        final String foreignColumn = attrib.getValue("foreign");
        addReference(localColumn, foreignColumn);
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param local name of the local column
     * @param foreign name of the foreign column
     */
    public void addReference(String local, String foreign) {
        _localColumnNameList.add(local);
        _foreignColumnNameList.add(foreign);
        _localForeignMap.put(local, foreign);
        _foreignLocalMap.put(foreign, local);
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param localColumnNameList Name list  of the local column
     * @param foreignColumnNameList Name list of the foreign column
     */
    public void addReference(List<String> localColumnNameList, List<String> foreignColumnNameList) {
        _localColumnNameList.addAll(localColumnNameList);
        _foreignColumnNameList.addAll(foreignColumnNameList);
        for (int i = 0; i < localColumnNameList.size(); i++) {
            _localForeignMap.put(localColumnNameList.get(i), foreignColumnNameList.get(i));
            _foreignLocalMap.put(foreignColumnNameList.get(i), localColumnNameList.get(i));
        }
    }

    // ===================================================================================
    //                                                                               Table
    //                                                                               =====
    /**
     * Get foreign table.
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

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /**
     * Get the value of foreign property name.
     * @return Generated string.
     */
    public String getForeignPropertyName() {
        return getForeignPropertyName(false);
    }

    /**
     * Get the value of foreign property name.
     * @return Generated string.
     */
    public String getForeignJavaBeansRulePropertyName() {
        return getForeignPropertyName(true);
    }

    /**
     * Get the value of foreign property name.
     * @return Generated string.
     */
    public String getForeignJavaBeansRulePropertyNameInitCap() {
        return initCap(getForeignPropertyName(true));
    }

    /**
     * Get the value of foreign property name.
     * @param isJavaBeansRule Is it java-beans rule?
     * @return Generated string.
     */
    protected String getForeignPropertyName(boolean isJavaBeansRule) {
        final List<Column> localColumnList = getLocalColumnList();
        String name = "";
        if (hasFixedSuffix()) {
            name = getFixedSuffix();
        } else {
            final List<String> multipleFKColumnNameList = new ArrayList<String>();
            for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
                final Column col = (Column) ite.next();
                if (col.isMultipleFK()) { // the bug exists (refer to the method's source code)
                    multipleFKColumnNameList.add(col.getName());
                    name = name + col.getJavaName();
                }
            }
            if (name.trim().length() > 0) { // means multiple FK columns exist
                final String aliasName = getMultipleFKPropertyColumnAliasName(getTable().getName(),
                        multipleFKColumnNameList);
                if (aliasName != null && aliasName.trim().length() > 0) { // my young code
                    final String firstUpper = aliasName.substring(0, 1).toUpperCase();
                    if (aliasName.trim().length() == 1) {
                        name = "By" + firstUpper;
                    } else {
                        name = "By" + firstUpper + aliasName.substring(1, aliasName.length());
                    }
                } else {
                    name = "By" + name;
                }
            }
        }
        if (getForeignTable().getName().equals(getTable().getName())) {
            name = name + "Self";
        }
        if (isJavaBeansRule) {
            name = getForeignTable().getJavaBeansRulePropertyName() + name;
        } else {
            name = getForeignTable().getUncapitalisedJavaName() + name;
        }
        if (_foreignPropertyNamePrefix != null) {
            name = _foreignPropertyNamePrefix + name;
        }
        return name;
    }

    protected String getMultipleFKPropertyColumnAliasName(String tableName, List<String> multipleFKColumnNameList) {
        final DfMultipleFKPropertyProperties prop = DfBuildProperties.getInstance().getMultipleFKPropertyProperties();
        final String columnAliasName = prop.getMultipleFKPropertyColumnAliasName(getTable().getName(),
                multipleFKColumnNameList);
        return columnAliasName;
    }

    public String getForeignPropertyNameInitCap() {
        final String foreignPropertyName = getForeignPropertyName();
        return foreignPropertyName.substring(0, 1).toUpperCase() + foreignPropertyName.substring(1);
    }

    // -----------------------------------------------------
    //                                       Foreign Reverse
    //                                       ---------------
    public String getForeignReverseRelationPropertyName() {
        if (canBeReferrer()) {
            if (isOneToOne()) {
                return getReferrerJavaBeansRulePropertyNameAsOne();
            } else {
                return getReferrerJavaBeansRulePropertyName();
            }
        } else {
            return null;
        }
    }

    public String getForeignReverseRelationPropertyNameArg() {
        final String propertyName = getForeignReverseRelationPropertyName();
        return propertyName != null ? "\"" + propertyName + "\"" : "null";
    }

    public String getForeignReverseRelationPropertyNameInitCap() {
        if (canBeReferrer()) {
            if (isOneToOne()) {
                return getReferrerJavaBeansRulePropertyNameAsOneInitCap();
            } else {
                return getReferrerJavaBeansRulePropertyNameInitCap();
            }
        } else {
            return null;
        }
    }

    public String getForeignReverseRelationPropertyNameInitCapArg() {
        final String propertyName = getForeignReverseRelationPropertyNameInitCap();
        return propertyName != null ? "\"" + propertyName + "\"" : "null";
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getReferrerPropertyName() {
        return getReferrerPropertyName(false);
    }

    public String getReferrerJavaBeansRulePropertyName() {
        return getReferrerPropertyName(true);
    }

    public String getReferrerJavaBeansRulePropertyNameInitCap() {
        final String referrerPropertyName = getReferrerPropertyName(true);
        return initCap(referrerPropertyName);
    }

    public String getReferrerPropertyName(boolean isJavaBeansRule) {
        final List<Column> localColumnList = getLocalColumnList();

        final List<String> columnNameList = new ArrayList<String>();

        String result = "";
        if (hasFixedSuffix()) {
            result = getFixedSuffix();
        } else {
            for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
                final Column col = (Column) ite.next();

                if (col.isMultipleFK()) {
                    columnNameList.add(col.getName());
                    result = result + col.getJavaName();
                }
            }
            if (result.trim().length() != 0) { // isMultipleFK()==true
                final String aliasName = getMultipleFKPropertyColumnAliasName(getForeignTable().getName(),
                        columnNameList);
                if (aliasName != null && aliasName.trim().length() != 0) {
                    final String firstUpper = aliasName.substring(0, 1).toUpperCase();
                    if (aliasName.trim().length() == 1) {
                        result = "By" + firstUpper;
                    } else {
                        result = "By" + firstUpper + aliasName.substring(1, aliasName.length());
                    }
                } else {
                    result = "By" + result;
                }
            }
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

    public String getReferrerPropertyNameAsOne() {
        return getReferrerPropertyNameAsOne(false);
    }

    public String getReferrerPropertyNameAsOneInitCap() {
        return initCap(getReferrerPropertyNameAsOne());
    }

    public String getReferrerPropertyNameInitCapAsOne() {
        return getReferrerPropertyNameAsOneInitCap();
    }

    public String getReferrerJavaBeansRulePropertyNameAsOne() {
        return getReferrerPropertyNameAsOne(true);
    }

    public String getReferrerJavaBeansRulePropertyNameAsOneInitCap() {
        return initCap(getReferrerPropertyNameAsOne(true));
    }

    protected String getReferrerPropertyNameAsOne(boolean isJavaBeansRule) {
        final List<Column> localColumnList = getLocalColumnList();

        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
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

    public String getReferrerPropertyNameInitCap() {
        final String referrerPropertyName = getReferrerPropertyName();
        return referrerPropertyName.substring(0, 1).toUpperCase() + referrerPropertyName.substring(1);
    }

    // -----------------------------------------------------
    //                                      Referrer Reverse
    //                                      ----------------
    public String getReferrerReverseRelationPropertyName() {
        return getForeignJavaBeansRulePropertyName();
    }

    public String getReferrerReverseRelationPropertyNameArg() {
        final String propertyName = getForeignJavaBeansRulePropertyName();
        return "\"" + propertyName + "\"";
    }

    // ===================================================================================
    //                                                                              Column
    //                                                                              ======
    // -----------------------------------------------------
    //                                                 Local
    //                                                 -----
    public List<Column> getLocalColumnList() {
        if (_localColumnList != null) {
            return _localColumnList;
        }
        final List<String> columnList = getLocalColumnNameList();
        if (columnList == null || columnList.isEmpty()) {
            String msg = "The list of local column is null or empty." + columnList;
            throw new IllegalStateException(msg);
        }
        final List<Column> resultList = new ArrayList<Column>();
        for (final Iterator<String> ite = columnList.iterator(); ite.hasNext();) {
            final String name = (String) ite.next();
            final Column col = getTable().getColumn(name);
            if (col == null) {
                String msg = "The columnName is not existing at the table: ";
                msg = msg + "columnName=" + name + " tableName=" + getTable().getName();
                throw new IllegalStateException(msg);
            }
            resultList.add(col);
        }
        _localColumnList = resultList;
        return _localColumnList;
    }

    public List<String> getLocalColumnNameList() {
        return _localColumnNameList;
    }

    public List<String> getLocalColumnJavaNameList() {
        final List<String> resultList = new ArrayList<String>();
        final List<Column> localColumnList = getLocalColumnList();
        for (Column column : localColumnList) {
            resultList.add(column.getJavaName());
        }
        return resultList;
    }

    public Column getLocalColumnAsOne() {
        return getTable().getColumn(getLocalColumnNameAsOne());
    }

    public String getLocalColumnNameAsOne() {
        final List<String> columnNameList = getLocalColumnNameList();
        if (columnNameList.size() != 1) {
            String msg = "This method is for only-one foreign-key:";
            msg = msg + " getLocalColumnNameList().size()=" + columnNameList.size();
            msg = msg + " baseTable=" + getTable().getName() + " foreignTable=" + getForeignTable().getName();
            throw new IllegalStateException(msg);
        }
        return columnNameList.get(0);
    }

    public String getLocalColumnJavaNameAsOne() {
        return getLocalColumnAsOne().getJavaName();
    }

    public Column getLocalColumnByForeignColumn(Column foreignColumn) {
        final String localColumnName = getForeignLocalMapping().get(foreignColumn.getName());
        return getTable().getColumn(localColumnName);
    }

    public boolean hasLocalColumnExceptPrimaryKey() {
        final List<Column> localColumnList = getLocalColumnList();
        for (Column column : localColumnList) {
            if (!column.isPrimaryKey()) {
                return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------
    //                                       Foreign Element
    //                                       ---------------
    public List<Column> getForeignColumnList() {
        if (_foreignColumnList != null) {
            return _foreignColumnList;
        }
        final Table foreignTable = getTable().getDatabase().getTable(getForeignTableName());
        final List<String> columnList = getForeignColumnNameList();
        if (columnList == null || columnList.isEmpty()) {
            String msg = "The list of foreign column is null or empty." + columnList;
            throw new IllegalStateException(msg);
        }
        final List<Column> resultList = new ArrayList<Column>();
        for (Iterator<String> ite = columnList.iterator(); ite.hasNext();) {
            final String name = (String) ite.next();
            final Column foreignCol = foreignTable.getColumn(name);
            resultList.add(foreignCol);
        }
        _foreignColumnList = resultList;
        return _foreignColumnList;
    }

    public List<String> getForeignColumnNameList() {
        return _foreignColumnNameList;
    }

    public Column getForeignColumnAsOne() {
        return getForeignTable().getColumn(getForeignColumnNameAsOne());
    }

    public String getForeignColumnNameAsOne() {
        final List<String> columnNameList = getForeignColumnNameList();
        if (columnNameList.size() != 1) {
            String msg = "This method is for only-one foreign-key:";
            msg = msg + " getForeignColumnNameList().size()=" + columnNameList.size();
            msg = msg + " baseTable=" + getTable().getName();
            msg = msg + " foreignTable=" + getForeignTable().getName();
            throw new IllegalStateException(msg);
        }
        return columnNameList.get(0);
    }

    public String getForeignColumnJavaNameAsOne() {
        final String columnName = getForeignColumnNameAsOne();
        final Table foreignTable = getForeignTable();
        return foreignTable.getColumn(columnName).getJavaName();
    }

    public Column getForeignColumnByLocalColumn(Column localColumn) {
        final String foreignColumnName = getLocalForeignMapping().get(localColumn.getName());
        return getForeignTable().getColumn(foreignColumnName);
    }

    // ===================================================================================
    //                                                                      Column Mapping
    //                                                                      ==============
    public Map<String, String> getLocalForeignMapping() {
        return _localForeignMap;
    }

    public Map<String, String> getForeignLocalMapping() {
        return _foreignLocalMap;
    }

    // ===================================================================================
    //                                                                   Column Expression
    //                                                                   =================
    // -----------------------------------------------------
    //                                                 Local
    //                                                 -----
    /**
     * Returns a comma delimited string of local column names.
     * @return Generated string.
     */
    public String getLocalColumnNameCommaString() { // same as 
        return DfColumnListToStringUtil.getColumnNameCommaString(getLocalColumnList());
    }

    /**
     * Returns a comma delimited string of local column getters. [getRcvlcqNo(), getSprlptTp()]
     * @return Generated string.
     */
    public String getLocalColumnGetterCommaString() {
        return DfColumnListToStringUtil.getColumnGetterCommaString(getLocalColumnList());
    }

    /**
     * Returns a local column name of first element.
     * @return Selected string.
     */
    public String getFirstLocalColumnName() {
        return getLocalColumnNameList().get(0);
    }

    // -----------------------------------------------------
    //                                               Foreign
    //                                               -------
    /**
     * Returns a comma delimited string of foreign column names.
     * @return Generated string.
     */
    public String getForeignColumnNameCommaString() {
        return DfColumnListToStringUtil.getColumnNameCommaString(getForeignColumnList());
    }

    /**
     * Returns a comma delimited string of foreign column getters. [getRcvlcqNo(), getSprlptTp()]
     * @return Generated string.
     */
    public String getForeignColumnGetterCommaString() {
        return DfColumnListToStringUtil.getColumnGetterCommaString(getForeignColumnList());
    }

    /**
     * Returns a foreign column name of first element.
     * @return Selected string.
     */
    public String getFirstForeignColumnName() {
        return getForeignColumnNameList().get(0);
    }

    // -----------------------------------------------------
    //                                          Setup String
    //                                          ------------
    /**
     * Returns ForeignTable-BeanSetupString. [setRcvlcqNo_Suffix(getRcvlcqNo()).setSprlptTp_Suffix(getSprlptTp())]
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getForeignTableBeanSetupString(String setterSuffix) {
        return getForeignTableBeanSetupString(setterSuffix, "set");
    }

    public String getForeignTableBeanSetupString(String setterSuffix, String setterPrefix) {
        final List<Column> localColumnList = getLocalColumnList();
        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = getForeignColumnByLocalColumn(localCol);
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
     * Returns ChildrenTable-BeanSetupString. [setRcvlcqNo_Suffix(getRcvlcqNo()).setSprlptTp_Suffix(getSprlptTp());]
     * About ForeginKey that Table#getReferrer() returns, Local means children.
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getChildrenTableBeanSetupString(String setterSuffix) {
        return getChildrenTableBeanSetupString(setterSuffix, "set");
    }

    public String getChildrenTableBeanSetupString(String setterSuffix, String setterPrefix) {
        List<Column> localColumnList = getLocalColumnList();
        String result = "";

        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = getForeignColumnByLocalColumn(localCol);
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

    // -----------------------------------------------------
    //                                         for S2Dao.NET
    //                                         -------------
    /**
     * Returns RelationKeysCommaString. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (LOCAL:FOREIGN) <br />
     * (for s2dao)
     * @return Generated string.
     */
    public String getRelationKeysCommaString() { // for S2Dao.NET (only used for C#)
        final List<Column> localColumnList = getLocalColumnList();
        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = getForeignColumnByLocalColumn(localCol);
            final String localName = localCol.getColumnSqlName();
            final String foreignName = foreignCol.getColumnSqlName();
            if ("".equals(result)) {
                result = localName + ":" + foreignName;
            } else {
                result = result + ", " + localName + ":" + foreignName;
            }
        }
        return result;
    }

    /**
     * Returns RelationKeysCommaString for OneToOneReferrer. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (FOREIGN:LOCAL) <br />
     * (for s2dao)
     * @return Generated string.
     */
    public String getRelationKeysCommaStringForOneToOneReferrer() { // for S2Dao.NET (only used for C#)
        final List<Column> foreignColumnList = getForeignColumnList();
        String result = "";
        for (final Iterator<Column> ite = foreignColumnList.iterator(); ite.hasNext();) {
            final Column foreignCol = (Column) ite.next();
            final Column localCol = getLocalColumnByForeignColumn(foreignCol);
            final String foreignName = foreignCol.getColumnSqlName();
            final String localName = localCol.getColumnSqlName();

            if ("".equals(result)) {
                result = foreignName + ":" + localName;
            } else {
                result = result + ", " + foreignName + ":" + localName;
            }
        }
        return result;
    }

    // -----------------------------------------------------
    //                                            for S2JDBC
    //                                            ----------
    public String getReferrerPropertyNameAsOneS2Jdbc() { // for S2JDBC
        final List<Column> localColumnList = getLocalColumnList();

        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
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
        return getTable().getUncapitalisedJavaName() + result;
    }

    // ===================================================================================
    //                                                                     Fixed Condition
    //                                                                     ===============
    public boolean hasFixedCondition() {
        return _fixedCondition != null && _fixedCondition.trim().length() > 0;
    }

    public boolean hasFixedSuffix() {
        return _fixedSuffix != null && _fixedSuffix.trim().length() > 0;
    }

    public boolean hasDynamicFixedCondition() {
        analyzeDynamicFixedConditionIfNeeds();
        return hasFixedCondition() && !_dynamicFixedConditionMap.isEmpty();
    }

    protected void analyzeDynamicFixedConditionIfNeeds() {
        if (!_dynamicFixedConditionMap.isEmpty()) {
            return; // already initialized
        }
        if (!hasFixedCondition() || !_fixedCondition.contains("/*") || !_fixedCondition.contains("*/")) {
            return; // No fixedCondition or No dynamicFixedCondition
        }
        final Map<String, String> fixedConditionReplacementMap = new LinkedHashMap<String, String>();
        String currentString = _fixedCondition;
        while (true) {
            if (currentString == null || currentString.trim().length() == 0) {
                break;
            }
            final int startIndex = currentString.indexOf("/*");
            if (startIndex < 0) {
                break;
            }
            final int endIndex = currentString.indexOf("*/");
            if (endIndex < 0) {
                break;
            }
            if (startIndex >= endIndex) {
                break;
            }
            final String peace = currentString.substring(startIndex + "/*".length(), endIndex);

            // Modify the variable 'currentString' for next loop!
            currentString = currentString.substring(endIndex + "*/".length());

            final int typeStartIndex = peace.indexOf("(");
            if (typeStartIndex < 0) {
                continue;
            }
            final int typeEndIndex = peace.indexOf(")");
            if (typeEndIndex < 0) {
                continue;
            }
            if (typeStartIndex >= typeEndIndex) {
                continue;
            }

            String parameterType = peace.substring(typeStartIndex + "(".length(), typeEndIndex);
            if (peace.startsWith("$cls")) { // embedded classification
                // Not Dynamic (Embedded)
                final String code = extractFixedConditionEmbeddedCommentClassification(peace, parameterType);
                final String expression = "/*" + peace + "*/";

                // Remove test value because of hard code.
                fixedConditionReplacementMap.put(expression + "null", expression);
                fixedConditionReplacementMap.put(expression + "Null", expression);
                fixedConditionReplacementMap.put(expression + "NULL", expression);

                fixedConditionReplacementMap.put(expression, code);
            } else { // dynamic binding
                parameterType = filterDynamicFixedConditionParameterType(parameterType);
                final String parameterName = peace.substring(0, typeStartIndex);
                _dynamicFixedConditionMap.put(parameterName, parameterType);
                final String parameterMapName = "parameterMap" + getForeignPropertyNameInitCap();
                final String parameterBase = "$$locationBase$$." + parameterMapName;
                final String bindVariableExp = "/*" + parameterBase + "." + parameterName + "*/";
                fixedConditionReplacementMap.put("/*" + peace + "*/", bindVariableExp);

                // e.g. /*IF $$parameterBase$$.foo != null*/
                final String parameterBaseKey = "$$parameterBase$$";
                if (!fixedConditionReplacementMap.containsKey(parameterBaseKey)) {
                    fixedConditionReplacementMap.put(parameterBaseKey, parameterBase);
                }
            }
        }
        if (fixedConditionReplacementMap.isEmpty()) {
            return;
        }
        final Set<Entry<String, String>> replaceSet = fixedConditionReplacementMap.entrySet();
        for (Entry<String, String> replaceEntry : replaceSet) {
            final String key = replaceEntry.getKey();
            final String value = replaceEntry.getValue();
            _fixedCondition = replace(_fixedCondition, key, value);
        }
    }

    protected String extractFixedConditionEmbeddedCommentClassification(String peace, String parameterType) {
        if (!parameterType.contains(".")) {
            String msg = "The classification expression should be 'classificationName.elementName':";
            msg = msg + " expression=" + parameterType + " embeddedComment=" + peace;
            throw new DfFixedConditionInvalidClassificationEmbeddedCommentException(msg);
        }
        final String classificationName = parameterType.substring(0, parameterType.indexOf("."));
        final String elementName = parameterType.substring(parameterType.indexOf(".") + ".".length());
        final Map<String, DfClassificationTop> topMap = getClassificationProperties().getClassificationTopMap();
        final DfClassificationTop classificationTop = topMap.get(classificationName);
        if (classificationTop == null) {
            String msg = "The classification name was NOT FOUND:";
            msg = msg + " classificationName=" + classificationName + " embeddedComment=" + peace;
            msg = msg + " classificationList=" + topMap.keySet();
            throw new DfFixedConditionInvalidClassificationEmbeddedCommentException(msg);
        }
        final List<DfClassificationElement> elementList = classificationTop.getClassificationElementList();
        String code = null;
        for (DfClassificationElement element : elementList) {
            final String name = element.getName();
            if (elementName.equals(name)) {
                code = element.getCode();
                break;
            }
        }
        if (code == null) {
            String msg = "The classification element name was NOT FOUND:";
            msg = msg + " elementName=" + elementName + " embeddedComment=" + peace;
            msg = msg + " classificationTop=" + classificationTop;
            throw new DfFixedConditionInvalidClassificationEmbeddedCommentException(msg);
        }
        final String codeType = classificationTop.getCodeType();
        if (codeType == null || !codeType.equals(DfClassificationTop.CODE_TYPE_NUMBER)) {
            code = "'" + code + "'";
        }
        return code;
    }

    protected String filterDynamicFixedConditionParameterType(String parameterType) {
        final DfPropertyTypePackageResolver packageResolver = new DfPropertyTypePackageResolver();
        return packageResolver.resolvePackageName(parameterType);
    }

    public String getDynamicFixedConditionArgs() {
        return buildDynamicFixedConditionArgs(false);
    }

    public String getDynamicFixedConditionFinalArgs() {
        return buildDynamicFixedConditionArgs(true);
    }

    protected String buildDynamicFixedConditionArgs(boolean finalArg) {
        final Set<String> parameterNameSet = _dynamicFixedConditionMap.keySet();
        final StringBuilder sb = new StringBuilder();
        for (String parameterName : parameterNameSet) {
            final String paramterType = _dynamicFixedConditionMap.get(parameterName);
            if (sb.length() > 0) {
                sb.append(", ");
            }
            if (finalArg) {
                sb.append("final ");
            }
            sb.append(paramterType).append(" ").append(parameterName);
        }
        return sb.toString();
    }

    public String getDynamicFixedConditionVariables() {
        final Set<String> parameterNameSet = _dynamicFixedConditionMap.keySet();
        final StringBuilder sb = new StringBuilder();
        for (String parameterName : parameterNameSet) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(parameterName);
        }
        return sb.toString();
    }

    public String getDynamicFixedConditionParameterMapSetup() {
        final Set<Entry<String, String>> entrySet = _dynamicFixedConditionMap.entrySet();
        final StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : entrySet) {
            final String parameterName = entry.getKey();
            final String parameterType = entry.getValue();
            sb.append("parameterMap.put(\"").append(parameterName).append("\", ");
            if (java.util.Date.class.getName().equals(parameterType)) {
                sb.append("fCTPD(").append(parameterName).append(")");
            } else {
                sb.append(parameterName);
            }
            sb.append(");");
        }
        return sb.toString();
    }

    public String getDynamicFixedConditionArgsJavaDocString() {
        final Set<Entry<String, String>> entrySet = _dynamicFixedConditionMap.entrySet();
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Entry<String, String> entry : entrySet) {
            final String parameterName = entry.getKey();
            if (count > 0) {
                sb.append("     * ");
            }
            sb.append("@param ").append(parameterName).append(" ");
            sb.append("The bind parameter of fixed condition for ").append(parameterName).append(". (NotNull)");
            sb.append(getBasicProperties().getSourceCodeLineSeparator());
            ++count;
        }
        final String result = Srl.rtrim(sb.toString()); // trim last line separator
        return result;
    }

    // ===================================================================================
    //                                                                          Class Name
    //                                                                          ==========
    // -----------------------------------------------------
    //                                    Foreign Class Name
    //                                    ------------------
    public String getForeignTableExtendedEntityClassName() {
        return getForeignTable().getExtendedEntityClassName();
    }

    public String getForeignTableDBMetaClassName() {
        return getForeignTable().getDBMetaClassName();
    }

    public String getForeignTableExtendedConditionBeanClassName() {
        return getForeignTable().getExtendedConditionBeanClassName();
    }

    public String getForeignTableExtendedConditionQueryClassName() {
        return getForeignTable().getExtendedConditionQueryClassName();
    }

    public String getForeignTableNestSelectSetupperClassName() {
        return getForeignTable().getNestSelectSetupperClassName();
    }

    public String getForeignTableNestSelectSetupperTerminalClassName() {
        return getForeignTable().getNestSelectSetupperTerminalClassName();
    }

    public String getForeignTableExtendedSimpleDtoClassName() {
        return getForeignTable().getSimpleDtoExtendedDtoClassName();
    }

    // -----------------------------------------------------
    //                                   Referrer Class Name
    //                                   -------------------
    public String getReferrerTableExtendedEntityClassName() {
        return getTable().getExtendedEntityClassName();
    }

    public String getReferrerTableExtendedBehaviorClassName() {
        return getTable().getExtendedBehaviorClassName();
    }

    public String getReferrerTableDBMetaClassName() {
        return getTable().getDBMetaClassName();
    }

    public String getReferrerTableExtendedConditionBeanClassName() {
        return getTable().getExtendedConditionBeanClassName();
    }

    public String getReferrerTableExtendedConditionQueryClassName() {
        return getTable().getExtendedConditionQueryClassName();
    }

    public String getReferrerTableNestSelectSetupperClassName() {
        return getTable().getNestSelectSetupperClassName();
    }

    public String getReferrerTableNestSelectSetupperTerminalClassName() {
        return getTable().getNestSelectSetupperTerminalClassName();
    }

    public String getReferrerTableExtendedSimpleDtoClassName() {
        return getTable().getSimpleDtoExtendedDtoClassName();
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Is this relation 'one-to-one'?
     * @return The determination, true or false.
     */
    public boolean isOneToOne() {
        if (_oneToOne == null) {
            _oneToOne = false;
            final List<Column> localColumnList = getLocalColumnList();
            final List<Column> localPrimaryColumnList = getTable().getPrimaryKey();
            if (localColumnList.equals(localPrimaryColumnList)) {
                _oneToOne = true;
            } else {
                final List<Unique> uniqueList = getTable().getUniqueList();
                for (final Unique unique : uniqueList) {
                    if (unique.hasSameColumnSet(localColumnList)) {
                        _oneToOne = true;
                    }
                }
            }
        }
        return _oneToOne;
    }

    public boolean isBizOneToOne() {
        if (_bizOneToOne == null) {
            _bizOneToOne = isOneToOne() && hasFixedCondition();
        }
        return _bizOneToOne;
    }

    public boolean isBizManyToOne() {
        if (_bizManyToOne == null) {
            _bizManyToOne = !isOneToOne() && hasFixedCondition();
        }
        return _bizManyToOne;
    }

    public boolean isSimpleKeyFK() {
        return _localColumnNameList.size() == 1;
    }

    public boolean isCompoundFK() {
        return _localColumnNameList.size() > 1;
    }

    public boolean isSelfReference() {
        return _localTable.getName().equals(_foreignTableName);
    }

    public boolean canBeReferrer() {
        if (hasFixedCondition()) {
            return false;
        }
        return isForeignColumnPrimaryKey() || isForeignColumnUnique();

        // *reference to unique key is unsupported basically
        //  (a-little-supported)
    }

    /**
     * Are all local columns primary-key?
     * @return The determination, true or false.
     */
    public boolean isLocalColumnPrimaryKey() {
        return isColumnPrimaryKey(getLocalColumnList());
    }

    /**
     * Are all foreign columns primary-key? <br />
     * Basically true. If biz-one-to-one and unique key, false.
     * @return The determination, true or false.
     */
    public boolean isForeignColumnPrimaryKey() {
        return isColumnPrimaryKey(getForeignColumnList());
    }

    /**
     * Are all foreign columns unique-key? <br />
     * @return The determination, true or false.
     */
    public boolean isForeignColumnUnique() {
        return isColumnUnique(getForeignColumnList());
    }

    protected boolean isColumnPrimaryKey(List<Column> columnList) {
        for (Column column : columnList) {
            if (!column.isPrimaryKey()) {
                return false;
            }
        }
        return true;
    }

    protected boolean isColumnUnique(List<Column> columnList) {
        for (Column column : columnList) {
            if (!column.isUnique()) {
                return false;
            }
        }
        return true;
    }

    // ===================================================================================
    //                                                                             Display
    //                                                                             =======
    // -----------------------------------------------------
    //                                               Foreign
    //                                               -------
    public String getForeignDispForJavaDoc() {
        return doGetForeignDispForJavaDoc("    ");
    }

    public String getForeignDispForJavaDocNest() {
        return doGetForeignDispForJavaDoc("        ");
    }

    public String doGetForeignDispForJavaDoc(String indent) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getForeignSimpleDisp()).append(".");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_comment)) {
            final String comment = resolveCommentForJavaDoc(_comment, indent);
            sb.append(" <br />").append(ln()).append(indent).append(" * ").append(comment);
        }
        return sb.toString();
    }

    public String getForeignSimpleDisp() {
        final StringBuilder sb = new StringBuilder();
        final Table foreignTable = getForeignTable();
        sb.append(foreignTable.getAliasExpression());
        sb.append(foreignTable.getName());
        sb.append(" by my ").append(getLocalColumnNameCommaString());
        sb.append(", named '").append(getForeignJavaBeansRulePropertyName()).append("'");
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                         ReferrerAsOne
    //                                         -------------
    public String getReferrerDispAsOneForJavaDoc() {
        return doGetReferrerDispAsOneForJavaDoc("    ");
    }

    public String getReferrerDispAsOneForJavaDocNest() {
        return doGetReferrerDispAsOneForJavaDoc("        ");
    }

    public String doGetReferrerDispAsOneForJavaDoc(String indent) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getReferrerSimpleDispAsOne()).append(".");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_comment)) {
            final String comment = resolveCommentForJavaDoc(_comment, indent);
            sb.append(" <br />").append(ln()).append(indent).append(" * ").append(comment);
        }
        return sb.toString();
    }

    public String getReferrerSimpleDispAsOne() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getTable().getAliasExpression());
        sb.append(getTable().getName());
        sb.append(" by your ").append(getLocalColumnNameCommaString());
        sb.append(", named '").append(getReferrerJavaBeansRulePropertyNameAsOne()).append("'");
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                              Referrer
    //                                              --------
    public String getReferrerDispForJavaDoc() {
        return doGetReferrerDispForJavaDoc("    ");
    }

    public String getReferrerDispForJavaDocNest() {
        return doGetReferrerDispForJavaDoc("        ");
    }

    public String doGetReferrerDispForJavaDoc(String indent) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getReferrerSimpleDisp()).append(".");
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_comment)) {
            final String comment = resolveCommentForJavaDoc(_comment, indent);
            sb.append(" <br />").append(ln()).append(indent).append(" * ").append(comment);
        }
        return sb.toString();
    }

    public String getReferrerSimpleDisp() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getTable().getAliasExpression());
        sb.append(getTable().getName());
        sb.append(" by your ").append(getLocalColumnNameCommaString());
        sb.append(", named '").append(getReferrerJavaBeansRulePropertyName()).append("'");
        return sb.toString();
    }

    // -----------------------------------------------------
    //                                         Common Helper
    //                                         -------------
    protected String resolveCommentForJavaDoc(String comment, String indent) {
        return getDocumentProperties().resolveTextForJavaDoc(comment, indent);
    }

    // ===================================================================================
    //                                                                 Implicit Conversion
    //                                                                 ===================
    public boolean canImplicitConversion() {
        if (!isSimpleKeyFK()) {
            return false;
        }
        final Column localColumn = getLocalColumnAsOne();
        final Column foreignColumn = getForeignColumnAsOne();
        // the scope is in String and Number types
        if ((localColumn.isJavaNativeStringObject() || localColumn.isJavaNativeNumberObject())
                && (foreignColumn.isJavaNativeStringObject() || foreignColumn.isJavaNativeNumberObject())) {
            return !localColumn.getJdbcType().equals(foreignColumn.getJdbcType());
        }
        return false;
    }

    // /- - - - - - - - - - - - - -
    // attention: local is referrer
    // - - - - - - - - - -/

    public boolean isConvertToReferrerByToString() {
        return getLocalColumnAsOne().isJavaNativeStringObject();
    }

    public boolean isConvertToReferrerByConstructor() {
        return getLocalColumnAsOne().isJavaNativeBigDecimal();
    }

    public boolean isConvertToReferrerByValueOf() {
        return getLocalColumnAsOne().isJavaNativeValueOfAbleObject();
    }

    // ===================================================================================
    //                                                              Relational Null Object
    //                                                              ======================
    public String getRelationalNullObjectProviderForeignExp() {
        return doGetRelationalNullObjectProviderExp(getForeignTable(), getLocalColumnGetterCommaString());
    }

    public String getRelationalNullObjectProviderReferrerExp() {
        return doGetRelationalNullObjectProviderExp(getTable(), getForeignColumnGetterCommaString());
    }

    protected String doGetRelationalNullObjectProviderExp(Table table, String getterCommaString) {
        final String exp = table.getRelationalNullObjectProviderForeignExp();
        final String resolvedExp = replace(exp, "$$PrimaryKey$$", getterCommaString);
        return resolvedExp;
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

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    // ===================================================================================
    //                                                                          Simple DTO
    //                                                                          ==========
    public String getSimpleDtoForeignVariableName() {
        return getProperties().getSimpleDtoProperties().buildFieldName(getForeignPropertyNameInitCap());
    }

    public String getSimpleDtoReferrerAsOneVariableName() {
        return getProperties().getSimpleDtoProperties().buildFieldName(getReferrerPropertyNameAsOneInitCap());
    }

    public String getSimpleDtoReferrerVariableName() {
        return getProperties().getSimpleDtoProperties().buildFieldName(getReferrerPropertyNameInitCap());
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String initCap(String str) {
        return Srl.initCap(str);
    }

    protected String initUncap(String str) {
        return Srl.initUncap(str);
    }

    protected String ln() {
        return DBFluteSystem.getBasicLn();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * String representation of the foreign key. This is an xml representation.
     * @return string representation in xml
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("    <foreign-key");
        sb.append(" foreignTable=\"").append(getForeignTableName()).append("\"");
        sb.append(" name=\"").append(getName()).append("\"");
        sb.append(">\n");

        for (int i = 0; i < _localColumnNameList.size(); i++) {
            sb.append("        <reference local=\"").append(_localColumnNameList.get(i));
            sb.append("\" foreign=\"").append(_foreignColumnNameList.get(i)).append("\"/>\n");
        }
        sb.append("    </foreign-key>");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Returns the name attribute.
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the name attribute.
     * @param name the name
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * Get the foreignTableName of the FK
     * @return the name of the foreign table
     */
    public String getForeignTableName() {
        return _foreignTableName;
    }

    /**
     * Set the foreignTableName of the FK
     * @param tableName the name of the foreign table
     */
    public void setForeignTableName(String tableName) {
        _foreignTableName = tableName;
    }

    /**
     * Set the base Table of the foreign key
     * @param baseTable the table
     */
    public void setTable(Table baseTable) {
        _localTable = baseTable;
    }

    /**
     * Get the base Table of the foreign key
     * @return the base table
     */
    public Table getTable() {
        return _localTable;
    }

    public boolean isAdditionalForeignKey() {
        return _additionalForeignKey;
    }

    public void setAdditionalForeignKey(boolean additionalForeignKey) {
        _additionalForeignKey = additionalForeignKey;
    }

    public String getFixedCondition() {
        return _fixedCondition;
    }

    public void setFixedCondition(String fixedCondition) {
        this._fixedCondition = fixedCondition;
    }

    public String getFixedSuffix() {
        return _fixedSuffix;
    }

    public void setFixedSuffix(String fixedSuffix) {
        this._fixedSuffix = fixedSuffix;
    }

    public boolean isFixedInline() {
        return _fixedInline;
    }

    public void setFixedInline(boolean fixedInline) {
        this._fixedInline = fixedInline;
    }

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        this._comment = comment;
    }
}