package com.example.dbflute.basic.dbflute.bsentity.customize;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of SimpleVendorCheck that the type is null. <br />
 * <pre>
 * [primary-key]
 *     
 * 
 * [column]
 *     VENDOR_CHECK_ID, DECIMAL_DIGIT, INTEGER_NON_DIGIT, TYPE_OF_BOOLEAN, TYPE_OF_TEXT
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
public abstract class BsSimpleVendorCheck implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is SimpleVendorCheck. */
    public static final String TABLE = "SimpleVendorCheck";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** VENDOR_CHECK_ID: {DECIMAL(16,0)} */
    protected Long _vendorCheckId;

    /** DECIMAL_DIGIT: {DECIMAL(5,3)} */
    protected java.math.BigDecimal _decimalDigit;

    /** INTEGER_NON_DIGIT: {DECIMAL(5,0)} */
    protected Integer _integerNonDigit;

    /** TYPE_OF_BOOLEAN: {BOOLEAN(1,0)} */
    protected Boolean _typeOfBoolean;

    /** TYPE_OF_TEXT: {CLOB(2147483647,0)} */
    protected String _typeOfText;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsSimpleVendorCheck() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "SimpleVendorCheck";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "simpleVendorCheck";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return com.example.dbflute.basic.dbflute.bsentity.customize.dbmeta.SimpleVendorCheckDbm.getInstance();
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
        if (other == null || !(other instanceof BsSimpleVendorCheck)) { return false; }
        final BsSimpleVendorCheck otherEntity = (BsSimpleVendorCheck)other;
        if (!helpComparingValue(getVendorCheckId(), otherEntity.getVendorCheckId())) { return false; }
        if (!helpComparingValue(getDecimalDigit(), otherEntity.getDecimalDigit())) { return false; }
        if (!helpComparingValue(getIntegerNonDigit(), otherEntity.getIntegerNonDigit())) { return false; }
        if (!helpComparingValue(getTypeOfBoolean(), otherEntity.getTypeOfBoolean())) { return false; }
        if (!helpComparingValue(getTypeOfText(), otherEntity.getTypeOfText())) { return false; }
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
        if (this.getVendorCheckId() != null) { result = result + this.getVendorCheckId().hashCode(); }
        if (this.getDecimalDigit() != null) { result = result + this.getDecimalDigit().hashCode(); }
        if (this.getIntegerNonDigit() != null) { result = result + this.getIntegerNonDigit().hashCode(); }
        if (this.getTypeOfBoolean() != null) { result = result + this.getTypeOfBoolean().hashCode(); }
        if (this.getTypeOfText() != null) { result = result + this.getTypeOfText().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getVendorCheckId());
        sb.append(delimiter).append(getDecimalDigit());
        sb.append(delimiter).append(getIntegerNonDigit());
        sb.append(delimiter).append(getTypeOfBoolean());
        sb.append(delimiter).append(getTypeOfText());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {DECIMAL(16,0)} */
    public static final String vendorCheckId_COLUMN = "VENDOR_CHECK_ID";

    /**
     * VENDOR_CHECK_ID: {DECIMAL(16,0)} <br />
     * @return The value of the column 'VENDOR_CHECK_ID'. (Nullable)
     */
    public Long getVendorCheckId() {
        return _vendorCheckId;
    }

    /**
     * VENDOR_CHECK_ID: {DECIMAL(16,0)} <br />
     * @param vendorCheckId The value of the column 'VENDOR_CHECK_ID'. (Nullable)
     */
    public void setVendorCheckId(Long vendorCheckId) {
        _modifiedProperties.addPropertyName("vendorCheckId");
        this._vendorCheckId = vendorCheckId;
    }

    /** The column annotation for S2Dao. {DECIMAL(5,3)} */
    public static final String decimalDigit_COLUMN = "DECIMAL_DIGIT";

    /**
     * DECIMAL_DIGIT: {DECIMAL(5,3)} <br />
     * @return The value of the column 'DECIMAL_DIGIT'. (Nullable)
     */
    public java.math.BigDecimal getDecimalDigit() {
        return _decimalDigit;
    }

    /**
     * DECIMAL_DIGIT: {DECIMAL(5,3)} <br />
     * @param decimalDigit The value of the column 'DECIMAL_DIGIT'. (Nullable)
     */
    public void setDecimalDigit(java.math.BigDecimal decimalDigit) {
        _modifiedProperties.addPropertyName("decimalDigit");
        this._decimalDigit = decimalDigit;
    }

    /** The column annotation for S2Dao. {DECIMAL(5,0)} */
    public static final String integerNonDigit_COLUMN = "INTEGER_NON_DIGIT";

    /**
     * INTEGER_NON_DIGIT: {DECIMAL(5,0)} <br />
     * @return The value of the column 'INTEGER_NON_DIGIT'. (Nullable)
     */
    public Integer getIntegerNonDigit() {
        return _integerNonDigit;
    }

    /**
     * INTEGER_NON_DIGIT: {DECIMAL(5,0)} <br />
     * @param integerNonDigit The value of the column 'INTEGER_NON_DIGIT'. (Nullable)
     */
    public void setIntegerNonDigit(Integer integerNonDigit) {
        _modifiedProperties.addPropertyName("integerNonDigit");
        this._integerNonDigit = integerNonDigit;
    }

    /** The column annotation for S2Dao. {BOOLEAN(1,0)} */
    public static final String typeOfBoolean_COLUMN = "TYPE_OF_BOOLEAN";

    /**
     * TYPE_OF_BOOLEAN: {BOOLEAN(1,0)} <br />
     * @return The value of the column 'TYPE_OF_BOOLEAN'. (Nullable)
     */
    public Boolean getTypeOfBoolean() {
        return _typeOfBoolean;
    }

    /**
     * TYPE_OF_BOOLEAN: {BOOLEAN(1,0)} <br />
     * @param typeOfBoolean The value of the column 'TYPE_OF_BOOLEAN'. (Nullable)
     */
    public void setTypeOfBoolean(Boolean typeOfBoolean) {
        _modifiedProperties.addPropertyName("typeOfBoolean");
        this._typeOfBoolean = typeOfBoolean;
    }

    /** The column annotation for S2Dao. {CLOB(2147483647,0)} */
    public static final String typeOfText_COLUMN = "TYPE_OF_TEXT";

    /**
     * TYPE_OF_TEXT: {CLOB(2147483647,0)} <br />
     * @return The value of the column 'TYPE_OF_TEXT'. (Nullable)
     */
    public String getTypeOfText() {
        return _typeOfText;
    }

    /**
     * TYPE_OF_TEXT: {CLOB(2147483647,0)} <br />
     * @param typeOfText The value of the column 'TYPE_OF_TEXT'. (Nullable)
     */
    public void setTypeOfText(String typeOfText) {
        _modifiedProperties.addPropertyName("typeOfText");
        this._typeOfText = typeOfText;
    }

}
