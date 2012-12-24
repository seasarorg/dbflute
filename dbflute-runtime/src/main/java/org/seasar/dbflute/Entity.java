/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.IllegalClassificationCodeException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.jdbc.ClassificationMeta;
import org.seasar.dbflute.jdbc.ParameterUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The interface of entity.
 * @author jflute
 */
public interface Entity {

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the target DB meta.
     * @return The instance of DBMeta type. (NotNull)
     */
    DBMeta getDBMeta();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return The string for name. (NotNull)
     */
    String getTableDbName();

    /**
     * Get table property name according to Java Beans rule.
     * @return The string for name. (NotNull)
     */
    String getTablePropertyName();

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * Does it have the value of primary keys?
     * @return The determination, true or false. (if all PK values are not null, returns true)
     */
    boolean hasPrimaryKeyValue();

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    /**
     * Get the set of modified properties. (basically for Framework)<br />
     * The properties needs to be according to Java Beans rule.
     * @return The set instance that contains names of modified property. (NotNull)
     */
    Set<String> modifiedProperties();

    /**
     * Clear the information of modified properties. (basically for Framework)
     */
    void clearModifiedInfo();

    /**
     * Does it have modifications of property names. (basically for Framework)
     * @return The determination, true or false.
     */
    boolean hasModification();

    /**
     * Entity modified properties. (basically for Framework)
     */
    public static class EntityModifiedProperties implements Serializable {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        /** The set of property names. */
        protected final Set<String> _propertyNameSet = new LinkedHashSet<String>();

        /**
         * Add property name. (according to Java Beans rule)
         * @param propertyName The string for name. (NotNull)
         */
        public void addPropertyName(String propertyName) {
            _propertyNameSet.add(propertyName);
        }

        /**
         * Get the set of properties.
         * @return The set of properties. (NotNull)
         */
        public Set<String> getPropertyNames() {
            return _propertyNameSet;
        }

        /**
         * Is empty?
         * @return The determination, true or false.
         */
        public boolean isEmpty() {
            return _propertyNameSet.isEmpty();
        }

        /**
         * Clear the set of properties.
         */
        public void clear() {
            _propertyNameSet.clear();
        }

        /**
         * Remove property name from the set. (according to Java Beans rule)
         * @param propertyName The string for name. (NotNull)
         */
        public void remove(String propertyName) {
            _propertyNameSet.remove(propertyName);
        }

        /**
         * Accept specified properties. (after clearing this properties)
         * @param properties The properties as copy-resource. (NotNull)
         */
        public void accept(EntityModifiedProperties properties) {
            clear();
            for (String propertyName : properties.getPropertyNames()) {
                addPropertyName(propertyName);
            }
        }
    }

    // ===================================================================================
    //                                                                    Extension Method
    //                                                                    ================
    /**
     * Calculate the hash-code, which is a default hash code, to identify the instance.
     * @return The hash-code from super.hashCode().
     */
    int instanceHash();

    /**
     * Convert the entity to display string with relation information.
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    String toStringWithRelation();

    /**
     * Build display string flexibly.
     * @param name The name for display. (NullAllowed: If it's null, it does not have a name)
     * @param column Does it contains column values or not?
     * @param relation Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    String buildDisplayString(String name, boolean column, boolean relation);

    // ===================================================================================
    //                                                                      Internal Class
    //                                                                      ==============
    public static final class InternalUtil {

        @SuppressWarnings("unchecked")
        public static <NUMBER extends Number> NUMBER toNumber(Object obj, Class<NUMBER> type) {
            return (NUMBER) DfTypeUtil.toNumber(obj, type);
        }

        public static Boolean toBoolean(Object obj) {
            return DfTypeUtil.toBoolean(obj);
        }

        public static boolean isSameValue(Object value1, Object value2) {
            if (value1 == null && value2 == null) {
                return true;
            }
            if (value1 == null || value2 == null) {
                return false;
            }
            if (value1 instanceof byte[] && value2 instanceof byte[]) {
                return isSameValueBytes((byte[]) value1, (byte[]) value2);
            }
            return value1.equals(value2);
        }

        public static boolean isSameValueBytes(byte[] bytes1, byte[] bytes2) {
            if (bytes1 == null && bytes2 == null) {
                return true;
            }
            if (bytes1 == null || bytes2 == null) {
                return false;
            }
            if (bytes1.length != bytes2.length) {
                return false;
            }
            for (int i = 0; i < bytes1.length; i++) {
                if (bytes1[i] != bytes2[i]) {
                    return false;
                }
            }
            return true;
        }

        public static int calculateHashcode(int result, Object value) { // calculateHashcode()
            if (value == null) {
                return result;
            }
            return (31 * result) + (value instanceof byte[] ? ((byte[]) value).length : value.hashCode());
        }

        public static String convertEmptyToNull(String value) {
            return ParameterUtil.convertEmptyToNull(value);
        }

        public static String toClassTitle(Entity entity) {
            return DfTypeUtil.toClassTitle(entity);
        }

        public static String toString(Date date, String pattern) {
            if (date == null) {
                return null;
            }
            final String str = DfTypeUtil.toString(date, pattern);
            return (DfTypeUtil.isDateBC(date) ? "BC" : "") + str;
        }

        public static String toString(byte[] bytes) {
            return "byte[" + (bytes != null ? String.valueOf(bytes.length) : "null") + "]";
        }

        public static void checkImplicitSet(Entity entity, String columnDbName, ClassificationMeta meta, Object code) {
            if (code != null && meta.codeOf(code) == null) {
                final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
                br.addNotice("The set value was not found in the classification of the column.");
                br.addItem("Table");
                br.addElement(entity.getTableDbName());
                br.addItem("Column");
                br.addElement(columnDbName);
                br.addItem("Classification");
                br.addElement(meta);
                br.addItem("Set Value");
                br.addElement(code);
                String msg = br.buildExceptionMessage();
                throw new IllegalClassificationCodeException(msg);
            }
        }
    }
}
