/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.dbmeta.info;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The information of foreign relation.
 * @author jflute
 */
public class ForeignInfo implements RelationInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _constraintName;
    protected final String _foreignPropertyName;
    protected final DBMeta _localDBMeta;
    protected final DBMeta _foreignDBMeta;
    protected final Map<ColumnInfo, ColumnInfo> _localForeignColumnInfoMap;
    protected final Map<ColumnInfo, ColumnInfo> _foreignLocalColumnInfoMap;
    protected final int _relationNo;
    protected final boolean _oneToOne;
    protected final boolean _bizOneToOne;
    protected final boolean _referrerAsOne;
    protected final boolean _additionalFK;
    protected final String _fixedCondition;
    protected final boolean _fixedInline;
    protected final String _reversePropertyName;
    protected final Method _readMethod;
    protected final Method _writeMethod;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ForeignInfo(String constraintName, String foreignPropertyName, DBMeta localDBMeta, DBMeta foreignDBMeta,
            Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap, int relationNo, boolean oneToOne,
            boolean bizOneToOne, boolean referrerAsOne, boolean additionalFK, String fixedCondition,
            boolean fixedInline, String reversePropertyName) {
        assertObjectNotNull("constraintName", constraintName);
        assertObjectNotNull("foreignPropertyName", foreignPropertyName);
        assertObjectNotNull("localDBMeta", localDBMeta);
        assertObjectNotNull("foreignDBMeta", foreignDBMeta);
        assertObjectNotNull("localForeignColumnInfoMap", localForeignColumnInfoMap);
        _constraintName = constraintName;
        _foreignPropertyName = foreignPropertyName;
        _localDBMeta = localDBMeta;
        _foreignDBMeta = foreignDBMeta;
        _localForeignColumnInfoMap = Collections.unmodifiableMap(localForeignColumnInfoMap);
        final Map<ColumnInfo, ColumnInfo> foreignLocalColumnInfoMap = new LinkedHashMap<ColumnInfo, ColumnInfo>();
        for (Entry<ColumnInfo, ColumnInfo> entry : localForeignColumnInfoMap.entrySet()) {
            foreignLocalColumnInfoMap.put(entry.getValue(), entry.getKey());
        }
        _foreignLocalColumnInfoMap = Collections.unmodifiableMap(foreignLocalColumnInfoMap);
        _relationNo = relationNo;
        _oneToOne = oneToOne;
        _bizOneToOne = bizOneToOne;
        _referrerAsOne = referrerAsOne;
        _additionalFK = additionalFK;
        _fixedCondition = fixedCondition;
        _fixedInline = fixedInline;
        _reversePropertyName = reversePropertyName;
        _readMethod = findReadMethod();
        _writeMethod = findWriteMethod();
    }

    // ===================================================================================
    //                                                                    Column Existence
    //                                                                    ================
    public boolean containsLocalColumn(ColumnInfo localColumn) {
        return doContainsLocalColumn(localColumn.getColumnDbName());
    }

    protected boolean doContainsLocalColumn(String columnName) {
        for (ColumnInfo columnInfo : _localForeignColumnInfoMap.keySet()) {
            if (columnInfo.getColumnDbName().equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsForeignColumn(ColumnInfo foreignColumn) {
        return doContainsForeignColumn(foreignColumn.getColumnDbName());
    }

    protected boolean doContainsForeignColumn(String columnName) {
        for (ColumnInfo columnInfo : _foreignLocalColumnInfoMap.keySet()) {
            if (columnInfo.getColumnDbName().equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                      Column Mapping
    //                                                                      ==============
    public ColumnInfo findLocalByForeign(String foreignColumnDbName) {
        final ColumnInfo keyColumnInfo = _foreignDBMeta.findColumnInfo(foreignColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo) _foreignLocalColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by foreignColumnDbName in foreignLocalColumnInfoMap:";
            msg = msg + " foreignColumnDbName=" + foreignColumnDbName;
            msg = msg + " foreignLocalColumnInfoMap=" + _foreignLocalColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    public ColumnInfo findForeignByLocal(String localColumnDbName) {
        final ColumnInfo keyColumnInfo = _localDBMeta.findColumnInfo(localColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo) _localForeignColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by localColumnDbName in localForeignColumnInfoMap:";
            msg = msg + " localColumnDbName=" + localColumnDbName;
            msg = msg + " localForeignColumnInfoMap=" + _localForeignColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    /**
     * Read the value to the entity.
     * @param localEntity The local entity of this column to read. (NotNull)
     * @param <PROPERTY> The type of property.
     * @return The read instance of foreign entity. (NullAllowed)
     */
    @SuppressWarnings("unchecked")
    public <PROPERTY extends Entity> PROPERTY read(Entity localEntity) {
        return (PROPERTY) invokeMethod(getReadMethod(), localEntity, new Object[] {});
    }

    /**
     * Get the read method for entity reflection.
     * @return The read method, cached in this instance. (NotNull)
     */
    public Method getReadMethod() {
        return _readMethod;
    }

    // -----------------------------------------------------
    //                                                 Write
    //                                                 -----
    /**
     * Write the value to the entity.
     * @param localEntity The local entity of this column to write. (NotNull)
     * @param foreignEntity The written instance of foreign entity. (NullAllowed: if null, null value is written)
     */
    public void write(Entity localEntity, Entity foreignEntity) {
        invokeMethod(getWriteMethod(), localEntity, new Object[] { foreignEntity });
    }

    /**
     * Get the write method for entity reflection.
     * @return The writer method, cached in this instance. (NotNull)
     */
    public Method getWriteMethod() {
        return _writeMethod;
    }

    // -----------------------------------------------------
    //                                                Finder
    //                                                ------
    protected Method findReadMethod() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final String methodName = buildAccessorName("get");
        final Method method = findMethod(localType, methodName, new Class[] {});
        if (method == null) {
            String msg = "Not found the method by the name: " + methodName;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected Method findWriteMethod() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final Class<? extends Entity> foreignType = _foreignDBMeta.getEntityType();
        final String methodName = buildAccessorName("set");
        final Method method = findMethod(localType, methodName, new Class[] { foreignType });
        if (method == null) {
            String msg = "Not found the method by the name and type: " + methodName + ", " + foreignType;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected String buildAccessorName(String prefix) {
        return prefix + initCap(_foreignPropertyName);
    }

    protected Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        return DfReflectionUtil.getAccessibleMethod(clazz, methodName, argTypes);
    }

    // -----------------------------------------------------
    //                                               Invoker
    //                                               -------
    protected Object invokeMethod(Method method, Object target, Object[] args) {
        return DfReflectionUtil.invoke(method, target, args);
    }

    // ===================================================================================
    //                                                             Relation Implementation
    //                                                             =======================
    public String getRelationPropertyName() {
        return getForeignPropertyName();
    }

    public DBMeta getTargetDBMeta() {
        return getForeignDBMeta();
    }

    public Map<ColumnInfo, ColumnInfo> getLocalTargetColumnInfoMap() {
        return getLocalForeignColumnInfoMap();
    }

    public boolean isReferrer() {
        return false;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(final String name) {
        return Srl.initCap(name);
    }

    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public int hashCode() {
        return _foreignPropertyName.hashCode() + _localDBMeta.hashCode() + _foreignDBMeta.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ForeignInfo)) {
            return false;
        }
        final ForeignInfo target = (ForeignInfo) obj;
        if (!this._foreignPropertyName.equals(target.getForeignPropertyName())) {
            return false;
        }
        if (!this._localDBMeta.equals(target.getLocalDBMeta())) {
            return false;
        }
        if (!this._foreignDBMeta.equals(target.getForeignDBMeta())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return _localDBMeta.getTableDbName() + "." + _foreignPropertyName + "->" + _foreignDBMeta.getTableDbName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * {@inheritDoc}
     */
    public String getConstraintName() {
        return _constraintName;
    }

    /**
     * Get the property name of the foreign relation. <br />
     * This is unique name in the table. <br />
     * For example, if the member entity has getMemberStatus(), this returns 'memberStatus'.
     * @return The string for property name. (NotNull)
     */
    public String getForeignPropertyName() {
        return _foreignPropertyName;
    }

    /**
     * Get the DB meta of the local table. <br />
     * For example, if the relation MEMBER and MEMBER_STATUS, this returns MEMBER's one.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getLocalDBMeta() {
        return _localDBMeta;
    }

    /**
     * Get the DB meta of the foreign table. <br />
     * For example, if the relation MEMBER and MEMBER_STATUS, this returns MEMBER_STATUS's one.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getForeignDBMeta() {
        return _foreignDBMeta;
    }

    /**
     * Get the read-only map, key is a local column info, value is a foreign column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getLocalForeignColumnInfoMap() {
        return _localForeignColumnInfoMap;
    }

    /**
     * Get the read-only map, key is a foreign column info, value is a local column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getForeignLocalColumnInfoMap() {
        return _foreignLocalColumnInfoMap;
    }

    /**
     * Get the number of a relation. (internal property)
     * @return The number of a relation. (NotNull, NotMinus)
     */
    public int getRelationNo() {
        return _relationNo;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isOneToOne() {
        return _oneToOne;
    }

    /**
     * Does the relation is biz-one-to-one?
     * @return The determination, true or false.
     */
    public boolean isBizOneToOne() {
        return _bizOneToOne;
    }

    /**
     * Does the relation is referrer-as-one?
     * @return The determination, true or false.
     */
    public boolean isReferrerAsOne() {
        return _referrerAsOne;
    }

    /**
     * Does the relation is from additional foreign key?
     * @return The determination, true or false.
     */
    public boolean isAdditionalFK() {
        return _additionalFK;
    }

    /**
     * Get the fixed condition if it's additional foreign key.
     * @return The string of fixed condition. (NullAllowed)
     */
    public String getFixedCondition() {
        return _fixedCondition;
    }

    /**
     * Does the fixed condition is for in-line view?
     * @return The determination, true or false.
     */
    public boolean isFixedInline() {
        return _fixedInline;
    }

    /**
     * {@inheritDoc}
     */
    public RelationInfo getReverseRelation() {
        return _reversePropertyName != null ? _foreignDBMeta.findRelationInfo(_reversePropertyName) : null;
    }

    // -----------------------------------------------------
    //                                               Derived
    //                                               -------
    /**
     * Does the relation is from pure foreign key?
     * @return The determination, true or false.
     */
    public boolean isPureFK() { // derived property
        return !_additionalFK && !_referrerAsOne;
    }

    /**
     * Do the FK columns have not null constraint?
     * @return The determination, true or false.
     */
    public boolean isNotNullFKColumn() {
        for (Entry<ColumnInfo, ColumnInfo> entry : getLocalForeignColumnInfoMap().entrySet()) {
            final ColumnInfo localColumnInfo = entry.getKey();
            if (!localColumnInfo.isNotNull()) {
                return false;
            }
        }
        return true;
    }
}
