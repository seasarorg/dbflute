package org.apache.torque.helper;

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

/**
 * Torque task utility.
 * 
 * @author mkubo
 */
public final class TorqueTaskUtil {

    /** Log-instance. */
    private static final Log _log = LogFactory.getLog(TorqueTaskUtil.class);

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