package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;


/**
 * The entity of VENDOR_CHECK that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     VENDOR_CHECK_ID
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
public abstract class BsVendorCheck implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is VENDOR_CHECK. */
    public static final String TABLE = "VENDOR_CHECK";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** VENDOR_CHECK_ID: {PK : NotNull : DECIMAL(16)} */
    protected Long _vendorCheckId;

    /** DECIMAL_DIGIT: {NotNull : DECIMAL(5, 3)} */
    protected java.math.BigDecimal _decimalDigit;

    /** INTEGER_NON_DIGIT: {NotNull : DECIMAL(5)} */
    protected Integer _integerNonDigit;

    /** TYPE_OF_BOOLEAN: {NotNull : BOOLEAN} */
    protected Boolean _typeOfBoolean;

    /** TYPE_OF_TEXT: {CLOB} */
    protected String _typeOfText;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsVendorCheck() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "VENDOR_CHECK";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "vendorCheck";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
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
        if (_vendorCheckId == null) { return false; }
        return true;
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
     * If the primary-key of the other is same as this one, returns true.
     * @param other Other entity.
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsVendorCheck)) { return false; }
        BsVendorCheck otherEntity = (BsVendorCheck)other;
        if (!helpComparingValue(getVendorCheckId(), otherEntity.getVendorCheckId())) { return false; }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) { return true; }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from primary-key.
     * @return Hash-code from primary-keys.
     */
    public int hashCode() {
        int result = 17;
        if (this.getVendorCheckId() != null) { result = result + getVendorCheckId().hashCode(); }
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

    /** The column annotation for S2Dao. {PK : NotNull : DECIMAL(16)} */
    public static final String vendorCheckId_COLUMN = "VENDOR_CHECK_ID";

    /**
     * VENDOR_CHECK_ID: {PK : NotNull : DECIMAL(16)} <br />
     * @return The value of the column 'VENDOR_CHECK_ID'. (Nullable)
     */
    public Long getVendorCheckId() {
        return _vendorCheckId;
    }

    /**
     * VENDOR_CHECK_ID: {PK : NotNull : DECIMAL(16)} <br />
     * @param vendorCheckId The value of the column 'VENDOR_CHECK_ID'. (Nullable)
     */
    public void setVendorCheckId(Long vendorCheckId) {
        _modifiedProperties.addPropertyName("vendorCheckId");
        this._vendorCheckId = vendorCheckId;
    }

    /** The column annotation for S2Dao. {NotNull : DECIMAL(5, 3)} */
    public static final String decimalDigit_COLUMN = "DECIMAL_DIGIT";

    /**
     * DECIMAL_DIGIT: {NotNull : DECIMAL(5, 3)} <br />
     * @return The value of the column 'DECIMAL_DIGIT'. (Nullable)
     */
    public java.math.BigDecimal getDecimalDigit() {
        return _decimalDigit;
    }

    /**
     * DECIMAL_DIGIT: {NotNull : DECIMAL(5, 3)} <br />
     * @param decimalDigit The value of the column 'DECIMAL_DIGIT'. (Nullable)
     */
    public void setDecimalDigit(java.math.BigDecimal decimalDigit) {
        _modifiedProperties.addPropertyName("decimalDigit");
        this._decimalDigit = decimalDigit;
    }

    /** The column annotation for S2Dao. {NotNull : DECIMAL(5)} */
    public static final String integerNonDigit_COLUMN = "INTEGER_NON_DIGIT";

    /**
     * INTEGER_NON_DIGIT: {NotNull : DECIMAL(5)} <br />
     * @return The value of the column 'INTEGER_NON_DIGIT'. (Nullable)
     */
    public Integer getIntegerNonDigit() {
        return _integerNonDigit;
    }

    /**
     * INTEGER_NON_DIGIT: {NotNull : DECIMAL(5)} <br />
     * @param integerNonDigit The value of the column 'INTEGER_NON_DIGIT'. (Nullable)
     */
    public void setIntegerNonDigit(Integer integerNonDigit) {
        _modifiedProperties.addPropertyName("integerNonDigit");
        this._integerNonDigit = integerNonDigit;
    }

    /** The column annotation for S2Dao. {NotNull : BOOLEAN} */
    public static final String typeOfBoolean_COLUMN = "TYPE_OF_BOOLEAN";

    /**
     * TYPE_OF_BOOLEAN: {NotNull : BOOLEAN} <br />
     * @return The value of the column 'TYPE_OF_BOOLEAN'. (Nullable)
     */
    public Boolean getTypeOfBoolean() {
        return _typeOfBoolean;
    }

    /**
     * TYPE_OF_BOOLEAN: {NotNull : BOOLEAN} <br />
     * @param typeOfBoolean The value of the column 'TYPE_OF_BOOLEAN'. (Nullable)
     */
    public void setTypeOfBoolean(Boolean typeOfBoolean) {
        _modifiedProperties.addPropertyName("typeOfBoolean");
        this._typeOfBoolean = typeOfBoolean;
    }

    /** The column annotation for S2Dao. {CLOB} */
    public static final String typeOfText_COLUMN = "TYPE_OF_TEXT";

    /**
     * TYPE_OF_TEXT: {CLOB} <br />
     * @return The value of the column 'TYPE_OF_TEXT'. (Nullable)
     */
    public String getTypeOfText() {
        return _typeOfText;
    }

    /**
     * TYPE_OF_TEXT: {CLOB} <br />
     * @param typeOfText The value of the column 'TYPE_OF_TEXT'. (Nullable)
     */
    public void setTypeOfText(String typeOfText) {
        _modifiedProperties.addPropertyName("typeOfText");
        this._typeOfText = typeOfText;
    }

}
