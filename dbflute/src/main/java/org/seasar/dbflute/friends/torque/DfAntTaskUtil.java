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
package org.seasar.dbflute.friends.torque;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfDBFluteTaskFailureException;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * Ant task utility.
 * @author jflute
 */
public final class DfAntTaskUtil {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(DfAntTaskUtil.class);

    /**
     * Get the property object that saves 'build-properties'.
     * Copy from TexenTask#setContextProperties(). A little modified...
     * @param file File-full-path-comma-string.
     * @param project Project-instance of ANT.
     * @return Context-properties.
     */
    @SuppressWarnings("unchecked")
    public static Properties getBuildProperties(String file, Project project) {
        final Properties prop = new Properties();
        try {
            // /---------------------------------------------------------------------------------------------------
            // Initialize torque properties as Properties and set up singleton class that saves 'build.properties'.
            //   This property is used by You. 
            // -------/
            final String sources[] = StringUtils.split(file, ",");
            for (int i = 0; i < sources.length; i++) {
                final Properties source = new Properties();
                FileInputStream fis = null;
                try {
                    final File fullPath = project.resolveFile(sources[i]);
                    _log.info("Using contextProperties file: " + fullPath);
                    fis = new FileInputStream(fullPath);
                    source.load(fis);
                } catch (IOException e) {
                    final ClassLoader classLoader = project.getClass().getClassLoader();
                    try {
                        final java.io.InputStream inputStream = classLoader.getResourceAsStream(sources[i]);
                        if (inputStream == null) {
                            String msg = "Context properties file " + sources[i];
                            msg = msg + " could not be found in the file system or on the classpath!";
                            throw new BuildException(msg);
                        }
                        source.load(inputStream);
                    } catch (IOException ioe) {
                        throw new RuntimeException("InputStream threw the exception!", ioe);
                    }
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                for (final Iterator ite = source.keySet().iterator(); ite.hasNext();) {
                    final String key = (String) ite.next();
                    final String value = source.getProperty(key);

                    prop.setProperty(key, value);
                }
            }

            // Initialize build-properties!
            DfBuildProperties.getInstance().setProperties(prop);

            // Show properties!
            _log.info("[Properties]: size=" + prop.size());
            for (final Iterator ite = prop.keySet().iterator(); ite.hasNext();) {
                final String key = (String) ite.next();
                final String value = prop.getProperty(key);
                _log.info("    " + key + " = " + value);
            }
        } catch (RuntimeException e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
            throw new IllegalStateException("buildContextProperties() threw the exception!", e);
        }
        return prop;
    }

    public static void logException(Exception e, String taskName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to execute DBFlute Task '" + taskName + "'!" + ln();
        msg = msg + ln();
        msg = msg + "[Basic Properties]" + ln();
        msg = msg + "database  = " + getBasicProperties().getDatabaseType() + ln();
        msg = msg + "language  = " + getBasicProperties().getTargetLanguage() + ln();
        msg = msg + "container = " + getBasicProperties().getTargetContainerName() + ln();
        msg = msg + ln();
        msg = msg + "[Database Properties]" + ln();
        msg = msg + "driver = " + getDatabaseProperties().getDatabaseDriver() + ln();
        msg = msg + "url    = " + getDatabaseProperties().getDatabaseUrl() + ln();
        msg = msg + "schema = " + getDatabaseProperties().getDatabaseSchema() + ln();
        msg = msg + "user   = " + getDatabaseProperties().getDatabaseUser() + ln();
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
        msg = msg + "[Basic Properties]" + ln();
        msg = msg + "database  = " + getBasicProperties().getDatabaseType() + ln();
        msg = msg + "language  = " + getBasicProperties().getTargetLanguage() + ln();
        msg = msg + "container = " + getBasicProperties().getTargetContainerName() + ln();
        msg = msg + ln();
        msg = msg + "[Database Properties]" + ln();
        msg = msg + "driver = " + getDatabaseProperties().getDatabaseDriver() + ln();
        msg = msg + "url    = " + getDatabaseProperties().getDatabaseUrl() + ln();
        msg = msg + "schema = " + getDatabaseProperties().getDatabaseSchema() + ln();
        msg = msg + "user   = " + getDatabaseProperties().getDatabaseUser() + ln();
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

    protected static DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected static DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected static DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    protected static String ln() {
        return System.getProperty("line.separator");
    }
}