/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.logic.various;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfDBFluteTaskFailureException;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * Ant task utility.
 * @author jflute
 */
public final class DfAntTaskUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(DfAntTaskUtil.class);

    // ===================================================================================
    //                                                             Build-Properties Set up
    //                                                             =======================
    /**
     * Get the property object that saves 'build-properties'.
     * @param file File-full-path-comma-string.
     * @param project Project-instance of ANT.
     * @return Context-properties.
     */
    public static Properties getBuildProperties(String file, Project project) {
        final Properties prop = new Properties();
        try {
            final String sources[] = DfStringUtil.splitList(file, ",").toArray(new String[] {});
            for (int i = 0; i < sources.length; i++) {
                final String source = sources[i];
                final Properties currentProp = new Properties();
                FileInputStream fis = null;
                try {
                    final File currentDirFile = new File(source);
                    final File targetFile;
                    if (currentDirFile.exists()) { // basically true
                        // from DBFlute client directory
                        targetFile = currentDirFile;
                    } else {
                        // from DBFlute module directory (old style)
                        targetFile = project.resolveFile(source);
                    }
                    _log.info("...Using contextProperties: " + targetFile);
                    fis = new FileInputStream(targetFile);
                    currentProp.load(fis);
                } catch (IOException e) {
                    // retry getting from class-path (basically unused)
                    final ClassLoader classLoader = project.getClass().getClassLoader();
                    InputStream ins = null;
                    try {
                        ins = classLoader.getResourceAsStream(source);
                        if (ins == null) {
                            String msg = "Context properties file " + source;
                            msg = msg + " could not be found in the file system or on the classpath!";
                            throw new BuildException(msg, e);
                        }
                        currentProp.load(ins);
                    } catch (IOException ignored) {
                        String msg = "Failed to load contextProperties:";
                        msg = msg + " file=" + source + " project=" + project;
                        throw new BuildException(msg, e);
                    } finally {
                        if (ins != null) {
                            try {
                                ins.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                final Set<Entry<Object, Object>> entrySet = currentProp.entrySet();
                for (Entry<Object, Object> entry : entrySet) {
                    prop.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }

            // show properties
            final Set<Entry<Object, Object>> entrySet = prop.entrySet();
            _log.info("[Build-Properties]: size=" + prop.size());
            for (Entry<Object, Object> entry : entrySet) {
                _log.info("  " + entry.getKey() + " = " + entry.getValue());
            }
            _log.info("");
        } catch (RuntimeException e) {
            String msg = "Look! Read the message below." + ln();
            msg = msg + "/- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
            msg = msg + "Failed to get build-properties!" + ln();
            msg = msg + ln();
            msg = msg + "[Advice]" + ln();
            msg = msg + "Check the existence of build.properties on DBFlute client directory." + ln();
            msg = msg + ln();
            msg = msg + "[File Name]" + ln() + file + ln();
            msg = msg + ln();
            msg = msg + "[Project]" + ln() + project + ln();
            msg = msg + "- - - - - - - - - -/";
            throw new IllegalStateException(msg, e);
        }
        return prop;
    }

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public static void logException(Exception e, String taskName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to execute DBFlute Task '" + taskName + "'!" + ln();
        msg = msg + ln();
        msg = msg + "[Exception]" + ln();
        msg = msg + "exception class   = " + e.getClass() + ln();
        msg = msg + "* * * * * * * * * */";
        _log.error(msg, e);
    }

    public static void logError(Error e, String taskName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to execute DBFlute Task '" + taskName + "'!" + ln();
        msg = msg + ln();
        msg = msg + "[Error]" + ln();
        msg = msg + "error class   = " + e.getClass() + ln();
        msg = msg + "* * * * * * * * * */";
        _log.error(msg, e);
    }

    public static String getDisplayTaskName(String taskName) {
        if (taskName.endsWith("df-jdbc")) {
            return "JDBC";
        } else if (taskName.equals("df-doc")) {
            return "Doc";
        } else if (taskName.equals("df-generate")) {
            return "Generate";
        } else if (taskName.equals("df-sql2entity")) {
            return "Sql2Entity";
        } else if (taskName.equals("df-outside-sql-test")) {
            return "OutsideSqlTest";
        } else if (taskName.equals("df-create-schema")) {
            return "ReplaceSchema";
        } else if (taskName.equals("df-load-data")) {
            return "ReplaceSchema";
        } else if (taskName.equals("df-take-finally")) {
            return "ReplaceSchema";
        } else {
            return taskName;
        }
    }

    public static void throwTaskFailure(String displayTaskName) {
        String msg = ln() + "/* * * * * * * * * * * * * * * * * * * * * * * * *";
        msg = msg + ln() + "Failed to execute DBFlute task: " + displayTaskName;
        msg = msg + ln() + "Look at the log: console or dbflute.log";
        msg = msg + ln() + "* * * * * * * * * */";
        throw new DfDBFluteTaskFailureException(msg);
    }

    // ===================================================================================
    //                                                                          Connection
    //                                                                          ==========
    public static void shutdownIfDerbyEmbedded(String driver) throws SQLException {
        if (!driver.startsWith("org.apache.derby.") || !driver.endsWith(".EmbeddedDriver")) {
            return;
        }
        final String shutdownUrl = "jdbc:derby:;shutdown=true";
        try {
            _log.info("...Shutting down the connection to Derby");
            DriverManager.getConnection(shutdownUrl);
        } catch (SQLException e) {
            if ("XJ015".equals(e.getSQLState())) {
                _log.info(" --> success: " + e.getMessage());
            } else {
                String msg = "Failed to shut down the connection to Derby:";
                msg = msg + " shutdownUrl=" + shutdownUrl;
                throw new DfJDBCException(msg, e);
            }
        }
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected static DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected static DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected static DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}