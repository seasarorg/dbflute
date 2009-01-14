package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of SUMMARY_PRODUCT that the type is VIEW. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_ID
 * 
 * [column]
 *     PRODUCT_ID, PRODUCT_NAME, PRODUCT_STATUS_CODE, LATEST_PURCHASE_DATETIME
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
public abstract class BsSummaryProduct implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is SUMMARY_PRODUCT. */
    public static final String TABLE = "SUMMARY_PRODUCT";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** PRODUCT_ID: {PK : INTEGER} */
    protected Integer _productId;

    /** PRODUCT_NAME: {VARCHAR(50)} */
    protected String _productName;

    /** PRODUCT_STATUS_CODE: {CHAR(3) : FK to PRODUCT_STATUS} */
    protected String _productStatusCode;

    /** LATEST_PURCHASE_DATETIME: {TIMESTAMP} */
    protected java.sql.Timestamp _latestPurchaseDatetime;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsSummaryProduct() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "SUMMARY_PRODUCT";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "summaryProduct";
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
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If the primary-key of the other is same as this one, returns true.
     * @param other Other entity.
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsSummaryProduct)) { return false; }
        BsSummaryProduct otherEntity = (BsSummaryProduct)other;
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
        sb.append(delimiter).append(getProductStatusCode());
        sb.append(delimiter).append(getLatestPurchaseDatetime());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : INTEGER} */
    public static final String productId_COLUMN = "PRODUCT_ID";

    /**
     * PRODUCT_ID: {PK : INTEGER} <br />
     * @return The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public Integer getProductId() {
        return _productId;
    }

    /**
     * PRODUCT_ID: {PK : INTEGER} <br />
     * @param productId The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public void setProductId(Integer productId) {
        _modifiedProperties.addPropertyName("productId");
        this._productId = productId;
    }

    /** The column annotation for S2Dao. {VARCHAR(50)} */
    public static final String productName_COLUMN = "PRODUCT_NAME";

    /**
     * PRODUCT_NAME: {VARCHAR(50)} <br />
     * @return The value of the column 'PRODUCT_NAME'. (Nullable)
     */
    public String getProductName() {
        return _productName;
    }

    /**
     * PRODUCT_NAME: {VARCHAR(50)} <br />
     * @param productName The value of the column 'PRODUCT_NAME'. (Nullable)
     */
    public void setProductName(String productName) {
        _modifiedProperties.addPropertyName("productName");
        this._productName = productName;
    }

    /** The column annotation for S2Dao. {CHAR(3) : FK to PRODUCT_STATUS} */
    public static final String productStatusCode_COLUMN = "PRODUCT_STATUS_CODE";

    /**
     * PRODUCT_STATUS_CODE: {CHAR(3) : FK to PRODUCT_STATUS} <br />
     * @return The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public String getProductStatusCode() {
        return _productStatusCode;
    }

    /**
     * PRODUCT_STATUS_CODE: {CHAR(3) : FK to PRODUCT_STATUS} <br />
     * @param productStatusCode The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public void setProductStatusCode(String productStatusCode) {
        _modifiedProperties.addPropertyName("productStatusCode");
        this._productStatusCode = productStatusCode;
    }

    /** The column annotation for S2Dao. {TIMESTAMP} */
    public static final String latestPurchaseDatetime_COLUMN = "LATEST_PURCHASE_DATETIME";

    /**
     * LATEST_PURCHASE_DATETIME: {TIMESTAMP} <br />
     * @return The value of the column 'LATEST_PURCHASE_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getLatestPurchaseDatetime() {
        return _latestPurchaseDatetime;
    }

    /**
     * LATEST_PURCHASE_DATETIME: {TIMESTAMP} <br />
     * @param latestPurchaseDatetime The value of the column 'LATEST_PURCHASE_DATETIME'. (Nullable)
     */
    public void setLatestPurchaseDatetime(java.sql.Timestamp latestPurchaseDatetime) {
        _modifiedProperties.addPropertyName("latestPurchaseDatetime");
        this._latestPurchaseDatetime = latestPurchaseDatetime;
    }

}
