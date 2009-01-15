package com.example.dbflute.basic.dbflute.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The cursor of PurchaseSummaryMember.
 * @author DBFlute(AutoGenerator)
 */
public class BsPurchaseSummaryMemberCursor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // -----------------------------------------------------
    //                                        Column DB Name
    //                                        --------------
    /** DB name of MEMBER_ID. */
    public static final String DB_NAME_MEMBER_ID = "MEMBER_ID";
    /** DB name of MEMBER_NAME. */
    public static final String DB_NAME_MEMBER_NAME = "MEMBER_NAME";
    /** DB name of MEMBER_BIRTHDAY. */
    public static final String DB_NAME_MEMBER_BIRTHDAY = "MEMBER_BIRTHDAY";
    /** DB name of MEMBER_FORMALIZED_DATETIME. */
    public static final String DB_NAME_MEMBER_FORMALIZED_DATETIME = "MEMBER_FORMALIZED_DATETIME";
    /** DB name of PURCHASE_SUMMARY. */
    public static final String DB_NAME_PURCHASE_SUMMARY = "PURCHASE_SUMMARY";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Wrapped result set. */
    protected ResultSet _rs;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsPurchaseSummaryMemberCursor() {
    }

    // ===================================================================================
    //                                                                             Prepare
    //                                                                             =======
    /**
     * Accept result set.
     * @param rs Result set. (NotNull)
     */
    public void accept(ResultSet rs) {
        this._rs = rs;
    }

    // ===================================================================================
    //                                                                              Direct
    //                                                                              ======
    /**
     * Get wrapped result set.
     * @return Wrapped result set. (NotNull)
     */
    public java.sql.ResultSet cursor() {
        return _rs;
    }

    // ===================================================================================
    //                                                                            Delegate
    //                                                                            ========
    /**
     * Move to next result.
     * @return Is exist next result.
     * @throws java.sql.SQLException
     */
    public boolean next() throws SQLException {
        return _rs.next();
    }

    // ===================================================================================
    //                                                                  Type Safe Accessor
    //                                                                  ==================
    /**
     * Get the value of memberId.
     * @return The value of memberId. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getMemberId() throws SQLException {
        return (Integer)extractValueAsNumber(Integer.class, "MEMBER_ID");
    }

    /**
     * Get the value of memberName.
     * @return The value of memberName. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getMemberName() throws SQLException {
        return extractValueAsString("MEMBER_NAME");
    }

    /**
     * Get the value of memberBirthday.
     * @return The value of memberBirthday. (Nullable)
     * @throws java.sql.SQLException
     */
    public java.util.Date getMemberBirthday() throws SQLException {
        return (java.util.Date)extractValueAsDate(java.util.Date.class, "MEMBER_BIRTHDAY");
    }

    /**
     * Get the value of memberFormalizedDatetime.
     * @return The value of memberFormalizedDatetime. (Nullable)
     * @throws java.sql.SQLException
     */
    public java.sql.Timestamp getMemberFormalizedDatetime() throws SQLException {
        return (java.sql.Timestamp)extractValueAsDate(java.sql.Timestamp.class, "MEMBER_FORMALIZED_DATETIME");
    }

    /**
     * Get the value of purchaseSummary.
     * @return The value of purchaseSummary. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getPurchaseSummary() throws SQLException {
        return (Integer)extractValueAsNumber(Integer.class, "PURCHASE_SUMMARY");
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String extractValueAsString(String name) throws SQLException {
        return _rs.getString(name);
    }

    protected Boolean extractValueAsBoolean(String name) throws SQLException {
        return _rs.getBoolean(name);
    }

    protected Object extractValueAsNumber(Class<?> type, String name) throws SQLException {
        return DfTypeUtil.toNumber(type, extractValueAsObject(name));
    }

    protected Object extractValueAsDate(Class<?> type, String name) throws SQLException {
        if (type.isAssignableFrom(java.sql.Timestamp.class)) {
            return _rs.getTimestamp(name);
        } else if (type.isAssignableFrom(java.sql.Date.class)) {
            return _rs.getDate(name);
        } else if (type.isAssignableFrom(java.util.Date.class)) {
            return toDate(_rs.getTimestamp(name));
        } else {
            return toDate(extractValueAsObject(name));
        }
    }

    protected java.util.Date toDate(Object object) {
        return DfTypeUtil.toDate(object);
    }

    protected Object extractValueAsObject(String name) throws SQLException {
        return _rs.getObject(name);
    }
}
