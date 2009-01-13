package com.example.dbflute.basic.dbflute.bsentity.customize;

import java.util.*;

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;


/**
 * The entity of VendorCheckDecimalSum that the type is null. <br />
 * <pre>
 * [primary-key]
 *     
 * 
 * [column]
 *     DECIMAL_DIGIT_SUM
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
public abstract class BsVendorCheckDecimalSum implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is VendorCheckDecimalSum. */
    public static final String TABLE = "VendorCheckDecimalSum";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** DECIMAL_DIGIT_SUM: {DECIMAL(5,3)} */
    protected java.math.BigDecimal _decimalDigitSum;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsVendorCheckDecimalSum() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "VendorCheckDecimalSum";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "vendorCheckDecimalSum";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta.VendorCheckDecimalSumDbm.getInstance();
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
        if (other == null || !(other instanceof BsVendorCheckDecimalSum)) { return false; }
        final BsVendorCheckDecimalSum otherEntity = (BsVendorCheckDecimalSum)other;
        if (!helpComparingValue(getDecimalDigitSum(), otherEntity.getDecimalDigitSum())) { return false; }
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
        if (this.getDecimalDigitSum() != null) { result = result + this.getDecimalDigitSum().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getDecimalDigitSum());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {DECIMAL(5,3)} */
    public static final String decimalDigitSum_COLUMN = "DECIMAL_DIGIT_SUM";

    /**
     * DECIMAL_DIGIT_SUM: {DECIMAL(5,3)} <br />
     * @return The value of the column 'DECIMAL_DIGIT_SUM'. (Nullable)
     */
    public java.math.BigDecimal getDecimalDigitSum() {
        return _decimalDigitSum;
    }

    /**
     * DECIMAL_DIGIT_SUM: {DECIMAL(5,3)} <br />
     * @param decimalDigitSum The value of the column 'DECIMAL_DIGIT_SUM'. (Nullable)
     */
    public void setDecimalDigitSum(java.math.BigDecimal decimalDigitSum) {
        _modifiedProperties.addPropertyName("decimalDigitSum");
        this._decimalDigitSum = decimalDigitSum;
    }

}
