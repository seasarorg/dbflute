package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.allcommon.EntityDefinedCommonColumn;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of PRODUCT that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_ID
 * 
 * [column]
 *     PRODUCT_ID, PRODUCT_NAME, PRODUCT_HANDLE_CODE, PRODUCT_STATUS_CODE, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     PRODUCT_ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     PRODUCT_STATUS
 * 
 * [referrer-table]
 *     PURCHASE
 * 
 * [foreign-property]
 *     productStatus
 * 
 * [referrer-property]
 *     purchaseList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsProduct implements EntityDefinedCommonColumn, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is PRODUCT. */
    public static final String TABLE = "PRODUCT";
    
    /** VERSION_NO-Annotation */
    public static final String VERSION_NO_PROPERTY = "versionNo";

    /** ID-Annotation */
    public static final String productId_ID = "identity";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** PRODUCT_ID: {PK : ID : NotNull : INTEGER} */
    protected Integer _productId;

    /** PRODUCT_NAME: {NotNull : VARCHAR(50)} */
    protected String _productName;

    /** PRODUCT_HANDLE_CODE: {UQ : NotNull : VARCHAR(100)} */
    protected String _productHandleCode;

    /** PRODUCT_STATUS_CODE: {NotNull : CHAR(3) : FK to PRODUCT_STATUS} */
    protected String _productStatusCode;

    /** REGISTER_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _registerDatetime;

    /** REGISTER_USER: {NotNull : VARCHAR(200)} */
    protected String _registerUser;

    /** REGISTER_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _registerProcess;

    /** UPDATE_DATETIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _updateDatetime;

    /** UPDATE_USER: {NotNull : VARCHAR(200)} */
    protected String _updateUser;

    /** UPDATE_PROCESS: {NotNull : VARCHAR(200)} */
    protected String _updateProcess;

    /** VERSION_NO: {NotNull : BIGINT} */
    protected Long _versionNo;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();

    /** Is common column auto set up effective? */
    protected boolean _canCommonColumnAutoSetup = true;
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsProduct() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "PRODUCT";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "product";
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
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [productStatus]
    // * * * * * * * * */
    public static final int productStatus_RELNO = 0;
    public static final String productStatus_RELKEYS = "PRODUCT_STATUS_CODE:PRODUCT_STATUS_CODE";

    /** PRODUCT_STATUS as 'productStatus'. */
    protected ProductStatus _parentProductStatus;

    /**
     * PRODUCT_STATUS as 'productStatus'. {without lazy-load}
     * @return The entity of foreign property 'productStatus'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public ProductStatus getProductStatus() {
        return _parentProductStatus;
    }

    /**
     * PRODUCT_STATUS as 'productStatus'.
     * @param productStatus The entity of foreign property 'productStatus'. (Nullable)
     */
    public void setProductStatus(ProductStatus productStatus) {
        _parentProductStatus = productStatus;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [purchaseList]
    // * * * * * * * * */
    /** PURCHASE as 'purchaseList'. */
    protected List<Purchase> _childrenPurchaseList;

    /**
     * PURCHASE as 'purchaseList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'purchaseList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<Purchase> getPurchaseList() {
        if (_childrenPurchaseList == null) { _childrenPurchaseList = new ArrayList<Purchase>(); }
        return _childrenPurchaseList;
    }

    /**
     * PURCHASE as 'purchaseList'.
     * @param purchaseList The entity list of referrer property 'purchaseList'. (Nullable)
     */
    public void setPurchaseList(List<Purchase> purchaseList) {
        _childrenPurchaseList = purchaseList;
    }


    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_productId == null) { return false; }
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
    //                                                           Common Column Auto Filter
    //                                                           =========================
    /**
     * Enable common column auto set up. {for after disable because the default is enabled}
     */
    public void enableCommonColumnAutoSetup() {
        _canCommonColumnAutoSetup = true;
    }

    /**
     * Disables auto set-up of common columns.
     */
    public void disableCommonColumnAutoSetup() {
        _canCommonColumnAutoSetup = false;
    }
    
    /**
     * Can the entity set up common column by auto?
     * @return Determination.
     */
    public boolean canCommonColumnAutoSetup() { // for Framework
        return _canCommonColumnAutoSetup;
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
        if (other == null || !(other instanceof BsProduct)) { return false; }
        BsProduct otherEntity = (BsProduct)other;
        if (!helpComparingValue(getProductId(), otherEntity.getProductId())) { return false; }
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
        if (this.getProductId() != null) { result = result + getProductId().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getProductId());
        sb.append(delimiter).append(getProductName());
        sb.append(delimiter).append(getProductHandleCode());
        sb.append(delimiter).append(getProductStatusCode());
        sb.append(delimiter).append(getRegisterDatetime());
        sb.append(delimiter).append(getRegisterUser());
        sb.append(delimiter).append(getRegisterProcess());
        sb.append(delimiter).append(getUpdateDatetime());
        sb.append(delimiter).append(getUpdateUser());
        sb.append(delimiter).append(getUpdateProcess());
        sb.append(delimiter).append(getVersionNo());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : ID : NotNull : INTEGER} */
    public static final String productId_COLUMN = "PRODUCT_ID";

    /**
     * PRODUCT_ID: {PK : ID : NotNull : INTEGER} <br />
     * @return The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public Integer getProductId() {
        return _productId;
    }

    /**
     * PRODUCT_ID: {PK : ID : NotNull : INTEGER} <br />
     * @param productId The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public void setProductId(Integer productId) {
        _modifiedProperties.addPropertyName("productId");
        this._productId = productId;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(50)} */
    public static final String productName_COLUMN = "PRODUCT_NAME";

    /**
     * PRODUCT_NAME: {NotNull : VARCHAR(50)} <br />
     * @return The value of the column 'PRODUCT_NAME'. (Nullable)
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * PRODUCT_NAME: {NotNull : VARCHAR(50)} <br />
     * @param productName The value of the column 'PRODUCT_NAME'. (Nullable)
     */
    public void setProductName(String productName) {
        _modifiedProperties.addPropertyName("productName");
        this._productName = productName;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : VARCHAR(100)} */
    public static final String productHandleCode_COLUMN = "PRODUCT_HANDLE_CODE";

    /**
     * PRODUCT_HANDLE_CODE: {UQ : NotNull : VARCHAR(100)} <br />
     * @return The value of the column 'PRODUCT_HANDLE_CODE'. (Nullable)
     */
    public String getProductHandleCode() {
        return _productHandleCode;
    }

    /**
     * PRODUCT_HANDLE_CODE: {UQ : NotNull : VARCHAR(100)} <br />
     * @param productHandleCode The value of the column 'PRODUCT_HANDLE_CODE'. (Nullable)
     */
    public void setProductHandleCode(String productHandleCode) {
        _modifiedProperties.addPropertyName("productHandleCode");
        this._productHandleCode = productHandleCode;
    }

    /** The column annotation for S2Dao. {NotNull : CHAR(3) : FK to PRODUCT_STATUS} */
    public static final String productStatusCode_COLUMN = "PRODUCT_STATUS_CODE";

    /**
     * PRODUCT_STATUS_CODE: {NotNull : CHAR(3) : FK to PRODUCT_STATUS} <br />
     * @return The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public String getProductStatusCode() {
        return _productStatusCode;
    }

    /**
     * PRODUCT_STATUS_CODE: {NotNull : CHAR(3) : FK to PRODUCT_STATUS} <br />
     * @param productStatusCode The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public void setProductStatusCode(String productStatusCode) {
        _modifiedProperties.addPropertyName("productStatusCode");
        this._productStatusCode = productStatusCode;
    }

    /** The column annotation for S2Dao. {NotNull : TIMESTAMP} */
    public static final String registerDatetime_COLUMN = "REGISTER_DATETIME";

    /**
     * REGISTER_DATETIME: {NotNull : TIMESTAMP} <br />
     * @return The value of the column 'REGISTER_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getRegisterDatetime() {
        return _registerDatetime;
    }

    /**
     * REGISTER_DATETIME: {NotNull : TIMESTAMP} <br />
     * @param registerDatetime The value of the column 'REGISTER_DATETIME'. (Nullable)
     */
    public void setRegisterDatetime(java.sql.Timestamp registerDatetime) {
        _modifiedProperties.addPropertyName("registerDatetime");
        this._registerDatetime = registerDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String registerUser_COLUMN = "REGISTER_USER";

    /**
     * REGISTER_USER: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'REGISTER_USER'. (Nullable)
     */
    public String getRegisterUser() {
        return _registerUser;
    }

    /**
     * REGISTER_USER: {NotNull : VARCHAR(200)} <br />
     * @param registerUser The value of the column 'REGISTER_USER'. (Nullable)
     */
    public void setRegisterUser(String registerUser) {
        _modifiedProperties.addPropertyName("registerUser");
        this._registerUser = registerUser;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String registerProcess_COLUMN = "REGISTER_PROCESS";

    /**
     * REGISTER_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'REGISTER_PROCESS'. (Nullable)
     */
    public String getRegisterProcess() {
        return _registerProcess;
    }

    /**
     * REGISTER_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @param registerProcess The value of the column 'REGISTER_PROCESS'. (Nullable)
     */
    public void setRegisterProcess(String registerProcess) {
        _modifiedProperties.addPropertyName("registerProcess");
        this._registerProcess = registerProcess;
    }

    /** The column annotation for S2Dao. {NotNull : TIMESTAMP} */
    public static final String updateDatetime_COLUMN = "UPDATE_DATETIME";

    /**
     * UPDATE_DATETIME: {NotNull : TIMESTAMP} <br />
     * @return The value of the column 'UPDATE_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getUpdateDatetime() {
        return _updateDatetime;
    }

    /**
     * UPDATE_DATETIME: {NotNull : TIMESTAMP} <br />
     * @param updateDatetime The value of the column 'UPDATE_DATETIME'. (Nullable)
     */
    public void setUpdateDatetime(java.sql.Timestamp updateDatetime) {
        _modifiedProperties.addPropertyName("updateDatetime");
        this._updateDatetime = updateDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String updateUser_COLUMN = "UPDATE_USER";

    /**
     * UPDATE_USER: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'UPDATE_USER'. (Nullable)
     */
    public String getUpdateUser() {
        return _updateUser;
    }

    /**
     * UPDATE_USER: {NotNull : VARCHAR(200)} <br />
     * @param updateUser The value of the column 'UPDATE_USER'. (Nullable)
     */
    public void setUpdateUser(String updateUser) {
        _modifiedProperties.addPropertyName("updateUser");
        this._updateUser = updateUser;
    }

    /** The column annotation for S2Dao. {NotNull : VARCHAR(200)} */
    public static final String updateProcess_COLUMN = "UPDATE_PROCESS";

    /**
     * UPDATE_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @return The value of the column 'UPDATE_PROCESS'. (Nullable)
     */
    public String getUpdateProcess() {
        return _updateProcess;
    }

    /**
     * UPDATE_PROCESS: {NotNull : VARCHAR(200)} <br />
     * @param updateProcess The value of the column 'UPDATE_PROCESS'. (Nullable)
     */
    public void setUpdateProcess(String updateProcess) {
        _modifiedProperties.addPropertyName("updateProcess");
        this._updateProcess = updateProcess;
    }

    /** The column annotation for S2Dao. {NotNull : BIGINT} */
    public static final String versionNo_COLUMN = "VERSION_NO";

    /**
     * VERSION_NO: {NotNull : BIGINT} <br />
     * @return The value of the column 'VERSION_NO'. (Nullable)
     */
    public Long getVersionNo() {
        return _versionNo;
    }

    /**
     * VERSION_NO: {NotNull : BIGINT} <br />
     * @param versionNo The value of the column 'VERSION_NO'. (Nullable)
     */
    public void setVersionNo(Long versionNo) {
        _modifiedProperties.addPropertyName("versionNo");
        this._versionNo = versionNo;
    }

}
