package org.apache.torque.task.bs;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.helper.TorqueBuildProperties;
import org.apache.torque.helper.TorqueTaskUtil;
import org.apache.torque.helper.properties.BasicProperties;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.texen.Generator;
import org.apache.velocity.texen.ant.TexenTask;

/**
 * Abstract DB meta texen task for Torque.
 * 
 * @author mkubo
 */
public abstract class TorqueTexenTask extends TexenTask {

    /** Log instance. */
    public static final Log _log = LogFactory.getLog(TorqueTexenTask.class);

    private String _targetDatabase;

    public String getTargetDatabase() {
        return _targetDatabase;
    }

    public void setTargetDatabase(String v) {
        _targetDatabase = v;
    }

    protected void fireSuperExecute() {
        // /----------------------------------------------
        // Set up the encoding of templates from property.
        // -----/
        setInputEncoding(getBasicProperties().getTemplateFileEncoding());
        setOutputEncoding(getBasicProperties().getSourceFileEncoding());

        try {
            super.execute();
        } catch (Exception e) {
            _log.info("/ * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            _log.error("super#execute() threw the exception!", e);
            _log.info("/ * * * * * * * * * /");
        }
    }

    public void setContextProperties(String file) {
        try {
            // /------------------------------------------------------------
            // Initialize internal context properties as ExtendedProperties.
            //   This property is used by Velocity Framework. 
            // -------/
            super.setContextProperties(file);
            {
                final Hashtable env = super.getProject().getProperties();
                for (final Iterator ite = env.keySet().iterator(); ite.hasNext();) {
                    final String key = (String) ite.next();
                    if (key.startsWith("torque.")) {
                        String newKey = key.substring("torque.".length());
                        for (int j = newKey.indexOf("."); j != -1; j = newKey.indexOf(".")) {
                            newKey = newKey.substring(0, j) + StringUtils.capitalise(newKey.substring(j + 1));
                        }
                        contextProperties.setProperty(newKey, (String) env.get(key));
                    }
                }
            }

            // /---------------------------------------------------------------------------------------------------
            // Initialize torque properties as Properties and set up singleton class that saves 'build.properties'.
            //   This property is used by You. 
            // -------/
            final Properties prop = TorqueTaskUtil.getBuildProperties(file, super.project);
            TorqueBuildProperties.getInstance().setProperties(prop);

        } catch (Exception e) {
            _log.warn("setContextProperties() threw the exception!!!", e);
        }
    }

    protected TorqueBuildProperties getProperties() {
        return TorqueBuildProperties.getInstance();
    }

    protected BasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }
}