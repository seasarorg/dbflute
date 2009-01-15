package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of PRODUCT_STATUS that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PRODUCT_STATUS_CODE
 * 
 * [column]
 *     PRODUCT_STATUS_CODE, PRODUCT_STATUS_NAME
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
 *     PRODUCT, SUMMARY_PRODUCT
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     productList, summaryProductList
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsProductStatus implements Entity, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is PRODUCT_STATUS. */
    public static final String TABLE = "PRODUCT_STATUS";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** PRODUCT_STATUS_CODE: {PK : NotNull : CHAR(3)} */
    protected String _productStatusCode;

    /** PRODUCT_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} */
    protected String _productStatusName;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsProductStatus() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "PRODUCT_STATUS";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "productStatus";
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
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [productList]
    // * * * * * * * * */
    /** PRODUCT as 'productList'. */
    protected List<Product> _childrenProductList;

    /**
     * PRODUCT as 'productList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'productList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<Product> getProductList() {
        if (_childrenProductList == null) { _childrenProductList = new ArrayList<Product>(); }
        return _childrenProductList;
    }

    /**
     * PRODUCT as 'productList'.
     * @param productList The entity list of referrer property 'productList'. (Nullable)
     */
    public void setProductList(List<Product> productList) {
        _childrenProductList = productList;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Referrer Property = [summaryProductList]
    // * * * * * * * * */
    /** SUMMARY_PRODUCT as 'summaryProductList'. */
    protected List<SummaryProduct> _childrenSummaryProductList;

    /**
     * SUMMARY_PRODUCT as 'summaryProductList'. {without lazy-load} <br />
     * @return The entity list of referrer property 'summaryProductList'. (NotNull: If it's not loaded yet, initializes the list instance of referrer as empty and returns it.)
     */
    public List<SummaryProduct> getSummaryProductList() {
        if (_childrenSummaryProductList == null) { _childrenSummaryProductList = new ArrayList<SummaryProduct>(); }
        return _childrenSummaryProductList;
    }

    /**
     * SUMMARY_PRODUCT as 'summaryProductList'.
     * @param summaryProductList The entity list of referrer property 'summaryProductList'. (Nullable)
     */
    public void setSummaryProductList(List<SummaryProduct> summaryProductList) {
        _childrenSummaryProductList = summaryProductList;
    }


    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_productStatusCode == null) { return false; }
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
        if (other == null || !(other instanceof BsProductStatus)) { return false; }
        BsProductStatus otherEntity = (BsProductStatus)other;
        if (!helpComparingValue(getProductStatusCode(), otherEntity.getProductStatusCode())) { return false; }
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
        if (this.getProductStatusCode() != null) { result = result + getProductStatusCode().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getProductStatusCode());
        sb.append(delimiter).append(getProductStatusName());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========

    /** The column annotation for S2Dao. {PK : NotNull : CHAR(3)} */
    public static final String productStatusCode_COLUMN = "PRODUCT_STATUS_CODE";

    /**
     * PRODUCT_STATUS_CODE: {PK : NotNull : CHAR(3)} <br />
     * @return The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public String getProductStatusCode() {
        return _productStatusCode;
    }

    /**
     * PRODUCT_STATUS_CODE: {PK : NotNull : CHAR(3)} <br />
     * @param productStatusCode The value of the column 'PRODUCT_STATUS_CODE'. (Nullable)
     */
    public void setProductStatusCode(String productStatusCode) {
        _modifiedProperties.addPropertyName("productStatusCode");
        this._productStatusCode = productStatusCode;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : VARCHAR(50)} */
    public static final String productStatusName_COLUMN = "PRODUCT_STATUS_NAME";

    /**
     * PRODUCT_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} <br />
     * @return The value of the column 'PRODUCT_STATUS_NAME'. (Nullable)
     */
    public String getProductStatusName() {
        return _productStatusName;
    }

    /**
     * PRODUCT_STATUS_NAME: {UQ : NotNull : VARCHAR(50)} <br />
     * @param productStatusName The value of the column 'PRODUCT_STATUS_NAME'. (Nullable)
     */
    public void setProductStatusName(String productStatusName) {
        _modifiedProperties.addPropertyName("productStatusName");
        this._productStatusName = productStatusName;
    }

}
