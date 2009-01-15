package com.example.dbflute.basic.dbflute.bsentity;

import java.util.*;

import com.example.dbflute.basic.dbflute.allcommon.CDef;
import com.example.dbflute.basic.dbflute.allcommon.EntityDefinedCommonColumn;
import org.seasar.dbflute.dbmeta.DBMeta;
import com.example.dbflute.basic.dbflute.allcommon.DBMetaInstanceHandler;
import com.example.dbflute.basic.dbflute.exentity.*;

/**
 * The entity of PURCHASE that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     PURCHASE_ID
 * 
 * [column]
 *     PURCHASE_ID, MEMBER_ID, PRODUCT_ID, PURCHASE_DATETIME, PURCHASE_COUNT, PURCHASE_PRICE, PAYMENT_COMPLETE_FLG, REGISTER_DATETIME, REGISTER_USER, REGISTER_PROCESS, UPDATE_DATETIME, UPDATE_USER, UPDATE_PROCESS, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     PURCHASE_ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign-table]
 *     MEMBER, PRODUCT, SUMMARY_PRODUCT
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     member, product, summaryProduct
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsPurchase implements EntityDefinedCommonColumn, java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** TABLE-Annotation for S2Dao. The value is PURCHASE. */
    public static final String TABLE = "PURCHASE";
    
    /** VERSION_NO-Annotation */
    public static final String VERSION_NO_PROPERTY = "versionNo";

    /** ID-Annotation */
    public static final String purchaseId_ID = "identity";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** PURCHASE_ID: {PK : ID : NotNull : BIGINT} */
    protected Long _purchaseId;

    /** MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} */
    protected Integer _memberId;

    /** PRODUCT_ID: {UQ : NotNull : INTEGER : FK to PRODUCT} */
    protected Integer _productId;

    /** PURCHASE_DATETIME: {UQ : NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _purchaseDatetime;

    /** PURCHASE_COUNT: {NotNull : INTEGER} */
    protected Integer _purchaseCount;

    /** PURCHASE_PRICE: {NotNull : INTEGER} */
    protected Integer _purchasePrice;

    /** PAYMENT_COMPLETE_FLG: {NotNull : INTEGER} */
    protected Integer _paymentCompleteFlg;

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
    public BsPurchase() {
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "PURCHASE";
    }

    public String getTablePropertyName() {// as JavaBeansRule
        return "purchase";
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
    /**
     * Classify the value of paymentCompleteFlg as the classification of Flg. <br />
     * フラグを示す
     * @param cls The value of paymentCompleteFlg as the classification of Flg. (Nullable)
     */
    public void classifyPaymentCompleteFlg(CDef.Flg cls) {
        setPaymentCompleteFlg(cls != null ? new Integer(cls.code()) : null);
    }

    /**
     * Classify the value of paymentCompleteFlg as True. <br />
     * はい: 有効を示す
     */
    public void classifyPaymentCompleteFlgTrue() {
        classifyPaymentCompleteFlg(CDef.Flg.True);
    }

    /**
     * Classify the value of paymentCompleteFlg as False. <br />
     * いいえ: 無効を示す
     */
    public void classifyPaymentCompleteFlgFalse() {
        classifyPaymentCompleteFlg(CDef.Flg.False);
    }

    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    /**
     * Get the value of paymentCompleteFlg as the classification of Flg. <br />
     * フラグを示す
     * @return The value of paymentCompleteFlg as the classification of Flg. (Nullable)
     */
    public CDef.Flg getPaymentCompleteFlgAsFlg() {
        return CDef.Flg.codeOf(_paymentCompleteFlg);
    }

    /**
     * Is the value of the column 'paymentCompleteFlg' 'True'? <br />
     * はい: 有効を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isPaymentCompleteFlgTrue() {
        CDef.Flg cls = getPaymentCompleteFlgAsFlg();
        return cls != null ? cls.equals(CDef.Flg.True) : false;
    }

    /**
     * Is the value of the column 'paymentCompleteFlg' 'False'? <br />
     * いいえ: 無効を示す
     * <pre>
     * The difference of capital letters and small letters is NOT distinguished.
     * If the value is null, this method returns false!
     * </pre>
     * @return Determination.
     */
    public boolean isPaymentCompleteFlgFalse() {
        CDef.Flg cls = getPaymentCompleteFlgAsFlg();
        return cls != null ? cls.equals(CDef.Flg.False) : false;
    }

    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    /**
     * Get the value of the column 'paymentCompleteFlg' as classification name.
     * @return The value of the column 'paymentCompleteFlg' as classification name. (Nullable)
     */
    public String getPaymentCompleteFlgName() {
        CDef.Flg cls = getPaymentCompleteFlgAsFlg();
        return cls != null ? cls.name() : null;
    }

    /**
     * Get the value of the column 'paymentCompleteFlg' as classification alias.
     * @return The value of the column 'paymentCompleteFlg' as classification alias. (Nullable)
     */
    public String getPaymentCompleteFlgAlias() {
        CDef.Flg cls = getPaymentCompleteFlgAsFlg();
        return cls != null ? cls.alias() : null;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [member]
    // * * * * * * * * */
    public static final int member_RELNO = 0;
    public static final String member_RELKEYS = "MEMBER_ID:MEMBER_ID";

    /** MEMBER as 'member'. */
    protected Member _parentMember;

    /**
     * MEMBER as 'member'. {without lazy-load}
     * @return The entity of foreign property 'member'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public Member getMember() {
        return _parentMember;
    }

    /**
     * MEMBER as 'member'.
     * @param member The entity of foreign property 'member'. (Nullable)
     */
    public void setMember(Member member) {
        _parentMember = member;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [product]
    // * * * * * * * * */
    public static final int product_RELNO = 1;
    public static final String product_RELKEYS = "PRODUCT_ID:PRODUCT_ID";

    /** PRODUCT as 'product'. */
    protected Product _parentProduct;

    /**
     * PRODUCT as 'product'. {without lazy-load}
     * @return The entity of foreign property 'product'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public Product getProduct() {
        return _parentProduct;
    }

    /**
     * PRODUCT as 'product'.
     * @param product The entity of foreign property 'product'. (Nullable)
     */
    public void setProduct(Product product) {
        _parentProduct = product;
    }

    // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //   Foreign Property = [summaryProduct]
    // * * * * * * * * */
    public static final int summaryProduct_RELNO = 2;
    public static final String summaryProduct_RELKEYS = "PRODUCT_ID:PRODUCT_ID";

    /** SUMMARY_PRODUCT as 'summaryProduct'. */
    protected SummaryProduct _parentSummaryProduct;

    /**
     * SUMMARY_PRODUCT as 'summaryProduct'. {without lazy-load}
     * @return The entity of foreign property 'summaryProduct'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public SummaryProduct getSummaryProduct() {
        return _parentSummaryProduct;
    }

    /**
     * SUMMARY_PRODUCT as 'summaryProduct'.
     * @param summaryProduct The entity of foreign property 'summaryProduct'. (Nullable)
     */
    public void setSummaryProduct(SummaryProduct summaryProduct) {
        _parentSummaryProduct = summaryProduct;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_purchaseId == null) { return false; }
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
        if (other == null || !(other instanceof BsPurchase)) { return false; }
        BsPurchase otherEntity = (BsPurchase)other;
        if (!helpComparingValue(getPurchaseId(), otherEntity.getPurchaseId())) { return false; }
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
        if (this.getPurchaseId() != null) { result = result + getPurchaseId().hashCode(); }
        return result;
    }

    /**
     * @return The view string of columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getPurchaseId());
        sb.append(delimiter).append(getMemberId());
        sb.append(delimiter).append(getProductId());
        sb.append(delimiter).append(getPurchaseDatetime());
        sb.append(delimiter).append(getPurchaseCount());
        sb.append(delimiter).append(getPurchasePrice());
        sb.append(delimiter).append(getPaymentCompleteFlg());
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

    /** The column annotation for S2Dao. {PK : ID : NotNull : BIGINT} */
    public static final String purchaseId_COLUMN = "PURCHASE_ID";

    /**
     * PURCHASE_ID: {PK : ID : NotNull : BIGINT} <br />
     * @return The value of the column 'PURCHASE_ID'. (Nullable)
     */
    public Long getPurchaseId() {
        return _purchaseId;
    }

    /**
     * PURCHASE_ID: {PK : ID : NotNull : BIGINT} <br />
     * @param purchaseId The value of the column 'PURCHASE_ID'. (Nullable)
     */
    public void setPurchaseId(Long purchaseId) {
        _modifiedProperties.addPropertyName("purchaseId");
        this._purchaseId = purchaseId;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : INTEGER : FK to MEMBER} */
    public static final String memberId_COLUMN = "MEMBER_ID";

    /**
     * MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} <br />
     * @return The value of the column 'MEMBER_ID'. (Nullable)
     */
    public Integer getMemberId() {
        return _memberId;
    }

    /**
     * MEMBER_ID: {UQ : NotNull : INTEGER : FK to MEMBER} <br />
     * @param memberId The value of the column 'MEMBER_ID'. (Nullable)
     */
    public void setMemberId(Integer memberId) {
        _modifiedProperties.addPropertyName("memberId");
        this._memberId = memberId;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : INTEGER : FK to PRODUCT} */
    public static final String productId_COLUMN = "PRODUCT_ID";

    /**
     * PRODUCT_ID: {UQ : NotNull : INTEGER : FK to PRODUCT} <br />
     * @return The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public Integer getProductId() {
        return _productId;
    }

    /**
     * PRODUCT_ID: {UQ : NotNull : INTEGER : FK to PRODUCT} <br />
     * @param productId The value of the column 'PRODUCT_ID'. (Nullable)
     */
    public void setProductId(Integer productId) {
        _modifiedProperties.addPropertyName("productId");
        this._productId = productId;
    }

    /** The column annotation for S2Dao. {UQ : NotNull : TIMESTAMP} */
    public static final String purchaseDatetime_COLUMN = "PURCHASE_DATETIME";

    /**
     * PURCHASE_DATETIME: {UQ : NotNull : TIMESTAMP} <br />
     * @return The value of the column 'PURCHASE_DATETIME'. (Nullable)
     */
    public java.sql.Timestamp getPurchaseDatetime() {
        return _purchaseDatetime;
    }

    /**
     * PURCHASE_DATETIME: {UQ : NotNull : TIMESTAMP} <br />
     * @param purchaseDatetime The value of the column 'PURCHASE_DATETIME'. (Nullable)
     */
    public void setPurchaseDatetime(java.sql.Timestamp purchaseDatetime) {
        _modifiedProperties.addPropertyName("purchaseDatetime");
        this._purchaseDatetime = purchaseDatetime;
    }

    /** The column annotation for S2Dao. {NotNull : INTEGER} */
    public static final String purchaseCount_COLUMN = "PURCHASE_COUNT";

    /**
     * PURCHASE_COUNT: {NotNull : INTEGER} <br />
     * @return The value of the column 'PURCHASE_COUNT'. (Nullable)
     */
    public Integer getPurchaseCount() {
        return _purchaseCount;
    }

    /**
     * PURCHASE_COUNT: {NotNull : INTEGER} <br />
     * @param purchaseCount The value of the column 'PURCHASE_COUNT'. (Nullable)
     */
    public void setPurchaseCount(Integer purchaseCount) {
        _modifiedProperties.addPropertyName("purchaseCount");
        this._purchaseCount = purchaseCount;
    }

    /** The column annotation for S2Dao. {NotNull : INTEGER} */
    public static final String purchasePrice_COLUMN = "PURCHASE_PRICE";

    /**
     * PURCHASE_PRICE: {NotNull : INTEGER} <br />
     * @return The value of the column 'PURCHASE_PRICE'. (Nullable)
     */
    public Integer getPurchasePrice() {
        return _purchasePrice;
    }

    /**
     * PURCHASE_PRICE: {NotNull : INTEGER} <br />
     * @param purchasePrice The value of the column 'PURCHASE_PRICE'. (Nullable)
     */
    public void setPurchasePrice(Integer purchasePrice) {
        _modifiedProperties.addPropertyName("purchasePrice");
        this._purchasePrice = purchasePrice;
    }

    /** The column annotation for S2Dao. {NotNull : INTEGER} */
    public static final String paymentCompleteFlg_COLUMN = "PAYMENT_COMPLETE_FLG";

    /**
     * PAYMENT_COMPLETE_FLG: {NotNull : INTEGER} <br />
     * @return The value of the column 'PAYMENT_COMPLETE_FLG'. (Nullable)
     */
    public Integer getPaymentCompleteFlg() {
        return _paymentCompleteFlg;
    }

    /**
     * PAYMENT_COMPLETE_FLG: {NotNull : INTEGER} <br />
     * @param paymentCompleteFlg The value of the column 'PAYMENT_COMPLETE_FLG'. (Nullable)
     */
    public void setPaymentCompleteFlg(Integer paymentCompleteFlg) {
        _modifiedProperties.addPropertyName("paymentCompleteFlg");
        this._paymentCompleteFlg = paymentCompleteFlg;
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
