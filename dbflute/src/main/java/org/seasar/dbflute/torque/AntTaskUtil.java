/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.TorqueBuildProperties;

/**
 * Torque task utility.
 * 
 * @author mkubo
 */
public final class AntTaskUtil {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(AntTaskUtil.class);

    /**
     * Get the property object that saves 'build-properties'.
     * <p>
     * Copy from TexenTask#setContextProperties(). A little modified...
     * 
     * @param file File-full-path-comma-string.
     * @param project Project-instance of ANT.
     * @return Context-properties.
     */
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
                    _log.debug("Using contextProperties file: " + fullPath);
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
            TorqueBuildProperties.getInstance().setProperties(prop);

            _log.debug("[Properties]: size=" + prop.size());
            for (final Iterator ite = prop.keySet().iterator(); ite.hasNext();) {
                final String key = (String) ite.next();
                final String value = prop.getProperty(key);
                _log.debug("    " + key + " = " + value);
            }
        } catch (Exception e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
            throw new IllegalStateException("buildContextProperties() threw the exception!", e);
        }
        return prop;
    }
}