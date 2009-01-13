package com.example.dbflute.basic.dbflute.various;

import java.sql.SQLException;

import com.example.dbflute.basic.unit.ContainerTestCase;

/**
 * @author jflute
 * @since 0.8.8 (2009/01/06 Tuesday)
 */
public class VendorMetaDataTest extends ContainerTestCase {

    // ===================================================================================
    //                                                                         Basic Thing
    //                                                                         ===========
    public void test_getDatabaseMajorVersion() throws SQLException {
        log("getDatabaseProductName(): " + getDatabaseMetaData().getDatabaseProductName());
        log("getDatabaseProductVersion(): " + getDatabaseMetaData().getDatabaseProductVersion());
        log("getDatabaseMajorVersion(): " + getDatabaseMetaData().getDatabaseMajorVersion());
        log("getDatabaseMinorVersion(): " + getDatabaseMetaData().getDatabaseMinorVersion());
    }
    
    // ===================================================================================
    //                                                                           Max Thing
    //                                                                           =========
    public void test_get_MaxThing() throws SQLException {
        log("getMaxStatementLength(): " + getDatabaseMetaData().getMaxStatementLength());
        log("getMaxTableNameLength(): " + getDatabaseMetaData().getMaxTableNameLength());
    }
}
