package com.example.dbflute.basic.dbflute.allcommon;

import org.dbflute.Entity;

/**
 * The interface of entity defined common column.
 * @author DBFlute(AutoGenerator)
 */
public interface EntityDefinedCommonColumn extends Entity {

    /**
     * Get the value of registerDatetime.
     * @return The value of registerDatetime. (Nullable)
     */
    public java.sql.Timestamp getRegisterDatetime();

    /**
     * Set the value of registerDatetime.
     * @param registerDatetime The value of registerDatetime. (Nullable)
     */
    public void setRegisterDatetime(java.sql.Timestamp registerDatetime);

    /**
     * Get the value of registerUser.
     * @return The value of registerUser. (Nullable)
     */
    public String getRegisterUser();

    /**
     * Set the value of registerUser.
     * @param registerUser The value of registerUser. (Nullable)
     */
    public void setRegisterUser(String registerUser);

    /**
     * Get the value of registerProcess.
     * @return The value of registerProcess. (Nullable)
     */
    public String getRegisterProcess();

    /**
     * Set the value of registerProcess.
     * @param registerProcess The value of registerProcess. (Nullable)
     */
    public void setRegisterProcess(String registerProcess);

    /**
     * Get the value of updateDatetime.
     * @return The value of updateDatetime. (Nullable)
     */
    public java.sql.Timestamp getUpdateDatetime();

    /**
     * Set the value of updateDatetime.
     * @param updateDatetime The value of updateDatetime. (Nullable)
     */
    public void setUpdateDatetime(java.sql.Timestamp updateDatetime);

    /**
     * Get the value of updateUser.
     * @return The value of updateUser. (Nullable)
     */
    public String getUpdateUser();

    /**
     * Set the value of updateUser.
     * @param updateUser The value of updateUser. (Nullable)
     */
    public void setUpdateUser(String updateUser);

    /**
     * Get the value of updateProcess.
     * @return The value of updateProcess. (Nullable)
     */
    public String getUpdateProcess();

    /**
     * Set the value of updateProcess.
     * @param updateProcess The value of updateProcess. (Nullable)
     */
    public void setUpdateProcess(String updateProcess);

    /**
	 * Enable common column auto set up. {for after disable because the default is enabled}
	 */
    public void enableCommonColumnAutoSetup();

    /**
	 * Disable common column auto set up.
	 */
    public void disableCommonColumnAutoSetup();
	
    /**
	 * Can the entity set up common column by auto?
	 * @return Determination.
	 */
	public boolean canCommonColumnAutoSetup();
}
