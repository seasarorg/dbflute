package org.apache.torque.helper.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.helper.stateless.FlPropertyUtil;
import org.apache.torque.helper.stateless.NameHintUtil;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyBooleanFormatException;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyIntegerFormatException;
import org.apache.torque.helper.stateless.FlPropertyUtil.PropertyNotFoundException;

/**
 * Build properties for Torque.
 * 
 * @author mkubo
 */
public final class BasicProperties extends AbstractHelperProperties {

    /** Log-instance */
    private static final Log _log = LogFactory.getLog(BasicProperties.class);

    /**
     * Constructor.
     */
    public BasicProperties(Properties prop) {
        super(prop);
    }

    // **********************************************************************************************
    //                                                                                       Property
    //                                                                                       ********
    // ===============================================================================
    //                                                            Properties - Project
    //                                                            ====================
    public String getProjectName() {
        return stringProp("torque.project", "");
    }

    // ===============================================================================
    //                                                           Properties - Database
    //                                                           =====================
    public String getDatabaseName() {
        return stringProp("torque.database", "");
    }

    // ===============================================================================
    //                                                            Properties - JavaDir
    //                                                            ====================
    public String getJavaDir() {
        return stringProp("torque.java.dir", "");
    }

    public String getJavaLocation_for_gen() {
        return stringProp("torque.java.location.for.gen", "");
    }

    public String getJavaLocation_for_main() {
        return stringProp("torque.java.location.for.main", "");
    }

    public String getJavaDir_for_gen() {
        final String fileSeparator = "/";
        final String javaDirBase = getJavaDir();
        final String javaLocation = getJavaLocation_for_gen();
        String outputPath = "";
        if (javaDirBase != null && javaDirBase.endsWith(fileSeparator)) {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation.substring(fileSeparator.length());
            } else {
                outputPath = javaDirBase + javaLocation;
            }
        } else {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation;
            } else {
                outputPath = javaDirBase + fileSeparator + javaLocation;
            }
        }
        return outputPath;
    }

    public String getJavaDir_for_main() {
        final String fileSeparator = "/";
        final String javaDirBase = getJavaDir();
        final String javaLocation = getJavaLocation_for_main();
        String outputPath = "";
        if (javaDirBase != null && javaDirBase.endsWith(fileSeparator)) {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation.substring(fileSeparator.length());
            } else {
                outputPath = javaDirBase + javaLocation;
            }
        } else {
            if (javaLocation != null && javaLocation.startsWith(fileSeparator)) {
                outputPath = javaDirBase + javaLocation;
            } else {
                outputPath = javaDirBase + fileSeparator + javaLocation;
            }
        }
        return outputPath;
    }

    // ===============================================================================
    //                                                           Properties - Language
    //                                                           =====================
    public String getTargetLanguage() {
        return stringProp("torque.targetLanguage", DEFAULT_targetLanguage);
    }

    public boolean isTargetLanguageJava() {
        return JAVA_targetLanguage.equals(getTargetLanguage());
    }

    public boolean isTargetLanguageCSharp() {
        return CSHARP_targetLanguage.equals(getTargetLanguage());
    }

    // ===============================================================================
    //                                                          Properties - Extension
    //                                                          ======================
    public String getTemplateFileExtension() {
        if (JAVA_targetLanguage.equalsIgnoreCase(getTargetLanguage())) {
            return JAVA_templateFileExtension;
        } else if (CSHARP_targetLanguage.equalsIgnoreCase(getTargetLanguage())) {
            return CSHARP_templateFileExtension;
        } else {
            return DEFAULT_templateFileExtension;
        }
    }

    public String getClassFileExtension() {
        if (JAVA_targetLanguage.equalsIgnoreCase(getTargetLanguage())) {
            return JAVA_classFileExtension;
        } else if (CSHARP_targetLanguage.equalsIgnoreCase(getTargetLanguage())) {
            return CSHARP_classFileExtension;
        } else {
            return DEFAULT_classFileExtension;
        }
    }
    
    // ===============================================================================
    //                                                           Properties - Encoding
    //                                                           =====================
    public String getTemplateFileEncoding() {
        return stringProp("torque.templateFileEncoding", DEFAULT_templateFileEncoding);
    }

    // ===============================================================================
    //                                                             Properties - Author
    //                                                             ===================
    public String getClassAuthor() {
        return stringProp("torque.classAuthor", "DBFlute(AutoGenerator)");
    }
    
    // ===============================================================================
    //                                                             Properties - SameAs
    //                                                             ===================
    public boolean isJavaNameOfTableSameAsDbName() {
        return booleanProp("torque.isJavaNameOfTableSameAsDbName", false);
    }

    public boolean isJavaNameOfColumnSameAsDbName() {
        return booleanProp("torque.isJavaNameOfColumnSameAsDbName", false);
    }
    
    // ===============================================================================
    //                                                          Properties - Available
    //                                                          ======================
    public boolean isAvailableEntityLazyLoad() {
        return booleanProp("torque.isAvailableEntityLazyLoad", false);
    }

    public boolean isAvailableBehaviorGeneration() {
        return booleanProp("torque.isAvailableBehaviorGeneration", false);
    }

    public boolean isAvailableCommonColumnSetupInterceptorToBehavior() {
        return booleanProp("torque.isAvailableCommonColumnSetupInterceptorToBehavior", false);
    }

    public boolean isAvailableCommonColumnSetupInterceptorToDao() {
        return booleanProp("torque.isAvailableCommonColumnSetupInterceptorToDao", false);
    }

    public boolean isAvailableGenerics() {
        return booleanProp("torque.isAvailableGenerics", true);
    }

    public String filterGenericsString(String genericsString) {
        if (isAvailableGenerics()) {
            return "<" + genericsString + ">";
        } else {
            return "";
        }
    }
    
    // ===============================================================================
    //                                                             Properties - Prefix
    //                                                             ===================
    public String getProjectPrefix() {
        return stringProp("torque.projectPrefix", "");
    }
    
    // ===============================================================================
    //                                                      Properties - Database Info
    //                                                      ==========================
    public String getDatabaseDriver() {
        return stringProp("torque.database.driver");
    }

    public String getDatabaseUri() {
        return stringProp("torque.database.url");
    }

    public String getDatabaseUser() {
        return stringProp("torque.database.user");
    }

    public String getDatabasePassword() {
        return stringProp("torque.database.password");
    }

    public Connection getConnection() {
        try {
            Class.forName(getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return DriverManager.getConnection(getDatabaseUri(), getDatabaseUser(), getDatabasePassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}