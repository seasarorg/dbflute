/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMeta.OptimisticLockType;

/**
 * The information of column.
 * @author jflute
 */
public class ColumnInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMeta dbmeta;
    protected final String columnDbName;
    protected final String columnAlias;
    protected final String propertyName;
    protected final Class<?> propertyType;
    protected final boolean primary;
    protected final Integer columnSize;
    protected final Integer columnDecimalDigits;
    protected final OptimisticLockType optimisticLockType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnInfo(DBMeta dbmeta, String columnDbName, String columnAlias, String propertyName
                    , Class<?> propertyType, boolean primary, Integer columnSize, Integer columnDecimalDigits) {
        this(dbmeta, columnDbName, columnAlias, propertyName, propertyType, primary
           , columnSize, columnDecimalDigits
           , OptimisticLockType.NONE);
    }

    public ColumnInfo(DBMeta dbmeta, String columnDbName, String columnAlias, String propertyName
                    , Class<?> propertyType, boolean primary, Integer columnSize, Integer columnDecimalDigits
                    , OptimisticLockType optimisticLockType) {
        assertObjectNotNull("dbmeta", dbmeta);
        assertObjectNotNull("columnDbName", columnDbName);
        assertObjectNotNull("propertyName", propertyName);
        assertObjectNotNull("propertyType", propertyType);
        assertObjectNotNull("optimisticLockType", optimisticLockType);
        this.dbmeta = dbmeta;
        this.columnDbName = columnDbName;
        this.columnAlias = columnAlias;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.primary = primary;
        this.columnSize = columnSize;
        this.columnDecimalDigits = columnDecimalDigits;
        this.optimisticLockType = optimisticLockType;
    }

    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public java.lang.reflect.Method findSetter() {
        return findMethod(dbmeta.getEntityType(), "set" + buildInitCapPropertyName(), new Class<?>[] { this.propertyType });
    }

    public java.lang.reflect.Method findGetter() {
        return findMethod(dbmeta.getEntityType(), "get" + buildInitCapPropertyName(), new Class<?>[] {});
    }

    protected String buildInitCapPropertyName() {
        return initCap(this.propertyName);
    }

    // ===================================================================================
    //                                                                Optimistic Lock Type
    //                                                                ====================
    public boolean isOptimisticLock() {
        return isVersionNo() || isUpdateDate();
    }

    public boolean isVersionNo() {
        return OptimisticLockType.VERSION_NO == optimisticLockType;
    }

    public boolean isUpdateDate() {
        return OptimisticLockType.UPDATE_DATE == optimisticLockType;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(final String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    protected java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        try {
            return clazz.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException ex) {
            String msg = "class=" + clazz + " method=" + methodName + "-" + java.util.Arrays.asList(argTypes);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
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
        return dbmeta.hashCode() + columnDbName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ColumnInfo)) {
            return false;
        }
        final ColumnInfo target = (ColumnInfo)obj;
        if (!this.dbmeta.equals(target.getDBMeta())) {
            return false;
        }
        if (!this.columnDbName.equals(target.getColumnDbName())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return dbmeta.getTableDbName() + "." + columnDbName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBMeta getDBMeta() {
        return dbmeta;
    }

    /**
     * Get the DB name of the column.
     * @return The DB name of the column. (NotNull)
     */
    public String getColumnDbName() {
        return this.columnDbName;
    }

    /**
     * Get the alias of the column.
     * @return The alias of the column. (Nullable: If the definition about aliases, it returns null.)
     */
    public String getColumnAlias() {
        return this.columnAlias;
    }

    /**
     * Get the name of property for the column. (JavaBeansRule)
     * @return The name of property for the column. (NotNull)
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Get the type of property for the column.
     * @return The type of property for the column. (NotNull)
     */
	public Class<?> getPropertyType() {
        return this.propertyType;
    }

    /**
     * Is the column is a part of primary keys?
     * @return Determination.
     */
    public boolean isPrimary() {
        return this.primary;
    }

    /**
     * Get the size of the column.
     * @return The size of the column. (Nullable: If the type does not have size, it returns null.)
     */
    public Integer getColumnSize() {
        return this.columnSize;
    }

    /**
     * Get the decimal digits of the column.
     * @return The decimal digits of the column. (Nullable: If the type does not have disits, it returns null.)
     */
    public Integer getColumnDecimalDigits() {
        return this.columnDecimalDigits;
    }
}
