package com.example.dbflute.basic.dbflute.bsbhv.pmbean;


/**
 * The parameter-bean of ResolvedPackageNamePmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsResolvedPackageNamePmb  {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of string1. */
    protected String _string1;

    /** The value of integer1. */
    protected Integer _integer1;

    /** The value of bigDecimal1. */
    protected java.math.BigDecimal _bigDecimal1;

    /** The value of bigDecimal2. */
    protected java.math.BigDecimal _bigDecimal2;

    /** The value of date1. */
    protected java.util.Date _date1;

    /** The value of date2. */
    protected java.util.Date _date2;

    /** The value of date3. */
    protected java.sql.Date _date3;

    /** The value of time1. */
    protected java.sql.Time _time1;

    /** The value of time2. */
    protected java.sql.Time _time2;

    /** The value of timestamp1. */
    protected java.sql.Timestamp _timestamp1;

    /** The value of timestamp2. */
    protected java.sql.Timestamp _timestamp2;

    /** The value of list1. */
    protected java.util.List<String> _list1;

    /** The value of list2. */
    protected java.util.List<String> _list2;

    /** The value of map1. */
    protected java.util.Map<String, String> _map1;

    /** The value of map2. */
    protected java.util.Map<String, String> _map2;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsResolvedPackageNamePmb() {
    }
    
    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * @param value Query value. (Nullable)
     * @return Converted value. (Nullable)
     */
    protected String convertEmptyToNullIfString(String value) {
        return filterRemoveEmptyString(value);
    }

    /**
     * @param value Query value string. (Nullable)
     * @return Removed-empty value. (Nullable)
     */
    protected String filterRemoveEmptyString(String value) {
        return ((value != null && !"".equals(value)) ? value : null);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * The override.
     * @return The view of properties. (NotNull)
     */
    @Override
    public String toString() {
        final String delimiter = ",";
        final StringBuffer sb = new StringBuffer();
        sb.append(delimiter).append(_string1);
        sb.append(delimiter).append(_integer1);
        sb.append(delimiter).append(_bigDecimal1);
        sb.append(delimiter).append(_bigDecimal2);
        sb.append(delimiter).append(_date1);
        sb.append(delimiter).append(_date2);
        sb.append(delimiter).append(_date3);
        sb.append(delimiter).append(_time1);
        sb.append(delimiter).append(_time2);
        sb.append(delimiter).append(_timestamp1);
        sb.append(delimiter).append(_timestamp2);
        sb.append(delimiter).append(_list1);
        sb.append(delimiter).append(_list2);
        sb.append(delimiter).append(_map1);
        sb.append(delimiter).append(_map2);
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of string1. (Converted empty to null)
     * @return The value of string1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public String getString1() {
        return (String)convertEmptyToNullIfString(_string1);
    }

    /**
     * Set the value of string1.
     * @param string1 The value of string1. (Nullable)
     */
    public void setString1(String string1) {
        _string1 = string1;
    }

    /**
     * Get the value of integer1. (Converted empty to null)
     * @return The value of integer1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public Integer getInteger1() {
        return _integer1;
    }

    /**
     * Set the value of integer1.
     * @param integer1 The value of integer1. (Nullable)
     */
    public void setInteger1(Integer integer1) {
        _integer1 = integer1;
    }

    /**
     * Get the value of bigDecimal1. (Converted empty to null)
     * @return The value of bigDecimal1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.math.BigDecimal getBigDecimal1() {
        return _bigDecimal1;
    }

    /**
     * Set the value of bigDecimal1.
     * @param bigDecimal1 The value of bigDecimal1. (Nullable)
     */
    public void setBigDecimal1(java.math.BigDecimal bigDecimal1) {
        _bigDecimal1 = bigDecimal1;
    }

    /**
     * Get the value of bigDecimal2. (Converted empty to null)
     * @return The value of bigDecimal2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.math.BigDecimal getBigDecimal2() {
        return _bigDecimal2;
    }

    /**
     * Set the value of bigDecimal2.
     * @param bigDecimal2 The value of bigDecimal2. (Nullable)
     */
    public void setBigDecimal2(java.math.BigDecimal bigDecimal2) {
        _bigDecimal2 = bigDecimal2;
    }

    /**
     * Get the value of date1. (Converted empty to null)
     * @return The value of date1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.Date getDate1() {
        return _date1;
    }

    /**
     * Set the value of date1.
     * @param date1 The value of date1. (Nullable)
     */
    public void setDate1(java.util.Date date1) {
        _date1 = date1;
    }

    /**
     * Get the value of date2. (Converted empty to null)
     * @return The value of date2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.Date getDate2() {
        return _date2;
    }

    /**
     * Set the value of date2.
     * @param date2 The value of date2. (Nullable)
     */
    public void setDate2(java.util.Date date2) {
        _date2 = date2;
    }

    /**
     * Get the value of date3. (Converted empty to null)
     * @return The value of date3. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.sql.Date getDate3() {
        return _date3;
    }

    /**
     * Set the value of date3.
     * @param date3 The value of date3. (Nullable)
     */
    public void setDate3(java.sql.Date date3) {
        _date3 = date3;
    }

    /**
     * Get the value of time1. (Converted empty to null)
     * @return The value of time1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.sql.Time getTime1() {
        return _time1;
    }

    /**
     * Set the value of time1.
     * @param time1 The value of time1. (Nullable)
     */
    public void setTime1(java.sql.Time time1) {
        _time1 = time1;
    }

    /**
     * Get the value of time2. (Converted empty to null)
     * @return The value of time2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.sql.Time getTime2() {
        return _time2;
    }

    /**
     * Set the value of time2.
     * @param time2 The value of time2. (Nullable)
     */
    public void setTime2(java.sql.Time time2) {
        _time2 = time2;
    }

    /**
     * Get the value of timestamp1. (Converted empty to null)
     * @return The value of timestamp1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.sql.Timestamp getTimestamp1() {
        return _timestamp1;
    }

    /**
     * Set the value of timestamp1.
     * @param timestamp1 The value of timestamp1. (Nullable)
     */
    public void setTimestamp1(java.sql.Timestamp timestamp1) {
        _timestamp1 = timestamp1;
    }

    /**
     * Get the value of timestamp2. (Converted empty to null)
     * @return The value of timestamp2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.sql.Timestamp getTimestamp2() {
        return _timestamp2;
    }

    /**
     * Set the value of timestamp2.
     * @param timestamp2 The value of timestamp2. (Nullable)
     */
    public void setTimestamp2(java.sql.Timestamp timestamp2) {
        _timestamp2 = timestamp2;
    }

    /**
     * Get the value of list1. (Converted empty to null)
     * @return The value of list1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.List<String> getList1() {
        return _list1;
    }

    /**
     * Set the value of list1.
     * @param list1 The value of list1. (Nullable)
     */
    public void setList1(java.util.List<String> list1) {
        _list1 = list1;
    }

    /**
     * Get the value of list2. (Converted empty to null)
     * @return The value of list2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.List<String> getList2() {
        return _list2;
    }

    /**
     * Set the value of list2.
     * @param list2 The value of list2. (Nullable)
     */
    public void setList2(java.util.List<String> list2) {
        _list2 = list2;
    }

    /**
     * Get the value of map1. (Converted empty to null)
     * @return The value of map1. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.Map<String, String> getMap1() {
        return _map1;
    }

    /**
     * Set the value of map1.
     * @param map1 The value of map1. (Nullable)
     */
    public void setMap1(java.util.Map<String, String> map1) {
        _map1 = map1;
    }

    /**
     * Get the value of map2. (Converted empty to null)
     * @return The value of map2. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public java.util.Map<String, String> getMap2() {
        return _map2;
    }

    /**
     * Set the value of map2.
     * @param map2 The value of map2. (Nullable)
     */
    public void setMap2(java.util.Map<String, String> map2) {
        _map2 = map2;
    }

}
