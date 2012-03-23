/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMeta.OptimisticLockType;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.jdbc.Classification;
import org.seasar.dbflute.jdbc.ClassificationMeta;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * The information of column.
 * @author jflute
 */
public class ColumnInfo {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The empty read-only list for empty property. */
    protected static final List<String> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<String>());

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMeta _dbmeta;
    protected final String _columnDbName;
    protected final ColumnSqlName _columnSqlName;
    protected final String _columnSynonym;
    protected final String _columnAlias;
    protected final boolean _notNull;
    protected final String _propertyName;
    protected final Class<?> _propertyType;
    protected final boolean _primary;
    protected final boolean _autoIncrement;
    protected final String _columnDbType;
    protected final Integer _columnSize;
    protected final Integer _decimalDigits;
    protected final boolean _commonColumn;
    protected final OptimisticLockType _optimisticLockType;
    protected final String _columnComment;
    protected final List<String> _foreignPropList;
    protected final List<String> _referrerPropList;
    protected final ClassificationMeta _classificationMeta;
    protected final Method _reader;
    protected final Method _writer;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnInfo(DBMeta dbmeta, String columnDbName, String columnSqlName, String columnSynonym,
            String columnAlias, boolean notNull, String propertyName, Class<?> propertyType, boolean primary,
            boolean autoIncrement, String columnDbType, Integer columnSize, Integer decimalDigits,
            boolean commonColumn, OptimisticLockType optimisticLockType, String columnComment,
            List<String> foreignPropList, List<String> referrerPropList, ClassificationMeta classificationMeta) {
        assertObjectNotNull("dbmeta", dbmeta);
        assertObjectNotNull("columnDbName", columnDbName);
        assertObjectNotNull("columnSqlName", columnSqlName);
        assertObjectNotNull("propertyName", propertyName);
        assertObjectNotNull("propertyType", propertyType);
        _dbmeta = dbmeta;
        _columnDbName = columnDbName;
        _columnSqlName = new ColumnSqlName(columnSqlName);
        _columnSynonym = columnSynonym;
        _columnAlias = columnAlias;
        _notNull = notNull;
        _propertyName = propertyName;
        _propertyType = propertyType;
        _primary = primary;
        _autoIncrement = autoIncrement;
        _columnSize = columnSize;
        _columnDbType = columnDbType;
        _decimalDigits = decimalDigits;
        _commonColumn = commonColumn;
        _optimisticLockType = optimisticLockType != null ? optimisticLockType : OptimisticLockType.NONE;
        _columnComment = columnComment;
        _foreignPropList = foreignPropList != null ? foreignPropList : EMPTY_LIST;
        _referrerPropList = referrerPropList != null ? referrerPropList : EMPTY_LIST;
        _classificationMeta = classificationMeta;
        _reader = findReader();
        _writer = findWriter();
    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    /**
     * Read the value from the entity.
     * @param entity The target entity of this column to read. (NotNull)
     * @param <PROPERTY> The type of the property.
     * @return The read value. (NullAllowed)
     */
    @SuppressWarnings("unchecked")
    public <PROPERTY> PROPERTY read(Entity entity) {
        return (PROPERTY) _dbmeta.readEntityProperty(_propertyName, entity);
    }

    /**
     * Get the read method for reflection.
     * @return The read method, cached in this instance. (NotNull)
     */
    public Method reader() {
        return _reader;
    }

    // -----------------------------------------------------
    //                                                 Write
    //                                                 -----
    /**
     * Write the value to the entity.
     * @param entity The target entity of this column to write. (NotNull)
     * @param value The written value. (NullAllowed: if null, null value is written)
     */
    public void write(Entity entity, Object value) {
        _dbmeta.writeEntityProperty(_propertyName, entity, value);
    }

    /**
     * Get the write method for reflection.
     * @return The writer method, cached in this instance. (NotNull)
     */
    public Method writer() {
        return _writer;
    }

    // -----------------------------------------------------
    //                                               Generic
    //                                               -------
    /**
     * Get the generic type of property type for list property.
     * @return The type instance. (NullAllowed: when not list type)
     */
    public Class<?> getGenericType() {
        return DfReflectionUtil.getGenericType(reader().getGenericReturnType());
    }

    // -----------------------------------------------------
    //                                                Finder
    //                                                ------
    protected Method findReader() {
        final Class<? extends Entity> entityType = _dbmeta.getEntityType();
        final String methodName = buildAccessorName("get");
        final Method method = findMethod(entityType, methodName, new Class<?>[] {});
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected Method findWriter() {
        final Class<? extends Entity> entityType = _dbmeta.getEntityType();
        final String methodName = buildAccessorName("set");
        final Method method = findMethod(entityType, methodName, new Class<?>[] { _propertyType });
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            msg = msg + " propertyType=" + _propertyType;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected String buildAccessorName(String prefix) {
        return prefix + initCap(_propertyName);
    }

    protected Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        return DfReflectionUtil.getAccessibleMethod(clazz, methodName, argTypes);
    }

    // ===================================================================================
    //                                                                        Convert Type
    //                                                                        ============
    @SuppressWarnings("unchecked")
    public <VALUE> VALUE toPropretyType(Object value) {
        if (value == null) {
            return null;
        }
        final VALUE result;
        if (value instanceof List<?>) {
            final List<?> valueList = (List<?>) value;
            final List<Object> resultList = new ArrayList<Object>();
            for (Object obj : valueList) {
                resultList.add(doConvertToPropretyType(obj));
            }
            result = (VALUE) resultList;
        } else {
            result = (VALUE) doConvertToPropretyType(value);
        }
        return result;
    }

    protected Object doConvertToPropretyType(Object value) {
        if (value != null && value instanceof Classification) {
            value = ((Classification) value).code();
        }
        if (value == null) {
            return null;
        }
        final Object converted;
        if (Number.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toNumber(value, _propertyType);
        } else if (Timestamp.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toTimestamp(value);
        } else if (Time.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toTime(value);
        } else if (Date.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toDate(value);
        } else if (Boolean.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toBoolean(value);
        } else if (byte[].class.isAssignableFrom(_propertyType)) {
            if (value instanceof Serializable) {
                converted = DfTypeUtil.toBinary((Serializable) value);
            } else {
                converted = value; // no change
            }
        } else if (UUID.class.isAssignableFrom(_propertyType)) {
            converted = DfTypeUtil.toUUID(value);
        } else {
            converted = value;
        }
        return converted;
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
        return _dbmeta.hashCode() + _columnDbName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ColumnInfo)) {
            return false;
        }
        final ColumnInfo target = (ColumnInfo) obj;
        if (!this._dbmeta.equals(target.getDBMeta())) {
            return false;
        }
        if (!this._columnDbName.equals(target.getColumnDbName())) {
            return false;
        }
        return true;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_dbmeta.getTableDbName());
        sb.append(".").append(_columnDbName);
        sb.append(":{");
        sb.append(_columnDbType);
        if (_columnSize != null) {
            sb.append("(").append(_columnSize);
            if (_decimalDigits != null) {
                sb.append(", ").append(_decimalDigits);
            }
            sb.append(")");
        }
        sb.append(", ").append(_propertyType.getName());
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the DB meta of the column's table.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getDBMeta() {
        return _dbmeta;
    }

    /**
     * Get the DB name of the column. <br />
     * This is for identity of column. (NOT for SQL)
     * @return The DB name of the column. (NotNull)
     */
    public String getColumnDbName() {
        return this._columnDbName;
    }

    /**
     * Get the SQL name of the column. <br />
     * This is for SQL, which is resolved about schema prefix and quoted and so on...  
     * @return The SQL-name object of the column. (NotNull)
     */
    public ColumnSqlName getColumnSqlName() {
        return this._columnSqlName;
    }

    /**
     * Get the synonym of the column. <br />
     * This is for the synonym of DBFlute. (for example, PgReservColumn handling)
     * @return The synonym of the column. (NotNull)
     */
    public String getColumnSynonym() {
        return this._columnSynonym;
    }

    /**
     * Get the alias of the column.
     * @return The alias of the column. (NullAllowed: when it cannot get an alias from meta)
     */
    public String getColumnAlias() {
        return this._columnAlias;
    }

    /**
     * Is the column not null?
     * @return The determination, true or false.
     */
    public boolean isNotNull() {
        return this._notNull;
    }

    /**
     * Get the name of property for the column. (JavaBeansRule)
     * @return The name of property for the column. (NotNull)
     */
    public String getPropertyName() {
        return this._propertyName;
    }

    /**
     * Get the type of property for the column.
     * @return The type of property for the column. (NotNull)
     */
    public Class<?> getPropertyType() {
        return this._propertyType;
    }

    /**
     * Is the property type String? (assignable from)
     * @return The determination, true or false.
     */
    public boolean isPropertyTypeString() {
        return String.class.isAssignableFrom(getPropertyType());
    }

    /**
     * Is the property type Number? (assignable from)
     * @return The determination, true or false.
     */
    public boolean isPropertyTypeNumber() {
        return Number.class.isAssignableFrom(getPropertyType());
    }

    /**
     * Is the property type Date? (assignable from)
     * @return The determination, true or false.
     */
    public boolean isPropertyTypeDate() {
        return Date.class.isAssignableFrom(getPropertyType());
    }

    /**
     * Is the column a part of primary keys?
     * @return The determination, true or false.
     */
    public boolean isPrimary() {
        return this._primary;
    }

    /**
     * Is the column auto increment?
     * @return The determination, true or false.
     */
    public boolean isAutoIncrement() {
        return this._autoIncrement;
    }

    /**
     * Get the DB type of the column.
     * @return The DB type of the column. (NotNull: If the type is unknown, it returns 'UnknownType'.)
     */
    public String getColumnDbType() {
        return this._columnDbType;
    }

    /**
     * Get the size of the column.
     * @return The size of the column. (NullAllowed: If the type does not have size, it returns null.)
     */
    public Integer getColumnSize() {
        return this._columnSize;
    }

    /**
     * Get the decimal digits of the column.
     * @return The decimal digits of the column. (NullAllowed: If the type does not have disits, it returns null.)
     */
    public Integer getDecimalDigits() {
        return this._decimalDigits;
    }

    /**
     * Is the column a part of common columns?
     * @return The determination, true or false.
     */
    public boolean isCommonColumn() {
        return this._commonColumn;
    }

    /**
     * Is the column for optimistic lock?
     * @return The determination, true or false.
     */
    public boolean isOptimisticLock() {
        return isVersionNo() || isUpdateDate();
    }

    /**
     * Is the column version-no for optimistic lock?
     * @return The determination, true or false.
     */
    public boolean isVersionNo() {
        return OptimisticLockType.VERSION_NO == _optimisticLockType;
    }

    /**
     * Is the column update-date for optimistic lock?
     * @return The determination, true or false.
     */
    public boolean isUpdateDate() {
        return OptimisticLockType.UPDATE_DATE == _optimisticLockType;
    }

    /**
     * Get the comment of the column. <br />
     * If the real comment contains the alias,
     * this result does NOT contain it and its delimiter.  
     * @return The comment of the column. (NullAllowed: when it cannot get an alias from meta)
     */
    public String getColumnComment() {
        return this._columnComment;
    }

    /**
     * Get the read-only list of the foreign info related to this column. <br />
     * It contains one-to-one relations.
     * @return The read-only list. (NotNull: when no FK, returns empty list)
     */
    public List<ForeignInfo> getForeignInfoList() {
        // find at this timing because initialization timing of column info is before FK's one.
        final List<ForeignInfo> foreignInfoList = new ArrayList<ForeignInfo>();
        for (String foreignProp : _foreignPropList) {
            foreignInfoList.add(getDBMeta().findForeignInfo(foreignProp));
        }
        return Collections.unmodifiableList(foreignInfoList); // as read-only
    }

    /**
     * Get the read-only list of the referrer info related to this column.
     * @return The read-only list. (NotNull: when no reference, returns empty list)
     */
    public List<ReferrerInfo> getReferrerInfoList() {
        // find at this timing because initialization timing of column info is before FK's one.
        final List<ReferrerInfo> referrerInfoList = new ArrayList<ReferrerInfo>();
        for (String fkProp : _referrerPropList) {
            referrerInfoList.add(getDBMeta().findReferrerInfo(fkProp));
        }
        return Collections.unmodifiableList(referrerInfoList); // as read-only
    }

    /**
     * Get the meta of classification related to the column.
     * @return The instance of classification meta. (NullAllowed)
     */
    public ClassificationMeta getClassificationMeta() {
        return _classificationMeta;
    }
}
