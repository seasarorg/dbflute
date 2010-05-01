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
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfFixedConditionInvalidClassificationEmbeddedCommentException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.logic.pkgresolver.DfStandardApiPackageResolver;
import org.seasar.dbflute.logic.various.DfColumnListToStringUtil;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfClassificationProperties;
import org.seasar.dbflute.properties.DfMultipleFKPropertyProperties;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationElement;
import org.seasar.dbflute.properties.assistant.classification.DfClassificationTop;
import org.seasar.dbflute.util.Srl;
import org.xml.sax.Attributes;

/**
 * A class for information about foreign keys of a table.
 * @author Modified by jflute
 */
public class ForeignKey {

    private static final Log _log = LogFactory.getLog(ForeignKey.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _name;

    private Table _localTable;
    private String _foreignTableName;

    private List<String> _localColumns = new ArrayList<String>(3);
    private List<String> _foreignColumns = new ArrayList<String>(3);

    protected Map<String, String> _localForeignMap = StringKeyMap.createAsFlexibleOrdered();
    protected Map<String, String> _foreignLocalMap = StringKeyMap.createAsFlexibleOrdered();

    private String _foreignPropertyNamePrefix;
    private boolean _additionalForeignKey;
    private String _fixedCondition;
    private String _fixedSuffix;
    private Map<String, String> _dynamicFixedConditionMap = new LinkedHashMap<String, String>();

    // ===================================================================================
    //                                                                                Load
    //                                                                                ====
    /**
     * Imports foreign key from an XML specification
     * @param attrib the XML attributes
     */
    public void loadFromXML(Attributes attrib) {
        _foreignTableName = attrib.getValue("foreignTable");
        _name = attrib.getValue("name");
    }

    public void setForeignPropertyNamePrefix(String propertyNamePrefix) {
        _foreignPropertyNamePrefix = propertyNamePrefix;
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param attrib the xml attributes
     */
    public void addReference(Attributes attrib) {
        addReference(attrib.getValue("local"), attrib.getValue("foreign"));
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param local name of the local column
     * @param foreign name of the foreign column
     */
    public void addReference(String local, String foreign) {
        _localColumns.add(local);
        _foreignColumns.add(foreign);
        _localForeignMap.put(local, foreign);
        _foreignLocalMap.put(foreign, local);
    }

    /**
     * Adds a new reference entry to the foreign key
     * @param localColumnNameList Name list  of the local column
     * @param foreignColumnNameList Name list of the foreign column
     */
    public void addReference(List<String> localColumnNameList, List<String> foreignColumnNameList) {
        _localColumns.addAll(localColumnNameList);
        _foreignColumns.addAll(foreignColumnNameList);
        for (int i = 0; i < localColumnNameList.size(); i++) {
            _localForeignMap.put(localColumnNameList.get(i), foreignColumnNameList.get(i));
            _foreignLocalMap.put(foreignColumnNameList.get(i), localColumnNameList.get(i));
        }
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

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Is this relation 'one-to-one'?
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

    public boolean isTwoOrMoreKeyFK() {
        return _localColumns.size() > 1;
    }

    public boolean isSelfReference() {
        return _localTable.getName().equals(_foreignTableName);
    }

    public boolean canBeReferrer() {
        return isForeignColumnsSameAsForeignTablePrimaryKeys() && !hasFixedCondition();
    }

    /**
     * @return Determination.
     */
    protected boolean isForeignColumnsSameAsForeignTablePrimaryKeys() {
        final List<String> foreginTablePrimaryKeyNameList = new ArrayList<String>();
        {
            final Table fkTable = _localTable.getDatabase().getTable(_foreignTableName);
            final List<Column> foreignTablePrimaryKeyList = fkTable.getPrimaryKey();
            for (Column column : foreignTablePrimaryKeyList) {
                foreginTablePrimaryKeyNameList.add(column.getName());
            }
        }
        if (foreginTablePrimaryKeyNameList.size() != _foreignColumns.size()) {
            return false;
        }
        for (String foreginTablePrimaryKeyName : foreginTablePrimaryKeyNameList) {
            boolean exists = false;
            for (String foreignColumn : _foreignColumns) {
                if (foreginTablePrimaryKeyName.equalsIgnoreCase(foreignColumn)) {
                    exists = true;
                }
            }
            if (!exists) {
                return false;
            }
        }
        return true;
    }

    // ===================================================================================
    //                                                     Get Columns & ColumnList Method
    //                                                     ===============================
    // -----------------------------------------------------
    //                                         Local Element
    //                                         -------------
    public List<String> getLocalColumns() {
        return _localColumns;
    }

    public List<Column> getLocalColumnList() {
        return getLocalColumnObjectList();
    }

    public Column getLocalColumnAsOne() {
        String localColumnNameAsOne = getLocalColumnNameAsOne();
        return getTable().getColumn(localColumnNameAsOne);
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

    public String getLocalColumnJavaNameAsOne() {
        final String columnName = getLocalColumnNameAsOne();
        final Table localTable = getTable();
        return localTable.getColumn(columnName).getJavaName();
    }

    public List<String> getLocalColumnJavaNameList() {
        List<String> resultList = new ArrayList<String>();
        List<Column> localColumnList = getLocalColumnList();
        for (Column column : localColumnList) {
            resultList.add(column.getJavaName());
        }
        return resultList;
    }

    public List<Column> getLocalColumnObjectList() {
        final List<String> columnList = getLocalColumns();
        if (columnList == null || columnList.isEmpty()) {
            String msg = "The localColumnList is null or empty." + columnList;
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
        return resultList;
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
        return getForeignColumnObjectList();
    }

    public List<String> getForeignColumnNameList() {
        return _foreignColumns;
    }

    public List<String> getForeignColumns() {// Old Style Name
        return _foreignColumns;
    }

    public Column getForeignColumnAsOne() {
        return getForeignTable().getColumn(getForeignColumnNameAsOne());
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
        for (Iterator<String> ite = columnList.iterator(); ite.hasNext();) {
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

    public Column getForeignColumnByLocalColumn(Column localColumn) {
        final String foreignColumnName = getLocalForeignMapping().get(localColumn.getName());
        return getForeignTable().getColumn(foreignColumnName);
    }

    // ==========================================================================================
    //                                                                  Get Column Mapping Method
    //                                                                  =========================
    public Map<String, String> getLocalForeignMapping() {
        return _localForeignMap;
    }

    public Map<String, String> getForeignLocalMapping() {
        return _foreignLocalMap;
    }

    // ==========================================================================================
    //                                                                     Generate String Method
    //                                                                     ======================
    /**
     * Returns a comma delimited string of local column names
     * @return Generated string.
     */
    public String getLocalColumnNames() {
        return Column.makeList(getLocalColumns());
    }

    /**
     * Returns a comma delimited string of foreign column names
     * @return Generated string.
     */
    public String getForeignColumnNames() {
        return Column.makeList(getForeignColumns());
    }

    /**
     * Returns first local column name.
     * @return Fisrt local column name.
     */
    public String getFirstLocalColumnName() {
        return getLocalColumns().get(0);
    }

    /**
     * Returns first local column name.
     * @return Fisrt local column name.
     */
    public String getFirstForeignColumnName() {
        return getForeignColumns().get(0);
    }

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
     * @param isJavaBeansRule Is java-beans rule.
     * @return Generated string.
     */
    protected String getForeignPropertyName(boolean isJavaBeansRule) {
        try {
            final List<Column> localColumnList = getLocalColumnObjectList();
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
                if (result.trim().length() != 0) {
                    final String aliasName = getMultipleFKPropertyColumnAliasName(getTable().getName(), columnNameList);
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

    protected String getMultipleFKPropertyColumnAliasName(String tableName, List<String> columnNameList) {
        final DfMultipleFKPropertyProperties prop = DfBuildProperties.getInstance().getMultipleFKPropertyProperties();
        final String columnAliasName = prop.getMultipleFKPropertyColumnAliasName(getTable().getName(), columnNameList);
        return columnAliasName;
    }

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
        final List<Column> localColumnList = getLocalColumnObjectList();

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
            if (result.trim().length() != 0) {// isMultipleFK()==true
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
        final List<Column> localColumnList = getLocalColumnObjectList();

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

    public String getForeignPropertyNameInitCap() {
        final String foreignPropertyName = getForeignPropertyName();
        return foreignPropertyName.substring(0, 1).toUpperCase() + foreignPropertyName.substring(1);
    }

    public String getReferrerPropertyNameInitCap() {
        final String referrerPropertyName = getReferrerPropertyName();
        return referrerPropertyName.substring(0, 1).toUpperCase() + referrerPropertyName.substring(1);
    }

    // For S2JDBC
    public String getReferrerPropertyNameAsOneS2Jdbc() {
        final List<Column> localColumnList = getLocalColumnObjectList();

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

    /**
     * Returns comma-string for local column name.
     * @return Generated string.
     */
    public String getLocalColumnNameCommaString() {
        return DfColumnListToStringUtil.getColumnNameCommaString(getLocalColumnList());
    }

    /**
     * Returns LocalColumn-Getter-CommaString.[getRcvlcqNo(), getSprlptTp()]
     * @return Generated string.
     */
    public String getLocalColumnGetterCommaString() {
        final List<Column> localColumnList = getLocalColumnObjectList();
        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
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
     * Returns ForeignTable-BeanSetupString. [setRcvlcqNo_Suffix(getRcvlcqNo()).setSprlptTp_Suffix(getSprlptTp())]
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getForeignTableBeanSetupString(String setterSuffix) {
        return getForeignTableBeanSetupString(setterSuffix, "set");
    }

    public String getForeignTableBeanSetupString(String setterSuffix, String setterPrefix) {
        final List<Column> localColumnList = getLocalColumnObjectList();
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
     * Abount ForeginKey that Table#getReferrer() returns, Local means children.
     * @param setterSuffix Setter suffix(_Equal and _IsNotNull and so on...).
     * @return Generated string.
     */
    public String getChildrenTableBeanSetupString(String setterSuffix) {
        return getChildrenTableBeanSetupString(setterSuffix, "set");
    }

    public String getChildrenTableBeanSetupString(String setterSuffix, String setterPrefix) {
        List<Column> localColumnList = getLocalColumnObjectList();
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

    /**
     * Returns RelationKeysCommaString. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (LOCAL:FOREIGN) <br />
     * (for s2dao)
     * @return Generated string.
     */
    public String getRelationKeysCommaString() {
        final List<Column> localColumnList = getLocalColumnObjectList();
        String result = "";
        for (final Iterator<Column> ite = localColumnList.iterator(); ite.hasNext();) {
            final Column localCol = (Column) ite.next();
            final Column foreignCol = getForeignColumnByLocalColumn(localCol);
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
     * Returns RelationKeysCommaString for OneToOneReferrer. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (FOREIGN:LOCAL) <br />
     * (for s2dao)
     * @return Generated string.
     */
    public String getRelationKeysCommaStringForOneToOneReferrer() {
        final List<Column> foreignColumnList = getForeignColumnObjectList();
        String result = "";
        for (final Iterator<Column> ite = foreignColumnList.iterator(); ite.hasNext();) {
            final Column foreignCol = (Column) ite.next();
            final Column localCol = getLocalColumnByForeignColumn(foreignCol);
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

    /**
     * Returns RelationKeysCommaString. [RECLCQ_NO:RECLCQ_NO, SPRLPT_TP:...] (FOREIGN:LOCAL) <br />
     * (for s2dao)
     * @return Generated string.
     */
    public String getChildKeysCommaString() {
        final List<Column> foreignColumnList = getForeignColumnObjectList();
        String result = "";
        for (final Iterator<Column> ite = foreignColumnList.iterator(); ite.hasNext();) {
            final Column foreignCol = (Column) ite.next();
            final Column localCol = getLocalColumnByForeignColumn(foreignCol);
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
            if (peace.startsWith("$cls")) {
                // Not Dynamic (Embedded)
                final String code = extractFixedConditionEmbeddedCommentClassification(peace, parameterType);
                final String expression = "/*" + peace + "*/";

                // Remove test value because of hard code.
                fixedConditionReplacementMap.put(expression + "null", expression);
                fixedConditionReplacementMap.put(expression + "Null", expression);
                fixedConditionReplacementMap.put(expression + "NULL", expression);

                fixedConditionReplacementMap.put(expression, code);
            } else {
                // Really Dynamic (Bind)
                parameterType = filterDynamicFixedConditionParameterType(parameterType);
                final String parameterName = peace.substring(0, typeStartIndex);
                _dynamicFixedConditionMap.put(parameterName, parameterType);
                final String parameterMapName = "parameterMap" + getForeignPropertyNameInitCap();
                final String after = "/*$$locationBase$$." + parameterMapName + "." + parameterName + "*/";
                fixedConditionReplacementMap.put("/*" + peace + "*/", after);
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
        final Map<String, List<Map<String, String>>> definitionMap = getClassificationProperties()
                .getClassificationDefinitionMap();
        final List<Map<String, String>> elementMapList = definitionMap.get(classificationName);
        if (elementMapList == null) {
            String msg = "The classification name was NOT FOUND:";
            msg = msg + " classificationName=" + classificationName + " embeddedComment=" + peace;
            msg = msg + " classificationList=" + definitionMap.keySet();
            throw new DfFixedConditionInvalidClassificationEmbeddedCommentException(msg);
        }
        String code = null;
        for (Map<String, String> elementMap : elementMapList) {
            String name = elementMap.get(DfClassificationElement.KEY_NAME);
            if (elementName.equals(name)) {
                code = elementMap.get(DfClassificationElement.KEY_CODE);
                break;
            }
        }
        if (code == null) {
            String msg = "The classification element name was NOT FOUND:";
            msg = msg + " elementName=" + elementName + " embeddedComment=" + peace;
            msg = msg + " elementMapList=" + elementMapList;
            throw new DfFixedConditionInvalidClassificationEmbeddedCommentException(msg);
        }
        final Map<String, Map<String, String>> topMap = getClassificationProperties()
                .getClassificationTopDefinitionMap();
        final Map<String, String> map = topMap.get(classificationName);
        final String codeType = map.get(DfClassificationTop.KEY_CODE_TYPE);
        if (codeType == null || !codeType.equals(DfClassificationTop.CODE_TYPE_NUMBER)) {
            code = "'" + code + "'";
        }
        return code;
    }

    protected String filterDynamicFixedConditionParameterType(String parameterType) {
        final DfBasicProperties basicProperties = DfBuildProperties.getInstance().getBasicProperties();
        final DfStandardApiPackageResolver packageResolver = new DfStandardApiPackageResolver(basicProperties);
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

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected DfClassificationProperties getClassificationProperties() {
        return DfBuildProperties.getInstance().getClassificationProperties();
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

        for (int i = 0; i < _localColumns.size(); i++) {
            sb.append("        <reference local=\"").append(_localColumns.get(i));
            sb.append("\" foreign=\"").append(_foreignColumns.get(i)).append("\"/>\n");
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
}