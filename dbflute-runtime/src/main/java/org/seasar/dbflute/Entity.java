package org.seasar.dbflute;

import java.util.Set;
import java.util.LinkedHashSet;

import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The interface of entity.
 * @author jflute
 */
public interface Entity {

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the instance of target dbmeta.
     * @return DBMeta. (NotNull)
     */
    public DBMeta getDBMeta();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    public String getTableDbName();

    /**
     * Get table property name.
     * @return Table property name. (NotNull)
     */
    public String getTablePropertyName();

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Has the value of primary-key?
     * @return Determination.
     */
    public boolean hasPrimaryKeyValue();

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    /**
     * Get modified property names. (JavaBeansRule)
     * @return Modified property names. (NotNull)
     */
    public Set<String> getModifiedPropertyNames();

    /**
     * Clear modified property names.
     */
    public void clearModifiedPropertyNames();

    /**
     * Entity modified properties.
     */
    public static class EntityModifiedProperties implements java.io.Serializable {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        /** Set of properties. */
        protected Set<String> _propertiesSet = new LinkedHashSet<String>();

        /**
         * Add property name. (JavaBeansRule)
         * @param propertyName Property name. (Nullable)
         */
        public void addPropertyName(String propertyName) {
            _propertiesSet.add(propertyName);
        }

        /**
         * Get the set of properties.
         * @return The set of properties. (NotNull)
         */
        public Set<String> getPropertyNames() {
            return _propertiesSet;
        }

        /**
         * Is empty?
         * @return Determination.
         */
        public boolean isEmpty() {
            return _propertiesSet.isEmpty();
        }

        /**
         * Clear the set of properties.
         */
        public void clear() {
            _propertiesSet.clear();
        }

        /**
         * Remove property name from the set. (JavaBeansRule)
         * @param propertyName Property name. (Nullable)
         */
        public void remove(String propertyName) {
            _propertiesSet.remove(propertyName);
        }
    }
}
