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
package org.seasar.dbflute.torque;

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
                try {
                    final File fullPath = project.resolveFile(sources[i]);
                    _log.info("Using contextProperties file: " + fullPath);
                    source.load(new FileInputStream(fullPath));
                } catch (Exception e) {
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
        } catch (Exception e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
            throw new IllegalStateException("buildContextProperties() threw the exception!", e);
        }
        return prop;
    }

    public static void logRuntimeException(RuntimeException e, String taskName) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "Failed to execute DBFlute Task '" + taskName + "'!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Basic Properties]" + getLineSeparator();
        msg = msg + "database  = " + getBasicProperties().getDatabaseName() + getLineSeparator();
        msg = msg + "language  = " + getBasicProperties().getTargetLanguage() + getLineSeparator();
        msg = msg + "container = " + getBasicProperties().getTargetContainerName() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Database Properties]" + getLineSeparator();
        msg = msg + "driver = " + getDatabaseProperties().getDatabaseDriver() + getLineSeparator();
        msg = msg + "url    = " + getDatabaseProperties().getDatabaseUrl() + getLineSeparator();
        msg = msg + "schema = " + getDatabaseProperties().getDatabaseSchema() + getLineSeparator();
        msg = msg + "user   = " + getDatabaseProperties().getDatabaseUser() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Runtime Exception]" + getLineSeparator();
        msg = msg + "exception class   = " + e.getClass() + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        _log.error(msg, e);
    }

    public static void logError(Error e, String taskName) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "Failed to execute DBFlute Task '" + taskName + "'!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Basic Properties]" + getLineSeparator();
        msg = msg + "database  = " + getBasicProperties().getDatabaseName() + getLineSeparator();
        msg = msg + "language  = " + getBasicProperties().getTargetLanguage() + getLineSeparator();
        msg = msg + "container = " + getBasicProperties().getTargetContainerName() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Database Properties]" + getLineSeparator();
        msg = msg + "driver = " + getDatabaseProperties().getDatabaseDriver() + getLineSeparator();
        msg = msg + "url    = " + getDatabaseProperties().getDatabaseUrl() + getLineSeparator();
        msg = msg + "schema = " + getDatabaseProperties().getDatabaseSchema() + getLineSeparator();
        msg = msg + "user   = " + getDatabaseProperties().getDatabaseUser() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Error]" + getLineSeparator();
        msg = msg + "error class   = " + e.getClass() + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        _log.error(msg, e);
    }

    public static String getDisplayTaskName(String taskName) {
        if (taskName.endsWith("jdbc-transform")) {
            return "JDBC";
        } else if (taskName.endsWith("doc")) {
            return "Doc";
        } else if (taskName.endsWith("data-model")) {
            return "Generate";
        } else if (taskName.endsWith("sql2entity")) {
            return "Sql2Entity";
        } else if (taskName.endsWith("outside-sql-test")) {
            return "OutsideSqlTest";
        } else if (taskName.endsWith("create-schema")) {
            return "ReplaceSchema";
        } else if (taskName.endsWith("load-data")) {
            return "ReplaceSchema";
        } else if (taskName.endsWith("take-finally")) {
            return "ReplaceSchema";
        } else {
            return taskName;
        }
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

    protected static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}