package com.example.dbflute.basic.dbflute.bsentity.customize;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of VendorCheckIntegerSum that the type is null. <br />
 * <pre>
 * [primary-key]
 *     
 * 
 * [column]
 *     INTEGER_NON_DIGIT_SUM
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsVendorCheckIntegerSum implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is VendorCheckIntegerSum. */
    public static final String TABLE = "VendorCheckIntegerSum";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** INTEGER_NON_DIGIT_SUM: {DECIMAL(5,0)} */
    protected Integer _integerNonDigitSum;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsVendorCheckIntegerSum() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "VendorCheckIntegerSum";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "vendorCheckIntegerSum";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta.VendorCheckIntegerSumDbm.getInstance();
    }

    // ===================================================================================
    //                                                          Classification Classifying
    //                                                          ==========================
    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        return false;
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    public Set<String> getModifiedPropertyNames() {
        return _modifiedProperties.getPropertyNames();
    }

    protected EntityModifiedProperties newEntityModifiedProperties() {
        return new EntityModifiedProperties();
    }

    public void clearModifiedPropertyNames() {
        _modifiedProperties.clear();
    }

    public boolean hasModification() {
        return !_modifiedProperties.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If the all-column value of the other is same as this one, returns true.
     * @param other Other entity. (Nullable)
     * @return Comparing result. If other is null, returns false.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsVendorCheckIntegerSum)) { return false; }
        final BsVendorCheckIntegerSum otherEntity = (BsVendorCheckIntegerSum)other;
        if (!helpComparingValue(getIntegerNonDigitSum(), otherEntity.getIntegerNonDigitSum())) { return false; }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) { return true; }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from all columns.
     * @return Hash-code from all-columns.
     */
    public int hashCode() {
        int result = 17;
        if (this.getIntegerNonDigitSum() != null) { result = result + this.getIntegerNonDigitSum().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getIntegerNonDigitSum());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {DECIMAL(5,0)} */
    public static final String integerNonDigitSum_COLUMN = "INTEGER_NON_DIGIT_SUM";

    /**
     * INTEGER_NON_DIGIT_SUM: {DECIMAL(5,0)} <br />
     * @return The value of the column 'INTEGER_NON_DIGIT_SUM'. (Nullable)
     */
    public Integer getIntegerNonDigitSum() {
        return _integerNonDigitSum;
    }

    /**
     * INTEGER_NON_DIGIT_SUM: {DECIMAL(5,0)} <br />
     * @param integerNonDigitSum The value of the column 'INTEGER_NON_DIGIT_SUM'. (Nullable)
     */
    public void setIntegerNonDigitSum(Integer integerNonDigitSum) {
        _modifiedProperties.addPropertyName("integerNonDigitSum");
        this._integerNonDigitSum = integerNonDigitSum;
    }

}
